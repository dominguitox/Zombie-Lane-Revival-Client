package com.dchoc.idead.tuner;

import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import java.io.EOFException;

public class EdgeAreaDefinition implements CsvIDs {
   private short mAreaName;
   private short mSide;
   private short mLockedEvent;
   private short mUnlockedEvent;
   private byte mVisibleLevel;
   private byte mUnlockLevel;
   private short mUnlockMission;
   private int mAnimationLocked = -1;
   private int mAnimationUnlocked = -1;
   private int mAnimationLockedEN = -1;
   private int mAnimationUnlockedEN = -1;
   private int mCollision = -1;

   public EdgeAreaDefinition(DChocByteArray data) throws EOFException {
      this.loadData(data);
   }

   public int getAreaName() {
      return this.mAreaName;
   }

   public int getSide() {
      return this.mSide;
   }

   public int getLockedEvent() {
      return this.mLockedEvent;
   }

   public int getUnlockedEvent() {
      return this.mUnlockedEvent;
   }

   public int getUnlockLevel() {
      return this.mUnlockLevel;
   }

   public int getAnimationLocked() {
      return this.mAnimationLocked;
   }

   public int getAnimationUnlocked() {
      return this.mAnimationUnlocked;
   }

   public int getAnimationLockedEN() {
      return this.mAnimationLockedEN;
   }

   public int getAnimationUnlockedEN() {
      return this.mAnimationUnlockedEN;
   }

   public int getCollision() {
      return this.mCollision;
   }

   public boolean isVisible() {
      return this.mVisibleLevel <= PlayerProfile.getLevel();
   }

   public boolean isLockedByMission() {
      Mission mission = this.getUnlockMission();
      return mission != null && !mission.isCompleted();
   }

   public boolean isLockedByLevel() {
      return PlayerProfile.getLevel() < this.mUnlockLevel;
   }

   public Mission getUnlockMission() {
      return MissionManager.getMission(this.mUnlockMission);
   }

   public void loadData(DChocByteArray data) throws EOFException {
      this.mAreaName = data.readShort();
      this.mSide = data.readShort();
      switch (this.mSide) {
         case 3881:
            this.mSide = 3881;
            break;
         case 3884:
            this.mSide = 3884;
            break;
         case 3888:
            this.mSide = 3888;
            break;
         case 3892:
            this.mSide = 3892;
      }

      this.mLockedEvent = data.readShort();
      this.mUnlockedEvent = data.readShort();
      this.mVisibleLevel = data.readByte();
      this.mUnlockLevel = data.readByte();
      this.mUnlockMission = data.readShort();
      this.mAnimationLocked = data.readInt();
      this.mAnimationUnlocked = data.readInt();
      this.mAnimationLockedEN = data.readInt();
      this.mAnimationUnlockedEN = data.readInt();
      this.mCollision = data.readInt();
   }
}
