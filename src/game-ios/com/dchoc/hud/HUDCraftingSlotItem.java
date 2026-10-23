package com.dchoc.hud;

public class HUDCraftingSlotItem extends HUDTargetSlotItem {
   public HUDCraftingSlotItem(int x, int y) {
      super(x, y, 74705, false);
      this.setSlotBackgroundVisible(false);
      this.mCollectibleHelper.setVisible(true);
   }
}
