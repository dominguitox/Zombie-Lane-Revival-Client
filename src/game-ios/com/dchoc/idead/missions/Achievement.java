package com.dchoc.idead.missions;

import com.dchoc.idead.GameEngine;
import com.dchoc.idead.events.EventManager;
import com.dchoc.idead.events.TimedEvent;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.social.SocialGameCenter;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.parser.Json;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import java.io.EOFException;
import java.util.Vector;

public class Achievement implements CsvIDs {
   public static final int INITIAL_TARGET_CAPACITY = 4;
   public static final int LEVEL_BRONZE = 63;
   public static final int LEVEL_GOLD = 65;
   public static final int LEVEL_PLATINIUM = 66;
   public static final int LEVEL_SILVER = 64;
   private int mCompletedDescriptionTextID;
   private int mDescriptionTextID;
   private int mID;
   private int mIconAnimationID;
   private Vector mObjectives = new Vector(4);
   private int mTitleTextID;

   public Achievement(DChocByteArray data) throws EOFException {
      this.loadData(data);
   }

   public Achievement() {
   }

   /** Build an achievement from one element of the JSON {@code achievements} array. */
   public static Achievement fromJson(java.util.Map<String, Object> data) {
      Achievement achievement = new Achievement();
      achievement.mID = Json.intValue(data, "id", -1);
      achievement.mTitleTextID = MissionJsonText.resolve(Json.object(data, "title"));
      achievement.mDescriptionTextID = MissionJsonText.resolve(Json.object(data, "description"));
      achievement.mCompletedDescriptionTextID = MissionJsonText.resolve(Json.object(data, "completedDescription"));
      achievement.mIconAnimationID = Json.intValue(data, "iconAnimationId", -1);
      return achievement;
   }

   public void updateCounters(int type, int subType, int value, int amount) {
      if (PlayerProfile.getLevel() >= Tuner.getLevelToUnlockAchievements()) {
         for (int i = 0; i < this.mObjectives.size(); i++) {
            AchievementObjective objective = (AchievementObjective)this.mObjectives.elementAt(i);
            if (!objective.isGoalReached() && objective.updateCounter(type, subType, value, amount)) {
               objective.addRewards();
               TimedEvent event = new TimedEvent(100, 2468, null);
               event.setAchievement(this);
               EventManager.addEvent(event);
               MissionManager.updateCounter(2541, objective.getID(), 1);
               MissionManager.updateCounter(2541, 80, 1);
            }

            if (objective.isValidUpdate(type, subType, value)) {
               this.reportObjectiveProgress(objective);
            }
         }
      }
   }

   public int getID() {
      return this.mID;
   }

   public int getTitleTextID() {
      return this.mTitleTextID;
   }

   public int getDescriptionTextID() {
      return this.mDescriptionTextID;
   }

   public int getCompletedDescriptionTextID() {
      return this.mCompletedDescriptionTextID;
   }

   public int getLevel() {
      if (((AchievementObjective)this.mObjectives.elementAt(2)).isGoalReached()) {
         return 66;
      } else if (((AchievementObjective)this.mObjectives.elementAt(1)).isGoalReached()) {
         return 65;
      } else {
         return ((AchievementObjective)this.mObjectives.elementAt(0)).isGoalReached() ? 64 : 63;
      }
   }

   public int getIconAnimationID() {
      return this.mIconAnimationID;
   }

   public void addObjective(AchievementObjective objective) {
      this.mObjectives.addElement(objective);
   }

   public Vector getObjectives() {
      return this.mObjectives;
   }

   public AchievementObjective getObjective(int id) {
      for (int i = 0; i < this.mObjectives.size(); i++) {
         AchievementObjective objective = (AchievementObjective)this.mObjectives.elementAt(i);
         if (objective.getID() == id) {
            return objective;
         }
      }

      return null;
   }

   public AchievementObjective getCurrentObjective() {
      switch (this.getLevel()) {
         case 63:
            return (AchievementObjective)this.mObjectives.elementAt(0);
         case 64:
            return (AchievementObjective)this.mObjectives.elementAt(1);
         case 65:
            return (AchievementObjective)this.mObjectives.elementAt(2);
         case 66:
            return (AchievementObjective)this.mObjectives.elementAt(3);
         default:
            return null;
      }
   }

   public void save(DChocByteArray data) {
      data.writeByte(0);

      for (int i = 0; i < this.mObjectives.size(); i++) {
         AchievementObjective objective = (AchievementObjective)this.mObjectives.elementAt(i);
         data.writeShort(objective.getID());
         objective.save(data);
      }
   }

   public void load(DChocByteArray data) throws EOFException {
      data.readByte();

      for (int i = 0; i < this.mObjectives.size(); i++) {
         this.getObjective(data.readShort()).load(data);
      }
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mTitleTextID = data.readShort();
      this.mDescriptionTextID = data.readShort();
      this.mCompletedDescriptionTextID = data.readShort();
      this.mIconAnimationID = data.readInt();
   }

   public void reportObjectiveProgress(AchievementObjective objective) {
      if (objective != null) {
         SocialGameCenter gameCenter = GameEngine.getInstance().getGameCenter();
         if (objective.isGoalReached()) {
            gameCenter.reportAchievementCompleted(objective.getID());
         } else {
            gameCenter.reportAchievemenProgress(objective.getID(), objective.getPercentage());
         }
      }
   }
}
