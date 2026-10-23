package com.dchoc.hud;

import com.dchoc.idead.GameEngine;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.servlets.CreateNewSession;
import com.dchoc.opengl.OGL;
import com.dchoc.playhaven.PlayHaven;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.WindowSocial;
import zlport.ios.Ios;

public class HUDFoldable extends HUDCollection {
   public static final int STATE_CLOSED = 0;
   public static final int STATE_OPENING = 1;
   public static final int STATE_OPENED = 2;
   public static final int STATE_CLOSING = 3;
   public static String[] BUTTON_NAMES = new String[]{"Market", "Inventory", "Customize", "Social", "Edit", "Settings", "Menu_Open", "Cancel", "Menu_Close"};
   private SpriteObject mLayout;
   private SpriteObject mOpenAnimation;
   private SpriteObject mCloseAnimation;
   private int mTimer;
   private int mState;
   private HUDButton mButtonMarket;
   private HUDButton mButtonInventory;
   private HUDButton mButtonCustomize;
   private HUDButton mButtonSocial;
   private HUDButton mButtonEdit;
   private HUDButton mButtonSettings;
   private HUDButton mButtonMenu;
   private HUDButton mButtonCancel;
   private HUDButtonWithOrangeLabel mButtonHome;
   private HUDImage mMenuBackground;
   private HUDImage mAdventureBackground;
   private HUDRedCounter mTipCounter;
   private HUDRedCounter mSocialCounter;
   private HUDRedCounter mMarketCounter;
   private int mOffsetX;
   private HUDObject[] mFoldables;

   protected HUDFoldable() {
      int width = Toolkit.getScreenWidth();
      int height = Toolkit.getScreenHeight();
      this.mLayout = AnimationsManager.load(74545);
      this.mOpenAnimation = AnimationsManager.load(74527);
      this.mCloseAnimation = AnimationsManager.load(74581);
      this.mFoldables = new HUDObject[9];
      HUDButton button = new HUDButton(0, 0, 0, 71023, (byte)7);
      this.mButtonMarket = button;
      button.setClipping(false);
      this.addObject(this.mButtonMarket);
      this.mButtonMarket.setTrackingEvent(BUTTON_NAMES[0]);
      button = new HUDButton(1, 0, 0, 71301, (byte)8);
      this.mButtonInventory = button;
      button.setClipping(false);
      this.addObject(this.mButtonInventory);
      this.mButtonInventory.setTrackingEvent(BUTTON_NAMES[1]);
      button = new HUDButton(2, 0, 0, 73951, (byte)22);
      this.mButtonCustomize = button;
      button.setClipping(false);
      this.addObject(this.mButtonCustomize);
      this.mButtonCustomize.setTrackingEvent(BUTTON_NAMES[2]);
      button = new HUDButton(3, 0, 0, 71019, (byte)10);
      this.mButtonSocial = button;
      button.setClipping(false);
      this.addObject(this.mButtonSocial);
      this.mButtonSocial.setTrackingEvent(BUTTON_NAMES[3]);
      button = new HUDButton(4, 0, 0, 71021, (byte)9);
      this.mButtonEdit = button;
      button.setClipping(false);
      this.addObject(this.mButtonEdit);
      this.mButtonEdit.setTrackingEvent(BUTTON_NAMES[4]);
      button = new HUDButton(5, 0, 0, 74587, (byte)11);
      this.mButtonSettings = button;
      button.setClipping(false);
      this.addObject(this.mButtonSettings);
      this.mButtonSettings.setTrackingEvent(BUTTON_NAMES[5]);
      this.mTipCounter = new HUDRedCounter();
      this.addObject(this.mTipCounter);
      this.mSocialCounter = new HUDRedCounter();
      this.addObject(this.mSocialCounter);
      this.mMarketCounter = new HUDRedCounter();
      this.addObject(this.mMarketCounter);
      this.mMenuBackground = new HUDImage(74519, width, height);
      this.addObject(this.mMenuBackground);
      this.mAdventureBackground = new HUDImage(78983, width, height);
      this.addObject(this.mAdventureBackground);
      CollisionBox box = this.mMenuBackground.getCollisionBox(1);
      this.mButtonMenu = new HUDButton(6, box.getX() + width, box.getY() + height, 74601, (byte)-1);
      this.addObject(this.mButtonMenu);
      this.mButtonMenu.setTrackingEvent(BUTTON_NAMES[6]);
      box = this.mMenuBackground.getCollisionBox(0);
      this.mButtonCancel = new HUDButton(7, box.getX() + width, box.getY() + height, 71017, (byte)12);
      this.addObject(this.mButtonCancel);
      this.mButtonCancel.setTrackingEvent(BUTTON_NAMES[7]);
      box = this.mAdventureBackground.getCollisionBox(2);
      this.mButtonHome = new HUDButtonWithOrangeLabel(8, box.getX() + width, box.getY() + height, 71025, (byte)-1);
      this.addObject(this.mButtonHome);
      this.setFoldable(
         this.mButtonMarket,
         this.mButtonInventory,
         this.mButtonCustomize,
         this.mButtonSocial,
         this.mButtonEdit,
         this.mButtonSettings,
         this.mTipCounter,
         this.mSocialCounter,
         this.mMarketCounter
      );
      this.mOffsetX = 0;
      this.changeState(0);
   }

   public void setAsHome() {
      this.mMenuBackground.setVisible(true);
      this.mAdventureBackground.setVisible(false);
      this.mButtonHome.setVisible(false);
      this.mButtonCustomize.setEnabled(true);
      this.mButtonEdit.setEnabled(true);
   }

   public void setAsAdventureArea() {
      this.mMenuBackground.setVisible(false);
      this.mAdventureBackground.setVisible(true);
      this.mButtonHome.setVisible(true);
      this.mButtonCustomize.setEnabled(false);
      this.mButtonEdit.setEnabled(false);
   }

   public void changeState(int state) {
      this.setFoldableVisible(state != 0);
      this.mTimer = 0;
      this.mState = state;
      if (state == 2) {
         PlayHaven.triggerOpenMenu();
      }
   }

   public int getState() {
      return this.mState;
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      int width = Toolkit.getScreenWidth();
      int height = Toolkit.getScreenHeight();
      int timer = this.mTimer;
      this.mTimer = timer + delta;
      if (this.mState == 1) {
         if (timer + delta > this.mOpenAnimation.getAnimationLength()) {
            this.changeState(2);
         }
      } else if (this.mState == 3) {
         int elapsed = this.mTimer;
         if (this.mCloseAnimation.getAnimationLength() < elapsed) {
            this.changeState(0);
         }
      }

      int offset;
      if (this.mState == 3) {
         offset = this.mCloseAnimation.getCurrentAnimationData().getTimelineValue(1, this.mTimer, false);
      } else if (this.mState == 1) {
         offset = this.mOpenAnimation.getCurrentAnimationData().getTimelineValue(1, this.mTimer, false);
      } else {
         offset = 0;
      }

      this.mOffsetX = offset;
      int i = 0;

      do {
         if (this.mFoldables[i] != null) {
            CollisionBox box = this.mLayout.getCollisionBox(i);
            int x = box.getX();
            int y = box.getY();
            this.mFoldables[i].setPosition(width + offset + x, y + height);
         }
      } while (++i != 9);

      if (this.mState != 0) {
         WindowSocial social = (WindowSocial)WindowManager.getWindow((byte)18);
         HUDRedCounter counter = this.mSocialCounter;
         counter.setValue(social.getNewMessageCount());
         this.mMarketCounter.setText(1668);
      }
   }

   public void increaseTipCounter() {
      this.mTipCounter.incrementValue();
   }

   public void resetTipCounter() {
      this.mTipCounter.setValue(0);
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         int width = Toolkit.getScreenWidth();
         int height = Toolkit.getScreenHeight();
         int clipX = OGL.getClipX();
         int clipY = OGL.getClipY();
         int clipWidth = OGL.getClipWidth();
         int clipHeight = OGL.getClipHeight();
         SpriteObject layout = this.mLayout;
         if (layout != null && this.mState != 0) {
            int pivotX = layout.getPivotX();
            int pivotY = this.mLayout.getPivotY();
            int layoutWidth = this.mLayout.getWidth();
            int layoutHeight = this.mLayout.getHeight();
            OGL.setClip(width - pivotX, height - pivotY, layoutWidth, layoutHeight);
            SpriteObject drawn = this.mLayout;
            int screenWidth = Toolkit.getScreenWidth();
            int offset = this.mOffsetX;
            drawn.draw(offset + screenWidth, Toolkit.getScreenHeight());
         }

         if (0 < this.mObjects.size()) {
            int i = 0;

            do {
               HUDObject object = (HUDObject)this.mObjects.elementAt(i);
               if (object.isVisible()) {
                  int x = clipX;
                  int y = clipY;
                  int w = clipWidth;
                  int h = clipHeight;
                  if (this.isFoldable(object)) {
                     int pivotX = this.mLayout.getPivotX();
                     int pivotY = this.mLayout.getPivotY();
                     w = this.mLayout.getWidth();
                     h = this.mLayout.getHeight();
                     x = width - pivotX;
                     y = height - pivotY;
                  }

                  OGL.setClip(x, y, w, h);
                  object.draw();
               }
            } while (++i < this.mObjects.size());
         }

         OGL.setClip(clipX, clipY, clipWidth, clipHeight);
      }
   }

   @Override
   public void callback(int id) {
      switch (id) {
         case 0:
            WindowManager.openWindow((byte)22);
            this.removeObject(this.mMarketCounter);
            break;
         case 1:
            WindowManager.openWindow((byte)20);
            break;
         case 2:
            WindowManager.openWindow((byte)25);
            break;
         case 3:
            if (!CreateNewSession.showOfflineMode()) {
               WindowManager.openWindow((byte)18);
               return;
            }

            WindowManager.openNPCDialog((byte)3);
            break;
         case 4:
            HUD.changeState(6);
            Ios.platform().showAd(0);
            break;
         case 5:
            WindowManager.openWindow((byte)21);
            break;
         case 6:
            if (this.mState == 0) {
               this.changeState(1);
               return;
            }

            if (this.mState == 2) {
               this.changeState(3);
            }
            break;
         case 7:
            this.cancelPlayerActions();
            break;
         case 8:
            GameEngine.getInstance().travelToHome();
      }
   }

   public boolean isOpened() {
      return this.mState == 2;
   }

   public HUDButton getInventoryButton() {
      return this.mButtonInventory;
   }

   public HUDButton getMenuButton() {
      return this.mButtonMenu;
   }

   public void setFoldable(
      HUDButton market,
      HUDButton inventory,
      HUDButton customize,
      HUDButton social,
      HUDButton edit,
      HUDButton settings,
      HUDRedCounter tips,
      HUDRedCounter socialCounter,
      HUDRedCounter marketCounter
   ) {
      this.mFoldables[0] = market;
      this.mFoldables[1] = inventory;
      this.mFoldables[2] = customize;
      this.mFoldables[3] = social;
      this.mFoldables[4] = edit;
      this.mFoldables[5] = settings;
      this.mFoldables[6] = tips;
      this.mFoldables[7] = socialCounter;
      this.mFoldables[8] = marketCounter;
   }

   public void setFoldableVisible(boolean visible) {
      for (int i = 0; i < 9; i++) {
         HUDObject object = this.mFoldables[i];
         if (object != null) {
            object.setVisible(visible);
         }
      }
   }

   public boolean isFoldable(HUDObject object) {
      for (int i = 0; i <= 8; i++) {
         if (this.mFoldables[i] == object) {
            return true;
         }
      }

      return false;
   }

   public void cancelPlayerActions() {
      PlayerCharacter player = IsometricUtils.getPlayer();
      if (player != null) {
         player.cancelActions(true);
      }
   }
}
