package javax.microedition.m3g;

public class CompositingMode extends Object3D {
   public static final int ALPHA = 64;
   public static final int ALPHA_ADD = 65;
   public static final int MODULATE = 66;
   public static final int MODULATE_X2 = 67;
   public static final int REPLACE = 68;

   public CompositingMode() {
   }

   CompositingMode(int var1) {
   }

   private static native int create();

   public native float getAlphaThreshold();

   public native int getBlending();

   public native float getDepthOffsetFactor();

   public native float getDepthOffsetUnits();

   public native boolean isAlphaWriteEnabled();

   public native boolean isColorWriteEnabled();

   public native boolean isDepthTestEnabled();

   public native boolean isDepthWriteEnabled();

   public native void setAlphaThreshold(float var1);

   public native void setAlphaWriteEnable(boolean var1);

   public native void setBlending(int var1);

   public native void setColorWriteEnable(boolean var1);

   public native void setDepthOffset(float var1, float var2);

   public native void setDepthTestEnable(boolean var1);

   public native void setDepthWriteEnable(boolean var1);
}
