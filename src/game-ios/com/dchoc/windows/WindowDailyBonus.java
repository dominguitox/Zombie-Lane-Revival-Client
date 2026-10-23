package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDDailyRewardPanel;
import com.dchoc.hud.HUDImage;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.player.DailyRewards;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;

public class WindowDailyBonus extends Window {
   private int mProgress = -1;
   private HUDImage mRaysOfLight;
   private RoundRectangle mRewardBG;
   private HUDAutoTextField mTitleText;
   private HUDAutoTextField mDescriptionTextTop;
   private HUDAutoTextField mDescriptionTextLeft;
   private HUDImage mRewardIcon;
   private HUDAutoTextField mAmountText;
   private HUDButton mCollectButton;
   private HUDDailyRewardPanel[] mRewardPanels;

   public WindowDailyBonus() {
      this.mBackground = AnimationsManager.loadShared(74619);
      this.mCollisions = AnimationsManager.loadShared(66985);
      this.initCollisions();
      this.mRewardBG = new RoundRectangle(0);
      RoundRectangle background = this.mRewardBG;
      background.setSizes(this.getCollisionBox(7));
      this.mTitleText = new HUDAutoTextField(this.getCollisionBox(5));
      this.mTitleText.setCentered(true, true);
      HUDAutoTextField text = this.mTitleText;
      text.setText(36, DCiDead.getFont(5));
      this.mDescriptionTextTop = new HUDAutoTextField(this.getCollisionBox(1));
      this.mDescriptionTextTop.setCentered(true, true);
      this.mDescriptionTextTop.setSplitUsingWidth(true);
      text = this.mDescriptionTextTop;
      text.setText(43, DCiDead.getFont(4));
      this.mDescriptionTextLeft = new HUDAutoTextField(this.getCollisionBox(3));
      this.mDescriptionTextLeft.setCentered(true, true);
      text = this.mDescriptionTextLeft;
      text.setText(62, DCiDead.getFont(3));
      this.mAmountText = new HUDAutoTextField(this.getCollisionBox(4));
      this.mAmountText.setCentered(true, true);
      this.mCollectButton = new HUDButton(9001, this.getCollisionBox(6), 3);
      this.mCollectButton.setText(54);
      this.mRaysOfLight = new HUDImage(66937);
      this.mRaysOfLight.setPosition(this.mWidth / 2, this.mHeight / 2);
   }

   public void setProgress(int progress) {
      this.mProgress = progress;
      this.mRewardPanels = new HUDDailyRewardPanel[5];
      DailyRewards rewards = PlayerProfile.getDailyRewards();

      for (int i = 0; i < 5; i++) {
         SpriteObject collisions = this.mCollisions;
         int currentProgress = this.mProgress;
         HUDDailyRewardPanel panel = new HUDDailyRewardPanel(collisions, i, currentProgress, rewards.getRewardAmount(i));
         this.mRewardPanels[i] = panel;
      }

      HUDAutoTextField text = this.mAmountText;
      String amount = "x" + rewards.getRewardAmount(progress);
      text.setText(amount, DCiDead.getFont(4));
      CollisionBox box = this.getCollisionBox(2);
      int type = rewards.getRewardType(progress);
      int value = rewards.getRewardAmount(progress);
      int icon = PlayerProfile.getRewardIconID(type, value);
      this.mRewardIcon = new HUDImage(icon, box);
      this.mRewardIcon.setAnimationFrame(0);
      this.mRewardIcon.setCenterType((byte)3);
      this.mObjects.addObject(this.mRaysOfLight);
      this.mObjects.addObject(this.mRewardBG);
      this.mObjects.addObject(this.mTitleText);
      this.mObjects.addObject(this.mDescriptionTextTop);
      this.mObjects.addObject(this.mDescriptionTextLeft);
      this.mObjects.addObject(this.mCollectButton);
      this.mObjects.addObject(this.mAmountText);
      this.mObjects.addObject(this.mRewardIcon);
      this.mObjects.addObject(this.mRewardPanels[4]);
      this.mObjects.addObject(this.mRewardPanels[3]);
      this.mObjects.addObject(this.mRewardPanels[2]);
      this.mObjects.addObject(this.mRewardPanels[1]);
      this.mObjects.addObject(this.mRewardPanels[0]);
   }

   @Override
   public void draw() {
      if (this.mProgress != -1) {
         super.draw();
      }
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      if (this.mState != 1) {
         this.mRaysOfLight.logicUpdate(delta);
      }
   }

   public void giveRewards() {
      if (this.mProgress >= 0) {
         DailyRewards rewards = PlayerProfile.getDailyRewards();
         int type = rewards.getRewardType(this.mProgress);
         int amount = rewards.getRewardAmount(this.mProgress);
         if (!PlayerProfile.addReward(type, amount, 3)) {
            PlayerProfile.addRewardItem(ItemManager.getItem(type), amount);
         }
      }
   }

   @Override
   public void unloadResources() {
      this.mRewardIcon.freeImages();
      this.mRewardIcon = null;
   }
}
