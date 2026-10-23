package javax.microedition.m3g;

import java.util.Hashtable;
import javax.microedition.lcdui.Graphics;

public class Graphics3D {
   public static final int ANTIALIAS = 2;
   public static final int DITHER = 4;
   static final int MAXLIGHTS = 256;
   static final int MAXSPRITECROPDIMENSION = 1024;
   static final int MAXTEXTUREDIMENSION = 1024;
   static final int MAXTRANSFORMSPERVERTEX = 2;
   static final int MAXVIEWPORTDIMENSION = 1024;
   static final int NUMTEXTUREUNITS = 2;
   static final int SUPPORTANTIALIASING = 0;
   static final int SUPPORTDITHERING = 0;
   static final int SUPPORTLOCALCAMERALIGHTING = 1;
   static final int SUPPORTMIPMAP = 0;
   static final int SUPPORTPERSPECTIVECORRECTION = 1;
   static final int SUPPORTTRUECOLOR = 0;
   public static final int TRUE_COLOR = 8;
   private static Hashtable properties;
   private Object boundTarget;
   private boolean isGraphics;
   int swerveHandle;

   Graphics3D() {
   }

   Graphics3D(int var1) {
   }

   private void bindTarget(Graphics var1) {
   }

   private native void bindTargetGraphics(Graphics var1, int var2, int var3, int var4, int var5);

   private native void clearImpl(Background var1);

   public static final Graphics3D getInstance() {
      return null;
   }

   private static native int getInstanceImpl();

   // $VF: Could not create synchronized statement, marking monitor enters and exits
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final Hashtable getProperties() {
      synchronized (Graphics3D.class){} // $VF: monitorenter 
      // $VF: monitorexit
      return null;
   }

   private native void releaseTargetGraphics(Graphics var1);

   private native void renderNode(Node var1, Transform var2);

   private native void renderPrimitive(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int var5);

   private native void renderWorld(World var1);

   private native void setBackBufferImage2D(Image2D var1);

   private native void setHints(boolean var1, int var2);

   private native void setViewportImpl(int var1, int var2, int var3, int var4);

   public native int addLight(Light var1, Transform var2);

   // $VF: Could not create synchronized statement, marking monitor enters and exits
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void bindTarget(Object var1) {
      synchronized (this){} // $VF: monitorenter 
      // $VF: monitorexit
   }

   // $VF: Could not create synchronized statement, marking monitor enters and exits
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void bindTarget(Object var1, boolean var2, int var3) {
      synchronized (this){} // $VF: monitorenter 
      // $VF: monitorexit
   }

   // $VF: Could not create synchronized statement, marking monitor enters and exits
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void clear(Background var1) {
      synchronized (this){} // $VF: monitorenter 
      // $VF: monitorexit
   }

   // $VF: Could not create synchronized statement, marking monitor enters and exits
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void releaseTarget() {
      synchronized (this){} // $VF: monitorenter 
      // $VF: monitorexit
   }

   // $VF: Could not create synchronized statement, marking monitor enters and exits
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void render(Node var1, Transform var2) {
      synchronized (this){} // $VF: monitorenter 
      // $VF: monitorexit
   }

   public void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4) {
   }

   // $VF: Could not create synchronized statement, marking monitor enters and exits
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int var5) {
      synchronized (this){} // $VF: monitorenter 
      // $VF: monitorexit
   }

   // $VF: Could not create synchronized statement, marking monitor enters and exits
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void render(World var1) {
      synchronized (this){} // $VF: monitorenter 
      // $VF: monitorexit
   }

   public native void resetLights();

   public native void setCamera(Camera var1, Transform var2);

   public native void setDepthRange(float var1, float var2);

   public native void setLight(int var1, Light var2, Transform var3);

   public void setViewport(int var1, int var2, int var3, int var4) {
   }
}
