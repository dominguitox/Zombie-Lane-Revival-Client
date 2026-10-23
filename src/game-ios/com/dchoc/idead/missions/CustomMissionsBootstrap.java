package com.dchoc.idead.missions;

import com.dchoc.idead.network.Base64;
import com.dchoc.parser.IParser;
import com.dchoc.parser.IParserHandler;
import com.dchoc.parser.XmlParser;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Installs custom mission definitions cached by the launcher before the
 * account save is loaded. Missions must exist before {@link MissionManager#load}
 * reads the saved mission states, otherwise their IDs are rejected.
 */
public final class CustomMissionsBootstrap {
   public static final String CACHE_PROPERTY = "zlport.custom.missions.cache";
   private static final String ELEMENT_MISSION = "mission";
   private static final String ELEMENT_DEFINITION = "definition";
   private static final String ELEMENT_OBJECTIVES = "objectives";
   private static final String ELEMENT_TEXTS = "texts";
   private static final int FIELD_NONE = 0;
   private static final int FIELD_DEFINITION = 1;
   private static final int FIELD_OBJECTIVES = 2;
   private static final int FIELD_TEXTS = 3;

   private CustomMissionsBootstrap() {
   }

   public static int installFromCache() {
      String cache = System.getProperty(CACHE_PROPERTY);
      if (cache == null || cache.isBlank()) {
         return 0;
      }

      Path file = Path.of(cache);
      if (!Files.isRegularFile(file)) {
         System.err.println("CustomMissionsBootstrap: cache not found: " + file);
         return 0;
      }

      try {
         Installer installer = new Installer();
         new XmlParser(Files.readAllBytes(file)).start(installer);
         if (installer.installed > 0) {
            System.out.println("CustomMissionsBootstrap: installed " + installer.installed
               + " custom missions from cache");
         }
         return installer.installed;
      } catch (Exception exception) {
         System.err.println("CustomMissionsBootstrap: could not read cache: " + exception);
         return 0;
      }
   }

   private static final class Installer implements IParserHandler {
      private final StringBuilder buffer = new StringBuilder();
      private String definition = "";
      private String objectives = "";
      private String texts = "";
      private int field = FIELD_NONE;
      private int installed;

      @Override
      public void startDocument(IParser parser) {
      }

      @Override
      public void endDocument(IParser parser) {
      }

      @Override
      public void startElement(IParser parser, String tagName) {
         if (ELEMENT_MISSION.equals(tagName)) {
            this.definition = "";
            this.objectives = "";
            this.texts = "";
         } else if (ELEMENT_DEFINITION.equals(tagName)) {
            this.field = FIELD_DEFINITION;
            this.buffer.setLength(0);
         } else if (ELEMENT_OBJECTIVES.equals(tagName)) {
            this.field = FIELD_OBJECTIVES;
            this.buffer.setLength(0);
         } else if (ELEMENT_TEXTS.equals(tagName)) {
            this.field = FIELD_TEXTS;
            this.buffer.setLength(0);
         }
      }

      @Override
      public void endElement(IParser parser, String tagName) {
         if (ELEMENT_DEFINITION.equals(tagName)) {
            this.definition = this.buffer.toString().trim();
            this.field = FIELD_NONE;
         } else if (ELEMENT_OBJECTIVES.equals(tagName)) {
            this.objectives = this.buffer.toString().trim();
            this.field = FIELD_NONE;
         } else if (ELEMENT_TEXTS.equals(tagName)) {
            this.texts = this.buffer.toString().trim();
            this.field = FIELD_NONE;
         } else if (ELEMENT_MISSION.equals(tagName)) {
            this.installMission();
            this.field = FIELD_NONE;
         }
      }

      @Override
      public void readAttribute(IParser parser, String name, String value, int start, int length) {
      }

      @Override
      public void readText(IParser parser, String text, int start, int length) {
         if (this.field != FIELD_NONE && text != null) {
            this.buffer.append(text);
         }
      }

      private void installMission() {
         if (this.definition.isEmpty()) {
            return;
         }

         byte[] missionBytes = Base64.decode(this.definition);
         if (missionBytes == null) {
            System.err.println("CustomMissionsBootstrap: skipping an invalid definition");
            return;
         }
         byte[] objectiveBytes = this.objectives.isEmpty() ? new byte[0] : Base64.decode(this.objectives);
         if (objectiveBytes == null) {
            objectiveBytes = new byte[0];
         }

         this.registerTexts(this.texts);
         DChocByteArray missionData = DChocByteArray.createByteArrayForReading(
            missionBytes, 0, missionBytes.length);
         DChocByteArray objectiveData = DChocByteArray.createByteArrayForReading(
            objectiveBytes, 0, objectiveBytes.length);
         if (MissionManager.addCustomMission(missionData, objectiveData) != null) {
            this.installed++;
         }
      }

      private void registerTexts(String payload) {
         if (payload == null || payload.isEmpty()) {
            return;
         }
         for (String entry : payload.split(";", -1)) {
            if (entry.isEmpty()) {
               continue;
            }
            int separator = entry.indexOf(':');
            if (separator <= 0) {
               System.err.println("CustomMissionsBootstrap: ignoring a malformed text entry");
               continue;
            }
            try {
               int id = Integer.parseInt(entry.substring(0, separator));
               byte[] encoded = Base64.decode(entry.substring(separator + 1));
               if (encoded != null) {
                  Toolkit.registerCustomText(id, new String(encoded, StandardCharsets.UTF_8));
               }
            } catch (RuntimeException exception) {
               System.err.println("CustomMissionsBootstrap: ignoring an invalid text entry: " + exception);
            }
         }
      }
   }
}
