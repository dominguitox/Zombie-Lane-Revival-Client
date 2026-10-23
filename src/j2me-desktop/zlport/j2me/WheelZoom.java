package zlport.j2me;

import com.dchoc.idead.isometric.Camera;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.tuner.Tuner;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

public final class WheelZoom {
   public static final float STEP = 1.06F;
   public static final float TAU_MS = 60.0F;
   static final float SNAP = 0.001F;
   private static final Logger LOG = System.getLogger("zlport.j2me");
   private static final Object LOCK = new Object();
   private static double pendingNotches;
   private static float anchorX;
   private static float anchorY;
   private static float target = Float.NaN;
   private static long lastFrame;
   private static boolean failureLogged;
   private static volatile float notchFactor = 1.06F;

   private WheelZoom() {
   }

   public static void setNotchFactor(float value) {
      notchFactor = value;
   }

   public static float notchFactor() {
      return notchFactor;
   }

   public static void add(double notches, float x, float y) {
      synchronized (LOCK) {
         pendingNotches += notches;
         anchorX = x;
         anchorY = y;
      }
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
            IsometricScene scene = GameScene.interactive();
            if (scene == null) {
               target = Float.NaN;
               return;
            }

            float current = scene.getScale();
            if (notches != 0.0) {
               float base = Float.isNaN(target) ? current : target;
               target = target(base, notches, Tuner.getMinZoom(), Tuner.getMaxZoom());
            }

            float next = step(current, target, dtMs);
            Camera camera = scene.getCamera();
            float x = anchored(camera.getX(), ax, current, next);
            float y = anchored(camera.getY(), ay, current, next);
            scene.setScale(next);
            scene.updateScale(0);
            camera.moveTo(x, y);
            if (next == target) {
               target = Float.NaN;
            }
         } catch (RuntimeException e) {
            target = Float.NaN;
            if (!failureLogged) {
               failureLogged = true;
               LOG.log(Level.ERROR, "zoom da roda falhou", e);
            }
         }
      }
   }

   public static float target(float base, double notches, float min, float max) {
      float t = (float)(base * Math.pow(notchFactor, notches));
      return Math.max(min, Math.min(max, t));
   }

   public static float step(float current, float target, float dtMs) {
      float diff = target - current;
      if (Math.abs(diff) <= 0.001F) {
         return target;
      }

      float next = current + diff * (float)(1.0 - Math.exp(-dtMs / 60.0F));
      return Math.abs(target - next) <= 0.001F ? target : next;
   }

   public static float anchored(float camera, float anchor, float from, float to) {
      return camera + anchor / from - anchor / to;
   }
}
