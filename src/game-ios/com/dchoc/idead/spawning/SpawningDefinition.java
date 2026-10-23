package com.dchoc.idead.spawning;

import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.Time;
import com.dchoc.idead.characters.ZombieCharacter;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.DecorationItem;
import com.dchoc.idead.items.DestructibleItem;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.items.ZombieItem;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.objects.LootObject;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tuner.LevelDefinition;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.iphonewrappers.ActivityWrapper;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;
import java.util.Vector;

public class SpawningDefinition implements CsvIDs {
   public static final int ID_INVALID = -1;
   public static final int LOCATION_NONE = 2447;
   public static final int SUBTYPE_BUSH = 1991;
   public static final int SUBTYPE_GRASS = 1988;
   public static final int SUBTYPE_INVALID = -1;
   public static final int SUBTYPE_NPC = 1990;
   public static final int SUBTYPE_PAPER = 1993;
   public static final int SUBTYPE_RUBBLE = 1989;
   public static final int SUBTYPE_SEGWAY = 1995;
   public static final int SUBTYPE_TRASH = 113;
   public static final int SUBTYPE_ZOMBIE = 2776;
   private short mID;
   private short mSubType;
   private short mSpawnID;
   private int mSpawnInterval;
   private byte mMinAmount;
   private byte mMaxAmount;
   private byte mHealth;
   private byte mMaxPercentage;
   private short mLocationID;
   private int mRewardItemsType;
   private byte mMinLevel;
   private byte mMaxLevel;
   private short mRequiredMissionID;
   private short mRequiredActiveMissionID;
   private boolean mStorePermanently;
   private boolean mGuarding;
   private long mTimestamp;
   private boolean mActive;
   private int[][] mRewardItems;
   private int mPcMaxPercentage = -1;

   public SpawningDefinition(DChocByteArray dChocByteArray) throws EOFException {
      this.mHealth = 0;
      this.mTimestamp = 0L;
      this.mActive = false;
      this.loadData(dChocByteArray);
   }

   public int logicUpdate(int dt) {
      boolean status = this.updateStatus();
      if (this.mActive != status) {
         if (!status && !this.mStorePermanently) {
            this.reset();
         }

         this.mActive = status;
      }

      if (!this.isActive()) {
         return 0;
      }

      if (this.mTimestamp == 0L) {
         this.mTimestamp = Time.getTimestamp();
      }

      return Time.getTimestampDelta(this.mTimestamp) < this.mSpawnInterval ? 0 : 2;
   }

   public boolean isActive() {
      return this.mActive;
   }

   public boolean canExecuteAt(int location) {
      int id = location;
      if (this.mLocationID != location && location == 2447) {
         id = this.mLocationID;
      }

      return this.isLocationValid(SpawningManager.getLocation(id));
   }

   public boolean execute() {
      boolean executed = this.execute(0, this.mLocationID);
      if (executed) {
         this.mTimestamp = Time.getTimestamp();
      }

      return executed;
   }

   public boolean execute(int amount, int location) {
      int id = location;
      if (this.mLocationID != location && location == 2447) {
         id = this.mLocationID;
      }

      LocationDefinition locationDefinition = SpawningManager.getLocation(id);
      if (!this.isLocationValid(locationDefinition)) {
         return false;
      }

      IsometricScene scene = IsometricUtils.getScene();
      if (scene == null) {
         return false;
      }

      boolean heavy = this.isHeavySpawn(amount);
      if (heavy) {
         ActivityWrapper.show(Toolkit.getScreenWidth() >> 1, Toolkit.getScreenHeight() >> 1);
      }

      if (this.isSpawningZombies()) {
         scene.updateAccessibleTiles();
      }

      int count = 1;
      if (this.mTimestamp != 0L) {
         count = (int)Math.abs(Time.getTimestampDelta(this.mTimestamp)) / this.mSpawnInterval;
         count = Math.min(Math.max(count, 1), 2);
      }

      while (count > 0) {
         this.spawnObjects(amount, locationDefinition);
         count--;
      }

      if (heavy) {
         ActivityWrapper.hide();
      }

      return true;
   }

   public boolean isSpawningZombies() {
      switch (this.mSubType) {
         case 113:
         case 405:
         case 1988:
         case 1989:
         case 1990:
         case 1991:
         case 1993:
         case 1995:
         case 3786:
         case 3790:
         case 3797:
         case 4497:
         case 4502:
         case 4505:
         case 4507:
         case 4984:
         case 5585:
         case 5595:
         case 5599:
         case 6069:
            return false;
         default:
            return true;
      }
   }

   public boolean isSpawningDecoration() {
      return this.mSubType == 405;
   }

   public boolean reset() {
      IsometricScene scene = IsometricUtils.getScene();
      if (this.isSpawningZombies()) {
         Vector objects = scene.getObjects(2);

         for (int i = 0; i < objects.size(); i++) {
            ZombieCharacter zombie = (ZombieCharacter)objects.elementAt(i);
            if (this.mSpawnID == zombie.getSpawnID()) {
               zombie.setHealth(0);
            }
         }
      } else if (this.isSpawningDecoration()) {
         Vector objects = scene.getObjects(4);

         for (int i = 0; i < objects.size(); i++) {
            IsometricObject object = (IsometricObject)objects.elementAt(i);
            if (this.mSpawnID == object.getID()) {
               scene.removeObject(object, true);
            }
         }
      } else {
         Vector objects = scene.getObjects(3);

         for (int i = 0; i < objects.size(); i++) {
            LootObject loot = (LootObject)objects.elementAt(i);
            if (this.mSpawnID == loot.getSpawnID()) {
               loot.setHealth(0);
            }
         }
      }

      return true;
   }

   public int calculateAmount() {
      Vector[] objects = IsometricUtils.getScene().getObjects();
      int amount = 0;
      if (this.isSpawningZombies()) {
         for (int i = 0; i < objects[2].size(); i++) {
            if (((ZombieCharacter)objects[2].elementAt(i)).getSpawnID() == this.mSpawnID) {
               amount++;
            }
         }
      } else if (!this.isSpawningDecoration()) {
         for (int i = 0; i < objects[3].size(); i++) {
            if (((LootObject)objects[3].elementAt(i)).getSpawnID() == this.mSpawnID) {
               amount++;
            }
         }
      }

      return amount;
   }

   public int getID() {
      return this.mID;
   }

   public int getSubType() {
      return this.mSubType;
   }

   public int getSpawnID() {
      return this.mSpawnID;
   }

   public int getLocationID() {
      return this.mLocationID;
   }

   public boolean isStorePermanently() {
      return this.mStorePermanently;
   }

   public boolean isGuarding() {
      return this.mGuarding;
   }

   public void setTimestamp(long timestamp) {
      this.mTimestamp = timestamp;
   }

   public long getTimestamp() {
      return this.mTimestamp;
   }

   public int[][] getRewardItems() {
      return this.mRewardItems;
   }

   protected void loadData(DChocByteArray dChocByteArray) throws EOFException {
      this.mID = dChocByteArray.readShort();
      this.mSubType = dChocByteArray.readShort();
      this.mSpawnID = dChocByteArray.readShort();
      this.mSpawnInterval = dChocByteArray.readInt() * 60000;
      this.mMinAmount = dChocByteArray.readByte();
      this.mMaxAmount = dChocByteArray.readByte();
      this.mHealth = dChocByteArray.readByte();
      this.mMaxPercentage = dChocByteArray.readByte();
      this.mLocationID = dChocByteArray.readShort();
      this.mRewardItemsType = dChocByteArray.readShort();
      this.mRewardItems = ToolkitHelpers.readIntValueList(dChocByteArray);
      this.mMinLevel = dChocByteArray.readByte();
      this.mMaxLevel = dChocByteArray.readByte();
      this.mRequiredMissionID = dChocByteArray.readShort();
      this.mRequiredActiveMissionID = dChocByteArray.readShort();
      this.mStorePermanently = dChocByteArray.readBoolean();
      this.mGuarding = dChocByteArray.readBoolean();
   }

   public boolean updateStatus() {
      if (this.mMaxPercentage < 1) {
         return false;
      }

      if (this.mRequiredMissionID != -1 && MissionManager.getActiveMission(this.mRequiredMissionID) == null) {
         return false;
      }

      if (this.mRequiredActiveMissionID != -1 && MissionManager.getActiveMission(this.mRequiredActiveMissionID) == null) {
         return false;
      }

      int level = PlayerProfile.getLevel();
      return this.mMinLevel >= 0 && this.mMinLevel > level ? false : this.mMaxLevel >= level || this.mMaxLevel < 0;
   }

   public boolean isHeavySpawn(int amount) {
      if (amount > 3) {
         return true;
      }

      switch (this.mSpawnID) {
         case 56:
         case 57:
         case 1987:
         case 3004:
         case 3110:
         case 3194:
         case 3782:
         case 4314:
         case 4315:
         case 4316:
         case 5564:
         case 5568:
         case 5569:
         case 6052:
         case 6220:
            return true;
         default:
            return false;
      }
   }

   public boolean isLocationValid(LocationDefinition location) {
      return location != null && GameEngineUtils.getAreaName() == location.getGamefield();
   }

   private void spawnObjects(int amount, LocationDefinition location) {
      if (location != null) {
         if (amount < 1) {
            amount = ToolkitHelpers.getRandomInt(this.mMinAmount, this.mMaxAmount);
            if (this.mMaxPercentage > 0) {
               int current = this.calculateAmount();
               int max = this.maxPercentage() * this.getMaxAmount() / 100;
               if (current + amount >= max) {
                  amount = max - current;
               }
            }

            if (amount == 0) {
               return;
            }
         }

         IsometricScene scene = IsometricUtils.getScene();
         if (this.isSpawningZombies()) {
            ZombieItem item = ItemManager.getZombie(this.mSpawnID);
            if (item == null) {
               return;
            }

            for (; amount > 0; amount--) {
               IsometricTile tile = location.calculateTile(item);
               if (tile != null) {
                  ZombieCharacter zombie = scene.createZombie(item, tile.getX(), tile.getY());
                  zombie.setRewardItem(ItemManager.calculateItem(this.mRewardItemsType, this.mRewardItems));
                  zombie.setSpawnID(this.mSpawnID);
                  zombie.setGuarding(this.mGuarding);
                  zombie.respawn();
               }
            }
         } else if (this.isSpawningDecoration()) {
            DecorationItem item = ItemManager.getDecoration(this.mSpawnID);
            if (item == null) {
               return;
            }

            for (; amount > 0; amount--) {
               IsometricTile tile = location.calculateTile(item);
               if (tile != null) {
                  scene.createDecoration(item, tile.getX(), tile.getY());
               }
            }
         } else {
            DestructibleItem item = ItemManager.getDestructible(this.mSpawnID);
            if (item == null) {
               return;
            }

            for (; amount > 0; amount--) {
               IsometricTile tile = location.calculateTile(item);
               if (tile != null) {
                  LootObject loot = scene.createLoot(item, tile.getX(), tile.getY());
                  loot.setRewardItem(ItemManager.calculateItem(this.mRewardItemsType, this.mRewardItems));
                  if (this.mHealth > 0) {
                     loot.setHealth(this.mHealth);
                  }

                  loot.setSpawnID(this.mSpawnID);
               }
            }
         }
      }
   }

   boolean isPeriodic() {
      return this.mMaxPercentage >= 1;
   }

   void applyPcMultiplier(float factor) {
      this.mMinAmount = (byte)Math.min(127, multiply(this.mMinAmount, factor));
      this.mMaxAmount = (byte)Math.min(127, multiply(this.mMaxAmount, factor));
      this.mPcMaxPercentage = multiply(this.mMaxPercentage, factor);
   }

   static int multiply(int value, float factor) {
      return Math.max(value, (int)Math.ceil(value * factor - 1.0E-4));
   }

   private int maxPercentage() {
      return this.mPcMaxPercentage >= 0 ? this.mPcMaxPercentage : this.mMaxPercentage;
   }

   private int getMaxAmount() {
      LevelDefinition level = Tuner.getLevel(PlayerProfile.getLevel());
      int area = GameEngineUtils.getAreaName();
      switch (this.mSubType) {
         case 113:
            return level.getMaxTrash();
         case 1988:
            return level.getMaxGrass();
         case 1989:
            return level.getMaxRubble();
         case 1991:
            return level.getMaxBrokenBush();
         case 1993:
            return level.getMaxPaper();
         case 2776:
            if (area == 4745) {
               return level.getMaxZombieCrashSite();
            } else if (area == 4054) {
               return level.getMaxZombieDowntown();
            } else {
               if (area == 5560) {
                  return level.getMaxZombieCure();
               }

               return level.getMaxZombie();
            }
         case 3294:
            return level.getMaxZombieRabbit();
         case 3783:
            return level.getMaxZombieClownHome();
         case 3784:
            return level.getMaxZombieClown();
         case 4299:
            return level.getMaxZombieCityEngineer();
         case 4495:
            return level.getMaxZombieDowntown();
         case 4497:
            return level.getMaxRubbleDowntown();
         case 4502:
            return level.getMaxGrassDowntown();
         case 4505:
            return level.getMaxBrokenBushDowntown();
         case 4507:
            return level.getMaxTrashDowntown();
         case 4752:
            return level.getMaxZombieCrashSite();
         case 5567:
            return level.getMaxZombieCure();
         case 5595:
            return level.getMaxDocNotes();
         case 6069:
            return level.getMaxOoze();
         default:
            return 0;
      }
   }
}
