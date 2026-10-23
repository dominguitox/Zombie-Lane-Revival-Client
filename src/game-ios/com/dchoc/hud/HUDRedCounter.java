package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.toolkit.Toolkit;

public class HUDRedCounter extends HUDObject {
   private ImageFont mFont = DCiDead.getFont(0);
   private RoundRectangle mRectangle;
   private String mText;
   private int mValue;

   public HUDRedCounter() {
      super((byte)28);
      RoundRectangle rectangle = new RoundRectangle(6);
      this.mRectangle = rectangle;
      rectangle.setClipping(false);
      this.mRectangle.setParent(this);
      this.mValue = 0;
      this.mText = "";
   }

   public void incrementValue() {
      this.setValue(this.mValue + 1);
   }

   public void setValue(int value) {
      if (this.mValue != value) {
         this.mValue = value;
         this.mText = String.valueOf(value);
         this.calculate();
      }
   }

   public void setText(int textId) {
      this.mValue = textId;
      this.mText = Toolkit.getText(textId);
      this.calculate();
   }

   @Override
   public void callback(int id) {
   }

   @Override
   public void setPosition(int x, int y) {
      int width = this.getWidth();
      int height = this.getHeight();
      super.setPosition(x - width / 2, y - height);
   }

   @Override
   public void draw() {
      if (this.isVisible() && this.mValue > 0) {
         this.mRectangle.draw();
         ImageFont font = this.mFont;
         String text = this.mText;
         int x = this.getX();
         int width = this.getWidth();
         int y = this.getY();
         int height = this.getHeight();
         int heightAgain = this.getHeight();
         int fontHeight = this.mFont.getHeight();
         font.drawString(text, x + width / 2, y + height - (heightAgain - fontHeight) / 2, 65);
         this.drawDebug();
      }
   }

   @Override
   public void logicUpdate(int delta) {
   }

   @Override
   public void pointerEvent(TouchEvent event) {
   }

   public void calculate() {
      int textWidth = this.mFont.stringWidth(this.mText);
      int minWidth = this.mRectangle.getMinWidth();
      this.mWidth = Math.max(textWidth * 3 / 2, minWidth);
      this.mHeight = this.mRectangle.getResourceHeight();
      RoundRectangle rectangle = this.mRectangle;
      int width = this.getWidth();
      int height = this.getHeight();
      rectangle.setSize(width, height);
   }
}
