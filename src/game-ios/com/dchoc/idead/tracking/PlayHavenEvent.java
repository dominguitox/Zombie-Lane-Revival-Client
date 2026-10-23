package com.dchoc.idead.tracking;

public class PlayHavenEvent extends Event {
   public PlayHavenEvent() {
      super(0, 49, -1);
   }

   public void trackOpened(String name) {
      this.setParameter(17, name);
      this.setAction(9);
      this.trackEvent();
   }

   public void trackLoaded(String name) {
      this.setParameter(17, name);
      this.setAction(34);
      this.trackEvent();
   }

   public void trackAccept(String name) {
      this.setParameter(17, name);
      this.setAction(31);
      this.trackEvent();
   }

   public void trackDecline(String name) {
      this.setParameter(17, name);
      this.setAction(32);
      this.trackEvent();
   }
}
