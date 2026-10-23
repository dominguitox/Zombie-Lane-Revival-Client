package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDSlot;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.ToolkitHelpers;
import java.util.Vector;

public class WindowTransactionConfirmation extends Window {
   private static HUDSlot[] mItemSlots;
   private static Vector mItemsToShow;
   private RoundRectangle mRectangleBG;
   private HUDAutoTextField mTitleText;
   private HUDAutoTextField mDescriptionText;

   public WindowTransactionConfirmation() {
      this.mBackground = AnimationsManager.loadShared(74621);
      this.mCollisions = AnimationsManager.loadShared(74887);
      this.initCollisions();
      this.mRectangleBG = new RoundRectangle(0);
      RoundRectangle rectangle = this.mRectangleBG;
      rectangle.setSizes(this.getCollisionBox(7));
      this.mTitleText = new HUDAutoTextField(this.getCollisionBox(5));
      HUDAutoTextField title = this.mTitleText;
      title.setText(2446, DCiDead.getFont(5));
      this.mTitleText.setCentered(true, true);
      this.mDescriptionText = new HUDAutoTextField(this.getCollisionBox(2));
      HUDAutoTextField description = this.mDescriptionText;
      description.setText(2352, DCiDead.getFont(4));
      this.mDescriptionText.setCentered(true, true);
      this.constructCloseButton(41000);
      mItemSlots = new HUDSlot[3];
      HUDSlot slot = new HUDSlot();
      mItemSlots[0] = slot;
      slot = new HUDSlot();
      mItemSlots[1] = slot;
      slot = new HUDSlot();
      mItemSlots[2] = slot;
      mItemsToShow = new Vector(6);
      this.mObjects.addObject(this.mTitleText);
      this.mObjects.addObject(this.mRectangleBG);
      this.mObjects.addObject(this.mDescriptionText);
      this.mObjects.addObject(mItemSlots[0]);
      this.mObjects.addObject(mItemSlots[1]);
      this.mObjects.addObject(mItemSlots[2]);
      this.mObjects.addObject(this.mButtonClose);
   }

   public void setTransaction(int amount, int currency) {
      this.findItemsToShow(currency);

      for (int i = 0; i < (mItemSlots == null ? 0 : mItemSlots.length); i++) {
         mItemSlots[i].setVisible(false);
      }

      int size = mItemsToShow.size();
      if (size > 0) {
         int i = 0;

         do {
            Object item = mItemsToShow.elementAt(i);
            HUDSlot slot = mItemSlots[i];
            slot.init(item, this, false);
            CollisionBox box = this.getCollisionBox(10);
            int y = box.getY();
            int height = box.getHeight();
            int slotHeight = mItemSlots[i].getHeight();
            box = this.getCollisionBox(7);
            int x = box.getX();
            box = this.getCollisionBox(7);
            int width = box.getWidth();
            int count = mItemsToShow.size();
            slot = mItemSlots[i];
            int segment = width / count;
            int slotWidth = slot.getWidth();
            slot.setPosition(segment * i + x + (segment - slotWidth) / 2, y + (height - slotHeight) / 2);
            mItemSlots[i].setVisible(true);
            i++;
            size = mItemsToShow.size();
         } while (i < size);
      }
   }

   public void findItemsToShow(int currency) {
      int balance = currency == 7 ? PlayerProfile.getCash() : PlayerProfile.getCoins();
      mItemsToShow.removeAllElements();
      WindowMarket market = (WindowMarket)WindowManager.getWindow((byte)22);
      Item item = market.getItemWithPrice(currency, balance, mItemsToShow);
      if (item != null) {
         mItemsToShow.addElement(item);
      }

      item = market.getItemWithPrice(currency, balance, mItemsToShow);
      if (item != null) {
         mItemsToShow.addElement(item);
      }

      item = market.getItemWithPrice(currency, balance, mItemsToShow);
      if (item != null) {
         mItemsToShow.addElement(item);
      }

      item = market.getItemWithPrice(currency, balance, mItemsToShow);
      if (item != null) {
         mItemsToShow.addElement(item);
      }

      item = market.getItemWithPrice(currency, balance, mItemsToShow);
      if (item != null) {
         mItemsToShow.addElement(item);
      }

      item = market.getItemWithPrice(currency, balance, mItemsToShow);
      if (item != null) {
         mItemsToShow.addElement(item);
      }

      while (mItemsToShow.size() > (mItemSlots == null ? 0 : mItemSlots.length)) {
         int size = mItemsToShow.size();
         int index = ToolkitHelpers.getRandomInt(0, size - 1);
         ToolkitHelpers.fastRemove(index, mItemsToShow);
      }
   }
}
