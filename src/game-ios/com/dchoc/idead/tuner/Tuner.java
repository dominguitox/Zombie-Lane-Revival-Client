package com.dchoc.idead.tuner;

import com.dchoc.collection.HashTable;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ResourceIDs;
import com.dchoc.toolkit.Sonda;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import java.util.Vector;

public class Tuner implements ResourceIDs, CsvIDs {
   public static final int INITIAL_LEVELS_CAPACITY = 192;
   public static final int INITIAL_COMBOS_CAPACITY = 16;
   public static final int MINUTES_TO_MILLISECONDS = 60000;
   public static final int ACTION_COST_ATTACK = 1;
   public static final int ACTION_COST_CONSTRUCT = 1;
   public static final int ACTION_COST_FARM = 1;
   public static final int ACTION_COST_LOOT = 1;
   public static final int ALERTED_COLOR = -65536;
   public static final float ALERTED_OUTLINE_SIZE = 5.0F;
   public static final int ATTACK_FENCE_PROBABILITY = 40;
   public static final int BUBBLE_AND_BAR_MARGIN = 5;
   public static final float CAMERA_BOUNCE_SPEED = 0.3F;
   public static final int CAMERA_FLY_SPEED_MIN = 500;
   public static final int CONNECTION_RETRY_TIME = 45000;
   public static final int CROP_DESTRUCTION_TIMER = 3600000;
   public static final long DAYS_TO_MILLISECONDS = 86400000L;
   public static final long DAY_TO_MINUTES = 1440L;
   public static final boolean DEBUG_ADD_SAVE_RESTORE_BACKUP_SUPPORT = false;
   public static final boolean DEBUG_LOADING_ISSUE = false;
   public static final boolean DEBUG_OUTPUT_LOADED_MISSION_FILE = false;
   public static final boolean DEBUG_OUTPUT_LOADED_SCENE_FILE = false;
   public static final boolean DEBUG_SERVLET_PARAMETERS = false;
   public static final boolean DEBUG_SERVLET_SIGNATURES = false;
   public static final boolean DEBUG_SUPPRESS_AZL237_DETECTION = false;
   public static final boolean ENABLE_DEBUG = false;
   public static final boolean ENABLE_DEBUG_DRAW = false;
   public static final boolean ENABLE_EDITOR = false;
   public static final boolean ENABLE_FPS = false;
   public static final boolean ENABLE_HEALTH_BAR = true;
   public static final boolean ENABLE_LOGGING = false;
   public static final boolean ENABLE_TAPPED_TOOLTIP = false;
   public static final boolean ENABLE_TOOLTIP = true;
   public static final boolean ENALBE_DEBUG_WEAPONS = false;
   public static final int FENCE_BREAKING_ZOMBIES_PERCENTAGE = 40;
   public static final int FENCE_DAILY_DAMAGE_MAX = 20;
   public static final int FENCE_DAMAGE_MAX = 2;
   public static final int FENCE_OVERALL_DAMAGE_MAX = 45;
   public static final int FPS_SAMPLE_TIME = 1000;
   public static final int HIGHLIGHT_COLOR = -1151311776;
   public static final int HIGHLIGHT_COLOR_GREEN = -1151271072;
   public static final int HIGHLIGHT_COLOR_RED = -1140891552;
   public static final int HIGHLIGHT_START_TIME = 200;
   public static final int HIRE_COST = 200;
   public static final long HOUR_TO_MILLISECONDS = 3600000L;
   public static final long HOUR_TO_MINUTES = 60L;
   public static final int HUD_BUTTON_TARGET_MARGIN_X = 0;
   public static final int HUD_BUTTON_TARGET_MARGIN_Y = 0;
   public static final int INVITE_RETRY_TIME = 30000;
   public static final int ITEM_LAYERS_COUNT = 3;
   public static final int ITEM_LAYER_FLOOR = 0;
   public static final int ITEM_LAYER_GROUND = 1;
   public static final int ITEM_LAYER_OBJECTS = 2;
   public static final float LOAD_MASK_ALPHA = 0.7F;
   public static final int LOAD_MASK_COLOR = 14671839;
   public static final float LOOT_REWARD_BOUNCE_MAX = 150.0F;
   public static final float LOOT_REWARD_BOUNCE_MIN = 75.0F;
   public static final int LOOT_REWARD_FLY_TIME = 750;
   public static final int LOOT_REWARD_MOVE_TIME_MAX = 1000;
   public static final int LOOT_REWARD_MOVE_TIME_MIN = 700;
   public static final int LOOT_REWARD_ON_SCREEN_TIME = 10000;
   public static final float MAP_BORDER_HEIGHT = 150.0F;
   public static final float MAP_BORDER_WIDTH = 100.0F;
   public static final int MAP_HEIGHT = 50;
   public static final int MAP_WIDTH = 50;
   public static final int MEMORY_SAVE_TIME = 150000;
   public static final int MILLISECS_PER_HOME_SCENE_VALIDATION = 5000;
   public static final long MINUTES_TO_SECONDS = 60L;
   public static final long NEIGHBOR_ACTION_VALID_UNTIL = 86400000L;
   public static final long NEIGHBOR_VISIT_TIME = 86400000L;
   public static final int PLAYER_ACTIONS_MAX = 5;
   public static final int PLAYER_ENERGY_INITIAL = 20;
   public static final int PLAYER_NEIGHBOR_ACTIONS_CHARGE_TIME = 86400000;
   public static final int PLAYER_NEIGHBOR_ACTIONS_MAX = 5;
   public static final int PlAYER_ENERGY_CHARGE = 1;
   public static final int PlAYER_ENERGY_CHARGE_TIME = 30000;
   public static final long SECONDS_TO_MILLISECONDS = 1000L;
   public static final int SPAWNING_COUNTER_MAX = 2;
   public static final float TEXT_SCALE_SEARCH_MARGIN = 0.2F;
   public static final float TEXT_SCALE_SEARCH_MIN = 0.2F;
   public static final float TILE_SCALE = 0.65F;
   public static final float TILE_SCALE_HD = 1.3F;
   public static final float TILE_SIZE = 48.0F;
   public static final float TILE_SIZE_HALF = 24.0F;
   public static final float TILE_SIZE_HALF_HD = 48.0F;
   public static final float TILE_SIZE_HD = 96.0F;
   public static final int TOOLTIP_BAR_CHANGE_TIME = 300;
   public static final int TOOLTIP_BAR_CLOSE_PAUSE = 1000;
   public static final int TOOLTIP_BAR_SCALE_TIME = 250;
   public static final boolean TOOLTIP_BAR_STRAIGT = true;
   public static final int TOOLTIP_BAR_WIDTH_CONSTRUCTION = 70;
   public static final int TOOLTIP_BAR_WIDTH_FENCE = 70;
   public static final int TOOLTIP_BAR_WIDTH_LOOT = 70;
   public static final int TOOLTIP_BAR_WIDTH_ZOMBIE_NORMAL = 65;
   public static final int TOOLTIP_BAR_WIDTH_ZOMBIE_STRONG = 80;
   public static final int TOOLTIP_BAR_WIDTH_ZOMBIE_WEAK = 50;
   public static final int TOOLTIP_BUBBLE_CLOSE_PAUSE = 1000;
   public static final int TOOLTIP_BUBBLE_SCALE_TIME = 250;
   public static final int TOOLTIP_MARGIN_X = 10;
   public static final int TOOLTIP_MARGIN_Y = 10;
   public static final int TOOLTIP_PIVOT_X = 40;
   public static final int TOOLTIP_PIVOT_Y = 40;
   public static final int TOUCH_HOLD_ACTION_TIME = 1000;
   public static final int TOUCH_HOLD_TIME = 500;
   public static final int TRANSPARENT_COLOR = 1147166816;
   public static final int TRANSPARENT_SEARCH_MAX_TILES = 2;
   public static final int TRANSPARENT_SEARCH_START_TILE = 1;
   public static final int ZOMBIE_ALERT_DISTANCE = 3;
   public static final int ZOMBIE_ATTACK_RANGE = 1;
   public static final int ZOMBIE_ATTACK_TIME_MAX = 10000;
   public static final int ZOMBIE_ATTACK_TIME_MIN = 5000;
   public static final int ZOMBIE_BOSS_ATTACK_RANGE = 2;
   public static final int ZOMBIE_DANGER_DISTANCE = 4;
   public static final int ZOMBIE_MOVE_TIME_MAX = 5000;
   public static final int ZOMBIE_MOVE_TIME_MIN = 2000;
   public static final int ZOMBIE_SAFE_DISTANCE = 8;
   public static final float ZOOM_INITIAL = 1.0F;
   public static final float ZOOM_STEP = 0.02F;
   public static Object EDITOR_MISSION_SETUPS = null;
   private static boolean smInitialized = false;
   private static float smPlayerSpeed;
   private static float smCharacterSpeed;
   private static float smZombieSpeed;
   private static byte smMaxLevels;
   private static byte smMaxDailyRewards;
   private static byte smInitialAreaWidth;
   private static byte smInitialAreaHeight;
   private static byte smSellValuePercentage;
   private static byte smCashToCoinsRate;
   private static byte smLevelUpPoints;
   private static byte smMaxAttackPercentagePoints;
   private static byte smMaxWishListItems;
   private static short smTutorialCompleteMission;
   private static short smMissionToUnlockVisitRob;
   private static short smMissionToUnlockFences;
   private static byte smDefaultSpawningInterval;
   private static byte smLevelToUnlockAchievements;
   private static boolean smDecreaseAmmoAtNeighbor;
   private static short smMissionToUnlockBuildingParts;
   private static short smMissionToUnlockCrafting;
   private static short smMissionToUnlockTutorHelping;
   private static short smMissionToUnlockDailyBonus;
   private static short smMissionToUnlockBuildingUpgrade;
   private static short smMissionToUnlockSpouse;
   private static short smMissionToUnlockDog;
   private static byte smMaxFollowing;
   private static byte smDangerRange;
   private static byte smSafeRange;
   private static byte smTimeUntilSafe;
   private static byte smProbabilityToFollow;
   private static byte smProbabilityToLikeFences;
   private static byte smProbabilityToStartAtFence;
   private static byte smMaxDailyNotifications;
   private static int smFromTime;
   private static int smToTime;
   private static int[] smGift0;
   private static int smGift1;
   private static int smGift2;
   private static int smGift3;
   private static HashTable smLevels;
   private static HashTable smCombos;
   private static AreaSetup smHomeArea;
   private static AreaSetup smTutorArea;
   private static AreaSetup smNeighborArea;
   private static Vector smAdventureAreas;
   private static Vector smEdgeAreas;

   public static void init() {
      smPlayerSpeed = 225.0F;
      smCharacterSpeed = 225.0F;
      smZombieSpeed = 37.5F;
      DChocByteArray data = Toolkit.getResourceByteArray(80410);

      try {
         smMaxLevels = data.readByte();
         smMaxDailyRewards = data.readByte();
         smInitialAreaWidth = data.readByte();
         smInitialAreaHeight = data.readByte();
         smSellValuePercentage = data.readByte();
         smCashToCoinsRate = data.readByte();
         smLevelUpPoints = data.readByte();
         smMaxAttackPercentagePoints = data.readByte();
         smMaxWishListItems = data.readByte();
         smTutorialCompleteMission = data.readShort();
         smMissionToUnlockVisitRob = data.readShort();
         smMissionToUnlockFences = data.readShort();
         smDefaultSpawningInterval = data.readByte();
         smLevelToUnlockAchievements = data.readByte();
         smDecreaseAmmoAtNeighbor = data.readBoolean();
         smMissionToUnlockBuildingParts = data.readShort();
         smMissionToUnlockCrafting = data.readShort();
         smMissionToUnlockTutorHelping = data.readShort();
         smMissionToUnlockDailyBonus = data.readShort();
         smMissionToUnlockBuildingUpgrade = data.readShort();
         data.readByte();
         data.readByte();
         smMissionToUnlockSpouse = data.readShort();
         smMissionToUnlockDog = data.readShort();
      } catch (Exception exception) {
         Sonda.probe("Tuner.parse (recurso 804xx)", exception);
      }

      data = Toolkit.getResourceByteArray(80432);

      try {
         smMaxFollowing = data.readByte();
         smDangerRange = data.readByte();
         smSafeRange = data.readByte();
         smTimeUntilSafe = data.readByte();
         smProbabilityToFollow = data.readByte();
         smProbabilityToLikeFences = data.readByte();
         smProbabilityToStartAtFence = data.readByte();
      } catch (Exception exception) {
         Sonda.probe("Tuner.parse (recurso 80432)", exception);
      }

      data = Toolkit.getResourceByteArray(80425);

      try {
         smMaxDailyNotifications = data.readByte();
         smFromTime = data.readInt() * 60000;
         smToTime = data.readInt() * 60000;
      } catch (Exception exception) {
         Sonda.probe("Tuner.parse (recurso 80425)", exception);
      }

      data = Toolkit.getResourceByteArray(80422);

      try {
         smGift0 = ToolkitHelpers.readIntArray(data);
         smGift1 = data.readInt();
         smGift2 = data.readInt();
         smGift3 = data.readInt();
      } catch (Exception exception) {
         Sonda.probe("Tuner.parse (recurso 80422)", exception);
      }

      smLevels = new HashTable(192);
      data = Toolkit.getResourceByteArray(80433);

      try {
         while (true) {
            LevelDefinition level = new LevelDefinition(data);
            smLevels.add(level.getLevel(), level);
         }
      } catch (Exception exception) {
         Sonda.probeNonEof("Tuner.levels 80433", exception);
         smCombos = new HashTable(16);
         data = Toolkit.getResourceByteArray(80431);

         try {
            while (true) {
               ComboDefinition combo = new ComboDefinition(data);
               smCombos.add(combo.getLevel(), combo);
            }
         } catch (Exception exceptionx) {
            Sonda.probeNonEof("Tuner.combos 80431", exceptionx);
            data = Toolkit.getResourceByteArray(80413);

            try {
               smHomeArea = new AreaSetup(data);
               smTutorArea = new AreaSetup(data);
               smNeighborArea = new AreaSetup(data);
            } catch (Exception exceptionxx) {
               Sonda.probe("Tuner.fixed areas 80413", exceptionxx);
            }

            smAdventureAreas = new Vector(4);
            data = Toolkit.getResourceByteArray(80414);

            try {
               while (true) {
                  smAdventureAreas.addElement(new AreaSetup(data));
               }
            } catch (Exception exceptionxx) {
               Sonda.probeNonEof("Tuner.adventure areas 80414", exceptionxx);
               smEdgeAreas = new Vector(10);
               data = Toolkit.getResourceByteArray(80415);

               try {
                  while (true) {
                     smEdgeAreas.addElement(new EdgeAreaDefinition(data));
                  }
               } catch (Exception exceptionxxx) {
                  Sonda.probeNonEof("Tuner.edgeAreas 80415", exceptionxxx);
                  smInitialized = true;
               }
            }
         }
      }
   }

   public static int getSellValuePercentage() {
      return smSellValuePercentage;
   }

   public static int getCashToCoinsRate() {
      return smCashToCoinsRate;
   }

   public static int getMissionToUnlockVisitRob() {
      return smMissionToUnlockVisitRob;
   }

   public static int getLevelToUnlockAchievements() {
      return smLevelToUnlockAchievements;
   }

   public static int getMissionToUnlockCrafting() {
      return smMissionToUnlockCrafting;
   }

   public static int getMissionToUnlockBuildingParts() {
      return smMissionToUnlockBuildingParts;
   }

   public static int getMissionToUnlockBuildingUpgrade() {
      return smMissionToUnlockBuildingUpgrade;
   }

   public static int getMissionToUnlockSpouse() {
      return smMissionToUnlockSpouse;
   }

   public static int getMissionToUnlockDog() {
      return smMissionToUnlockDog;
   }

   public static int getMaxFollowing() {
      return smMaxFollowing;
   }

   public static int getDangerRange() {
      return smDangerRange;
   }

   public static int getSafeRange() {
      return smSafeRange;
   }

   public static int getProbabilityToFollow() {
      return smProbabilityToFollow;
   }

   public static int getProbabilityToLikeFences() {
      return smProbabilityToLikeFences;
   }

   public static int getMaxDailyNotifications() {
      return smMaxDailyNotifications;
   }

   public static int getFromTime() {
      return smFromTime;
   }

   public static int getToTime() {
      return smToTime;
   }

   public static int[] getGift0() {
      return smGift0;
   }

   public static int getGift1() {
      return smGift1;
   }

   public static int getGift2() {
      return smGift2;
   }

   public static int getGift3() {
      return smGift3;
   }

   public static LevelDefinition getLevel(int level) {
      return (LevelDefinition)smLevels.get(level);
   }

   public static LevelDefinition getNextLevel(int level) {
      return (LevelDefinition)smLevels.get(level + 1);
   }

   public static ComboDefinition getNextCombo(int level) {
      return (ComboDefinition)smCombos.get(level + 1);
   }

   public static AreaSetup getHomeArea() {
      return smHomeArea;
   }

   public static AreaSetup getTutorArea() {
      return smTutorArea;
   }

   public static AreaSetup getNeighborArea() {
      return smNeighborArea;
   }

   public static AreaSetup getAdventureArea(int name) {
      for (int i = 0; i < smAdventureAreas.size(); i++) {
         AreaSetup area = (AreaSetup)smAdventureAreas.elementAt(i);
         if (area.getName() == name) {
            return area;
         }
      }

      return null;
   }

   public static Vector getEdgeAreas() {
      return smEdgeAreas;
   }

   public static void setPlayerSpeed(float speed) {
      smPlayerSpeed = speed;
   }

   public static float getPlayerSpeed(boolean hd) {
      float speed = smPlayerSpeed;
      if (hd) {
         speed *= DeviceWrapper.useHDIngameGraphics() ? 2.0F : 1.0F;
      }

      return speed;
   }

   public static float getCharacterSpeed() {
      return smCharacterSpeed * (DeviceWrapper.useHDIngameGraphics() ? 2.0F : 1.0F);
   }

   public static float getZombieSpeed() {
      return smZombieSpeed * (DeviceWrapper.useHDIngameGraphics() ? 2.0F : 1.0F);
   }

   public static float getMinZoom() {
      if (DeviceWrapper.isVGA()) {
         return 0.75F;
      } else if (DeviceWrapper.useHDGraphics()) {
         return DeviceWrapper.useHDIngameGraphics() ? 0.5F : 1.0F;
      } else {
         return 0.5F;
      }
   }

   public static float getMaxZoom() {
      if (DeviceWrapper.isVGA()) {
         return 1.5F;
      } else if (!DeviceWrapper.useHDGraphics()) {
         return 1.0F;
      } else if (!DeviceWrapper.useHDIngameGraphics()) {
         return 2.0F;
      } else {
         return DeviceWrapper.isIPAD() ? 1.0F : 2.0F;
      }
   }

   public static float getInitialZoom() {
      if (DeviceWrapper.isVGA()) {
         return 1.5F;
      } else {
         return DeviceWrapper.isIPOD4() ? 2.0F : 1.0F;
      }
   }
}
