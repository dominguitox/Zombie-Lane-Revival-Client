package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUD;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDSlot;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.AvatarItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.idead.player.Inventory;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.spriteobject.CollisionBox;

public class WindowNewItem extends Window {
   private RoundRectangle mRectangleBG;
   private HUDAutoTextField mTitle;
   private HUDAutoTextField mDescription;
   private HUDSlot mItemSlot;
   private HUDButton mButtonStore;
   private HUDButton mButtonEquip;
   private Item mItem;

   public WindowNewItem() {
      this.mBackground = AnimationsManager.loadShared(74621);
      this.mCollisions = AnimationsManager.loadShared(74737);
      this.initCollisions();
      this.mRectangleBG = new RoundRectangle(0);
      RoundRectangle rectangle = this.mRectangleBG;
      rectangle.setSizes(this.getCollisionBox(6));
      this.mTitle = new HUDAutoTextField(this.getCollisionBox(1));
      this.mTitle.setCentered(true, true);
      HUDAutoTextField title = this.mTitle;
      title.setText(1688, DCiDead.getFont(5));
      this.mDescription = new HUDAutoTextField(this.getCollisionBox(5));
      this.mDescription.setCentered(true, true);
      this.mDescription.setSplitUsingWidth(true);
      this.mItemSlot = new HUDSlot();
      this.mItemSlot.disableButton(true);
      this.mItemSlot.setExternalButton(true);
      this.mButtonStore = new HUDButton(16000, this.mCollisions.getCollisionBox(10), 6);
      this.mButtonEquip = new HUDButton(16001, this.mCollisions.getCollisionBox(11), 3);
      this.mButtonStore.setText(1673);
      this.mObjects.addObject(this.mRectangleBG);
      this.mObjects.addObject(this.mTitle);
      this.mObjects.addObject(this.mItemSlot);
      this.mObjects.addObject(this.mDescription);
      this.mObjects.addObject(this.mButtonStore);
      this.mObjects.addObject(this.mButtonEquip);
   }

   public void setItem(Item item, int text) {
      if (item != null) {
         if (item.getType() == 1911) {
            AvatarDescription avatar = PlayerProfile.getActiveAvatar();
            if (avatar != null) {
               int gender = avatar.getGender();
               if (gender != -1 && ((AvatarItem)item).getGender() != gender && ((AvatarItem)item).getGenderLink() != -1) {
                  int link = ((AvatarItem)item).getGenderLink();
                  AvatarItem linked = ItemManager.getAvatar(link);
                  Inventory inventory = PlayerProfile.getInventory();
                  if (inventory.getItemCount(linked) > 0) {
                     item = linked;
                  }
               }
            }
         }

         this.mItem = item;
         this.mItemSlot.init(item, this, false);
         this.mItemSlot.setRemoveMissionAndLevelLocks();
         HUDAutoTextField description = this.mDescription;
         description.setText(text, DCiDead.getFont(4));
         CollisionBox box = this.getCollisionBox(6);
         HUDSlot slot = this.mItemSlot;
         int x = box.getX();
         int width = box.getWidth();
         int slotWidth = this.mItemSlot.getWidth();
         int y = box.getY();
         int height = box.getHeight();
         int slotHeight = this.mItemSlot.getHeight();
         slot.setPosition(x + (width - slotWidth) / 2, y + (height - slotHeight) / 2);
         int type = this.mItem.getType();
         if (type != 101 && type != 147 && type != 405 && type != 1104 && type != 1583) {
            this.mButtonEquip.setText(134);
         } else {
            this.mButtonEquip.setText(1675);
         }

         type = item.getType();
         if (type != 47 && type != 1913) {
            this.mButtonEquip.setVisible(true);
            this.mButtonStore.changeStyle(6);
            this.mButtonStore.setVisible(true);
            HUDButton store = this.mButtonStore;
            store.setPosition(this.getCollisionBox(10));
         } else {
            this.mButtonEquip.setVisible(false);
            this.mButtonStore.changeStyle(3);
            this.mButtonStore.setVisible(true);
            HUDButton store = this.mButtonStore;
            x = box.getX();
            width = box.getWidth();
            int buttonWidth = this.mButtonStore.getWidth();
            box = this.getCollisionBox(10);
            y = box.getY();
            store.setPosition(x + (width - buttonWidth) / 2, y);
         }

         this.mButtonEquip.setEnabled(true);
      }
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      if (this.mItem != null && this.mItem.getType() == 1911) {
         AvatarItem item = (AvatarItem)this.mItem;
         AvatarDescription avatar = PlayerProfile.getActiveAvatar();
         int gender = -1;
         if (avatar != null) {
            gender = avatar.getGender();
         }

         HUDButton button = this.mButtonEquip;
         button.setEnabled(gender == item.getGender());
      }
   }

   @Override
   public void callback(int id) {
      if (this.mState == 0) {
         if (id == 16000) {
            WindowManager.closeWindow();
         } else if (id == 16001) {
            int type = this.mItem.getType();
            if (type != 147 && type != 405 && type != 1104 && type != 1583) {
               PlayerProfile.useItem(this.mItem, false);
               WindowManager.closeWindow();
            } else {
               WindowManager.closeWindow();
               IsometricScene scene = IsometricUtils.getScene();
               IsometricTile tile = scene.getTileAtCenterOfScreen();
               Item item = this.mItem;
               float x = tile.getX();
               float y = tile.getY();
               IsometricObject object = scene.createObject(item, x, y);
               scene.setSelectedObject(object);
               scene.checkSelectedObjectAtValidPosition();
               HUD.changeState(4);
            }
         }

         if ((WindowManager.getPreviousWindow2() & 0xFF) == 18) {
            WindowManager.openWindow((byte)18);
         }
      }
   }

   @Override
   public void unloadResources() {
      this.mItemSlot.freeImages();
   }
}
