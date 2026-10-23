package com.dchoc.hud;

import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public class HUDProgressbarEnergy extends HUDProgressbar {
   private static int mMode;
   private static SpriteObject mBackgroundForFriendsPlace;
   private static SpriteObject mSemicolon;
   private static SpriteObject mZero;
   private static HUDInteger mEnergyMinutes;
   private static HUDInteger mEnergySeconds;
   private static HUDInteger mEnergyPoints;
   private static boolean smAddZero;

   public HUDProgressbarEnergy(int background, int fill, int symbol) {
      super(background, fill, symbol);
      mMode = 0;
      mBackgroundForFriendsPlace = AnimationsManager.loadShared(67419);
      mSemicolon = AnimationsManager.loadShared(65743);
      mZero = AnimationsManager.loadShared(65741);
      mEnergyMinutes = new HUDInteger(65563);
      mEnergySeconds = new HUDInteger(65563);
      mEnergyPoints = new HUDInteger(65563);
      this.setMaxValue(PlayerProfile.getEnergyMax());
      this.setValue(PlayerProfile.getEnergy());
   }

   @Override
   public void logicUpdate(int delta) {
      int time = PlayerProfile.getEnergyChargeTime(true);
      mEnergyPoints.setValue(PlayerProfile.getEnergy());
      mEnergyMinutes.setValue(time / 60);
      int seconds = time % 60;
      mEnergySeconds.setValue(seconds);
      smAddZero = seconds < 10;
      this.setMaxValue(PlayerProfile.getEnergyMax());
      this.setValue(PlayerProfile.getEnergy());
      super.logicUpdate(delta);
      mEnergyMinutes.logicUpdate(delta);
      mEnergySeconds.logicUpdate(delta);
      mEnergyPoints.logicUpdate(delta);
   }

   public int getMode() {
      return mMode;
   }

   public void setMode(int mode) {
      mMode = mode;
   }

   @Override
   public void draw() {
      if (mMode == 1) {
         mBackgroundForFriendsPlace.draw(this.mPosX, 0);
         NeighborProfile neighbor = GameEngineUtils.getNeighbor();

         for (int i = 0; i < neighbor.getActionsLeft(); i++) {
            CollisionBox box = mBackgroundForFriendsPlace.getCollisionBox(i);
            SpriteObject symbol = this.mSymbol;
            int x = this.mPosX;
            int bx = box.getX();
            int by = box.getY();
            symbol.draw(x + bx, by);
         }
      } else if (mMode == 0) {
         super.draw();
         int screenWidth = Toolkit.getScreenWidth();
         CollisionBox semicolon = mSemicolon.getCollisionBox(0);
         CollisionBox zero = mZero.getCollisionBox(0);
         if (!PlayerProfile.hasFullEnergy() && TutorialFlow.isCompleted()) {
            int width = mEnergyMinutes.getWidth();
            width += semicolon.getWidth();
            width += mEnergySeconds.getWidth();
            if (smAddZero) {
               width += zero.getWidth();
            }

            int x;
            int y;
            if (!this.mForcedPosition) {
               y = this.specialPos1Y;
               x = this.specialPos1X + screenWidth / 2;
            } else {
               SpriteObject background = this.getBackground();
               int forced = this.mForcedX;
               x = background.getCollisionBox(1).getX() + forced;
               y = this.specialPos1Y + this.mForcedY;
            }

            x -= width / 2;
            mEnergyMinutes.setPosition(x + mEnergyMinutes.getWidth() / 2, y);
            mEnergyMinutes.draw();
            x += mEnergyMinutes.getWidth();
            mSemicolon.draw(x + semicolon.getWidth() / 2, y);
            x += semicolon.getWidth();
            if (smAddZero) {
               mZero.draw(x + zero.getWidth() / 2, y);
               x += zero.getWidth();
            }

            mEnergySeconds.setPosition(x + mEnergySeconds.getWidth() / 2, y);
            mEnergySeconds.draw();
         }

         if (!this.mForcedPosition) {
            mEnergyPoints.setPosition(this.specialPos2X + screenWidth / 2, this.specialPos2Y);
         } else {
            SpriteObject background = this.getBackground();
            int forced = this.mForcedX;
            HUDInteger points = mEnergyPoints;
            int x = background.getCollisionBox(2).getX();
            points.setPosition(x + forced, this.specialPos2Y + this.mForcedY);
         }

         mEnergyPoints.draw();
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      super.pointerEvent(event, mMode == 1 ? 4 : 2);
   }

   @Override
   public int getX() {
      int width = Toolkit.getScreenWidth();
      return this.specialPos1X + (width >> 1);
   }

   @Override
   public int getY() {
      return this.specialPos1Y;
   }

   @Override
   public void callback(int id) {
   }
}
