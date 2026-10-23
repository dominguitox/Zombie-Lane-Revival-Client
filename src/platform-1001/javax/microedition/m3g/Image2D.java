package javax.microedition.m3g;

public class Image2D extends Object3D {
   public static final int ALPHA = 96;
   public static final int LUMINANCE = 97;
   public static final int LUMINANCE_ALPHA = 98;
   public static final int RGB = 99;
   public static final int RGBA = 100;

   Image2D(int var1) {
   }

   public Image2D(int var1, int var2, int var3) {
   }

   private Image2D(int var1, int var2, int var3, boolean var4) {
   }

   public Image2D(int var1, int var2, int var3, byte[] var4) {
   }

   public Image2D(int var1, int var2, int var3, byte[] var4, byte[] var5) {
   }

   public Image2D(int var1, Object var2) {
   }

   private static native int create(int var0, int var1, int var2, byte[] var3);

   private static int createImage(int var0, Object var1) {
      return -1;
   }

   private static native int createMutable(int var0, int var1, int var2);

   private static native int createPalettized(int var0, int var1, int var2, byte[] var3, byte[] var4);

   private static native int createUninitialized(int var0, int var1, int var2, boolean var3);

   private native void setRGB(int[] var1, int var2, int var3, int var4, int var5, boolean var6);

   public native int getFormat();

   public native int getHeight();

   public native int getWidth();

   public native boolean isMutable();

   public native void set(int var1, int var2, int var3, int var4, byte[] var5);
}
