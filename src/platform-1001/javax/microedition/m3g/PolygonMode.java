package javax.microedition.m3g;

public class PolygonMode extends Object3D {
   public static final int CULL_BACK = 160;
   public static final int CULL_FRONT = 161;
   public static final int CULL_NONE = 162;
   public static final int SHADE_FLAT = 164;
   public static final int SHADE_SMOOTH = 165;
   public static final int WINDING_CCW = 168;
   public static final int WINDING_CW = 169;

   public PolygonMode() {
   }

   PolygonMode(int var1) {
   }

   private static native int create();

   public native int getCulling();

   public native int getShading();

   public native int getWinding();

   public native boolean isTwoSidedLightingEnabled();

   public native void setCulling(int var1);

   public native void setLocalCameraLightingEnable(boolean var1);

   public native void setPerspectiveCorrectionEnable(boolean var1);

   public native void setShading(int var1);

   public native void setTwoSidedLightingEnable(boolean var1);

   public native void setWinding(int var1);
}
