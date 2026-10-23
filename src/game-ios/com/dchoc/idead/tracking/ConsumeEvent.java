package com.dchoc.idead.tracking;

public class ConsumeEvent extends Event {
   public ConsumeEvent() {
      super(2, 17, 3);
   }

   public void trackConsume(int csv1, int csv2) {
      this.setParameterCSV(17, csv1);
      this.setParameterCSV(18, csv2);
      this.trackEvent();
   }
}
