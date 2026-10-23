package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public class RoundRectangle extends HUDObject {
   public static final int RECTANGLE_TYPE_YELLOW = 0;
   public static final int RECTANGLE_TYPE_TAB = 1;
   public static final int RECTANGLE_TYPE_TOUCH_OVERLAY = 2;
   public static final int RECTANGLE_TYPE_TOUCH_BG = 3;
   public static final int RECTANGLE_TYPE_TOUCH_MASK = 4;
   public static final int RECTANGLE_TYPE_ORANGE = 5;
   private static final int COLOR_BORDER = 4206874;
   private static final int COLOR_TOP = -665199;
   private static final int COLOR_MIDDLE = -4957;
   private static SpriteObject mYellowLeftTop;
   private static SpriteObject mYellowRightTop;
   private static SpriteObject mYellowLeftBottom;
   private static SpriteObject mYellowRightBottom;
   private static SpriteObject mYellowTop;
   private static SpriteObject mYellowBottom;
   private static SpriteObject mYellowLeft;
   private static SpriteObject mTouchBgLeftTop;
   private static SpriteObject mTouchBgRightTop;
   private static SpriteObject mTouchBgLeftBottom;
   private static SpriteObject mTouchBgRightBottom;
   private static SpriteObject mOverlayLeftTop;
   private static SpriteObject mOverlayRightTop;
   private static SpriteObject mOverlayLeftBottom;
   private static SpriteObject mOverlayRightBottom;
   private static SpriteObject mOverlayUp;
   private static SpriteObject mOverlayDown;
   private static SpriteObject mTabLeftUp;
   private static SpriteObject mTabRightUp;
   private static SpriteObject mTabTop;
   private static SpriteObject mTabLeft;
   private static SpriteObject mMaskLeftTop;
   private static SpriteObject mMaskRightTop;
   private static SpriteObject mMaskLeftBottom;
   private static SpriteObject mMaskRightBottom;
   private static SpriteObject mOrangeLabelLeft;
   private static SpriteObject mOrangeLabelMiddle;
   private static SpriteObject mOrangeLabelRight;
   private static SpriteObject mRedNotificationLeft;
   private static SpriteObject mRedNotificationMiddle;
   private static SpriteObject mRedNotificationRight;
   private static boolean smAssetsLoaded;
   public int mRectangleType;
   private boolean mHasGradient;
   private boolean mHasBottomPart;
   private boolean mHasRightPart;
   private boolean mHasLeftPart;
   private boolean mClipping = true;

   public RoundRectangle(int type, CollisionBox box, HUDObject parent) {
      super((byte)9);
      this.mHasGradient = true;
      this.mHasBottomPart = true;
      this.mHasRightPart = true;
      this.mHasLeftPart = true;
      this.mRectangleType = type;
      super.setSizes(box);
      this.setParent(parent);
      this.loadAssets();
   }

   public RoundRectangle(int type) {
      super((byte)9);
      this.mHasGradient = true;
      this.mHasBottomPart = true;
      this.mHasRightPart = true;
      this.mHasLeftPart = true;
      this.mRectangleType = type;
      this.loadAssets();
   }

   @Override
   public void callback(int id) {
   }

   public void setBottomPart(boolean part) {
      this.mHasBottomPart = part;
   }

   public void setRightPart(boolean part) {
      this.mHasRightPart = part;
   }

   public void setLeftPart(boolean part) {
      this.mHasLeftPart = part;
   }

   public void setGradient(boolean gradient) {
      this.mHasGradient = gradient;
   }

   public void setClipping(boolean clipping) {
      this.mClipping = clipping;
   }

   public int getResourceHeight() {
      return this.mRectangleType != 6 ? -1 : mRedNotificationLeft.getCollisionBox(0).getHeight();
   }

   public int getMinWidth() {
      if (this.mRectangleType != 6) {
         return -1;
      }

      int left = mRedNotificationLeft.getCollisionBox(0).getWidth();
      int middle = mRedNotificationMiddle.getCollisionBox(0).getWidth();
      int right = mRedNotificationRight.getCollisionBox(0).getWidth();
      return right + left + middle;
   }

   @Override
   public void draw() {
      int x = this.mPosX;
      int y = this.mPosY;
      if (this.mParent != null) {
         x += this.mParent.getX();
         y += this.mParent.getY();
      }

      int clipX = OGL.getClipX();
      int clipY = OGL.getClipY();
      int clipWidth = OGL.getClipWidth();
      int clipHeight = OGL.getClipHeight();
      int left;
      int width;
      int height;
      if (!this.mClipping) {
         height = Toolkit.getScreenHeight();
         left = clipX;
         width = clipWidth;
      } else {
         width = Toolkit.getScreenWidth();
         height = Toolkit.getScreenHeight();
         left = 0;
      }

      OGL.setClip(left, 0, width, height);
      switch (this.mRectangleType) {
         case 0:
            this.drawYellowRectangle(x, y);
            break;
         case 1:
            this.drawTabRectangle(x, y);
            break;
         case 2:
            this.drawOverlayRectangle(x, y);
            break;
         case 3:
            this.drawTouchBgRectangle(x, y);
            break;
         case 4:
            this.drawTouchMask(x, y);
            break;
         case 5:
            this.drawOrangeRectangle(x, y);
            break;
         case 6:
            this.drawRedNotificationRectangle(x, y);
      }

      this.drawDebug();
      OGL.setClip(clipX, clipY, clipWidth, clipHeight);
   }

   public int getLeftBorderWidth() {
      if (this.mRectangleType != 5) {
         return -1;
      }

      SpriteObject sprite = this.mIsMirrored ? mOrangeLabelRight : mOrangeLabelLeft;
      return sprite.getCollisionBox(0).getWidth();
   }

   public int getRightBorderWidth() {
      if (this.mRectangleType != 5) {
         return -1;
      }

      SpriteObject sprite = this.mIsMirrored ? mOrangeLabelLeft : mOrangeLabelRight;
      return sprite.getCollisionBox(0).getWidth();
   }

   public int getLeftBorderHeight() {
      return this.mRectangleType != 5 ? -1 : mOrangeLabelLeft.getCollisionBox(0).getHeight();
   }

   @Override
   public void logicUpdate(int delta) {
   }

   @Override
   public void pointerEvent(TouchEvent event) {
   }

   public void loadAssets() {
      if (!smAssetsLoaded) {
         mYellowLeftTop = AnimationsManager.loadShared(74549);
         mYellowRightTop = AnimationsManager.loadShared(74543);
         mYellowLeftBottom = AnimationsManager.loadShared(74511);
         mYellowRightBottom = AnimationsManager.loadShared(74585);
         mYellowTop = AnimationsManager.loadShared(74565);
         mYellowBottom = AnimationsManager.loadShared(74555);
         mYellowLeft = AnimationsManager.loadShared(74513);
         mTouchBgLeftTop = AnimationsManager.loadShared(74521);
         mTouchBgRightTop = AnimationsManager.loadShared(74531);
         mTouchBgLeftBottom = AnimationsManager.loadShared(74539);
         mTouchBgRightBottom = AnimationsManager.loadShared(74553);
         mOverlayLeftTop = AnimationsManager.loadShared(74537);
         mOverlayRightTop = AnimationsManager.loadShared(74575);
         mOverlayLeftBottom = AnimationsManager.loadShared(74525);
         mOverlayRightBottom = AnimationsManager.loadShared(74573);
         mOverlayUp = AnimationsManager.loadShared(74515);
         mOverlayDown = AnimationsManager.loadShared(74529);
         mTabLeftUp = AnimationsManager.loadShared(74557);
         mTabRightUp = AnimationsManager.loadShared(74517);
         mTabTop = AnimationsManager.loadShared(74565);
         mTabLeft = AnimationsManager.loadShared(74513);
         mMaskLeftTop = AnimationsManager.loadShared(74855);
         mMaskRightTop = AnimationsManager.loadShared(74853);
         mMaskLeftBottom = AnimationsManager.loadShared(74851);
         mMaskRightBottom = AnimationsManager.loadShared(74849);
         mOrangeLabelLeft = AnimationsManager.loadShared(74847);
         mOrangeLabelMiddle = AnimationsManager.loadShared(74843);
         mOrangeLabelRight = AnimationsManager.loadShared(74845);
         mRedNotificationLeft = AnimationsManager.loadShared(77633);
         mRedNotificationMiddle = AnimationsManager.loadShared(77653);
         mRedNotificationRight = AnimationsManager.loadShared(77475);
         smAssetsLoaded = true;
      }
   }

   public void drawRedNotificationRectangle(int x, int y) {
      mRedNotificationLeft.draw(x, y);
      mRedNotificationRight.draw(this.mWidth + x, y);
      tileRect(
         mRedNotificationLeft.getCollisionBox(0).getWidth() + x,
         y,
         this.mWidth
            - mRedNotificationLeft.getCollisionBox(0).getWidth()
            - mRedNotificationRight.getCollisionBox(0).getWidth()
            + mRedNotificationMiddle.getCollisionBox(0).getWidth(),
         mRedNotificationMiddle
      );
   }

   private static void tileRect(int x, int y, int width, SpriteObject sprite) {
      int first = sprite.getCollisionBox(0).getWidth();
      int end = width + x;
      if (x <= first + end) {
         do {
            int tile = sprite.getCollisionBox(0).getWidth();
            if (end < tile + x) {
               tile = end - x;
            }

            if (0 < tile) {
               OGL.setClip(x, 0, tile, Toolkit.getScreenHeight());
               sprite.draw(x, y);
            }

            x += sprite.getCollisionBox(0).getWidth();
         } while (x <= sprite.getCollisionBox(0).getWidth() + end);
      }
   }

   public void drawTabRectangle(int x, int y) {
      mTabLeftUp.draw(x, y);
      mTabRightUp.draw(this.mWidth + x - mTabRightUp.getCollisionBox(0).getWidth(), y);
      int cornerHeight = mTabLeftUp.getCollisionBox(0).getHeight();
      OGL.setColorRGB(5126181);
      OGL.fillRect(x, mTabLeftUp.getCollisionBox(0).getHeight() + y, mTabLeft.getCollisionBox(0).getWidth(), this.mHeight - cornerHeight);
      OGL.fillRect(
         this.mWidth + x - mTabLeft.getCollisionBox(0).getWidth(),
         mTabLeftUp.getCollisionBox(0).getHeight() + y,
         mTabLeft.getCollisionBox(0).getWidth(),
         this.mHeight - cornerHeight
      );
      int leftWidth = mTabLeftUp.getCollisionBox(0).getWidth();
      int rightWidth = mTabRightUp.getCollisionBox(0).getWidth();
      OGL.setColorRGB(5126181);
      OGL.fillRect(mTabLeftUp.getCollisionBox(0).getWidth() + x, y, this.mWidth - leftWidth - rightWidth, mTabTop.getCollisionBox(0).getHeight());
      OGL.setColorRGB(13085052);
      OGL.fillRect(
         mTabLeftUp.getCollisionBox(0).getWidth() + x,
         mTabTop.getCollisionBox(0).getHeight() + y,
         this.mWidth - mTabLeftUp.getCollisionBox(0).getWidth() - mTabRightUp.getCollisionBox(0).getWidth(),
         this.mHeight - mTabTop.getCollisionBox(0).getHeight()
      );
      OGL.fillRect(
         mTabLeft.getCollisionBox(0).getWidth() + x,
         mTabLeftUp.getCollisionBox(0).getHeight() + y,
         this.mWidth + mTabLeft.getCollisionBox(0).getWidth() * -2,
         this.mHeight - mTabLeftUp.getCollisionBox(0).getHeight()
      );
   }

   public void drawTouchBgRectangle(int x, int y) {
      mTouchBgLeftTop.draw(x, y);
      mTouchBgRightTop.draw(this.mWidth + x - mTouchBgRightTop.getCollisionBox(0).getWidth(), y);
      mTouchBgLeftBottom.draw(x, this.mHeight + y - mTouchBgLeftBottom.getCollisionBox(0).getHeight());
      mTouchBgRightBottom.draw(
         this.mWidth + x - mTouchBgRightBottom.getCollisionBox(0).getWidth(), this.mHeight + y - mTouchBgRightBottom.getCollisionBox(0).getHeight()
      );
      OGL.setColorRGB(5196598);
      OGL.fillRect(
         mTouchBgLeftTop.getCollisionBox(0).getWidth() + x,
         y,
         this.mWidth - mTouchBgLeftTop.getCollisionBox(0).getWidth() - mTouchBgRightTop.getCollisionBox(0).getWidth(),
         this.mHeight
      );
      OGL.fillRect(
         x,
         mTouchBgLeftTop.getCollisionBox(0).getHeight() + y,
         this.mWidth,
         this.mHeight - mTouchBgLeftTop.getCollisionBox(0).getHeight() - mTouchBgLeftBottom.getCollisionBox(0).getHeight()
      );
   }

   public void drawTouchMask(int x, int y) {
      mMaskLeftTop.draw(x, y);
      mMaskRightTop.draw(this.mWidth + x - mMaskRightTop.getCollisionBox(0).getWidth(), y);
      mMaskLeftBottom.draw(x, this.mHeight + y - mMaskLeftBottom.getCollisionBox(0).getHeight());
      mMaskRightBottom.draw(
         this.mWidth + x - mMaskRightBottom.getCollisionBox(0).getWidth(), y + this.mHeight - mMaskRightBottom.getCollisionBox(0).getHeight()
      );
   }

   public void drawOverlayRectangle(int x, int y) {
      mOverlayLeftTop.draw(x, y);
      mOverlayRightTop.draw(this.mWidth + x - mOverlayRightTop.getCollisionBox(0).getWidth(), y);
      mOverlayLeftBottom.draw(x, this.mHeight + y - mOverlayLeftBottom.getCollisionBox(0).getHeight());
      mOverlayRightBottom.draw(
         x + this.mWidth - mOverlayRightBottom.getCollisionBox(0).getWidth(), this.mHeight + y - mOverlayRightBottom.getCollisionBox(0).getHeight()
      );
      int span = this.mWidth - mOverlayLeftTop.getCollisionBox(0).getWidth() - mOverlayRightTop.getCollisionBox(0).getWidth();
      int position = mOverlayLeftTop.getCollisionBox(0).getWidth();
      if (position <= mOverlayUp.getCollisionBox(0).getWidth() + span) {
         int end = position + span;

         do {
            int tile = mOverlayUp.getCollisionBox(0).getWidth();
            if (end < tile + position) {
               tile = end - position;
            }

            OGL.setClip(position + x, 0, tile, Toolkit.getScreenHeight());
            mOverlayUp.draw(position + x, y);
            position += mOverlayUp.getCollisionBox(0).getWidth();
         } while (position <= mOverlayUp.getCollisionBox(0).getWidth() + span);
      }

      span = this.mWidth - mOverlayLeftBottom.getCollisionBox(0).getWidth() - mOverlayRightBottom.getCollisionBox(0).getWidth();
      position = mOverlayLeftBottom.getCollisionBox(0).getWidth();
      if (position <= mOverlayDown.getCollisionBox(0).getWidth() + span) {
         int end = span + position;

         do {
            int tile = mOverlayDown.getCollisionBox(0).getWidth();
            if (end < tile + position) {
               tile = end - position;
            }

            OGL.setClip(position + x, 0, tile, Toolkit.getScreenHeight());
            mOverlayDown.draw(position + x, y + this.mHeight - mOverlayDown.getCollisionBox(0).getHeight());
            position += mOverlayDown.getCollisionBox(0).getWidth();
         } while (position <= mOverlayDown.getCollisionBox(0).getWidth() + span);
      }
   }

   public void drawYellowRectangle(int x, int y) {
      if (this.mHasLeftPart) {
         mYellowLeftTop.draw(x, y);
      }

      if (this.mHasRightPart) {
         mYellowRightTop.draw(this.mWidth + x - mYellowRightTop.getCollisionBox(0).getWidth(), y);
      }

      if (this.mHasBottomPart) {
         if (this.mHasLeftPart) {
            mYellowLeftBottom.draw(x, this.mHeight + y - mYellowLeftBottom.getCollisionBox(0).getHeight());
         }

         if (this.mHasRightPart) {
            mYellowRightBottom.draw(
               this.mWidth + x - mYellowRightBottom.getCollisionBox(0).getWidth(), this.mHeight + y - mYellowRightBottom.getCollisionBox(0).getHeight()
            );
         }
      }

      int leftTop = 0;
      if (this.mHasLeftPart) {
         leftTop = mYellowLeftTop.getCollisionBox(0).getWidth();
      }

      int rightTop = 0;
      if (this.mHasRightPart) {
         rightTop = mYellowRightTop.getCollisionBox(0).getWidth();
      }

      OGL.setColorRGB(4206874);
      int topX = 0;
      if (this.mHasLeftPart) {
         topX = mYellowLeftTop.getCollisionBox(0).getWidth();
      }

      OGL.fillRect(topX + x, y, this.mWidth - leftTop - rightTop, mYellowTop.getCollisionBox(0).getHeight());
      if (this.mHasBottomPart) {
         int leftBottom = 0;
         if (this.mHasLeftPart) {
            leftBottom = mYellowLeftBottom.getCollisionBox(0).getWidth();
         }

         int rightBottom = 0;
         if (this.mHasRightPart) {
            rightBottom = mYellowRightBottom.getCollisionBox(0).getWidth();
         }

         int span = this.mWidth - leftBottom - rightBottom;
         int position = 0;
         if (this.mHasLeftPart) {
            position = mYellowLeftBottom.getCollisionBox(0).getWidth();
         }

         if (position <= mYellowBottom.getCollisionBox(0).getWidth() + span) {
            int end = span + position;

            do {
               int tile = mYellowBottom.getCollisionBox(0).getWidth();
               if (end < tile + position) {
                  tile = end - position;
               }

               if (0 < tile) {
                  OGL.setClip(position + x, 0, tile, Toolkit.getScreenHeight());
                  mYellowBottom.draw(position + x, this.mHeight + y - mYellowBottom.getHeight());
               }

               position += mYellowBottom.getCollisionBox(0).getWidth();
            } while (position <= mYellowBottom.getCollisionBox(0).getWidth() + span);
         }
      }

      int leftWidth = 0;
      if (this.mHasLeftPart) {
         int bottomHeight = 0;
         leftWidth = mYellowLeft.getCollisionBox(0).getWidth();
         OGL.setClip(0, 0, Toolkit.getScreenWidth(), Toolkit.getScreenHeight());
         OGL.setColorRGB(4206874);
         int sideY = mYellowLeftTop.getHeight();
         int topHeight = mYellowLeftTop.getHeight();
         if (this.mHasBottomPart) {
            bottomHeight = mYellowLeftBottom.getHeight();
         }

         OGL.fillRect(x, sideY + y, leftWidth, this.mHeight - topHeight - bottomHeight);
      }

      int rightWidth;
      if (!this.mHasRightPart) {
         rightWidth = 0;
      } else {
         int bottomHeight = 0;
         rightWidth = mYellowLeft.getCollisionBox(0).getWidth();
         OGL.setClip(0, 0, Toolkit.getScreenWidth(), Toolkit.getScreenHeight());
         OGL.setColorRGB(4206874);
         int sideY = mYellowLeftTop.getHeight();
         int topHeight = mYellowLeftTop.getHeight();
         if (this.mHasBottomPart) {
            bottomHeight = mYellowLeftBottom.getHeight();
         }

         OGL.fillRect(x - rightWidth + this.mWidth, sideY + y, rightWidth, this.mHeight - topHeight - bottomHeight);
      }

      OGL.setClip(x, y, this.mWidth, this.mHeight);
      OGL.setColorARGB(-665199);
      int stripX = 0;
      if (this.mHasLeftPart) {
         stripX = mYellowLeftTop.getCollisionBox(0).getWidth();
      }

      int stripY = mYellowTop.getCollisionBox(0).getHeight();
      int stripLeft = 0;
      if (this.mHasLeftPart) {
         stripLeft = mYellowLeftTop.getCollisionBox(0).getWidth();
      }

      int stripRight = 0;
      if (this.mHasRightPart) {
         stripRight = mYellowRightTop.getCollisionBox(0).getWidth();
      }

      OGL.fillRect(
         stripX + x,
         stripY + y,
         this.mWidth - stripLeft - stripRight,
         mYellowLeftTop.getCollisionBox(0).getHeight() - mYellowTop.getCollisionBox(0).getHeight()
      );
      boolean gradient = this.mHasGradient;
      OGL.setClip(x, y, this.mWidth, this.mHeight);
      OGL.setColorARGB(-665199);
      int innerWidth = this.mWidth - (rightWidth + leftWidth);
      if (gradient) {
         OGL.setColor2ARGB(-4957);
         int firstY = mYellowLeftTop.getCollisionBox(0).getHeight();
         int firstTop = mYellowLeftTop.getCollisionBox(0).getHeight();
         int firstBottom = 0;
         if (this.mHasBottomPart) {
            firstBottom = mYellowBottom.getCollisionBox(0).getHeight();
         }

         OGL.fillRectGradient(leftWidth + x, firstY + y, innerWidth, (this.mHeight - firstTop - firstBottom) / 2, false);
         OGL.setColorARGB(-4957);
         OGL.setColor2ARGB(-665199);
         int secondY = mYellowTop.getCollisionBox(0).getHeight();
         int secondTop = mYellowTop.getCollisionBox(0).getHeight();
         int secondBottom = 0;
         if (this.mHasBottomPart) {
            secondBottom = mYellowBottom.getCollisionBox(0).getHeight();
         }

         int heightTop = mYellowTop.getCollisionBox(0).getHeight();
         int heightBottom = 0;
         if (this.mHasBottomPart) {
            heightBottom = mYellowBottom.getCollisionBox(0).getHeight();
         }

         OGL.fillRectGradient(
            leftWidth + x, secondY + y + (this.mHeight - secondTop - secondBottom) / 2, innerWidth, (this.mHeight - heightTop - heightBottom) / 2 + 1, false
         );
      } else {
         int firstY = mYellowLeftTop.getCollisionBox(0).getHeight();
         int firstTop = mYellowLeftTop.getCollisionBox(0).getHeight();
         int firstBottom = 0;
         if (this.mHasBottomPart) {
            firstBottom = mYellowBottom.getCollisionBox(0).getHeight();
         }

         OGL.fillRect(x + leftWidth, y + firstY, innerWidth, (this.mHeight - firstTop - firstBottom) / 2);
         int secondY = mYellowTop.getCollisionBox(0).getHeight();
         int secondTop = mYellowTop.getCollisionBox(0).getHeight();
         int secondBottom = 0;
         if (this.mHasBottomPart) {
            secondBottom = mYellowBottom.getCollisionBox(0).getHeight();
         }

         int heightTop = mYellowTop.getCollisionBox(0).getHeight();
         int heightBottom = 0;
         if (this.mHasBottomPart) {
            heightBottom = mYellowBottom.getCollisionBox(0).getHeight();
         }

         OGL.fillRect(x + leftWidth, y + secondY + (this.mHeight - secondTop - secondBottom) / 2, innerWidth, (this.mHeight - heightTop - heightBottom) / 2 + 1);
      }
   }

   public void drawOrangeRectangle(int x, int y) {
      int start;
      int width;
      if (!this.mIsMirrored) {
         mOrangeLabelLeft.draw(x, y);
         mOrangeLabelRight.draw(this.mWidth + x - mOrangeLabelRight.getCollisionBox(0).getWidth(), y);
         start = mOrangeLabelLeft.getCollisionBox(0).getWidth();
         width = this.mWidth - mOrangeLabelLeft.getCollisionBox(0).getWidth() - mOrangeLabelRight.getCollisionBox(0).getWidth();
      } else {
         mOrangeLabelLeft.draw(this.mWidth + x, y, 1);
         mOrangeLabelRight.draw(mOrangeLabelRight.getCollisionBox(0).getWidth() + x, y, 1);
         start = mOrangeLabelRight.getCollisionBox(0).getWidth();
         width = this.mWidth - mOrangeLabelLeft.getCollisionBox(0).getWidth() - mOrangeLabelRight.getCollisionBox(0).getWidth();
      }

      tileRect(start + x, y, width, mOrangeLabelMiddle);
      this.setPosition(x, y);
      this.drawDebug();
   }
}
