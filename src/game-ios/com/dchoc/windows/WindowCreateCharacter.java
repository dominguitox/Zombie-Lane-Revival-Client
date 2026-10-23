package com.dchoc.windows;

import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDOptionButton;
import com.dchoc.hud.PaperDoll;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.AvatarItem;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.MathUtils;
import java.util.Vector;

public class WindowCreateCharacter extends Window {
   private static RoundRectangle mRectangle;
   private static HUDButton mButtonPlay;
   private static HUDOptionButton[] mSexButtons;
   private static int mGenderIndex;
   private static SpriteObject[] mShotgunAnim;
   private static HUDOptionButton[] mSkinButtons;
   private static HUDButton mButtonCustom;
   private static HUDButton mButtonRandom;
   private static int mSexButtonHeight;
   private static CollisionBox mPaperDollBodyBox;
   private static int mPaperDollCenterX;
   private static int mPaperDollCenterY;
   private static AvatarDescription mAvatarDescription;
   private static int[] mPaperDollHeightMax;
   private static HUDAutoTextField mTitle;

   @Override
   public void draw() {
      super.draw();
      if (this.mState != 1) {
         int windowX = this.getX();
         int centerX = mPaperDollCenterX;
         int boxX = mPaperDollBodyBox.getX();
         int boxWidth = mPaperDollBodyBox.getWidth();
         int secondBoxX = mPaperDollBodyBox.getX();
         int secondBoxWidth = mPaperDollBodyBox.getWidth();
         int windowY = this.getY();
         int centerY = mPaperDollCenterY;
         int boxY = mPaperDollBodyBox.getY();
         int maxHeight = mPaperDollHeightMax[mGenderIndex];
         AvatarDescription avatar = PlayerProfile.getActiveAvatar();
         SpriteObject weapon;
         if (mGenderIndex == 1) {
            weapon = mShotgunAnim[1];
         } else {
            if (mGenderIndex != 0) {
               return;
            }

            weapon = mShotgunAnim[0];
         }

         PaperDoll.drawPaperdoll(
            centerX + windowX - boxX - boxWidth / 2 + secondBoxX * 2 + secondBoxWidth, centerY + windowY - boxY - maxHeight / 2, avatar, true, weapon
         );
      }
   }

   @Override
   public void callback(int id) {
      if (Integer.compareUnsigned(id, 11000) < 0) {
         switch (id) {
            case 3112:
               PlayerProfile.getActiveAvatar().setGender(1921);
               mGenderIndex = 0;
               mPaperDollBodyBox = PaperDoll.getBodyBox(0);
               this.adjustSkinColorButtons();
               break;
            case 3113:
               PlayerProfile.getActiveAvatar().setGender(1922);
               mGenderIndex = 1;
               mPaperDollBodyBox = PaperDoll.getBodyBox(1);
               this.adjustSkinColorButtons();
               break;
            case 3114:
               mAvatarDescription.unequipItem(2123);
               break;
            case 3115:
            case 3116:
            case 3117:
               AvatarItem[] tones = this.getCurrentSkintones();
               mAvatarDescription.equipItem(tones[id - 3115]);
         }
      } else if (id == 11000) {
         WindowManager.closeWindow();
         if (!TutorialFlow.isCompleted()) {
            TutorialFlow.changeState(1);
         }
      } else if (id == 11002) {
         WindowManager.closeWindow();
         WindowManager.openWindow((byte)25);
         if (!TutorialFlow.isCompleted()) {
            TutorialFlow.changeState(1);
         }
      } else if (id == 11001) {
         this.randomizeCharacter();
      }
   }

   @Override
   public void loadResources() {
      this.mCollisions = AnimationsManager.loadShared(74713);
      this.mBackground = AnimationsManager.loadShared(74619);
      this.initCollisions();
      mTitle = new HUDAutoTextField(this.getCollisionBox(1));
      mTitle.setCentered(true, false);
      mTitle.setText(142, 5);
      HUDAutoTextField title = mTitle;
      title.setPositionY(title.getY() + 5);
      mButtonPlay = new HUDButton(11000, 0, 0, 74817);
      mButtonPlay.setText(1929);
      CollisionBox rectangleBox = this.getCollisionBox(2);
      mRectangle = new RoundRectangle(0);
      RoundRectangle rectangle = mRectangle;
      rectangle.setSize(rectangleBox.getWidth(), rectangleBox.getHeight());
      CollisionBox wood = this.getCollisionBox(0);
      rectangle = mRectangle;
      int woodWidth = wood.getWidth();
      int rectangleWidth = mRectangle.getWidth();
      int woodY = wood.getY();
      int fontHeight = mTitle.getFont().getHeight();
      rectangle.setPosition((woodWidth - rectangleWidth) / 2, woodY + fontHeight + 5);
      HUDButton play = mButtonPlay;
      int rectangleX = mRectangle.getX();
      rectangleWidth = mRectangle.getWidth();
      int playWidth = mButtonPlay.getWidth();
      int rectangleY = mRectangle.getY();
      int rectangleHeight = mRectangle.getHeight();
      int playHeight = mButtonPlay.getHeight();
      play.setPosition(rectangleX + (rectangleWidth - playWidth) / 2, rectangleY + rectangleHeight - playHeight / 2);
      mSexButtons = new HUDOptionButton[2];
      CollisionBox box2 = this.getCollisionBox(2);
      CollisionBox box3 = this.getCollisionBox(3);
      int box3X = box3.getX();
      int box2X = box2.getX();
      int maleOffsetX = box3X - box2X;
      rectangleX = mRectangle.getX();
      rectangleY = mRectangle.getY();
      int box3Y = box3.getY();
      int box2Y = box2.getY();
      HUDOptionButton option = new HUDOptionButton(74631, rectangleX + maleOffsetX, rectangleY + box3Y - box2Y, 3112, mSexButtons);
      mSexButtons[0] = option;
      CollisionBox box4 = this.getCollisionBox(4);
      rectangleX = mRectangle.getX();
      int box4X = box4.getX();
      box2X = box2.getX();
      rectangleY = mRectangle.getY();
      int box4Y = box4.getY();
      box2Y = box2.getY();
      option = new HUDOptionButton(74625, rectangleX + box4X - box2X, rectangleY + box4Y - box2Y, 3113, mSexButtons);
      mSexButtons[1] = option;
      mSexButtons[0].press();
      SpriteObject image = mSexButtons[0].getImage();

      for (int i = 0; i < (mSexButtons == null ? 0 : mSexButtons.length); i++) {
         HUDOptionButton button = mSexButtons[i];
         int x = button.getX();
         int width = image.getWidth();
         int y = mSexButtons[i].getY();
         int height = image.getHeight();
         button.setPosition(x + width / 2, y + height / 2);
      }

      int halfWidth = 0;
      int halfHeight = 0;
      if (image != null) {
         halfWidth = image.getWidth() / 2;
         halfHeight = image.getHeight() / 2;
         mSexButtonHeight = image.getHeight();
      }

      mPaperDollHeightMax = new int[2];
      mGenderIndex = 1;
      CollisionBox body = PaperDoll.getBodyBox(1);
      mPaperDollBodyBox = body;
      int bodyHeight = body.getHeight();
      mPaperDollHeightMax[mGenderIndex] = bodyHeight;
      mGenderIndex = 0;
      body = PaperDoll.getBodyBox(0);
      mPaperDollBodyBox = body;
      bodyHeight = body.getHeight();
      mPaperDollHeightMax[mGenderIndex] = bodyHeight;
      int femaleX = mSexButtons[1].getX();
      int maleX = mSexButtons[0].getX();
      int secondMaleX = mSexButtons[0].getX();
      mPaperDollCenterX = secondMaleX + halfWidth + (femaleX - maleX - 2 * halfWidth) / 2;
      int maleY = mSexButtons[0].getY();
      int playY = mButtonPlay.getY();
      int secondMaleY = mSexButtons[0].getY();
      mPaperDollCenterY = maleY + halfHeight + (playY - halfHeight - secondMaleY) / 2;
      mShotgunAnim = new SpriteObject[2];
      SpriteObject weapon = AnimationsManager.loadShared(67183);
      mShotgunAnim[0] = weapon;
      weapon = AnimationsManager.loadShared(68967);
      mShotgunAnim[1] = weapon;
      mButtonRandom = new HUDButton(11001, 0, 0, 66245);
      mButtonRandom.setText(280);
      CollisionBox skinBox = AnimationsManager.loadShared(74627).getCollisionBox(0);
      int skinWidth = skinBox.getWidth();
      int skinHeight = skinBox.getHeight();
      maleY = mSexButtons[0].getY();
      int sexHeight = mSexButtonHeight;
      playY = mButtonPlay.getY();
      int randomHeight = mButtonRandom.getHeight();
      int baseY = maleY + sexHeight / 2;
      int gap = playY - baseY - randomHeight - skinHeight;
      int margin = gap / 6;
      rectangleX = mRectangle.getX();
      rectangleWidth = mRectangle.getWidth();
      int baseX = -4 * skinWidth - 15 - maleOffsetX + rectangleX + rectangleWidth;
      HUDButton random = mButtonRandom;
      int randomWidth = random.getWidth();
      random.setPosition(baseX + (4 * skinWidth + 15 - randomWidth) / 2, baseY + (gap - 2 * margin) / 2);
      int randomY = mButtonRandom.getY();
      randomHeight = mButtonRandom.getHeight();
      mSkinButtons = new HUDOptionButton[4];
      int skinX = baseX + skinWidth / 2;
      int skinY = randomY + 2 * margin + randomHeight + skinHeight / 2;
      option = new HUDOptionButton(74627, skinX, skinY, 3114, mSkinButtons);
      mSkinButtons[0] = option;
      option = new HUDOptionButton(74623, skinX + skinWidth + 5, skinY, 3115, mSkinButtons);
      mSkinButtons[1] = option;
      option = new HUDOptionButton(74629, skinX + 2 * (skinWidth + 5), skinY, 3116, mSkinButtons);
      mSkinButtons[2] = option;
      option = new HUDOptionButton(74639, skinX + 3 * (skinWidth + 5), skinY, 3117, mSkinButtons);
      mSkinButtons[3] = option;
      mSkinButtons[1].press();
      mAvatarDescription = PlayerProfile.getActiveAvatar();
      this.mObjects.addObject(mRectangle);
      this.mObjects.addObject(mTitle);
      this.mObjects.addObject(mButtonRandom);
      this.mObjects.addObject(mButtonPlay);
      this.mObjects.addObject(mSkinButtons[0]);
      this.mObjects.addObject(mSkinButtons[1]);
      this.mObjects.addObject(mSkinButtons[2]);
      this.mObjects.addObject(mSkinButtons[3]);
      this.mObjects.addObject(mSexButtons[0]);
      this.mObjects.addObject(mSexButtons[1]);
   }

   @Override
   public void unloadResources() {
      if (mTitle != null) {
         mTitle.freeImages();
         mTitle = null;
      }

      if (mButtonPlay != null) {
         mButtonPlay.freeImages();
         mButtonPlay = null;
      }

      if (mButtonCustom != null) {
         mButtonCustom.freeImages();
         mButtonCustom = null;
      }

      if (mButtonRandom != null) {
         mButtonRandom.freeImages();
         mButtonRandom = null;
      }

      if (mSkinButtons != null) {
         for (int i = 0; i < (mSkinButtons == null ? 0 : mSkinButtons.length); i++) {
            if (mSkinButtons[i] != null) {
               mSkinButtons[i].freeImages();
               mSkinButtons[i] = null;
            }
         }

         mSkinButtons = null;
      }

      if (mSexButtons != null) {
         for (int i = 0; i < (mSexButtons == null ? 0 : mSexButtons.length); i++) {
            if (mSexButtons[i] != null) {
               mSexButtons[i].freeImages();
               mSexButtons[i] = null;
            }
         }

         mSexButtons = null;
      }

      if (mRectangle != null) {
         mRectangle.freeImages();
         mRectangle = null;
      }

      if (mShotgunAnim != null) {
         for (int i = 0; i < (mShotgunAnim == null ? 0 : mShotgunAnim.length); i++) {
            if (mShotgunAnim[i] != null) {
               mShotgunAnim[i].freeResources();
               mShotgunAnim[i] = null;
            }
         }

         mShotgunAnim = null;
      }
   }

   public void randomizeCharacter() {
      Vector items = ItemManager.getItems(1911);
      int[] subtypes = new int[]{2123, 2118, 2117, 2121, 2115, 2122, 2120};
      Vector[] choices = new Vector[7];

      for (int i = 0; i < choices.length; i++) {
         choices[i] = new Vector();
      }

      for (int i = 0; i < items.size(); i++) {
         AvatarItem item = (AvatarItem)items.elementAt(i);

         for (int j = 0; j < subtypes.length; j++) {
            int gender = item.getGender();
            if (gender == PlayerProfile.getActiveAvatar().getGender()
               && item.getSubType() == subtypes[j]
               && item.getRequiredLevel() < 1
               && item.isAvailableForPurchase()) {
               choices[j].addElement(item);
            }
         }
      }

      choices[0].addElement(null);

      for (int i = 0; i < choices.length; i++) {
         if (choices[i].size() > 0) {
            AvatarItem item = (AvatarItem)choices[i].elementAt(MathUtils.random(choices[i].size()));
            int subtype = subtypes[i];
            AvatarDescription avatar = PlayerProfile.getActiveAvatar();
            if (subtype == 2123 && item == null) {
               avatar.unequipItem(2123);
            } else {
               avatar.equipItem(item);
            }
         }
      }

      this.adjustSkinColorButtons();
   }

   public void adjustSkinColorButtons() {
      AvatarItem[] tones = this.getCurrentSkintones();

      for (int i = 0; i < (mSkinButtons == null ? 0 : mSkinButtons.length); i++) {
         mSkinButtons[i].unpress();
      }

      mSkinButtons[0].press();

      for (int i = 0; tones != null && i < tones.length; i++) {
         if (PlayerProfile.getActiveAvatar().isEquipped(tones[i])) {
            mSkinButtons[i + 1].press();
            return;
         }
      }
   }

   public AvatarItem[] getCurrentSkintones() {
      if (PlayerProfile.getActiveAvatar().getGender() == 1921) {
         return new AvatarItem[]{ItemManager.getAvatar(25), ItemManager.getAvatar(2170), ItemManager.getAvatar(2171)};
      } else {
         return PlayerProfile.getActiveAvatar().getGender() == 1922
            ? new AvatarItem[]{ItemManager.getAvatar(34), ItemManager.getAvatar(2206), ItemManager.getAvatar(2207)}
            : null;
      }
   }
}
