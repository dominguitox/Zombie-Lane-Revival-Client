package zlport.ios.runtime;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Map;

final class PlayerActionLog {
   private static final Logger LOG = System.getLogger("zlport.ios");
   private final Path path;
   private final String profileId;

   PlayerActionLog(Path profileDirectory) {
      Path directory = profileDirectory.toAbsolutePath().normalize();
      this.path = directory.resolve("actions.jsonl");
      this.profileId = directory.getFileName().toString();
   }

   synchronized void append(Map<String, String> action) {
      StringBuilder line = new StringBuilder("{");
      appendField(line, "timestamp", Instant.now().toString());
      appendField(line, "profile_id", this.profileId);
      for (Map.Entry<String, String> field : action.entrySet()) {
         appendField(line, field.getKey(), field.getValue());
      }
      line.append('}').append(System.lineSeparator());

      try {
         Files.createDirectories(this.path.getParent());
         Files.writeString(
            this.path,
            line,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.APPEND
         );
      } catch (IOException exception) {
         LOG.log(Level.WARNING, "No se pudo registrar una acción del perfil en " + this.path, exception);
      }
   }

   private static void appendField(StringBuilder json, String name, String value) {
      if (json.length() > 1) {
         json.append(',');
      }
      appendString(json, name);
      json.append(':');
      appendString(json, value);
   }

   private static void appendString(StringBuilder json, String value) {
      json.append('"');
      for (int index = 0; index < value.length(); index++) {
         char character = value.charAt(index);
         switch (character) {
            case '"' -> json.append("\\\"");
            case '\\' -> json.append("\\\\");
            case '\b' -> json.append("\\b");
            case '\f' -> json.append("\\f");
            case '\n' -> json.append("\\n");
            case '\r' -> json.append("\\r");
            case '\t' -> json.append("\\t");
            default -> {
               if (character < 0x20) {
                  json.append("\\u");
                  String hex = Integer.toHexString(character);
                  json.append("0".repeat(4 - hex.length())).append(hex);
               } else {
                  json.append(character);
               }
            }
         }
      }
      json.append('"');
   }
}
