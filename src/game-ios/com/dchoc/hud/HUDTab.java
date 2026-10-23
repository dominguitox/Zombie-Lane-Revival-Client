package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.spriteobject.CollisionBox;

public class HUDTab extends HUDObject {
   public static final int STATE_PRESSED = 2;
   public static final int STATE_PRESSING = 1;
   public static final int STATE_UNPRESSED = 0;
   public static final int TAB_INVENTORY = 1000;
   public static final int TAB_COLLECTIONS = 1001;
   public static final int TAB_ACHIEVEMENTS = 1002;
   public static final int TAB_SURVIVAL_TIPS = 1003;
   public static final int TAB_CUSTOMIZATION_MAIN = 2000;
   public static final int TAB_CUSTOMIZATION_SPOUSE = 2001;
   public static final int TAB_CUSTOMIZATION_DOG = 2002;
   private int mState;
   private HUDAutoTextField mActiveText;
   private HUDAutoTextField mInactiveText;
   private RoundRectangle mActiveBox;
   private RoundRectangle mInactiveBox;
   private HUDImage mActiveImage;
   private HUDImage mInactiveImage;
   private HUDRedCounter mRedCounter;
   private HUDTab[] mNeighborTabs;

   public HUDTab(int id, CollisionBox active, CollisionBox inactive, int resource, boolean image) {
      super((byte)7, id);
      this.mActiveBox = new RoundRectangle(0, active, this);
      this.mInactiveBox = new RoundRectangle(1, inactive, this);
      this.mActiveImage = new HUDImage(resource);
      this.mInactiveImage = new HUDImage(resource);
      this.mActiveImage.setParent(this);
      this.mInactiveImage.setParent(this);
      HUDImage activeImage = this.mActiveImage;
      int x = this.mActiveBox.getX();
      int width = this.mActiveBox.getWidth();
      int y = this.mActiveBox.getY();
      int height = this.mActiveBox.getHeight();
      activeImage.setPosition(x + width / 2, y + height / 2);
      HUDImage inactiveImage = this.mInactiveImage;
      x = this.mInactiveBox.getX();
      width = this.mInactiveBox.getWidth();
      y = this.mInactiveBox.getY();
      height = this.mInactiveBox.getHeight();
      inactiveImage.setPosition(x + width / 2, y + height / 2);
      this.mActiveBox.setGradient(false);
      this.initRedCounter();
      this.setState(0);
   }

   public HUDTab(int id, CollisionBox active, CollisionBox inactive, int textId) {
      super((byte)7, id);
      this.mActiveBox = new RoundRectangle(0, active, this);
      this.mInactiveBox = new RoundRectangle(1, inactive, this);
      int spaceWidth = DCiDead.getFont(0).stringWidth(" ");
      this.mActiveText = new HUDAutoTextField(active, textId, 0, true, true, this);
      this.mInactiveText = new HUDAutoTextField(inactive, textId, 0, true, true, this);
      HUDAutoTextField text = this.mActiveText;
      int x = active.getX();
      int y = active.getY();
      int width = active.getWidth();
      int height = active.getHeight();
      text.setSizes(x + spaceWidth, y, width - 2 * spaceWidth, height);
      text = this.mInactiveText;
      x = inactive.getX();
      y = inactive.getY();
      width = inactive.getWidth();
      height = inactive.getHeight();
      text.setSizes(x + spaceWidth, y, width - 2 * spaceWidth, height);
      this.mActiveText.setScaleToFit(true);
      this.mInactiveText.setScaleToFit(true);
      this.mActiveBox.setGradient(false);
      this.initRedCounter();
      this.setState(0);
   }

   public void press() {
      this.unpressNeighbors();
      this.setState(2);
   }

   public void setNeighbors(HUDTab[] neighbors) {
      this.mNeighborTabs = neighbors;
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         if (this.mState == 2) {
            this.mActiveBox.draw();
            if (this.mActiveText != null) {
               this.mActiveText.draw();
            }

            HUDImage image = this.mActiveImage;
            if (image != null) {
               image.setAnimationFrame(0);
               this.mActiveImage.draw();
            }
         } else if (Integer.compareUnsigned(this.mState, 1) <= 0) {
            this.mInactiveBox.draw();
            if (this.mInactiveText != null) {
               this.mInactiveText.draw();
            }

            HUDImage image = this.mInactiveImage;
            if (image != null) {
               image.setAnimationFrame(1);
               this.mInactiveImage.draw();
            }
         }

         this.drawDebug();
      }
   }

   public void drawCounter() {
      this.mRedCounter.draw();
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.mIsVisible) {
         int type = event.getType();
         if (type == 2) {
            RoundRectangle box = this.mActiveBox;
            boolean inside = box.isInside(event.getX(), event.getY());
            if (inside && this.mState == 1) {
               this.press();
               this.callback(this.mId);
               iWrapper.playSoundFx(51);
               event.setConsumed(true);
            }
         } else if (type == 0) {
            RoundRectangle box = this.mActiveBox;
            boolean inside = box.isInside(event.getX(), event.getY());
            if (inside && this.mState == 0) {
               this.setState(1);
               event.setConsumed(true);
            }
         }
      }
   }

   @Override
   public void logicUpdate(int delta) {
      HUDAutoTextField text;
      if (this.mState == 2) {
         text = this.mActiveText;
      } else {
         if (Integer.compareUnsigned(this.mState, 1) > 0) {
            return;
         }

         text = this.mInactiveText;
      }

      if (text != null) {
         text.logicUpdate(delta);
      }
   }

   public void unpress() {
      this.setState(0);
   }

   public boolean isPressed() {
      return this.mState == 2;
   }

   @Override
   public void callback(int id) {
      this.mParent.callback(id);
   }

   public void initRedCounter() {
      HUDRedCounter counter = new HUDRedCounter();
      this.mRedCounter = counter;
      counter.setValue(99);
      this.mRedCounter.setParent(this);
   }

   public void unpressNeighbors() {
      HUDTab[] neighbors = this.mNeighborTabs;
      if (neighbors != null) {
         for (int index = 0; index < (neighbors == null ? 0 : (this.mNeighborTabs == null ? 0 : this.mNeighborTabs.length)); index++) {
            neighbors = this.mNeighborTabs;
            HUDTab tab = neighbors[index];
            if (tab != null) {
               tab.unpress();
               neighbors = this.mNeighborTabs;
            }
         }
      }
   }

   public void setState(int state) {
      this.mState = state;
      RoundRectangle box;
      HUDRedCounter counter;
      int x;
      int width;
      if (state == 2) {
         counter = this.mRedCounter;
         x = this.mActiveBox.getX();
         width = this.mActiveBox.getWidth();
         box = this.mActiveBox;
      } else {
         if (Integer.compareUnsigned(state, 1) > 0) {
            return;
         }

         counter = this.mRedCounter;
         x = this.mInactiveBox.getX();
         width = this.mInactiveBox.getWidth();
         box = this.mInactiveBox;
      }

      int y = box.getY();
      int height = this.mRedCounter.getHeight();
      counter.setPosition(width + x, y + height / 2);
   }
}
