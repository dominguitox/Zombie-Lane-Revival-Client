package com.dchoc.idead.notifications;

import com.dchoc.toolkit.DChocByteArray;
import java.io.EOFException;

public class NotificationDefinition {
   private int mAmount;
   private short mDescription;
   private long mDelay;
   private short mID;
   private long mOffset;
   private short mRewardID;
   private short mRewardType;

   public NotificationDefinition(DChocByteArray dChocByteArray) throws EOFException {
      this.mID = dChocByteArray.readShort();
      this.mDescription = dChocByteArray.readShort();
      this.mDelay = dChocByteArray.readInt() * 60000L;
      this.mOffset = dChocByteArray.readInt() * 60000L;
      this.mRewardType = dChocByteArray.readShort();
      this.mRewardID = dChocByteArray.readShort();
      this.mAmount = dChocByteArray.readInt();
   }

   public int getAmount() {
      return this.mAmount;
   }

   public long getDelay() {
      return this.mDelay;
   }

   public int getDescription() {
      return this.mDescription;
   }

   public int getID() {
      return this.mID;
   }

   public long getOffset() {
      return this.mOffset;
   }

   public int getRewardID() {
      return this.mRewardID;
   }

   public int getRewardType() {
      return this.mRewardType;
   }

   public void loadData(DChocByteArray dChocByteArray) throws EOFException {
      this.mID = dChocByteArray.readShort();
      this.mDescription = dChocByteArray.readShort();
      this.mDelay = dChocByteArray.readInt() * 60000L;
      this.mOffset = dChocByteArray.readInt() * 60000L;
      this.mRewardType = dChocByteArray.readShort();
      this.mRewardID = dChocByteArray.readShort();
      this.mAmount = dChocByteArray.readInt();
   }
}
