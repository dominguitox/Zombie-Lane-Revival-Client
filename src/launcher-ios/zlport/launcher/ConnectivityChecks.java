package zlport.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLException;

/** Explicit, read-only connectivity checks for the game, public status site and report receiver. */
public final class ConnectivityChecks {
   static final URI PUBLIC_WEB = URI.create("https://zl.jorgelio.cl/status");
   static final URI PUBLIC_STATUS = URI.create("https://zl.jorgelio.cl/status/public-status.json");
   static final URI REPORTS_HEALTH = URI.create("https://reports.jorgelio.cl/health.php");
   private static final Duration TIMEOUT = Duration.ofSeconds(4);
   private static final Duration STATUS_MAX_AGE = Duration.ofMinutes(5);
   private static final int MAX_RESPONSE_BYTES = 64 * 1024;
   private static final Pattern UPDATED_AT = Pattern.compile("\"updatedAt\"\\s*:\\s*\"([^\"]{1,64})\"");
   private static final Pattern ONLINE = Pattern.compile("\"online\"\\s*:\\s*(true|false)");
   private static final Pattern PLAYERS = Pattern.compile("\"players\"\\s*:\\s*\\[");

   private ConnectivityChecks() {
   }

   public record Check(String service, String endpoint, boolean available, String detail) {
   }

   /** Runs only when requested by the user; HTTP calls execute concurrently and send no identity or report data. */
   public static List<Check> checkAll(ServerConnection gameServer) {
      CompletableFuture<Check> game = CompletableFuture.supplyAsync(() -> checkGame(gameServer));
      CompletableFuture<Check> web = CompletableFuture.supplyAsync(() -> checkWeb(PUBLIC_WEB));
      CompletableFuture<Check> status = CompletableFuture.supplyAsync(() -> checkPublicStatus(PUBLIC_STATUS, Instant.now()));
      CompletableFuture<Check> reports = CompletableFuture.supplyAsync(() -> checkReports(REPORTS_HEALTH));
      CompletableFuture.allOf(game, web, status, reports).join();
      return List.of(game.join(), web.join(), status.join(), reports.join());
   }

   /** LOCAL mode checks only the report receiver, without contacting game services. */
   public static List<Check> checkReportsOnly() {
      return checkReportsOnly(REPORTS_HEALTH);
   }

   static List<Check> checkReportsOnly(URI endpoint) {
      return List.of(checkReports(endpoint));
   }

   static Check checkGame(ServerConnection server) {
      String endpoint = server.healthUrl();
      ServerConnection.Probe result = server.probe();
      if (result.ok()) {
         return new Check("game", endpoint, true, LauncherText.format("probe.error.http", 200));
      }
      Matcher status = Pattern.compile("^HTTP (\\d{3})").matcher(result.detail());
      if (status.find()) {
         return new Check("game", endpoint, false, httpFailure(Integer.parseInt(status.group(1))));
      }
      return new Check("game", endpoint, false, networkFailure(result.detail()));
   }

   static Check checkWeb(URI endpoint) {
      Response response;
      try {
         response = get(endpoint, "text/html");
      } catch (IOException | RuntimeException exception) {
         return new Check("web", endpoint.toString(), false, networkFailure(exception));
      }
      if (response.status() != HttpURLConnection.HTTP_OK) {
         return new Check("web", endpoint.toString(), false, httpFailure(response.status()));
      }
      if (!response.contentType().toLowerCase(Locale.ROOT).startsWith("text/html")) {
         return new Check("web", endpoint.toString(), false, LauncherText.get("probe.error.contentType"));
      }
      return new Check("web", endpoint.toString(), true, LauncherText.format("probe.error.http", response.status()));
   }

   static Check checkPublicStatus(URI endpoint, Instant now) {
      Response response;
      try {
         response = get(endpoint, "application/json");
      } catch (IOException | RuntimeException exception) {
         return new Check("snapshot", endpoint.toString(), false, networkFailure(exception));
      }
      if (response.status() != HttpURLConnection.HTTP_OK) {
         return new Check("snapshot", endpoint.toString(), false, httpFailure(response.status()));
      }
      if (!response.contentType().toLowerCase(Locale.ROOT).startsWith("application/json")) {
         return new Check("snapshot", endpoint.toString(), false, LauncherText.get("probe.error.contentType"));
      }

      Matcher timestamp = UPDATED_AT.matcher(response.body());
      Matcher online = ONLINE.matcher(response.body());
      if (!timestamp.find() || !online.find() || !PLAYERS.matcher(response.body()).find()) {
         return new Check("snapshot", endpoint.toString(), false, LauncherText.get("probe.error.snapshotInvalid"));
      }
      if (!"true".equals(online.group(1))) {
         return new Check("snapshot", endpoint.toString(), false, LauncherText.get("probe.error.serverOffline"));
      }
      try {
         Instant updatedAt = Instant.parse(timestamp.group(1));
         Duration age = Duration.between(updatedAt, now);
         if (age.compareTo(STATUS_MAX_AGE) > 0 || age.compareTo(Duration.ofMinutes(-1)) < 0) {
            return new Check("snapshot", endpoint.toString(), false, LauncherText.get("probe.error.snapshotStale"));
         }
      } catch (DateTimeParseException exception) {
         return new Check("snapshot", endpoint.toString(), false, LauncherText.get("probe.error.snapshotInvalid"));
      }
      return new Check("snapshot", endpoint.toString(), true, LauncherText.format("probe.error.http", response.status()));
   }

   static Check checkReports(URI endpoint) {
      Response response;
      try {
         response = get(endpoint, "text/plain");
      } catch (IOException | RuntimeException exception) {
         return new Check("reports", endpoint.toString(), false, networkFailure(exception));
      }
      if (response.status() != HttpURLConnection.HTTP_OK) {
         return new Check("reports", endpoint.toString(), false, httpFailure(response.status()));
      }
      if (!"ok".equalsIgnoreCase(response.body().strip())) {
         return new Check("reports", endpoint.toString(), false, LauncherText.get("probe.error.unexpected"));
      }
      return new Check("reports", endpoint.toString(), true, LauncherText.format("probe.error.http", response.status()));
   }

   private record Response(int status, String contentType, String body) {
   }

   private static Response get(URI uri, String accept) throws IOException {
      if (!safeEndpoint(uri)) {
         throw new IOException("unsafe connectivity-check URL");
      }
      HttpURLConnection connection = null;
      try {
         connection = (HttpURLConnection)uri.toURL().openConnection();
         connection.setConnectTimeout((int)TIMEOUT.toMillis());
         connection.setReadTimeout((int)TIMEOUT.toMillis());
         connection.setInstanceFollowRedirects(false);
         connection.setUseCaches(false);
         connection.setRequestMethod("GET");
         connection.setRequestProperty("Accept", accept);
         connection.setRequestProperty("Cache-Control", "no-cache");
         connection.setRequestProperty("Connection", "close");
         int status = connection.getResponseCode();
         InputStream stream = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
         byte[] bytes;
         if (stream == null) {
            bytes = new byte[0];
         } else {
            try (stream) {
               bytes = stream.readNBytes(MAX_RESPONSE_BYTES + 1);
            }
         }
         if (bytes.length > MAX_RESPONSE_BYTES) {
            throw new IOException("connectivity-check response too large");
         }
         String contentType = connection.getContentType();
         return new Response(status, contentType == null ? "" : contentType, new String(bytes, java.nio.charset.StandardCharsets.UTF_8));
      } finally {
         if (connection != null) {
            connection.disconnect();
         }
      }
   }

   private static boolean safeEndpoint(URI uri) {
      if (uri == null || uri.getHost() == null || uri.getUserInfo() != null || uri.getQuery() != null || uri.getFragment() != null) {
         return false;
      }
      if ("https".equalsIgnoreCase(uri.getScheme())) {
         return true;
      }
      if (!"http".equalsIgnoreCase(uri.getScheme())) {
         return false;
      }
      String host = uri.getHost();
      return host.equalsIgnoreCase("localhost") || host.equals("127.0.0.1") || host.equals("::1");
   }

   private static String httpFailure(int status) {
      if (status == HttpURLConnection.HTTP_NOT_FOUND) {
         return LauncherText.get("probe.error.notDeployed");
      }
      if (status >= 300 && status < 400) {
         return LauncherText.format("probe.error.redirect", status);
      }
      return LauncherText.format("probe.error.http", status);
   }

   private static String networkFailure(Exception exception) {
      if (exception instanceof SocketTimeoutException) {
         return LauncherText.get("probe.error.timeout");
      }
      if (exception instanceof UnknownHostException) {
         return LauncherText.get("probe.error.dns");
      }
      if (exception instanceof SSLException) {
         return LauncherText.get("probe.error.tls");
      }
      return LauncherText.get("probe.error.network");
   }

   private static String networkFailure(String detail) {
      String normalized = detail == null ? "" : detail.toLowerCase(Locale.ROOT);
      if (normalized.contains("timed out") || normalized.contains("timeout")) {
         return LauncherText.get("probe.error.timeout");
      }
      if (normalized.contains("unknownhost") || normalized.contains("no such host")) {
         return LauncherText.get("probe.error.dns");
      }
      if (normalized.contains("ssl") || normalized.contains("certificate")) {
         return LauncherText.get("probe.error.tls");
      }
      return LauncherText.get("probe.error.network");
   }
}
