package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDConstructionSlotItem;
import com.dchoc.hud.HUDCounter;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.CollectibleItem;
import com.dchoc.idead.items.ConstructionItem;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.objects.ConstructionObject;
import com.dchoc.idead.player.Inventory;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.spriteobject.CollisionBox;

public class WindowFinishConstruction extends Window {
   public static final int BUTTON_CLOSE = 1;
   public static final int BUTTON_FINISH = 2;
   public static final int COLLISION_CLOSE = 0;
   public static final int COLLISION_COUNTER_CASH = 21;
   public static final int COLLISION_COUNTER_COINS = 22;
   public static final int COLLISION_COUNTER_FOOD = 20;
   public static final int COLLISION_FINISH = 1;
   public static final int COLLISION_RECTANGLE_SLOTS = 3;
   public static final int COLLISION_SLOT1 = 4;
   public static final int COLLISION_SLOT2 = 5;
   public static final int COLLISION_SLOT3 = 6;
   public static final int COLLISION_SLOT4 = 7;
   public static final int COLLISION_TITLE = 2;
   private ConstructionObject mConstruction;
   private HUDAutoTextField mTextTitle;
   private RoundRectangle mRectangleSlots;
   private HUDConstructionSlotItem mSlot1;
   private HUDConstructionSlotItem mSlot2;
   private HUDConstructionSlotItem mSlot3;
   private HUDConstructionSlotItem mSlot4;
   private HUDButton mButtonFinish;
   private HUDCounter mCounterFood;
   private HUDCounter mCounterCash;
   private HUDCounter mCounterCoins;

   public WindowFinishConstruction() {
      this.mBackground = AnimationsManager.loadShared(74619);
      this.mCollisions = AnimationsManager.loadShared(67985);
      this.initCollisions();
      this.constructCloseButton(1);
      this.mObjects.addObject(this.mButtonClose);
      CollisionBox box = this.mCollisions.getCollisionBox(20);
      this.mCounterFood = new HUDCounter(2, 66215, box.getX(), box.getY(), (byte)0);
      this.mObjects.addObject(this.mCounterFood);
      box = this.mCollisions.getCollisionBox(21);
      this.mCounterCash = new HUDCounter(1, 66249, box.getX(), box.getY(), (byte)6);
      this.mObjects.addObject(this.mCounterCash);
      box = this.mCollisions.getCollisionBox(22);
      this.mCounterCoins = new HUDCounter(0, 66223, box.getX(), box.getY(), (byte)5);
      this.mObjects.addObject(this.mCounterCoins);
      box = this.mCollisions.getCollisionBox(3);
      this.mRectangleSlots = new RoundRectangle(0);
      RoundRectangle rectangle = this.mRectangleSlots;
      rectangle.setSizes(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mObjects.addObject(this.mRectangleSlots);
      box = this.mCollisions.getCollisionBox(4);
      this.mSlot1 = new HUDConstructionSlotItem(box.getX(), box.getY());
      this.mObjects.addObject(this.mSlot1);
      box = this.mCollisions.getCollisionBox(5);
      this.mSlot2 = new HUDConstructionSlotItem(box.getX(), box.getY());
      this.mObjects.addObject(this.mSlot2);
      box = this.mCollisions.getCollisionBox(6);
      this.mSlot3 = new HUDConstructionSlotItem(box.getX(), box.getY());
      this.mObjects.addObject(this.mSlot3);
      box = this.mCollisions.getCollisionBox(7);
      this.mSlot4 = new HUDConstructionSlotItem(box.getX(), box.getY());
      this.mObjects.addObject(this.mSlot4);
      box = this.mCollisions.getCollisionBox(2);
      this.mTextTitle = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextTitle.setCentered(true, true);
      this.mTextTitle.setScaleToFit(true);
      this.mObjects.addObject(this.mTextTitle);
      box = this.mCollisions.getCollisionBox(1);
      this.mButtonFinish = new HUDButton(2, box, 2);
      this.mButtonFinish.setText(56);
      this.mObjects.addObject(this.mButtonFinish);
      this.mConstruction = null;
   }

   public void enablePlusHelperIcon(boolean enable) {
      this.mSlot1.enablePlusHelperIcon(enable);
      this.mSlot2.enablePlusHelperIcon(enable);
      this.mSlot3.enablePlusHelperIcon(enable);
      this.mSlot4.enablePlusHelperIcon(enable);
   }

   public void setConstruction(ConstructionObject construction) {
      if (construction != null) {
         this.mConstruction = construction;
         ConstructionItem item = (ConstructionItem)construction.getItem();
         HUDAutoTextField title = this.mTextTitle;
         title.setText(item.getFinish(), DCiDead.getFont(5));
         int[][] items = item.getCollectibleItems();
         CollectibleItem item1 = ItemManager.getCollectible(items[0][0]);
         CollectibleItem item2 = ItemManager.getCollectible(items[1][0]);
         CollectibleItem item3 = ItemManager.getCollectible(items[2][0]);
         CollectibleItem item4 = ItemManager.getCollectible(items[3][0]);
         this.mSlot1.setValues(item1, items[0][1]);
         this.mSlot2.setValues(item2, items[1][1]);
         this.mSlot3.setValues(item3, items[2][1]);
         this.mSlot4.setValues(item4, items[3][1]);
         HUDButton button = this.mButtonFinish;
         button.setEnabled(this.mSlot1.isValueReached() && this.mSlot2.isValueReached() && this.mSlot3.isValueReached() && this.mSlot4.isValueReached());
         if (item.getID() != 166 && item.getID() != 3316) {
            this.enablePlusHelperIcon(true);
         } else {
            this.enablePlusHelperIcon(false);
         }
      }
   }

   @Override
   public void callback(int id) {
      if (id != -1) {
         if (id == 2) {
            this.finish();
         } else if (id == 1) {
            WindowManager.closeWindow();
         }
      } else {
         HUDButton button = this.mButtonFinish;
         button.setEnabled(this.mSlot1.isValueReached() && this.mSlot2.isValueReached() && this.mSlot3.isValueReached() && this.mSlot4.isValueReached());
      }
   }

   public void finish() {
      ConstructionItem item = (ConstructionItem)this.mConstruction.getItem();
      int[][] items = item.getCollectibleItems();
      Inventory inventory = PlayerProfile.getInventory();

      for (int i = 0; i < (items == null ? 0 : items.length); i++) {
         inventory.removeItem(ItemManager.getCollectible(items[i][0]), items[i][1]);
      }

      this.mConstruction.changeState(2);
      MissionManager.updateCounter(2520, item.getID(), 1);
      MissionManager.updateCounter(2520, 80, 1);
      WindowManager.closeWindow();
   }
}
