package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDImage;
import com.dchoc.hud.HUDInteger;
import com.dchoc.hud.HUDRewardsPanel;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.SlotBackground;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tuner.LevelDefinition;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.playhaven.PlayHaven;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;

public class WindowLevelUpBase extends Window {
   protected static final int COLLISION_ID_BACKGROUND_RECTANGLE = 7;
   protected static final int COLLISION_ID_CENTER_BUTTON_LARGE = 60;
   protected static final int COLLISION_ID_HEADER = 5;
   protected static final int COLLISION_ID_LEVEL_NUMBER = 10;
   protected static final int COLLISION_ID_REWARDS = 12;
   protected static final int COLLISION_ID_SUB_HEADER = 1;
   protected static final int[] ICON_ANIM = new int[]{74021, 74027, 74025, 74023};
   protected static final int[] ICON_TEXT = new int[]{1994, 1664, 1659, 1662};
   protected HUDAutoTextField smTitleText;
   protected HUDAutoTextField smRewardsText;
   protected RoundRectangle mRectangleBG;
   protected SlotBackground mRectangleRewards;
   protected HUDButton smButtonOk;
   protected HUDInteger smLevel;
   protected HUDRewardsPanel mRewardIconsPanel;
   protected SpriteObject smStarsExplosion;
   protected HUDImage smForegroundStars;
   protected HUDImage mXRays;
   protected int smStarsX;
   protected int smStarsY;

   public WindowLevelUpBase(int resource) {
      this.mBackground = AnimationsManager.loadShared(74619);
      this.mCollisions = new SpriteObject(resource, true, 0);
      this.smForegroundStars = new HUDImage(66991);
      this.initCollisions();
      this.mXRays = new HUDImage(66937);
      this.mXRays.setPosition(this.mWidth / 2, this.mHeight / 2);
   }

   @Override
   public void initialize() {
      this.initComponents();
      this.createObjects();
   }

   public void init(int level) {
      this.smLevel.setValue(level);
      LevelDefinition definition = Tuner.getLevel(level);
      this.mRewardIconsPanel.startAddingIcons();
      HUDRewardsPanel panel = this.mRewardIconsPanel;
      panel.addIcon(0, Toolkit.getText(ICON_TEXT[0]));
      if (definition.getMaxEnergyIncrease() > 0) {
         String text = "+" + definition.getMaxEnergyIncrease() + " " + Toolkit.getText(ICON_TEXT[1]);
         this.mRewardIconsPanel.addIcon(1, text);
      }

      if (definition.getCash() > 0) {
         String text = "+" + definition.getCash() + " " + Toolkit.getText(ICON_TEXT[2]);
         this.mRewardIconsPanel.addIcon(2, text);
      }

      if (definition.getCoins() > 0) {
         String text = "+" + definition.getCoins() + " " + Toolkit.getText(ICON_TEXT[3]);
         this.mRewardIconsPanel.addIcon(3, text);
      }

      this.mRewardIconsPanel.endAddingIcons();
   }

   @Override
   public void draw() {
      if (this.mState != 1) {
         super.draw();
         this.smStarsExplosion.draw(this.smStarsX, this.smStarsY);
      }
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      this.smStarsExplosion.logicUpdate(delta);
      if (this.smStarsExplosion.isFinishedAnimation()) {
         this.smStarsExplosion.setAnimation(0, 1, false);
         int width = Toolkit.getScreenWidth();
         int random = ToolkitHelpers.getRandomInt(0, Toolkit.getScreenWidth() / 2);
         this.smStarsX = width / 4 + random;
         int height = Toolkit.getScreenHeight();
         random = ToolkitHelpers.getRandomInt(0, Toolkit.getScreenHeight() / 2);
         this.smStarsY = height / 4 + random;
      }

      if (this.mState != 1) {
         this.smForegroundStars.logicUpdate(delta);
      }

      int activity = DCiDead.getActivityState();
      this.smButtonOk.setEnabled(activity != 1);
   }

   @Override
   public int getOpenSoundEffect() {
      return 48;
   }

   protected void initComponents() {
      this.mRectangleBG = new RoundRectangle(0);
      RoundRectangle rectangle = this.mRectangleBG;
      rectangle.setSizes(this.getCollisionBox(7));
      this.mRectangleRewards = new SlotBackground(this.getCollisionBox(12), this);
      this.smTitleText = new HUDAutoTextField(this.getCollisionBox(5));
      this.smTitleText.setCentered(true, true);
      HUDAutoTextField text = this.smTitleText;
      text.setText(1740, DCiDead.getFont(5));
      this.smRewardsText = new HUDAutoTextField(this.getCollisionBox(1));
      this.smRewardsText.setCentered(true, true);
      text = this.smRewardsText;
      text.setText(112, DCiDead.getFont(4));
      this.smButtonOk = new HUDButton(15000, this.getCollisionBox(60), 2);
      this.smButtonOk.setText(54);
      this.smLevel = new HUDInteger(66959);
      CollisionBox box = this.getCollisionBox(10);
      HUDInteger integer = this.smLevel;
      int x = box.getX();
      int width = box.getWidth();
      int y = box.getY();
      int height = box.getHeight();
      integer.setPosition(x + width / 2, y + height / 2);
      this.smStarsExplosion = AnimationsManager.loadShared(66979);
      this.smStarsExplosion.setAnimation(0, 1, false);
      this.smStarsX = Toolkit.getScreenWidth() >> 1;
      this.smStarsY = Toolkit.getScreenHeight() >> 1;
      SpriteObject[] icons = new SpriteObject[ICON_ANIM.length];

      for (int i = 0; i < ICON_ANIM.length; i++) {
         icons[i] = AnimationsManager.loadShared(ICON_ANIM[i]);
      }

      this.mRewardIconsPanel = new HUDRewardsPanel(this.getCollisionBox(12), icons, DCiDead.getFont(3));
   }

   protected void createObjects() {
      this.mObjects.addObject(this.mRectangleBG);
      this.mObjects.addObject(this.mXRays);
      this.mObjects.addObject(this.mRectangleRewards);
      this.mObjects.addObject(this.smTitleText);
      this.mObjects.addObject(this.smRewardsText);
      this.mObjects.addObject(this.smButtonOk);
      this.mObjects.addObject(this.mRewardIconsPanel);
      this.mObjects.addObject(this.smForegroundStars);
      this.mObjects.addObject(this.smLevel);
   }

   @Override
   public void setState(byte state) {
      super.setState(state);
      if (state == 1) {
         if (TutorialFlow.isCompleted() && !PlayerProfile.isStarterPack1Purchased()) {
            if (PlayerProfile.getLevel() < 3) {
               return;
            }

            if (PlayerProfile.getLevel() < 16) {
               WindowManager.openWindow((byte)35);
            }
         }
      } else if (state == 0) {
         PlayHaven.triggerLevelUp();
         GameEngine.getInstance().setSaveGameState(false);
      }
   }
}
