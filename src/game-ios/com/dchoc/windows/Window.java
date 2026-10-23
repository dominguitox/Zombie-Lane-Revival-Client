package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDCollection;
import com.dchoc.hud.HUDObject;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tracking.WindowEvent;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.Animation;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;
import zlport.ios.Ios;

public abstract class Window extends HUDObject {
   private static final int ALPHA_MAX = 128;
   public static final String DEFAULT_AREA = "main_view";
   public static final int STATE_OPENED = 0;
   public static final int STATE_CLOSED = 1;
   public static final int STATE_OPENING = 2;
   public static final int STATE_CLOSING = 3;
   public static final int STATE_WAITING = 4;
   protected boolean mFadeBackground = true;
   protected boolean mIsModal = true;
   protected byte mState = 1;
   protected SpriteObject mCollisions;
   protected SpriteObject mBackground;
   protected SpriteObject mAnimationIn;
   protected SpriteObject mAnimationOut;
   protected HUDButton mButtonClose;
   protected HUDButton mButtonImpactVideo;
   protected HUDCollection mObjects = new HUDCollection();
   protected int mTooltipScaleTimer;
   private boolean mEnableFadeBackground;
   private int mCurrentPosX;
   private int mCurrentPosY;
   protected int mMarketMode;
   private boolean mIsTooltipBubble;
   private boolean mPlayTooltipSound;
   private WindowEvent mWindowEvent;

   public Window() {
      super((byte)1);
      this.mObjects.setParent(this);
      this.mAnimationIn = AnimationsManager.load(66771);
      this.mAnimationOut = AnimationsManager.load(66759);
      this.mAnimationIn.setAnimation(0, 1, false);
      this.mAnimationOut.setAnimation(0, 1, false);
      this.mEnableFadeBackground = true;
      this.mWindowEvent = new WindowEvent();
   }

   public boolean isTooltipBubble() {
      return this.mIsTooltipBubble;
   }

   public boolean isFullscreen() {
      boolean fullscreen = false;
      if (this.mState == 0) {
         SpriteObject background = this.mBackground;
         if (background != AnimationsManager.loadShared(74619)) {
            return false;
         }

         fullscreen = !DeviceWrapper.isIPAD();
      }

      return fullscreen;
   }

   public boolean isModal() {
      return this.mIsModal;
   }

   public boolean isClosed() {
      byte state = this.mState;
      return !this.mIsTooltipBubble ? state == 1 : state == 4 || state == 1;
   }

   public boolean isClosing() {
      return this.mState == 3;
   }

   public boolean isOpened() {
      return this.mState == 0;
   }

   public boolean isOpening() {
      return this.mState == 2;
   }

   public void setMarketMode(int mode) {
      this.mMarketMode = mode;
   }

   @Override
   public void draw() {
      if (this.mState != 1 && this.mState != 4) {
         int clipX = OGL.getClipX();
         int clipY = OGL.getClipY();
         int clipWidth = OGL.getClipWidth();
         int clipHeight = OGL.getClipHeight();
         OGL.setClip(0, 0, Toolkit.getScreenWidth(), Toolkit.getScreenHeight());
         if (this.mEnableFadeBackground) {
            this.fadeBackground();
         }

         if (this.mBackground != null) {
            this.mBackground.draw(this.mCurrentPosX + (this.mWidth >> 1), this.mCurrentPosY + (this.mHeight >> 1));
         }

         if (this.mMarketMode == 8 && Ios.platform().isImpactSupported()) {
            this.mObjects.setObjectVisible(56000, true);
         } else {
            this.mObjects.setObjectVisible(56000, false);
         }

         this.mObjects.draw();
         if (this.mMarketMode == 8 && Ios.platform().isImpactSupported()) {
            DCiDead.drawFreeEnergyText();
         }

         OGL.setClip(clipX, clipY, clipWidth, clipHeight);
      }
   }

   @Override
   public void freeImages() {
      this.mObjects.freeImages();
   }

   public void drawImageCentered(SpriteObject sprite, CollisionBox box) {
      int x = this.getX();
      int boxX = box.getX();
      int boxWidth = box.getWidth();
      int y = this.getY();
      int boxY = box.getY();
      int boxHeight = box.getHeight();
      sprite.draw(boxX + x + boxWidth / 2, y + boxY + boxHeight / 2);
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.mState == 0) {
         this.mObjects.pointerEvent(event);
      }

      event.setConsumed(this.mIsModal);
   }

   public void open() {
      if (this.mIsTooltipBubble) {
         this.mPlayTooltipSound = true;
      }

      this.setState((byte)2);
      if (!this.mIsTooltipBubble) {
         WindowEvent event = this.mWindowEvent;
         if (event == null) {
            return;
         }

         event.trackShown(this.getWindowName());
      }
   }

   public void openForced() {
      this.setState((byte)0);
   }

   public void close() {
      this.setState((byte)3);
      if (!this.mIsTooltipBubble) {
         WindowEvent event = this.mWindowEvent;
         if (event != null) {
            event.trackClose(this.getWindowName());
         }
      }
   }

   public void closeForced() {
      this.setState((byte)1);
   }

   @Override
   public void logicUpdate(int delta) {
      byte state = this.mState;
      if (state == 3) {
         if (!this.mIsTooltipBubble) {
            this.mAnimationOut.logicUpdate(delta);
            Animation animation = this.mAnimationOut.getCurrentAnimationData();
            int time = this.mAnimationOut.getElapsedTime();
            int posX = this.mPosX;
            this.mCurrentPosX = animation.getTimelineValue(1, time, true) + posX;
            int posY = this.mPosY;
            this.mCurrentPosY = animation.getTimelineValue(2, time, true) + posY;
            if (!this.mAnimationOut.isFinishedAnimation()) {
               return;
            }
         } else {
            int timer = this.mTooltipScaleTimer;
            this.mTooltipScaleTimer = timer - delta;
            if (0 < timer - delta) {
               return;
            }
         }

         this.setState((byte)1);
      } else if (state == 2) {
         if (!this.mIsTooltipBubble) {
            if (this.mCurrentPosX == 10000 && this.mCurrentPosY == 10000) {
               iWrapper.playSoundFx(this.getOpenSoundEffect());
            }

            this.mAnimationIn.logicUpdate(delta);
            Animation animation = this.mAnimationIn.getCurrentAnimationData();
            int time = this.mAnimationIn.getElapsedTime();
            int posX = this.mPosX;
            this.mCurrentPosX = animation.getTimelineValue(1, time, true) + posX;
            int posY = this.mPosY;
            this.mCurrentPosY = animation.getTimelineValue(2, time, true) + posY;
            if (!this.mAnimationIn.isFinishedAnimation()) {
               return;
            }
         } else {
            if (this.mPlayTooltipSound) {
               this.mPlayTooltipSound = false;
               iWrapper.playSoundFx(this.getOpenSoundEffect());
            }

            int timer = this.mTooltipScaleTimer;
            this.mTooltipScaleTimer = timer + delta;
            if (timer + delta < 250) {
               return;
            }
         }

         this.setState((byte)0);
      } else {
         if (state != 0) {
            return;
         }

         this.mObjects.logicUpdate(delta);
      }
   }

   @Override
   public void callback(int id) {
   }

   public void initialize() {
   }

   public CollisionBox getCollisionBox(int index) {
      return this.mCollisions.getCollisionBox(index);
   }

   public int getCollisionBoxX(int index) {
      return this.mCollisions.getCollisionBox(index).getX();
   }

   public int getCollisionBoxY(int index) {
      return this.mCollisions.getCollisionBox(index).getY();
   }

   public int getCollisionBoxWidth(int index) {
      return this.mCollisions.getCollisionBox(index).getWidth();
   }

   public int getCollisionBoxHeight(int index) {
      return this.mCollisions.getCollisionBox(index).getHeight();
   }

   public void setCurrentPosY(int y) {
      this.mCurrentPosY = y;
   }

   @Override
   public int getX() {
      return this.mCurrentPosX;
   }

   @Override
   public int getY() {
      return this.mCurrentPosY;
   }

   public int getOpenSoundEffect() {
      return 35;
   }

   public void enableFadeBackground(boolean enable) {
      this.mEnableFadeBackground = enable;
   }

   public void addTrackingEvent(WindowEvent event) {
      this.mWindowEvent = event;
   }

   public WindowEvent getTrackingEvent() {
      return this.mWindowEvent;
   }

   public String getWindowName() {
      return WindowManager.getWindowName((byte)this.mId);
   }

   public void trackTab() {
      if (!this.isClosed()) {
         WindowEvent event = this.mWindowEvent;
         event.trackTab(this.getWindowName(), this.getActiveAreaName());
      }
   }

   public String getActiveAreaName() {
      return "main_view";
   }

   protected void initCollisions() {
      if (this.mBackground != null) {
         CollisionBox box = this.mBackground.getCollisionBox(1);
         if (box == null) {
            return;
         }

         this.mPosX = Toolkit.getScreenWidth() - box.getWidth() >> 1;
         this.mPosY = Toolkit.getScreenHeight() - box.getHeight() >> 1;
         this.mWidth = box.getWidth();
         this.mHeight = box.getHeight();
      }
   }

   protected void constructCloseButton(int id) {
      if (this.mBackground != null && this.mBackground.getCollisionBox(0) != null && this.mBackground.getCollisionBox(1) != null) {
         CollisionBox box0 = this.mBackground.getCollisionBox(0);
         CollisionBox box1 = this.mBackground.getCollisionBox(1);
         int x = box0.getX();
         int width = box1.getWidth();
         int y = box0.getY();
         int height = box1.getHeight();
         this.mButtonClose = new HUDButton(id, x + width / 2, y + height / 2, 66353);
      }
   }

   public void constructFlurryVideoButtonInMarket(int id) {
   }

   public void constructFlurryVideoButton(int id) {
   }

   public void constructImpactVideoButtonInMarket(int id) {
      if (Ios.platform().isImpactSupported()
         && this.mBackground != null
         && this.mBackground.getCollisionBox(0) != null
         && this.mBackground.getCollisionBox(1) != null) {
         CollisionBox box0 = this.mBackground.getCollisionBox(0);
         CollisionBox box1 = this.mBackground.getCollisionBox(1);
         int x = box0.getX();
         int y = box0.getY();
         int height = box1.getHeight();
         if (DeviceWrapper.isIPHONE4()) {
            x = box0.getX();
            y = box0.getY();
            height = box1.getHeight();
            x += 60;
            y = y + height / 2 + 300;
         } else {
            x += 30;
            y = y + height / 2 + 150;
         }

         if (DeviceWrapper.isIPAD() || DeviceWrapper.isIPAD1()) {
            x = box0.getX();
            y = box0.getY();
            height = box1.getHeight();
            x += 40;
            y = y + height / 2 + 300;
         }

         if (DeviceWrapper.isIPHONE5()) {
            x = box0.getX();
            y = box0.getY();
            height = box1.getHeight();
            x += 30;
            y = y + height / 2 + 300;
         }

         this.mButtonImpactVideo = new HUDButton(id, x, y, 66245);
      }
   }

   public void constructImpactVideoButton(int id) {
      if (Ios.platform().isImpactSupported()
         && this.mBackground != null
         && this.mBackground.getCollisionBox(0) != null
         && this.mBackground.getCollisionBox(1) != null) {
         CollisionBox box0 = this.mBackground.getCollisionBox(0);
         CollisionBox box1 = this.mBackground.getCollisionBox(1);
         int x = box0.getX();
         int y = box0.getY();
         int height = box1.getHeight();
         this.mButtonImpactVideo = new HUDButton(id, x - 200, y + height / 2, 66245);
      }
   }

   protected void setAsTooltipBubble() {
      this.mIsTooltipBubble = true;
   }

   protected void fadeBackground() {
      if (this.mFadeBackground) {
         byte state = this.mState;
         int alpha = 128;
         if (state != 0) {
            if (state == 2) {
               int elapsed = this.mAnimationIn.getElapsedTime();
               int length = this.mAnimationIn.getAnimationLength();
               alpha = (int)((float)elapsed / length * 128.0F);
            } else {
               alpha = 0;
               if (state == 3) {
                  int elapsed = this.mAnimationOut.getElapsedTime();
                  int length = this.mAnimationOut.getAnimationLength();
                  alpha = (int)(128.0F - (float)elapsed / length * 128.0F);
               }
            }
         }

         OGL.setAlpha(alpha / 255.0F);
         OGL.setColorRGB(0);
         OGL.fillRect(0, 0, Toolkit.getScreenWidth(), Toolkit.getScreenHeight());
         OGL.setAlpha(1.0F);
      }
   }

   public void loadResources() {
   }

   public void unloadResources() {
   }

   public void setState(byte state) {
      if (state == 2) {
         this.loadResources();
         if (!this.mIsTooltipBubble) {
            this.mAnimationIn.setAnimation(0, 1, false);
            this.mCurrentPosX = 10000;
            this.mCurrentPosY = 10000;
         } else {
            this.mCurrentPosX = 0;
            this.mCurrentPosY = 0;
            this.mTooltipScaleTimer = 0;
         }
      } else if (state == 3) {
         if (!this.mIsTooltipBubble) {
            this.mAnimationOut.setAnimation(0, 1, false);
         } else {
            this.mTooltipScaleTimer = 250;
         }
      }

      if (state == 1) {
         this.unloadResources();
      }

      this.mState = state;
   }
}
