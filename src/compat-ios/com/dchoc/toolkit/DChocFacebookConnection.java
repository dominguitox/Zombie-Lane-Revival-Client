package com.dchoc.toolkit;

public class DChocFacebookConnection implements IFacebookConnection {
   private IFacebookResponseListener listener;

   public void getAppFriends() {
   }

   public UserProfile getCurrentUser() {
      return null;
   }

   public void getFriends() {
   }

   public boolean isAlreadyLoggedIn() {
      return false;
   }

   public void login() {
      if (this.listener != null) {
         this.listener.responseReceived(new FacebookResponse(null, -1));
      }
   }

   public void postToFeed(FacebookPost post) {
   }

   public void postToInvite(FacebookPost post, String friendId) {
   }

   public void setFacebookResponseListener(IFacebookResponseListener listener) {
      this.listener = listener;
   }
}
