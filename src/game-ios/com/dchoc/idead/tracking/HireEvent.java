package com.dchoc.idead.tracking;

public class HireEvent extends Event {
   public HireEvent() {
      super(3, 9, 6);
   }

   public void trackHire(String recipients, int csv, int unused) {
      this.setParameter(35, recipients);
      this.setParameterCSV(17, csv);
      this.setParameterCSV(18, csv);
      this.trackEvent();
   }
}
