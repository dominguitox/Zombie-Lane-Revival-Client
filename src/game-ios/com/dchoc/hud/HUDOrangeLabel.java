package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.Animation;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public class HUDOrangeLabel extends HUDObject {
   public static final int MODE_NEW = 0;
   public static final int MODE_PROGRESS = 1;
   public static final int MODE_CLICK_HERE = 2;
   public static final int MODE_NEW_MESSAGE = 3;
   public static final int MODE_NEW_TIP = 4;
   public static final int MODE_RETURN_HOME = 5;
   private static final int[] TEXT_IDS = new int[]{1665, 1669, 143, 1667, 1666, 2443};
   private HUDAutoTextField smTexts;
   private RoundRectangle orangeRect;
   private SpriteObject mMissionLogArrowMovement;
   private SpriteObject mMissionLogArrowAnimation;
   private int mCurrentColorModification;
   private int mAnimatedOffsetX;
   private int mCurrentMode;

   public HUDOrangeLabel(HUDObject parent, boolean left) {
      super((byte)24);
      super.setParent(parent);
      this.mMissionLogArrowMovement = new SpriteObject(left ? 65699 : 74927, true, 0);
      this.mMissionLogArrowAnimation = new SpriteObject(65661, true, 0);
      HUDAutoTextField text = new HUDAutoTextField(0, 0, 0, 0);
      this.smTexts = text;
      text.setParent(this);
      RoundRectangle rectangle = new RoundRectangle(5);
      this.orangeRect = rectangle;
      rectangle.setParent(this);
      this.orangeRect.setMirror(!left);
      this.mHeight = this.orangeRect.getLeftBorderHeight();
      this.mCurrentMode = -1;
      this.setVisible(false);
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.isVisible()) {
         this.smTexts.logicUpdate(delta);
         this.mMissionLogArrowMovement.logicUpdate(delta);
         this.mMissionLogArrowAnimation.logicUpdate(delta);
         Animation animation = this.mMissionLogArrowMovement.getCurrentAnimationData();
         int time = this.mMissionLogArrowMovement.getElapsedTime();
         this.mAnimatedOffsetX = animation.getTimelineValue(1, time, true);
         animation = this.mMissionLogArrowAnimation.getCurrentAnimationData();
         time = this.mMissionLogArrowAnimation.getElapsedTime();
         this.mCurrentColorModification = animation.getTimelineValue(0, time, true);
         if (this.mMissionLogArrowAnimation.isFinishedAnimation()) {
            if (Integer.compareUnsigned(this.mCurrentMode, 5) > 0) {
               return;
            }

            int modeMask = 1 << this.mCurrentMode;
            if ((modeMask & 53) != 0) {
               this.mMissionLogArrowAnimation.setAnimation(0, 1, false);
            } else if ((modeMask & 10) != 0) {
               this.setVisible(false);
            }
         }
      }
   }

   @Override
   public boolean isInside(int x, int y) {
      return this.isVisible() && this.mCurrentColorModification != 8421504 && super.isInside(x, y);
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         int leftWidth = this.orangeRect.getLeftBorderWidth();
         int rightWidth = this.orangeRect.getRightBorderWidth();
         ImageFont font = DCiDead.getFont(6);
         String text = Toolkit.getText(TEXT_IDS[this.mCurrentMode]);
         int textWidth = font.stringWidth(text);
         this.mWidth = rightWidth + leftWidth + textWidth;
         this.orangeRect.setWidth(this.mWidth);
         OGL.pushParameters();
         OGL.getParameters().setColorModification(this.mCurrentColorModification);
         if (this.mParent.isHighlighted() || this.isHighlighted()) {
            OGL.pushParameters();
            OGL.setColorModification(-1151311776);
         }

         this.orangeRect.setPosition(this.mAnimatedOffsetX, 0);
         this.orangeRect.draw();
         this.orangeRect.drawDebug(65280);
         HUDAutoTextField field = this.smTexts;
         leftWidth = this.orangeRect.getLeftBorderWidth();
         field.setPosition(this.mAnimatedOffsetX + leftWidth, 0);
         this.smTexts.draw();
         this.smTexts.drawDebug(16711935);
         if (this.mParent.isHighlighted() || this.isHighlighted()) {
            OGL.popParameters();
         }

         OGL.popParameters();
      }
   }

   public void setMode(int mode) {
      if (this.mCurrentMode != mode) {
         this.mCurrentMode = mode;
         this.mMissionLogArrowAnimation.setAnimation(0, 1, false);
         this.setVisible(true);
         this.recalculate();
      }
   }

   @Override
   public void callback(int id) {
   }

   @Override
   public void pointerEvent(TouchEvent event) {
   }

   public void notifyChildResize(HUDObject child) {
      this.recalculate();
   }

   public void recalculate() {
      ImageFont font = DCiDead.getFont(6);
      String text = Toolkit.getText(TEXT_IDS[this.mCurrentMode]);
      int width = font.stringWidth(text);
      this.smTexts.setCentered(true, true);
      this.smTexts.setText(TEXT_IDS[this.mCurrentMode], 6);
      HUDAutoTextField field = this.smTexts;
      int height = this.orangeRect.getLeftBorderHeight();
      field.setSizes(0, 0, width, height);
      int leftWidth = this.orangeRect.getLeftBorderWidth();
      int rightWidth = this.orangeRect.getRightBorderWidth();
      this.mWidth = leftWidth + width + rightWidth;
      this.orangeRect.setWidth(this.mWidth);
   }
}
