package com.dchoc.idead.notifications;

public class Notification {
   private static final String VALUE_SEPARATOR = ",";
   private int mAmount;
   private long mActivationTime;
   private int mID;
   private int mRewardID;
   private int mRewardType;
   private String mText;

   public Notification(int id, String text, long activationTime, int rewardType, int rewardID, int amount) {
      this.mID = id;
      this.mText = text;
      this.mActivationTime = activationTime;
      this.mRewardType = rewardType;
      this.mRewardID = rewardID;
      this.mAmount = amount;
   }

   public long getActivationTime() {
      return this.mActivationTime;
   }

   public int getAmount() {
      return this.mAmount;
   }

   public int getID() {
      return this.mID;
   }

   public int getRewardID() {
      return this.mRewardID;
   }

   public int getRewardType() {
      return this.mRewardType;
   }

   public String getText() {
      return this.mText;
   }

   public void setActivationTime(long activationTime) {
      this.mActivationTime = activationTime;
   }

   public void setID(int id) {
      this.mID = id;
   }

   public void setText(String text) {
      this.mText = text;
   }
}
