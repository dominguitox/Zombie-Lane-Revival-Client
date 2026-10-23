package com.dchoc.idead.tracking;

public class ActionEvent extends Event {
   public ActionEvent(int unused) {
      super(2, 21, -1);
   }

   public void setParams(int action, int csv1, int csv2) {
      this.setAction(action);
      this.setParameterCSV(17, csv1);
      this.setParameterCSV(18, csv2);
   }

   public void trackAction(boolean social) {
      if (social) {
         this.setGroup(3);
         this.setType(24);
      } else {
         this.setGroup(2);
         this.setType(21);
      }

      this.trackEvent();
   }
}
