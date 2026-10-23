package com.dchoc.idead.actions;

import com.dchoc.hud.HUD;
import com.dchoc.hud.HealthBar;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.characters.ActionCharacter;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tracking.ActionEvent;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.WindowTooltip;
import java.util.LinkedHashMap;
import java.util.Map;
import zlport.ios.Ios;

public class Action {
   public static final int TYPE_NONE = 0;
   public static final int TYPE_MOVE = 1;
   public static final int TYPE_ATTACK = 2;
   public static final int TYPE_CONSTRUCT = 3;
   public static final int TYPE_LOOT = 4;
   public static final int TYPE_FARMING = 5;
   protected byte mType;
   protected ActionCharacter mActor;
   protected IsometricObject mTarget;
   protected int mElapsedTime;
   protected int mClickCount;
   protected boolean mPrepared;
   protected boolean mStarted;
   protected int mHealth;
   protected boolean mHired;
   protected int mEnergyCost;
   private ActionEvent mEvent;

   protected Action(byte type) {
      this.mType = type;
      this.mEvent = new ActionEvent(0);
   }

   public void init(ActionCharacter actor, IsometricObject target) {
      this.mActor = actor;
      this.mTarget = target;
      Item item = null;
      if (target != null) {
         item = target.getItem();
      }

      this.mEnergyCost = item == null ? 0 : item.getEnergyDecrease();
      this.mElapsedTime = 0;
      this.mStarted = false;
      this.mPrepared = false;
      this.mClickCount = 1;
      this.mHealth = 0;
      this.mHired = false;
   }

   public int logicUpdate(int delta) {
      if (this.mStarted) {
         this.mElapsedTime += delta;
      }

      return 0;
   }

   public void prepare() {
      this.mPrepared = true;
   }

   public void start() {
      this.mStarted = true;
      this.mClickCount--;
      if (this.mActor != null && this.mActor.isPlayer()) {
         this.logPlayerAction();
      }
   }

   private void logPlayerAction() {
      Map<String, String> details = new LinkedHashMap<>();
      details.put("event", "action_attempt");
      details.put("action", switch (this.mType) {
         case TYPE_MOVE -> "move";
         case TYPE_ATTACK -> "attack";
         case TYPE_CONSTRUCT -> "construct";
         case TYPE_LOOT -> "loot";
         case TYPE_FARMING -> "farming";
         default -> "unknown";
      });
      details.put("energy_cost", Integer.toString(this.mEnergyCost));
      details.put("neighbor_visit", Boolean.toString(GameEngineUtils.isAtNeigborsPlace()));
      if (this.mTarget != null) {
         details.put("target_type", Integer.toString(this.mTarget.getType()));
         details.put("target_item_id", Integer.toString(this.mTarget.getID()));
         details.put("target_x", Float.toString(this.mTarget.getX()));
         details.put("target_y", Float.toString(this.mTarget.getY()));
      } else if (this instanceof MoveAction move) {
         details.put("target_x", Float.toString(move.getTargetX()));
         details.put("target_y", Float.toString(move.getTargetY()));
      }
      Ios.platform().logPlayerAction(details);
   }

   public void stop(String text) {
      this.mStarted = false;
      this.mClickCount = 0;
      if (text != null) {
         this.addFloater(text);
      }

      this.closeTooltip();
   }

   public boolean isCompleted() {
      return true;
   }

   public void cancel() {
   }

   public boolean isCancelable() {
      return false;
   }

   public void setClickCount(int clickCount) {
      this.mClickCount = clickCount;
   }

   public int getClickCount() {
      return this.mClickCount;
   }

   public ActionCharacter getActor() {
      return this.mActor;
   }

   public IsometricObject getTarget() {
      return this.mTarget;
   }

   public void setStarted(boolean started) {
      this.mStarted = started;
   }

   public boolean isStarted() {
      return this.mStarted;
   }

   public void setHired(boolean hired) {
      this.mHired = hired;
   }

   public boolean isHired() {
      return this.mHired;
   }

   public void setHealth(int health) {
      this.mHealth = health;
   }

   public int getHealth() {
      return this.mHealth;
   }

   public int getType() {
      return this.mType;
   }

   public int getEnergyCost() {
      return this.mEnergyCost;
   }

   public ActionEvent getEvent() {
      return this.mEvent;
   }

   protected void closeTooltip() {
      WindowTooltip tooltip = HUD.getTooltip(this.mTarget);
      if (tooltip != null) {
         tooltip.closeTooltip(true);
      }

      HealthBar healthBar = HUD.getHealthBar(this.mTarget);
      if (healthBar != null) {
         healthBar.close();
      }
   }

   protected void addReward(float x, float y, Item item) {
      if (item != null) {
         IsometricScene scene = IsometricUtils.getScene();
         if (item.getRewardCoins() > 0) {
            scene.getLootRewards().addCoins(x, y, item.getRewardCoins(), false);
         }

         if (item.getRewardXp() > 0) {
            scene.getLootRewards().addXp(x, y, item.getRewardXp(), false);
         }

         if (item.getRewardFood() > 0) {
            scene.getLootRewards().addFood(x, y, item.getRewardFood(), false);
         }

         if (item.getRewardStone() > 0) {
            scene.getLootRewards().addStone(x, y, item.getRewardStone(), false);
         }

         if (item.getRewardEnergy() > 0) {
            scene.getLootRewards().addEnergy(x, y, item.getRewardEnergy(), false);
         }
      }
   }

   protected boolean consumeCost(int cost) {
      if (this.mActor.isPlayer()) {
         if (GameEngineUtils.isAtNeigborsPlace()) {
            NeighborProfile neighbor = GameEngineUtils.getNeighbor();
            int actionsLeft = neighbor.getActionsLeft();
            if (cost == 0) {
               cost = 1;
            }

            if (actionsLeft < cost) {
               this.mActor.cancelActions(false);
               this.stop(Toolkit.getText(1710));
               return false;
            }

            neighbor.decreaseActionsLeft(cost);
         } else {
            if (PlayerProfile.getEnergy() < cost) {
               this.mActor.cancelActions(false);
               this.stop(null);
               WindowManager.openWindow((byte)3);
               return false;
            }

            PlayerProfile.removeEnergy(cost);
         }

         if ((this.mType & 255) > 1) {
            this.mEvent.trackAction(GameEngineUtils.isAtNeigborsPlace());
         }
      }

      return true;
   }

   protected boolean hasMoreActions(IsometricObject target) {
      return this.mActor.hasActionWith(target);
   }

   protected void addFloater(String text) {
      IsometricUtils.getScene().addFloater(text, this.mActor.getScreenX(), this.mActor.getScreenY());
   }
}
