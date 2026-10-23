package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDCollection;
import com.dchoc.hud.HUDItemSurvivalTip;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.spriteobject.CollisionBox;
import java.util.Vector;

public class WindowSurvivalTip extends Window {
   private HUDItemSurvivalTip mTipInfo;
   private HUDAutoTextField mTitle;

   public WindowSurvivalTip() {
      this.mCollisions = AnimationsManager.loadShared(74741);
      this.mBackground = AnimationsManager.loadShared(74615);
      this.initCollisions();
      this.constructCloseButton(37000);
      CollisionBox box = this.mCollisions.getCollisionBox(2);
      this.mTitle = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      HUDAutoTextField title = this.mTitle;
      title.setText(1666, DCiDead.getFont(5));
      this.mTitle.setCentered(true, false);
      this.mObjects.addObject(this.mTitle);
      this.mObjects.addObject(this.mButtonClose);
   }

   public void setOptions(Mission mission) {
      Vector<Mission> tips = MissionManager.getSurvivalTips();
      int index = tips.indexOf(mission);
      this.mTipInfo = new HUDItemSurvivalTip(index + 1, mission, tips.size());
      CollisionBox box = this.mCollisions.getCollisionBox(1);
      HUDItemSurvivalTip tip = this.mTipInfo;
      tip.setPosition(box.getX(), box.getY());
      this.mTipInfo.setParent(this);
      HUDCollection collection = this.mObjects;
      collection.removeObject(this.mTipInfo.getId());
      this.mObjects.addObject(this.mTipInfo);
   }
}
