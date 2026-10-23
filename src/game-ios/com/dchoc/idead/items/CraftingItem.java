package com.dchoc.idead.items;

import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class CraftingItem extends Item {
   private short mCreatesType;
   private short mCreatesID;
   private int[] mCollectibleItems;

   protected CraftingItem(DChocByteArray data) throws EOFException {
      this.mType = 1913;
      this.loadData(data);
   }

   public int[] getCollectibleItems() {
      return this.mCollectibleItems;
   }

   public int getCreatesType() {
      return this.mCreatesType;
   }

   public int getCreatesID() {
      return this.mCreatesID;
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mTitle = data.readShort();
      this.mDescription = data.readShort();
      this.mCreatesType = data.readShort();
      this.mCreatesID = data.readShort();
      data.readByte();
      this.mCollectibleItems = ToolkitHelpers.readIntArray(data);
      this.mIconAnimationID = data.readInt();
   }
}
