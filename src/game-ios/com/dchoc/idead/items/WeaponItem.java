package com.dchoc.idead.items;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class WeaponItem extends Item implements CsvIDs {
   public static final int SUBTYPE_MELEE = 79;
   public static final int SUBTYPE_DIG = 2107;
   public static final int SUBTYPE_PUNCH = 2109;
   public static final int ANIMATION_TYPE_MELEE = 79;
   public static final int ANIMATION_TYPE_DIG = 2107;
   public static final int ANIMATION_TYPE_PUNCH = 2109;
   public static final int ANIMATION_TYPE_CRAFT_SHOOT = 2111;
   public static final int ANIMATION_TYPE_SHOOT = 2102;
   public static final int ANIMATION_TYPE_SHOOT_BURST = 2104;
   public static final int ANIMATION_TYPE_THROW = 2113;
   public static final int DAMAGE_TYPE_ELECTRIC = 2108;
   public static final int DAMAGE_TYPE_FIRE = 2110;
   public static final int DAMAGE_TYPE_FREEZE = 2112;
   public static final int DAMAGE_TYPE_NORMAL = -1;
   public static final int SUBTYPE_COUNT = 8;
   public static final int SUBTYPE_CRAFTSHOOT = 2111;
   public static final int SUBTYPE_GUN = 2101;
   public static final int SUBTYPE_GUN_AUTOMATIC = 2103;
   public static final int SUBTYPE_INVALID = -1;
   public static final int SUBTYPE_MG = 2106;
   public static final int SUBTYPE_THROW = 2113;
   protected byte mDamage;
   protected int mSoundEffectID;
   protected byte mMaxAmmo;
   protected byte mAmmoUsage;
   protected byte mRequiredLevel;
   protected short mRequiredMission;
   protected short mDamageType = -1;
   protected boolean mCrafted = false;
   protected byte mXpMultiplier;
   protected byte mSoundFrame;
   protected short mAnimationType;
   protected int[][] mSpecialDamages;
   protected int[] mPaperdollAnimationID;
   protected SpriteObject[] mPaperdollAnimation;
   protected int mHitAnimationID;
   protected SpriteObject mHitAnimation;
   private boolean mNewItem;

   public WeaponItem(DChocByteArray data) throws EOFException {
      this.mType = 1907;
      this.loadData(data);
      if (this.mSoundEffectID != -1) {
         Toolkit.preloadSound(this.mSoundEffectID);
      }
   }

   public void loadPaperdollAnimation() {
      if (this.mPaperdollAnimation == null) {
         this.mPaperdollAnimation = AnimationsManager.load(this.mPaperdollAnimationID);
      }
   }

   public SpriteObject[] getPaperdollAnimation() {
      this.loadPaperdollAnimation();
      return this.mPaperdollAnimation;
   }

   public int getDamage() {
      return this.mDamage;
   }

   public boolean hasSpecialDamages() {
      return this.mSpecialDamages != null;
   }

   public int[][] getSpecialDamages() {
      return this.mSpecialDamages;
   }

   public int getSndEffectID() {
      return this.mSoundEffectID;
   }

   public int getMaxAmmo() {
      return this.mMaxAmmo;
   }

   public int getAmmoUsage() {
      return this.mAmmoUsage;
   }

   public int getRequiredLevel() {
      return this.mRequiredLevel;
   }

   public int getDamageType() {
      return this.mDamageType;
   }

   public boolean isCrafted() {
      return this.mCrafted;
   }

   public int getXpMultiplier() {
      return this.mXpMultiplier;
   }

   public boolean isNewItem() {
      return this.mNewItem;
   }

   public void setNewItem(boolean newItem) {
      this.mNewItem = newItem;
   }

   public int getRange() {
      return this.mSubType != 79 && this.mSubType != 2107 && this.mSubType != 2109 ? 3 : 1;
   }

   public int getAnimationType() {
      return this.mAnimationType;
   }

   public int getSndFrame() {
      return this.mSoundFrame;
   }

   public SpriteObject getHitAnimation() {
      return this.mHitAnimation;
   }

   public void playFrameSound(int frame) {
      if (this.getSndFrame() == frame) {
         iWrapper.playSoundFx(this.getSndEffectID());
      }
   }

   public void playWeaponSound() {
      iWrapper.playSoundFx(this.getSndEffectID());
   }

   @Override
   public boolean isLockedByLevel() {
      return PlayerProfile.getLevel() < this.mRequiredLevel;
   }

   @Override
   public boolean isLockedByMission() {
      Mission mission = MissionManager.getMission(this.mRequiredMission);
      return mission != null && !mission.isCompleted() ? !mission.isActive() : false;
   }

   @Override
   public SpriteObject[] loadAnimations() {
      if (this.mHitAnimation == null && this.mHitAnimationID != -1) {
         this.mHitAnimation = AnimationsManager.loadShared(this.mHitAnimationID, this.mGfxProfile);
         this.mHitAnimation.setAnimation(0, 1, true);
      }

      return super.loadAnimations();
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mTitle = data.readShort();
      this.mDescription = data.readShort();
      this.mDamage = data.readByte();
      this.mSpecialDamages = ToolkitHelpers.readIntValueList(data);
      this.mSoundFrame = data.readByte();
      this.mSoundEffectID = data.readInt();
      this.mSubType = data.readShort();
      this.mAnimationType = data.readShort();
      this.mCrafted = data.readBoolean();
      this.mMaxAmmo = data.readByte();
      this.mAmmoUsage = data.readByte();
      this.mRequiredLevel = data.readByte();
      this.mRequiredMission = data.readShort();
      this.mPrice = data.readInt();
      this.mCurrency = data.readShort();
      this.mSecondPrice = data.readInt();
      this.mSecondCurrency = data.readShort();
      data.readInt();
      data.readShort();
      data.readBoolean();
      this.mIconAnimationID = data.readInt();
      this.mAnimationIDs = ToolkitHelpers.readResourceIDs(data, true);
      this.mXpMultiplier = data.readByte();
      data.readByte();
      this.mPaperdollAnimationID = ToolkitHelpers.readResourceIDs(data, true);
      this.mDamageType = data.readShort();
      data.readBoolean();
      this.mHitAnimationID = data.readInt();
   }
}
