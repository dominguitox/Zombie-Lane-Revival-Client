package zlport.launcher;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipFile;

/** Loopback-only contract test: never contacts the configured public host. */
public final class SessionReportSmoke {
   public static void main(String[] args) throws Exception {
      Path root = Files.createTempDirectory("zl-report-smoke-");
      AtomicInteger requests = new AtomicInteger();
      List<String> ids = new ArrayList<>();
      AtomicInteger response = new AtomicInteger(200);
      HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
       server.createContext("/reports/index.php", exchange -> {
         requests.incrementAndGet();
         if (!"POST".equals(exchange.getRequestMethod())
                || !"alpha".equals(exchange.getRequestHeaders().getFirst("X-ZL-Consent"))
                || !"a".repeat(32).equals(exchange.getRequestHeaders().getFirst("X-ZL-Report-Token"))
                || !"application/zip".equals(exchange.getRequestHeaders().getFirst("Content-Type"))
                   || !"0.1.3".equals(exchange.getRequestHeaders().getFirst("X-ZL-Game-Version"))) {
             throw new AssertionError("Wrong upload contract");
         }
         ids.add(exchange.getRequestHeaders().getFirst("X-ZL-Report-Id"));
         byte[] bytes = exchange.getRequestBody().readAllBytes();
         if (bytes.length < 4 || bytes[0] != 'P' || bytes[1] != 'K') {
            throw new AssertionError("Not a ZIP");
         }
         exchange.sendResponseHeaders(response.get(), -1);
         exchange.close();
      });
      server.start();
      try {
            System.setProperty("zlport.client.version", "0.1.3");
            check(SessionReport.VERSION.equals("0.1.3"), "reports must identify the official release as 0.1.3");
            check(SessionReport.DEFAULT_REPORT_URL.equals("https://reports.jorgelio.cl/reports/index.php"),
               "default endpoint must target the direct PHP script on shared hosting");
          // LOCAL mode installs this handler to block game traffic. Report
          // uploads must still be able to reach their explicitly allowed loopback endpoint.
          OfflineNetwork.install();
          Path data = Files.createDirectories(root.resolve("data"));
         Files.writeString(data.resolve("alpha.properties"),
            "preguntado=true\nconsentimiento=true\n", StandardCharsets.UTF_8);
         check(!AlphaConsent.askIfNeeded(data, false), "legacy consent must not authorize new destination");
         System.setProperty(AlphaConsent.PROPERTY, "true");
         check(AlphaConsent.askIfNeeded(data, false), "explicit opt-in failed");
         System.clearProperty(AlphaConsent.PROPERTY);
         check(AlphaConsent.enabled(data), "new consent was not stored");

          String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/reports/index.php";
         System.setProperty(SessionReport.REPORT_URL_PROPERTY, url);

         // A public receiver must not see a request until this tester has a key.
         URI publicEndpoint = URI.create(SessionReport.DEFAULT_REPORT_URL);
         SessionReport.Session noToken = SessionReport.begin(data, root, "local", "", "", null, publicEndpoint);
         SessionReport.finish(noToken, true, null, null, 0);
         check(requests.get() == 0 && count(data.resolve(SessionReport.PENDING_DIR)) == 1,
            "public endpoint without a tester key must leave the ZIP pending");
         Files.delete(data.resolve(SessionReport.PENDING_DIR).resolve(noToken.id + ".zip"));
         Files.writeString(data.resolve(SessionReport.TOKEN_FILE), "a".repeat(32), StandardCharsets.US_ASCII);
         URI endpoint = SessionReport.reportEndpoint();
         check(endpoint != null, "loopback endpoint rejected");
         for (String invalid : List.of("http://example.com/reports", "file:///tmp/report", "https://u:p@example.com/reports")) {
            System.setProperty(SessionReport.REPORT_URL_PROPERTY, invalid);
            check(SessionReport.reportEndpoint() == null, "unsafe endpoint accepted");
         }
         System.setProperty(SessionReport.REPORT_URL_PROPERTY, url);

         Path runtime = Files.createDirectories(root.resolve("runtime"));
         response.set(503);
         SessionReport.Session session = SessionReport.begin(data, runtime, "local", "account-A", "tester", null, endpoint);
          SessionReport.finish(session, true, null, null, 0);
          check(requests.get() == 1, "one session must attempt one upload");
          Path pending = data.resolve(SessionReport.PENDING_DIR);
          check(count(pending) == 1, "failed upload must remain pending");
          Path sessionZip = pending.resolve(session.id + ".zip");
          try (ZipFile zip = new ZipFile(sessionZip.toFile(), StandardCharsets.UTF_8)) {
             String metadata;
             String manifest;
             try (var input = zip.getInputStream(zip.getEntry("sesion.json"))) {
                metadata = new String(input.readAllBytes(), StandardCharsets.UTF_8);
             }
             try (var input = zip.getInputStream(zip.getEntry("informe.txt"))) {
                manifest = new String(input.readAllBytes(), StandardCharsets.UTF_8);
             }
                check(metadata.contains("\"version\": \"0.1.3\""), "session metadata must report the official version");
                check(manifest.startsWith("Zombie Lane 0.1.3 - informe"), "report manifest must report the official version");
          }
         String originalId = ids.get(0);
         response.set(200);
         SessionReport.uploadPending(data, endpoint);
         check(requests.get() == 2 && originalId.equals(ids.get(1)), "retry must keep the same report ID");
         check(count(pending) == 0 && count(data.resolve(SessionReport.SENT_DIR)) == 1,
            "successful upload must move the ZIP exactly once");
         SessionReport.uploadPending(data, endpoint);
         check(requests.get() == 2, "successful report must not be retried");

         response.set(302);
         SessionReport.Session redirected = SessionReport.begin(data, runtime, "local", "", "", null, endpoint);
         SessionReport.finish(redirected, true, null, null, 0);
         check(requests.get() == 3 && count(pending) == 1, "redirect must not count as a successful upload");
         Files.delete(pending.resolve(redirected.id + ".zip"));
         response.set(200);

         for (int i = 0; i < 4; i++) {
            Files.writeString(pending.resolve("queued-" + i + ".zip"), "PK" + "test", StandardCharsets.US_ASCII);
         }
         SessionReport.uploadPending(data, endpoint);
         check(requests.get() == 6 && count(pending) == 1, "at most three pending uploads per launch");
         Path oversized = pending.resolve("too-large.zip");
         Files.write(oversized, new byte[(int)SessionReport.MAX_UPLOAD_BYTES + 1]);
         int previous = requests.get();
         SessionReport.uploadPending(data, endpoint);
         check(requests.get() == previous + 1 && Files.exists(oversized), "oversized ZIP must not upload");

         int beforeDecline = requests.get();
         SessionReport.Session declined = SessionReport.begin(data, runtime, "local", "", "", null, null);
         SessionReport.finish(declined, false, null, null, 0);
         check(requests.get() == beforeDecline, "declined consent must not upload");
         SessionReport.Session revoked = SessionReport.begin(data, runtime, "local", "", "", null, endpoint);
         Files.writeString(data.resolve("alpha.properties"),
            "preguntado=true\nconsentimiento=false\npolitica=reports.jorgelio.cl-30d-v1\n");
         SessionReport.finish(revoked, true, null, null, 0);
         check(requests.get() == beforeDecline, "revoking consent during session must stop upload");
         Files.setLastModifiedTime(oversized, FileTime.from(Instant.now().minusSeconds(31L * 86400)));
         SessionReport.uploadPending(data, null);
         check(!Files.exists(oversized), "reports older than 30 days must be purged locally");
         System.out.println("SessionReportSmoke: OK");
      } finally {
          server.stop(0);
          System.clearProperty(SessionReport.REPORT_URL_PROPERTY);
          System.clearProperty(SessionReport.CLIENT_VERSION_PROPERTY);
          System.clearProperty(AlphaConsent.PROPERTY);
      }
   }

   private static long count(Path dir) throws Exception {
      if (!Files.isDirectory(dir)) return 0;
      try (var files = Files.list(dir)) {
         return files.filter(path -> path.toString().endsWith(".zip")).count();
      }
   }

   private static void check(boolean condition, String message) {
      if (!condition) throw new AssertionError(message);
   }
}
