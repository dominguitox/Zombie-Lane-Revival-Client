package zlport.android;

import android.content.SharedPreferences;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

final class FileSharedPreferences implements SharedPreferences {
   private static final Logger LOG = System.getLogger("zlport.android");
   private final Path file;
   private final Map<String, Object> values = new HashMap<>();

   FileSharedPreferences(Path file) {
      this.file = file;
      if (Files.isRegularFile(file)) {
         this.load();
      }
   }

   private void load() {
      Properties p = new Properties();

      try (Reader r = Files.newBufferedReader(this.file, StandardCharsets.UTF_8)) {
         p.load(r);
      } catch (IOException | IllegalArgumentException e) {
         LOG.log(Level.ERROR, "preferências ilegíveis, começando vazias: " + this.file, e);
         return;
      }

      for (String key : p.stringPropertyNames()) {
         String raw = p.getProperty(key);
         Object value = decode(raw);
         if (value == null) {
            LOG.log(Level.WARNING, "valor sem tipo reconhecido em " + this.file + ", ignorado: " + key + "=" + raw);
         } else {
            this.values.put(key, value);
         }
      }
   }

   private static Object decode(String raw) {
      if (raw.length() >= 2 && raw.charAt(1) == ':') {
         String text = raw.substring(2);

         try {
            return switch (raw.charAt(0)) {
               case 'b' -> Boolean.parseBoolean(text);
               case 'i' -> Integer.parseInt(text);
               case 'l' -> Long.parseLong(text);
               case 's' -> text;
               default -> null;
            };
         } catch (NumberFormatException e) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String encode(Object value) {
      char type = (char)(value instanceof Boolean ? 98 : (value instanceof Integer ? 105 : (value instanceof Long ? 108 : 115)));
      return type + ":" + value;
   }

   @Override
   public synchronized SharedPreferences.Editor edit() {
      return new FileSharedPreferences.FileEditor();
   }

   @Override
   public synchronized Map getAll() {
      return new HashMap<>(this.values);
   }

   @Override
   public synchronized boolean getBoolean(String key, boolean defValue) {
      Object v = this.values.get(key);
      return v == null ? defValue : (Boolean)v;
   }

   @Override
   public synchronized int getInt(String key, int defValue) {
      Object v = this.values.get(key);
      return v == null ? defValue : (Integer)v;
   }

   @Override
   public synchronized long getLong(String key, long defValue) {
      Object v = this.values.get(key);
      return v == null ? defValue : (Long)v;
   }

   @Override
   public synchronized String getString(String key, String defValue) {
      Object v = this.values.get(key);
      return v == null ? defValue : (String)v;
   }

   private synchronized boolean commit(Map<String, Object> changes) {
      changes.forEach((key, value) -> {
         if (value == null) {
            this.values.remove(key);
         } else {
            this.values.put(key, value);
         }
      });
      StringBuilder text = new StringBuilder();

      for (Entry<String, Object> e : new TreeMap<>(this.values).entrySet()) {
         text.append(propertyLine(e.getKey(), encode(e.getValue())));
      }

      Path tmp = this.file.resolveSibling(this.file.getFileName() + ".tmp");

      try {
         Files.createDirectories(this.file.getParent());
         Files.writeString(tmp, text, StandardCharsets.UTF_8);
         Files.move(tmp, this.file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         return true;
      } catch (IOException e) {
         LOG.log(Level.ERROR, "não foi possível gravar as preferências " + this.file, e);
         return false;
      }
   }

   private static String propertyLine(String key, String value) {
      Properties one = new Properties();
      one.setProperty(key, value);
      StringWriter w = new StringWriter();

      try {
         one.store(w, null);
      } catch (IOException e) {
         throw new IllegalStateException(e);
      }

      StringBuilder line = new StringBuilder();

      for (String l : w.toString().split("\\R")) {
         if (!l.startsWith("#") && !l.isEmpty()) {
            line.append(l).append('\n');
         }
      }

      return line.toString();
   }

   private final class FileEditor implements SharedPreferences.Editor {
      private final Map<String, Object> changes = new HashMap<>();

      @Override
      public boolean commit() {
         synchronized (this) {
            return FileSharedPreferences.this.commit(new HashMap<>(this.changes));
         }
      }

      @Override
      public synchronized SharedPreferences.Editor putBoolean(String key, boolean value) {
         this.changes.put(key, value);
         return this;
      }

      @Override
      public synchronized SharedPreferences.Editor putInt(String key, int value) {
         this.changes.put(key, value);
         return this;
      }

      @Override
      public synchronized SharedPreferences.Editor putLong(String key, long value) {
         this.changes.put(key, value);
         return this;
      }

      @Override
      public synchronized SharedPreferences.Editor putString(String key, String value) {
         this.changes.put(key, value);
         return this;
      }
   }
}
