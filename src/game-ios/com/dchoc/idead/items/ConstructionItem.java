package com.dchoc.idead.items;

import com.dchoc.idead.Profiler;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class ConstructionItem extends Item {
   private byte mRequiredClicks;
   private short mStonePerClick;
   private short mCoinsPerClick;
   private short mRequiredMission;
   private short mRequiredMission2;
   private short mClickRewardXp;
   protected short[] mAnimationsReferences;
   private short mFinish;
   private short mShowAfterMission;
   private int[][] mCollectibleItems;
   private int[] mCraftingItems;
   private int[][] mUpgradeItems;
   private int[] mActions;
   private int[] mShadowAnimationIDs;

   protected ConstructionItem(DChocByteArray data) throws EOFException {
      this.mType = 147;
      this.mFinish = -1;
      this.loadData(data);
      if (this.mAnimationIDs != null) {
         this.mAnimations = new SpriteObject[this.mAnimationIDs.length];
         this.mAnimationsReferences = new short[this.mAnimationIDs.length];
      }
   }

   public int getRequiredMission() {
      return this.mRequiredMission;
   }

   public int[][] getCollectibleItems() {
      return this.mCollectibleItems;
   }

   public boolean hasCollectibles() {
      return this.mCollectibleItems != null && this.mCollectibleItems.length > 0;
   }

   public int[] getCraftingItems() {
      return this.mCraftingItems;
   }

   public boolean hasCraftables() {
      return this.mCraftingItems != null && this.mCraftingItems.length > 0;
   }

   public int[][] getUpgradeItems() {
      return this.mUpgradeItems;
   }

   public boolean isUpgradeable() {
      return this.mUpgradeItems != null && this.mUpgradeItems.length > 0;
   }

   public int getRequiredClicks() {
      return this.mRequiredClicks;
   }

   public int getStonePerClick() {
      return this.mStonePerClick;
   }

   public int getCoinsPerClick() {
      return this.mCoinsPerClick;
   }

   public int getClickRewardXp() {
      return this.mClickRewardXp;
   }

   public int[] getActions() {
      return this.mActions;
   }

   public int getFinish() {
      return this.mFinish;
   }

   public int getShowAfterMission() {
      return this.mShowAfterMission;
   }

   public int[] getShadowAnimationIDs() {
      return this.mShadowAnimationIDs;
   }

   public SpriteObject[] loadAnimation(int index) {
      if (this.mAnimationIDs == null) {
         return null;
      }

      this.loadCollisions();
      SpriteObject[] animations = this.mAnimations;
      if (animations[index] == null) {
         Profiler.startTimer(25);
         this.mAnimationReferences = 0;
         this.mAnimations[index] = AnimationsManager.loadShared(this.mAnimationIDs[index], this.mGfxProfile);
         this.calculateBounds(true);
         Profiler.stopTimer(25);
         animations = this.mAnimations;
      }

      this.mAnimationsReferences[index]++;
      return animations;
   }

   public void unloadAnimation(int index) {
      if (this.mAnimationIDs != null && index >= 0) {
         this.mAnimationsReferences[index]--;
         if (this.mAnimationsReferences[index] < 1) {
            Profiler.startTimer(26);
            AnimationsManager.unloadShared(this.mAnimationIDs[index], this.mAnimations[index]);
            this.mAnimations[index] = null;
            this.mAnimationsReferences[index] = 0;
            Profiler.stopTimer(26);
         }
      }
   }

   @Override
   public SpriteObject[] loadAnimations() {
      return this.mAnimations;
   }

   @Override
   public void unloadAnimations() {
   }

   @Override
   public boolean isLockedByMission() {
      Mission mission = MissionManager.getMission(this.mRequiredMission);
      return mission != null && !mission.isCompleted() ? !mission.isActive() : false;
   }

   public boolean isLockedByMission2() {
      Mission mission = MissionManager.getMission(this.mRequiredMission2);
      return mission != null && !mission.isCompleted() ? !mission.isActive() : false;
   }

   private void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mLayer = data.readByte();
      this.mTitle = data.readShort();
      this.mDescription = data.readShort();
      this.mActions = ToolkitHelpers.readIntArray(data);
      this.mFinish = data.readShort();
      this.mShowAfterMission = data.readShort();
      data.readByte();
      this.mRequiredMission = data.readShort();
      this.mRequiredMission2 = data.readShort();
      this.mPrice = data.readInt();
      this.mCurrency = data.readShort();
      this.mRequiredClicks = data.readByte();
      this.mStonePerClick = data.readShort();
      this.mCoinsPerClick = data.readShort();
      this.mClickRewardXp = data.readShort();
      data.readShort();
      this.mTileSizeX = data.readByte();
      this.mTileSizeY = data.readByte();
      this.mTileSizeZ = data.readByte();
      this.mWalkable = data.readBoolean();
      this.mCollectibleItems = ToolkitHelpers.readIntValueList(data);
      this.mCraftingItems = ToolkitHelpers.readIntArray(data);
      this.mIconAnimationID = data.readInt();
      this.mAnimationIDs = ToolkitHelpers.readResourceIDs(data, true);
      this.mCollisionIDs = ToolkitHelpers.readResourceIDs(data, true);
      data.readBoolean();
      this.mUpgradeItems = ToolkitHelpers.readIntValueList(data);
      this.mShadowAnimationIDs = ToolkitHelpers.readIntArray(data);
   }
}
