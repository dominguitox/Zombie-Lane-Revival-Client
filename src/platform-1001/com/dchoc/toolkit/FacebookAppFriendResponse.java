package com.dchoc.toolkit;

public class FacebookAppFriendResponse extends FacebookResponse {
   private UserProfile[] appFriends;

   public FacebookAppFriendResponse(FacebookGraphAPIRequest var1, int var2, UserProfile[] var3) {
      super(var1, var2);
      this.appFriends = var3;
   }

   public UserProfile[] getFriends() {
      return this.appFriends;
   }
}
