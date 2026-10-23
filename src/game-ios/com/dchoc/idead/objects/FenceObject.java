package com.dchoc.idead.objects;

import com.dchoc.idead.actions.Action;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.characters.ActionCharacter;
import com.dchoc.idead.isometric.Camera;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.items.FenceItem;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.spriteobject.SpriteObject;

public class FenceObject extends ObjectWithHealth {
   public static final int ANIMATION_CORNER_LOWERLEFT = 3;
   public static final int ANIMATION_CORNER_LOWERRIGHT = 4;
   public static final int ANIMATION_CORNER_UPPERLEFT = 5;
   public static final int ANIMATION_CORNER_UPPERRIGHT = 6;
   public static final int ANIMATION_HORIZONTAL = 1;
   public static final int ANIMATION_T_SECTION_DOWN = 7;
   public static final int ANIMATION_T_SECTION_LEFT = 8;
   public static final int ANIMATION_T_SECTION_RIGHT = 9;
   public static final int ANIMATION_T_SECTION_UP = 10;
   public static final int ANIMATION_VERTICAL = 0;
   public static final int ANIMATION_X_SECTION = 2;
   private static boolean smAnimationLoaded;
   private static SpriteObject smShakeAnimation;

   public FenceObject(IsometricScene scene) {
      super(scene, 5);
      if (!smAnimationLoaded) {
         smShakeAnimation = AnimationsManager.loadShared(78059, DeviceWrapper.useHDIngameGraphics() ? 2 : 1);
         smAnimationLoaded = true;
      }
   }

   public void init(float x, float y, FenceItem item) {
      super.init(x, y, item);
   }

   @Override
   public boolean canInteractWith(ActionCharacter actor) {
      return this.getHealth() != this.getMaxHealth();
   }

   @Override
   public Action interactWith(ActionCharacter actor) {
      return this.hasMaxHealth() ? null : actor.addAction(4, this);
   }

   @Override
   public int logicUpdate(int delta) {
      int result = super.logicUpdate(delta);
      if (result != 0) {
         return result;
      } else {
         return this.isAlive() ? 0 : 1;
      }
   }

   @Override
   public boolean doDraw(Camera camera) {
      if (!this.isAlive()) {
         return false;
      }

      float screenX = this.mToScreen.getX();
      float screenY = this.mToScreen.getY();
      if (this.isFixing()) {
         smShakeAnimation.setElapsedTime(this.mFixTimer);
         int shakeX = smShakeAnimation.getTimelineX();
         int shakeY = smShakeAnimation.getTimelineY();
         this.mToScreen.add(shakeX, shakeY);
      }

      boolean drawn = super.doDraw(camera);
      if (this.isFixing()) {
         this.mToScreen.set(screenX, screenY);
      }

      return drawn;
   }

   @Override
   public SpriteObject getCollision() {
      SpriteObject[] collisions = this.mItem.getCollisions();
      if (collisions == null) {
         return null;
      } else {
         return collisions.length < this.mAnimations.getAnimation() ? null : collisions[this.mAnimations.getAnimation()];
      }
   }

   public void updateEdges() {
      if (this.mTile != null) {
         IsometricTile up = this.mScene.getTile(this.mTile.getColumn(), this.mTile.getRow() - 1);
         boolean upFence = false;
         if (up != null) {
            upFence = up.hasFence();
         }

         IsometricTile down = this.mScene.getTile(this.mTile.getColumn(), this.mTile.getRow() + 1);
         boolean downFence = false;
         if (down != null) {
            downFence = down.hasFence();
         }

         IsometricTile left = this.mScene.getTile(this.mTile.getColumn() - 1, this.mTile.getRow());
         boolean leftFence = false;
         if (left != null) {
            leftFence = left.hasFence();
         }

         IsometricTile right = this.mScene.getTile(this.mTile.getColumn() + 1, this.mTile.getRow());
         boolean rightFence = false;
         if (right != null) {
            rightFence = right.hasFence();
         }

         this.setEdges(upFence, downFence, leftFence, rightFence);
      }
   }

   @Override
   protected void updateAnimations(int delta) {
      if (this.mAnimations != null) {
         int count = this.mAnimations.getAnimationFrameCount();
         if (count >= 2) {
            int frame = Math.min(this.getMaxHealth() - this.mHealth, count - 1);
            if (this.mAnimations.getAnimationFrame() != frame) {
               this.mAnimations.setAnimationFrame(frame);
            }
         }
      }
   }

   public void setEdges(boolean up, boolean down, boolean left, boolean right) {
      if (up || down || !left && !right) {
         if ((up || down) && !left && !right) {
            this.mAnimations.changeAnimation(0, true);
         } else if (up && down && left && right) {
            this.mAnimations.changeAnimation(2, true);
         } else if (up && !down && !left && right) {
            this.mAnimations.changeAnimation(3, true);
         } else if (up && !down && left && !right) {
            this.mAnimations.changeAnimation(4, true);
         } else if (!up && down && !left && right) {
            this.mAnimations.changeAnimation(5, true);
         } else if (!up && down && left && !right) {
            this.mAnimations.changeAnimation(6, true);
         } else if (!up && down && left && right) {
            this.mAnimations.changeAnimation(7, true);
         } else if (up && down && left && !right) {
            this.mAnimations.changeAnimation(8, true);
         } else if (up && down && !left && right) {
            this.mAnimations.changeAnimation(9, true);
         } else if (up && !down && left && right) {
            this.mAnimations.changeAnimation(10, true);
         }
      } else {
         this.mAnimations.changeAnimation(1, true);
      }

      this.updateAnimations(0);
   }
}
