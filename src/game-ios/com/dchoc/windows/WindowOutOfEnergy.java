package com.dchoc.windows;

import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDSlot;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.playhaven.PlayHaven;
import com.dchoc.spriteobject.CollisionBox;
import zlport.ios.Ios;

public class WindowOutOfEnergy extends Window {
   private static RoundRectangle mRectangle;
   private static HUDSlot[] mItems;
   private static HUDAutoTextField mTitle;

   public WindowOutOfEnergy() {
      this.mCollisions = AnimationsManager.loadShared(67277);
      this.mBackground = AnimationsManager.loadShared(74621);
      this.initCollisions();
      this.constructCloseButton(13000);
      if (Ios.platform().isImpactSupported()) {
         this.constructImpactVideoButton(13001);
      }

      int[] ids = new int[]{1923, 1924, 1928};
      mTitle = new HUDAutoTextField(this.getCollisionBox(1));
      mTitle.setCentered(true, true);
      mTitle.setSplitUsingWidth(true);
      mTitle.setScaleToFit(true);
      mTitle.searchScale(true);
      mTitle.setText(1693, 5);
      mRectangle = new RoundRectangle(0);
      RoundRectangle rectangle = mRectangle;
      rectangle.setSizes(this.getCollisionBox(20));
      mItems = new HUDSlot[3];

      for (int i = 0; i < mItems.length; i++) {
         CollisionBox box = this.mCollisions.getCollisionBox(10 + i);
         HUDSlot item = new HUDSlot(ItemManager.getItem(ids[i]), this, false);
         mItems[i] = item;
         HUDSlot slot = mItems[i];
         slot.setPositionX(box.getX());
         slot = mItems[i];
         slot.setPositionY(box.getY());
      }

      this.mObjects.addObject(mRectangle);
      this.mObjects.addObject(mTitle);
      this.mObjects.addObject(this.mButtonClose);
      this.mObjects.addObject(mItems[0]);
      this.mObjects.addObject(mItems[1]);
      this.mObjects.addObject(mItems[2]);
   }

   @Override
   public void open() {
      super.open();

      for (int i = 0; i < (mItems == null ? 0 : mItems.length); i++) {
         mItems[i].refresh();
      }
   }

   @Override
   public void setState(byte state) {
      super.setState(state);
      if (state == 0) {
         PlayHaven.triggerOutOfEnergy();
      }
   }
}
