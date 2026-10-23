package com.dchoc.windows;

import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.tracking.PopupEvent;
import com.dchoc.spriteobject.SpriteObject;

public class WindowOutOfCashCoins extends Window {
   public static final int MODE_CASH = 0;
   public static final int MODE_COINS = 1;
   private static HUDButton buttonBuy;
   private static HUDButton buttonSkip;
   private static int[] mButtonTitles;
   private static PopupEvent[] mEvents;
   private static SpriteObject[] mImages;
   private static HUDAutoTextField mMessage;
   private static int mMode;
   private static RoundRectangle mRectangle;
   private static HUDAutoTextField mTitle;
   private static int[] mTitles;

   public WindowOutOfCashCoins() {
      this.mCollisions = AnimationsManager.loadShared(67351);
      this.mBackground = AnimationsManager.loadShared(74621);
      this.initCollisions();
      mImages = new SpriteObject[]{AnimationsManager.loadShared(67341), AnimationsManager.loadShared(67349)};
      mTitles = new int[]{2346, 2345};
      mTitle = new HUDAutoTextField(this.getCollisionBox(1));
      mTitle.setCentered(true, true);
      mTitle.setScaleToFit(true);
      mMessage = new HUDAutoTextField(this.getCollisionBox(20));
      mMessage.setCentered(true, true);
      mMessage.setSplitUsingWidth(true);
      mMessage.setText(2437, 4);
      mButtonTitles = new int[]{2237, 1838};
      mEvents = new PopupEvent[]{new PopupEvent(3), new PopupEvent(2)};
      buttonSkip = new HUDButton(18000, this.mCollisions.getCollisionBox(10), 6);
      buttonBuy = new HUDButton(18001, this.mCollisions.getCollisionBox(11), 3);
      buttonSkip.setText(104);
      mRectangle = new RoundRectangle(0);
      mRectangle.setSizes(this.getCollisionBox(6));
      this.mObjects.addObject(mRectangle);
      this.mObjects.addObject(mTitle);
      this.mObjects.addObject(mMessage);
      this.mObjects.addObject(buttonBuy);
      this.mObjects.addObject(buttonSkip);
      this.setMode(1);
   }

   @Override
   public void draw() {
      super.draw();
      if (this.mState != 1) {
         this.drawImageCentered(mImages[mMode], this.getCollisionBox(30));
      }
   }

   public void setMode(int mode) {
      mMode = mode;
      mTitle.setText(mTitles[mode], 5);
      buttonBuy.setText(mButtonTitles[mMode]);
      this.addTrackingEvent(mEvents[mMode]);
   }

   @Override
   public void callback(int id) {
      if (id == 18001) {
         WindowManager.closeWindow();
         WindowManager.openWindow((byte)22);
         WindowMarket market = (WindowMarket)WindowManager.getWindow((byte)22);
         if (mMode == 1) {
            market.setMode(6);
         } else if (mMode == 0) {
            market.setMode(7);
         }
      }
   }
}
