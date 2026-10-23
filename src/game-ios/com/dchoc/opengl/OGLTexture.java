package com.dchoc.opengl;

public class OGLTexture {
   public static final int COLOR_DEPTH_4444 = 2;
   public static final int COLOR_DEPTH_5551 = 1;
   public static final int COLOR_DEPTH_565 = 3;
   public static final int COLOR_DEPTH_8888 = 0;
   private int mColorDepth;
   private String mFilename;
   private short mHeight;
   private int mID;
   private short mWidth;

   public OGLTexture(int id, String filename, short width, short height, int colorDepth) {
      this.mID = id;
      this.mFilename = filename;
      this.mWidth = width;
      this.mHeight = height;
      this.mColorDepth = colorDepth;
   }

   public int getColorDepth() {
      return this.mColorDepth;
   }

   public String getFilename() {
      return this.mFilename;
   }

   public int getHeight() {
      return this.mHeight;
   }

   public int getID() {
      return this.mID;
   }

   public int getWidth() {
      return this.mWidth;
   }
}
