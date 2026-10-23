package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tracking.ButtonPressedEvent;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public class HUDButtonOnOff extends HUDObject {
   private static final String ON = "on";
   private static final String OFF = "off";
   public static final int STATE_ON = 0;
   public static final int STATE_OFF = 1;
   private SpriteObject mAnimation;
   private int mState;
   private boolean mDrawText;
   private boolean mLocked;
   private ButtonPressedEvent mTrackinEvent;

   public HUDButtonOnOff(int id, int x, int y, int resource, boolean drawText) {
      super((byte)11, id);
      super.setPosition(x, y);
      this.mDrawText = drawText;
      this.mAnimation = AnimationsManager.loadShared(resource);
      this.mState = 0;
      this.mLocked = false;
   }

   public void setLocked(boolean locked) {
      this.mLocked = locked;
   }

   public boolean isLocked() {
      return this.mLocked;
   }

   public void setStatus(boolean status) {
      this.setState(status ? 0 : 1);
   }

   public void setState(int state) {
      this.mState = state;
   }

   public int getState() {
      return this.mState;
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         int x = this.getX();
         int y = this.getY();
         if (this.mAnimation != null) {
            int animationCount = this.mAnimation.getAnimationCount();
            int frame = this.mState;
            if (animationCount < frame) {
               frame = 0;
            }

            this.mAnimation.setAnimationFrame(frame);
            this.mAnimation.draw(x, y);
            if (this.mDrawText) {
               ImageFont font = DCiDead.getFont(4);
               int textId = this.mState == 0 ? 2342 : 2343;
               String text = Toolkit.getText(textId);
               int width = this.mAnimation.getWidth();
               int height = font.getHeight();
               font.drawString(text, (width << 1) / 3 + x, y - (height >> 1), 20);
            }
         }
      }
   }

   @Override
   public void logicUpdate(int delta) {
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible() && !event.isConsumed() && !this.mLocked) {
         boolean inside = this.isInside(event.getX(), event.getY());
         boolean initialInside = this.isInside(event.getInitialX(), event.getInitialY());
         if (event.getType() == 0 && inside) {
            iWrapper.playSoundFx(50);
         } else if ((event.getType() != 1 || !initialInside) && (event.getType() != 2 || !inside)) {
            if (event.getType() != 3 || !inside) {
               return;
            }

            this.switchState();
            HUDObject parent = this.mParent;
            if (parent != null) {
               parent.callback(this.mId);
            }

            ButtonPressedEvent tracking = this.mTrackinEvent;
            if (tracking != null) {
               tracking.setValue(this.mState == 0 ? "on" : "off");
               this.mTrackinEvent.trackEvent();
            }
         }

         event.setConsumed(true);
      }
   }

   @Override
   public boolean isInside(int x, int y) {
      if (this.mAnimation == null) {
         return false;
      }

      int ownX = this.getX();
      int ownY = this.getY();
      CollisionBox box = this.mAnimation.getCollisionBox(0);
      return box != null
         && box.getX() + ownX <= x
         && box.getX() + ownX + box.getWidth() >= x
         && box.getY() + ownY <= y
         && box.getY() + ownY + box.getHeight() >= y;
   }

   @Override
   public void callback(int id) {
   }

   public void setTrackingEvent(String name) {
      this.mTrackinEvent = new ButtonPressedEvent(name);
   }

   public void switchState() {
      this.mState = this.mState == 0 ? 1 : 0;
   }
}
