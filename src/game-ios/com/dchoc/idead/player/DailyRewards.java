package com.dchoc.idead.player;

import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;

public class DailyRewards {
   public static final int NUMBER_OF_DAYS = 5;
   public int[] mRewardAmounts = new int[5];
   public int[] mRewardTypes = new int[5];

   public DailyRewards() {
      DChocByteArray data = Toolkit.getResourceByteArray(80439);

      try {
         while (true) {
            int day = data.readInt();
            int type = data.readInt();
            int[][] values = ToolkitHelpers.readIntValueList(data);
            int amount = data.readInt();
            int index = day - 1;
            if (Integer.compareUnsigned(index, 4) <= 0) {
               if (type == 145) {
                  int selected = ToolkitHelpers.getRandomInt(0, (values == null ? 0 : values.length) - 1);
                  this.mRewardTypes[index] = values[selected][0];
                  this.mRewardAmounts[index] = values[selected][1];
               } else {
                  this.mRewardTypes[index] = type;
                  this.mRewardAmounts[index] = amount;
               }
               continue;
            }
         }
      } catch (Exception exception) {
         System.err.println(exception);
      }
   }

   public int getRewardType(int day) {
      return this.mRewardTypes[day];
   }

   public int getRewardAmount(int day) {
      return this.mRewardAmounts[day];
   }
}
