package javax.microedition.m3g;

public class Sprite3D extends Node {
   Sprite3D(int var1) {
   }

   public Sprite3D(boolean var1, Image2D var2, Appearance var3) {
   }

   private static native int create(boolean var0, Image2D var1, Appearance var2);

   private native int getAppearanceImpl();

   private native int getImageImpl();

   private native void setAppearanceImpl(Appearance var1);

   private native void setImageImpl(Image2D var1);

   public Appearance getAppearance() {
      return null;
   }

   public native int getCropHeight();

   public native int getCropWidth();

   public native int getCropX();

   public native int getCropY();

   public Image2D getImage() {
      return null;
   }

   public native boolean isScaled();

   public void setAppearance(Appearance var1) {
   }

   public native void setCrop(int var1, int var2, int var3, int var4);

   public void setImage(Image2D var1) {
   }
}
