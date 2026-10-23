package zlport.launcher;

import java.io.IOException;
import java.io.Writer;
import java.lang.System.Logger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

final class LogFile extends Handler {
   static final String DIR = "logs";
   static final String NAME = "zombie-lane.log";
   static final int KEEP = 5;
   static final int TAIL_LINES = 200;
   private static final Logger LOG = System.getLogger("zlport.launcher");
   private final Path path;
   private final Writer out;
   private final ArrayDeque<String> tail = new ArrayDeque<>();

   private LogFile(Path path, Writer out) {
      this.path = path;
      this.out = out;
      this.setFormatter(new ConsoleLogging.LineFormatter());
   }

   static LogFile install(Path dataDir, Level level) {
      Path dir = dataDir.resolve("logs");

      LogFile file;
      try {
         Files.createDirectories(dir);
         rotate(dir);
         Path path = dir.resolve("zombie-lane.log");
         file = new LogFile(path, Files.newBufferedWriter(path, StandardCharsets.UTF_8));
      } catch (IOException | RuntimeException e) {
         LOG.log(java.lang.System.Logger.Level.WARNING, "No se pudo abrir el log en archivo " + dir.toAbsolutePath() + "; se sigue solo con la consola", e);
         return null;
      }

      file.setLevel(level);
      java.util.logging.Logger.getLogger("").addHandler(file);
      LOG.log(
         java.lang.System.Logger.Level.INFO,
         "Zombie Lane · Java "
            + System.getProperty("java.version")
            + " ("
            + System.getProperty("java.vendor")
            + ") · "
            + System.getProperty("os.name")
            + " "
            + System.getProperty("os.version")
            + " ("
            + System.getProperty("os.arch")
            + ") · datos en "
            + dataDir.toAbsolutePath()
      );
      return file;
   }

   static void rotate(Path dir) throws IOException {
      Files.deleteIfExists(dir.resolve(numbered(5)));

      for (int i = 4; i >= 1; i--) {
         Path from = dir.resolve(numbered(i));
         if (Files.exists(from)) {
            Files.move(from, dir.resolve(numbered(i + 1)), StandardCopyOption.REPLACE_EXISTING);
         }
      }

      Path current = dir.resolve("zombie-lane.log");
      if (Files.exists(current)) {
         Files.move(current, dir.resolve(numbered(1)), StandardCopyOption.REPLACE_EXISTING);
      }
   }

   static String numbered(int i) {
      return "zombie-lane." + i + ".log";
   }

   @Override
   public synchronized void publish(LogRecord r) {
      if (this.isLoggable(r)) {
         String text = this.getFormatter().format(r);

         for (String line : text.split("\\R")) {
            if (this.tail.size() == 200) {
               this.tail.removeFirst();
            }

            this.tail.addLast(line);
         }

         try {
            this.out.write(text);
            this.out.flush();
         } catch (IOException e) {
            this.reportError(null, e, 1);
         }
      }
   }

   synchronized List<String> tail() {
      return List.copyOf(this.tail);
   }

   Path path() {
      return this.path;
   }

   @Override
   public synchronized void flush() {
      try {
         this.out.flush();
      } catch (IOException e) {
         this.reportError(null, e, 2);
      }
   }

   @Override
   public synchronized void close() {
      java.util.logging.Logger.getLogger("").removeHandler(this);

      try {
         this.out.close();
      } catch (IOException e) {
         this.reportError(null, e, 3);
      }
   }
}
