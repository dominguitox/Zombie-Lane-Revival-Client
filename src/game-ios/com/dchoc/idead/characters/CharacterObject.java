package com.dchoc.idead.characters;

import com.dchoc.idead.animation.CharacterAnimations;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.objects.DynamicObject;
import java.util.Vector;

public class CharacterObject extends DynamicObject {
   public static final int INITIAL_PATH_CAPACITY = 4;
   public static final int STATE_IDLE = 0;
   public static final int STATE_MOVING = 1;
   public static final int STATE_MELEE = 2;
   public static final int STATE_SHOOT = 3;
   public static final int STATE_HIT = 4;
   public static final int STATE_DIG = 5;
   public static final int STATE_RESPAWN = 6;
   public static final int STATE_REPAIR = 7;
   public static final int STATE_THROW = 9;
   public static final int STATE_PUNCH = 10;
   public static final int STATE_SHOOT_CRAFTABLE = 11;
   public static final int STATE_SHOOT_BURST = 12;
   public static final int STATE_DIG_CRAFTABLE = 13;
   public static final int STATE_FINISH_ATTACK = 14;
   protected Vector mTargetPath = new Vector(4);
   protected int mState;
   protected int mHealth;
   protected int mHealthMax;

   public CharacterObject(IsometricScene scene, int type) {
      super(scene, type);
   }

   public void init(float x, float y, Item item, float speed, int health) {
      super.init(x, y, item, speed);
      this.mHealth = health;
      this.mHealthMax = health;
      this.changeState(0);
   }

   @Override
   public int logicUpdate(int delta) {
      int result = super.logicUpdate(delta);
      if (result != 0) {
         return result;
      }

      this.updateDirection();
      if (Integer.compareUnsigned(this.mState, 1) > 0 && this.mAnimations.getAnimationMark(1)) {
         this.changeState(14);
         return 0;
      }

      if (this.mAnimations.isAnimationFinished()) {
         this.changeState(0);
      }

      return 0;
   }

   public void eventRecieved(int type) {
      if (type == 4 || type == 5) {
         this.mSpeed.set(0.0F, 0.0F);
         this.changeState(0);
      }
   }

   @Override
   public void updatePosition(int delta) {
      if (this.mSpeed.getX() != 0.0F || this.mSpeed.getY() != 0.0F) {
         if (this.mState == 1) {
            float seconds = delta / 1000.0F;
            float stepX = this.mSpeed.getX() * seconds;
            float stepY = seconds * this.mSpeed.getY();
            float stepSquared = stepX * stepX + stepY * stepY;
            IsometricTile nextTile = null;
            float dx;
            float dy;
            if (this.mTargetPath.isEmpty()) {
               dx = this.mTargetPosition.getX() - this.mPosition.getX();
               dy = this.mTargetPosition.getY() - this.mPosition.getY();
            } else {
               nextTile = (IsometricTile)this.mTargetPath.lastElement();
               dx = nextTile.getX() - this.mPosition.getX();
               dy = nextTile.getY() - this.mPosition.getY();
            }

            float remainingSquared = dx * dx + dy * dy;
            if (stepSquared >= remainingSquared) {
               if (nextTile == null) {
                  this.setPosition(this.mTargetPosition.getX(), this.mTargetPosition.getY());
                  this.eventRecieved(5);
               } else {
                  this.setPosition(nextTile.getX(), nextTile.getY());
                  this.mTargetPath.removeElementAt(this.mTargetPath.size() - 1);
                  if (this.mTargetPath.isEmpty()) {
                     this.calculateSpeed(
                        this.mTargetPosition.getX() - this.mPosition.getX(), this.mTargetPosition.getY() - this.mPosition.getY(), this.mInitialSpeed
                     );
                  } else {
                     IsometricTile tile = (IsometricTile)this.mTargetPath.lastElement();
                     this.calculateSpeed(tile.getX() - this.mPosition.getX(), tile.getY() - this.mPosition.getY(), this.mInitialSpeed);
                  }
               }
            } else {
               this.setPosition(stepX + this.mPosition.getX(), stepY + this.mPosition.getY());
            }
         }
      }
   }

   @Override
   public boolean moveTo(float x, float y) {
      if (this.mTargetPath == null) {
         return false;
      }

      this.mTargetPath.removeAllElements();
      if (this.mPosition.getX() == x && this.mPosition.getY() == y) {
         this.eventRecieved(5);
         return true;
      }

      boolean pathFound = this.mScene
         .getPathfinder()
         .findPath(this.mPosition.getX(), this.mPosition.getY(), x, y, this.mTargetPath, this.mType != 2, this.mType != 2);
      if (pathFound) {
         this.mTargetPosition.set(x, y);
         if (this.mTargetPath.isEmpty()) {
            this.calculateSpeed(x - this.mPosition.getX(), y - this.mPosition.getY(), this.mInitialSpeed);
         } else {
            IsometricTile tile = (IsometricTile)this.mTargetPath.lastElement();
            this.calculateSpeed(tile.getX() - this.mPosition.getX(), tile.getY() - this.mPosition.getY(), this.mInitialSpeed);
         }

         this.changeState(1);
         this.eventRecieved(3);
         return true;
      } else {
         this.changeState(0);
         this.eventRecieved(4);
         return false;
      }
   }

   public void turnTo(float x, float y) {
      float dx = x - this.mPosition.getX();
      float dy = y - this.mPosition.getY();
      int direction;
      if (Math.abs(dx) > Math.abs(dy)) {
         direction = dx < 0.0F ? 3 : 2;
      } else {
         direction = dy < 0.0F ? 1 : 0;
      }

      this.mDirection = direction;
      this.updateDirection();
   }

   public void addHealth(int amount) {
      this.mHealth = Math.min(this.mHealth + amount, this.mHealthMax);
   }

   public void removeHealth(int amount) {
      this.mHealth = Math.max(this.mHealth - amount, 0);
   }

   public boolean isAlive() {
      return this.mHealth > 0;
   }

   public void setHealth(int health) {
      this.mHealth = Math.max(Math.min(health, this.mHealthMax), 0);
   }

   public int getHealth() {
      return this.mHealth;
   }

   public int getHealthMax() {
      return this.mHealthMax;
   }

   public int getState() {
      return this.mState;
   }

   @Override
   public void remove() {
      super.remove();
      this.mState = 0;
      this.mHealth = 0;
   }

   public void updateDirection() {
      CharacterAnimations animations = (CharacterAnimations)this.mAnimations;
      if (animations.getDirection() != this.mDirection && this.mDirection >= 0 && this.mDirection <= 3) {
         animations.changeDirection(this.mDirection);
      }
   }

   public void changeState(int state) {
      if (this.mState == 1 && state != 1) {
         this.resetSpeed();
      }

      this.mState = state;
   }

   @Override
   protected void changeTile(IsometricTile tile) {
      this.eventRecieved(10);
      super.changeTile(tile);
      this.eventRecieved(6);
   }
}
