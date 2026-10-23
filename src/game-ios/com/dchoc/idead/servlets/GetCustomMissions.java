package com.dchoc.idead.servlets;

import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.network.Base64;
import com.dchoc.parser.IParser;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;

/**
 * Downloads custom missions published by the local server and injects them into
 * the running mission manager.
 *
 * <p>Custom missions (holiday events, admin-authored missions) are stored on the
 * server as Base64-encoded binary definitions in the same layout the game uses
 * for its packed mission tables. This servlet reads the server's XML response
 * and hands each mission and its objectives to
 * {@link MissionManager#addCustomMission}. The missions are added hidden: they do
 * not appear on the mission HUD until {@link MissionManager#showCustomMission}
 * is invoked for them.
 */
public class GetCustomMissions extends ServletRequestWithXml {
   public static final String ELEMENT_MISSION = "mission";
   public static final String ELEMENT_DEFINITION = "definition";
   public static final String ELEMENT_OBJECTIVES = "objectives";
   public static final String ELEMENT_TEXTS = "texts";
   public static final String ELEMENT_SHOW = "show";
   public static final String ELEMENT_REVISION = "revision";
   private static final int FIELD_NONE = 0;
   private static final int FIELD_DEFINITION = 1;
   private static final int FIELD_OBJECTIVES = 2;
   private static final int FIELD_SHOW = 3;
   private static final int FIELD_TEXTS = 4;
   private static final int FIELD_REVISION = 5;
   private final StringBuilder mBuffer = new StringBuilder();
   private String mDefinition = "";
   private String mObjectives = "";
   private String mTexts = "";
   private int mField = FIELD_NONE;
   private long mRevision;

   public GetCustomMissions() {
      super("GetCustomMissions", false);
   }

   public void init() {
      this.clearParameters();
      if (this.mRevision > 0L) {
         this.addParameter(ELEMENT_REVISION, Long.toString(this.mRevision));
      }
      this.mBuffer.setLength(0);
      this.mDefinition = "";
      this.mObjectives = "";
      this.mTexts = "";
      this.mField = FIELD_NONE;
   }

   @Override
   public void startElement(IParser parser, String tagName) {
      super.startElement(parser, tagName);
      if (ELEMENT_MISSION.equals(tagName)) {
         this.mDefinition = "";
         this.mObjectives = "";
         this.mTexts = "";
      } else if (ELEMENT_DEFINITION.equals(tagName)) {
         this.mField = FIELD_DEFINITION;
         this.mBuffer.setLength(0);
      } else if (ELEMENT_OBJECTIVES.equals(tagName)) {
         this.mField = FIELD_OBJECTIVES;
         this.mBuffer.setLength(0);
      } else if (ELEMENT_TEXTS.equals(tagName)) {
         this.mField = FIELD_TEXTS;
         this.mBuffer.setLength(0);
      } else if (ELEMENT_SHOW.equals(tagName)) {
         this.mField = FIELD_SHOW;
         this.mBuffer.setLength(0);
      } else if (ELEMENT_REVISION.equals(tagName)) {
         this.mField = FIELD_REVISION;
         this.mBuffer.setLength(0);
      }
   }

   @Override
   public void readText(IParser parser, String text, int start, int length) {
      super.readText(parser, text, start, length);
      if (this.mField != FIELD_NONE && text != null) {
         this.mBuffer.append(text);
      }
   }

   @Override
   public void endElement(IParser parser, String tagName, String text) {
      if (ELEMENT_DEFINITION.equals(tagName)) {
         this.mDefinition = this.mBuffer.toString();
         this.mField = FIELD_NONE;
      } else if (ELEMENT_OBJECTIVES.equals(tagName)) {
         this.mObjectives = this.mBuffer.toString();
         this.mField = FIELD_NONE;
      } else if (ELEMENT_TEXTS.equals(tagName)) {
         this.mTexts = this.mBuffer.toString();
         this.mField = FIELD_NONE;
      } else if (ELEMENT_MISSION.equals(tagName)) {
         this.installMission();
         this.mField = FIELD_NONE;
      } else if (ELEMENT_SHOW.equals(tagName)) {
         this.showMission(this.mBuffer.toString());
         this.mField = FIELD_NONE;
      } else if (ELEMENT_REVISION.equals(tagName)) {
         try {
            this.mRevision = Long.parseLong(this.mBuffer.toString().trim());
         } catch (NumberFormatException exception) {
            this.debug("Ignoring a malformed revision: " + this.mBuffer);
         }
         this.mField = FIELD_NONE;
      }
   }

   private void showMission(String missionId) {
      if (missionId == null || missionId.isBlank()) {
         return;
      }
      try {
         com.dchoc.idead.missions.CustomMissions.show(Integer.parseInt(missionId.trim()));
      } catch (NumberFormatException exception) {
         this.debug("Ignoring a malformed show request: " + missionId);
      }
   }

   private void installMission() {
      if (this.mDefinition.isEmpty()) {
         return;
      }

      byte[] missionBytes = Base64.decode(this.mDefinition);
      if (missionBytes == null) {
         this.debug("Skipping a custom mission with an invalid definition payload.");
         return;
      }
      byte[] objectiveBytes = this.mObjectives == null || this.mObjectives.isEmpty()
         ? new byte[0]
         : Base64.decode(this.mObjectives);
      if (objectiveBytes == null) {
         objectiveBytes = new byte[0];
      }

      this.registerTexts(this.mTexts);
      DChocByteArray missionData = DChocByteArray.createByteArrayForReading(missionBytes, 0, missionBytes.length);
      DChocByteArray objectiveData = DChocByteArray.createByteArrayForReading(objectiveBytes, 0, objectiveBytes.length);
      MissionManager.addCustomMission(missionData, objectiveData);
   }

   private void registerTexts(String payload) {
      if (payload == null || payload.isEmpty()) {
         return;
      }
      String[] entries = payload.split(";", -1);
      for (String entry : entries) {
         if (entry.isEmpty()) continue;
         int separator = entry.indexOf(':');
         if (separator <= 0) {
            this.debug("Ignoring malformed custom text entry.");
            continue;
         }
         try {
            int id = Integer.parseInt(entry.substring(0, separator));
            byte[] encoded = Base64.decode(entry.substring(separator + 1));
            if (encoded != null) {
               Toolkit.registerCustomText(id, new String(encoded, java.nio.charset.StandardCharsets.UTF_8));
            }
         } catch (RuntimeException exception) {
            this.debug("Ignoring invalid custom text entry: " + exception);
         }
      }
   }

   @Override
   public void debug(String message) {
      System.out.println("GetCustomMissions: " + message);
   }
}
