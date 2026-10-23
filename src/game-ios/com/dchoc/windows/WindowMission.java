package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDImage;
import com.dchoc.hud.HUDObject;
import com.dchoc.hud.HUDTarget;
import com.dchoc.hud.Rectangle;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.SlotBackground;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionObjective;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.social.SocialTwitter;
import com.dchoc.idead.tracking.BuyMissionStepEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import java.util.Vector;

public class WindowMission extends Window {
   public static final int BUTTON_BUY_OBJECTIVE = 0;
   public static final int BUTTON_FACEBOOK = 4;
   public static final int BUTTON_OK = 3;
   public static final int BUTTON_TWITTER = 5;
   public static final int COLLISION_DELIMITER_1 = 110;
   public static final int COLLISION_DELIMITER_2 = 111;
   public static final int COLLISION_DESCRIPTION = 2;
   public static final int COLLISION_DESCRIPTION_RECTANGLE = 61;
   public static final int COLLISION_FACEBOOK = 6;
   public static final int COLLISION_HINT = 3;
   public static final int COLLISION_HINT_IMAGE = 8;
   public static final int COLLISION_HINT_LARGE = 9;
   public static final int COLLISION_HINT_RECTANGLE = 60;
   public static final int COLLISION_HINT_RECTANGLE_LARGE = 62;
   public static final int COLLISION_OBJECTIVE_BUY = 40;
   public static final int COLLISION_OBJECTIVE_DESCRIPTION = 20;
   public static final int COLLISION_OBJECTIVE_ICON = 10;
   public static final int COLLISION_OBJECTIVE_SLOT = 50;
   public static final int COLLISION_OBJECTIVE_TARGET = 30;
   public static final int COLLISION_OK = 4;
   public static final int COLLISION_TIME_REMAINING = 100;
   public static final int COLLISION_TIME_REMAINING_TEXT = 101;
   public static final int COLLISION_TITLE = 5;
   public static final int COLLISION_TWITTER = 7;
   public static final String EMPTY_SPACE = " ";
   public static final int OBJECTIVES_MAX = 3;
   private static Mission mMission;
   private HUDAutoTextField mTextTitle;
   private HUDAutoTextField mTextDescription;
   private HUDAutoTextField mTextHint;
   private HUDAutoTextField mTextTimeRemaining;
   private SlotBackground mSlotObjectives;
   private HUDImage mImageHint;
   private RoundRectangle mRectangleDescription;
   private RoundRectangle mRectangleHint;
   private RoundRectangle mRectangleTimeRemaining;
   private Rectangle mDelimiterRectangle1;
   private Rectangle mDelimiterRectangle2;
   private HUDButton mButtonOK;
   private HUDButton mButtonFacebook;
   private HUDButton mButtonTwitter;
   private BuyMissionStepEvent mBuyStepEvent;
   private HUDAutoTextField[] mTextObjectiveDescription;
   private HUDTarget[] mTargetObjective;
   private HUDAutoTextField[] mTargetObjectiveText;
   private HUDImage[] mImageObjectiveComplete;
   private HUDImage[] mImageObjectiveIcon;
   private HUDButton[] mButtonObjectiveBuy;

   public WindowMission() {
      this.mBackground = AnimationsManager.loadShared(74619);
      this.mCollisions = AnimationsManager.loadShared(66847);
      this.initCollisions();
      CollisionBox box = this.mCollisions.getCollisionBox(61);
      this.mRectangleDescription = new RoundRectangle(0);
      RoundRectangle rectangle = this.mRectangleDescription;
      rectangle.setSizes(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mObjects.addObject(this.mRectangleDescription);
      box = this.mCollisions.getCollisionBox(60);
      this.mRectangleHint = new RoundRectangle(0);
      rectangle = this.mRectangleHint;
      rectangle.setSizes(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mObjects.addObject(this.mRectangleHint);
      box = this.mCollisions.getCollisionBox(100);
      this.mRectangleTimeRemaining = new RoundRectangle(0);
      this.mRectangleTimeRemaining.setGradient(false);
      rectangle = this.mRectangleTimeRemaining;
      rectangle.setSizes(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mObjects.addObject(this.mRectangleTimeRemaining);
      box = this.mCollisions.getCollisionBox(50);
      int x = box.getX();
      int y = box.getY();
      int width = this.mCollisions.getWidth();
      int height = this.mCollisions.getHeight();
      this.mSlotObjectives = new SlotBackground(x, y, width, height);
      this.mObjects.addObject(this.mSlotObjectives);
      this.mImageObjectiveIcon = new HUDImage[3];
      this.mTextObjectiveDescription = new HUDAutoTextField[3];
      this.mTargetObjective = new HUDTarget[3];
      this.mTargetObjectiveText = new HUDAutoTextField[3];
      this.mButtonObjectiveBuy = new HUDButton[3];
      this.mImageObjectiveComplete = new HUDImage[3];

      for (int i = 0; i < 3; i++) {
         box = this.mCollisions.getCollisionBox(10 + i);
         x = box.getX();
         width = box.getWidth();
         y = box.getY();
         height = box.getHeight();
         HUDImage image = new HUDImage(-1, x + (width >> 1), y + (height >> 1));
         this.mImageObjectiveIcon[i] = image;
         box = this.mCollisions.getCollisionBox(20 + i);
         HUDAutoTextField text = new HUDAutoTextField(box);
         this.mTextObjectiveDescription[i] = text;
         this.mTextObjectiveDescription[i].setCentered(false, true);
         this.mTextObjectiveDescription[i].setSplitUsingWidth(true);
         box = this.mCollisions.getCollisionBox(30 + i);
         HUDTarget target = new HUDTarget(74607, 74795);
         this.mTargetObjective[i] = target;
         text = new HUDAutoTextField(box);
         this.mTargetObjectiveText[i] = text;
         this.mTargetObjectiveText[i].setCentered(false, true);
         box = this.mCollisions.getCollisionBox(40 + i);
         HUDButton button = new HUDButton(i, box.getX(), box.getY(), 66339);
         this.mButtonObjectiveBuy[i] = button;
         box = this.mCollisions.getCollisionBox(40 + i);
         image = new HUDImage(67275);
         this.mImageObjectiveComplete[i] = image;
         HUDImage complete = this.mImageObjectiveComplete[i];
         x = box.getX();
         width = box.getWidth();
         int imageWidth = this.mImageObjectiveComplete[i].getWidth();
         y = box.getY();
         height = box.getHeight();
         complete.setPosition(x + width - imageWidth / 2, y + (height >> 1));
         this.mObjects.addObject(this.mImageObjectiveIcon[i]);
         this.mObjects.addObject(this.mTextObjectiveDescription[i]);
         this.mObjects.addObject(this.mTargetObjective[i]);
         this.mObjects.addObject(this.mTargetObjectiveText[i]);
         this.mObjects.addObject(this.mButtonObjectiveBuy[i]);
         this.mObjects.addObject(this.mImageObjectiveComplete[i]);
      }

      box = this.mCollisions.getCollisionBox(8);
      this.mImageHint = new HUDImage(67281, box.getX(), box.getY());
      this.mObjects.addObject(this.mImageHint);
      box = this.mCollisions.getCollisionBox(5);
      this.mTextTitle = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextTitle.setCentered(true, true);
      this.mTextTitle.setScaleToFit(true);
      this.mObjects.addObject(this.mTextTitle);
      box = this.mCollisions.getCollisionBox(2);
      this.mTextDescription = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextDescription.setCentered(true, true);
      this.mTextDescription.setSplitUsingWidth(true);
      this.mObjects.addObject(this.mTextDescription);
      box = this.mCollisions.getCollisionBox(3);
      this.mTextHint = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextHint.setCentered(false, true);
      this.mTextHint.setSplitUsingWidth(true);
      this.mObjects.addObject(this.mTextHint);
      box = this.mCollisions.getCollisionBox(101);
      this.mTextTimeRemaining = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextTimeRemaining.setCentered(true, true);
      this.mObjects.addObject(this.mTextTimeRemaining);
      box = this.mCollisions.getCollisionBox(4);
      this.mButtonOK = new HUDButton(3, box, 2);
      this.mButtonOK.setText(54);
      this.mObjects.addObject(this.mButtonOK);
      box = this.mCollisions.getCollisionBox(6);
      x = box.getX();
      width = box.getWidth();
      y = box.getY();
      height = box.getHeight();
      this.mButtonFacebook = new HUDButton(4, x + (width >> 1), y + (height >> 1), 74665);
      this.mObjects.addObject(this.mButtonFacebook);
      box = this.mCollisions.getCollisionBox(7);
      x = box.getX();
      width = box.getWidth();
      y = box.getY();
      height = box.getHeight();
      this.mButtonTwitter = new HUDButton(5, x + (width >> 1), y + (height >> 1), 74647);
      this.mObjects.addObject(this.mButtonTwitter);
      box = this.mCollisions.getCollisionBox(110);
      this.mDelimiterRectangle1 = new Rectangle(box, this, 12429940);
      this.mObjects.addObject(this.mDelimiterRectangle1);
      box = this.mCollisions.getCollisionBox(111);
      this.mDelimiterRectangle2 = new Rectangle(box, this, 12429940);
      this.mObjects.addObject(this.mDelimiterRectangle2);
   }

   public void setMission(Mission mission) {
      if (mission != null) {
         mMission = mission;
         SocialTwitter twitter = GameEngine.getInstance().getTwitter();
         HUDButton button = this.mButtonTwitter;
         button.setEnabled(twitter.isAvailable());
         button = this.mButtonFacebook;
         button.setVisible(mission.getFacebookPost() != null);
         button = this.mButtonTwitter;
         button.setVisible(mission.getTwitterPost() != -1);
         boolean large = mission.getFacebookPost() == null && mission.getTwitterPost() == -1;
         CollisionBox box = this.mCollisions.getCollisionBox(large ? 9 : 3);
         HUDAutoTextField text = this.mTextHint;
         text.setSizes(box.getX(), box.getY(), box.getWidth(), box.getHeight());
         box = this.mCollisions.getCollisionBox(large ? 62 : 60);
         RoundRectangle rectangle = this.mRectangleHint;
         rectangle.setSizes(box.getX(), box.getY(), box.getWidth(), box.getHeight());
         text = this.mTextTitle;
         text.setText(mission.getTitleTextID(), DCiDead.getFont(5));
         text = this.mTextDescription;
         text.setText(mission.getDescriptionTextID(), DCiDead.getFont(4));
         if (this.mTextDescription.getSplittedText() != null && this.mTextDescription.getSplittedText().length > 2) {
            text = this.mTextDescription;
            text.setText(mission.getDescriptionTextID(), DCiDead.getFont(3));
         }

         rectangle = this.mRectangleHint;
         rectangle.setVisible(mission.getHintTextID() != -1);
         text = this.mTextHint;
         text.setVisible(mission.getHintTextID() != -1);
         HUDImage image = this.mImageHint;
         image.setVisible(mission.getHintTextID() != -1);
         if (mission.getHintTextID() != -1) {
            text = this.mTextHint;
            text.setText(mission.getHintTextID(), DCiDead.getFont(3));
         }

         Vector objectives = mission.getObjectives();
         this.setObjective(objectives.size() > 0 ? (MissionObjective)objectives.elementAt(0) : null, 0);
         this.setObjective(objectives.size() > 1 ? (MissionObjective)objectives.elementAt(1) : null, 1);
         this.setObjective(objectives.size() > 2 ? (MissionObjective)objectives.elementAt(2) : null, 2);
         Rectangle delimiter = this.mDelimiterRectangle1;
         delimiter.setVisible(objectives.size() > 1);
         delimiter = this.mDelimiterRectangle2;
         delimiter.setVisible(objectives.size() > 2);
         if (!mission.hasDuration()) {
            this.mTextTimeRemaining.setVisible(false);
            this.mRectangleTimeRemaining.setVisible(false);
         } else {
            boolean second = objectives.size() >= 2 && objectives.elementAt(1) != null;
            CollisionBox timeBox = this.mCollisions.getCollisionBox(second ? 201 : 101);
            CollisionBox backgroundBox = this.mCollisions.getCollisionBox(second ? 200 : 100);
            text = this.mTextTimeRemaining;
            text.setPosition(timeBox.getX(), timeBox.getY());
            rectangle = this.mRectangleTimeRemaining;
            rectangle.setPosition(backgroundBox.getX(), backgroundBox.getY());
            this.mTextTimeRemaining.setVisible(true);
            this.mRectangleTimeRemaining.setVisible(true);
            String time = ToolkitHelpers.formatTime(mission.getTimeLeft() / 60000, true) + " " + Toolkit.getText(29);
            text = this.mTextTimeRemaining;
            text.setText(time, DCiDead.getFont(2));
         }

         SpriteObject collisions = this.mCollisions;
         box = collisions.getCollisionBox(objectives.size() + 49);
         SlotBackground slot = this.mSlotObjectives;
         slot.setSize(box.getWidth(), box.getHeight());
         this.mBuyStepEvent = new BuyMissionStepEvent();
      }
   }

   public void setObjective(MissionObjective objective, int index) {
      this.mImageObjectiveIcon[index].setVisible(objective != null);
      this.mTextObjectiveDescription[index].setVisible(objective != null);
      HUDButton button = this.mButtonObjectiveBuy[index];
      button.setVisible(objective != null && !objective.isCompleted() && !objective.isPurchased() && objective.getUnlockPrice() > 0);
      HUDImage complete = this.mImageObjectiveComplete[index];
      if (objective == null) {
         complete.setVisible(false);
         this.mTargetObjectiveText[index].setVisible(false);
         this.mTargetObjective[index].setVisible(false);
      } else {
         complete.setVisible(objective.isCompleted());
         HUDObject selected;
         if (objective.getType() == 2571) {
            this.mTargetObjective[index].setVisible(false);
            selected = this.mTargetObjectiveText[index];
         } else {
            this.mTargetObjectiveText[index].setVisible(false);
            selected = this.mTargetObjective[index];
         }

         selected.setVisible(!objective.isCompleted());
         HUDImage icon = this.mImageObjectiveIcon[index];
         icon.setImage(objective.getIconID());
         if (objective.getUnlockPrice() > 0) {
            button = this.mButtonObjectiveBuy[index];
            button.setText(Integer.toString(objective.getUnlockPrice()));
            int currency = objective.getUnlockCurrency();
            this.mButtonObjectiveBuy[index].setIcon(currency == 9 ? 66253 : 66255);
         }

         int counter = objective.getCounter();
         int goal = objective.getGoal();
         if (objective.getType() == 2571) {
            String time = ToolkitHelpers.formatTime(goal - counter, false);
            this.mTargetObjectiveText[index].setText(time, 4);
         } else {
            this.mTargetObjective[index].setValues(counter, goal);
         }

         CollisionBox box = this.mCollisions.getCollisionBox(40 + index);
         int y = box.getY();
         int height = box.getHeight();
         int targetHeight = this.mTargetObjective[index].getHeight();
         int price = objective.getUnlockPrice();
         HUDTarget target = this.mTargetObjective[index];
         y += (height - targetHeight) / 2;
         int x = box.getX();
         if (price < 1) {
            int width = box.getWidth();
            int targetWidth = this.mTargetObjective[index].getWidth();
            target.setPosition(x + width - 5 - targetWidth, y);
         } else {
            int targetWidth = this.mTargetObjective[index].getWidth();
            target.setPosition(x - 5 - targetWidth, y);
         }

         box = this.mCollisions.getCollisionBox(20 + index);
         int width = box.getWidth();
         this.mTextObjectiveDescription[index].setWidth(width);
         int descriptionX = this.mTextObjectiveDescription[index].getX();
         int descriptionWidth = this.mTextObjectiveDescription[index].getWidth();
         int targetX = this.mTargetObjective[index].getX();
         HUDAutoTextField description = this.mTextObjectiveDescription[index];
         if (targetX < descriptionWidth + descriptionX) {
            width = -5 - (descriptionWidth + descriptionX) + targetX + description.getWidth();
         }

         description.setWidth(width);
         this.mTextObjectiveDescription[index].setSplitUsingWidth(true);
         description = this.mTextObjectiveDescription[index];
         int textId = objective.getDescriptionTextID();
         String goalText = Integer.toString(objective.getGoal());
         description.setText(textId, 4, goalText, -1);
      }
   }

   @Override
   public void callback(int id) {
      if (id == 3) {
         WindowManager.closeWindow();
         if (mMission.getID() == 2343) {
            ((WindowNpcDialog)WindowManager.getWindow((byte)19)).checkConnectionDialog();
         }
      } else if (Integer.compareUnsigned(id, 3) < 0) {
         MissionObjective objective = (MissionObjective)mMission.getObjectives().elementAt(id);
         BuyMissionStepEvent event = this.mBuyStepEvent;
         int missionId = mMission.getID();
         int objectiveId = objective.getID();
         int price = objective.getUnlockPrice();
         int goal = objective.getGoal();
         int counter = objective.getCounter();
         event.trackSpend(missionId, objectiveId, price, goal - counter);
         this.buyObjective(objective);
      } else if (id == 4) {
         int[] post = mMission.getFacebookPost();
         WindowSocial social = (WindowSocial)WindowManager.getWindow((byte)18);
         social.postFacebook(Toolkit.getText(post[0]), Toolkit.getText(post[1]), Toolkit.getText(post[2]), 1);
      } else if (id == 5) {
         int post = mMission.getTwitterPost();
         WindowSocial social = (WindowSocial)WindowManager.getWindow((byte)18);
         social.postTwitter(Toolkit.getText(post));
      }
   }

   public void buyObjective(MissionObjective objective) {
      if (objective != null && !objective.isPurchased()) {
         int currency = objective.getUnlockCurrency();
         int price = objective.getUnlockPrice();
         if (currency == 9) {
            if (PlayerProfile.getCoins() < price) {
               WindowManager.closeWindow();
               ((WindowOutOfCashCoins)WindowManager.getWindow((byte)11)).setMode(1);
               WindowManager.openWindow((byte)11);
               return;
            }

            PlayerProfile.removeCoins(price);
         } else if (currency == 7) {
            if (PlayerProfile.getCash() < price) {
               WindowManager.closeWindow();
               ((WindowOutOfCashCoins)WindowManager.getWindow((byte)11)).setMode(0);
               WindowManager.openWindow((byte)11);
               return;
            }

            PlayerProfile.removeCash(price);
         }

         objective.setPurchased(true);
         this.setMission(mMission);
      }
   }
}
