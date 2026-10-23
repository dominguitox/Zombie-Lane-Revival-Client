package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.CollectibleItem;
import com.dchoc.idead.items.Collection;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.WindowTooltip;

public class HUDCollectibleHelper extends HUDObject {
   private CollectibleItem mItem;
   private SlotBackground mSlotBackground;
   private HUDImage mImageIcon;
   private HUDImage mImagePlus;
   private HUDCollection mCollection = new HUDCollection();
   private CollisionBox mBox = AnimationsManager.loadShared(78513).getCollisionBox(0);

   public HUDCollectibleHelper() {
      super((byte)29);
      this.setWidth(this.mBox.getWidth());
      this.setHeight(this.mBox.getHeight());
      int border = SlotBackground.getBorderWidth();
      int x = this.mBox.getX();
      int y = this.mBox.getY();
      int width = this.mBox.getWidth();
      int height = this.mBox.getHeight();
      this.mSlotBackground = new SlotBackground(x - border, y - border, border * 2 + width, height + border * 2);
      this.mImageIcon = new HUDImage();
      this.mImagePlus = new HUDImage(78841);
      this.mImagePlus.setVisible(false);
      this.mCollection.addObject(this.mSlotBackground);
      this.mCollection.addObject(this.mImageIcon);
      this.mCollection.addObject(this.mImagePlus);
      this.mCollection.setParent(this);
   }

   public void enablePlusIcon(boolean enable) {
      HUDImage plus = this.mImagePlus;
      if (enable) {
         plus.setVisible(true);
      } else {
         plus.setVisible(false);
      }
   }

   public void setItem(CollectibleItem item) {
      this.mItem = item;
      Collection collection = ItemManager.getCollection(item);
      int fallback = 78511;
      if (item.getID() != 48) {
         fallback = -1;
         if (item.getID() == 2021) {
            fallback = 78511;
         }
      }

      int id = this.mItem.getID();

      this.mImageIcon.setImage(switch (id) {
         case 2011 -> 66519;
         case 2013 -> 66475;
         case 2014, 2015 -> 66557;
         case 2016 -> 66503;
         case 2018 -> 66523;
         case 2019 -> 66507;
         case 2020 -> 66545;
         case 3801, 3802, 3803, 3804 -> 79365;
         case 4801, 4802 -> 79907;
         case 4803, 4804 -> 79909;
         default -> {
            yield fallback;
            if (collection != null) {
               yield collection.getIconAnimationID();
            }
         }
      });
      int boxWidth = this.mBox.getWidth();
      int iconWidth = this.mImageIcon.getWidth();
      int boxHeight = this.mBox.getHeight();
      int iconHeight = this.mImageIcon.getHeight();
      this.mImageIcon.setPositionX((boxWidth - iconWidth) / 2);
      this.mImageIcon.setPositionY((boxHeight - iconHeight) / 2);
      HUDImage plus = this.mImagePlus;
      int backgroundWidth = this.mSlotBackground.getWidth();
      int border = SlotBackground.getBorderWidth();
      int plusWidth = this.mImagePlus.getWidth();
      plus.setPositionX(backgroundWidth - border - plusWidth / 2);
      plus = this.mImagePlus;
      int boxY = this.mBox.getY();
      int plusHeight = this.mImagePlus.getHeight();
      plus.setPositionY(boxY + plusHeight / 2);
      this.mImageIcon.setClipBox(this.mBox);
   }

   @Override
   public void callback(int id) {
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         this.mCollection.draw();
         this.drawDebug();
      }
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.isVisible()) {
         this.mCollection.logicUpdate(delta);
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible() && !event.isConsumed() && this.isInside(event) && event.getType() == 0) {
         event.setConsumed(true);
         if (WindowTooltip.isEnableTooltips()) {
            int x = this.getX();
            int y = this.getY();
            int width = this.getWidth();
            int height = this.getHeight();
            String text = Toolkit.getText(this.mItem.getDescription());
            HUD.createTooltip(7, x, y, width, height, 10, text, true);
         }
      }
   }
}
