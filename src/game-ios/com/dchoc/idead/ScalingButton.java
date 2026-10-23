package com.dchoc.idead;

import com.dchoc.imagefont.ImageFont;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.IButton;
import javax.microedition.lcdui.Graphics;

public class ScalingButton implements IButton {
   public static final int ALIGNMENT_BOTTOM = 32;
   public static final int ALIGNMENT_CENTER = 67;
   public static final int ALIGNMENT_HCENTER = 1;
   public static final int ALIGNMENT_LEFT = 4;
   public static final int ALIGNMENT_RIGHT = 8;
   public static final int ALIGNMENT_TOP = 16;
   public static final int ALIGNMENT_VCENTER = 66;
   public static final int BUTTON_DISPATCH_EVENT = 6;
   public static final int BUTTON_HIGHLIGHTED = 1;
   public static final int BUTTON_IDLE = 0;
   public static final int BUTTON_LOCKED_HIGHLIGHTED = 4;
   public static final int BUTTON_LOCKED_IDLE = 3;
   public static final int BUTTON_LOCKED_SELECTED = 5;
   public static final int BUTTON_SELECTED = 2;
   private static final int COLLISION_BOX_AREA_LEFT = 0;
   private static final int COLLISION_BOX_AREA_RIGHT = 1;
   private static final int COLLISION_BOX_MARGIN = 2;
   public static final int EVENT_DATA_EVENT_ID = 0;
   public static final int EVENT_DATA_EVENT_TYPE = 1;
   private static final int INDEX_TILE_WIDTH = 5;
   private static final int INDEX_TILE_X = 4;
   private static final int INDEX_X_LEFT = 0;
   private static final int INDEX_X_RIGHT = 2;
   private static final int INDEX_Y_LEFT = 1;
   private static final int INDEX_Y_RIGHT = 3;
   private static final int PRESSED_SIZE_INCREASE = 10;
   public static final int SHRINK_TO_FIT = -1;
   public static final int STATE_NONE = -1;
   private static final int SWITCH_MARGIN = 30;
   public static final int SWITCH_NOT_USED = -1;
   private static final int TEXT_XMARGIN = 3;
   private static final int TEXT_YMARGIN = 3;
   private int mAlignment;
   private int mButtonCollisionXOffset;
   private int mButtonCollisionYOffset;
   private int mButtonHeight;
   private SpriteObject mButtonIcon;
   private int mButtonTextAreaHeight;
   private int mButtonTextAreaWidth;
   private int[][] mButtonTextPosition;
   private SpriteObject mButtonTiles;
   private int mButtonWidth;
   private int mButtonX;
   private int mButtonY;
   private int[] mEvent;
   private ImageFont[] mFonts;
   private String mLabel;
   private int mLeftTileStart;
   private int mMiddleTileStart;
   private int mMiddleTileWidth;
   private int mNextState;
   private int mRightTileWidth;
   private int mState;
   private SpriteObject mSwitchGraphics;
   private int mSwitchState;
   private String[] mSwitchTexts;
   private int mSwitchValueCount;
   private int mTextAlignment;
   private boolean mVisible;

   public ScalingButton(
      int i, int i2, int i3, int i4, int i5, int i6, String str, int[] iArr, int i7, int i8, int i9, ImageFont[] imageFontArr, int i10, String[] strArr
   ) {
   }

   @Override
   public void destroy() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ScalingButton.destroy");
   }

   @Override
   public void doDraw(Graphics graphics) {
      throw new UnsupportedOperationException("sem código na 1.4.4: ScalingButton.doDraw");
   }

   @Override
   public int getHeight() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ScalingButton.getHeight");
   }

   @Override
   public int getPivotX() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ScalingButton.getPivotX");
   }

   @Override
   public int getPivotY() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ScalingButton.getPivotY");
   }

   @Override
   public int getWidth() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ScalingButton.getWidth");
   }

   @Override
   public int getX() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ScalingButton.getX");
   }

   @Override
   public int getY() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ScalingButton.getY");
   }

   @Override
   public int[] logicUpdate(int i) {
      throw new UnsupportedOperationException("sem código na 1.4.4: ScalingButton.logicUpdate");
   }

   @Override
   public boolean pointerEventOccurred(int i, int i2, int i3) {
      throw new UnsupportedOperationException("sem código na 1.4.4: ScalingButton.pointerEventOccurred");
   }

   @Override
   public void resetState() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ScalingButton.resetState");
   }

   @Override
   public void setPosition(int i, int i2) {
      throw new UnsupportedOperationException("sem código na 1.4.4: ScalingButton.setPosition");
   }

   @Override
   public void setSize(int i, int i2) {
      throw new UnsupportedOperationException("sem código na 1.4.4: ScalingButton.setSize");
   }
}
