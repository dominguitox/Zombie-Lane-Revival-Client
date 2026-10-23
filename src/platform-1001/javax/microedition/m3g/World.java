package javax.microedition.m3g;

public class World extends Group {
   public World() {
   }

   World(int var1) {
   }

   private static native int create();

   private native int getActiveCameraImpl();

   private native int getBackgroundImpl();

   private native void setActiveCameraImpl(Camera var1);

   private native void setBackgroundImpl(Background var1);

   public Camera getActiveCamera() {
      return null;
   }

   public Background getBackground() {
      return null;
   }

   public void setActiveCamera(Camera var1) {
   }

   public void setBackground(Background var1) {
   }
}
