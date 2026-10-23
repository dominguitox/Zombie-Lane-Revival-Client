package zlport.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class OfflineNetwork {
   private static final Logger LOG = System.getLogger("zlport.launcher.network");
   private static final Map<String, AtomicInteger> ATTEMPTS = new ConcurrentHashMap<>();
   private static boolean installed;

   private OfflineNetwork() {
   }

   public static synchronized void install() {
      if (!installed) {
         URL.setURLStreamHandlerFactory(protocol -> !protocol.equals("http") && !protocol.equals("https") ? null : new OfflineNetwork.OfflineHandler());
         installed = true;
      }
   }

   public static Map<String, Integer> attempts() {
      Map<String, Integer> copy = new TreeMap<>();
      ATTEMPTS.forEach((destination, count) -> copy.put(destination, count.get()));
      return copy;
   }

   private static void attempt(String method, URL url) {
      String destination = method + " " + url.getProtocol() + "://" + url.getAuthority() + url.getPath();
      if (ATTEMPTS.computeIfAbsent(destination, d -> new AtomicInteger()).getAndIncrement() == 0) {
         LOG.log(Level.INFO, "Sin red (port offline): " + destination);
      }
   }

   private static final class OfflineConnection extends HttpURLConnection {
      OfflineConnection(URL url) {
         super(url);
      }

      @Override
      public void connect() throws IOException {
         OfflineNetwork.attempt(this.getRequestMethod(), this.url);
         throw new ConnectException("offline: " + this.url.getHost());
      }

      @Override
      public InputStream getInputStream() throws IOException {
         this.connect();
         throw new AssertionError();
      }

      @Override
      public OutputStream getOutputStream() throws IOException {
         this.connect();
         throw new AssertionError();
      }

      @Override
      public void disconnect() {
      }

      @Override
      public boolean usingProxy() {
         return false;
      }
   }

   private static final class OfflineHandler extends URLStreamHandler {
      @Override
      protected URLConnection openConnection(URL url) {
         return new OfflineNetwork.OfflineConnection(url);
      }
   }
}
