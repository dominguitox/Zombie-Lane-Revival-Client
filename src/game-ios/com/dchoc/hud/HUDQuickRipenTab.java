package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.objects.FarmingObject;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tracking.QuickRipenEvent;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.math.Logarithm;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.DavinciUtilities;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.windows.WindowQuickRipen;

public class HUDQuickRipenTab extends HUDObject {
   private static SpriteObject mProgressBackground;
   private static SpriteObject mProgressFill;
   private static SpriteObject mCropIcon;
   private static int mCropIconMarginY;
   private int mState;
   private HUDCollection mObjects;
   private SpriteObject mLayout;
   private RoundRectangle mRectangle;
   private HUDImage mBackground;
   private HUDImage mReviveImage;
   private HUDImage mCashImage;
   private HUDImage mTimerImage;
   private HUDAutoTextField mTitle;
   private HUDTime mTimer;
   private HUDInteger mCash;
   private HUDButton mButton;
   private FarmingObject mCrop;
   private int mLeftWidth;
   private int mRightWidth;
   private int mCenterWidth;
   private int mCenterX;
   private int mAreaWidth;
   private int mProgressWidth;
   private int mInitialProgressWidth;
   private int mVisibleHeight;
   private int mFullHeight;
   private int mSlideTime;
   private int mVisibleTime;
   private int mRipenDelay;
   private int mRipenElapsed;
   private long mInitialTimeLeft;
   private long mInitialTimeElapsed;
   private int mBaseY;
   private float mCropScale;
   private float mCashScale;
   private float mReviveScale;
   private boolean mPendingCallback;
   private QuickRipenEvent mEvent;

   public HUDQuickRipenTab() {
      super((byte)-1);
   }

   public void init(HUDProgressbarEnergy energy) {
      this.initProgressBar();
      this.mLayout = AnimationsManager.loadShared(78845);
      this.mObjects = new HUDCollection();
      this.mObjects.setParent(this);
      CollisionBox box = this.mLayout.getCollisionBox(0);
      this.mBackground = new HUDImage(78843, box);
      box = this.mLayout.getCollisionBox(2);
      this.mRectangle = new RoundRectangle(0);
      this.mRectangle.setSizes(box);
      box = this.mLayout.getCollisionBox(3);
      this.mTitle = new HUDAutoTextField(box);
      this.mTitle.setCentered(false, true);
      this.mTitle.setSplitUsingWidth(true);
      box = this.mLayout.getCollisionBox(4);
      this.mTimer = new HUDTime(0, 0, 65563, 65743);
      HUDTime timer = this.mTimer;
      int x = box.getX();
      int width = box.getWidth();
      int y = box.getY();
      int height = box.getHeight();
      timer.setPosition(x + (width >> 1), y + height / 3 + (DeviceWrapper.useHDGraphics() ? 2 : 1));
      this.mTimer.setCenterType((byte)3);
      this.mTimer.setParent(this);
      box = this.mLayout.getCollisionBox(7);
      this.mReviveImage = new HUDImage(78847, box);
      this.mReviveImage.setParent(this);
      width = box.getWidth();
      int imageWidth = this.mReviveImage.getWidth();
      this.mReviveScale = (float)width / imageWidth;
      box = this.mLayout.getCollisionBox(6);
      this.mCashImage = new HUDImage(74773, box);
      this.mCashImage.setParent(this);
      width = box.getWidth();
      imageWidth = this.mCashImage.getWidth();
      this.mCashScale = (float)width / imageWidth;
      box = this.mLayout.getCollisionBox(8);
      this.mTimerImage = new HUDImage(66255, box);
      this.mTimerImage.setParent(this);
      box = this.mLayout.getCollisionBox(5);
      this.mButton = new HUDButton(55000, box, 4);
      box = this.mLayout.getCollisionBox(9);
      this.mCash = new HUDInteger(65571);
      HUDInteger cash = this.mCash;
      x = box.getX();
      width = box.getWidth();
      y = box.getY();
      height = box.getHeight();
      cash.setPosition(x + width / 2, y + height / 2);
      this.mCash.setCenterType((byte)3);
      this.mObjects.addObject(this.mBackground);
      this.mObjects.addObject(this.mRectangle);
      this.mObjects.addObject(this.mTitle);
      this.mObjects.addObject(this.mButton);
      this.mObjects.addObject(this.mCash);
      box = this.mLayout.getCollisionBox(0);
      this.mFullHeight = box.getHeight();
      this.mBaseY = box.getY();
      this.mPendingCallback = false;
      x = energy.getX();
      width = this.mBackground.getWidth();
      boolean hd = DeviceWrapper.useHDGraphics();
      this.setPosition(x - width + (hd ? 0 : 5), this.mBaseY);
      this.setVisible(false);
      this.mEvent = new QuickRipenEvent();
   }

   public void changeState(int state) {
      switch (state) {
         case 0:
            this.mCrop = null;
            this.mVisibleHeight = 0;
            this.mSlideTime = 0;
            this.setVisible(false);
            this.mState = 0;
            return;
         case 2:
            this.mVisibleTime = 280;
            this.mState = 5;
            return;
         case 3:
            this.mButton.setEnabled(true);
            this.mButton.resetState();
            this.setVisible(true);
            this.mState = 3;
            return;
         case 4:
            this.mVisibleHeight = 0;
            this.mSlideTime = 0;
            this.mButton.setEnabled(true);
            this.mButton.resetState();
            this.setVisible(true);
            this.mState = 4;
            return;
         case 5:
            this.mVisibleTime = 3000;
            this.mState = 5;
            return;
         case 7:
            this.mRipenDelay = 280;
            this.mRipenElapsed = 0;
            this.mInitialTimeLeft = this.mCrop.getGrowingTimeLeft();
            this.mInitialTimeElapsed = this.mCrop.getGrowingTimeElapsed();
            this.mInitialProgressWidth = this.mProgressWidth;
            this.mButton.setEnabled(false);
         case 1:
         case 6:
         default:
            this.mState = state;
      }
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.isVisible() && this.mState != 0) {
         if (this.mPendingCallback && this.mState == 5) {
            this.mPendingCallback = false;
            this.changeState(6);
            this.callback(55000);
         } else {
            if (this.mState == 7) {
               this.updateRipen(delta);
            } else {
               this.updateTimer();
               this.updateProgressBar();
               this.updateSlide(delta);
               this.updateVisibleTime(delta);
               if (this.mState == 5 && !this.mCrop.isGrowing()) {
                  this.changeState(1);
               }
            }

            this.mObjects.logicUpdate(delta);
         }
      }
   }

   @Override
   public void draw() {
      if (this.isVisible() && this.mState != 0) {
         this.mObjects.draw();
         OGL.pushParameters();
         OGL.setScale(this.mCropScale, this.mCropScale);
         CollisionBox box = this.mLayout.getCollisionBox(1);
         SpriteObject icon = mCropIcon;
         int x = this.getX();
         int bx = box.getX();
         int width = box.getWidth();
         int y = this.getY();
         int by = box.getY();
         int height = box.getHeight();
         icon.draw(x + bx + width / 2, by + y + height / 2 + mCropIconMarginY);
         OGL.popParameters();
         OGL.pushParameters();
         OGL.setScale(this.mCashScale, this.mCashScale);
         this.mCashImage.draw();
         OGL.popParameters();
         OGL.pushParameters();
         OGL.setScale(this.mReviveScale, this.mReviveScale);
         this.mReviveImage.draw();
         OGL.popParameters();
         this.mTimerImage.draw();
         box = this.mLayout.getCollisionBox(4);
         x = this.getX();
         bx = box.getX();
         y = this.getY();
         by = box.getY();
         width = box.getWidth();
         this.drawProgressBar(x + bx, y + by, width, this.mProgressWidth);
         this.mTimer.draw();
      }
   }

   public void setObject(IsometricObject object) {
      if (object != null && object.getType() == 6) {
         if (this.mState != 0) {
            if (this.mCrop != null && this.mCrop.equals(object)) {
               return;
            }

            if (this.mState == 7) {
               this.finishRipen();
            }
         }

         FarmingObject crop = (FarmingObject)object;
         if (crop.isGrowing()) {
            this.mCrop = crop;
            Item item = crop.getItem();
            mCropIcon = this.mCrop.getAnimations().getActiveAnimation();
            CollisionBox box = this.mLayout.getCollisionBox(1);
            int width = mCropIcon.getFrameWidth();
            int height = mCropIcon.getFrameHeight();
            this.mCropScale = width > height ? (float)box.getWidth() / width : (float)box.getHeight() / height;
            this.mTitle.setText(item.getTitle(), 2);
            this.updateTimer();
            this.updateCashValue();
            this.updateCropIconMargin();
            this.changeState(4);
         }
      }
   }

   @Override
   public void callback(int id) {
      if (id == 55000) {
         WindowQuickRipen window = (WindowQuickRipen)WindowManager.getWindow((byte)32);
         if (window.isClosed()) {
            if (this.mState == 1) {
               this.changeState(3);
               this.mPendingCallback = true;
            } else {
               FarmingObject crop = this.mCrop;
               if (window.setCrop(crop, this.mCash.getValue())) {
                  WindowManager.openWindow((byte)32);
                  this.changeState(6);
               }
            }
         }
      }
   }

   public void initProgressBar() {
      mProgressBackground = new SpriteObject(DavinciUtilities.loadAnimation(78933, true, 0));
      mProgressFill = new SpriteObject(DavinciUtilities.loadAnimation(73689, true, 0));
      int[] layout = IsometricUtils.calculateBarLayout(mProgressFill);
      this.mAreaWidth = layout[0];
      this.mCenterX = layout[2];
      this.mCenterWidth = layout[3];
      this.mLeftWidth = layout[4];
      this.mRightWidth = layout[5];
   }

   public void updateSlide(int delta) {
      if (this.mState == 1) {
         this.mSlideTime = Math.max(this.mSlideTime - delta, 0);
      } else if (this.mState == 3 || this.mState == 4) {
         this.mSlideTime = Math.min(this.mSlideTime + delta, 500);
      }

      if (this.mState == 1 || this.mState == 3 || this.mState == 4) {
         this.mVisibleHeight = this.mFullHeight * this.mSlideTime / 500;
         this.setPositionY(this.mVisibleHeight + this.mBaseY - this.mFullHeight);
         CollisionBox box = this.mLayout.getCollisionBox(7);
         HUDImage image = this.mReviveImage;
         int x = box.getX();
         int w = box.getWidth();
         int y = box.getY();
         int h = box.getHeight();
         image.setPosition(x + w / 2, y + h / 2);
         box = this.mLayout.getCollisionBox(6);
         image = this.mCashImage;
         x = box.getX();
         w = box.getWidth();
         y = box.getY();
         h = box.getHeight();
         image.setPosition(x + w / 2, y + h / 2);
         box = this.mLayout.getCollisionBox(8);
         image = this.mTimerImage;
         x = box.getX();
         w = box.getWidth();
         int imageWidth = this.mTimerImage.getWidth();
         y = box.getY();
         h = box.getHeight();
         image.setPosition(x + w - imageWidth / 2, y + h / 2);
      }

      if (this.mState == 1 && this.mSlideTime == 0) {
         this.changeState(0);
      } else if ((this.mState == 3 || this.mState == 4) && this.mSlideTime == 500) {
         this.changeState(5);
      }
   }

   public void updateVisibleTime(int delta) {
      if (this.mState == 5) {
         this.mVisibleTime -= delta;
         if (this.mVisibleTime <= 0) {
            this.mVisibleTime = 0;
            this.changeState(1);
         }
      }
   }

   public void updateRipen(int delta) {
      if (this.mState == 7) {
         if (this.mRipenDelay >= 1) {
            this.mRipenDelay -= delta;
         } else {
            this.mRipenElapsed += delta;
            int width = this.mLayout.getCollisionBox(4).getWidth();
            float ratio = this.mRipenElapsed / 1500.0F;
            this.mProgressWidth = this.mInitialProgressWidth + (int)((width - this.mInitialProgressWidth) * ratio);
            if (this.mRipenElapsed > 1499) {
               this.finishRipen();
               this.changeState(1);
            }

            HUDTime timer = this.mTimer;
            FarmingObject crop = this.mCrop;
            int progressed = (int)((float)this.mInitialTimeLeft * ratio);
            int max = crop.getGrowingTimeMax();
            timer.setTime(max - (this.mInitialTimeElapsed + progressed));
         }
      }
   }

   public void finishRipen() {
      if (this.mCrop != null) {
         int cash = this.mCash.getValue();
         PlayerProfile.removeCash(cash);
         this.mCrop.setInstaGrow();
         Item item = this.mCrop.getItem();
         QuickRipenEvent event = this.mEvent;
         int subtype = item.getSubType();
         int id = item.getID();
         event.trackEvent(subtype, id, cash);
      }
   }

   public void updateProgressBar() {
      long elapsed = this.mCrop.getGrowingTimeElapsed();
      int max = this.mCrop.getGrowingTimeMax();
      int width = this.mLayout.getCollisionBox(4).getWidth();
      this.mProgressWidth = (int)(width * ((float)elapsed / max));
   }

   public void updateTimer() {
      long time = this.mCrop.getGrowingTimeLeft();
      this.mTimer.setTime(time);
   }

   public void updateCashValue() {
      int seconds = this.mTimer.getTimeLeft();
      int value = 1;
      if (seconds >= 3600) {
         int minutes = seconds / 60 % 60;
         if (seconds >= 7200) {
            minutes += seconds / 3600 * 60 - 60;
         }

         if (minutes > 0) {
            value = Logarithm.roundInt(2.0 * Logarithm.logn10(minutes) + 1.0);
         }
      }

      this.mCash.setValue(value);
   }

   public void drawProgressBar(int x, int y, int total, int filled) {
      IsometricUtils.drawBar(
         mProgressBackground, x, y, this.mLeftWidth, this.mRightWidth, this.mCenterWidth, this.mCenterX, this.mAreaWidth, total, total, 1.0F, false, true
      );
      if (total < filled) {
         filled = total;
      }

      IsometricUtils.drawBar(
         mProgressFill, x, y, this.mLeftWidth, this.mRightWidth, this.mCenterWidth, this.mCenterX, this.mAreaWidth, total, filled, 1.0F, false, false
      );
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible()) {
         this.mObjects.pointerEvent(event);
      }
   }

   public void updateCropIconMargin() {
      int height = mCropIcon.getHeight();
      float heightScale = this.mCropScale;
      int frame = mCropIcon.getFrameHeight();
      float frameScale = this.mCropScale;
      int pivot = mCropIcon.getPivotY();
      mCropIconMarginY = Logarithm.roundInt(frame * frameScale * 0.5F - (height * heightScale - pivot * this.mCropScale));
   }
}
