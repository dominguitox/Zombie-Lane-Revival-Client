package com.dchoc.idead.actions;

import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.areas.Area;
import com.dchoc.idead.characters.ActionCharacter;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.FarmingItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.objects.FarmingObject;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tracking.EarnGCEvent;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.toolkit.Toolkit;

public class FarmingAction extends Action {
   public static final int STATE_HARVEST = 0;
   public static final int STATE_HARVESTED = 1;
   public static final int STATE_COMPLETED = 2;
   private FarmingObject mCrop;
   private int mState;
   private EarnGCEvent mEarnEvent = new EarnGCEvent(7);

   public FarmingAction() {
      super((byte)5);
   }

   @Override
   public void init(ActionCharacter actor, IsometricObject target) {
      super.init(actor, target);
      this.mCrop = (FarmingObject)target;
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
      Item item = this.mCrop.getItem();
      if (state == 2) {
         if (!this.hasMoreActions(this.mCrop)) {
            this.mCrop.setHighlighted(false);
         }
      } else if (state == 1) {
         boolean visiting = GameEngineUtils.isAtNeigborsPlace();
         if (!this.mCrop.isWithered() || !visiting && this.mActor.isPlayer()) {
            this.mCrop.harvest();
         } else {
            this.mCrop.setRevived(true);
            MissionManager.updateCounter(2291, 80, 1);
         }

         if (visiting) {
            PlayerProfile.addNeighbrAction(item, 0);
         }

         this.addReward(this.mCrop.getScreenX(), this.mCrop.getScreenY(), this.mCrop.getItem());
         this.updateCounters();
      } else if (state == 0) {
         this.mActor.turnTo(this.mCrop.getX(), this.mCrop.getY());
         FarmingItem farmingItem = (FarmingItem)item;
         if (item.getSubType() != 1938 || farmingItem.getMaxAge() >= 0) {
            this.mActor.dig(this.mCrop);
         }
      }

      this.mState = state;
   }

   @Override
   public void prepare() {
      super.prepare();
      this.mCrop.setHighlighted(true);
   }

   @Override
   public void start() {
      this.mState = 2;
      super.start();
      if (TutorialFlow.isCompleted()
         && this.mCrop != null
         && this.mCrop.isAlive()
         && !this.mCrop.isGrowing()
         && (!this.mCrop.isWithered() || !this.mActor.isPlayer() || GameEngineUtils.isAtNeigborsPlace())) {
         if (this.mCrop.isAlerted()) {
            this.stop(Toolkit.getText(1706));
         } else {
            this.getEvent().setParams(25, this.mCrop.getID(), -1);
            if (this.consumeCost(this.mEnergyCost)) {
               this.changeState(0);
            }
         }
      } else {
         this.stop(Toolkit.getText(1707));
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
      this.mCrop.setHighlighted(false);
      if (this.mStarted) {
         this.mActor.stop();
      }
   }

   protected void updateCounters() {
      Item item = this.mCrop.getItem();
      Area area = GameEngineUtils.getArea();
      if (area.getName() != -1) {
         MissionManager.updateCounter(2507, area.getName(), item.getID(), 1);
         MissionManager.updateCounter(2507, area.getName(), item.getSubType(), 1);
         MissionManager.updateCounter(2507, area.getName(), 80, 1);
         if (area.getName() == 2514) {
            MissionManager.updateCounter(2507, 1926, item.getID(), 1);
            MissionManager.updateCounter(2507, 1926, item.getSubType(), 1);
            MissionManager.updateCounter(2507, 1926, 80, 1);
         }
      }

      MissionManager.updateCounter(2498, item.getID(), 1);
      MissionManager.updateCounter(2498, item.getSubType(), 1);
      MissionManager.updateCounter(2498, 80, 1);
      if (this.mActor.isPlayer()) {
         AvatarDescription avatar = ((PlayerCharacter)this.mActor).getAvatar();
         MissionManager.updateCounter(2561, avatar.getCharacter(), item.getID(), 1);
         MissionManager.updateCounter(2561, avatar.getCharacter(), item.getSubType(), 1);
         MissionManager.updateCounter(2561, avatar.getCharacter(), 80, 1);
      }
   }

   @Override
   protected void addReward(float x, float y, Item item) {
      if (item != null) {
         IsometricScene scene = IsometricUtils.getScene();
         if (GameEngineUtils.isAtNeigborsPlace()) {
            FarmingItem cropItem = (FarmingItem)item;
            if (cropItem.getNeighborRewardCoins() > 0) {
               scene.getLootRewards().addCoins(x, y, cropItem.getNeighborRewardCoins(), false);
            }

            if (cropItem.getNeighborRewardXp() > 0) {
               scene.getLootRewards().addXp(x, y, cropItem.getNeighborRewardXp(), false);
            }

            if (cropItem.getNeighborRewardFood() > 0) {
               scene.getLootRewards().addFood(x, y, cropItem.getNeighborRewardFood(), false);
            }
         } else {
            super.addReward(x, y, item);
            if (item.getRewardCoins() > 0) {
               this.mEarnEvent.trackEarn(item.getRewardCoins());
            }

            FarmingItem cropItem = (FarmingItem)item;
            Item reward = ItemManager.getItem(cropItem.getRewardItemType(), cropItem.getRewardItem());
            if (reward != null && !reward.isLockedByMission()) {
               scene.getLootRewards().addItem(x, y, reward, 1, false);
            }
         }
      }
   }
}
