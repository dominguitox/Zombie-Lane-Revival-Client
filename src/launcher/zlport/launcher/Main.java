package zlport.launcher;

import java.io.IOException;
import java.lang.System.Logger;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public final class Main {
   private static final Logger LOG = System.getLogger("zlport.launcher");

   private Main() {
   }

   public static void main(String[] args) {
      System.setProperty("java.awt.headless", "true");
      ConsoleLogging.install();

      Optional<Config> parsed;
      try {
         parsed = Config.parse(args);
      } catch (IllegalArgumentException e) {
         System.err.println(e.getMessage());
         System.err
            .println(
               "Uso: zombie-lane [opciones]\n  --size LxA        ventana de ese tamaño, sin maximizar (por defecto: maximizada; restaurada, 1280x720)\n  --data-dir DIR    datos del usuario (por defecto %APPDATA%\\ZombieLane; o -Dzlport.dataDir)\n  --gl-debug        comprueba glGetError en cada llamada de GL (o -Dzlport.gl.debug=true)\n  --no-audio        sin sonido (o -Dzlport.audio=false)\n  --midlet CLASE    MIDlet a ejecutar (por defecto: el juego, com.dchoc.DCiDead; banco de la pantalla de\n                    carga: zlport.launcher.bench.LoadingScreenBench)\n  --help            esta ayuda"
            );
         System.exit(2);
         return;
      }

      if (parsed.isEmpty()) {
         System.out
            .println(
               "Uso: zombie-lane [opciones]\n  --size LxA        ventana de ese tamaño, sin maximizar (por defecto: maximizada; restaurada, 1280x720)\n  --data-dir DIR    datos del usuario (por defecto %APPDATA%\\ZombieLane; o -Dzlport.dataDir)\n  --gl-debug        comprueba glGetError en cada llamada de GL (o -Dzlport.gl.debug=true)\n  --no-audio        sin sonido (o -Dzlport.audio=false)\n  --midlet CLASE    MIDlet a ejecutar (por defecto: el juego, com.dchoc.DCiDead; banco de la pantalla de\n                    carga: zlport.launcher.bench.LoadingScreenBench)\n  --help            esta ayuda"
            );
      } else {
         Settings settings = Settings.load(parsed.get().dataDir());
         String level = System.getProperty("zlport.log", settings.logLevel());
         ConsoleLogging.install(level);
         LogFile logFile = LogFile.install(parsed.get().dataDir(), Level.parse(level));
         settings.warnings().forEach(w -> LOG.log(java.lang.System.Logger.Level.WARNING, w));
         Config config = Config.parse(settings, args).orElseThrow();

         int exitCode;
         try {
            Launcher.Result result = new Launcher(config).run();
            if (result.window() != null) {
               try {
                  Settings.saveWindow(config.dataDir(), result.window());
               } catch (IOException | RuntimeException e) {
                  LOG.log(java.lang.System.Logger.Level.WARNING, "No se pudo guardar la configuración de la ventana", e);
               }
            }

            exitCode = result.exitCode();
         } catch (RuntimeException | Error e) {
            LOG.log(java.lang.System.Logger.Level.ERROR, "falha fora do launcher", e);
            exitCode = 1;
         }

         if (exitCode != 0) {
            reportFailure(config, logFile);
         }

         if (logFile != null) {
            logFile.close();
         }

         System.exit(exitCode);
      }
   }

   private static void reportFailure(Config config, LogFile logFile) {
      Path report = null;

      try {
         report = CrashReport.write(
            config.dataDir(), LocalDateTime.now(), config.toString(), logFile == null ? List.of() : logFile.tail(), logFile == null ? null : logFile.path()
         );
         LOG.log(java.lang.System.Logger.Level.ERROR, "Informe de fallo en " + report.toAbsolutePath());
      } catch (IOException | RuntimeException e) {
         LOG.log(java.lang.System.Logger.Level.ERROR, "No se pudo guardar el informe de fallo", e);
      }

      if (config.visible()) {
         WindowsDialogs.showError(
            "Zombie Lane", "O Zombie Lane encontrou um erro e vai fechar." + (report == null ? "" : "\n\nDetalhes em:\n" + report.toAbsolutePath())
         );
      }
   }
}
