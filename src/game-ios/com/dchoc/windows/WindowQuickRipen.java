package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUD;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDImage;
import com.dchoc.hud.HUDInteger;
import com.dchoc.hud.HUDQuickRipenTab;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.objects.FarmingObject;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.Toolkit;

public class WindowQuickRipen extends Window {
   private HUDInteger mCashCounter;
   private static HUDButton mButtonBuy;
   private static HUDButton mButtonNotNow;
   private static int mCash;
   private static HUDImage mCashImage;
   private static FarmingObject mCrop;
   private static HUDAutoTextField mDescription1;
   private static HUDAutoTextField mDescription2;
   private static RoundRectangle mRectangleBackground;
   private static HUDImage mReviveImage;
   private static HUDAutoTextField mTitle;
   private static String[] smParam;

   public WindowQuickRipen() {
      this.mBackground = AnimationsManager.loadShared(74621);
      this.mCollisions = AnimationsManager.loadShared(78931);
      this.initCollisions();
      CollisionBox box = this.getCollisionBox(4);
      mRectangleBackground = new RoundRectangle(0);
      mRectangleBackground.setSizes(box);
      box = this.getCollisionBox(1);
      mTitle = new HUDAutoTextField(box);
      mTitle.setCentered(true, true);
      HUDAutoTextField text = mTitle;
      text.setText(2763, DCiDead.getFont(5));
      box = this.getCollisionBox(2);
      mDescription1 = new HUDAutoTextField(box);
      mDescription1.setCentered(true, true);
      text = mDescription1;
      text.setText(2764, DCiDead.getFont(4));
      box = this.getCollisionBox(3);
      mDescription2 = new HUDAutoTextField(box);
      mDescription2.setCentered(true, true);
      box = this.getCollisionBox(5);
      mReviveImage = new HUDImage(75015, box);
      mReviveImage.setCenterType((byte)3);
      box = this.getCollisionBox(6);
      mButtonNotNow = new HUDButton(32004, box, 6);
      mButtonNotNow.setText(2359);
      box = this.getCollisionBox(7);
      mButtonBuy = new HUDButton(55001, box, 3);
      box = this.getCollisionBox(8);
      mCashImage = new HUDImage(66255, box);
      HUDImage image = mCashImage;
      image.setPositionX(box.getX() + box.getWidth() - mCashImage.getWidth() / 2);
      mCashImage.setCenterType((byte)2);
      box = this.getCollisionBox(9);
      this.mCashCounter = new HUDInteger(65571);
      HUDInteger counter = this.mCashCounter;
      counter.setPosition(box.getX() + box.getWidth() / 2, box.getY() + box.getHeight() / 2);
      this.mCashCounter.setCenterType((byte)3);
      this.mObjects.addObject(mRectangleBackground);
      this.mObjects.addObject(mTitle);
      this.mObjects.addObject(mDescription1);
      this.mObjects.addObject(mDescription2);
      this.mObjects.addObject(mReviveImage);
      this.mObjects.addObject(mButtonNotNow);
      this.mObjects.addObject(mButtonBuy);
      this.mObjects.addObject(mCashImage);
      this.mObjects.addObject(this.mCashCounter);
      smParam = new String[1];
   }

   public boolean setCrop(FarmingObject crop, int cash) {
      if (crop == null) {
         return false;
      }

      mCrop = crop;
      mCash = cash;
      this.mCashCounter.setValue(cash);
      smParam[0] = Toolkit.getText(mCrop.getItem().getTitle());
      HUDAutoTextField text = mDescription2;
      text.setText(Toolkit.replaceParameters(Toolkit.getText(2765), smParam), DCiDead.getFont(4));
      return true;
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      HUDButton button = mButtonBuy;
      button.setEnabled(mCrop.isGrowing());
   }

   @Override
   public void callback(int id) {
      if (this.mState == 0) {
         HUDQuickRipenTab tab = HUD.getInGame().getQuickRipenTab();
         if (id == 32004 || id == 55001) {
            WindowManager.closeWindow();
            if (id == 55001) {
               if (PlayerProfile.getCash() >= mCash) {
                  tab.changeState(7);
                  return;
               }

               WindowOutOfCashCoins money = (WindowOutOfCashCoins)WindowManager.getWindow((byte)11);
               money.setMode(0);
               WindowManager.openWindow((byte)11);
            }

            tab.changeState(2);
         }
      }
   }
}
