package com.dchoc.idead.tracking;

import com.dchoc.idead.tutorial.TutorialFlow;

public class TutorialTrackingEvent extends Event {
   public TutorialTrackingEvent(int step) {
      super(1, 12, 8);
      this.setStep(step);
   }

   public void setStep(int step) {
      this.setParameter(17, TutorialFlow.TUTORIAL_STEPS[step]);
      this.setParameter(18, step);
   }

   public void trackCompleted() {
      this.setAction(8);
      this.trackEvent();
   }

   public void trackStarted() {
      this.setAction(2);
      this.trackEvent();
   }
}
