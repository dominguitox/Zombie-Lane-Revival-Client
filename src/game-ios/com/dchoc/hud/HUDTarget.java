package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.spriteobject.CollisionBox;

public class HUDTarget extends HUDObject {
   public static final String SEPARATOR = "/";
   private ImageFont mFont;
   private String mValue;
   private int mCount;
   private int mTarget;
   private HUDImage mSeparator;
   private HUDInteger mIntegerCount;
   private HUDInteger mIntegerTarget;

   public HUDTarget(int fontIndex) {
      super((byte)5);
      this.mFont = DCiDead.getFont(fontIndex);
      this.mIntegerCount = null;
      this.mIntegerTarget = null;
      this.mCount = 0;
      this.mTarget = 0;
      this.mValue = null;
   }

   public HUDTarget(int digitsResource, int separatorResource) {
      super((byte)5);
      this.mSeparator = new HUDImage(separatorResource);
      this.mIntegerCount = new HUDInteger(digitsResource);
      this.mIntegerTarget = new HUDInteger(digitsResource);
      this.mIntegerCount.setCenterType((byte)0);
      this.mIntegerTarget.setCenterType((byte)0);
      this.mFont = null;
      this.mValue = null;
      this.mCount = 0;
      this.mTarget = 0;
   }

   public void setValues(int target, int count) {
      if (this.mTarget != target || this.mCount != count || this.mFont != null && this.mValue == null) {
         this.mCount = count;
         this.mTarget = target;
         if (this.mFont == null) {
            this.mIntegerCount.setValue(count);
            this.mIntegerTarget.setValue(target);
            CollisionBox box = this.mSeparator.getCollisionBox(0);
            int separatorWidth = box.getWidth();
            int countWidth = this.mIntegerCount.getWidth();
            int targetWidth = this.mIntegerTarget.getWidth();
            this.setWidth(countWidth + separatorWidth + targetWidth);
            this.setHeight(this.mIntegerCount.getHeight());
         } else {
            String targetText = Integer.toString(target);
            String countText = Integer.toString(this.mCount);
            this.mValue = targetText + "/" + countText;
            this.setWidth(this.mFont.stringWidth(this.mValue));
            this.setHeight(this.mFont.getHeight());
         }
      }
   }

   @Override
   public void callback(int id) {
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         int x = this.getX();
         int y = this.getY();
         if (this.mCenterType == 3) {
            y -= this.mHeight >> 1;
            x -= this.mWidth >> 1;
         }

         ImageFont font = this.mFont;
         if (font == null) {
            HUDImage separator = this.mSeparator;
            if (separator != null) {
               CollisionBox box = separator.getCollisionBox(0);
               int separatorWidth = box.getWidth();
               int separatorHeight = box.getHeight();
               this.mIntegerTarget.setPosition(x, y);
               HUDInteger count = this.mIntegerCount;
               int targetWidth = this.mIntegerTarget.getWidth();
               count.setPosition(separatorWidth + x + targetWidth, y);
               separator = this.mSeparator;
               targetWidth = this.mIntegerTarget.getWidth();
               separator.setPosition(targetWidth + x + separatorWidth / 2, y + separatorHeight / 2);
               this.mIntegerTarget.draw();
               this.mSeparator.draw();
               this.mIntegerCount.draw();
               this.mIntegerTarget.drawDebug();
               this.mIntegerCount.drawDebug();
               this.mSeparator.drawDebug();
            }
         } else if (this.mValue != null) {
            font.drawString(this.mValue, x, y, 20);
         }

         this.drawDebug(16711680);
      }
   }

   @Override
   public void logicUpdate(int delta) {
   }

   @Override
   public void pointerEvent(TouchEvent event) {
   }
}
