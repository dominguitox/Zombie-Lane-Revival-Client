package javax.microedition.m3g;

public class Material extends Object3D {
   public static final int AMBIENT = 1024;
   public static final int DIFFUSE = 2048;
   public static final int EMISSIVE = 4096;
   public static final int SPECULAR = 8192;

   public Material() {
   }

   Material(int var1) {
   }

   private static native int create();

   public native int getColor(int var1);

   public native float getShininess();

   public native boolean isVertexColorTrackingEnabled();

   public native void setColor(int var1, int var2);

   public native void setShininess(float var1);

   public native void setVertexColorTrackingEnable(boolean var1);
}
