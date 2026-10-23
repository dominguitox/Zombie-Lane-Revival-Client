package com.dchoc.idead.tracking;

public class VisitingEvent extends Event {
   public VisitingEvent() {
      super(3, 25, 2);
   }

   public void trackStarted(String friend) {
      this.setAction(2);
      this.setType(25);
      this.setParameter(35, friend);
      this.trackEvent();
   }

   public void trackTutorStarted() {
      this.visitStarted(26);
   }

   public void trackCircusStarted() {
      this.visitStarted(44);
   }

   public void trackDowntownStarted() {
      this.visitStarted(45);
   }

   public void trackCrashSiteStarted() {
      this.visitStarted(46);
   }

   public void trackCureStarted() {
      this.visitStarted(47);
   }

   public void trackLoaded() {
      this.setAction(34);
      this.trackEvent();
   }

   public void trackError() {
      this.setAction(10);
      this.trackEvent();
   }

   public void trackCompleted() {
      this.setAction(8);
      this.trackEvent();
   }

   private void visitStarted(int type) {
      this.setAction(2);
      this.setType(type);
      this.removeParameter(35);
      this.trackEvent();
   }
}
