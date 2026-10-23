package com.dchoc.idead.actions;

import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.areas.Area;
import com.dchoc.idead.characters.ActionCharacter;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.DestructibleItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.objects.ObjectWithHealth;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.idead.player.Inventory;
import com.dchoc.idead.player.InventoryItem;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tracking.EarnGCEvent;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.WindowRequiredItem;

public class LootAction extends Action {
   public static final int STATE_LOOT = 0;
   public static final int STATE_LOOTED = 1;
   public static final int STATE_COMPLETED = 2;
   public static final int STATE_REQUIRED_ITEMS = 3;
   private ObjectWithHealth mLoot;
   private int mState;
   private EarnGCEvent mEarnEvent = new EarnGCEvent(6);

   public LootAction() {
      super((byte)4);
   }

   @Override
   public void init(ActionCharacter actor, IsometricObject target) {
      super.init(actor, target);
      this.mLoot = (ObjectWithHealth)target;
   }

   @Override
   public int logicUpdate(int delta) {
      super.logicUpdate(delta);
      if (this.mState == 1) {
         this.changeState(2);
      } else if (this.mState == 0 && this.mActor.getState() == 0) {
         this.changeState(1);
      }

      return 0;
   }

   public void changeState(int state) {
      Item item = this.mLoot.getItem();
      if (state == 2) {
         if (!this.hasMoreActions(this.mLoot)) {
            this.mLoot.setHighlighted(false);
         }
      } else if (state == 1) {
         if (this.mLoot.getType() == 5) {
            this.mLoot.increaseHealth(1);
         } else {
            this.mLoot.reduceHealth(1);
         }

         if (GameEngineUtils.isAtNeigborsPlace()) {
            PlayerProfile.addNeighbrAction(item, 1);
         }

         if (this.mActor.isPlayer()) {
            InventoryItem weapon = PlayerProfile.getCurrentWeapon();
            if (weapon == null || !weapon.isUsable()) {
               PlayerProfile.switchWeapon();
               this.mActor.cancelActions(false);
            }
         }

         this.updateCounters();
         this.addReward(this.mLoot.getScreenX(), this.mLoot.getScreenY(), this.mLoot.getItem());
      } else if (state == 0) {
         this.mActor.turnTo(this.mLoot.getX(), this.mLoot.getY());
         if (this.mLoot.getType() == 5) {
            this.mActor.repair(this.mLoot);
            this.mLoot.setFixing(true);
         } else {
            DestructibleItem destructible = (DestructibleItem)item;
            if (item.getSubType() != 1990 && item.getSubType() != 49) {
               if (!destructible.isFixable() && !this.useRepairAnimation(destructible)) {
                  this.mActor.dig(this.mLoot);
               } else {
                  this.mActor.repair(this.mLoot);
               }
            }
         }
      }

      this.mState = state;
   }

   @Override
   public void prepare() {
      super.prepare();
      this.mLoot.setHighlighted(true);
   }

   @Override
   public void start() {
      this.mState = 2;
      super.start();
      if (!TutorialFlow.isCompleted()) {
         Item item = this.mLoot.getItem();
         if ((TutorialFlow.getState() != 1 || item.getSubType() != 49) && (TutorialFlow.getState() != 5 || item.getSubType() != 1990)) {
            this.stop(Toolkit.getText(1707));
            return;
         }
      }

      if (this.mLoot == null || !this.mLoot.isAlive() || !this.mLoot.isValid()) {
         this.stop(null);
      } else if (IsometricUtils.calculateDistance(this.mActor, this.mTarget) > 1) {
         this.stop(Toolkit.getText(1999));
      } else {
         Item item = this.mLoot.getItem();
         if (item.isLockedByMission()) {
            int text = item.getSubType() == 5571 && item.getID() != 5570 ? 4205 : 1707;
            this.stop(Toolkit.getText(text));
         } else if (this.mLoot.isAlerted()) {
            this.stop(Toolkit.getText(1706));
         } else if (this.mLoot.getType() == 5 && this.mLoot.hasMaxHealth()) {
            this.stop(null);
         } else {
            if (this.mLoot.getType() == 5) {
               this.getEvent().setParams(23, this.mLoot.getItem().getSubType(), -1);
            } else if (this.mLoot.getType() == 0) {
               this.getEvent().setParams(25, this.mLoot.getItem().getSubType(), -1);
            } else if (this.mLoot.getType() == 3) {
               this.getEvent().setParams(24, this.mLoot.getItem().getSubType(), -1);
            }

            if (this.tryConsumeCost(this.mEnergyCost, true)) {
               this.changeState(0);
            }
         }
      }
   }

   public boolean tryConsumeCost(int amount, boolean showWindow) {
      DestructibleItem item = (DestructibleItem)this.mLoot.getItem();
      int[][] required = item.getItemsPerClick();
      if (required != null && showWindow && item.getSubType() == 4758) {
         this.changeState(3);
         WindowRequiredItem window = (WindowRequiredItem)WindowManager.getWindow((byte)34);
         window.setItem(this, required);
         WindowManager.openWindow((byte)34);
         return false;
      } else {
         return this.consumeCost(amount);
      }
   }

   @Override
   public void stop(String text) {
      super.stop(text);
      this.changeState(2);
   }

   @Override
   public boolean isCompleted() {
      return this.mState == 2;
   }

   @Override
   public void cancel() {
      this.closeTooltip();
      this.mLoot.setHighlighted(false);
      if (this.mStarted) {
         this.mActor.stop();
      }
   }

   @Override
   public boolean consumeCost(int amount) {
      Item item = this.mLoot.getItem();
      Inventory inventory = PlayerProfile.getInventory();
      int[][] required = ((DestructibleItem)item).getItemsPerClick();
      if (required == null) {
         return super.consumeCost(amount);
      }

      for (int i = 0; i < required.length; i++) {
         int[] row = required[i];
         Item requiredItem = ItemManager.getItem(row[0]);
         if (requiredItem != null && inventory.getItemCount(requiredItem) < row[1]) {
            this.stop(Toolkit.getText(this.getItemFloater(row[0])));
            this.mActor.cancelActions(false);
            return false;
         }
      }

      if (!super.consumeCost(amount)) {
         return false;
      }

      for (int i = 0; i < required.length; i++) {
         int[] row = required[i];
         inventory.removeItem(ItemManager.getItem(row[0]), row[1]);
      }

      return true;
   }

   protected void updateCounters() {
      Item item = this.mLoot.getItem();
      Area area = GameEngineUtils.getArea();
      if (area.getName() != -1) {
         int action = this.mLoot.getType() == 5 ? 2597 : 2513;
         MissionManager.updateCounter(action, area.getName(), item.getID(), 1);
         MissionManager.updateCounter(action, area.getName(), item.getSubType(), 1);
         MissionManager.updateCounter(action, area.getName(), 80, 1);
         if (area.getName() == 2514) {
            MissionManager.updateCounter(action, 1926, item.getID(), 1);
            MissionManager.updateCounter(action, 1926, item.getSubType(), 1);
            MissionManager.updateCounter(action, 1926, 80, 1);
         }
      }

      if (this.mLoot.getType() == 5) {
         MissionManager.updateCounter(2495, item.getID(), 1);
         MissionManager.updateCounter(2495, item.getSubType(), 1);
         MissionManager.updateCounter(2495, 80, 1);
         if (this.mActor.isPlayer()) {
            AvatarDescription avatar = ((PlayerCharacter)this.mActor).getAvatar();
            MissionManager.updateCounter(2533, avatar.getCharacter(), item.getID(), 1);
            MissionManager.updateCounter(2533, avatar.getCharacter(), item.getSubType(), 1);
            MissionManager.updateCounter(2533, avatar.getCharacter(), 80, 1);
         }
      } else {
         MissionManager.updateCounter(112, item.getID(), 1);
         MissionManager.updateCounter(112, item.getSubType(), 1);
         MissionManager.updateCounter(112, 80, 1);
         if (this.mActor.isPlayer()) {
            AvatarDescription avatar = ((PlayerCharacter)this.mActor).getAvatar();
            MissionManager.updateCounter(2553, avatar.getCharacter(), item.getID(), 1);
            MissionManager.updateCounter(2553, avatar.getCharacter(), item.getSubType(), 1);
            MissionManager.updateCounter(2553, avatar.getCharacter(), 80, 1);
         }

         if (!this.mLoot.isAlive()) {
            MissionManager.updateCounter(3864, item.getID(), 1);
            MissionManager.updateCounter(3864, item.getSubType(), 1);
            MissionManager.updateCounter(3864, 80, 1);
         }
      }
   }

   @Override
   protected void addReward(float x, float y, Item item) {
      super.addReward(x, y, item);
      if (item != null) {
         IsometricScene scene = IsometricUtils.getScene();
         if (item.getRewardCoins() > 0) {
            this.mEarnEvent.trackEarn(item.getRewardCoins());
         }

         Item reward = this.mLoot.getRewardItem();
         if (!this.mLoot.isAlive() && reward != null && !reward.isLockedByMission()) {
            scene.getLootRewards().addItem(x, y, reward, 1, false);
         }
      }
   }

   public boolean useRepairAnimation(DestructibleItem item) {
      return item.getID() == 5586;
   }

   public int getItemFloater(int id) {
      if (id == 149) {
         return 4282;
      } else if (id == 2033) {
         return 4280;
      } else {
         return id == 2050 ? 4281 : 1707;
      }
   }
}
