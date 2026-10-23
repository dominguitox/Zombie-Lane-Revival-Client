package com.dchoc.idead.neighbors;

import com.dchoc.idead.Time;
import com.dchoc.idead.commands.CommandBase;
import com.dchoc.idead.items.GiftItem;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.player.Profile;
import com.dchoc.idead.servlets.AcceptNeighborActionsRequest;
import com.dchoc.idead.social.SocialFriend;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.toolkit.ToolkitHelpers;
import java.util.Vector;

public class NeighborProfile extends Profile {
   public static final int INITIAL_ACTIONS_CAPACITY = 5;
   public static final int INITIAL_GIFTS_CAPACITY = 5;
   public static final int STATUS_ACCEPTED = 1;
   public static final int STATUS_INVALID = -1;
   private int mActionsLeft;
   private long mActionsTime;
   private Vector mCommands;
   private Vector mGifts;
   private int mStatus;
   private int mPlatform;
   private AcceptNeighborActionsRequest mAcceptActions;
   private boolean mHired;

   public NeighborProfile(String name, String userId, int status, int platform, int type) {
      super(name, type);
      this.mUserID = userId;
      this.mStatus = status;
      this.mPlatform = platform;
      this.mAcceptActions = new AcceptNeighborActionsRequest();
      this.mGifts = new Vector(5);
      this.mCommands = new Vector(5);
      this.loadDefaultGifts();
      this.mHired = true;
      this.mActionsLeft = 0;
      this.mLevel = 0;
      this.mActionsTime = 0L;
   }

   public void updateGifts() {
      int[] gift0 = Tuner.getGift0();
      NeighborGift neighborGift = null;

      for (int i = 0; gift0 != null && i < gift0.length; i++) {
         neighborGift = this.getGift(gift0[i]);
         if (neighborGift != null) {
            break;
         }
      }

      if (gift0 != null && gift0.length - 1 > -1) {
         for (int length = gift0.length - 1; length >= 0; length--) {
            GiftItem gift = ItemManager.getGift(gift0[length]);
            if (gift != null && !gift.isLockedByLevel() && !gift.isLockedByMission()) {
               if (gift == neighborGift.getGiftItem()) {
                  return;
               }

               NeighborGift newGift = new NeighborGift(this, gift, -1L, 1);
               int index = this.mGifts.indexOf(neighborGift);
               this.mGifts.setElementAt(newGift, index);
               return;
            }
         }
      }
   }

   public boolean compare(SocialFriend friend) {
      if (this.mFacebookID != null && friend.getFacebookID() != null && this.mFacebookID.equals(friend.getFacebookID())) {
         return true;
      } else {
         return this.mGameCenterID != null && friend.getGameCenterID() != null ? this.mGameCenterID.equals(friend.getGameCenterID()) : false;
      }
   }

   public boolean compare(NeighborProfile neighbor) {
      return this.mUserID != null && neighbor.getUserID() != null ? this.mUserID.equals(neighbor.getUserID()) : false;
   }

   public void addCommand(CommandBase command) {
      if (command != null) {
         this.mCommands.addElement(command);
      }
   }

   public void removeCommand(long id) {
      if (id != -1L) {
         for (int i = 0; i < this.mCommands.size(); i++) {
            CommandBase command = (CommandBase)this.mCommands.elementAt(i);
            if (command.getID() == id) {
               ToolkitHelpers.fastRemove(i, this.mCommands);
               return;
            }
         }
      }
   }

   public CommandBase getCommand(long id) {
      if (id != -1L) {
         for (int i = 0; i < this.mCommands.size(); i++) {
            CommandBase command = (CommandBase)this.mCommands.elementAt(i);
            if (command.getID() == id) {
               return command;
            }
         }
      }

      return null;
   }

   public boolean hasCommands() {
      return !this.mCommands.isEmpty();
   }

   public Vector getCommands() {
      return this.mCommands;
   }

   public void acceptCommands(boolean accept) {
      if (!this.mCommands.isEmpty()) {
         this.mAcceptActions.init(this, accept);
         this.mAcceptActions.execute();
      }
   }

   public boolean isAcceptingCommands() {
      return this.mAcceptActions.isWaitingForResponse();
   }

   public NeighborGift getGift(int itemId) {
      for (int i = 0; i < this.mGifts.size(); i++) {
         NeighborGift gift = (NeighborGift)this.mGifts.elementAt(i);
         if (gift.getGiftItem().getID() == itemId) {
            return gift;
         }
      }

      return null;
   }

   public Vector getGifts() {
      return this.mGifts;
   }

   public void setHired(boolean hired) {
      this.mHired = hired;
   }

   public boolean isHired() {
      return this.mHired;
   }

   public void setActionsLeft(int actionsLeft) {
      this.mActionsLeft = actionsLeft;
   }

   public int getActionsLeft() {
      return this.mActionsLeft;
   }

   public void decreaseActionsLeft(int amount) {
      if (amount > 0 && this.mActionsLeft >= amount) {
         if (this.mActionsLeft == 5) {
            this.mActionsTime = Time.getTimestamp();
         }

         this.mActionsLeft -= amount;
      }
   }

   public void setActionsTime(long timestamp) {
      this.mActionsTime = timestamp;
   }

   public long getActionsTime() {
      return this.mActionsTime;
   }

   public void setStatus(int status) {
      this.mStatus = status;
   }

   public int getStatus() {
      return this.mStatus;
   }

   public void setPlatform(int platform) {
      this.mPlatform = platform;
   }

   public int getPlatform() {
      return this.mPlatform;
   }

   public int getSocialIconType() {
      if (this.mPlatform == 4) {
         return 4;
      } else if (this.mFacebookID != null) {
         return 1;
      } else {
         return this.mGameCenterID != null ? 2 : 3;
      }
   }

   public void loadDefaultGifts() {
      int[] gift0 = Tuner.getGift0();
      GiftItem gift = ItemManager.getGift(gift0[0]);
      if (gift != null) {
         this.mGifts.addElement(new NeighborGift(this, gift, -1L, 1));
      }

      GiftItem gift1 = ItemManager.getGift(Tuner.getGift1());
      if (gift1 != null) {
         this.mGifts.addElement(new NeighborGift(this, gift1, -1L, 1));
      }

      GiftItem gift2 = ItemManager.getGift(Tuner.getGift2());
      if (gift2 != null) {
         this.mGifts.addElement(new NeighborGift(this, gift2, -1L, 1));
      }

      GiftItem gift3 = ItemManager.getGift(Tuner.getGift3());
      if (gift3 != null) {
         this.mGifts.addElement(new NeighborGift(this, gift3, -1L, 1));
      }

      this.updateGifts();
   }
}
