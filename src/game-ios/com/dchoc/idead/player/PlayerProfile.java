package com.dchoc.idead.player;

import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.Profiler;
import com.dchoc.idead.Time;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.events.EventManager;
import com.dchoc.idead.events.TimedEvent;
import com.dchoc.idead.fixes.Fixes;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.AvatarItem;
import com.dchoc.idead.items.BoosterItem;
import com.dchoc.idead.items.EdibleItem;
import com.dchoc.idead.items.GiftItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.servlets.AddNeighborActions;
import com.dchoc.idead.tracking.ConsumeEvent;
import com.dchoc.idead.tracking.EarnGCEvent;
import com.dchoc.idead.tracking.EarnPCEvent;
import com.dchoc.idead.tracking.ErrorEvent;
import com.dchoc.idead.tracking.LevelUpEvent;
import com.dchoc.idead.tracking.SpendEvent;
import com.dchoc.idead.tracking.TrackingEvents;
import com.dchoc.idead.tuner.LevelDefinition;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.iphonewrappers.ActivityWrapper;
import com.dchoc.playhaven.PlayHaven;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import com.dchoc.windows.WindowOutOfCashCoins;
import java.util.Vector;
import zlport.ios.Ios;

public class PlayerProfile implements CsvIDs {
   public static final int CURRENCY_CASH = 7;
   public static final int CURRENCY_COINS = 9;
   public static final int CURRENCY_ENERGY = 107;
   public static final int CURRENCY_FOOD = 13;
   public static final int CURRENCY_INVALID = -1;
   public static final int CURRENCY_STONE = 11;
   public static final int CURRENCY_XP = 2095;
   public static final int INITIAL_NEIGHBORS_CAPACITY = 8;
   public static final String INITIAL_PLAYER_NAME = "Player";
   public static final String INITIAL_PLAYER_NAME_NEW = "Neighbor";
   public static final String RMS_PROFILE = "RMS_PROFILE";
   private static LevelUpEvent mLevelUpEvent;
   private static Profile smProfile;
   public static Inventory smInventory;
   private static DailyRewards smDailyRewards;
   private static VisitingRewards smVisitingRewards;
   private static Vector smAcceptedNeighbors;
   private static boolean smInitialized;
   private static int smSessionCount;
   private static int smResumeCount;
   private static int smScore;
   private static int smXp;
   private static int smEnergy;
   private static int smEnergyMax;
   private static int smEnergyChargeTimeMax;
   private static long smEnergyChargeTimestamp;
   private static long smNextDailyRewardTimestamp;
   private static int smCash;
   private static int smCoins;
   private static int smStone;
   private static int smFood;
   private static int smHiresLeft;
   private static int smHiresMax;
   private static long smHireCountdown;
   private static boolean smShowApologyDialog;
   private static boolean smTutorialComplete;
   private static boolean smStarterPack1Purchased;
   private static String smFriendCode;
   private static String smFlurryFreeText;
   private static SpendEvent[] smSlotEvents;
   private static ConsumeEvent smConsumeEvent;
   private static EarnPCEvent smEarnPCEvent;
   private static EarnGCEvent smEarnGCDailyEvent;
   private static EarnGCEvent smEarnGCGiftEvent;
   private static EarnGCEvent smEarnGCVisitEvent;

   public static void init() {
      smProfile = new Profile("Neighbor", 0);
      smEnergyChargeTimeMax = 300000;
      applyEnergyChargeTimeOverride();
      smEnergyChargeTimestamp = Time.getTimestamp();
      smInventory = new Inventory();
      smDailyRewards = new DailyRewards();
      smVisitingRewards = new VisitingRewards();
      smAcceptedNeighbors = new Vector(8);
      smScore = 0;
      smHiresLeft = 0;
      smHiresMax = 0;
      smHireCountdown = 0L;
      smNextDailyRewardTimestamp = 0L;
      smResumeCount = 0;
      smSessionCount = 0;
      smTutorialComplete = false;
      smStarterPack1Purchased = false;
      smShowApologyDialog = false;
      mLevelUpEvent = new LevelUpEvent();
      loadDefaults();
      GameEngine.getInstance().loadPlayerProfile();
      smSlotEvents = new SpendEvent[]{new SpendEvent(3), new SpendEvent(2), new SpendEvent(16)};
      smConsumeEvent = new ConsumeEvent();
      smEarnPCEvent = new EarnPCEvent();
      smEarnGCDailyEvent = new EarnGCEvent(0);
      smEarnGCGiftEvent = new EarnGCEvent(1);
      smEarnGCVisitEvent = new EarnGCEvent(2);
      smInitialized = true;
      smFlurryFreeText = "Free";
   }

   public static void loadDefaults() {
      addAvatar(15, false);
      DChocByteArray data = Toolkit.getResourceByteArray(80412);

      try {
         smProfile.setLevel(data.readInt());
         smXp = data.readInt();
         smCoins = data.readInt();
         smCash = data.readInt();
         smEnergyMax = data.readInt();
         smEnergy = smEnergyMax;

         for (int i = 0; i < 5; i++) {
            data.readInt();
         }

         smFood = data.readInt();
         smStone = data.readInt();
         smFriendCode = "";
      } catch (Exception exception) {
         System.err.println(exception);
      }
   }

   public static boolean isInitialized() {
      return smInitialized;
   }

   public static int logicUpdate(int deltaTime) {
      if (smShowApologyDialog && GameEngineUtils.isConnected()) {
         EventManager.addEvent(new TimedEvent(250, 3249, null));
         smShowApologyDialog = false;
      }

      updateEnergy(deltaTime);
      updateBoosters(deltaTime);
      return 0;
   }

   public static void updateEnergy(int deltaTime) {
      if (smEnergy < smEnergyMax) {
         long elapsed = Time.getTimestampDelta(smEnergyChargeTimestamp);
         if (elapsed >= smEnergyChargeTimeMax) {
            int charged = Math.min((int)(elapsed / smEnergyChargeTimeMax), smEnergyMax - smEnergy);
            if (charged >= 0) {
               smEnergy += charged;
               smEnergy = Math.min(smEnergy, smEnergyMax);
            }

            if (smEnergy >= smEnergyMax) {
               PlayHaven.triggerEnergyFull();
               return;
            }

            smEnergyChargeTimestamp = smEnergyChargeTimestamp + (long)smEnergyChargeTimeMax * charged;
         }
      }
   }

   public static void updateBoosters(int deltaTime) {
      Vector boosters = smInventory.getBoosterItems();

      for (int i = 0; i < boosters.size(); i++) {
         InventoryItem booster = (InventoryItem)boosters.elementAt(i);
         if (!booster.isActive()) {
            smInventory.removeItem(booster, 1);
            EventManager.addEvent(new TimedEvent(150, 3279, new int[]{booster.getID()}));
         }
      }
   }

   public static Inventory getInventory() {
      return smInventory;
   }

   public static DailyRewards getDailyRewards() {
      return smDailyRewards;
   }

   public static VisitingRewards getVisitingRewards() {
      return smVisitingRewards;
   }

   public static void addDailyReward(int day) {
      if (Integer.compareUnsigned(day, 4) <= 0) {
         EventManager.addEvent(new TimedEvent(2000, 46, new int[]{day}));
      }
   }

   public static Profile getProfile() {
      return smProfile;
   }

   public static boolean canBuy(Item item, int amount, boolean showWindow, boolean secondPrice) {
      if (item != null && item.getPrice() >= 1) {
         int currency = secondPrice ? item.getSecondCurrency() : item.getCurrency();
         int total = (secondPrice ? item.getSecondPrice() : item.getPrice()) * amount;
         if (currency == 13) {
            return total <= smFood;
         }

         boolean enough = currency == 7 ? total <= smCash : currency == 9 && total <= smCoins;
         if (!enough && showWindow) {
            WindowManager.closeWindow();
            WindowOutOfCashCoins window = (WindowOutOfCashCoins)WindowManager.getWindow((byte)11);
            window.setMode(currency == 9 ? 1 : 0);
            WindowManager.openWindow((byte)11);
         }

         return enough;
      } else {
         return false;
      }
   }

   public static int getCoins() {
      return smCoins;
   }

   public static int getCash() {
      return smCash;
   }

   public static int getFood() {
      return smFood;
   }

   public static InventoryItem buyItem(Item item, int amount, boolean secondPrice) {
      if (item != null && item.getPrice() >= 1 && canBuy(item, amount, true, secondPrice)) {
         int currency = secondPrice ? item.getSecondCurrency() : item.getCurrency();
         int total = (secondPrice ? item.getSecondPrice() : item.getPrice()) * amount;
         if (currency == 7) {
            removeCash(total);
         } else if (currency == 13) {
            removeFood(total);
         } else if (currency == 9) {
            removeCoins(total);
         }

         int type = item.getType();
         int id = item.getID();
         int specialIndex = id - 1937;
         if (Integer.compareUnsigned(specialIndex, 4) < 0 && specialIndex != 1) {
            type = 405;
         }

         MissionManager.updateCounter(2518, id, amount);
         MissionManager.updateCounter(2518, type, amount);
         MissionManager.updateCounter(2518, item.getSubType(), amount);
         int units = item.getType() == 1907 ? ((WeaponItem)item).getMaxAmmo() : 1;
         int[] spendSlots = new int[]{0, 0, 1, 1, -1, -1, 2, 2};
         int slot = Integer.compareUnsigned(currency - 7, 8) < 0 ? spendSlots[currency - 7] : -1;
         if (slot >= 0) {
            smSlotEvents[slot].trackSpend(item.getID(), item.getType(), total, units);
         }

         return smInventory.addItem(item, units * amount);
      } else {
         return null;
      }
   }

   public static void removeCoins(int amount) {
      smCoins = Math.max(smCoins - amount, 0);
   }

   public static void removeCash(int amount) {
      smCash -= amount;
      MissionManager.updateCounter(106, 7, amount);
      MissionManager.updateCounter(106, 80, amount);
      smCash = Math.max(smCash, 0);
   }

   public static void removeFood(int amount) {
      smFood = Math.max(smFood - amount, 0);
   }

   public static boolean useItem(Item item, boolean flag) {
      return useItem(smInventory.getItem(item), false);
   }

   public static boolean useItem(InventoryItem inventoryItem, boolean flag) {
      if (inventoryItem != null && inventoryItem.isUsable()) {
         Item item = inventoryItem.getItem();
         switch (item.getType()) {
            case 101:
               EdibleItem edible = (EdibleItem)item;
               MissionManager.updateCounter(100, item.getID(), 1);
               MissionManager.updateCounter(100, item.getType(), 1);
               MissionManager.updateCounter(100, 80, 1);
               int energy;
               if (edible.getEnergyGain() < 1) {
                  int previousEnergy = smEnergy;
                  int previousMax = smEnergyMax;
                  energy = edible.getMaxEnergyGain();
                  int maximum = smEnergyMax;
                  smEnergyMax = edible.getMaxEnergyGain() + maximum;
                  energy += previousMax - previousEnergy;
                  if (smEnergy < smEnergyMax) {
                     smEnergy = smEnergyMax;
                  }
               } else {
                  addEnergy(edible.getEnergyGain(), true);
                  energy = edible.getEnergyGain();
               }

               MissionManager.updateCounter(2963, 80, energy);
               smConsumeEvent.trackConsume(item.getID(), item.getType());
               smInventory.removeItem(inventoryItem, 1);
               break;
            case 147:
            case 405:
            case 1104:
            case 1583:
               return false;
            case 1907:
               if (inventoryItem.isUsable()) {
                  smProfile.setActiveWeapon((WeaponItem)inventoryItem.getItem());
               }

               PlayerCharacter player = IsometricUtils.getPlayer();
               if (player != null) {
                  player.cancelActions(true);
               }
               break;
            case 1911:
               smProfile.getActiveAvatar().equipItem((AvatarItem)item);
               break;
            case 3270:
               BoosterItem booster = (BoosterItem)item;
               if (inventoryItem.getUseTime() == 0L) {
                  inventoryItem.setUseTime(Time.getTimestamp());
                  inventoryItem.setDuration(booster.getDuration());
               }

               if (inventoryItem.getAmount() > 1) {
                  inventoryItem.increaseDuration(booster.getDuration() * (inventoryItem.getAmount() - 1));
                  inventoryItem.setAmount(1);
               }

               mergeBoosters();
         }

         return true;
      } else {
         return false;
      }
   }

   public static void mergeBoosters() {
      Vector boosters = smInventory.getBoosterItems();

      for (int i = 0; i < boosters.size(); i++) {
         InventoryItem current = (InventoryItem)boosters.elementAt(i);
         BoosterItem currentItem = (BoosterItem)current.getItem();
         if (current.isActive()) {
            for (int j = 0; j < boosters.size(); j++) {
               InventoryItem other = (InventoryItem)boosters.elementAt(j);
               if (other != current) {
                  BoosterItem otherItem = (BoosterItem)other.getItem();
                  if (!other.isActive() || currentItem.getEffect() == otherItem.getEffect()) {
                     if (currentItem.getPercentage() < otherItem.getPercentage()) {
                        other.increaseDuration(current.getDuration());
                        smInventory.removeItem(current, current.getAmount());
                        i--;
                        break;
                     }

                     current.increaseDuration(other.getDuration());
                     smInventory.removeItem(other, other.getAmount());
                     j--;
                  }
               }
            }
         }
      }
   }

   public static void addEnergy(int amount, boolean overflow) {
      if (amount >= 0) {
         smEnergy += amount;
         if (!overflow) {
            smEnergy = Math.min(smEnergy, smEnergyMax);
         }
      }
   }

   public static int getEnergy() {
      return smEnergy;
   }

   public static int getEnergyMax() {
      return smEnergyMax;
   }

   public static void changeWeapon(InventoryItem item) {
      if (item != null) {
         if (item.isUsable()) {
            smProfile.setActiveWeapon((WeaponItem)item.getItem());
         }

         PlayerCharacter player = IsometricUtils.getPlayer();
         if (player != null) {
            player.cancelActions(true);
         }
      }
   }

   public static int getEffectPercentage(int effect) {
      Vector boosters = smInventory.getBoosterItems();

      for (int i = 0; i < boosters.size(); i++) {
         InventoryItem booster = (InventoryItem)boosters.elementAt(i);
         if (booster.isActive()) {
            BoosterItem item = (BoosterItem)booster.getItem();
            if (item.getEffect() == effect) {
               return item.getPercentage();
            }
         }
      }

      return 0;
   }

   public static boolean useWeapon(WeaponItem weapon) {
      InventoryItem item = smInventory.getItem(weapon);
      if (item == null) {
         return false;
      }

      int amount = item.getAmount();
      int usage = weapon.getAmmoUsage();
      int remaining = amount - usage;
      if (remaining <= 0) {
         switchWeapon();
      }

      smInventory.removeItem(item, weapon.getAmmoUsage());
      Vector weapons = smInventory.getWeaponItems();

      for (int i = 0; i < weapons.size(); i++) {
         WeaponItem available = (WeaponItem)((InventoryItem)weapons.elementAt(i)).getItem();
         if (available.getSubType() != 79) {
            return remaining > 0;
         }
      }

      if (amount != usage && remaining >= 0) {
         return true;
      }

      if (TutorialFlow.isCompleted() && weapon.getID() != 49) {
         WindowManager.openWindow((byte)12);
      }

      return false;
   }

   public static void switchWeapon() {
      ActivityWrapper.show(Toolkit.getScreenWidth() >> 1, Toolkit.getScreenHeight() >> 1);
      Vector weapons = smInventory.getWeaponItems();
      AvatarDescription avatar = smProfile.getActiveAvatar();
      if (avatar != null && avatar.getGender() != 43 && !weapons.isEmpty()) {
         InventoryItem current = smInventory.getItem(smProfile.getActiveWeapon());
         if (current == null) {
            changeWeapon((InventoryItem)weapons.firstElement());
            return;
         }

         int next = 0;

         for (int i = 0; i < weapons.size(); i++) {
            if (((InventoryItem)weapons.elementAt(i)).getID() == current.getID()) {
               next = i + 1;
               break;
            }
         }

         if (next >= weapons.size()) {
            next = 0;
         }

         changeWeapon((InventoryItem)weapons.elementAt(next));
      }

      ActivityWrapper.hide();
   }

   public static InventoryItem getCurrentWeapon() {
      AvatarDescription avatar = smProfile.getActiveAvatar();
      return avatar == null ? null : smInventory.getItem(avatar.getWeapon());
   }

   public static int getCurrentAmmo() {
      InventoryItem weapon = getCurrentWeapon();
      return weapon == null ? 0 : weapon.getAmount();
   }

   public static boolean isNeighborAccepted(NeighborProfile neighbor) {
      for (int i = 0; i < smAcceptedNeighbors.size(); i++) {
         if (((String)smAcceptedNeighbors.elementAt(i)).equals(neighbor.getUserID())) {
            return true;
         }
      }

      return false;
   }

   public static void acceptNeighbor(NeighborProfile neighbor) {
      if (neighbor != null) {
         for (int i = 0; i < smAcceptedNeighbors.size(); i++) {
            if (((String)smAcceptedNeighbors.elementAt(i)).equals(neighbor.getUserID())) {
               return;
            }
         }

         smAcceptedNeighbors.addElement(neighbor.getUserID());
      }
   }

   public static int getEnergyChargeTime(boolean seconds) {
      long remaining = smEnergyChargeTimeMax - Time.getTimestampDelta(smEnergyChargeTimestamp);
      return (int)(remaining / (seconds ? 1000L : 1L));
   }

   private static void applyEnergyChargeTimeOverride() {
      Integer override = Ios.platform().energyChargeTimeOverride();
      if (override != null) {
         smEnergyChargeTimeMax = override;
      }
   }

   public static int getEnergyChargeTimeMax() {
      return smEnergyChargeTimeMax;
   }

   public static long getFullEnergyChargeTime() {
      int missing = smEnergyMax - smEnergy;
      return missing <= 0 ? 0L : smEnergyChargeTimeMax * (missing - 1) - Time.getTimestampDelta(smEnergyChargeTimestamp) + smEnergyChargeTimeMax;
   }

   public static void setEnergy(int energy) {
      smEnergy = energy;
   }

   public static void removeEnergy(int amount) {
      int previous = smEnergy;
      smEnergy -= amount;
      if (smEnergy < smEnergyMax && smEnergyMax <= previous) {
         smEnergyChargeTimestamp = Time.getTimestamp();
      }

      if (smEnergy < 0) {
         smEnergy = 0;
      } else if (smEnergy != 0) {
         return;
      }

      MissionManager.updateCounter(2476, 107, 1);
      MissionManager.updateCounter(2476, 80, 1);
   }

   public static boolean hasFullEnergy() {
      return smEnergy >= smEnergyMax;
   }

   public static void setHiresLeft(int hires) {
      smHiresLeft = hires;
   }

   public static int getHiresLeft() {
      return smHiresLeft;
   }

   public static void setHiresMax(int hires) {
      smHiresMax = hires;
   }

   public static void setHireCountdown(long timestamp) {
      smHireCountdown = timestamp;
   }

   public static long getHireCountdownLeft() {
      return smHireCountdown == 0L ? 0L : Time.getCoundownLeft(smHireCountdown);
   }

   public static int getLevel() {
      return smProfile.getLevel();
   }

   public static LevelDefinition getCurrentLevel() {
      return Tuner.getLevel(smProfile.getLevel());
   }

   public static int getSessionCount() {
      return smSessionCount;
   }

   public static void setResumeCount(int count) {
      smResumeCount = count;
   }

   public static int getResumeCount() {
      return smResumeCount;
   }

   public static void addXp(int amount) {
      smXp += amount;
      int level = smProfile.getLevel();
      LevelDefinition next = Tuner.getNextLevel(level);
      if (next != null && smXp >= next.getXpLimit()) {
         smProfile.setLevel(next.getLevel());
         EventManager.addEvent(new TimedEvent(1000, 45, new int[]{level}));
         addCoins(next.getCoins());
         int cash = next.getCash();
         if (cash >= 0) {
            smCash += cash;
         }

         smEnergyMax = smEnergyMax + next.getMaxEnergyIncrease();
         if (smEnergy < smEnergyMax) {
            smEnergy = smEnergyMax;
         }

         mLevelUpEvent.trackEvent();
         if (cash > 0) {
            smEarnPCEvent.trackEvent(TrackingEvents.getEventType(6), cash);
         }

         IsometricUtils.getScene().setUpdateEdges();
      }
   }

   public static void addCoins(int amount) {
      if (amount >= 0) {
         MissionManager.updateCounter(2491, 9, amount);
         MissionManager.updateCounter(2491, 80, amount);
         smCoins += amount;
      }
   }

   public static int getXpForLevelUp() {
      LevelDefinition next = Tuner.getNextLevel(smProfile.getLevel());
      return (next == null ? 0 : next.getXpLimit()) - smXp;
   }

   public static int getXpLimit() {
      LevelDefinition next = Tuner.getNextLevel(smProfile.getLevel());
      return next == null ? 0 : next.getXpLimit();
   }

   public static int getXp() {
      return smXp;
   }

   public static int getXpLimitForPreviousLevel() {
      LevelDefinition current = Tuner.getLevel(smProfile.getLevel());
      return current == null ? 0 : current.getXpLimit();
   }

   public static void setCash(int cash) {
      smCash = cash;
   }

   public static void setFriendCode(String code) {
      smFriendCode = code;
   }

   public static String getFriendCode() {
      return smFriendCode;
   }

   public static String getFlurryText() {
      return smFlurryFreeText;
   }

   public static void setCoins(int coins) {
      smCoins = coins;
   }

   public static void addStone(int amount) {
      if (amount >= 0) {
         MissionManager.updateCounter(2491, 11, amount);
         MissionManager.updateCounter(2491, 80, amount);
         smStone += amount;
      }
   }

   public static void removeStone(int amount) {
      smStone = Math.max(smStone - amount, 0);
   }

   public static void setStone(int stone) {
      smStone = stone;
   }

   public static int getStone() {
      return smStone;
   }

   public static void addFood(int amount) {
      if (amount >= 0) {
         MissionManager.updateCounter(2491, 13, amount);
         MissionManager.updateCounter(2491, 80, amount);
         smFood += amount;
      }
   }

   public static void setFood(int food) {
      smFood = food;
   }

   public static void setNextDailyRewardTime(long timestamp) {
      smNextDailyRewardTimestamp = timestamp;
   }

   public static long getNextDailyRewardTime() {
      return smNextDailyRewardTimestamp;
   }

   public static void setTutorialComplete(boolean complete) {
      smTutorialComplete = complete;
   }

   public static boolean isTutorialComplete() {
      return smTutorialComplete;
   }

   public static boolean isStarterPack1Purchased() {
      return smStarterPack1Purchased;
   }

   public static boolean addReward(int type, int amount, int source) {
      switch (type) {
         case 7:
            if (amount >= 0) {
               smCash += amount;
            }

            if (amount > 0) {
               smEarnPCEvent.trackEvent(TrackingEvents.getRewardType(source), amount);
            }

            return true;
         case 9:
            addCoins(amount);
            if (source == 1) {
               smEarnGCGiftEvent.trackEarn(amount);
            } else if (source == 2) {
               smEarnGCVisitEvent.trackEarn(amount);
            } else if (source == 3) {
               smEarnGCDailyEvent.trackEarn(amount);
            }

            return true;
         case 11:
            addStone(amount);
            return true;
         case 13:
            addFood(amount);
            return true;
         case 107:
            addEnergy(amount, true);
            return true;
         case 2095:
            addXp(amount);
            return true;
         default:
            return false;
      }
   }

   public static InventoryItem addRewardItem(Item item, int amount) {
      if (item == null) {
         return null;
      }

      if (item.getType() == 1909) {
         GiftItem gift = (GiftItem)item;
         int units = gift.getCreatesAmount();
         int type = gift.getCreatesType();
         if (type == 6213) {
            Fixes.completeMissionObjectives();
            return null;
         }

         if (type == 3224) {
            Fixes.fixResetMissionSetups();
            return null;
         }

         amount *= units;
         if (addReward(gift.getCreatesType(), amount, 1)) {
            return null;
         }

         item = ItemManager.getItem(gift.getCreatesType(), gift.getCreatesID());
         if (item == null) {
            return null;
         }
      }

      if (item.getType() == 1907) {
         amount *= ((WeaponItem)item).getMaxAmmo();
      }

      return smInventory.addItem(item, amount);
   }

   public static void addVisitingRewards(VisitingRewards rewards) {
      addXp(rewards.getXpReward());
      int coins = rewards.getCoinReward();
      addCoins(coins);
      smEarnGCVisitEvent.trackEarn(coins);
      addEnergy(rewards.getEnergyReward(), true);
      addRewardItem(ItemManager.getCollectible(48), rewards.getDuctTapeReward());
   }

   public static int getRewardIconID(int type, int id) {
      if (type == 9) {
         return 66089;
      }

      if (type == 13) {
         return 66097;
      }

      Item item = ItemManager.getItem(type);
      return item == null ? -1 : item.getIconAnimationID();
   }

   public static AvatarDescription getActiveAvatar() {
      return smProfile.getActiveAvatar();
   }

   public static void setActiveAvatar(int character) {
      smProfile.setActiveAvatar(character);
      IsometricScene scene = IsometricUtils.getScene();
      PlayerCharacter current = scene.getPlayer();
      if (current != null) {
         current.cancelActions(true);
      }

      PlayerCharacter player = scene.getPlayer(smProfile.getActiveAvatar());
      if (player != null) {
         scene.setPlayer(player);
         scene.centerToObject(player);
      }
   }

   public static void addAvatar(int character, boolean createPlayer) {
      AvatarDescription avatar = smProfile.getAvatar(character);
      if (avatar == null) {
         int gender;
         if (character == 41) {
            gender = smProfile.getAvatar(15).getGender() == 1921 ? 1922 : 1921;
         } else {
            gender = character == 15 ? 1921 : 43;
         }

         avatar = new AvatarDescription(gender, character);
         if (gender == 43) {
            loadDefaultAvatarItems(80421, 43, avatar);
         } else {
            loadDefaultAvatarItems(80419, 1921, avatar);
            loadDefaultAvatarItems(80420, 1922, avatar);
         }

         smProfile.addAvatar(avatar);
      }

      if (createPlayer && GameEngineUtils.isAtHome()) {
         IsometricScene scene = IsometricUtils.getScene();
         if (scene.getPlayer(avatar) == null) {
            PlayerCharacter player = scene.getPlayer();
            IsometricTile tile = null;
            if (player != null) {
               tile = IsometricUtils.getTileAsSpiralSearch(scene.getTileAt(player.getX(), player.getY()), 2, 50, 1, 1, true, true);
            }

            if (tile == null) {
               tile = scene.getTileAtCenterOfScreen();
            }

            scene.createPlayer(avatar, tile.getX(), tile.getY(), false);
         }
      }
   }

   public static AvatarDescription getAvatar(int character) {
      return smProfile.getAvatar(character);
   }

   public static Vector getAvatars() {
      return smProfile.getAvatars();
   }

   public static void addNeighbrAction(Item item, int action) {
      if (item != null) {
         NeighborProfile neighbor = GameEngineUtils.getNeighbor();
         if (neighbor.getType() != 2514) {
            new AddNeighborActions(neighbor, item, action).execute();
         }
      }
   }

   public static DChocByteArray save() {
      if (!smInitialized) {
         return null;
      }

      Profiler.startTimer(13);
      DChocByteArray data = smProfile.save();
      data.writeInt(smSessionCount);
      data.writeInt(smScore);
      data.writeInt(smXp);
      data.writeInt(smEnergyMax);
      data.writeInt(smEnergy);
      data.writeInt(smCash);
      data.writeInt(smCoins);
      data.writeInt(smStone);
      data.writeInt(smFood);
      smInventory.save(data);
      ToolkitHelpers.writeLong(smEnergyChargeTimestamp, data);
      data.writeInt(smAcceptedNeighbors.size());

      for (int i = 0; i < smAcceptedNeighbors.size(); i++) {
         data.writeUTF((String)smAcceptedNeighbors.elementAt(i));
      }

      data.writeUTF(smFriendCode);
      data.writeInt(smEnergyChargeTimeMax);
      data.writeBoolean(smShowApologyDialog);
      data.writeInt(smResumeCount);
      data.writeBoolean(smTutorialComplete);
      data.writeBoolean(smStarterPack1Purchased);
      Profiler.stopTimer(13);
      return data;
   }

   public static boolean load(DChocByteArray data) {
      if (data == null) {
         return false;
      }

      Profiler.startTimer(12);

      try {
         smProfile.load(data);
         smSessionCount = data.readInt() + 1;
         smScore = data.readInt();
         smXp = data.readInt();
         smEnergyMax = Math.max(smEnergyMax, data.readInt());
         smEnergy = data.readInt();
         smCash = data.readInt();
         smCoins = data.readInt();
         smStone = data.readInt();
         smFood = data.readInt();
         Fixes.fixCurrencies();
         smInventory.load(data);
         Fixes.fixInventory();
         smEnergyChargeTimestamp = Fixes.fixTimestamp(ToolkitHelpers.readLong(data));
         smAcceptedNeighbors.removeAllElements();
         int count = data.readInt();

         for (int i = 0; i < count; i++) {
            String userID = data.readUTF();
            if (userID != null) {
               smAcceptedNeighbors.addElement(userID);
            }
         }

         int version = smProfile.getVersion();
         if (version == -1) {
            return true;
         }

         if (version > 117) {
            smFriendCode = data.readUTF();
            int chargeTime = data.readInt();
            if (chargeTime > 0) {
               smEnergyChargeTimeMax = chargeTime;
            }
         }

         applyEnergyChargeTimeOverride();
         if (Integer.compareUnsigned(version - 113, 8) < 0) {
            smShowApologyDialog = true;
         }

         if (version > 120) {
            smShowApologyDialog = data.readBoolean();
            if (version > 136) {
               smResumeCount = data.readInt();
               smTutorialComplete = data.readBoolean();
            }
         }

         smStarterPack1Purchased = data.readBoolean();
      } catch (Exception exception) {
         new ErrorEvent().trackLoadError("PlayerProfile.load", exception.getMessage(), null);
         Profiler.stopTimer(12);
         return false;
      }

      Profiler.stopTimer(12);
      return true;
   }

   public static void addInAppPurchaseStarterPack(int cash, int coins, int energy, int weapons) {
      if (smInitialized) {
         GameEngine engine = GameEngine.getInstance();
         smCash += cash;
         smCoins += coins;
         addEnergy(energy, true);
         WeaponItem weapon = (WeaponItem)ItemManager.getItem(6252);
         smInventory.addItem(weapon, weapon.getMaxAmmo() * weapons);
         smStarterPack1Purchased = true;
         engine.savePlayerProfile();
      }
   }

   public static void addInAppPurchaseCash(int cash) {
      boolean initialized = smInitialized;
      smCash += cash;
      if (initialized) {
         GameEngine.getInstance().savePlayerProfile();
      }
   }

   public static void addInAppPurchaseCoins(int coins) {
      boolean initialized = smInitialized;
      smCoins += coins;
      if (initialized) {
         GameEngine.getInstance().savePlayerProfile();
      }
   }

   private static void loadDefaultAvatarItems(int resource, int gender, AvatarDescription avatar) {
      DChocByteArray data = Toolkit.getResourceByteArray(resource);

      try {
         data.readInt();

         while (true) {
            AvatarItem item = ItemManager.getAvatar(data.readInt());
            if (item != null) {
               if (smInventory.getItemCount(item) == 0) {
                  smInventory.addItem(item, 1);
               }

               avatar.equipItemForGender(item, gender);
            }
         }
      } catch (Exception exception) {
         System.err.println(exception);
      }
   }

   public static int getAcceptedNeighborCount() {
      return smAcceptedNeighbors == null ? 0 : smAcceptedNeighbors.size();
   }
}
