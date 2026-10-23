package com.dchoc.idead.isometric;

import com.dchoc.imagefont.ImageFont;
import com.dchoc.math.Vec2;
import com.dchoc.opengl.OGL;

public class Floater {
   public static final float FLOATING_SPEED = 35.0F;
   public static final int FLOATING_TIME = 3000;
   private int mElapsedTime;
   private ImageFont mFont;
   private Vec2 mPosition = new Vec2();
   private String mText;

   public void init(String text, ImageFont font, float x, float y) {
      this.mText = text;
      this.mFont = font;
      this.mPosition.set(x, y);
      this.mElapsedTime = 0;
   }

   public int logicUpdate(int delta) {
      this.mElapsedTime += delta;
      return 0;
   }

   public void doDraw(Camera camera) {
      if (this.isActive()) {
         float scale = IsometricUtils.getScene().getScale();
         float x = this.mPosition.getX();
         float cameraX = camera.getX();
         float y = this.mPosition.getY();
         float cameraY = camera.getY();
         int elapsed = this.mElapsedTime;
         float drawX = scale * (x - cameraX);
         float drawY = scale * (y - cameraY + elapsed / -1000.0F * 35.0F);
         if (elapsed > 1500) {
            OGL.pushParameters();
            OGL.setAlpha((elapsed - 1500) / -1500.0F + 1.0F);
         }

         this.mFont.drawMultiline(this.mText, (int)drawX, (int)drawY, 17);
         if (this.mElapsedTime > 1500) {
            OGL.popParameters();
         }
      }
   }

   public boolean isActive() {
      return this.mElapsedTime <= 3000;
   }

   public int getElapsedTime() {
      return this.mElapsedTime;
   }
}
