package com.dchoc.hud;

import com.dchoc.idead.touch.TouchEvent;
import zlport.ios.Ios;

public class HUDMenuPanel extends HUDObject {
   public static final int MENU_TYPE_MARKET = 0;
   public static final int MENU_TYPE_CUSTOMIZATION_MALE = 1;
   public static final int MENU_TYPE_CUSTOMIZATION_FEMALE = 2;
   public static final int MENU_TYPE_CUSTOMIZATION_DOG = 3;
   private HUDItemMenu mSelected;
   private HUDImage mSelector;
   private HUDItemMenu[] mMenuItems;

   public HUDMenuPanel(int type) {
      super((byte)13);
      if (type == 0) {
         this.mSelector = new HUDImage(74667);
      } else {
         this.mSelector = new HUDImage(74659);
      }

      switch (type) {
         case 0: {
            HUDItemMenu[] items = new HUDItemMenu[Ios.platform().isImpactSupported() ? 9 : 8];
            items[0] = new HUDItemMenu(this.mSelector, 0, 74655, 1664);
            items[1] = new HUDItemMenu(this.mSelector, 1, 74657, 1842);
            items[2] = new HUDItemMenu(this.mSelector, 2, 74649, 170);
            items[3] = new HUDItemMenu(this.mSelector, 3, 74663, 1867);
            items[4] = new HUDItemMenu(this.mSelector, 4, 74651, 1844);
            items[5] = new HUDItemMenu(this.mSelector, 5, 78939, 2766);
            items[6] = new HUDItemMenu(this.mSelector, 6, 74653, 1838);
            items[7] = new HUDItemMenu(this.mSelector, 7, 74661, 2237);
            if (Ios.platform().isImpactSupported()) {
               items[8] = new HUDItemMenu(this.mSelector, 8, 74655, 4318);
            }

            this.mMenuItems = items;
            break;
         }
         case 1: {
            HUDItemMenu[] items = new HUDItemMenu[]{
               new HUDItemMenu(this.mSelector, 0, 1862),
               new HUDItemMenu(this.mSelector, 1, 1863),
               new HUDItemMenu(this.mSelector, 2, 636),
               new HUDItemMenu(this.mSelector, 3, 710),
               new HUDItemMenu(this.mSelector, 4, 1864),
               new HUDItemMenu(this.mSelector, 5, 1865),
               new HUDItemMenu(this.mSelector, 6, 1866),
               new HUDItemMenu(this.mSelector, 7, 44)
            };
            this.mMenuItems = items;
            break;
         }
         case 2: {
            HUDItemMenu[] items = new HUDItemMenu[]{
               new HUDItemMenu(this.mSelector, 100, 1862),
               new HUDItemMenu(this.mSelector, 101, 1863),
               new HUDItemMenu(this.mSelector, 102, 710),
               new HUDItemMenu(this.mSelector, 103, 1864),
               new HUDItemMenu(this.mSelector, 104, 1865),
               new HUDItemMenu(this.mSelector, 105, 1866),
               new HUDItemMenu(this.mSelector, 106, 44)
            };
            this.mMenuItems = items;
            break;
         }
         case 3: {
            HUDItemMenu[] items = new HUDItemMenu[]{new HUDItemMenu(this.mSelector, 200, 1033), new HUDItemMenu(this.mSelector, 201, 1034)};
            this.mMenuItems = items;
         }
      }

      for (int i = 0; i < (this.mMenuItems == null ? 0 : this.mMenuItems.length); i++) {
         this.mMenuItems[i].setMenuPanel(this);
         this.mMenuItems[i].setPositionY(this.mMenuItems[i].getHeight() * i);
         this.mMenuItems[i].setParent(this);
         this.mHeight = this.mHeight + this.mMenuItems[i].getHeight();
      }

      this.mWidth = this.mSelector.getCollisionBox(0).getWidth();

      for (int i = 0; i < (this.mMenuItems == null ? 0 : this.mMenuItems.length); i++) {
         this.mMenuItems[i].setWidth(this.mWidth);
      }
   }

   @Override
   public void callback(int id) {
      this.mParent.callback(id);
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         for (int i = 0; i < (this.mMenuItems == null ? 0 : this.mMenuItems.length); i++) {
            if (!this.mMenuItems[i].isSelected()) {
               this.mMenuItems[i].draw();
            }
         }

         if (this.mSelected != null) {
            this.mSelected.draw();
         }

         this.drawDebug();
      }
   }

   @Override
   public void logicUpdate(int delta) {
      for (int i = 0; i < (this.mMenuItems == null ? 0 : this.mMenuItems.length); i++) {
         this.mMenuItems[i].logicUpdate(delta);
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible()) {
         for (int i = 0; i < (this.mMenuItems == null ? 0 : this.mMenuItems.length); i++) {
            this.mMenuItems[i].pointerEvent(event);
         }
      }
   }

   public void select(int id) {
      this.unselectAll();
      if (id > 199) {
         id -= 200;
      }

      if (id > 99) {
         id -= 100;
      }

      this.mMenuItems[id].select();
      this.mSelected = this.mMenuItems[id];
   }

   public void unselectAll() {
      for (int i = 0; i < (this.mMenuItems == null ? 0 : this.mMenuItems.length); i++) {
         this.mMenuItems[i].unselect();
      }
   }

   public HUDImage getSelector() {
      return this.mSelector;
   }
}
