package com.dchoc.idead.missions;

import com.dchoc.idead.Time;
import com.dchoc.idead.fixes.Fixes;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.idead.player.Inventory;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class Counter implements CsvIDs {
   public static final int SUBTYPE_CRAFTED = 2661;
   public static final int SUBTYPE_HIRE = 118;
   public static final int SUBTYPE_INVALID = -1;
   public static final int SUBTYPE_NEIGHBOR = 1926;
   public static final int SUBTYPE_SPOUSE = 41;
   public static final int SUBTYPE_TUTOR = 2514;
   public static final int TYPE_ACCEPT_MISSION = 2704;
   public static final int TYPE_ALER_ZOMBIE = 2971;
   public static final int TYPE_BUY = 2518;
   public static final int TYPE_BUY_OR_PICKUP = 2582;
   public static final int TYPE_CENTER_CAMERA = 2733;
   public static final int TYPE_CLEAN_UP = 2291;
   public static final int TYPE_CLEAR = 112;
   public static final int TYPE_CLEAR_AT = 2513;
   public static final int TYPE_CLEAR_WITH = 2553;
   public static final int TYPE_COLLECT = 2491;
   public static final int TYPE_COMPLETE = 2520;
   public static final int TYPE_COMPLETE_ACHIEVEMENT = 2541;
   public static final int TYPE_CONNECTIVITY = 2885;
   public static final int TYPE_CONNECTIVITY_REMINDER = 2887;
   public static final int TYPE_CONSTRUCT = 2493;
   public static final int TYPE_CONSUME = 100;
   public static final int TYPE_CRAFT = 2526;
   public static final int TYPE_CUSTOMIZE = 2341;
   public static final int TYPE_ENERGY_FROM_ITEM = 2963;
   public static final int TYPE_FARM = 128;
   public static final int TYPE_HARVEST = 2498;
   public static final int TYPE_HARVEST_AT = 2507;
   public static final int TYPE_HARVEST_WITH = 2561;
   public static final int TYPE_HAVE = 2516;
   public static final int TYPE_HAVE_SUBTYPE = 2642;
   public static final int TYPE_HIRE = 118;
   public static final int TYPE_HIT_BY_ZOMBIE = 2714;
   public static final int TYPE_HIT_ZOMBIE = 2489;
   public static final int TYPE_INVALID = -1;
   public static final int TYPE_KILL_ZOMBIE = 95;
   public static final int TYPE_KILL_ZOMBIE_AT = 2593;
   public static final int TYPE_KILL_ZOMBIE_AT_NEIGHBOT_WITH = 2658;
   public static final int TYPE_KILL_ZOMBIE_WITH = 78;
   public static final int TYPE_KILL_ZOMBIE_WITH_CRAFTED = 90;
   public static final int TYPE_MOVE = 2500;
   public static final int TYPE_OPEN_MISSION = 2895;
   public static final int TYPE_OUT_OF = 2476;
   public static final int TYPE_PICK_UP = 2480;
   public static final int TYPE_PICK_UP_COMBO = 2482;
   public static final int TYPE_REPAIR = 2495;
   public static final int TYPE_REPAIR_AT = 2597;
   public static final int TYPE_REPAIR_WITH = 2533;
   public static final int TYPE_SELL_ADULT = 2504;
   public static final int TYPE_TRADE_COLLECTION = 2546;
   public static final int TYPE_UPGRADE = 2528;
   public static final int TYPE_VISIT = 123;
   public static final int TYPE_WAIT = 2571;
   public static final int UPDATE_TIME = 500;
   public static final int VALUE_ANY = 80;
   public static final int VALUE_NONE = -1;
   protected int mCounter;
   protected int mGoal;
   protected long mStartTime;
   protected int mSubType;
   protected int mType = -1;
   private int mUpdateTimer;
   protected int mValue;

   public Counter() {
      this.mSubType = -1;
      this.mValue = -1;
      this.mUpdateTimer = 500;
   }

   public boolean isValidUpdate(int type, int subType, int value) {
      return this.mType == type || this.mType == 2582 && (type == 2480 || type == 2518) ? this.mSubType == subType && this.mValue == value : false;
   }

   public boolean isManuallyUpdated() {
      return this.mType != 2341 && this.mType != 2516 && this.mType != 2571 && this.mType != 2642 && this.mType != 6048;
   }

   public int logicUpdate(int deltaTime) {
      if (this.isManuallyUpdated()) {
         return 0;
      }

      this.mUpdateTimer += deltaTime;
      int result = 0;
      if (this.mUpdateTimer >= 500) {
         int count = 0;
         if (this.mType == 2341) {
            AvatarDescription avatar = PlayerProfile.getActiveAvatar();
            if (avatar != null) {
               count = avatar.isEquippedWithGroup(this.mValue) ? 1 : 0;
            }
         } else if (this.mType == 2516 || this.mType == 2642) {
            switch (this.mValue) {
               case 7:
                  count = PlayerProfile.getCash();
                  break;
               case 9:
                  count = PlayerProfile.getCoins();
                  break;
               case 11:
                  count = PlayerProfile.getStone();
                  break;
               case 13:
                  count = PlayerProfile.getFood();
                  break;
               case 107:
                  count = PlayerProfile.getEnergy();
                  break;
               default:
                  Inventory inventory = PlayerProfile.getInventory();
                  count = this.mType == 2642 ? inventory.getItemWithSubTypeCount(this.mValue) : inventory.getItemCount(this.mValue);
            }
         } else if (this.mType == 2571) {
            if (this.mStartTime == 0L) {
               this.mStartTime = Time.getTimestamp();
            }

            count = (int)(Time.getTimestampDelta(this.mStartTime) / 60000L);
         } else if (this.mType == 6048) {
            count = MissionManager.isMissionCompleted(this.mValue) ? 1 : 0;
         }

         int previous = this.getCounter();
         this.setCounter(count);
         this.mUpdateTimer = 0;
         if (previous != count) {
            result = 11;
         }
      }

      if (this.isGoalReached()) {
         result = 7;
      }

      return result;
   }

   public boolean updateCounter(int type, int subType, int value, int amount) {
      if (!this.isValidUpdate(type, subType, value)) {
         return false;
      }

      this.mCounter += amount;
      return this.isGoalReached();
   }

   public boolean isGoalReached() {
      return this.mGoal > 0 ? Math.max(this.mGoal, 1) <= this.mCounter : this.mCounter > 0;
   }

   public int getCountLeft() {
      return Math.max(0, this.mGoal - this.mCounter);
   }

   public void resetCounter() {
      this.mCounter = 0;
   }

   public void setCounter(int counter) {
      this.mCounter = counter;
   }

   public int getCounter() {
      return this.mCounter;
   }

   public void setStartTime(long timestamp) {
      this.mStartTime = timestamp;
   }

   public long getStartTime() {
      return this.mStartTime;
   }

   public int getType() {
      return this.mType;
   }

   public int getValue() {
      return this.mValue;
   }

   public int getGoal() {
      return this.mGoal;
   }

   public float getPercentage() {
      return (float)this.mCounter / this.mGoal * 100.0F;
   }

   public void save(DChocByteArray data) {
      data.writeInt(this.mCounter);
      if (this.mType == 2571) {
         ToolkitHelpers.writeLong(this.mStartTime, data);
      }
   }

   public void load(DChocByteArray data) throws EOFException {
      this.mCounter = data.readInt();
      if (this.mType == 2571) {
         this.mStartTime = Fixes.fixTimestamp(ToolkitHelpers.readLong(data));
      }
   }
}
