package javax.microedition.m3g;

public abstract class Node extends Transformable {
   public static final int NONE = 144;
   public static final int ORIGIN = 145;
   public static final int X_AXIS = 146;
   public static final int Y_AXIS = 147;
   public static final int Z_AXIS = 148;

   Node() {
   }

   Node(int var1) {
   }

   private native int getParentImpl();

   private native void setAlignmentImpl(Node var1, int var2, Node var3, int var4);

   public final native void align(Node var1);

   public native float getAlphaFactor();

   public Node getParent() {
      return null;
   }

   public native int getScope();

   public native boolean getTransformTo(Node var1, Transform var2);

   public native boolean isPickingEnabled();

   public native boolean isRenderingEnabled();

   public void setAlignment(Node var1, int var2, Node var3, int var4) {
   }

   public native void setAlphaFactor(float var1);

   public native void setPickingEnable(boolean var1);

   public native void setRenderingEnable(boolean var1);

   public native void setScope(int var1);
}
