package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButtonOnOff;
import com.dchoc.hud.HUDImage;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.Game;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.playhaven.PlayHaven;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.Toolkit;

public class WindowSettings extends Window {
   private static final String[] BUTTON_NAMES = new String[]{
      "close", "music", "sfx", "notifications1", "notifications2", "notifications3", "en", "fre", "it", "de", "es"
   };
   private HUDAutoTextField mTextTitle;
   private HUDAutoTextField mTextMusic;
   private HUDAutoTextField mTextSounds;
   private HUDAutoTextField mTextLanguages;
   private HUDAutoTextField mTextNotifications;
   private HUDAutoTextField mTextNotifications1;
   private HUDAutoTextField mTextNotifications2;
   private HUDAutoTextField mTextNotifications3;
   private HUDAutoTextField mTextEnglish;
   private HUDAutoTextField mTextFrench;
   private HUDAutoTextField mTextItalian;
   private HUDAutoTextField mTextGerman;
   private HUDAutoTextField mTextSpanish;
   private RoundRectangle mRectangle;
   private HUDImage mImageMusic;
   private HUDImage mImageSounds;
   private HUDImage mImageLanguages;
   private HUDImage mImageNotifications;
   private HUDButtonOnOff mButtonMusic;
   private HUDButtonOnOff mButtonSounds;
   private HUDButtonOnOff mButtonNotifications1;
   private HUDButtonOnOff mButtonNotifications2;
   private HUDButtonOnOff mButtonNotifications3;
   private HUDButtonOnOff mButtonEnglish;
   private HUDButtonOnOff mButtonFrench;
   private HUDButtonOnOff mButtonItalian;
   private HUDButtonOnOff mButtonGerman;
   private HUDButtonOnOff mButtonSpanish;
   private int mUpdateCounter;
   private boolean mRestartMusic;

   public WindowSettings() {
      this.mBackground = AnimationsManager.loadShared(74619);
      this.mCollisions = AnimationsManager.loadShared(74669);
      this.initCollisions();
      this.constructCloseButton(0);
      this.mObjects.addObject(this.mButtonClose);
      CollisionBox box = this.mCollisions.getCollisionBox(2);
      this.mRectangle = new RoundRectangle(0);
      RoundRectangle rectangle = this.mRectangle;
      rectangle.setSizes(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mObjects.addObject(this.mRectangle);
      box = this.mCollisions.getCollisionBox(10);
      this.mImageMusic = new HUDImage(74699, box.getX(), box.getY());
      this.mObjects.addObject(this.mImageMusic);
      box = this.mCollisions.getCollisionBox(11);
      this.mImageSounds = new HUDImage(74685, box.getX(), box.getY());
      this.mObjects.addObject(this.mImageSounds);
      box = this.mCollisions.getCollisionBox(12);
      this.mImageLanguages = new HUDImage(74689, box.getX(), box.getY());
      this.mObjects.addObject(this.mImageLanguages);
      box = this.mCollisions.getCollisionBox(13);
      this.mImageNotifications = new HUDImage(74697, box.getX(), box.getY());
      this.mObjects.addObject(this.mImageNotifications);
      box = this.mCollisions.getCollisionBox(1);
      this.mTextTitle = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextTitle.setCentered(true, true);
      HUDAutoTextField text = this.mTextTitle;
      text.setText(116, DCiDead.getFont(5));
      this.mObjects.addObject(this.mTextTitle);
      box = this.mCollisions.getCollisionBox(20);
      this.mTextMusic = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextMusic.setCentered(false, true);
      text = this.mTextMusic;
      text.setText(2330, DCiDead.getFont(4));
      this.mObjects.addObject(this.mTextMusic);
      box = this.mCollisions.getCollisionBox(21);
      this.mTextSounds = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextSounds.setCentered(false, true);
      text = this.mTextSounds;
      text.setText(2331, DCiDead.getFont(4));
      this.mObjects.addObject(this.mTextSounds);
      box = this.mCollisions.getCollisionBox(22);
      this.mTextLanguages = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextLanguages.setCentered(false, true);
      text = this.mTextLanguages;
      text.setText(2332, DCiDead.getFont(4));
      this.mObjects.addObject(this.mTextLanguages);
      box = this.mCollisions.getCollisionBox(23);
      this.mTextNotifications = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextNotifications.setCentered(false, true);
      text = this.mTextNotifications;
      text.setText(2338, DCiDead.getFont(4));
      this.mObjects.addObject(this.mTextNotifications);
      box = this.mCollisions.getCollisionBox(91);
      this.mTextNotifications1 = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextNotifications1.setCentered(true, true);
      text = this.mTextNotifications1;
      text.setText(2339, DCiDead.getFont(3));
      this.mObjects.addObject(this.mTextNotifications1);
      box = this.mCollisions.getCollisionBox(92);
      this.mTextNotifications2 = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextNotifications2.setCentered(true, true);
      text = this.mTextNotifications2;
      text.setText(2340, DCiDead.getFont(3));
      this.mObjects.addObject(this.mTextNotifications2);
      box = this.mCollisions.getCollisionBox(93);
      this.mTextNotifications3 = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextNotifications3.setCentered(true, true);
      text = this.mTextNotifications3;
      text.setText(2341, DCiDead.getFont(3));
      this.mObjects.addObject(this.mTextNotifications3);
      box = this.mCollisions.getCollisionBox(61);
      this.mTextEnglish = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextEnglish.setCentered(true, true);
      text = this.mTextEnglish;
      text.setText(2333, DCiDead.getFont(3));
      this.mObjects.addObject(this.mTextEnglish);
      box = this.mCollisions.getCollisionBox(62);
      this.mTextFrench = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextFrench.setCentered(true, true);
      text = this.mTextFrench;
      text.setText(2334, DCiDead.getFont(3));
      this.mObjects.addObject(this.mTextFrench);
      box = this.mCollisions.getCollisionBox(63);
      this.mTextItalian = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextItalian.setCentered(true, true);
      text = this.mTextItalian;
      text.setText(2335, DCiDead.getFont(3));
      this.mObjects.addObject(this.mTextItalian);
      box = this.mCollisions.getCollisionBox(64);
      this.mTextGerman = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextGerman.setCentered(true, true);
      text = this.mTextGerman;
      text.setText(2336, DCiDead.getFont(3));
      this.mObjects.addObject(this.mTextGerman);
      box = this.mCollisions.getCollisionBox(65);
      this.mTextSpanish = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextSpanish.setCentered(true, true);
      text = this.mTextSpanish;
      text.setText(2337, DCiDead.getFont(3));
      this.mObjects.addObject(this.mTextSpanish);
      box = this.mCollisions.getCollisionBox(30);
      this.mButtonMusic = new HUDButtonOnOff(1, box.getX(), box.getY(), 74687, true);
      this.mObjects.addObject(this.mButtonMusic);
      this.mButtonMusic.setTrackingEvent(BUTTON_NAMES[1]);
      box = this.mCollisions.getCollisionBox(31);
      this.mButtonSounds = new HUDButtonOnOff(2, box.getX(), box.getY(), 74687, true);
      this.mObjects.addObject(this.mButtonSounds);
      this.mButtonSounds.setTrackingEvent(BUTTON_NAMES[2]);
      box = this.mCollisions.getCollisionBox(81);
      this.mButtonNotifications1 = new HUDButtonOnOff(3, box.getX(), box.getY(), 74687, true);
      this.mObjects.addObject(this.mButtonNotifications1);
      this.mButtonNotifications1.setTrackingEvent(BUTTON_NAMES[3]);
      box = this.mCollisions.getCollisionBox(82);
      this.mButtonNotifications2 = new HUDButtonOnOff(4, box.getX(), box.getY(), 74687, true);
      this.mObjects.addObject(this.mButtonNotifications2);
      this.mButtonNotifications2.setTrackingEvent(BUTTON_NAMES[4]);
      box = this.mCollisions.getCollisionBox(83);
      this.mButtonNotifications3 = new HUDButtonOnOff(5, box.getX(), box.getY(), 74687, true);
      this.mObjects.addObject(this.mButtonNotifications3);
      this.mButtonNotifications3.setTrackingEvent(BUTTON_NAMES[5]);
      box = this.mCollisions.getCollisionBox(51);
      this.mButtonEnglish = new HUDButtonOnOff(6, box.getX(), box.getY(), 74693, false);
      this.mButtonEnglish.setState(0);
      this.mObjects.addObject(this.mButtonEnglish);
      this.mButtonEnglish.setTrackingEvent(BUTTON_NAMES[6]);
      box = this.mCollisions.getCollisionBox(52);
      this.mButtonFrench = new HUDButtonOnOff(7, box.getX(), box.getY(), 74691, false);
      this.mButtonFrench.setState(1);
      this.mObjects.addObject(this.mButtonFrench);
      this.mButtonFrench.setTrackingEvent(BUTTON_NAMES[7]);
      box = this.mCollisions.getCollisionBox(53);
      this.mButtonItalian = new HUDButtonOnOff(8, box.getX(), box.getY(), 74701, false);
      this.mButtonItalian.setState(1);
      this.mObjects.addObject(this.mButtonItalian);
      this.mButtonItalian.setTrackingEvent(BUTTON_NAMES[8]);
      box = this.mCollisions.getCollisionBox(54);
      this.mButtonGerman = new HUDButtonOnOff(9, box.getX(), box.getY(), 74695, false);
      this.mButtonGerman.setState(1);
      this.mObjects.addObject(this.mButtonGerman);
      this.mButtonGerman.setTrackingEvent(BUTTON_NAMES[9]);
      box = this.mCollisions.getCollisionBox(55);
      this.mButtonSpanish = new HUDButtonOnOff(10, box.getX(), box.getY(), 74703, false);
      this.mButtonSpanish.setState(1);
      this.mObjects.addObject(this.mButtonSpanish);
      this.mButtonSpanish.setTrackingEvent(BUTTON_NAMES[10]);
      this.mRestartMusic = false;
      this.mUpdateCounter = 0;
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      if (this.mRestartMusic) {
         this.mUpdateCounter++;
         if (this.mUpdateCounter < 3) {
            return;
         }

         iWrapper.switchMusic(GameEngineUtils.getAreaMusic());
         this.mRestartMusic = false;
      }
   }

   @Override
   public void open() {
      super.open();
      this.updateSettings();
   }

   @Override
   public void callback(int id) {
      Game game = Game.getInstance();
      switch (id) {
         case 0:
            WindowManager.closeWindow();
            break;
         case 1:
            game.setMusic(this.mButtonMusic.getState() == 0);
            break;
         case 2:
            game.setSoundEffects(this.mButtonSounds.getState() == 0);
            break;
         case 3:
            game.setCropsAndHarvesting(this.mButtonNotifications1.getState() == 0);
            break;
         case 4:
            game.setNeighborActions(this.mButtonNotifications2.getState() == 0);
            GameEngine.getInstance().setSetOption();
            break;
         case 5:
            game.setEnergyRefill(this.mButtonNotifications3.getState() == 0);
            break;
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
            game.setLanguage(id - 6);
            IsometricUtils.getScene().getFloor().updateEdges();
            this.mRestartMusic = true;
            this.mUpdateCounter = 0;
      }

      this.updateSettings();
   }

   @Override
   public void setState(byte state) {
      super.setState(state);
      if (state == 1) {
         PlayHaven.triggerCloseSettings();
      } else if (state == 0) {
         PlayHaven.triggerOpenSettings();
      }
   }

   public void updateSettings() {
      Game game = Game.getInstance();
      HUDButtonOnOff button = this.mButtonMusic;
      button.setStatus(game.musicEnabled());
      button = this.mButtonSounds;
      button.setStatus(game.soundEffectsEnabled());
      button = this.mButtonNotifications1;
      button.setStatus(game.cropsAndHarvestingEnabled());
      button = this.mButtonNotifications2;
      button.setStatus(game.neighborActionsEnabled());
      button = this.mButtonNotifications3;
      button.setStatus(game.energyRefillEnabled());
      int language = Toolkit.getSelectedLanguageIndex();
      this.mButtonEnglish.setStatus(language == 0);
      this.mButtonFrench.setStatus(language == 1);
      this.mButtonItalian.setStatus(language == 2);
      this.mButtonGerman.setStatus(language == 3);
      this.mButtonSpanish.setStatus(language == 4);
   }
}
