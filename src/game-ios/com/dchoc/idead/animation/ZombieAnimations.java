package com.dchoc.idead.animation;

import com.dchoc.idead.characters.ZombieCharacter;

public class ZombieAnimations extends CharacterAnimations {
   public static final int ANIMATION_RESPAWN_DRAW_IDLE_TIME = 5200;

   public ZombieAnimations(int initialAnimation) {
      super(initialAnimation);
   }

   public void init(ZombieCharacter zombie) {
      super.init(zombie);
   }

   @Override
   public void doDraw(float x, float y) {
      if (this.getAnimation() == 11 && this.getElapsedTime() >= 5200) {
         int idle = this.getAnimationDirection(this.mDirection);
         if (isValidAnimation(idle, this.mAnimations)) {
            this.mAnimations[idle].draw(x, y, this.mDirection == 3 ? 1 : 0);
         }
      }

      super.doDraw(x, y);
   }
}
