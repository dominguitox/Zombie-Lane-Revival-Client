package com.dchoc.idead.tracking;

public class GiftSendEvent extends Event {
   public GiftSendEvent(int csv, String recipients) {
      super(3, 8, 5);
      this.setParameterCSV(17, csv);
      if (recipients != null) {
         this.setParameter(35, recipients);
      }
   }

   public void trackAccept(boolean accepted) {
      if (accepted) {
         this.setAction(31);
      } else {
         this.setAction(32);
      }

      this.trackEvent();
   }

   public void trackSend() {
      this.setAction(5);
      this.trackEvent();
   }
}
