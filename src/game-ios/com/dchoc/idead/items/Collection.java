package com.dchoc.idead.items;

import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class Collection {
   private short mID;
   private short mTitleID;
   private int mIconAnimationID;
   private int[] mItems;
   private int[][] mRewardAmount;

   public Collection(DChocByteArray data) throws EOFException {
      this.loadData(data);
   }

   public int getID() {
      return this.mID;
   }

   public int getIconAnimationID() {
      return this.mIconAnimationID;
   }

   public int[] getItems() {
      return this.mItems;
   }

   public int getTitleID() {
      return this.mTitleID;
   }

   public int[][] getRewardAmount() {
      return this.mRewardAmount;
   }

   public int getItemIndex(CollectibleItem item) {
      if (item == null) {
         return -1;
      }

      int count = this.mItems == null ? 0 : this.mItems.length;

      for (int i = 0; i < count; i++) {
         if (item.getID() == this.mItems[i]) {
            return i;
         }
      }

      return -1;
   }

   private void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mItems = ToolkitHelpers.readResourceIDs(data, true);
      this.mTitleID = data.readShort();
      this.mIconAnimationID = data.readInt();
      this.mRewardAmount = ToolkitHelpers.readIntValueList(data);
   }
}
