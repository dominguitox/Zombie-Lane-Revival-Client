package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.GiftItem;
import com.dchoc.idead.neighbors.NeighborGift;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;

public class HUDGift extends HUDObject {
   protected SpriteObject mLayout;
   private NeighborGift mGift;
   private HUDCollection mCollection;
   private SlotBackground mSlotBackground;
   private HUDImage mImageIcon;
   private HUDButton mButtonSend;
   private HUDAutoTextField mTextLocked;
   private HUDTime mTime;

   public HUDGift(int x, int y) {
      super((byte)-1);
      this.setPosition(x, y);
      this.mLayout = AnimationsManager.loadShared(74777);
      this.mLayout.getCollisionBox(0);
      HUDCollection collection = new HUDCollection();
      this.mCollection = collection;
      collection.setParent(this);
      SlotBackground background = new SlotBackground(this.mLayout.getCollisionBox(0));
      this.mSlotBackground = background;
      this.mCollection.addObject(background);
      CollisionBox box = this.mLayout.getCollisionBox(2);
      HUDImage icon = new HUDImage(-1, box.getX(), box.getY());
      this.mImageIcon = icon;
      icon.setAnimationFrame(0);
      this.mCollection.addObject(this.mImageIcon);
      box = this.mLayout.getCollisionBox(1);
      HUDButton send = new HUDButton(0, box.getX(), box.getY(), 66339);
      this.mButtonSend = send;
      send.setText(1571);
      this.mCollection.addObject(this.mButtonSend);
      CollisionBox background0 = this.mLayout.getCollisionBox(0);
      int height1 = background0.getHeight();
      int y0 = background0.getY();
      int height2 = background0.getHeight();
      int y1 = this.mLayout.getCollisionBox(1).getY();
      int textX = background0.getX();
      int textY = background0.getY();
      int textWidth = background0.getWidth();
      int textHeight = y1 + (height1 - y0 - height2);
      HUDAutoTextField locked = new HUDAutoTextField(textX, textY, textWidth, textHeight);
      this.mTextLocked = locked;
      locked.setSplitUsingWidth(true);
      this.mTextLocked.setScaleToFit(true);
      this.mTextLocked.setCentered(true, true);
      this.mTextLocked.setText(1699, 2);
      this.mCollection.addObject(this.mTextLocked);
      int timeX = background0.getX();
      int timeWidth = background0.getWidth();
      int timeY = background0.getY();
      HUDTime time = new HUDTime(timeX + timeWidth / 2, timeY + textHeight / 2, 2);
      this.mTime = time;
      time.setCenterType((byte)3);
      this.mCollection.addObject(this.mTime);
   }

   public void setGift(NeighborGift gift) {
      if (gift != null) {
         this.mGift = gift;
         GiftItem giftItem = gift.getGiftItem();
         this.mTextLocked.setVisible(giftItem.isLockedByLevel());
         this.mImageIcon.setImage(giftItem.getIconAnimationID());
      }
   }

   public NeighborGift getGift() {
      return this.mGift;
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         this.mCollection.draw();
      }
   }

   @Override
   public void logicUpdate(int delta) {
      this.mCollection.logicUpdate(delta);
      if (this.mGift != null) {
         boolean enabled = this.isSendButtonEnabled();
         this.mButtonSend.setEnabled(enabled);
         this.mImageIcon.setSemitransparent(!enabled);
         if (!this.mGift.canSend() && !this.mTextLocked.isVisible()) {
            long left = this.mGift.getCountdownLeft();
            if (left <= 0L) {
               this.mTime.setVisible(false);
            } else {
               this.mTime.setTime(left);
               this.mTime.setVisible(true);
            }
         } else {
            this.mTime.setVisible(false);
         }
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      this.mCollection.pointerEvent(event);
   }

   @Override
   public void callback(int id) {
      if (id == 0) {
         this.mGift.send();
      }
   }

   public boolean isSendButtonEnabled() {
      if (this.mGift == null) {
         return false;
      } else {
         NeighborProfile neighbor = this.mGift.getNeighbor();
         if (neighbor == null || neighbor.getType() != 1926) {
            return false;
         } else {
            return this.mTextLocked.isVisible() ? false : this.mGift.canSend();
         }
      }
   }
}
