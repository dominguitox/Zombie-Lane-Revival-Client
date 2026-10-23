package com.dchoc.iphonewrappers;

import com.dchoc.idead.Time;
import com.dchoc.toolkit.Toolkit;
import zlport.ios.Ios;

public class ActivityWrapper {
   private static long smFrameTimestamp;
   private static Object activityMain;
   private static Object activityFrame;
   private static final Object[] activities = new Object[3];

   public static void init() {
      smFrameTimestamp = 0L;
   }

   public static void startFrameTimer() {
   }

   public static void stopFrameTimer() {
      if (activityFrame != null) {
         removeIndicator(activityFrame);
         activityFrame = null;
      }
   }

   public static boolean isFrameUpdateIndicatorVisible() {
      return smFrameTimestamp != 0L && Time.getTimestampDelta(smFrameTimestamp) >= 350L;
   }

   public static void updateFrameTime() {
      smFrameTimestamp = Time.getTimestamp();
   }

   public static void show(int x, int y) {
      int width = Toolkit.getScreenWidth();
      int height = Toolkit.getScreenHeight();
      if (activityMain == null) {
         activityMain = Ios.platform().createActivityIndicator(1);
         Object window = Ios.platform().window();
         Ios.platform().addSubview(window, activityMain);
         Ios.platform().bringSubviewToFront(window, activityMain);
         float boundsHeight = window == null ? 0.0F : Ios.platform().windowHeight(window);
         float scale = width / boundsHeight;
         Ios.platform().setActivityIndicatorCenter(activityMain, (height - y) / scale, (width - x) / scale);
         Ios.platform().startActivityIndicator(activityMain);
      }
   }

   public static void show(int index, int x, int y) {
      int width = Toolkit.getScreenWidth();
      int height = Toolkit.getScreenHeight();
      if (index >= 1 && index <= 3 && activities[index - 1] == null) {
         Object indicator = Ios.platform().createActivityIndicator(2);
         activities[index - 1] = indicator;
         Object window = Ios.platform().window();
         Ios.platform().addSubview(window, indicator);
         Ios.platform().bringSubviewToFront(window, indicator);
         float boundsHeight = window == null ? 0.0F : Ios.platform().windowHeight(window);
         float scale = width / boundsHeight;
         Ios.platform().setActivityIndicatorCenter(indicator, (height - y) / scale, x / scale);
         Ios.platform().startActivityIndicator(indicator);
      }
   }

   public static void hide() {
      if (activityMain != null) {
         removeIndicator(activityMain);
         activityMain = null;
      }
   }

   public static void hide(int index) {
      if (index >= 1 && index <= 3 && activities[index - 1] != null) {
         removeIndicator(activities[index - 1]);
         activities[index - 1] = null;
      }
   }

   private static void removeIndicator(Object indicator) {
      Ios.platform().stopActivityIndicator(indicator);
      Ios.platform().removeActivityIndicatorFromSuperview(indicator);
      Ios.platform().releaseActivityIndicator(indicator);
   }
}
