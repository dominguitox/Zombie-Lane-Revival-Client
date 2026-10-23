package com.dchoc.idead.tracking;

import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.Logger;

public class ErrorEvent extends Event {
   public ErrorEvent() {
      super(1, 38, 10);
   }

   public void trackDavinciError(String tag, String detail, String extra) {
      this.setType(39);
      this.trackError(tag, detail, extra);
   }

   public void trackBundleError(String tag, String detail, String extra) {
      this.setType(40);
      this.trackError(tag, detail, extra);
   }

   public void trackLoadError(String tag, String detail, String extra) {
      this.setType(41);
      this.trackError(tag, detail, extra);
   }

   public void trackFatalError(String tag, String detail, String extra) {
      this.setType(42);
      this.trackError(tag, detail, extra);
   }

   public void trackError(String tag, String detail, String extra) {
      Logger.error(tag, detail + " - " + extra);
      this.setParameter(9, tag);
      if (detail != null) {
         this.setParameter(17, detail);
      }

      if (extra != null) {
         this.setParameter(18, extra);
      }

      this.setParameter(24, Integer.toString((int)(GameEngineUtils.getPlayTime() / 1000L)));
      this.trackEvent(false);
   }
}
