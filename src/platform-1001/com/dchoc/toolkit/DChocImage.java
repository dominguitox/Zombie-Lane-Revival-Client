package com.dchoc.toolkit;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Texture2D;

public class DChocImage {
   public static final byte COLOR_DEPTH_4444 = 2;
   public static final byte COLOR_DEPTH_5551 = 1;
   public static final byte COLOR_DEPTH_565 = 3;
   public static final byte COLOR_DEPTH_8888 = 0;
   public static final byte COLOR_DEPTH_DEFAULT = -1;
   private static Graphics smGetRGBGraphics;
   private static Image smGetRGBImage;
   private Image2D image2d;
   private int[][] mBuffer;
   private Image mDelegate;
   private int[] mEffectParam;
   private Graphics mGraphics;
   private int mRegionHeight;
   private int mRegionWidth;
   private int mRegionX;
   private int mRegionY;
   private Texture2D texture2d;

   public DChocImage(int var1) {
      this(Toolkit.getImage(var1));
   }

   public DChocImage(int var1, byte var2) {
      this(Toolkit.getImage(var1));
   }

   public DChocImage(int var1, int var2) {
      this.mDelegate = Image.createImage(var1, var2);
      this.mGraphics = this.mDelegate.getGraphics();
   }

   public DChocImage(DChocImage var1) {
      this.mDelegate = var1.getImage();
      if (var1.isMutable()) {
         this.mGraphics = var1.getGraphics();
      }

      this.setRegion(var1.getRegionX(), var1.getRegionY(), var1.getRegionWidth(), var1.getRegionHeight());
   }

   public DChocImage(Image var1) {
      this.mDelegate = var1;
      if (var1.isMutable()) {
         this.mGraphics = var1.getGraphics();
      }
   }

   public void clearRegion() {
      this.setRegion(0, 0, 0, 0);
   }

   public void copyEffectParam(int[] var1) {
      if (var1 != null) {
         int var3 = var1.length;
         int var2 = var3;
         if (this.mEffectParam == null) {
            this.mEffectParam = new int[var3];
            var2 = var3;
         }

         while (--var2 >= 0) {
            this.mEffectParam[var2] = var1[var2];
         }
      } else {
         this.mEffectParam = null;
      }
   }

   public int[][] getBuffer() {
      return this.mBuffer;
   }

   public int[] getEffectParams() {
      return this.mEffectParam;
   }

   public Graphics getGraphics() {
      return this.mGraphics;
   }

   public int getHeight() {
      int var1;
      if (this.mRegionHeight != 0) {
         var1 = this.mRegionHeight;
      } else {
         var1 = this.mDelegate.getHeight();
      }

      return var1;
   }

   public Image getImage() {
      return this.mDelegate;
   }

   public Image2D getImage2D() {
      return this.image2d;
   }

   public void getRGB(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (this.isRegioned()) {
         var4 += this.mRegionX;
         var5 += this.mRegionY;
      }

      this.mDelegate.getRGB(var1, var2, var3, var4, var5, var6, var7);
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

   public Texture2D getTexture2D() {
      return this.texture2d;
   }

   public int getWidth() {
      int var1;
      if (this.mRegionWidth != 0) {
         var1 = this.mRegionWidth;
      } else {
         var1 = this.mDelegate.getWidth();
      }

      return var1;
   }

   public boolean isLoadingCompleted() {
      return true;
   }

   public boolean isMutable() {
      return this.mDelegate.isMutable();
   }

   public boolean isRegioned() {
      boolean var1;
      if (this.mRegionWidth != 0 && this.mRegionHeight != 0) {
         var1 = true;
      } else {
         var1 = false;
      }

      return var1;
   }

   public void setBuffer(int[][] var1) {
      this.mBuffer = var1;
   }

   public void setRegion(int var1, int var2, int var3, int var4) {
      this.mRegionX = var1;
      this.mRegionY = var2;
      this.mRegionWidth = var3;
      this.mRegionHeight = var4;
      if (this.isMutable()) {
         if (this.isRegioned()) {
            this.mGraphics.setClip(var1, var2, var3, var4);
         } else {
            this.mGraphics.setClip(0, 0, this.mDelegate.getWidth(), this.mDelegate.getHeight());
         }
      }
   }
}
