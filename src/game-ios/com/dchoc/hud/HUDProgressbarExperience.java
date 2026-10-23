package com.dchoc.hud;

import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.toolkit.Toolkit;

public class HUDProgressbarExperience extends HUDProgressbar {
   private static HUDInteger mCurrentLevel;
   private static HUDInteger mExperience;

   public HUDProgressbarExperience(int background, int fill, int symbol) {
      super(background, fill, symbol);
      mCurrentLevel = new HUDInteger(65571);
      mExperience = new HUDInteger(65563, 65559);
      this.setMaxValue(PlayerProfile.getXpLimit());
      this.setValue(PlayerProfile.getXp());
   }

   @Override
   public void draw() {
      super.draw();
      int width = Toolkit.getScreenWidth();
      mCurrentLevel.setPosition(this.specialPos1X + (width >> 1), this.specialPos1Y);
      mCurrentLevel.draw();
      mExperience.setPosition(this.specialPos2X + (width >> 1), this.specialPos2Y);
      mExperience.draw();
   }

   @Override
   public void logicUpdate(int delta) {
      int level = PlayerProfile.getLevel();
      int previous = 0;
      if (level > 1) {
         previous = PlayerProfile.getXpLimitForPreviousLevel();
      }

      this.mMaxValue = PlayerProfile.getXpLimit() - previous;
      int oldLevel = mCurrentLevel.getValue();
      mCurrentLevel.setValue(PlayerProfile.getLevel());
      mExperience.setValue(PlayerProfile.getXp());
      this.setValue(PlayerProfile.getXp() - previous);
      super.logicUpdate(delta);
      mCurrentLevel.logicUpdate(delta);
      mExperience.logicUpdate(delta);
      if (oldLevel != PlayerProfile.getLevel()) {
         this.finishAnimation();
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      super.pointerEvent(event, 3);
   }

   @Override
   public int getX() {
      int width = Toolkit.getScreenWidth();
      return this.specialPos2X + (width >> 1);
   }

   @Override
   public int getY() {
      return this.specialPos2Y;
   }

   @Override
   public void callback(int id) {
   }
}
