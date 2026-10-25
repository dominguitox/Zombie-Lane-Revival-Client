package zlport.ios.runtime;

import com.dchoc.toolkit.DChocFacebookConnection;
import com.dchoc.toolkit.FacebookResponse;
import com.dchoc.toolkit.IFacebookConnection;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Stream;
import zlport.ios.IosPlatform;
import zlport.ios.LocalProfile;
import zlport.ios.IosTexture;

public final class PcIosPlatform implements IosPlatform {
   private static final Logger LOG = System.getLogger("zlport.ios");
   private static final String SYSTEM_VERSION = "7.1.2";
   private static final String ADVERTISING_IDENTIFIER_KEY = "zlport.ios.advertisingIdentifier";
    private final Path bundleDir;
    private final int energyChargeTimeMs;
    private final float spawnMultiplier;
    private final long neighborIntervalMs;
    private final boolean cashCheat;
    private final List<LocalProfile> localProfiles;
   private final Path cachesDir;
   private final Path preferencesFile;
   private final PlayerActionLog playerActionLog;
   private final Properties infoPlist;
   private final Properties preferences;
   private final float portraitHeight;
   private final Object window = new Object();
   private IFacebookConnection facebookConnection;

   public PcIosPlatform(Path bundleDir, Path dataDir, int screenWidth, int screenHeight) {
      this(bundleDir, dataDir, screenWidth, screenHeight, 0);
   }

   public PcIosPlatform(Path bundleDir, Path dataDir, int screenWidth, int screenHeight, int energyChargeTimeMs) {
      this(bundleDir, dataDir, screenWidth, screenHeight, energyChargeTimeMs, 1.0F);
   }

   public PcIosPlatform(Path bundleDir, Path dataDir, int screenWidth, int screenHeight, int energyChargeTimeMs, float spawnMultiplier) {
      this(bundleDir, dataDir, screenWidth, screenHeight, energyChargeTimeMs, spawnMultiplier, 0);
   }

    public PcIosPlatform(
       Path bundleDir, Path dataDir, int screenWidth, int screenHeight, int energyChargeTimeMs, float spawnMultiplier, int neighborIntervalMinutes
    ) {
       this(bundleDir, dataDir, screenWidth, screenHeight, energyChargeTimeMs, spawnMultiplier, neighborIntervalMinutes, false, List.of());
    }

    public PcIosPlatform(
       Path bundleDir,
       Path dataDir,
       int screenWidth,
       int screenHeight,
       int energyChargeTimeMs,
       float spawnMultiplier,
       int neighborIntervalMinutes,
       boolean cashCheat,
       List<LocalProfile> localProfiles
    ) {
       this.energyChargeTimeMs = energyChargeTimeMs;
       this.spawnMultiplier = spawnMultiplier;
       this.neighborIntervalMs = neighborIntervalMinutes * 60000L;
       this.cashCheat = cashCheat;
       this.localProfiles = List.copyOf(localProfiles);
      this.bundleDir = Objects.requireNonNull(bundleDir).toAbsolutePath().normalize();
      Path data = Objects.requireNonNull(dataDir).toAbsolutePath().normalize();
      this.cachesDir = data.resolve("Library/Caches");
      this.preferencesFile = data.resolve("preferences.properties");
      this.playerActionLog = new PlayerActionLog(data);
      this.portraitHeight = Math.max(screenWidth, screenHeight) / 2.0F;

      try {
         Files.createDirectories(this.bundleDir);
         Files.createDirectories(this.cachesDir);
         this.infoPlist = readProperties(this.bundleDir.resolve("Info.plist.properties"));
         this.preferences = readProperties(this.preferencesFile);
      } catch (IOException exception) {
         throw new UncheckedIOException("Fallo al preparar los datos de la plataforma iOS", exception);
      }
   }

   private static Properties readProperties(Path file) throws IOException {
      Properties result = new Properties();
      if (Files.exists(file)) {
         try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            result.load(reader);
         }
      }

      return result;
   }

   public String infoPlistString(String key) {
      return this.infoPlist.getProperty(key);
   }

   public boolean infoPlistBoolean(String key) {
      String value = this.infoPlist.getProperty(key);
      if (value != null && !value.isBlank()) {
         String text = value.stripLeading();
         char first = text.charAt(0);
         if (first != 'Y' && first != 'y' && first != 'T' && first != 't') {
            try {
               return Double.parseDouble(text) != 0.0;
            } catch (NumberFormatException exception) {
               return false;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public synchronized String preference(String key) {
      return this.preferences.getProperty(key);
   }

   public synchronized void setPreference(String value, String key) {
      this.preferences.setProperty(key, value);
   }

   public synchronized boolean synchronizePreferences() {
      try {
         StringWriter writer = new StringWriter();
         this.preferences.store(writer, "Preferencias de la plataforma iOS en el PC");
         writeAtomically(this.preferencesFile, writer.toString().getBytes(StandardCharsets.UTF_8));
         return true;
      } catch (IOException exception) {
         LOG.log(Level.WARNING, "Fallo al sincronizar las preferencias", exception);
         return false;
      }
   }

   private static void writeAtomically(Path target, byte[] data) throws IOException {
      Path temporary = Files.createTempFile(target.getParent(), "zlport-", ".tmp");

      try {
         Files.write(temporary, data);
         Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } finally {
         Files.deleteIfExists(temporary);
      }
   }

   public String deviceModel() {
      return "iPhone";
   }

   public int compareSystemVersion(String version, int options) {
      Objects.requireNonNull(version);
      if ((options & 64) == 0) {
         return Integer.signum("7.1.2".compareTo(version));
      }

      int left = 0;
      int right = 0;

      while (left < "7.1.2".length() && right < version.length()) {
         char a = "7.1.2".charAt(left);
         char b = version.charAt(right);
         if (isDigit(a) && isDigit(b)) {
            int leftEnd = digitEnd("7.1.2", left);
            int rightEnd = digitEnd(version, right);
            int comparison = new BigInteger("7.1.2".substring(left, leftEnd)).compareTo(new BigInteger(version.substring(right, rightEnd)));
            if (comparison != 0) {
               return Integer.signum(comparison);
            }

            left = leftEnd;
            right = rightEnd;
         } else {
            if (a != b) {
               return a < b ? -1 : 1;
            }

            left++;
            right++;
         }
      }

      return Integer.compare("7.1.2".length() - left, version.length() - right);
   }

   private static boolean isDigit(char value) {
      return value >= '0' && value <= '9';
   }

   private static int digitEnd(String text, int start) {
      int end = start;

      while (end < text.length() && isDigit(text.charAt(end))) {
         end++;
      }

      return end;
   }

   public boolean canOpenUrl(String url) {
      return "tel://".equals(url);
   }

   public boolean isImageSourceAvailable(int sourceType) {
      return sourceType == 1;
   }

   public boolean classExists(String name) {
      return "ASIdentifierManager".equals(name);
   }

   public synchronized String advertisingIdentifier() {
      String identifier = this.preferences.getProperty("zlport.ios.advertisingIdentifier");
      if (identifier == null) {
         identifier = UUID.randomUUID().toString().toUpperCase(Locale.ROOT);
         this.preferences.setProperty("zlport.ios.advertisingIdentifier", identifier);
         if (!this.synchronizePreferences()) {
            throw new IllegalStateException("No se pudo guardar el ID de publicidad del port");
         }
      }

      return identifier;
   }

   public String countryCode() {
      String country = Locale.getDefault().getCountry();
      return country.isEmpty() ? null : country;
   }

   public int networkInterfaceIndex(String name) {
      try {
         Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

         while (interfaces != null && interfaces.hasMoreElements()) {
            NetworkInterface network = interfaces.nextElement();
            byte[] address = network.getHardwareAddress();
            if (!network.isLoopback() && address != null && address.length > 0) {
               return Math.max(0, network.getIndex());
            }
         }
      } catch (SocketException exception) {
         LOG.log(Level.WARNING, "Fallo al buscar la interfaz de red", exception);
      }

      return 0;
   }

   public byte[] networkInterfaceMacAddress(int interfaceIndex) {
      if (interfaceIndex <= 0) {
         return null;
      }

      try {
         NetworkInterface network = NetworkInterface.getByIndex(interfaceIndex);
         if (network != null && !network.isLoopback()) {
            byte[] address = network.getHardwareAddress();
            if (address != null && address.length > 0) {
               return address;
            }
         }
      } catch (SocketException exception) {
         LOG.log(Level.WARNING, "Fallo al leer la dirección de hardware", exception);
      }

      return null;
   }

   public float mainScreenHeight() {
      return this.portraitHeight;
   }

   public Object window() {
      return this.window;
   }

   public float windowHeight(Object window) {
      return window == null ? 0.0F : this.portraitHeight;
   }

   public Object createActivityIndicator(int style) {
      LOG.log(Level.DEBUG, "Indicador de actividad ignorado en el PC, estilo {0}", style);
      return new Object();
   }

   public void addSubview(Object window, Object indicator) {
      ignored("UIWindow.addSubview");
   }

   public void bringSubviewToFront(Object window, Object indicator) {
      ignored("UIWindow.bringSubviewToFront");
   }

   public void setActivityIndicatorCenter(Object indicator, float x, float y) {
      ignored("UIActivityIndicatorView.setCenter");
   }

   public void startActivityIndicator(Object indicator) {
      ignored("UIActivityIndicatorView.startAnimating");
   }

   public void stopActivityIndicator(Object indicator) {
      ignored("UIActivityIndicatorView.stopAnimating");
   }

   public void removeActivityIndicatorFromSuperview(Object indicator) {
      ignored("UIActivityIndicatorView.removeFromSuperview");
   }

   public void releaseActivityIndicator(Object indicator) {
      ignored("UIActivityIndicatorView.release");
   }

   public void copyText(String text) {
      if (GraphicsEnvironment.isHeadless()) {
         ignored("UIPasteboard.setString en entorno sin pantalla");
      } else {
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
      }
   }

   public void showAlertView(String title, String message, String cancelButtonTitle) {
      LOG.log(Level.DEBUG, "Alerta aplazado a la etapa 6: {0}: {1} ({2})", title, message, cancelButtonTitle);
   }

   public void initializeCustomizer() {
      ignored("initiallizeCustomizer, pendiente de la etapa 6");
   }

   public void displayCustomizerPopup() {
      ignored("displayCustomizerPopup, pendiente de la etapa 6");
   }

   public int getNeedUpdate() {
      ignored("getNeedUpdate");
      return 0;
   }

   public void initializeAds(boolean flag, int x, int y, int value3, int value4, int value5) {
      ignored("AdManagerInitializeAds, ADR-003");
   }

   public void logEvent(String event, Map<String, String> parameters) {
      LOG.log(Level.DEBUG, "Flurry ignorado en el PC (ADR-003): {0}, parámetros {1}", event, parameters);
   }

   public void logPlayerAction(Map<String, String> action) {
      this.playerActionLog.append(action);
   }

   private static void ignored(String operation) {
      LOG.log(Level.DEBUG, "Servicio nativo ignorado en el PC: {0}", operation);
   }

   private static Path relativePath(Path root, String name) {
      Path relative = Path.of(name);
      Path result = root.resolve(relative).normalize();
      if (!relative.isAbsolute() && result.startsWith(root)) {
         return result;
      } else {
         throw new IllegalArgumentException("Ruta fuera del directorio de la aplicación: " + name);
      }
   }

   public boolean createCacheDirectory(String path, boolean withIntermediateDirectories) {
      try {
         Path target = relativePath(this.cachesDir, path);
         if (withIntermediateDirectories) {
            Files.createDirectories(target);
         } else {
            try {
               Files.createDirectory(target);
            } catch (FileAlreadyExistsException exception) {
               return Files.isDirectory(target);
            }
         }

         return true;
      } catch (IOException | IllegalArgumentException exception) {
         LOG.log(Level.WARNING, "Fallo al crear el directorio en caches: " + path, exception);
         return false;
      }
   }

   public boolean cacheFileExists(String path) {
      try {
         return Files.exists(relativePath(this.cachesDir, path));
      } catch (IllegalArgumentException exception) {
         LOG.log(Level.WARNING, "Ruta no válida en caches: " + path, exception);
         return false;
      }
   }

   public boolean writeCacheFileAtomically(String path, byte[] data) {
      try {
         writeAtomically(relativePath(this.cachesDir, path), data);
         return true;
      } catch (IOException | IllegalArgumentException exception) {
         LOG.log(Level.WARNING, "Fallo al escribir el archivo en caches: " + path, exception);
         return false;
      }
   }

   public void removeAllCacheFiles() {
      try (Stream<Path> files = Files.list(this.cachesDir)) {
         for (Path file : files.toList()) {
            if (!Files.isDirectory(file)) {
               try {
                  Files.deleteIfExists(file);
               } catch (IOException exception) {
                  LOG.log(Level.WARNING, "Fallo al borrar el archivo en caches: " + file, exception);
               }
            }
         }
      } catch (IOException exception) {
         LOG.log(Level.WARNING, "Fallo al listar caches", exception);
      }
   }

   public void playMovie(String path) {
      ignored("iDCMoviePlayer.play: " + path);
   }

   public void resumeMovie() {
      ignored("iDCMoviePlayer.resume");
   }

   public boolean isMovieFinished() {
      ignored("iDCMoviePlayer.isMovieFinished");
      return true;
   }

   public InputStream openResource(String name) {
      Objects.requireNonNull(name);
      int dot = name.indexOf(46);
      String base = dot < 0 ? name : name.substring(0, dot);
      String extension = dot < 0 ? "" : name.substring(dot + 1);
      String bundledName = extension.isEmpty() ? base : base + "." + extension;

      try {
         Path file = this.findFile(bundledName, base);
         if (file != null) {
            return Files.newInputStream(file);
         } else {
            throw new NullPointerException("Resource not found: " + name);
         }
      } catch (IOException | IllegalArgumentException exception) {
         NullPointerException failure = new NullPointerException("Could not open the resource: " + name);
         failure.initCause(exception);
         throw failure;
      }
   }

   private Path findFile(String bundledName, String cachedName) {
      Path bundled = relativePath(this.bundleDir, bundledName);
      if (Files.isRegularFile(bundled)) {
         return bundled;
      }

      Path cached = relativePath(this.cachesDir, cachedName);
      return Files.isRegularFile(cached) ? cached : null;
   }

   public IosTexture loadTexture(String filename, int colorDepth) {
      byte[] png = null;

      try {
         Path file = this.findFile(filename + ".png", filename + ".png");
         if (file != null) {
            png = Files.readAllBytes(file);
         }
      } catch (IOException exception) {
         LOG.log(Level.WARNING, "Fallo al leer el PNG: " + filename, exception);
      }

      return IosTextureLoader.load(IosDriver.currentGl(), png, colorDepth);
   }

   public void unloadTexture(int id) {
      IosTextureLoader.unload(IosDriver.currentGl(), id);
   }

   public String stringByAddingPercentEscapes(String text) {
      if (text == null) {
         return null;
      }

      byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
      StringBuilder sb = new StringBuilder(bytes.length);

      for (byte b : bytes) {
         int c = b & 255;
         if ((c < 97 || c > 122)
            && (c < 65 || c > 90)
            && (c < 48 || c > 57)
            && c != 33
            && c != 36
            && c != 38
            && c != 39
            && c != 40
            && c != 41
            && c != 42
            && c != 43
            && c != 44
            && c != 45
            && c != 46
            && c != 47
            && c != 58
            && c != 59
            && c != 61
            && c != 63
            && c != 64
            && c != 95
            && c != 126) {
            sb.append('%');
            String hex = Integer.toHexString(c).toUpperCase(Locale.ROOT);
            if (hex.length() < 2) {
               sb.append('0');
            }

            sb.append(hex);
         } else {
            sb.append((char)c);
         }
      }

      return sb.toString();
   }

   public void uploadRegistrationEvent(String userId) {
      LOG.log(Level.DEBUG, "uploadRegistrationEvent: {0}", userId);
   }

   public void cancelLocalNotifications() {
      ignored("cancelAllLocalNotifications e setApplicationIconBadgeNumber:0");
   }

   public void registerForRemoteNotifications(int types) {
      ignored("registerForRemoteNotificationTypes:" + types);
   }

   public boolean scheduleLocalNotification(double secondsFromNow, String alertBody, Map<String, String> userInfo) {
      LOG.log(Level.DEBUG, "Notificación local ignorada en el PC: {0} s, {1}, {2}", secondsFromNow, alertBody, userInfo);
      return false;
   }

   public boolean isGameCenterAvailable() {
      ignored("isGameCenterAvailable");
      return false;
   }

   public void createGameCenterManager() {
      ignored("GameCenterManager alloc/init e setDelegate:");
   }

   public void authenticateGameCenterUser() {
      ignored("authenticateLocalUser");
   }

   public boolean isGameCenterPlayerAuthenticated() {
      ignored("GKLocalPlayer isAuthenticated");
      return false;
   }

   public String gameCenterPlayerAlias() {
      ignored("GKLocalPlayer alias");
      return null;
   }

   public String gameCenterPlayerID() {
      ignored("GKLocalPlayer playerID");
      return null;
   }

   public void loadGameCenterFriends(Consumer<List<String[]>> handler) {
      ignored("loadFriendsWithCompletionHandler:");
      handler.accept(null);
   }

   public void reportGameCenterScore(String category, long score) {
      ignored("reportScore:forCategory: " + category + " " + score);
   }

   public void submitGameCenterAchievement(String identifier, double percentComplete) {
      ignored("submitAchievement:percentComplete: " + identifier + " " + percentComplete);
   }

   public boolean canSendTweet() {
      ignored("TWTweetComposeViewController canSendTweet");
      return false;
   }

   public void presentTweetComposer(String text, String url, IntConsumer onResult) {
      ignored("TWTweetComposeViewController " + text + " " + url);
   }

   public IFacebookConnection facebookConnection(String apiKey, String secret, Consumer<FacebookResponse> listener) {
      if (this.facebookConnection == null) {
         this.facebookConnection = new DChocFacebookConnection();
         if (listener != null) {
            this.facebookConnection.setFacebookResponseListener(listener::accept);
         }
      }

      return this.facebookConnection;
   }

   public void showPlayHavenContent(String placement, IntConsumer listener) {
      ignored("PHPublisherContentRequest " + placement);
   }

   public void startChartboostSession() {
      ignored("ChartBoostCOntroller startChartBoostSession");
   }

   public boolean isImpactSupported() {
      ignored("ApplifierController isImpactSupported");
      return true;
   }

   public boolean showImpactVideoAds() {
      ignored("showImpactVideoAds");
      return false;
   }

   public void hideAd() {
      ignored("AdManagerHideAd");
   }

   public void showAd(int type) {
      ignored("AdManagerShowAd");
   }

    public Integer energyChargeTimeOverride() {
       return this.energyChargeTimeMs > 0 ? this.energyChargeTimeMs : null;
    }

    public Boolean cashCheatOverride() {
       return this.cashCheat ? Boolean.TRUE : null;
    }

    public Float spawnMultiplierOverride() {
      return this.spawnMultiplier > 1.0F ? this.spawnMultiplier : null;
   }

   public Long neighborIntervalOverride() {
      return this.neighborIntervalMs > 0L ? this.neighborIntervalMs : null;
   }

   public List<LocalProfile> localProfiles() {
      return this.localProfiles;
   }

   public boolean httpHeadHasResponse(String url) {
      try {
         HttpURLConnection connection = (HttpURLConnection)URI.create(url).toURL().openConnection();
         connection.setRequestMethod("HEAD");
         connection.setConnectTimeout(60000);
         connection.setReadTimeout(60000);

         try {
            connection.getResponseCode();
            return true;
         } finally {
            connection.disconnect();
         }
      } catch (IOException | IllegalArgumentException exception) {
         LOG.log(Level.DEBUG, "HEAD sin respuesta: {0}", exception.toString());
         return false;
      }
   }

   public void logNativeMessage(String message) {
      LOG.log(Level.DEBUG, "NSLog: {0}", message);
   }
}
