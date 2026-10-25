package zlport.launcher;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;

final class Settings {
   static final String FILE_NAME = "zombie-lane.properties";
   static final String WIDTH = "janela.largura";
   static final String HEIGHT = "janela.altura";
   static final String X = "janela.x";
   static final String Y = "janela.y";
   static final String MAXIMIZED = "janela.maximizada";
   static final String FULLSCREEN = "telaCheia";
   static final String PAUSE_ON_FOCUS_LOSS = "pausarSemFoco";
   static final String WHEEL_STEP = "roda.passo";
   static final String LOG_LEVEL = "log.nivel";
    static final String ENERGY_RECHARGE = "energia.recarga";
    static final String SPAWN_MULTIPLIER = "spawn.multiplicador";
    static final String NEIGHBOR_INTERVAL = "vizinho.intervalo";
    static final String CASH_CHEAT = "trucos.billetes";
    static final String AUDIO = "audio";
   static final String GL_DEBUG = "gl.debug";
   static final float MIN_WHEEL_STEP = 1.01F;
   static final float MAX_WHEEL_STEP = 1.5F;
   static final int ENERGY_RECHARGE_DEFAULT = 30;
   static final int MAX_ENERGY_RECHARGE = 3600;
   static final float SPAWN_MULTIPLIER_DEFAULT = 2.0F;
   static final float MIN_SPAWN_MULTIPLIER = 1.0F;
   static final float MAX_SPAWN_MULTIPLIER = 5.0F;
   static final int NEIGHBOR_INTERVAL_DEFAULT = 5;
   static final int MAX_NEIGHBOR_INTERVAL = 1440;
   private static final List<String> KEYS = List.of(
      WIDTH,
      HEIGHT,
      X,
      Y,
      MAXIMIZED,
      FULLSCREEN,
      PAUSE_ON_FOCUS_LOSS,
      WHEEL_STEP,
      LOG_LEVEL,
      ENERGY_RECHARGE,
      SPAWN_MULTIPLIER,
      NEIGHBOR_INTERVAL,
      CASH_CHEAT,
      AUDIO,
      GL_DEBUG
   );
   private final Properties raw;
   private final List<String> warnings = new ArrayList<>();
   private final Integer width;
   private final Integer height;
   private final Integer x;
   private final Integer y;
   private final boolean maximized;
   private final boolean fullscreen;
   private final boolean pauseOnFocusLoss;
   private final float wheelStep;
   private final String logLevel;
    private final int energyRecharge;
    private final float spawnMultiplier;
    private final int neighborInterval;
    private final boolean cashCheat;
    private final boolean audio;
    private final boolean glDebug;

   static Settings defaults() {
      return new Settings(new Properties(), null);
   }

   /** Builds from properties that already exist (the launcher uses this to validate the values it writes). */
   static Settings fromProperties(Properties p) {
      return new Settings((Properties)p.clone(), null);
   }

   static Settings load(Path dataDir) {
      Path file = dataDir.resolve("zombie-lane.properties");
      if (!Files.isRegularFile(file)) {
         return defaults();
      }

      Properties p = new Properties();

      try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
         p.load(r);
      } catch (IOException | IllegalArgumentException e) {
         return new Settings(new Properties(), "No se pudo leer " + file + " (" + e.getMessage() + "); se usan los valores por defecto");
      }

      return new Settings(p, null);
   }

   private Settings(Properties raw, String loadWarning) {
      this.raw = raw;
      if (loadWarning != null) {
         this.warnings.add(loadWarning);
      }

      Integer w = this.integer("janela.largura", 640, 16384);
      Integer h = this.integer("janela.altura", 360, 16384);
      boolean size = w != null && h != null;
      this.width = size ? w : null;
      this.height = size ? h : null;
      Integer px = this.integer("janela.x", -32768, 32767);
      Integer py = this.integer("janela.y", -32768, 32767);
      boolean pos = px != null && py != null;
      this.x = pos ? px : null;
      this.y = pos ? py : null;
      this.maximized = this.bool(MAXIMIZED, true);
      this.fullscreen = this.bool(FULLSCREEN, false);
      this.pauseOnFocusLoss = this.bool(PAUSE_ON_FOCUS_LOSS, true);
      this.wheelStep = this.decimal(WHEEL_STEP, 1.06F, MIN_WHEEL_STEP, MAX_WHEEL_STEP);
      this.logLevel = this.level(LOG_LEVEL, "INFO");
      Integer recharge = this.integer(ENERGY_RECHARGE, 1, MAX_ENERGY_RECHARGE);
      this.energyRecharge = recharge != null ? recharge : ENERGY_RECHARGE_DEFAULT;
      this.spawnMultiplier = this.decimal(SPAWN_MULTIPLIER, SPAWN_MULTIPLIER_DEFAULT, MIN_SPAWN_MULTIPLIER, MAX_SPAWN_MULTIPLIER);
      Integer interval = this.integer(NEIGHBOR_INTERVAL, 1, MAX_NEIGHBOR_INTERVAL);
      this.neighborInterval = interval != null ? interval : NEIGHBOR_INTERVAL_DEFAULT;
      this.cashCheat = this.bool(CASH_CHEAT, false);
      this.audio = this.bool(AUDIO, true);
      this.glDebug = this.bool(GL_DEBUG, false);
   }

   Integer width() {
      return this.width;
   }

   Integer height() {
      return this.height;
   }

   Integer x() {
      return this.x;
   }

   Integer y() {
      return this.y;
   }

   boolean maximized() {
      return this.maximized;
   }

   boolean fullscreen() {
      return this.fullscreen;
   }

   boolean pauseOnFocusLoss() {
      return this.pauseOnFocusLoss;
   }

   float wheelStep() {
      return this.wheelStep;
   }

   String logLevel() {
      return this.logLevel;
   }

   int energyRechargeSeconds() {
      return this.energyRecharge;
   }

   float spawnMultiplier() {
      return this.spawnMultiplier;
   }

    int neighborIntervalMinutes() {
       return this.neighborInterval;
    }

    /** The infinite-cash cheat; offline play only (the server launch ignores it). */
    boolean cashCheat() {
       return this.cashCheat;
    }

    boolean audio() {
      return this.audio;
   }

   boolean glDebug() {
      return this.glDebug;
   }

   Properties raw() {
      return (Properties)this.raw.clone();
   }

   List<String> warnings() {
      return List.copyOf(this.warnings);
   }

   private String text(String key) {
      String value = this.raw.getProperty(key);
      return value == null ? null : value.strip();
   }

   private Integer integer(String key, int min, int max) {
      String value = this.text(key);
      if (value == null) {
         return null;
      }

      try {
         int n = Integer.parseInt(value);
         if (n >= min && n <= max) {
            return n;
         }
      } catch (NumberFormatException var6) {
      }

      this.invalid(key, value, "inteiro de " + min + " a " + max);
      return null;
   }

   private boolean bool(String key, boolean def) {
      String value = this.text(key);
      if (value == null) {
         return def;
      }

      if (value.equalsIgnoreCase("true")) {
         return true;
      }

      if (value.equalsIgnoreCase("false")) {
         return false;
      }

      this.invalid(key, value, "true ou false");
      return def;
   }

   private float decimal(String key, float def, float min, float max) {
      String value = this.text(key);
      if (value == null) {
         return def;
      }

      try {
         float f = Float.parseFloat(value.replace(',', '.'));
         if (!Float.isNaN(f) && !Float.isInfinite(f) && f >= min && f <= max) {
            return f;
         }
      } catch (NumberFormatException var7) {
      }

      this.invalid(key, value, "decimal de " + min + " a " + max);
      return def;
   }

   private String level(String key, String def) {
      String value = this.text(key);
      if (value == null) {
         return def;
      }

      try {
         return Level.parse(value.toUpperCase(Locale.ROOT)).getName();
      } catch (IllegalArgumentException e) {
         this.invalid(key, value, "SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL ou OFF");
         return def;
      }
   }

   private void invalid(String key, String value, String expected) {
      this.warnings.add("zombie-lane.properties: " + key + "=" + value + " no es válido (" + expected + "); se usa el valor por defecto");
   }

   static void saveWindow(Path dataDir, Launcher.WindowState window) throws IOException {
      Properties current = load(dataDir).raw;
      writeOptions(dataDir, render(current, window));
   }

   /**
    * Writes the launcher options (all the game keys plus the new audio/gl.debug
    * ones) over the profile's zombie-lane.properties and preserves every other
    * key it does not understand. Missing x/y geometry is not written: the game
    * records it on exit.
    */
   static void saveLauncherOptions(Path dataDir, Launcher.WindowState window, java.util.Map<String, String> own) throws IOException {
      Properties merged = load(dataDir).raw;
      for (java.util.Map.Entry<String, String> entry : own.entrySet()) {
         merged.setProperty(entry.getKey(), entry.getValue());
      }
      writeOptions(dataDir, render(merged, window));
   }

   private static void writeOptions(Path dataDir, String text) throws IOException {
      Files.createDirectories(dataDir);
      Path file = dataDir.resolve("zombie-lane.properties");
      Path tmp = dataDir.resolve("zombie-lane.properties.tmp");
      Files.writeString(tmp, text, StandardCharsets.UTF_8);

      try {
         Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (AtomicMoveNotSupportedException e) {
         Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
      }
   }

   static String render(Properties current, Launcher.WindowState w) {
      String n = System.lineSeparator();
      StringBuilder s = new StringBuilder();
      s.append("# Zombie Lane: configuración. Un valor inválido usa el valor por defecto (el aviso queda en logs/).").append(n);
      s.append("# La ventana y la pantalla completa las guarda el juego al cerrar; casi todas las claves también se pueden cambiar desde el lanzador gráfico.").append(n);
      s.append(n);
      s.append("# Ventana restaurada: tamaño y posición del área de dibujo, en píxeles.").append(n);
      s.append(WIDTH).append('=').append(w.width()).append(n);
      s.append(HEIGHT).append('=').append(w.height()).append(n);
      if (w.x() != null) {
         s.append(X).append('=').append(w.x()).append(n);
      }
      if (w.y() != null) {
         s.append(Y).append('=').append(w.y()).append(n);
      }
      s.append("# Abre maximizada (true/false).").append(n);
      s.append(MAXIMIZED).append('=').append(w.maximized()).append(n);
      s.append("# Abre en pantalla completa (true/false); F11 o Alt+Enter alternan.").append(n);
      s.append(FULLSCREEN).append('=').append(w.fullscreen()).append(n);
      s.append(n);
      s.append("# Congela el juego y pausa el sonido cuando la ventana pierde el foco (true/false).").append(n);
      s.append(PAUSE_ON_FOCUS_LOSS).append('=').append(current.getProperty(PAUSE_ON_FOCUS_LOSS, "true").strip()).append(n);
      s.append("# Zoom por paso de la rueda del ratón, de 1.01 a 1.5.").append(n);
      s.append(WHEEL_STEP).append('=').append(current.getProperty(WHEEL_STEP, "1.06").strip()).append(n);
      s.append("# Sonido del juego (true/false).").append(n);
      s.append(AUDIO).append('=').append(current.getProperty(AUDIO, "true").strip()).append(n);
      s.append("# Depuración de OpenGL en cada llamada (true/false; diagnóstico).").append(n);
      s.append(GL_DEBUG).append('=').append(current.getProperty(GL_DEBUG, "false").strip()).append(n);
      s.append("# Nivel del log (logs/zombie-lane.log): SEVERE, WARNING, INFO o FINE (con la depuración del juego).").append(n);
      s.append(LOG_LEVEL).append('=').append(current.getProperty(LOG_LEVEL, "INFO").strip()).append(n);
      s.append("# Solo la versión 1.4.4: segundos para recargar 1 de energía, de 1 a 3600 (el iPhone usaba 300). La casilla \"energía infinita\" del lanzador escribe 1; con la casilla desmarcada el lanzador conserva el valor que haya aquí.").append(n);
      s.append(ENERGY_RECHARGE).append('=').append(current.getProperty(ENERGY_RECHARGE, String.valueOf(ENERGY_RECHARGE_DEFAULT)).strip()).append(n);
      s.append("# Solo la versión 1.4.4: multiplica los zombis que aparecen (por vez y el tope en el mapa), de 1.0 (el del iPhone) a 5.0.").append(n);
      s.append(SPAWN_MULTIPLIER).append('=').append(current.getProperty(SPAWN_MULTIPLIER, String.valueOf(SPAWN_MULTIPLIER_DEFAULT)).strip()).append(n);
      s.append("# Solo la versión 1.4.4: minutos entre los premios de la visita a Rob y el reinicio de las 5 acciones, de 1 a 1440 (el iPhone usaba 1440 = 24 h).").append(n);
      s.append(NEIGHBOR_INTERVAL).append('=').append(current.getProperty(NEIGHBOR_INTERVAL, String.valueOf(NEIGHBOR_INTERVAL_DEFAULT)).strip()).append(n);
      s.append("# Trucos locales: billetes infinitos (true/false); solo surte efecto lanzando en local.").append(n);
      s.append(CASH_CHEAT).append('=').append(current.getProperty(CASH_CHEAT, "false").strip()).append(n);
      Properties unknown = new Properties();

      for (String key : current.stringPropertyNames()) {
         if (!KEYS.contains(key)) {
            unknown.setProperty(key, current.getProperty(key));
         }
      }

      if (!unknown.isEmpty()) {
         StringWriter text = new StringWriter();

         try {
            unknown.store(text, null);
         } catch (IOException e) {
            throw new IllegalStateException(e);
         }

         List<String> lines = text.toString().lines().filter(linex -> !linex.isBlank() && !linex.startsWith("#")).sorted().toList();
         s.append(n);
         s.append("# Otras claves (esta versión no las usa)").append(n);

         for (String line : lines) {
            s.append(line).append(n);
         }
      }

      return s.toString();
   }
}
