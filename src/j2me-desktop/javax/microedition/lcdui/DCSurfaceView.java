package javax.microedition.lcdui;

import android.content.Context;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.tuner.Tuner;
import javax.microedition.khronos.opengles.GL;
import zlport.j2me.Platform;
import zlport.j2me.Renderer;
import zlport.j2me.Touch;
import zlport.stubs.NotPorted;

public class DCSurfaceView implements IDCView {
   public static int scaleDelta = 0;
   private static long lastGestureEventAt = 0L;
   private static int mScalefactor = 0;
   final Displayable displayable;
   private final Renderer mRenderer;
   private final char[] inputChars = new char[1];
   private float initDistance;
   private boolean zoomingIn;
   private boolean zoomingOut;

   public DCSurfaceView(Context context, Displayable displayable) {
      this.displayable = displayable;
      this.mRenderer = Platform.driver().createRenderer(context, displayable);
      Platform.surface().setRenderer(this.mRenderer);
   }

   public static boolean disableDrag() {
      return System.currentTimeMillis() - lastGestureEventAt <= 400L;
   }

   public boolean dispatchTouch(Touch e) {
      return this.onTouch(e) || this.onTouchEvent(e);
   }

   boolean onTouch(Touch e) {
      if (e.getPointerCount() != 1) {
         if ((e.getAction() & 0xFF) == 5) {
            this.initDistance = getDistance(e);
         } else {
            IsometricScene.FORCE_GESTURE_EVENT = true;
            float distance = getDistance(e);
            lastGestureEventAt = System.currentTimeMillis();
            if (distance > Tuner.TOUCH_DRAG_THRESHOLD) {
               int i = (int)((int)distance - this.initDistance);
               scaleDelta = i >> 5;
               if (mScalefactor > i) {
                  if (this.zoomingIn) {
                     this.initDistance = getDistance(e);
                  }

                  this.zoomingOut = true;
                  this.zoomingIn = false;
               } else {
                  if (this.zoomingOut) {
                     this.initDistance = getDistance(e);
                  }

                  this.zoomingIn = true;
                  this.zoomingOut = false;
               }

               mScalefactor = i;
            }
         }
      }

      sleep15();
      return false;
   }

   boolean onTouchEvent(Touch e) {
      if (e.getAction() == 0) {
         this.displayable.pointerPressed((int)e.getX(), (int)e.getY());
      }

      if (e.getAction() == 1) {
         this.displayable.pointerReleased((int)e.getX(), (int)e.getY());
      }

      if (e.getAction() == 2) {
         this.displayable.pointerDragged((int)e.getX(), (int)e.getY());
      }

      sleep15();
      return true;
   }

   public boolean onKeyDown(int keyCode, char ch) {
      if (isMediaKey(keyCode)) {
         return false;
      }

      this.displayable.keyPressed(keyCode);
      this.callKeyEventWithChars(keyCode, 0, ch);
      return keyCode != 24 && keyCode != 25;
   }

   public boolean onKeyUp(int keyCode, char ch) {
      if (isMediaKey(keyCode)) {
         return false;
      }

      this.displayable.keyReleased(keyCode);
      this.callKeyEventWithChars(keyCode, 1, ch);
      return true;
   }

   private void callKeyEventWithChars(int keyCode, int eventType, char ch) {
      if (ch != 0 && !isSpecialKey(keyCode)) {
         this.inputChars[0] = ch;
         this.displayable.keyEventWithChars(keyCode, eventType, this.inputChars);
      } else {
         this.displayable.keyEventWithChars(keyCode, eventType, null);
      }
   }

   private static boolean isMediaKey(int k) {
      return k == 79 || k == 85 || k == 86 || k == 90 || k == 87 || k == 88 || k == 89;
   }

   private static boolean isSpecialKey(int k) {
      return k == 57
         || k == 58
         || k == 59
         || k == 60
         || k == 66
         || k == 25
         || k == 24
         || k == 23
         || k == 20
         || k == 21
         || k == 22
         || k == 19
         || k == 4
         || k == 67
         || k == 82
         || k == 5
         || k == 27
         || k == 80;
   }

   private static float getDistance(Touch e) {
      float x = e.getX(0) - e.getX(1);
      float y = e.getY(0) - e.getY(1);
      return (float)Math.sqrt(x * x + y * y);
   }

   private static void sleep15() {
      try {
         Thread.sleep(15L);
      } catch (InterruptedException var1) {
      }
   }

   public void createGraphicsContext() {
   }

   public void destroyGraphicsContext() {
      NotPorted.method("javax.microedition.lcdui.DCSurfaceView#destroyGraphicsContext()");
   }

   public void doDraw() {
      Platform.driver().doDraw();
   }

   public void doRequestRender() {
      Platform.driver().requestRender();
   }

   public GL getGraphicsContext() {
      return Platform.driver().graphicsContext();
   }

   public boolean isGraphicsContextReady() {
      return Platform.driver().graphicsContextReady();
   }
}
