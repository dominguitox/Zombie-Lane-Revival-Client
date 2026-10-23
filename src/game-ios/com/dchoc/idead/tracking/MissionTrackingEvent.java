package com.dchoc.idead.tracking;

public class MissionTrackingEvent extends Event {
   public MissionTrackingEvent(int csv) {
      super(1, 10, 2);
      this.setParameterCSV(17, csv);
   }

   public void trackStarted() {
      this.setAction(2);
      this.trackEvent();
   }

   public void trackCompleted() {
      this.setAction(8);
      this.trackEvent();
   }

   public void trackObjectiveCompleted(int csv) {
      this.setParameterCSV(18, csv);
      this.setAction(17);
      this.trackEvent();
      this.removeParameter(18);
   }

   public void trackExpired() {
      this.setAction(11);
      this.trackEvent();
   }
}
