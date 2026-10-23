package com.dchoc.hud;

import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;

public class HUDRewardsPanel extends HUDObject {
   private int mIconHeightMax;
   private int mIconY;
   private int mTextY;
   private ImageFont mTextFont;
   private CollisionBox mBoundsCollisionBox;
   private int mIconCount;
   private int mFullWidth;
   private SpriteObject[] mIconAnim;
   private String[] mIconText;

   public HUDRewardsPanel(CollisionBox box, SpriteObject[] icons, ImageFont font) {
      super((byte)15);
      this.mIconAnim = icons;
      this.mBoundsCollisionBox = box;
      this.mTextFont = font;

      for (int i = 0; i < this.mIconAnim.length; i++) {
         if (this.mIconHeightMax < this.mIconAnim[i].getHeight()) {
            this.mIconHeightMax = this.mIconAnim[i].getHeight();
         }
      }

      this.mIconText = new String[this.mIconAnim.length];
   }

   public void startAddingIcons() {
      for (int i = 0; i < this.mIconText.length; i++) {
         this.mIconText[i] = null;
      }

      this.mIconCount = 1;
      this.mFullWidth = 0;
   }

   public void addIcon(int index, String text) {
      this.mIconText[index] = text;
      this.mIconCount++;
      this.mFullWidth = this.mIconAnim[index].getWidth() + this.mFullWidth;
   }

   public void endAddingIcons() {
      int y = this.mBoundsCollisionBox.getY();
      int height = this.mBoundsCollisionBox.getHeight();
      int iconHeight = this.mIconHeightMax;
      int fontHeight = this.mTextFont.getHeight();
      this.mIconY = y + (height - iconHeight - fontHeight >> 1) + (this.mIconHeightMax >> 1);
      this.mTextY = this.mIconY + (this.mIconHeightMax >> 1);
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         int parentX = 0;
         int parentY = 0;
         if (this.mParent != null) {
            parentX = this.mParent.getX();
            parentY = this.mParent.getY();
         }

         int iconY = this.mIconY + parentY;
         int textY = this.mTextY + parentY;
         int step = 0;
         if (this.mIconCount > 1) {
            step = this.mBoundsCollisionBox.getWidth() / (this.mIconCount - 1);
         }

         int x = this.mBoundsCollisionBox.getX() + parentX + step / 2;

         for (int i = 0; i < this.mIconText.length; i++) {
            if (this.mIconText[i] != null) {
               this.mIconAnim[i].draw(x, iconY);
               this.mTextFont.drawString(this.mIconText[i], x, textY, 17);
               x += step;
            }
         }
      }
   }

   @Override
   public void logicUpdate(int delta) {
   }

   @Override
   public void pointerEvent(TouchEvent event) {
   }

   @Override
   public void callback(int id) {
   }
}
