package zlport.launcher;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/** Loopback-only checks for the manual, read-only service probes. */
public final class ConnectivityChecksSmoke {
   public static void main(String[] args) throws Exception {
      AtomicInteger nonGetRequests = new AtomicInteger();
      AtomicBoolean healthRedirect = new AtomicBoolean();
      AtomicBoolean sensitiveDataSeen = new AtomicBoolean();
      List<String> requestedPaths = new CopyOnWriteArrayList<>();
      HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
      server.createContext("/", exchange -> {
         var headers = exchange.getRequestHeaders();
         if (headers.containsKey("Authorization") || headers.containsKey("X-ZL-Report-Token")
               || headers.containsKey("X-ZL-Consent") || headers.containsKey("X-ZL-Account")
               || exchange.getRequestBody().readAllBytes().length != 0) {
            sensitiveDataSeen.set(true);
         }
         if (!"GET".equals(exchange.getRequestMethod())) {
            nonGetRequests.incrementAndGet();
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
          }
          String path = exchange.getRequestURI().getPath();
          requestedPaths.add(path);
         if ("/status".equals(path)) {
            reply(exchange, 200, "text/html; charset=utf-8", "<html>status</html>");
         } else if ("/status/public-status.json".equals(path)) {
            reply(exchange, 200, "application/json; charset=utf-8",
               "{\"updatedAt\":\"" + Instant.now() + "\",\"online\":true,\"players\":[]}");
         } else if ("/stale.json".equals(path)) {
            reply(exchange, 200, "application/json", "{\"updatedAt\":\"2020-01-01T00:00:00Z\",\"online\":true,\"players\":[]}");
         } else if ("/offline.json".equals(path)) {
            reply(exchange, 200, "application/json", "{\"updatedAt\":\"" + Instant.now() + "\",\"online\":false,\"players\":[]}");
          } else if ("/health".equals(path)) {
             if (healthRedirect.get()) {
                exchange.getResponseHeaders().set("Location", "https://example.invalid/health");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
             } else {
                reply(exchange, 200, "text/plain; charset=utf-8", "ok");
             }
          } else if ("/health.php".equals(path)) {
             reply(exchange, 200, "text/plain; charset=utf-8", "ok");
         } else if ("/redirect".equals(path)) {
            exchange.getResponseHeaders().set("Location", "https://example.invalid/health");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
         } else {
            reply(exchange, 404, "text/plain; charset=utf-8", "not found");
         }
      });
      server.start();
      try {
         String base = "http://127.0.0.1:" + server.getAddress().getPort();
         check(ConnectivityChecks.checkGame(ServerConnection.of("127.0.0.1", String.valueOf(server.getAddress().getPort()))).available(),
            "game health check succeeds");
         check(!ConnectivityChecks.checkGame(ServerConnection.of("bad%host", "6767")).available(),
            "malformed game host becomes a failed result instead of crashing the check");
         healthRedirect.set(true);
         check(!ConnectivityChecks.checkGame(ServerConnection.of("127.0.0.1", String.valueOf(server.getAddress().getPort()))).available(),
            "game health redirects are not followed");
         healthRedirect.set(false);
         check(ConnectivityChecks.checkWeb(URI.create(base + "/status")).available(), "public web page check succeeds");
         check(ConnectivityChecks.checkPublicStatus(URI.create(base + "/status/public-status.json"), Instant.now()).available(),
            "fresh public status snapshot succeeds");
         check(!ConnectivityChecks.checkPublicStatus(URI.create(base + "/stale.json"), Instant.now()).available(),
            "stale public snapshot is not reported as healthy");
         check(!ConnectivityChecks.checkPublicStatus(URI.create(base + "/offline.json"), Instant.now()).available(),
            "offline game snapshot is not reported as healthy");
          check(ConnectivityChecks.checkReports(URI.create(base + "/health.php")).available(), "reports health check succeeds");
          int beforeLocalOnly = requestedPaths.size();
          List<ConnectivityChecks.Check> reportsOnly = ConnectivityChecks.checkReportsOnly(URI.create(base + "/health.php"));
          check(reportsOnly.size() == 1 && "reports".equals(reportsOnly.get(0).service())
                && reportsOnly.get(0).available(), "LOCAL mode probe checks only report receiver");
          check(requestedPaths.subList(beforeLocalOnly, requestedPaths.size()).equals(List.of("/health.php")),
                "LOCAL mode probe contacts no game or public-status endpoint");
         check(!ConnectivityChecks.checkReports(URI.create(base + "/missing")).available(), "missing reports route is unavailable");
         check(!ConnectivityChecks.checkWeb(URI.create(base + "/redirect")).available(), "redirects are not followed");
         check(!ConnectivityChecks.checkWeb(URI.create("http://example.com/status")).available(),
            "unencrypted checks are rejected outside loopback");
         check(nonGetRequests.get() == 0, "probes use only GET and never upload a report");
         check(!sensitiveDataSeen.get(), "probes do not send credentials, report headers or a body");
         System.out.println("ConnectivityChecksSmoke: OK");
      } finally {
         server.stop(0);
      }
   }

   private static void reply(com.sun.net.httpserver.HttpExchange exchange, int status, String contentType, String body)
         throws java.io.IOException {
      byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().set("Content-Type", contentType);
      exchange.sendResponseHeaders(status, bytes.length);
      try (var output = exchange.getResponseBody()) {
         output.write(bytes);
      }
   }

   private static void check(boolean condition, String message) {
      if (!condition) {
         throw new AssertionError(message);
      }
   }
}
