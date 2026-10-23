package com.dchoc.idead.items;

import com.dchoc.idead.missions.Achievement;
import com.dchoc.idead.missions.AchievementObjective;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class FarmingItem extends Item implements CsvIDs {
   public static final int SUBTYPE_AUTOMATIC = 1938;
   public static final int SUBTYPE_CROPS = 129;
   public static final int SUBTYPE_INVALID = -1;
   private boolean mNewItem;
   private int mGrowingTime;
   private int mRewardCash;
   private short mRewardItemType;
   private short mRewardItem;
   private short mNeighborRewardFood;
   private short mNeighborRewardXp;
   private short mNeighborRewardCoins;
   private int mWitherTime;
   private short mWitherRewardXp;
   private short mWitherRewardCoins;
   private int mRevivePriceCoins;
   private short mReviveCurrency;
   private int mMaxAge;
   private short mRequiredMission;
   private byte mEnergyDecrease;
   private int[] mActions;

   protected FarmingItem(DChocByteArray data) throws EOFException {
      this.mType = 1583;
      this.loadData(data);
   }

   public boolean isNewItem() {
      return this.mNewItem;
   }

   public void setNewItem(boolean newItem) {
      this.mNewItem = newItem;
   }

   public int getGrowingTime() {
      return this.mGrowingTime;
   }

   public boolean canGrow() {
      return this.mGrowingTime >= 0;
   }

   public int getRewardCash() {
      return this.mRewardCash;
   }

   public int getRewardItemType() {
      return this.mRewardItemType;
   }

   public int getRewardItem() {
      return this.mRewardItem;
   }

   public int getNeighborRewardFood() {
      return this.mNeighborRewardFood;
   }

   public int getNeighborRewardXp() {
      return this.mNeighborRewardXp;
   }

   public int getNeighborRewardCoins() {
      return this.mNeighborRewardCoins;
   }

   public int getWitherTime() {
      return this.mWitherTime;
   }

   public boolean canWither() {
      return this.mWitherTime >= 0;
   }

   public int getWitherRewardXp() {
      return this.mWitherRewardXp;
   }

   public int getWitherRewardCoins() {
      return this.mWitherRewardCoins;
   }

   public int getRevivePriceCoins() {
      return this.mRevivePriceCoins;
   }

   public int getReviveCurrency() {
      return this.mReviveCurrency;
   }

   public int getMaxAge() {
      return this.mMaxAge;
   }

   public boolean hasMaxAge() {
      return this.mMaxAge >= 0;
   }

   public int[] getActions() {
      return this.mActions;
   }

   public int getRequiredMission() {
      return this.mRequiredMission;
   }

   @Override
   public int getEnergyDecrease() {
      return this.mEnergyDecrease;
   }

   @Override
   public boolean isLockedByMission() {
      Mission mission = MissionManager.getMission(this.mRequiredMission);
      if (mission == null) {
         if (this.mID != 1953) {
            return false;
         }

         Achievement achievement = MissionManager.getAchievement(this.mRequiredMission);
         AchievementObjective objective = achievement.getCurrentObjective();
         return achievement.getLevel() != 66 ? true : !objective.isGoalReached();
      } else {
         return mission.isCompleted() ? false : !mission.isActive();
      }
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mTitle = data.readShort();
      this.mDescription = data.readShort();
      this.mLayer = data.readByte();
      this.mActions = ToolkitHelpers.readIntArray(data);
      this.mSubType = data.readShort();
      this.mRequiredMission = data.readShort();
      data.readByte();
      this.mPrice = data.readInt();
      this.mCurrency = data.readShort();
      this.mTileSizeX = data.readByte();
      this.mTileSizeY = data.readByte();
      this.mTileSizeZ = data.readByte();
      this.mWalkable = data.readBoolean();
      this.mGrowingTime = data.readInt() * 60000;
      this.mRewardFood = data.readShort();
      this.mRewardCash = data.readShort();
      this.mRewardEnergy = data.readShort();
      this.mRewardXp = data.readShort();
      this.mRewardCoins = data.readShort();
      this.mRewardItemType = data.readShort();
      this.mRewardItem = data.readShort();
      this.mNeighborRewardFood = data.readShort();
      this.mNeighborRewardXp = data.readShort();
      this.mNeighborRewardCoins = data.readShort();
      this.mWitherTime = data.readInt() * 60000;
      this.mWitherRewardXp = data.readShort();
      this.mWitherRewardCoins = data.readShort();
      this.mMaxAge = data.readInt() * 60000;
      data.readBoolean();
      this.mIconAnimationID = data.readInt();
      this.mAnimationIDs = ToolkitHelpers.readResourceIDs(data, false);
      this.mCollisionIDs = ToolkitHelpers.readResourceIDs(data, false);
      data.readBoolean();
      this.mRevivePriceCoins = data.readInt();
      this.mReviveCurrency = data.readShort();
      this.mShadowAnimation = data.readInt();
      this.mEnergyDecrease = data.readByte();
   }
}
