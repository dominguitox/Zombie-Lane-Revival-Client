package com.dchoc.hud;

import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.DestructibleItem;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.objects.ConstructionObject;
import com.dchoc.idead.objects.FarmingObject;
import com.dchoc.idead.objects.ObjectWithHealth;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.Window;
import com.dchoc.windows.WindowMarket;
import com.dchoc.windows.WindowMission;
import com.dchoc.windows.WindowStuff;
import com.dchoc.windows.WindowTooltip;
import com.dchoc.windows.WindowVisitNeighbor;
import zlport.ios.Ios;

public class HUD {
   private static final int TOOLTIP_MAX = 20;
   public static boolean HIDE_INGAME = false;
   public static boolean UPDATE_GAME = true;
   private static HUDGame smInGame;
   private static WindowTooltip[] smTooltips;
   private static int smTooltipIndex;
   private static HealthBar[] smHealthBars;
   private static int smHealthBarIndex;
   private static int smState;

   public static void init() {
      PaperDoll.init();
      smInGame = new HUDGame();
      WindowManager.init();
      smTooltips = new WindowTooltip[20];

      for (int i = 0; i < smTooltips.length; i++) {
         smTooltips[i] = new WindowTooltip();
      }

      smTooltipIndex = 0;
      HealthBar.loadAnimations();
      smHealthBars = new HealthBar[20];

      for (int i = 0; i < smHealthBars.length; i++) {
         smHealthBars[i] = new HealthBar();
      }

      smHealthBarIndex = 0;
      changeState(1);
   }

   public static void changeState(int state) {
      switch (state) {
         case 1:
            Ios.platform().hideAd();
            smInGame.changeState(0);
            HIDE_INGAME = false;
            break;
         case 2:
            smInGame.changeState(1);
            HIDE_INGAME = false;
            break;
         case 3:
            smInGame.changeState(2);
            HIDE_INGAME = false;
            break;
         case 4:
            smInGame.changeState(3);
            break;
         case 5:
            smInGame.changeState(5);
            break;
         case 6:
            smInGame.changeState(6);
         case 7:
         default:
            break;
         case 8:
            smInGame.changeState(7);
            break;
         case 9:
            smInGame.changeState(4);
      }

      smState = state;
   }

   public static void increateTipsCounter() {
      smInGame.getFoldable().increaseTipCounter();
      ((WindowStuff)WindowManager.getWindow((byte)20)).increaseTipCounter();
   }

   public static HUDGame getInGame() {
      return smInGame;
   }

   public static void resetTipsCounter() {
      smInGame.getFoldable().resetTipCounter();
      ((WindowStuff)WindowManager.getWindow((byte)20)).resetTipCounter();
   }

   public static void pointerEvent(TouchEvent event) {
      checkTooltipClose(event);
      if (event.getType() == 0) {
         WindowTooltip.enableTooltips(true);
      } else if (event.getType() == 1) {
         WindowTooltip.enableTooltips(false);
      }

      if (WindowManager.isNPCDialogOpen(9)) {
         smInGame.getPaperDoll().pointerEventProfile(event);
      } else {
         boolean free = WindowManager.processTouchEvent(event);
         if (TutorialFlow.isCompleted() && free) {
            smInGame.pointerEvent(event);
         }
      }
   }

   private static void checkTooltipClose(TouchEvent event) {
      if (event.getType() == 2) {
         for (int i = 0; i < smTooltips.length; i++) {
            if (!smTooltips[i].isClosed()) {
               smTooltips[i].checkCloseAfterRelease();
            }
         }
      }
   }

   public static int getState() {
      return smState;
   }

   public static void resetState() {
      smInGame.resetState();
   }

   public static void logicUpdate(int delta) {
      WindowManager.logicUpdate(delta);
      Window window;
      HUDObject game;
      if (!WindowManager.isEmpty() && (window = WindowManager.firstOpenedWindow()) != null && !window.isModal()) {
         game = smInGame.getCombo();
      } else {
         game = smInGame;
      }

      game.logicUpdate(delta);

      for (int i = 0; i < smHealthBars.length; i++) {
         if (!smHealthBars[i].isClosed()) {
            smHealthBars[i].logicUpdate(delta);
         }
      }

      for (int i = 0; i < smTooltips.length; i++) {
         if (!smTooltips[i].isClosed()) {
            smTooltips[i].logicUpdate(delta);
         }
      }
   }

   public static HUDCombo getComboMeter() {
      return smInGame.getCombo();
   }

   public static void drawHUD() {
      drawHealthBars();
      drawTooltips(0);
      if (!HIDE_INGAME && (UPDATE_GAME || WindowManager.isNPCDialogOpen(9))) {
         smInGame.draw();
      }

      drawTooltips(1);
      if (!WindowManager.isEmpty()) {
         WindowManager.firstOpenedWindow().draw();
         if (!TutorialFlow.isCompleted()) {
            smInGame.getCombo().draw();
         }
      }

      drawTooltips(2);
   }

   private static void drawHealthBars() {
      for (int i = 0; i < smHealthBars.length; i++) {
         if (!smHealthBars[i].isClosed()) {
            smHealthBars[i].draw();
         }
      }
   }

   public static void drawTooltips(int mode) {
      for (int i = 0; smTooltips.length > i; i++) {
         if (!smTooltips[i].isClosed()) {
            int type = smTooltips[i].getTooltipType();
            boolean top = smTooltips[i].isTopLayer();
            if (mode == 2) {
               if (top) {
                  smTooltips[i].draw();
               }
            } else if (mode == 1) {
               if (Integer.compareUnsigned(type - 25, 1) > 0) {
                  smTooltips[i].draw();
               }
            } else if (mode == 0 && Integer.compareUnsigned(type - 25, 2) < 0) {
               smTooltips[i].draw();
            }
         }
      }
   }

   public static boolean isSceneActive() {
      return UPDATE_GAME;
   }

   public static WindowTooltip getTooltip(IsometricObject target) {
      WindowTooltip tooltip = null;
      if (target != null) {
         int i = 0;

         while (true) {
            if (smTooltips.length <= i) {
               return null;
            }

            if (!smTooltips[i].isClosed() && smTooltips[i].getTargetObject() != null && smTooltips[i].getTargetObject() == target) {
               tooltip = smTooltips[i];
               break;
            }

            i++;
         }
      }

      return tooltip;
   }

   public static WindowTooltip getNextTooltip() {
      int index = smTooltipIndex;
      if (index == 20) {
         index = 0;
         smTooltipIndex = 0;
      }

      WindowTooltip tooltip = smTooltips[index];
      smTooltipIndex = index + 1;
      tooltip.setTopLayer(false);
      return tooltip;
   }

   public static HealthBar getHealthBar(IsometricObject target) {
      HealthBar bar = null;
      if (target != null) {
         int i = 0;

         while (true) {
            if (smHealthBars.length <= i) {
               return null;
            }

            if (!smHealthBars[i].isClosed() && smHealthBars[i].getTargetObject() != null && smHealthBars[i].getTargetObject() == target) {
               bar = smHealthBars[i];
               break;
            }

            i++;
         }
      }

      return bar;
   }

   public static void createHealthBar(IsometricObject target, int touch, boolean auto) {
      if (target != null
         && target.getType() != 1
         && target.getType() != 7
         && (target.getType() != 3 || ((ObjectWithHealth)target).isAlive())
         && (TutorialFlow.isCompleted() || !TutorialFlow.isCompleted() && TutorialFlow.getState() == 2 && target.getType() == 2)
         && (target.getType() != 6 || !((FarmingObject)target).isWithered())) {
         int touchType = 0;
         if (touch != 5) {
            if (touch != 3) {
               return;
            }

            touchType = 1;
            if (auto) {
               touchType = 2;
            }
         }

         HealthBar bar = getHealthBar(target);
         boolean created;
         if (bar == null) {
            created = true;
            int index = smHealthBarIndex;
            if (index == 20) {
               index = 0;
               smHealthBarIndex = 0;
            }

            bar = smHealthBars[index];
            smHealthBarIndex = index + 1;
         } else {
            created = false;
         }

         bar.setTouchType(touchType);
         if (bar.initHealthBar(target, created)) {
            bar.setBar();
         }
      }
   }

   public static void createTooltip(int type, int x, int y, int width, int height) {
      WindowTooltip tooltip = getNextTooltip();
      tooltip.initNewTooltip(null);
      tooltip.setTopLayer(false);
      tooltip.setTooltip(type, x, y, width, height, -1, null, null);
   }

   public static void createTooltip(int type, int x, int y, int width, int height, int extra, String text, boolean topLayer) {
      WindowTooltip tooltip = getNextTooltip();
      tooltip.initNewTooltip(null);
      tooltip.setTopLayer(topLayer);
      tooltip.setTooltip(type, x, y, width, height, extra, null, text);
   }

   public static void createSceneTooltip(IsometricObject target, int touch, int text) {
      createSceneTooltip(target, touch, text, null);
   }

   public static void createSceneTooltip(IsometricObject target, int touch, int text, String extra) {
      boolean enabled = WindowTooltip.isEnableTooltips();
      if (target != null) {
         if (enabled) {
            if (target.getType() != 1) {
               if (target.getType() != 7) {
                  if (touch == 3) {
                     if (target.getType() == 0) {
                        if (((ConstructionObject)target).getState() != 0) {
                           return;
                        }
                     } else if (target.getType() == 3) {
                        if (!((DestructibleItem)target.getItem()).isFixable()) {
                           return;
                        }
                     } else {
                        if (target.getType() != 6) {
                           return;
                        }

                        if (!((FarmingObject)target).isGrowing()) {
                           return;
                        }
                     }
                  }

                  if (!TutorialFlow.isCompleted() && (TutorialFlow.isCompleted() || TutorialFlow.getState() != 2 || target.getType() != 2)) {
                     return;
                  }

                  int touchType = 0;
                  if (touch != 5) {
                     if (touch != 3) {
                        return;
                     }

                     touchType = 1;
                  }

                  HealthBar bar = getHealthBar(target);
                  WindowTooltip tooltip = getTooltip(target);
                  if (tooltip == null) {
                     tooltip = getNextTooltip();
                  }

                  initSceneTooltip(target, tooltip, bar, touchType, text, extra);
               }
            }
         }
      }
   }

   public static void initSceneTooltip(IsometricObject target, WindowTooltip tooltip, HealthBar bar, int touchType, int text, String extra) {
      tooltip.setTouchType(touchType);
      tooltip.initNewTooltip(target);
      int type = 25;
      if (text != -1) {
         WindowTooltip.addText(Toolkit.getText(text));
         type = 26;
      }

      tooltip.setTooltip(type, bar, extra);
   }

   public static void updateHealthBarAtActionStart(IsometricObject target) {
      int i = 0;

      while (i < smHealthBars.length && (smHealthBars[i].isClosed() || !smHealthBars[i].updateAtActionStart(target))) {
         i++;
      }
   }

   public static void checkHealthBarAfterAction(IsometricObject target) {
      for (int i = 0; i < smHealthBars.length; i++) {
         if (!smHealthBars[i].isClosed()) {
            smHealthBars[i].checkCloseAfterAction(target);
         }
      }
   }

   public static void checkTooltipAfterAction(IsometricObject target) {
      for (int i = 0; i < smTooltips.length; i++) {
         if (!smTooltips[i].isClosed()) {
            smTooltips[i].checkCloseAfterAction(target);
         }
      }
   }

   public static void closeTooltips(boolean holdOnly) {
      for (int i = 0; i < smTooltips.length; i++) {
         WindowTooltip tooltip = smTooltips[i];
         if (tooltip != null && !tooltip.isClosed() && (!holdOnly || smTooltips[i].getTouchType() == 0)) {
            smTooltips[i].closeTooltip(true);
         }
      }
   }

   public static void closeHealthBar(IsometricObject target) {
      for (int i = 0; i < smHealthBars.length; i++) {
         HealthBar bar = smHealthBars[i];
         if (bar != null && !bar.isClosed()) {
            IsometricObject barTarget = smHealthBars[i].getTargetObject();
            if (barTarget != null && barTarget == target) {
               smHealthBars[i].close();
               return;
            }
         }
      }
   }

   public static void closeHealthBars(boolean force, boolean holdOnly) {
      for (int i = 0; i < smHealthBars.length; i++) {
         HealthBar bar = smHealthBars[i];
         if (bar != null
            && !bar.isClosed()
            && (force || !IsometricUtils.getScene().getPlayer().hasActionWith(smHealthBars[i].getTargetObject()))
            && (!holdOnly || smHealthBars[i].getTouchType() == 0)) {
            smHealthBars[i].close();
         }
      }
   }

   public static void openQuickRipenTab(IsometricObject target) {
      smInGame.openQuickRipenTab(target);
   }

   public static void openMission(Mission mission) {
      if (mission != null) {
         if (mission.getType() != 2399) {
            ((WindowMission)WindowManager.getWindow((byte)1)).setMission(mission);
            WindowManager.openWindow((byte)1);
         }

         MissionManager.updateCounter(2895, mission.getID(), 1);
      }
   }

   public static void showVisitNeighbor(String name, int coins, int xp, int energy, int items) {
      WindowVisitNeighbor window = (WindowVisitNeighbor)WindowManager.getWindow((byte)8);
      window.setNeighborName(name);
      window.setRewards(coins, xp, energy, items);
      WindowManager.openWindow((byte)8);
   }

   public static HUDMissions getMissions() {
      return smInGame.getMissions();
   }

   public static void initializePayments() {
      ((WindowMarket)WindowManager.getWindow((byte)22)).requestCashAndCoinds();
   }
}
