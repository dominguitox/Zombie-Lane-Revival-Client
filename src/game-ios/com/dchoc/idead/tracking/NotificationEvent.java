package com.dchoc.idead.tracking;

public class NotificationEvent extends Event {
   public NotificationEvent() {
      super(3, 43, 5);
   }

   public void trackSend(int csv) {
      this.setAction(5);
      this.setParameterCSV(17, csv);
      this.trackEvent();
   }

   public void trackOpened(int csv) {
      this.setAction(9);
      this.setParameterCSV(17, csv);
      this.trackEvent();
   }
}
