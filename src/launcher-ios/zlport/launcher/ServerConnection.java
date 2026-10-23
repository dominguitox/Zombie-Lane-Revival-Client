package zlport.launcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Server connection typed by the player: host, port and user name. Pure logic
 * (no Swing) so it can be tested without a window.
 *
 * <p>The composed game URL is always {@code http://HOST:PORT/dead/} (the shape
 * {@code SettingsWrapper} already expects) and the connection check uses a
 * single {@code GET /health} request with short timeouts.</p>
 *
 * <p>The connection is plain unencrypted HTTP: suitable only for a trusted
 * network (LAN or the same machine). Only the host the player typed is ever
 * contacted; no external or historical hosts are involved.</p>
 */
public record ServerConnection(String host, int port) {
   /** Valid TCP port range (1..65535). */
   public static final int MIN_PORT = 1;
   public static final int MAX_PORT = 65535;
   /** User name length limit; the field is optional, kept for future authentication. */
   public static final int MAX_USER_LENGTH = 24;
   static final Duration PROBE_TIMEOUT = Duration.ofSeconds(3);
   static final String GAME_BASE = "/dead/";
   static final String HEALTH_PATH = "/health";
   static final String REPORT_PATH = "/reports";
   static final Duration AUTH_TIMEOUT = Duration.ofSeconds(10);
   static final Duration SAVE_TIMEOUT = Duration.ofSeconds(30);
   static final Duration REPORT_TIMEOUT = Duration.ofSeconds(30);
   static final String[] SAVE_FIELDS = {"avatar", "gamefield", "missions", "area"};

   /**
    * Account credentials are sent in the body of /auth/register or /auth/login
    * from the pre-game launcher, and again by the game's CreateNewSession
    * handshake, which re-authenticates with the server-issued account id (or
    * the typed user in scripted launches) plus the staged password. Plain
    * unencrypted HTTP: loopback or trusted LAN only.
    */

   public ServerConnection {
      String error = validateHost(host);
      if (error != null) {
         throw new IllegalArgumentException(error);
      }
      if (port < MIN_PORT || port > MAX_PORT) {
         throw new IllegalArgumentException("El puerto " + port + " no es válido: debe ser un entero de " + MIN_PORT + " a " + MAX_PORT);
      }
   }

   /** Game base URL for the client: http://HOST:PORT/dead/ */
   public String gameUrl() {
      return "http://" + this.host + ":" + this.port + GAME_BASE;
   }

   /** Connection-check URL: http://HOST:PORT/health */
   public String healthUrl() {
      return "http://" + this.host + ":" + this.port + HEALTH_PATH;
   }

   /** Validates a host without Swing; returns the rejection reason or null when valid. */
   public static String validateHost(String raw) {
      if (raw == null) {
         return "Escribe el host o la IP del servidor.";
      }
      String host = normalizeHost(raw);
      if (host.isEmpty()) {
         return "Escribe el host o la IP del servidor.";
      }
      if (host.indexOf(':') >= 0) {
         return "Escribe el puerto en su campo; IPv6 no está permitido en este campo.";
      }
      if (host.indexOf('/') >= 0 || host.indexOf('\\') >= 0 || host.indexOf('?') >= 0 || host.indexOf('#') >= 0 || host.indexOf('@') >= 0) {
         return "El host no puede contener rutas ni separadores; escribe solo la IP o el nombre.";
      }
      for (int i = 0; i < host.length(); i++) {
         if (Character.isWhitespace(host.charAt(i))) {
            return "El host no puede contener espacios.";
         }
      }
      return null;
   }

   /** Returns the host cleaned up (scheme and spaces removed), or null if the input was not text. */
   public static String normalizeHost(String raw) {
      if (raw == null) {
         return null;
      }
      return stripScheme(raw.strip());
   }

   /**
    * Validates the user name (letters, digits, spaces and . _ - only, at most
    * {@value #MAX_USER_LENGTH} characters; empty is allowed for the caller to
    * make required in SERVER mode); returns the rejection reason or null when
    * valid. In SERVER mode the server resolves the account by this name or by
    * the numeric id it issues, both with the password (see PendingCredentials
    * and docs/launcher-review.md).
    */
   public static String validateUser(String raw) {
      String user = raw == null ? "" : raw.strip();
      if (user.isEmpty()) {
         return null;
      }
      if (user.length() > MAX_USER_LENGTH) {
         return "El usuario no puede pasar de " + MAX_USER_LENGTH + " caracteres.";
      }
      for (int i = 0; i < user.length(); i++) {
         char c = user.charAt(i);
         boolean allowed = Character.isLetterOrDigit(c) || c == ' ' || c == '.' || c == '_' || c == '-' || (c >= 0xC0 && c <= 0xFF);
         if (!allowed) {
            return "El usuario solo admite letras, números, espacios y . _ -";
         }
      }
      return null;
   }

   /** Strips the http:// or https:// scheme (tolerates pasting a composed URL). */
   static String stripScheme(String host) {
      String lower = host.toLowerCase(Locale.ROOT);
      if (lower.startsWith("https://")) {
         return host.substring(8);
      }
      if (lower.startsWith("http://")) {
         return host.substring(7);
      }
      if (lower.startsWith("http:") || lower.startsWith("https:")) {
         return host.substring(host.indexOf(':') + 1);
      }
      return host;
   }

   /** Extracts host and port from an already composed game URL (to seed the UI); empty when not usable. */
   public static Optional<ServerConnection> parseServerUrl(String urlText) {
      if (urlText == null) {
         return Optional.empty();
      }
      String rest = normalizeHost(urlText);
      if (rest == null || rest.isEmpty()) {
         return Optional.empty();
      }
      if (rest.indexOf('/') >= 0) {
         rest = rest.substring(0, rest.indexOf('/'));
      }
      int colon = rest.lastIndexOf(':');
      if (colon <= 0) {
         // Without a port nothing is seeded: no values are invented for the UI.
         return Optional.empty();
      }
      String host = rest.substring(0, colon);
      String portText = rest.substring(colon + 1);
      try {
         return Optional.of(of(host, portText));
      } catch (IllegalArgumentException exception) {
         return Optional.empty();
      }
   }

   /** Builds a connection validating host and port; throws IllegalArgumentException with the rejection reason. */
   public static ServerConnection of(String host, String portText) {
      String hostError = validateHost(host);
      if (hostError != null) {
         throw new IllegalArgumentException(hostError);
      }
      int port;
      try {
         port = Integer.parseInt(portText.strip());
      } catch (NumberFormatException exception) {
         throw new IllegalArgumentException("El puerto '" + portText + "' no es válido: debe ser un entero de " + MIN_PORT + " a " + MAX_PORT);
      }
      if (port < MIN_PORT || port > MAX_PORT) {
         throw new IllegalArgumentException("El puerto " + portText + " no es válido: debe ser un entero de " + MIN_PORT + " a " + MAX_PORT);
      }
      return new ServerConnection(normalizeHost(host), port);
   }

   /** Lee una dirección HOST:PUERTO estricta (tolera pegar una URL de juego con esquema); error si no hay puerto. */
   public static ServerConnection parseEndpoint(String text) {
      String rest = normalizeHost(text);
      if (rest == null || rest.isEmpty()) {
         throw new IllegalArgumentException("escribe una dirección HOST:PUERTO, por ejemplo 192.168.1.10:6767");
      }
      int path = rest.indexOf('/');
      if (path >= 0) {
         rest = rest.substring(0, path);
      }
      int query = rest.indexOf('?');
      if (query >= 0) {
         rest = rest.substring(0, query);
      }
      int colon = rest.lastIndexOf(':');
      if (colon < 0) {
         throw new IllegalArgumentException(
            "escribe una dirección HOST:PUERTO, por ejemplo 192.168.1.10:6767, no '" + text + "'"
         );
      }
      return of(rest.substring(0, colon), rest.substring(colon + 1));
   }

   /** Connection probe outcome: classification plus a detail text for the UI. */
   public static record Probe(boolean ok, String detail) {
      /** Something answered but it was not /health. */
      public static Probe status(int responseCode, String body) {
         return new Probe(false, "HTTP " + responseCode + (body.isEmpty() ? "" : " " + body));
      }
   }

   /**
    * Checks the connection with a single GET /health request and short
    * timeouts. Never throws: it returns the classification plus a detail text
    * for the UI.
    */
   public AuthResult authenticate(String username, char[] password, boolean register) {
      HttpURLConnection connection = null;
      try {
         String path = register ? "/auth/register" : "/auth/login";
         connection = (HttpURLConnection)URI.create("http://" + this.host + ":" + this.port + path).toURL().openConnection();
         connection.setConnectTimeout((int)AUTH_TIMEOUT.toMillis());
         connection.setReadTimeout((int)AUTH_TIMEOUT.toMillis());
         connection.setRequestMethod("POST");
         connection.setDoOutput(true);
         connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
         String secret = new String(password);
         String form;
         try {
            form = "username=" + java.net.URLEncoder.encode(username, java.nio.charset.StandardCharsets.UTF_8)
               + "&password=" + java.net.URLEncoder.encode(secret, java.nio.charset.StandardCharsets.UTF_8);
         } finally {
            secret = "";
         }
         try (var output = connection.getOutputStream()) {
            output.write(form.getBytes(java.nio.charset.StandardCharsets.UTF_8));
         }
         int status = connection.getResponseCode();
         String body = readBody(connection);
         if (status != HttpURLConnection.HTTP_OK) {
            return new AuthResult(false, "", "", body.isBlank() ? "HTTP " + status : body);
         }
         java.util.regex.Matcher id = java.util.regex.Pattern.compile("\\\"dcgId\\\"\\s*:\\s*\\\"(\\d+)\\\"").matcher(body);
         java.util.regex.Matcher session = java.util.regex.Pattern.compile("\\\"sessionId\\\"\\s*:\\s*\\\"([a-fA-F0-9-]+)\\\"").matcher(body);
         if (!id.find() || !session.find()) {
            return new AuthResult(false, "", "", "Respuesta de autenticación inválida.");
         }
         return new AuthResult(true, id.group(1), session.group(1), "ok");
      } catch (IOException | RuntimeException exception) {
         return new AuthResult(false, "", "", exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage());
      } finally {
         if (connection != null) connection.disconnect();
         if (password != null) java.util.Arrays.fill(password, '\0');
      }
   }

   public record AuthResult(boolean ok, String accountId, String sessionId, String detail) { }

   /**
    * Downloads the account save stored on the server. The values travel as
    * Base64 inside a small XML document; the save is authoritative and the
    * local files are only a disposable working copy.
    */
   public Map<String, String> fetchSave(String accountId) throws IOException {
      String body = postSave("/dead/GetAccountSave", "dcgid=" + encode(accountId));
      Map<String, String> save = new LinkedHashMap<>();
      for (String field : SAVE_FIELDS) {
         Matcher matcher = Pattern.compile("<" + field + ">([^<]*)</" + field + ">").matcher(body);
         if (matcher.find()) {
            save.put(field, xmlDecode(matcher.group(1)));
         }
      }
      return save;
   }

   /**
    * Downloads the published custom mission catalog so the game can install
    * definitions before it reads the account save. The catalog request must
    * not consume pending show notifications.
    */
   public String fetchCustomMissions() throws IOException {
      return postSave("/dead/GetCustomMissions", "catalog=1");
   }

   /** Uploads changed Base64 fields; the server stamps sequence and hashes. */
   public void uploadSave(String accountId, Map<String, String> fields) throws IOException {
      StringBuilder form = new StringBuilder("dcgid=").append(encode(accountId));
      for (Map.Entry<String, String> field : fields.entrySet()) {
         form.append('&').append(encode(field.getKey())).append('=').append(encode(field.getValue()));
      }
      postSave("/dead/UpdateAccountInformation", form.toString());
   }

   /** Sube el zip del informe de sesión; el servidor lo guarda y aplica retención. */
   public void uploadReport(Path file, String accountId, String reportId) throws IOException {
      HttpURLConnection connection = null;
      try {
         connection = (HttpURLConnection)URI.create("http://" + this.host + ":" + this.port + REPORT_PATH)
            .toURL().openConnection();
         connection.setConnectTimeout((int)REPORT_TIMEOUT.toMillis());
         connection.setReadTimeout((int)REPORT_TIMEOUT.toMillis());
         connection.setRequestMethod("POST");
         connection.setDoOutput(true);
         connection.setRequestProperty("Content-Type", "application/zip");
         connection.setRequestProperty("X-ZL-Consent", "alpha");
         connection.setRequestProperty("X-ZL-Account", header(accountId));
         connection.setRequestProperty("X-ZL-Report-Id", header(reportId));
         connection.setRequestProperty("X-ZL-Game-Version", SessionReport.VERSION);
         long size = Files.size(file);
         connection.setFixedLengthStreamingMode(size);
         try (var output = connection.getOutputStream()) {
            Files.copy(file, output);
         }
         int status = connection.getResponseCode();
         String body = readBody(connection);
         if (status != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP " + status + (body.isBlank() ? "" : ": " + body));
         }
      } finally {
         if (connection != null) {
            connection.disconnect();
         }
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

   private String postSave(String path, String form) throws IOException {
      HttpURLConnection connection = null;
      try {
         connection = (HttpURLConnection)URI.create("http://" + this.host + ":" + this.port + path).toURL().openConnection();
         connection.setConnectTimeout((int)SAVE_TIMEOUT.toMillis());
         connection.setReadTimeout((int)SAVE_TIMEOUT.toMillis());
         connection.setRequestMethod("POST");
         connection.setDoOutput(true);
         connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
         try (var output = connection.getOutputStream()) {
            output.write(form.getBytes(StandardCharsets.UTF_8));
         }
         int status = connection.getResponseCode();
         String body = readBody(connection);
         if (status != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP " + status + (body.isBlank() ? "" : ": " + body));
         }
         return body;
      } finally {
         if (connection != null) {
            connection.disconnect();
         }
      }
   }

   private static String encode(String value) {
      return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
   }

   private static String xmlDecode(String text) {
      return text.replace("&lt;", "<")
         .replace("&gt;", ">")
         .replace("&quot;", "\"")
         .replace("&apos;", "'")
         .replace("&amp;", "&");
   }

   public Probe probe() {
      HttpURLConnection connection = null;
      try {
         URI uri = URI.create(this.healthUrl());
         connection = (HttpURLConnection)uri.toURL().openConnection();
         connection.setConnectTimeout((int)PROBE_TIMEOUT.toMillis());
         connection.setReadTimeout((int)PROBE_TIMEOUT.toMillis());
         connection.setRequestMethod("GET");
         connection.setRequestProperty("Connection", "close");
         int responseCode = connection.getResponseCode();
         String body = readBody(connection);
         if (responseCode == HttpURLConnection.HTTP_OK && body.toLowerCase(Locale.ROOT).startsWith("ok")) {
            return new Probe(true, "ok");
         }
         return Probe.status(responseCode, body);
      } catch (IOException | RuntimeException exception) {
         String detail = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
         return new Probe(false, detail);
      } finally {
         if (connection != null) {
            connection.disconnect();
         }
      }
   }

   static String readBody(HttpURLConnection connection) throws IOException {
      InputStream stream = connection.getResponseCode() >= 400 ? connection.getErrorStream() : connection.getInputStream();
      if (stream == null) {
         return "";
      }
      try (stream) {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         byte[] chunk = new byte[8192];
         int read;
         while ((read = stream.read(chunk)) != -1) {
            out.write(chunk, 0, read);
         }
         return out.toString(StandardCharsets.UTF_8).strip();
      }
   }

   @Override
   public String toString() {
      return "http://" + this.host + ":" + this.port + GAME_BASE;
   }
}
