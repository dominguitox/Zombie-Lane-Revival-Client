package com.dchoc.toolkit;

public interface IFacebookConnection {
   void getAppFriends();

   UserProfile getCurrentUser();

   void getFriends();

   boolean isAlreadyLoggedIn();

   void login();

   void postToFeed(FacebookPost var1);

   void postToInvite(FacebookPost var1, String var2);

   void setFacebookResponseListener(IFacebookResponseListener var1);
}
