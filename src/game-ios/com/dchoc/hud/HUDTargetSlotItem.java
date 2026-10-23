package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.CollectibleItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;

public class HUDTargetSlotItem extends HUDObject {
   public static final int BUTTON_BUY_ITEM = -1;
   public static final int COLLISION_BACKGROUND = 0;
   public static final int COLLISION_BUY = 3;
   public static final int COLLISION_ICON = 1;
   public static final int COLLISION_TARGET = 2;
   protected Item mItem;
   protected int mTarget;
   protected int mCount;
   protected HUDCollection mCollection;
   protected HUDButton mButtonBuy;
   protected HUDImage mImageIcon;
   protected HUDTarget mTargetCount;
   protected SlotBackground mSlotBackground;
   protected SpriteObject mLayout;
   protected HUDCollectibleHelper mCollectibleHelper;
   protected boolean mHideBuyButton;
   protected boolean mBuyRemainingItems;

   public HUDTargetSlotItem(int x, int y, int layout, boolean bigTarget) {
      super((byte)-1);
      this.setPosition(x, y);
      this.mLayout = AnimationsManager.loadShared(layout);
      this.mLayout.getCollisionBox(0);
      this.mCollection = new HUDCollection();
      this.mCollection.setParent(this);
      this.mSlotBackground = new SlotBackground(this.mLayout.getCollisionBox(0));
      this.mCollection.addObject(this.mSlotBackground);
      CollisionBox box = this.mLayout.getCollisionBox(1);
      int boxX = box.getX();
      int boxWidth = box.getWidth();
      int boxY = box.getY();
      int boxHeight = box.getHeight();
      this.mImageIcon = new HUDImage(-1, boxX + (boxWidth >> 1), boxY + (boxHeight >> 1));
      this.mImageIcon.setAnimationFrame(0);
      this.mCollection.addObject(this.mImageIcon);
      box = this.mLayout.getCollisionBox(2);
      HUDTarget target;
      if (bigTarget) {
         target = new HUDTarget(74607, 74795);
      } else {
         target = new HUDTarget(4);
      }

      this.mTargetCount = target;
      boxX = box.getX();
      boxWidth = box.getWidth();
      boxY = box.getY();
      boxHeight = box.getHeight();
      target.setPosition(boxX + (boxWidth >> 1), boxY + (boxHeight >> 1));
      this.mTargetCount.setCenterType((byte)3);
      this.mCollection.addObject(this.mTargetCount);
      box = this.mLayout.getCollisionBox(3);
      this.mButtonBuy = new HUDButton(-1, box.getX(), box.getY(), 66339);
      this.mCollection.addObject(this.mButtonBuy);
      this.mCollectibleHelper = new HUDCollectibleHelper();
      this.mCollectibleHelper.setVisible(false);
      this.mCollectibleHelper.setParent(this);
      box = this.mLayout.getCollisionBox(1);
      HUDCollectibleHelper helper = this.mCollectibleHelper;
      boxX = box.getX();
      boxWidth = box.getWidth();
      int helperWidth = this.mCollectibleHelper.getWidth();
      boxY = box.getY();
      helper.setPosition(boxX + boxWidth - helperWidth / 2, boxY);
      this.mCollection.addObject(this.mCollectibleHelper);
      this.mHideBuyButton = false;
      this.mBuyRemainingItems = false;
      this.mItem = null;
      this.mTarget = 0;
      this.mCount = 0;
   }

   public void setHideBuyButton(boolean hide) {
      this.mHideBuyButton = hide;
   }

   public void setBuyRemainingItems(boolean buyRemaining) {
      this.mBuyRemainingItems = buyRemaining;
   }

   public void setSlotBackgroundVisible(boolean visible) {
      this.mSlotBackground.setVisible(visible);
   }

   public void setValues(Item item, int target) {
      if (item != null) {
         this.mItem = item;
         this.mTarget = target;
         HUDImage icon = this.mImageIcon;
         icon.setImage(item.getIconAnimationID());
         this.mCount = PlayerProfile.getInventory().getItemCount(item);
         this.mTargetCount.setValues(this.mCount, this.mTarget);
         this.mButtonBuy.setVisible(!this.mHideBuyButton || this.mCount < this.mTarget);
         HUDButton button = this.mButtonBuy;
         int price = item.getPrice();
         int amount = 1;
         if (this.mBuyRemainingItems) {
            amount = this.mTarget - this.mCount;
         }

         button.setText(Integer.toString(amount * price));
         int currency = item.getCurrency();
         this.mButtonBuy.setIcon(currency == 9 ? 66253 : 66255);
         if (item.getType() == 47) {
            this.mCollectibleHelper.setItem((CollectibleItem)item);
         }
      }
   }

   public boolean isValueReached() {
      return this.mTarget <= this.mCount;
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
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      this.mCollection.pointerEvent(event);
   }

   @Override
   public void callback(int id) {
      if (id == -1) {
         int amount = 1;
         if (this.mBuyRemainingItems) {
            amount = this.mTarget - this.mCount;
         }

         if (PlayerProfile.buyItem(this.mItem, amount, false) != null) {
            this.setValues(this.mItem, this.mTarget);
            HUDObject parent = this.mParent;
            if (parent != null) {
               parent.callback(-1);
            }
         }
      }
   }

   public Item getItem() {
      return this.mItem;
   }
}
