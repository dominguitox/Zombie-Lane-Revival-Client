package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUD;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDBubble;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDImage;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.Toolkit;

public class WindowNpcMonolog extends Window implements CsvIDs {
   public static final int NPC_DAISY = 3105;
   public static final int NPC_DYSON = 3242;
   public static final int NPC_FRANK = 2869;
   public static final int NPC_JERRY = 2870;
   public static final int NPC_KEVIN = 3014;
   public static final int NPC_NONE = -1;
   public static final int NPC_PHONE = 2868;
   public static final int NPC_ROB = 807;
   public static final int NPC_ROB_SHOTGUN = 2874;
   private short mNPC = -1;
   private HUDButton mButtonOK = new HUDButton(23000, null, 3);
   private HUDAutoTextField mText;
   private HUDImage mPortrait;
   private SpriteObject mDialogAnimation;
   private SpriteObject mDialogPointerAnimation;
   private int mMarginWidth;
   private int mMarginHeight;
   private int mDialogTextBGInitialWidth;
   private int mDialogTextBGInitialHeight;
   private boolean mFlipNPC;
   private Mission mMission;
   private boolean mUseWideBubble;
   private int mBubbleInitialWidth;
   private int mBubbleWidth;
   private int mTextMargin;

   public WindowNpcMonolog() {
      this.mButtonOK.setParent(this);
      this.mButtonOK.setText(54);
      this.mPortrait = new HUDImage();
      this.mDialogAnimation = AnimationsManager.loadShared(67311);
      this.mDialogPointerAnimation = AnimationsManager.loadShared(67313);
      this.mUseWideBubble = false;
      CollisionBox bubble = this.mDialogAnimation.getCollisionBox(3);
      CollisionBox textBox = this.mDialogAnimation.getCollisionBox(0);
      int width = bubble.getWidth();
      this.mBubbleWidth = width;
      this.mBubbleInitialWidth = width;
      int textX = textBox.getX();
      this.mTextMargin = textX - bubble.getX();
      this.mDialogTextBGInitialWidth = textBox.getWidth();
      this.mDialogTextBGInitialHeight = textBox.getHeight();
      this.mText = new HUDAutoTextField(0, 0, this.mDialogTextBGInitialWidth, this.mDialogTextBGInitialHeight);
      this.mText.setParent(this);
      this.mText.setCentered(true, false);
      this.mText.setSplitUsingWidth(true);
      CollisionBox corner = this.mDialogAnimation.getCollisionBox(2);
      this.mMarginHeight = corner.getHeight();
      this.mMarginWidth = corner.getWidth();
      this.mObjects.addObject(this.mText);
      this.mObjects.addObject(this.mButtonOK);
      this.mObjects.addObject(this.mPortrait);
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      HUDImage portrait = this.mPortrait;
      if (portrait != null) {
         if (this.mFlipNPC) {
            portrait.setPosition(this.mBubbleWidth - this.mMarginWidth / 2, 0);
         } else {
            int width = portrait.getWidth();
            portrait.setPosition(this.mMarginWidth / 2 - width, 0);
         }
      }
   }

   @Override
   public void draw() {
      if (TutorialFlow.isCompleted()) {
         this.mFadeBackground = true;
         this.fadeBackground();
      } else {
         TutorialFlow.drawSpotlight();
      }

      int width = this.mBubbleWidth;
      int textHeight = this.mText.getTextHeight();
      int x = this.getX();
      int y = this.getY();
      HUDBubble.drawBubbleBackground(x, y, width, textHeight + 2 * this.mMarginHeight, this.mDialogAnimation, false, 1.0F);
      SpriteObject pointer = this.mDialogPointerAnimation;
      if (pointer != null) {
         if (!this.mFlipNPC) {
            x = this.getX();
            y = this.getY();
            pointer.draw(x, y + this.mMarginHeight);
         } else {
            x = this.getX();
            y = this.getY();
            pointer.draw(x + width, y + this.mMarginHeight, 1);
         }
      }

      this.mFadeBackground = false;
      super.draw();
   }

   @Override
   public void callback(int id) {
      if (id == 23000) {
         this.close();
         Mission mission = this.mMission;
         if (mission == null) {
            return;
         }

         mission.introEventFinished();
         if (this.mMission.hasIntroEvent()) {
            HUD.openMission(this.mMission);
         }
      }
   }

   public void setOkButtonVisible(boolean visible) {
      this.mButtonOK.setVisible(visible);
   }

   public void setModal(boolean modal) {
      this.mIsModal = modal;
   }

   public void setWideBubble(boolean wide) {
      if (this.mUseWideBubble != wide) {
         this.mUseWideBubble = wide;
         int width = this.mBubbleInitialWidth;
         if (wide) {
            width = width * 3 / 2;
         }

         this.mBubbleWidth = width;
         this.mText.setWidth(this.mBubbleWidth - 2 * this.mTextMargin);
      }
   }

   public void setText(String value) {
      int font = 3;
      if (value != null && value.length() < 80) {
         font = 4;
      }

      HUDAutoTextField text = this.mText;
      text.setText(value, DCiDead.getFont(font));
      this.updateButtonPosition();
   }

   public void setNPC(short npc) {
      this.mNPC = npc;
      this.setPortraitImage();
   }

   public int getNPC() {
      return this.mNPC;
   }

   public void setMission(Mission mission) {
      this.mMission = mission;
   }

   public Mission getMission() {
      return this.mMission;
   }

   @Override
   public void setPosition(int x, int y) {
      this.mPosX = x;
      this.mPosY = y;
      int width = Toolkit.getScreenWidth();
      this.mFlipNPC = (width >> 1) + this.mMarginWidth < x;
      if (this.mFlipNPC) {
         width = this.mDialogAnimation.getWidth();
         this.mPosX -= width;
      }

      CollisionBox box = this.mDialogAnimation.getCollisionBox(0);
      HUDAutoTextField text = this.mText;
      text.setPosition(box.getX(), box.getY());
      this.updateButtonPosition();
   }

   @Override
   public int getWidth() {
      return this.mDialogAnimation.getWidth();
   }

   @Override
   public int getHeight() {
      int height = this.mText.getTextHeight();
      return height + 2 * this.mMarginHeight;
   }

   public void updateButtonPosition() {
      CollisionBox box = this.mDialogAnimation.getCollisionBox(1);
      int height = this.mDialogAnimation.getHeight();
      int y = box.getY();
      HUDButton button = this.mButtonOK;
      int initialWidth = this.mBubbleInitialWidth;
      int width = this.mBubbleWidth;
      int x = box.getX();
      int textHeight = this.mText.getTextHeight();
      button.setPosition(x + (width - initialWidth) / 2, textHeight + y - height + 2 * this.mMarginHeight);
   }

   public void setPortraitImage() {
      switch (this.mNPC) {
         case 807:
            this.mPortrait.setImage(66373);
            break;
         case 2868:
            this.mPortrait.setImage(67407);
            break;
         case 2869:
            this.mPortrait.setImage(66369);
            break;
         case 2870:
            this.mPortrait.setImage(66457);
            break;
         case 2874:
            this.mPortrait.setImage(66383);
            break;
         case 3007:
            this.mPortrait.setImage(75745);
            break;
         case 3111:
            this.mPortrait.setImage(76271);
            break;
         case 3233:
         case 3245:
            this.mPortrait.setImage(77039);
            break;
         case 3807:
            this.mPortrait.setImage(77137);
            break;
         case 4328:
            this.mPortrait.setImage(77575);
            break;
         case 4330:
            this.mPortrait.setImage(77427);
            break;
         case 4337:
            this.mPortrait.setImage(77499);
            break;
         case 4361:
            this.mPortrait.setImage(77723);
      }
   }
}
