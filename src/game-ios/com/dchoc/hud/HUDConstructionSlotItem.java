package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.items.Item;
import com.dchoc.spriteobject.CollisionBox;

public class HUDConstructionSlotItem extends HUDTargetSlotItem {
   public static final int COLLISION_TITLE = 4;
   private HUDAutoTextField mTextTitle;

   public HUDConstructionSlotItem(int x, int y) {
      super(x, y, 74727, true);
      CollisionBox box = this.mLayout.getCollisionBox(4);
      this.mTextTitle = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextTitle.setSplitUsingWidth(true);
      this.mTextTitle.setScaleToFit(true);
      this.mTextTitle.setCentered(true, true);
      this.mCollection.addObject(this.mTextTitle);
      this.setBuyRemainingItems(true);
      this.setHideBuyButton(true);
      this.mCollectibleHelper.setVisible(true);
   }

   public void enablePlusHelperIcon(boolean enable) {
      this.mCollectibleHelper.enablePlusIcon(enable);
   }

   @Override
   public void setValues(Item item, int target) {
      super.setValues(item, target);
      if (item != null) {
         HUDAutoTextField title = this.mTextTitle;
         title.setText(item.getTitle(), DCiDead.getFont(2));
      }
   }
}
