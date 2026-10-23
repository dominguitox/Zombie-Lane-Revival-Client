package com.dchoc.idead.tuner;

import com.dchoc.toolkit.DChocByteArray;
import java.io.EOFException;

public class ComboDefinition {
   private byte mLevel;
   private byte mRequiredItems;
   private byte mRewards;
   private short mRewardType;

   public ComboDefinition(DChocByteArray dChocByteArray) throws EOFException {
      this.mLevel = dChocByteArray.readByte();
      this.mRequiredItems = dChocByteArray.readByte();
      this.mRewards = dChocByteArray.readByte();
      this.mRewardType = dChocByteArray.readShort();
   }

   public int getLevel() {
      return this.mLevel;
   }

   public int getRequiredItems() {
      return this.mRequiredItems;
   }

   public int getRewardType() {
      return this.mRewardType;
   }

   public int getRewards() {
      return this.mRewards;
   }

   protected void loadData(DChocByteArray dChocByteArray) throws EOFException {
      this.mLevel = dChocByteArray.readByte();
      this.mRequiredItems = dChocByteArray.readByte();
      this.mRewards = dChocByteArray.readByte();
      this.mRewardType = dChocByteArray.readShort();
   }
}
