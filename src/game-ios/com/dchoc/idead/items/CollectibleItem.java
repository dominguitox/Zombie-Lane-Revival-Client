package com.dchoc.idead.items;

import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class CollectibleItem extends Item implements CsvIDs {
   public static final int SUBTYPE_COMMON = 2001;
   public static final int SUBTYPE_INVALID = -1;
   public static final int SUBTYPE_PARTS = 2012;
   public static final int SUBTYPE_RARE = 1997;
   public static final int SUBTYPE_UNCOMMON = 1999;
   public static final int SUBTYPE_UPGRADES = 2022;
   private short mRequiredMission;
   private int[][] mPackagedItems;

   public CollectibleItem(DChocByteArray data) throws EOFException {
      this.mType = 47;
      this.loadData(data);
   }

   public int getRequiredMission() {
      return this.mRequiredMission;
   }

   public boolean hasPackagedItems() {
      return this.mPackagedItems != null;
   }

   public int[][] getPackagedItems() {
      return this.mPackagedItems;
   }

   @Override
   public boolean isLockedByMission() {
      Mission mission = MissionManager.getMission(this.mRequiredMission);
      return mission != null && !mission.isCompleted() ? !mission.isActive() : false;
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mTitle = data.readShort();
      this.mDescription = data.readShort();
      this.mSubType = data.readShort();
      this.mPrice = data.readInt();
      this.mCurrency = data.readShort();
      data.readBoolean();
      this.mIconAnimationID = data.readInt();
      this.mRequiredMission = data.readShort();
      this.mPackagedItems = ToolkitHelpers.readIntValueList(data);
   }
}
