package zlport.ios.runtime;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUD;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.Game;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.isometric.IsometricScene;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

final class IosGameScene {
   private static final VarHandle FLOW_STATE = flowState();

   private IosGameScene() {
   }

   static IsometricScene touchable() {
      if (Game.isInstanceCreated() && (int)FLOW_STATE.get((Game)Game.getInstance()) == 2 && DCiDead.getActivityState() != 0) {
         GameEngine engine = GameEngine.getInstance();
         if (engine == null) {
            return null;
         } else {
            int state = engine.getState();
            if (state < 2 || state == 8 || state == 9) {
               return null;
            } else {
               return HUD.isSceneActive() && WindowManager.isEmpty() ? engine.getScene() : null;
            }
         }
      } else {
         return null;
      }
   }

   private static VarHandle flowState() {
      try {
         return MethodHandles.privateLookupIn(Game.class, MethodHandles.lookup()).findVarHandle(Game.class, "mCurrentState", int.class);
      } catch (ReflectiveOperationException e) {
         throw new ExceptionInInitializerError(e);
      }
   }
}
