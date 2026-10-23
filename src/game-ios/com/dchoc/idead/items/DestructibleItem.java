package com.dchoc.idead.items;

import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class DestructibleItem extends Item implements CsvIDs {
   public static final int SUBTYPE_INVALID = -1;
   public static final int SUBTYPE_TRASH = 113;
   public static final int SUBTYPE_ZOMBIE_WEAK = 1981;
   public static final int SUBTYPE_ZOMBIE_NORMAL = 1983;
   public static final int SUBTYPE_ZOMBIE_HEAVY = 1985;
   public static final int SUBTYPE_ZOMBIE_BOSS = 1987;
   public static final int SUBTYPE_GRASS = 1988;
   public static final int SUBTYPE_RUBBLE = 1989;
   public static final int SUBTYPE_NPC = 1990;
   public static final int SUBTYPE_BUSH = 1991;
   public static final int SUBTYPES_COUNT = 12;
   public static final int SUBTYPE_FENCE = 1104;
   public static final int SUBTYPE_PAPER = 1993;
   public static final int SUBTYPE_SEGWAY = 1995;
   public static final int SUBTYPE_SHOVEL = 49;
   protected byte mMaxHealth;
   protected boolean mKilledOnlyCraftable;
   protected byte mEnergyDecrease;
   protected boolean mHideAtNeighbor;
   protected boolean mIndestructible;
   protected short mRewardExtraType;
   protected int mClearSound;
   protected int mAlertSound;
   protected short mRequiredMission;
   protected short mUnlockMission;
   protected int[][] mRewardExtraItems;
   protected int[] mDestroySounds;
   protected int[][] mItemsPerClick;

   protected DestructibleItem() {
      this.mType = 50;
      this.mRequiredMission = -1;
      this.mUnlockMission = -1;
      this.mSubType = -1;
      this.mMaxHealth = 0;
      this.mEnergyDecrease = 1;
      this.mKilledOnlyCraftable = false;
      this.mWalkable = true;
      this.mIndestructible = false;
      this.mItemsPerClick = null;
   }

   public DestructibleItem(DChocByteArray data) throws EOFException {
      this.mType = 50;
      this.mRequiredMission = -1;
      this.loadData(data);
   }

   public int getMaxHealth() {
      return this.mMaxHealth;
   }

   public int getRewardExtraType() {
      return this.mRewardExtraType;
   }

   public int[][] getRewardExtraItems() {
      return this.mRewardExtraItems;
   }

   public int getUnlockMission() {
      return this.mUnlockMission;
   }

   @Override
   public boolean isWalkable() {
      return this.mWalkable;
   }

   public boolean isKilledOnlyCraftable() {
      return this.mKilledOnlyCraftable;
   }

   public int getDefense() {
      return this.mID != 5564 && this.mID != 6052 && this.mID != 6220 ? 0 : 100;
   }

   public boolean isGarbage() {
      return this.mSubType == 113 || this.mSubType == 1988 || this.mSubType == 1989 || this.mSubType == 1991 || this.mSubType >= 4319 && this.mSubType <= 4322;
   }

   public boolean isZombie() {
      switch (this.mSubType) {
         case 1981:
         case 1983:
         case 1985:
         case 1987:
         case 4301:
         case 4314:
         case 4317:
         case 4752:
         case 4756:
         case 5565:
         case 5567:
         case 5569:
            return true;
         default:
            return false;
      }
   }

   public boolean isZombieBoss() {
      switch (this.mSubType) {
         case 1987:
         case 4314:
         case 4317:
         case 4756:
         case 5565:
         case 5569:
            return true;
         default:
            return false;
      }
   }

   public boolean isZombieMilitary() {
      return this.mID == 3225 || this.mID == 4304 || this.mID == 5566 || this.mID == 5568;
   }

   public boolean isFixable() {
      return this.mSubType >= 4323 && this.mSubType <= 4326 || this.mSubType == 4758 || this.mSubType == 5571 || this.mSubType == 5577;
   }

   public boolean isNPC() {
      return this.mSubType == 1990;
   }

   public boolean isTurrent() {
      return this.mID == 5599;
   }

   public boolean isHideAtNeighbor() {
      return this.mHideAtNeighbor;
   }

   public boolean isIndestructible() {
      return this.mIndestructible;
   }

   public int getClearSound() {
      return this.mClearSound;
   }

   public int getDestroySound() {
      int count = this.mDestroySounds == null ? 0 : this.mDestroySounds.length;
      return count < 1 ? -1 : this.mDestroySounds[ToolkitHelpers.getRandomInt(0, count - 1)];
   }

   public int getAlertSound() {
      return this.mAlertSound;
   }

   public int[][] getItemsPerClick() {
      return this.mItemsPerClick;
   }

   @Override
   public boolean isLockedByMission() {
      Mission mission = MissionManager.getMission(this.mRequiredMission);
      if (mission == null || mission.isCompleted()) {
         return false;
      } else {
         return this.mID != 5570 && this.mSubType == 5571 ? true : !mission.isActive();
      }
   }

   @Override
   public int getEnergyDecrease() {
      return this.mEnergyDecrease;
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mLayer = data.readByte();
      this.mTitle = data.readShort();
      this.mDescription = data.readShort();
      this.mSubType = data.readShort();
      this.mAction = data.readShort();
      if (!this.isZombie()) {
         this.mRequiredMission = data.readShort();
      }

      this.mItemsPerClick = ToolkitHelpers.readIntValueList(data);
      this.mKilledOnlyCraftable = data.readBoolean();
      this.mHideAtNeighbor = data.readBoolean();
      data.readBoolean();
      this.mRewardXp = data.readShort();
      this.mRewardCoins = data.readShort();
      this.mRewardStone = data.readShort();
      this.mRewardFood = data.readShort();
      this.mRewardEnergy = data.readShort();
      this.mUnlockMission = data.readShort();
      data.readInt();
      this.mMaxHealth = data.readByte();
      this.mTileSizeX = data.readByte();
      this.mTileSizeY = data.readByte();
      this.mTileSizeZ = data.readByte();
      this.mWalkable = data.readBoolean();
      this.mEnergyDecrease = data.readByte();
      this.mIndestructible = data.readBoolean();
      this.mAnimationIDs = ToolkitHelpers.readResourceIDs(data, true);
      this.mCollisionIDs = ToolkitHelpers.readResourceIDs(data, !this.isZombie());
      this.mRewardExtraType = data.readShort();
      this.mRewardExtraItems = ToolkitHelpers.readIntValueList(data);
      this.mShadowAnimation = data.readInt();
      this.mClearSound = data.readInt();
      this.mDestroySounds = ToolkitHelpers.readResourceIDs(data, false);
      if (this.isZombie()) {
         this.mAlertSound = data.readInt();
      }
   }
}
