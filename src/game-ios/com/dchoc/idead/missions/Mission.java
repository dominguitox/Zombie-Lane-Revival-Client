package com.dchoc.idead.missions;

import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.Time;
import com.dchoc.idead.events.EventManager;
import com.dchoc.idead.events.TimedEvent;
import com.dchoc.idead.fixes.Fixes;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tracking.MissionTrackingEvent;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.parser.Json;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;
import java.util.Vector;

public class Mission implements CsvIDs {
   public static final int ID_INVALID = -1;
   public static final int INITIAL_OBJECTIVES_CAPACITY = 3;
   public static final int STATE_ACCEPTED = 2;
   public static final int STATE_INACTIVE = 0;
   public static final int STATE_REWARDS_COLLECTED = 3;
   public static final int STATE_WAITING_TO_BE_ACCEPTED = 1;
   public static final int TYPE_COMING_MISSION = 3008;
   public static final int TYPE_HIDDEN_MISSION = 2286;
   public static final int TYPE_INVALID = -1;
   public static final int TYPE_MISSION = 2292;
   public static final int TYPE_TIP = 2399;
   public static final int TYPE_TUTORIAL = 2288;
   private long mAcceptanceTime;
   protected boolean mCanRetry;
   protected boolean mCompleted;
   protected int mCompletedTextID;
   protected int mDebriefTextID;
   protected int mDescriptionTextID;
   private long mDuration;
   private int mExpirationEventType;
   private int[] mFacebookPost;
   protected boolean mHidden;
   protected int mHintTextID;
   protected int mID;
   protected int mIconAnimationID;
   private int[] mIntroEventParameters;
   private int mIntroEventType;
   protected int mIntroIconAnimationID;
   private long mLastUpdateCounter;
   private MissionTrackingEvent mMissionEvent;
   protected boolean mNew;
   protected boolean mObjectiveCompleted;
   protected Vector mObjectives = new Vector(3);
   protected int mRequiredLevel;
   private int[] mRequiredMissions;
   private int mRewardCoins;
   private int[][] mRewardItems;
   private int mRewardXp;
   protected int mState;
   protected int mTitleTextID;
   private int mTwitterPost;
   protected int mType;
   private boolean mLocked;
   private boolean mSaveAfterCompleted;
   private int mNotificationID;

   public Mission() {
      this.mNew = true;
      this.mIconAnimationID = -1;
      this.mIntroIconAnimationID = -1;
      this.mIntroEventType = -1;
      this.mExpirationEventType = -1;
      this.mDebriefTextID = -1;
      this.mCompletedTextID = -1;
      this.mHintTextID = -1;
      this.mTitleTextID = -1;
      this.mDescriptionTextID = -1;
      this.mLastUpdateCounter = Time.getTimestamp();
   }

   public Mission(DChocByteArray data) throws EOFException {
      this();
      this.loadData(data);
      this.initTrackingEvent();
   }

   private void initTrackingEvent() {
      if (this.mType == 2399) {
         this.mMissionEvent = new MissionTrackingEvent(this.mID);
         this.mMissionEvent.setType(18);
      } else if (this.mType == 2292) {
         this.mMissionEvent = new MissionTrackingEvent(this.mID);
      }
   }

   /**
    * Build a mission from one object of the JSON export (the schema produced by
    * {@code dump_missions.py} and the Mission Editor). Objectives, setups, and
    * events are attached by {@code MissionManager} after construction.
    */
   public static Mission fromJson(java.util.Map<String, Object> data) {
      Mission mission = new Mission();
      mission.mID = Json.intValue(data, "id", -1);
      java.util.Map<String, Object> texts = Json.object(data, "texts");
      mission.mTitleTextID = MissionJsonText.resolve(Json.object(texts, "title"));
      mission.mHintTextID = MissionJsonText.resolve(Json.object(texts, "hint"));
      mission.mDescriptionTextID = MissionJsonText.resolve(Json.object(texts, "description"));
      mission.mCompletedTextID = MissionJsonText.resolve(Json.object(texts, "completed"));
      java.util.Map<String, Object> rewards = Json.object(data, "rewards");
      mission.mRewardXp = Json.intValue(rewards, "xp", 0);
      mission.mRewardCoins = Json.intValue(rewards, "coins", 0);
      mission.mRewardItems = readRewardPairs(Json.array(rewards, "items"));
      java.util.Map<String, Object> requirements = Json.object(data, "requirements");
      mission.mRequiredLevel = Json.intValue(requirements, "level", 0);
      mission.mRequiredMissions = Json.intArray(Json.get(requirements, "missionIds"));
      mission.mLocked = Json.booleanValue(requirements, "explicitlyLocked", false);
      java.util.Map<String, Object> duration = Json.object(data, "duration");
      mission.mDuration = Json.longValue(duration, "milliseconds",
            Json.longValue(duration, "sourceValueMinutes", -1L));
      mission.mType = Json.intValue(data, "typeId", -1);
      java.util.Map<String, Object> social = Json.object(data, "socialPost");
      mission.mFacebookPost = Json.intArray(Json.get(social, "facebook"));
      mission.mTwitterPost = Json.intValue(social, "twitter", -1);
      mission.mCanRetry = Json.booleanValue(data, "canRetryAfterExpiration", false);
      mission.mSaveAfterCompleted = Json.booleanValue(data, "saveAfterCompleted", false);
      mission.mIconAnimationID = Json.intValue(data, "iconAnimationId", -1);
      java.util.Map<String, Object> intro = Json.object(data, "intro");
      mission.mIntroEventType = Json.intValue(intro, "eventTypeId", -1);
      mission.mIntroEventParameters = Json.intArray(Json.get(intro, "parameters"));
      mission.mIntroIconAnimationID = Json.intValue(intro, "iconAnimationId", -1);
      mission.mExpirationEventType = Json.intValue(data, "expirationEventTypeId", -1);
      mission.mNotificationID = Json.intValue(data, "notificationId", -1);
      mission.initTrackingEvent();
      return mission;
   }

   private static int[][] readRewardPairs(java.util.List<Object> list) {
      if (list == null || list.isEmpty()) {
         return null;
      }

      int[][] values = new int[list.size()][2];
      for (int i = 0; i < list.size(); i++) {
         java.util.Map<String, Object> pair = Json.asObject(list.get(i));
         values[i][0] = Json.intValue(pair, "id", 0);
         values[i][1] = Json.intValue(pair, "amount", 0);
      }

      return values;
   }

   public int logicUpdate(int deltaTime) {
      for (int i = 0; i < this.mObjectives.size(); i++) {
         MissionObjective objective = (MissionObjective)this.mObjectives.elementAt(i);
         if (objective.logicUpdate(deltaTime) == 11) {
            this.mLastUpdateCounter = Time.getTimestamp();
         }
      }

      this.checkForCompletedObjectives(false);
      return 0;
   }

   public void updateCounters(int type, int subType, int value, int amount) {
      for (int i = 0; i < this.mObjectives.size(); i++) {
         MissionObjective objective = (MissionObjective)this.mObjectives.elementAt(i);
         if (!objective.isGoalReached() && objective.isResetCounter() && objective.updateCounter(type, subType, value, amount) && this.mType == 2288) {
            TutorialFlow.tutorialObjectiveCompleted(objective);
         }

         if (objective.isValidUpdate(type, subType, value)) {
            this.mLastUpdateCounter = Time.getTimestamp();
         }
      }
   }

   public void changeState(int state) {
      if (this.mState != state) {
         Vector active = MissionManager.getActiveMissions();
         if (this.mState == 2) {
            active.removeElement(this);
            if (this.mType == 2288) {
               TutorialFlow.tutorialMissionCompleted(this);
            }
         } else if (state == 2) {
            if (!active.contains(this)) {
               active.addElement(this);
            }

            if (this.mType == 2288) {
               TutorialFlow.tutorialMissionActivated(this);
            }
         }

         if (state == 2) {
            if (this.mID == Tuner.getMissionToUnlockVisitRob()) {
               GameEngineUtils.getRob().unlock();
            }

            this.mAcceptanceTime = System.currentTimeMillis();
            MissionManager.updateCounter(2704, this.mID, 1);
            MissionManager.updateCounter(2704, 80, 1);
            this.resetObjectives();
            this.executeSetups();
            if (this.mMissionEvent != null) {
               this.mMissionEvent.trackStarted();
            }
         } else if (state == 3) {
            this.consumeObjectives();
            this.resetSetups(false);
            if (this.mRewardCoins > 0) {
               PlayerProfile.addCoins(this.mRewardCoins);
            }

            if (this.mRewardXp > 0) {
               PlayerProfile.addXp(this.mRewardXp);
            }

            if (this.mRewardItems != null) {
               for (int i = 0; i < this.mRewardItems.length; i++) {
                  Item item = ItemManager.getItem(this.mRewardItems[i][0]);
                  PlayerProfile.getInventory().addItem(item, this.mRewardItems[i][1]);
               }
            }

            if (this.mMissionEvent != null) {
               this.mMissionEvent.trackCompleted();
            }
         }

         this.mState = state;
      }
   }

   public boolean activate() {
      if (this.mState != 0) {
         return false;
      }

      this.changeState(this.mIntroEventType == -1 ? 2 : 1);
      this.mLastUpdateCounter = Time.getTimestamp();
      return true;
   }

   public boolean deactivate() {
      int previous = this.mState;
      if (previous != 0) {
         this.resetSetups(true);
         this.changeState(0);
      }

      return previous != 0;
   }

   public void executeSetups() {
      Vector setups = MissionManager.getMissionSetups();

      for (int i = 0; i < setups.size(); i++) {
         MissionSetup setup = (MissionSetup)setups.elementAt(i);
         if (setup.getMissionID() == this.mID && !setup.execute()) {
            MissionManager.addDelayedMissionSetup(setup);
         }
      }
   }

   public void resetSetups(boolean force) {
      Vector setups = MissionManager.getMissionSetups();

      for (int i = 0; i < setups.size(); i++) {
         MissionSetup setup = (MissionSetup)setups.elementAt(i);
         if (setup.getMissionID() == this.mID) {
            setup.reset(force);
         }
      }
   }

   public boolean accept() {
      if (this.mIntroEventType != -1 && this.mState == 1) {
         this.changeState(2);
         return true;
      } else {
         return false;
      }
   }

   public void collectRewards() {
      if (this.mState == 2) {
         this.changeState(3);
      }
   }

   public boolean hasIntroEvent() {
      return this.mIntroEventType != -1;
   }

   public void executeIntroEvent() {
      EventManager.addEvent(new TimedEvent(0, this.mIntroEventType, this.mIntroEventParameters, this));
   }

   public void introEventFinished() {
      if (this.mState == 1) {
         this.accept();
      }
   }

   public void executeExpirationEvent() {
      if (this.mExpirationEventType == -1) {
         this.expirationEventFinished();
      } else {
         EventManager.addEvent(new TimedEvent(0, this.mExpirationEventType, null, this));
      }

      if (this.mMissionEvent != null) {
         this.mMissionEvent.trackExpired();
      }
   }

   public void expirationEventFinished() {
      if (this.mState == 2) {
         this.resetObjectives();
         this.resetSetups(true);
         if (this.mCanRetry) {
            this.changeState(0);
            this.mNew = true;
            MissionManager.setUpdateNewMissions();
         } else {
            this.mCompleted = true;
            this.changeState(4);
            MissionManager.setUpdateNewMissions();
         }
      }
   }

   public void executeOutroEvents() {
      Vector events = MissionManager.getMissionEvents();

      for (int i = 0; i < events.size(); i++) {
         MissionEvent event = (MissionEvent)events.elementAt(i);
         int id = event.getMissionID();
         int type = event.getMissionType();
         if (type != -1 && type == this.mType || id != -1 && id == this.mID) {
            event.execute(this);
         }
      }
   }

   public int getTimeLeft() {
      return (int)this.mDuration - (int)Time.getTimestampDelta(this.mAcceptanceTime);
   }

   public int getCountLeft() {
      int count = 0;

      for (int i = 0; i < this.mObjectives.size(); i++) {
         count += ((MissionObjective)this.mObjectives.elementAt(i)).getCountLeft();
      }

      return count;
   }

   public void checkForCompletedObjectives(boolean force) {
      if (!this.mCompleted && this.mState != 0) {
         if (force) {
            if (this.mState == 3) {
               this.mCompleted = true;
            } else {
               this.mCompleted = this.mState == 4;
               if (this.mState != 4) {
                  return;
               }
            }

            for (int i = 0; i < this.mObjectives.size(); i++) {
               MissionObjective objective = (MissionObjective)this.mObjectives.elementAt(i);
               if (!objective.isCompleted()) {
                  objective.setCounter(objective.getGoal());
               }
            }

            this.mObjectiveCompleted = false;
         } else {
            this.mObjectiveCompleted = false;
            boolean completed = true;

            for (int i = 0; i < this.mObjectives.size(); i++) {
               MissionObjective objective = (MissionObjective)this.mObjectives.elementAt(i);
               boolean objectiveCompleted = objective.isCompleted();
               if (objective.isJustCompleted()) {
                  this.mObjectiveCompleted = true;
                  iWrapper.playSoundFx(34);
                  objective.resetJustComplted();
                  if (this.mMissionEvent != null) {
                     this.mMissionEvent.trackObjectiveCompleted(objective.getID());
                  }
               }

               completed &= objectiveCompleted;
            }

            this.mCompleted = completed;
         }
      }
   }

   public void resetNew() {
      this.mNew = false;
   }

   public void resetObjectiveCompleted() {
      this.mObjectiveCompleted = false;
   }

   public boolean isInactive() {
      return this.mState == 0;
   }

   public void setCompleted(boolean completed) {
      this.mCompleted = completed;
   }

   public boolean isActive() {
      return this.mState == 2;
   }

   public void setObjectiveUpdateTime(long timestamp) {
      this.mLastUpdateCounter = timestamp;
   }

   public boolean hasDuration() {
      return this.mDuration >= 0L;
   }

   public void addObjective(MissionObjective objective) {
      this.mObjectives.addElement(objective);
   }

   public MissionObjective getObjective(int id) {
      for (int i = 0; i < this.mObjectives.size(); i++) {
         MissionObjective objective = (MissionObjective)this.mObjectives.elementAt(i);
         if (objective.getID() == id) {
            return objective;
         }
      }

      return null;
   }

   public MissionObjective getObjectiveByType(int type, int value) {
      for (int i = 0; i < this.mObjectives.size(); i++) {
         MissionObjective objective = (MissionObjective)this.mObjectives.elementAt(i);
         if (objective.getType() == type && (objective.getValue() == value || objective.getValue() == -1)) {
            return objective;
         }
      }

      return null;
   }

   public boolean isLockedByLevel() {
      return PlayerProfile.getLevel() < this.mRequiredLevel;
   }

   public boolean isLockedByMission() {
      if (this.mRequiredMissions != null) {
         for (int i = 0; i < this.mRequiredMissions.length; i++) {
            Mission mission = MissionManager.getMission(this.mRequiredMissions[i]);
            if (mission != null && mission.getState() != 3) {
               return true;
            }
         }
      }

      return false;
   }

   public void save(DChocByteArray data) {
      data.writeByte(this.mState);
      if (this.mState != 0) {
         if (this.mState == 3) {
            if (this.mType == 2399) {
               data.writeBoolean(this.mNew);
            }
         } else {
            data.writeBoolean(this.mNew);
            data.writeBoolean(this.mCompleted);
            data.writeBoolean(this.mObjectiveCompleted);
            if (this.hasDuration()) {
               ToolkitHelpers.writeLong(this.mAcceptanceTime, data);
            }

            for (int i = 0; i < this.mObjectives.size(); i++) {
               MissionObjective objective = (MissionObjective)this.mObjectives.elementAt(i);
               data.writeShort(objective.getID());
               objective.save(data);
            }

            ToolkitHelpers.writeLong(this.mLastUpdateCounter, data);
         }
      }
   }

   public void load(DChocByteArray data, int version) throws EOFException {
      this.mState = data.readByte();
      if (this.mState != 0) {
         if (version >= 133 && (this.mState & 0xFF) == 3) {
            this.mNew = this.mType == 2399 && data.readBoolean();
            this.checkForCompletedObjectives(true);
         } else {
            this.mNew = data.readBoolean();
            this.mCompleted = data.readBoolean();
            this.mObjectiveCompleted = data.readBoolean();
            if (this.hasDuration()) {
               this.mAcceptanceTime = Fixes.fixTimestamp(ToolkitHelpers.readLong(data));
            }

            for (int i = 0; i < this.mObjectives.size(); i++) {
               this.getObjective(data.readShort()).load(data);
            }

            this.mLastUpdateCounter = Fixes.fixTimestamp(ToolkitHelpers.readLong(data));
            this.checkForCompletedObjectives(false);
         }
      }
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mTitleTextID = data.readShort();
      this.mHintTextID = data.readShort();
      this.mDescriptionTextID = data.readShort();
      this.mCompletedTextID = data.readShort();
      data.readShort();
      this.mRewardXp = data.readShort();
      this.mRewardCoins = data.readShort();
      this.mRewardItems = ToolkitHelpers.readIntValueList(data);
      this.mRequiredMissions = ToolkitHelpers.readIntArray(data);
      this.mRequiredLevel = data.readByte();
      this.mType = data.readShort();
      this.mFacebookPost = ToolkitHelpers.readIntArray(data);
      this.mTwitterPost = data.readShort();
      int minutes = data.readInt();
      this.mDuration = minutes > 0 ? minutes * 60000L : minutes;
      this.mLocked = data.readBoolean();
      this.mCanRetry = data.readBoolean();
      this.mSaveAfterCompleted = data.readBoolean();
      this.mIconAnimationID = data.readInt();
      data.readInt();
      this.mIntroEventType = data.readShort();
      this.mIntroEventParameters = ToolkitHelpers.readIntArray(data);
      this.mIntroIconAnimationID = data.readInt();
      this.mExpirationEventType = data.readShort();
      this.mNotificationID = data.readShort();
   }

   public void consumeObjectives() {
      for (int i = 0; i < this.mObjectives.size(); i++) {
         MissionObjective objective = (MissionObjective)this.mObjectives.elementAt(i);
         if (objective.isConsumed() && !objective.isPurchased()) {
            int value = objective.getValue();
            int amount = objective.getGoal();
            switch (value) {
               case 7:
                  PlayerProfile.removeCash(amount);
                  break;
               case 8:
               case 10:
               case 12:
               default:
                  PlayerProfile.getInventory().removeItem(ItemManager.getItem(value), amount);
                  break;
               case 9:
                  PlayerProfile.removeCoins(amount);
                  break;
               case 11:
                  PlayerProfile.removeStone(amount);
                  break;
               case 13:
                  PlayerProfile.removeFood(amount);
            }
         }
      }
   }

   public void resetObjectives() {
      for (int i = 0; i < this.mObjectives.size(); i++) {
         MissionObjective objective = (MissionObjective)this.mObjectives.elementAt(i);
         if (objective.isResetCounter()) {
            objective.resetCounter();
         }

         objective.logicUpdate(0);
      }
   }

   public boolean isNew() {
      return this.mNew;
   }

   public boolean isObjectiveCompleted() {
      return this.mObjectiveCompleted;
   }

   public boolean isLocked() {
      return this.mLocked;
   }

   public boolean isSaveAfterCompleted() {
      return this.mSaveAfterCompleted;
   }

   public int getRewardCoins() {
      return this.mRewardCoins;
   }

   public int getRewardXp() {
      return this.mRewardXp;
   }

   public int[][] getRewardItems() {
      return this.mRewardItems;
   }

   public int getIconID() {
      return this.mIconAnimationID;
   }

   public int getIntroIconID() {
      return this.mIntroIconAnimationID;
   }

   public int getTitleTextID() {
      return this.mTitleTextID;
   }

   public int getDescriptionTextID() {
      return this.mDescriptionTextID;
   }

   public int getCompletedID() {
      return this.mCompletedTextID;
   }

   public int getHintTextID() {
      return this.mHintTextID;
   }

   public long getAcceptanceTime() {
      return this.mAcceptanceTime;
   }

   public long getObjectiveUpdatedTime() {
      return this.mLastUpdateCounter;
   }

   public long getDuration() {
      return this.mDuration;
   }

   public int[] getFacebookPost() {
      return this.mFacebookPost;
   }

   public int getTwitterPost() {
      return this.mTwitterPost;
   }

   public int getNotificationID() {
      return this.mNotificationID;
   }

   public int getRequiredLevel() {
      return this.mRequiredLevel;
   }

   public Vector getObjectives() {
      return this.mObjectives;
   }

   public boolean isCompleted() {
      return this.mCompleted;
   }

   public int getID() {
      return this.mID;
   }

   public int getType() {
      return this.mType;
   }

   public int getState() {
      return this.mState;
   }
}
