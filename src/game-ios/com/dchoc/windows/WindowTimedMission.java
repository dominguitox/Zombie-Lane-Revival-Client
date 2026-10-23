package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUD;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDImage;
import com.dchoc.hud.HUDInteger;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.events.EventManager;
import com.dchoc.idead.events.TimedEvent;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.spriteobject.CollisionBox;

public class WindowTimedMission extends Window {
   public static final int BUTTON_OK = 0;
   public static final int COLLISION_BACKGROUND = 2;
   public static final int COLLISION_DESCRIPTION = 4;
   public static final int COLLISION_OK = 10;
   public static final int COLLISION_TITLE = 1;
   private static final int OBJECT_ID_TIMER = 1;
   private static final int OBJECT_ID_TIMER_GLOW = 2;
   private int mMode;
   private Mission mMission;
   private HUDAutoTextField mTextTitle;
   private HUDAutoTextField mTextDescription;
   private RoundRectangle mRectangleBackground;
   private HUDImage mImageTimer;
   private HUDImage mImageTimerGlow;
   private HUDImage mImageHordeComing;
   private HUDImage mImageCash;
   private HUDButton mButtonOK;
   private HUDButton mButtonDecline;
   private HUDButton mButtonBuy;
   private HUDInteger mPrice;
   private boolean mMissionExpired;

   public WindowTimedMission() {
      this.mBackground = AnimationsManager.loadShared(74621);
      this.mCollisions = AnimationsManager.loadShared(67333);
      this.initCollisions();
      CollisionBox box = this.mCollisions.getCollisionBox(2);
      this.mRectangleBackground = new RoundRectangle(0);
      this.mRectangleBackground.setSizes(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      box = this.mCollisions.getCollisionBox(5);
      this.mImageHordeComing = new HUDImage(-1, box.getX() + (box.getWidth() >> 1), box.getY() + (box.getHeight() >> 1));
      box = this.mCollisions.getCollisionBox(1);
      this.mTextTitle = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextTitle.setCentered(true, true);
      box = this.mCollisions.getCollisionBox(4);
      this.mTextDescription = new HUDAutoTextField(box.getX(), box.getY() - 3, box.getWidth(), box.getHeight());
      this.mTextDescription.setCentered(true, false);
      this.mTextDescription.setAlignBottom(true);
      this.mTextDescription.setSplitUsingWidth(true);
      this.mButtonOK = new HUDButton(0, this.mCollisions.getCollisionBox(10), 2);
      this.mButtonDecline = new HUDButton(32004, this.getCollisionBox(11), 6);
      this.mButtonDecline.setText(2359);
      this.mButtonBuy = new HUDButton(1, this.getCollisionBox(12), 3);
      box = this.mCollisions.getCollisionBox(13);
      this.mImageCash = new HUDImage(66255, box);
      this.mImageCash.setPosition(box.getX() + box.getWidth() - this.mImageCash.getWidth() / 2, box.getY() + box.getHeight() / 2);
      this.mCollisions.getCollisionBox(12);
      this.mPrice = new HUDInteger(65571);
      this.mPrice.setCenterType((byte)3);
      box = this.mCollisions.getCollisionBox(1);
      this.mImageTimer = new HUDImage(74773, -1, -1);
      this.mImageTimerGlow = new HUDImage(74775, -1, -1);
      this.mImageTimer.setPositionY(box.getY() + this.mImageTimer.getHeight() / 2);
      this.mImageTimerGlow.setPositionY(this.mImageTimer.getPositionY());
      this.mObjects.addObject(this.mRectangleBackground);
      this.mObjects.addObject(this.mImageHordeComing);
      this.mObjects.addObject(this.mTextTitle);
      this.mObjects.addObject(this.mTextDescription);
      this.mObjects.addObject(this.mButtonOK);
      this.mObjects.addObject(this.mButtonDecline);
      this.mObjects.addObject(this.mButtonBuy);
      this.mObjects.addObject(this.mImageCash);
      this.mObjects.addObject(this.mPrice);
      this.mObjects.addObject(this.mImageTimerGlow);
      this.mObjects.addObject(this.mImageTimer);
      this.mMissionExpired = false;
   }

   public void setMission(Mission mission, boolean expired, int mode) {
      if (mission != null) {
         this.mMission = mission;
         this.mMissionExpired = expired;
         this.mMode = mode;
         if (mode == 0) {
            this.mImageHordeComing.setImage(67339);
         } else if (mode == 1) {
            this.mImageHordeComing.setImage(expired ? 78935 : 78963);
         }

         this.mButtonOK.setVisible(false);
         this.mButtonDecline.setVisible(false);
         this.mButtonBuy.setVisible(false);
         this.mPrice.setVisible(false);
         this.mImageCash.setVisible(false);
         if (!expired) {
            this.mTextTitle.setText(mission.getTitleTextID(), DCiDead.getFont(5));
            if (mode == 1) {
               this.mTextDescription.setText(2793, DCiDead.getFont(3));
            } else {
               this.mTextDescription.setText(mission.getDescriptionTextID(), DCiDead.getFont(3));
            }

            this.mButtonOK.setText(55);
            this.mButtonOK.setVisible(true);
            this.mImageTimer.setVisible(false);
            this.mImageTimerGlow.setVisible(false);
         } else {
            this.mTextTitle.setText(2060, DCiDead.getFont(5));
            if (mode == 0) {
               this.mTextDescription.setText(2061, DCiDead.getFont(3));
               this.mButtonOK.setText(54);
               this.mButtonOK.setVisible(true);
            } else if (mode == 1) {
               this.mTextDescription.setText(2799, DCiDead.getFont(3));
               this.mButtonDecline.setVisible(true);
               this.mButtonBuy.setVisible(true);
               Item item = ItemManager.getItem(3300);
               this.mPrice.setValue(item.getPrice() << 1);
               CollisionBox box = this.mCollisions.getCollisionBox(14);
               this.mPrice.setPosition(box.getX() + this.mPrice.getWidth() / 2, box.getY() + box.getHeight() / 2);
               this.mPrice.setVisible(true);
               this.mImageCash.setVisible(true);
            }

            this.updateClockPosition();
            this.mImageTimer.setVisible(true);
            this.mImageTimerGlow.setVisible(true);
         }
      }
   }

   @Override
   public void callback(int id) {
      if (id != 0) {
         if (id == 1 || id == 32004) {
            if (this.mMission != null) {
               this.mMission.expirationEventFinished();
            }

            if (id == 1) {
               Item time = ItemManager.getItem(3300);
               Item extra = ItemManager.getItem(3301);
               if (PlayerProfile.canBuy(time, 2, true, false)) {
                  PlayerProfile.buyItem(time, 1, false);
                  PlayerProfile.buyItem(extra, 1, false);
                  int[] parameters = new int[]{1911, 3300, 2794};
                  EventManager.addEvent(new TimedEvent(50, 2470, parameters));
               }
            }

            WindowManager.closeWindow();
         }
      } else {
         if (this.mMission != null) {
            if (!this.mMissionExpired) {
               this.mMission.introEventFinished();
            } else {
               this.mMission.expirationEventFinished();
            }
         }

         WindowManager.closeWindow();
         if (!this.mMissionExpired) {
            HUD.openMission(this.mMission);
         }
      }
   }

   public void updateClockPosition() {
      CollisionBox box = this.mCollisions.getCollisionBox(1);
      int space = this.mTextTitle.getFont().charWidth(' ');
      this.mImageTimer.setPositionX(box.getX() + space / 2 + box.getWidth() / 2 + this.mTextTitle.getTextWidth() / 2 + this.mImageTimer.getWidth() / 2);
      this.mImageTimerGlow.setPositionX(this.mImageTimer.getPositionX());
   }
}
