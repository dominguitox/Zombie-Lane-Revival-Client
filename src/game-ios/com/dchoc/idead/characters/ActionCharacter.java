package com.dchoc.idead.characters;

import com.dchoc.hud.HUD;
import com.dchoc.idead.actions.Action;
import com.dchoc.idead.actions.ActionsManager;
import com.dchoc.idead.actions.MoveAction;
import com.dchoc.idead.animation.CharacterAnimations;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;
import java.util.Vector;

public class ActionCharacter extends CharacterObject {
   protected Vector mActions = new Vector(10);
   protected Action mCurrentAction;

   public ActionCharacter(IsometricScene scene, int type) {
      super(scene, type);
   }

   @Override
   public void init(float x, float y, Item item, float speed, int health) {
      super.init(x, y, item, speed, 1);
      this.mActions.removeAllElements();
      this.mCurrentAction = null;
   }

   @Override
   public void remove() {
      super.remove();
      this.cancelActions(true);
   }

   @Override
   public int logicUpdate(int delta) {
      int result = super.logicUpdate(delta);
      if (result != 0) {
         return result;
      }

      this.updateActions(delta);
      return 0;
   }

   public boolean hasActionWith(IsometricObject target) {
      if (target == null) {
         return false;
      }

      for (int i = 0; i < this.mActions.size(); i++) {
         if (((Action)this.mActions.elementAt(i)).getTarget() == target) {
            return true;
         }
      }

      return false;
   }

   public WeaponItem getWeapon() {
      return null;
   }

   public boolean isPlayer() {
      return false;
   }

   @Override
   public void eventRecieved(int type) {
      super.eventRecieved(type);
      if (type == 4) {
         this.cancelActions(true);
      }
   }

   public void stop() {
      this.changeState(0);
   }

   public void hit(IsometricObject target) {
      this.changeState(4);
      int sound = this.getSoundFX(this.mState, target);
      if (sound != -1) {
         iWrapper.playSoundFx(sound);
      }
   }

   public void dig(IsometricObject target) {
      this.changeState(5);
      int sound = this.getSoundFX(this.mState, target);
      if (sound != -1) {
         iWrapper.playSoundFx(sound);
      }
   }

   public void repair(IsometricObject target) {
      this.changeState(7);
      int sound = this.getSoundFX(this.mState, target);
      if (sound != -1) {
         iWrapper.playSoundFx(sound);
      }
   }

   public void attack(IsometricObject target, WeaponItem weapon) {
      if (weapon != null) {
         int animation = weapon.getAnimationType();
         switch (animation) {
            case 79:
               this.changeState(2);
               break;
            case 2102:
               this.changeState(3);
               break;
            case 2104:
               this.changeState(12);
               break;
            case 2107:
               this.changeState(13);
               break;
            case 2109:
               this.changeState(10);
               break;
            case 2111:
               this.changeState(11);
               break;
            case 2113:
               this.changeState(9);
         }

         int sound = this.getSoundFX(this.mState, target);
         if (sound != -1) {
            iWrapper.playSoundFx(sound);
         }
      }
   }

   public boolean isAttacking() {
      int offset = this.mState - 2;
      return Integer.compareUnsigned(offset, 12) < 0 && (3971 >>> offset & 1) != 0;
   }

   public boolean isMoving() {
      return this.mState == 1;
   }

   public boolean hasActions() {
      return this.mCurrentAction != null || !this.mActions.isEmpty();
   }

   public void cancelActions(boolean includeCurrent) {
      if (includeCurrent && this.mCurrentAction != null) {
         this.mCurrentAction.setClickCount(0);
         if (!this.mCurrentAction.isStarted() || this.mCurrentAction.isCancelable()) {
            this.mCurrentAction.cancel();
            ActionsManager.poolAction(this.mCurrentAction);
            this.mCurrentAction = null;
         }
      }

      for (int i = 0; i < this.mActions.size(); i++) {
         Action action = (Action)this.mActions.elementAt(i);
         action.setClickCount(0);
         action.cancel();
         ActionsManager.poolAction(action);
      }

      this.mActions.removeAllElements();
      HUD.closeTooltips(false);
      HUD.closeHealthBars(false, false);
   }

   public boolean addMoveAction(float x, float y) {
      Action last = this.getLastAction();
      if (last != null && last.getType() == 1) {
         if (last != this.mCurrentAction) {
            ((MoveAction)last).setTarget(x, y);
            return true;
         }

         last.cancel();
         ActionsManager.poolAction(this.mCurrentAction);
         this.mCurrentAction = null;
      }

      if (this.mActions.size() < 10) {
         Action action = ActionsManager.createAction(1);
         if (action != null) {
            action.init(this, null);
            ((MoveAction)action).setTarget(x, y);
            action.prepare();
            this.mActions.addElement(action);
            return true;
         }
      }

      return false;
   }

   public Action getCurrentAction() {
      return this.mCurrentAction;
   }

   public IsometricObject getCurrentActionTarget() {
      return this.mCurrentAction == null ? null : this.mCurrentAction.getTarget();
   }

   public Vector getActions() {
      return this.mActions;
   }

   public Action getLastAction() {
      return this.mActions.isEmpty() ? this.mCurrentAction : (Action)this.mActions.lastElement();
   }

   public Action addAction(int type, IsometricObject target) {
      if (this.hasActions()) {
         Action merged = this.tryMergeActions(this.mCurrentAction, type, target) ? this.mCurrentAction : null;
         if (merged != null) {
            return merged;
         }

         for (int i = 0; i < this.mActions.size(); i++) {
            Action action = (Action)this.mActions.elementAt(i);
            if (this.tryMergeActions(action, type, target)) {
               return action;
            }
         }
      }

      if (this.mActions.size() < 10) {
         Action action = ActionsManager.createAction(type);
         if (action != null) {
            action.init(this, target);
            action.prepare();
            this.mActions.addElement(action);
            return action;
         }
      }

      return null;
   }

   @Override
   public boolean isReleasable() {
      return true;
   }

   @Override
   public float getBoundsX() {
      SpriteObject[] collisions = this.mItem.getCollisions();
      if (collisions != null && collisions[0].getCollisionBox(0) != null) {
         float x = this.mToScreen.getX();
         CollisionBox box = collisions[0].getCollisionBox(0);
         return x + box.getX();
      } else {
         return super.getBoundsX();
      }
   }

   @Override
   public float getBoundsY() {
      SpriteObject[] collisions = this.mItem.getCollisions();
      if (collisions != null && collisions[0].getCollisionBox(0) != null) {
         float y = this.mToScreen.getY();
         CollisionBox box = collisions[0].getCollisionBox(0);
         return y + box.getY();
      } else {
         return super.getBoundsY();
      }
   }

   @Override
   public float getBoundsWidth() {
      SpriteObject[] collisions = this.mItem.getCollisions();
      if (collisions != null && collisions[0].getCollisionBox(0) != null) {
         CollisionBox box = collisions[0].getCollisionBox(0);
         return box.getWidth();
      } else {
         return super.getBoundsWidth();
      }
   }

   @Override
   public float getBoundsHeight() {
      SpriteObject[] collisions = this.mItem.getCollisions();
      if (collisions != null && collisions[0].getCollisionBox(0) != null) {
         CollisionBox box = collisions[0].getCollisionBox(0);
         return box.getHeight();
      } else {
         return super.getBoundsHeight();
      }
   }

   public void updateActions(int delta) {
      if (this.mCurrentAction != null && this.mCurrentAction.getClickCount() < 1 && this.mCurrentAction.isCompleted()) {
         if (this.mCurrentAction.getType() != 1) {
            HUD.checkHealthBarAfterAction(this.mCurrentAction.getTarget());
            HUD.checkTooltipAfterAction(this.mCurrentAction.getTarget());
         }

         ActionsManager.poolAction(this.mCurrentAction);
         this.mCurrentAction = null;

         for (int i = 0; i < this.mActions.size(); i++) {
            ((Action)this.mActions.elementAt(i)).prepare();
         }
      }

      if (this.mCurrentAction == null && !this.mActions.isEmpty()) {
         for (this.mCurrentAction = (Action)this.mActions.firstElement();
            this.mCurrentAction.getType() == 1 && this.mActions.size() >= 2;
            this.mCurrentAction = (Action)this.mActions.firstElement()
         ) {
            this.mActions.removeElementAt(0);
            this.mCurrentAction.cancel();
            ActionsManager.poolAction(this.mCurrentAction);
         }

         if (this.mCurrentAction.getType() == 1) {
            this.mActions.removeElementAt(0);
            this.mCurrentAction.start();
         } else {
            int range = 1;
            WeaponItem weapon = null;
            if (this.mCurrentAction.getType() == 2) {
               weapon = this.getWeapon();
               if (weapon != null) {
                  range = weapon.getRange();
               }
            }

            int distance = IsometricUtils.calculateDistance(this, this.mCurrentAction.getTarget());
            if (distance <= range) {
               if (distance == 1 && this.mCurrentAction.getType() == 2 && range > 1) {
                  IsometricTile rangedTile = IsometricUtils.getTileAsRangedAttack(this, this.mCurrentAction.getTarget());
                  if (rangedTile != null) {
                     MoveAction move = (MoveAction)ActionsManager.createAction(1);
                     move.init(this, null);
                     move.setTarget(rangedTile.getX(), rangedTile.getY());
                     move.prepare();
                     this.mCurrentAction = move;
                     move.start();
                  } else {
                     this.updateTooltipAtActionStart();
                     this.mActions.removeElementAt(0);
                     this.mCurrentAction.start();
                  }
               } else {
                  if (this.mCurrentAction.getType() != 2) {
                     this.updateTooltipAtActionStart();
                  }

                  this.mActions.removeElementAt(0);
                  this.mCurrentAction.start();
               }
            } else {
               IsometricTile tile = IsometricUtils.findClosestTile(this, this.mCurrentAction.getTarget(), range, true, true);
               if (tile == null) {
                  this.addFloater(Toolkit.getText(1999));
                  this.mCurrentAction = null;
                  this.cancelActions(true);
                  return;
               }

               MoveAction move = (MoveAction)ActionsManager.createAction(1);
               move.init(this, null);
               move.setTarget(tile.getX(), tile.getY());
               move.prepare();
               this.mCurrentAction = move;
               move.start();
            }
         }
      }

      if (this.mCurrentAction != null) {
         if (this.mCurrentAction.getClickCount() > 0 && this.mCurrentAction.isCompleted()) {
            this.mCurrentAction.prepare();
            this.mCurrentAction.start();
            this.updateTooltipAtActionStart();
         }

         this.mCurrentAction.logicUpdate(delta);
      }
   }

   public void addFloater(String text) {
      this.mScene.addFloater(text, this.mToScreen.getX(), this.mToScreen.getY());
   }

   @Override
   public void changeState(int state) {
      int previous = this.mState;
      if (previous == 1 && state != 1) {
         this.resetSpeed();
      }

      if (state != 14) {
         boolean looping = Integer.compareUnsigned(state, 2) < 0;
         ((CharacterAnimations)this.mAnimations).changeAnimation(this.getAnimation(state), looping);
      }

      this.mState = state;
   }

   public int getAnimation(int state) {
      return state;
   }

   public int getSoundFX(int state, IsometricObject target) {
      return -1;
   }

   public void updateTooltipAtActionStart() {
      if (this.mCurrentAction != null && Integer.compareUnsigned(this.mCurrentAction.getType() - 3, 3) < 0) {
         HUD.updateHealthBarAtActionStart(this.mCurrentAction.getTarget());
      }
   }

   public boolean tryMergeActions(Action action, int type, IsometricObject target) {
      if (action != null && action.getType() != 1 && action.getType() == type && action.getTarget() == target) {
         action.setClickCount(action.getClickCount() + 1);
         return true;
      } else {
         return false;
      }
   }
}
