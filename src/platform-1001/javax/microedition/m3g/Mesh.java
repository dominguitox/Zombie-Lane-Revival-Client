package javax.microedition.m3g;

public class Mesh extends Node {
   Mesh(int var1) {
   }

   public Mesh(VertexBuffer var1, IndexBuffer var2, Appearance var3) {
   }

   public Mesh(VertexBuffer var1, IndexBuffer[] var2, Appearance[] var3) {
   }

   private static native int createMultiSubmesh(VertexBuffer var0, int[] var1, int[] var2);

   private static native int createSingleSubmesh(VertexBuffer var0, IndexBuffer var1, Appearance var2);

   private native int getAppearanceImpl(int var1);

   private native int getIndexBufferImpl(int var1);

   private native int getVertexBufferImpl();

   private native void setAppearanceImpl(int var1, Appearance var2);

   public Appearance getAppearance(int var1) {
      return null;
   }

   public IndexBuffer getIndexBuffer(int var1) {
      return null;
   }

   public native int getSubmeshCount();

   public VertexBuffer getVertexBuffer() {
      return null;
   }

   public void setAppearance(int var1, Appearance var2) {
   }
}
