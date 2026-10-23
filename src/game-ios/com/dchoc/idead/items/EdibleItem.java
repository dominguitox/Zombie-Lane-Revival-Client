package com.dchoc.idead.items;

import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import java.io.EOFException;

public class EdibleItem extends Item implements CsvIDs {
   public static final int SUBTYPE_COLA = 1931;
   public static final int SUBTYPE_ENERGY_PACK = 1925;
   public static final int SUBTYPE_INVALID = -1;
   protected byte mEnergyGain;
   protected byte mMaxEnergyGain;
   private boolean mNewItem;

   protected EdibleItem(DChocByteArray data) throws EOFException {
      this.mType = 101;
      this.loadData(data);
   }

   public int getEnergyGain() {
      return this.mEnergyGain;
   }

   public int getMaxEnergyGain() {
      return this.mMaxEnergyGain;
   }

   public boolean isNewItem() {
      return this.mNewItem;
   }

   public void setNewItem(boolean newItem) {
      this.mNewItem = newItem;
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mTitle = data.readShort();
      this.mDescription = data.readShort();
      this.mSubType = data.readShort();
      this.mEnergyGain = data.readByte();
      this.mMaxEnergyGain = data.readByte();
      data.readByte();
      this.mPrice = data.readInt();
      this.mCurrency = data.readShort();
      data.readInt();
      data.readShort();
      data.readBoolean();
      this.mIconAnimationID = data.readInt();
   }
}
