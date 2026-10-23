package javax.microedition.m3g;

public class Transform {
   int swerveHandle;

   public Transform() {
   }

   Transform(int var1) {
   }

   public Transform(Transform var1) {
   }

   private static native int create();

   private static native int createCopy(Transform var0);

   private native void setMatrix(float[] var1);

   private native void setTransform(Transform var1);

   private native void transformPoints(float[] var1);

   public native void get(float[] var1);

   public native void invert();

   public native void postMultiply(Transform var1);

   public native void postRotate(float var1, float var2, float var3, float var4);

   public native void postRotateQuat(float var1, float var2, float var3, float var4);

   public native void postScale(float var1, float var2, float var3);

   public native void postTranslate(float var1, float var2, float var3);

   public void set(Transform var1) {
   }

   public void set(float[] var1) {
   }

   public native void setIdentity();

   public native void transform(VertexArray var1, float[] var2, boolean var3);

   public void transform(float[] var1) {
   }

   public native void transpose();
}
