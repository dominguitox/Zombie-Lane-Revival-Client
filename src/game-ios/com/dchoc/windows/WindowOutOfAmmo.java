package com.dchoc.windows;

import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDSlot;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.player.Inventory;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.spriteobject.CollisionBox;
import java.util.Vector;

public class WindowOutOfAmmo extends Window {
   private static HUDSlot[] mItems;
   private static RoundRectangle mRectangleBG;
   private static HUDAutoTextField mTitleText;
   private static HUDAutoTextField mDescriptionText;

   public WindowOutOfAmmo() {
      this.mBackground = AnimationsManager.loadShared(74621);
      this.mCollisions = AnimationsManager.loadShared(67319);
      this.initCollisions();
      this.constructCloseButton(20000);
      mTitleText = new HUDAutoTextField(this.getCollisionBox(10));
      mTitleText.setCentered(true, false);
      mTitleText.setSplitUsingWidth(true);
      mTitleText.setScaleToFit(true);
      mTitleText.searchScale(true);
      mTitleText.setText(182, 5);
      mDescriptionText = new HUDAutoTextField(this.getCollisionBox(20));
      mDescriptionText.setCentered(true, true);
      mDescriptionText.setText(2437, 4);
      mRectangleBG = new RoundRectangle(0);
      RoundRectangle rectangle = mRectangleBG;
      rectangle.setSizes(this.getCollisionBox(30));
      HUDSlot[] items = new HUDSlot[]{new HUDSlot(), new HUDSlot(), new HUDSlot()};
      mItems = items;
      this.mObjects.addObject(this.mButtonClose);
      this.mObjects.addObject(mRectangleBG);
      this.mObjects.addObject(mTitleText);
      this.mObjects.addObject(mDescriptionText);
      this.mObjects.addObject(mItems[0]);
      this.mObjects.addObject(mItems[1]);
      this.mObjects.addObject(mItems[2]);
   }

   @Override
   public void open() {
      super.open();

      for (int i = 0; i < (mItems == null ? 0 : mItems.length); i++) {
         mItems[i].setVisible(false);
      }

      Vector items = new Vector();
      items.addElement(ItemManager.getItem(85));
      Item item = ItemManager.getItem(2066);
      if (!item.isLockedByLevel()) {
         items.addElement(item);
      }

      item = ItemManager.getItem(2070);
      if (!item.isLockedByLevel()) {
         Inventory inventory = PlayerProfile.getInventory();
         if (inventory.getItemCount(2070) < 1) {
            items.addElement(item);
         }
      }

      item = ItemManager.getItem(2105);
      if (!item.isLockedByLevel()) {
         items.addElement(item);
      }

      int visible = 0;

      for (int i = 0; i < (mItems == null ? 0 : mItems.length); i++) {
         int size = items.size();
         HUDSlot slot = mItems[i];
         if (i < size) {
            slot.init(items.elementAt(i), this, false);
            mItems[i].setVisible(true);
            visible++;
         } else {
            slot.setVisible(false);
         }
      }

      CollisionBox horizontal = this.getCollisionBox(30);
      CollisionBox vertical = this.getCollisionBox(40);
      int y = vertical.getY();
      int height = vertical.getHeight();
      int slotHeight = mItems[0].getHeight();
      int segment = horizontal.getWidth() / visible;
      int column = 0;

      for (int i = 0; i < (mItems == null ? 0 : mItems.length); i++) {
         if (mItems[i].isVisible()) {
            HUDSlot slot = mItems[i];
            int x = horizontal.getX();
            int slotWidth = mItems[i].getWidth();
            slot.setPosition(column * segment + x + (segment - slotWidth) / 2, y + (height - slotHeight) / 2);
            column++;
         }
      }
   }
}
