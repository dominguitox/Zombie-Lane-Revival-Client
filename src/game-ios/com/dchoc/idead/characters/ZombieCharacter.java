package com.dchoc.idead.characters;

import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.actions.Action;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.animation.ZombieAnimations;
import com.dchoc.idead.isometric.Camera;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.DestructibleItem;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.items.ZombieItem;
import com.dchoc.idead.objects.FarmingObject;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.ToolkitHelpers;

public class ZombieCharacter extends CharacterObject {
   private static int smFollowers;
   private static boolean smAnimationLoaded;
   private static SpriteObject smAlertedAnimation;
   private static SpriteObject smWeaponHitAnimation;
   private static float smWeaponHitScreenX;
   private static float smWeaponHitScreenY;
   private int mLogicTimer;
   private int mDistanceToPlayer;
   private boolean mGuarding;
   private boolean mMoving;
   private boolean mFollowing;
   private boolean mLikesFences;
   private boolean mFreezed;
   private boolean mPendingAlert;
   private WeaponItem mHitWeapon;
   private int mSpawnID;
   private boolean mDrawingWeaponHit;

   public ZombieCharacter(IsometricScene scene) {
      super(scene, 2);
      if (!smAnimationLoaded) {
         smAlertedAnimation = AnimationsManager.loadShared(66365, this.mGfxProfile);
         smAlertedAnimation.setLooping(1);
         smAnimationLoaded = true;
      }
   }

   public static void resetFollowers() {
      smFollowers = 0;
   }

   public void loadAnimations() {
      if (!smAnimationLoaded) {
         smAlertedAnimation = AnimationsManager.loadShared(66365, this.mGfxProfile);
         smAlertedAnimation.setLooping(1);
         smAnimationLoaded = true;
      }
   }

   public void init(float x, float y, ZombieItem item) {
      super.init(x, y, item, Tuner.getZombieSpeed(), item.getMaxHealth());
      this.mFreezed = false;
      this.mGuarding = false;
      this.mMoving = true;
      this.mFollowing = false;
      this.mDistanceToPlayer = this.getDistanceToPlayer();
      this.mLikesFences = ToolkitHelpers.getRandomInt(0, 100) < Tuner.getProbabilityToLikeFences();
      this.mLogicTimer = this.getNextMove();
      this.mSpawnID = -1;
      this.mPendingAlert = false;
      this.mDrawingWeaponHit = false;
   }

   @Override
   public void remove() {
      super.remove();
      this.setHealth(0);
      this.setFollowingPlayer(false);
      this.setGuarding(false);
   }

   @Override
   public boolean canInteractWith(ActionCharacter actor) {
      return this.isAlive();
   }

   @Override
   public Action interactWith(ActionCharacter actor) {
      return actor.addAction(2, this);
   }

   @Override
   public int logicUpdate(int delta) {
      int result = super.logicUpdate(delta);
      if (result != 0) {
         return result;
      }

      this.setInitialSpeed(Tuner.getZombieSpeed());
      if (this.mState == 0 && !this.isAlive()) {
         this.mDrawingWeaponHit = false;
         return 1;
      }

      if (this.mState == 2) {
         int threshold = this.mAnimations.getAnimationLength() * 60 / 100;
         int elapsed = this.mAnimations.getElapsedTime();
         if (elapsed >= threshold && elapsed - delta < threshold) {
            this.eventRecieved(9);
         }
      }

      if (this.mDrawingWeaponHit && smWeaponHitAnimation != null) {
         smWeaponHitAnimation.logicUpdate(delta);
         if (smWeaponHitAnimation.isFinishedAnimation()) {
            this.mDrawingWeaponHit = false;
         }
      }

      this.updateLogic(delta);
      return 0;
   }

   public void showWeaponHitAnimation(SpriteObject animation) {
      if (animation != null) {
         smWeaponHitAnimation = animation;
         smWeaponHitAnimation.setAnimationFrame(0);
         smWeaponHitScreenX = this.mToScreen.getX();
         smWeaponHitScreenY = this.mToScreen.getY();
         this.mDrawingWeaponHit = true;
      }
   }

   @Override
   public void eventRecieved(int type) {
      super.eventRecieved(type);
      if (type == 3) {
         if (this.mFollowing && this.mPendingAlert) {
            this.playAlertSound();
            this.mPendingAlert = false;
         }
      } else if (type == 4) {
         this.setFollowingPlayer(false);
      } else if (type == 6 || type == 8) {
         this.mDistanceToPlayer = this.getDistanceToPlayer();
         if (TutorialFlow.isCompleted() && !GameEngineUtils.isAtNeigborsPlace() && !this.mGuarding) {
            if (!this.mFollowing) {
               if (this.isAlive()
                  && smFollowers < Tuner.getMaxFollowing()
                  && this.mDistanceToPlayer < Tuner.getDangerRange()
                  && ToolkitHelpers.getRandomInt(0, 100) < Tuner.getProbabilityToFollow()) {
                  this.setMoving(true);
                  this.setFollowingPlayer(true);
                  if (this.mState != 2) {
                     this.idle();
                     this.mLogicTimer = 0;
                  }
               }

               if (!this.mFollowing) {
                  return;
               }
            }

            if (this.mDistanceToPlayer > Tuner.getSafeRange()) {
               this.setFollowingPlayer(false);
               this.idle();
            } else if (this.mDistanceToPlayer < 2) {
               this.idle();
               if (this.mPendingAlert) {
                  this.playAlertSound();
                  this.mPendingAlert = false;
               }
            } else {
               PlayerCharacter player = this.mScene.getPlayer();
               if (player != null && player.getTile() != this.mScene.getTileAt(this.mTargetPosition.getX(), this.mTargetPosition.getY())) {
                  this.moveTo(player.getX(), player.getY());
               }
            }

            this.mScene.setUpdateAlertedObjects();
         }
      } else if (type == 9) {
         if (this.mDistanceToPlayer <= this.getHitRadius()) {
            PlayerCharacter player = this.mScene.getPlayer();
            if (player != null) {
               player.hit(this);
            }
         }
      } else if (type == 10) {
         IsometricTile tile = this.getTile();
         if (tile != null) {
            FarmingObject crop = tile.getCrop();
            if (crop != null && crop.isAttacked()) {
               crop.setAttacked(false);
            }
         }
      }
   }

   public int getNextMove() {
      return ToolkitHelpers.getRandomInt(0, 500) + 1750;
   }

   public boolean isFollowingPlayer() {
      return this.mFollowing;
   }

   public void setFollowingPlayer(boolean following) {
      if (this.mFollowing != following) {
         this.mFollowing = following;
         if (following) {
            this.mScene.addAlertedZombie(this);
            this.mPendingAlert = true;
            smFollowers++;
         } else {
            this.mScene.removeAlertedZombie(this);
            this.mPendingAlert = false;
            smFollowers--;
         }
      }
   }

   public boolean isAlerting() {
      return this.mFollowing || this.mGuarding;
   }

   public int getAlertRange() {
      return this.isZombieBoss() ? 4 : 2;
   }

   public int getHitRadius() {
      return this.isZombieBoss() ? 2 : 1;
   }

   public int getDistanceToPlayer() {
      PlayerCharacter player = this.mScene.getPlayer();
      if (player != null) {
         IsometricTile playerTile = player.getTile();
         IsometricTile tile = this.getTile();
         if (playerTile != null && tile != null) {
            int dx = Math.abs(playerTile.getColumn() - tile.getColumn());
            int dy = Math.abs(playerTile.getRow() - tile.getRow());
            return Math.max(dx, dy);
         }
      }

      return Integer.MAX_VALUE;
   }

   public boolean isMoving() {
      return this.mMoving;
   }

   public void setMoving(boolean moving) {
      this.mMoving = moving;
   }

   public void setGuarding(boolean guarding) {
      if (this.mGuarding != guarding) {
         this.mGuarding = guarding;
         if (guarding) {
            this.mScene.addAlertedZombie(this);
         } else {
            this.mScene.removeAlertedZombie(this);
         }
      }
   }

   public boolean isGuarding() {
      return this.mGuarding;
   }

   @Override
   public boolean doDraw(Camera camera) {
      if (!super.doDraw(camera)) {
         return false;
      }

      float x = this.mToScreen.getX() - camera.getX();
      float y = this.mToScreen.getY() - camera.getY();
      if (this.mFollowing || this.mGuarding) {
         int a = this.mAnimations.getAnimationFrameHeight();
         int b = this.mAnimations.getAnimationFramePivotY();
         smAlertedAnimation.setElapsedTime(AnimationsManager.getTimer());
         smAlertedAnimation.draw(x, y - (a - ((float)a - b)));
      }

      if (!TutorialFlow.isCompleted() && TutorialFlow.isCollectingRewards() && this.isAlive()) {
         TutorialFlow.drawArrow((int)x, (int)y, 0);
      }

      if (this.mDrawingWeaponHit && smWeaponHitAnimation != null) {
         smWeaponHitAnimation.draw(smWeaponHitScreenX - camera.getX(), smWeaponHitScreenY - camera.getY());
      }

      return true;
   }

   @Override
   public boolean isReleasable() {
      int id = this.mItem.getID();
      return id != 5564 && id != 6052 && id != 6220 ? this.isZombieBoss() : false;
   }

   public void respawn() {
      this.changeState(6);
   }

   public void hit(WeaponItem weapon) {
      this.mHitWeapon = weapon;
      this.changeState(4);
      if (!this.isAlive()) {
         this.setFollowingPlayer(false);
         this.setGuarding(false);
         IsometricTile tile = this.getTile();
         if (tile != null) {
            FarmingObject crop = tile.getCrop();
            if (crop != null && crop.isAttacked()) {
               crop.setAttacked(false);
            }
         }
      }

      this.playHitSound(weapon);
   }

   public int getHitDamage(WeaponItem weapon) {
      if (weapon == null) {
         return 1;
      }

      int damage = weapon.getDamage();
      if (this.isKilledOnlyCraftable() && !weapon.isCrafted()) {
         damage = 0;
      } else if (this.getDefense() == 100) {
         damage = 0;
      }

      if (weapon.hasSpecialDamages()) {
         int[][] special = weapon.getSpecialDamages();

         for (int i = 0; special != null && i < special.length; i++) {
            if (special[i][0] == this.mItem.getID()) {
               return special[i][1];
            }
         }
      }

      return damage;
   }

   public void playHitSound(WeaponItem weapon) {
      if (weapon != null) {
         DestructibleItem item = (DestructibleItem)this.mItem;
         int damageType = weapon.getDamageType();
         int sound;
         if (damageType == -1) {
            sound = item.getClearSound();
         } else if (damageType == 2108) {
            sound = 21;
         } else if (damageType == 2110) {
            sound = 22;
         } else if (damageType == 2112) {
            sound = 23;
         } else {
            sound = -1;
         }

         if (!this.isAlive()) {
            int destroySound = item.getDestroySound();
            if (destroySound != -1) {
               sound = destroySound;
            }
         }

         if (sound != -1) {
            iWrapper.playSoundFx(sound);
         }
      }
   }

   public void playAlertSound() {
      if (((DestructibleItem)this.mItem).getAlertSound() != -1) {
         iWrapper.playSoundFx(((DestructibleItem)this.mItem).getAlertSound());
      }
   }

   public void alertAfterGettingHit() {
      if (TutorialFlow.isCompleted() && !GameEngineUtils.isAtNeigborsPlace() && this.isAlive() && !this.mGuarding && smFollowers < Tuner.getMaxFollowing()) {
         this.setMoving(true);
         this.setFollowingPlayer(true);
      }
   }

   public void attack() {
      this.changeState(2);
   }

   public void idle() {
      this.changeState(0);
   }

   public boolean isZombieBoss() {
      return ((DestructibleItem)this.mItem).isZombieBoss();
   }

   public boolean isKilledOnlyCraftable() {
      return ((DestructibleItem)this.mItem).isKilledOnlyCraftable();
   }

   public int getDefense() {
      return ((DestructibleItem)this.mItem).getDefense();
   }

   public void setFreezed(boolean freezed) {
      this.mFreezed = freezed;
      if (freezed) {
         this.changeState(0);
      }
   }

   public boolean isFreezed() {
      return this.mFreezed;
   }

   public void setSpawnID(int id) {
      this.mSpawnID = id;
   }

   public int getSpawnID() {
      return this.mSpawnID;
   }

   @Override
   public void changeState(int state) {
      if (this.mState == 1 && state != 1) {
         this.resetSpeed();
      }

      ZombieAnimations animations = (ZombieAnimations)this.mAnimations;
      if ((state == 0 || state == 1) && this.mState != state) {
         animations.changeAnimation(state, true);
      } else if (state == 2) {
         animations.changeAnimation(2, false);
      } else if (state == 4) {
         boolean alive = this.isAlive();
         int animation = alive ? 3 : 7;
         if (this.mHitWeapon != null) {
            if (this.mHitWeapon.getDamageType() == 2110) {
               animation = alive ? 4 : 8;
            } else if (this.mHitWeapon.getDamageType() == 2108) {
               animation = alive ? 5 : 9;
            } else if (this.mHitWeapon.getDamageType() == 2112) {
               animation = alive ? 6 : 10;
            }
         }

         if (!animations.hasAnimation(animation)) {
            animation = alive ? 3 : 7;
         }

         animations.changeAnimation(animation, false);
      } else if (state == 6) {
         animations.changeAnimation(11, false);
      }

      this.mState = state;
   }

   public void updateLogic(int delta) {
      if (this.mState == 0 && !this.mFreezed) {
         this.mLogicTimer -= delta;
         if (this.mLogicTimer < 0) {
            int probability = 100;
            if (!this.mGuarding && !this.destroyCrops()) {
               probability = this.destroyFences() ? 100 : 40;
            }

            if (this.mFollowing && this.mDistanceToPlayer > 1) {
               probability = 0;
            }

            if (!TutorialFlow.isCompleted()) {
               probability = 75;
            }

            int random = ToolkitHelpers.getRandomInt(0, 100);
            if (probability < random) {
               if (this.mMoving) {
                  this.doMove();
               }
            } else {
               if (this.mFollowing) {
                  PlayerCharacter player = this.mScene.getPlayer();
                  if (player != null) {
                     this.turnTo(player.getX(), player.getY());
                  }
               }

               this.attack();
            }

            this.mLogicTimer = this.getNextMove();
         }
      }
   }

   public void doMove() {
      if (TutorialFlow.isCompleted() && !GameEngineUtils.isAtNeigborsPlace()) {
         if (!this.mFollowing) {
            IsometricTile tile = this.getTile();
            if (tile != null) {
               IsometricTile target = IsometricUtils.calculateNextTileForZombie(this, tile.getColumn(), tile.getRow());
               if (target != null) {
                  this.moveTo(target.getX(), target.getY());
               }
            }
         } else if (this.mDistanceToPlayer > 1) {
            PlayerCharacter player = this.mScene.getPlayer();
            if (player != null) {
               this.moveTo(player.getX(), player.getY());
            }
         }
      }
   }

   public boolean destroyCrops() {
      if (!GameEngineUtils.isAtNeigborsPlace() && !this.mFollowing) {
         IsometricTile tile = this.getTile();
         if (tile == null) {
            return false;
         }

         FarmingObject crop = tile.getCrop();
         if (crop != null && !crop.isDestroyed()) {
            if (!crop.isAttacked()) {
               crop.attacked();
            }

            crop.setFixing(true);
            this.setMoving(false);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean destroyFences() {
      return false;
   }
}
