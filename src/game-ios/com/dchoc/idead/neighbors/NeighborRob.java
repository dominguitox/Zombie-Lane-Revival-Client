package com.dchoc.idead.neighbors;

import com.dchoc.idead.fixes.Fixes;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.toolkit.DChocByteArray;

public class NeighborRob extends NeighborProfile {
   public static final String NAME = "Rob";
   public static final String ID = "1";

   public NeighborRob() {
      super("Rob", "1", 1, 3, 2514);
      this.setActionsLeft(5);
      this.setLevel(7);
   }

   @Override
   public WeaponItem getActiveWeapon() {
      return ItemManager.getWeapon(19);
   }

   @Override
   public boolean load(DChocByteArray data) {
      super.load(data);
      Fixes.fixRob(this);
      return true;
   }
}
