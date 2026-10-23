package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.Collection;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.player.Inventory;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.SpriteObject;

public class HUDItemCollection extends HUDItem {
   private HUDImage zombieFace;
   private HUDButton buttonTrade;
   private HUDAutoTextField mTitle;
   private HUDAutoTextField mDescription;
   private Collection mCollection;
   private SlotBackground mSlotBackground;
   private Rectangle mLine;
   private HUDImage[] collectibleIcons;
   private HUDButton[] buttonBuy;
   private HUDInteger[] collectibleAmounts;
   private int[] amounts;
   private int[][] mRewards;

   public HUDItemCollection(Collection collection) {
      super(0);
      SpriteObject layout = AnimationsManager.loadShared(74643);
      this.mSlotBackground = new SlotBackground(layout.getCollisionBox(10), this);
      this.mLine = new Rectangle(layout.getCollisionBox(34), this, 14006942);
      HUDImage face = new HUDImage(collection.getIconAnimationID());
      this.zombieFace = face;
      face.setPosition(layout.getCollisionBox(30));
      this.mCollection = collection;
      this.mRewards = collection.getRewardAmount();
      this.mWidth = layout.getCollisionBox(0).getWidth();
      this.mHeight = layout.getCollisionBox(0).getHeight();
      HUDButton trade = new HUDButton(4003, layout.getCollisionBox(33), 4);
      this.buttonTrade = trade;
      trade.setText(1687);
      this.buttonTrade.setInheritedClipping(true, true);
      HUDAutoTextField title = new HUDAutoTextField(layout.getCollisionBox(31));
      this.mTitle = title;
      title.setText(collection.getTitleID(), 2);
      HUDAutoTextField description = new HUDAutoTextField(layout.getCollisionBox(32));
      this.mDescription = description;
      int rewardType = this.mRewards[0][0];
      String reward = Integer.toString(this.mRewards[0][1]);
      description.setText(rewardType == 9 ? 164 : 165, 3, reward, -1);
      this.collectibleIcons = new HUDImage[collection.getItems().length];
      this.collectibleAmounts = new HUDInteger[collection.getItems().length];
      this.buttonBuy = new HUDButton[collection.getItems().length];
      this.amounts = new int[collection.getItems().length];

      for (int i = 0; i < (this.collectibleIcons == null ? 0 : this.collectibleIcons.length); i++) {
         Inventory inventory = PlayerProfile.getInventory();
         this.amounts[i] = inventory.getItemCount(ItemManager.getItem(collection.getItems()[i]));
         this.collectibleIcons[i] = new HUDImage(ItemManager.getItem(collection.getItems()[i]).getIconAnimationID());
         this.collectibleIcons[i].setPosition(layout.getCollisionBox(i + 50));
         this.collectibleIcons[i].setAnimationFrame(0);
         this.collectibleAmounts[i] = new HUDInteger(74607);
         this.collectibleAmounts[i].setValue(this.amounts[i]);
         this.collectibleAmounts[i].setPosition(layout.getCollisionBox(i + 60));
         this.buttonBuy[i] = new HUDButton(i + 4009, 0, 0, 66339);
         this.buttonBuy[i].setPosition(layout.getCollisionBox(i + 70));
         this.buttonBuy[i].setIcon(66255);
         HUDButton buy = this.buttonBuy[i];
         buy.setText(Integer.toString(ItemManager.getItem(collection.getItems()[i]).getPrice()));
         this.buttonBuy[i].setInheritedClipping(true, true);
      }

      this.mObjects = new HUDObject[]{
         this.mSlotBackground,
         this.mLine,
         this.zombieFace,
         this.collectibleIcons[0],
         this.collectibleIcons[1],
         this.collectibleIcons[2],
         this.collectibleIcons[3],
         this.collectibleIcons[4],
         this.mTitle,
         this.mDescription,
         this.collectibleAmounts[0],
         this.collectibleAmounts[1],
         this.collectibleAmounts[2],
         this.collectibleAmounts[3],
         this.collectibleAmounts[4],
         this.buttonBuy[0],
         this.buttonBuy[1],
         this.buttonBuy[2],
         this.buttonBuy[3],
         this.buttonBuy[4],
         this.buttonTrade
      };

      for (int i = 0; i < (this.mObjects == null ? 0 : this.mObjects.length); i++) {
         HUDObject object = this.mObjects[i];
         if (object != null) {
            object.setParent(this);
         }
      }

      this.refresh();
   }

   public Collection getCollection() {
      return this.mCollection;
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      for (int i = 0; i < (this.buttonBuy == null ? 0 : this.buttonBuy.length); i++) {
         this.buttonBuy[i].pointerEvent(event);
      }

      this.buttonTrade.pointerEvent(event);
   }

   @Override
   public void callback(int id) {
      if (id - 4009 >= 0 && id - 4009 < 5) {
         PlayerProfile.buyItem(ItemManager.getItem(this.mCollection.getItems()[id - 4009]), 1, false);
      } else if (id == 4003) {
         if (this.mCollection.getItems().length != 0) {
            int i = 0;

            do {
               Inventory inventory = PlayerProfile.getInventory();
               inventory.removeItem(ItemManager.getItem(this.mCollection.getItems()[i]), 1);
            } while (++i < this.mCollection.getItems().length);
         }

         MissionManager.updateCounter(2546, this.mCollection.getID(), 1);
         MissionManager.updateCounter(2546, 80, 1);
         int amount = this.mRewards[0][1];
         if (this.mRewards[0][0] == 9) {
            PlayerProfile.addCoins(amount);
         } else {
            PlayerProfile.addXp(amount);
         }
      }

      this.refresh();
   }

   @Override
   public void refresh() {
      for (int i = 0; i < (this.collectibleIcons == null ? 0 : this.collectibleIcons.length); i++) {
         Inventory inventory = PlayerProfile.getInventory();
         this.amounts[i] = inventory.getItemCount(ItemManager.getItem(this.mCollection.getItems()[i]));
         this.collectibleAmounts[i].setValue(this.amounts[i]);
      }

      for (int i = 0; i < (this.collectibleAmounts == null ? 0 : this.collectibleAmounts.length); i++) {
         this.collectibleIcons[i].setSemitransparent(this.amounts[i] == 0);
      }

      this.buttonTrade.enable();

      for (int i = 0; i < (this.collectibleAmounts == null ? 0 : this.collectibleAmounts.length); i++) {
         if (this.amounts[i] < 1) {
            this.buttonTrade.disable();
            return;
         }
      }
   }
}
