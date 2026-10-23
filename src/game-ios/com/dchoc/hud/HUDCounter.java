package com.dchoc.hud;

import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.windows.WindowTooltip;

public class HUDCounter extends HUDObject {
   public static final int TYPE_NONE = -1;
   public static final int TYPE_COINS = 0;
   public static final int TYPE_CASH = 1;
   public static final int TYPE_FOOD = 2;
   public static final int TYPE_BRICKS = 3;
   private HUDImage mBackground;
   private HUDInteger mInteger;
   private CollisionBox mCollisionBox;
   private int mCounterType = -1;
   private int[] mRefDimensions = new int[4];

   public HUDCounter(int type, int image, CollisionBox box) {
      super((byte)-1);
      this.mCounterType = type;
      if (box == null) {
         this.init(image, 0, 0);
      } else {
         int x = box.getX();
         int y = box.getY();
         this.init(image, x, y);
      }
   }

   public HUDCounter(int type, int image, int x, int y, byte tooltip) {
      super((byte)-1);
      this.mCounterType = type;
      this.mTooltipType = tooltip;
      this.init(image, x, y);
   }

   public void setAnimated(boolean animated) {
      this.mInteger.setAnimated(animated);
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.isVisible()) {
         switch (this.mCounterType) {
            case 0:
               this.setValue(PlayerProfile.getCoins());
               break;
            case 1:
               this.setValue(PlayerProfile.getCash());
               break;
            case 2:
               this.setValue(PlayerProfile.getFood());
               break;
            case 3:
               this.setValue(PlayerProfile.getStone());
         }

         this.mInteger.logicUpdate(delta);
      }
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         this.mBackground.draw();
         HUDInteger counter = this.mInteger;
         int x = this.getX();
         int boxX = this.mCollisionBox.getX();
         int y = this.getY();
         int boxY = this.mCollisionBox.getY();
         int height = this.mCollisionBox.getHeight();
         counter.setPosition(boxX + x, y + boxY + height / 2);
         this.mInteger.draw();
      }
   }

   public int getDestinationPositionForLootX() {
      int x = this.getX();
      return this.mCollisionBox.getX() + x;
   }

   public int getDestinationPositionForLootY() {
      int y = this.getY();
      int boxY = this.mCollisionBox.getY();
      return boxY + y + this.mCollisionBox.getHeight() / 2;
   }

   @Override
   public boolean isInside(int x, int y) {
      int[] box = this.mRefDimensions;
      return x >= box[0] && x <= box[2] + box[0] && y >= box[1] && y <= box[1] + box[3];
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (TutorialFlow.isCompleted() && WindowTooltip.isEnableTooltips() && this.isInside(event.getX(), event.getY())) {
         if (event.getType() == 0) {
            event.setConsumed(true);
            return;
         }

         if (event.getType() == 5) {
            event.setConsumed(true);
            int[] box = this.mRefDimensions;
            this.createTooltip(box[0], box[1], box[2], box[3], this.mTooltipType);
         }
      }
   }

   @Override
   public void callback(int id) {
   }

   public void init(int image, int x, int y) {
      if (image != -1) {
         this.mBackground = new HUDImage(image);
         this.mBackground.setParent(this);
         this.mCollisionBox = this.mBackground.getCollisionBox(0);
      }

      this.mPosX = x;
      this.mPosY = y;
      this.mIsVisible = true;
      this.mInteger = new HUDInteger(65563, 65559);
      this.setRefDimensions();
   }

   public void setValue(int value) {
      this.mInteger.setValue(value);
   }

   public void setRefDimensions() {
      int x = this.getX();
      int y = this.getY();
      int width = this.mBackground.getWidth();
      int height = this.mBackground.getHeight();
      if (this.mBackground.getCollisionBox(1) != null) {
         CollisionBox box = this.mBackground.getCollisionBox(1);
         int boxX = box.getX();
         int boxY = box.getY();
         width = box.getWidth();
         x += boxX;
         y += boxY;
         height = box.getHeight();
      }

      int[] box = this.mRefDimensions;
      box[0] = x;
      box[1] = y;
      box[2] = width;
      box[3] = height;
   }
}
