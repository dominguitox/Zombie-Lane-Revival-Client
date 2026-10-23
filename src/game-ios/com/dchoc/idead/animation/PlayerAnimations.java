package com.dchoc.idead.animation;

import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.actions.Action;
import com.dchoc.idead.actions.AttackAction;
import com.dchoc.idead.areas.Area;
import com.dchoc.idead.areas.AreaCircus;
import com.dchoc.idead.areas.AreaCure;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.items.AvatarItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.items.PlayerItem;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.ToolkitHelpers;
import java.util.Vector;

public class PlayerAnimations extends CharacterAnimations {
   private int mCurrentWeaponFrame;
   private WeaponItem mWeapon;
   private Vector mItems = new Vector(13);
   private float mAnimationFactor;
   private float mAnimationRemainder;

   public PlayerAnimations(int initialAnimation) {
      super(initialAnimation);
   }

   @Override
   public void init(IsometricObject object) {
      super.init(object);
      this.loadItems();
      this.mCurrentWeaponFrame = 0;
      this.mAnimationFactor = 1.0F;
      this.mAnimationRemainder = 0.0F;
   }

   @Override
   public int logicUpdate(int delta) {
      if (((PlayerCharacter)this.mObject).isMoving()) {
         float scaled = this.mAnimationRemainder + delta * this.mAnimationFactor;
         delta = (int)scaled;
         this.mAnimationRemainder = scaled - delta;
      }

      return super.logicUpdate(delta);
   }

   @Override
   public void release() {
      super.release();
      this.unloadItems();
   }

   public void setAnimationFactor(float factor) {
      this.mAnimationFactor = factor;
   }

   public float getAnimationFactor() {
      return this.mAnimationFactor;
   }

   @Override
   public void doDraw(float x, float y) {
      PlayerCharacter player = (PlayerCharacter)this.mObject;
      PlayerItem playerItem = (PlayerItem)player.getItem();
      AvatarDescription avatar = player.getAvatar();
      if (playerItem.getGender() != avatar.getGender()) {
         int state = this.getAnimation();
         int direction = this.getDirection();
         int time = this.getElapsedTime();
         boolean looping = this.isAnimationLooping();
         playerItem = ItemManager.getPlayer(avatar.getGender());
         this.loadAnimations(playerItem);
         this.mObject.setItem(playerItem);
         this.changeAnimation(state, direction, looping);
         this.setElapsedTime(time);
      }

      if (this.hasItemsChanged()) {
         this.updateItems();
      }

      int time = this.getElapsedTime();
      AvatarItem skin = avatar.getItem(2123);
      int direction = this.mDirection;
      boolean restrictToCostume = false;
      if (avatar.getGender() != 43 && !avatar.isEquippedWithID(28)) {
         restrictToCostume = !avatar.isEquippedWithID(37);
      }

      int[] drawingOrder = AvatarItem.DRAWING_ORDER;

      for (int i = 0; drawingOrder != null && i < drawingOrder.length; i++) {
         int layer = drawingOrder[i];
         if (layer == 2121) {
            if (skin != null && skin.getColorModification() != -8355712) {
               OGL.pushParameters();
               OGL.setColorModification(skin.getColorModification());
            }

            SpriteObject[] torso = playerItem.getTorsoAnimations();
            if (isValidAnimation(this.mAnimation, torso)) {
               torso[this.mAnimation].setElapsedTime(time);
               torso[this.mAnimation].draw(x, y, direction == 3 ? 1 : 0);
            }

            if (skin != null && skin.getColorModification() != -8355712) {
               OGL.popParameters();
            }
         } else if (layer == 2122) {
            if (skin != null && skin.getColorModification() != -8355712) {
               OGL.pushParameters();
               OGL.setColorModification(skin.getColorModification());
            }

            if (isValidAnimation(this.mAnimation, this.mAnimations)) {
               this.mAnimations[this.mAnimation].draw(x, y, direction == 3 ? 1 : 0);
            }

            if (skin != null && skin.getColorModification() != -8355712) {
               OGL.popParameters();
            }
         }

         AvatarItem item = avatar.getItem(drawingOrder[i]);
         if (item != null && item.getGender() == avatar.getGender() && (!restrictToCostume || item.getSubType() == 2116)) {
            this.drawItem(x, y, item, time);
         }
      }

      if (avatar.getGender() != 43) {
         Area area = GameEngineUtils.getArea();
         if (area != null) {
            if (area.getName() == 3818) {
               AreaCircus circus = (AreaCircus)area;
               if (circus.useMalletAnimation(player)) {
                  this.drawWeapon(x, y, circus.getMallet(), time);
                  return;
               }
            } else if (area.getName() == 5560) {
               AreaCure cure = (AreaCure)area;
               if (cure.useFlamethrowerAnimation(player)) {
                  this.drawWeapon(x, y, cure.getFlamethrower(), time);
                  return;
               }
            }
         }

         if (player.getState() != 5 && player.getState() != 7 || this.mWeapon != null && this.mWeapon.getID() == 49) {
            if (this.mWeapon == null) {
               return;
            }

            this.drawWeapon(x, y, this.mWeapon, time);
         } else {
            WeaponItem weapon = ItemManager.getWeapon(49);
            weapon.loadAnimations();
            this.drawWeapon(x, y, weapon, time);
         }
      }
   }

   @Override
   public void changeAnimation(int state, boolean looping) {
      super.changeAnimation(state, looping);
      this.mCurrentWeaponFrame = 0;
   }

   protected void drawItem(float x, float y, Item item, int time) {
      SpriteObject[] animations = item.getAnimations();
      int index = this.mAnimation;
      int color = -8355712;
      if (item.getType() == 1911) {
         color = ((AvatarItem)item).getColorModification();
      } else if (item.getType() == 1907) {
         PlayerCharacter player = (PlayerCharacter)this.mObject;
         AvatarDescription avatar = player.getAvatar();
         if (player.getState() == 13) {
            index = this.getAnimationIndex(3);
         }

         if (avatar.getGender() == 1922) {
            index += (animations == null ? 0 : animations.length) >> 1;
         }
      }

      int direction = this.mDirection;
      if (isValidAnimation(this.mAnimation, animations)) {
         animations[index].setElapsedTime(time);
         if (color == -8355712) {
            animations[index].draw(x, y, direction == 3 ? 1 : 0);
            return;
         }

         OGL.pushParameters();
         OGL.setColorModification(color);
         animations[index].draw(x, y, direction == 3 ? 1 : 0);
         OGL.popParameters();
      }
   }

   private void loadItems() {
      PlayerCharacter player = (PlayerCharacter)this.mObject;
      Vector items = player.getAvatar().getItems();
      this.mItems.removeAllElements();

      for (int i = 0; i < items.size(); i++) {
         Item item = (Item)items.elementAt(i);
         item.loadAnimations();
         this.mItems.addElement(item);
      }

      this.mWeapon = player.getWeapon();
      if (this.mWeapon != null) {
         this.mWeapon.loadAnimations();
      }
   }

   public boolean hasItemsChanged() {
      Vector items = ((PlayerCharacter)this.mObject).getAvatar().getItems();
      if (this.mItems.size() != items.size()) {
         return true;
      }

      if (this.mWeapon != this.getCurrentWeapon()) {
         return true;
      }

      for (int i = 0; i < this.mItems.size(); i++) {
         Item cached = (Item)this.mItems.elementAt(i);
         Item item = (Item)items.elementAt(i);
         if (cached.getID() != item.getID()) {
            return true;
         }
      }

      return false;
   }

   private void updateItems() {
      Vector items = ((PlayerCharacter)this.mObject).getAvatar().getItems();
      int i = 0;

      while (i < this.mItems.size()) {
         Item item = (Item)this.mItems.elementAt(i);
         if (items.indexOf(item) == -1) {
            item.unloadAnimations();
            ToolkitHelpers.fastRemove(i, this.mItems);
         } else {
            i++;
         }
      }

      for (int var4 = 0; var4 < items.size(); var4++) {
         Item item = (Item)items.elementAt(var4);
         if (this.mItems.indexOf(item) == -1) {
            item.loadAnimations();
            this.mItems.addElement(item);
         }
      }

      WeaponItem weapon = this.getCurrentWeapon();
      if (this.mWeapon != weapon) {
         if (this.mWeapon != null) {
            this.mWeapon.unloadAnimations();
         }

         if (weapon != null) {
            weapon.loadAnimations();
         }

         this.mWeapon = weapon;
      }
   }

   public WeaponItem getCurrentWeapon() {
      PlayerCharacter player = (PlayerCharacter)this.mObject;
      if (player.isAttacking()) {
         Action action = player.getCurrentAction();
         if (action != null && action.getType() == 2) {
            return ((AttackAction)action).getWeapon();
         }
      }

      return player.getWeapon();
   }

   private void unloadItems() {
      if (this.mWeapon != null) {
         this.mWeapon.unloadAnimations();
      }

      for (int i = 0; i < this.mItems.size(); i++) {
         ((Item)this.mItems.elementAt(i)).unloadAnimations();
      }
   }

   private void drawWeapon(float x, float y, WeaponItem weapon, int time) {
      PlayerCharacter player = (PlayerCharacter)this.mObject;
      this.drawItem(x, y, weapon, time);
      SpriteObject[] animations = weapon.getAnimations();
      if (player.isAttacking()) {
         if (isValidAnimation(this.mAnimation, animations)) {
            int index = this.mAnimation;
            AvatarDescription avatar = player.getAvatar();
            if (avatar.getGender() == 1922) {
               index += animations.length >> 1;
            }

            if (this.mCurrentWeaponFrame != animations[index].getCurrentFrameIndex()) {
               boolean hasSound = animations[index].getCollisionBox(11) != null;
               if (hasSound) {
                  weapon.playWeaponSound();
               }

               if (animations[index].getCollisionBox(10) != null) {
                  this.mAnimationMarks[1] = true;
               } else if (!hasSound) {
                  this.clearMarks();
               }
            } else {
               this.clearMarks();
            }

            this.mCurrentWeaponFrame = animations[index].getCurrentFrameIndex();
         }
      }
   }
}
