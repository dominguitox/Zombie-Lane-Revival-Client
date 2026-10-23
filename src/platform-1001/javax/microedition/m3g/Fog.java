package javax.microedition.m3g;

public class Fog extends Object3D {
   public static final int EXPONENTIAL = 80;
   public static final int LINEAR = 81;

   public Fog() {
   }

   Fog(int var1) {
   }

   private static native int create();

   public native int getColor();

   public native float getDensity();

   public native float getFarDistance();

   public native int getMode();

   public native float getNearDistance();

   public native void setColor(int var1);

   public native void setDensity(float var1);

   public native void setLinear(float var1, float var2);

   public native void setMode(int var1);
}
