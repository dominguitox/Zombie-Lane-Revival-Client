package com.dchoc.hud;

import com.dchoc.opengl.OGL;

public class HUDAutoTextFieldScale extends HUDAutoTextField {
   public static final float INITIAL_SCALE = 1.0F;
   private boolean mCenterScale;
   private float mScaleFactor = 1.0F;

   public HUDAutoTextFieldScale() {
      super(0, 0, 0, 0);
   }

   public void setScaleFactor(float factor) {
      float value;
      if (factor < 0.1F) {
         value = 0.0F;
      } else {
         value = factor;
         if (1.0F < factor) {
            value = 1.0F;
         }
      }

      this.mScaleFactor = value;
   }

   public void enableCenterScale(boolean enable) {
      this.mCenterScale = enable;
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         if (!this.mCenterScale || this.mScaleFactor != 0.0F) {
            if (this.mFont != null) {
               int x = this.getX();
               int offsetX = this.mOffsetX;
               int y = this.getY();
               int offsetY = this.mOffsetY;
               int lineHeight = this.mFont.getHeight();
               int baseY = offsetY + y;
               if (this.mCenterScale) {
                  OGL.pushParameters();
                  OGL.setScale(this.mScaleFactor, this.mScaleFactor);
                  int height = this.mHeight;
                  int scaledHeight = (int)(height * this.mScaleFactor);
                  lineHeight = (int)(lineHeight * this.mScaleFactor);
                  baseY += height - scaledHeight >> 1;
               }

               if (this.mSemiTransparent) {
                  OGL.pushParameters();
                  OGL.getParameters().setAlpha(0.2F);
               }

               if (this.mText != null && this.mFont != null) {
                  int baseX = offsetX + x;
                  int lineY = baseY;

                  for (int i = 0; i < this.mSplittedText.length; i++) {
                     int anchor = 20;
                     int lineX = baseX;
                     if (this.mCenterHorizontally) {
                        lineX = baseX + (this.mWidth >> 1);
                        anchor = 17;
                     }

                     int top = i * lineHeight + baseY;
                     int drawY;
                     if (!this.mCenterVertically) {
                        drawY = lineY;
                        if (this.mAlignBottom) {
                           drawY = top + this.mHeight - this.mTextHeight;
                        }
                     } else {
                        drawY = top + (this.mHeight - this.mTextHeight >> 1);
                     }

                     if (!this.mCenterScale) {
                        this.mFont.drawString(this.mSplittedText[i], lineX, drawY, anchor);
                     } else {
                        this.mFont.drawString(this.mSplittedText[i], lineX, drawY, anchor, true);
                     }

                     lineY += lineHeight;
                  }
               }

               if (this.mSemiTransparent) {
                  OGL.popParameters();
               }

               if (this.mCenterScale) {
                  OGL.popParameters();
               }
            }
         }
      }
   }
}
