package com.dchoc.idead.objects;

import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.items.Item;
import com.dchoc.math.Trigonometry;
import com.dchoc.math.Vec2;

public class DynamicObject extends InteractiveObject {
   public static final int DIRECTION_DOWN = 0;
   public static final int DIRECTION_LEFT = 3;
   public static final int DIRECTION_RIGHT = 2;
   public static final int DIRECTION_UP = 1;
   protected int mDirection;
   protected float mInitialSpeed;
   protected Vec2 mSpeed;
   protected Vec2 mTargetPosition = new Vec2();

   public DynamicObject(IsometricScene scene, int type) {
      super(scene, type);
      this.mSpeed = new Vec2();
   }

   public void init(float x, float y, Item item, float speed) {
      super.init(x, y, item);
      this.mInitialSpeed = speed;
      this.mDirection = 0;
   }

   @Override
   public int logicUpdate(int delta) {
      this.updatePosition(delta);
      return super.logicUpdate(delta);
   }

   public void updatePosition(int delta) {
      if (this.mSpeed.getX() != 0.0F || this.mSpeed.getY() != 0.0F) {
         float speedX = this.mSpeed.getX();
         float speedY = this.mSpeed.getY();
         float targetX = this.mTargetPosition.getX();
         float time = delta / 1000.0F;
         float positionX = this.mPosition.getX();
         float targetY = this.mTargetPosition.getY();
         float stepX = speedX * time;
         float stepY = time * speedY;
         float distanceX = targetX - positionX;
         float distanceY = targetY - this.mPosition.getY();
         if (stepX * stepX + stepY * stepY >= distanceX * distanceX + distanceY * distanceY) {
            this.setPosition(this.mTargetPosition.getX(), this.mTargetPosition.getY());
            this.mSpeed.set(0.0F, 0.0F);
         } else {
            this.setPosition(stepX + this.mPosition.getX(), stepY + this.mPosition.getY());
         }
      }
   }

   public boolean moveTo(float x, float y) {
      this.mTargetPosition.set(x, y);
      this.calculateSpeed(x - this.mPosition.getX(), y - this.mPosition.getY(), this.mInitialSpeed);
      return true;
   }

   public void resetSpeed() {
      this.mSpeed.set(0.0F, 0.0F);
   }

   public void setDirection(int direction) {
      this.mDirection = direction;
   }

   public int getDirection() {
      return this.mDirection;
   }

   public void setInitialSpeed(float speed) {
      this.mInitialSpeed = speed;
   }

   public float getInitialSpeed() {
      return this.mInitialSpeed;
   }

   public float getTargetX() {
      return this.mTargetPosition.getX();
   }

   public float getTargetY() {
      return this.mTargetPosition.getY();
   }

   protected void calculateSpeed(float x, float y, float speed) {
      double angle = Trigonometry.atan2(y, x);
      this.mSpeed.set((float)(speed * Math.cos(angle)), (float)(speed * Math.sin(angle)));
      if (Math.abs(this.mSpeed.getX()) < Math.abs(this.mSpeed.getY())) {
         this.mDirection = this.mSpeed.getY() < 0.0F ? 1 : 0;
      } else {
         this.mDirection = this.mSpeed.getX() < 0.0F ? 3 : 2;
      }
   }
}
