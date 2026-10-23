package com.dchoc.iphonewrappers;

import com.dchoc.toolkit.Toolkit;
import zlport.ios.Ios;

public class iWrapper {
   private static boolean smSoundsOn;
   private static int smMusicRid = -1;
   private static int smNextMusicRid = -1;
   private static int smFramesWaited;
   private static Object activityIndicator2;

   public static void playMusic(int resourceId) {
      if (resourceId != -1) {
         int volume = Toolkit.getMusicVolume();
         if (volume > 0 && Toolkit.playMidi(resourceId, -1, volume, 1) != null) {
            smMusicRid = resourceId;
         }
      }
   }

   public static void switchMusic(int resourceId) {
      if (Toolkit.isPlayingMusic()) {
         Toolkit.stopCurrentMusic();
         smMusicRid = -1;
         smNextMusicRid = resourceId;
         smFramesWaited = 0;
      } else {
         playMusic(resourceId);
      }
   }

   public static void trySwitchMusic() {
      if (smNextMusicRid != -1) {
         smFramesWaited++;
         if (smFramesWaited < 1) {
            return;
         }

         if (!Toolkit.isPlayingMusic()) {
            playMusic(smNextMusicRid);
         }

         smNextMusicRid = -1;
      }
   }

   public static void stopMusic() {
      Toolkit.stopCurrentMusic();
      smMusicRid = -1;
   }

   public static int getMusic() {
      return smMusicRid;
   }

   public static void enableSoundFX(boolean enabled) {
      smSoundsOn = enabled;
   }

   public static void playSoundFx(int resourceId) {
      if (smSoundsOn && resourceId != -1) {
         int volume = Toolkit.getSoundEffectVolume();
         if (volume > 0) {
            Toolkit.playMidi(resourceId, 1, volume, 0);
         }
      }
   }

   public static void stopAllSounds() {
   }

   public static void preloadSoundEffect(int resourceId) {
   }

   public static void stopLoadingAnim() {
      if (activityIndicator2 != null) {
         Ios.platform().stopActivityIndicator(activityIndicator2);
         Ios.platform().removeActivityIndicatorFromSuperview(activityIndicator2);
         Ios.platform().releaseActivityIndicator(activityIndicator2);
         activityIndicator2 = null;
      } else {
         System.out.println("startLoading was not called before this stopLoading call.");
      }
   }
}
