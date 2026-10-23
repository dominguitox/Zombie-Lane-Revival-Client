package com.dchoc.toolkit;

public class EmptyFacebookConnection implements IFacebookConnection {
   public static IFacebookConnection getFacebookConnection(String var0, String var1) {
      return null;
   }

   @Override
   public void getAppFriends() {
   }

   @Override
   public UserProfile getCurrentUser() {
      return null;
   }

   @Override
   public void getFriends() {
   }

   @Override
   public boolean isAlreadyLoggedIn() {
      return false;
   }

   @Override
   public void login() {
   }

   @Override
   public void postToFeed(FacebookPost var1) {
   }

   @Override
   public void postToInvite(FacebookPost var1, String var2) {
   }

   @Override
   public void setFacebookResponseListener(IFacebookResponseListener var1) {
   }
}
