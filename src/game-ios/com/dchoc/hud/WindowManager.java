package com.dchoc.hud;

import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.Window;
import com.dchoc.windows.WindowAchievementAward;
import com.dchoc.windows.WindowBoosterTimeout;
import com.dchoc.windows.WindowCrafting;
import com.dchoc.windows.WindowCreateCharacter;
import com.dchoc.windows.WindowCustomization;
import com.dchoc.windows.WindowDailyBonus;
import com.dchoc.windows.WindowFinishConstruction;
import com.dchoc.windows.WindowLevelUp;
import com.dchoc.windows.WindowLevelUpUpgrade;
import com.dchoc.windows.WindowLifestrip;
import com.dchoc.windows.WindowMarket;
import com.dchoc.windows.WindowMission;
import com.dchoc.windows.WindowMissionComplete;
import com.dchoc.windows.WindowMissionList;
import com.dchoc.windows.WindowNewItem;
import com.dchoc.windows.WindowNpcDialog;
import com.dchoc.windows.WindowNpcMonolog;
import com.dchoc.windows.WindowOutOfAmmo;
import com.dchoc.windows.WindowOutOfCashCoins;
import com.dchoc.windows.WindowOutOfEnergy;
import com.dchoc.windows.WindowQuickRipen;
import com.dchoc.windows.WindowRequiredItem;
import com.dchoc.windows.WindowSellItem;
import com.dchoc.windows.WindowSettings;
import com.dchoc.windows.WindowSocial;
import com.dchoc.windows.WindowStarterPack;
import com.dchoc.windows.WindowStuff;
import com.dchoc.windows.WindowSurvivalTip;
import com.dchoc.windows.WindowTeaser;
import com.dchoc.windows.WindowTimedMission;
import com.dchoc.windows.WindowTradeCollection;
import com.dchoc.windows.WindowTransactionConfirmation;
import com.dchoc.windows.WindowTraveling;
import com.dchoc.windows.WindowVisitNeighbor;
import com.dchoc.windows.WindowWitheredCrops;
import java.util.Vector;

public class WindowManager {
   public static final int WINDOW_INVALID = -1;
   public static final int WINDOW_CREATE_CHARACTER = 0;
   public static final int WINDOW_MISSION = 1;
   public static final int WINDOW_MISSION_COMPLETE = 2;
   public static final int WINDOW_OUT_OF_ENERGY = 3;
   public static final int WINDOW_DAILY_REWARD = 4;
   public static final int WINDOW_NPC_TEXTBOX = 5;
   public static final int WINDOW_LEVEL_UP = 6;
   public static final int WINDOW_LEVEL_UP_UPGRADE = 7;
   public static final int WINDOW_VISIT_NEIGHBOR = 8;
   public static final int WINDOW_CRAFTING = 9;
   public static final int WINDOW_FINISH_BUILDING = 10;
   public static final int WINDOW_OUT_OF_CASH_OR_COINS = 11;
   public static final int WINDOW_OUT_OF_AMMO = 12;
   public static final int WINDOW_TIMED_MISSION = 13;
   public static final int WINDOW_NEW_ITEM = 14;
   public static final int WINDOW_SELL_ITEM = 15;
   public static final int WINDOW_LIFESTRIP = 16;
   public static final int WINDOW_ACHIEVEMENT_AWARD = 17;
   public static final int WINDOW_SOCIAL = 18;
   public static final int WINDOW_NPC_DIALOG = 19;
   public static final int WINDOW_STUFF = 20;
   public static final int WINDOW_SETTINGS = 21;
   public static final int WINDOW_MARKET = 22;
   public static final int WINDOW_SURVIVAL_TIP_INFO = 23;
   public static final int WINDOW_TRAVELING = 24;
   public static final int WINDOW_CUSTOMIZATION = 25;
   public static final int WINDOW_MISSION_LIST = 26;
   public static final int WINDOW_TRANSACTION_CONFIRMATION = 27;
   public static final int WINDOW_WITHERED_CROPS = 28;
   public static final int WINDOW_TRADE_COLLECTION = 29;
   public static final int WINDOW_MISSION_COMPLETE_2 = 30;
   public static final int WINDOW_BOOSTER_TIMEOUT = 31;
   public static final int WINDOW_QUICK_RIPEN_CROP = 32;
   public static final int WINDOW_TEASER = 33;
   public static final int WINDOW_REQUIRED_ITEM = 34;
   public static final int WINDOW_STARTER_PACK = 35;
   public static final String[] WINDOW_NAMES = new String[]{
      "WINDOW_CREATE_CHARACTER",
      "WINDOW_MISSION",
      "WINDOW_MISSION_COMPLETE",
      "WINDOW_OUT_OF_ENERGY",
      "WINDOW_DAILY_REWARD",
      "WINDOW_NPC_TEXTBOX",
      "WINDOW_LEVEL_UP",
      "WINDOW_LEVEL_UP_UPGRADE",
      "WINDOW_VISIT_NEIGHBOR",
      "WINDOW_CRAFTING",
      "WINDOW_FINISH_BUILDING",
      "WINDOW_OUT_OF_CASH_OR_COINS",
      "WINDOW_OUT_OF_AMMO",
      "WINDOW_TIMED_MISSION",
      "WINDOW_NEW_ITEM",
      "WINDOW_SELL_ITEM",
      "WINDOW_LIFESTRIP",
      "WINDOW_ACHIEVEMENT_AWARD",
      "WINDOW_SOCIAL",
      "WINDOW_NPC_DIALOG",
      "WINDOW_STUFF",
      "WINDOW_SETTINGS",
      "WINDOW_MARKET",
      "WINDOW_SURVIVAL_TIP_INFO",
      "WINDOW_TRAVELING",
      "WINDOW_CUSTOMIZATION",
      "WINDOW_MISSION_LIST",
      "WINDOW_TRANSACTION_CONFIRMATION",
      "WINDOW_WITHERED_CROPS",
      "WINDOW_TRADE_COLLECTION",
      "WINDOW_MISSION_COMPLETE_2",
      "WINDOW_BOOSTER_TIMEOUT",
      "WINDOW_QUICK_RIPEN_CROP",
      "WINDOW_TEASER",
      "WINDOW_REQUIRED_ITEM",
      "WINDOW_STARTER_PACK"
   };
   private static Vector smOpenedWindows = new Vector(4);
   private static Window[] smWindows;
   private static byte[] smPreviousWindows;

   public static void init() {
      Window[] windows = new Window[]{
         new WindowCreateCharacter(),
         new WindowMission(),
         new WindowMissionComplete(),
         new WindowOutOfEnergy(),
         new WindowDailyBonus(),
         new WindowNpcMonolog(),
         new WindowLevelUp(),
         new WindowLevelUpUpgrade(),
         new WindowVisitNeighbor(),
         new WindowCrafting(),
         new WindowFinishConstruction(),
         new WindowOutOfCashCoins(),
         new WindowOutOfAmmo(),
         new WindowTimedMission(),
         new WindowNewItem(),
         new WindowSellItem(),
         new WindowLifestrip(),
         new WindowAchievementAward(),
         new WindowSocial(),
         new WindowNpcDialog(),
         new WindowStuff(),
         new WindowSettings(),
         new WindowMarket(),
         new WindowSurvivalTip(),
         new WindowTraveling(),
         new WindowCustomization(),
         new WindowMissionList(),
         new WindowTransactionConfirmation(),
         new WindowWitheredCrops(),
         new WindowTradeCollection(),
         new WindowMissionComplete(),
         new WindowBoosterTimeout(),
         new WindowQuickRipen(),
         new WindowTeaser(),
         new WindowRequiredItem(),
         new WindowStarterPack()
      };
      smWindows = windows;

      for (int i = 0; i < smWindows.length; i++) {
         if (smWindows[i] != null) {
            smWindows[i].initialize();
            smWindows[i].setId((byte)i);
         }
      }

      smPreviousWindows = new byte[]{-1, -1, -1};
   }

   public static boolean processTouchEvent(TouchEvent event) {
      boolean free = true;
      if (!smOpenedWindows.isEmpty()) {
         Window window = (Window)smOpenedWindows.firstElement();
         window.pointerEvent(event);
         if (!window.isModal()) {
            return true;
         }

         event.setConsumed(true);
         free = false;
      }

      return free;
   }

   public static void logicUpdate(int delta) {
      if (!smOpenedWindows.isEmpty()) {
         Window window = (Window)smOpenedWindows.firstElement();
         window.logicUpdate(delta);
         if (window.isClosed()) {
            smOpenedWindows.removeElement(window);
         }
      }

      if (TutorialFlow.isCompleted() && !smOpenedWindows.isEmpty()) {
         HUD.UPDATE_GAME = !((Window)smOpenedWindows.firstElement()).isOpened();
         WindowNpcDialog.updateRateUsTrigger();
      } else {
         HUD.UPDATE_GAME = true;
         WindowNpcDialog.updateRateUsTrigger();
      }
   }

   public static boolean isEmpty() {
      return smOpenedWindows.isEmpty();
   }

   public static Window firstOpenedWindow() {
      return (Window)smOpenedWindows.firstElement();
   }

   public static Window getWindow(byte index) {
      return smWindows[index];
   }

   public static boolean isWindowOpened(byte index) {
      boolean opened = false;
      if (smWindows != null) {
         Window window = smWindows[index];
         if (window == null) {
            return false;
         }

         opened = window.isOpened();
      }

      return opened;
   }

   public static boolean isFullscreenWindowVisible() {
      return smOpenedWindows.size() < 1 ? false : ((Window)smOpenedWindows.firstElement()).isFullscreen();
   }

   public static void openWindow(byte index) {
      if (smWindows[index].isClosed() || index == 0) {
         smWindows[index].open();
         smOpenedWindows.removeElement(smWindows[index]);
         smOpenedWindows.addElement(smWindows[index]);
         HUD.closeTooltips(true);
         HUD.closeHealthBars(false, true);
      }
   }

   public static void closeWindow() {
      if (smOpenedWindows.size() >= 1) {
         Window window = (Window)smOpenedWindows.firstElement();
         window.close();
         byte id = (byte)window.getId();
         smPreviousWindows[0] = smPreviousWindows[1];
         smPreviousWindows[1] = smPreviousWindows[2];
         smPreviousWindows[2] = id;
      }
   }

   public static void forceCloseWindow() {
      if (smOpenedWindows.size() >= 1) {
         Window window = (Window)smOpenedWindows.firstElement();
         window.closeForced();
         byte id = (byte)window.getId();
         smPreviousWindows[0] = smPreviousWindows[1];
         smPreviousWindows[1] = smPreviousWindows[2];
         smPreviousWindows[2] = id;
         smOpenedWindows.removeElement(window);
      }
   }

   public static String getWindowName(byte index) {
      return WINDOW_NAMES[index];
   }

   public static int getPreviousWindow2() {
      return smPreviousWindows[1];
   }

   public static boolean isNPCDialogOpen(int mode) {
      Window[] windows = smWindows;
      Window dialog = null;
      if (windows != null) {
         dialog = windows[19];
      }

      return windows != null && dialog != null && dialog.isOpened() ? ((WindowNpcDialog)smWindows[19]).getMode() == mode : false;
   }

   public static void openNPCDialog(byte mode) {
      if (((WindowNpcDialog)smWindows[19]).setMode(mode)) {
         openWindow((byte)19);
      }
   }

   public static boolean isNPCDialogOpeningOrClosing() {
      boolean result;
      if (smOpenedWindows.contains(smWindows[5])) {
         if (smWindows[5].isOpening()) {
            return true;
         }

         result = smWindows[5].isClosing();
      } else {
         result = false;
      }

      return result;
   }

   public static void openNPCMonolog(String text, int npc, Mission mission, int x, int y) {
      forceCloseWindow();
      setNPCMonologOptions((WindowNpcMonolog)smWindows[5], text, (short)npc, mission, x, y);
      openWindow((byte)5);
   }

   public static void setNPCMonologOptions(WindowNpcMonolog window, String text, short npc, Mission mission, int x, int y) {
      boolean completed = TutorialFlow.isCompleted();
      window.setModal(completed);
      window.setWideBubble(completed);
      window.setText(text);
      window.setNPC(npc);
      window.setMission(mission);
      if ((x & y) == -1 || completed) {
         int screenWidth = Toolkit.getScreenWidth();
         int width = window.getWidth();
         int screenHeight = Toolkit.getScreenHeight();
         int height = window.getHeight();
         x = screenWidth - width >> 1;
         y = screenHeight - height >> 1;
      }

      window.setPosition(x, y);
      window.setOkButtonVisible(TutorialFlow.isCompleted() || TutorialFlow.getState() == 6);
   }
}
