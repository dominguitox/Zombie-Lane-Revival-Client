package zlport.launcher;

import com.dchoc.idead.network.Base64;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Server-authoritative save adapter for SERVER mode.
 *
 * <p>The server owns the account save. The launcher downloads it before the
 * game starts, materializes the RMS records in a throwaway directory and lets
 * the game play against that copy. The game keeps updating the copy through
 * its normal RMS writes, and its own {@code UpdateAccountInformation} requests
 * push the same state to the server while playing. When the run ends, this
 * class uploads the final RMS state and deletes the throwaway directory, so
 * nothing is left in a local profile.</p>
 *
 * <p>The server keeps a single gamefield plus the area label, so only the
 * active area is restored and uploaded. See docs/server-saves.md.</p>
 */
final class ServerSaveSync {
   private static final String RMS_PROFILE = "RMS_PROFILE";
   private static final String RMS_MISSIONS = "RMS_MISSIONS";
   private static final String RMS_GAMEFIELD = "RMS_GAMEFIELD";
   private static final String RMS_CIRCUS = "RMS_CIRCUS";
   private static final String RMS_DOWNTOWN = "RMS_DOWNTOWN";
   private static final String RMS_CRASHSITE = "RMS_CRASHSITE";
   private static final String RMS_CURE = "RMS_CURE";
   private static final String[] AREA_RECORDS = {
      RMS_GAMEFIELD, RMS_CIRCUS, RMS_DOWNTOWN, RMS_CRASHSITE, RMS_CURE
   };
   private static final String[] GAME_SETTINGS = {"settings_data", "RMS_AB_TEST_data"};
   private static final String CUSTOM_MISSIONS_CACHE = "custom-missions-cache.xml";

   private ServerSaveSync() {
   }

   record Session(ServerConnection connection, String accountId, Path directory, String preferredRecord,
         Path customMissionsCache) {
   }

   static Session download(ServerConnection connection, String accountId, Path dataRoot) throws IOException {
      Map<String, String> save = connection.fetchSave(accountId);
      String record = recordFor(save.get("area"));
      // Bajo logs/sesiones para que las acciones sobrevivan a un cierre brusco.
      Path sessions = Files.createDirectories(dataRoot.resolve("logs").resolve("sesiones"));
      Path directory = Files.createTempDirectory(sessions, "server-save-");
      try {
         Path customMissionsCache = loadCustomMissions(connection, dataRoot, directory);
         writeRecord(directory, RMS_PROFILE, save.get("avatar"));
         writeRecord(directory, RMS_MISSIONS, save.get("missions"));
         writeRecord(directory, record, save.get("gamefield"));
         copyGameSettings(dataRoot, directory);
         return new Session(connection, accountId, directory, record, customMissionsCache);
      } catch (IOException | RuntimeException exception) {
         delete(directory);
         throw exception;
      }
   }

   /**
    * Cada sesión recibe su propia copia del catálogo. Antes se escribía una
    * única caché en el directorio de datos y dos instancias podían pisársela.
    * Se conserva además una caché por servidor como último valor válido.
    */
   private static Path loadCustomMissions(ServerConnection connection, Path dataRoot, Path sessionDirectory)
         throws IOException {
      Path sessionCache = sessionDirectory.resolve(CUSTOM_MISSIONS_CACHE);
      Path lastKnown = lastKnownCache(dataRoot, connection);
      try {
         String xml = connection.fetchCustomMissions();
         if (!hasMissions(xml) && hasMissions(lastKnown)) {
            copyCache(lastKnown, sessionCache);
            System.err.println("El servidor no devolvió misiones personalizadas; se usa la última caché válida"
               + " para no invalidar la partida.");
            return sessionCache;
         }
         writeCache(sessionCache, xml);
         if (hasMissions(xml) || !Files.isRegularFile(lastKnown)) {
            writeCache(lastKnown, xml);
         }
         return sessionCache;
      } catch (IOException exception) {
         if (copyCache(lastKnown, sessionCache)) {
            System.err.println("No se pudieron actualizar las misiones personalizadas; se usa la última caché: "
               + exception.getMessage());
            return sessionCache;
         }
         throw exception;
      }
   }

   private static Path lastKnownCache(Path dataRoot, ServerConnection connection) {
      String host = connection.host().replaceAll("[^A-Za-z0-9.-]", "_");
      Path scoped = dataRoot.resolve("custom-missions-cache-" + host + "-" + connection.port() + ".xml");
      if (Files.isRegularFile(scoped)) {
         return scoped;
      }
      Path legacy = dataRoot.resolve(CUSTOM_MISSIONS_CACHE);
      return Files.isRegularFile(legacy) ? legacy : scoped;
   }

   private static boolean hasMissions(Path cache) {
      if (!Files.isRegularFile(cache)) {
         return false;
      }
      try {
         return hasMissions(Files.readString(cache, StandardCharsets.UTF_8));
      } catch (IOException exception) {
         return false;
      }
   }

   private static boolean hasMissions(String xml) {
      if (xml == null) {
         return false;
      }
      int index = 0;
      while ((index = xml.indexOf("<mission", index)) >= 0) {
         int next = index + "<mission".length();
         if (next < xml.length()) {
            char following = xml.charAt(next);
            if (following == '>' || Character.isWhitespace(following)) {
               return true;
            }
         }
         index = next;
      }
      return false;
   }

   private static boolean copyCache(Path source, Path target) throws IOException {
      if (!Files.isRegularFile(source)) {
         return false;
      }
      Files.createDirectories(target.getParent());
      Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
      return true;
   }

   private static void writeCache(Path cache, String xml) throws IOException {
      Files.createDirectories(cache.getParent());
      Path temporary = Files.createTempFile(cache.getParent(), "custom-missions-", ".tmp");
      try {
         Files.writeString(temporary, xml, StandardCharsets.UTF_8);
         try {
            Files.move(temporary, cache, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporary, cache, StandardCopyOption.REPLACE_EXISTING);
         }
      } finally {
         Files.deleteIfExists(temporary);
      }
   }

   static void upload(Session session) throws IOException {
      Map<String, String> fields = new LinkedHashMap<>();
      addField(session.directory(), RMS_PROFILE, "avatar", fields);
      addField(session.directory(), RMS_MISSIONS, "missions", fields);
      String record = newestAreaRecord(session.directory(), session.preferredRecord());
      if (record != null) {
         byte[] data = Files.readAllBytes(recordFile(session.directory(), record));
         if (data.length > 0) {
            fields.put("gamefield", Base64.encode(data));
            fields.put("area", areaFor(record));
         }
      }
      if (!fields.isEmpty()) {
         session.connection().uploadSave(session.accountId(), fields);
      }
   }

   static void delete(Session session) {
      delete(session.directory());
   }

   private static void copyGameSettings(Path dataRoot, Path sessionDirectory) throws IOException {
      Path source = dataRoot.resolve("files");
      Path target = sessionDirectory.resolve("files");
      for (String record : GAME_SETTINGS) {
         Path file = source.resolve(record).resolve("1");
         if (!Files.isRegularFile(file) || Files.size(file) == 0) {
            continue;
         }
         Path destination = target.resolve(record).resolve("1");
         Files.createDirectories(destination.getParent());
         Files.copy(file, destination, StandardCopyOption.REPLACE_EXISTING);
      }
   }

   private static void addField(Path directory, String record, String field, Map<String, String> fields) throws IOException {
      Path file = recordFile(directory, record);
      if (!Files.isRegularFile(file) || Files.size(file) == 0) {
         return;
      }
      fields.put(field, Base64.encode(Files.readAllBytes(file)));
   }

   private static String newestAreaRecord(Path directory, String preferred) throws IOException {
      Path filesDir = directory.resolve("files");
      if (!Files.isDirectory(filesDir)) {
         return null;
      }
      try (Stream<Path> files = Files.list(filesDir)) {
         return files
            .filter(Files::isDirectory)
            .map(path -> path.getFileName().toString())
            .filter(name -> name.endsWith("_data"))
            .map(name -> name.substring(0, name.length() - "_data".length()))
            .filter(ServerSaveSync::isAreaRecord)
            .filter(name -> {
               try {
                  Path file = recordFile(directory, name);
                  return Files.isRegularFile(file) && Files.size(file) > 0;
               } catch (IOException exception) {
                  return false;
               }
            })
            .max(Comparator.<String>comparingLong(name -> modified(directory, name))
               .thenComparingInt(name -> name.equals(preferred) ? 1 : 0))
            .orElse(null);
      }
   }

   private static long modified(Path directory, String record) {
      try {
         return Files.getLastModifiedTime(recordFile(directory, record)).toMillis();
      } catch (IOException exception) {
         return Long.MIN_VALUE;
      }
   }

   private static void writeRecord(Path directory, String record, String base64) throws IOException {
      if (base64 == null || base64.isBlank()) {
         return;
      }
      Path file = recordFile(directory, record);
      byte[] data = Base64.decode(base64);
      if (data == null) {
         throw new IOException("Codificación no válida de la partida guardada para " + record);
      }
      Files.createDirectories(file.getParent());
      Files.write(file, data);
   }

   private static Path recordFile(Path directory, String record) {
      return directory.resolve("files").resolve(record + "_data").resolve("1");
   }

   private static boolean isAreaRecord(String record) {
      for (String areaRecord : AREA_RECORDS) {
         if (areaRecord.equals(record)) {
            return true;
         }
      }
      return false;
   }

   private static String recordFor(String area) {
      if ("circus".equals(area)) {
         return RMS_CIRCUS;
      }
      if ("downtown".equals(area)) {
         return RMS_DOWNTOWN;
      }
      if ("crashsite".equals(area)) {
         return RMS_CRASHSITE;
      }
      if ("cure".equals(area)) {
         return RMS_CURE;
      }
      return RMS_GAMEFIELD;
   }

   private static String areaFor(String record) {
      if (RMS_CIRCUS.equals(record)) {
         return "circus";
      }
      if (RMS_DOWNTOWN.equals(record)) {
         return "downtown";
      }
      if (RMS_CRASHSITE.equals(record)) {
         return "crashsite";
      }
      if (RMS_CURE.equals(record)) {
         return "cure";
      }
      return "home";
   }

   private static void delete(Path directory) {
      if (directory == null) {
         return;
      }
      try (Stream<Path> paths = Files.walk(directory)) {
         paths.sorted(Comparator.reverseOrder()).forEach(path -> {
            try {
               Files.deleteIfExists(path);
            } catch (IOException ignored) {
            }
         });
      } catch (IOException ignored) {
      }
   }
}
