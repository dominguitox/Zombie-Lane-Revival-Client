package com.dchoc.idead.tracking;

import com.dchoc.idead.GameEngine;

public class LoadingTimeEvent extends Event {
   private boolean mTrackResumeLoading;
   private long mStartMillis;

   public LoadingTimeEvent() {
      super(0, 28, -1);
   }

   public void startLoadingTime() {
      this.mStartMillis = System.currentTimeMillis();
   }

   public boolean isTrackResumeLoading() {
      return this.mTrackResumeLoading;
   }

   public void enableTrackResumeLoading() {
      this.mTrackResumeLoading = true;
   }

   public void trackInitialLoadingTime() {
      this.setType(28);
      this.setAction(this.elapsedLabel());
      this.trackEvent();
   }

   public void trackResumeLoadingTime() {
      this.setType(29);
      this.setAction(this.elapsedLabel());
      this.trackEvent();
      this.mTrackResumeLoading = false;
   }

   public void trackAreaLoadingTime() {
      long area = GameEngine.getInstance().getArea().getName() & 4294967295L;
      int type;
      if (area == 4294967295L) {
         type = 30;
      } else if (area < 3818L) {
         if (area != 1926L) {
            return;
         }

         type = 31;
      } else if (area < 5560L) {
         if (area == 3818L) {
            type = 32;
         } else if (area == 4054L) {
            type = 33;
         } else {
            if (area != 4745L) {
               return;
            }

            type = 34;
         }
      } else {
         if (area != 5560L) {
            return;
         }

         type = 35;
      }

      this.setType(type);
      this.setAction(this.elapsedLabel());
      this.trackEvent();
   }

   private int elapsedLabel() {
      return TrackingEvents.getLoadingTimeLabel(System.currentTimeMillis() - this.mStartMillis);
   }
}
