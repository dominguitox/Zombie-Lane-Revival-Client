package javax.microedition.lcdui;

import android.content.Context;
import com.dchoc.opengl.OGLTexture;
import com.dchoc.toolkit.Toolkit;
import java.lang.System.Logger.Level;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.midlet.MIDlet;
import zlport.j2me.Renderer;

public class DCRenderer implements Renderer {
   private static int _height;
   private static GL mGL;
   private int _width;
   private final Context mContext;
   private final Displayable mDisplayable;
   public static volatile boolean mGLReady = false;
   public static boolean mLoadingComplete = false;
   public static boolean smDrawLoadingScreen = false;
   public static ConcurrentLinkedQueue<OGLTexture> smLoadTextures = new ConcurrentLinkedQueue<>();
   public static ConcurrentLinkedQueue<OGLTexture> smUnloadTextures = new ConcurrentLinkedQueue<>();
   public static ConcurrentLinkedQueue<OGLTexture> smActiveTextures = new ConcurrentLinkedQueue<>();
   public static Object smProcessingTexturesLock = new Object();
   public static int smFrameCounter = 0;

   public DCRenderer(Context context, Displayable displayable) {
      this.mContext = context;
      this.mDisplayable = displayable;
   }

   public static GL getGL() {
      return mGL;
   }

   public static int getSurfaceHeight() {
      return _height;
   }

   public static boolean isGLReady() {
      return mGLReady;
   }

   public static void sleepUntilTexturesLoaded() {
      while (smLoadTextures.size() > 0) {
         try {
            Thread.sleep(50L);
         } catch (Exception var1) {
         }
      }
   }

   @Override
   public void onDrawFrame(GL10 gl10) {
      GL11 gl11 = (GL11)gl10;
      smFrameCounter++;
      if (smFrameCounter < 0) {
         smFrameCounter = 5;
      }

      if (MIDlet.isPaused() || smFrameCounter < 5) {
         gl11.glDisable(3089);
         gl11.glClearColor(1.0F, 1.0F, 1.0F, 0.0F);
         gl11.glClear(16384);
         gl11.glEnable(3089);
      }

      if (MIDlet.isPaused()) {
         mGLReady = false;
         smUnloadTextures.clear();
      } else {
         mGLReady = true;
         loadPendingTextures(gl11);
         gl11.glMatrixMode(5889);
         gl11.glLoadIdentity();
         gl11.glOrthof(0.0F, this._width, 0.0F, _height, -1.0F, 1.0F);
         gl11.glMatrixMode(5888);
         gl11.glLoadIdentity();
         if (isGLReady()) {
            try {
               GLGraphics g = GLGraphics.getInstance();
               g.setGl(gl11);
               this.mDisplayable.paint(g);
               gl11.glPopMatrix();
            } catch (Exception e) {
               System.getLogger("zlport.j2me").log(Level.ERROR, "DRAW EXCEPTION! " + e, e);
            }
         }

         if (smUnloadTextures != null && smUnloadTextures.size() > 0) {
            synchronized (smProcessingTexturesLock) {
               for (OGLTexture unload : smUnloadTextures) {
                  for (OGLTexture active : smActiveTextures) {
                     if (active.getFilename().equals(unload.getFilename())) {
                        smActiveTextures.remove(active);
                     }
                  }

                  unload.unload(gl11);
                  smUnloadTextures.remove(unload);
               }
            }
         }

         Toolkit.sm_okToPaint = false;
      }
   }

   @Override
   public void pumpTextures(GL10 gl10) {
      if (!MIDlet.isPaused()) {
         loadPendingTextures((GL11)gl10);
      }
   }

   private static void loadPendingTextures(GL11 gl11) {
      if (smLoadTextures != null && smLoadTextures.size() > 0) {
         synchronized (smProcessingTexturesLock) {
            for (OGLTexture texture : smLoadTextures) {
               texture.load(gl11);
               smLoadTextures.remove(texture);
               if (!smActiveTextures.contains(texture)) {
                  smActiveTextures.add(texture);
               }
            }
         }
      }
   }

   @Override
   public void onSurfaceChanged(GL10 gl10, int width, int height) {
      GL11 gl11 = (GL11)gl10;
      gl11.glViewport(0, 0, width, height);
      gl11.glMatrixMode(5889);
      gl11.glLoadIdentity();
      gl11.glDisable(3089);
      gl11.glClear(16384);
      gl11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
      gl11.glEnable(3089);
      gl11.glMatrixMode(5888);
      gl11.glLoadIdentity();
      this._width = width;
      _height = height;
   }

   @Override
   public void onSurfaceCreated(GL10 gl10) {
      GL11 gl11 = (GL11)gl10;
      gl11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
      gl11.glHint(3152, 4354);
      gl11.glHint(3154, 4354);
      mLoadingComplete = false;
      mGL = gl11;
      mGLReady = false;
   }
}
