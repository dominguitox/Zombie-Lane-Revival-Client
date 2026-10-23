package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public class HUDOptionButton extends HUDObject {
   public static final int STATE_UNPRESSED = 0;
   public static final int STATE_PRESSING = 1;
   public static final int STATE_PRESSED = 2;
   public static final int STATE_UNPRESSING = 3;
   public static final int STATE_DISABLED = 4;
   private int mId;
   private String mLabel;
   private int mState;
   private SpriteObject mImage;
   private CollisionBox mBox;
   private ImageFont mFont;
   private HUDOptionButton[] mNeighbors;

   public HUDOptionButton(int image, int x, int y, int id, HUDOptionButton[] neighbors) {
      super((byte)0);
      this.mImage = AnimationsManager.load(image);
      if (image == -1) {
         this.mFont = DCiDead.getFont(0);
      } else if (image == 66245) {
         this.mFont = DCiDead.getFont(1);
      }

      this.mBox = this.mImage.getCollisionBox(0);
      this.mPosX = x;
      this.mPosY = y;
      this.mId = id;
      this.mIsVisible = true;
      this.mNeighbors = neighbors;
      this.setState(0);
   }

   public void setText(String text) {
      this.mLabel = text;
   }

   public void setText(int text) {
      this.mLabel = Toolkit.getText(text);
   }

   @Override
   public void draw() {
      if (this.mIsVisible) {
         int parentX = 0;
         int parentY = 0;
         if (this.mParent != null) {
            parentX = this.mParent.getX();
            parentY = this.mParent.getY();
         }

         if (this.mState >= 1 && this.mState <= 3) {
            this.mImage.setAnimationFrame(1);
         } else if (this.mState == 0) {
            this.mImage.setAnimationFrame(0);
         } else if (this.mState == 4) {
            this.mImage.setAnimationFrame(2);
         }

         this.mImage.draw(this.mPosX + parentX, this.mPosY + parentY);
         String label = this.mLabel;
         if (label != null) {
            int x = this.mPosX;
            ImageFont font = this.mFont;
            int width = this.mBox.getWidth();
            int y = this.mPosY;
            int height = this.mBox.getHeight();
            int fontHeight = font.getHeight();
            font.drawString(label, x + parentX + width / 2, parentY + y + height / 2 + fontHeight / 2, 33);
         }
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.mIsVisible) {
         int parentX = 0;
         int parentY = 0;
         if (this.mParent != null) {
            parentX = this.mParent.getX();
            parentY = this.mParent.getY();
         }

         int type = event.getType();
         if (type == 2) {
            if (this.mState == 1) {
               if (this.mParent != null) {
                  this.mParent.callback(this.mId);
               }

               this.setState(2);
               iWrapper.playSoundFx(51);
            } else {
               if (this.mState != 3) {
                  return;
               }

               this.setState(0);
            }

            event.setConsumed(true);
         } else if (type == 0 && this.mState == 0) {
            if (event.getX() >= this.mPosX + this.mBox.getX() + parentX) {
               int x = event.getX();
               int posX = this.mPosX;
               int boxX = this.mBox.getX();
               if (this.mBox.getWidth() + posX + parentX + boxX >= x) {
                  if (event.getY() >= this.mPosY + this.mBox.getY() + parentY) {
                     int y = event.getY();
                     int posY = this.mPosY;
                     int boxY = this.mBox.getY();
                     if (this.mBox.getHeight() + posY + parentY + boxY >= y) {
                        if (this.mState == 0) {
                           if (this.mNeighbors != null) {
                              for (int i = 0; i < (this.mNeighbors == null ? 0 : this.mNeighbors.length); i++) {
                                 this.mNeighbors[i].unpress();
                              }
                           }

                           this.setState(1);
                        }

                        event.setConsumed(true);
                     }
                  }
               }
            }
         }
      }
   }

   public void enable() {
      this.setState(0);
   }

   public void disable() {
      this.setState(4);
   }

   public boolean isDisabled() {
      return this.mState == 4;
   }

   public void unpress() {
      if (this.mState != 4) {
         this.setState(0);
      }
   }

   public void press() {
      if (this.mNeighbors != null) {
         for (int i = 0; i < (this.mNeighbors == null ? 0 : this.mNeighbors.length); i++) {
            HUDOptionButton neighbor = this.mNeighbors[i];
            if (neighbor != null && !neighbor.isDisabled()) {
               this.mNeighbors[i].unpress();
            }
         }
      }

      this.setState(2);
   }

   public boolean isPressed() {
      return this.mState == 2;
   }

   @Override
   public void logicUpdate(int delta) {
   }

   @Override
   public void callback(int id) {
   }

   public SpriteObject getImage() {
      return this.mImage;
   }

   public void setState(int state) {
      this.mState = state;
   }
}
