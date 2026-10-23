package com.dchoc.idead.tracking;

public class GeneralPurchaseEvent extends Event {
   private static final String STARTER_PACK = "starter_pack";

   public GeneralPurchaseEvent(String product, int kind) {
      super(2, 50, 3);
      this.setParameter(17, product);
      if (kind == 0) {
         this.setParameter(18, "starter_pack");
         this.setType(50);
      }
   }

   public void trackStarted() {
      this.setAction(2);
      this.trackEvent();
   }

   public void trackCompleted() {
      this.setAction(8);
      this.trackEvent();
   }

   public void trackError() {
      this.setAction(10);
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
}
