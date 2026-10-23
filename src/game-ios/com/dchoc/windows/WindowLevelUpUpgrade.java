package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDSlot;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.spriteobject.CollisionBox;
import java.util.Vector;

public class WindowLevelUpUpgrade extends WindowLevelUpBase {
   private static HUDSlot mItemSlot;
   private static Item[] smUnlockedItems;
   private HUDAutoTextField smUnlockText;

   public WindowLevelUpUpgrade() {
      super(67001);
      initUnlockArrays();
   }

   private static void initUnlockArrays() {
      int level = PlayerProfile.getLevel();
      smUnlockedItems = new Item[150];
      Vector items = ItemManager.getItems(1907);
      int size = items.size();
      if (size > 0) {
         int i = 0;

         do {
            WeaponItem item = (WeaponItem)items.elementAt(i);
            if (item != null) {
               int required = item.getRequiredLevel();
               if (required > 1 && required > level && required < (smUnlockedItems == null ? 0 : smUnlockedItems.length)) {
                  smUnlockedItems[required] = item;
               }
            }

            i++;
            size = items.size();
         } while (i < size);
      }
   }

   @Override
   public void init(int level) {
      super.init(level);
      Item item = smUnlockedItems[level];
      if (item != null) {
         mItemSlot.init(item, this, false);
         CollisionBox box = this.getCollisionBox(51);
         HUDSlot slot = mItemSlot;
         int x = box.getX();
         int width = box.getWidth();
         int slotWidth = mItemSlot.getWidth();
         int y = box.getY();
         int height = box.getHeight();
         int slotHeight = mItemSlot.getHeight();
         slot.setPosition(x + (width - slotWidth) / 2, y + (height - slotHeight) / 2);
      }
   }

   public static boolean isUpgrade(int level) {
      return level < (smUnlockedItems == null ? 0 : smUnlockedItems.length) ? smUnlockedItems[level] != null : false;
   }

   @Override
   protected void initComponents() {
      super.initComponents();
      this.smUnlockText = new HUDAutoTextField(this.getCollisionBox(50));
      this.smUnlockText.setCentered(true, true);
      HUDAutoTextField text = this.smUnlockText;
      text.setText(114, DCiDead.getFont(4));
      mItemSlot = new HUDSlot();
   }

   @Override
   protected void createObjects() {
      this.mObjects.addObject(this.mRectangleBG);
      this.mObjects.addObject(this.mRectangleRewards);
      this.mObjects.addObject(this.smTitleText);
      this.mObjects.addObject(this.smRewardsText);
      this.mObjects.addObject(this.smUnlockText);
      this.mObjects.addObject(mItemSlot);
      this.mObjects.addObject(this.smButtonOk);
      this.mObjects.addObject(this.mRewardIconsPanel);
      this.mObjects.addObject(this.smForegroundStars);
      this.mObjects.addObject(this.smLevel);
   }
}
