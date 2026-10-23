package com.dchoc.idead.missions;

import com.dchoc.parser.Json;
import com.dchoc.toolkit.Toolkit;
import java.util.Map;

/**
 * Resolves a JSON text entry ({@code {"textId": N, "text": "..."}}) into a text
 * ID and registers the literal string as a runtime override, so the text stored
 * in {@code missions.json} is what the game shows regardless of the packed
 * language table.
 */
final class MissionJsonText {
   private static int smNextSyntheticId = -100000;

   private MissionJsonText() {
   }

   static int resolve(Map<String, Object> entry) {
      if (entry == null) {
         return -1;
      }

      int id = Json.intValue(entry, "textId", -1);
      String text = Json.stringValue(entry, "text");
      if (text == null) {
         return id;
      }

      if (id == -1) {
         id = smNextSyntheticId--;
      }

      if (id != -2) {
         Toolkit.registerText(id, text);
      }

      return id;
   }
}
