package com.dchoc.idead.tracking;

public class WindowEvent extends Event {
   public WindowEvent() {
      super(0, 19, 13);
   }

   public void trackShown(String name) {
      this.setParameter(17, name);
      this.setAction(13);
      this.trackEvent();
   }

   public void trackTab(String name, String tab) {
      this.setParameter(17, name);
      this.setParameter(18, tab);
      this.setAction(19);
      this.trackEvent();
   }

   public void trackSkipped(String name) {
      this.setParameter(17, name);
      this.setAction(14);
      this.trackEvent();
   }

   public void trackClose(String name) {
      this.setParameter(17, name);
      this.setAction(16);
      this.trackEvent();
   }
}
