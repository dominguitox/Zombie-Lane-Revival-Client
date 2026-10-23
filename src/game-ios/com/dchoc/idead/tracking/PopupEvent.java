package com.dchoc.idead.tracking;

public class PopupEvent extends WindowEvent {
   public static final int POPOUP_OO_ENERGY = 0;
   public static final int POPOUP_OO_AMMO = 1;
   public static final int POPOUP_OO_COINS = 2;
   public static final int POPOUP_OO_CASH = 3;
   public static final int POPOUP_OO_BOUGHT_CASH = 4;
   public static final int POPOUP_OO_BOUGHT_COINS = 5;
   private static final String[] POPUP_NAMES = new String[]{"OutOfEnergy", "OutOfAmmo", "OutOfCoins", "OutOfCash", "BoughtCash", "BoughCoins"};
   private int mPopup;

   public PopupEvent(int popup) {
      this.mPopup = popup;
   }

   public void trackBuy() {
      this.setAction(15);
      this.trackEvent();
   }

   public void trackBuyProduct(int csv) {
      this.setParameterCSV(17, csv);
      this.trackBuy();
      this.removeParameter(17);
   }

   public void trackShown() {
      this.trackShown(POPUP_NAMES[this.mPopup]);
   }

   public void trackSkipped() {
      this.trackShown(POPUP_NAMES[this.mPopup]);
   }
}
