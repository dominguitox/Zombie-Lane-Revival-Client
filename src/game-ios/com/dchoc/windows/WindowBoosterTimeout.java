package com.dchoc.windows;

import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDSlot;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.BoosterItem;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.spriteobject.CollisionBox;
import java.util.Vector;

public class WindowBoosterTimeout extends Window {
   private HUDAutoTextField mTitle;
   private HUDAutoTextField mDescription;
   private RoundRectangle mRectangleBG;
   private HUDSlot[] mItems;

   public WindowBoosterTimeout() {
      this.mBackground = AnimationsManager.loadShared(74621);
      this.mCollisions = AnimationsManager.loadShared(67319);
      this.initCollisions();
      this.constructCloseButton(21000);
      this.mTitle = new HUDAutoTextField(this.getCollisionBox(10));
      this.mTitle.setCentered(true, false);
      this.mTitle.setSplitUsingWidth(true);
      this.mTitle.setScaleToFit(true);
      this.mTitle.searchScale(true);
      this.mTitle.setText(2769, 5);
      this.mDescription = new HUDAutoTextField(this.getCollisionBox(20));
      this.mDescription.setCentered(true, true);
      this.mDescription.setSplitUsingWidth(true);
      this.mDescription.setText(2770, 4);
      this.mRectangleBG = new RoundRectangle(0);
      RoundRectangle rectangle = this.mRectangleBG;
      rectangle.setSizes(this.getCollisionBox(30));
      HUDSlot[] items = new HUDSlot[]{new HUDSlot(), new HUDSlot(), new HUDSlot()};
      this.mItems = items;
      this.mObjects.addObject(this.mButtonClose);
      this.mObjects.addObject(this.mRectangleBG);
      this.mObjects.addObject(this.mTitle);
      this.mObjects.addObject(this.mDescription);
      this.mObjects.addObject(this.mItems[0]);
      this.mObjects.addObject(this.mItems[1]);
      this.mObjects.addObject(this.mItems[2]);
   }

   public void setBooster(BoosterItem booster) {
      for (int i = 0; i < (this.mItems == null ? 0 : this.mItems.length); i++) {
         this.mItems[i].setVisible(false);
      }

      if (booster != null) {
         HUDAutoTextField description = this.mDescription;
         description.setText(2770, 4, null, booster.getTitle());
         Vector items = new Vector();
         items.addElement(booster);
         if (booster.getID() != 3272) {
            items.addElement(ItemManager.getBooster(3272));
         }

         if (booster.getID() != 3274) {
            items.addElement(ItemManager.getBooster(3274));
         }

         if (booster.getID() != 3276) {
            items.addElement(ItemManager.getBooster(3276));
         }

         int visible = 0;

         for (int i = 0; i < (this.mItems == null ? 0 : this.mItems.length); i++) {
            int size = items.size();
            HUDSlot slot = this.mItems[i];
            if (i < size) {
               slot.init(items.elementAt(i), this, false);
               this.mItems[i].setVisible(true);
               visible++;
            } else {
               slot.setVisible(false);
            }
         }

         CollisionBox horizontal = this.getCollisionBox(30);
         CollisionBox vertical = this.getCollisionBox(40);
         int y = vertical.getY();
         int height = vertical.getHeight();
         int slotHeight = this.mItems[0].getHeight();
         int segment = horizontal.getWidth() / visible;
         int column = 0;

         for (int i = 0; i < (this.mItems == null ? 0 : this.mItems.length); i++) {
            if (this.mItems[i].isVisible()) {
               HUDSlot slot = this.mItems[i];
               int x = horizontal.getX();
               int slotWidth = this.mItems[i].getWidth();
               slot.setPosition(column * segment + x + (segment - slotWidth) / 2, y + (height - slotHeight) / 2);
               column++;
            }
         }
      }
   }

   @Override
   public void unloadResources() {
      for (int i = 0; i < (this.mItems == null ? 0 : this.mItems.length); i++) {
         HUDSlot slot = this.mItems[i];
         if (slot != null) {
            slot.freeImages();
         }
      }
   }
}
