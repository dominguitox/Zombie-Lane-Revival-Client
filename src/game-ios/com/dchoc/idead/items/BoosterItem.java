package com.dchoc.idead.items;

import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import java.io.EOFException;

public class BoosterItem extends Item implements CsvIDs {
   private byte mRequiredLevel;
   private int mDuration;
   private short mEffect;
   private int mPercentage;
   private short mRequiredMission;

   public BoosterItem(DChocByteArray data) throws EOFException {
      this.mType = 3270;
      this.loadData(data);
   }

   public int getDuration() {
      return this.mDuration;
   }

   public int getEffect() {
      return this.mEffect;
   }

   public int getPercentage() {
      return this.mPercentage;
   }

   public int getRequiredLevel() {
      return this.mRequiredLevel;
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

   public void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mTitle = data.readShort();
      this.mDescription = data.readShort();
      this.mRequiredMission = data.readShort();
      this.mRequiredLevel = data.readByte();
      this.mPrice = data.readInt();
      this.mCurrency = data.readShort();
      this.mDuration = data.readInt() * 60000;
      this.mEffect = data.readShort();
      this.mPercentage = data.readShort();
      this.mIconAnimationID = data.readInt();
   }
}
