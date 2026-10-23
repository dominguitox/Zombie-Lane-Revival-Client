package com.dchoc.idead.items;

import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.toolkit.DChocByteArray;
import java.io.EOFException;

public class GiftItem extends Item {
   private short mCreatesType;
   private short mCreatesID;
   private int mCreatesAmount;
   private byte mRequiredLevel;
   private short mRequiredMission;
   private byte mDailyLimit;
   private boolean mVisibleOnGiftPage;
   private boolean mGiftable;
   private boolean mGiftBack;

   protected GiftItem(DChocByteArray data) throws EOFException {
      this.mType = 1909;
      this.loadData(data);
   }

   public int getCreatesType() {
      return this.mCreatesType;
   }

   public int getCreatesID() {
      return this.mCreatesID;
   }

   public int getCreatesAmount() {
      return this.mCreatesAmount;
   }

   public int getRequiredLevel() {
      return this.mRequiredLevel;
   }

   public int getRequiredMission() {
      return this.mRequiredMission;
   }

   public int getDailyLimit() {
      return this.mDailyLimit;
   }

   public boolean isVisibleOnGiftPage() {
      return this.mVisibleOnGiftPage;
   }

   public boolean isGiftable() {
      return this.mGiftable;
   }

   public boolean isGiftBack() {
      return this.mGiftBack;
   }

   @Override
   public boolean isLockedByMission() {
      Mission mission = MissionManager.getMission(this.mRequiredMission);
      return mission != null && !mission.isCompleted() ? !mission.isActive() : false;
   }

   @Override
   public boolean isLockedByLevel() {
      return PlayerProfile.getLevel() < this.mRequiredLevel;
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mTitle = data.readShort();
      this.mDescription = data.readShort();
      this.mCreatesType = data.readShort();
      this.mCreatesID = data.readShort();
      this.mCreatesAmount = data.readInt();
      this.mRequiredLevel = data.readByte();
      this.mRequiredMission = data.readShort();
      data.readShort();
      this.mDailyLimit = data.readByte();
      this.mVisibleOnGiftPage = data.readBoolean();
      this.mGiftable = data.readBoolean();
      this.mGiftBack = data.readBoolean();
      this.mIconAnimationID = data.readInt();
   }
}
