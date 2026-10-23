package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDImage;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.Achievement;
import com.dchoc.idead.missions.AchievementObjective;
import com.dchoc.playhaven.PlayHaven;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.Toolkit;

public class WindowAchievementAward extends Window {
   private static final int[] REWARD_NAMES = new int[]{2433, 2434, 2435, 2436};
   private static Achievement smAchievement;
   private static HUDImage smAchievementIcon;
   private static HUDAutoTextField smAchievementTitle;
   private static HUDAutoTextField smAwardType;
   private static RoundRectangle smBackground;
   private static HUDButton smButtonOk;
   private static HUDImage smRewardCoins;
   private static HUDImage smRewardPlatinum;
   private static HUDImage smRewardXp;
   private static HUDAutoTextField smRewardsCoins;
   private static HUDAutoTextField smRewardsPlatinum;
   private static HUDAutoTextField smRewardsTitle;
   private static HUDAutoTextField smRewardsXp;
   private static HUDImage smShieldBig;
   private static HUDImage smShieldSmall;
   private static HUDAutoTextField smTitle;

   public WindowAchievementAward() {
      this.mBackground = AnimationsManager.loadShared(74621);
      this.mCollisions = AnimationsManager.loadShared(74935);
      this.initCollisions();
      smBackground = new RoundRectangle(0);
      smBackground.setSizes(this.getCollisionBox(2));
      smTitle = new HUDAutoTextField(this.getCollisionBox(1));
      smTitle.setCentered(true, true);
      smTitle.setText(2426, DCiDead.getFont(5));
      smAchievementTitle = new HUDAutoTextField(this.getCollisionBox(20));
      smAchievementTitle.setCentered(true, true);
      smAchievementTitle.setSplitUsingWidth(true);
      smAwardType = new HUDAutoTextField(this.getCollisionBox(21));
      smAwardType.setCentered(true, true);
      smRewardsTitle = new HUDAutoTextField(this.getCollisionBox(22));
      smRewardsTitle.setCentered(true, true);
      smRewardsTitle.setText(112, DCiDead.getFont(4));
      smRewardsCoins = new HUDAutoTextField(this.getCollisionBox(40));
      smRewardsXp = new HUDAutoTextField(this.getCollisionBox(41));
      smRewardsPlatinum = new HUDAutoTextField(this.getCollisionBox(42));
      smButtonOk = new HUDButton(52000, this.getCollisionBox(10), 3);
      smButtonOk.setText(54);
      CollisionBox box = this.getCollisionBox(6);
      smShieldSmall = new HUDImage(-1);
      smShieldSmall.setPosition(box.getX() + box.getWidth() / 2, box.getY() + box.getHeight() / 2);
      smShieldSmall.setVisible(false);
      smShieldBig = new HUDImage(-1);
      smShieldBig.setPosition(box.getX() + box.getWidth() / 2, box.getY() + box.getHeight() / 2);
      smShieldBig.setVisible(false);
      box = this.getCollisionBox(30);
      smRewardCoins = new HUDImage(-1);
      smRewardCoins.setPosition(box.getX() + box.getWidth() / 2, box.getY() + box.getHeight() / 2);
      box = this.getCollisionBox(31);
      smRewardXp = new HUDImage(-1);
      smRewardXp.setPosition(box.getX() + box.getWidth() / 2, box.getY() + box.getHeight() / 2);
      smAchievementIcon = new HUDImage();
      smRewardPlatinum = new HUDImage();
      this.mObjects.addObject(smBackground);
      this.mObjects.addObject(smTitle);
      this.mObjects.addObject(smAchievementTitle);
      this.mObjects.addObject(smAwardType);
      this.mObjects.addObject(smRewardsTitle);
      this.mObjects.addObject(smRewardsCoins);
      this.mObjects.addObject(smRewardsXp);
      this.mObjects.addObject(smRewardsPlatinum);
      this.mObjects.addObject(smRewardPlatinum);
      this.mObjects.addObject(smButtonOk);
      this.mObjects.addObject(smShieldBig);
      this.mObjects.addObject(smShieldSmall);
      this.mObjects.addObject(smAchievementIcon);
      this.mObjects.addObject(smRewardCoins);
      this.mObjects.addObject(smRewardXp);
   }

   public void setOptions(Achievement achievement) {
      smAchievement = achievement;
      AchievementObjective current = achievement.getCurrentObjective();
      int level = achievement.getLevel();
      int index;
      if (level == 64) {
         index = 0;
      } else if (level == 66) {
         AchievementObjective last = (AchievementObjective)smAchievement.getObjectives().elementAt(3);
         index = last.isGoalReached() ? 3 : 2;
      } else {
         if (level != 65) {
            return;
         }

         index = 1;
      }

      smAchievementTitle.setText(smAchievement.getTitleTextID(), 0);
      smAwardType.setText(REWARD_NAMES[index], DCiDead.getFont(3));
      if (smAchievement.getLevel() == 66 && ((AchievementObjective)smAchievement.getObjectives().elementAt(3)).isGoalReached()) {
         smShieldSmall.setVisible(false);
         smShieldBig.setVisible(true);
         smRewardPlatinum.setVisible(true);
         smRewardCoins.setVisible(false);
         smRewardXp.setVisible(false);
         smRewardsCoins.setVisible(false);
         smRewardsXp.setVisible(false);
         smRewardsPlatinum.setVisible(true);
      } else {
         smShieldSmall.setVisible(true);
         smShieldBig.setVisible(false);
         smRewardPlatinum.setVisible(false);
         smRewardCoins.setVisible(true);
         smRewardXp.setVisible(true);
         smRewardsCoins.setVisible(true);
         smRewardsXp.setVisible(true);
         smRewardsPlatinum.setVisible(false);
      }

      CollisionBox box = this.getCollisionBox(6);
      smAchievementIcon.setImage(smAchievement.getIconAnimationID());
      smAchievementIcon.setPosition(box.getX() + box.getWidth() / 2, box.getY() + box.getHeight() / 2);
      level = smAchievement.getLevel();
      if (level == 66) {
         boolean reached = ((AchievementObjective)smAchievement.getObjectives().elementAt(3)).isGoalReached();
         if (reached) {
            smAwardType.setText(1880, DCiDead.getFont(3));
            smAchievementIcon.setAnimationFrame(3);
         } else {
            smAwardType.setText(1879, DCiDead.getFont(3));
            smAchievementIcon.setAnimationFrame(2);
         }
      } else if (level == 65) {
         smAwardType.setText(1878, DCiDead.getFont(3));
         smAchievementIcon.setAnimationFrame(1);
      } else if (level == 64) {
         smAwardType.setText(1877, DCiDead.getFont(3));
         smAchievementIcon.setAnimationFrame(0);
      }

      if (smAchievement.getLevel() == 66 && ((AchievementObjective)smAchievement.getObjectives().elementAt(3)).isGoalReached()) {
         Item item = ItemManager.getItem(current.getRewards()[0][0]);
         smRewardPlatinum.setImage(item.getIconAnimationID());
         CollisionBox target = this.mCollisions.getCollisionBox(32);
         CollisionBox image = smRewardPlatinum.getCollisionBox(0);
         if (item.getSubType() == 2116) {
            int x = target.getX() + target.getWidth() / 2 - image.getX();
            smRewardPlatinum.setPosition(x - image.getWidth() / 2, target.getY() - image.getY());
            smRewardPlatinum.setClipBox(target);
         } else {
            CollisionBox again = smRewardPlatinum.getCollisionBox(0);
            int x = target.getX() + target.getWidth() / 2;
            if (again == null) {
               smRewardPlatinum.setPosition(x, target.getY() + target.getHeight() / 2);
            } else {
               int imageX = image.getX();
               int imageWidth = image.getWidth();
               int targetY = target.getY();
               int targetHeight = target.getHeight();
               int imageY = image.getY();
               int imageHeight = image.getHeight();
               smRewardPlatinum.setPosition(x - imageX - imageWidth / 2, targetY + targetHeight / 2 - imageY - imageHeight / 2);
            }

            smRewardPlatinum.setClipBox(null);
         }

         smRewardsPlatinum.setText(Toolkit.getText(item.getTitle()), DCiDead.getFont(3));
      } else {
         AchievementObjective objective = (AchievementObjective)smAchievement.getObjectives().elementAt(index);
         if (objective.getRewards()[0][0] == 9) {
            smRewardsCoins.setText("+ " + objective.getRewards()[0][1] + " " + Toolkit.getText(1662), DCiDead.getFont(3));
            smRewardsCoins.setCentered(true, true);
         }

         if (objective.getRewards()[1][0] == 2095) {
            smRewardsXp.setText("+ " + objective.getRewards()[1][1] + " " + Toolkit.getText(1663), DCiDead.getFont(3));
            smRewardsXp.setCentered(true, true);
         }
      }
   }

   @Override
   public void loadResources() {
      smShieldSmall.setImage(75019);
      smShieldBig.setImage(75021);
      smRewardCoins.setImage(74023);
      smRewardXp.setImage(67321);
   }

   @Override
   public void unloadResources() {
      smShieldSmall.freeImages();
      smShieldBig.freeImages();
      smAchievementIcon.freeImages();
      smRewardCoins.freeImages();
      smRewardXp.freeImages();
      smRewardPlatinum.freeImages();
   }

   @Override
   public void setState(byte state) {
      super.setState(state);
      if (state == 0) {
         PlayHaven.triggerAchievementCompleted();
      }
   }
}
