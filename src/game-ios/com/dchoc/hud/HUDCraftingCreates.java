package com.dchoc.hud;

import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.spriteobject.CollisionBox;

public class HUDCraftingCreates extends HUDCraftingSlotItem {
   private static final int COLLISION_CRAFT_BUTTON = 4;
   private HUDAutoTextField mTextDescription;

   public HUDCraftingCreates(int x, int y) {
      super(x, y);
      CollisionBox box = this.mLayout.getCollisionBox(2);
      this.mTextDescription = new HUDAutoTextField(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mTextDescription.setCentered(true, true);
      this.mCollection.addObject(this.mTextDescription);
      this.mButtonBuy.changeStyle(3);
      HUDButton button = this.mButtonBuy;
      button.setPosition(this.mLayout.getCollisionBox(4));
      this.mCollectibleHelper.setVisible(false);
   }

   @Override
   public void setValues(Item item, int target) {
      if (item != null) {
         this.mItem = item;
         this.mTarget = target;
         HUDImage icon = this.mImageIcon;
         icon.setImage(item.getIconAnimationID());
         this.mCount = PlayerProfile.getInventory().getItemCount(item);
         this.mTargetCount.setVisible(false);
         this.mButtonBuy.setText(1860);
         if (item.getType() == 1907) {
            WeaponItem weapon = (WeaponItem)this.mItem;
            String damage = Integer.toString(weapon.getDamage());
            String ammo = Integer.toString(weapon.getMaxAmmo());
            this.mTextDescription.setText(2135, 4, damage, -1, ammo, -1);
         }
      }
   }

   public void enableCrafting(boolean enable) {
      this.mButtonBuy.setEnabled(enable);
   }

   @Override
   public void callback(int id) {
      if (id == -1) {
         HUDObject parent = this.mParent;
         if (parent != null) {
            parent.callback(4);
         }
      }
   }
}
