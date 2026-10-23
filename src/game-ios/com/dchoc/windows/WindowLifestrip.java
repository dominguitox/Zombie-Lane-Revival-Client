package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDInteger;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.Animation;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public class WindowLifestrip extends Window {
   protected static final int COLLISION_ID_BUTTON = 4;
   protected static final int COLLISION_ID_DESCRIPTION = 1;
   protected static final int COLLISION_ID_HEADER = 10;
   protected static final int COLLISION_ID_ICON = 2;
   public static final int STATE_CLOSING_2 = 5;
   public static final int STATE_OPENING_2 = 4;
   private HUDButton mButton;
   private HUDAutoTextField mTitleField;
   private HUDAutoTextField mDescriptionField;
   private HUDInteger mCurrentPercentage;
   private int mProgressStart;
   private int mProgressStop;
   private int mProgressCurrent;
   private int mElapsedTime;
   private int numberOfTicks;
   private SpriteObject mBluePercentSign;
   private SpriteObject mBackgroundFront = AnimationsManager.loadShared(74789);
   private SpriteObject mXRays;
   private SpriteObject mCurrentIcon;
   private SpriteObject mProgressBackground;
   private SpriteObject mProgressFill;
   private CollisionBox mTitleBox;
   private CollisionBox mDescriptionBox;

   public WindowLifestrip() {
      this.mCollisions = AnimationsManager.loadShared(67077);
      this.initCollisions();
      this.mXRays = AnimationsManager.loadShared(66937);
      this.mProgressBackground = AnimationsManager.loadShared(67041);
      this.mProgressFill = AnimationsManager.loadShared(67073);
      this.mBluePercentSign = AnimationsManager.loadShared(73847);
      this.mButton = new HUDButton(24000, this.getCollisionBox(4), 3);
      this.mButton.setText(54);
      this.mButton.setParent(this);
      this.mCurrentPercentage = new HUDInteger(65571);
      this.mTitleBox = this.mCollisions.getCollisionBox(10);
      this.mDescriptionBox = this.mCollisions.getCollisionBox(1);
      this.mTitleField = new HUDAutoTextField(this.mTitleBox);
      this.mTitleField.setCentered(true, true);
      this.mDescriptionField = new HUDAutoTextField(this.mDescriptionBox);
      this.mDescriptionField.setCentered(true, true);
      this.mDescriptionField.setSplitUsingWidth(true);
      this.mProgressStart = 0;
      this.mProgressStop = 0;
      this.mState = 1;
   }

   public void setOptions(int description, int title, int start, int stop) {
      int icon = 74769;
      if (title != 310) {
         if (title == 308) {
            icon = 69637;
         } else {
            icon = 67093;
         }
      }

      this.setOptions(description, title, start, stop, icon);
   }

   public void setOptions(int description, int title, int start, int stop, int icon) {
      this.mProgressStart = start;
      this.mProgressStop = stop;
      this.mProgressCurrent = start;
      this.mTitleField.setText(title, DCiDead.getFont(5));
      this.mDescriptionField.setText(Toolkit.getText(description), DCiDead.getFont(4));
      this.mElapsedTime = 0;
      this.numberOfTicks = 0;
      this.mCurrentIcon = AnimationsManager.loadShared(icon);
   }

   @Override
   public void draw() {
      int width = Toolkit.getScreenWidth();
      int height = Toolkit.getScreenHeight();
      int half = width >> 1;
      this.mBackgroundFront.draw(half, height >> 1);
      byte state = this.mState;
      int offset = 0;
      if (state != 0) {
         Animation data;
         SpriteObject animation;
         if (state == 3) {
            data = this.mAnimationOut.getCurrentAnimationData();
            animation = this.mAnimationOut;
         } else {
            if (state != 4) {
               return;
            }

            data = this.mAnimationIn.getCurrentAnimationData();
            animation = this.mAnimationIn;
         }

         offset = data.getTimelineValue(2, animation.getElapsedTime(), true);
      }

      this.mXRays.draw(half, half);
      this.mTitleField.setPosition((width - this.mTitleBox.getWidth()) / 2, this.mTitleBox.getY() + offset);
      this.mDescriptionField.setPosition((width - this.mDescriptionBox.getWidth()) / 2, this.mDescriptionBox.getY() + offset);
      this.mTitleField.draw();
      this.mDescriptionField.draw();
      CollisionBox iconBox = this.getCollisionBox(2);
      this.mCurrentIcon.draw(width / 2, iconBox.getY() + offset + iconBox.getHeight() / 2);
      CollisionBox buttonBox = this.getCollisionBox(4);
      int iconY = iconBox.getY();
      int iconHeight = iconBox.getHeight();
      int buttonY = buttonBox.getY();
      int bottom = iconHeight + iconY;
      int progressY = bottom + offset + (buttonY - bottom - this.mProgressBackground.getHeight()) / 2;
      this.mProgressBackground.draw((width - this.mProgressBackground.getWidth()) / 2, progressY);
      int backgroundWidth = this.mProgressBackground.getWidth();
      int pivot = Math.abs(this.mProgressFill.getPivotX());
      int current = this.mProgressCurrent;
      OGL.setClip(pivot + (width - backgroundWidth) / 2, 0, current * this.mProgressFill.getWidth() / 100, Toolkit.getScreenHeight());
      this.mProgressFill.draw((width - this.mProgressBackground.getWidth()) / 2, progressY);
      OGL.setClip(0, 0, Toolkit.getScreenWidth(), Toolkit.getScreenHeight());
      this.mCurrentPercentage.setValue(this.mProgressCurrent);
      int percentageWidth = this.mCurrentPercentage.getWidth();
      int percentageY = progressY + this.mProgressBackground.getHeight() / 2;
      int percentageX = width / 2 - percentageWidth / 2;
      this.mCurrentPercentage.setPosition(percentageX, percentageY);
      this.mCurrentPercentage.draw();
      this.mBluePercentSign.draw(this.mCurrentPercentage.getWidth() + percentageX, percentageY);
      this.mButton.setPosition((width - this.mButton.getWidth()) / 2, this.getCollisionBox(4).getY() + offset);
      this.mButton.draw();
   }

   @Override
   public void logicUpdate(int delta) {
      this.mBackgroundFront.logicUpdate(delta);
      this.mXRays.logicUpdate(delta);
      this.mAnimationIn.logicUpdate(delta);
      this.mAnimationOut.logicUpdate(delta);
      this.mCurrentIcon.logicUpdate(delta);
      byte state = this.mState;
      if (state == 0 || state == 3 || state == 4) {
         this.numberOfTicks += delta;
         if (this.numberOfTicks >= 100) {
            if (this.mProgressCurrent < this.mProgressStop) {
               this.mProgressCurrent++;
            }

            this.numberOfTicks = 0;
         }
      }

      switch (state) {
         case 0:
            this.mElapsedTime += delta;
         case 1:
         default:
            break;
         case 2:
            if (this.mBackgroundFront.isFinishedAnimation()) {
               this.mAnimationIn.setAnimation(0, 1, false);
               this.mState = 4;
            }
            break;
         case 3:
            if (this.mAnimationOut.isFinishedAnimation()) {
               this.mBackgroundFront.setAnimation(0, 1, false);
               this.mState = 5;
            }
            break;
         case 4:
            if (this.mAnimationIn.isFinishedAnimation()) {
               this.mState = 0;
            }
            break;
         case 5:
            if (this.mBackgroundFront.isFinishedAnimation()) {
               this.mState = 1;
            }
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.mState == 0) {
         this.mButton.pointerEvent(event);
      }
   }

   @Override
   public void open() {
      this.mBackgroundFront.setReversedPlayback(false);
      this.mBackgroundFront.setAnimation(0, 1, false);
      iWrapper.playSoundFx(47);
      this.mState = 2;
   }

   @Override
   public void close() {
      this.mBackgroundFront.setReversedPlayback(true);
      this.mAnimationOut.setAnimation(0, 1, false);
      this.mState = 3;
   }

   @Override
   public void callback(int id) {
      if (this.mState == 0 && id == 24000) {
         this.close();
      }
   }
}
