package zlport.launcher;

import com.dchoc.toolkit.Toolkit;
import java.io.IOException;
import java.lang.System.Logger;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import zlport.dev.DevMode;
import zlport.ios.Ios;
import zlport.ios.LocalProfile;
import zlport.ios.runtime.IosDriver;
import zlport.ios.runtime.PcIosPlatform;
import zlport.j2me.Platform;
import zlport.launcher.Launcher.FrameListener;
import zlport.launcher.Launcher.Result;
import zlport.launcher.Presentation.Size;

public final class IosMain {
   static final int LOGICAL_WIDTH = 1136;
   static final int LOGICAL_HEIGHT = 640;
   static final String MIDLET = "com.dchoc.DCiDead";
   static final String BUNDLE_PROPERTY = "zlport.ios.bundle";
   static final String CUSTOM_MISSIONS_CACHE_PROPERTY = "zlport.custom.missions.cache";
   static final String USAGE = "Uso: zombie-lane-ios [opciones]\n  --data-dir DIR    raíz local de los perfiles (por defecto %APPDATA%\\ZombieLane-1.4.4; o -Dzlport.dataDir)\n  --no-profile-picker usa directamente el directorio indicado\n  --no-launcher     sin lanzador gráfico: como antes (selector de perfil si corresponde)\n  --mode local|server preselecciona el modo del lanzador (local = sin red; server = conexión)\n  --server HOST:PUERTO servidor preseleccionado del lanzador (por ej.: 192.168.1.10:6767)\n  --user NOMBRE     usuario preseleccionado del lanzador (identidad en modo servidor)\n  --gl-debug        comprueba glGetError en cada llamada de GL (o -Dzlport.gl.debug=true)\n  --no-audio        sin sonido (o -Dzlport.audio=false)\n  --help            esta ayuda\nPantalla lógica fija: 1136x640. --size no se acepta; la ventana se puede redimensionar.\nPaquete de la app: -Dzlport.ios.bundle=DIR; por defecto game-assets-ios junto a la instalación.\nMisiones globales: -Dzlport.ios.missions=DIR; por defecto <data-dir>\\missions, compartido por todos los perfiles.";
   private static final Logger LOG = System.getLogger("zlport.launcher");

   private IosMain() {
   }

   public static void main(String[] args) {
      ConsoleLogging.install();

      if (DevMode.enabled()) {
         System.out.println("Zombie Lane " + DevMode.banner() + " edición de desarrollo: ganchos de desarrollo activos");
      }

      LaunchFlags flags;
      try {
         flags = parseLaunchFlags(args);
      } catch (IllegalArgumentException exception) {
         System.err.println(exception.getMessage());
         System.err.println(USAGE);
         System.exit(2);
         return;
      }
      String[] rest = flags.rest();

      Optional<Config> parsed;
      try {
         parsed = parse(Settings.defaults(), withoutOption(rest, "--no-profile-picker"));
      } catch (IllegalArgumentException exception) {
         System.err.println(exception.getMessage());
         System.err.println(USAGE);
         System.exit(2);
         return;
      }

      if (parsed.isEmpty()) {
         System.out.println(USAGE);
      } else {
         Config initialConfig = parsed.orElseThrow();
         Path dataDir = initialConfig.dataDir();
         Path dataRoot = dataDir;
         boolean usesPicker = usesProfilePicker(rest);
         PreLauncherState state = stateSeed(dataRoot);
         Optional<PreLauncherDialog.Selection> selection = Optional.empty();

         // Language preference: launcher.properties may not exist at all (first
         // run) or may predate this feature. In both cases the prompt is shown
         // once, before the launcher; --no-launcher uses the system language.
         String language = state.language();
         if (language.isEmpty()) {
            if (!flags.noLauncher()) {
               Optional<String> chosen = LanguageDialog.show(LauncherLanguage.systemDefault().code());
               if (chosen.isEmpty()) {
                  return;
               }
               language = chosen.orElseThrow();
               state = state.withLanguage(language);
               LauncherText.setLanguage(language);
               try {
                  state.save(dataRoot);
               } catch (IOException exception) {
                  WindowsDialogs.showError(
                     "Zombie Lane", LauncherText.format("error.saveLanguage", exception.getMessage())
                  );
                  return;
               }
            } else {
               language = LauncherLanguage.systemDefault().code();
            }
         }
         LauncherText.setLanguage(language);
         GameLanguage.exposeProperty(language);
         PreLauncherState runState = state;

         if (!flags.noLauncher()) {
            // Graphical launcher: one screen with mode, profile and options.
            try {
               List<LocalProfiles.ProfileInfo> profiles = LocalProfiles.profiles(dataDir);
               PreLauncherState seed = seedFrom(state, flags);
               selection = PreLauncherDialog.show(dataRoot, seed, usesPicker, profiles);
               if (selection.isEmpty()) {
                  return;
               }
               applySelection(selection.orElseThrow());
               if (selection.orElseThrow().profile() != null) {
                  dataDir = selection.orElseThrow().profile().directory();
               }
            } catch (IOException exception) {
               System.err.println("No se pudieron cargar los perfiles locales: " + exception.getMessage());
               System.exit(1);
               return;
            }
         } else {
            // No graphical launcher: the previous flow, plus mode/connection from flags.
            PreLauncherState.Mode bootMode = flags.mode().orElse(state.mode());
            if (usesPicker && bootMode == PreLauncherState.Mode.LOCAL) {
               try {
                  Optional<Path> selectedProfile = LocalProfiles.choose(dataDir);
                  if (selectedProfile.isEmpty()) {
                     return;
                  }
                  dataDir = selectedProfile.orElseThrow();
               } catch (IOException exception) {
                  System.err.println("No se pudieron cargar los perfiles locales: " + exception.getMessage());
                  System.exit(1);
                  return;
               }
            }
            applyWithoutLauncher(flags, state);
         }

         // Account identity rule (see docs/decisions.md "Account identity"):
         // in SERVER mode the launcher authenticates and the server-issued
         // numeric account id becomes the identity for this run; the local
         // folder is only save storage. The typed user is the fallback
         // identity when no dialog ran (scripted launches). In LOCAL mode
         // nothing talks to a server, so the local profile folder name stays
         // as today. An explicit -Dzlport.account.id always wins.
         boolean serverMode = selection.map(PreLauncherDialog.Selection::mode)
            .orElseGet(() -> modeOf(flags).orElse(runState.mode())) == PreLauncherState.Mode.SERVER;
         String identityUser = selection.map(PreLauncherDialog.Selection::user)
            .orElseGet(() -> userIdentity(flags, runState));
         String selectedAccountId = selection.map(PreLauncherDialog.Selection::accountId).orElse(null);
         String accountId = selectedAccountId;
         if (accountId == null) {
            accountId = accountIdFor(dataDir, serverMode, identityUser);
         }
         if (System.getProperty("zlport.account.id") == null && accountId != null) {
            System.setProperty("zlport.account.id", accountId);
            if (serverMode) {
               LOG.log(java.lang.System.Logger.Level.INFO, "Cuenta de servidor autenticada");
            }
         }

         // SERVER mode: the account save lives on the server. Download it into
         // a throwaway directory; the game writes only there and the final
         // state is uploaded back when the run ends. Local profiles are never
         // read for a server account.
         ServerConnection saveServer = selection.map(PreLauncherDialog.Selection::server).orElse(null);
         if (serverMode && saveServer == null) {
            saveServer = ServerConnection.parseServerUrl(System.getProperty("zlport.server.url")).orElse(null);
         }
         ServerSaveSync.Session serverSave = null;
         Path runtimeDir = dataDir;
         if (serverMode) {
            if (saveServer == null || accountId == null) {
               String message = "El modo servidor necesita una cuenta autenticada y una conexión al servidor.";
               System.err.println(message);
               if (selection.isPresent()) WindowsDialogs.showError("Zombie Lane", message);
               System.exit(1);
               return;
            }
            try {
               serverSave = ServerSaveSync.download(saveServer, accountId, dataRoot);
               runtimeDir = serverSave.directory();
               System.setProperty(CUSTOM_MISSIONS_CACHE_PROPERTY, serverSave.customMissionsCache().toString());
               LOG.log(java.lang.System.Logger.Level.INFO, "Partida del servidor descargada en una carpeta temporal de sesión");
            } catch (IOException | RuntimeException exception) {
               String message = "No se pudo cargar la partida del servidor: " + exception.getMessage();
               System.err.println(message);
               if (selection.isPresent()) WindowsDialogs.showError("Zombie Lane", message);
               System.exit(1);
               return;
            }
         }

         // Apply the launcher language to the selected profile before the game
         // reads settings. A new profile has no settings record yet: the
         // zlport.language property lets Game.loadSettings() create a valid
         // full record instead of writing a one-byte file here.
         try {
            GameLanguage.apply(runtimeDir, language);
         } catch (IOException | RuntimeException exception) {
            LOG.log(java.lang.System.Logger.Level.WARNING,
               "No se pudo aplicar el idioma al perfil; se usará la propiedad del juego", exception);
         }

         // Local profiles are only injected as neighbors in LOCAL mode: with a
         // server, the roster comes from the server's account list instead.
         List<LocalProfile> localProfiles;
         if (serverMode) {
            localProfiles = List.of();
         } else {
            try {
               localProfiles = List.copyOf(LocalProfiles.neighbors(dataDir));
            } catch (IOException exception) {
               System.err.println("No se pudieron cargar los vecinos locales: " + exception.getMessage());
               System.exit(1);
               return;
            }
         }

         Settings settings = Settings.load(dataDir);
         String level = System.getProperty("zlport.log", settings.logLevel());
         ConsoleLogging.install(level);
         LogFile logFile = LogFile.install(dataRoot, Level.parse(level));
         settings.warnings().forEach(warning -> LOG.log(java.lang.System.Logger.Level.WARNING, warning));
         String[] configArgs = usesPicker ? withoutDataDirectory(rest) : withoutOption(rest, "--no-profile-picker");
         Config config = parse(settings, runtimeDir, configArgs).orElseThrow();

         // Informe de la alpha: consentimiento una vez, recuperación de la
         // sesión anterior si murió y marca de la sesión actual.
         boolean reportConsent = AlphaConsent.askIfNeeded(dataRoot, !flags.noLauncher());
         SessionReport.recover(dataRoot, reportConsent);
         if (reportConsent) {
            SessionReport.uploadPending(dataRoot, saveServer, accountId);
         }
         SessionReport.Session reportSession = SessionReport.begin(
            dataRoot,
            runtimeDir,
            serverMode ? "servidor" : "local",
            accountId,
            identityUser,
            saveServer
         );
         printStartupSummary(dataRoot, runtimeDir, serverMode, saveServer, accountId, identityUser, reportConsent);

         Result result = null;
         int exitCode;
         try {
            if (DevMode.enabled()) {
               applyDevMissions(selection.map(PreLauncherDialog.Selection::devMissions).orElse(state.devMissions()));
            }
            String missionSource = configureMissionSource(serverMode, dataRoot);
            if (missionSource == null) {
               LOG.log(java.lang.System.Logger.Level.INFO,
                  "Modo servidor: misiones compartidas desactivadas; las definiciones vienen del paquete y del servidor");
            } else {
               LOG.log(java.lang.System.Logger.Level.INFO, "Directorio de misiones compartidas: " + missionSource);
            }
            installRuntime(
               bundleDirectory(),
               config.dataDir(),
               settings.energyRechargeSeconds(),
               settings.spawnMultiplier(),
               settings.neighborIntervalMinutes(),
               // Offline helpers do not reach a server session.
               !serverMode && settings.cashCheat(),
               localProfiles
            );
            logSessionAction("sesion_inicio", serverMode ? "servidor" : "local", accountId);
            result = createLauncher(config, (frame, width, height) -> {}).run();
            if (result.window() != null) {
               try {
                  Settings.saveWindow(dataRoot, result.window());
               } catch (IOException | RuntimeException exception) {
                  LOG.log(java.lang.System.Logger.Level.WARNING, "No se pudo guardar la configuración de la ventana", exception);
               }
            }

            exitCode = result.exitCode();
         } catch (RuntimeException | Error exception) {
            LOG.log(java.lang.System.Logger.Level.ERROR, "Fallo fuera del lanzador iOS", exception);
            exitCode = 1;
         } finally {
            System.clearProperty(CUSTOM_MISSIONS_CACHE_PROPERTY);
            // Copiar las acciones antes de borrar el directorio temporal del save.
            SessionReport.capture(reportSession);
            // Read the language before the server-save directory is deleted, so
            // a change made in the in-game options survives the session.
            syncLanguage(dataRoot, runtimeDir);
            GameLanguage.clearProperty();
            if (serverSave != null) {
               try {
                  ServerSaveSync.upload(serverSave);
                  LOG.log(java.lang.System.Logger.Level.INFO, "Partida subida al servidor");
               } catch (IOException | RuntimeException exception) {
                  LOG.log(java.lang.System.Logger.Level.WARNING, "no se pudo subir la partida al servidor", exception);
               } finally {
                  ServerSaveSync.delete(serverSave);
               }
            }
            logSessionAction("sesion_fin", serverMode ? "servidor" : "local", accountId);
            Ios.uninstall();
         }

         if (result != null) {
            logSessionSummary(result, exitCode);
         }
         Path crashReport = null;
         if (exitCode != 0) {
            crashReport = reportFailure(config, logFile, dataRoot);
         }

         if (logFile != null) {
            logFile.close();
         }
         SessionReport.finish(reportSession, reportConsent, crashReport, result, exitCode);

         System.exit(exitCode);
      }
   }

   static Optional<Config> parse(Settings settings, String... args) {
      return parse(settings, defaultDataDir(), args);
   }

   /** New pre-launcher flags, split from the remaining arguments (which still go to Config.parse). */
   static record LaunchFlags(
      Optional<PreLauncherState.Mode> mode, Optional<ServerConnection> server, Optional<String> user, boolean noLauncher, String[] rest
   ) {
   }

   /** Splits --mode/--server/--user/--no-launcher from the rest; the rest goes untouched to the classic parser. */
   static LaunchFlags parseLaunchFlags(String... args) {
      List<String> rest = new ArrayList<>();
      Optional<PreLauncherState.Mode> mode = Optional.empty();
      Optional<ServerConnection> server = Optional.empty();
      Optional<String> user = Optional.empty();
      boolean noLauncher = false;

      for (int i = 0; i < args.length; i++) {
         String arg = args[i];
         if (arg.equals("--mode")) {
            String value = value(args, ++i, arg);
            mode = Optional.of(modeOf(value));
         } else if (arg.equals("--server")) {
            String value = value(args, ++i, arg);
            try {
               server = Optional.of(ServerConnection.parseEndpoint(value));
            } catch (IllegalArgumentException exception) {
               throw new IllegalArgumentException("--server " + exception.getMessage());
            }
         } else if (arg.equals("--user")) {
            String value = value(args, ++i, arg);
            String error = ServerConnection.validateUser(value);
            if (error != null) {
               throw new IllegalArgumentException("--user: " + error);
            }
            user = Optional.of(value.strip());
         } else if (arg.equals("--no-launcher")) {
            noLauncher = true;
         } else {
            rest.add(arg);
         }
      }

      return new LaunchFlags(mode, server, user, noLauncher, rest.toArray(String[]::new));
   }

   /** Mode typed by the user: local, server, or servidor (Spanish alias). */
   static PreLauncherState.Mode modeOf(String text) {
      String mode = text.toLowerCase(Locale.ROOT);
      if ("local".equals(mode)) {
         return PreLauncherState.Mode.LOCAL;
      }
      if ("server".equals(mode) || "servidor".equals(mode)) {
         return PreLauncherState.Mode.SERVER;
      }
      throw new IllegalArgumentException("El modo '" + text + "' no es válido: --mode local o --mode server.");
   }

   private static String value(String[] args, int i, String option) {
      if (i >= args.length) {
         throw new IllegalArgumentException(option + " necesita un valor");
      }
      String value = args[i];
      if (value.startsWith("--")) {
         throw new IllegalArgumentException(option + " necesita un valor (recibió " + value + ")");
      }
      return value;
   }

   /** Saved state; when launcher.properties does not exist yet, -Dzlport.server.url (if present) prepares the first-run connection. */
   static PreLauncherState stateSeed(Path dataRoot) {
      if (Files.isRegularFile(dataRoot.resolve(PreLauncherState.FILE_NAME))) {
         return PreLauncherState.load(dataRoot);
      }
      Optional<ServerConnection> server = ServerConnection.parseServerUrl(System.getProperty("zlport.server.url"));
      if (server.isEmpty()) {
         return PreLauncherState.load(dataRoot);
      }
      ServerConnection connection = server.orElseThrow();
      return PreLauncherState.of(PreLauncherState.Mode.SERVER, connection.host(), connection.port(), "", true, "", "");
   }

   /** Launcher seed: flags &gt; saved state &gt; -Dzlport.server.url &gt; default (127.0.0.1:6767). */
   static PreLauncherState seedFrom(PreLauncherState state, LaunchFlags flags) {
      PreLauncherState.Mode mode = flags.mode().isPresent() ? flags.mode().orElseThrow() : state.mode();
      String host = state.host();
      int port = state.port();
      if (flags.server().isPresent()) {
         ServerConnection connection = flags.server().orElseThrow();
         host = connection.host();
         port = connection.port();
      } else if (host.isEmpty()) {
         Optional<ServerConnection> seedUrl = ServerConnection.parseServerUrl(System.getProperty("zlport.server.url"));
         if (seedUrl.isPresent()) {
            host = seedUrl.orElseThrow().host();
            port = seedUrl.orElseThrow().port();
         }
      }
      String user = flags.user().isPresent() ? flags.user().orElseThrow() : state.rememberUser() ? state.user() : "";
      return PreLauncherState.of(mode, host, port, user, state.rememberUser(), state.lastProfile(), state.devMissions(), state.language());
   }

   /** Applies the launcher-screen choice to startup: server connection or network blocking. */
   static void applySelection(PreLauncherDialog.Selection selection) {
      if (selection.mode() == PreLauncherState.Mode.SERVER && selection.server() != null) {
         System.setProperty("zlport.server.url", selection.server().gameUrl());
         System.setProperty("zlport.server.mode", "true");
         if (selection.accountId() != null) System.setProperty("zlport.account.id", selection.accountId());
      } else {
         System.clearProperty("zlport.server.mode");
         installOfflineNetwork();
      }
   }

   /** Mode from the flags; empty when --mode was not supplied. */
   static Optional<PreLauncherState.Mode> modeOf(LaunchFlags flags) {
      return flags.mode();
   }

   /** User name for the identity fallback: the flag wins, then the remembered state user. */
   static String userIdentity(LaunchFlags flags, PreLauncherState state) {
      if (flags.user().isPresent()) {
         return flags.user().orElseThrow();
      }
      return state.rememberUser() ? state.user() : "";
   }

   /**
    * Account id for this run when no launcher dialog ran (see
    * docs/decisions.md "Account identity"). An explicit -Dzlport.account.id
    * wins. In SERVER mode the typed, valid user name is the fallback
    * identity: the server resolves the account by numeric id or by that user
    * name, both with the password. The dialog path never reaches this
    * method, because the authentication result carries the server-issued
    * numeric account id. In LOCAL mode nothing talks to a server, so the
    * numeric profile folder keeps today's local value. Null means no property
    * is set by the launcher.
    */
   static String accountIdFor(Path dataDir, boolean serverMode, String user) {
      String explicit = System.getProperty("zlport.account.id");
      if (explicit != null) {
         return explicit;
      }
      if (serverMode) {
         String normalizedUser = user == null ? "" : user.strip();
         return normalizedUser.isEmpty() || ServerConnection.validateUser(normalizedUser) != null ? null : normalizedUser;
      }
      String fileName = dataDir.getFileName() == null ? "" : dataDir.getFileName().toString();
      return fileName.matches("\\d{15}") ? fileName : null;
   }

   /** Copies the language left by the game in settings_data back to launcher.properties. */
   private static void syncLanguage(Path dataRoot, Path runtimeDir) {
      try {
         Optional<String> gameLanguage = GameLanguage.read(runtimeDir);
         if (gameLanguage.isEmpty()) {
            return;
         }
         String language = gameLanguage.orElseThrow();
         PreLauncherState current = PreLauncherState.load(dataRoot);
         if (language.equals(current.language())) {
            return;
         }
         current.withLanguage(language).save(dataRoot);
         LOG.log(java.lang.System.Logger.Level.INFO, "Idioma del juego sincronizado con el lanzador: " + language);
      } catch (IOException | RuntimeException exception) {
         LOG.log(java.lang.System.Logger.Level.WARNING,
            "No se pudo sincronizar el idioma del juego con el lanzador", exception);
      }
   }

   /** Same as applySelection but resolving the connection from flags, state and property (no dialog). */
   private static void applyWithoutLauncher(LaunchFlags flags, PreLauncherState state) {
      PreLauncherState.Mode mode = flags.mode().isPresent() ? flags.mode().orElseThrow() : state.mode();
      if (mode == PreLauncherState.Mode.LOCAL) {
         System.clearProperty("zlport.server.mode");
         installOfflineNetwork();
         return;
      }
      System.setProperty("zlport.server.mode", "true");
      if (flags.server().isPresent()) {
         System.setProperty("zlport.server.url", flags.server().orElseThrow().gameUrl());
      } else if (System.getProperty("zlport.server.url") == null && !state.host().isEmpty()) {
         System.setProperty("zlport.server.url", new ServerConnection(state.host(), state.port()).gameUrl());
      }
      // Scripted SERVER mode runs must not silently fall back to offline play:
      // the same /health gate the dialog uses refuses the launch.
      Optional<ServerConnection> target = ServerConnection.parseServerUrl(System.getProperty("zlport.server.url"));
      if (target.isPresent()) {
         String unavailable = PreLauncherDialog.serverGateMessage(target.orElseThrow());
         if (unavailable != null) {
            System.err.println(unavailable.replace("\n", " "));
            System.exit(1);
         }
      }
   }

   /** Installs the network guard (LOCAL mode only): every http/https attempt by the game fails immediately, with no sockets opened. */
   private static void installOfflineNetwork() {
      try {
         OfflineNetwork.install();
      } catch (RuntimeException | Error exception) {
         LOG.log(java.lang.System.Logger.Level.WARNING, "No se pudo instalar el bloqueo de red del modo local", exception);
      }
   }

   /** Dev editions only: shared missions directory chosen in the launcher (empty = the default). */
   private static void applyDevMissions(String devMissions) {
      if (!devMissions.isEmpty()) {
         System.setProperty("zlport.ios.missions", devMissions);
      }
   }

   private static Optional<Config> parse(Settings settings, Path dataDir, String... args) {
      for (int i = 0; i < args.length; i++) {
         if (args[i].equals("--size") || args[i].startsWith("--size=")) {
            throw new IllegalArgumentException("--size no se acepta en 1.4.4: la pantalla lógica es fija de 1136x640; redimensiona la ventana desde el sistema.");
         }

         if (args[i].equals("--data-dir") || args[i].equals("--midlet")) {
            i++;
         }
      }

      Optional<Config> config = Config.parse(settings, dataDir, args);
      if (config.isPresent() && !"com.dchoc.DCiDead".equals(config.orElseThrow().midlet())) {
         throw new IllegalArgumentException("El lanzador de 1.4.4 solo usa el MIDlet com.dchoc.DCiDead.");
      } else {
         return config;
      }
   }

   private static boolean usesProfilePicker(String[] args) {
      for (String arg : args) {
         if ("--no-profile-picker".equals(arg)) {
            return false;
         }
      }
      return true;
   }

   private static String[] withoutOption(String[] args, String option) {
      return java.util.Arrays.stream(args).filter(argument -> !option.equals(argument)).toArray(String[]::new);
   }

   private static String[] withoutDataDirectory(String[] args) {
      java.util.ArrayList<String> filtered = new java.util.ArrayList<>();
      for (int index = 0; index < args.length; index++) {
         if ("--data-dir".equals(args[index])) {
            index++;
         } else {
            filtered.add(args[index]);
         }
      }
      return filtered.toArray(String[]::new);
   }

   static Path defaultDataDir() {
      String property = System.getProperty("zlport.dataDir");
      if (property != null && !property.isBlank()) {
         return Path.of(property);
      }

      String appData = System.getenv("APPDATA");
      return appData != null && !appData.isBlank() ? Path.of(appData, "ZombieLane-1.4.4") : Path.of(System.getProperty("user.home"), ".zombielane-1.4.4");
   }

   static Size logicalSize() {
      return new Size(1136, 640);
   }

   static Launcher createLauncher(Config config, FrameListener listener) {
      return new Launcher(config, listener, logicalSize());
   }

    static void installRuntime(Path bundle, Path dataDir, int energyRechargeSeconds, float spawnMultiplier, int neighborIntervalMinutes) {
       installRuntime(bundle, dataDir, energyRechargeSeconds, spawnMultiplier, neighborIntervalMinutes, false, List.of());
    }

    static void installRuntime(
       Path bundle,
       Path dataDir,
       int energyRechargeSeconds,
       float spawnMultiplier,
       int neighborIntervalMinutes,
       boolean cashCheat,
       List<LocalProfile> localProfiles
    ) {
       Ios.install(
          new PcIosPlatform(
             bundle, dataDir, 1136, 640, energyRechargeSeconds * 1000, spawnMultiplier, neighborIntervalMinutes, cashCheat, localProfiles
          )
       );
       Platform.installDriver(new IosDriver());
    }

   /**
    * Selects the mission definition sources. LOCAL keeps the shared missions
    * directory (bundle, then shared pack); SERVER disables it so the bundle and
    * the server-issued missions are the only sources. Returns the LOCAL shared
    * directory, or null in SERVER mode. See docs/missions-restore.md.
    */
   static String configureMissionSource(boolean serverMode, Path dataRoot) {
      if (serverMode) {
         System.clearProperty("zlport.ios.missions");
         Toolkit.setSharedMissionsEnabled(false);
         return null;
      }
      String directory = missionsDirectory(dataRoot).toString();
      System.setProperty("zlport.ios.missions", directory);
      Toolkit.setSharedMissionsEnabled(true);
      return directory;
   }

   static Path missionsDirectory(Path dataRoot) {
      String property = System.getProperty("zlport.ios.missions");
      if (property != null && !property.isBlank()) {
         return Path.of(property).toAbsolutePath().normalize();
      }

      return dataRoot.toAbsolutePath().normalize().resolve("missions");
   }

   static Path bundleDirectory() {
      String property = System.getProperty("zlport.ios.bundle");
      if (property != null && !property.isBlank()) {
         return Path.of(property).toAbsolutePath().normalize();
      }

      try {
         Path location = Path.of(IosMain.class.getProtectionDomain().getCodeSource().getLocation().toURI());
         return installedBundle(location);
      } catch (URISyntaxException exception) {
         throw new IllegalStateException("No se pudo localizar la instalación de la 1.4.4", exception);
      }
   }

   static Path installedBundle(Path codeLocation) {
      Path location = codeLocation.toAbsolutePath().normalize();
      Path installation = location;
      if (location.getFileName().toString().endsWith(".jar")) {
         installation = location.getParent();
         if (installation.getFileName() != null && installation.getFileName().toString().equals("lib")) {
            installation = installation.getParent();
         }
      }

      return installation.resolve("game-assets-ios");
   }

   private static void logSessionAction(String event, String mode, String accountId) {
      try {
         Map<String, String> details = new LinkedHashMap<>();
         details.put("event", event);
         details.put("mode", mode);
         details.put("version", SessionReport.VERSION);
         if (accountId != null && !accountId.isBlank()) {
            details.put("account_id", accountId);
         }
         Ios.platform().logPlayerAction(details);
      } catch (IllegalStateException ignored) {
         // El runtime ya se desinstaló (o todavía no se instaló).
      }
   }

   private static void printStartupSummary(Path dataRoot, Path runtimeDir, boolean serverMode,
         ServerConnection server, String accountId, String user, boolean reportConsent) {
      LOG.log(java.lang.System.Logger.Level.INFO, "Zombie Lane " + SessionReport.VERSION + " (alpha)");
      LOG.log(java.lang.System.Logger.Level.INFO,
         "Modo: " + (serverMode ? "servidor" : "local") + " | Datos: " + dataRoot.toAbsolutePath()
            + " | Perfil: " + runtimeDir.toAbsolutePath());
      if (serverMode && server != null) {
         LOG.log(java.lang.System.Logger.Level.INFO,
            "Servidor: " + server.host() + ":" + server.port()
               + " | Cuenta: " + (accountId == null ? "(sin identificar)" : accountId)
               + " | Usuario: " + (user == null || user.isBlank() ? "(sin identificar)" : user));
      }
      LOG.log(java.lang.System.Logger.Level.INFO,
         "Informe de sesión: " + (reportConsent ? "activado (se enviará al terminar)" : "desactivado (solo registros locales)"));
   }

   private static void logSessionSummary(Result result, int exitCode) {
      double seconds = result.seconds();
      double fps = seconds <= 0.0 ? 0.0 : result.frames() / seconds;
      LOG.log(java.lang.System.Logger.Level.INFO,
         String.format(Locale.ROOT,
            "Sesión finalizada: %d fotogramas en %.1f s (%.1f FPS), salida %d",
            result.frames(), seconds, fps, exitCode));
      if (result.glErrors() > 0) {
         LOG.log(java.lang.System.Logger.Level.WARNING, "Errores de OpenGL detectados: " + result.glErrors());
      }
      if (!result.offThreadGl().isEmpty()) {
         LOG.log(java.lang.System.Logger.Level.WARNING,
            "Llamadas OpenGL fuera del hilo: " + result.offThreadGl().size());
      }
      if (!result.notPorted().isEmpty()) {
         LOG.log(java.lang.System.Logger.Level.INFO,
            "Funciones no portadas usadas: " + result.notPorted().size());
      }
   }

   private static Path reportFailure(Config config, LogFile logFile, Path reportDir) {
      Path report = null;

      try {
         report = CrashReport.write(
            reportDir, LocalDateTime.now(), config.toString(), logFile == null ? List.of() : logFile.tail(), logFile == null ? null : logFile.path()
         );
         LOG.log(java.lang.System.Logger.Level.ERROR, "Informe de fallo guardado en " + report.toAbsolutePath());
      } catch (IOException | RuntimeException exception) {
         LOG.log(java.lang.System.Logger.Level.ERROR, "No se pudo guardar el informe de fallo", exception);
      }

      if (config.visible()) {
         WindowsDialogs.showError(
            "Zombie Lane 1.4.4", "Zombie Lane 1.4.4 encontró un error y va a cerrarse."
               + (report == null ? "" : "\n\nDetalles en:\n" + report.toAbsolutePath())
         );
      }
      return report;
   }
}
