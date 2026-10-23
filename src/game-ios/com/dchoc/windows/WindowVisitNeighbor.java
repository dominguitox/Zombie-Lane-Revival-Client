package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDRewardsPanel;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public class WindowVisitNeighbor extends Window {
   protected static final int COLLISION_ID_BACKGROUND_RECTANGLE = 7;
   protected static final int COLLISION_ID_BUTTON = 6;
   protected static final int COLLISION_ID_HEADER = 5;
   protected static final int COLLISION_ID_REWARDS = 12;
   protected static final int COLLISION_ID_REWARDS_TEXT = 2;
   private static final int[] ICON_ANIM = new int[]{66101, 66089, 66093, 66723};
   private HUDButton mButtonOk;
   private RoundRectangle mRectangleBG;
   private HUDAutoTextField mTitleText;
   private HUDAutoTextField mRewardsText;
   private HUDRewardsPanel mRewardIconsPanel;

   public WindowVisitNeighbor() {
      this.mBackground = AnimationsManager.loadShared(74621);
      this.mCollisions = AnimationsManager.loadShared(67279);
      this.initCollisions();
      this.mRectangleBG = new RoundRectangle(0);
      this.mRectangleBG.setSizes(this.getCollisionBox(7));
      this.mTitleText = new HUDAutoTextField(this.getCollisionBox(5));
      this.mTitleText.setText(1712, DCiDead.getFont(5));
      this.mTitleText.setCentered(true, true);
      this.mRewardsText = new HUDAutoTextField(this.getCollisionBox(2));
      this.mRewardsText.setCentered(true, true);
      SpriteObject[] icons = new SpriteObject[ICON_ANIM.length];

      for (int i = 0; i < icons.length; i++) {
         icons[i] = AnimationsManager.loadShared(ICON_ANIM[i]);
      }

      icons[1].setAnimationFrame(3);
      this.mRewardIconsPanel = new HUDRewardsPanel(this.getCollisionBox(12), icons, DCiDead.getFont(3));
      this.mButtonOk = new HUDButton(14000, this.mCollisions.getCollisionBox(6), 2);
      this.mButtonOk.setText(54);
      this.mObjects.addObject(this.mTitleText);
      this.mObjects.addObject(this.mRectangleBG);
      this.mObjects.addObject(this.mRewardsText);
      this.mObjects.addObject(this.mRewardIconsPanel);
      this.mObjects.addObject(this.mButtonOk);
   }

   @Override
   public void draw() {
      super.draw();
   }

   public void setNeighborName(String name) {
      this.mRewardsText.setText(1715, 4, name, -1);
   }

   public void setRewards(int xp, int coins, int third, int fourth) {
      this.mRewardIconsPanel.startAddingIcons();
      if (xp > 0) {
         this.mRewardIconsPanel.addIcon(0, "+" + xp + " " + Toolkit.getText(1663));
      }

      if (coins > 0) {
         this.mRewardIconsPanel.addIcon(1, "+" + coins + " " + Toolkit.getText(1662));
      }

      if (third > 0) {
         this.mRewardIconsPanel.addIcon(2, "+" + third + " " + Toolkit.getText(1664));
      }

      if (fourth > 0) {
         this.mRewardIconsPanel.addIcon(3, "+" + fourth + " " + Toolkit.getText(412));
      }

      this.mRewardIconsPanel.endAddingIcons();
   }
}
