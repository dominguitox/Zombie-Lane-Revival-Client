package com.dchoc.idead.objects;

import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.items.Item;

public class StaticObject extends InteractiveObject {
   public StaticObject(IsometricScene scene, int type) {
      super(scene, type);
   }

   @Override
   public void init(float x, float y, Item item) {
      super.init(x, y, item);
   }
}
