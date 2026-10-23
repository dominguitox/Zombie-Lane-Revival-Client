package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.ToolkitHelpers;

public class HUDTime extends HUDObject {
   public static String SEPARATOR = ":";
   private ImageFont mFont;
   private HUDInteger mDigits;
   private SpriteObject mSeparator;
   private int mHours;
   private int mMinutes;
   private int mSeconds;
   private int mTimeLeft;

   public HUDTime(int x, int y, int font) {
      super((byte)5);
      this.mFont = DCiDead.getFont(font);
      this.mDigits = null;
      this.mSeparator = null;
      this.setSizes(x, y, 0, this.mFont.getHeight());
      this.init();
   }

   public HUDTime(int x, int y, int digits, int separator) {
      super((byte)5);
      this.mFont = null;
      this.mDigits = new HUDInteger(digits);
      this.mSeparator = AnimationsManager.loadShared(separator);
      this.mDigits.setCenterType((byte)0);
      this.mDigits.setTwoDigits(true);
      this.setSizes(x, y, 0, this.mDigits.getHeight());
      this.init();
   }

   public void setTime(long time) {
      int seconds = Math.abs((int)time / 1000);
      int hours = seconds / 3600;
      int minutes = seconds / 60 % 60;
      int rest = seconds % 60;
      this.mHours = hours;
      this.mMinutes = minutes;
      this.mSeconds = rest;
      this.mTimeLeft = seconds;
      int separatorWidth;
      if (this.mFont == null) {
         if (this.mDigits == null) {
            return;
         }

         this.mWidth = this.mDigits.calculateWidth(hours);
         this.mWidth = this.mDigits.calculateWidth(minutes) + this.mWidth;
         this.mWidth = this.mDigits.calculateWidth(rest) + this.mWidth;
         if (this.mSeparator == null) {
            return;
         }

         separatorWidth = this.mSeparator.getWidth();
      } else {
         String hoursText = ToolkitHelpers.getNumber(hours);
         String minutesText = ToolkitHelpers.getNumber(minutes);
         String secondsText = ToolkitHelpers.getNumber(rest);
         this.mWidth = this.mFont.stringWidth(hoursText);
         this.mWidth = this.mFont.stringWidth(minutesText) + this.mWidth;
         this.mWidth = this.mFont.stringWidth(secondsText) + this.mWidth;
         separatorWidth = this.mFont.stringWidth(SEPARATOR);
      }

      this.mWidth += separatorWidth * 2;
   }

   @Override
   public void callback(int id) {
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         int x = this.getFixedX();
         int y = this.getFixedY();
         if (this.mFont == null) {
            if (this.mDigits != null) {
               this.mDigits.setValue(this.mHours);
               this.mDigits.setPosition(x, y);
               this.mDigits.draw();
               int position = this.mDigits.getWidth() + x;
               if (this.mSeparator != null) {
                  int separatorWidth = this.mSeparator.getWidth();
                  int digitsHeight = this.mDigits.getHeight();
                  this.mSeparator.draw(position + (separatorWidth >> 1), y + (digitsHeight >> 1));
                  position += this.mSeparator.getWidth();
               }

               this.mDigits.setValue(this.mMinutes);
               this.mDigits.setPosition(position, y);
               this.mDigits.draw();
               position = this.mDigits.getWidth() + position;
               if (this.mSeparator != null) {
                  int separatorWidth = this.mSeparator.getWidth();
                  int digitsHeight = this.mDigits.getHeight();
                  this.mSeparator.draw(position + (separatorWidth >> 1), y + (digitsHeight >> 1));
                  position += this.mSeparator.getWidth();
               }

               this.mDigits.setValue(this.mSeconds);
               this.mDigits.setPosition(position, y);
               this.mDigits.draw();
               this.mDigits.getWidth();
            }
         } else {
            String hoursText = ToolkitHelpers.getNumber(this.mHours);
            String minutesText = ToolkitHelpers.getNumber(this.mMinutes);
            String secondsText = ToolkitHelpers.getNumber(this.mSeconds);
            this.mFont.drawString(hoursText, x, y, 20);
            int hoursWidth = this.mFont.stringWidth(hoursText);
            this.mFont.drawString(SEPARATOR, hoursWidth + x, y, 20);
            int position = hoursWidth + x + this.mFont.stringWidth(SEPARATOR);
            this.mFont.drawString(minutesText, position, y, 20);
            position += this.mFont.stringWidth(minutesText);
            this.mFont.drawString(SEPARATOR, position, y, 20);
            int separatorWidth = this.mFont.stringWidth(SEPARATOR);
            this.mFont.drawString(secondsText, separatorWidth + position, y, 20);
            this.mFont.stringWidth(secondsText);
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

   public int getTimeLeft() {
      return this.mTimeLeft;
   }

   public void init() {
      this.setCenterType((byte)3);
      this.mHours = 0;
      this.mMinutes = 0;
      this.mSeconds = 0;
   }
}
