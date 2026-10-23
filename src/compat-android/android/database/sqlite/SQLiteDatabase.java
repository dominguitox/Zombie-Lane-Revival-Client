package android.database.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SQLiteDatabase extends SQLiteClosable {
   private static final Logger LOG = System.getLogger("zlport.android.sqlite");
   private static final Pattern CREATE = Pattern.compile(
      "create table (\\w+) ?\\( ?_id integer primary key autoincrement, key text not null, value blob not null, time_is integer not null ?\\) ?;?"
   );
   private static final Pattern KEY_SELECTION = Pattern.compile("\\s*key\\s*=\\s*'((?:[^']|'')*)'\\s*");
   private static final List<String> COLUMNS = List.of("_id", "key", "value", "time_is");
   private static final Pattern RESERVED = Pattern.compile("con|prn|aux|nul|com[0-9]|lpt[0-9]");
   private static final String VERSION_FILE = "user_version.txt";
   private final Path mPath;
   private boolean mOpen = true;

   private SQLiteDatabase(Path path) {
      this.mPath = path;
   }

   static SQLiteDatabase openOrCreate(Path path) {
      try {
         Files.createDirectories(path);
      } catch (IOException e) {
         throw new SQLException("não foi possível criar o banco " + path, e);
      }

      return new SQLiteDatabase(path);
   }

   public String getPath() {
      return this.mPath.toString();
   }

   public synchronized boolean isOpen() {
      return this.mOpen;
   }

   public synchronized void close() {
      this.mOpen = false;
   }

   public synchronized int getVersion() {
      this.checkOpen();
      Path file = this.mPath.resolve("user_version.txt");

      try {
         return Files.isRegularFile(file) ? Integer.parseInt(Files.readString(file, StandardCharsets.UTF_8).strip()) : 0;
      } catch (IOException | NumberFormatException e) {
         throw new SQLException("versão ilegível em " + file, e);
      }
   }

   public synchronized void setVersion(int version) {
      this.checkOpen();
      write(this.mPath.resolve("user_version.txt"), Integer.toString(version).getBytes(StandardCharsets.UTF_8), null);
   }

   public synchronized void execSQL(String sql) throws SQLException {
      this.checkOpen();
      String normalized = sql.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
      Matcher m = CREATE.matcher(normalized);
      if (!m.matches()) {
         throw new SQLException("SQL fora do que o port emula (FASE-03 §4.4): " + sql);
      }

      Path table = this.mPath.resolve(m.group(1));
      if (Files.isDirectory(table)) {
         throw new SQLException("table " + m.group(1) + " already exists");
      }

      try {
         Files.createDirectory(table);
      } catch (IOException e) {
         throw new SQLException("não foi possível criar a tabela " + table, e);
      }

      LOG.log(Level.DEBUG, () -> "tabela " + m.group(1) + " criada em " + this.mPath);
   }

   public synchronized long insert(String table, String nullColumnHack, ContentValues values) {
      this.checkOpen();
      Path dir = this.table(table);
      SQLiteDatabase.Row row = row(values, true);
      if (row.missing != null) {
         LOG.log(Level.ERROR, "Error inserting " + values + " em " + table + ": NOT NULL constraint failed: " + table + "." + row.missing);
         return -1L;
      }

      Path file = dir.resolve(fileName(row.key));
      if (Files.exists(file)) {
         throw new SQLException("o emulador guarda uma linha por chave, e '" + row.key + "' já existe em " + table + " (FASE-03 §4.4)");
      }

      write(file, row.value, row.time);
      LOG.log(Level.DEBUG, () -> "insert " + table + " '" + row.key + "' (" + row.value.length + " bytes)");
      return rows(dir).size();
   }

   public synchronized Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
      this.checkOpen();
      if (selectionArgs == null && groupBy == null && having == null && orderBy == null) {
         Path dir = this.table(table);
         List<String> wanted = columns == null ? COLUMNS : List.of(columns);

         for (String c : wanted) {
            if (!COLUMNS.contains(c)) {
               throw new SQLException("no such column: " + c);
            }
         }

         List<Path> files = rows(dir);
         List<Object[]> result = new ArrayList<>();
         String key = selectedKey(selection);

         for (int i = 0; i < files.size(); i++) {
            Path file = files.get(i);
            String rowKey = keyOf(file.getFileName().toString());
            if (key == null || key.equals(rowKey)) {
               Object[] values = new Object[wanted.size()];

               for (int c = 0; c < values.length; c++) {
                  values[c] = switch ((String)wanted.get(c)) {
                     case "_id" -> (long)(i + 1);
                     case "key" -> rowKey;
                     case "value" -> read(file);
                     default -> modified(file);
                  };
               }

               result.add(values);
            }
         }

         LOG.log(Level.DEBUG, () -> "query " + table + " " + (key == null ? "(todas)" : "key='" + key + "'") + ": " + result.size() + " linha(s)");
         return new SQLiteDatabase.RowsCursor(wanted, result);
      } else {
         throw new SQLException("consulta fora do que o port emula (FASE-03 §4.4): argumentos, agrupamento ou ordem");
      }
   }

   public synchronized int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
      this.checkOpen();
      if (whereArgs != null) {
         throw new SQLException("update fora do que o port emula (FASE-03 §4.4): argumentos");
      }

      Path dir = this.table(table);
      String key = selectedKey(whereClause);
      SQLiteDatabase.Row row = row(values, false);
      if (row.missing != null) {
         throw new SQLException("NOT NULL constraint failed: " + table + "." + row.missing);
      }

      int changed = 0;

      for (Path file : rows(dir)) {
         String rowKey = keyOf(file.getFileName().toString());
         if (key == null || key.equals(rowKey)) {
            if (row.key != null && !row.key.equals(rowKey)) {
               throw new SQLException("o emulador não troca a chave de uma linha ('" + rowKey + "' → '" + row.key + "', FASE-03 §4.4)");
            }

            write(file, row.value != null ? row.value : read(file), row.time != null ? row.time : modified(file));
            changed++;
         }
      }

      int n = changed;
      LOG.log(Level.DEBUG, () -> "update " + table + " " + (key == null ? "(todas)" : "key='" + key + "'") + ": " + n + " linha(s)");
      return changed;
   }

   private static SQLiteDatabase.Row row(ContentValues values, boolean insert) {
      for (String c : values.keySet()) {
         if (!COLUMNS.contains(c) || c.equals("_id")) {
            throw new SQLException("coluna fora do que o port emula (FASE-03 §4.4): " + c);
         }
      }

      Object key = values.get("key");
      Object value = values.get("value");
      Object time = values.get("time_is");
      String missing = null;

      for (String c : List.of("key", "value", "time_is")) {
         if ((insert || values.containsKey(c)) && values.get(c) == null) {
            missing = c;
            break;
         }
      }

      if ((key == null || key instanceof String) && (value == null || value instanceof byte[]) && (time == null || time instanceof Long)) {
         return new SQLiteDatabase.Row((String)key, (byte[])value, (Long)time, missing);
      } else {
         throw new SQLException("tipo fora do que o port emula (FASE-03 §4.4): " + values);
      }
   }

   private static String selectedKey(String selection) {
      if (selection == null) {
         return null;
      } else {
         Matcher m = KEY_SELECTION.matcher(selection);
         if (!m.matches()) {
            throw new SQLException("seleção fora do que o port emula (FASE-03 §4.4): " + selection);
         } else {
            return m.group(1).replace("''", "'");
         }
      }
   }

   private Path table(String name) {
      Path dir = name.matches("\\w+") ? this.mPath.resolve(name.toLowerCase(Locale.ROOT)) : null;
      if (dir != null && Files.isDirectory(dir)) {
         return dir;
      } else {
         throw new SQLException("no such table: " + name);
      }
   }

   private static List<Path> rows(Path dir) {
      List<Path> files = new ArrayList<>();

      try (DirectoryStream<Path> s = Files.newDirectoryStream(dir)) {
         for (Path p : s) {
            if (Files.isRegularFile(p) && !p.getFileName().toString().endsWith(".tmp")) {
               files.add(p);
            }
         }
      } catch (IOException e) {
         throw new SQLException("não foi possível ler a tabela " + dir, e);
      }

      files.sort(null);
      return files;
   }

   private static byte[] read(Path file) {
      try {
         return Files.readAllBytes(file);
      } catch (IOException e) {
         throw new SQLException("não foi possível ler " + file, e);
      }
   }

   private static long modified(Path file) {
      try {
         return Files.getLastModifiedTime(file).toMillis();
      } catch (IOException e) {
         throw new SQLException("não foi possível ler " + file, e);
      }
   }

   private static void write(Path file, byte[] content, Long time) {
      Path tmp = file.resolveSibling(file.getFileName() + ".tmp");

      try {
         Files.write(tmp, content);
         if (time != null) {
            Files.setLastModifiedTime(tmp, FileTime.fromMillis(time));
         }

         Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (IOException e) {
         throw new SQLException("não foi possível gravar " + file, e);
      }
   }

   static String fileName(String key) {
      if (key.isEmpty()) {
         return "%";
      }

      StringBuilder name = new StringBuilder();

      for (byte b : key.getBytes(StandardCharsets.UTF_8)) {
         int c = b & 255;
         if ((c < 97 || c > 122) && (c < 48 || c > 57) && c != 95 && c != 45) {
            name.append('%').append(String.format("%02X", c));
         } else {
            name.append((char)c);
         }
      }

      if (RESERVED.matcher(name).matches()) {
         name.replace(0, 1, String.format("%%%02X", Integer.valueOf(name.charAt(0))));
      }

      if (name.length() > 200) {
         throw new SQLException("chave longa demais para o emulador (FASE-03 §4.4): " + key);
      } else {
         return name.toString();
      }
   }

   static String keyOf(String fileName) {
      if (fileName.equals("%")) {
         return "";
      }

      ByteArrayOutputStream bytes = new ByteArrayOutputStream();

      for (int i = 0; i < fileName.length(); i++) {
         char c = fileName.charAt(i);
         if (c == '%' && i + 2 < fileName.length()) {
            bytes.write(Integer.parseInt(fileName.substring(i + 1, i + 3), 16));
            i += 2;
         } else {
            bytes.write(c);
         }
      }

      return bytes.toString(StandardCharsets.UTF_8);
   }

   private void checkOpen() {
      if (!this.mOpen) {
         throw new IllegalStateException("attempt to re-open an already-closed object: SQLiteDatabase: " + this.mPath);
      }
   }

   public interface CursorFactory {
   }

   private record Row(String key, byte[] value, Long time, String missing) {
   }

   private static final class RowsCursor implements Cursor {
      private final List<String> columns;
      private final List<Object[]> rows;
      private int position = -1;

      RowsCursor(List<String> columns, List<Object[]> rows) {
         this.columns = columns;
         this.rows = rows;
      }

      @Override
      public void close() {
      }

      @Override
      public int getCount() {
         return this.rows.size();
      }

      @Override
      public boolean moveToNext() {
         if (this.position < this.rows.size()) {
            this.position++;
         }

         return this.position < this.rows.size();
      }

      private Object value(int column) {
         if (this.position >= 0 && this.position < this.rows.size()) {
            return this.rows.get(this.position)[column];
         } else {
            throw new IndexOutOfBoundsException("Index " + this.position + " requested, with a size of " + this.rows.size());
         }
      }

      @Override
      public byte[] getBlob(int column) {
         Object v = this.value(column);
         if (v instanceof String s) {
            return s.getBytes(StandardCharsets.UTF_8);
         } else if (v instanceof byte[] b) {
            return (byte[])b.clone();
         } else {
            throw new SQLException("unable to convert INTEGER to BLOB (coluna " + this.columns.get(column) + ")");
         }
      }

      @Override
      public String getString(int column) {
         Object v = this.value(column);
         if (v instanceof byte[]) {
            throw new SQLException("unable to convert BLOB to string (coluna " + this.columns.get(column) + ")");
         } else {
            return v.toString();
         }
      }
   }
}
