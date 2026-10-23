package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.WindowMissionList;
import java.util.Vector;

public class HUDMissions extends HUDObject {
   private static int NUMBER_OF_ICONS;
   private static Vector<HUDMissionIcon> smIcons;
   private static SpriteObject smLayout;
   private HUDMissionIcon mShowWindow;

   public HUDMissions() {
      super((byte)-1);
      smIcons = new Vector<>(6);
      smLayout = AnimationsManager.loadShared(65643);
      CollisionBox box = AnimationsManager.loadShared(65551).getCollisionBox(1);
      int y = box.getY();
      int height = box.getHeight();
      SpriteObject upper = AnimationsManager.loadShared(65597);
      SpriteObject lower = AnimationsManager.loadShared(66047);
      int upperY = upper.getCollisionBox(6).getY();
      int lowerY = lower.getCollisionBox(0).getY();
      int screenHeight = Toolkit.getScreenHeight();
      int width = smLayout.getCollisionBox(0).getWidth();
      int top = y + height * 3 / 2;
      int available = upperY - top + lowerY + screenHeight;
      this.setSizes(0, top, width, available);
      NUMBER_OF_ICONS = available / smLayout.getCollisionBox(0).getHeight();
      this.mShowWindow = new HUDMissionIcon();
      this.mShowWindow.setExpandable(true);
      this.mShowWindow.setVisible(false);
      HUDMissionIcon show = this.mShowWindow;
      int iconHeight = smLayout.getCollisionBox(0).getHeight();
      show.setPosition(0, (NUMBER_OF_ICONS - 1) * iconHeight);
      this.mShowWindow.setParent(this);
   }

   public void addMission(Mission mission) {
      HUDMissionIcon icon = new HUDMissionIcon();
      icon.setOptions(mission);
      icon.setParent(this);
      smIcons.insertElementAt(icon, 0);
      this.calculatePositions();
      ((WindowMissionList)WindowManager.getWindow((byte)26)).addMission(mission);
   }

   public void removeMission(Mission mission) {
      for (int i = 0; i < smIcons.size(); i++) {
         if (smIcons.elementAt(i).getMission() == mission) {
            smIcons.elementAt(i).freeImages();
            smIcons.removeElementAt(i);
            break;
         }
      }

      this.calculatePositions();
      ((WindowMissionList)WindowManager.getWindow((byte)26)).removeMission(mission);
   }

   public boolean containsMission(Mission mission) {
      for (int i = 0; i < smIcons.size(); i++) {
         if (smIcons.elementAt(i).getMission() == mission) {
            return true;
         }
      }

      return false;
   }

   public boolean openMission(Mission mission) {
      for (int i = 0; i < smIcons.size(); i++) {
         if (smIcons.elementAt(i).getMission() == mission) {
            smIcons.elementAt(i).doAction();
            return true;
         }
      }

      return false;
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible()) {
         for (int i = 0; i < smIcons.size(); i++) {
            smIcons.elementAt(i).pointerEvent(event);
         }

         this.mShowWindow.pointerEvent(event);
      }
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         for (int i = 0; i < smIcons.size(); i++) {
            smIcons.elementAt(i).draw();
         }

         this.mShowWindow.draw();
         this.drawDebug();
      }
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.isVisible()) {
         for (int i = 0; i < smIcons.size(); i++) {
            smIcons.elementAt(i).logicUpdate(delta);
         }

         this.mShowWindow.logicUpdate(delta);
      }
   }

   public Vector<HUDMissionIcon> getMissions() {
      return smIcons;
   }

   @Override
   public void callback(int id) {
   }

   public void calculatePositions() {
      this.mShowWindow.setVisible(smIcons.size() > NUMBER_OF_ICONS);

      for (int i = 0; i < smIcons.size(); i++) {
         HUDMissionIcon icon = smIcons.elementAt(i);
         icon.setPosition(0, smLayout.getCollisionBox(0).getHeight() * i);
         icon.setVisible(i <= NUMBER_OF_ICONS - 1 && (i <= NUMBER_OF_ICONS - 2 || smIcons.size() <= NUMBER_OF_ICONS));
      }
   }
}
