package com.dchoc.idead.events;

import com.dchoc.hud.HUD;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.BoosterItem;
import com.dchoc.idead.items.GiftItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.Achievement;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.neighbors.NeighborGift;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.playhaven.PlayHaven;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.Window;
import com.dchoc.windows.WindowAchievementAward;
import com.dchoc.windows.WindowBoosterTimeout;
import com.dchoc.windows.WindowDailyBonus;
import com.dchoc.windows.WindowLevelUp;
import com.dchoc.windows.WindowLevelUpUpgrade;
import com.dchoc.windows.WindowLifestrip;
import com.dchoc.windows.WindowMissionComplete;
import com.dchoc.windows.WindowNewItem;
import com.dchoc.windows.WindowSocial;
import com.dchoc.windows.WindowTeaser;
import com.dchoc.windows.WindowTimedMission;
import java.util.Collections;
import zlport.ios.Ios;

public class TimedEvent implements CsvIDs {
   private int mType;
   private int mDelay;
   private int mElapsedTime;
   private boolean mActive;
   private boolean mExecuted;
   private Mission mMission;
   private Achievement mAchievement;
   private int[] mParameters;

   public TimedEvent(int delay, int type, int[] parameters, Mission mission) {
      this.mMission = mission;
      this.mDelay = delay;
      this.mType = type;
      this.mParameters = parameters;
      this.mActive = false;
      this.mExecuted = false;
      this.mElapsedTime = 0;
   }

   public TimedEvent(int delay, int type, int[] parameters) {
      this.mMission = null;
      this.mDelay = delay;
      this.mType = type;
      this.mParameters = parameters;
      this.mActive = false;
      this.mExecuted = false;
      this.mElapsedTime = 0;
   }

   public void setAchievement(Achievement achievement) {
      this.mAchievement = achievement;
   }

   public int logicUpdate(int dt) {
      if (this.mActive) {
         this.mElapsedTime += dt;
         if (!this.mExecuted) {
            if (this.mDelay <= this.mElapsedTime) {
               return 2;
            }

            return 0;
         }
      }

      return 0;
   }

   public void setActive(boolean active) {
      this.mActive = active;
   }

   public boolean isActive() {
      return this.mActive;
   }

   public boolean isExecuted() {
      return this.mExecuted;
   }

   public int getDelay() {
      return this.mDelay;
   }

   public int getTimeLeft() {
      return this.mDelay - this.mElapsedTime;
   }

   public void init(int delay, int type, int[] parameters) {
      this.mDelay = delay;
      this.mType = type;
      this.mParameters = parameters;
      this.mActive = false;
      this.mExecuted = false;
      this.mElapsedTime = 0;
   }

   protected void execute() {
      GameEngine engine = GameEngine.getInstance();
      switch (this.mType) {
         case 1:
            WindowManager.openNPCDialog((byte)3);
            break;
         case 45:
            int level = this.mParameters[0] + 1;
            if (WindowLevelUpUpgrade.isUpgrade(level)) {
               ((WindowLevelUpUpgrade)WindowManager.getWindow((byte)7)).init(level);
               WindowManager.openWindow((byte)7);
               Ios.platform().logEvent("LEVEL_UP_UPGRADE", Collections.singletonMap("Level", Integer.toString(level)));
            } else {
               ((WindowLevelUp)WindowManager.getWindow((byte)6)).init(level);
               WindowManager.openWindow((byte)6);
               Ios.platform().logEvent("LEVEL_UP", Collections.singletonMap("Level", Integer.toString(level)));
            }

            MissionManager.setUpdateNewMissions();
            break;
         case 46:
            if (TutorialFlow.isCompleted()) {
               ((WindowDailyBonus)WindowManager.getWindow((byte)4)).setProgress(this.mParameters[0]);
               WindowManager.openWindow((byte)4);
            }
            break;
         case 2294:
            WindowManager.openNPCMonolog(Toolkit.getText(this.mParameters[1]), this.mParameters[0], this.mMission, -1, -1);
            break;
         case 2313:
         case 2314:
         case 3303:
         case 3304:
            ((WindowTimedMission)WindowManager.getWindow((byte)13))
               .setMission(this.mMission, this.mType == 2314 || this.mType == 3304, this.mType != 3303 && this.mType != 3304 ? 0 : 1);
            WindowManager.openWindow((byte)13);
            break;
         case 2465:
            MissionManager.setUpdateNewMissions();
            break;
         case 2466:
            Window w2 = WindowManager.getWindow((byte)2);
            Window w30 = WindowManager.getWindow((byte)30);
            if (w2.isClosed()) {
               ((WindowMissionComplete)w2).setMission(this.mMission);
               WindowManager.openWindow((byte)2);
            } else {
               ((WindowMissionComplete)w30).setMission(this.mMission);
               WindowManager.openWindow((byte)30);
            }
            break;
         case 2467:
            return;
         case 2468:
            ((WindowAchievementAward)WindowManager.getWindow((byte)17)).setOptions(this.mAchievement);
            WindowManager.openWindow((byte)17);
            break;
         case 2469:
            WindowManager.openNPCDialog((byte)5);
            break;
         case 2470:
            WindowNewItem itemWindow = (WindowNewItem)WindowManager.getWindow((byte)14);
            Item item;
            int count;
            if (this.mParameters == null) {
               item = ItemManager.getItem(0);
               count = -1;
            } else if (this.mParameters.length == 3) {
               item = ItemManager.getItem(this.mParameters[0], this.mParameters[1]);
               count = this.mParameters[2];
            } else if (this.mParameters.length == 2) {
               item = ItemManager.getItem(this.mParameters[0], this.mParameters[1]);
               count = -1;
            } else {
               item = ItemManager.getItem(this.mParameters[0]);
               count = -1;
            }

            itemWindow.setItem(item, count);
            WindowManager.openWindow((byte)14);
         case 2471:
            break;
         case 2472:
            WindowLifestrip w16 = (WindowLifestrip)WindowManager.getWindow((byte)16);
            if (this.mParameters.length == 5) {
               w16.setOptions(this.mParameters[0], this.mParameters[1], this.mParameters[2], this.mParameters[3], this.mParameters[4]);
            } else {
               w16.setOptions(this.mParameters[0], this.mParameters[1], this.mParameters[2], this.mParameters[3]);
            }

            WindowManager.openWindow((byte)16);
            break;
         case 2473:
            PlayerProfile.addAvatar(41, true);
            break;
         case 2474:
            PlayerProfile.addAvatar(43, true);
            break;
         case 2734:
            HUD.getInGame().showSpeechBubble(IsometricUtils.getPlayer(), this.mParameters[0], this.mParameters[1]);
            break;
         case 2880:
            WindowManager.openNPCDialog((byte)7);
            break;
         case 2881:
            WindowManager.openNPCDialog((byte)8);
            break;
         case 2882:
            WindowManager.openNPCDialog((byte)6);
            break;
         case 2883:
            WindowManager.openNPCDialog((byte)10);
            break;
         case 2894:
            WindowManager.openNPCDialog((byte)9);
            break;
         case 2895:
            HUD.getMissions().openMission(this.mMission);
            break;
         case 2974:
            WindowManager.openNPCDialog((byte)1);
            break;
         case 3249:
            WindowManager.openNPCDialog((byte)12);
            break;
         case 3279:
            BoosterItem booster = ItemManager.getBooster(this.mParameters[0]);
            if (booster != null) {
               ((WindowBoosterTimeout)WindowManager.getWindow((byte)31)).setBooster(booster);
               WindowManager.openWindow((byte)31);
            }
            break;
         case 3882:
            WindowTeaser circusWindow = (WindowTeaser)WindowManager.getWindow((byte)33);
            int circusLevel = PlayerProfile.getLevel();
            circusWindow.setType((byte)(circusLevel > 9 ? 1 : 0), this.mParameters[0]);
            WindowManager.openWindow((byte)33);
            break;
         case 3883:
            engine.travelToCircus();
            break;
         case 3884:
         case 3887:
         case 3888:
         case 3891:
         case 3892:
         case 3895:
            return;
         case 3885:
            WindowTeaser cureWindow = (WindowTeaser)WindowManager.getWindow((byte)33);
            int cureLevel = PlayerProfile.getLevel();
            cureWindow.setType((byte)(cureLevel > 24 ? 7 : 6), this.mParameters[0]);
            WindowManager.openWindow((byte)33);
            break;
         case 3886:
            engine.travelToCure();
            break;
         case 3889:
            WindowTeaser downtownWindow = (WindowTeaser)WindowManager.getWindow((byte)33);
            int dtLevel = PlayerProfile.getLevel();
            if (dtLevel <= 14) {
               downtownWindow.setType((byte)2, this.mParameters[0]);
            } else if (MissionManager.getMission(this.mParameters[0]).isActive()) {
               downtownWindow.setType((byte)3, this.mParameters[0]);
            } else {
               this.mParameters[0] = -1;
               downtownWindow.setType((byte)2, -1);
            }

            WindowManager.openWindow((byte)33);
            break;
         case 3890:
            engine.travelToDowntown();
            break;
         case 3893:
            WindowTeaser crashWindow = (WindowTeaser)WindowManager.getWindow((byte)33);
            int crashLevel = PlayerProfile.getLevel();
            crashWindow.setType((byte)(crashLevel > 19 ? 5 : 4), this.mParameters[0]);
            WindowManager.openWindow((byte)33);
            break;
         case 3894:
            engine.travelToCrashSite();
            break;
         case 3896:
            engine.travelToHome();
            break;
         case 4053:
            engine.setSaveGameState(false);
            break;
         case 5545:
            IsometricUtils.setObjectHealth(IsometricUtils.getObjectWithID(this.mParameters[0], this.mParameters[1]), this.mParameters[2], true);
            break;
         case 6057:
            WindowManager.openNPCDialog((byte)11);
            break;
         case 6058:
            if (this.mParameters[0] != -1) {
               iWrapper.playSoundFx(this.mParameters[0]);
            }
            break;
         case 6211:
            PlayHaven.showContent(this.mParameters[0], false);
            break;
         case 6212:
         case 6214:
            GiftItem gift = ItemManager.getGift(this.mType);
            if (gift == null) {
               return;
            }

            NeighborProfile neighbor = new NeighborProfile(Toolkit.getText(2236), new String(""), 1, 3, 1926);
            NeighborGift ng = new NeighborGift(neighbor, gift, 1L, 1);
            ng.setStatus(2);
            ng.setReward(true);
            ((WindowSocial)WindowManager.getWindow((byte)18)).addGift(ng);
            break;
         default:
            return;
      }

      this.mExecuted = true;
   }
}
