package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.CollectibleItem;
import com.dchoc.idead.items.Collection;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.player.Inventory;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.Animation;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public class HUDCollectionBar extends HUDObject {
   private static SpriteObject mOpeningAnimation;
   private static SpriteObject mClosingAnimation;
   private static SpriteObject mIconAppearingAnimation;
   private static SpriteObject mPanel;
   private static HUDAutoTextField mTitle;
   private static HUDImage[] mIcons;
   private static HUDAutoTextField[] mCounters;
   private static HUDImage mNewIcon;
   private static HUDAutoTextField mNewCounter;
   private int mState;
   private long mLastPickupTime;
   private CollectibleItem mLastCollectible;
   private Collection mCurrentCollection;

   protected HUDCollectionBar() {
      super((byte)25);
      mOpeningAnimation = AnimationsManager.loadShared(74861);
      mClosingAnimation = AnimationsManager.loadShared(74859);
      mIconAppearingAnimation = AnimationsManager.loadShared(74897);
      mPanel = AnimationsManager.loadShared(74857);
      mTitle = new HUDAutoTextField(mPanel.getCollisionBox(0));
      mTitle.setCentered(true, false);
      mIcons = new HUDImage[5];

      for (int i = 0; i < mIcons.length; i++) {
         mIcons[i] = new HUDImage();
         mIcons[i].setPosition(mPanel.getCollisionBox(i + 1));
         mIcons[i].setParent(this);
         mIcons[i].setAnimationFrame(0);
      }

      mCounters = new HUDAutoTextField[5];

      for (int i = 0; i < mCounters.length; i++) {
         mCounters[i] = new HUDAutoTextField(mPanel.getCollisionBox(i + 6));
         mCounters[i].setParent(this);
      }
   }

   public void collectiblePickedUp(CollectibleItem item) {
      this.mLastCollectible = item;
      Collection previous = this.mCurrentCollection;
      this.mCurrentCollection = ItemManager.getCollection(item);
      if (this.mCurrentCollection != null) {
         int index = this.mCurrentCollection.getItemIndex(item);
         this.mLastPickupTime = System.currentTimeMillis();
         mTitle.setText(this.mCurrentCollection.getTitleID(), 2);
         mTitle.setParent(this);

         for (int i = 0; i < this.mCurrentCollection.getItems().length; i++) {
            Inventory inventory = PlayerProfile.getInventory();
            int count = inventory.getItemCount(ItemManager.getItem(this.mCurrentCollection.getItems()[i]));
            mIcons[i].setImage(ItemManager.getItem(this.mCurrentCollection.getItems()[i]).getIconAnimationID());
            mIcons[i].setScaleRatio(0.5F);
            mIcons[i].setAlpha(count == 0 ? 0.2F : 1.0F);
            mCounters[i].setText("x" + count, 0);
            mCounters[i].setVisible(index != i && count > 0);
         }

         mNewIcon = mIcons[index];
         mNewCounter = mCounters[index];
         if (this.mState == 0 || this.mState == 1 || this.mState == 4) {
            this.mState = 1;
            mOpeningAnimation.setAnimation(0, 1, false);
         } else if (this.mState == 2 || this.mState == 3) {
            if (previous == this.mCurrentCollection) {
               this.mState = 1;
               mOpeningAnimation.setAnimation(0, 1, false);
            } else {
               this.mState = 2;
               mIconAppearingAnimation.setAnimation(0, 1, false);
            }
         }
      }
   }

   @Override
   public void draw() {
      if (this.mState >= 1 && this.mState <= 4) {
         int width = Toolkit.getScreenWidth();
         this.setPosition(this.mPosX + width, 0);
         SpriteObject panel = mPanel;
         int x = this.getX();
         int y = this.getY();
         panel.draw(x, y);
         mTitle.draw();

         for (int i = 0; i < 5; i++) {
            mIcons[i].draw();
            mCounters[i].draw();
         }
      }
   }

   @Override
   public void logicUpdate(int delta) {
      switch (this.mState) {
         case 1:
            mOpeningAnimation.logicUpdate(delta);
            Animation datax = mOpeningAnimation.getCurrentAnimationData();
            int elapsedx = mOpeningAnimation.getElapsedTime();
            this.mPosX = datax.getTimelineValue(1, elapsedx, true);
            if (mOpeningAnimation.isFinishedAnimation()) {
               this.mState = 2;
               mIconAppearingAnimation.setAnimation(0, 1, false);
            }
            break;
         case 2:
            mIconAppearingAnimation.logicUpdate(delta);
            Animation datax = mIconAppearingAnimation.getCurrentAnimationData();
            int elapsedx = mIconAppearingAnimation.getElapsedTime();
            int color = datax.getTimelineValue(0, elapsedx, true);
            int scale = datax.getTimelineValue(3, elapsedx, true);
            mNewIcon.setAlpha(color >> 24);
            mNewIcon.setScaleRatio(scale * 9.765625E-4F);
            if (mIconAppearingAnimation.isFinishedAnimation()) {
               this.mState = 3;
               mNewCounter.setVisible(true);
               mNewIcon.setAlpha(1.0F);
            }

            this.mPosX = 0;
            break;
         case 3:
            if (System.currentTimeMillis() - this.mLastPickupTime > 5000L) {
               this.mState = 4;
               mClosingAnimation.setAnimation(0, 1, false);
            }

            this.mPosX = 0;
            break;
         case 4:
            mClosingAnimation.logicUpdate(delta);
            Animation data = mClosingAnimation.getCurrentAnimationData();
            int elapsed = mClosingAnimation.getElapsedTime();
            this.mPosX = data.getTimelineValue(1, elapsed, true);
            if (mClosingAnimation.isFinishedAnimation()) {
               this.mState = 0;
            }
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
   }

   @Override
   public void callback(int id) {
   }
}
