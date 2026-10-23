package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public class HUDBubble extends HUDObject {
   public static final int PIPKA_NONE = -1;
   public static final int PIPKA_LEFT_TOP = 0;
   public static final int PIPKA_LEFT_CENTER = 1;
   public static final int PIPKA_LEFT_BOTTOM = 2;
   public static final int PIPKA_RIGHT_TOP = 3;
   public static final int PIPKA_RIGHT_CENTER = 4;
   public static final int PIPKA_RIGHT_BOTTOM = 5;
   public static final int PIPKA_TOP_LEFT = 6;
   public static final int PIPKA_TOP_CENTER = 7;
   public static final int PIPKA_TOP_RIGHT = 8;
   public static final int PIPKA_BOTTOM_LEFT = 9;
   public static final int PIPKA_BOTTOM_CENTER = 10;
   public static final int PIPKA_BOTTOM_RIGHT = 11;
   private static SpriteObject mBubbleBackground;
   private static SpriteObject mPipka;
   private boolean mTooltip;
   private int mPipkaPosX;
   private int mPipkaPosY;
   private boolean mCenterScale;
   private float mScaleFactor;
   private int mPipkaPosType = -1;

   public HUDBubble() {
      super((byte)5);
      this.mTooltip = false;
      mPipka = AnimationsManager.loadShared(67313);
      mBubbleBackground = AnimationsManager.loadShared(67311);
   }

   public void setPipkaPosType(int type) {
      this.mPipkaPosType = type;
   }

   public void enableCenterScale(boolean enable) {
      this.mCenterScale = enable;
   }

   public void setScaleFactor(float factor) {
      float value;
      if (factor < 0.1F) {
         value = 0.0F;
      } else {
         value = factor;
         if (factor > 1.0F) {
            value = 1.0F;
         }
      }

      this.mScaleFactor = value;
   }

   public void setAsTooltip() {
      this.mTooltip = true;
   }

   public void disablePipkaPosition() {
      this.mPipkaPosType = -1;
      this.mPipkaPosX = -1;
      this.mPipkaPosY = -1;
   }

   public void setPipkaPosition(int x, int y) {
      this.mPipkaPosX = x;
      this.mPipkaPosY = y;
   }

   public int getPipkaX() {
      return this.mPipkaPosX;
   }

   public int getPipkaY() {
      return this.mPipkaPosY;
   }

   @Override
   public void draw() {
      int parentX = 0;
      int parentY = 0;
      if (this.mParent != null) {
         parentX = this.mParent.getX();
         parentY = this.mParent.getY();
      }

      int width;
      int height;
      float scale;
      if (!this.mCenterScale) {
         scale = this.mScaleFactor;
         width = this.mWidth;
         height = this.mHeight;
      } else {
         scale = this.mScaleFactor;
         if (scale == 0.0F) {
            return;
         }

         width = this.mWidth;
         if ((int)(width * scale) == 0) {
            return;
         }

         height = this.mHeight;
         if ((int)(height * scale) == 0) {
            return;
         }
      }

      drawBubbleBackground(this.mPosX + parentX, this.mPosY + parentY, width, height, mBubbleBackground, this.mCenterScale, scale);
      if (this.mPipkaPosType == -1) {
         mPipka.draw(this.mPipkaPosX + parentX, this.mPipkaPosY + parentY);
      } else {
         float pipkaScale = 1.0F;
         if (this.mTooltip) {
            DeviceWrapper.useHDGraphics();
            pipkaScale = Math.min(0.65F, this.mScaleFactor);
         }

         width = this.mWidth;
         height = this.mHeight;
         int x = this.mPosX;
         int y = this.mPosY;
         int pipkaWidth = mPipka.getWidth();
         y += parentY;
         x += parentX;
         int pipkaHeight = mPipka.getHeight();
         int relativeX = this.mPipkaPosX;
         if (relativeX != -1) {
            relativeX -= x;
         }

         int forcedX = relativeX;
         if (this.mCenterScale) {
            int originalWidth = this.mWidth;
            int originalHeight = this.mHeight;
            float factor = this.mScaleFactor;
            height = (int)(originalHeight * factor);
            width = (int)(originalWidth * factor);
            y = y + (originalHeight >> 1) - (height >> 1);
            x = x + (originalWidth >> 1) - (width >> 1);
            forcedX = -1;
            if (relativeX != -1) {
               forcedX = (int)(relativeX * factor);
            }
         }

         int secondWidth = mPipka.getWidth();
         int pivotX = mPipka.getPivotX();
         int pivotY = mPipka.getPivotY();
         CollisionBox box = mBubbleBackground.getCollisionBox(4);
         float factor = this.mScaleFactor;
         int inset = box.getHeight();
         int scaledHeight = (int)(pipkaHeight * pipkaScale);
         int scaledWidth = (int)(pipkaWidth * pipkaScale);
         int offsetX = (int)((secondWidth - pivotX) * pipkaScale);
         int offsetY = (int)(pivotY * pipkaScale);
         if (factor > 0.0F && factor < 1.0F) {
            inset = (int)(inset * this.mScaleFactor);
         }

         box = mPipka.getCollisionBox(0);
         int collisionWidth = box.getWidth();
         if (pipkaScale > 0.0F && pipkaScale < 1.0F) {
            collisionWidth = (int)(collisionWidth * pipkaScale) + 1;
         }

         OGL.pushParameters();
         int pipkaX = 0;
         int pipkaY = 0;
         int halfWidth = scaledWidth >> 1;
         if (Integer.compareUnsigned(this.mPipkaPosType, 11) <= 0) {
            int halfHeight = scaledHeight >> 1;
            int thirdHeightHalf = (height / 3 + (height >> 31)) / 2;
            int thirdWidthHalf = (width / 3 + (width >> 31)) / 2;
            switch (this.mPipkaPosType) {
               case 0:
                  pipkaY = y - halfHeight - offsetY + thirdHeightHalf;
                  pipkaX = inset + x - collisionWidth;
                  break;
               case 1:
                  pipkaX = inset + x - collisionWidth;
                  pipkaY = (height >> 1) - halfHeight + y + offsetY;
                  break;
               case 2:
                  pipkaY = height - halfHeight + y - offsetY - thirdHeightHalf;
                  pipkaX = inset + x - collisionWidth;
                  break;
               case 3:
                  pipkaY = y - halfHeight - offsetY + thirdHeightHalf;
                  pipkaX = x + width - inset + collisionWidth;
                  break;
               case 4:
                  pipkaY = (height >> 1) - halfHeight + y + offsetY;
                  pipkaX = x + width - inset + collisionWidth;
                  break;
               case 5:
                  pipkaY = height - halfHeight + y - offsetY - thirdHeightHalf;
                  pipkaX = x + width - inset + collisionWidth;
                  break;
               case 6:
                  pipkaX = x - halfWidth - offsetX + thirdWidthHalf;
                  pipkaY = inset + y - collisionWidth;
                  break;
               case 7:
                  pipkaY = inset + y - collisionWidth;
                  pipkaX = (width >> 1) - halfWidth + x - offsetX;
                  break;
               case 8:
                  pipkaX = width - halfWidth + x - offsetX - thirdWidthHalf;
                  pipkaY = inset + y - collisionWidth;
                  break;
               case 9:
                  pipkaX = x - halfWidth + thirdWidthHalf;
                  pipkaY = y + height - inset + collisionWidth;
                  break;
               case 10:
                  pipkaY = y + height - inset + collisionWidth;
                  pipkaX = (width >> 1) - halfWidth + x - offsetX;
                  break;
               case 11:
                  pipkaX = width - halfWidth + x - thirdWidthHalf;
                  pipkaY = y + height - inset + collisionWidth;
            }
         }

         if (forcedX != -1) {
            pipkaX = x - halfWidth + forcedX;
         }

         OGL.setScale(pipkaScale, pipkaScale);
         int flip = 0;
         int type = this.mPipkaPosType;
         if (Integer.compareUnsigned(type, 12) < 0) {
            int mask = 1 << type;
            if ((mask & 56) != 0) {
               flip = 1;
            } else if ((mask & 448) != 0) {
               OGL.setAngle(90.0F);
               flip = 2;
            } else if ((mask & 3584) != 0) {
               OGL.setAngle(270.0F);
            }
         }

         mPipka.draw(pipkaX, pipkaY, flip);
         OGL.popParameters();
      }
   }

   public static void drawBubbleBackground(int x, int y, int width, int height, SpriteObject sprite, boolean centerScale, float factor) {
      int drawWidth = width;
      int drawHeight = height;
      if (centerScale) {
         drawHeight = (int)(height * factor);
         drawWidth = (int)(width * factor);
         y = y + (height >> 1) - (drawHeight >> 1);
         x = x + (width >> 1) - (drawWidth >> 1);
      }

      int screenWidth = Toolkit.getScreenWidth();
      int screenHeight = Toolkit.getScreenHeight();
      OGL.setClip(0, 0, screenWidth, screenHeight);
      CollisionBox outer = sprite.getCollisionBox(3);
      CollisionBox inner = sprite.getCollisionBox(2);
      int outerWidth = outer.getWidth();
      int outerHeight = outer.getHeight();
      int borderWidth = inner.getWidth();
      int borderHeight = inner.getHeight();
      if (centerScale) {
         OGL.pushParameters();
         OGL.setScale(factor, factor);
         borderHeight = (int)(borderHeight * factor);
         borderWidth = (int)(borderWidth * factor);
         outerHeight = (int)(outerHeight * factor);
         outerWidth = (int)(outerWidth * factor);
      }

      int tileHeight = outerHeight - 2 * borderHeight;
      int tileWidth = outerWidth - 2 * borderWidth;
      OGL.setClip(x, y, borderWidth, borderHeight);
      sprite.draw(x, y);
      int rightClip = x + drawWidth - borderWidth;
      OGL.setClip(rightClip, y, borderWidth, borderHeight);
      int rightDraw = x + drawWidth - outerWidth;
      sprite.draw(rightDraw, y);
      int bottomClip = y + drawHeight - borderHeight;
      OGL.setClip(x, bottomClip, borderWidth, borderHeight);
      int bottomDraw = y + drawHeight - outerHeight;
      sprite.draw(x, bottomDraw);
      OGL.setClip(rightClip, bottomClip, borderWidth, borderHeight);
      sprite.draw(rightDraw, bottomDraw);
      screenWidth = Toolkit.getScreenWidth();
      screenHeight = Toolkit.getScreenHeight();
      OGL.setClip(0, 0, screenWidth, screenHeight);
      int innerX = borderWidth + x;
      if (innerX < rightClip) {
         int remaining = drawWidth - 2 * borderWidth;
         int topRemaining = remaining;
         int drawX = x;

         do {
            int clipWidth = tileWidth;
            if (rightClip < borderWidth + tileWidth + drawX) {
               clipWidth = topRemaining;
            }

            OGL.setClip(borderWidth + drawX, y, clipWidth, borderHeight);
            sprite.draw(drawX, y);
            drawX += tileWidth;
            topRemaining -= tileWidth;
         } while (borderWidth + drawX < rightClip);

         drawX = x;

         for (int clipX = innerX; clipX < rightClip; clipX = borderWidth + drawX) {
            int clipWidth = tileWidth;
            if (rightClip < borderWidth + tileWidth + drawX) {
               clipWidth = remaining;
            }

            OGL.setClip(borderWidth + drawX, bottomClip, clipWidth, borderHeight);
            sprite.draw(drawX, bottomDraw);
            remaining -= tileWidth;
            drawX += tileWidth;
         }
      }

      int innerY = y + borderHeight;
      if (innerY < bottomClip) {
         int remaining = drawHeight - 2 * borderHeight;
         int leftRemaining = remaining;
         int drawY = y;

         do {
            int clipHeight = tileHeight;
            if (bottomClip < borderHeight + tileHeight + drawY) {
               clipHeight = leftRemaining;
            }

            OGL.setClip(x, borderHeight + drawY, borderWidth, clipHeight);
            sprite.draw(x, drawY);
            drawY += tileHeight;
            leftRemaining -= tileHeight;
         } while (borderHeight + drawY < bottomClip);

         drawY = y;

         for (int clipY = innerY; clipY < bottomClip; drawY += tileHeight) {
            int clipHeight = tileHeight;
            if (bottomClip < borderHeight + tileHeight + drawY) {
               clipHeight = remaining;
            }

            OGL.setClip(rightClip, borderHeight + drawY, borderWidth, clipHeight);
            sprite.draw(rightDraw, drawY);
            remaining -= tileHeight;
            clipY = borderHeight + drawY + tileHeight;
         }
      }

      if (centerScale) {
         OGL.popParameters();
      }

      screenWidth = Toolkit.getScreenWidth();
      screenHeight = Toolkit.getScreenHeight();
      OGL.setClip(0, 0, screenWidth, screenHeight);
      OGL.setColorRGB(16774357);
      CollisionBox box = mBubbleBackground.getCollisionBox(4);
      int inset = box.getHeight();
      if (factor < 1.0F && centerScale) {
         inset = (int)(box.getHeight() * factor);
      }

      int quarter = inset / 4;
      OGL.fillRect(innerX - quarter, y + inset, drawWidth - 2 * (borderWidth - quarter), drawHeight - 2 * inset);
   }

   @Override
   public void pointerEvent(TouchEvent event) {
   }

   @Override
   public void logicUpdate(int delta) {
   }

   @Override
   public void callback(int id) {
   }

   public SpriteObject getBackground() {
      return mBubbleBackground;
   }

   public int getPipkaWidth() {
      boolean tooltip = this.mTooltip;
      int width = mPipka.getWidth();
      if (tooltip) {
         width = (int)(width * 0.65F);
         DeviceWrapper.useHDGraphics();
      }

      return width;
   }

   public int getPipkaHeight() {
      boolean tooltip = this.mTooltip;
      int height = mPipka.getHeight();
      if (tooltip) {
         height = (int)(height * 0.65F);
         DeviceWrapper.useHDGraphics();
      }

      return height;
   }
}
