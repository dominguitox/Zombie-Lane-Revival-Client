package com.dchoc.idead.neighbors;

import com.dchoc.idead.items.GiftItem;
import com.dchoc.idead.servlets.AcceptGift;
import com.dchoc.idead.servlets.SendGift;
import com.dchoc.idead.tracking.GiftSendEvent;

public class NeighborGift {
   public static final long ID_INVALID = -1L;
   public static final int STATUS_NONE = 0;
   public static final int STATUS_ACCEPTED = 1;
   public static final int STATUS_RECEIVED = 2;
   public static final int STATUS_CANCELLED = 3;
   public static final int STATUS_SENT = 4;
   public static final int STATUS_INVALID = -1;
   public static final String[] STATUS_NAMES = new String[]{"none", "accepted", "received", "cancelled", "sent"};
   private GiftItem mGift;
   private long mID;
   private int mAmount;
   private NeighborProfile mNeighbor;
   private int mStatus;
   private AcceptGift mAcceptGift;
   private SendGift mSendGift;
   private long mCountdown;
   private boolean mReward;
   private GiftSendEvent mTracking;

   public NeighborGift(NeighborProfile neighbor, GiftItem gift, long id, int amount) {
      this.mNeighbor = neighbor;
      this.mGift = gift;
      this.mID = id;
      this.mAmount = amount;
      this.mAcceptGift = new AcceptGift();
      this.mSendGift = new SendGift();
      this.mStatus = 0;
      this.mCountdown = 0L;
      this.mReward = false;
      int giftId = gift.getID();
      String userId = neighbor.getUserID();
      this.mTracking = new GiftSendEvent(giftId, userId);
   }

   public long getID() {
      return this.mID;
   }

   public int getAmount() {
      return this.mAmount;
   }

   public void accept(boolean accept) {
      if (this.mStatus != 1) {
         this.mAcceptGift.init(this, accept);
         this.mAcceptGift.execute();
         this.mTracking.trackAccept(accept);
      }
   }

   public boolean isAccepting() {
      return this.mAcceptGift.isWaitingForResponse();
   }

   public boolean isAccepted() {
      return this.mStatus == 1;
   }

   public void send() {
      if (this.canSend()) {
         this.mSendGift.init(this);
         this.mSendGift.execute();
         this.mTracking.trackSend();
      }
   }

   public boolean isSending() {
      return this.mSendGift.isWaitingForResponse();
   }

   public boolean isSent() {
      return this.mStatus == 4;
   }

   public boolean canSend() {
      return !this.mSendGift.isWaitingForResponse() && this.getCountdownLeft() < 0L && !this.mGift.isLockedByLevel();
   }

   public void setReward(boolean reward) {
      this.mReward = reward;
   }

   public boolean isReward() {
      return this.mReward;
   }

   public void setStatus(int status) {
      this.mStatus = status;
   }

   public int getStatus() {
      return this.mStatus;
   }

   public void setCountdown(long timestamp) {
      this.mCountdown = timestamp;
   }

   public long getCountdownLeft() {
      return this.mCountdown != 0L ? this.mCountdown - System.currentTimeMillis() : 0L;
   }

   public NeighborProfile getNeighbor() {
      return this.mNeighbor;
   }

   public GiftItem getGiftItem() {
      return this.mGift;
   }
}
