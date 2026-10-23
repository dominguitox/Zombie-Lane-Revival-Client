package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.objects.LootRewardManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tuner.ComboDefinition;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.Animation;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;

public class HUDCombo extends HUDObject {
   private static int smState;
   private int mLevel;
   private ComboDefinition mNextCombo;
   private int mClickCount;
   private SpriteObject mStarExplosion;
   private SpriteObject mComboMeterBack = AnimationsManager.load(65579);
   private SpriteObject mComboAnimationMoveIn;
   private SpriteObject mComboAnimationMoveOut;
   private SpriteObject mCurrentAnimationMove;
   private HUDInteger mComboState;
   private SpriteObject mSymbolX;
   private CollisionBox mCounterBox;
   private CollisionBox mFillBox;
   private CollisionBox mSymbolBox;
   private long mLastClickTime;
   private long mLastGlowTime;
   private int mCurrentAnimationGlowIndex;
   private SpriteObject[][] mComboMeterFills;

   public HUDCombo(int x, int y) {
      super((byte)-1);
      this.mStarExplosion = AnimationsManager.load(74879);
      this.mStarExplosion.setAnimation(0, 0, false);
      this.mComboMeterFills = new SpriteObject[5][];
      this.mComboMeterFills[0] = new SpriteObject[]{AnimationsManager.load(65605), AnimationsManager.load(65609), AnimationsManager.load(65615)};
      this.mComboMeterFills[1] = new SpriteObject[]{AnimationsManager.load(65603), AnimationsManager.load(65631), AnimationsManager.load(65621)};
      this.mComboMeterFills[2] = new SpriteObject[]{AnimationsManager.load(65629), AnimationsManager.load(65625), AnimationsManager.load(65607)};
      this.mComboMeterFills[3] = new SpriteObject[]{AnimationsManager.load(65611), AnimationsManager.load(65627), AnimationsManager.load(65623)};
      this.mComboMeterFills[4] = new SpriteObject[]{AnimationsManager.load(65617), AnimationsManager.load(65619), AnimationsManager.load(65633)};
      this.mComboAnimationMoveIn = AnimationsManager.load(65583);
      this.mComboAnimationMoveOut = AnimationsManager.load(65585);
      this.mSymbolX = AnimationsManager.load(65745);
      this.mComboMeterBack.setAnimation(0, -1, false);
      this.mCurrentAnimationMove = this.mComboAnimationMoveIn;
      this.mComboState = new HUDInteger(65563, 65559);
      this.mCounterBox = this.mComboMeterBack.getCollisionBox(0);
      this.mFillBox = this.mComboMeterBack.getCollisionBox(1);
      this.mSymbolBox = this.mSymbolX.getCollisionBox(0);
      this.changeLevel(0);
      this.mPosX = x;
      this.mPosY = y;
      this.setState(0);
   }

   @Override
   public void logicUpdate(int delta) {
      switch (smState) {
         case 1:
            this.updatePosition();
            if (this.mCurrentAnimationMove.isFinishedAnimation()) {
               this.setState(2);
            }
            break;
         case 2:
            if (System.currentTimeMillis() - this.mLastClickTime > 3000L && (TutorialFlow.isCompleted() || !TutorialFlow.isCollectingRewards())) {
               this.setState(3);
            }
            break;
         case 3:
            if (System.currentTimeMillis() - this.mLastClickTime > 7000L) {
               this.setState(4);
            }
            break;
         case 4:
            this.updatePosition();
            if (this.mCurrentAnimationMove.isFinishedAnimation()) {
               this.setState(0);
            }
      }

      if (smState != 0) {
         if (smState != 3) {
            this.mComboMeterBack.logicUpdate(delta);
         }

         if (System.currentTimeMillis() - this.mLastGlowTime > 250L) {
            this.mLastGlowTime = System.currentTimeMillis();
            this.mCurrentAnimationGlowIndex++;
            if (this.mCurrentAnimationGlowIndex >= this.mComboMeterFills[Math.min(this.mLevel, this.mComboMeterFills.length - 1)].length) {
               this.mCurrentAnimationGlowIndex = 0;
            }
         }

         int count = this.mComboMeterFills[0][0].getFrameCount();
         int frame = this.mClickCount * count / this.mNextCombo.getRequiredItems();
         if (frame == count - 1 && this.mClickCount < this.mNextCombo.getRequiredItems()) {
            frame--;
         }

         if (frame == 0 && this.mClickCount != 0) {
            frame = 1;
         }

         SpriteObject fill = this.mComboMeterFills[Math.min(this.mLevel, this.mComboMeterFills.length - 1)][this.mCurrentAnimationGlowIndex];
         fill.setAnimationFrame(Math.min(frame, this.mComboMeterFills[0][0].getFrameCount()));
         this.mComboState.logicUpdate(delta);
         this.mCurrentAnimationMove.logicUpdate(delta);
         this.mStarExplosion.logicUpdate(delta);
      }
   }

   public void increase() {
      if (smState == 0 || smState == 4) {
         this.setState(1);
      } else if (smState != 1) {
         this.setState(2);
      }

      this.mLastClickTime = System.currentTimeMillis();
      int clicks = this.mClickCount++;
      if (this.mNextCombo.getRequiredItems() <= clicks + 1) {
         LootRewardManager rewards = IsometricUtils.getScene().getLootRewards();
         int type = this.mNextCombo.getRewardType();
         int amount = this.mNextCombo.getRewards();
         rewards.addComboReward(type, amount);
         this.changeLevel(this.mLevel + 1);
         MissionManager.updateCounter(2482, 80, 1);
         iWrapper.playSoundFx(45);
         if (this.mLevel > 0) {
            this.mStarExplosion.setAnimation(0, 1, false);
         }
      }

      this.mComboState.setValue(this.mLevel + 1);
   }

   @Override
   public void draw() {
      if (smState != 0) {
         float alpha = 1.0F;
         if (smState == 3 || smState == 4) {
            OGL.pushParameters();
            alpha = 1.0F - 0.75F * ((int)(System.currentTimeMillis() - this.mLastClickTime) - 3000) / 4000.0F;
            OGL.setAlpha(alpha);
         }

         this.mComboMeterBack.draw(this.mPosX, this.mPosY);
         SpriteObject fill = this.mComboMeterFills[Math.min(this.mLevel, this.mComboMeterFills.length - 1)][this.mCurrentAnimationGlowIndex];
         int px = this.mPosX;
         int bx = this.mFillBox.getX();
         int py = this.mPosY;
         int by = this.mFillBox.getY();
         fill.draw(bx + px, by + py);
         int width = this.mComboState.getWidth();
         int symbolWidth = this.mSymbolBox.getWidth();
         px = this.mPosX;
         bx = this.mCounterBox.getX();
         py = this.mPosY;
         by = this.mCounterBox.getY();
         int height = this.mCounterBox.getHeight();
         int y = by + py + height / 2;
         int x = bx + px - (width + symbolWidth) / 2;
         this.mSymbolX.draw(x + this.mSymbolX.getWidth() / 2, y);
         symbolWidth = this.mSymbolBox.getWidth();
         this.mComboState.setAlpha(alpha);
         x += symbolWidth;
         this.mComboState.setPosition(x + this.mComboState.getWidth() / 2, y);
         this.mComboState.draw();
         this.mStarExplosion.draw(x, y);
         if (smState == 3 || smState == 4) {
            OGL.popParameters();
         }
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
   }

   @Override
   public void callback(int id) {
   }

   public void changeLevel(int level) {
      ComboDefinition next = Tuner.getNextCombo(level);
      if (next != null) {
         this.mLevel = level;
         this.mNextCombo = next;
      }

      MissionManager.updateCounter(3287, 80, 1);
      switch (level) {
         case 1:
         case 2:
            MissionManager.updateCounter(3287, -1, 1);
            break;
         case 3:
            MissionManager.updateCounter(3287, 3306, 1);
            break;
         case 4:
            MissionManager.updateCounter(3287, 3288, 1);
      }

      this.mComboState.setValue(this.mLevel + 1);
      this.mClickCount = 0;
   }

   public void updatePosition() {
      Animation animation = this.mCurrentAnimationMove.getCurrentAnimationData();
      int elapsed = this.mCurrentAnimationMove.getElapsedTime();
      this.mPosY = animation.getTimelineValue(2, elapsed, true);
   }

   public void setState(int state) {
      if (state == 4) {
         this.mCurrentAnimationMove = this.mComboAnimationMoveOut;
         this.mCurrentAnimationMove.setAnimation(0, 1, false);
      } else if (state == 1) {
         this.mCurrentAnimationMove = this.mComboAnimationMoveIn;
         this.mCurrentAnimationMove.setAnimation(0, 1, false);
         this.changeLevel(0);
      }

      smState = state;
   }
}
