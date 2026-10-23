package com.dchoc.idead.missions;

import com.dchoc.parser.Json;
import com.dchoc.idead.spawning.SpawningDefinition;
import com.dchoc.idead.spawning.SpawningManager;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import java.io.EOFException;

public class MissionSetup implements CsvIDs {
   public static final int TYPE_INVALID = -1;
   public static final int TYPE_SPAWN_ITEM = 2436;
   public static final int TYPE_SPAWN_ZOMBIE = 2432;
   protected int mAmount;
   protected int mID;
   protected int mLocationID;
   protected int mMissionID;
   protected int mSpawningID;
   protected int mType;

   public MissionSetup(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mMissionID = data.readShort();
      this.mType = data.readShort();
      this.mAmount = data.readInt();
      data.readBoolean();
      this.mSpawningID = data.readShort();
      this.mLocationID = data.readShort();
   }

   public MissionSetup() {
   }

   /** Build a setup from one element of a mission's JSON {@code setups} array. */
   public static MissionSetup fromJson(java.util.Map<String, Object> data) {
      MissionSetup setup = new MissionSetup();
      setup.mID = Json.intValue(data, "id", -1);
      setup.mMissionID = Json.intValue(data, "missionId", -1);
      setup.mType = Json.intValue(data, "typeId", -1);
      setup.mAmount = Json.intValue(data, "amount", 0);
      setup.mSpawningID = Json.intValue(data, "spawningId", -1);
      setup.mLocationID = Json.intValue(data, "locationId", -1);
      return setup;
   }

   public boolean canExecute() {
      if (this.mType != 2432 && this.mType != 2436 && this.mType != 3056) {
         return false;
      }

      SpawningDefinition spawning = SpawningManager.getSpawning(this.mSpawningID);
      return spawning != null && spawning.canExecuteAt(this.mLocationID);
   }

   public boolean execute() {
      return this.execute(this.mAmount);
   }

   public boolean execute(int amount) {
      if (this.mType == 2432 || this.mType == 2436) {
         SpawningDefinition spawning = SpawningManager.getSpawning(this.mSpawningID);
         return spawning != null && spawning.execute(amount, this.mLocationID);
      }

      if (this.mType != 3056) {
         return false;
      }

      SpawningDefinition spawning = SpawningManager.getSpawning(this.mSpawningID);
      return spawning != null && spawning.reset();
   }

   public boolean reset(boolean force) {
      if (this.mType == 2432 || this.mType == 2436) {
         SpawningDefinition spawning = SpawningManager.getSpawning(this.mSpawningID);
         if (spawning != null && (!spawning.isStorePermanently() || force)) {
            return spawning.reset();
         }
      }

      return this.mType == 3056 ? this.execute(this.mAmount) : false;
   }

   public int getID() {
      return this.mID;
   }

   public int getMissionID() {
      return this.mMissionID;
   }

   public int getSpawningID() {
      return this.mSpawningID;
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mMissionID = data.readShort();
      this.mType = data.readShort();
      this.mAmount = data.readInt();
      data.readBoolean();
      this.mSpawningID = data.readShort();
      this.mLocationID = data.readShort();
   }
}
