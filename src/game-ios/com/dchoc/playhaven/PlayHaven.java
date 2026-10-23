package com.dchoc.playhaven;

import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tracking.PlayHavenEvent;
import com.dchoc.toolkit.CsvIDs;
import java.util.function.IntConsumer;
import zlport.ios.Ios;

public class PlayHaven {
   public static PlayHavenEvent smTrackingEvent = new PlayHavenEvent();
   private static boolean mShowedPlayHeaven;

   public static void showContent(int id, boolean unused) {
      if (id >= 0 && id < CsvIDs.CSV_ID_NAMES.length) {
         final String placement = CsvIDs.CSV_ID_NAMES[id];
         if (id == 45 || id == 6048 || id == 6216 || id == 6218) {
            if (!mShowedPlayHeaven) {
               mShowedPlayHeaven = true;
               Ios.platform().showPlayHavenContent(placement, new IntConsumer() {
                  @Override
                  public void accept(int code) {
                     if (code == 0) {
                        PlayHaven.smTrackingEvent.trackOpened(placement);
                     } else if (code == 1) {
                        PlayHaven.smTrackingEvent.trackLoaded(placement);
                     } else if (code == 2) {
                        PlayHaven.smTrackingEvent.trackAccept(placement);
                     } else if (code == 3) {
                        PlayHaven.smTrackingEvent.trackDecline(placement);
                     }
                  }
               });
            } else {
               Ios.platform().startChartboostSession();
            }
         }
      }
   }

   public static void triggerAchievementCompleted() {
      showContent(6218, false);
   }

   public static void triggerCloseCustomization() {
   }

   public static void triggerCloseInventory() {
   }

   public static void triggerCloseSettings() {
   }

   public static void triggerCloseSocialMenu() {
   }

   public static void triggerCloseStore() {
   }

   public static void triggerCompleteMission() {
      showContent(6048, false);
   }

   public static void triggerEnergyFull() {
   }

   public static void triggerLevelUp() {
      int level = PlayerProfile.getLevel();
      if (level != 0 && level % 5 == 0) {
         showContent(45, false);
      }
   }

   public static void triggerLoad() {
      showContent(PlayerProfile.isTutorialComplete() ? 6216 : 6215, false);
   }

   public static void triggerOpenCustomization() {
   }

   public static void triggerOpenInventory() {
   }

   public static void triggerOpenMenu() {
   }

   public static void triggerOpenSettings() {
   }

   public static void triggerOpenSocialMenu() {
   }

   public static void triggerOpenStore() {
   }

   public static void triggerOutOfEnergy() {
   }

   public static void triggerResume() {
   }

   public static void triggerReturnHome() {
   }

   public static void triggerSession() {
      int count = PlayerProfile.getResumeCount();
      if (count != 0 && count % 10 == 0) {
      }
   }
}
