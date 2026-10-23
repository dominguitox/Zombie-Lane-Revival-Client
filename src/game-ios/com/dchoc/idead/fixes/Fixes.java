package com.dchoc.idead.fixes;

import com.dchoc.idead.Time;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.missions.MissionObjective;
import com.dchoc.idead.missions.MissionSetup;
import com.dchoc.idead.neighbors.NeighborRob;
import com.dchoc.idead.notifications.NotificationDefinition;
import com.dchoc.idead.notifications.NotificationManager;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.idead.player.Inventory;
import com.dchoc.idead.player.InventoryItem;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.spawning.SpawningDefinition;
import com.dchoc.idead.spawning.SpawningManager;
import com.dchoc.idead.tuner.AreaSetup;
import com.dchoc.idead.tuner.Tuner;
import java.util.Vector;

public class Fixes {
   public static void fixMissingCharacters(AreaSetup setup) {
      if (setup != null) {
         if (!setup.isHome()) {
            fixActiveCharacter(setup);
            return;
         }

         IsometricScene scene = IsometricUtils.getScene();
         Mission spouseMission = MissionManager.getMission(Tuner.getMissionToUnlockSpouse());
         if (spouseMission != null && spouseMission.isCompleted() && PlayerProfile.getAvatar(41) == null) {
            PlayerProfile.addAvatar(41, false);
         }

         Mission dogMission = MissionManager.getMission(Tuner.getMissionToUnlockDog());
         if (dogMission != null && dogMission.isCompleted() && PlayerProfile.getAvatar(43) == null) {
            PlayerProfile.addAvatar(43, false);
         }

         Vector avatars = PlayerProfile.getAvatars();

         for (int i = 0; i < avatars.size(); i++) {
            AvatarDescription avatar = (AvatarDescription)avatars.elementAt(i);
            if (scene.getPlayer(avatar) == null) {
               PlayerProfile.addAvatar(avatar.getCharacter(), true);
            }
         }
      }
   }

   public static void fixActiveCharacter(AreaSetup setup) {
      if (setup != null) {
         IsometricScene scene = IsometricUtils.getScene();
         AvatarDescription activeAvatar = PlayerProfile.getActiveAvatar();
         PlayerCharacter activePlayer = scene.getPlayer(activeAvatar);
         if (activePlayer == null) {
            int startX = setup.getPlayerStartX();
            int startY = setup.getPlayerStartY();
            IsometricTile tile = scene.getTile(startX, startY);
            if (tile == null) {
               tile = scene.getTileAtCenterOfScreen();
            }

            scene.createPlayer(activeAvatar, tile.getX(), tile.getY(), true);
         } else {
            PlayerCharacter currentPlayer = scene.getPlayer();
            if (currentPlayer != activePlayer && activeAvatar != null) {
               PlayerProfile.setActiveAvatar(activeAvatar.getCharacter());
            }
         }
      }
   }

   public static void fixInventoryItem(InventoryItem item) {
      if (item != null) {
         item.setAmount(Math.abs(item.getAmount()));
      }
   }

   public static void fixCurrencies() {
      PlayerProfile.setEnergy(Math.abs(PlayerProfile.getEnergy()));
      PlayerProfile.setCash(Math.abs(PlayerProfile.getCash()));
      PlayerProfile.setCoins(Math.abs(PlayerProfile.getCoins()));
      PlayerProfile.setStone(Math.abs(PlayerProfile.getStone()));
      PlayerProfile.setFood(Math.abs(PlayerProfile.getFood()));
   }

   public static void fixInventory() {
      Inventory inventory = PlayerProfile.getInventory();
      WeaponItem weapon = ItemManager.getWeapon(49);
      InventoryItem item = inventory.getItem(weapon);
      if (item == null) {
         item = inventory.addItem(weapon, 1);
      }

      if (PlayerProfile.getCurrentWeapon() == null) {
         PlayerProfile.changeWeapon(item);
      }
   }

   public static void fixResetMissionSetups() {
      Vector missions = MissionManager.getActiveMissions();

      for (int i = 0; i < missions.size(); i++) {
         Mission mission = (Mission)missions.elementAt(i);
         mission.resetSetups(true);
         mission.executeSetups();
      }
   }

   public static long fixTimestamp(long timestamp) {
      long now = Time.getTimestamp();
      return now < timestamp ? now : timestamp;
   }

   public static void fixMissionSetups() {
      Vector missions = MissionManager.getActiveMissions();

      for (int i = 0; i < missions.size(); i++) {
         Mission mission = (Mission)missions.elementAt(i);
         if (mission != null) {
            Vector objectives = mission.getObjectives();

            for (int j = 0; j < objectives.size(); j++) {
               MissionObjective objective = (MissionObjective)objectives.elementAt(j);
               fixMissionSetups(objective);
            }
         }
      }
   }

   public static void fixMissionSetups(Mission mission) {
      if (mission != null) {
         Vector objectives = mission.getObjectives();

         for (int i = 0; i < objectives.size(); i++) {
            MissionObjective objective = (MissionObjective)objectives.elementAt(i);
            fixMissionSetups(objective);
         }
      }
   }

   public static void fixMissionSetups(MissionObjective objective) {
      if (objective != null) {
         int[] connectedSetupIds = objective.getConnectedMissionSetupIDs();
         if (!objective.isCompleted() && connectedSetupIds != null) {
            int goal = objective.getGoal();
            int counter = objective.getCounter();
            int availableSpawns = 0;

            for (int i = 0; i < connectedSetupIds.length; i++) {
               MissionSetup setup = MissionManager.getMissionSetup(connectedSetupIds[i]);
               if (setup == null) {
                  return;
               }

               if (!setup.canExecute()) {
                  return;
               }

               SpawningDefinition spawning = SpawningManager.getSpawning(setup.getSpawningID());
               if (spawning == null) {
                  return;
               }

               availableSpawns += spawning.calculateAmount();
            }

            int needed = goal - counter;
            if (availableSpawns < needed) {
               do {
                  int index = ++availableSpawns % connectedSetupIds.length;
                  MissionSetup setup = MissionManager.getMissionSetup(connectedSetupIds[index]);
                  if (setup != null) {
                     setup.execute(1);
                  }
               } while (needed != availableSpawns);
            }
         }
      }
   }

   public static void completeMissionObjectives() {
      Vector missions = MissionManager.getActiveMissions();

      for (int i = 0; i < missions.size(); i++) {
         Mission mission = (Mission)missions.elementAt(i);
         completeMissionObjectives(mission);
      }
   }

   public static void completeMissionObjectives(Mission mission) {
      if (mission != null) {
         Vector objectives = mission.getObjectives();

         for (int i = 0; i < objectives.size(); i++) {
            MissionObjective objective = (MissionObjective)objectives.elementAt(i);
            if (objective != null && !objective.isCompleted() && objective.getUnlockPrice() == -1) {
               objective.completeObjective();
            }
         }
      }
   }

   public static void fixRob(NeighborRob rob) {
      if (rob != null) {
         rob.setName("Rob");
         rob.setUserID("1");
         rob.setLevel(7);
      }
   }

   public static void fixMission(Mission mission) {
      if (mission != null && mission.getState() != 0 && mission.getNotificationID() != -1) {
         NotificationDefinition notification = NotificationManager.getNotification(mission.getNotificationID());
         if (notification != null) {
            long updatedTime = mission.getObjectiveUpdatedTime();
            long delay = notification.getDelay();
            if (updatedTime + delay < Time.getTimestamp()) {
               mission.setObjectiveUpdateTime(Time.getTimestamp());
            }
         }
      }
   }
}
