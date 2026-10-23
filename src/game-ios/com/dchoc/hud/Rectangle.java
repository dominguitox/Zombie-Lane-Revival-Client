package com.dchoc.hud;

import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;

public class Rectangle extends HUDObject {
   public static final int COLOR_DELIMITER = 12429940;
   public static final int COLOR_LINE_IN_COLLECTION_ITEM = 14006942;
   public static final int COLOR_SEMITRANSPARENT_GREEN = -2141126816;
   public static final int COLOR_SEMITRANSPARENT_RED = -2130747296;
   private int mColor;

   public Rectangle(CollisionBox box, HUDObject parent, int color) {
      super((byte)6);
      super.setSizes(box);
      this.setParent(parent);
      this.mColor = color;
   }

   @Override
   public void callback(int id) {
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         OGL.setColorARGB(this.mColor);
         OGL.fillRect(this.getX(), this.getY(), this.mWidth, this.mHeight);
      }
   }

   @Override
   public void logicUpdate(int delta) {
   }

   @Override
   public void pointerEvent(TouchEvent event) {
   }
}
