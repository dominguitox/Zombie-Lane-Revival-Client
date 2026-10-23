package zlport.launcher;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * Own state of the pre-game launcher, stored in {@code launcher.properties} in
 * the profile root (by default {@code %APPDATA%\ZombieLane-1.4.4}). It keeps
 * the last mode, last connection (host, port), the remembered user and the
 * last profile: UI convenience only. Per-profile game options live where the
 * game already reads them ({@code zombie-lane.properties}), so there is no
 * second source of truth.
 *
 * <p>THE PASSWORD IS NEVER PERSISTED: there is no field for it in this class
 * and none is written here or anywhere else. Staging happens in
 * {@link PendingCredentials}, memory-only.</p>
 */
public final class PreLauncherState {
   public enum Mode {
      LOCAL, SERVER
   }

   public static final String FILE_NAME = "launcher.properties";
   static final String MODE = "mode";
   static final String HOST = "server.host";
   static final String PORT = "server.port";
   static final String USER = "server.user";
   static final String REMEMBER_USER = "server.remember-user";
   static final String LAST_PROFILE = "profile.last";
   static final String LANGUAGE = "language";
   /** Dev editions only: shared missions directory; empty value = the default. */
   static final String DEV_MISSIONS = "dev.missions-directory";

   private final Mode mode;
   private final String host;
   private final int port;
   private final String user;
   private final boolean rememberUser;
   private final String lastProfile;
   private final String devMissions;
   private final String language;

   private static final Mode DEFAULT_MODE = Mode.SERVER;
   private static final String DEFAULT_HOST = "127.0.0.1";
   private static final int DEFAULT_PORT = 6767;

   private PreLauncherState(Mode mode, String host, int port, String user, boolean rememberUser, String lastProfile, String devMissions, String language) {
      this.mode = mode == null ? DEFAULT_MODE : mode;
      this.host = host == null ? "" : host.strip();
      this.port = port;
      this.user = user == null ? "" : user.strip();
      this.rememberUser = rememberUser;
      this.lastProfile = lastProfile == null ? "" : lastProfile.strip();
      this.devMissions = devMissions == null ? "" : devMissions.strip();
      this.language = LauncherLanguage.normalize(language).orElse("");
   }

   /** Builds a state validating the field values: invalid ones fall back to their defaults. */
   public static PreLauncherState of(
      Mode mode, String host, Integer port, String user, boolean rememberUser, String lastProfile, String devMissions
   ) {
      return of(mode, host, port, user, rememberUser, lastProfile, devMissions, "");
   }

   /** Builds a state validating the field values: invalid ones fall back to their defaults. */
   public static PreLauncherState of(
      Mode mode, String host, Integer port, String user, boolean rememberUser, String lastProfile, String devMissions, String language
   ) {
      int resolvedPort = port == null ? DEFAULT_PORT : port;
      if (resolvedPort < ServerConnection.MIN_PORT || resolvedPort > ServerConnection.MAX_PORT) {
         resolvedPort = DEFAULT_PORT;
      }
      String resolvedUser = (user == null ? "" : user.strip());
      if (ServerConnection.validateUser(resolvedUser) != null) {
         resolvedUser = "";
      }
      String resolvedProfile = (lastProfile == null ? "" : lastProfile.strip());
      if (!resolvedProfile.matches("\\d{15}")) {
         resolvedProfile = "";
      }
      return new PreLauncherState(mode, host, resolvedPort, resolvedUser, rememberUser, resolvedProfile, devMissions, language);
   }

   public static PreLauncherState defaults() {
      return new PreLauncherState(DEFAULT_MODE, DEFAULT_HOST, DEFAULT_PORT, "", true, "", "", "");
   }

   /** Reads the file; any missing or invalid value falls back to the defaults. */
   public static PreLauncherState load(Path dataRoot) {
      Path file = dataRoot.resolve(FILE_NAME);
      if (!Files.isRegularFile(file)) {
         return defaults();
      }

      Properties p = new Properties();
      try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
         p.load(r);
      } catch (IOException | IllegalArgumentException e) {
         return defaults();
      }

      Mode mode = DEFAULT_MODE;
      String modeText = p.getProperty(MODE, "").strip();
      if ("local".equalsIgnoreCase(modeText)) {
         mode = Mode.LOCAL;
      } else if ("server".equalsIgnoreCase(modeText) || "servidor".equalsIgnoreCase(modeText)) {
         mode = Mode.SERVER;
      }
      int port = DEFAULT_PORT;
      String portText = p.getProperty(PORT, "").strip();
      if (!portText.isEmpty()) {
         try {
            port = Integer.parseInt(portText);
         } catch (NumberFormatException ignored) {
            port = DEFAULT_PORT;
         }
      }
      boolean remember = !"false".equalsIgnoreCase(p.getProperty(REMEMBER_USER, "true").strip());
      String devMissions = p.getProperty(DEV_MISSIONS, "").strip();
      String host = p.getProperty(HOST, "").strip();
      if (ServerConnection.validateHost(host) != null) {
         host = "";
      }
      String user = p.getProperty(USER, "").strip();
      if (ServerConnection.validateUser(user) != null) {
         user = "";
      }
      String lastProfile = p.getProperty(LAST_PROFILE, "").strip();
      if (!lastProfile.matches("\\d{15}")) {
         lastProfile = "";
      }
      String language = LauncherLanguage.normalize(p.getProperty(LANGUAGE, "").strip()).orElse("");
      return new PreLauncherState(mode, host, port, user, remember, lastProfile, devMissions, language);
   }

   /** Writes the file atomically (temporary file then move). */
   public void save(Path dataRoot) throws IOException {
      Files.createDirectories(dataRoot);
      Path file = dataRoot.resolve(FILE_NAME);
      Path tmp = dataRoot.resolve(FILE_NAME + ".tmp");
      try {
         Files.writeString(tmp, this.render(), StandardCharsets.UTF_8);
         try {
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
         }
      } finally {
         Files.deleteIfExists(tmp);
      }
   }

   private String render() {
      String n = System.lineSeparator();
      StringBuilder s = new StringBuilder();
      s.append("# Pre-game launcher state. Convenience only: game and launch options").append(n);
      s.append("# live in zombie-lane.properties inside each profile folder.").append(n);
      s.append("# No password is ever stored in this file or anywhere; there is no field for it.").append(n);
      if (!this.language.isEmpty()) {
         s.append(LANGUAGE).append('=').append(this.language).append(n);
      }
      s.append(MODE).append('=').append(this.mode == Mode.LOCAL ? "local" : "server").append(n);
      s.append(HOST).append('=').append(this.host).append(n);
      s.append(PORT).append('=').append(this.port).append(n);
      if (this.rememberUser) {
         s.append(USER).append('=').append(this.user).append(n);
      }
      s.append(REMEMBER_USER).append('=').append(this.rememberUser).append(n);
      s.append(LAST_PROFILE).append('=').append(this.lastProfile).append(n);
      if (!this.devMissions.isEmpty()) {
         // Hand-rolled writer: escape backslashes the way java.util.Properties reads them.
         s.append(DEV_MISSIONS).append('=').append(this.devMissions.replace("\\", "\\\\")).append(n);
      }
      return s.toString();
   }

   /** Test hook: the rendered file text without touching the disk. */
   static String renderText(PreLauncherState state) {
      return state.render();
   }

   public Mode mode() {
      return this.mode;
   }

   public String host() {
      return this.host;
   }

   public int port() {
      return this.port;
   }

   public String user() {
      return this.user;
   }

   public boolean rememberUser() {
      return this.rememberUser;
   }

   public String lastProfile() {
      return this.lastProfile;
   }

   public String devMissions() {
      return this.devMissions;
   }

   public String language() {
      return this.language;
   }

   public PreLauncherState withUser(String user) {
      return new PreLauncherState(this.mode, this.host, this.port, user, this.rememberUser, this.lastProfile, this.devMissions, this.language);
   }

   public PreLauncherState withMissionDirectory(String directory) {
      return new PreLauncherState(this.mode, this.host, this.port, this.user, this.rememberUser, this.lastProfile, directory, this.language);
   }

   public PreLauncherState withLanguage(String language) {
      return new PreLauncherState(this.mode, this.host, this.port, this.user, this.rememberUser, this.lastProfile, this.devMissions, language);
   }
}
