package javax.microedition.m3g;

public class KeyframeSequence extends Object3D {
   public static final int CONSTANT = 192;
   public static final int LINEAR = 176;
   public static final int LOOP = 193;
   public static final int SLERP = 177;
   public static final int SPLINE = 178;
   public static final int SQUAD = 179;
   public static final int STEP = 180;

   KeyframeSequence(int var1) {
   }

   public KeyframeSequence(int var1, int var2, int var3) {
   }

   private static native int create(int var0, int var1, int var2);

   public native int getDuration();

   public native int getRepeatMode();

   public native void setDuration(int var1);

   public native void setKeyframe(int var1, int var2, float[] var3);

   public native void setRepeatMode(int var1);

   public native void setValidRange(int var1, int var2);
}
