package com.dchoc.idead.items;

import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class DecorationItem extends Item {
   private short mShowAfterMission;
   private boolean mRotatable;
   private boolean mNewItem;
   private boolean mSaleItem;

   protected DecorationItem(DChocByteArray data) throws EOFException {
      this.mType = 405;
      this.loadData(data);
   }

   public int getShowAfterMission() {
      return this.mShowAfterMission;
   }

   public boolean isRotatable() {
      return this.mRotatable;
   }

   public boolean isNewItem() {
      return this.mNewItem;
   }

   public boolean isSaleItem() {
      return this.mSaleItem;
   }

   public void setNewItem(boolean newItem) {
      this.mNewItem = newItem;
   }

   public void setSaleItem(boolean saleItem) {
      this.mSaleItem = saleItem;
   }

   private void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mLayer = data.readByte();
      this.mTitle = data.readShort();
      this.mDescription = data.readShort();
      this.mAction = data.readShort();
      data.readByte();
      this.mShowAfterMission = data.readShort();
      this.mPrice = data.readInt();
      this.mCurrency = data.readShort();
      this.mTileSizeX = data.readByte();
      this.mTileSizeY = data.readByte();
      this.mTileSizeZ = data.readByte();
      this.mWalkable = data.readBoolean();
      data.readBoolean();
      this.mRotatable = data.readBoolean();
      this.mIconAnimationID = data.readInt();
      this.mAnimationIDs = ToolkitHelpers.readResourceIDs(data, true);
      this.mCollisionIDs = ToolkitHelpers.readResourceIDs(data, true);
      this.mShadowAnimation = data.readInt();
   }
}
