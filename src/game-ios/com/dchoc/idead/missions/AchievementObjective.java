package com.dchoc.idead.missions;

import com.dchoc.hud.WindowManager;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.parser.Json;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import com.dchoc.windows.WindowCustomization;
import java.io.EOFException;

public class AchievementObjective extends Counter {
   private int mAchievementID;
   private int mID;
   private int mLevel;
   private int[][] mRewards;

   public AchievementObjective(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mAchievementID = data.readShort();
      this.mLevel = data.readShort();
      this.mType = data.readShort();
      this.mSubType = data.readShort();
      this.mValue = data.readShort();
      this.mGoal = data.readInt();
      this.mRewards = ToolkitHelpers.readIntValueList(data);
   }

   public AchievementObjective() {
   }

   /** Build a tier objective from one element of an achievement's JSON {@code objectives}. */
   public static AchievementObjective fromJson(java.util.Map<String, Object> data) {
      AchievementObjective objective = new AchievementObjective();
      objective.mID = Json.intValue(data, "id", -1);
      objective.mAchievementID = Json.intValue(data, "achievementId", -1);
      objective.mLevel = Json.intValue(data, "tier", -1);
      java.util.Map<String, Object> condition = Json.object(data, "condition");
      objective.mType = Json.intValue(condition, "counterTypeId", -1);
      objective.mSubType = Json.intValue(condition, "subtypeId", -1);
      objective.mValue = Json.intValue(condition, "valueId", -1);
      objective.mGoal = Json.intValue(condition, "target", 0);
      objective.mRewards = readRewardPairs(Json.array(data, "rewards"));
      return objective;
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

   public int getID() {
      return this.mID;
   }

   public int getAchievementID() {
      return this.mAchievementID;
   }

   public int getLevel() {
      return this.mLevel;
   }

   public int[][] getRewards() {
      return this.mRewards;
   }

   public void addRewards() {
      if (this.mRewards != null) {
         boolean avatarReceived = false;

         for (int i = 0; i < this.mRewards.length; i++) {
            int id = this.mRewards[i][0];
            int amount = this.mRewards[i][1];
            if (id == 2095) {
               PlayerProfile.addXp(amount);
            } else if (id == 9) {
               PlayerProfile.addCoins(amount);
            } else {
               Item item = ItemManager.getItem(id);
               PlayerProfile.getInventory().addItem(item, amount);
               if (item != null) {
                  avatarReceived |= item.getType() == 1911;
               }
            }
         }

         if (avatarReceived) {
            ((WindowCustomization)WindowManager.getWindow((byte)25)).setInitTouchAreas();
         }
      }
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mAchievementID = data.readShort();
      this.mLevel = data.readShort();
      this.mType = data.readShort();
      this.mSubType = data.readShort();
      this.mValue = data.readShort();
      this.mGoal = data.readInt();
      this.mRewards = ToolkitHelpers.readIntValueList(data);
   }
}
