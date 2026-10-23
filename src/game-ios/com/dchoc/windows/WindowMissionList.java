package com.dchoc.windows;

import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDItemMissionSlot;
import com.dchoc.hud.HUDTouchArea;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.missions.Mission;
import java.util.Vector;

public class WindowMissionList extends Window {
   public RoundRectangle smRect;
   public HUDAutoTextField smTitle;
   public HUDTouchArea smMissionsList;

   public WindowMissionList() {
      this.mBackground = AnimationsManager.loadShared(74809);
      this.mCollisions = AnimationsManager.loadShared(74885);
      this.initCollisions();
      this.constructCloseButton(40000);
      this.smRect = new RoundRectangle(0);
      this.smRect.setSizes(this.mCollisions.getCollisionBox(1));
      this.smTitle = new HUDAutoTextField(this.mCollisions.getCollisionBox(2));
      this.smTitle.setText(2430, 5);
      this.smTitle.setCentered(true, true);
      this.smMissionsList = new HUDTouchArea(false, true, true, this.mCollisions.getCollisionBox(3), new Vector(), false);
      this.smMissionsList.setScrollbar(true);
      this.smMissionsList.setFadingBorder(true);
      this.mObjects.addObject(this.mButtonClose);
      this.mObjects.addObject(this.smRect);
      this.mObjects.addObject(this.smTitle);
      this.mObjects.addObject(this.smMissionsList);
   }

   @Override
   public void open() {
      super.open();
      this.smMissionsList.resetPosition();
   }

   public void addMission(Mission mission) {
      HUDItemMissionSlot item = new HUDItemMissionSlot(mission);
      this.smMissionsList.addItem(item);
   }

   public void removeMission(Mission mission) {
      Vector items = this.smMissionsList.getItems();

      for (int i = 0; i < items.size(); i++) {
         HUDItemMissionSlot item = (HUDItemMissionSlot)items.elementAt(i);
         if (item.getMission() == mission) {
            item.freeImages();
            this.smMissionsList.removeItem(item);
            return;
         }
      }
   }
}
