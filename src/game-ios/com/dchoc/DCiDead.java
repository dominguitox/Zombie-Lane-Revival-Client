package com.dchoc;

import com.dchoc.idead.ApplicationControl;
import com.dchoc.idead.FlowProcessor;
import com.dchoc.idead.Game;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.tracking.ErrorEvent;
import com.dchoc.idead.tracking.Event;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.iphonewrappers.ActivityWrapper;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.iphonewrappers.SettingsWrapper;
import com.dchoc.menuobject.MenuObject;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.DavinciUtilities;
import com.dchoc.spriteobject.ServerAssetLoader;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.DChocMIDlet;
import com.dchoc.toolkit.Sonda;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import zlport.ios.Ios;

public class DCiDead extends DChocMIDlet implements ApplicationControl {
   public static final int ACTIVITY_STATE_NONE = -1;
   public static final int ACTIVITY_STATE_LOADING = 0;
   public static final int ACTIVITY_STATE_SAVING = 1;
   public static final int FONT_BROWN_BUTTON = 0;
   public static final int FONT_GREEN_BUTTON = 1;
   public static final int FONT_HEADER = 2;
   public static final int FONT_LIGHT_BROWN = 3;
   public static final int FONT_BIG_BROWN = 4;
   public static final int FONT_HEADER_2 = 5;
   public static final int FONT_OBJECTIVES = 6;
   public static final int FONTS_COUNT = 7;
   public static ImageFont[] smFonts;
   public static String[] smActivityTexts;
   public static SpriteObject smLoadingScreen;
   private static SpriteObject smLoadingBar;
   private static String[] smLoadingBarTagLine;
   private static String[] smFlurryVideoAdText;
   private static String smFlurryVideoAdClickText;
   private static String smFlurryExitButtonText;
   private static String smFlurryNoAdsAvail;
   private static int smLoadingBarCenterX;
   private static int smLoadingBarCenterWidth;
   private static int smLoadingBarLeftWidth;
   private static int smLoadingBarRightWidth;
   private static int smLoadingBarBoxAreaWidth;
   private static int smActivityState = -1;
   private static int smActivityTransition = -1;
   private static int smActivityTimer;
   private static boolean smActivityInstant;
   private static boolean smActivityStopped;
   private FlowProcessor mGameFlowProcessor;
   private FlowProcessor mCurrentFlowProcessor;
   private FlowProcessor mNextFlowProcessor;
   private boolean mInitialized;
   private boolean mLoading;
   private int mMaxLoadingCount;
   private int mLoadingCounter;
   private int mInitialFlowLoadingPercentage;

   public static ImageFont getFont(int index) {
      return smFonts[index];
   }

   @Override
   public void logicUpdate(int delta) {
      if (!this.mInitialized) {
         if (Ios.platform().getNeedUpdate() == 0) {
            this.initialize();
         }
      } else if (!this.updateActivityTimer(delta)) {
         if (this.mCurrentFlowProcessor != this.mNextFlowProcessor) {
            this.mCurrentFlowProcessor = this.mNextFlowProcessor;
         }

         if (this.mCurrentFlowProcessor != null) {
            this.mCurrentFlowProcessor.logicUpdate(delta);
            if (this.mInitialFlowLoadingPercentage != -1) {
               this.mInitialFlowLoadingPercentage = this.mCurrentFlowProcessor.getLoadingPercentage();
            }
         }
      }
   }

   @Override
   public void doPostDraw(Graphics graphics) {
      if (this.mCurrentFlowProcessor != null) {
         this.mCurrentFlowProcessor.doPostDraw(graphics);
      }
   }

   @Override
   public void doDraw(Graphics graphics) {
      Toolkit.getRenderingPlatform().setGraphicsContext(graphics);
      OGL.applyDefaults();
      if (!this.mLoading) {
         if (this.mCurrentFlowProcessor != null) {
            if (this.mInitialFlowLoadingPercentage == -1) {
               this.mCurrentFlowProcessor.doDraw(graphics);
            } else {
               drawLoadingScreen(0);
            }
         }
      } else {
         drawLoadingScreen(0);
      }

      drawActivityAnim();
   }

   public static void drawLoadingScreen(int percentage) {
      if (smLoadingScreen != null) {
         int width = Toolkit.getScreenWidth();
         OGL.setColorRGB(16777215);
         OGL.fillRect(0, 0, width, Toolkit.getScreenHeight());
         smLoadingScreen.draw((width - smLoadingScreen.getWidth()) / 2, 0);
         if (smFonts != null) {
            CollisionBox box = smLoadingScreen.getCollisionBox(1);
            int barWidth = (int)(Toolkit.getScreenWidth() * 0.35F);
            int x = (width - barWidth) / 2;
            int y = box.getY() - box.getHeight();
            smLoadingBar.setAnimationFrame(2);
            IsometricUtils.drawBar(
               smLoadingBar,
               x,
               y,
               smLoadingBarLeftWidth,
               smLoadingBarRightWidth,
               smLoadingBarCenterWidth,
               smLoadingBarCenterX,
               smLoadingBarBoxAreaWidth,
               barWidth,
               barWidth,
               1.0F,
               false,
               true
            );
            smLoadingBar.setAnimationFrame(0);
            IsometricUtils.drawBar(
               smLoadingBar,
               x,
               y,
               smLoadingBarLeftWidth,
               smLoadingBarRightWidth,
               smLoadingBarCenterWidth,
               smLoadingBarCenterX,
               smLoadingBarBoxAreaWidth,
               barWidth,
               Math.min(barWidth * percentage / 100, barWidth),
               1.0F,
               false,
               false
            );
            int count = smLoadingBarTagLine == null ? 0 : smLoadingBarTagLine.length;
            y = box.getY() + (box.getHeight() >> 1) - (count * smFonts[2].getHeight() >> 1);

            for (int index = 0; index < count; index++) {
               smFonts[2].drawString(smLoadingBarTagLine[index], width >> 1, y, 17);
               y += smFonts[2].getHeight();
            }
         }
      }
   }

   @Override
   public void keyEventOccurred(int key, int type) {
      if (!this.mLoading && this.mCurrentFlowProcessor != null) {
         this.mCurrentFlowProcessor.keyEventOccurred(key, type);
      }
   }

   public void keyEventOccurred(int type, int key, int value, char[] characters) {
      if (!this.mLoading && this.mCurrentFlowProcessor != null) {
         this.mCurrentFlowProcessor.keyEventOccurred(type, key, value, characters);
      }
   }

   @Override
   public void pointerEventOccurred(int x, int y, int type) {
      if (!this.mLoading && smActivityState != 0 && this.mCurrentFlowProcessor != null) {
         this.mCurrentFlowProcessor.pointerEventOccurred(x, y, type);
      }
   }

   @Override
   public void touchEventOccurred(int[][] touches, int[] gestures) {
      if (!this.mLoading && smActivityState != 0 && this.mCurrentFlowProcessor != null) {
         this.mCurrentFlowProcessor.touchEventOccurred(touches, gestures);
      }
   }

   public static int getActivityState() {
      return smActivityState;
   }

   @Override
   public void appEventOccurred(int event) {
      if (event == 3) {
         Event.close();
      }

      if (!this.mLoading && this.mCurrentFlowProcessor != null) {
         switch (event) {
            case 0:
            case 3:
               this.mCurrentFlowProcessor.pause();
               break;
            case 1:
               this.mCurrentFlowProcessor.resume();
            case 2:
            case 4:
            case 5:
            case 6:
            default:
               break;
            case 7:
               this.mCurrentFlowProcessor.controllerActivated();
               break;
            case 8:
               this.mCurrentFlowProcessor.memoryWarning();
         }
      }
   }

   @Override
   public void switchFlowProcessor(int id) {
      this.mNextFlowProcessor = this.mGameFlowProcessor;
   }

   @Override
   public void setLanguage(int language) {
      if (this.mInitialized) {
         this.mGameFlowProcessor.languageChanged();
      }

      Toolkit.setSelectedLanguage(language);
   }

   public static void initializeLoadingScreen() {
      smLoadingScreen = new SpriteObject(DavinciUtilities.loadAnimation(DeviceWrapper.isIPHONE4() ? 74735 : 66269, true, 0));
      smLoadingBar = new SpriteObject(DavinciUtilities.loadAnimation(73691, true, 0));
      int[] layout = IsometricUtils.calculateBarLayout(smLoadingBar);
      smLoadingBarBoxAreaWidth = layout[0];
      smLoadingBarCenterX = layout[2];
      smLoadingBarCenterWidth = layout[3];
      smLoadingBarLeftWidth = layout[4];
      smLoadingBarRightWidth = layout[5];
   }

   public static void freeLoadingScreen() {
      if (smLoadingScreen != null) {
         smLoadingScreen.freeResources();
         smLoadingScreen = null;
      }
   }

   public static void drawFreeEnergyText() {
      Toolkit.getScreenWidth();
      Toolkit.getScreenHeight();
      boolean iphone4 = DeviceWrapper.isIPHONE4();
      int y = iphone4 ? 200 : 100;
      int textX = iphone4 ? 260 : 130;
      int buttonY = iphone4 ? 392 : 196;
      int buttonX = iphone4 ? 548 : 274;
      if (DeviceWrapper.isIPAD() || DeviceWrapper.isIPAD1()) {
         y = 250;
         textX = 320;
         buttonY = 452;
         buttonX = 568;
      }

      if (DeviceWrapper.isIPHONE5()) {
         y = 200;
         textX = 360;
         buttonY = 392;
         buttonX = 598;
      }

      int count = smFlurryVideoAdText == null ? 0 : smFlurryVideoAdText.length;

      for (int index = 0; index < count; index++) {
         smFonts[2].drawString(smFlurryVideoAdText[index], textX, y, 0);
         y += smFonts[2].getHeight();
      }

      smFonts[2].drawString(smFlurryVideoAdClickText, buttonX, buttonY, 0);
   }

   public static void setActivityState(int state, boolean instant) {
      smActivityInstant = instant;
      if (smActivityState == -1) {
         smActivityTransition = 0;
         smActivityTimer = instant ? 200 : 0;
         smActivityState = state;
      } else if (state == -1) {
         if (smActivityTimer < 1001) {
            smActivityStopped = true;
            return;
         }

         smActivityTransition = 1;
         smActivityTimer = instant ? 200 : 0;
      }
   }

   public static void loadActivityTexts() {
      smActivityTexts[0] = Toolkit.getText(2447);
      smActivityTexts[1] = Toolkit.getText(2448);
   }

   private void initialize() {
      if (!this.mLoading) {
         this.mMaxLoadingCount = 4;
         this.mLoadingCounter = 0;
         this.mLoading = true;
         OGL.init();
         ToolkitHelpers.init();
         SettingsWrapper.init();
         ActivityWrapper.init();
         this.initializeGraphics();
         initializeLoadingScreen();
         Event.initialize();
      } else if (this.mLoadingCounter >= this.mMaxLoadingCount) {
         this.mInitialized = true;
         this.mLoading = false;
         this.mNextFlowProcessor = this.mGameFlowProcessor;
         this.resetTimer();
      } else {
         switch (this.mLoadingCounter) {
            case 0:
               initializeFonts();
               break;
            case 1:
               this.initializeGameFlow();
            case 2:
            default:
               break;
            case 3:
               this.initializePaymentManager();
         }

         this.mLoadingCounter++;
      }
   }

   private void initializePaymentManager() {
   }

   private void initializeGraphics() {
      DavinciUtilities.initialize();
   }

   public static void initializeFonts() {
      try {
         smFonts = new ImageFont[7];
         int[] resources = new int[]{66335, 66331, 66329, 66337, 74645, 70827, 74683};

         for (int index = 0; index < smFonts.length; index++) {
            int data = 65537 + index * 2 + (DeviceWrapper.useHDGraphics() ? 1 : 0);
            smFonts[index] = new ImageFont(resources[index], Toolkit.getResourceByteArray(data));
         }

         Toolkit.setSoftkeyImageFont(smFonts[0]);
         smLoadingBarTagLine = MenuObject.splitString(Toolkit.getText(1807), smFonts[2], smLoadingScreen.getCollisionBox(1).getWidth());
         String message = "Click Below button to Watch Video ADs. Complete watching you will get free energy as a Gift to your game.";
         smFlurryVideoAdClickText = "Click";
         smFlurryExitButtonText = "Exit";
         smFlurryNoAdsAvail = "No Ads Available";
         int width = !DeviceWrapper.isIPAD() && !DeviceWrapper.isIPAD1() && !DeviceWrapper.isIPHONE4() && !DeviceWrapper.isIPHONE5() ? 300 : 600;
         smFlurryVideoAdText = MenuObject.splitString(message, smFonts[2], width);
         smActivityTexts = new String[2];
         loadActivityTexts();
      } catch (IOException exception) {
         Sonda.fail("DCiDead.initializeFonts", exception);
      }
   }

   private void initializeGameFlow() {
      Game game = Game.getInstance();
      game.init(this);
      this.mGameFlowProcessor = new FlowProcessor(65536, game);
      String model = Ios.platform().deviceModel();
      int x;
      int y;
      if (!"iPad".equals(model) && !"iPadSimulator".equals(model)) {
         float height = Ios.platform().mainScreenHeight();
         x = height == 568.0F ? 125 : 80;
         y = 270;
      } else {
         x = 500;
         y = 40;
      }

      Ios.platform().initializeAds(false, x, y, 1, -1, -1);
   }

   private static void drawActivityAnim() {
      if (smActivityState != -1 || smActivityTransition != -1) {
         float alpha = 1.0F;
         if (smActivityTransition == 0 || smActivityTransition == 1) {
            int time = smActivityTransition == 1 ? 200 - smActivityTimer : smActivityTimer;
            alpha = Math.max(0.0F, Math.min(time / 200.0F, 1.0F));
         }

         int width = Toolkit.getScreenWidth();
         int centerY = Toolkit.getScreenHeight() >> 1;
         int fontHeight = smFonts[2].getHeight();
         int height = fontHeight << 2;
         int top = centerY - (height >> 1);
         OGL.pushParameters();
         OGL.setAlpha(alpha * 0.5F);
         OGL.setColorRGB(0);
         OGL.fillRect(0, top, Toolkit.getScreenWidth(), height);
         OGL.setAlpha(alpha);
         OGL.fillRect(0, top, Toolkit.getScreenWidth(), 3);
         OGL.fillRect(0, centerY + (height >> 1) - 3, Toolkit.getScreenWidth(), 3);
         smFonts[2].drawString(smActivityTexts[smActivityState], width >> 1, centerY - (fontHeight >> 1), 17);
         OGL.popParameters();
      }
   }

   private boolean updateActivityTimer(int delta) {
      if (smActivityState == -1 && smActivityTransition == -1) {
         return false;
      }

      smActivityTimer += delta;
      if (smActivityStopped) {
         if (smActivityTimer > 1000) {
            smActivityTransition = 1;
            smActivityTimer = smActivityInstant ? 200 : 0;
            smActivityStopped = false;
         }

         return true;
      } else {
         if (smActivityTransition == -1) {
            return false;
         }

         if (smActivityTimer > 199) {
            if (smActivityTransition == 1) {
               smActivityState = -1;
            }

            smActivityTransition = -1;
            smActivityTimer = 0;
         }

         return true;
      }
   }

   @Override
   protected void startApp() {
      super.startApp();
   }

   @Override
   public void pauseApp() {
      super.pauseApp();
   }

   @Override
   protected void destroyApp(boolean unconditional) {
      super.destroyApp(unconditional);
   }
}
