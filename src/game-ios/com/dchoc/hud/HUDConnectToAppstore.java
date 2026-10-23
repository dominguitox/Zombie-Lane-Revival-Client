package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.windows.WindowMarket;

public class HUDConnectToAppstore extends HUDObject {
   private static SlotBackground mBackground;
   private static HUDButton mButtonRetry;
   private static HUDAutoTextField mInfo;
   private static HUDAutoTextField mTitle;
   private static HUDCollection mObjects;

   public HUDConnectToAppstore() {
      super((byte)3);
      SpriteObject sprite = AnimationsManager.loadShared(75023);
      CollisionBox box0 = sprite.getCollisionBox(0);
      CollisionBox box1 = sprite.getCollisionBox(1);
      CollisionBox box2 = sprite.getCollisionBox(2);
      CollisionBox box3 = sprite.getCollisionBox(3);
      this.mWidth = box0.getWidth();
      this.mHeight = box0.getHeight();
      mBackground = new SlotBackground(box0);
      HUDAutoTextField title = new HUDAutoTextField(box1);
      mTitle = title;
      title.setCentered(true, true);
      mTitle.setText(2449, 2);
      HUDAutoTextField info = new HUDAutoTextField(box2);
      mInfo = info;
      info.setCentered(true, true);
      mInfo.setSplitUsingWidth(true);
      mInfo.setText(2450, 3);
      HUDButton button = new HUDButton(53000, box3, 3);
      mButtonRetry = button;
      button.setText(2360);
      HUDCollection objects = new HUDCollection();
      mObjects = objects;
      objects.setParent(this);
      mObjects.addObject(mBackground);
      mObjects.addObject(mTitle);
      mObjects.addObject(mInfo);
      mObjects.addObject(mButtonRetry);
   }

   @Override
   public void callback(int id) {
      if (id == 53000) {
         ((WindowMarket)this.mParent.getParent()).requestCashAndCoinds();
      }
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         mObjects.draw();
      }
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.isVisible()) {
         mObjects.logicUpdate(delta);
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible()) {
         mObjects.pointerEvent(event);
      }
   }
}
