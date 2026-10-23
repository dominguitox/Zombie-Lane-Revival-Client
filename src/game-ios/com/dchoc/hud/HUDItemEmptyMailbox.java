package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.windows.WindowNpcDialog;
import com.dchoc.windows.WindowSocial;

public class HUDItemEmptyMailbox extends HUDItem {
   protected SlotBackground mSlotBackground;
   private HUDAutoTextField mTextTitle;
   private HUDAutoTextField mTextDescription;
   private HUDButton mButtonSendGifts;
   private SpriteObject mCollisions;

   public HUDItemEmptyMailbox() {
      this.setType((byte)17);
      this.mCollisions = AnimationsManager.loadShared(74787);
      CollisionBox size = this.mCollisions.getCollisionBox(0);
      this.setSize(size.getWidth(), size.getHeight());
      CollisionBox box = this.mCollisions.getCollisionBox(0);
      this.mSlotBackground = new SlotBackground(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      HUDAutoTextField title = new HUDAutoTextField(this.mCollisions.getCollisionBox(2));
      this.mTextTitle = title;
      title.setText(2280, DCiDead.getFont(2));
      this.mTextTitle.setCentered(true, true);
      HUDAutoTextField description = new HUDAutoTextField(this.mCollisions.getCollisionBox(3));
      this.mTextDescription = description;
      description.setText(2281, DCiDead.getFont(3));
      this.mTextDescription.setCentered(true, true);
      CollisionBox button = this.mCollisions.getCollisionBox(1);
      HUDButton sendGifts = new HUDButton(0, button, 2);
      this.mButtonSendGifts = sendGifts;
      sendGifts.setText(1802);
      this.mObjects = new HUDObject[]{this.mSlotBackground, this.mTextTitle, this.mTextDescription, this.mButtonSendGifts};
      this.initObjects();
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible()) {
         for (int i = 0; i < (this.mObjects == null ? 0 : this.mObjects.length); i++) {
            if (this.mObjects[i] != null) {
               this.mObjects[i].pointerEvent(event);
            }
         }
      }
   }

   @Override
   public void callback(int id) {
      if (id == 0) {
         if (!((WindowNpcDialog)WindowManager.getWindow((byte)19)).checkConnectionDialog()) {
            ((WindowSocial)WindowManager.getWindow((byte)18)).showNeighbors(null);
         }
      }
   }
}
