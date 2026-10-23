package com.dchoc.toolkit;

import javax.microedition.lcdui.Graphics;

public class AppleAppStorePaymentManager implements IPaymentManager {
   private IPaymentListener listener;

   public void doDraw(Graphics graphics) {
   }

   public void handleRequestPurchase() {
   }

   public boolean isInAppPaymentSupported() {
      return false;
   }

   public void keyEventOccurred(int keyCode, int type) {
   }

   public int logicUpdate(int delta) {
      return 1;
   }

   public void pointerEventOccurred(int type, int x, int y) {
   }

   public void purchaseProduct(PurchaseRequest purchaseRequest) {
      if (this.listener != null) {
         this.listener.productPurchased(new PaymentTransaction(purchaseRequest, false, 1, 0));
      }
   }

   public void requestAvailableProducts() {
   }

   public void restoreCompletedTransactions() {
   }

   public void setPaymentListener(IPaymentListener listener) {
      this.listener = listener;
   }
}
