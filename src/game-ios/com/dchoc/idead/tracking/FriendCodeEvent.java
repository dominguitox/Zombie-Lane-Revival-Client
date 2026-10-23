package com.dchoc.idead.tracking;

public class FriendCodeEvent extends Event {
   public FriendCodeEvent() {
      super(3, 37, 51);
   }

   public void trackFriendCode(int result) {
      this.setParameter(17, smProductValues[result]);
      this.trackEvent();
   }
}
