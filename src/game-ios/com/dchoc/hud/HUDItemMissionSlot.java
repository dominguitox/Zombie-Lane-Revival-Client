package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.SpriteObject;

public class HUDItemMissionSlot extends HUDItem {
   private Mission mMission;
   private HUDMissionIcon mIcon;
   private HUDAutoTextField mTitle;
   private HUDAutoTextField mDescription;
   private SlotBackground mBackground;

   public HUDItemMissionSlot(Mission mission) {
      this.mMission = mission;
      SpriteObject sprite = AnimationsManager.loadShared(74883);
      this.mWidth = sprite.getCollisionBox(0).getWidth();
      this.mHeight = sprite.getCollisionBox(0).getHeight();
      this.mBackground = new SlotBackground(sprite.getCollisionBox(1));
      this.mIcon = new HUDMissionIcon();
      this.mIcon.setOptions(this.mMission);
      HUDMissionIcon icon = this.mIcon;
      int x = sprite.getCollisionBox(2).getX();
      int width = this.mIcon.getWidth();
      int y = sprite.getCollisionBox(2).getY();
      int height = this.mIcon.getHeight();
      icon.setPosition(x - width / 2, y - height / 2);
      this.mTitle = new HUDAutoTextField(sprite.getCollisionBox(3));
      this.mDescription = new HUDAutoTextField(sprite.getCollisionBox(4));
      this.mDescription.setSplitUsingWidth(true);
      int type = mission.getType();
      HUDAutoTextField title = this.mTitle;
      if (type == 2399) {
         title.setText(1666, 2);
         this.mDescription.setText(2432, 4);
      } else {
         title.setText(this.mMission.getTitleTextID(), 2);
         this.mDescription.setText(this.mMission.getDescriptionTextID(), 4);
      }

      this.mObjects = new HUDObject[]{this.mBackground, this.mIcon, this.mTitle, this.mDescription};
      this.initObjects();
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible() && !event.isConsumed() && this.isInside(event.getX(), event.getY()) && event.getType() == 3) {
         WindowManager.closeWindow();
         this.mIcon.doAction();
      }
   }

   public Mission getMission() {
      return this.mMission;
   }

   @Override
   public void freeImages() {
      this.mIcon.freeImages();
      this.mTitle.freeImages();
      this.mDescription.freeImages();
      this.mBackground.freeImages();
   }
}
