package zlport.launcher;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import zlport.ios.LocalProfile;

public final class LocalProfiles {
   private static final Pattern PROFILE_ID = Pattern.compile("\\d{15}");
   private static final SecureRandom RANDOM = new SecureRandom();
   private static final String DIRECTORY_NAME = "players";
   private static final String METADATA_NAME = "profile.properties";

   private LocalProfiles() {
   }

   public static Optional<Path> choose(Path dataRoot) throws IOException {
      Path profilesDirectory = dataRoot.resolve(DIRECTORY_NAME);
      Files.createDirectories(profilesDirectory);
      if (list(profilesDirectory).isEmpty()) {
         importLegacySave(dataRoot, profilesDirectory);
      }

      while (true) {
         List<Profile> profiles = list(profilesDirectory);
         JComboBox<Profile> selector = new JComboBox<>(profiles.toArray(Profile[]::new));
         if (!profiles.isEmpty()) {
            selector.setSelectedIndex(0);
         }

         JPanel panel = new JPanel();
         panel.setLayout(new java.awt.GridLayout(0, 1));
         panel.add(new JLabel(profiles.isEmpty() ? "No hay perfiles locales. Crea uno nuevo para empezar." : "Perfil local:"));
         if (profiles.isEmpty() && Files.isDirectory(dataRoot.resolve("files"))) {
            panel.add(new JLabel("Se detectó un guardado anterior fuera de players; no se copiará ni modificará."));
         }
         if (!profiles.isEmpty()) {
            panel.add(selector);
         }

         Object[] options = profiles.isEmpty()
            ? new Object[] { "Crear perfil nuevo", "Cancelar" }
            : new Object[] { "Iniciar", "Crear perfil nuevo", "Cancelar" };
         int selected = JOptionPane.showOptionDialog(
            null,
            panel,
            "Zombie Lane - Seleccionar perfil",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
         );
         if (selected < 0 || selected == options.length - 1) {
            return Optional.empty();
         }

         if (profiles.isEmpty() || selected == 1) {
            create(profilesDirectory);
         } else {
            Profile profile = (Profile)selector.getSelectedItem();
            return Optional.of(profile.directory());
         }
      }
   }

   /** Fields of a local profile as seen by the pre-game launcher; it does not change LocalProfiles' own logic. */
   public record ProfileInfo(String id, String name, Path directory) {
      @Override
      public String toString() {
         return this.name + " (" + this.id + ")";
      }
   }

   /** Lists the profiles under the root for the pre-game launcher; reuses {@link #choose(Path)}'s logic without its dialog: creates the players directory, imports the flat save when there are no profiles, and returns the valid profiles. */
   public static List<ProfileInfo> profiles(Path dataRoot) throws IOException {
      Path profilesDirectory = dataRoot.resolve(DIRECTORY_NAME);
      Files.createDirectories(profilesDirectory);
      if (list(profilesDirectory).isEmpty()) {
         importLegacySave(dataRoot, profilesDirectory);
      }
      List<ProfileInfo> profiles = new ArrayList<>();
      for (Profile profile : list(profilesDirectory)) {
         profiles.add(new ProfileInfo(profile.id(), profile.name(), profile.directory()));
      }
      return profiles;
   }

   /** Creates a new profile with the given name; non-interactive (no dialogs), so it is safe from scripts and tests. Empty when the name is blank. */
   public static Optional<ProfileInfo> createNew(Path dataRoot, String name) throws IOException {
      String trimmed = name == null ? "" : name.strip();
      if (trimmed.isEmpty()) {
         return Optional.empty();
      }
      Path profilesDirectory = dataRoot.resolve(DIRECTORY_NAME);
      Files.createDirectories(profilesDirectory);
      return createNamed(profilesDirectory, trimmed);
   }

   public static List<LocalProfile> neighbors(Path profileDirectory) throws IOException {
      Path profile = profileDirectory.toAbsolutePath().normalize();
      Path profilesDirectory = profile.getParent();
      if (profilesDirectory == null
         || !"players".equals(profilesDirectory.getFileName().toString())
         || !Files.isDirectory(profilesDirectory)) {
         return List.of();
      }

      List<LocalProfile> neighbors = new ArrayList<>();
      for (Profile candidate : list(profilesDirectory)) {
         if (!candidate.directory().toAbsolutePath().normalize().equals(profile)) {
            neighbors.add(new LocalProfile(candidate.id(), candidate.name()));
         }
      }
      return List.copyOf(neighbors);
   }

   private static List<Profile> list(Path profilesDirectory) throws IOException {
      List<Profile> profiles = new ArrayList<>();
      try (var children = Files.list(profilesDirectory)) {
         for (Path directory : children.filter(path -> Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)).toList()) {
            String id = directory.getFileName().toString();
            if (!PROFILE_ID.matcher(id).matches()) {
               continue;
            }

            Properties metadata = new Properties();
            Path metadataFile = directory.resolve(METADATA_NAME);
            if (Files.isRegularFile(metadataFile, LinkOption.NOFOLLOW_LINKS)) {
               try (var reader = Files.newBufferedReader(metadataFile)) {
                  metadata.load(reader);
               }
            }
            String name = metadata.getProperty("name", id).strip();
            profiles.add(new Profile(id, name.isEmpty() ? id : name, directory));
         }
      }
      profiles.sort(Comparator.comparing(Profile::name, String.CASE_INSENSITIVE_ORDER).thenComparing(Profile::id));
      return profiles;
   }

   private static void create(Path profilesDirectory) throws IOException {
      JTextField nameField = new JTextField(24);
      JPanel panel = new JPanel();
      panel.add(new JLabel("Nombre del perfil:"));
      panel.add(nameField);
      int answer = JOptionPane.showConfirmDialog(
         null,
         panel,
         "Crear perfil nuevo",
         JOptionPane.OK_CANCEL_OPTION,
         JOptionPane.PLAIN_MESSAGE
      );
      if (answer != JOptionPane.OK_OPTION) {
         return;
      }

      String name = nameField.getText().strip();
      if (name.isEmpty()) {
         JOptionPane.showMessageDialog(null, "Escribe un nombre para el perfil.", "Zombie Lane", JOptionPane.INFORMATION_MESSAGE);
         return;
      }

      createNamed(profilesDirectory, name);
   }

   /** Generates the id and writes the metadata of a new profile; returns it. */
   private static Optional<ProfileInfo> createNamed(Path profilesDirectory, String name) throws IOException {
      Path profileDirectory;
      while (true) {
         String id = newId();
         try {
            profileDirectory = Files.createDirectory(profilesDirectory.resolve(id));
            break;
         } catch (java.nio.file.FileAlreadyExistsException ignored) {
            // Generate another id if there is a collision.
         }
      }

      try {
         Properties metadata = new Properties();
         metadata.setProperty("id", profileDirectory.getFileName().toString());
         metadata.setProperty("name", name);
         try (var writer = Files.newBufferedWriter(profileDirectory.resolve(METADATA_NAME))) {
            metadata.store(writer, "Local Zombie Lane profile");
         }
      } catch (IOException exception) {
         Files.deleteIfExists(profileDirectory.resolve(METADATA_NAME));
         Files.deleteIfExists(profileDirectory);
         throw exception;
      }
      return Optional.of(new ProfileInfo(profileDirectory.getFileName().toString(), name, profileDirectory));
   }

   private static void importLegacySave(Path dataRoot, Path profilesDirectory) throws IOException {
      Path legacyFiles = dataRoot.resolve("files");
      if (!Files.isDirectory(legacyFiles) || !containsRecord(legacyFiles)) {
         return;
      }

      String id;
      Path profileDirectory;
      Path stagingDirectory;
      do {
         id = newId();
         profileDirectory = profilesDirectory.resolve(id);
         stagingDirectory = profilesDirectory.resolve(".importing-" + id);
      } while (Files.exists(profileDirectory) || Files.exists(stagingDirectory));

      Files.createDirectory(stagingDirectory);
      try {
         Path stagedFiles = stagingDirectory.resolve("files");
         try (var paths = Files.walk(legacyFiles)) {
            for (Path source : paths.toList()) {
               Path relative = legacyFiles.relativize(source);
               Path destination = stagedFiles.resolve(relative);
               if (Files.isDirectory(source, LinkOption.NOFOLLOW_LINKS)) {
                  Files.createDirectories(destination);
               } else if (Files.isRegularFile(source, LinkOption.NOFOLLOW_LINKS)) {
                  Files.createDirectories(destination.getParent());
                  Files.copy(source, destination);
               }
            }
         }

         Properties metadata = new Properties();
         metadata.setProperty("id", id);
         metadata.setProperty("name", "Perfil existente");
         metadata.setProperty("importedFrom", "legacy-local-save");
         try (var writer = Files.newBufferedWriter(stagingDirectory.resolve(METADATA_NAME))) {
            metadata.store(writer, "Imported local Zombie Lane profile");
         }

         try {
            Files.move(stagingDirectory, profileDirectory, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException exception) {
            Files.move(stagingDirectory, profileDirectory);
         }
      } catch (IOException | RuntimeException exception) {
         deleteTree(stagingDirectory);
         throw exception;
      }
   }

   private static boolean containsRecord(Path filesDirectory) throws IOException {
      try (var paths = Files.walk(filesDirectory)) {
         return paths.anyMatch(path ->
            Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)
               && path.getFileName().toString().equals("1")
               && path.getParent() != null
               && path.getParent().getFileName().toString().endsWith("_data")
         );
      }
   }

   private static String newId() {
      return Long.toString(100_000_000_000_000L + RANDOM.nextLong(900_000_000_000_000L));
   }

   private static void deleteTree(Path root) throws IOException {
      if (!Files.exists(root, LinkOption.NOFOLLOW_LINKS)) {
         return;
      }
      try (var paths = Files.walk(root)) {
         for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
            Files.deleteIfExists(path);
         }
      }
   }

   private record Profile(String id, String name, Path directory) {
      @Override
      public String toString() {
         return this.name + " (" + this.id + ")";
      }
   }
}
