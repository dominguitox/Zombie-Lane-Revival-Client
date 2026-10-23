package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDCounter;
import com.dchoc.hud.HUDCraftingSlot;
import com.dchoc.hud.HUDCraftingSlotItem;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.SlotBackground;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.ConstructionItem;
import com.dchoc.idead.items.CraftingItem;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.objects.ConstructionObject;
import com.dchoc.idead.player.Inventory;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.spriteobject.CollisionBox;

public class WindowCrafting extends Window {
   public static final int BUTTON_CLOSE = 1;
   public static final int BUTTON_CRAFT_ITEM = 4;
   public static final int BUTTON_UPGRADE = 2;
   public static final int COLLISION_CLOSE = 0;
   public static final int COLLISION_COUNTER_CASH = 20;
   public static final int COLLISION_RECTANGLE_SLOTS = 3;
   public static final int COLLISION_SLOT1 = 4;
   public static final int COLLISION_SLOT2 = 5;
   public static final int COLLISION_TITLE = 2;
   public static final int COLLISION_UPGRADE = 6;
   public static final int COLLISION_UPGRADE_ITEM = 9;
   public static final int COLLISION_UPGRADE_TEXT = 7;
   private ConstructionObject mConstruction;
   private HUDAutoTextField mTextTitle;
   private HUDAutoTextField mTextUpgrade;
   private HUDAutoTextField mTextLocked;
   private RoundRectangle mRectangleSlots;
   private SlotBackground mSlot1;
   private SlotBackground mSlot2;
   private HUDCraftingSlotItem mSlotItemUpgrade;
   private HUDCraftingSlot mCraftingSlot1;
   private HUDCraftingSlot mCraftingSlot2;
   private HUDButton mButtonUpgrade;
   private HUDCounter mCounterCash;

   public WindowCrafting() {
      this.mBackground = AnimationsManager.loadShared(74619);
      this.mCollisions = AnimationsManager.loadShared(66917);
      this.initCollisions();
      this.constructCloseButton(1);
      this.mObjects.addObject(this.mButtonClose);
      CollisionBox box = this.mCollisions.getCollisionBox(20);
      this.mCounterCash = new HUDCounter(1, 66249, box.getX(), box.getY(), (byte)6);
      this.mObjects.addObject(this.mCounterCash);
      box = this.mCollisions.getCollisionBox(3);
      this.mRectangleSlots = new RoundRectangle(0);
      RoundRectangle rectangle = this.mRectangleSlots;
      rectangle.setSizes(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mObjects.addObject(this.mRectangleSlots);
      box = this.mCollisions.getCollisionBox(4);
      this.mSlot1 = new SlotBackground(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mObjects.addObject(this.mSlot1);
      box = this.mCollisions.getCollisionBox(5);
      this.mSlot2 = new SlotBackground(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mObjects.addObject(this.mSlot2);
      this.mTextTitle = new HUDAutoTextField(this.mCollisions.getCollisionBox(2));
      this.mTextTitle.setCentered(false, true);
      this.mObjects.addObject(this.mTextTitle);
      this.mTextLocked = new HUDAutoTextField(this.mCollisions.getCollisionBox(5));
      this.mTextLocked.setCentered(true, true);
      this.mTextLocked.setSplitUsingWidth(true);
      HUDAutoTextField text = this.mTextLocked;
      text.setText(37, DCiDead.getFont(2));
      this.mObjects.addObject(this.mTextLocked);
      this.mTextUpgrade = new HUDAutoTextField(this.mCollisions.getCollisionBox(7));
      this.mTextUpgrade.setCentered(false, true);
      this.mTextUpgrade.setSplitUsingWidth(true);
      text = this.mTextUpgrade;
      text.setText(1642, DCiDead.getFont(4));
      this.mObjects.addObject(this.mTextUpgrade);
      box = this.mCollisions.getCollisionBox(6);
      this.mButtonUpgrade = new HUDButton(2, box, 2);
      this.mButtonUpgrade.setText(1870);
      this.mObjects.addObject(this.mButtonUpgrade);
      box = this.mCollisions.getCollisionBox(9);
      this.mSlotItemUpgrade = new HUDCraftingSlotItem(box.getX(), box.getY());
      this.mSlotItemUpgrade.setBuyRemainingItems(true);
      this.mSlotItemUpgrade.setHideBuyButton(true);
      this.mObjects.addObject(this.mSlotItemUpgrade);
      box = this.mCollisions.getCollisionBox(4);
      this.mCraftingSlot1 = new HUDCraftingSlot(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mObjects.addObject(this.mCraftingSlot1);
      box = this.mCollisions.getCollisionBox(5);
      this.mCraftingSlot2 = new HUDCraftingSlot(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mObjects.addObject(this.mCraftingSlot2);
      this.mConstruction = null;
   }

   public void setConstruction(ConstructionObject construction) {
      if (construction != null) {
         this.mConstruction = construction;
         ConstructionItem item = (ConstructionItem)construction.getItem();
         HUDAutoTextField title = this.mTextTitle;
         title.setText(item.getTitle(), DCiDead.getFont(5));
         boolean upgraded = this.setUpgraded();
         int[] craftings = item.getCraftingItems();
         CraftingItem first = null;
         CraftingItem second = null;
         if (craftings != null) {
            if (craftings.length > 0) {
               first = ItemManager.getCrafting(craftings[0]);
            }

            if (craftings.length > 1) {
               second = ItemManager.getCrafting(craftings[1]);
            }
         }

         if (upgraded) {
            second = null;
         }

         this.setCraftables(first, second);
      }
   }

   public void setCraftables(CraftingItem first, CraftingItem second) {
      this.mCraftingSlot1.setVisible(first != null);
      this.mCraftingSlot2.setVisible(second != null);
      this.mCraftingSlot1.setCraftable(first);
      this.mCraftingSlot2.setCraftable(second);
   }

   @Override
   public void callback(int id) {
      if (id == -1) {
         HUDButton button = this.mButtonUpgrade;
         button.setEnabled(this.mSlotItemUpgrade.isValueReached());
      } else if (id == 2) {
         this.upgrade();
      } else if (id == 1) {
         WindowManager.closeWindow();
      }
   }

   public boolean setUpgraded() {
      ConstructionItem item = (ConstructionItem)this.mConstruction.getItem();
      int state = this.mConstruction.getState();
      Mission mission = MissionManager.getMission(Tuner.getMissionToUnlockBuildingUpgrade());
      boolean completed = true;
      if (mission != null) {
         completed = mission.isCompleted();
      }

      boolean locked = !completed;
      this.mTextLocked.setVisible(locked);
      boolean hide = state == 3 || locked;
      HUDAutoTextField text = this.mTextUpgrade;
      boolean visible = false;
      if (!hide) {
         visible = item.isUpgradeable();
      }

      text.setVisible(visible);
      HUDButton button = this.mButtonUpgrade;
      visible = false;
      if (!hide) {
         visible = item.isUpgradeable();
      }

      button.setVisible(visible);
      HUDCraftingSlotItem slot = this.mSlotItemUpgrade;
      visible = false;
      if (!hide) {
         visible = item.isUpgradeable();
      }

      slot.setVisible(visible);
      if (!completed) {
         return true;
      }

      if (this.mConstruction.getState() == 3) {
         return false;
      }

      if (!item.isUpgradeable()) {
         return false;
      }

      int[][] upgrade = item.getUpgradeItems();
      this.mSlotItemUpgrade.setValues(ItemManager.getCollectible(upgrade[0][0]), upgrade[0][1]);
      button = this.mButtonUpgrade;
      button.setEnabled(this.mSlotItemUpgrade.isValueReached());
      return true;
   }

   public void upgrade() {
      ConstructionItem item = (ConstructionItem)this.mConstruction.getItem();
      int[][] upgrade = item.getUpgradeItems();
      Inventory inventory = PlayerProfile.getInventory();
      inventory.removeItem(ItemManager.getCollectible(upgrade[0][0]), upgrade[0][1]);
      this.mConstruction.changeState(3);
      MissionManager.updateCounter(2528, item.getID(), 1);
      MissionManager.updateCounter(2528, 80, 1);
      this.setConstruction(this.mConstruction);
   }
}
