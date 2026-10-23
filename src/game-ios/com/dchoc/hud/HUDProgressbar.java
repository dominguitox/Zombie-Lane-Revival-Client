package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.Animation;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public abstract class HUDProgressbar extends HUDObject {
   protected SpriteObject mSymbol;
   protected int mMaxValue;
   protected int mCurrentValue;
   protected int specialPos1X;
   protected int specialPos1Y;
   protected int specialPos2X;
   protected int specialPos2Y;
   protected boolean mForcedPosition;
   protected int mForcedX;
   protected int mForcedY;
   private SpriteObject mBackground;
   private SpriteObject mFill;
   private SpriteObject mCurrentAnimation;
   private SpriteObject mAnimationIn;
   private SpriteObject mAnimationOut;
   private int mCurrentWidthOfClip;
   private int mTargetWidthOfClip;
   private boolean mIsAnimated;
   private int[] mRefDimensions = new int[4];

   public HUDProgressbar(int background, int fill, int symbol) {
      super((byte)-1);
      this.mBackground = AnimationsManager.loadShared(background);
      this.mFill = AnimationsManager.loadShared(fill);
      this.mSymbol = AnimationsManager.loadShared(symbol);
      this.mAnimationIn = AnimationsManager.load(65587);
      this.mAnimationOut = AnimationsManager.load(65589);
      this.mCurrentAnimation = this.mAnimationIn;
      Animation data = this.mAnimationIn.getCurrentAnimationData();
      int elapsed = this.mCurrentAnimation.getElapsedTime();
      int offset = data.getTimelineValue(1, elapsed, true);
      CollisionBox box = this.mBackground.getCollisionBox(1);
      this.specialPos1X = box.getX() + offset;
      int y = box.getY();
      this.specialPos1Y = y + box.getHeight() / 2;
      box = this.mBackground.getCollisionBox(2);
      this.specialPos2X = box.getX() + offset;
      y = box.getY();
      this.specialPos2Y = y + box.getHeight() / 2;
      this.mIsAnimated = true;
      this.mForcedPosition = false;
   }

   @Override
   public void logicUpdate(int delta) {
      this.mCurrentAnimation.logicUpdate(delta);
      if (this.mIsAnimated && this.mCurrentWidthOfClip != this.mTargetWidthOfClip) {
         int current = this.mCurrentWidthOfClip;
         int target = this.mTargetWidthOfClip;
         if (current < target) {
            this.mCurrentWidthOfClip = ++current;
         }

         if (target < current) {
            this.mCurrentWidthOfClip = current - 1;
         }
      }

      Animation data = this.mCurrentAnimation.getCurrentAnimationData();
      int elapsed = this.mCurrentAnimation.getElapsedTime();
      int offset = data.getTimelineValue(1, elapsed, true);
      int previousX = this.mPosX;
      this.mPosX = offset + (Toolkit.getScreenWidth() >> 1);
      this.specialPos1X = this.mBackground.getCollisionBox(1).getX() + offset;
      this.specialPos2X = this.mBackground.getCollisionBox(2).getX() + offset;
      this.mSymbol.logicUpdate(delta);
      if (this.mPosX != previousX) {
         this.setRefDimensions();
      }
   }

   @Override
   public void draw() {
      int screenWidth = Toolkit.getScreenWidth();
      int screenHeight = Toolkit.getScreenHeight();
      int forcedX = this.mForcedX;
      int forcedY = this.mForcedY;
      if (!this.mForcedPosition) {
         this.mBackground.draw(this.mPosX, 0);
         int x = this.mPosX;
         int pivot = this.mFill.getPivotX();
         OGL.setClip(x - pivot, 0, this.mCurrentWidthOfClip, screenHeight);
         this.mFill.draw(this.mPosX, 0);
      } else {
         this.mBackground.draw(forcedX, forcedY);
         int pivot = this.mFill.getPivotX();
         OGL.setClip(forcedX - pivot, 0, this.mCurrentWidthOfClip, screenHeight);
         this.mFill.draw(forcedX, forcedY);
      }

      OGL.setClip(0, 0, screenWidth, screenHeight);
      CollisionBox box = this.mBackground.getCollisionBox(0);
      SpriteObject symbol = this.mSymbol;
      if (!this.mForcedPosition) {
         int x = this.mPosX;
         x += box.getX();
         int y = box.getY();
         symbol.draw(x, y);
      } else {
         int x = box.getX() + forcedX;
         int y = box.getY() + forcedY;
         symbol.draw(x, y);
      }
   }

   public void switchMode(boolean in) {
      this.mCurrentAnimation = in ? this.mAnimationIn : this.mAnimationOut;
      this.mCurrentAnimation.setAnimation(0, 1, false);
   }

   public void setValue(int value) {
      if (this.mCurrentValue != value) {
         this.mCurrentValue = value;
         this.updateClip();
      }
   }

   public void setMaxValue(int value) {
      if (this.mMaxValue != value) {
         this.mMaxValue = value;
         this.updateClip();
      }
   }

   public int[] getRefDimensions() {
      return this.mRefDimensions;
   }

   public void setRefDimensions() {
      int x = this.mPosX;
      int width = this.mBackground.getWidth();
      int height = this.mBackground.getHeight();
      int y = 0;
      if (this.mBackground.getCollisionBox(3) != null) {
         CollisionBox box = this.mBackground.getCollisionBox(3);
         int boxX = box.getX();
         y = box.getY();
         width = box.getWidth();
         x += boxX;
         height = box.getHeight();
      }

      int[] box = this.mRefDimensions;
      box[0] = x;
      box[1] = y;
      box[2] = width;
      box[3] = height;
   }

   @Override
   public boolean isInside(int x, int y) {
      int[] box = this.mRefDimensions;
      return x >= box[0] && x <= box[2] + box[0] && y >= box[1] && y <= box[1] + box[3];
   }

   public void pointerEvent(TouchEvent event, int tooltip) {
      if (this.isInside(event.getX(), event.getY())) {
         if (event.getType() != 0) {
            if (event.getType() != 5) {
               return;
            }

            int[] box = this.mRefDimensions;
            this.createTooltip(box[0], box[1], box[2], box[3], tooltip);
         }

         event.setConsumed(true);
      }
   }

   public void enableForcedPosition(boolean forced) {
      this.mForcedPosition = forced;
   }

   public void setForcedX(int x) {
      this.mForcedX = x;
   }

   public void setForcedY(int y) {
      this.mForcedY = y;
   }

   public SpriteObject getBackground() {
      return this.mBackground;
   }

   protected void finishAnimation() {
      this.mCurrentWidthOfClip = this.mTargetWidthOfClip;
   }

   public void updateClip() {
      boolean animated = this.mIsAnimated;
      int width = this.mFill.getWidth();
      int max = this.mMaxValue;
      int value = this.mCurrentValue;
      int clip = value * width / max;
      if (!animated) {
         this.mCurrentWidthOfClip = clip;
      } else {
         this.mTargetWidthOfClip = clip;
      }

      if (value < 0) {
         value = 0;
         this.mCurrentValue = 0;
      }

      if (max < value) {
         this.mCurrentValue = max;
      }

      if (this.mSymbol.hasAnimationTimeline()) {
         this.mSymbol.setAnimation(0, 1, false);
      }
   }
}
