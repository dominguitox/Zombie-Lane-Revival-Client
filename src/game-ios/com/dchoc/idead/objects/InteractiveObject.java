package com.dchoc.idead.objects;

import com.dchoc.hud.HUD;
import com.dchoc.hud.HealthBar;
import com.dchoc.idead.actions.Action;
import com.dchoc.idead.characters.ActionCharacter;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.ConstructionItem;
import com.dchoc.idead.items.DestructibleItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.windows.WindowTooltip;

public abstract class InteractiveObject extends IsometricObject {
   public InteractiveObject(IsometricScene scene, int type) {
      super(scene, type);
   }

   @Override
   public void init(float x, float y, Item item) {
      super.init(x, y, item);
   }

   public boolean canInteractWith(ActionCharacter actor) {
      return false;
   }

   public Action interactWith(ActionCharacter actor) {
      return null;
   }

   @Override
   public void touchEventOccured(TouchEvent event) {
      PlayerCharacter player = this.mScene.getPlayer();
      switch (event.getType()) {
         case 0:
            if (!this.canInteractWith(player) && this.getType() != 5 && this.getType() != 6 && this.getType() != 3) {
               if (this.getType() != 0) {
                  return;
               }

               ConstructionItem construction = (ConstructionItem)this.mItem;
               if (((ConstructionObject)this).getState() != 1) {
                  return;
               }

               if (construction.getCollectibleItems() != null) {
                  return;
               }
            } else if (this.getType() == 3 && ((DestructibleItem)this.mItem).isIndestructible()) {
               return;
            }

            this.mTouchX = event.getX();
            this.mTouchY = event.getY();
            this.changeHighlightState(0);
            return;
         case 1:
            if (this.mHighlightState == -1) {
               return;
            } else {
               if (IsometricUtils.getScene().getState() != 0) {
                  return;
               }

               int dx = Math.abs(event.getX() - this.mTouchX);
               int dy = Math.abs(event.getY() - this.mTouchY);
               if (dx > IsometricTile.getTileSize() * 0.5F || dy > IsometricTile.getTileSize() * 0.5F) {
                  this.disableHighlight();
               }

               return;
            }
         case 2:
            if (this.mHighlightState != -1) {
               if (this.mHighlightState != 1 && this.getType() != 2) {
                  if (this.mType == 6 && !this.canInteractWith(player)) {
                     HUD.openQuickRipenTab(this);
                  }
               } else if (this.canInteractWith(player)) {
                  if (this.interactWith(player) != null) {
                     event.setConsumed(true);
                     if (this.showHealthBar()) {
                        HUD.createHealthBar(this, 3, false);
                     }

                     if (this.showTooltip(true)) {
                        WindowTooltip tooltip = HUD.getTooltip(this);
                        if (tooltip == null || tooltip.getTouchType() != 1) {
                           HUD.createSceneTooltip(this, 3, -1);
                        }
                     }
                  }
               } else if (this.mType == 6 && !this.canInteractWith(player)) {
                  HUD.openQuickRipenTab(this);
               }

               this.changeHighlightState(-1);
            }

            if (!player.hasActionWith(this)) {
               HealthBar healthBar = HUD.getHealthBar(this);
               if (healthBar != null && healthBar.getTouchType() == 0) {
                  healthBar.close();
               }
            }

            WindowTooltip tooltip = HUD.getTooltip(this);
            if (tooltip != null && tooltip.getTouchType() == 0) {
               tooltip.closeTooltip(true);
            }

            return;
         case 3:
            if (this.canInteractWith(player)) {
               if (this.interactWith(player) != null) {
                  event.setConsumed(true);
                  if (this.showHealthBar()) {
                     HUD.createHealthBar(this, 3, false);
                  }

                  if (this.showTooltip(true)) {
                     HUD.createSceneTooltip(this, 3, -1);
                  }
               }

               this.changeHighlightState(-1);
            } else if (this.mType == 6) {
               if (!this.canInteractWith(player)) {
                  HUD.openQuickRipenTab(this);
               }
            } else if (this.mType == 5) {
               this.setHighlighted(true);
               HUD.createHealthBar(this, 3, true);
            }

            return;
         case 4:
            if (!player.hasActionWith(this)) {
               HealthBar bar = HUD.getHealthBar(this);
               WindowTooltip open = HUD.getTooltip(this);
               if (bar != null) {
                  bar.close();
               }

               if (open != null) {
                  open.closeTooltip(true);
               }
            }

            this.mHighlightState = -1;
            this.setHighlighted(false);
            return;
         case 5:
            if (this.mHighlightState != -1 && this.showTooltip(false)) {
               WindowTooltip current = HUD.getTooltip(this);
               if (current == null || current.getTouchType() != 1) {
                  HUD.createSceneTooltip(this, 5, -1);
               }
            }

            return;
      }
   }

   public void disableHighlight() {
      this.setHighlighted(false);
      HealthBar healthBar = HUD.getHealthBar(this);
      if (healthBar != null) {
         healthBar.close();
      }

      WindowTooltip tooltip = HUD.getTooltip(this);
      if (tooltip != null) {
         tooltip.close();
      }

      this.mHighlightState = -1;
   }
}
