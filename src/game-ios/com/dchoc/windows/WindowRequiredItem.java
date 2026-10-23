package com.dchoc.windows;

import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDBatterySlotItem;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.actions.LootAction;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.CollectibleItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.spriteobject.CollisionBox;

public class WindowRequiredItem extends Window {
   private HUDAutoTextField mTextTitle;
   private HUDAutoTextField mTextDescription;
   private RoundRectangle mRectangle;
   private HUDBatterySlotItem mSlotItem;
   private HUDButton mButtonActivate;
   private LootAction mLootAction;
   private int mRequiredAmount;

   public WindowRequiredItem() {
      this.mBackground = AnimationsManager.loadShared(74621);
      this.mCollisions = AnimationsManager.loadShared(79877);
      this.initCollisions();
      this.constructCloseButton(1);
      this.mObjects.addObject(this.mButtonClose);
      this.mTextTitle = new HUDAutoTextField(this.mCollisions.getCollisionBox(1));
      this.mTextTitle.setCentered(true, false);
      this.mTextTitle.setSplitUsingWidth(true);
      this.mTextTitle.setScaleToFit(true);
      this.mTextTitle.searchScale(true);
      this.mTextTitle.setText(3934, 5);
      this.mTextDescription = new HUDAutoTextField(this.mCollisions.getCollisionBox(20));
      this.mTextDescription.setCentered(true, false);
      this.mTextDescription.setSplitUsingWidth(true);
      this.mTextDescription.setText(3935, 3);
      CollisionBox box = this.mCollisions.getCollisionBox(30);
      this.mRectangle = new RoundRectangle(0);
      this.mRectangle.setSizes(box);
      box = this.mCollisions.getCollisionBox(10);
      this.mSlotItem = new HUDBatterySlotItem(box.getX(), box.getY());
      box = this.mCollisions.getCollisionBox(0);
      this.mButtonActivate = new HUDButton(2, box, 3);
      this.mButtonActivate.setText(3937);
      this.mObjects.addObject(this.mRectangle);
      this.mObjects.addObject(this.mSlotItem);
      this.mObjects.addObject(this.mButtonActivate);
      this.mObjects.addObject(this.mTextTitle);
      this.mObjects.addObject(this.mTextDescription);
   }

   public void setItem(LootAction action, int[][] items) {
      this.mLootAction = action;
      this.mRequiredAmount = items[0][1];
      CollectibleItem item = ItemManager.getCollectible(items[0][0]);
      this.mSlotItem.setValues(item, items[0][1]);
      this.updateActivateButton();
   }

   @Override
   public void callback(int id) {
      if (id == -1) {
         this.updateActivateButton();
      } else if (id == 2) {
         WindowManager.closeWindow();
         LootAction action = this.mLootAction;
         boolean consumed = action.tryConsumeCost(action.getEnergyCost(), false);
         if (consumed) {
            this.mLootAction.changeState(0);
         } else {
            this.mLootAction.stop(null);
         }
      } else if (id == 1) {
         WindowManager.closeWindow();
         this.mLootAction.stop(null);
      }
   }

   public void updateActivateButton() {
      Item item = this.mSlotItem.getItem();
      int count = PlayerProfile.getInventory().getItemCount(item);
      HUDButton button = this.mButtonActivate;
      if (count < this.mRequiredAmount) {
         button.setEnabled(false);
      } else {
         button.setEnabled(true);
      }
   }
}
