package javax.microedition.m3g;

public class Light extends Node {
   public static final int AMBIENT = 128;
   public static final int DIRECTIONAL = 129;
   public static final int OMNI = 130;
   public static final int SPOT = 131;

   public Light() {
   }

   Light(int var1) {
   }

   private static native int create();

   public native int getColor();

   public native float getConstantAttenuation();

   public native float getIntensity();

   public native float getLinearAttenuation();

   public native int getMode();

   public native float getQuadraticAttenuation();

   public native float getSpotAngle();

   public native float getSpotExponent();

   public native void setAttenuation(float var1, float var2, float var3);

   public native void setColor(int var1);

   public native void setIntensity(float var1);

   public native void setMode(int var1);

   public native void setSpotAngle(float var1);

   public native void setSpotExponent(float var1);
}
