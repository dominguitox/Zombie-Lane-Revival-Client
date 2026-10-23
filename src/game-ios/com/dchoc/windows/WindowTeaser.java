package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDImage;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.isometric.IsometricFloor;
import com.dchoc.spriteobject.CollisionBox;

public class WindowTeaser extends Window {
   private static final int[] TITLE = new int[]{2813, 2973, 3606, 3607, 3609, 3622, 3611, 4208};
   private static final int[] DESCRIPTION = new int[]{2814, 2974, 3608, 3636, 3610, 4082, 3612, 4209};
   private static final int[] PORTRAIT = new int[]{77137, 77137, 77575, 77575, 77039, 77039, 77039, 77039};
   private static final int[] ICON = new int[]{77273, 77273, 79709, 79709, 79875, 79875, 80119, 80119};
   private static final byte[] ICON_FRAME = new byte[]{1, 1, 0, 0, 0, 0, 0, 0};
   private HUDAutoTextField mTitle;
   private HUDAutoTextField mDescription;
   private RoundRectangle mRectangle;
   private HUDImage mPortrait;
   private HUDImage mIcon;
   private HUDButton mButton;
   private byte mTeaserType;
   private int mUnlockMission;

   public WindowTeaser() {
      this.mBackground = AnimationsManager.loadShared(74621);
      this.mCollisions = AnimationsManager.loadShared(79361);
      this.initCollisions();
      CollisionBox box = this.getCollisionBox(1);
      this.mTitle = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTitle.setCentered(true, true);
      this.mTitle.setSplitUsingWidth(true);
      this.mTitle.setScaleToFit(true);
      this.mTitle.searchScale(true);
      box = this.getCollisionBox(2);
      this.mDescription = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mDescription.setCentered(true, true);
      this.mDescription.setSplitUsingWidth(true);
      box = this.getCollisionBox(20);
      this.mRectangle = new RoundRectangle(0);
      RoundRectangle rectangle = this.mRectangle;
      rectangle.setSizes(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      box = this.getCollisionBox(30);
      this.mPortrait = new HUDImage(-1, box);
      box = this.getCollisionBox(40);
      this.mIcon = new HUDImage(-1, box);
      this.mIcon.enableUpdate(false);
      box = this.getCollisionBox(10);
      this.mButton = new HUDButton(0, box, 2);
      this.mButton.setText(54);
      this.mObjects.addObject(this.mRectangle);
      this.mObjects.addObject(this.mTitle);
      this.mObjects.addObject(this.mPortrait);
      this.mObjects.addObject(this.mDescription);
      this.mObjects.addObject(this.mIcon);
      this.mObjects.addObject(this.mButton);
   }

   public void setType(byte type, int unlockMission) {
      this.mTeaserType = type;
      this.mUnlockMission = unlockMission;
   }

   @Override
   public void callback(int id) {
      if (id == 0) {
         WindowManager.closeWindow();
         int mission = this.mUnlockMission;
         if (mission != -1) {
            IsometricFloor.showUnlockMission(mission);
         }
      }
   }

   @Override
   public void loadResources() {
      HUDAutoTextField text = this.mTitle;
      text.setText(TITLE[this.mTeaserType], DCiDead.getFont(5));
      text = this.mDescription;
      text.setText(DESCRIPTION[this.mTeaserType], DCiDead.getFont(4));
      this.mPortrait.setImage(PORTRAIT[this.mTeaserType], true);
      this.mIcon.setImage(ICON[this.mTeaserType], true);
      this.mIcon.setAnimationFrame(ICON_FRAME[this.mTeaserType]);
   }

   @Override
   public void unloadResources() {
      if (this.mPortrait != null) {
         this.mPortrait.freeImages();
      }

      if (this.mIcon != null) {
         this.mIcon.freeImages();
      }
   }
}
