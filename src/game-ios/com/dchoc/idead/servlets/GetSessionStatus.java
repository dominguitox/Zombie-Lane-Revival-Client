package com.dchoc.idead.servlets;

import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.commands.ActionCommand;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.GiftItem;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.CustomMissions;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.neighbors.NeighborGift;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.network.Base64;
import com.dchoc.parser.IParser;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.HTTPResponse;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import com.dchoc.windows.Window;
import com.dchoc.windows.WindowSocial;
import java.util.HashSet;
import java.util.Set;

public class GetSessionStatus extends ServletRequestWithXml {
   public static final String ELEMENT_ACTION = "action";
   public static final String ELEMENT_AMOUNT = "amount";
   public static final String ELEMENT_DEFINITION = "definition";
   public static final String ELEMENT_GIFT = "gift";
   public static final String ELEMENT_ID = "id";
   public static final String ELEMENT_ITEM_ID = "item_id";
   public static final String ELEMENT_MAILBOX_REVISION = "mailbox_revision";
   public static final String ELEMENT_MISSION = "mission";
   public static final String ELEMENT_MISSION_REVISION = "mission_revision";
   public static final String ELEMENT_NEIGHBOR_ID = "neighbor_dcgid";
   public static final String ELEMENT_OBJECTIVES = "objectives";
   public static final String ELEMENT_SENDER_ID = "sender_dcgId";
   public static final String ELEMENT_SHOW = "show";
   public static final String ELEMENT_SHOW_REVISION = "show_revision";
   public static final String ELEMENT_TEXTS = "texts";
   public static final String ELEMENT_UPDATED_HEALTH = "updated_health";
   private static final int FIELD_NONE = 0;
   private static final int FIELD_AMOUNT = 1;
   private static final int FIELD_DEFINITION = 2;
   private static final int FIELD_ID = 3;
   private static final int FIELD_ITEM_ID = 4;
   private static final int FIELD_MAILBOX_REVISION = 5;
   private static final int FIELD_MISSION_REVISION = 6;
   private static final int FIELD_NEIGHBOR_ID = 7;
   private static final int FIELD_OBJECTIVES = 8;
   private static final int FIELD_SENDER_ID = 9;
   private static final int FIELD_SHOW_REVISION = 10;
   private static final int FIELD_TEXTS = 11;
   private static final int FIELD_UPDATED_HEALTH = 12;
   private static final int FIELD_SHOW = 13;
   private final StringBuilder mBuffer = new StringBuilder();
   private final Set<Long> mPendingGiftIds = new HashSet<>();
   private final Set<Long> mPendingActionIds = new HashSet<>();
   private String mDefinition = "";
   private String mObjectives = "";
   private String mTexts = "";
   private String mNeighborId;
   private String mSenderId;
   private int mField = FIELD_NONE;
   private int mItemId = -1;
   private int mUpdatedHealth = 1;
   private int mAmount = 1;
   private long mId;
   private long mMissionRevision;
   private long mShowRevision;
   private long mMailboxRevision;
   private long mPreviousMailboxRevision;
   private boolean mMailboxChanged;
   private boolean mActionsChanged;

   public GetSessionStatus() {
      super("GetSessionStatus", false);
   }

   public void init() {
      this.clearParameters();
      this.mPreviousMailboxRevision = this.mMailboxRevision;
      this.addParameter(ELEMENT_MISSION_REVISION, Long.toString(this.mMissionRevision));
      this.addParameter(ELEMENT_SHOW_REVISION, Long.toString(this.mShowRevision));
      this.addParameter(ELEMENT_MAILBOX_REVISION, Long.toString(this.mMailboxRevision));
      this.mBuffer.setLength(0);
      this.mDefinition = "";
      this.mObjectives = "";
      this.mTexts = "";
      this.mNeighborId = null;
      this.mSenderId = null;
      this.mField = FIELD_NONE;
      this.mItemId = -1;
      this.mUpdatedHealth = 1;
      this.mAmount = 1;
      this.mId = 0L;
      this.mMailboxChanged = false;
      this.mActionsChanged = false;
      this.mPendingGiftIds.clear();
      this.mPendingActionIds.clear();
   }

   @Override
   public void processResponse() {
      boolean reachable = this.mResponse != null;
      int responseCode = this.mResponse == null ? HTTPResponse.RESPONSE_CODE_ERROR : this.mResponse.getResponseCode();
      super.processResponse();
      GameEngine engine = GameEngine.getInstance();
      if (engine != null) {
         engine.setServerReachable(reachable);
         if (responseCode == 404) {
            engine.setSessionStatusUnsupported();
         }
      }
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
      } else if (ELEMENT_GIFT.equals(tagName)) {
         this.mId = 0L;
         this.mItemId = -1;
         this.mAmount = 1;
         this.mSenderId = null;
      } else if (ELEMENT_ACTION.equals(tagName)) {
         this.mId = 0L;
         this.mItemId = -1;
         this.mUpdatedHealth = 1;
         this.mNeighborId = null;
      } else if (ELEMENT_ID.equals(tagName)) {
         this.mField = FIELD_ID;
         this.mBuffer.setLength(0);
      } else if (ELEMENT_ITEM_ID.equals(tagName)) {
         this.mField = FIELD_ITEM_ID;
         this.mBuffer.setLength(0);
      } else if (ELEMENT_AMOUNT.equals(tagName)) {
         this.mField = FIELD_AMOUNT;
         this.mBuffer.setLength(0);
      } else if (ELEMENT_NEIGHBOR_ID.equals(tagName)) {
         this.mField = FIELD_NEIGHBOR_ID;
         this.mBuffer.setLength(0);
      } else if (ELEMENT_SENDER_ID.equals(tagName)) {
         this.mField = FIELD_SENDER_ID;
         this.mBuffer.setLength(0);
      } else if (ELEMENT_UPDATED_HEALTH.equals(tagName)) {
         this.mField = FIELD_UPDATED_HEALTH;
         this.mBuffer.setLength(0);
      } else if (ELEMENT_MISSION_REVISION.equals(tagName)) {
         this.mField = FIELD_MISSION_REVISION;
         this.mBuffer.setLength(0);
      } else if (ELEMENT_SHOW_REVISION.equals(tagName)) {
         this.mField = FIELD_SHOW_REVISION;
         this.mBuffer.setLength(0);
      } else if (ELEMENT_MAILBOX_REVISION.equals(tagName)) {
         this.mField = FIELD_MAILBOX_REVISION;
         this.mBuffer.setLength(0);
      } else if (ELEMENT_SHOW.equals(tagName)) {
         this.mField = FIELD_SHOW;
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
      } else if (ELEMENT_GIFT.equals(tagName)) {
         this.installGift();
         this.mField = FIELD_NONE;
      } else if (ELEMENT_ACTION.equals(tagName)) {
         this.installAction();
         this.mField = FIELD_NONE;
      } else if (ELEMENT_ID.equals(tagName)) {
         this.mId = ToolkitHelpers.parseLong(this.mBuffer.toString(), 0L);
         this.mField = FIELD_NONE;
      } else if (ELEMENT_ITEM_ID.equals(tagName)) {
         this.mItemId = ToolkitHelpers.parseInt(this.mBuffer.toString(), -1);
         this.mField = FIELD_NONE;
      } else if (ELEMENT_AMOUNT.equals(tagName)) {
         this.mAmount = ToolkitHelpers.parseInt(this.mBuffer.toString(), 1);
         this.mField = FIELD_NONE;
      } else if (ELEMENT_NEIGHBOR_ID.equals(tagName)) {
         this.mNeighborId = this.mBuffer.toString();
         this.mField = FIELD_NONE;
      } else if (ELEMENT_SENDER_ID.equals(tagName)) {
         this.mSenderId = this.mBuffer.toString();
         this.mField = FIELD_NONE;
      } else if (ELEMENT_UPDATED_HEALTH.equals(tagName)) {
         this.mUpdatedHealth = ToolkitHelpers.parseInt(this.mBuffer.toString(), 1);
         this.mField = FIELD_NONE;
      } else if (ELEMENT_MISSION_REVISION.equals(tagName)) {
         this.mMissionRevision = parseRevision(this.mBuffer.toString());
         this.mField = FIELD_NONE;
      } else if (ELEMENT_SHOW_REVISION.equals(tagName)) {
         this.mShowRevision = parseRevision(this.mBuffer.toString());
         this.mField = FIELD_NONE;
      } else if (ELEMENT_MAILBOX_REVISION.equals(tagName)) {
         long revision = parseRevision(this.mBuffer.toString());
         this.mMailboxChanged = revision != this.mMailboxRevision;
         this.mMailboxRevision = revision;
         this.mField = FIELD_NONE;
      }
   }

   @Override
   public void endDocument(IParser parser) {
      WindowSocial social = this.socialWindow();
      if (this.mMailboxChanged && social != null) {
         social.syncPendingGifts(this.mPendingGiftIds);
         this.mActionsChanged |= social.syncPendingActions(this.mPendingActionIds);
      } else if (this.mMailboxChanged) {
         this.mMailboxRevision = this.mPreviousMailboxRevision;
      }
      if (this.mActionsChanged) {
         IsometricUtils.getScene().setUpdateNeighbors();
      }
      super.endDocument(parser);
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
      MissionManager.addCustomMission(
         DChocByteArray.createByteArrayForReading(missionBytes, 0, missionBytes.length),
         DChocByteArray.createByteArrayForReading(objectiveBytes, 0, objectiveBytes.length));
   }

   private void showMission(String missionId) {
      if (missionId == null || missionId.isBlank()) {
         return;
      }
      try {
         CustomMissions.show(Integer.parseInt(missionId.trim()));
      } catch (NumberFormatException exception) {
         this.debug("Ignoring a malformed show request: " + missionId);
      }
   }

   private void installGift() {
      if (this.mId <= 0L || this.mItemId < 0 || this.mSenderId == null) {
         return;
      }
      WindowSocial social = this.socialWindow();
      if (social == null) {
         return;
      }
      this.mPendingGiftIds.add(this.mId);
      if (social.getGift(this.mId) != null) {
         return;
      }
      GiftItem giftItem = ItemManager.getGift(this.mItemId);
      if (giftItem == null) {
         this.debug("Ignoring a gift with an unknown item: " + this.mItemId);
         return;
      }
      NeighborProfile sender = social.getNeighbor(this.mSenderId);
      if (sender == null) {
         sender = new NeighborProfile(Toolkit.getText(2236), this.mSenderId, 1, 3, 1926);
      }
      NeighborGift gift = new NeighborGift(sender, giftItem, this.mId, this.mAmount);
      gift.setStatus(2);
      social.addGift(gift);
   }

   private void installAction() {
      if (this.mId <= 0L || this.mItemId < 0 || this.mNeighborId == null) {
         return;
      }
      WindowSocial social = this.socialWindow();
      if (social == null) {
         return;
      }
      this.mPendingActionIds.add(this.mId);
      NeighborProfile neighbor = social.getNeighbor(this.mNeighborId);
      if (neighbor == null) {
         neighbor = new NeighborProfile(Toolkit.getText(2236), this.mNeighborId, 1, 3, 1926);
         social.addNeighbor(neighbor);
      }
      if (neighbor.getCommand(this.mId) == null) {
         neighbor.addCommand(new ActionCommand(this.mItemId, this.mUpdatedHealth, this.mId));
         this.mActionsChanged = true;
      }
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

   private WindowSocial socialWindow() {
      Window window = WindowManager.getWindow((byte)18);
      return window instanceof WindowSocial ? (WindowSocial)window : null;
   }

   private static long parseRevision(String value) {
      try {
         return Math.max(0L, Long.parseLong(value.trim()));
      } catch (NumberFormatException exception) {
         return 0L;
      }
   }

   @Override
   public void debug(String message) {
      System.out.println("GetSessionStatus: " + message);
   }
}
