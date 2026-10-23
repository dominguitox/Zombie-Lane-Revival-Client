package javax.microedition.m3g;

public class AnimationController extends Object3D {
   public AnimationController() {
   }

   AnimationController(int var1) {
   }

   private static native int create();

   public native int getActiveIntervalEnd();

   public native int getActiveIntervalStart();

   public native float getPosition(int var1);

   public native float getSpeed();

   public native float getWeight();

   public native void setActiveInterval(int var1, int var2);

   public native void setPosition(float var1, int var2);

   public native void setSpeed(float var1, int var2);

   public native void setWeight(float var1);
}
