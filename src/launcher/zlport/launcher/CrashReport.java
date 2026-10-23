package zlport.launcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

final class CrashReport {
   private static final DateTimeFormatter FILE = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
   private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

   private CrashReport() {
   }

   static Path write(Path dataDir, LocalDateTime now, String config, List<String> tail, Path logPath) throws IOException {
      Path dir = Files.createDirectories(dataDir.resolve("logs"));
      String base = "falha-" + now.format(FILE);
      Path file = dir.resolve(base + ".txt");

      for (int i = 2; Files.exists(file); i++) {
         file = dir.resolve(base + "-" + i + ".txt");
      }

      String n = System.lineSeparator();
      StringBuilder s = new StringBuilder();
      s.append("Zombie Lane: informe de fallo").append(n);
      s.append("Cuándo: ").append(now.format(WHEN)).append(n);
      s.append("Java: ").append(System.getProperty("java.version")).append(" (").append(System.getProperty("java.vendor")).append(')').append(n);
      s.append("Sistema: ")
         .append(System.getProperty("os.name"))
         .append(' ')
         .append(System.getProperty("os.version"))
         .append(" (")
         .append(System.getProperty("os.arch"))
         .append(')')
         .append(n);
      s.append("Configuración: ").append(config).append(n);
      s.append("Log completo: ").append(logPath == null ? "(sin log en archivo)" : logPath.toAbsolutePath()).append(n);
      s.append(n);
      s.append("El juego terminó con un fallo. El error, con la pila, y lo anterior están en las últimas líneas del log:").append(n);
      s.append("---- últimas ").append(tail.size()).append(" líneas del log ----").append(n);

      for (String line : tail) {
         s.append(line).append(n);
      }

      Files.writeString(file, s.toString(), StandardCharsets.UTF_8);
      return file;
   }
}
