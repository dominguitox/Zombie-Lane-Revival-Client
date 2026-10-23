package com.dchoc.idead.objects;

import com.dchoc.hud.HUD;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDCounter;
import com.dchoc.hud.HUDFoldable;
import com.dchoc.hud.HUDGame;
import com.dchoc.hud.HUDProgressbarExperience;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.isometric.Camera;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.items.CollectibleItem;
import com.dchoc.idead.items.Collection;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.math.Rect2;
import com.dchoc.math.Vec2;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import com.dchoc.windows.WindowTradeCollection;

public class LootReward {
   private static final int[] LOOT_SOUNDS = new int[]{43, 38, 40, 37, 41, 42};
   public static final int TYPES_COUNT = 6;
   public static final int TYPE_COINS = 0;
   public static final int TYPE_ENERGY = 4;
   public static final int TYPE_FOOD = 2;
   public static final int TYPE_INVALID = -1;
   public static final int TYPE_ITEM = 5;
   public static final int TYPE_STONE = 1;
   public static final int TYPE_XP = 3;
   private boolean mAlternateFrame;
   private int mAmount;
   private SpriteObject mAnimation;
   float mBaseX;
   float mBaseY;
   private float mBounceY;
   private boolean mCollected;
   private int mElapsedTime;
   protected boolean mHighlighted;
   private Item mItem;
   private float mOffsetX;
   private float mOffsetY;
   private Vec2 mPosition;
   private boolean mRewardAdded;
   private IsometricScene mScene;
   private byte mType;

   public LootReward(IsometricScene scene) {
      this.mScene = scene;
      this.mPosition = new Vec2();
   }

   public void init(float x, float y, Rect2 bounds, SpriteObject animation, byte type, int amount, Item item, boolean alternateFrame) {
      this.mPosition.set(x, y);
      this.mBaseX = x;
      this.mBaseY = y;
      this.mAnimation = animation;
      this.mType = type;
      this.mAmount = amount;
      this.mItem = item;
      this.mAlternateFrame = alternateFrame;
      this.mRewardAdded = false;
      this.mCollected = false;
      this.mHighlighted = false;
      this.mElapsedTime = -ToolkitHelpers.getRandomInt(700, 1000);
      float left = bounds.getX();
      float randomX = ToolkitHelpers.getRandomFloat();
      this.mOffsetX = left + randomX * bounds.getWidth();
      float top = bounds.getY();
      float randomY = ToolkitHelpers.getRandomFloat();
      this.mOffsetY = top + randomY * bounds.getHeight();
      this.mBounceY = ToolkitHelpers.getRandomFloat(75.0F, 150.0F);
      if (this.mType == 3) {
         this.addReward();
      }
   }

   public int logicUpdate(int delta) {
      this.mElapsedTime += delta;
      if (this.mCollected && this.mElapsedTime >= 750) {
         if (!this.mRewardAdded) {
            this.addReward();
         }

         iWrapper.playSoundFx(LOOT_SOUNDS[this.mType]);
         return 1;
      } else {
         if (this.mElapsedTime >= 10000 && !this.mCollected && TutorialFlow.isCompleted()) {
            this.setCollected(true, true);
         }

         float x;
         float y;
         if (this.mCollected) {
            Camera camera = this.mScene.getCamera();
            float scale = this.mScene.getScale();
            float cameraX = camera.getX();
            float cameraY = camera.getY();
            float inverse = 1.0F / scale;
            float time = this.mElapsedTime / 750.0F;
            x = (cameraX + this.mOffsetX * inverse - this.mBaseX) * time + this.mBaseX;
            y = this.mBaseY + time * (cameraY + inverse * this.mOffsetY - this.mBaseY);
         } else {
            float factor = 1.0F;
            if (this.mElapsedTime < 0) {
               factor = 1.0F - Math.abs(this.mElapsedTime / 1000.0F);
            }

            double bounce = factor * 3.0F;
            float fraction = (float)(bounce - Math.floor(bounce));
            x = this.mBaseX + factor * this.mOffsetX;
            y = this.mBounceY * (fraction * fraction - fraction) / (fraction + 1.0F) + (this.mBaseY + factor * this.mOffsetY);
         }

         this.mPosition.set(x, y);
         return 0;
      }
   }

   public void addReward() {
      if (!this.mRewardAdded) {
         MissionManager.updateCounter(2480, 80, 1);
         switch (this.mType) {
            case 0:
               PlayerProfile.addCoins(this.mAmount);
               MissionManager.updateCounter(2480, 9, this.mAmount);
               break;
            case 1:
               PlayerProfile.addStone(this.mAmount);
               MissionManager.updateCounter(2480, 11, this.mAmount);
               break;
            case 2:
               PlayerProfile.addFood(this.mAmount);
               MissionManager.updateCounter(2480, 13, this.mAmount);
               break;
            case 3:
               PlayerProfile.addXp(this.mAmount);
               break;
            case 4:
               PlayerProfile.addEnergy(this.mAmount, true);
               break;
            case 5:
               if (this.mItem != null) {
                  if (this.mItem.getType() == 47) {
                     Item item = this.mItem;
                     Collection collection = ItemManager.getCollection((CollectibleItem)item);
                     int before = 0;
                     if (collection != null) {
                        before = ItemManager.getCollectionAmount(collection);
                     }

                     PlayerProfile.getInventory().addItem(this.mItem, this.mAmount);
                     MissionManager.updateCounter(2480, this.mItem.getID(), this.mAmount);
                     MissionManager.updateCounter(2480, item.getSubType(), this.mAmount);
                     if (collection != null) {
                        if (before < ItemManager.getCollectionAmount(collection)) {
                           if (!GameEngineUtils.isAtNeigborsPlace()) {
                              ((WindowTradeCollection)WindowManager.getWindow((byte)29)).setCollection(collection);
                              WindowManager.openWindow((byte)29);
                           }
                        } else {
                           HUD.getInGame().getCollectionBar().collectiblePickedUp((CollectibleItem)item);
                        }
                     }
                  } else {
                     PlayerProfile.addRewardItem(this.mItem, this.mAmount);
                     MissionManager.updateCounter(2480, this.mItem.getID(), this.mAmount);
                  }
               }
         }

         this.mRewardAdded = true;
      }
   }

   public void doDraw(Camera camera) {
      if (!this.mCollected || this.mElapsedTime <= 749) {
         float screenScaleX = OGL.getScreenScaleX();
         float screenScaleY = OGL.getScreenScaleY();
         OGL.setScreenScale(1.0F, 1.0F);
         float positionX = this.mPosition.getX();
         float cameraX = camera.getX();
         float scaleX = this.mScene.getScale();
         float positionY = this.mPosition.getY();
         float cameraY = camera.getY();
         float scaleY = this.mScene.getScale();
         if (this.mCollected) {
            float alpha = this.mElapsedTime / -750.0F + 1.0F;
            OGL.pushParameters();
            OGL.setAlpha(alpha);
            OGL.setScale(alpha, alpha);
         }

         if (this.mAnimation != null) {
            float x = (positionX - cameraX) * scaleX;
            float y = (positionY - cameraY) * scaleY;
            if (this.mHighlighted) {
               OGL.pushParameters();
               OGL.setColorModification(-1151311776);
            }

            int frame = 0;
            if (this.mAlternateFrame) {
               frame = Math.min(this.mAnimation.getFrameCount() - 1, 1);
            }

            this.mAnimation.setAnimationFrame(frame);
            this.mAnimation.draw(x, y);
            if (this.mHighlighted) {
               OGL.popParameters();
            }
         }

         if (!TutorialFlow.isCompleted() && TutorialFlow.isCollectingRewards()) {
            OGL.setScreenScale(screenScaleX, screenScaleY);
            float arrowX = this.mPosition.getX() - camera.getX();
            float arrowY = this.mPosition.getY() - camera.getY();
            TutorialFlow.drawArrow((int)arrowX, (int)arrowY, 2);
         }

         if (this.mCollected) {
            OGL.popParameters();
         }

         OGL.setScreenScale(screenScaleX, screenScaleY);
      }
   }

   public void setCollected(boolean collected, boolean automatic) {
      this.mCollected = collected;
      if (collected) {
         this.mElapsedTime = 10000;
         this.mBaseX = this.mPosition.getX();
         this.mBaseY = this.mPosition.getY();
         int targetX = Toolkit.getScreenWidth();
         float targetY = 0.0F;
         HUDGame game = HUD.getInGame();
         switch (this.mType) {
            case 0:
            case 1:
            case 2:
               HUDCounter counter;
               if (this.mType == 0) {
                  counter = game.getCoinsCounter();
               } else if (this.mType == 1) {
                  counter = game.getBricksCounter();
               } else {
                  counter = game.getFoodCounter();
               }

               targetX = counter.getDestinationPositionForLootX();
               targetY = counter.getDestinationPositionForLootY();
               break;
            case 3:
               HUDProgressbarExperience progressbar = game.getXPProgressbar();
               targetX = progressbar.getX();
               targetY = progressbar.getY();
            case 4:
            default:
               break;
            case 5:
               HUDFoldable foldable = game.getFoldable();
               HUDButton button;
               if (foldable.isOpened()) {
                  button = foldable.getInventoryButton();
               } else {
                  button = foldable.getMenuButton();
               }

               targetX = button.getX();
               targetY = button.getY();
         }

         if (automatic) {
            this.mOffsetX = targetX;
            this.mOffsetY = targetY;
         } else {
            iWrapper.playSoundFx(39);
            this.mOffsetX = targetX;
            this.mOffsetY = targetY;
            if (!this.mRewardAdded && this.mItem != null) {
               this.addReward();
            }
         }

         this.mElapsedTime = 0;
      }
   }

   public boolean isCollected() {
      return this.mCollected;
   }

   public void setHighlighted(boolean highlighted) {
      this.mHighlighted = highlighted;
   }

   public boolean isRewardAdded() {
      return this.mRewardAdded;
   }

   public int getType() {
      return this.mType;
   }

   public int getAmount() {
      return this.mAmount;
   }

   public SpriteObject getAnimation() {
      return this.mAnimation;
   }

   public Item getItem() {
      return this.mItem;
   }

   public float getX() {
      return this.mPosition.getX();
   }

   public float getY() {
      return this.mPosition.getY();
   }
}
