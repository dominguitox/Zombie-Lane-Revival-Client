package com.dchoc.idead.player;

import com.dchoc.collection.HashTable;
import com.dchoc.collection.HashTableNode;
import com.dchoc.idead.fixes.Fixes;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.tracking.ErrorEvent;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Sonda;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;
import java.util.Vector;

public class Inventory {
   public static final int INITIAL_ITEMS_CAPACITY = 64;
   public static final int INITIAL_WEAPONS_CAPACITY = 16;
   HashTable mItems = new HashTable(64);
   private Vector mWeaponItems = new Vector(16);
   private Vector mBoosterItems = new Vector(3);

   public InventoryItem addItem(Item item, int amount) {
      if (item == null) {
         return null;
      }

      InventoryItem inventoryItem = this.getItem(item);
      if (inventoryItem != null) {
         inventoryItem.increaseAmount(amount);
      } else if (amount > 0) {
         inventoryItem = new InventoryItem(item, amount);
         this.mItems.add(item.getID(), inventoryItem);
         if (inventoryItem.isWeapon()) {
            this.mWeaponItems.addElement(inventoryItem);
         }

         if (inventoryItem.isBooster()) {
            this.mBoosterItems.addElement(inventoryItem);
         }
      }

      MissionManager.updateCounter(2491, item.getID(), amount);
      MissionManager.updateCounter(2491, item.getType(), amount);
      MissionManager.updateCounter(2491, item.getSubType(), amount);
      MissionManager.updateCounter(2491, 80, amount);
      if (item.getType() == 1907 && PlayerProfile.getCurrentWeapon() == null) {
         PlayerProfile.changeWeapon(inventoryItem);
      }

      return inventoryItem;
   }

   public void removeItem(Item item, int amount) {
      this.removeItem(this.getItem(item), amount);
   }

   public void removeItem(InventoryItem item, int amount) {
      if (item != null && amount != 0) {
         item.decreaseAmount(amount);
         if (item.getAmount() == 0) {
            if (item.isWeapon()) {
               this.mWeaponItems.removeElement(item);
            }

            if (item.isBooster()) {
               this.mBoosterItems.removeElement(item);
            }

            this.mItems.remove(item.getID());
         }
      }
   }

   public InventoryItem getItem(Item item) {
      return item == null ? null : this.getItem(item.getID());
   }

   public InventoryItem getItem(int id) {
      return id == -1 ? null : (InventoryItem)this.mItems.get(id);
   }

   public Vector getWeaponItems() {
      return this.mWeaponItems;
   }

   public Vector getBoosterItems() {
      return this.mBoosterItems;
   }

   public int getItemCount(Item item) {
      return item == null ? 0 : this.getItemCount(item.getID());
   }

   public int getItemCount(int id) {
      InventoryItem item = this.getItem(id);
      return item == null ? 0 : item.getAmount();
   }

   public int getItemWithSubTypeCount(int subType) {
      int count = 0;
      HashTableNode[] nodes = this.mItems.getNodes();
      int length = nodes == null ? 0 : nodes.length;

      for (int i = 0; i < length; i++) {
         for (HashTableNode node = nodes[i]; node != null; node = node.getNext()) {
            InventoryItem item = (InventoryItem)node.getValue();
            if (item != null && item.getSubType() == subType) {
               count += item.getAmount();
            }
         }
      }

      return count;
   }

   public HashTable getItems() {
      return this.mItems;
   }

   public void save(DChocByteArray data) {
      data.writeShort(this.mItems.size());
      if (this.mItems.size() > 0) {
         HashTableNode[] nodes = this.mItems.getNodes();
         int length = nodes == null ? 0 : nodes.length;

         for (int i = 0; i < length; i++) {
            for (HashTableNode node = nodes[i]; node != null; node = node.getNext()) {
               InventoryItem item = (InventoryItem)node.getValue();
               if (item != null) {
                  if (item.isValid()) {
                     data.writeShort(item.getID());
                     data.writeInt(item.getAmount());
                     if (item.isBooster()) {
                        data.writeInt(item.getDuration());
                        ToolkitHelpers.writeLong(item.getUseTime(), data);
                     }
                  } else {
                     data.writeShort(-1);
                     data.writeInt(0);
                  }
               }
            }
         }
      }
   }

   public boolean load(DChocByteArray data) {
      try {
         int count = data.readShort();

         for (int i = 0; i < count; i++) {
            int id = data.readShort();
            int amount = Math.abs(data.readInt());
            InventoryItem item = new InventoryItem(ItemManager.getItem(id), amount);
            if (item.isValid()) {
               this.mItems.add(id, item);
               if (item.isWeapon()) {
                  this.mWeaponItems.addElement(item);
               }

               if (item.isBooster()) {
                  item.setDuration(data.readInt());
                  item.setUseTime(Fixes.fixTimestamp(ToolkitHelpers.readLong(data)));
                  this.mBoosterItems.addElement(item);
               }

               Fixes.fixInventoryItem(item);
            }
         }
      } catch (EOFException exception) {
         new ErrorEvent().trackLoadError("Inventory.load", exception.getMessage(), null);
         return false;
      } catch (Exception exception) {
         Sonda.probe("Inventory.load (items)", exception);
      }

      return true;
   }

   public void debugAddAvatarItems() {
      this.addItem(ItemManager.getItem(2074), 1);
      this.addItem(ItemManager.getItem(3204), 1);
   }

   public void debugAddInvisibilityRing() {
      this.addItem(ItemManager.getItem(2056), 1);
   }

   public void debugAddAccessories() {
      this.addItem(ItemManager.getItem(2151), 1);
   }

   public void debugAddCostumes() {
      int[] ids = new int[]{2208, 2210, 2209, 2211, 2213, 2212};

      for (int id : ids) {
         this.addItem(ItemManager.getItem(id), 1);
      }
   }

   public void debugInventory() {
      int[] ids = new int[]{1948, 1936, 1965, 1105, 2979, 1977, 2978, 2981, 1924, 1928, 1930};

      for (int id : ids) {
         this.addItem(ItemManager.getItem(id), 10);
      }
   }

   private void debugWeapons() {
      int[] ids = new int[]{85, 2066, 2105, 2067, 2068, 1961, 2061, 1959, 1960, 149, 2070, 156, 1962, 163, 159, 1963, 152, 2114, 1964, 167, 2764, 2763, 5609};

      for (int id : ids) {
         this.addItem(ItemManager.getItem(id), 10);
      }
   }
}
