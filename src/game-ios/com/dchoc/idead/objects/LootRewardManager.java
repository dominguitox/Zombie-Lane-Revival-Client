package com.dchoc.idead.objects;

import com.dchoc.collection.ObjectPool;
import com.dchoc.hud.HUD;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.isometric.Camera;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tracking.EarnGCEvent;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.math.Rect2;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import java.util.Vector;

public class LootRewardManager {
   public static final int ANIMATION_BOUNDS = 5;
   public static final int ANIMATION_COINS = 0;
   public static final int ANIMATION_ENERGY = 4;
   public static final int ANIMATION_FOOD = 2;
   public static final int ANIMATION_STONE = 3;
   public static final int ANIMATION_XP = 1;
   public static final int INITIAL_LOOT_REWARDS_CAPACITY = 24;
   public static final int INITIAL_LOOT_REWARD_POOL_CAPACITY = 24;
   public static final int[] LOOT_REWARD_IDS = new int[]{66089, 66101, 66097, 66079, 66093, 66123};
   public static final int STATE_ACTIVE = 1;
   public static final int STATE_INACTIVE = 0;
   private static byte mState;
   private SpriteObject[] mAnimations;
   private CollisionBox mCollisionBounds;
   private boolean mConsumeTouch;
   private EarnGCEvent mEarnEvent;
   private Vector mHighlightLootRewards;
   private ObjectPool mLootRewardPool;
   private Vector mLootRewards;
   private Rect2 mOffsetBounds;
   private IsometricScene mScene;

   public LootRewardManager(IsometricScene scene) {
      this.mScene = scene;
      this.mLootRewardPool = new ObjectPool(24);
      this.mLootRewards = new Vector(24);
      this.mHighlightLootRewards = new Vector(24);

      for (int i = 0; i < 24; i++) {
         this.mLootRewardPool.add(new LootReward(scene));
      }

      this.mAnimations = AnimationsManager.load(LOOT_REWARD_IDS);
      if (DeviceWrapper.useHDIngameGraphics()) {
         this.mOffsetBounds = new Rect2(-80.0F, -80.0F, 160.0F, 160.0F);
      } else {
         this.mOffsetBounds = new Rect2(-40.0F, -40.0F, 80.0F, 80.0F);
      }

      this.mCollisionBounds = this.mAnimations[5].getCollisionBox(0);
      this.mConsumeTouch = false;
      this.mEarnEvent = new EarnGCEvent(4);
   }

   public int logicUpdate(int delta) {
      if (this.mLootRewards.size() > 0) {
         LootReward finished = null;
         int i = 0;

         do {
            LootReward reward = (LootReward)this.mLootRewards.elementAt(i);
            int result = reward.logicUpdate(delta);
            i++;
            if (result == 1) {
               finished = reward;
            }
         } while (i < this.mLootRewards.size());

         if (finished != null) {
            this.removeReward(finished);
         }
      }

      if (this.mHighlightLootRewards.size() > 0) {
         LootReward finished = null;
         int i = 0;

         do {
            LootReward reward = (LootReward)this.mHighlightLootRewards.elementAt(i);
            int result = reward.logicUpdate(delta);
            i++;
            if (result == 1) {
               finished = reward;
            }
         } while (i < this.mHighlightLootRewards.size());

         if (finished != null) {
            this.removeReward(finished);
         }
      }

      return 0;
   }

   public void processToucEvent(TouchEvent event) {
      Camera camera = this.mScene.getCamera();
      float scale = this.mScene.getScale();
      float cameraX = camera.getX();
      int touchX = event.getX();
      float cameraY = camera.getY();
      float inverse = 1.0F / scale;
      int touchY = event.getY();
      float x = inverse * touchX;
      float y = inverse * touchY;
      int type = event.getType();
      if (type == 2 || type == 3) {
         this.collectRewards(false);
         mState = 0;
      } else if (type == 0 || type == 1) {
         this.findClosestLootReward(cameraX + x, cameraY + y);
         this.collectRewards(true);
         if (mState == 1) {
            this.mConsumeTouch = true;
         }
      }

      if (this.mConsumeTouch) {
         event.setConsumed(true);
         if (event.getType() == 3 || event.getType() == 2) {
            this.mConsumeTouch = false;
         }
      }
   }

   public void findClosestLootReward(float x, float y) {
      if (this.mLootRewards.size() >= 1) {
         int i = 0;

         do {
            LootReward reward = (LootReward)this.mLootRewards.elementAt(i);
            float scale = this.mScene.getScale();
            int boundsX = this.mCollisionBounds.getX();
            int boundsY = this.mCollisionBounds.getY();
            int boundsHeight = this.mCollisionBounds.getHeight();
            int boundsWidth = this.mCollisionBounds.getWidth();
            float rewardX = reward.getX();
            float rewardY = reward.getY();
            float inverse = 1.0F / scale;
            if (Rect2.isPointInside(x, y, inverse * boundsX + rewardX, inverse * boundsY + rewardY, inverse * boundsWidth, inverse * boundsHeight)) {
               mState = 1;
               this.mLootRewards.removeElement(reward);
               this.mHighlightLootRewards.addElement(reward);
               reward.setHighlighted(true);
               i--;
            }
         } while (++i < this.mLootRewards.size());
      }
   }

   public void doDraw(Camera camera) {
      for (int i = 0; i < this.mLootRewards.size(); i++) {
         ((LootReward)this.mLootRewards.elementAt(i)).doDraw(camera);
      }

      for (int i = 0; i < this.mHighlightLootRewards.size(); i++) {
         ((LootReward)this.mHighlightLootRewards.elementAt(i)).doDraw(camera);
      }
   }

   public void addCoins(float x, float y, int amount, boolean alternateFrame) {
      this.addLootReward(x, y, (byte)0, amount, null, alternateFrame);
   }

   public void addFood(float x, float y, int amount, boolean alternateFrame) {
      this.addLootReward(x, y, (byte)2, amount, null, alternateFrame);
   }

   public void addStone(float x, float y, int amount, boolean alternateFrame) {
      this.addLootReward(x, y, (byte)1, amount, null, alternateFrame);
   }

   public void addXp(float x, float y, int amount, boolean alternateFrame) {
      this.addLootReward(x, y, (byte)3, amount, null, alternateFrame);
   }

   public void addEnergy(float x, float y, int amount, boolean alternateFrame) {
      this.addLootReward(x, y, (byte)4, amount, null, alternateFrame);
   }

   public void addItem(float x, float y, Item item, int amount, boolean alternateFrame) {
      this.addLootReward(x, y, (byte)5, amount, item, alternateFrame);
   }

   public void addLootReward(float x, float y, byte type, int amount, Item item, boolean alternateFrame) {
      LootReward reward = (LootReward)this.mLootRewardPool.create();
      if (reward == null) {
         reward = new LootReward(this.mScene);
      }

      SpriteObject animation = this.getAnimation(type, item);
      reward.init(x, y, this.mOffsetBounds, animation, type, amount, item, alternateFrame);
      this.mLootRewards.addElement(reward);
   }

   public void addComboReward(int combo, int coins) {
      PlayerCharacter player = this.mScene.getPlayer();

      for (int i = coins; i > 0; i--) {
         this.addCoins(player.getScreenX(), player.getScreenY(), 1, false);
      }

      this.mEarnEvent.trackEarn(coins);
   }

   public int getLootAmount(int type) {
      int amount = 0;

      for (int i = 0; i < this.mLootRewards.size(); i++) {
         LootReward reward = (LootReward)this.mLootRewards.elementAt(i);
         if (reward != null && reward.getType() == type) {
            amount += reward.getAmount();
         }
      }

      return amount;
   }

   public void clearRewards() {
      if (this.mLootRewards.size() >= 1) {
         do {
            LootReward reward = (LootReward)this.mLootRewards.elementAt(0);
            if (!reward.isCollected()) {
               reward.addReward();
            }

            this.removeReward(reward);
         } while (this.mLootRewards.size() > 0);
      }
   }

   public Rect2 getOffsetBounds() {
      return this.mOffsetBounds;
   }

   public void collectRewards(boolean onlyOne) {
      for (int i = 0; i < this.mHighlightLootRewards.size(); i++) {
         LootReward reward = (LootReward)this.mHighlightLootRewards.elementAt(i);
         if (!reward.isCollected()) {
            mState = 1;
            reward.setCollected(true, false);
            HUD.getComboMeter().increase();
            if (onlyOne) {
               return;
            }
         }
      }
   }

   public void removeReward(LootReward reward) {
      this.mLootRewards.removeElement(reward);
      this.mHighlightLootRewards.removeElement(reward);
      this.mLootRewardPool.add(reward);
   }

   public SpriteObject getAnimation(int type, Item item) {
      switch (type) {
         case 0:
            return this.mAnimations[0];
         case 1:
            return this.mAnimations[3];
         case 2:
            return this.mAnimations[2];
         case 3:
            return this.mAnimations[1];
         case 4:
            return this.mAnimations[4];
         case 5:
            if (item == null) {
               return null;
            }

            return AnimationsManager.loadShared(item.getIconAnimationID());
         default:
            return null;
      }
   }
}
