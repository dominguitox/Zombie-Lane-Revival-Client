package com.dchoc.idead.animation;

import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.items.Item;
import com.dchoc.spriteobject.SpriteObject;

public class Animations {
   public static final int ANIM_MARK_ACTION = 1;
   public static final int ANIM_MARK_COUNT = 2;
   public static final int ANIM_MARK_SFX = 0;
   protected IsometricObject mObject;
   protected Item mItem;
   protected int mAnimation;
   protected SpriteObject[] mAnimations;
   protected boolean[] mAnimationMarks = new boolean[2];

   public void init(IsometricObject object) {
      this.mObject = object;
      this.loadAnimations(object.getItem());
      this.clearMarks();
   }

   public void release() {
      this.unloadAnimations();
   }

   public int logicUpdate(int delta) {
      if (isValidAnimation(this.mAnimation, this.mAnimations)) {
         this.mAnimations[this.mAnimation].logicUpdate(delta);
      }

      return 0;
   }

   protected static boolean isValidAnimation(int index, SpriteObject[] animations) {
      return animations != null && index >= 0 && index < animations.length && animations[index] != null;
   }

   public void doDraw(float x, float y) {
      if (isValidAnimation(this.mAnimation, this.mAnimations)) {
         this.mAnimations[this.mAnimation].draw(x, y);
      }
   }

   public boolean isAnimationFinished() {
      return isValidAnimation(this.mAnimation, this.mAnimations) && this.mAnimations[this.mAnimation].isFinishedAnimation();
   }

   public boolean getAnimationMark(int mark) {
      boolean result = this.mAnimationMarks[mark];
      this.mAnimationMarks[mark] = false;
      return result;
   }

   public void clearMarks() {
      if (this.mAnimationMarks != null) {
         for (int i = 0; i < this.mAnimationMarks.length; i++) {
            this.mAnimationMarks[i] = false;
         }
      }
   }

   public void changeAnimation(int index, boolean looping) {
      this.mAnimation = index;
      if (isValidAnimation(index, this.mAnimations)) {
         this.mAnimations[this.mAnimation].setAnimation(0, looping ? -1 : 1, false);
         this.clearMarks();
      }
   }

   public Item getItem() {
      return this.mItem;
   }

   public int getAnimationCount() {
      return this.mAnimations == null ? 0 : this.mAnimations.length;
   }

   public int getAnimation() {
      return this.mAnimation;
   }

   public int getAnimationLength() {
      return isValidAnimation(this.mAnimation, this.mAnimations) ? this.mAnimations[this.mAnimation].getAnimationLength() : 0;
   }

   public int getAnimationFrameWidth() {
      return isValidAnimation(this.mAnimation, this.mAnimations) ? this.mAnimations[this.mAnimation].getFrameWidth() : 0;
   }

   public int getAnimationFrameHeight() {
      return isValidAnimation(this.mAnimation, this.mAnimations) ? this.mAnimations[this.mAnimation].getFrameHeight() : 0;
   }

   public int getAnimationFramePivotX() {
      return isValidAnimation(this.mAnimation, this.mAnimations) ? this.mAnimations[this.mAnimation].getPivotY() : 0;
   }

   public int getAnimationFramePivotY() {
      return isValidAnimation(this.mAnimation, this.mAnimations) ? this.mAnimations[this.mAnimation].getPivotY() : 0;
   }

   public int getAnimationFrameCount() {
      return isValidAnimation(this.mAnimation, this.mAnimations) ? this.mAnimations[this.mAnimation].getFrameCount() : 0;
   }

   public int getAnimationFrame() {
      return isValidAnimation(this.mAnimation, this.mAnimations) ? this.mAnimations[this.mAnimation].getCurrentFrameIndex() : 0;
   }

   public SpriteObject getActiveAnimation() {
      return this.mAnimations[this.mAnimation];
   }

   public void setAnimationFrame(int frame) {
      if (isValidAnimation(this.mAnimation, this.mAnimations)) {
         this.mAnimations[this.mAnimation].setAnimationFrame(frame);
      }
   }

   public void setElapsedTime(int time) {
      if (isValidAnimation(this.mAnimation, this.mAnimations)) {
         this.mAnimations[this.mAnimation].setElapsedTime(time);
      }
   }

   public int getElapsedTime() {
      return isValidAnimation(this.mAnimation, this.mAnimations) ? this.mAnimations[this.mAnimation].getElapsedTime() : 0;
   }

   public void setAnimationLooping(boolean looping) {
      if (isValidAnimation(this.mAnimation, this.mAnimations)) {
         this.mAnimations[this.mAnimation].setLooping(looping ? -1 : 1);
      }
   }

   public boolean isAnimationLooping() {
      return isValidAnimation(this.mAnimation, this.mAnimations) && this.mAnimations[this.mAnimation].isLooping();
   }

   protected void loadAnimations(Item item) {
      if (this.mItem != item) {
         this.unloadAnimations();
         this.mItem = item;
         SpriteObject[] source = item.loadAnimations();
         if (source != null) {
            this.mAnimations = new SpriteObject[source.length];

            for (int i = 0; i < source.length; i++) {
               this.mAnimations[i] = source[i] == null ? null : source[i].clone();
            }
         }

         this.mAnimation = 0;
      }
   }

   protected void unloadAnimations() {
      if (this.mItem != null) {
         if (this.mAnimations != null) {
            AnimationsManager.unload(this.mAnimations);
            this.mAnimations = null;
         }

         this.mItem.unloadAnimations();
         this.mItem = null;
      }
   }
}
