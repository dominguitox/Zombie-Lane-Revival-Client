package com.dchoc.idead.actions;

import com.dchoc.idead.GameEngine;
import com.dchoc.idead.characters.ActionCharacter;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.ConstructionItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.objects.ConstructionObject;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.toolkit.Toolkit;

public class ConstructAction extends Action {
   public static final int STATE_CONSTRUCT = 0;
   public static final int STATE_CONSTRUCTED = 1;
   public static final int STATE_COMPLETED = 2;
   private ConstructionObject mConstruction;
   private int mState;

   public ConstructAction() {
      super((byte)3);
   }

   @Override
   public void init(ActionCharacter actor, IsometricObject target) {
      super.init(actor, target);
      this.mConstruction = (ConstructionObject)target;
   }

   @Override
   public int logicUpdate(int delta) {
      super.logicUpdate(delta);
      if (this.mState == 1) {
         this.mConstruction.increaseClicks(1);
         if (!this.hasMoreActions(this.mConstruction)) {
            this.mConstruction.setHighlighted(false);
         }

         this.updateCounters();
         this.addReward(this.mConstruction.getScreenX(), this.mConstruction.getScreenY(), this.mConstruction.getItem());
         this.changeState(2);
      } else if (this.mState == 0 && this.mActor.getState() == 0) {
         this.changeState(1);
      }

      return 0;
   }

   public void changeState(int state) {
      if (state == 2) {
         if (!this.hasMoreActions(this.mConstruction)) {
            this.mConstruction.setHighlighted(false);
         }
      } else if (state == 0) {
         this.mActor.turnTo(this.mConstruction.getX(), this.mConstruction.getY());
         this.mActor.repair(this.mConstruction);
      }

      this.mState = state;
   }

   @Override
   public void prepare() {
      super.prepare();
      this.mConstruction.setHighlighted(true);
   }

   @Override
   public void start() {
      this.mState = 2;
      super.start();
      if (TutorialFlow.isCompleted() && this.mConstruction != null && this.mConstruction.getState() < 1) {
         Item item = this.mConstruction.getItem();
         if (item.isLockedByLevel() || item.isLockedByMission()) {
            this.stop(Toolkit.getText(1707));
         } else if (this.mConstruction.isAlerted()) {
            this.stop(Toolkit.getText(1706));
         } else if (GameEngine.getInstance().isVisitingNeighbor()) {
            this.stop(Toolkit.getText(1707));
         } else {
            if (this.consumeCost(this.mEnergyCost)) {
               this.changeState(0);
            }
         }
      } else {
         this.stop(Toolkit.getText(1707));
      }
   }

   @Override
   public boolean consumeCost(int energy) {
      if (!this.mActor.isPlayer()) {
         return false;
      }

      ConstructionItem item = (ConstructionItem)this.mConstruction.getItem();
      int stone = item.getStonePerClick();
      if (PlayerProfile.getStone() < stone) {
         this.stop(Toolkit.getText(1701));
         iWrapper.playSoundFx(54);
         return false;
      }

      int coins = item.getCoinsPerClick();
      if (PlayerProfile.getCoins() < coins) {
         this.stop(Toolkit.getText(1702));
         iWrapper.playSoundFx(54);
         return false;
      }

      if (!super.consumeCost(energy)) {
         return false;
      }

      if (item.getStonePerClick() > 0) {
         PlayerProfile.removeStone(item.getStonePerClick());
      }

      if (item.getCoinsPerClick() > 0) {
         PlayerProfile.removeCoins(item.getCoinsPerClick());
      }

      this.getEvent().setParams(25, item.getID(), -1);
      return true;
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
      this.mConstruction.setHighlighted(false);
      if (this.mStarted) {
         this.mActor.stop();
      }
   }

   @Override
   protected void addReward(float x, float y, Item item) {
      if (item != null && item.getType() == 147) {
         ConstructionItem constructionItem = (ConstructionItem)item;
         IsometricScene scene = IsometricUtils.getScene();
         if (constructionItem.getClickRewardXp() > 0) {
            scene.getLootRewards().addXp(x, y, constructionItem.getClickRewardXp(), false);
         }
      }
   }

   protected void updateCounters() {
      if (this.mActor.isPlayer()) {
         Item item = this.mConstruction.getItem();
         MissionManager.updateCounter(2493, item.getID(), 1);
         MissionManager.updateCounter(2493, 80, 1);
      }
   }
}
