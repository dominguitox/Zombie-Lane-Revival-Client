package com.dchoc.windows;

import com.dchoc.collection.HashTableNode;
import com.dchoc.hud.HUD;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDCounter;
import com.dchoc.hud.HUDItem;
import com.dchoc.hud.HUDItemAchievement;
import com.dchoc.hud.HUDItemCollection;
import com.dchoc.hud.HUDItemSurvivalTip;
import com.dchoc.hud.HUDRedCounter;
import com.dchoc.hud.HUDSetOfSlots;
import com.dchoc.hud.HUDSlot;
import com.dchoc.hud.HUDTab;
import com.dchoc.hud.HUDTouchArea;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.CollectibleItem;
import com.dchoc.idead.items.Collection;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.Achievement;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.player.InventoryItem;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.playhaven.PlayHaven;
import com.dchoc.spriteobject.CollisionBox;
import java.util.Vector;

public class WindowStuff extends Window {
   private static String[] TAB_NAMES = new String[]{"Inventory", "collections", "Survival", "Achievements"};
   private int[] mInventoryOrder = new int[]{1909, 101, 1907, 405, 1104};
   private String mActiveAreaName = "main_view";
   private HUDAutoTextField mTitle;
   private HUDAutoTextField mAchievementsLocked;
   private HUDAutoTextField mSurvivalTipsLocked;
   private HUDTouchArea mInventoryArea;
   private HUDTouchArea mCollectionsArea;
   private HUDTouchArea mSurvivalTipsArea;
   private HUDTouchArea mAchievementsArea;
   private HUDCounter mCurrencyCash;
   private HUDCounter mCurrencyCoins;
   private RoundRectangle mYellowBackground;
   private HUDRedCounter mTipCounter;
   private HUDTab[] mPages;

   public WindowStuff() {
      this.mCollisions = AnimationsManager.loadShared(74641);
      this.mBackground = AnimationsManager.loadShared(74619);
      this.initCollisions();
      this.constructCloseButton(34000);
      HUDAutoTextField text = new HUDAutoTextField(this.getCollisionBox(30));
      this.mTitle = text;
      text.setCentered(true, true);
      this.mTitle.setText(18, 5);
      text = new HUDAutoTextField(this.mCollisions.getCollisionBox(60));
      this.mAchievementsLocked = text;
      text.setCentered(true, true);
      this.mAchievementsLocked.setSplitUsingWidth(true);
      this.mAchievementsLocked.setText(1685, 5);
      text = new HUDAutoTextField(this.mCollisions.getCollisionBox(60));
      this.mSurvivalTipsLocked = text;
      text.setCentered(true, true);
      this.mSurvivalTipsLocked.setSplitUsingWidth(true);
      this.mSurvivalTipsLocked.setText(2748, 5);
      this.mCurrencyCash = new HUDCounter(1, 66249, this.mCollisions.getCollisionBox(21));
      this.mCurrencyCoins = new HUDCounter(0, 66223, this.mCollisions.getCollisionBox(22));
      RoundRectangle background = new RoundRectangle(0);
      this.mYellowBackground = background;
      background.setSizes(this.mCollisions.getCollisionBox(6));
      HUDTab[] pages = new HUDTab[]{
         new HUDTab(1000, this.mCollisions.getCollisionBox(12), this.mCollisions.getCollisionBox(8), 1680),
         new HUDTab(1001, this.mCollisions.getCollisionBox(13), this.mCollisions.getCollisionBox(9), 1681),
         new HUDTab(1002, this.mCollisions.getCollisionBox(14), this.mCollisions.getCollisionBox(10), 1682),
         new HUDTab(1003, this.mCollisions.getCollisionBox(15), this.mCollisions.getCollisionBox(11), 1686)
      };
      this.mPages = pages;
      this.mTipCounter = new HUDRedCounter();

      for (int i = 0; i < (this.mPages == null ? 0 : this.mPages.length); i++) {
         this.mPages[i].setNeighbors(this.mPages);
      }

      CollisionBox area = this.mCollisions.getCollisionBox(7);
      Vector items = new Vector(ItemManager.smCollections.size());

      for (int i = 0; i < ItemManager.smCollections.size(); i++) {
         items.addElement(new HUDItemCollection((Collection)ItemManager.smCollections.elementAt(i)));
      }

      HUDTouchArea touchArea = new HUDTouchArea(true, false, true, area, items, true);
      this.mCollectionsArea = touchArea;
      touchArea.setScrollbar(true);
      items = new Vector(MissionManager.getAchivements().size());

      for (int i = 0; i < MissionManager.getAchivements().size(); i++) {
         items.addElement(new HUDItemAchievement((Achievement)MissionManager.getAchivements().elementAt(i)));
      }

      touchArea = new HUDTouchArea(true, false, true, area, items, true);
      this.mAchievementsArea = touchArea;
      touchArea.setScrollbar(true);
      items = new Vector(24);
      touchArea = new HUDTouchArea(false, true, true, area, items, true);
      this.mSurvivalTipsArea = touchArea;
      touchArea.setScrollbar(true);
      this.mSurvivalTipsArea.setSorted(true);
      this.mObjects.addObject(this.mTitle);
      this.mObjects.addObject(this.mButtonClose);
      this.mObjects.addObject(this.mCurrencyCash);
      this.mObjects.addObject(this.mCurrencyCoins);
      this.mObjects.addObject(this.mYellowBackground);
      this.mObjects.addObject(this.mPages[0]);
      this.mObjects.addObject(this.mPages[1]);
      this.mObjects.addObject(this.mPages[2]);
      this.mObjects.addObject(this.mPages[3]);
      this.mObjects.addObject(this.mTipCounter);
      this.mObjects.addObject(this.mCollectionsArea);
      this.mObjects.addObject(this.mAchievementsArea);
      this.mObjects.addObject(this.mSurvivalTipsArea);
      this.mObjects.addObject(this.mAchievementsLocked);
      this.mObjects.addObject(this.mSurvivalTipsLocked);
      this.createInventoryArea();
      this.createSurvivalTipsArea();
      this.mInventoryArea.setVisible(false);
      this.mCollectionsArea.setVisible(true);
      this.mAchievementsArea.setVisible(false);
      this.mSurvivalTipsArea.setVisible(false);
      this.mCollectionsArea.setFadingBorder(true);
      this.mSurvivalTipsArea.setFadingBorder(true);
      this.mAchievementsArea.setFadingBorder(true);
   }

   public void openTab(int tab) {
      if (tab >= 1000 && tab <= 1003) {
         this.mPages[tab - 1000].press();
         this.callback(tab);
      }
   }

   public void scrollToCollection(Collection collection) {
      for (int i = 0; i < this.mCollectionsArea.getItems().size(); i++) {
         HUDItemCollection item = (HUDItemCollection)this.mCollectionsArea.getItems().elementAt(i);
         int id = item.getCollection().getID();
         if (id == collection.getID()) {
            this.mCollectionsArea.gotoPage(i);
            return;
         }
      }
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      this.updateCounterPosition();
   }

   @Override
   public void open() {
      super.open();
      this.createInventoryArea();
      if (this.mCollectionsArea.getItems().size() > 0) {
         int i = 0;

         do {
            ((HUDItem)this.mCollectionsArea.getItems().elementAt(i)).refresh();
         } while (++i < this.mCollectionsArea.getItems().size());
      }

      if (this.mAchievementsArea.getItems().size() > 0) {
         int i = 0;

         do {
            ((HUDItem)this.mAchievementsArea.getItems().elementAt(i)).refresh();
         } while (++i < this.mAchievementsArea.getItems().size());
      }

      this.openTab(1000);
   }

   @Override
   public void callback(int id) {
      this.mAchievementsArea.setVisible(false);
      this.mCollectionsArea.setVisible(false);
      this.mInventoryArea.setVisible(false);
      this.mSurvivalTipsArea.setVisible(false);
      this.mAchievementsLocked.setVisible(false);
      this.mSurvivalTipsLocked.setVisible(false);
      this.mAchievementsArea.setHideItems(false);
      this.mSurvivalTipsArea.setHideItems(false);
      switch (id) {
         case 1000:
            this.mActiveAreaName = TAB_NAMES[0];
            this.mInventoryArea.setVisible(true);
            break;
         case 1001:
            this.mActiveAreaName = TAB_NAMES[1];
            this.mCollectionsArea.setVisible(true);
            break;
         case 1002:
            this.mActiveAreaName = TAB_NAMES[3];
            this.mAchievementsArea.setVisible(true);
            if (this.isAchievementsLocked()) {
               this.mAchievementsArea.setHideItems(true);
               this.mAchievementsLocked.setVisible(true);
            }
            break;
         case 1003:
            this.mActiveAreaName = TAB_NAMES[2];
            this.mSurvivalTipsArea.setVisible(true);
            this.createSurvivalTipsArea();
            if (this.isSurvivalTipsLocked()) {
               this.mSurvivalTipsArea.setHideItems(true);
               this.mSurvivalTipsLocked.setVisible(true);
            }

            HUD.resetTipsCounter();
      }

      this.trackTab();
      this.updateCounterPosition();
   }

   public HUDItemSurvivalTip getSurvivalTip(Mission mission) {
      Vector items = this.mSurvivalTipsArea.getItems();

      for (int i = 0; i < items.size(); i++) {
         HUDItemSurvivalTip tip = (HUDItemSurvivalTip)items.elementAt(i);
         if (tip.getMission() == mission) {
            return tip;
         }
      }

      return null;
   }

   public void updateInventoryArea() {
      Vector slots = new Vector(10);
      Vector sets = this.mInventoryArea.getItems();
      boolean changed = false;
      if (sets.size() > 0) {
         int i = 0;

         do {
            HUDSetOfSlots set = (HUDSetOfSlots)sets.elementAt(i);
            if (set != null) {
               Vector items = set.getItems();
               int count = items.size();

               while (count > 0) {
                  HUDSlot slot = (HUDSlot)items.elementAt(0);
                  boolean removed = true;
                  if (slot != null && slot.getAmount() > 0) {
                     slots.addElement(slot);
                     removed = changed;
                  }

                  items.removeElementAt(0);
                  count = items.size();
                  changed = removed;
               }
            }
         } while (++i < sets.size());
      }

      if (sets.size() > 0) {
         int i = 0;
         int next = 0;

         do {
            HUDSetOfSlots set = (HUDSetOfSlots)sets.elementAt(i);
            if (set != null && set.getMaxNumOfSlots() > 0) {
               int j = 0;

               do {
                  if (next < slots.size()) {
                     HUDSlot slot = (HUDSlot)slots.elementAt(next);
                     slot.setParent(set);
                     set.addSlot(slot);
                     next++;
                  }
               } while (++j < set.getMaxNumOfSlots());
            }

            set.refresh();
         } while (++i < sets.size());
      }

      if (sets.size() > 0) {
         int i = 0;

         do {
            HUDSetOfSlots set = (HUDSetOfSlots)sets.elementAt(i);
            if (set.getItems().size() == 0) {
               sets.removeElementAt(i);
               set.freeImages();
               i--;
            }
         } while (++i < sets.size());
      }

      if (changed) {
         this.mInventoryArea.calculateContainer();
      }
   }

   @Override
   public String getActiveAreaName() {
      return this.mActiveAreaName;
   }

   public void increaseTipCounter() {
      this.mTipCounter.incrementValue();
   }

   public void resetTipCounter() {
      this.mTipCounter.setValue(0);
   }

   @Override
   public void setState(byte state) {
      super.setState(state);
      if (state == 1) {
         PlayHaven.triggerCloseInventory();
      } else if (state == 0) {
         PlayHaven.triggerOpenInventory();
      }
   }

   public boolean isAchievementsLocked() {
      return PlayerProfile.getLevel() < Tuner.getLevelToUnlockAchievements();
   }

   public boolean isSurvivalTipsLocked() {
      return this.mSurvivalTipsArea.isEmpty();
   }

   public void updateCounterPosition() {
      CollisionBox box = this.mCollisions.getCollisionBox(this.mPages[3].isPressed() ? 15 : 11);
      HUDRedCounter counter = this.mTipCounter;
      int x = box.getX();
      int width = box.getWidth();
      int y = box.getY();
      int height = this.mTipCounter.getHeight();
      counter.setPosition(width + x, y + height / 2);
   }

   public void createSurvivalTipsArea() {
      Vector items = this.mSurvivalTipsArea.getItems();
      if (items.size() > 0) {
         int i = 0;

         do {
            ((HUDItem)items.elementAt(i)).refresh();
         } while (++i < items.size());
      }

      Vector tips = MissionManager.getSurvivalTips();
      if (tips.size() > 0) {
         boolean added = false;
         int i = 0;

         do {
            Mission mission = (Mission)tips.elementAt(i);
            if (mission.isCompleted()) {
               if (this.getSurvivalTip(mission) == null) {
                  HUDItemSurvivalTip tip = new HUDItemSurvivalTip(i + 1, mission, tips.size());
                  this.mSurvivalTipsArea.addItem(tip);
                  added = true;
               }

               if (mission.isNew()) {
                  this.mSurvivalTipsArea.resetPosition();
               }
            }
         } while (++i < tips.size());

         if (added) {
            return;
         }
      }

      this.mSurvivalTipsArea.sortItems();
   }

   public void createInventoryArea() {
      this.mObjects.removeObject(this.mInventoryArea);
      Vector items = new Vector(16);
      Vector sets = new Vector(16);
      HashTableNode[] nodes = PlayerProfile.getInventory().getItems().getNodes();
      int width = this.mCollisions.getCollisionBox(7).getWidth();

      for (int i = 0; i < (nodes == null ? 0 : nodes.length); i++) {
         for (HashTableNode node = nodes[i]; node != null; node = node.getNext()) {
            InventoryItem item = (InventoryItem)node.getValue();
            int type = item.getItem().getType();
            if (type != 1911 && (type != 47 || ItemManager.getCollection((CollectibleItem)item.getItem()) == null)) {
               items.addElement(item);
            }
         }
      }

      int j = 0;

      while (true) {
         int count = this.mInventoryOrder == null ? 0 : this.mInventoryOrder.length;
         int size = items.size();
         if (count <= j) {
            if (size > 0) {
               j = 0;

               do {
                  HUDSetOfSlots.addSlotToSet(sets, items.elementAt(j), width, 4, false);
               } while (++j < items.size());
            }

            HUDTouchArea area = new HUDTouchArea(false, true, true, this.mCollisions.getCollisionBox(7), sets, false);
            this.mInventoryArea = area;
            area.setParent(this);
            this.mInventoryArea.setFadingBorder(true);
            if (this.mInventoryArea != null) {
               Vector inventory = this.mInventoryArea.getItems();
               if (inventory.size() > 0) {
                  int i = 0;

                  do {
                     ((HUDItem)inventory.elementAt(i)).refresh();
                  } while (++i < inventory.size());
               }
            }

            this.mObjects.addObject(this.mInventoryArea);
            return;
         }

         if (size > 0) {
            int k = 0;

            do {
               InventoryItem item = (InventoryItem)items.elementAt(k);
               if (item.getItem().getType() == this.mInventoryOrder[j]) {
                  HUDSetOfSlots.addSlotToSet(sets, item, width, 4, false);
                  items.removeElementAt(k);
                  k--;
               }
            } while (++k < items.size());
         }

         j++;
      }
   }
}
