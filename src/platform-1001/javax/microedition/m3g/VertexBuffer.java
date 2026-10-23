package javax.microedition.m3g;

public class VertexBuffer extends Object3D {
   public VertexBuffer() {
   }

   VertexBuffer(int var1) {
   }

   private static native int create();

   private native int getColorsImpl();

   private native int getNormalsImpl();

   private native int getPositionsImpl(float[] var1);

   private native int getTexCoordsImpl(int var1, float[] var2);

   private native void setColorsImpl(VertexArray var1);

   private native void setNormalsImpl(VertexArray var1);

   private native void setPositionsImpl(VertexArray var1, float var2, float[] var3);

   private native void setTexCoordsImpl(int var1, VertexArray var2, float var3, float[] var4);

   public VertexArray getColors() {
      return null;
   }

   public native int getDefaultColor();

   public VertexArray getNormals() {
      return null;
   }

   public VertexArray getPositions(float[] var1) {
      return null;
   }

   public VertexArray getTexCoords(int var1, float[] var2) {
      return null;
   }

   public native int getVertexCount();

   public void setColors(VertexArray var1) {
   }

   public native void setDefaultColor(int var1);

   public void setNormals(VertexArray var1) {
   }

   public void setPositions(VertexArray var1, float var2, float[] var3) {
   }

   public void setTexCoords(int var1, VertexArray var2, float var3, float[] var4) {
   }
}
