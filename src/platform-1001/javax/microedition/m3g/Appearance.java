package javax.microedition.m3g;

public class Appearance extends Object3D {
   public Appearance() {
   }

   Appearance(int var1) {
   }

   private static native int create();

   private native int getCompositingModeImpl();

   private native int getFogImpl();

   private native int getMaterialImpl();

   private native int getPolygonModeImpl();

   private native int getTextureImpl(int var1);

   private native void setCompositingModeImpl(CompositingMode var1);

   private native void setFogImpl(Fog var1);

   private native void setMaterialImpl(Material var1);

   private native void setPolygonModeImpl(PolygonMode var1);

   private native void setTextureImpl(int var1, Texture2D var2);

   public CompositingMode getCompositingMode() {
      return null;
   }

   public Fog getFog() {
      return null;
   }

   public native int getLayer();

   public Material getMaterial() {
      return null;
   }

   public PolygonMode getPolygonMode() {
      return null;
   }

   public Texture2D getTexture(int var1) {
      return null;
   }

   public void setCompositingMode(CompositingMode var1) {
   }

   public void setFog(Fog var1) {
   }

   public native void setLayer(int var1);

   public void setMaterial(Material var1) {
   }

   public void setPolygonMode(PolygonMode var1) {
   }

   public void setTexture(int var1, Texture2D var2) {
   }
}
