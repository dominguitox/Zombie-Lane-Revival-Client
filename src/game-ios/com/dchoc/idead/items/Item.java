package com.dchoc.idead.items;

import com.dchoc.idead.Profiler;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.math.Rect2;
import com.dchoc.spriteobject.Animation;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.CsvIDs;

public class Item implements CsvIDs {
   public static final int ID_INVALID = -1;
   public static final int TYPE_INVALID = -1;
   public static final int TYPE_COLLECTIBLE = 47;
   public static final int TYPE_DESTRUCTIBLE = 50;
   public static final int TYPE_WEAPON = 1907;
   public static final int TYPE_AVATAR = 1911;
   public static final int TYPE_DECORATION = 405;
   public static final int TYPE_EDIBLE = 101;
   public static final int TYPE_CRAFTING = 1913;
   public static final int TYPE_BOOSTER = 3270;
   public static final int TYPE_FARMING = 1583;
   public static final int TYPE_CONSTRUCTION = 147;
   public static final int TYPE_PLAYER = 837;
   public static final int TYPE_FENCE = 1104;
   public static final int TYPE_GIFT = 1909;
   public static final int TYPE_PAYMENT = 3003;
   public static final int TYPES_COUNT = 13;
   public static final int[] TYPES = new int[]{47, 50, 1907, 1911, 405, 101, 1913, 3270, 1583, 147, 837, 1104, 1909};
   static Rect2 smNoBounds = new Rect2();
   Rect2 mBounds;
   protected short mType = -1;
   protected short mID = -1;
   protected short mTitle = -1;
   protected short mDescription = -1;
   protected short mAction = -1;
   public byte mLayer = 0;
   protected short mAnimationReferences = 0;
   protected int mIconAnimationID = -1;
   protected int mGfxProfile;
   protected byte mTileSizeX = 1;
   protected byte mTileSizeY = 1;
   protected byte mTileSizeZ;
   protected int mPrice = 0;
   protected short mCurrency;
   protected int mSecondPrice = 0;
   protected short mSecondCurrency;
   protected boolean mWalkable = false;
   protected short mRewardCoins = 0;
   protected short mRewardStone = 0;
   protected short mRewardFood = 0;
   protected short mRewardXp = 0;
   protected short mRewardEnergy = 0;
   protected int mShadowAnimation = -1;
   protected short mSubType = -1;
   protected int[] mAnimationIDs;
   protected SpriteObject[] mAnimations;
   protected int[] mCollisionIDs;
   protected SpriteObject[] mCollisions;

   protected Item() {
      this.mGfxProfile = DeviceWrapper.useHDIngameGraphics() ? 2 : 1;
   }

   public int getType() {
      return this.mType;
   }

   public int getSubType() {
      return this.mSubType;
   }

   public int getID() {
      return this.mID;
   }

   public int getLayer() {
      return this.mLayer;
   }

   public int getTileSizeX() {
      return this.mTileSizeX;
   }

   public int getTileSizeY() {
      return this.mTileSizeY;
   }

   public int getTileSizeZ() {
      return this.mTileSizeZ;
   }

   public int getPrice() {
      return this.mPrice;
   }

   public int getCurrency() {
      return this.mCurrency;
   }

   public boolean hasValidPrice() {
      return this.mPrice < 1 ? false : this.mCurrency != -1;
   }

   public int getSecondPrice() {
      return this.mSecondPrice;
   }

   public int getSecondCurrency() {
      return this.mSecondCurrency;
   }

   public int getRewardCoins() {
      return this.mRewardCoins;
   }

   public int getRewardStone() {
      return this.mRewardStone;
   }

   public int getRewardFood() {
      return this.mRewardFood;
   }

   public int getRewardXp() {
      return this.mRewardXp;
   }

   public int getRewardEnergy() {
      return this.mRewardEnergy;
   }

   public int getShadowAnimationID() {
      return this.mShadowAnimation;
   }

   public int getTitle() {
      return this.mTitle;
   }

   public int getDescription() {
      return this.mDescription;
   }

   public int getAction() {
      return this.mAction;
   }

   public boolean isWalkable() {
      return this.mWalkable;
   }

   public boolean hasAnimations() {
      return this.mAnimationIDs != null;
   }

   public SpriteObject[] loadAnimations() {
      this.loadCollisions();
      SpriteObject[] animations = this.mAnimations;
      if (animations == null && this.mAnimationIDs != null) {
         Profiler.startTimer(25);
         this.mAnimations = AnimationsManager.loadShared(this.mAnimationIDs, this.mGfxProfile);
         this.calculateBounds(false);
         Profiler.stopTimer(25);
         animations = this.mAnimations;
      }

      this.mAnimationReferences++;
      return animations;
   }

   public SpriteObject[] getAnimations() {
      return this.mAnimations;
   }

   public void unloadAnimations() {
      this.mAnimationReferences--;
      if (this.mAnimationReferences < 1) {
         Profiler.startTimer(26);
         AnimationsManager.unloadShared(this.mAnimationIDs, this.mAnimations);
         this.mAnimations = null;
         this.mAnimationReferences = 0;
         Profiler.stopTimer(26);
      }
   }

   public Rect2 getBounds() {
      return this.mBounds != null ? this.mBounds : smNoBounds;
   }

   public SpriteObject[] getCollisions() {
      return this.mCollisions;
   }

   public int getIconAnimationID() {
      return this.mIconAnimationID;
   }

   public boolean isLockedByLevel() {
      return false;
   }

   public boolean isLockedByMission() {
      return false;
   }

   public int getEnergyDecrease() {
      return 1;
   }

   protected void loadCollisions() {
      if (this.mCollisions == null) {
         if (this.mCollisionIDs != null) {
            Profiler.startTimer(27);
            this.mCollisions = AnimationsManager.loadShared(this.mCollisionIDs, this.mGfxProfile);
            Profiler.stopTimer(27);
         }
      }
   }

   protected void calculateBounds(boolean force) {
      if (force || this.mAnimations != null && this.mBounds == null) {
         CollisionBox box = null;
         if (this.mCollisions != null && this.mCollisions[0] != null) {
            box = this.mCollisions[0].getCollisionBox(2);
         }

         int left = 0;
         int top = 0;
         int right = 0;
         int bottom = 0;
         if (box != null) {
            left = box.getX();
            top = box.getY();
            int x = box.getX();
            right = box.getWidth() + x;
            int y = box.getY();
            bottom = box.getHeight() + y;
         } else {
            int count = this.mAnimations == null ? 0 : this.mAnimations.length;

            for (int i = 0; i < count; i++) {
               SpriteObject animation = this.mAnimations[i];
               if (animation != null) {
                  Animation data = animation.getCurrentAnimationData();
                  if (data != null) {
                     if (-data.getX() < left || i == 0) {
                        left = -data.getX();
                     }

                     if (-data.getY() < top || i == 0) {
                        top = -data.getY();
                     }

                     if (data.getWidth() - data.getX() > right || i == 0) {
                        right = data.getWidth() - data.getX();
                     }

                     if (data.getHeight() - data.getY() > bottom || i == 0) {
                        bottom = data.getHeight() - data.getY();
                     }
                  }
               }
            }
         }

         if (this.mBounds == null) {
            this.mBounds = new Rect2();
         }

         this.mBounds.set(left, top, right - left, bottom - top);
      }
   }
}
