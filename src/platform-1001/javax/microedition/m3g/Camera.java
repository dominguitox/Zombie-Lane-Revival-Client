package javax.microedition.m3g;

public class Camera extends Node {
   public static final int GENERIC = 48;
   public static final int PARALLEL = 49;
   public static final int PERSPECTIVE = 50;

   public Camera() {
   }

   Camera(int var1) {
   }

   private static native int create();

   private native int getProjectionParams(float[] var1);

   private native int getProjectionTransform(Transform var1);

   public int getProjection(Transform var1) {
      return -1;
   }

   public int getProjection(float[] var1) {
      return -1;
   }

   public native void setGeneric(Transform var1);

   public native void setParallel(float var1, float var2, float var3, float var4);

   public native void setPerspective(float var1, float var2, float var3, float var4);
}
