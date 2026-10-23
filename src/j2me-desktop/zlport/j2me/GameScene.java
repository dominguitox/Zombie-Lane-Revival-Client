package zlport.j2me;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUD;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.Game;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.tutorial.TutorialFlow;

final class GameScene {
   private GameScene() {
   }

   static IsometricScene interactive() {
      GameEngine engine = GameEngine.getInstance();
      if (engine != null && !DCiDead.mLoading && !GameEngine.reloading && Game.getInstance().getCurrentState() == 2) {
         int state = engine.getState();
         if (state != 2 && state != 4) {
            return null;
         } else {
            return TutorialFlow.isCompleted() && HUD.isSceneActive() && WindowManager.isEmpty() ? engine.getScene() : null;
         }
      } else {
         return null;
      }
   }
}
