package com.dchoc.idead.spawning;

import com.dchoc.collection.HashTable;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;
import java.util.Vector;
import zlport.ios.Ios;

public class SpawningManager {
   public static final int INITIAL_ACTIVE_SPAWNINGS_CAPACITY = 32;
   public static final int INITIAL_LOCATIONS_CAPACITY = 200;
   public static final int INITIAL_SPAWNINGS_CAPACITY = 150;
   private static Vector smSpawnings;
   private static HashTable smSpawnIDs;
   private static Vector smActiveSpawnings;
   private static HashTable smLocations;

   public static void init() {
      smSpawnings = new Vector(150);
      smSpawnIDs = new HashTable(150);
      smActiveSpawnings = new Vector(32);
      smLocations = new HashTable(200);
      DChocByteArray data = Toolkit.getResourceByteArray(80485);

      try {
         while (true) {
            SpawningDefinition spawning = new SpawningDefinition(data);
            smSpawnings.addElement(spawning);
            smSpawnIDs.add(spawning.getSpawnID(), spawning);
         }
      } catch (Exception exception) {
         System.err.println(exception);
         data = Toolkit.getResourceByteArray(80486);

         try {
            while (true) {
               LocationDefinition location = new LocationDefinition(data);
               smLocations.add(location.getID(), location);
            }
         } catch (Exception exceptionx) {
            System.err.println(exceptionx);
            applyPcSpawnMultiplier();
         }
      }
   }

   private static void applyPcSpawnMultiplier() {
      Float factor = Ios.platform().spawnMultiplierOverride();
      if (factor != null && !(factor <= 1.0F)) {
         for (int i = 0; i < smSpawnings.size(); i++) {
            SpawningDefinition spawning = (SpawningDefinition)smSpawnings.elementAt(i);
            if (spawning.isSpawningZombies() && spawning.isPeriodic()) {
               spawning.applyPcMultiplier(factor);
            }
         }
      }
   }

   public static int logicUpdate(int dt) {
      if (GameEngineUtils.isAtNeigborsPlace()) {
         return 0;
      }

      boolean executed = false;

      for (int i = 0; i < smSpawnings.size(); i++) {
         SpawningDefinition spawning = (SpawningDefinition)smSpawnings.elementAt(i);
         if (spawning.logicUpdate(dt) == 2 && !executed) {
            executed = spawning.execute();
         }
      }

      return 0;
   }

   public static SpawningDefinition getSpawning(int id) {
      if (id == -1) {
         return null;
      }

      for (int i = 0; i < smSpawnings.size(); i++) {
         SpawningDefinition spawning = (SpawningDefinition)smSpawnings.elementAt(i);
         if (spawning.getID() == id) {
            return spawning;
         }
      }

      return null;
   }

   public static SpawningDefinition getSpawnID(int spawnID) {
      return (SpawningDefinition)smSpawnIDs.get(spawnID);
   }

   public static Vector getActiveSpawnings() {
      smActiveSpawnings.removeAllElements();

      for (int i = 0; i < smSpawnings.size(); i++) {
         SpawningDefinition spawning = (SpawningDefinition)smSpawnings.elementAt(i);
         if (spawning.isActive()) {
            smActiveSpawnings.addElement(spawning);
         }
      }

      return smActiveSpawnings;
   }

   public static LocationDefinition getLocation(int id) {
      return (LocationDefinition)smLocations.get(id);
   }
}
