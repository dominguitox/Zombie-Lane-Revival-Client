package com.dchoc.windows;

import com.dchoc.hud.HUD;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDSlot;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.objects.FarmingObject;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tracking.EarnGCEvent;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.spriteobject.CollisionBox;

public class WindowSellItem extends Window {
   private static HUDButton mButtonNo;
   private static HUDButton mButtonYes;
   private static HUDAutoTextField mDescription;
   private static Item mItem;
   private static HUDSlot mItemSlot;
   private static HUDAutoTextField mTitle;
   private static RoundRectangle smBackground;
   private static IsometricObject smObject;
   private EarnGCEvent mEvent;

   public WindowSellItem() {
      this.mBackground = AnimationsManager.loadShared(74621);
      this.mCollisions = AnimationsManager.loadShared(80183);
      this.initCollisions();
      mButtonNo = new HUDButton(19001, this.mCollisions.getCollisionBox(10), 6);
      mButtonYes = new HUDButton(19000, this.mCollisions.getCollisionBox(11), 3);
      mButtonNo.setText(46);
      mButtonYes.setText(47);
      mTitle = new HUDAutoTextField(this.mCollisions.getCollisionBox(1));
      mTitle.setCentered(true, false);
      mTitle.setText(1728, 5);
      mItemSlot = new HUDSlot();
      mItemSlot.disableButton(true);
      mItemSlot.disableDescription(true);
      mDescription = new HUDAutoTextField(this.getCollisionBox(5));
      mDescription.setCentered(true, false);
      mDescription.setSplitUsingWidth(true);
      smBackground = new RoundRectangle(0);
      RoundRectangle rectangle = smBackground;
      rectangle.setSizes(this.getCollisionBox(6));
      this.mObjects.addObject(smBackground);
      this.mObjects.addObject(mTitle);
      this.mObjects.addObject(mItemSlot);
      this.mObjects.addObject(mDescription);
      this.mObjects.addObject(mButtonYes);
      this.mObjects.addObject(mButtonNo);
      this.mEvent = new EarnGCEvent(3);
   }

   public void setObject(IsometricObject object) {
      smObject = object;
      Item item = object.getItem();
      mItem = item;
      if (item != null) {
         int value;
         if (item.getCurrency() == 9) {
            int price = item.getPrice();
            int percent = Tuner.getSellValuePercentage();
            value = price * percent / 100;
         } else if (item.getCurrency() == 7) {
            int price = item.getPrice();
            int rate = Tuner.getCashToCoinsRate();
            int percent = Tuner.getSellValuePercentage();
            value = rate * price * percent / 100;
         } else {
            value = -1;
         }

         CollisionBox box = this.getCollisionBox(7);
         HUDAutoTextField description = mDescription;
         description.setText(1729, 3, Integer.toString(value), -1);
         mItemSlot.init(mItem, this, false, true);
         HUDSlot slot = mItemSlot;
         int x = box.getX();
         int width = box.getWidth();
         int slotWidth = mItemSlot.getWidth();
         int y = box.getY();
         int height = box.getHeight();
         int slotHeight = mItemSlot.getHeight();
         slot.setPosition(x + (width - slotWidth) / 2, y + (height - slotHeight) / 2);
         mItemSlot.setRemoveMissionAndLevelLocks();
      }
   }

   @Override
   public void callback(int id) {
      if (this.mState == 0) {
         IsometricScene scene = IsometricUtils.getScene();
         Item objectItem = smObject.getItem();
         if (id != 19000) {
            if (id == 19001) {
               WindowManager.closeWindow();
            }
         } else {
            WindowManager.closeWindow();
            Item item = mItem;
            int value;
            if (item.getCurrency() == 9) {
               int price = item.getPrice();
               int percent = Tuner.getSellValuePercentage();
               value = price * percent / 100;
            } else if (item.getCurrency() == 7) {
               int price = item.getPrice();
               int rate = Tuner.getCashToCoinsRate();
               int percent = Tuner.getSellValuePercentage();
               value = rate * price * percent / 100;
            } else {
               value = -1;
            }

            PlayerProfile.addCoins(value);
            scene.removeObject(smObject, true);
            scene.changeState(0);
            WindowManager.closeWindow();
            if (smObject.getType() == 6 && ((FarmingObject)smObject).isAdult()) {
               int itemId = objectItem.getID();
               MissionManager.updateCounter(2504, itemId, 1);
               MissionManager.updateCounter(2504, 80, 1);
            }

            HUD.changeState(6);
            EarnGCEvent event = this.mEvent;
            int itemId = mItem.getID();
            int subType = mItem.getSubType();
            item = mItem;
            if (item.getCurrency() == 9) {
               int price = item.getPrice();
               int percent = Tuner.getSellValuePercentage();
               value = price * percent / 100;
            } else if (item.getCurrency() == 7) {
               int price = item.getPrice();
               int rate = Tuner.getCashToCoinsRate();
               int percent = Tuner.getSellValuePercentage();
               value = rate * price * percent / 100;
            } else {
               value = -1;
            }

            event.trackEarn(itemId, subType, value);
         }
      }
   }
}
