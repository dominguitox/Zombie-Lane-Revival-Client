package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDImage;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.FarmingItem;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.objects.FarmingObject;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.toolkit.Toolkit;

public class WindowWitheredCrops extends Window {
   private static HUDButton mButtonClearOut;
   private static HUDButton mButtonRevive;
   private static HUDAutoTextField mTitle;
   private static HUDAutoTextField mDescription1;
   private static HUDAutoTextField mDescription2;
   private static FarmingItem mItem;
   private static FarmingObject mObject;
   private static final String[] mParams = new String[1];
   private static RoundRectangle mRectangleBackground;
   private static HUDImage mReviveIcon;
   private static String mTextDescription2;
   private static String mTextRevive;

   public WindowWitheredCrops() {
      this.mBackground = AnimationsManager.loadShared(74621);
      this.mCollisions = AnimationsManager.loadShared(74903);
      this.initCollisions();
      mRectangleBackground = new RoundRectangle(0);
      RoundRectangle rectangle = mRectangleBackground;
      rectangle.setSizes(this.getCollisionBox(1));
      mTitle = new HUDAutoTextField(this.getCollisionBox(4));
      mTitle.setCentered(true, true);
      HUDAutoTextField text = mTitle;
      text.setText(1856, DCiDead.getFont(5));
      mDescription1 = new HUDAutoTextField(this.getCollisionBox(5));
      mDescription1.setCentered(true, true);
      mDescription1.setSplitUsingWidth(true);
      text = mDescription1;
      text.setText(1857, DCiDead.getFont(4));
      mDescription2 = new HUDAutoTextField(this.getCollisionBox(8));
      mDescription2.setCentered(true, true);
      mDescription1.setSplitUsingWidth(true);
      mReviveIcon = new HUDImage(75015, this.getCollisionBox(6));
      mReviveIcon.setCenterType((byte)3);
      mButtonClearOut = new HUDButton(50000, this.mCollisions.getCollisionBox(2), 6);
      mButtonClearOut.setText(2350);
      mButtonRevive = new HUDButton(50001, this.mCollisions.getCollisionBox(3), 2);
      mTextDescription2 = Toolkit.getText(2349);
      mTextRevive = Toolkit.getText(2351);
      this.mObjects.addObject(mRectangleBackground);
      this.mObjects.addObject(mTitle);
      this.mObjects.addObject(mDescription1);
      this.mObjects.addObject(mDescription2);
      this.mObjects.addObject(mReviveIcon);
      this.mObjects.addObject(mButtonClearOut);
      this.mObjects.addObject(mButtonRevive);
   }

   public void setTarget(FarmingObject object) {
      mObject = object;
      mItem = (FarmingItem)object.getItem();
      mParams[0] = Toolkit.getText(mItem.getTitle());
      HUDAutoTextField text = mDescription2;
      text.setText(Toolkit.replaceParameters(mTextDescription2, mParams), DCiDead.getFont(4));
      mParams[0] = Integer.toString(mItem.getRevivePriceCoins());
      HUDButton button = mButtonRevive;
      button.setText(Toolkit.replaceParameters(mTextRevive, mParams));
      int currency = mItem.getReviveCurrency();
      if (currency == 7) {
         mButtonRevive.setIcon(66255);
      } else if (currency == 9) {
         mButtonRevive.setIcon(66253);
      }
   }

   @Override
   public void callback(int id) {
      if (this.mState == 0) {
         if (id == 50001) {
            int price = mItem.getRevivePriceCoins();
            int currency = mItem.getReviveCurrency();
            int mode;
            if (currency == 7) {
               if (price <= PlayerProfile.getCash()) {
                  PlayerProfile.removeCash(price);
                  WindowManager.closeWindow();
                  mObject.setRevived(true);
                  MissionManager.updateCounter(2291, 80, 1);
                  return;
               }

               WindowManager.closeWindow();
               mode = 0;
            } else {
               if (currency != 9) {
                  return;
               }

               if (price <= PlayerProfile.getCoins()) {
                  PlayerProfile.removeCoins(price);
                  WindowManager.closeWindow();
                  mObject.setRevived(true);
                  MissionManager.updateCounter(2291, 80, 1);
                  return;
               }

               WindowManager.closeWindow();
               mode = 1;
            }

            WindowOutOfCashCoins money = (WindowOutOfCashCoins)WindowManager.getWindow((byte)11);
            money.setMode(mode);
            WindowManager.openWindow((byte)11);
            return;
         }

         if (id == 50000) {
            mObject.setHarvested(true);
            WindowManager.closeWindow();
         }
      }
   }
}
