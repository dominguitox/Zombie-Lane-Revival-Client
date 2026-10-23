package zlport.ios.runtime;

import com.dchoc.idead.isometric.Camera;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.idead.tutorial.TutorialFlow;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import zlport.j2me.WheelZoom;

public final class IosWheelZoom {
   private static final Logger LOG = System.getLogger("zlport.ios");
   private static final Object LOCK = new Object();
   private static double pendingNotches;
   private static float anchorX;
   private static float anchorY;
   private static volatile float target = Float.NaN;
   private static long lastFrame;
   private static boolean failureLogged;

   private IosWheelZoom() {
   }

   public static void add(double notches, float x, float y) {
      synchronized (LOCK) {
         pendingNotches += notches;
         anchorX = x;
         anchorY = y;
      }
   }

   public static boolean pending() {
      synchronized (LOCK) {
         if (pendingNotches != 0.0) {
            return true;
         }
      }

      return !Float.isNaN(target);
   }

   public static void apply(long now) {
      double notches;
      float ax;
      float ay;
      synchronized (LOCK) {
         notches = pendingNotches;
         pendingNotches = 0.0;
         ax = anchorX;
         ay = anchorY;
      }

      float dtMs = Math.min((float)(now - lastFrame) / 1000000.0F, 100.0F);
      lastFrame = now;
      if (notches != 0.0 || !Float.isNaN(target)) {
         try {
            IsometricScene scene = IosGameScene.touchable();
            if (scene == null || !TutorialFlow.isCompleted()) {
               target = Float.NaN;
               return;
            }

            float current = scene.getScale();
            float goal = target;
            if (notches != 0.0) {
               goal = WheelZoom.target(Float.isNaN(goal) ? current : goal, notches, Tuner.getMinZoom(), Tuner.getMaxZoom());
            }

            float next = WheelZoom.step(current, goal, dtMs);
            Camera camera = scene.getCamera();
            float x = WheelZoom.anchored(camera.getX(), ax, current, next);
            float y = WheelZoom.anchored(camera.getY(), ay, current, next);
            scene.setScale(next);
            camera.moveTo(x, y);
            target = next == goal ? Float.NaN : goal;
         } catch (RuntimeException | LinkageError e) {
            target = Float.NaN;
            if (!failureLogged) {
               failureLogged = true;
               LOG.log(Level.ERROR, "zoom da roda falhou", e);
            }
         }
      }
   }

   static void reset() {
      synchronized (LOCK) {
         pendingNotches = 0.0;
      }

      target = Float.NaN;
      lastFrame = 0L;
      failureLogged = false;
   }
}
