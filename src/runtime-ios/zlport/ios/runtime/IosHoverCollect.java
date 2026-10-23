package zlport.ios.runtime;

import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.objects.LootRewardManager;
import com.dchoc.idead.touch.TouchEvent;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import zlport.j2me.HoverCollect;

public final class IosHoverCollect {
   static final int MAX_PENDING = 64;
   private static final int DRAGGED = 1;
   private static final int RELEASED = 2;
   private static final float[] END = new float[0];
   private static final Logger LOG = System.getLogger("zlport.ios");
   private static final Object LOCK = new Object();
   private static final ArrayDeque<float[]> pending = new ArrayDeque<>();
   private static boolean hasLast;
   private static float lastX;
   private static float lastY;
   private static boolean failureLogged;

   private IosHoverCollect() {
   }

   public static void add(float x, float y) {
      synchronized (LOCK) {
         if (pending.size() >= 64) {
            pending.pollFirst();
         }

         pending.addLast(new float[]{x, y});
      }
   }

   public static void end() {
      synchronized (LOCK) {
         pending.addLast(END);
      }
   }

   public static boolean pending() {
      synchronized (LOCK) {
         return !pending.isEmpty();
      }
   }

   public static void apply() {
      List<float[]> points;
      synchronized (LOCK) {
         if (pending.isEmpty()) {
            return;
         }

         points = new ArrayList<>(pending);
         pending.clear();
      }

      try {
         IsometricScene scene = IosGameScene.touchable();
         if (scene == null) {
            hasLast = false;
            return;
         }

         LootRewardManager loot = scene.getLootRewards();
         boolean sent = false;

         for (float[] p : points) {
            if (p == END) {
               hasLast = false;
            } else {
               for (float[] q : HoverCollect.path(hasLast ? lastX : p[0], hasLast ? lastY : p[1], p[0], p[1], 20.0F)) {
                  loot.processToucEvent(event(q[0], q[1], 1));
                  sent = true;
               }

               lastX = p[0];
               lastY = p[1];
               hasLast = true;
            }
         }

         if (sent) {
            loot.processToucEvent(event(lastX, lastY, 2));
         }
      } catch (RuntimeException | LinkageError e) {
         hasLast = false;
         if (!failureLogged) {
            failureLogged = true;
            LOG.log(Level.ERROR, "coleta pelo cursor falhou", e);
         }
      }
   }

   static void reset() {
      synchronized (LOCK) {
         pending.clear();
      }

      hasLast = false;
      failureLogged = false;
   }

   private static TouchEvent event(float x, float y, int type) {
      int ix = Math.round(x);
      int iy = Math.round(y);
      return new TouchEvent(ix, iy, ix, iy, type);
   }
}
