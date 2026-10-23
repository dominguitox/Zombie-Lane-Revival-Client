package com.dchoc.idead.actions;

import com.dchoc.hud.HUD;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.areas.Area;
import com.dchoc.idead.characters.ActionCharacter;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.characters.ZombieCharacter;
import com.dchoc.idead.events.EventManager;
import com.dchoc.idead.events.TimedEvent;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.DestructibleItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.idead.player.InventoryItem;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tracking.EarnGCEvent;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public class AttackAction extends Action {
   public static final int STATE_ATTACKING = 0;
   public static final int STATE_ATTACK = 1;
   public static final int STATE_FINISH_ATTACK = 2;
   public static final int STATE_ATTACKED = 3;
   public static final int STATE_COUNTER_ATTACK = 4;
   public static final int STATE_COUNTER_ATTACKED = 5;
   public static final int STATE_COMPLETED = 6;
   private ZombieCharacter mZombie;
   private WeaponItem mWeapon;
   private int mDamage;
   private int mState;
   private EarnGCEvent mEarnEvent = new EarnGCEvent(5);

   public AttackAction() {
      super((byte)2);
   }

   @Override
   public void init(ActionCharacter actor, IsometricObject target) {
      super.init(actor, target);
      this.mZombie = (ZombieCharacter)target;
      this.mWeapon = actor.getWeapon();
      this.mDamage = this.mZombie.getHitDamage(this.mWeapon);
   }

   @Override
   public int logicUpdate(int delta) {
      super.logicUpdate(delta);
      switch (this.mState) {
         case 0:
            if (this.mActor.getState() == 0) {
               this.changeState(1);
            }
            break;
         case 1:
            if (this.mActor.getState() == 0 || this.mActor.getState() == 14) {
               this.changeState(2);
            }
            break;
         case 2:
            if (this.mActor.getState() == 0) {
               this.changeState(3);
            }
            break;
         case 3:
            if (this.mZombie.getState() != 0) {
               if (!this.mZombie.isValid()) {
                  this.changeState(6);
               }
            } else if (this.mActor.isPlayer()
               && this.mZombie.isAlive()
               && !this.mWeapon.isCrafted()
               && IsometricUtils.calculateDistance(this.mZombie, this.mActor) < 2) {
               this.changeState(4);
            } else {
               this.changeState(6);
            }
            break;
         case 4:
            if (this.mZombie.getState() == 0) {
               this.changeState(5);
            }
            break;
         case 5:
            if (this.mActor.getState() == 0) {
               this.changeState(6);
            }
      }

      return 0;
   }

   public void changeState(int state) {
      Item item = this.mZombie.getItem();
      if (state == 1) {
         this.mActor.turnTo(this.mZombie.getX(), this.mZombie.getY());
         this.mActor.attack(this.mZombie, this.mActor.getWeapon());
         this.mZombie.setHighlighted(false);
      } else if (state == 2) {
         if (this.mDamage < 1) {
            DestructibleItem destructible = (DestructibleItem)this.mZombie.getItem();
            int text = destructible.isZombieMilitary() ? 2745 : (this.mZombie.getDefense() == 100 ? 4279 : (destructible.isKilledOnlyCraftable() ? 1705 : -1));
            if (text != -1) {
               this.addFloater(Toolkit.getText(text));
            }
         } else {
            this.mZombie.removeHealth(this.mDamage);
            HUD.updateHealthBarAtActionStart(this.mZombie);
            this.mZombie.hit(this.mWeapon);
            this.addReward(this.mZombie.getScreenX(), this.mZombie.getScreenY(), item);
         }

         SpriteObject hitAnimation = this.mWeapon.getHitAnimation();
         if (hitAnimation != null) {
            this.mZombie.showWeaponHitAnimation(hitAnimation);
         }

         if (GameEngineUtils.isAtNeigborsPlace()) {
            PlayerProfile.addNeighbrAction(item, this.mDamage);
         }

         if (this.mActor.isPlayer() && !PlayerProfile.useWeapon(this.mWeapon)) {
            this.mActor.cancelActions(false);
         }

         if (this.mZombie.isAlive()) {
            this.mZombie.alertAfterGettingHit();
         }

         this.updateCounters();
      } else if (state == 4) {
         this.mZombie.turnTo(this.mActor.getX(), this.mActor.getY());
         this.mZombie.attack();
      } else if (state == 6) {
         this.mZombie.setFreezed(false);
         this.mZombie.setHighlighted(false);
      }

      this.mState = state;
   }

   @Override
   public void prepare() {
      super.prepare();
      this.mZombie.setFreezed(true);
      this.mZombie.setHighlighted(true);
   }

   @Override
   public void start() {
      super.start();
      if (!TutorialFlow.isCompleted() && !TutorialFlow.isCollectingRewards()) {
         this.stop(Toolkit.getText(1707));
      } else if (this.mZombie != null && this.mZombie.isAlive() && this.mZombie.isValid() && this.mWeapon != null) {
         DestructibleItem item = (DestructibleItem)this.mZombie.getItem();
         Mission mission = MissionManager.getMission(item.getUnlockMission());
         if (mission != null) {
            if (!mission.isCompleted() && mission.isActive()) {
               this.stop(null);
               EventManager.addEvent(new TimedEvent(0, 2895, null, mission));
               return;
            }

            if (!mission.isCompleted()) {
               this.stop(Toolkit.getText(1707));
               this.updateCounters();
               return;
            }
         }

         if (this.consumeCost(this.mEnergyCost)) {
            this.changeState(0);
         }
      } else {
         this.stop(null);
      }
   }

   @Override
   public boolean consumeCost(int amount) {
      if (this.mActor.isPlayer()) {
         InventoryItem inventoryItem = PlayerProfile.getInventory().getItem(this.mWeapon);
         if (inventoryItem == null || !inventoryItem.isUsable()) {
            this.stop(null);
            return false;
         }
      }

      this.getEvent().setParams(21, this.mTarget.getItem().getSubType(), this.mWeapon.getID());
      return super.consumeCost(amount);
   }

   @Override
   public void stop(String text) {
      super.stop(text);
      this.changeState(6);
   }

   @Override
   public boolean isCompleted() {
      return this.mState == 6;
   }

   @Override
   public void cancel() {
      this.closeTooltip();
      this.mZombie.setHighlighted(false);
      this.mZombie.setFreezed(false);
      if (this.mStarted) {
         this.mActor.stop();
      }
   }

   public WeaponItem getWeapon() {
      return this.mWeapon;
   }

   public int getDamage() {
      return this.mDamage;
   }

   @Override
   protected void addReward(float x, float y, Item item) {
      int xpMultiplier = 1;
      if (this.mWeapon != null && this.mWeapon.getXpMultiplier() > 1) {
         xpMultiplier = this.mWeapon.getXpMultiplier();
      }

      int effect = PlayerProfile.getEffectPercentage(3273);
      IsometricScene scene = IsometricUtils.getScene();
      if (item.getRewardCoins() > 0) {
         int coins = item.getRewardCoins();
         int reward = effect > 0 ? coins + Math.max(1, coins * effect / 100) : coins;
         scene.getLootRewards().addCoins(x, y, reward, effect > 0);
         this.mEarnEvent.trackEarn(reward);
      }

      if (item.getRewardXp() > 0) {
         int xp = item.getRewardXp() * xpMultiplier;
         int reward = effect > 0 ? xp + Math.max(1, xp * effect / 100) : xp;
         scene.getLootRewards().addXp(x, y, reward, effect > 0);
      }

      if (item.getRewardEnergy() > 0) {
         int energy = item.getRewardEnergy();
         int reward = effect > 0 ? energy + Math.max(1, energy * effect / 100) : energy;
         scene.getLootRewards().addEnergy(x, y, reward, effect > 0);
      }

      Item rewardItem = this.mZombie.getRewardItem();
      if (rewardItem != null && !rewardItem.isLockedByMission() && !this.mZombie.isAlive()) {
         int count = 1 + (effect > 0 ? Math.max(1, effect / 100) : 0);
         scene.getLootRewards().addItem(x, y, rewardItem, count, effect > 0);
      }
   }

   protected void updateCounters() {
      Item item = this.mZombie.getItem();
      if (this.mZombie.isAlive()) {
         MissionManager.updateCounter(2489, item.getID(), 1);
         MissionManager.updateCounter(2489, item.getSubType(), 1);
         MissionManager.updateCounter(2489, 80, 1);
      } else {
         Area area = GameEngineUtils.getArea();
         if (area.getName() != -1) {
            MissionManager.updateCounter(2593, area.getName(), item.getID(), 1);
            MissionManager.updateCounter(2593, area.getName(), item.getSubType(), 1);
            MissionManager.updateCounter(2593, area.getName(), 80, 1);
            if (area.getName() == 2514) {
               MissionManager.updateCounter(2593, 1926, item.getID(), 1);
               MissionManager.updateCounter(2593, 1926, item.getSubType(), 1);
               MissionManager.updateCounter(2593, 1926, 80, 1);
            }

            if (area.getType() == 2 && this.mWeapon.isCrafted()) {
               MissionManager.updateCounter(2658, this.mWeapon.getID(), item.getID(), 1);
               MissionManager.updateCounter(2658, this.mWeapon.getID(), 80, 1);
               MissionManager.updateCounter(2658, this.mWeapon.getSubType(), item.getID(), 1);
               MissionManager.updateCounter(2658, this.mWeapon.getSubType(), 80, 1);
            }
         }

         MissionManager.updateCounter(95, item.getID(), 1);
         MissionManager.updateCounter(95, item.getSubType(), 1);
         MissionManager.updateCounter(95, 80, 1);
         if (this.mHired) {
            MissionManager.updateCounter(78, 118, item.getID(), 1);
            MissionManager.updateCounter(78, 118, item.getSubType(), 1);
            MissionManager.updateCounter(78, 118, 80, 1);
         }

         if (this.mActor.isPlayer()) {
            AvatarDescription avatar = ((PlayerCharacter)this.mActor).getAvatar();
            MissionManager.updateCounter(78, avatar.getCharacter(), item.getID(), 1);
            MissionManager.updateCounter(78, avatar.getCharacter(), item.getSubType(), 1);
            MissionManager.updateCounter(78, avatar.getCharacter(), 80, 1);
         }

         if (this.mActor.isPlayer()) {
            MissionManager.updateCounter(78, this.mWeapon.getID(), item.getID(), 1);
            MissionManager.updateCounter(78, this.mWeapon.getID(), item.getSubType(), 1);
            MissionManager.updateCounter(78, this.mWeapon.getID(), 80, 1);
            MissionManager.updateCounter(78, this.mWeapon.getSubType(), item.getID(), 1);
            MissionManager.updateCounter(78, this.mWeapon.getSubType(), 80, 1);
            if (this.mWeapon.isCrafted()) {
               MissionManager.updateCounter(78, 2661, item.getID(), 1);
               MissionManager.updateCounter(78, 2661, item.getSubType(), 1);
               MissionManager.updateCounter(78, 2661, 80, 1);
            }
         }
      }
   }
}
