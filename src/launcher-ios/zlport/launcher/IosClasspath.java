package zlport.launcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

final class IosClasspath {
   private static final String MIDLET_RESOURCE = "com/dchoc/DCiDead.class";

   private IosClasspath() {
   }

   static List<String> problems(String classpath) {
      List<String> problems = new ArrayList<>();
      int copies = 0;

      for (String entry : classpath.split(Pattern.quote(File.pathSeparator))) {
         if (entry.isBlank()) {
            problems.add("Entrada vacía en el classpath de ejecución");
         } else {
            Path path = Path.of(entry).toAbsolutePath().normalize();
            if (Files.exists(path)) {
               String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
               if (name.equals("game-core.jar")
                  || name.equals("game-original.jar")
                  || name.equals("game-assets")
                  || name.startsWith("compat-sdk")
                  || name.startsWith("compat-periphery")) {
                  problems.add("Dependencia de la 1.0.01 en el classpath: " + path);
               }

               try {
                  boolean hasMidlet;
                  if (Files.isDirectory(path)) {
                     hasMidlet = Files.isRegularFile(path.resolve("com/dchoc/DCiDead.class"));
                  } else {
                     try (ZipFile jar = new ZipFile(path.toFile())) {
                        hasMidlet = jar.getEntry("com/dchoc/DCiDead.class") != null;
                     }
                  }

                  if (hasMidlet) {
                     copies++;
                     if (!isGameIos(path)) {
                        problems.add("DCiDead fuera de game-ios (posible juego 1.0.01): " + path);
                     }
                  }
               } catch (IOException exception) {
                  problems.add("No se pudo examinar " + path + ": " + exception);
               }
            }
         }
      }

      if (copies != 1) {
         problems.add("Se esperaba una única DCiDead de la 1.4.4; encontradas " + copies + " copias");
      }

      return List.copyOf(problems);
   }

   private static boolean isGameIos(Path path) {
      String name = path.getFileName().toString();
      return !Files.isRegularFile(path)
         ? path.endsWith(Path.of("game-ios", "build", "classes", "java", "main"))
         : name.equals("game-ios.jar") || name.startsWith("game-ios-") && name.endsWith(".jar");
   }
}
