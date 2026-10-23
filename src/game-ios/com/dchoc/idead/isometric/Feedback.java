package com.dchoc.idead.isometric;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.math.Vec2;
import com.dchoc.spriteobject.SpriteObject;

public class Feedback {
   private SpriteObject mAnimation = AnimationsManager.loadShared(71311);
   private int mElapsedTime;
   private Vec2 mPosition = new Vec2();

   public Feedback() {
      this.mElapsedTime = this.mAnimation.getAnimationLength();
   }

   public void init(float x, float y) {
      this.mPosition.set(x, y);
      this.mElapsedTime = 0;
   }

   public int logicUpdate(int delta) {
      this.mElapsedTime += delta;
      return 0;
   }

   public void doDraw(Camera camera) {
      if (this.isActive()) {
         float x = this.mPosition.getX();
         float cameraX = camera.getX();
         float y = this.mPosition.getY();
         float cameraY = camera.getY();
         this.mAnimation.setElapsedTime(this.mElapsedTime);
         this.mAnimation.draw(x - cameraX, y - cameraY);
      }
   }

   public boolean isActive() {
      return this.mElapsedTime < this.mAnimation.getAnimationLength();
   }

   public int getElapsedTime() {
      return this.mElapsedTime;
   }
}
