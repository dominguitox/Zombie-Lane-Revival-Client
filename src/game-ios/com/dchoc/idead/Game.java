package com.dchoc.idead;

import com.dchoc.DCiDead;
import com.dchoc.idead.tracking.Event;
import com.dchoc.idead.tracking.InstallEvent;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.menuobject.MenuObject;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.IRenderingPlatform;
import com.dchoc.toolkit.Toolkit;
import java.io.EOFException;
import java.util.Collections;
import java.util.Locale;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import zlport.ios.Ios;
import zlport.launcher.LauncherLanguage;

public class Game implements FlowHandler {
   public static final boolean DEBUG_ZOOM = false;
   public static final int LANGUAGE_INDEX_ENGLISH = 0;
   public static final int LANGUAGE_INDEX_FRENCH = 1;
   public static final int LANGUAGE_INDEX_GERMAN = 3;
   public static final int LANGUAGE_INDEX_ITALIAN = 2;
   public static final int LANGUAGE_INDEX_SPANISH = 4;
   private static Game smInstance;
   private static boolean smRunning;
   private ApplicationControl mApplicationControl;
   private GameEngine mGameEngine;
   private int mCurrentMusic = -1;
   private int mCurrentState;
   private int mScreenTimer;
   private int mSelectedLanguage = -1;
   private boolean mSkipSplash;
   private boolean mShowIntro;
   private boolean mPauseEventScheduled;
   private boolean mSoundEffectsOn;
   private boolean mMusicOn;
   private boolean mCropsAndHarvestingOn;
   private boolean mNeighborActionsOn;
   private boolean mEnergyRefillOn;
   private boolean mCrashed;
   private boolean mFadeTransitions;
   int mState;

   private Game() {
   }

   @Override
   public void controllerActivated() {
   }

   public boolean crashedEnabled() {
      return this.mCrashed;
   }

   public boolean cropsAndHarvestingEnabled() {
      return this.mCropsAndHarvestingOn;
   }

   @Override
   public void doDraw(int p0, Graphics p1) {
      if (p0 == 2 && this.mGameEngine != null) {
         this.mGameEngine.doDraw();
      }
   }

   @Override
   public void drawLoadingScreen(int p0) {
      DCiDead.drawLoadingScreen(p0);
   }

   @Override
   public void drawMenu(int p0, Graphics p1, MenuObject p2) {
   }

   @Override
   public void drawPostLoadingScreenTransition(int p0, int p1, int p2, int p3, Graphics p4) {
      int duration = this.getLoadingScreenTransitionDuration(p0, p1, p2);
      this.fadeToColor(16777215, (p0 & 1) != 0 ? duration - p3 : p3, duration, p4);
   }

   @Override
   public void drawPostStateTransition(int p0, int p1, int p2, int p3, Graphics p4, MenuObject p5) {
      int duration = this.getStateTransitionDuration(p0, p1, p2);
      this.fadeToColor(16777215, (p0 & 1) != 0 ? duration - p3 : p3, duration, p4);
   }

   private void dumpMemory() {
   }

   public boolean energyRefillEnabled() {
      return this.mEnergyRefillOn;
   }

   @Override
   public boolean evaluateBranchCondition(int p0) {
      return false;
   }

   @Override
   public void eventOccurred(int p0, int p1) {
   }

   private void fadeToColor(int p0, int p1, int p2, Graphics p3) {
      int width = Toolkit.getScreenWidth();
      int height = Toolkit.getScreenHeight();
      IRenderingPlatform platform = Toolkit.getRenderingPlatform();
      platform.setClip(0, 0, width, height);
      platform.setColorARGB(p0 & 16777215 | p1 * 255 / p2 << 24);
      int tileWidth = (width >> 2) + width % (width >> 2);
      int tileHeight = (height >> 2) + height % (height >> 2);

      for (int y = 0; y < height; y += tileHeight) {
         for (int x = 0; x < width; x += tileWidth) {
            platform.fillRect(x, y, tileWidth, tileHeight);
         }
      }

      platform.setColorARGB(p0 | 0xFF000000);
   }

   @Override
   public int getDavinciLoadingPercentage(int p0) {
      return 75;
   }

   private int getDeviceLangauge() {
      String forced = System.getProperty(LauncherLanguage.PROPERTY);
      int forcedIndex = LauncherLanguage.indexForCode(forced);
      if (forcedIndex >= 0) {
         return forcedIndex;
      }

      String language = Locale.getDefault().getLanguage();

      return switch (language) {
         case "en" -> 0;
         case "de" -> 3;
         case "fr" -> 1;
         case "it" -> 2;
         case "es" -> 4;
         default -> this.mSelectedLanguage;
      };
   }

   public GameEngine getGameEngine() {
      return this.mGameEngine;
   }

   public static Game getInstance() {
      if (smInstance == null) {
         smInstance = new Game();
         smRunning = false;
      }

      return smInstance;
   }

   @Override
   public int getLoadingScreenTransitionDuration(int p0, int p1, int p2) {
      if (p1 != 1 && this.mFadeTransitions) {
         return 400;
      }

      this.mFadeTransitions = false;
      return 0;
   }

   @Override
   public int getStateGroupLoadingCount(int p0) {
      return p0 == 2 ? 1 : (p0 == 0 ? this.mGameEngine.getLoadingCount() : 0);
   }

   @Override
   public int getStateLoadingCount(int p0) {
      return 0;
   }

   @Override
   public int getStateTransitionDuration(int p0, int p1, int p2) {
      if (p1 != 1 && this.mFadeTransitions) {
         return 400;
      }

      this.mFadeTransitions = false;
      return 0;
   }

   public void init(ApplicationControl p0) {
      this.mApplicationControl = p0;
      this.mScreenTimer = 0;
      this.mFadeTransitions = false;
      this.mShowIntro = !DeviceWrapper.isIOS3();
      this.mMusicOn = true;
      this.mSoundEffectsOn = true;
      this.mCropsAndHarvestingOn = true;
      this.mNeighborActionsOn = true;
      this.mEnergyRefillOn = true;
      this.mCrashed = false;
      this.mGameEngine = new GameEngine(this);
      this.loadSettings();
      this.mCurrentMusic = -1;
   }

   @Override
   public boolean isChildGroup(int p0, int p1) {
      return false;
   }

   public static boolean isInstanceCreated() {
      return smInstance != null;
   }

   @Override
   public boolean isMenuNeeded(int p0) {
      return false;
   }

   public static boolean isRunning() {
      return smRunning;
   }

   public boolean isShowIntro() {
      return this.mShowIntro;
   }

   @Override
   public void keyEventOccurred(int p0, int p1, int p2) {
      int action = Toolkit.getToolkitGameAction(p1);
      boolean skip = p2 == 0 && action == 53 || p2 == 3 && Toolkit.getSoftKeyType(p1) == 0;
      if (p0 == 2) {
         this.mGameEngine.keyEventOccurred(2, p1, p2);
      } else if (Integer.compareUnsigned(p0, 2) < 0 && skip) {
         this.mSkipSplash = true;
      }
   }

   @Override
   public void keyEventOccurred(int p0, int p1, int p2, int p3, char[] p4) {
   }

   @Override
   public void licenseManagerActivated() {
   }

   private void loadSettings() {
      if (Toolkit.getToolkitProperty(7) != null) {
         this.mSelectedLanguage = Toolkit.getSelectedLanguageIndex();
      } else if (Toolkit.getLanguageDescriptions().length == 1) {
         this.mSelectedLanguage = 0;
      }

      int language = this.getDeviceLangauge();
      if (language != this.mSelectedLanguage) {
         this.mSelectedLanguage = language;
         this.mApplicationControl.setLanguage(this.mSelectedLanguage);
      }

      DChocByteArray data = Toolkit.readRecordByteArray("settings");
      if (data == null) {
         String uid = Event.getUID();
         Ios.platform().logEvent("APP_INSTALLED", Collections.singletonMap("User_Id", uid));
         new InstallEvent().trackEvent();
         this.saveSettings();
      } else {
         try {
            this.mSelectedLanguage = data.readUnsignedByte();
            if (this.mSelectedLanguage < 0 || this.mSelectedLanguage >= Toolkit.getLanguageDescriptions().length) {
               this.mSelectedLanguage = 0;
            }

            this.mApplicationControl.setLanguage(this.mSelectedLanguage);
            this.mMusicOn = data.readBoolean();
            this.mSoundEffectsOn = data.readBoolean();
            int music = this.mMusicOn ? 5 : 0;
            int effects = this.mSoundEffectsOn ? 5 : 0;
            if (Toolkit.getMusicVolume() != music) {
               Toolkit.setMusicVolume(music);
            }

            if (Toolkit.getSoundEffectVolume() != effects) {
               Toolkit.setSoundEffectVolume(effects);
            }

            this.mShowIntro = data.readBoolean();
            this.mCropsAndHarvestingOn = data.readBoolean();
            this.mNeighborActionsOn = data.readBoolean();
            this.mEnergyRefillOn = data.readBoolean();
            this.mCrashed = data.readBoolean();
         } catch (EOFException exception) {
            System.err.println(exception);
         }
      }
   }

   @Override
   public void loadState(int p0, int p1) {
   }

   @Override
   public void loadStateGroup(int p0, int p1) {
      if (p0 == 0 && this.mGameEngine.getState() == 0) {
         this.mGameEngine.load(p1);
      }
   }

   @Override
   public int logicUpdate(int p0, int p1) {
      smRunning = true;
      this.mScreenTimer += p1;
      this.dumpMemory();
      if (this.mPauseEventScheduled) {
         this.mPauseEventScheduled = false;
      }

      if (Integer.compareUnsigned(p0, 1) <= 0) {
         return -2;
      } else {
         return p0 == 2 ? this.mGameEngine.logicUpdate(p1) : -1;
      }
   }

   @Override
   public void memoryWarning() {
      if (this.mGameEngine != null) {
         this.mGameEngine.memoryWarning();
      }
   }

   @Override
   public void menuSetInputItem(int p0, MenuObject p1, int p2, int p3, String p4, String p5, SpriteObject p6, Image[] p7, int p8, int p9) {
   }

   @Override
   public void menuSetItem(int p0, MenuObject p1, int p2, int p3, String p4, SpriteObject p5, Image[] p6, int p7) {
   }

   @Override
   public void menuSetScreen(int p0, MenuObject p1, int p2, int p3, int p4) {
   }

   @Override
   public void menuSetSize(int p0, MenuObject p1) {
   }

   @Override
   public void menuSetSliderItem(int p0, MenuObject p1, int p2, String p3, SpriteObject p4, Image[] p5, int p6, int p7, int p8) {
   }

   @Override
   public void menuSetSoftkey(int p0, MenuObject p1, int p2, int p3) {
   }

   @Override
   public void menuSetStyle(int p0, MenuObject p1, int p2) {
   }

   @Override
   public void menuSetSwitchItem(int p0, MenuObject p1, int p2, String p3, SpriteObject p4, Image[] p5, String[] p6, SpriteObject p7, Image[] p8, int p9) {
   }

   @Override
   public void menuSetTitleBar(int p0, MenuObject p1, String p2, Image p3, int p4) {
   }

   @Override
   public void menuSetTitleBarDvc(int p0, MenuObject p1, String p2, SpriteObject p3, int p4) {
   }

   public boolean musicEnabled() {
      return this.mMusicOn;
   }

   public boolean neighborActionsEnabled() {
      return this.mNeighborActionsOn;
   }

   @Override
   public void pauseGame() {
      this.mPauseEventScheduled = true;
      this.mCurrentMusic = -1;
      if (this.mGameEngine != null) {
         this.mGameEngine.pauseGame();
      }
   }

   @Override
   public void pointerEventOccurred(int p0, int p1, int p2, int p3) {
      if (Integer.compareUnsigned(p0, 2) < 0 && p3 == 0) {
         this.mSkipSplash = true;
      }
   }

   @Override
   public void processMenu(int p0, MenuObject p1, int p2) {
   }

   @Override
   public String processText(int p0, int p1, int p2) {
      String text = Toolkit.getText(p1);
      return text != null && text.length() != 0 ? text : null;
   }

   @Override
   public void resumeGame() {
      if (this.mGameEngine != null) {
         this.mGameEngine.resumeGame();
      }
   }

   private void saveSettings() {
      DChocByteArray data = DChocByteArray.createByteArrayForWriting();
      data.writeByte(this.mSelectedLanguage);
      data.writeBoolean(this.mMusicOn);
      data.writeBoolean(this.mSoundEffectsOn);
      int music = this.mMusicOn ? 5 : 0;
      int effects = this.mSoundEffectsOn ? 5 : 0;
      if (Toolkit.getMusicVolume() != music) {
         Toolkit.setMusicVolume(music);
      }

      if (Toolkit.getSoundEffectVolume() != effects) {
         Toolkit.setSoundEffectVolume(effects);
      }

      data.writeBoolean(this.mShowIntro);
      data.writeBoolean(this.mCropsAndHarvestingOn);
      data.writeBoolean(this.mNeighborActionsOn);
      data.writeBoolean(this.mEnergyRefillOn);
      data.writeBoolean(this.mCrashed);
      Toolkit.writeRecordByteArray("settings", data, 1);
   }

   @Override
   public void screenSizeChanged() {
   }

   public void setCrashed(boolean p0) {
      this.mCrashed = p0;
      this.saveSettings();
   }

   public void setCropsAndHarvesting(boolean p0) {
      this.mCropsAndHarvestingOn = p0;
      this.saveSettings();
   }

   public void setEnergyRefill(boolean p0) {
      this.mEnergyRefillOn = p0;
      this.saveSettings();
   }

   public void setLanguage(int p0) {
      this.mSelectedLanguage = p0;
      if (Toolkit.getSelectedLanguageIndex() != p0) {
         this.mApplicationControl.setLanguage(this.mSelectedLanguage);
         this.saveSettings();
      }

      DCiDead.loadActivityTexts();
   }

   @Override
   public void setLoading(int p0, boolean p1) {
      if (p1) {
         Toolkit.stopMusic();
         Toolkit.stopSoundEffect();
         this.mCurrentMusic = -1;
      }
   }

   @Override
   public void setLoadingScreenTransition(int p0, boolean p1) {
   }

   public void setMusic(boolean p0) {
      this.mMusicOn = p0;
      this.saveSettings();
      iWrapper.switchMusic(GameEngineUtils.getAreaMusic());
   }

   public void setNeighborActions(boolean p0) {
      this.mNeighborActionsOn = p0;
      this.saveSettings();
   }

   public void setShowIntro(boolean p0) {
      this.mShowIntro = p0;
      this.saveSettings();
   }

   public void setSoundEffects(boolean p0) {
      this.mSoundEffectsOn = p0;
      this.saveSettings();
   }

   private void setSoundsEnabled(boolean p0) {
      int volume = Toolkit.getMusicVolume();
      volume = p0 ? (volume == 0 ? 3 : volume) : 0;
      Toolkit.setSoundEffectVolume(volume);
      Toolkit.setMusicVolume(volume);
   }

   @Override
   public void setStateTransition(int p0, boolean p1) {
   }

   @Override
   public boolean shouldStoreStateToHistory(int p0) {
      return Integer.compareUnsigned(p0, 1) > 0;
   }

   public boolean soundEffectsEnabled() {
      return this.mSoundEffectsOn;
   }

   @Override
   public void switchState(int p0, int p1, MenuObject p2) {
      this.updateMusic(p1, false);
      this.mCurrentState = p1;
   }

   @Override
   public void touchEventOccurred(int p0, int[][] p1, int[] p2) {
      if (p0 == 2 && this.mGameEngine != null) {
         this.mGameEngine.touchEventOccurred(p1, p2);
      }
   }

   @Override
   public void unloadState(int p0, int p1) {
   }

   @Override
   public void unloadStateGroup(int p0, int p1) {
   }

   @Override
   public void updateLoadingScreenTransition(int p0, int p1, int p2, int p3) {
   }

   private void updateMusic(int p0, boolean p1) {
   }

   @Override
   public void updateStateTransition(int p0, int p1, int p2, int p3, MenuObject p4) {
   }
}
