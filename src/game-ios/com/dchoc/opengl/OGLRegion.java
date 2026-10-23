package com.dchoc.opengl;

public class OGLRegion {
   private short mRegionHeight;
   private short mRegionWidth;
   private short mRegionX;
   private short mRegionY;
   private OGLTexture mTexture;

   public OGLRegion(OGLTexture texture, int x, int y, int width, int height) {
      this.mTexture = texture;
      this.mRegionX = (short)x;
      this.mRegionY = (short)y;
      this.mRegionWidth = (short)width;
      this.mRegionHeight = (short)height;
   }

   public int getRegionHeight() {
      return this.mRegionHeight;
   }

   public int getRegionWidth() {
      return this.mRegionWidth;
   }

   public int getRegionX() {
      return this.mRegionX;
   }

   public int getRegionY() {
      return this.mRegionY;
   }

   public OGLTexture getTexture() {
      return this.mTexture;
   }

   public void setRegion(int x, int y, int width, int height, boolean unused) {
      this.mRegionX = (short)x;
      this.mRegionY = (short)y;
      this.mRegionWidth = (short)width;
      this.mRegionHeight = (short)height;
   }
}
