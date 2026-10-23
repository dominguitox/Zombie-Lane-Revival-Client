package com.dchoc.idead.social;

import com.dchoc.idead.GameEngine;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.player.Profile;
import com.dchoc.idead.servlets.BindPlatformAccount;
import com.dchoc.idead.tracking.SocialLoginEvent;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;
import zlport.ios.Ios;

public class SocialGameCenter {
   private static final int CSV_ACHIEVEMENT = 0;
   private static final int GC_ACHIEVEMENT = 1;
   private static final int[][] GC_SKU_MAPPING = new int[][]{
      {77, 81},
      {81, 85},
      {82, 86},
      {83, 87},
      {84, 88},
      {86, 90},
      {87, 91},
      {88, 92},
      {89, 93},
      {91, 95},
      {92, 96},
      {93, 97},
      {94, 98},
      {96, 100},
      {97, 101},
      {98, 102},
      {99, 103},
      {102, 106},
      {103, 107},
      {104, 108},
      {105, 109},
      {108, 112},
      {109, 113},
      {110, 114},
      {111, 115},
      {114, 118},
      {115, 119},
      {116, 120},
      {117, 121},
      {119, 122},
      {120, 123},
      {121, 124},
      {122, 126},
      {124, 128},
      {125, 129},
      {126, 130},
      {127, 131},
      {130, 134},
      {131, 135},
      {132, 136}
   };
   public static final int INITIAL_FRIENDS_CAPACITY = 16;
   private static final int INVALID_ACHIEVEMENT = -1;
   private static Vector smAppFriends;
   private static SocialLoginEvent smTracking;
   private static ISocialListener smListener;
   private boolean mBindPlatformAccount;
   private boolean mUpdatePlayerName;
   private String mName;
   private String mUserID;
   private boolean mLoginTracked;

   public SocialGameCenter() {
      smAppFriends = new Vector(16);
      smTracking = new SocialLoginEvent(0);
      this.createManager();
      smListener = null;
      this.mName = null;
      this.mUserID = null;
      this.mBindPlatformAccount = true;
      this.mUpdatePlayerName = true;
      this.mLoginTracked = false;
   }

   public int logicUpdate(int dt) {
      if (GameEngine.getInstance().isConnected() && this.isConnected() && this.mBindPlatformAccount) {
         new BindPlatformAccount("6", this.getUserID(), this.getName()).execute();
         this.mBindPlatformAccount = false;
      }

      if (this.isConnected() && PlayerProfile.isInitialized() && this.mUpdatePlayerName) {
         Profile profile = PlayerProfile.getProfile();
         String current = profile.getName();
         String name = this.getName();
         if (name != null && current != null && (current.equals("Player") || current.equals("Neighbor"))) {
            profile.setName(name);
         }

         this.mUpdatePlayerName = false;
         if (!this.mLoginTracked) {
            smTracking.trackCompleted(this.getUserID());
            this.mLoginTracked = true;
         }
      }

      return 0;
   }

   public void setListener(ISocialListener listener) {
      smListener = listener;
   }

   public void dummyMethod() {
   }

   public void connect() {
      this.mName = null;
      this.mUserID = null;
      if (this.isConnected()) {
         if (smListener != null) {
            smListener.connect(0, 0);
         }
      } else {
         if (this.isAvailable()) {
            Ios.platform().authenticateGameCenterUser();
         }

         smTracking.trackStarted();
      }
   }

   public void getAppFriends() {
      smAppFriends.removeAllElements();
      if (this.isAvailable()) {
         if (Ios.platform().isGameCenterPlayerAuthenticated()) {
            Ios.platform().loadGameCenterFriends(new Consumer<List<String[]>>() {
               public void accept(List<String[]> friends) {
                  SocialGameCenter.friendsLoaded(friends);
               }
            });
         } else {
            if (smListener != null) {
               smListener.getAppFriends(1, smAppFriends, 0);
            }
         }
      }
   }

   private static void friendsLoaded(List<String[]> friends) {
      if (friends == null) {
         if (smListener != null) {
            smListener.getAppFriends(1, smAppFriends, 0);
         }
      } else {
         for (String[] friend : friends) {
            smAppFriends.addElement(new SocialFriend(friend[0], friend[1]));
         }

         smTracking.trackFriends(smAppFriends == null ? 0 : smAppFriends.size());
         if (smListener != null) {
            smListener.getAppFriends(0, smAppFriends, 0);
         }
      }
   }

   public void reportScore(String category, int score) {
      if (this.isAvailable()) {
         Ios.platform().reportGameCenterScore(category, score);
      }
   }

   public void reportAchievemenProgress(int achievement, float percent) {
      int sku = this.getGCSku(achievement);
      if (sku != -1 && this.isAvailable()) {
         Ios.platform().submitGameCenterAchievement(Integer.toString(sku), percent);
      }
   }

   public void reportAchievementCompleted(int achievement) {
      this.reportAchievemenProgress(achievement, 100.0F);
   }

   public boolean isConnected() {
      return this.isAvailable() && Ios.platform().isGameCenterPlayerAuthenticated();
   }

   public String getName() {
      if (this.mName == null) {
         this.mName = Ios.platform().gameCenterPlayerAlias();
      }

      return this.mName;
   }

   public String getUserID() {
      if (this.mUserID == null) {
         this.mUserID = Ios.platform().gameCenterPlayerID();
      }

      return this.mUserID;
   }

   public boolean isAvailable() {
      return Ios.platform().isGameCenterAvailable();
   }

   private void createManager() {
      if (this.isAvailable()) {
         Ios.platform().createGameCenterManager();
      }
   }

   private int getGCSku(int achievement) {
      for (int i = 0; i < GC_SKU_MAPPING.length; i++) {
         if (GC_SKU_MAPPING[i][0] == achievement) {
            return GC_SKU_MAPPING[i][1];
         }
      }

      return -1;
   }
}
