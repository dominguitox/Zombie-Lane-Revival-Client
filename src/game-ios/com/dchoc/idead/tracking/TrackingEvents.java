package com.dchoc.idead.tracking;

public class TrackingEvents {
   public static final int EVENT_NONE = -1;
   public static final int EVENT_APP_INSTALLED = 0;
   public static final int EVENT_SESSION_STARTED = 1;
   public static final int EVENT_STARTED = 2;
   public static final int EVENT_CONFIRMED = 3;
   public static final int EVENT_CANCELLED = 4;
   public static final int EVENT_SENT = 5;
   public static final int EVENT_FRIEND = 6;
   public static final int EVENT_VISIT = 7;
   public static final int EVENT_COMPLETED = 8;
   public static final int EVENT_OPENED = 9;
   public static final int EVENT_ERROR = 10;
   public static final int EVENT_EXPIRED = 11;
   public static final int EVENT_BUY_MISSION_STEP = 12;
   public static final int EVENT_SHOWN = 13;
   public static final int EVENT_SKIPPED = 14;
   public static final int EVENT_BUY = 15;
   public static final int EVENT_CLOSED = 16;
   public static final int EVENT_OBJECTIVE_COMPLETED = 17;
   public static final int EVENT_PRESSED = 18;
   public static final int EVENT_SWITCH_TAB = 19;
   public static final int EVENT_SELL_ITEM = 20;
   public static final int EVENT_ATTACK_ZOMBIE = 21;
   public static final int EVENT_HARVEST_CROPS = 22;
   public static final int EVENT_FENCE_REPAIR = 23;
   public static final int EVENT_CLEAR_OBJECT = 24;
   public static final int EVENT_BUILD_OBJECT = 25;
   public static final int EVENT_DAILY_REWARD = 26;
   public static final int EVENT_GET_FRIENDS = 27;
   public static final int EVENT_GET_APPFRIENDS = 28;
   public static final int EVENT_POST_TO_FEED = 29;
   public static final int EVENT_DEFAULT = 30;
   public static final int EVENT_ACCEPT = 31;
   public static final int EVENT_DECLINE = 32;
   public static final int EVENT_TUTOR = 33;
   public static final int EVENT_LOADED = 34;
   public static final int EVENT_CRASHED = 35;
   public static final int EVENT_COMBO = 36;
   public static final int EVENT_GIFT = 37;
   public static final int EVENT_ADD_FRIEND = 51;
   public static final int EVENT_QUICK_RIPEN = 52;
   public static final int EVENT_VALIDATION_FAILED = 53;
   public static final int EVENT_VALIDATION_OK = 54;
   public static final int EVENT_VALIDATION_ERROR = 55;
   public static final int GROUP_GAME = 0;
   public static final int GROUP_LEVEL = 1;
   public static final int GROUP_ECONOMY = 2;
   public static final int GROUP_SOCIAL = 3;
   public static final int GROUP_GAME_ERROR = 4;
   public static final int TYPE_APP_INSTALLED = 0;
   public static final int TYPE_SESSION_STARTED = 1;
   public static final int TYPE_SPEND_GC = 2;
   public static final int TYPE_SPEND_PC = 3;
   public static final int TYPE_EARN_GC = 4;
   public static final int TYPE_EARN_PC = 5;
   public static final int TYPE_LEVEL_UP = 6;
   public static final int TYPE_FEED = 7;
   public static final int TYPE_GIFT = 8;
   public static final int TYPE_HIRE_FRIEND = 9;
   public static final int TYPE_MISSION = 10;
   public static final int TYPE_MENU = 11;
   public static final int TYPE_TUTORIAL = 12;
   public static final int TYPE_PURCHASE_PC = 13;
   public static final int TYPE_PURCHASE_GC = 14;
   public static final int TYPE_POPUP = 15;
   public static final int TYPE_SPEND_FOOD = 16;
   public static final int TYPE_CONSUME = 17;
   public static final int TYPE_SURVIVAL = 18;
   public static final int TYPE_WINDOW = 19;
   public static final int TYPE_BUTTON = 20;
   public static final int TYPE_SPEND_ENERGY = 21;
   public static final int TYPE_SNS_LOGIN = 22;
   public static final int TYPE_SNS_FEED_POST = 23;
   public static final int TYPE_FRIEND_ACTION = 24;
   public static final int TYPE_VISIT_FRIEND = 25;
   public static final int TYPE_VISIT_NEIGHBOR = 26;
   public static final int TYPE_SESSION_CRASHED = 27;
   public static final int TYPE_LOADING_TIME = 28;
   public static final int TYPE_LOADING_TIME_RESUME = 29;
   public static final int TYPE_LOADING_TIME_HOME = 30;
   public static final int TYPE_LOADING_TIME_NEIGHBOR = 31;
   public static final int TYPE_LOADING_TIME_CIRCUS = 32;
   public static final int TYPE_LOADING_TIME_DOWNTOWN = 33;
   public static final int TYPE_LOADING_TIME_CRASHSITE = 34;
   public static final int TYPE_LOADING_TIME_CURE = 35;
   public static final int TYPE_AB_TEST = 36;
   public static final int TYPE_FRIEND_CODE = 37;
   public static final int TYPE_GENERAL_ERROR = 38;
   public static final int TYPE_DAVINCI_ERROR = 39;
   public static final int TYPE_BUNDLE_ERROR = 40;
   public static final int TYPE_LOAD_ERROR = 41;
   public static final int TYPE_FATAL_ERROR = 42;
   public static final int TYPE_PUSH_NOTIF = 43;
   public static final int TYPE_VISIT_CIRCUS = 44;
   public static final int TYPE_VISIT_DOWNTOWN = 45;
   public static final int TYPE_VISIT_CRASHSITE = 46;
   public static final int TYPE_VISIT_CURE = 47;
   public static final int TYPE_JAILBROKEN = 48;
   public static final int TYPE_PLAYHAVEN = 49;
   public static final int TYPE_STARTER_PACK1 = 50;
   static final String[] GROUPS = new String[]{"Game", "Level", "Economy", "Social", "Game Error"};
   static final String[] TYPES = new String[]{
      "App Installed",
      "Session Started",
      "Spend GC",
      "Spend PC",
      "Earn GC",
      "Earn PC",
      "Level Up",
      "Feed",
      "Gift",
      "Hire Friend",
      "Mission",
      "Menu",
      "Play Tutorial",
      "Purchase PC",
      "Purchase GC",
      "Pop Up",
      "Spend Food",
      "Consume",
      "Survival Tip",
      "Window",
      "Button",
      "Spend Energy",
      "SNS Login",
      "SNS Feed Post",
      "Friend Action",
      "Visit Friend",
      "Visit Neighbor",
      "Session Crashed",
      "Loading Time",
      "Loading Time Resume",
      "Loading Time Home",
      "Loading Time Neighbor",
      "Loading Time Circus",
      "Loading Time Downtown",
      "Loading Time Crashsite",
      "Loading Time Cure",
      "AB Test",
      "Friend Code",
      "General Error",
      "Davinci Error",
      "Bundle Error",
      "Load Error",
      "Fatal Error",
      "Push Nofif",
      "Visit Circus",
      "Visit Downtown",
      "Visit CrashSite",
      "Visit Cure",
      "Jailbroken",
      "PlayHaven",
      "StarterPack1"
   };
   static final String[] TYPES_SHORT = new String[]{
      "AppInstalled",
      "SessionStarted",
      "SpendGC",
      "SpendPC",
      "EarnGC",
      "EarnPC",
      "LevelUp",
      "Feed",
      "Gift",
      "HireFriend",
      "Missn",
      "Menu",
      "PlayTutorial",
      "PurchasePC",
      "PurchaseGC",
      "PopUp",
      "SpendFood",
      "Consume",
      "SurvivalTip",
      "Window",
      "Button",
      "SpendEnergy",
      "SNSLogin",
      "SNSFeedPost",
      "FriendAction",
      "VisitFriend",
      "VisitNeighbor",
      "SessionCrashed",
      "LoadingTime",
      "LoadingTimeResume",
      "LoadingTimeHome",
      "LoadingTimeNeighbor",
      "LoadingTimeCircus",
      "LoadingTimeDowntown",
      "LoadingTimeCrashsite",
      "LoadingTimeCure",
      "A/B Test",
      "FriendCode",
      "GeneralError",
      "DavinciError",
      "BundleError",
      "LoadError",
      "FatalError",
      "PushNotif",
      "VisitCircus",
      "VisitDowntown",
      "VisitCrashSite",
      "VisitCure",
      "Jailbroken",
      "PlayHaven",
      null
   };
   static final String[] HIGH_LEVEL_TYPES_APSALAR = new String[]{
      "App Installed", "Spend GC", "Spend PC", "Spend Food", "Spend Energy", "Hire Friend", "Friend Action"
   };
   static final String[] ACTIONS = new String[]{
      "Installed",
      "OnClient",
      "Started",
      "Confirm",
      "Cancel",
      "Sent",
      "Friend",
      "Visit",
      "Completed",
      "Opened",
      "Error",
      "Expired",
      "Buy Mission Step",
      "Shown",
      "Skipped",
      "Buy",
      "Closed",
      "Objective Completed",
      "Pressed",
      "Switched Tab",
      "Sell item",
      "Attack Zombie",
      "Harvest Crop",
      "Fence Repair",
      "Clear Object",
      "Build Object",
      "Daily Reward",
      "Get Friends",
      "Get App Friends",
      "Post to Feed",
      "Default",
      "Accept",
      "Decline",
      "Tutor",
      "Loaded",
      "Crashed",
      "Combo",
      "Gift",
      "0-10",
      "10-20",
      "20-30",
      "30-60",
      "60-90",
      "90-120",
      "120-180",
      "180-300",
      "300+",
      "more_speed",
      "15_cash",
      "1min_energy",
      "control_group",
      "Add Friend",
      "Quick Ripen",
      "validation_failed",
      "validation_OK",
      "validation_error",
      "Jailbroken"
   };
   static final String[] ACTIONS_SHORT = new String[]{
      "Installed",
      "OnClient",
      "Started",
      "Confirmed",
      "Cancelled",
      "Sent",
      "Friend",
      "Visit",
      "Completed",
      "Opened",
      "Error",
      "Expired",
      "BuyStep",
      "Shown",
      "Skipped",
      "Buy",
      "Closed",
      "ObjectCompl",
      "Pressed",
      "TabSwitch",
      "Sell",
      "Attack",
      "Harvest",
      "Repair",
      "Clear",
      "Build",
      "DailyReward",
      "Friends",
      "AppFriends",
      "PostToFeed",
      "Default",
      "Accept",
      "Decline",
      "Tutor",
      "Loaded",
      "Crashed",
      "Combo",
      "Gift",
      "0-10",
      "10-20",
      "20-30",
      "30-60",
      "60-90",
      "90-120",
      "120-180",
      "180-300",
      "300+",
      "more_speed",
      "15_cash",
      "1min_energy",
      "control_group",
      "AddFriend",
      "QuickRipen",
      "validation_failed",
      "validation_OK",
      "validation_error",
      "Jailbroken"
   };
   static final String[] REWARD_TYPES = new String[]{"item", "gift", "visit", "daily"};

   public static String getEventGroup(int index) {
      return GROUPS[index];
   }

   public static String getEventType(int index) {
      return TYPES[index];
   }

   public static String getRewardType(int index) {
      return REWARD_TYPES[index];
   }

   public static String getUserAction(int index) {
      return index == -1 ? null : ACTIONS[index];
   }

   public static int getLoadingTimeLabel(long millis) {
      if (millis <= 10000L) {
         return 38;
      } else if (millis <= 20000L) {
         return 39;
      } else if (millis <= 30000L) {
         return 40;
      } else if (millis <= 60000L) {
         return 41;
      } else if (millis <= 90000L) {
         return 42;
      } else if (millis <= 120000L) {
         return 43;
      } else if (millis <= 180000L) {
         return 44;
      } else {
         return millis <= 300000L ? 45 : 46;
      }
   }
}
