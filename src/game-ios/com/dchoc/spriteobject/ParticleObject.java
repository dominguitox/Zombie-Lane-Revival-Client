package com.dchoc.spriteobject;

import com.dchoc.toolkit.MathUtils;

public class ParticleObject {
   public static final int MILLISECONDS_TO_SECONDS = 10;
   private int acceleration;
   private SpriteObject animation;
   private int duration;
   private int dx;
   private int dy;
   private int elapsedTime;
   private int gravityX;
   private int gravityY;
   private boolean isAlive;
   private int maxVelocity;
   private int minVelocity;
   private int velocity;
   private int x;
   private int y;

   public boolean isAlive() {
      return this.isAlive;
   }

   public void render(int offsetX, int offsetY) {
      if (this.isAlive) {
         int elapsedSquared = this.elapsedTime * this.elapsedTime >>> 10;
         int target = 0;
         if (this.acceleration != 0) {
            if (this.acceleration > 0) {
               target = this.maxVelocity;
            } else {
               target = this.minVelocity;
            }
         }

         int accelerationTime = this.acceleration != 0 ? (target - this.velocity << 10) / this.acceleration : 0;
         int distance;
         if (accelerationTime == 0) {
            distance = this.velocity * this.elapsedTime >> 10;
         } else if (this.elapsedTime < accelerationTime) {
            distance = (this.elapsedTime * this.velocity >> 10) + (this.acceleration * elapsedSquared >> 11);
         } else {
            distance = (target * (this.elapsedTime - accelerationTime) >> 10)
               + ((accelerationTime * accelerationTime >>> 10) * this.acceleration >> 11)
               + (this.velocity * accelerationTime >> 10);
         }

         int moveX = this.x + (this.dx * distance >> 15);
         int moveY = (distance * this.dy >> 15) + this.y;
         int gravityOffsetX = moveX + (this.gravityX * elapsedSquared >> 11);
         int gravityOffsetY = moveY + (elapsedSquared * this.gravityY >> 11);
         this.animation.setElapsedTime(this.elapsedTime * this.animation.getAnimationLength() / this.duration);
         this.animation.draw(gravityOffsetX + offsetX, gravityOffsetY + offsetY);
      }
   }

   public void setAcceleration(int value) {
      this.acceleration = value;
   }

   public void setAlive(boolean alive) {
      this.isAlive = alive;
      if (!alive) {
         this.elapsedTime = 0;
      }
   }

   public void setAngle(int angle) {
      int radians = 65536 * angle / 360;
      this.dx = MathUtils.cos(radians);
      this.dy = -MathUtils.sin(radians);
   }

   public void setAnimation(SpriteObject spriteObject) {
      this.animation = spriteObject;
   }

   public void setDuration(int value) {
      this.duration = value;
   }

   public void setGravity(int gravityXValue, int gravityYValue) {
      this.gravityX = gravityXValue;
      this.gravityY = gravityYValue;
   }

   public void setStartLocation(int startX, int startY) {
      this.x = startX;
      this.y = startY;
   }

   public void setVelocity(int value) {
      this.velocity = Math.min(this.maxVelocity, Math.max(this.minVelocity, value));
   }

   public void setVelocity(int min, int max) {
      this.minVelocity = min;
      this.maxVelocity = max;
   }

   public void update(int deltaTime) {
      this.elapsedTime += deltaTime;
      if (this.elapsedTime >= this.duration) {
         this.setAlive(false);
      }
   }
}
