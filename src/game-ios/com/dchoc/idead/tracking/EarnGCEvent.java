package com.dchoc.idead.tracking;

import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.player.PlayerProfile;

public class EarnGCEvent extends Event {
   public EarnGCEvent(int source) {
      super(2, 4, 20);
      this.setType(source);
   }

   @Override
   public void setType(int source) {
      super.setType(4);
      switch (source) {
         case 0:
            this.setAction(26);
            break;
         case 1:
            this.setAction(37);
            break;
         case 2:
            this.setAction(7);
         case 3:
         default:
            break;
         case 4:
            this.setAction(36);
            break;
         case 5:
            this.setAction(21);
            break;
         case 6:
            this.setAction(24);
            break;
         case 7:
            this.setAction(22);
      }
   }

   public void trackEarn(int delta) {
      int coins = PlayerProfile.getCoins();
      int loot = IsometricUtils.getScene().getLootRewards().getLootAmount(0);
      this.setParameter(30, delta);
      this.setParameter(31, loot + coins);
      this.trackEvent();
   }

   public void trackEarn(int csv1, int csv2, int delta) {
      this.setParameterCSV(17, csv1);
      this.setParameterCSV(18, csv2);
      this.setParameter(30, delta);
      this.setParameter(31, PlayerProfile.getCoins());
      this.trackEvent();
   }
}
