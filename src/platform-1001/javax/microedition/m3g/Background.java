package javax.microedition.m3g;

public class Background extends Object3D {
   public static final int BORDER = 32;
   public static final int REPEAT = 33;

   public Background() {
   }

   Background(int var1) {
   }

   private static native int create();

   private native int getImageImpl();

   private native void setImageImpl(Image2D var1);

   public native int getColor();

   public native int getCropHeight();

   public native int getCropWidth();

   public native int getCropX();

   public native int getCropY();

   public Image2D getImage() {
      return null;
   }

   public native int getImageModeX();

   public native int getImageModeY();

   public native boolean isColorClearEnabled();

   public native boolean isDepthClearEnabled();

   public native void setColor(int var1);

   public native void setColorClearEnable(boolean var1);

   public native void setCrop(int var1, int var2, int var3, int var4);

   public native void setDepthClearEnable(boolean var1);

   public void setImage(Image2D var1) {
   }

   public native void setImageMode(int var1, int var2);
}
