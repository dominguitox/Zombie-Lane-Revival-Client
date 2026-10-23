package com.dchoc.idead.objects;

import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.items.DestructibleItem;
import com.dchoc.toolkit.ToolkitHelpers;

public class ObjectWithHealth extends InteractiveObject {
   private boolean mFixable;
   protected int mHealth;

   public ObjectWithHealth(IsometricScene scene, int type) {
      super(scene, type);
   }

   public void init(float x, float y, DestructibleItem item) {
      super.init(x, y, item);
      this.mHealth = item.getMaxHealth();
      this.mFixable = item.isFixable();
      if (this.mAnimations != null) {
         int maxHealth = item.getMaxHealth();
         int count = this.mAnimations.getAnimationCount();
         if (maxHealth == 1 && count >= 2 && !item.isFixable()) {
            this.mAnimations.changeAnimation(ToolkitHelpers.getRandomInt(0, count - 1), true);
         } else {
            if (this.hasRandomElapsedTime()) {
               this.updateAnimations(0);
               this.mAnimations.setElapsedTime(ToolkitHelpers.getRandomInt(0, this.mAnimations.getAnimationLength()));
            }
         }
      }
   }

   @Override
   public void remove() {
      super.remove();
      this.mHealth = 0;
   }

   public void increaseHealth(int amount) {
      this.setHealth(amount + this.mHealth);
   }

   public void reduceHealth(int amount) {
      this.setHealth(Math.max(0, this.mHealth - amount));
   }

   public void setHealth(int health) {
      this.mHealth = Math.max(Math.min(health, this.getMaxHealth()), 0);
      if (this.showDeathAnimation()) {
         this.mAnimations.changeAnimation(this.mAnimations.getAnimationCount() - 1, false);
      }
   }

   public int getHealth() {
      return Math.min(this.mHealth, this.getMaxHealth());
   }

   public void setRealHealth(int health) {
      this.mHealth = health;
      if (this.showDeathAnimation()) {
         this.mAnimations.changeAnimation(this.mAnimations.getAnimationCount() - 1, false);
      }
   }

   public int getRealHealth() {
      return this.mHealth;
   }

   public int getMaxHealth() {
      return ((DestructibleItem)this.mItem).getMaxHealth();
   }

   public boolean hasMaxHealth() {
      return this.getHealth() == this.getMaxHealth();
   }

   public boolean isAlive() {
      return this.getHealth() > 0;
   }

   @Override
   protected void updateAnimations(int delta) {
      super.updateAnimations(delta);
      if (this.mAnimations != null) {
         if (!this.showDeathAnimation()) {
            int maxHealth = this.getMaxHealth();
            boolean deathAnimation = this.hasDeathAnimation();
            int count = this.mAnimations.getAnimationCount();
            if (deathAnimation) {
               count--;
            }

            if (count >= 2) {
               if (maxHealth >= 2 || this.mFixable) {
                  int animation = this.mAnimations.getAnimation();
                  int health = this.getRealHealth();
                  if (this.mFixable && maxHealth == 1) {
                     animation = Math.min(Math.max(1 - health, 0), count - 1);
                  } else if (count < maxHealth) {
                     animation = Math.min(Math.max(count - count * health / maxHealth, 0), count - 1);
                  } else if (count == maxHealth) {
                     animation = Math.min(Math.max(maxHealth - health, 0), count - 1);
                  } else if (count > maxHealth) {
                     animation = Math.min(Math.max(health + (count - maxHealth), 0), count - 1);
                  }

                  if (this.mAnimations.getAnimation() != animation) {
                     this.mAnimations.changeAnimation(animation, true);
                  }
               }
            }
         }
      }
   }

   public boolean hasRandomElapsedTime() {
      return this.mItem != null && this.mItem.getID() == 5599;
   }

   public boolean hasDeathAnimation() {
      if (this.mItem == null) {
         return false;
      }

      if (this.mItem.getSubType() == 3797) {
         return true;
      }

      int bit = this.mItem.getID() - 5576;
      return bit >= 0 && bit <= 23 ? (8405249 >>> bit & 1) != 0 : false;
   }

   public boolean showDeathAnimation() {
      return this.isAlive() ? false : this.hasDeathAnimation();
   }

   public boolean isAnimationFinished() {
      return !this.showDeathAnimation() ? true : this.mAnimations.isAnimationFinished();
   }
}
