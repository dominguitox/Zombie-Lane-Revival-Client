package javax.microedition.m3g;

public abstract class Object3D {
   boolean ii;
   int swerveHandle;
   Object uo;

   Object3D() {
   }

   Object3D(int var1) {
   }

   private native void addAnimationTrackImpl(AnimationTrack var1);

   private static void duplicateHelper(Object3D var0, Object3D var1) {
   }

   private native int duplicateImpl();

   private native int findImpl(int var1);

   private native int getAnimationTrackImpl(int var1);

   private native int getReferencesImpl(int[] var1);

   public void addAnimationTrack(AnimationTrack var1) {
   }

   public final native int animate(int var1);

   public final Object3D duplicate() {
      return null;
   }

   public Object3D find(int var1) {
      return null;
   }

   public AnimationTrack getAnimationTrack(int var1) {
      return null;
   }

   public native int getAnimationTrackCount();

   public int getReferences(Object3D[] var1) {
      return -1;
   }

   public native int getUserID();

   public Object getUserObject() {
      return null;
   }

   native int getUserParameterID(int var1);

   native int getUserParameterValue(int var1, byte[] var2);

   public native void removeAnimationTrack(AnimationTrack var1);

   native void removeUserParameters();

   public native void setUserID(int var1);

   public void setUserObject(Object var1) {
   }
}
