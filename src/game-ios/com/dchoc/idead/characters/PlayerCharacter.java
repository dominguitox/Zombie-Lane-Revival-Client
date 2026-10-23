package com.dchoc.idead.characters;

import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.animation.PlayerAnimations;
import com.dchoc.idead.areas.Area;
import com.dchoc.idead.areas.AreaCircus;
import com.dchoc.idead.areas.AreaCure;
import com.dchoc.idead.isometric.Camera;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.items.DestructibleItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.PlayerItem;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.objects.ObjectWithHealth;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.idead.player.InventoryItem;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.spriteobject.SpriteObject;
import java.util.Vector;

public class PlayerCharacter extends ActionCharacter {
   private static boolean smAnimationLoaded;
   private static SpriteObject[] smArrowAnimation;
   private static SpriteObject smOuchAnimation;
   private static SpriteObject smStarsAnimation;
   protected AvatarDescription mAvatar;
   private boolean mDizzy;
   private int mDizzyTimer;

   public PlayerCharacter(IsometricScene scene) {
      this(scene, 1);
   }

   protected PlayerCharacter(IsometricScene scene, int type) {
      super(scene, type);
      this.loadAnimations();
   }

   public void init(float x, float y, PlayerItem item, AvatarDescription avatar) {
      this.mAvatar = avatar;
      float speed = this.isPlayer() ? Tuner.getPlayerSpeed(true) : Tuner.getCharacterSpeed();
      super.init(x, y, item, speed, 1);
      this.mDizzy = false;
      this.mDizzyTimer = 0;
   }

   @Override
   public int logicUpdate(int delta) {
      int result = super.logicUpdate(delta);
      if (result != 0) {
         return result;
      }

      if (this.isPlayer()) {
         float speed = Tuner.getPlayerSpeed(true);
         float effect = PlayerProfile.getEffectPercentage(3275);
         float factor = 1.0F;
         boolean boosted = false;
         if (effect > 0.0F) {
            factor = effect / 100.0F + 1.0F;
            speed += speed * effect / 100.0F;
            boosted = this.mScene.getPlayer() == this;
         }

         this.mSpeedBoosted = boosted;
         this.setMovingSpeed(speed, factor);
      } else {
         this.setMovingSpeed(Tuner.getCharacterSpeed(), 1.0F);
      }

      if (this.mDizzy) {
         this.mDizzyTimer += delta;
         if (this.mDizzyTimer > 2000) {
            this.mDizzy = false;
         }
      }

      return 0;
   }

   @Override
   public WeaponItem getWeapon() {
      InventoryItem item = PlayerProfile.getCurrentWeapon();
      return item == null ? null : (WeaponItem)item.getItem();
   }

   @Override
   public boolean isPlayer() {
      return true;
   }

   @Override
   public void eventRecieved(int type) {
      super.eventRecieved(type);
      if (type == 6 && TutorialFlow.isCompleted() && this.isPlayer() && this.getTile() != null) {
         Vector objects = this.mScene.getObjects(2);

         for (int i = 0; i < objects.size(); i++) {
            ((ZombieCharacter)objects.elementAt(i)).eventRecieved(8);
         }
      }
   }

   @Override
   public boolean doDraw(Camera camera) {
      if (!super.doDraw(camera)) {
         return false;
      }

      float x = this.mToScreen.getX() - camera.getX();
      float y = this.mToScreen.getY() - camera.getY();
      SpriteObject sprite;
      if (this.mState == 4) {
         sprite = smOuchAnimation;
      } else if (this.mDizzy) {
         sprite = smStarsAnimation;
      } else {
         if (this.mScene.getPlayer() != this) {
            return true;
         }

         int gender = this.mAvatar.getGender();
         int index = gender == 43 ? 2 : (gender == 1922 ? 1 : 0);
         sprite = smArrowAnimation[index];
      }

      sprite.setElapsedTime(AnimationsManager.getTimer());
      sprite.draw(x, y);
      return true;
   }

   @Override
   public void hit(IsometricObject target) {
      super.hit(target);
      this.mDizzy = true;
      this.mDizzyTimer = 0;
      MissionManager.updateCounter(2714, 80, 1);
   }

   public void setAvatar(AvatarDescription avatar) {
      this.mAvatar = avatar;
   }

   public AvatarDescription getAvatar() {
      return this.mAvatar;
   }

   @Override
   public int getAnimation(int state) {
      switch (state) {
         case 0:
            return 0;
         case 1:
            return 1;
         case 2:
            return 3;
         case 3:
            return 4;
         case 4:
            return 7;
         case 5:
            Area area = GameEngineUtils.getArea();
            if (area == null) {
               return 2;
            } else if (area.getName() == 3818) {
               AreaCircus circus = (AreaCircus)area;
               return circus.useMallet(this.getCurrentActionTarget()) ? circus.getMalletAnimation() : 2;
            } else {
               if (area.getName() == 5560) {
                  AreaCure cure = (AreaCure)area;
                  return cure.useFlamethrower(this.getCurrentActionTarget()) ? cure.getFlamethrowerAnimation() : 2;
               }

               return 2;
            }
         case 6:
         case 8:
         default:
            return 0;
         case 7:
            return this.mAvatar.getGender() == 43 ? 2 : 8;
         case 9:
            return 5;
         case 10:
            return 6;
         case 11:
            return 9;
         case 12:
            return 10;
         case 13:
            return 2;
      }
   }

   @Override
   public int getSoundFX(int state, IsometricObject target) {
      if (state == 4) {
         int gender = this.mAvatar.getGender();
         return gender == 43 ? 18 : (gender == 1922 ? 19 : 20);
      }

      if (state == 7) {
         return 52;
      }

      if (state == 5) {
         if (target != null && target.getType() == 3) {
            Item item = target.getItem();
            int health = ((ObjectWithHealth)target).getHealth();
            DestructibleItem destructible = (DestructibleItem)item;
            int sound = health < 2 ? destructible.getDestroySound() : destructible.getClearSound();
            return sound == -1 ? 32 : sound;
         } else {
            return 32;
         }
      } else {
         return -1;
      }
   }

   public void loadAnimations() {
      if (!smAnimationLoaded) {
         SpriteObject[] arrows = new SpriteObject[]{
            AnimationsManager.loadShared(66361, this.mGfxProfile),
            AnimationsManager.loadShared(74941, this.mGfxProfile),
            AnimationsManager.loadShared(74943, this.mGfxProfile)
         };
         smArrowAnimation = arrows;
         smOuchAnimation = AnimationsManager.loadShared(66357, this.mGfxProfile);
         smStarsAnimation = AnimationsManager.loadShared(66363, this.mGfxProfile);
         smAnimationLoaded = true;
      }
   }

   public void setMovingSpeed(float speed, float factor) {
      this.setInitialSpeed(speed);
      if (this.isPlayer()) {
         ((PlayerAnimations)this.mAnimations).setAnimationFactor(factor);
      }
   }
}
