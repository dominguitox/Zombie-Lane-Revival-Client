package javax.microedition.m3g;

public abstract class Transformable extends Object3D {
   Transformable() {
   }

   Transformable(int var1) {
   }

   public native void getCompositeTransform(Transform var1);

   public native void getOrientation(float[] var1);

   public native void getScale(float[] var1);

   public native void getTransform(Transform var1);

   public native void getTranslation(float[] var1);

   public native void postRotate(float var1, float var2, float var3, float var4);

   public native void preRotate(float var1, float var2, float var3, float var4);

   public native void scale(float var1, float var2, float var3);

   public native void setOrientation(float var1, float var2, float var3, float var4);

   public native void setScale(float var1, float var2, float var3);

   public native void setTransform(Transform var1);

   public native void setTranslation(float var1, float var2, float var3);

   public native void translate(float var1, float var2, float var3);
}
