package com.dchoc.idead.notifications;

import com.dchoc.hud.ReplaceParameters;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.Game;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.Time;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.events.EventManager;
import com.dchoc.idead.events.TimedEvent;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.CollectibleItem;
import com.dchoc.idead.items.CraftingItem;
import com.dchoc.idead.items.FarmingItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.objects.ConstructionObject;
import com.dchoc.idead.objects.FarmingObject;
import com.dchoc.idead.objects.FenceObject;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.servlets.UpdateDeviceToken;
import com.dchoc.idead.tracking.NotificationEvent;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.Sonda;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.WindowMarket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import zlport.ios.Ios;

public class NotificationManager implements CsvIDs {
   private static Vector smNotifications;
   private static Vector smAddedNotifications;
   private static NotificationEvent smTrackNotification;
   private static Vector smLaunchNotifications;
   private static String smDeviceToken = null;
   private static boolean smUpdateDeviceToken;
   private static boolean smSceneNotificationsCreated;

   public static void init() {
      smNotifications = new Vector(48);
      smAddedNotifications = new Vector(16);
      smTrackNotification = new NotificationEvent();
      DChocByteArray data = Toolkit.getResourceByteArray(80487);

      try {
         while (true) {
            smNotifications.addElement(new NotificationDefinition(data));
         }
      } catch (Exception exception) {
         Sonda.probeNonEof("NotificationManager.load (80487)", exception);
         smSceneNotificationsCreated = false;
         smUpdateDeviceToken = false;
         Ios.platform().registerForRemoteNotifications(7);
         smAddedNotifications.removeAllElements();
         Ios.platform().cancelLocalNotifications();
         smSceneNotificationsCreated = false;
      }
   }

   public static void removeNotifications() {
      smAddedNotifications.removeAllElements();
      Ios.platform().cancelLocalNotifications();
      smSceneNotificationsCreated = false;
   }

   public static int logicUpdate(int dt) {
      boolean connected = GameEngineUtils.isConnected();
      if (smUpdateDeviceToken && connected) {
         new UpdateDeviceToken("3", smDeviceToken).execute();
         smUpdateDeviceToken = false;
      }

      if (GameEngineUtils.isLoaded() && smLaunchNotifications != null && !smLaunchNotifications.isEmpty()) {
         long now = Time.getTimestamp();
         Notification closest = null;

         for (int i = 0; i < smLaunchNotifications.size(); i++) {
            Notification notification = (Notification)smLaunchNotifications.elementAt(i);
            processNotification(notification);
            if (closest == null || Math.abs(now - notification.getActivationTime()) < Math.abs(now - closest.getActivationTime())) {
               closest = notification;
            }
         }

         if (closest != null && Math.abs(now - closest.getActivationTime()) < 1800000L) {
            smTrackNotification.trackOpened(closest.getID());
         }

         smLaunchNotifications.removeAllElements();
      }

      return 0;
   }

   public static void processNotification(Notification notification) {
      if (notification != null && notification.getID() != -1 && notification.getAmount() > 0) {
         Item item = ItemManager.getItem(notification.getRewardType(), notification.getRewardID());
         if (item != null) {
            PlayerProfile.getInventory().addItem(item, notification.getAmount());
            int[] reward = new int[]{notification.getRewardType(), notification.getRewardID()};
            EventManager.addEvent(new TimedEvent(50, 2470, reward));
         }
      }
   }

   public static void createNotifications() {
      Game game = Game.getInstance();
      createSceneNotifications();
      if (!smSceneNotificationsCreated) {
         smAddedNotifications.removeAllElements();
         Ios.platform().cancelLocalNotifications();
         smSceneNotificationsCreated = false;
      }

      if (game.energyRefillEnabled()) {
         long time = PlayerProfile.getFullEnergyChargeTime();
         if (time > 60000L) {
            addNotification(getNotification(2812), Time.getTimestampAfter(time), null);
         }
      }

      long dailyReward = PlayerProfile.getNextDailyRewardTime();
      if (TutorialFlow.isCompleted() && dailyReward > 0L) {
         addNotification(getNotification(46), dailyReward, null);
      }

      createMissionNotifications();
      createRetentionNotifications();
      applyAll();
   }

   public static void createSceneNotifications() {
      if (!smSceneNotificationsCreated && GameEngineUtils.isAtHome() && GameEngineUtils.isLoaded() && TutorialFlow.isCompleted()) {
         Game game = Game.getInstance();
         smAddedNotifications.removeAllElements();
         Ios.platform().cancelLocalNotifications();
         smSceneNotificationsCreated = false;
         if (game.cropsAndHarvestingEnabled()) {
            createFarmingNotifications();
         }

         createCharacterNotifications();
         createFenceNotifications();
         smSceneNotificationsCreated = true;
      }
   }

   public static NotificationDefinition getNotification(int id) {
      if (id == -1) {
         return null;
      }

      for (int i = 0; i < smNotifications.size(); i++) {
         NotificationDefinition definition = (NotificationDefinition)smNotifications.elementAt(i);
         if (definition.getID() == id) {
            return definition;
         }
      }

      return null;
   }

   public static void createMissionNotifications() {
      Vector missions = MissionManager.getActiveMissions();

      for (int i = 0; i < missions.size(); i++) {
         Mission mission = (Mission)missions.elementAt(i);
         long updated = mission.getObjectiveUpdatedTime();
         if (mission.hasDuration()) {
            long end = mission.getAcceptanceTime() + mission.getDuration() * 6L / 10L;
            if (end > Time.fromHours(1) + updated) {
               String text = ReplaceParameters.replace(Toolkit.getText(2249), Integer.toString(mission.getCountLeft()));
               addNotification(getNotification(2823), end, text);
            }
         }

         if (mission.getNotificationID() != -1) {
            NotificationDefinition definition = getNotification(mission.getNotificationID());
            if (definition != null) {
               addNotification(definition, updated + definition.getDelay(), null);
            }
         }
      }
   }

   public static void createRetentionNotifications() {
      if (!TutorialFlow.isCompleted()) {
         Mission mission = MissionManager.getMission(2289);
         NotificationDefinition definition;
         if (mission != null && mission.isCompleted()) {
            definition = getNotification(2832);
         } else {
            definition = getNotification(2829);
         }

         addNotification(definition, 0L, null);
         addLastNotifications();
      } else {
         Object avatar = PlayerProfile.getAvatar(41);
         if (MissionManager.getCraftTime() != 0L) {
            Vector objects = IsometricUtils.getScene().getObjects(0);

            for (int i = 0; i < objects.size(); i++) {
               CraftingItem craftable = ((ConstructionObject)objects.elementAt(i)).getAvailableCraftable();
               if (craftable != null) {
                  CollectibleItem first = ItemManager.getCollectible(craftable.getCollectibleItems()[0]);
                  CollectibleItem second = ItemManager.getCollectible(craftable.getCollectibleItems()[1]);
                  if (first != null && second != null) {
                     NotificationDefinition definition = getNotification(2851);
                     if (definition != null) {
                        long time = MissionManager.getCraftTime() + definition.getDelay();
                        String text = ReplaceParameters.replace(
                           Toolkit.getText(definition.getDescription()),
                           Toolkit.getText(first.getTitle()),
                           Toolkit.getText(second.getTitle()),
                           Toolkit.getText(craftable.getTitle())
                        );
                        addNotification(definition, time, text);
                     }
                     break;
                  }
               }
            }
         }

         if (TutorialFlow.isCompleted() && MissionManager.getCustomizeTime() != 0L && avatar != null) {
            NotificationDefinition definition = getNotification(2853);
            long time = MissionManager.getCustomizeTime();
            if (definition != null) {
               addNotification(definition, time + definition.getDelay(), null);
            }
         }

         if (MissionManager.getHarvestTime() != 0L) {
            NotificationDefinition definition = getNotification(2855);
            long time = MissionManager.getHarvestTime();
            if (definition != null) {
               addNotification(definition, time + definition.getDelay(), null);
            }
         }

         if (MissionManager.getClearTime() != 0L) {
            NotificationDefinition definition = getNotification(2858);
            long time = MissionManager.getClearTime();
            if (definition != null) {
               addNotification(definition, time + definition.getDelay(), null);
            }
         }

         if (MissionManager.getRepairTime() != 0L) {
            NotificationDefinition definition = getNotification(2857);
            long time = MissionManager.getRepairTime();
            if (definition != null) {
               addNotification(definition, time + definition.getDelay(), null);
            }
         }

         if (TutorialFlow.isCompleted() && MissionManager.getSpendTime() != 0L && PlayerProfile.getCash() >= 10) {
            WindowMarket market = (WindowMarket)WindowManager.getWindow((byte)22);
            Item item = market.getItemWithPrice(7, PlayerProfile.getCash(), null);
            NotificationDefinition definition = getNotification(2859);
            if (item != null && definition != null) {
               long time = MissionManager.getSpendTime() + definition.getDelay();
               String text = ReplaceParameters.replace(Toolkit.getText(definition.getDescription()), Toolkit.getText(item.getTitle()));
               addNotification(definition, time, text);
            }
         }

         if (MissionManager.getUpgradeTime() != 0L && (MissionManager.isMissionCompleted(2367) || MissionManager.isMissionCompleted(44))) {
            NotificationDefinition definition = getNotification(2862);
            long time = MissionManager.getUpgradeTime();
            if (definition != null) {
               addNotification(definition, time + definition.getDelay(), null);
            }
         }

         addLastNotifications();
      }
   }

   private static void addLastNotifications() {
      addNotification(getNotification(2839), 0L, null);
      addNotification(getNotification(2841), 0L, null);
   }

   public static void applyAll() {
      for (int i = 0; i < smAddedNotifications.size(); i++) {
         Notification notification = (Notification)smAddedNotifications.elementAt(i);
         long delta = notification.getActivationTime() - Time.getTimestamp();
         if (notification.getText() != null && delta >= 0L) {
            double seconds = delta / 1000L;
            Map<String, String> userInfo = new LinkedHashMap<>();
            userInfo.put("id", Integer.toString(notification.getID()));
            userInfo.put("rewardType", Integer.toString(notification.getRewardType()));
            userInfo.put("rewardID", Integer.toString(notification.getRewardID()));
            userInfo.put("amount", Integer.toString(notification.getAmount()));
            userInfo.put("activationTime", Long.toString(notification.getActivationTime()));
            if (Ios.platform().scheduleLocalNotification(seconds, notification.getText(), userInfo)) {
               smTrackNotification.trackSend(notification.getID());
            }
         }
      }

      smAddedNotifications.removeAllElements();
   }

   public static boolean addNotification(NotificationDefinition definition, long time, String text) {
      if (definition == null) {
         return false;
      }

      if (time <= 0L) {
         long delay = definition.getDelay() > 0L ? definition.getDelay() : Time.fromSeconds(5L);
         time = Time.getTimestampAfter(delay);
      }

      if (text == null) {
         text = Toolkit.getText(definition.getDescription());
      }

      boolean valid = isValidNotification(definition, time);
      if (text != null && valid) {
         smAddedNotifications.addElement(
            new Notification(definition.getID(), text, time, definition.getRewardType(), definition.getRewardID(), definition.getAmount())
         );
         return true;
      } else {
         return false;
      }
   }

   public static void createFarmingNotifications() {
      Vector objects = IsometricUtils.getScene().getObjects(6);

      for (int i = 0; i < objects.size(); i++) {
         FarmingObject farming = (FarmingObject)objects.elementAt(i);
         FarmingItem item = (FarmingItem)farming.getItem();
         if (item.getID() != 1586 && item.canGrow() && farming.isGrowing()) {
            addNotification(getNotification(2814), farming.getTimestamp() + item.getGrowingTime(), null);
         }

         if (item.getID() != 1586 && item.canWither() && !farming.isGrowing() && !farming.isWithered()) {
            long time = farming.getTimestamp() + item.getGrowingTime();
            int wither = item.getWitherTime() * 4 / 10;
            addNotification(getNotification(2816), time + wither, null);
         }

         if (item.canWither() && farming.isAttacked()) {
            long time = farming.getDestructTimestamp();
            addNotification(getNotification(2817), time + 3600000L, null);
         }
      }
   }

   public static void createCharacterNotifications() {
      IsometricScene scene = IsometricUtils.getScene();
      if (PlayerProfile.getAvatar(41) != null && isPlayerAccessible(scene)) {
         addNotification(getNotification(2820), 0L, null);
      }

      if (PlayerProfile.getAvatar(43) != null && isPlayerAccessible(scene)) {
         addNotification(getNotification(2822), 0L, null);
      }
   }

   private static boolean isPlayerAccessible(IsometricScene scene) {
      PlayerCharacter player = scene.getPlayer();
      if (player == null) {
         return false;
      }

      IsometricTile tile = player.getTile();
      return tile != null && tile.isAccessible();
   }

   public static void createFenceNotifications() {
      Vector objects = IsometricUtils.getScene().getObjects(5);
      int damaged = 0;

      for (int i = 0; i < objects.size(); i++) {
         if (((FenceObject)objects.elementAt(i)).getHealth() <= 1) {
            if (++damaged >= 2) {
               addNotification(getNotification(2495), 0L, null);
               return;
            }
         }
      }
   }

   public static void registeredForNotifications(String deviceToken) {
      smDeviceToken = deviceToken;
      smUpdateDeviceToken = true;
   }

   public static void launchedWithNotification(int id, int rewardType, int rewardID, int amount, long time) {
      if (smLaunchNotifications == null) {
         smLaunchNotifications = new Vector(2);
      }

      smLaunchNotifications.addElement(new Notification(id, null, time, rewardType, rewardID, amount));
   }

   public static boolean isValidNotification(NotificationDefinition definition, long time) {
      if (definition == null) {
         return false;
      }

      boolean past = Time.getTimestamp() > time;
      long timeZone = Time.getTimeZone();
      if (past) {
         return false;
      }

      long dateTime = Time.getDateTime(time + timeZone);
      if (dateTime >= Tuner.getFromTime() && dateTime <= Tuner.getToTime()) {
         long date = Time.getDate(time + timeZone);
         long offset = definition.getOffset();
         int count = 0;

         for (int i = 0; i < smAddedNotifications.size(); i++) {
            Notification notification = (Notification)smAddedNotifications.elementAt(i);
            boolean merge;
            if (offset > 0L) {
               merge = notification.getID() == definition.getID() && Math.abs(notification.getActivationTime() - time) < offset;
            } else {
               merge = offset == 0L && notification.getID() == definition.getID();
            }

            if (merge) {
               if (notification.getActivationTime() > time) {
                  notification.setActivationTime(time);
               }

               return false;
            }

            if (Time.getDate(notification.getActivationTime() + timeZone) == date) {
               if (++count >= Tuner.getMaxDailyNotifications()) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
