package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDImage;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.SlotBackground;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.Collection;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.spriteobject.CollisionBox;

public class WindowTradeCollection extends Window {
   private HUDAutoTextField mTitle;
   private HUDAutoTextField mCollectionTitle;
   private HUDAutoTextField mRewardDescription;
   private HUDAutoTextField mWouldYouLike;
   private static RoundRectangle mBack;
   private static HUDButton mButtonSkip;
   private static HUDButton mButtonTrade;
   private static Collection mCurrentCollection;
   private static HUDImage[] mIcons;
   private static SlotBackground mSlot;

   public WindowTradeCollection() {
      this.mBackground = AnimationsManager.loadShared(74621);
      this.mCollisions = AnimationsManager.loadShared(74911);
      this.initCollisions();
      this.mTitle = new HUDAutoTextField(this.getCollisionBox(1));
      this.mTitle.setCentered(true, true);
      HUDAutoTextField text = this.mTitle;
      text.setText(1547, DCiDead.getFont(5));
      mBack = new RoundRectangle(0);
      RoundRectangle back = mBack;
      back.setSizes(this.mCollisions.getCollisionBox(2));
      mSlot = new SlotBackground(this.mCollisions.getCollisionBox(9));
      this.mCollectionTitle = new HUDAutoTextField(this.mCollisions.getCollisionBox(5));
      this.mCollectionTitle.setCentered(true, false);
      this.mRewardDescription = new HUDAutoTextField(this.mCollisions.getCollisionBox(6));
      this.mRewardDescription.setCentered(true, false);
      mButtonSkip = new HUDButton(51000, null, 6);
      mButtonSkip.setText(104);
      mButtonTrade = new HUDButton(51001, null, 3);
      mButtonTrade.setText(1687);
      CollisionBox box = this.mCollisions.getCollisionBox(2);
      int width = box.getWidth();
      int skipWidth = mButtonSkip.getWidth();
      int tradeWidth = mButtonTrade.getWidth();
      int y = box.getY();
      int height = box.getHeight();
      int skipHeight = mButtonSkip.getHeight();
      int gap = (width - skipWidth - tradeWidth) / 3;
      y += height - skipHeight / 2;
      HUDButton button = mButtonSkip;
      button.setPosition(box.getX() + gap, y);
      button = mButtonTrade;
      int x = box.getX();
      skipWidth = mButtonSkip.getWidth();
      button.setPosition(x + 2 * gap + skipWidth, y);
      this.mWouldYouLike = new HUDAutoTextField(this.mCollisions.getCollisionBox(4));
      this.mWouldYouLike.setText(1548, 3);
      this.mWouldYouLike.setCentered(true, true);
      this.mWouldYouLike.setSplitUsingWidth(true);
      mIcons = new HUDImage[5];

      for (int i = 0; i < (mIcons == null ? 0 : mIcons.length); i++) {
         mIcons[i] = new HUDImage();
         mIcons[i].setAnimationFrame(0);
      }

      this.mObjects.addObject(this.mTitle);
      this.mObjects.addObject(mBack);
      this.mObjects.addObject(mSlot);
      this.mObjects.addObject(this.mCollectionTitle);
      this.mObjects.addObject(mIcons[0]);
      this.mObjects.addObject(mIcons[1]);
      this.mObjects.addObject(mIcons[2]);
      this.mObjects.addObject(mIcons[3]);
      this.mObjects.addObject(mIcons[4]);
      this.mObjects.addObject(this.mRewardDescription);
      this.mObjects.addObject(this.mWouldYouLike);
      this.mObjects.addObject(mButtonSkip);
      this.mObjects.addObject(mButtonTrade);
   }

   public void setCollection(Collection collection) {
      mCurrentCollection = collection;
      HUDAutoTextField text = this.mCollectionTitle;
      text.setText(collection.getTitleID(), 4);
      CollisionBox box = this.mCollisions.getCollisionBox(3);
      int width = box.getWidth();
      int offset = width / 10;

      for (int i = 0; i < (mIcons == null ? 0 : mIcons.length); i++) {
         HUDImage icon = mIcons[i];
         icon.setImage(ItemManager.getItem(collection.getItems()[i]).getIconAnimationID());
         icon = mIcons[i];
         icon.setPositionY(box.getY() + box.getHeight() / 2);
         icon = mIcons[i];
         icon.setPositionX(box.getX() + offset);
         offset += width / 5;
      }

      text = this.mRewardDescription;
      int currency = collection.getRewardAmount()[0][0];
      int amount = collection.getRewardAmount()[0][1];
      String amountText = Integer.toString(amount);
      text.setText(currency == 9 ? 164 : 165, 3, amountText, -1);
   }

   @Override
   public void callback(int id) {
      if (id == 51001) {
         WindowManager.closeWindow();
         WindowManager.openWindow((byte)20);
         WindowStuff stuff = (WindowStuff)WindowManager.getWindow((byte)20);
         stuff.openTab(1001);
         stuff.scrollToCollection(mCurrentCollection);
      } else {
         if (id == 51000) {
            WindowManager.closeWindow();
         }
      }
   }
}
