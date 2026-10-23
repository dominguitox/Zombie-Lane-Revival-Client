package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.events.EventManager;
import com.dchoc.idead.events.TimedEvent;
import com.dchoc.idead.items.CollectibleItem;
import com.dchoc.idead.items.CraftingItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.player.Inventory;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;

public class HUDCraftingSlot extends HUDObject {
   public static final int COLLISION_EQUAL = 12;
   public static final int COLLISION_PLUS1 = 10;
   public static final int COLLISION_PLUS2 = 11;
   public static final int COLLISION_SLOT_ITEM = 2;
   public static final int COLLISION_SLOT_ITEM_CREATES = 5;
   public static final int COLLISION_TITLE = 1;
   public static final int MAX_COLLECTIBLES = 3;
   private CraftingItem mCraftable;
   private HUDCollection mCollection;
   private HUDImage mImagePlus1;
   private HUDImage mImagePlus2;
   private HUDImage mImageEqual;
   private HUDAutoTextField mTextTitle;
   private HUDCraftingCreates mSlotItemCreates;
   private SpriteObject mLayoutSlot2;
   private SpriteObject mLayoutSlot3;
   private HUDCraftingSlotItem[] mSlotItemCollectibles;

   public HUDCraftingSlot(int x, int y, int width, int height) {
      super((byte)-1);
      this.setSizes(x, y, width, height);
      this.mCollection = new HUDCollection();
      this.mCollection.setParent(this);
      this.mLayoutSlot2 = AnimationsManager.loadShared(74671);
      this.mLayoutSlot3 = AnimationsManager.loadShared(74673);
      CollisionBox box = this.mLayoutSlot3.getCollisionBox(1);
      this.mTextTitle = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextTitle.setCentered(false, true);
      this.mCollection.addObject(this.mTextTitle);
      this.mSlotItemCollectibles = new HUDCraftingSlotItem[3];

      for (int i = 0; i < 3; i++) {
         box = this.mLayoutSlot3.getCollisionBox(i + 2);
         this.mSlotItemCollectibles[i] = new HUDCraftingSlotItem(box.getX(), box.getY());
         this.mCollection.addObject(this.mSlotItemCollectibles[i]);
      }

      box = this.mLayoutSlot3.getCollisionBox(5);
      this.mSlotItemCreates = new HUDCraftingCreates(box.getX(), box.getY());
      this.mCollection.addObject(this.mSlotItemCreates);
      box = this.mLayoutSlot3.getCollisionBox(10);
      this.mImagePlus1 = new HUDImage(66875, box.getX(), box.getY());
      this.mCollection.addObject(this.mImagePlus1);
      box = this.mLayoutSlot3.getCollisionBox(11);
      this.mImagePlus2 = new HUDImage(66875, box.getX(), box.getY());
      this.mCollection.addObject(this.mImagePlus2);
      box = this.mLayoutSlot3.getCollisionBox(12);
      this.mImageEqual = new HUDImage(66881, box.getX(), box.getY());
      this.mCollection.addObject(this.mImageEqual);
      this.mCraftable = null;
   }

   public void setCraftable(CraftingItem craftable) {
      if (craftable != null) {
         this.mCraftable = craftable;
         HUDAutoTextField title = this.mTextTitle;
         title.setText(craftable.getTitle(), DCiDead.getFont(2));
         Item creates = ItemManager.getItem(craftable.getCreatesType(), craftable.getCreatesID());
         this.mSlotItemCreates.setValues(creates, 0);
         this.mSlotItemCreates.enableCrafting(true);
         int[] ids = this.mCraftable.getCollectibleItems();
         SpriteObject layout = ids != null && ids.length == 2 ? this.mLayoutSlot2 : this.mLayoutSlot3;

         for (int i = 0; i < 3; i++) {
            HUDCraftingSlotItem slot = this.mSlotItemCollectibles[i];
            slot.setVisible(i < (ids == null ? 0 : ids.length));
            if (i < (ids == null ? 0 : ids.length)) {
               CollisionBox box = layout.getCollisionBox(i + 2);
               slot = this.mSlotItemCollectibles[i];
               int boxX = box.getX();
               int boxY = box.getY();
               slot.setPosition(boxX, boxY);
               CollectibleItem collectible = ItemManager.getCollectible(ids[i]);
               this.mSlotItemCollectibles[i].setValues(collectible, 1);
               if (!this.mSlotItemCollectibles[i].isValueReached()) {
                  this.mSlotItemCreates.enableCrafting(false);
               }
            }
         }

         CollisionBox box = layout.getCollisionBox(10);
         HUDImage image = this.mImagePlus1;
         int boxX = box.getX();
         int boxY = box.getY();
         image.setPosition(boxX, boxY);
         this.mImagePlus2.setVisible(ids != null && ids.length > 2);
         box = layout.getCollisionBox(12);
         image = this.mImageEqual;
         boxX = box.getX();
         boxY = box.getY();
         image.setPosition(boxX, boxY);
      }
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         this.mCollection.draw();
      }
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.isVisible()) {
         this.mCollection.logicUpdate(delta);
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible()) {
         this.mCollection.pointerEvent(event);
      }
   }

   @Override
   public void callback(int id) {
      if (id == 4) {
         this.craftItem();
      } else if (id != -1) {
         return;
      }

      this.setCraftable(this.mCraftable);
   }

   public void craftItem() {
      Inventory inventory = PlayerProfile.getInventory();
      int[] ids = this.mCraftable.getCollectibleItems();

      for (int i = 0; i < (ids == null ? 0 : ids.length); i++) {
         inventory.removeItem(ItemManager.getCollectible(ids[i]), 1);
      }

      Item item = ItemManager.getItem(this.mCraftable.getCreatesType(), this.mCraftable.getCreatesID());
      int amount = 1;
      if (item.getType() == 1907) {
         amount = ((WeaponItem)item).getMaxAmmo();
      }

      MissionManager.updateCounter(2526, item.getID(), 1);
      MissionManager.updateCounter(2526, item.getType(), 1);
      MissionManager.updateCounter(2526, item.getSubType(), 1);
      MissionManager.updateCounter(2526, 80, 1);
      inventory.addItem(item, amount);
      int weaponId = PlayerProfile.getCurrentWeapon().getID();
      if (weaponId != item.getID()) {
         WindowManager.closeWindow();
         int[] parameters = new int[]{item.getType(), item.getID()};
         EventManager.addEvent(new TimedEvent(50, 2470, parameters));
      }
   }
}
