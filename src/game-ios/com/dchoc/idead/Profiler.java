package com.dchoc.idead;

public class Profiler {
   public static final int TIMER_ANIM_MANAGER = 24;
   public static final int TIMER_AVATAR = 6;
   public static final int TIMER_CSVS = 1;
   public static final int TIMER_EVENT_MANAGER = 19;
   public static final int TIMER_GAMEFIELD = 5;
   public static final int TIMER_GAMEFIELD_LOAD_CSV = 13;
   public static final int TIMER_GAMEFIELD_LOAD_RMS = 14;
   public static final int TIMER_GC = 38;
   public static final int TIMER_GET_BYTE_ARRAY = 26;
   public static final int TIMER_HUD_INIT = 16;
   public static final int TIMER_HUD_INIT_BUBBLE = 34;
   public static final int TIMER_HUD_INIT_COMPONENTS = 30;
   public static final int TIMER_HUD_INIT_COUNTERS = 28;
   public static final int TIMER_HUD_INIT_FONTS = 27;
   public static final int TIMER_HUD_INIT_MISSIONS = 32;
   public static final int TIMER_HUD_INIT_PAPERDOLL = 36;
   public static final int TIMER_HUD_INIT_PROGRESSBARS = 29;
   public static final int TIMER_HUD_INIT_STATE = 37;
   public static final int TIMER_HUD_INIT_STRIP = 31;
   public static final int TIMER_HUD_INIT_TOOLTIP = 35;
   public static final int TIMER_HUD_INIT_WINDOWS = 33;
   public static final int TIMER_ITEMS = 3;
   public static final int TIMER_ITEM_MANAGER = 23;
   public static final int TIMER_LOADING = 4;
   public static final int TIMER_LOAD_FILE = 25;
   public static final int TIMER_MISSIONMANAGER = 12;
   public static final int TIMER_MISSION_MANAGER = 17;
   public static final int TIMER_NEW_PROFILE = 21;
   public static final int TIMER_PAYMENTS = 11;
   public static final int TIMER_PLAYERPROFILE = 22;
   public static final int TIMER_ROBS = 9;
   public static final int TIMER_SCENELOADER_INIT = 15;
   public static final int TIMER_SCENE_INIT = 20;
   public static final int TIMER_SOCIAL = 8;
   public static final int TIMER_SPAWNING_MANAGER = 18;
   public static final int TIMER_SPRITEOBJECTS = 0;
   public static final int TIMER_TEXTURES = 2;
   public static final int TIMER_TUNER = 7;
   public static final int TIMER_TUTORIALFLOW = 10;
   private static final int AMOUNT_TIMERS = 28;
   private static final int TYPE_LOADING_STEP = 0;
   private static final int TYPE_RESOURCES = 1;
   private static final int TYPE_LOW_LEVEL = 2;
   private static final int TYPE_CREATING = 3;
   private static final String[] TYPE_NAMES = new String[]{
      "============= Loading =============",
      "============ Resources ============",
      "============ Low level ============",
      "============ Creating ============="
   };
   private static String[] TIMER_NAMES = new String[]{
      "Profiler: CreateAnimations: ",
      "Profiler: LoadTextures: ",
      "Profiler: GetByteArrays ",
      "Profiler: LoadFiles: ",
      "Profiler: GarbageCollector: ",
      "Profiler: GarbageCollectorMalloc: ",
      "Profiler: Base64Decode: ",
      "Profiler: Base64Encode: ",
      "Profiler: LoadScene: ",
      "Profiler: SaveScene: ",
      "Profiler: LoadMissions: ",
      "Profiler: SaveMissions: ",
      "Profiler: LoadProfile: ",
      "Profiler: SaveProfile: ",
      "Profiler: LoadNeighborData: ",
      "Profiler: UpdateObjectPool: ",
      "Profiler: CreateConstruction: ",
      "Profiler: CreatePlayer: ",
      "Profiler: CreateZombie: ",
      "Profiler: CreateLoot: ",
      "Profiler: CreateDecoration: ",
      "Profiler: CreateFence: ",
      "Profiler: CreateFarming: ",
      "Profiler: CreateNeighbor: ",
      "Profiler: CreateNpc: ",
      "Profiler: LoadItemAnimations: ",
      "Profiler: UnloadItemAnimations: ",
      "Profiler: LoadItemCollisions: "
   };
   private static int[] TIMER_TYPES = new int[]{1, 1, 1, 1, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1};
   private static int[] smTimers = new int[28];
   private static long[] smStartTime = new long[28];
   private static int[] smTimerCounters = new int[28];
   private static int[] smTimerCallCount = new int[28];

   public static void startTimer(int timer) {
   }

   public static void stopTimer(int timer) {
   }
}
