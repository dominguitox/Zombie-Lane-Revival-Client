package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDImage;
import com.dchoc.hud.HUDRewardsPanel;
import com.dchoc.hud.Rectangle;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.missions.Mission;
import com.dchoc.playhaven.PlayHaven;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public class WindowMissionComplete extends Window {
   private HUDButton mButtonOk;
   private Mission mMission;
   private RoundRectangle mRectangleBG;
   private Rectangle mDelimiterRectangle;
   private HUDAutoTextField mTitleText;
   private HUDAutoTextField mMissionName;
   private HUDAutoTextField mRewardsText;
   private HUDRewardsPanel mRewardIconsPanel;
   private HUDImage mXRays;

   public WindowMissionComplete() {
      this.mBackground = AnimationsManager.loadShared(74621);
      this.mCollisions = AnimationsManager.loadShared(66943);
      this.initCollisions();
      this.mRectangleBG = new RoundRectangle(0);
      RoundRectangle rectangle = this.mRectangleBG;
      rectangle.setSizes(this.getCollisionBox(7));
      this.mTitleText = new HUDAutoTextField(this.getCollisionBox(5));
      HUDAutoTextField text = this.mTitleText;
      text.setText(125, DCiDead.getFont(5));
      this.mTitleText.setCentered(true, true);
      this.mMissionName = new HUDAutoTextField(this.getCollisionBox(1));
      this.mRewardsText = new HUDAutoTextField(this.getCollisionBox(2));
      text = this.mRewardsText;
      text.setText(112, DCiDead.getFont(4));
      this.mRewardsText.setCentered(true, true);
      SpriteObject[] icons = new SpriteObject[]{AnimationsManager.loadShared(66089), null};
      icons[0].setAnimationFrame(3);
      icons[1] = AnimationsManager.loadShared(66101);
      icons[1].setAnimationFrame(3);
      this.mRewardIconsPanel = new HUDRewardsPanel(this.getCollisionBox(12), icons, DCiDead.getFont(3));
      this.mButtonOk = new HUDButton(14000, this.mCollisions.getCollisionBox(6), 2);
      this.mButtonOk.setText(54);
      this.mXRays = new HUDImage(66937);
      this.mXRays.setPosition(this.mWidth / 2, this.mHeight / 2);
      this.mDelimiterRectangle = new Rectangle(this.getCollisionBox(13), this, 12429940);
      this.mObjects.addObject(this.mXRays);
      this.mObjects.addObject(this.mTitleText);
      this.mObjects.addObject(this.mRectangleBG);
      this.mObjects.addObject(this.mMissionName);
      this.mObjects.addObject(this.mRewardsText);
      this.mObjects.addObject(this.mRewardIconsPanel);
      this.mObjects.addObject(this.mButtonOk);
      this.mObjects.addObject(this.mDelimiterRectangle);
   }

   public void setMission(Mission mission) {
      this.mMission = mission;
      HUDAutoTextField text = this.mMissionName;
      text.setText(mission.getTitleTextID(), DCiDead.getFont(4));
      this.mMissionName.setCentered(true, true);
      this.mRewardIconsPanel.startAddingIcons();
      if (this.mMission.getRewardCoins() > 0) {
         HUDRewardsPanel panel = this.mRewardIconsPanel;
         panel.addIcon(0, "+" + this.mMission.getRewardCoins() + " " + Toolkit.getText(1662));
      }

      if (this.mMission.getRewardXp() > 0) {
         HUDRewardsPanel panel = this.mRewardIconsPanel;
         panel.addIcon(1, "+" + this.mMission.getRewardXp() + " " + Toolkit.getText(1663));
      }

      this.mRewardIconsPanel.endAddingIcons();
   }

   @Override
   public int getOpenSoundEffect() {
      return 46;
   }

   @Override
   public void setState(byte state) {
      super.setState(state);
      if (state == 1) {
         PlayHaven.triggerCompleteMission();
      }
   }
}
