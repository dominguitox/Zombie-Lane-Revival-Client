package zlport.launcher;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public record Config(
   int width,
   int height,
   Path dataDir,
   boolean glDebug,
   String midlet,
   boolean visible,
   boolean audio,
   Config.Placement placement,
   Config.Preferences preferences
) {
   public static final int DEFAULT_WIDTH = 1280;
   public static final int DEFAULT_HEIGHT = 720;
   public static final String DEFAULT_MIDLET = "com.dchoc.DCiDead";
   public static final String BENCH_MIDLET = "zlport.launcher.bench.LoadingScreenBench";
   public static final String DATA_DIR_PROPERTY = "zlport.dataDir";
   public static final String GL_DEBUG_PROPERTY = "zlport.gl.debug";
   public static final String AUDIO_PROPERTY = "zlport.audio";
   static final String USAGE = "Uso: zombie-lane [opciones]\n  --size LxA        ventana de ese tamaño, sin maximizar (por defecto: maximizada; restaurada, 1280x720)\n  --data-dir DIR    datos del usuario (por defecto %APPDATA%\\ZombieLane; o -Dzlport.dataDir)\n  --gl-debug        comprueba glGetError en cada llamada de GL (o -Dzlport.gl.debug=true)\n  --no-audio        sin sonido (o -Dzlport.audio=false)\n  --midlet CLASE    MIDlet a ejecutar (por defecto: el juego, com.dchoc.DCiDead; banco de la pantalla de\n                    carga: zlport.launcher.bench.LoadingScreenBench)\n  --help            esta ayuda";

   public Config(
      int width,
      int height,
      Path dataDir,
      boolean glDebug,
      String midlet,
      boolean visible,
      boolean audio,
      Config.Placement placement,
      Config.Preferences preferences
   ) {
      if (width > 0 && height > 0 && width <= 16384 && height <= 16384) {
         Objects.requireNonNull(placement, "placement");
         Objects.requireNonNull(preferences, "preferences");
         this.width = width;
         this.height = height;
         this.dataDir = dataDir;
         this.glDebug = glDebug;
         this.midlet = midlet;
         this.visible = visible;
         this.audio = audio;
         this.placement = placement;
         this.preferences = preferences;
      } else {
         throw new IllegalArgumentException("Tamaño de ventana no válido: " + width + "x" + height);
      }
   }

   public Config(int width, int height, Path dataDir, boolean glDebug, String midlet, boolean visible, boolean audio) {
      this(width, height, dataDir, glDebug, midlet, visible, audio, Config.Placement.CENTERED, Config.Preferences.DEFAULTS);
   }

   public Config(int width, int height, Path dataDir, boolean glDebug, String midlet, boolean visible) {
      this(width, height, dataDir, glDebug, midlet, visible, true);
   }

   static Optional<Config> parse(String... args) {
      return parse(Settings.defaults(), args);
   }

   static Optional<Config> parse(Settings settings, String... args) {
      return parse(settings, defaultDataDir(), args);
   }

   static Optional<Config> parse(Settings settings, Path defaultDirectory, String... args) {
      int width = settings.width() != null ? settings.width() : DEFAULT_WIDTH;
      int height = settings.height() != null ? settings.height() : DEFAULT_HEIGHT;
      Path dataDir = defaultDirectory;
      boolean glDebug = resolveGlDebugFlag(args, settings);
      boolean audio = resolveAudioFlag(args, settings);
      String midlet = DEFAULT_MIDLET;
      Config.Placement placement = new Config.Placement(settings.x(), settings.y(), settings.maximized(), settings.fullscreen());
      Config.Preferences preferences = new Config.Preferences(settings.pauseOnFocusLoss(), settings.wheelStep());

      for (int i = 0; i < args.length; i++) {
         String arg = args[i];
         switch (arg) {
            case "--help":
            case "-h":
               return Optional.empty();
            case "--gl-debug":
               glDebug = true;
               break;
            case "--no-audio":
               audio = false;
               break;
            case "--size":
               String value = value(args, ++i, arg);
               String[] parts = value.toLowerCase(Locale.ROOT).split("x");
               if (parts.length != 2) {
                  throw new IllegalArgumentException("--size espera LxA (ej.: 1280x800), recibido '" + value + "'");
               }

               try {
                  width = Integer.parseInt(parts[0].strip());
                  height = Integer.parseInt(parts[1].strip());
               } catch (NumberFormatException e) {
                  throw new IllegalArgumentException("--size espera LxA (ej.: 1280x800), recibido '" + value + "'");
               }

               placement = Config.Placement.CENTERED;
               break;
            case "--data-dir":
               dataDir = Path.of(value(args, ++i, arg));
               break;
            case "--midlet":
               midlet = value(args, ++i, arg);
               break;
            default:
               throw new IllegalArgumentException("Opción desconocida: " + arg);
         }
      }

      return Optional.of(new Config(width, height, dataDir, glDebug, midlet, true, audio, placement, preferences));
   }

   /** GL debug: the --gl-debug flag wins, then the -Dzlport.gl.debug property, then the gl.debug key of zombie-lane.properties. */
   private static boolean resolveGlDebugFlag(String[] args, Settings settings) {
      for (String arg : args) {
         if (arg.equals("--gl-debug") || arg.startsWith("--gl-debug=")) {
            return true;
         }
      }

      String property = System.getProperty(GL_DEBUG_PROPERTY);
      if (property != null && !property.isBlank()) {
         return "true".equalsIgnoreCase(property.strip());
      }
      return settings.glDebug();
   }

   /** Audio: the --no-audio flag wins, then the -Dzlport.audio property, then the audio key of zombie-lane.properties. */
   private static boolean resolveAudioFlag(String[] args, Settings settings) {
      for (String arg : args) {
         if (arg.equals("--no-audio")) {
            return false;
         }
      }

      String property = System.getProperty(AUDIO_PROPERTY);
      if (property != null && !property.isBlank()) {
         return !"false".equalsIgnoreCase(property.strip());
      }
      return settings.audio();
   }

   private static String value(String[] args, int i, String option) {
      if (i >= args.length) {
         throw new IllegalArgumentException(option + " necesita un valor");
      } else {
         return args[i];
      }
   }

   static Path defaultDataDir() {
      String property = System.getProperty("zlport.dataDir");
      if (property != null && !property.isBlank()) {
         return Path.of(property);
      }

      String appData = System.getenv("APPDATA");
      return appData != null && !appData.isBlank() ? Path.of(appData, "ZombieLane") : Path.of(System.getProperty("user.home"), ".zombielane");
   }

   Config withVisible(boolean value) {
      return new Config(this.width, this.height, this.dataDir, this.glDebug, this.midlet, value, this.audio, this.placement, this.preferences);
   }

   public record Placement(Integer x, Integer y, boolean maximized, boolean fullscreen) {
      public static final Config.Placement CENTERED = new Config.Placement(null, null, false, false);
      public static final Config.Placement FIRST_OPEN = new Config.Placement(null, null, true, false);
   }

   public record Preferences(boolean pauseOnFocusLoss, float wheelStep) {
      public static final Config.Preferences DEFAULTS = new Config.Preferences(true, 1.06F);
   }
}
