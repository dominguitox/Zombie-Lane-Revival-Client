package zlport.launcher;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Reads and writes the language byte of the game's {@code settings_data/1}
 * record without touching any other field. A missing record is left missing:
 * the launcher exposes {@link #PROPERTY} so {@code Game.loadSettings()} can
 * seed a new profile with a valid full record.
 */
public final class GameLanguage {
   public static final String PROPERTY = LauncherLanguage.PROPERTY;
   private static final String RECORD_DIRECTORY = "settings_data";
   private static final String RECORD_FILE = "1";

   private GameLanguage() {
   }

   public static Path settingsFile(Path dataDir) {
      return dataDir.resolve("files").resolve(RECORD_DIRECTORY).resolve(RECORD_FILE);
   }

   public static Optional<String> read(Path dataDir) throws IOException {
      Path file = settingsFile(dataDir);
      if (!Files.isRegularFile(file)) {
         return Optional.empty();
      }
      byte[] data = Files.readAllBytes(file);
      if (data.length == 0) {
         return Optional.empty();
      }
      return LauncherLanguage.codeForIndex(data[0] & 0xFF);
   }

   /** Writes only the language byte when the record already exists; returns true when it changed. */
   public static boolean apply(Path dataDir, String code) throws IOException {
      int index = LauncherLanguage.indexForCode(code);
      Path file = settingsFile(dataDir);
      if (index < 0 || !Files.isRegularFile(file)) {
         return false;
      }
      byte[] data = Files.readAllBytes(file);
      if (data.length == 0 || (data[0] & 0xFF) == index) {
         return false;
      }
      data[0] = (byte)index;
      Path temporary = Files.createTempFile(file.getParent(), "settings-", ".tmp");
      try {
         Files.write(temporary, data);
         try {
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
         }
      } finally {
         Files.deleteIfExists(temporary);
      }
      return true;
   }

   public static void exposeProperty(String code) {
      Optional<String> normalized = LauncherLanguage.normalize(code);
      if (normalized.isPresent()) {
         System.setProperty(PROPERTY, normalized.orElseThrow());
      } else {
         System.clearProperty(PROPERTY);
      }
   }

   public static void clearProperty() {
      System.clearProperty(PROPERTY);
   }
}
