package com.dchoc.idead.isometric;

import com.dchoc.idead.Transform;
import com.dchoc.math.Rect2;
import com.dchoc.toolkit.Toolkit;

public class Camera extends Rect2 {
   private float mBoundsHeight;
   private float mBoundsWidth;
   private float mBoundsX;
   private float mBoundsY;
   private int mDeltaTime;
   private float mDeltaX = 0.0F;
   private float mDeltaY = 0.0F;
   private float mFlyDeltaX;
   private float mFlyDeltaY;
   private int mFlyTimeMax;
   private int mFlyTimer;
   private boolean mFlyTo;
   private boolean mFreeScroll;
   private float mOldX;
   private float mOldY;

   public Camera(float x, float y, float width, float height) {
      super(x, y, width, height);
      this.mBoundsX = 0.0F;
      this.mBoundsY = 0.0F;
      this.mBoundsWidth = 0.0F;
      this.mBoundsHeight = 0.0F;
      this.mDeltaTime = 0;
   }

   public void setBounds(float x, float y, float width, float height) {
      this.mBoundsHeight = height;
      this.mBoundsX = x;
      this.mBoundsY = y;
      this.mBoundsWidth = width;
   }

   public int logicUpdate(int delta) {
      if (this.mFlyTo) {
         this.updateFlyPosition(delta);
         return 0;
      }

      this.mDeltaTime += delta;
      if (this.mFreeScroll) {
         float factor = this.mDeltaTime / 1000.0F * 0.3F;
         if (this.mX < this.mBoundsX) {
            this.mDeltaX = factor * (this.mBoundsX - this.mX);
         } else if (this.mX + this.mWidth > this.mBoundsX + this.mBoundsWidth) {
            this.mDeltaX = factor * (this.mBoundsX + this.mBoundsWidth - (this.mX + this.mWidth));
         }

         if (this.mY < this.mBoundsY) {
            this.mDeltaY = factor * (this.mBoundsY - this.mY);
         } else if (this.mY + this.mHeight > this.mBoundsY + this.mBoundsHeight) {
            this.mDeltaY = factor * (this.mBoundsY + this.mBoundsHeight - (this.mY + this.mHeight));
         }

         if (this.mX < this.mBoundsX && this.mX + this.mWidth > this.mBoundsX + this.mBoundsWidth) {
            this.mDeltaX = 0.0F;
         }

         if (this.mY < this.mBoundsY && this.mY + this.mHeight > this.mBoundsY + this.mBoundsHeight) {
            this.mDeltaY = 0.0F;
         }
      }

      this.mX = this.mDeltaX + this.mX;
      this.mY = this.mDeltaY + this.mY;
      this.mDeltaX = 0.0F;
      this.mDeltaY = 0.0F;
      return 0;
   }

   public void move(float dx, float dy) {
      this.mDeltaX += dx;
      this.mDeltaY += dy;
      this.mFlyTo = false;
   }

   public void moveTo(float x, float y) {
      this.mX = x;
      this.mY = y;
      this.mFlyTo = false;
   }

   public void moveToWithBounds(float x, float y) {
      this.mX = x;
      this.mY = y;
      if (this.mX < this.mBoundsX) {
         this.mX = this.mBoundsX;
      } else if (this.mX + this.mWidth > this.mBoundsX + this.mBoundsWidth) {
         this.mX = this.mBoundsX + this.mBoundsWidth - this.mWidth;
      }

      if (this.mY < this.mBoundsY) {
         this.mY = this.mBoundsY;
      } else if (this.mY + this.mHeight > this.mBoundsY + this.mBoundsHeight) {
         this.mY = this.mBoundsY + this.mBoundsHeight - this.mHeight;
      }

      this.mFlyTo = false;
   }

   public void flyTo(IsometricObject object) {
      if (object != null) {
         this.mOldX = this.mX;
         this.mOldY = this.mY;
         this.mFlyTimer = 0;
         float boundsX = object.getBoundsX();
         float boundsWidth = object.getBoundsWidth();
         float boundsY = object.getBoundsY();
         float boundsHeight = object.getBoundsHeight();
         float targetX = boundsX + boundsWidth * 0.5F - this.getWidth() * 0.5F;
         float targetY = boundsY + boundsHeight * 0.5F - this.getHeight() * 0.5F;
         this.mFlyDeltaX = -(this.mX - targetX);
         this.mFlyDeltaY = -(this.mY - targetY);
         float distance;
         int screen;
         if (Math.abs(this.mFlyDeltaX) > Math.abs(this.mFlyDeltaY)) {
            distance = Math.abs(this.mFlyDeltaX);
            screen = Toolkit.getScreenWidth();
         } else {
            distance = Math.abs(this.mFlyDeltaY);
            screen = Toolkit.getScreenHeight();
         }

         this.mFlyTimeMax = (int)(screen / 2 * 500.0F / distance);
         this.mFlyTo = true;
      }
   }

   public void setFreeScroll(boolean freeScroll) {
      this.mFreeScroll = freeScroll;
      if (freeScroll) {
         this.mDeltaTime = 0;
      }
   }

   public boolean isFreeScroll() {
      return this.mFreeScroll;
   }

   public float getWorldX1() {
      return Transform.toWorldX(this.mX, this.mY + this.mHeight);
   }

   public float getWorldY1() {
      return Transform.toWorldX(this.mX, this.mY);
   }

   public float getWorldX2() {
      return Transform.toWorldX(this.mX + this.mWidth, this.mY);
   }

   public float getWorldY2() {
      return Transform.toWorldX(this.mX + this.mWidth, this.mY + this.mHeight);
   }

   public void updateFlyPosition(int delta) {
      this.mFlyTimer += delta;
      if (this.mFlyTimer > this.mFlyTimeMax) {
         this.mFlyTimer = this.mFlyTimeMax;
         this.mFlyTo = false;
      }

      this.mX = this.mOldX + this.mFlyDeltaX * this.mFlyTimer / this.mFlyTimeMax;
      this.mY = this.mOldY + this.mFlyDeltaY * this.mFlyTimer / this.mFlyTimeMax;
   }
}
