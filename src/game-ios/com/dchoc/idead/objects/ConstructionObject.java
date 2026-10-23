package com.dchoc.idead.objects;

import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.actions.Action;
import com.dchoc.idead.characters.ActionCharacter;
import com.dchoc.idead.isometric.Camera;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.items.ConstructionItem;
import com.dchoc.idead.items.CraftingItem;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.WindowCrafting;
import com.dchoc.windows.WindowFinishConstruction;

public class ConstructionObject extends InteractiveObject {
   public static final int ANIMATION_PARTS_READY = 1;
   public static final int ANIMATION_READY = 2;
   public static final int ANIMATION_UNDER_CONSTRUCTION = 0;
   public static final int ANIMATION_UPGRADED = 3;
   public static final int STATE_PARTS_READY = 1;
   public static final int STATE_READY = 2;
   public static final int STATE_UNDER_CONSTRUCTION = 0;
   public static final int STATE_UPGRADED = 3;
   private int mClicks;
   private Mission mShowAfterMission;
   protected int mState;

   public ConstructionObject(IsometricScene scene) {
      super(scene, 0);
   }

   public void init(float x, float y, ConstructionItem item) {
      this.init(x, y, item, 0, 0);
   }

   public void init(float x, float y, ConstructionItem item, int state, int clicks) {
      super.init(x, y, item);
      this.mShowAfterMission = MissionManager.getMission(item.getShowAfterMission());
      this.mClicks = clicks;
      this.changeState(state);
   }

   @Override
   public boolean canInteractWith(ActionCharacter actor) {
      if (!actor.isPlayer()) {
         return false;
      } else if (GameEngine.getInstance().isVisitingNeighbor()) {
         return false;
      } else {
         return this.mState == 1 ? ((ConstructionItem)this.mItem).getCollectibleItems() != null : true;
      }
   }

   @Override
   public Action interactWith(ActionCharacter actor) {
      if (!actor.isPlayer()) {
         return null;
      }

      if (GameEngine.getInstance().isVisitingNeighbor()) {
         return null;
      }

      ConstructionItem item = (ConstructionItem)this.mItem;
      if (this.mState != 2 && this.mState != 3) {
         if (this.mState != 1) {
            return actor.addAction(3, this);
         }

         if (!item.hasCollectibles()) {
            return null;
         }

         if (MissionManager.getMission(Tuner.getMissionToUnlockBuildingParts()).isCompleted() && !item.isLockedByMission2()) {
            ((WindowFinishConstruction)WindowManager.getWindow((byte)10)).setConstruction(this);
            WindowManager.openWindow((byte)10);
            return null;
         }
      } else {
         if (!item.hasCraftables()) {
            return null;
         }

         if (MissionManager.getMission(Tuner.getMissionToUnlockCrafting()).isCompleted()) {
            ((WindowCrafting)WindowManager.getWindow((byte)9)).setConstruction(this);
            WindowManager.openWindow((byte)9);
            return null;
         }
      }

      this.mScene.addFloater(Toolkit.getText(1707), this.getScreenX(), this.getScreenY());
      return null;
   }

   @Override
   public int logicUpdate(int delta) {
      int result = super.logicUpdate(delta);
      if (result != 0) {
         return result;
      }

      if (this.mState != 0) {
         return 0;
      }

      ConstructionItem item = (ConstructionItem)this.mItem;
      if (this.mClicks == item.getRequiredClicks()) {
         this.changeState(1);
      } else {
         if (item.hasCollectibles()) {
            return 0;
         }

         this.changeAnimation(this.mState);
      }

      return 0;
   }

   public void changeState(int state) {
      this.changeAnimation(state);
      this.mState = state;
   }

   @Override
   public boolean doDraw(Camera camera) {
      return this.isHiddenByMission() ? false : super.doDraw(camera);
   }

   public boolean isHiddenByMission() {
      return this.mShowAfterMission != null ? !this.mShowAfterMission.isCompleted() : false;
   }

   public CraftingItem getAvailableCraftable() {
      int[] crafts = ((ConstructionItem)this.mItem).getCraftingItems();
      if (crafts == null) {
         return null;
      }

      if (this.mState < 2) {
         return null;
      }

      if (crafts.length > 0) {
         CraftingItem crafting = ItemManager.getCrafting(crafts[0]);
         if (crafting != null && ItemManager.getCraftableAmount(crafting) > 0) {
            return crafting;
         }
      }

      if (this.mState == 3 && crafts.length >= 2) {
         CraftingItem crafting = ItemManager.getCrafting(crafts[0]);
         Mission mission = MissionManager.getMission(Tuner.getMissionToUnlockBuildingUpgrade());
         if (mission != null) {
            boolean completed = mission.isCompleted();
            if (crafting == null || !completed) {
               return null;
            }
         } else if (crafting == null) {
            return null;
         }

         return ItemManager.getCraftableAmount(crafting) > 0 ? crafting : null;
      } else {
         return null;
      }
   }

   public boolean isCompleted() {
      return this.mState == 2 || this.mState == 3;
   }

   public boolean isUpgraded() {
      return this.mState == 3;
   }

   public void increaseClicks(int clicks) {
      int previous = this.mClicks;
      ConstructionItem item = (ConstructionItem)this.mItem;
      this.mClicks = previous + clicks;
      if (previous + clicks > item.getRequiredClicks()) {
         this.mClicks = item.getRequiredClicks();
      }
   }

   public int getState() {
      return this.mState;
   }

   public void setClicks(int clicks) {
      this.mClicks = clicks;
   }

   public int getClicks() {
      return this.mClicks;
   }

   @Override
   public SpriteObject getCollision() {
      SpriteObject[] collisions = this.mItem.getCollisions();
      if (collisions == null) {
         return null;
      } else if (this.mAnimations.getAnimation() < collisions.length) {
         return collisions[this.mAnimations.getAnimation()];
      } else {
         return collisions.length > 0 ? collisions[0] : null;
      }
   }

   @Override
   public boolean isReleasable() {
      return true;
   }

   @Override
   public boolean isHiddenable() {
      return true;
   }

   @Override
   protected boolean showTooltip(boolean tooltip) {
      return !tooltip ? true : this.mState == 0;
   }

   public void changeAnimation(int state) {
      ConstructionItem item = (ConstructionItem)this.mItem;
      int animation = this.mAnimations.getAnimation();
      int count = this.mAnimations.getAnimationCount();
      switch (state) {
         case 0:
            animation = 0;
            if (!item.hasCollectibles()) {
               animation = Math.min(Math.max(this.mClicks * count / item.getRequiredClicks(), 0), count - 1);
            }
            break;
         case 1:
            animation = count - 1;
            if (item.hasCollectibles()) {
               animation = 1;
            }
            break;
         case 2:
         case 3:
            animation = state;
      }

      if (animation != this.mAnimations.getAnimation()) {
         this.mAnimations.changeAnimation(animation, true);
         int[] shadows = item.getShadowAnimationIDs();
         if (shadows != null && animation >= 0 && animation < shadows.length) {
            this.setAnimationShadow(shadows[animation]);
         }

         this.mScene.getObjectsTree().update(this);
      }
   }
}
