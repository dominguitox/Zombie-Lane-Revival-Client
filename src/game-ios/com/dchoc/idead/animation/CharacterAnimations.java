package com.dchoc.idead.animation;

import com.dchoc.idead.isometric.IsometricObject;

public class CharacterAnimations extends Animations {
   public static final int DIRECTION_DOWN = 0;
   public static final int DIRECTION_UP = 1;
   public static final int DIRECTION_RIGHT = 2;
   public static final int DIRECTION_LEFT = 3;
   public static final int DIRECTIONS_COUNT = 4;
   public static final int ANIMATION_DIRECTION_DOWN = 0;
   public static final int ANIMATION_DIRECTION_UP = 1;
   public static final int ANIMATION_DIRECTION_SIDE = 2;
   public static final int ANIMATION_DIRECTIONS_COUNT = 3;
   protected int mDirection;
   protected int mInitialAnimation;

   public CharacterAnimations(int initialAnimation) {
      this.mInitialAnimation = initialAnimation;
   }

   @Override
   public void init(IsometricObject object) {
      super.init(object);
      this.mDirection = 0;
      this.changeAnimation(this.mInitialAnimation, true);
   }

   @Override
   public void changeAnimation(int state, boolean looping) {
      super.changeAnimation(state * 3 + this.getAnimationDirection(this.mDirection), looping);
   }

   public int getAnimationIndex(int state) {
      return state * 3 + this.getAnimationDirection(this.mDirection);
   }

   public void changeAnimation(int state, int direction, boolean looping) {
      this.mDirection = direction;
      this.changeAnimation(state, looping);
   }

   public boolean hasAnimation(int state) {
      for (int i = 0; i < 3; i++) {
         if (isValidAnimation(state * 3 + i, this.mAnimations)) {
            return true;
         }
      }

      return false;
   }

   public void changeDirection(int direction) {
      if (this.mDirection != direction) {
         int state = this.getAnimation();
         int time = this.getElapsedTime();
         boolean looping = this.isAnimationLooping();
         this.changeAnimation(state, direction, looping);
         this.setElapsedTime(time);
      }
   }

   @Override
   public int getAnimation() {
      return (this.mAnimation - this.getAnimationDirection(this.mDirection)) / 3;
   }

   public int getDirection() {
      return this.mDirection;
   }

   @Override
   public void doDraw(float x, float y) {
      if (isValidAnimation(this.mAnimation, this.mAnimations)) {
         this.mAnimations[this.mAnimation].draw(x, y, this.mDirection == 3 ? 1 : 0);
      }
   }

   protected int getAnimationDirection(int direction) {
      return switch (direction) {
         case 1 -> 1;
         case 2, 3 -> 2;
         default -> 0;
      };
   }
}
