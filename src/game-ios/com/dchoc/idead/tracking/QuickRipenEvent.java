package com.dchoc.idead.tracking;

import com.dchoc.idead.player.PlayerProfile;

public class QuickRipenEvent extends Event {
   public QuickRipenEvent() {
      super(2, 3, 52);
   }

   public void trackEvent(int csv1, int csv2, int delta) {
      this.setParameterCSV(17, csv1);
      this.setParameterCSV(18, csv2);
      this.setParameter(33, PlayerProfile.getCash());
      this.setParameter(32, delta);
      this.trackEvent();
   }
}
