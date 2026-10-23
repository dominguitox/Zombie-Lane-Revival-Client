package com.dchoc.idead.player;

import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;

public class VisitingRewards {
   private int mRewardXp;
   private int mRewardCoins;
   private int mRewardEnergy;
   private int mRewardDuctTape;

   public VisitingRewards() {
      DChocByteArray data = Toolkit.getResourceByteArray(80428);

      try {
         data.readInt();
         int reward = data.readInt();
         if (reward > 0) {
            this.mRewardXp = reward;
         }

         reward = data.readInt();
         if (reward > 0) {
            this.mRewardCoins = reward;
         }

         reward = data.readInt();
         if (reward > 0) {
            this.mRewardEnergy = reward;
         }

         reward = data.readInt();
         if (reward > 0) {
            this.mRewardDuctTape = 1;
         }
      } catch (Exception exception) {
         System.err.println(exception);
      }
   }

   public int getXpReward() {
      return this.mRewardXp;
   }

   public int getCoinReward() {
      return this.mRewardCoins;
   }

   public int getEnergyReward() {
      return this.mRewardEnergy;
   }

   public int getDuctTapeReward() {
      return this.mRewardDuctTape;
   }

   public int addRewards() {
      return this.mRewardDuctTape;
   }
}
