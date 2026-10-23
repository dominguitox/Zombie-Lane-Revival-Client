package com.dchoc.idead.characters;

import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.items.PlayerItem;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.neighbors.NeighborProfile;

public class NPCCharacter extends NeighborCharacter {
   private static final int[] ANIMATIONS = new int[]{1, 1, 3, 3, 0, 2, 0, 4, 0, 3, 3, 3, 3, 3};

   public NPCCharacter(IsometricScene scene) {
      super(scene, 8);
   }

   public void init(float x, float y, PlayerItem item, NeighborProfile profile) {
      super.init(x, y, item, profile, -1);
   }

   @Override
   public WeaponItem getWeapon() {
      return this.mNeighbor.getActiveWeapon();
   }

   @Override
   public int getAnimation(int state) {
      return Integer.compareUnsigned(state, ANIMATIONS.length) < 0 ? ANIMATIONS[state] : 0;
   }

   public int getSoundFX(int state) {
      if (state == 5) {
         return 32;
      } else {
         return state == 7 ? 52 : -1;
      }
   }
}
