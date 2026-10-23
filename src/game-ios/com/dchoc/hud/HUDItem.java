package com.dchoc.hud;

import com.dchoc.idead.touch.TouchEvent;

public abstract class HUDItem extends HUDObject {
   protected int mSpaceBetweenItems;
   protected boolean mWasTapped;
   protected HUDTouchArea mTouchAreaParent;
   protected HUDObject[] mObjects;
   private int mTargetPosX;
   private int mTargetPosY;
   private HUDSpeedController mSpeedController = new HUDSpeedController(0);

   public HUDItem() {
      super((byte)3);
   }

   public HUDItem(int id) {
      super((byte)3, id);
   }

   @Override
   public void freeImages() {
      if (this.mObjects != null) {
         for (int i = 0; i < (this.mObjects == null ? 0 : this.mObjects.length); i++) {
            if (this.mObjects[i] != null) {
               this.mObjects[i].freeImages();
            }
         }
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
   }

   @Override
   public boolean isVisible() {
      if (this.mParent != null) {
         return true;
      }

      this.mTouchAreaParent.getContainerX();
      int containerY = this.mTouchAreaParent.getContainerY();
      int y = this.mPosY;
      int clipY = this.mTouchAreaParent.getClipAreaY();
      int clipHeight = this.mTouchAreaParent.getClipAreaHeight();
      if (y + containerY > clipY + clipHeight) {
         return false;
      }

      int height = this.mHeight;
      return height + y + containerY >= this.mTouchAreaParent.getClipAreaY();
   }

   public void setTouchAreaParent(HUDTouchArea parent) {
      this.mTouchAreaParent = parent;
   }

   public int getSpace() {
      return this.mSpaceBetweenItems;
   }

   public boolean isMoving() {
      return this.mPosX == this.mTargetPosX ? false : this.mPosY != this.mTargetPosY;
   }

   @Override
   public void callback(int id) {
   }

   @Override
   public void draw() {
      for (int i = 0; i < (this.mObjects == null ? 0 : this.mObjects.length); i++) {
         if (this.mObjects[i] != null) {
            this.mObjects[i].draw();
         }
      }

      this.drawDebug(16711935);
   }

   @Override
   public void logicUpdate(int delta) {
      this.mSpeedController.logicUpdate(delta);
      if (this.mPosX != this.mTargetPosX) {
         int speed = this.mSpeedController.getCurrentSpeed();
         this.mPosX += speed;
         if (speed > 0 && this.mPosX > this.mTargetPosX) {
            this.mPosX = this.mTargetPosX;
         }

         if (speed < 0 && this.mPosX < this.mTargetPosX) {
            this.mPosX = this.mTargetPosX;
         }
      }

      if (this.mPosY != this.mTargetPosY) {
         int speed = this.mSpeedController.getCurrentSpeed();
         this.mPosY += speed;
         if (speed > 0 && this.mPosY > this.mTargetPosY) {
            this.mPosY = this.mTargetPosY;
         }

         if (speed < 0 && this.mPosY < this.mTargetPosY) {
            this.mPosY = this.mTargetPosY;
         }
      }

      if (this.mObjects != null) {
         for (int i = 0; i < (this.mObjects == null ? 0 : this.mObjects.length); i++) {
            if (this.mObjects[i] != null) {
               this.mObjects[i].logicUpdate(delta);
            }
         }
      }
   }

   public void setX(int x) {
      this.mPosX = x;
      this.mTargetPosX = x;
   }

   public void setY(int y) {
      this.mPosY = y;
      this.mTargetPosY = y;
   }

   public void setTargetX(int x) {
      this.mTargetPosX = x;
      this.mSpeedController.setParameters(x - this.mPosX, 200);
   }

   public void setTargetY(int y) {
      this.mTargetPosY = y;
      this.mSpeedController.setParameters(y - this.mPosY, 200);
   }

   @Override
   public int getX() {
      if (this.mTouchAreaParent != null) {
         return this.mTouchAreaParent.getContainerX() + this.mPosX;
      } else {
         return this.mParent != null ? this.mParent.getX() + this.mPosX : this.mPosX;
      }
   }

   @Override
   public int getY() {
      if (this.mTouchAreaParent != null) {
         return this.mTouchAreaParent.getContainerY() + this.mPosY;
      } else {
         return this.mParent != null ? this.mParent.getY() + this.mPosY : this.mPosY;
      }
   }

   public int getParameterForSort() {
      return this.mId;
   }

   public String getStringForSort() {
      return "";
   }

   public void refresh() {
   }

   protected void initObjects() {
      if (this.mObjects != null) {
         for (int i = 0; i < (this.mObjects == null ? 0 : this.mObjects.length); i++) {
            if (this.mObjects[i] != null) {
               this.mObjects[i].setParent(this);
            }
         }
      }
   }
}
