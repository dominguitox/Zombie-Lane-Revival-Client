package com.dchoc.idead.tracking;

import com.dchoc.idead.player.PlayerProfile;

public class EarnPCEvent extends Event {
   public EarnPCEvent() {
      super(2, 5, 3);
   }

   public void trackEvent(String source, int delta) {
      this.setParameter(17, source);
      this.setParameter(32, delta);
      this.setParameter(33, PlayerProfile.getCash());
      this.trackEvent();
   }
}
