package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.Achievement;
import com.dchoc.idead.missions.AchievementObjective;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;

public class HUDItemAchievement extends HUDItem {
   public static final int[] REWARD_NAMES = new int[]{2433, 2434, 2435, 2436};
   public static final int STATE_BRONZE = 0;
   public static final int STATE_SILVER = 1;
   public static final int STATE_GOLD = 2;
   public static final int STATE_PLATINUM = 3;
   public static final int STATE_ALLSTARS = 4;
   private static SpriteObject mSlotInfo;
   private static SpriteObject mFill;
   private static SpriteObject mFillBackground;
   private int mState;
   private Achievement mAchievement;
   private AchievementObjective mCurrentObjective;
   private int mCurrentValue;
   private int mMaxValue;
   private int mCurrentWidthOfClip;
   private HUDImage mSlotNormal1;
   private HUDImage mSlotNormal2;
   private HUDImage mSlotNormal3;
   private HUDImage mSlotNormalBig;
   private HUDImage mSlotBronze;
   private HUDImage mSlotSilver;
   private HUDImage mSlotGold;
   private HUDImage mSlotPlatinum;
   private HUDTarget mProgressTarget;
   private HUDAutoTextField mTitle;
   private HUDAutoTextField mDescription;
   private HUDAutoTextField mPlatinumReward;
   private SlotBackground mSlotBackground;
   private HUDImage[][] setsOfSlots;
   private HUDImage[] allSlots;
   private HUDImage[] mRewardsIcons;
   private HUDImage[] mPrizeIcons;
   private HUDAutoTextField[] mSlotTitles;
   private HUDAutoTextField[][] mSlotRewards;

   public HUDItemAchievement(Achievement achievement) {
      super(0);
      this.mAchievement = achievement;
      mSlotInfo = AnimationsManager.loadShared(74767);
      HUDAutoTextField text = new HUDAutoTextField(mSlotInfo.getCollisionBox(0));
      this.mTitle = text;
      text.setText(this.mAchievement.getTitleTextID(), 2);
      this.mDescription = new HUDAutoTextField(mSlotInfo.getCollisionBox(9));
      this.mSlotTitles = new HUDAutoTextField[4];

      for (int i = 0; i < 4; i++) {
         this.mSlotTitles[i] = new HUDAutoTextField(mSlotInfo.getCollisionBox(i + 50));
         this.mSlotTitles[i].setScaleToFit(true);
         HUDAutoTextField title = this.mSlotTitles[i];
         int name = REWARD_NAMES[i];
         title.setText(name, i == 3 ? DCiDead.getFont(4) : DCiDead.getFont(3));
      }

      this.mRewardsIcons = new HUDImage[4];
      this.mRewardsIcons[0] = new HUDImage(66385);
      this.mRewardsIcons[1] = new HUDImage(66389);
      this.mRewardsIcons[2] = new HUDImage(66391);
      AchievementObjective last = (AchievementObjective)this.mAchievement.getObjectives().elementAt(3);
      Item item = ItemManager.getItem(last.getRewards()[0][0]);
      this.mRewardsIcons[3] = new HUDImage(item.getIconAnimationID());

      for (int i = 0; i < (this.mRewardsIcons == null ? 0 : this.mRewardsIcons.length); i++) {
         CollisionBox box = mSlotInfo.getCollisionBox(i + 1);
         HUDImage icon = this.mRewardsIcons[i];
         int x = box.getX();
         int width = box.getWidth();
         int y = box.getY();
         int height = box.getHeight();
         icon.setPosition(x + width / 2, y + height * 2 / 5);
      }

      this.mPrizeIcons = new HUDImage[4];

      for (int i = 0; i < (this.mPrizeIcons == null ? 0 : this.mPrizeIcons.length); i++) {
         CollisionBox box = mSlotInfo.getCollisionBox(i + 1);
         int image = this.mAchievement.getIconAnimationID();
         int x = box.getX();
         int width = box.getWidth();
         int y = box.getY();
         int height = box.getHeight();
         this.mPrizeIcons[i] = new HUDImage(image, x + width / 2, y + height / 2);
         this.mPrizeIcons[i].setAnimationFrame(i);
      }

      if (item.getType() == 1911) {
         int subType = item.getSubType();
         SpriteObject slotInfo = mSlotInfo;
         if (subType == 2116) {
            CollisionBox box = slotInfo.getCollisionBox(8);
            CollisionBox iconBox = this.mRewardsIcons[3].getCollisionBox(0);
            HUDImage icon = this.mRewardsIcons[3];
            int x = box.getX();
            int iconX = iconBox.getX();
            int y = box.getY();
            int iconY = iconBox.getY();
            icon.setPosition(x - iconX, y - iconY);
            this.mRewardsIcons[3].setClipBox(box);
         } else {
            CollisionBox box = slotInfo.getCollisionBox(4);
            CollisionBox iconBox = this.mRewardsIcons[3].getCollisionBox(0);
            HUDImage icon = this.mRewardsIcons[3];
            int x = box.getX();
            int width = box.getWidth();
            int iconX = iconBox.getX();
            int iconWidth = iconBox.getWidth();
            int y = box.getY();
            int height = box.getHeight();
            int iconY = iconBox.getY();
            int iconHeight = iconBox.getHeight();
            icon.setPosition(x + (width / 2 - iconX - iconWidth / 2), height * 2 / 5 + y - iconY - iconHeight / 2);
         }
      }

      this.mSlotRewards = new HUDAutoTextField[3][2];

      for (int i = 0; i < (this.mSlotRewards == null ? 0 : this.mSlotRewards.length); i++) {
         this.mSlotRewards[i][0] = new HUDAutoTextField(mSlotInfo.getCollisionBox(i + 60));
         this.mSlotRewards[i][1] = new HUDAutoTextField(mSlotInfo.getCollisionBox(i + 70));
         AchievementObjective objective = (AchievementObjective)this.mAchievement.getObjectives().elementAt(i);
         HUDAutoTextField reward = this.mSlotRewards[i][0];
         reward.setText(Integer.toString(objective.getRewards()[0][1]), DCiDead.getFont(3));
         reward = this.mSlotRewards[i][1];
         reward.setText(Integer.toString(objective.getRewards()[1][1]), DCiDead.getFont(3));
      }

      text = new HUDAutoTextField(mSlotInfo.getCollisionBox(80));
      this.mPlatinumReward = text;
      text.setScaleToFit(true);
      text = this.mPlatinumReward;
      text.setText(item.getTitle(), DCiDead.getFont(3));
      mFill = AnimationsManager.loadShared(66387);
      mFillBackground = AnimationsManager.loadShared(66751);
      this.mSlotNormal1 = new HUDImage(66381);
      this.mSlotNormal2 = new HUDImage(66381);
      this.mSlotNormal3 = new HUDImage(66381);
      this.mSlotNormalBig = new HUDImage(66393);
      this.mSlotNormal1.setPosition(mSlotInfo.getCollisionBox(1));
      this.mSlotNormal2.setPosition(mSlotInfo.getCollisionBox(2));
      this.mSlotNormal3.setPosition(mSlotInfo.getCollisionBox(3));
      this.mSlotNormalBig.setPosition(mSlotInfo.getCollisionBox(4));
      this.mSlotBronze = new HUDImage(66403);
      this.mSlotSilver = new HUDImage(66401);
      this.mSlotGold = new HUDImage(66371);
      this.mSlotPlatinum = new HUDImage(66399);
      this.mSlotBronze.setPosition(mSlotInfo.getCollisionBox(1));
      this.mSlotSilver.setPosition(mSlotInfo.getCollisionBox(2));
      this.mSlotGold.setPosition(mSlotInfo.getCollisionBox(3));
      this.mSlotPlatinum.setPosition(mSlotInfo.getCollisionBox(4));
      this.mProgressTarget = new HUDTarget(0);
      CollisionBox box = mSlotInfo.getCollisionBox(5);
      HUDTarget target = this.mProgressTarget;
      int x = box.getX();
      int width = box.getWidth();
      int y = box.getY();
      int height = box.getHeight();
      target.setPosition(x + width / 2, y + height / 2);
      this.mProgressTarget.setCenterType((byte)3);
      this.mSlotBackground = new SlotBackground(mSlotInfo.getCollisionBox(10), this);
      this.setsOfSlots = new HUDImage[][]{
         {this.mSlotNormal1, this.mSlotNormal2, this.mSlotNormal3, this.mSlotNormalBig},
         {this.mSlotBronze, this.mSlotNormal2, this.mSlotNormal3, this.mSlotNormalBig},
         {this.mSlotBronze, this.mSlotSilver, this.mSlotNormal3, this.mSlotNormalBig},
         {this.mSlotBronze, this.mSlotSilver, this.mSlotGold, this.mSlotNormalBig},
         {this.mSlotBronze, this.mSlotSilver, this.mSlotGold, this.mSlotPlatinum}
      };
      this.allSlots = new HUDImage[]{
         this.mSlotNormal1, this.mSlotNormal2, this.mSlotNormal3, this.mSlotNormalBig, this.mSlotBronze, this.mSlotSilver, this.mSlotGold, this.mSlotPlatinum
      };
      this.mObjects = new HUDObject[]{
         this.mSlotBackground,
         this.mTitle,
         this.mDescription,
         this.mProgressTarget,
         this.mSlotNormal1,
         this.mSlotNormal2,
         this.mSlotNormal3,
         this.mSlotNormalBig,
         this.mSlotBronze,
         this.mSlotSilver,
         this.mSlotGold,
         this.mSlotPlatinum,
         this.mSlotRewards[0][0],
         this.mSlotRewards[0][1],
         this.mSlotRewards[1][0],
         this.mSlotRewards[1][1],
         this.mSlotRewards[2][0],
         this.mSlotRewards[2][1],
         this.mRewardsIcons[0],
         this.mRewardsIcons[1],
         this.mRewardsIcons[2],
         this.mRewardsIcons[3],
         this.mPlatinumReward,
         this.mSlotTitles[0],
         this.mSlotTitles[1],
         this.mSlotTitles[2],
         this.mSlotTitles[3],
         this.mPrizeIcons[0],
         this.mPrizeIcons[1],
         this.mPrizeIcons[2],
         this.mPrizeIcons[3]
      };

      for (int i = 0; i < (this.mObjects == null ? 0 : this.mObjects.length); i++) {
         this.mObjects[i].setParent(this);
      }

      this.mWidth = mSlotInfo.getCollisionBox(11).getWidth();
      this.mHeight = mSlotInfo.getCollisionBox(11).getHeight();
      this.refresh();
   }

   @Override
   public void refresh() {
      for (int i = 0; i < (this.allSlots == null ? 0 : this.allSlots.length); i++) {
         this.allSlots[i].setVisible(false);
      }

      for (int i = 0; i < (this.mRewardsIcons == null ? 0 : this.mRewardsIcons.length); i++) {
         this.mRewardsIcons[i].setVisible(false);
      }

      for (int i = 0; i < (this.mPrizeIcons == null ? 0 : this.mPrizeIcons.length); i++) {
         this.mPrizeIcons[i].setVisible(false);
      }

      for (int i = 0; i < (this.mSlotRewards == null ? 0 : this.mSlotRewards.length); i++) {
         for (int j = 0; j < (this.mSlotRewards[i] == null ? 0 : this.mSlotRewards[i].length); j++) {
            this.mSlotRewards[i][j].setVisible(false);
         }
      }

      for (int i = 0; i < (this.mSlotTitles == null ? 0 : this.mSlotTitles.length); i++) {
         this.mSlotTitles[i].setVisible(false);
      }

      int objective;
      switch (this.mAchievement.getLevel()) {
         case 63:
            objective = 0;
            this.mState = 0;
            break;
         case 64:
            objective = 1;
            this.mState = 1;
            break;
         case 65:
            objective = 2;
            this.mState = 2;
            break;
         case 66:
            objective = 3;
            AchievementObjective last = (AchievementObjective)this.mAchievement.getObjectives().elementAt(3);
            this.mState = last.isGoalReached() ? 4 : 3;
            break;
         default:
            objective = 0;
      }

      for (int i = 0; i <= this.mState; i++) {
         for (int j = 0; j < (this.setsOfSlots[i] == null ? 0 : this.setsOfSlots[i].length); j++) {
            this.setsOfSlots[i][j].setVisible(true);
         }
      }

      for (int i = this.mState; i < (this.mSlotTitles == null ? 0 : this.mSlotTitles.length); i++) {
         this.mSlotTitles[i].setVisible(true);
      }

      for (int i = 0; i < this.mState; i++) {
         this.mPrizeIcons[i].setVisible(true);
      }

      for (int i = this.mState; i < (this.mRewardsIcons == null ? 0 : this.mRewardsIcons.length); i++) {
         this.mRewardsIcons[i].setVisible(true);
      }

      for (int i = this.mState; i < (this.mSlotRewards == null ? 0 : this.mSlotRewards.length); i++) {
         for (int j = 0; j < (this.mSlotRewards[i] == null ? 0 : this.mSlotRewards[i].length); j++) {
            this.mSlotRewards[i][j].setVisible(true);
         }
      }

      if (this.mState == 4) {
         this.mPlatinumReward.setVisible(false);
      }

      this.mCurrentObjective = (AchievementObjective)this.mAchievement.getObjectives().elementAt(objective);
      this.mMaxValue = this.mCurrentObjective.getGoal();
      this.setValue(this.mCurrentObjective.getCounter());
      HUDAutoTextField description = this.mDescription;
      int text = this.mAchievement.getDescriptionTextID();
      description.setText(text, 3, Integer.toString(this.mCurrentObjective.getGoal()), -1);
   }

   @Override
   public void draw() {
      super.draw();
      SpriteObject background = mFillBackground;
      int x = this.getX();
      int boxX = mSlotInfo.getCollisionBox(5).getX();
      int y = this.getY();
      int boxY = mSlotInfo.getCollisionBox(5).getY();
      background.draw(boxX + x, boxY + y);
      if (this.mCurrentWidthOfClip > 0) {
         x = this.getX();
         boxX = mSlotInfo.getCollisionBox(5).getX();
         if (this.mCurrentWidthOfClip > 0) {
            int clipX = OGL.getClipX();
            int clipWidth = OGL.getClipWidth();
            int clipY = OGL.getClipY();
            int clip = this.mCurrentWidthOfClip;
            OGL.setClip(boxX + x, clipY, clip, OGL.getClipHeight());
            SpriteObject fill = mFill;
            x = this.getX();
            boxX = mSlotInfo.getCollisionBox(5).getX();
            y = this.getY();
            boxY = mSlotInfo.getCollisionBox(5).getY();
            fill.draw(boxX + x, boxY + y);
            clipY = OGL.getClipY();
            OGL.setClip(clipX, clipY, clipWidth, OGL.getClipHeight());
         }
      }

      this.mProgressTarget.draw();
   }

   public void setValue(int value) {
      if (this.mCurrentValue != value) {
         this.mCurrentValue = value;
         int width = mSlotInfo.getCollisionBox(5).getWidth();
         this.mCurrentWidthOfClip = this.mCurrentValue * width / this.mMaxValue;
         if (this.mCurrentValue < 0) {
            this.mCurrentValue = 0;
         }

         if (this.mCurrentValue > this.mMaxValue) {
            this.mCurrentValue = this.mMaxValue;
         }

         this.mProgressTarget.setValues(this.mCurrentValue, this.mMaxValue);
      }
   }
}
