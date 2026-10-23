package com.dchoc.idead.player;

import com.dchoc.idead.items.AvatarItem;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;
import java.util.Vector;

public class AvatarDescription implements CsvIDs {
   public static final int CHARACTER_DOG = 43;
   public static final int CHARACTER_MAIN = 15;
   public static final int CHARACTER_SPOUSE = 41;
   private int mCharacter;
   private Vector mCurrentItems;
   private Vector mFemaleItems;
   private int mGender;
   private Vector mMaleItems;
   private WeaponItem mWeapon;

   public AvatarDescription(int gender, int character) {
      this.mCharacter = character;
      this.mMaleItems = new Vector(13);
      this.mFemaleItems = new Vector(13);
      this.setGender(gender);
      this.mWeapon = null;
      if (character != 15) {
         this.equipWeapon(ItemManager.getWeapon(49));
      }
   }

   public AvatarDescription(DChocByteArray data) throws EOFException {
      this.mMaleItems = new Vector(13);
      this.mFemaleItems = new Vector(13);
      this.loadData(data);
   }

   public boolean isEquipped(AvatarItem item) {
      if (item == null) {
         return false;
      }

      for (int i = 0; i < this.mCurrentItems.size(); i++) {
         if (((AvatarItem)this.mCurrentItems.elementAt(i)).getID() == item.getID()) {
            return true;
         }
      }

      return false;
   }

   public boolean isEquippedWithID(int id) {
      if (id == -1) {
         return false;
      }

      for (int i = 0; i < this.mCurrentItems.size(); i++) {
         if (((AvatarItem)this.mCurrentItems.elementAt(i)).getID() == id) {
            return true;
         }
      }

      return false;
   }

   public boolean isEquippedWithGroup(int group) {
      if (group == -1) {
         return false;
      }

      for (int i = 0; i < this.mCurrentItems.size(); i++) {
         if (((AvatarItem)this.mCurrentItems.elementAt(i)).getGroup() == group) {
            return true;
         }
      }

      return false;
   }

   public void equipItem(AvatarItem item) {
      this.equipItem(item, this.mCurrentItems);
   }

   public void equipItemForGender(AvatarItem item, int gender) {
      this.equipItem(item, gender == 1922 ? this.mFemaleItems : this.mMaleItems);
   }

   public void unequipItem(int subType) {
      this.unequipItem(subType, this.mCurrentItems);
   }

   public AvatarItem getItem(int subType) {
      for (int i = 0; i < this.mCurrentItems.size(); i++) {
         AvatarItem item = (AvatarItem)this.mCurrentItems.elementAt(i);
         if (item.getSubType() == subType) {
            return item;
         }
      }

      return null;
   }

   public void equipWeapon(WeaponItem weapon) {
      if (weapon != null && (this.mGender != 43 || weapon.getID() == 49)) {
         this.mWeapon = weapon;
      }
   }

   public WeaponItem getWeapon() {
      return this.mWeapon;
   }

   public int getCharacter() {
      return this.mCharacter;
   }

   public int getGender() {
      return this.mGender;
   }

   public void setGender(int gender) {
      this.mGender = gender;
      this.mCurrentItems = gender == 1922 ? this.mFemaleItems : this.mMaleItems;
   }

   public Vector getItems() {
      return this.mCurrentItems;
   }

   public void saveData(DChocByteArray data) {
      data.writeShort(this.mGender);
      data.writeShort(this.mCharacter);
      data.writeShort(this.mWeapon == null ? -1 : this.mWeapon.getID());
      data.writeShort(this.mMaleItems.size());

      for (int i = 0; i < this.mMaleItems.size(); i++) {
         data.writeShort(((AvatarItem)this.mMaleItems.elementAt(i)).getID());
      }

      data.writeShort(this.mFemaleItems.size());

      for (int i = 0; i < this.mFemaleItems.size(); i++) {
         data.writeShort(((AvatarItem)this.mFemaleItems.elementAt(i)).getID());
      }
   }

   public void loadData(DChocByteArray data) throws EOFException {
      this.setGender(data.readShort());
      this.mCharacter = data.readShort();
      WeaponItem weapon = ItemManager.getWeapon(data.readShort());
      if (weapon == null) {
         weapon = ItemManager.getWeapon(49);
      }

      this.equipWeapon(weapon);
      int maleCount = data.readShort();
      int next = data.readShort();

      for (int i = 0; i < maleCount; i++) {
         this.equipItem(ItemManager.getAvatar(next), this.mMaleItems);
         next = data.readShort();
      }

      for (int i = 0; i < next; i++) {
         this.equipItem(ItemManager.getAvatar(data.readShort()), this.mFemaleItems);
      }

      this.setGender(this.mGender);
   }

   private void equipItem(AvatarItem item, Vector items) {
      if (item != null) {
         this.unequipItem(item.getSubType(), items);
         items.addElement(item);
      }
   }

   private void unequipItem(int subType, Vector items) {
      for (int i = 0; i < items.size(); i++) {
         if (((AvatarItem)items.elementAt(i)).getSubType() == subType) {
            ToolkitHelpers.fastRemove(i, items);
            return;
         }
      }
   }
}
