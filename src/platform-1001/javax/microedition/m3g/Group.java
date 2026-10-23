package javax.microedition.m3g;

public class Group extends Node {
   public Group() {
   }

   Group(int var1) {
   }

   private native void addChildImpl(Node var1);

   private static native int create();

   private native int getChildImpl(int var1);

   private native boolean pickCamera(int var1, float var2, float var3, Camera var4, RayIntersection var5);

   private native boolean pickNode(int var1, float var2, float var3, float var4, float var5, float var6, float var7, RayIntersection var8);

   public void addChild(Node var1) {
   }

   public Node getChild(int var1) {
      return null;
   }

   public native int getChildCount();

   public boolean pick(int var1, float var2, float var3, float var4, float var5, float var6, float var7, RayIntersection var8) {
      return false;
   }

   public boolean pick(int var1, float var2, float var3, Camera var4, RayIntersection var5) {
      return false;
   }

   public native void removeChild(Node var1);
}
