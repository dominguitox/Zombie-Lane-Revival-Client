package com.dchoc.hud;

public final class HUDSpeedController {
   public static final int TYPE_CONSTANT = 0;
   public static final int TYPE_INERTIA = 1;
   private int mCurrentSpeed;
   private int mSpeed;
   private int mTime;
   private int mTimeElapsed;
   private int mType;

   public HUDSpeedController(int type) {
      this.mType = type;
   }

   public int getCurrentSpeed() {
      return this.mCurrentSpeed;
   }

   public void logicUpdate(int delta) {
      this.mTimeElapsed += delta;
      if (this.mType == 1) {
         if (this.mTime != 0) {
            this.mCurrentSpeed = this.mSpeed * delta / this.mTime;
         }

         if (this.mTimeElapsed < 250) {
            return;
         }

         if (Math.abs(this.mSpeed) <= 2) {
            this.mSpeed = 0;
         } else {
            this.mSpeed = this.mSpeed - this.mSpeed / 3;
         }

         this.mTimeElapsed -= 250;
      } else if (this.mType == 0 && this.mTime != 0) {
         this.mCurrentSpeed = this.mSpeed * delta / this.mTime;
         if (this.mCurrentSpeed == 0) {
            this.mCurrentSpeed = this.mSpeed > 0 ? 1 : -1;
         }
      }
   }

   public void setParameters(int speed, int time) {
      this.mTimeElapsed = 0;
      this.mTime = time;
      this.mSpeed = speed;
   }
}
