package zlport.launcher;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Informe técnico de una sesión de la alpha. El zip incluye el log, las
 * acciones de juego, la configuración y un resumen legible. Se guarda primero
 * en {@code logs/informes/pendientes}; si hay consentimiento y servidor se sube
 * a {@code POST /reports}. Si la subida falla, el zip se queda pendiente y se
 * reintenta en el siguiente arranque.
 */
final class SessionReport {
   static final String VERSION = "1.4.4";
   static final String MARKER_FILE = "sesion-en-curso.properties";
   static final String PENDING_DIR = "logs/informes/pendientes";
   static final String SENT_DIR = "logs/informes/enviados";
   private static final DateTimeFormatter ID = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
   private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   private static final java.lang.System.Logger LOG = System.getLogger("zlport.launcher");

   private SessionReport() {
   }

   static final class Session {
      final Path dataRoot;
      final Path runtimeDir;
      final Path staging;
      final String id;
      final String mode;
      final String accountId;
      final String user;
      final String serverUrl;
      final Instant startedAt;
      final ServerConnection server;

      private Session(Path dataRoot, Path runtimeDir, Path staging, String id, String mode, String accountId,
            String user, String serverUrl, Instant startedAt, ServerConnection server) {
         this.dataRoot = dataRoot;
         this.runtimeDir = runtimeDir;
         this.staging = staging;
         this.id = id;
         this.mode = mode;
         this.accountId = accountId;
         this.user = user;
         this.serverUrl = serverUrl;
         this.startedAt = startedAt;
         this.server = server;
      }
   }

   /** Marca el inicio de la sesión para poder recuperarla si el proceso muere. */
   static Session begin(Path dataRoot, Path runtimeDir, String mode, String accountId, String user,
         ServerConnection server) {
      Instant now = Instant.now();
      String id = ID.format(LocalDateTime.ofInstant(now, ZoneId.systemDefault()))
         + "-" + UUID.randomUUID().toString().substring(0, 6);
      Path staging = null;
      try {
         staging = Files.createDirectories(dataRoot.resolve("logs").resolve("sesiones").resolve(id));
         Properties marker = new Properties();
         marker.setProperty("id", id);
         marker.setProperty("inicio", Long.toString(now.toEpochMilli()));
         marker.setProperty("modo", mode == null ? "local" : mode);
         marker.setProperty("cuenta", accountId == null ? "" : accountId);
         marker.setProperty("usuario", user == null ? "" : user);
         marker.setProperty("servidor", server == null ? "" : server.host() + ":" + server.port());
         marker.setProperty("runtime", runtimeDir.toAbsolutePath().normalize().toString());
         marker.setProperty("version", VERSION);
         try (Writer out = Files.newBufferedWriter(dataRoot.resolve("logs").resolve(MARKER_FILE), StandardCharsets.UTF_8)) {
            marker.store(out, "Sesión de Zombie Lane en curso; si este archivo queda al arrancar, la sesión anterior no cerró bien");
         }
      } catch (IOException | RuntimeException exception) {
         LOG.log(java.lang.System.Logger.Level.WARNING, "No se pudo marcar el inicio del informe de sesión", exception);
      }
      return new Session(dataRoot, runtimeDir, staging, id, mode, accountId, user,
         server == null ? "" : server.host() + ":" + server.port(), now, server);
   }

   /** Copia las acciones antes de que SERVER mode borre su directorio temporal. */
   static void capture(Session session) {
      if (session == null || session.staging == null) {
         return;
      }
      copy(session.runtimeDir.resolve("actions.jsonl"), session.staging.resolve("acciones.jsonl"));
      copy(session.runtimeDir.resolve("zombie-lane.properties"), session.staging.resolve("configuracion.txt"));
      copy(session.dataRoot.resolve("launcher.properties"), session.staging.resolve("lanzador.txt"));
   }

   /** Cierra el informe, lo sube si hay consentimiento y borra la marca de sesión. */
   static void finish(Session session, boolean consent, Path crashReport, Launcher.Result result, int exitCode) {
      if (session == null) {
         return;
      }
      Path marker = session.dataRoot.resolve("logs").resolve(MARKER_FILE);
      if (!consent) {
         deleteTree(session.staging);
         deleteFile(marker);
         return;
      }
      try {
         writeManifest(session, crashReport, result, exitCode, false);
         writeSessionJson(session, result, exitCode, false);
         writeMachineInfo(session.staging.resolve("equipo.txt"));
         copy(session.dataRoot.resolve("logs").resolve("zombie-lane.log"), session.staging.resolve("zombie-lane.log"));
         copy(session.dataRoot.resolve("logs").resolve("zombie-lane.1.log"), session.staging.resolve("zombie-lane.1.log"));
         copy(crashReport, session.staging.resolve("fallo.txt"));
         Path zip = zip(session.staging, session.dataRoot.resolve(PENDING_DIR).resolve(session.id + ".zip"));
         deleteTree(session.staging);
         deleteFile(marker);
         LOG.log(java.lang.System.Logger.Level.INFO, "Informe de sesión guardado en " + zip.toAbsolutePath());
         upload(session, zip);
      } catch (IOException | RuntimeException exception) {
         LOG.log(java.lang.System.Logger.Level.WARNING, "No se pudo crear el informe de sesión", exception);
      }
   }

   /**
    * Si existe la marca de una sesión anterior, genera su informe como cierre
    * brusco. Se llama al arrancar, antes de crear la marca nueva.
    */
   static void recover(Path dataRoot, boolean consent) {
      Path marker = dataRoot.resolve("logs").resolve(MARKER_FILE);
      if (!Files.isRegularFile(marker)) {
         return;
      }
      if (!consent) {
         deleteFile(marker);
         return;
      }
      Properties properties = new Properties();
      try (Reader reader = Files.newBufferedReader(marker, StandardCharsets.UTF_8)) {
         properties.load(reader);
      } catch (IOException | IllegalArgumentException exception) {
         LOG.log(java.lang.System.Logger.Level.WARNING, "No se pudo leer la marca de la sesión anterior", exception);
         deleteFile(marker);
         return;
      }

      String id = properties.getProperty("id", "sesion-anterior");
      Instant startedAt = Instant.ofEpochMilli(parseLong(properties.getProperty("inicio"), System.currentTimeMillis()));
      Path runtimeDir = Path.of(properties.getProperty("runtime", dataRoot.toString()));
      String serverText = properties.getProperty("servidor", "");
      ServerConnection server = parseServer(serverText);
      Path staging = null;
      try {
         staging = Files.createDirectories(dataRoot.resolve("logs").resolve("sesiones").resolve(id + "-recuperada"));
         Session session = new Session(
            dataRoot, runtimeDir, staging, id + "-recuperada",
            properties.getProperty("modo", "local"),
            properties.getProperty("cuenta", ""),
            properties.getProperty("usuario", ""),
            serverText, startedAt, server
         );
         copy(runtimeDir.resolve("actions.jsonl"), staging.resolve("acciones.jsonl"));
         copy(runtimeDir.resolve("zombie-lane.properties"), staging.resolve("configuracion.txt"));
         copy(dataRoot.resolve("launcher.properties"), staging.resolve("lanzador.txt"));
         writeManifest(session, null, null, 1, true);
         writeSessionJson(session, null, 1, true);
         writeMachineInfo(staging.resolve("equipo.txt"));
         copy(dataRoot.resolve("logs").resolve("zombie-lane.log"), staging.resolve("zombie-lane.log"));
         copy(dataRoot.resolve("logs").resolve("zombie-lane.1.log"), staging.resolve("zombie-lane.1.log"));
         Path zip = zip(staging, dataRoot.resolve(PENDING_DIR).resolve(session.id + ".zip"));
         deleteTree(staging);
         deleteFile(marker);
         Path sessionsRoot = dataRoot.resolve("logs").resolve("sesiones").toAbsolutePath().normalize();
         Path normalizedRuntime = runtimeDir.toAbsolutePath().normalize();
         if (normalizedRuntime.startsWith(sessionsRoot)) {
            deleteTree(normalizedRuntime);
         }
         LOG.log(java.lang.System.Logger.Level.WARNING,
            "La sesión anterior no cerró correctamente; informe guardado en " + zip.toAbsolutePath());
         upload(session, zip);
      } catch (IOException | RuntimeException exception) {
         LOG.log(java.lang.System.Logger.Level.WARNING, "No se pudo recuperar el informe de la sesión anterior", exception);
      }
   }

   /** Reintenta los informes pendientes; se llama al arrancar con un servidor disponible. */
   static void uploadPending(Path dataRoot, ServerConnection server, String accountId) {
      if (server == null) {
         return;
      }
      Path pending = dataRoot.resolve(PENDING_DIR);
      if (!Files.isDirectory(pending)) {
         return;
      }
      try (var files = Files.list(pending)) {
         List<Path> zips = files.filter(path -> path.getFileName().toString().endsWith(".zip"))
            .sorted()
            .toList();
         for (Path zip : zips) {
            uploadFile(dataRoot, zip, server, accountId);
         }
      } catch (IOException exception) {
         LOG.log(java.lang.System.Logger.Level.WARNING, "No se pudieron revisar los informes pendientes", exception);
      }
   }

   private static void upload(Session session, Path zip) {
      if (session.server == null) {
         LOG.log(java.lang.System.Logger.Level.INFO,
            "Modo local: el informe no se envía; quedó en " + zip.toAbsolutePath());
         return;
      }
      uploadFile(session.dataRoot, zip, session.server, session.accountId);
   }

   private static void uploadFile(Path dataRoot, Path zip, ServerConnection server, String accountId) {
      Path sent = dataRoot.resolve(SENT_DIR);
      try {
         server.uploadReport(zip, accountId, zip.getFileName().toString().replace(".zip", ""));
         Files.createDirectories(sent);
         Files.move(zip, sent.resolve(zip.getFileName()), StandardCopyOption.REPLACE_EXISTING);
         LOG.log(java.lang.System.Logger.Level.INFO, "Informe enviado al servidor: " + zip.getFileName());
      } catch (IOException | RuntimeException exception) {
         LOG.log(java.lang.System.Logger.Level.WARNING,
            "No se pudo enviar el informe; queda pendiente en " + zip.toAbsolutePath()
               + " (" + exception.getMessage() + ")");
      }
   }

   private static void writeManifest(Session session, Path crashReport, Launcher.Result result, int exitCode,
         boolean recovered) throws IOException {
      String n = System.lineSeparator();
      StringBuilder text = new StringBuilder();
      text.append("Zombie Lane ").append(VERSION).append(" - informe de diagnóstico (alpha)").append(n);
      text.append("Sesión: ").append(session.id).append(n);
      text.append("Inicio: ").append(WHEN.format(LocalDateTime.ofInstant(session.startedAt, ZoneId.systemDefault()))).append(n);
      text.append("Fin: ").append(WHEN.format(LocalDateTime.now())).append(n);
      text.append("Duración: ").append(Duration.between(session.startedAt, Instant.now()).toSeconds()).append(" s").append(n);
      text.append("Modo: ").append(session.mode == null ? "local" : session.mode).append(n);
      text.append("Servidor: ").append(session.serverUrl == null || session.serverUrl.isEmpty() ? "(ninguno)" : session.serverUrl).append(n);
      text.append("Cuenta: ").append(session.accountId == null ? "" : session.accountId).append(n);
      text.append("Usuario: ").append(session.user == null ? "" : session.user).append(n);
      text.append("Cierre: ").append(recovered ? "brusco (la sesión anterior no terminó)" : exitCode == 0 ? "normal" : "con error")
         .append(n);
      text.append("Código de salida: ").append(exitCode).append(n);
      if (result != null) {
         text.append("Fotogramas: ").append(result.frames()).append(n);
         text.append("Tiempo de juego: ").append(String.format(Locale.ROOT, "%.1f", result.seconds())).append(" s").append(n);
         text.append("Errores GL: ").append(result.glErrors()).append(n);
         text.append("Llamadas GL fuera del hilo: ").append(result.offThreadGl().size()).append(n);
         text.append("No portados usados: ").append(result.notPorted().size()).append(n);
      }
      if (crashReport != null) {
         text.append("Informe de fallo: ").append(crashReport.toAbsolutePath()).append(n);
      }
      text.append(n);
      text.append("Contenido del zip:").append(n);
      text.append("  zombie-lane.log / zombie-lane.1.log  registro técnico").append(n);
      text.append("  acciones.jsonl                        acciones de juego").append(n);
      text.append("  configuracion.txt                     opciones del juego").append(n);
      text.append("  lanzador.txt                          modo y conexión del lanzador").append(n);
      text.append("  equipo.txt                            sistema y Java").append(n);
      text.append("  fallo.txt                             solo si hubo error").append(n);
      text.append(n);
      text.append("No se incluyen contraseñas ni la partida guardada.").append(n);
      write(session.staging.resolve("informe.txt"), text.toString());
   }

   private static void writeSessionJson(Session session, Launcher.Result result, int exitCode, boolean recovered)
         throws IOException {
      StringBuilder json = new StringBuilder("{\n");
      json.append("  \"version\": ").append(quote(VERSION)).append(",\n");
      json.append("  \"sesion\": ").append(quote(session.id)).append(",\n");
      json.append("  \"inicio\": ").append(quote(session.startedAt.toString())).append(",\n");
      json.append("  \"fin\": ").append(quote(Instant.now().toString())).append(",\n");
      json.append("  \"modo\": ").append(quote(session.mode == null ? "local" : session.mode)).append(",\n");
      json.append("  \"servidor\": ").append(quote(session.serverUrl == null ? "" : session.serverUrl)).append(",\n");
      json.append("  \"cuenta\": ").append(quote(session.accountId == null ? "" : session.accountId)).append(",\n");
      json.append("  \"usuario\": ").append(quote(session.user == null ? "" : session.user)).append(",\n");
      json.append("  \"cierre_brusco\": ").append(recovered).append(",\n");
      json.append("  \"codigo_salida\": ").append(exitCode);
      if (result != null) {
         json.append(",\n  \"fotogramas\": ").append(result.frames());
         json.append(",\n  \"segundos\": ").append(result.seconds());
         json.append(",\n  \"errores_gl\": ").append(result.glErrors());
         json.append(",\n  \"gl_fuera_hilo\": ").append(result.offThreadGl().size());
         appendStringArray(json, "no_portados", result.notPorted());
      }
      json.append("\n}\n");
      write(session.staging.resolve("sesion.json"), json.toString());
   }

   private static void appendStringArray(StringBuilder json, String name, Set<String> values) {
      json.append(",\n  ").append(quote(name)).append(": [");
      int index = 0;
      for (String value : values) {
         if (index++ == 20) {
            break;
         }
         if (index > 1) {
            json.append(", ");
         }
         json.append(quote(value));
      }
      json.append(']');
   }

   private static void writeMachineInfo(Path file) throws IOException {
      String n = System.lineSeparator();
      StringBuilder text = new StringBuilder();
      text.append("Sistema: ").append(System.getProperty("os.name")).append(' ')
         .append(System.getProperty("os.version")).append(" (").append(System.getProperty("os.arch")).append(')').append(n);
      text.append("Java: ").append(System.getProperty("java.version")).append(" (").append(System.getProperty("java.vendor")).append(')').append(n);
      text.append("VM: ").append(System.getProperty("java.vm.name")).append(n);
      text.append("Procesadores: ").append(Runtime.getRuntime().availableProcessors()).append(n);
      text.append("Memoria máxima: ").append(Runtime.getRuntime().maxMemory() / (1024 * 1024)).append(" MB").append(n);
      text.append("Idioma: ").append(Locale.getDefault()).append(n);
      text.append("Zona horaria: ").append(java.util.TimeZone.getDefault().getID()).append(n);
      write(file, text.toString());
   }

   private static Path zip(Path source, Path target) throws IOException {
      Files.createDirectories(target.getParent());
      Path temporary = target.resolveSibling(target.getFileName() + ".tmp");
      try (OutputStream out = Files.newOutputStream(temporary, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
           ZipOutputStream zip = new ZipOutputStream(out, StandardCharsets.UTF_8)) {
         List<Path> files;
         try (var walk = Files.walk(source)) {
            files = walk.filter(Files::isRegularFile)
               .sorted(Comparator.comparing(Path::toString))
               .toList();
         }
         for (Path file : files) {
            String name = source.relativize(file).toString().replace('\\', '/');
            zip.putNextEntry(new ZipEntry(name));
            Files.copy(file, zip);
            zip.closeEntry();
         }
      }
      Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
      return target;
   }

   private static void write(Path file, String text) throws IOException {
      Files.createDirectories(file.getParent());
      Files.writeString(file, text, StandardCharsets.UTF_8);
   }

   private static void copy(Path source, Path target) {
      if (source == null || !Files.isRegularFile(source)) {
         return;
      }
      try {
         Files.createDirectories(target.getParent());
         Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException exception) {
         LOG.log(java.lang.System.Logger.Level.WARNING, "No se pudo copiar " + source + " al informe", exception);
      }
   }

   private static void deleteFile(Path file) {
      try {
         Files.deleteIfExists(file);
      } catch (IOException ignored) {
      }
   }

   private static void deleteTree(Path directory) {
      if (directory == null || !Files.isDirectory(directory)) {
         return;
      }
      try (var paths = Files.walk(directory)) {
         paths.sorted(Comparator.reverseOrder()).forEach(path -> {
            try {
               Files.deleteIfExists(path);
            } catch (IOException ignored) {
            }
         });
      } catch (IOException ignored) {
      }
   }

   private static long parseLong(String value, long fallback) {
      try {
         return Long.parseLong(value);
      } catch (NumberFormatException exception) {
         return fallback;
      }
   }

   private static ServerConnection parseServer(String text) {
      if (text == null || text.isBlank()) {
         return null;
      }
      try {
         return ServerConnection.parseEndpoint(text);
      } catch (IllegalArgumentException exception) {
         return null;
      }
   }

   private static String quote(String value) {
      StringBuilder out = new StringBuilder(value.length() + 2).append('"');
      for (int index = 0; index < value.length(); index++) {
         char character = value.charAt(index);
         switch (character) {
            case '"' -> out.append("\\\"");
            case '\\' -> out.append("\\\\");
            case '\b' -> out.append("\\b");
            case '\f' -> out.append("\\f");
            case '\n' -> out.append("\\n");
            case '\r' -> out.append("\\r");
            case '\t' -> out.append("\\t");
            default -> {
               if (character < 0x20) {
                  out.append(String.format("\\u%04x", (int)character));
               } else {
                  out.append(character);
               }
            }
         }
      }
      return out.append('"').toString();
   }
}
