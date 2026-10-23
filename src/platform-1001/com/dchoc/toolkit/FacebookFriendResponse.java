package com.dchoc.toolkit;

public class FacebookFriendResponse extends FacebookResponse {
   private UserProfile[] friends;

   public FacebookFriendResponse(FacebookGraphAPIRequest var1, int var2, UserProfile[] var3) {
      super(var1, var2);
      this.friends = var3;
   }

   public UserProfile[] getFriends() {
      return this.friends;
   }
}
