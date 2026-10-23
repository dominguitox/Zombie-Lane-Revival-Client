package com.dchoc.idead.tracking;

import com.dchoc.idead.player.PlayerProfile;

public class BuyCurrencyEvent extends Event {
   public static final int CURRENCY_CASH = 0;
   public static final int CURRENCY_COINS = 1;
   private static final String CASH_STRING = "cash";
   private static final String COINS_STRING = "coins";
   private int mProductType;

   public BuyCurrencyEvent(String product, int productType, int delta, int price) {
      super(2, 5, 3);
      this.setParameter(17, product);
      this.mProductType = productType;
      if (productType == 0) {
         this.setParameter(18, "cash");
         this.setType(13);
         this.setParameter(32, delta);
         this.setParameter(33, PlayerProfile.getCash());
      } else {
         this.setParameter(18, "coins");
         this.setType(14);
         this.setParameter(30, delta);
         this.setParameter(31, PlayerProfile.getCoins());
      }

      this.setParameter(34, price / 10);
   }

   public void trackStarted() {
      this.setAction(2);
      this.trackEvent();
   }

   public void trackError() {
      this.setAction(10);
      this.trackEvent();
   }

   public void trackCompleted() {
      this.setAction(8);
      this.updateBalance();
      this.trackEvent();
   }

   public void trackCancelled() {
      this.setAction(4);
      this.trackEvent();
   }

   public void trackValidationFailed() {
      this.setAction(53);
      this.trackEvent();
   }

   public void trackValidationOK() {
      this.setAction(54);
      this.trackEvent();
   }

   public void trackValidationError() {
      this.setAction(55);
      this.trackEvent();
   }

   private void updateBalance() {
      if (this.mProductType == 0) {
         this.setParameter(33, PlayerProfile.getCash());
      } else {
         this.setParameter(31, PlayerProfile.getCoins());
      }
   }
}
