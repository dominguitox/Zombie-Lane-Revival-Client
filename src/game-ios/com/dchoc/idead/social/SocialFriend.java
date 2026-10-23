package com.dchoc.idead.social;

import com.dchoc.idead.servlets.AddNeighborRequest;
import com.dchoc.toolkit.UserProfile;

public class SocialFriend {
   public static final int INVALID_FRIENDCODE = -1;
   private static final String EMPTY_STRING = new String("");
   private String mName;
   private String mFacebookID;
   private String mGameCenterID;
   private AddNeighborRequest mAddNeighbor;
   private String mFriendCode;
   private boolean mAdded;

   public SocialFriend(String friendCode) {
      this.mFriendCode = friendCode;
      this.mName = null;
      this.mFacebookID = null;
      this.mGameCenterID = null;
      this.mAddNeighbor = new AddNeighborRequest();
      this.mAdded = false;
   }

   public SocialFriend(String name, String gameCenterID) {
      this.mFriendCode = EMPTY_STRING;
      this.mName = name;
      this.mFacebookID = null;
      this.mGameCenterID = gameCenterID;
      this.mAddNeighbor = new AddNeighborRequest();
      this.mAdded = false;
   }

   public SocialFriend(UserProfile userProfile) {
      this.mFriendCode = EMPTY_STRING;
      this.mName = userProfile.getName();
      this.mFacebookID = userProfile.getId();
      this.mGameCenterID = null;
      this.mAddNeighbor = new AddNeighborRequest();
      this.mAdded = false;
   }

   public void invite() {
      if (!this.isInviting()) {
         this.mAddNeighbor.init(this);
         this.mAddNeighbor.execute();
      }
   }

   public boolean isInviting() {
      return this.mAddNeighbor.isWaitingForResponse();
   }

   public boolean isInviteAdded() {
      return this.mAddNeighbor.isResponseRecieved() && this.mAddNeighbor.isAdded();
   }

   public boolean isInviteFailed() {
      return this.mAddNeighbor.isResponseRecieved() && !this.mAddNeighbor.isAdded() && this.mAddNeighbor.isSucceed();
   }

   public void setAdded(boolean added) {
      this.mAdded = added;
   }

   public boolean isAdded() {
      return this.mAdded;
   }

   public boolean isAlreadyNeighbor() {
      return this.mAddNeighbor.isResponseRecieved() && this.mAddNeighbor.isAlreadyNeighbor();
   }

   public boolean compare(SocialFriend friend) {
      if (friend == null) {
         return false;
      } else if (this.mFacebookID != null && friend.mFacebookID != null && this.mFacebookID.equals(friend.mFacebookID)) {
         return true;
      } else if (this.mGameCenterID != null && friend.mGameCenterID != null && this.mGameCenterID.equals(friend.mGameCenterID)) {
         return true;
      } else {
         return this.mFriendCode != EMPTY_STRING && !friend.mFriendCode.equals(EMPTY_STRING) ? this.mFriendCode.equals(friend.mFriendCode) : false;
      }
   }

   public String getName() {
      return this.mName;
   }

   public String getFacebookID() {
      return this.mFacebookID;
   }

   public String getGameCenterID() {
      return this.mGameCenterID;
   }

   public String getFriendCode() {
      return this.mFriendCode;
   }

   public int getSocialIconType() {
      if (this.mFacebookID != null) {
         return 1;
      } else {
         return this.mGameCenterID != null ? 2 : 3;
      }
   }
}
