package com.dchoc.windows;

import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.areas.Area;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.iphonewrappers.ActivityWrapper;
import com.dchoc.toolkit.Toolkit;

public class WindowTraveling extends Window {
   private Area mArea;
   private NeighborProfile mNeighbor;
   private boolean mLoad;
   private HUDAutoTextField mTextTraveling;

   public WindowTraveling() {
      this.mBackground = AnimationsManager.loadShared(74615);
      this.mCollisions = AnimationsManager.loadShared(74771);
      this.initCollisions();
      this.mTextTraveling = new HUDAutoTextField(this.getCollisionBox(1));
      this.mTextTraveling.setCentered(true, true);
      this.mTextTraveling.setSplitUsingWidth(true);
      this.mTextTraveling.setText(1872, 5);
      this.mObjects.addObject(this.mTextTraveling);
      this.mArea = null;
      this.mNeighbor = null;
      this.mLoad = false;
   }

   @Override
   public void open() {
      super.open();
      this.mTextTraveling.logicUpdate(0);
   }

   public void setArea(Area area, NeighborProfile neighbor) {
      if (area != null) {
         this.mArea = area;
         this.mNeighbor = neighbor;
         if (this.mState == 0) {
            this.setState((byte)0);
         }
      }
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.mState == 0) {
         GameEngine engine = GameEngine.getInstance();
         int state = engine.getState();
         if (!this.mLoad) {
            if (Integer.compareUnsigned(state, 7) < 0 && (1 << state & 84) != 0) {
               this.close();
            }
         } else {
            this.mLoad = false;
            int width = Toolkit.getScreenWidth();
            int height = Toolkit.getScreenHeight();
            ActivityWrapper.show(width >> 1, height >> 1);
            engine.loadArea(this.mArea, this.mNeighbor);
            engine.changeState(1);
         }
      }

      super.logicUpdate(delta);
   }

   @Override
   public void close() {
      super.close();
      ActivityWrapper.hide();
   }

   @Override
   public void setState(byte state) {
      super.setState(state);
      if (this.mState == 2) {
         this.mLoad = true;
      }
   }
}
