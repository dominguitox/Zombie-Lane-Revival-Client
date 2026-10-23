package com.dchoc.idead.actions;

import com.dchoc.toolkit.ToolkitHelpers;
import java.util.Vector;

public class ActionsManager {
   private static Vector smActionsPool;

   public static void init() {
      smActionsPool = new Vector(24);
   }

   public static Action createAction(int type) {
      for (int i = 0; i < smActionsPool.size(); i++) {
         Action action = (Action)smActionsPool.elementAt(i);
         if (action.getType() == type) {
            ToolkitHelpers.fastRemove(i, smActionsPool);
            return action;
         }
      }

      switch (type) {
         case 1:
            return new MoveAction();
         case 2:
            return new AttackAction();
         case 3:
            return new ConstructAction();
         case 4:
            return new LootAction();
         case 5:
            return new FarmingAction();
         default:
            return null;
      }
   }

   public static void poolAction(Action action) {
      if (action != null) {
         if (!smActionsPool.contains(action)) {
            smActionsPool.addElement(action);
         }
      }
   }
}
