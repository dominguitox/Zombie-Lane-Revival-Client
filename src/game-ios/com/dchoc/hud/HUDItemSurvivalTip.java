package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.missions.Mission;
import com.dchoc.spriteobject.SpriteObject;

public class HUDItemSurvivalTip extends HUDItem {
   private SlotBackground mSlotBackground;
   private HUDImage mThumbnail;
   private HUDAutoTextField mTitle;
   private HUDAutoTextField mDescription;
   private HUDTarget mCounter;
   private Mission mMission;
   private int mIndex;

   public HUDItemSurvivalTip(int index, Mission mission, int total) {
      super(index);
      this.mIndex = index;
      this.mMission = mission;
      SpriteObject sprite = AnimationsManager.loadShared(74745);
      this.mWidth = sprite.getCollisionBox(0).getWidth();
      this.mHeight = sprite.getCollisionBox(0).getHeight();
      this.mSlotBackground = new SlotBackground(sprite.getCollisionBox(10), this);
      int icon = mission.getIconID();
      int x = sprite.getCollisionBox(20).getX();
      int width = sprite.getCollisionBox(20).getWidth();
      int y = sprite.getCollisionBox(20).getY();
      int height = sprite.getCollisionBox(20).getHeight();
      this.mThumbnail = new HUDImage(icon, x + width / 2, y + height / 2);
      this.mTitle = new HUDAutoTextField(sprite.getCollisionBox(21));
      HUDAutoTextField text = this.mTitle;
      text.setText(this.mMission.getTitleTextID(), 2);
      this.mDescription = new HUDAutoTextField(sprite.getCollisionBox(22));
      this.mDescription.setSplitUsingWidth(true);
      text = this.mDescription;
      text.setText(this.mMission.getDescriptionTextID(), 3);
      this.mCounter = new HUDTarget(74607, 74795);
      this.mCounter.setValues(index, total);
      HUDTarget counter = this.mCounter;
      x = sprite.getCollisionBox(22).getX();
      width = sprite.getCollisionBox(22).getWidth();
      int counterWidth = this.mCounter.getWidth();
      y = sprite.getCollisionBox(21).getY();
      counter.setPosition(x + width - counterWidth, y);
      this.mObjects = new HUDObject[]{this.mSlotBackground, this.mThumbnail, this.mTitle, this.mDescription, this.mCounter};
      this.initObjects();
      this.refresh();
   }

   public int getIndex() {
      return this.mIndex;
   }

   public Mission getMission() {
      return this.mMission;
   }

   @Override
   public int getParameterForSort() {
      boolean dark = this.mSlotBackground.isDarkColors();
      return dark ? -this.mId : this.mId;
   }

   @Override
   public void refresh() {
      this.mThumbnail.setVisible(true);
      this.mTitle.setVisible(true);
      this.mDescription.setVisible(true);
      this.mCounter.setVisible(true);
      SlotBackground slot = this.mSlotBackground;
      slot.setDarkColors(this.mMission.isNew());
      this.mMission.resetNew();
   }
}
