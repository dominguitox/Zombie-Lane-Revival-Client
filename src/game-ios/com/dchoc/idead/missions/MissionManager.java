package com.dchoc.idead.missions;

import com.dchoc.hud.HUD;
import com.dchoc.hud.HUDMissions;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.Profiler;
import com.dchoc.idead.Time;
import com.dchoc.idead.events.EventManager;
import com.dchoc.idead.events.TimedEvent;
import com.dchoc.idead.fixes.Fixes;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.spawning.LocationDefinition;
import com.dchoc.idead.spawning.SpawningDefinition;
import com.dchoc.idead.spawning.SpawningManager;
import com.dchoc.idead.tracking.ErrorEvent;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.iphonewrappers.SettingsWrapper;
import com.dchoc.parser.Json;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MissionManager {
   public static final int INITIAL_ACHIEVEMENTS_CAPACITY = 16;
   public static final int INITIAL_ACTIVE_MISSIONS_CAPACITY = 16;
   public static final int INITIAL_DELAYED_MISSION_SETUPS_CAPACITY = 8;
   public static final int INITIAL_MISSIONS_CAPACITY = 380;
   public static final int INITIAL_MISSION_EVENTS_CAPACITY = 96;
   public static final int INITIAL_MISSION_SETUPS_CAPACITY = 330;
   public static final int INITIAL_NO_RESET_OBJECTIVES = 196;
   public static final int INITIAL_OBJECTIVES_CAPACITY = 580;
   public static final int INITIAL_SURVIVAL_TIPS_CAPACITY = 32;
   public static final String LEADERBOARD_KILLED_ZOMBIES = "killedzombies";
   public static final String RMS_MISSIONS = "RMS_MISSIONS";
   public static final String RMS_MISSIONS_BACKUP_V1 = "RMS_MISSION_BACKUP_V1";
   public static final String RMS_MISSIONS_BACKUP_V2 = "RMS_MISSION_BACKUP_V2";
   public static final String RMS_MISSIONS_BACKUP_V3 = "RMS_MISSION_BACKUP_V3";
   public static final String RMS_MISSIONS_DBG_BACKUP = "RMS_MISSIONS_DBG_BACKUP";
   private static Vector smAchievements;
   private static Vector smActiveMissions;
   private static Vector smDelayedMissionSetups;
   public static int smKilledZombies;
   private static Vector smMissionEvents;
   private static Vector smMissionSetups;
   private static Vector smMissions;
   private static Vector smNoResetObjectives;
   private static Vector smObjectives;
   private static Vector smSurvivalTips;
   public static boolean smUpdateNewMissions;
   private static long smCraftTime;
   private static long smHarvestTime;
   private static long smUpgradeTime;
   private static long smClearTime;
   private static long smSpendTime;
   private static long smCustomizeTime;
   private static long smRepairTime;
   private static boolean smLoadIncomplete;

   public static void init() {
      smMissionSetups = new Vector(330);
      smDelayedMissionSetups = new Vector(8);
      smMissions = new Vector(380);
      smObjectives = new Vector(580);
      smNoResetObjectives = new Vector(196);
      smSurvivalTips = new Vector(32);
      smAchievements = new Vector(16);
      smActiveMissions = new Vector(16);
      smMissionEvents = new Vector(96);
      smUpdateNewMissions = true;
      smCraftTime = 0L;
      smHarvestTime = 0L;
      smUpgradeTime = 0L;
      smClearTime = 0L;
      smSpendTime = Time.getTimestamp();
      smCustomizeTime = Time.getTimestamp();
      smRepairTime = 0L;
      smLoadIncomplete = false;
      loadData();
      CustomMissionsBootstrap.installFromCache();
   }

   /**
    * Load the mission definitions. The shared {@code missions.json} is the
    * primary source; when it is absent or invalid the packed {@code r1} tables
    * are used as a fallback.
    */
   public static void loadData() {
      String json = Toolkit.loadSharedMissionsJson();
      if (json != null) {
         try {
            if (loadDataFromJson(json)) {
               logLoadedDefinitions("json");
               return;
            }
         } catch (RuntimeException exception) {
            System.err.println("MissionManager: could not read missions.json, falling back to r1: " + exception);
         }
      }

      loadDataFromBinary();
      logLoadedDefinitions("binary");
   }

   private static void logLoadedDefinitions(String source) {
      int objectives = 0;
      for (int i = 0; i < smMissions.size(); i++) {
         objectives += ((Mission)smMissions.elementAt(i)).getObjectives().size();
      }
      System.out.println("MissionManager: loaded " + smMissions.size() + " missions, " + objectives
         + " objectives, " + smAchievements.size() + " achievements (source=" + source + ")");
   }

   /**
    * Build the mission collections from the JSON export used by
    * {@code dump_missions.py} and the Mission Editor. Returns false when the
    * document does not look like a mission export, so the caller can fall back
    * to the packed tables.
    */
   public static boolean loadDataFromJson(String json) {
      Map<String, Object> root = Json.asObject(Json.parse(json));
      if (root == null) {
         return false;
      }

      List<Object> missions = Json.array(root, "missions");
      if (missions == null) {
         return false;
      }

      for (Object value : missions) {
         Map<String, Object> missionData = Json.asObject(value);
         if (missionData == null) {
            continue;
         }

         Mission mission = Mission.fromJson(missionData);
         smMissions.addElement(mission);
         if (mission.getType() == 2399) {
            smSurvivalTips.addElement(mission);
         }

         List<Object> objectives = Json.array(missionData, "objectives");
         if (objectives != null) {
            for (Object objectiveValue : objectives) {
               MissionObjective objective = MissionObjective.fromJson(Json.asObject(objectiveValue));
               if (objective == null) {
                  continue;
               }

               if (!objective.isResetCounter()) {
                  smNoResetObjectives.addElement(objective);
               }

               smObjectives.addElement(objective);
               mission.addObjective(objective);
            }
         }

         List<Object> setups = Json.array(missionData, "setups");
         if (setups != null) {
            for (Object setupValue : setups) {
               MissionSetup setup = MissionSetup.fromJson(Json.asObject(setupValue));
               if (setup != null) {
                  smMissionSetups.addElement(setup);
               }
            }
         }
      }

      addJsonSetups(Json.array(root, "unassignedSetups"));
      addJsonMissionEvents(Json.array(root, "missionEventsByType"));
      addJsonMissionEvents(Json.array(root, "missionEventsById"));
      addJsonObjectiveEvents(Json.array(root, "objectiveEvents"));

      List<Object> achievements = Json.array(root, "achievements");
      if (achievements != null) {
         for (Object value : achievements) {
            Map<String, Object> achievementData = Json.asObject(value);
            if (achievementData == null) {
               continue;
            }

            Achievement achievement = Achievement.fromJson(achievementData);
            smAchievements.addElement(achievement);
            List<Object> objectives = Json.array(achievementData, "objectives");
            if (objectives != null) {
               for (Object objectiveValue : objectives) {
                  AchievementObjective objective = AchievementObjective.fromJson(Json.asObject(objectiveValue));
                  if (objective != null) {
                     achievement.addObjective(objective);
                  }
               }
            }
         }
      }

      return true;
   }

   private static void addJsonSetups(List<Object> setups) {
      if (setups == null) {
         return;
      }

      for (Object value : setups) {
         MissionSetup setup = MissionSetup.fromJson(Json.asObject(value));
         if (setup != null) {
            smMissionSetups.addElement(setup);
         }
      }
   }

   private static void addJsonMissionEvents(List<Object> events) {
      if (events == null) {
         return;
      }

      for (Object value : events) {
         MissionEvent event = MissionEvent.fromJson(Json.asObject(value));
         if (event != null) {
            smMissionEvents.addElement(event);
         }
      }
   }

   private static void addJsonObjectiveEvents(List<Object> events) {
      if (events == null) {
         return;
      }

      for (Object value : events) {
         ObjectiveEvent event = ObjectiveEvent.fromJson(Json.asObject(value));
         if (event == null) {
            continue;
         }

         Mission mission = getMission(event.getMissionID());
         if (mission != null) {
            MissionObjective objective = mission.getObjectiveByType(event.getType(), event.getValue());
            if (objective != null) {
               objective.addProgressEvent(event);
            }
         }
      }
   }

   public static void loadDataFromBinary() {
      DChocByteArray data = Toolkit.getResourceByteArray(80468);

      try {
         while (true) {
            Mission mission = new Mission(data);
            smMissions.addElement(mission);
            if (mission.getType() == 2399) {
               smSurvivalTips.addElement(mission);
            }
         }
      } catch (Exception exception) {
         if (!(exception instanceof EOFException)) {
            exception.printStackTrace();
         }

         System.err.println(exception);
         data = Toolkit.getResourceByteArray(80475);

         try {
            while (true) {
               MissionObjective objective = new MissionObjective(data);
               if (!objective.isResetCounter()) {
                  smNoResetObjectives.addElement(objective);
               }

               smObjectives.addElement(objective);
               int missionId = objective.getMissionID();
               Vector missions = smMissions;
               int i = 0;

               Mission mission;
               do {
                  missions.size();
                  mission = (Mission)missions.elementAt(i);
                  i++;
               } while (mission.getID() != missionId);

               mission.addObjective(objective);
            }
         } catch (Exception exceptionx) {
            if (!(exceptionx instanceof EOFException)) {
               exceptionx.printStackTrace();
            }

            System.err.println(exceptionx);
            data = Toolkit.getResourceByteArray(80470);

            try {
               while (true) {
                  smMissionSetups.addElement(new MissionSetup(data));
               }
            } catch (Exception exceptionxx) {
               if (!(exceptionxx instanceof EOFException)) {
                  exceptionxx.printStackTrace();
               }

               System.err.println(exceptionxx);
               data = Toolkit.getResourceByteArray(80472);

               try {
                  while (true) {
                     smMissionEvents.addElement(new MissionEvent(data, true));
                  }
               } catch (Exception exceptionxxx) {
                  if (!(exceptionxxx instanceof EOFException)) {
                     exceptionxxx.printStackTrace();
                  }

                  System.err.println(exceptionxxx);
                  data = Toolkit.getResourceByteArray(80473);

                  try {
                     while (true) {
                        smMissionEvents.addElement(new MissionEvent(data, false));
                     }
                  } catch (Exception exceptionxxxx) {
                     if (!(exceptionxxxx instanceof EOFException)) {
                        exceptionxxxx.printStackTrace();
                     }

                     System.err.println(exceptionxxxx);
                     data = Toolkit.getResourceByteArray(80476);

                     try {
                        while (true) {
                           ObjectiveEvent event = new ObjectiveEvent(data);
                           int missionId = event.getMissionID();
                           Vector missions = smMissions;
                           int i = 0;

                           Mission mission;
                           do {
                              missions.size();
                              mission = (Mission)missions.elementAt(i);
                              i++;
                           } while (mission.getID() != missionId);

                           mission.getObjectiveByType(event.getType(), event.getValue()).addProgressEvent(event);
                        }
                     } catch (Exception exceptionxxxxx) {
                        if (!(exceptionxxxxx instanceof EOFException)) {
                           exceptionxxxxx.printStackTrace();
                        }

                        System.err.println(exceptionxxxxx);
                        data = Toolkit.getResourceByteArray(80435);

                        try {
                           while (true) {
                              smAchievements.addElement(new Achievement(data));
                           }
                        } catch (Exception exceptionxxxxxx) {
                           if (!(exceptionxxxxxx instanceof EOFException)) {
                              exceptionxxxxxx.printStackTrace();
                           }

                           System.err.println(exceptionxxxxxx);
                           data = Toolkit.getResourceByteArray(80436);

                           try {
                              while (true) {
                                 AchievementObjective objective = new AchievementObjective(data);
                                 int achievementId = objective.getAchievementID();
                                 Vector achievements = smAchievements;
                                 int i = 0;

                                 Achievement achievement;
                                 do {
                                    achievements.size();
                                    achievement = (Achievement)achievements.elementAt(i);
                                    i++;
                                 } while (achievement.getID() != achievementId);

                                 achievement.addObjective(objective);
                              }
                           } catch (Exception exceptionxxxxxxx) {
                              if (!(exceptionxxxxxxx instanceof EOFException)) {
                                 exceptionxxxxxxx.printStackTrace();
                              }

                              System.err.println(exceptionxxxxxxx);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static void updateCounter(int type, int value, int amount) {
      updateCounter(type, -1, value, amount);
   }

   public static void updateCounter(int type, int subType, int value, int amount) {
      if (smActiveMissions != null && smNoResetObjectives != null) {
         for (int i = 0; i < smNoResetObjectives.size(); i++) {
            ((MissionObjective)smNoResetObjectives.elementAt(i)).updateCounter(type, subType, value, amount);
         }

         for (int i = 0; i < smActiveMissions.size(); i++) {
            ((Mission)smActiveMissions.elementAt(i)).updateCounters(type, subType, value, amount);
         }

         for (int i = 0; i < smAchievements.size(); i++) {
            ((Achievement)smAchievements.elementAt(i)).updateCounters(type, subType, value, amount);
         }

         if (type == 95 && subType == -1 && value == 80) {
            smKilledZombies += amount;
            GameEngine.getInstance().getGameCenter().reportScore("killedzombies", smKilledZombies);
         }

         switch (type) {
            case 106:
               smSpendTime = Time.getTimestamp();
               break;
            case 112:
               if (TutorialFlow.isCompleted()) {
                  smClearTime = Time.getTimestamp();
               }
               break;
            case 2495:
               smRepairTime = Time.getTimestamp();
               break;
            case 2498:
               smHarvestTime = Time.getTimestamp();
               break;
            case 2518:
               if (value == 1911) {
                  smCustomizeTime = Time.getTimestamp();
               }
               break;
            case 2526:
               smCraftTime = Time.getTimestamp();
               break;
            case 2528:
               smUpgradeTime = Time.getTimestamp();
         }
      }
   }

   public static int logicUpdate(int deltaTime) {
      updateDelayedMissionSetups();

      for (int i = 0; i < smActiveMissions.size(); i++) {
         Mission mission = (Mission)smActiveMissions.elementAt(i);
         mission.logicUpdate(deltaTime);
         if (mission.isCompleted() || mission.hasDuration() && mission.getTimeLeft() < 0) {
            if (mission.hasDuration() && mission.getTimeLeft() < 0) {
               mission.executeExpirationEvent();
            } else {
               mission.collectRewards();
               mission.executeOutroEvents();
               if (mission.isSaveAfterCompleted()) {
                  EventManager.addEvent(new TimedEvent(1500, 4053, null));
               }
            }

            if (mission.getType() == 2399) {
               HUD.increateTipsCounter();
            } else {
               HUD.getMissions().removeMission(mission);
            }

            IsometricUtils.getScene().setUpdateEdges();
            return 0;
         }
      }

      if (smUpdateNewMissions) {
         updateNewMissions();
         smUpdateNewMissions = false;
      }

      return 0;
   }

   public static void updateDelayedMissionSetups() {
      int i = 0;

      while (i < smDelayedMissionSetups.size()) {
         MissionSetup setup = (MissionSetup)smDelayedMissionSetups.elementAt(i);
         if (setup.execute()) {
            ToolkitHelpers.fastRemove(i, smDelayedMissionSetups);
         } else {
            i++;
         }
      }
   }

   private static void updateNewMissions() {
      for (int i = 0; i < smMissions.size(); i++) {
         Mission mission = (Mission)smMissions.elementAt(i);
         if (mission.getState() == 0) {
            if (!mission.isLocked() && !mission.isLockedByLevel() && !mission.isLockedByMission()) {
               mission.activate();
               if (mission.getType() == 2292) {
                  HUDMissions hud = HUD.getMissions();
                  hud.addMission(mission);
                  if (mission.isNew() && mission.getID() == 4318) {
                     hud.openMission(mission);
                  }
               }
            }
         } else if (mission.getState() == 2 || mission.getState() == 1) {
            if (mission.getState() == 2 && !smActiveMissions.contains(mission)) {
               if (mission.isLocked()) {
                  mission.deactivate();
                  continue;
               }

               smActiveMissions.addElement(mission);
               Fixes.fixMissionSetups(mission);
            }

            if (mission.getType() == 2292) {
               HUDMissions hud = HUD.getMissions();
               if (!hud.containsMission(mission)) {
                  hud.addMission(mission);
               }
            }
         }
      }
   }

   public static void updateNeighborMissionSetups() {
      int area = GameEngineUtils.getAreaName();

      for (int i = 0; i < smMissionSetups.size(); i++) {
         MissionSetup setup = (MissionSetup)smMissionSetups.elementAt(i);
         int missionId = setup.getMissionID();
         if (missionId != -1 && getActiveMission(missionId) != null) {
            SpawningDefinition spawning = SpawningManager.getSpawning(setup.getSpawningID());
            if (spawning != null) {
               LocationDefinition location = SpawningManager.getLocation(spawning.getLocationID());
               if (area == location.getGamefield() || area == 2514 && location.getGamefield() == 1926) {
                  addDelayedMissionSetup(setup);
               }
            }
         }
      }
   }

   public static Mission getActiveMission(int id) {
      if (id != -1) {
         for (int i = 0; i < smActiveMissions.size(); i++) {
            Mission mission = (Mission)smActiveMissions.elementAt(i);
            if (mission.getID() == id) {
               return mission;
            }
         }
      }

      return null;
   }

   public static boolean addDelayedMissionSetup(MissionSetup setup) {
      if (setup != null) {
         for (int i = 0; i < smDelayedMissionSetups.size(); i++) {
            MissionSetup previous = (MissionSetup)smDelayedMissionSetups.elementAt(i);
            if (setup.getID() == previous.getID()) {
               return false;
            }
         }

         smDelayedMissionSetups.addElement(setup);
         return true;
      } else {
         return false;
      }
   }

   public static void setUpdateNewMissions() {
      smUpdateNewMissions = true;
   }

   public static MissionSetup getMissionSetup(int id) {
      if (id != -1) {
         for (int i = 0; i < smMissionSetups.size(); i++) {
            MissionSetup setup = (MissionSetup)smMissionSetups.elementAt(i);
            if (setup.getID() == id) {
               return setup;
            }
         }
      }

      return null;
   }

   public static Achievement getAchievement(int id) {
      for (int i = 0; i < smAchievements.size(); i++) {
         Achievement achievement = (Achievement)smAchievements.elementAt(i);
         if (achievement.getID() == id) {
            return achievement;
         }
      }

      return null;
   }

   public static Mission getMission(int id) {
      if (id != -1) {
         for (int i = 0; i < smMissions.size(); i++) {
            Mission mission = (Mission)smMissions.elementAt(i);
            if (mission.getID() == id) {
               return mission;
            }
         }
      }

      return null;
   }

   public static Mission addCustomMission(DChocByteArray data, DChocByteArray objectivesData) {
      if (data == null) {
         return null;
      }

      Mission mission;
      try {
         mission = new Mission(data);
      } catch (Exception exception) {
         System.err.println("Custom mission could not be read: " + exception);
         return null;
      }

      Mission existing = getMission(mission.getID());
      if (existing != null) {
         smUpdateNewMissions = true;
         return existing;
      }

      attachCustomObjectives(mission, objectivesData);
      smMissions.addElement(mission);
      if (mission.getType() == 2399) {
         smSurvivalTips.addElement(mission);
      }

      smUpdateNewMissions = true;
      return mission;
   }

   private static void attachCustomObjectives(Mission mission, DChocByteArray data) {
      if (data == null) {
         return;
      }

      try {
         while (true) {
            MissionObjective objective = new MissionObjective(data);
            if (objective.getMissionID() == mission.getID()) {
               mission.addObjective(objective);
               if (!objective.isResetCounter()) {
                  smNoResetObjectives.addElement(objective);
               }

               smObjectives.addElement(objective);
            }
         }
      } catch (Exception exception) {
         if (!(exception instanceof EOFException)) {
            System.err.println("Custom mission objectives could not be read: " + exception);
         }
      }
   }

   public static void showCustomMission(Mission mission) {
      if (mission == null) {
         return;
      }

      if (mission.getState() == 0) {
         mission.activate();
      }

      if (mission.getType() == 2292) {
         HUDMissions hud = HUD.getMissions();
         if (!hud.containsMission(mission)) {
            hud.addMission(mission);
         }
         hud.openMission(mission);
      }

      smUpdateNewMissions = true;
   }

   public static boolean showCustomMission(int id) {
      Mission mission = getMission(id);
      if (mission == null) {
         return false;
      }

      showCustomMission(mission);
      return true;
   }

   public static Vector getMissions() {
      return smMissions;
   }

   public static boolean isMissionCompleted(int id) {
      if (id != -1) {
         for (int i = 0; i < smMissions.size(); i++) {
            Mission mission = (Mission)smMissions.elementAt(i);
            if (mission.getID() == id) {
               return mission.isCompleted();
            }
         }
      }

      return true;
   }

   public static DChocByteArray save() {
      Profiler.startTimer(11);
      DChocByteArray data = DChocByteArray.createByteArrayForWriting(4096);
      data.writeUTF(SettingsWrapper.getVersion());
      data.writeShort(smMissions.size());

      for (int i = 0; i < smMissions.size(); i++) {
         Mission mission = (Mission)smMissions.elementAt(i);
         data.writeShort(mission.getID());
         mission.save(data);
      }

      data.writeByte(smAchievements.size());

      for (int i = 0; i < smAchievements.size(); i++) {
         Achievement achievement = (Achievement)smAchievements.elementAt(i);
         data.writeShort(achievement.getID());
         achievement.save(data);
      }

      data.writeInt(smKilledZombies);
      data.writeShort(smDelayedMissionSetups.size());

      for (int i = 0; i < smDelayedMissionSetups.size(); i++) {
         data.writeShort(((MissionSetup)smDelayedMissionSetups.elementAt(i)).getID());
      }

      Vector spawnings = SpawningManager.getActiveSpawnings();
      data.writeShort(spawnings.size());

      for (int i = 0; i < spawnings.size(); i++) {
         SpawningDefinition spawning = (SpawningDefinition)spawnings.elementAt(i);
         data.writeShort(spawning.getID());
         ToolkitHelpers.writeLong(spawning.getTimestamp(), data);
      }

      data.writeShort(smNoResetObjectives.size());

      for (int i = 0; i < smNoResetObjectives.size(); i++) {
         MissionObjective objective = (MissionObjective)smNoResetObjectives.elementAt(i);
         data.writeShort(objective.getID());
         objective.save(data);
      }

      ToolkitHelpers.writeLong(smCraftTime, data);
      ToolkitHelpers.writeLong(smHarvestTime, data);
      ToolkitHelpers.writeLong(smUpgradeTime, data);
      ToolkitHelpers.writeLong(smClearTime, data);
      ToolkitHelpers.writeLong(smSpendTime, data);
      ToolkitHelpers.writeLong(smCustomizeTime, data);
      ToolkitHelpers.writeLong(smRepairTime, data);
      Profiler.stopTimer(11);
      return data;
   }

   public static boolean load(DChocByteArray data) {
      smLoadIncomplete = true;
      if (data == null) {
         return false;
      }

      Profiler.startTimer(10);

      try {
         int version = SettingsWrapper.parseVersion(data.readUTF());
         int count = data.readShort();

         for (int i = 0; i < count; i++) {
            int id = data.readShort();
            Mission mission = getMission(id);
            if (id == -1 || mission == null) {
               throw new Exception("Invalid mission ID " + id);
            }

            mission.load(data, version);
            Fixes.fixMission(mission);
            if (mission.getType() == 2399 && mission.isCompleted() && mission.isNew()) {
               HUD.increateTipsCounter();
            }
         }

         int var9 = data.readByte();

         for (int i = 0; i < var9; i++) {
            int id = data.readShort();
            Achievement achievement = id == -1 ? null : getAchievement(id);
            if (achievement == null) {
               throw new Exception("Invalid achievement ID " + id);
            }

            achievement.load(data);
         }

         smKilledZombies = data.readInt();
         var9 = data.readShort();

         for (int i = 0; i < var9; i++) {
            MissionSetup setup = getMissionSetup(data.readShort());
            if (setup != null) {
               smDelayedMissionSetups.addElement(setup);
            }
         }

         var9 = data.readShort();

         for (int i = 0; i < var9; i++) {
            int id = data.readShort();
            long timestamp = Fixes.fixTimestamp(ToolkitHelpers.readLong(data));
            SpawningDefinition spawning = SpawningManager.getSpawning(id);
            if (spawning != null) {
               spawning.setTimestamp(timestamp);
            }
         }

         var9 = data.readShort();

         for (int i = 0; i < var9; i++) {
            int id = data.readShort();
            MissionObjective objective = null;
            if (id != -1) {
               for (int j = 0; j < smObjectives.size(); j++) {
                  MissionObjective candidate = (MissionObjective)smObjectives.elementAt(j);
                  if (candidate.getID() == id) {
                     objective = candidate;
                     break;
                  }
               }
            }

            if (objective == null) {
               throw new Exception("Invalid objective ID " + id);
            }

            objective.load(data);
         }

         if (version > 125) {
            smCraftTime = ToolkitHelpers.readLong(data);
            smHarvestTime = ToolkitHelpers.readLong(data);
            smUpgradeTime = ToolkitHelpers.readLong(data);
            smClearTime = ToolkitHelpers.readLong(data);
            smSpendTime = ToolkitHelpers.readLong(data);
            smCustomizeTime = ToolkitHelpers.readLong(data);
            if (version > 128) {
               smRepairTime = ToolkitHelpers.readLong(data);
            }
         }
      } catch (Exception exception) {
         new ErrorEvent().trackLoadError("MissionManager.load", exception.getMessage(), null);
         Profiler.stopTimer(10);
         return false;
      }

      Profiler.stopTimer(10);
      smLoadIncomplete = false;
      return true;
   }

   public static boolean isLoadIncomplete() {
      return smLoadIncomplete;
   }

   public static Vector getMissionSetups() {
      return smMissionSetups;
   }

   public static Vector getMissionEvents() {
      return smMissionEvents;
   }

   public static Vector getAchivements() {
      return smAchievements;
   }

   public static Vector getActiveMissions() {
      return smActiveMissions;
   }

   public static Vector getSurvivalTips() {
      return smSurvivalTips;
   }

   public static long getCraftTime() {
      return smCraftTime;
   }

   public static long getHarvestTime() {
      return smHarvestTime;
   }

   public static long getUpgradeTime() {
      return smUpgradeTime;
   }

   public static long getClearTime() {
      return smClearTime;
   }

   public static long getSpendTime() {
      return smSpendTime;
   }

   public static long getCustomizeTime() {
      return smCustomizeTime;
   }

   public static long getRepairTime() {
      return smRepairTime;
   }
}
