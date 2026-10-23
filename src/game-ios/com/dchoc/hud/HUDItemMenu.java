package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.Toolkit;

public class HUDItemMenu extends HUDObject {
   private int mId;
   private HUDImage mIcon;
   private HUDAutoTextField mText;
   private boolean mIsSelected;
   private HUDMenuPanel mMenuPanel;

   public HUDItemMenu(HUDImage selector, int id, int icon, int text) {
      super((byte)14);
      this.mId = id;
      CollisionBox box = selector.getCollisionBox(0);
      HUDImage image = new HUDImage(icon);
      this.mIcon = image;
      image.setPosition(0, (box.getHeight() - this.mIcon.getHeight()) / 2);
      this.mIcon.setParent(this);
      HUDAutoTextField field = new HUDAutoTextField(
         this.mIcon.getCollisionBox(0).getWidth(),
         (box.getHeight() - DCiDead.getFont(4).getHeight()) / 2,
         box.getWidth() + this.mIcon.getCollisionBox(0).getWidth() * -2,
         Toolkit.getScreenHeight()
      );
      this.mText = field;
      field.setScaleToFit(true);
      this.mText.setText(text, 4);
      this.mText.setParent(this);
      this.mWidth = box.getWidth();
      this.mHeight = box.getHeight();
   }

   public HUDItemMenu(HUDImage selector, int id, int text) {
      super((byte)14);
      this.mId = id;
      CollisionBox box = selector.getCollisionBox(1);
      HUDAutoTextField field = new HUDAutoTextField(
         box.getWidth(), (box.getHeight() - DCiDead.getFont(4).getHeight()) / 2, selector.getCollisionBox(0).getWidth(), Toolkit.getScreenHeight()
      );
      this.mText = field;
      field.setScaleToFit(true);
      this.mText.setText(text, 4);
      this.mText.setParent(this);
      this.mWidth = selector.getCollisionBox(0).getWidth();
      this.mHeight = box.getHeight();
   }

   public void setMenuPanel(HUDMenuPanel panel) {
      this.mMenuPanel = panel;
   }

   @Override
   public void callback(int id) {
   }

   @Override
   public void draw() {
      OGL.setClip(0, 0, Toolkit.getScreenWidth(), Toolkit.getScreenHeight());
      if (this.mIsSelected) {
         HUDImage selector = this.mMenuPanel.getSelector();
         selector.setPosition(this.getX(), this.getY() + (this.mHeight - selector.getCollisionBox(0).getHeight()) / 2);
         selector.draw();
      }

      if (this.mIcon != null) {
         this.mIcon.draw();
      }

      this.mText.draw();
      this.drawDebug(16711680);
   }

   @Override
   public void logicUpdate(int delta) {
      this.mText.logicUpdate(delta);
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible() && this.isInside(event.getX(), event.getY())) {
         this.mMenuPanel.unselectAll();
         int id = this.mId;
         if (id > 199) {
            id -= 200;
         }

         if (id > 99) {
            id -= 100;
         }

         this.mMenuPanel.select(id);
         this.mParent.callback(this.mId);
      }
   }

   public boolean isSelected() {
      return this.mIsSelected;
   }

   public void select() {
      this.mIsSelected = true;
   }

   public void unselect() {
      this.mIsSelected = false;
   }
}
