package com.dchoc.idead.player;

import com.dchoc.idead.Time;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.WeaponItem;

public class InventoryItem {
   private Item mItem;
   private int mAmount;
   private long mUseTime;
   private int mDuration;

   public InventoryItem(Item item, int amount) {
      this.mItem = item;
      this.mAmount = amount;
      this.mUseTime = 0L;
      this.mDuration = 0;
   }

   public boolean isValid() {
      return this.mItem != null;
   }

   public boolean isWeapon() {
      return this.mItem != null && this.mItem.getType() == 1907;
   }

   public boolean isBooster() {
      return this.mItem != null && this.mItem.getType() == 3270;
   }

   public boolean isUsable() {
      return !this.isWeapon() ? this.mAmount > 0 : this.mAmount > 0 || ((WeaponItem)this.mItem).getAmmoUsage() == 0;
   }

   public boolean isUsingAmmo() {
      return this.isWeapon() && ((WeaponItem)this.mItem).getAmmoUsage() > 0;
   }

   public boolean isActive() {
      return this.isBooster() && this.getUseTimeLeft() > 0L;
   }

   public Item getItem() {
      return this.mItem;
   }

   public int getType() {
      return this.mItem.getType();
   }

   public int getID() {
      return this.mItem.getID();
   }

   public int getSubType() {
      return this.mItem.getSubType();
   }

   public void setAmount(int amount) {
      this.mAmount = amount;
   }

   public void increaseAmount(int amount) {
      this.mAmount += amount;
   }

   public void decreaseAmount(int amount) {
      this.mAmount = Math.max(this.mAmount - amount, 0);
   }

   public int getAmount() {
      return this.mAmount;
   }

   public void setDuration(int duration) {
      this.mDuration = duration;
   }

   public void increaseDuration(int duration) {
      this.mDuration += duration;
   }

   public void decreaseDuration(int duration) {
      this.mDuration = Math.max(this.mDuration - duration, 0);
   }

   public int getDuration() {
      return this.mDuration;
   }

   public void setUseTime(long timestamp) {
      this.mUseTime = timestamp;
   }

   public long getUseTime() {
      return this.mUseTime;
   }

   public long getUseTimeLeft() {
      return this.mUseTime == 0L ? 0L : this.mDuration - Time.getTimestampDelta(this.mUseTime);
   }
}
