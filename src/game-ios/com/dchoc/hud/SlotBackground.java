package com.dchoc.hud;

import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;

public class SlotBackground extends HUDObject {
   private boolean mDarkColors;

   public SlotBackground(CollisionBox box, HUDObject parent) {
      super((byte)8);
      super.setSizes(box);
      this.setParent(parent);
      this.mDarkColors = false;
   }

   public SlotBackground(int x, int y, int width, int height) {
      super((byte)8);
      super.setSizes(x, y, width, height);
      this.mDarkColors = false;
   }

   public SlotBackground(CollisionBox box) {
      super((byte)8);
      int x = box.getX();
      int y = box.getY();
      int width = box.getWidth();
      int height = box.getHeight();
      super.setSizes(x, y, width, height);
      this.mDarkColors = false;
   }

   public static int getBorderWidth() {
      return 2;
   }

   public void setDarkColors(boolean dark) {
      this.mDarkColors = dark;
   }

   public boolean isDarkColors() {
      return this.mDarkColors;
   }

   @Override
   public void callback(int id) {
   }

   public void drawAlpha() {
      OGL.setColorARGB(859782187);
      OGL.fillRect(this.getX() - 2, this.getY() + 2, this.mWidth, this.mHeight);
      OGL.setColorARGB(870555498);
      OGL.fillRect(this.getX(), this.getY(), this.mWidth, this.mHeight);
      OGL.setColorARGB(872412373);
      OGL.setColor2ARGB(872413932);
      OGL.fillRectGradient(this.getX() + 2, this.getY() + 2, this.mWidth - 4, (this.mHeight - 4) / 2, false);
      OGL.setColorARGB(872413932);
      OGL.setColor2ARGB(872412373);
      OGL.fillRectGradient(this.getX() + 2, this.getY() + this.mHeight / 2, this.mWidth - 4, (this.mHeight - 4) / 2, false);
   }

   @Override
   public void draw() {
      boolean dark = this.mDarkColors;
      OGL.setColorARGB(-2143339477);
      OGL.fillRect(this.getX() - 2, this.getY() + 2, this.mWidth, this.mHeight);
      OGL.setColorARGB(-1859734);
      OGL.fillRect(this.getX(), this.getY(), this.mWidth, this.mHeight);
      int first = dark ? -534636 : -2859;
      int second = dark ? -2859 : -1300;
      OGL.setColorARGB(first);
      OGL.setColor2ARGB(second);
      OGL.fillRectGradient(this.getX() + 2, this.getY() + 2, this.mWidth - 4, (this.mHeight - 4) / 2, false);
      OGL.setColorARGB(second);
      OGL.setColor2ARGB(first);
      OGL.fillRectGradient(this.getX() + 2, this.getY() + this.mHeight / 2, this.mWidth - 4, (this.mHeight - 4) / 2, false);
   }

   @Override
   public void logicUpdate(int delta) {
   }

   @Override
   public void pointerEvent(TouchEvent event) {
   }
}
