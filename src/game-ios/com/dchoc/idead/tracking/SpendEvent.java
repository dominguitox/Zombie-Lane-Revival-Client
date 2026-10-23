package com.dchoc.idead.tracking;

import com.dchoc.idead.player.PlayerProfile;

public class SpendEvent extends Event {
   private int mSpendType;

   public SpendEvent(int type) {
      super(2, type, 3);
      this.mSpendType = type;
   }

   public void trackSpend(int csv1, int csv2, int delta, int unused) {
      this.setParameterCSV(17, csv1);
      this.setParameterCSV(18, csv2);
      if (this.mSpendType == 2) {
         this.setParameter(31, PlayerProfile.getCoins());
         this.setParameter(30, delta);
      } else if (this.mSpendType == 3) {
         this.setParameter(33, PlayerProfile.getCash());
         this.setParameter(32, delta);
      }

      this.trackEvent();
   }
}
