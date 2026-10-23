package com.dchoc.hud;

import com.dchoc.idead.items.GiftItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.neighbors.NeighborGift;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.player.InventoryItem;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;

public class HUDItemAcceptGift extends HUDItemMailbox {
   private NeighborGift mGift = null;
   private boolean mHireReward;

   public HUDItemAcceptGift() {
      super((byte)18);
   }

   public void setGift(NeighborGift gift) {
      if (gift != null && gift.getGiftItem() != null) {
         this.mGift = gift;
         GiftItem giftItem = gift.getGiftItem();
         NeighborProfile neighbor = gift.getNeighbor();
         this.setValues(neighbor.getName(), 1820, neighbor.getType() == 2514, neighbor.getSocialIconType());
         this.mImageIcon.setImage(giftItem.getIconAnimationID());
         Item item = ItemManager.getItem(giftItem.getCreatesType(), giftItem.getCreatesID());
         if (item != null) {
            if (item.getType() == 101) {
               this.setButtons(false, true, true);
            } else {
               this.setButtons(true, false, false);
            }

            int amount = gift.getAmount();
            HUDAutoTextField description = this.mTextDescription;
            if (amount >= 2) {
               description.setText(1787, 3, Integer.toString(gift.getAmount()), -1, null, item.getTitle());
            } else {
               description.setText(1786, 3, null, item.getTitle());
            }
         } else {
            this.setButtons(true, false, false);
            if (giftItem.getID() == 2063) {
               HUDAutoTextField description = this.mTextDescription;
               int creates = giftItem.getCreatesAmount();
               description.setText(1970, 3, Integer.toString(creates * gift.getAmount()), -1);
            } else {
               int amount = gift.getAmount();
               HUDAutoTextField description = this.mTextDescription;
               if (amount >= 2) {
                  description.setText(1787, 3, Integer.toString(gift.getAmount()), -1, null, giftItem.getTitle());
               } else {
                  description.setText(1786, 3, null, giftItem.getTitle());
               }
            }
         }
      }
   }

   public NeighborGift getGift() {
      return this.mGift;
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      if (this.mGift != null) {
         this.mButtonAccept.setEnabled(!this.mGift.isAccepting());
         this.mButtonKeep.setEnabled(!this.mGift.isAccepting());
         this.mButtonHire.setEnabled(!this.mGift.isAccepting());
         if (!this.isAccepted()) {
            if (this.mGift.isAccepted()) {
               this.addGift();
               this.setAccepted(true);
            }
         }
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      for (int i = (this.mObjects == null ? 0 : this.mObjects.length) - 1; i >= 0; i--) {
         if (this.mObjects[i] != null) {
            this.mObjects[i].pointerEvent(event);
         }
      }
   }

   @Override
   public void callback(int id) {
      if (Integer.compareUnsigned(id, 2) <= 0) {
         this.mHireReward = id == 1;
         if (this.mGift.isReward()) {
            this.mGift.setStatus(1);
         } else {
            this.mGift.accept(true);
         }
      }
   }

   public void addGift() {
      InventoryItem inventoryItem = PlayerProfile.addRewardItem(this.mGift.getGiftItem(), this.mGift.getAmount());
      if (inventoryItem != null) {
         Item item = inventoryItem.getItem();
         if (this.mHireReward && item.getType() == 101 || item.getType() == 3270) {
            PlayerProfile.useItem(item, false);
         }
      }
   }
}
