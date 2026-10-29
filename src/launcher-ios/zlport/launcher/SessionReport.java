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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
 * en {@code logs/informes/pendientes}; con consentimiento se sube a un
 * receptor HTTPS independiente del servidor de juego. Si la subida falla,
 * el zip queda pendiente y se reintenta al arrancar.
 */
final class SessionReport {
   static final String CLIENT_VERSION_PROPERTY = "zlport.client.version";
    static final String RELEASE_VERSION = "0.1.3";
    /** Build identity from the package; defaults to the current release for direct source launches. */
   static final String VERSION = configuredClientVersion();
   static final String MARKER_FILE = "sesion-en-curso.properties";
   static final String PENDING_DIR = "logs/informes/pendientes";
   static final String SENT_DIR = "logs/informes/enviados";
   static final String REPORT_URL_PROPERTY = "zlport.alpha.report-url";
   static final String DEFAULT_REPORT_URL = "https://reports.jorgelio.cl/reports/index.php";
   static final String TOKEN_FILE = "report-token.txt";
   static final long MAX_UPLOAD_BYTES = 1024L * 1024L;
   private static final int MAX_PENDING_UPLOADS_PER_START = 3;
   private static final Duration LOCAL_RETENTION = Duration.ofDays(30);
   // LOCAL mode replaces URL.openConnection with OfflineNetwork. Keep uploads
   // on this dedicated client; reportEndpoint() restricts destinations to HTTPS
   // or explicit loopback HTTP, so game networking remains offline.
   private static final HttpClient REPORT_CLIENT = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(10))
      .followRedirects(HttpClient.Redirect.NEVER)
      .build();
   private static final DateTimeFormatter ID = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
   private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   private static final java.lang.System.Logger LOG = System.getLogger("zlport.launcher");

   private SessionReport() {
   }

   private static String configuredClientVersion() {
      String configured = System.getProperty(CLIENT_VERSION_PROPERTY, RELEASE_VERSION).strip();
      return configured.matches("[0-9A-Za-z][0-9A-Za-z._-]{0,63}") ? configured : RELEASE_VERSION;
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
      final URI reportEndpoint;

      private Session(Path dataRoot, Path runtimeDir, Path staging, String id, String mode, String accountId,
            String user, String serverUrl, Instant startedAt, URI reportEndpoint) {
         this.dataRoot = dataRoot;
         this.runtimeDir = runtimeDir;
         this.staging = staging;
         this.id = id;
         this.mode = mode;
         this.accountId = accountId;
         this.user = user;
         this.serverUrl = serverUrl;
         this.startedAt = startedAt;
         this.reportEndpoint = reportEndpoint;
      }
   }

   /** Marca el inicio de la sesión para poder recuperarla si el proceso muere. */
   static Session begin(Path dataRoot, Path runtimeDir, String mode, String accountId, String user,
         ServerConnection server, URI reportEndpoint) {
      Instant now = Instant.now();
      String id = ID.format(LocalDateTime.ofInstant(now, ZoneId.systemDefault()))
         + "-" + UUID.randomUUID();
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
          server == null ? "" : server.host() + ":" + server.port(), now, reportEndpoint);
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
       if (!consent || !AlphaConsent.enabled(session.dataRoot)) {
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
          upload(session.dataRoot, zip, session.reportEndpoint, session.accountId);
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
      Path staging = null;
      try {
         staging = Files.createDirectories(dataRoot.resolve("logs").resolve("sesiones").resolve(id + "-recuperada"));
         Session session = new Session(
            dataRoot, runtimeDir, staging, id + "-recuperada",
            properties.getProperty("modo", "local"),
            properties.getProperty("cuenta", ""),
            properties.getProperty("usuario", ""),
             serverText, startedAt, null
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
      } catch (IOException | RuntimeException exception) {
         LOG.log(java.lang.System.Logger.Level.WARNING, "No se pudo recuperar el informe de la sesión anterior", exception);
      }
   }

   /** Reintenta los informes pendientes; se llama al arrancar con un servidor disponible. */
   static void uploadPending(Path dataRoot, URI reportEndpoint) {
      purgeOld(dataRoot.resolve(PENDING_DIR));
      purgeOld(dataRoot.resolve(SENT_DIR));
      if (reportEndpoint == null) {
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
          int attempted = 0;
          for (Path zip : zips) {
             if (attempted >= MAX_PENDING_UPLOADS_PER_START) {
                break;
             }
             if (Files.size(zip) > MAX_UPLOAD_BYTES) {
                LOG.log(java.lang.System.Logger.Level.WARNING,
                   "Informe mayor que el límite de 1 MiB; queda pendiente sin enviar: " + zip.getFileName());
                continue;
             }
             attempted++;
             uploadFile(dataRoot, zip, reportEndpoint, "");
         }
      } catch (IOException exception) {
         LOG.log(java.lang.System.Logger.Level.WARNING, "No se pudieron revisar los informes pendientes", exception);
      }
   }

   private static void purgeOld(Path directory) {
      if (!Files.isDirectory(directory)) {
         return;
      }
      try (var files = Files.list(directory)) {
         Instant cutoff = Instant.now().minus(LOCAL_RETENTION);
         for (Path file : files.filter(path -> path.getFileName().toString().endsWith(".zip")).toList()) {
            if (Files.isRegularFile(file) && Files.getLastModifiedTime(file).toInstant().isBefore(cutoff)) {
               Files.delete(file);
            }
         }
      } catch (IOException exception) {
         LOG.log(java.lang.System.Logger.Level.WARNING, "No se pudieron purgar informes locales antiguos", exception);
      }
   }

   static URI reportEndpoint() {
      String configured = System.getProperty(REPORT_URL_PROPERTY, DEFAULT_REPORT_URL).strip();
      if (configured.isEmpty()) {
         return null;
      }
      try {
         URI endpoint = URI.create(configured);
         String scheme = endpoint.getScheme();
         boolean https = "https".equalsIgnoreCase(scheme);
         boolean localHttp = "http".equalsIgnoreCase(scheme) && endpoint.getHost() != null
            && (endpoint.getHost().equalsIgnoreCase("localhost") || endpoint.getHost().equals("127.0.0.1")
               || endpoint.getHost().equals("::1"));
         if ((!https && !localHttp) || endpoint.getHost() == null || endpoint.getUserInfo() != null
               || endpoint.getFragment() != null || endpoint.getQuery() != null) {
            throw new IllegalArgumentException("report URL must use HTTPS (HTTP is allowed only for loopback tests)");
         }
         return endpoint;
      } catch (IllegalArgumentException exception) {
         LOG.log(java.lang.System.Logger.Level.WARNING,
            "URL de informes no válida; el ZIP se conservará localmente (" + exception.getMessage() + ")");
         return null;
      }
   }

   private static void upload(Path dataRoot, Path zip, URI reportEndpoint, String accountId) {
      if (reportEndpoint == null) {
         LOG.log(java.lang.System.Logger.Level.INFO,
            "No hay destino HTTPS de informes; el ZIP quedó local en " + zip.toAbsolutePath());
         return;
      }
      uploadFile(dataRoot, zip, reportEndpoint, accountId);
   }

   private static void uploadFile(Path dataRoot, Path zip, URI reportEndpoint, String accountId) {
      Path sent = dataRoot.resolve(SENT_DIR);
      try {
          String token = reportToken(dataRoot);
          if (token == null && !loopback(reportEndpoint)) {
             LOG.log(java.lang.System.Logger.Level.WARNING,
                "No hay clave de tester válida; informe pendiente sin enviar: " + zip.getFileName());
             return;
          }
          long size = Files.size(zip);
          if (size > MAX_UPLOAD_BYTES) {
             LOG.log(java.lang.System.Logger.Level.WARNING,
                "Informe mayor que el límite de 1 MiB; queda pendiente sin enviar: " + zip.getFileName());
             return;
          }
          HttpRequest.Builder request = HttpRequest.newBuilder(reportEndpoint)
             .timeout(Duration.ofSeconds(15))
             .header("Content-Type", "application/zip")
             .header("X-ZL-Consent", "alpha")
             .POST(HttpRequest.BodyPublishers.ofFile(zip));
          if (token != null) {
             request.header("X-ZL-Report-Token", token);
          }
          request.header("X-ZL-Account", header(accountId));
          request.header("X-ZL-Report-Id", header(zip.getFileName().toString().replace(".zip", "")));
          request.header("X-ZL-Game-Version", VERSION);
          HttpResponse<Void> response = REPORT_CLIENT.send(request.build(), HttpResponse.BodyHandlers.discarding());
          int status = response.statusCode();
          if (status != 200) {
             throw new IOException("HTTP " + status);
          }
          Files.createDirectories(sent);
          Files.move(zip, sent.resolve(zip.getFileName()), StandardCopyOption.REPLACE_EXISTING);
          LOG.log(java.lang.System.Logger.Level.INFO, "Informe recibido por el endpoint de diagnóstico: " + zip.getFileName());
       } catch (IOException | RuntimeException | InterruptedException exception) {
          if (exception instanceof InterruptedException) {
             Thread.currentThread().interrupt();
          }
          LOG.log(java.lang.System.Logger.Level.WARNING,
             "No se pudo enviar el informe; queda pendiente en " + zip.toAbsolutePath()
                 + " (" + exception.getMessage() + ")");
       }
   }

   private static boolean loopback(URI endpoint) {
      String host = endpoint.getHost();
      return host != null && (host.equalsIgnoreCase("localhost") || host.equals("127.0.0.1")
         || host.equals("::1"));
   }

   private static String reportToken(Path dataRoot) {
      Path file = dataRoot.resolve(TOKEN_FILE);
      try {
         if (!Files.isRegularFile(file) || Files.size(file) > 256) {
            return null;
         }
         String value = Files.readString(file, StandardCharsets.US_ASCII).strip();
         return value.matches("[A-Za-z0-9_-]{32,128}") ? value : null;
      } catch (IOException exception) {
         return null;
      }
   }

   private static String header(String value) {
      if (value == null) {
         return "";
      }
      StringBuilder out = new StringBuilder(value.length());
      for (int index = 0; index < value.length(); index++) {
         char character = value.charAt(index);
         out.append(character >= 0x20 && character <= 0x7E ? character : '?');
      }
      return out.toString();
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
