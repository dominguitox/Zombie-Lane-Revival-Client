package com.dchoc.idead.items;

import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class AvatarItem extends Item implements CsvIDs {
   public static final int SUBTYPE_JACKET = 2115;
   public static final int SUBTYPE_COSTUME = 2116;
   public static final int SUBTYPE_HAIR = 2117;
   public static final int SUBTYPE_FACE = 2118;
   public static final int SUBTYPE_HAT = 2119;
   public static final int SUBTYPE_SHOES = 2120;
   public static final int SUBTYPE_SHIRT = 2121;
   public static final int SUBTYPE_PANTS = 2122;
   public static final int SUBTYPE_SPECIALS = 2124;
   public static final int SUBTYPE_COLLAR = 2125;
   public static final int SUBTYPE_BREED = 2127;
   public static final int SUBTYPE_HEAD = 2128;
   public static final int DRAW_LEGS_BEFORE = 2122;
   public static final int DRAW_TORSO_BEFORE = 2121;
   public static final int GENDER_DOG = 43;
   public static final int GENDER_FEMALE = 1922;
   public static final int GENDER_INVALID = -1;
   public static final int GENDER_MALE = 1921;
   public static final int SUBTYPES_COUNT = 13;
   public static final int SUBTYPE_INVALID = -1;
   public static final int SUBTYPE_SKINTONE = 2123;
   public static final int[] DRAWING_ORDER = new int[]{2128, 2127, 2125, 2122, 2120, 2121, 2115, 2118, 2117, 2119, 2116, 2124};
   protected short mGender;
   protected byte mRequiredLevel;
   protected short mRequiredMission;
   protected boolean mAvailableForPurchase = true;
   protected int mColorModification = -8355712;
   protected int mColorModification2 = -8355712;
   private short mGroup = -1;
   private short mGenderLink = -1;

   protected AvatarItem(DChocByteArray data) throws EOFException {
      this.mType = 1911;
      this.loadData(data);
   }

   public int getGender() {
      return this.mGender;
   }

   public int getRequiredLevel() {
      return this.mRequiredLevel;
   }

   public boolean isAvailableForPurchase() {
      return this.mAvailableForPurchase;
   }

   public void setAvailableForPurchase(boolean available) {
      this.mAvailableForPurchase = available;
   }

   public int getColorModification() {
      return this.mColorModification;
   }

   public int getColorModification2() {
      return this.mColorModification2;
   }

   public int getGroup() {
      return this.mGroup;
   }

   public int getGenderLink() {
      return this.mGenderLink;
   }

   @Override
   public boolean isLockedByLevel() {
      return PlayerProfile.getLevel() < this.mRequiredLevel;
   }

   @Override
   public boolean isLockedByMission() {
      Mission mission = MissionManager.getMission(this.mRequiredMission);
      if (mission == null || mission.isCompleted()) {
         return false;
      } else {
         return mission.isInactive() ? mission.getRequiredLevel() != 102 : true;
      }
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mTitle = data.readShort();
      this.mDescription = data.readShort();
      this.mSubType = data.readShort();
      this.mGender = data.readShort();
      this.mRequiredLevel = data.readByte();
      this.mRequiredMission = data.readShort();
      this.mPrice = data.readInt();
      this.mCurrency = data.readShort();
      this.mAvailableForPurchase = data.readBoolean();
      data.readBoolean();
      this.mIconAnimationID = data.readInt();
      this.mAnimationIDs = ToolkitHelpers.readResourceIDs(data, true);
      this.mColorModification = data.readInt();
      this.mColorModification2 = data.readInt();
      this.mGroup = data.readShort();
      this.mGenderLink = data.readShort();
   }
}
