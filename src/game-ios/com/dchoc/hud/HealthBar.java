package com.dchoc.hud;

import com.dchoc.idead.GameEngine;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.characters.CharacterObject;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.characters.ZombieCharacter;
import com.dchoc.idead.isometric.Camera;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.ConstructionItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.objects.ConstructionObject;
import com.dchoc.idead.objects.LootObject;
import com.dchoc.idead.objects.ObjectWithHealth;
import com.dchoc.idead.player.InventoryItem;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.windows.WindowTooltip;

public class HealthBar {
   private static final int COLOR_BLUE = 0;
   private static final int COLOR_RED = 1;
   private static final int COLOR_MAX = 2;
   private static final int STATE_NONE = -1;
   private static final int STATE_OPENING = 0;
   private static final int STATE_OPENED = 1;
   private static final int STATE_CHANGE = 2;
   private static final int STATE_CLOSING = 3;
   public static final int TOUCH_TYPE_NONE = -1;
   public static final int TOUCH_TYPE_HOLD = 0;
   public static final int TOUCH_TYPE_TAPPED = 1;
   public static final int TOUCH_TYPE_TAPPED_AUTO = 2;
   private static int[] smPieceLeft = new int[2];
   private static int[] smPieceRight = new int[2];
   private static int[] smSliceBoxX = new int[2];
   private static int[] smSliceBoxWidth = new int[2];
   private static int[] smBoxAreaWidth = new int[2];
   private static int[] smBoxAreaHeight = new int[2];
   private static SpriteObject smSlash;
   private static SpriteObject smBlueBar;
   private static SpriteObject smRedBar;
   private static int smSlashWidth;
   public int mTouchType = -1;
   private int mPositionType;
   private boolean mEnableBar;
   private boolean mCloseAfterOpening;
   private boolean mCloseAfterValueChanges;
   private boolean mRepeatChange;
   private boolean mScaling;
   private int mForcedCloseTimer;
   private int mCloseTimer;
   private int mScaleTimer;
   private int mScaleTimeEnd;
   private byte mMaxCount;
   private byte mHealthCount;
   private byte mStartCount;
   private byte mTappedCount;
   private byte mPendingChange;
   private int mChangeStep;
   private int mPlayerStones;
   private int mPlayerCoins;
   private int mChangeTimer;
   private float mChangingBarWidth;
   private float mBarSegmentWidth;
   private int mBarX;
   private int mBarY;
   private int mNumberX;
   private int mNumberY;
   private float mBarScale;
   private int mValueWidth;
   private int mValueMaxWidth;
   private int mCounterWidth;
   private HUDInteger mValue;
   private HUDInteger mValueMax;
   private IsometricObject mTargetObject;
   private boolean mFixable;
   private float mTargetX;
   private float mTargetY;
   private int mBarColor;
   private int mBarWidth;
   private int mBarHeight;
   private int mState = -1;

   public HealthBar() {
      this.mPositionType = -1;
      this.mValue = new HUDInteger(65571);
      this.mValueMax = new HUDInteger(65571);
      this.mValue.setCenterType((byte)0);
      this.mValueMax.setCenterType((byte)0);
      this.mFixable = false;
   }

   public static void loadAnimations() {
      smSlash = AnimationsManager.loadShared(66745);
      smSlashWidth = smSlash.getWidth();
      smBlueBar = AnimationsManager.loadShared(73691);
      smRedBar = AnimationsManager.loadShared(73689);
      int[] layout = IsometricUtils.calculateBarLayout(smBlueBar);
      smBoxAreaWidth[0] = layout[0];
      smBoxAreaHeight[0] = layout[1];
      smSliceBoxX[0] = layout[2];
      smSliceBoxWidth[0] = layout[3];
      smPieceLeft[0] = layout[4];
      smPieceRight[0] = layout[5];
      layout = IsometricUtils.calculateBarLayout(smRedBar);
      smBoxAreaWidth[1] = layout[0];
      smBoxAreaHeight[1] = layout[1];
      smSliceBoxX[1] = layout[2];
      smSliceBoxWidth[1] = layout[3];
      smPieceLeft[1] = layout[4];
      smPieceRight[1] = layout[5];
   }

   public void logicUpdate(int delta) {
      if (this.mTargetObject == null && Integer.compareUnsigned(this.mState, 2) < 0) {
         this.switchState(3);
      }

      if (this.mScaling) {
         boolean ended = false;
         int timer = this.mScaleTimer;
         int end = this.mScaleTimeEnd;
         if (timer < end) {
            timer += delta;
            this.mScaleTimer = timer;
            if (end <= timer) {
               if (this.mState == 0) {
                  this.switchState(1);
               }

               this.mScaling = false;
               ended = true;
            }
         } else if (end < timer) {
            timer -= delta;
            this.mScaleTimer = timer;
            if (timer <= end) {
               this.switchState(-1);
               this.mScaling = false;
               ended = true;
            }
         }

         if (!ended) {
            float scale = timer / 250.0F;
            if (scale > 1.0F) {
               scale = 1.0F;
            } else if (scale < 0.1F) {
               scale = 0.0F;
            }

            this.mBarScale = scale;
         }
      }

      int state = this.mState;
      if (state == 1) {
         if (!this.mCloseAfterOpening) {
            if (this.mCloseTimer < 1) {
               if (0 < this.mForcedCloseTimer) {
                  this.mForcedCloseTimer -= delta;
                  if (this.mForcedCloseTimer < 1) {
                     this.switchState(3);
                  }
               }
            } else {
               this.mCloseTimer -= delta;
               if (this.mCloseTimer < 1) {
                  this.mCloseTimer = 0;
                  this.switchState(3);
               }
            }
         } else if (this.mPendingChange == 0) {
            this.mCloseAfterOpening = false;
            this.mCloseTimer = 1000;
         }

         state = this.mState;
      }

      if (state == 2) {
         int timer = this.mChangeTimer;
         if (0 < timer) {
            timer -= delta;
            if (timer < 1) {
               timer = 0;
            }

            this.mChangeTimer = timer;
         }

         if (timer == 0) {
            this.switchState(1);
            int step = this.mChangeStep;
            byte health = (byte)(this.mHealthCount + step);
            this.mHealthCount = health;
            byte pending = (byte)(this.mPendingChange - step);
            this.mPendingChange = pending;
            this.mChangingBarWidth = this.mChangingBarWidth + step * this.mBarSegmentWidth;
            if (health < 0) {
               this.mHealthCount = 0;
            }

            if (pending < 0) {
               this.mPendingChange = 0;
            }

            this.updateNumbers(false);
            this.updateCounterWidth();
            this.mNumberX = this.mBarX + (this.mBarWidth - this.mCounterWidth >> 1);
            if (!this.mCloseAfterValueChanges) {
               if (this.mRepeatChange) {
                  this.mRepeatChange = false;
                  this.switchState(2);
               }
            } else {
               this.mCloseAfterValueChanges = false;
               this.mCloseTimer = 1000;
            }
         }
      }

      if (this.mState != -1 && this.mTargetObject != null) {
         this.updatePosition();
      }
   }

   public void draw() {
      if (this.mScaling) {
         float scale = this.mBarScale;
         if (scale == 0.0F) {
            return;
         }

         OGL.setScale(scale, scale);
      }

      SpriteObject bar = this.getBarAnimation();
      boolean done = false;
      int width1;
      int width0;
      if (!this.mFixable && this.mTargetObject.getType() != 0 && this.mTargetObject.getType() != 5) {
         int maxWidth = this.mBarWidth;
         int progress = this.getProgressCount();
         byte max = this.mMaxCount;
         width0 = maxWidth * progress / max;
         width1 = 0;
         if (this.mState != 2) {
            width1 = this.mHealthCount * this.mBarWidth / max;
            done = true;
         }
      } else {
         int maxWidth = this.mBarWidth;
         int progress = this.getProgressCount();
         byte max = this.mMaxCount;
         width1 = maxWidth * progress / max;
         width0 = 0;
         if (this.mState != 2) {
            width0 = this.mHealthCount * this.mBarWidth / max;
            done = true;
         }
      }

      if (!done) {
         float change = this.mBarSegmentWidth * Math.abs(this.mChangeStep);
         float partial = change * (300 - this.mChangeTimer) / 300.0F;
         if (change < partial) {
            partial = change;
         }

         if (!this.mFixable && this.mTargetObject.getType() != 0 && this.mTargetObject.getType() != 5) {
            width1 = (int)(this.mChangingBarWidth - partial);
            if (width1 < width0) {
               width1 = width0;
            }
         } else {
            width0 = (int)(partial + this.mChangingBarWidth);
            if (width1 < width0) {
               width0 = width1;
            }
         }
      }

      int x = this.mBarX;
      int y = this.mBarY;
      int color = this.mBarColor;
      int pieceLeft = smPieceLeft[color];
      int pieceRight = smPieceRight[color];
      int areaWidth = smBoxAreaWidth[color];
      int sliceWidth = smSliceBoxWidth[color];
      int sliceX = smSliceBoxX[color];
      bar.setAnimationFrame(2);
      IsometricUtils.drawBar(
         bar, x, y, pieceLeft, pieceRight, sliceWidth, sliceX, areaWidth, this.mBarWidth, this.mBarWidth, this.mBarScale, this.mScaling, true
      );
      bar.setAnimationFrame(1);
      IsometricUtils.drawBar(bar, x, y, pieceLeft, pieceRight, sliceWidth, sliceX, areaWidth, this.mBarWidth, width1, this.mBarScale, this.mScaling, false);
      bar.setAnimationFrame(0);
      IsometricUtils.drawBar(bar, x, y, pieceLeft, pieceRight, sliceWidth, sliceX, areaWidth, this.mBarWidth, width0, this.mBarScale, this.mScaling, false);
      int numberX = this.mNumberX;
      int numberY = this.mNumberY;
      this.mValue.setPosition(numberX, numberY - this.mValue.getValueHeight() / 2);
      this.mValue.draw();
      int slashX = this.mValueWidth + numberX + smSlashWidth / 2;
      smSlash.draw(slashX, numberY);
      int slashWidth = smSlashWidth;
      this.mValueMax.setPosition(slashX + slashWidth / 2, numberY - this.mValueMax.getValueHeight() / 2);
      this.mValueMax.draw();
      if (this.mScaling) {
         OGL.setScale(1.0F, 1.0F);
      }
   }

   public int getWidth() {
      return this.mBarWidth;
   }

   public int getHeight() {
      SpriteObject bar = smBlueBar;
      return bar != null ? bar.getCollisionBox(1).getHeight() : 0;
   }

   public int getHealthCount() {
      return this.mHealthCount;
   }

   public int getMaxCount() {
      return this.mMaxCount;
   }

   public IsometricObject getTargetObject() {
      return this.mTargetObject;
   }

   public int getPositionType() {
      return this.mPositionType;
   }

   public boolean initHealthBar(IsometricObject target, boolean reset) {
      this.mTargetX = -1.0F;
      this.mTargetY = -1.0F;
      if (reset) {
         this.mCloseAfterOpening = false;
         this.mCloseAfterValueChanges = false;
         this.mRepeatChange = false;
      }

      if (!this.mEnableBar) {
         if (this.mTargetObject == target) {
            return false;
         }

         if (this.initCounters(target, reset)) {
            this.mTargetObject = target;
            this.mEnableBar = true;
         } else if (!this.mEnableBar) {
            return false;
         }
      }

      this.increaseTappedCount();
      return true;
   }

   public boolean updateAtActionStart(IsometricObject target) {
      boolean result = false;
      if (this.mTargetObject == target) {
         if (this.mState != 2) {
            this.switchState(2);
            return true;
         }

         result = true;
         this.mRepeatChange = true;
      }

      return result;
   }

   public void checkCloseAfterAction(IsometricObject target) {
      if (this.mTargetObject == target) {
         if (this.mCloseTimer != 0) {
            return;
         }

         if (this.mState == 0) {
            this.mCloseAfterOpening = true;
            return;
         }

         PlayerCharacter player = IsometricUtils.getPlayer();
         if (!player.hasActionWith(this.mTargetObject)) {
            if (this.mState == 1) {
               this.mCloseTimer = 1000;
               return;
            }

            this.mCloseAfterValueChanges = true;
         }
      }
   }

   public void checkCloseAfterRelease(TouchEvent event) {
      if (event.getType() == 2 && this.mTouchType == 0) {
         if (this.mState == 3) {
            return;
         }

         if (this.mState == 0) {
            this.mCloseAfterOpening = true;
            return;
         }

         if (this.mCloseTimer == 0) {
            this.mCloseTimer = 1000;
         }
      }
   }

   public void close() {
      if (this.mTouchType != 2) {
         int state = this.mState;
         if (state != -1 && state != 3) {
            if (state != 0) {
               this.switchState(3);
               return;
            }

            this.mState = 3;
            this.mScaleTimeEnd = 0;
         }
      }
   }

   public boolean isClosed() {
      return !this.mEnableBar;
   }

   public void setBar() {
      this.setBarColor();
      if (this.getBarAnimation() == null) {
         this.mEnableBar = false;
      } else {
         this.setBarSize();
         this.updateNumbers(true);
         this.updateCounterWidth();
         int health = this.mHealthCount;
         int width = health * this.mBarWidth / this.mMaxCount;
         this.mChangingBarWidth = width;
         this.mBarSegmentWidth = (float)width / health;
         this.updatePosition();
         if (this.mState == 3) {
            this.mState = 0;
            this.mScaleTimeEnd = 250;
         } else if (this.mState == -1) {
            this.mNumberX = this.mBarX + (this.mBarWidth - this.mCounterWidth >> 1);
            this.mNumberY = this.mBarY + (this.mBarHeight >> 1);
            this.switchState(0);
         }
      }
   }

   public boolean isCloseAfterOpening() {
      return this.mCloseAfterOpening;
   }

   public int getCloseTimer() {
      return this.mCloseTimer;
   }

   public int getTouchType() {
      return this.mTouchType;
   }

   public void setTouchType(int type) {
      int forced = -1;
      this.mTouchType = type;
      if (type == 2) {
         forced = 1000;
      }

      this.mForcedCloseTimer = forced;
   }

   public void switchState(int state) {
      if (state == -1) {
         this.mTargetObject = null;
         this.mCloseTimer = 0;
         this.mScaleTimer = 0;
         this.mScaleTimeEnd = 0;
         this.mEnableBar = false;
         this.mScaling = false;
         this.mPositionType = -1;
         this.mState = -1;
      } else if (state != 0) {
         if (state == 2) {
            this.mChangeTimer = this.mHealthCount < 1 ? 0 : 300;
         } else if (state == 3) {
            if (this.mTargetObject != null && this.mTargetObject.isHighlighted()) {
               this.mTargetObject.setHighlighted(false);
            }

            int timer = 250;
            int end = 0;
            this.mScaleTimer = timer;
            this.mScaleTimeEnd = end;
            this.mScaling = true;
            this.mState = state;
            return;
         }

         this.mState = state;
      } else {
         int timer = 0;
         int end = 250;
         this.mScaleTimer = timer;
         this.mScaleTimeEnd = end;
         this.mScaling = true;
         this.mState = state;
      }
   }

   public void updateNumbers(boolean updateMax) {
      this.mValue.setValue(this.mHealthCount);
      this.mValueWidth = this.mValue.getWidth();
      if (updateMax) {
         this.mValueMax.setValue(this.mMaxCount);
         this.mValueMaxWidth = this.mValueMax.getWidth();
      }

      WindowTooltip tooltip = HUD.getTooltip(this.mTargetObject);
      if (tooltip != null) {
         tooltip.updateCounterText(this.mHealthCount, this.mMaxCount);
      }
   }

   public void updateCounterWidth() {
      this.mCounterWidth = this.mValueWidth + this.mValueMaxWidth;
      this.mCounterWidth = smSlashWidth + this.mValueWidth + this.mValueMaxWidth;
   }

   public SpriteObject getBarAnimation() {
      if (this.mBarColor == 1) {
         return smRedBar;
      } else {
         return this.mBarColor != 0 ? null : smBlueBar;
      }
   }

   public void setBarColor() {
      if (this.mTargetObject != null) {
         int type = this.mTargetObject.getType();
         if (Integer.compareUnsigned(type, 7) < 0) {
            int color;
            if ((1 << (type & 0xFF) & 105) == 0) {
               if (type != 2) {
                  return;
               }

               color = 1;
            } else {
               color = 0;
            }

            this.mBarColor = color;
         }
      }
   }

   public void setBarSize() {
      int type = this.mTargetObject.getType();
      boolean write = true;
      int width = 0;
      switch (type) {
         case 0:
         case 3:
         case 5:
         case 6:
            width = 70;
            break;
         case 1:
         case 4:
         default:
            write = false;
            break;
         case 2:
            CharacterObject character = (CharacterObject)this.mTargetObject;
            if (character.getHealthMax() < 3) {
               width = 50;
            } else if (character.getHealthMax() < 5) {
               width = 65;
            } else {
               width = 80;
            }
      }

      if (write) {
         this.mBarWidth = width;
      }

      if (DeviceWrapper.isIPAD() || DeviceWrapper.isIPHONE4() || DeviceWrapper.isVGA()) {
         this.mBarWidth <<= 1;
      }

      this.mBarHeight = this.getBarAnimation().getCollisionBox(1).getHeight();
   }

   public boolean initCounters(IsometricObject target, boolean reset) {
      this.mMaxCount = 0;
      this.mStartCount = 0;
      this.mTappedCount = 0;
      this.mPendingChange = 0;
      this.mFixable = false;
      if (target == null) {
         return false;
      }

      if (target.getType() == 0) {
         ConstructionObject construction = (ConstructionObject)target;
         if (construction.getState() != 0) {
            return false;
         }

         this.mMaxCount = (byte)((ConstructionItem)construction.getItem()).getRequiredClicks();
         byte clicks = (byte)construction.getClicks();
         this.mStartCount = clicks;
         this.mHealthCount = clicks;
         this.mPlayerStones = PlayerProfile.getStone();
         this.mPlayerCoins = PlayerProfile.getCoins();
      } else {
         if (target.getType() == 6) {
            this.mMaxCount = 1;
            this.mStartCount = 1;
            this.mHealthCount = 1;
            this.mChangeStep = -1;
            return true;
         }

         if (target.getType() != 5) {
            if (target.getType() == 3) {
               LootObject loot = (LootObject)target;
               this.mFixable = loot.isFixable();
               byte max = (byte)loot.getMaxHealth();
               this.mMaxCount = max;
               byte start;
               int step;
               if (!this.mFixable) {
                  start = (byte)loot.getHealth();
                  step = -1;
                  this.mStartCount = start;
               } else {
                  this.mMaxCount = (byte)(max + 1);
                  start = (byte)(max + 1 - (byte)loot.getHealth());
                  this.mStartCount = start;
                  step = 1;
               }

               this.mChangeStep = step;
               this.mHealthCount = start;
               if (!reset) {
                  return true;
               }

               if (this.mFixable) {
                  return true;
               }

               this.mMaxCount = start;
            } else {
               if (target.getType() != 2) {
                  return false;
               }

               ZombieCharacter zombie = (ZombieCharacter)target;
               this.mMaxCount = (byte)zombie.getHealthMax();
               byte health = (byte)zombie.getHealth();
               this.mStartCount = health;
               this.mHealthCount = health;
               PlayerCharacter player = IsometricUtils.getPlayer();
               this.mChangeStep = -zombie.getHitDamage(player.getWeapon());
            }

            return true;
         }

         ObjectWithHealth fence = (ObjectWithHealth)target;
         this.mMaxCount = (byte)fence.getMaxHealth();
         byte health = (byte)fence.getHealth();
         this.mStartCount = health;
         this.mHealthCount = health;
      }

      this.mChangeStep = 1;
      return true;
   }

   public void increaseTappedCount() {
      IsometricObject target = this.mTargetObject;
      if (target != null && this.mTouchType != 0) {
         boolean checkMax = false;
         int tapped = 0;
         int start = 0;
         if (target.getType() == 0) {
            ConstructionItem item = (ConstructionItem)this.mTargetObject.getItem();
            byte count = this.mTappedCount;
            if (this.mMaxCount <= this.mStartCount + count) {
               return;
            }

            int stones = this.mPlayerStones;
            if (stones - item.getStonePerClick() * count < item.getStonePerClick()) {
               return;
            }

            count = this.mTappedCount;
            int coins = this.mPlayerCoins;
            if (coins - item.getCoinsPerClick() * count < item.getCoinsPerClick()) {
               return;
            }
         } else if (this.mTargetObject.getType() == 5) {
            tapped = this.mTappedCount;
            start = this.mStartCount;
            checkMax = true;
         } else if (this.mTargetObject.getType() == 6) {
            if (this.mStartCount - this.mTappedCount - Math.abs(this.mChangeStep) < 0) {
               return;
            }
         } else if (this.mTargetObject.getType() != 3) {
            if (this.mStartCount - this.mTappedCount < 1) {
               return;
            }

            if (this.mTargetObject.getType() != 2) {
               return;
            }

            ZombieCharacter zombie = (ZombieCharacter)this.mTargetObject;
            InventoryItem weapon = PlayerProfile.getCurrentWeapon();
            Item item = weapon.getItem();
            if (weapon == null) {
               return;
            }

            if (weapon.getItem() == null) {
               return;
            }

            if (zombie.isKilledOnlyCraftable()) {
               if (!zombie.isKilledOnlyCraftable()) {
                  return;
               }

               if (!((WeaponItem)item).isCrafted()) {
                  return;
               }
            }

            if (this.mStartCount - this.mTappedCount - Math.abs(this.mChangeStep) < 0) {
               this.mTappedCount = this.mStartCount;
               return;
            }
         } else {
            tapped = this.mTappedCount;
            start = this.mStartCount;
            if (!this.mFixable) {
               if (start - tapped - Math.abs(this.mChangeStep) < 0) {
                  return;
               }
            } else {
               checkMax = true;
            }
         }

         if (!checkMax || this.mMaxCount >= Math.abs(this.mChangeStep) + tapped + start) {
            this.mTappedCount = (byte)(Math.abs(this.mChangeStep) + this.mTappedCount);
            this.mPendingChange = (byte)(Math.abs(this.mChangeStep) + this.mPendingChange);
         }
      }
   }

   public int getProgressCount() {
      int count = 0;
      IsometricObject target = this.mTargetObject;
      if (target != null) {
         if (this.mFixable || target.getType() == 0 || this.mTargetObject.getType() == 5) {
            return this.mStartCount + this.mTappedCount;
         }

         if (this.mTargetObject.getType() != 6 && this.mTargetObject.getType() != 3 && this.mTargetObject.getType() != 2) {
            return 0;
         }

         count = this.mStartCount - this.mTappedCount;
      }

      return count;
   }

   public void updatePosition() {
      IsometricScene scene = GameEngine.getInstance().getScene();
      Camera camera = scene.getCamera();
      float scale = scene.getScale();
      float screenX = this.mTargetObject.getScreenX();
      float cameraX = camera.getX();
      float screenY = this.mTargetObject.getScreenY();
      float cameraY = camera.getY();
      float x = scale * (screenX - cameraX);
      float y = scale * (screenY - cameraY);
      if (x != this.mTargetX || y != this.mTargetY) {
         this.mTargetX = x;
         this.mTargetY = y;
         this.updatePositionWithTarget();
      }
   }

   public void updatePositionWithTarget() {
      int position = this.mPositionType;
      IsometricObject target = this.mTargetObject;
      int[] xy = IsometricUtils.getPositionTypeXY(target, position, this.getWidth(), this.getHeight(), 0, 0, false, true, -1);
      if (xy != null) {
         int x = xy[0];
         this.mBarX = x - this.getWidth() / 2;
         int y = xy[1] - this.getHeight() / 2;
         this.mBarY = y;
         this.mPositionType = xy[2];
         this.mNumberX = this.mBarX + (this.mBarWidth - this.mCounterWidth >> 1);
         this.mNumberY = y + (this.mBarHeight >> 1);
      }
   }
}
