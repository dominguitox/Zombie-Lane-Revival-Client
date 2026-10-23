package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.Animation;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;

public class HUDInteger extends HUDObject {
   private boolean mIsAnimated = false;
   private int mValue;
   private byte mDigitsChanged;
   private SpriteObject mDigits;
   private SpriteObject mEffect;
   private int mEffectTimer;
   private float mAlpha;
   private boolean mIsTwoDigits;

   public HUDInteger(int digits) {
      super((byte)16);
      this.init(digits, -1);
   }

   public HUDInteger(int digits, int effect) {
      super((byte)16);
      this.init(digits, effect);
   }

   public HUDInteger(int digits, int x, int y) {
      super((byte)16);
      super.setPosition(x, y);
      this.init(digits, -1);
   }

   public void setValue(int value) {
      int current = this.mValue;
      if (current != value || current == Integer.MAX_VALUE) {
         int count;
         if (current == Integer.MAX_VALUE) {
            int digits = Math.abs(value);
            count = 1;
            if (digits != 0) {
               count = 0;
               boolean more;
               if (0 < digits) {
                  do {
                     count++;
                     more = 9 < digits;
                     digits /= 10;
                  } while (more);
               }
            }
         } else {
            int digits = Math.abs(value - current);
            count = 1;
            if (digits != 0) {
               count = 0;
               boolean more;
               if (0 < digits) {
                  do {
                     count++;
                     more = 9 < digits;
                     digits /= 10;
                  } while (more);
               }
            }
         }

         this.mDigitsChanged = (byte)Math.abs(count);
         this.mValue = value;
         this.mEffectTimer = 0;
         this.setWidth(this.calculateWidth(value));
      }
   }

   public void setTwoDigits(boolean twoDigits) {
      this.mIsTwoDigits = twoDigits;
   }

   public boolean isTwoDigits() {
      return this.mIsTwoDigits;
   }

   public void setAnimated(boolean animated) {
      this.mIsAnimated = animated;
   }

   public boolean isAnimated() {
      return this.mIsAnimated;
   }

   public int getValue() {
      return this.mValue;
   }

   public int getValueHeight() {
      return this.mDigits.getHeight();
   }

   public void setAlpha(float alpha) {
      this.mAlpha = alpha;
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.mEffect != null) {
         int timer = this.mEffectTimer;
         this.mEffectTimer = timer + delta;
         if (this.mEffect.getAnimationLength() < timer + delta) {
            this.mDigitsChanged = 0;
         }
      }
   }

   @Override
   public void draw() {
      int value = Math.abs(this.mValue);
      int scaleY = 0;
      int color = 0;
      int scaleX = 0;
      if (this.mEffect != null) {
         Animation animation = this.mEffect.getCurrentAnimationData();
         color = animation.getTimelineValue(0, this.mEffectTimer, true);
         scaleX = animation.getTimelineValue(3, this.mEffectTimer, true);
         scaleY = animation.getTimelineValue(4, this.mEffectTimer, true);
      }

      int x = this.getX();
      int y = this.getY();
      if (this.mCenterType == 0) {
         y += this.getHeight() >> 1;
      } else if (this.mCenterType == 3) {
         x -= this.getWidth() >> 1;
      }

      int width = this.getWidth();
      int index = 0;
      int digits = value;

      do {
         this.mDigits.setAnimationFrame(digits % 10);
         CollisionBox box = this.mDigits.getCollisionBox(0);
         width -= box.getWidth();
         boolean pushed;
         if (this.mAlpha == 1.0F) {
            pushed = false;
            if (this.mIsAnimated && this.mEffect != null && index < this.mDigitsChanged) {
               OGL.pushParameters();
               OGL.setScaleInt(scaleX, scaleY);
               OGL.setColorModification(color);
               pushed = true;
            }
         } else {
            OGL.pushParameters();
            OGL.setAlpha(this.mAlpha);
            pushed = true;
         }

         this.mDigits.draw(width + x - box.getX(), y);
         if (this.mIsTwoDigits && value < 10) {
            this.mDigits.setAnimationFrame(0);
            box = this.mDigits.getCollisionBox(0);
            this.mDigits.draw(width + x - box.getX() - box.getWidth(), y);
         }

         if (pushed) {
            OGL.popParameters();
         }

         index++;
         digits /= 10;
      } while (digits != 0);
   }

   @Override
   public void pointerEvent(TouchEvent event) {
   }

   @Override
   public void callback(int id) {
   }

   public int getDigitWidth(int digit) {
      if (digit != 0 && digit < 10) {
         this.mDigits.setAnimationFrame(digit);
         return this.mDigits.getCollisionBox(0).getWidth();
      } else {
         return 0;
      }
   }

   public int calculateWidth(int value) {
      int width = 0;
      if (this.mDigits != null) {
         int absolute = Math.abs(value);
         width = 0;
         int digits = absolute;

         do {
            this.mDigits.setAnimationFrame(digits % 10);
            width += this.mDigits.getCollisionBox(0).getWidth();
            digits /= 10;
         } while (digits != 0);

         if (this.mIsTwoDigits && absolute < 10) {
            this.mDigits.setAnimationFrame(0);
            width += this.mDigits.getCollisionBox(0).getWidth();
         }
      }

      return width;
   }

   public void init(int digits, int effect) {
      this.mDigits = AnimationsManager.loadShared(digits);
      this.mEffect = AnimationsManager.loadShared(effect);
      this.setHeight(this.mDigits.getFrameHeight());
      this.mValue = Integer.MAX_VALUE;
      this.mDigitsChanged = 0;
      this.mEffectTimer = 0;
      this.mAlpha = 1.0F;
      this.setValue(0);
      this.setCenterType((byte)3);
   }
}
