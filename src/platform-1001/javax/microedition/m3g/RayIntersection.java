package javax.microedition.m3g;

public class RayIntersection {
   int swerveHandle;

   public RayIntersection() {
   }

   RayIntersection(int var1) {
   }

   private static native int create();

   private native int getIntersectedImpl();

   public native float getDistance();

   public Node getIntersected() {
      return null;
   }

   public native float getNormalX();

   public native float getNormalY();

   public native float getNormalZ();

   public native void getRay(float[] var1);

   public native int getSubmeshIndex();

   public native float getTextureS(int var1);

   public native float getTextureT(int var1);
}
