package com.dchoc.hud;

import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.windows.WindowTooltip;

public abstract class HUDObject {
   public static final byte CENTER_NONE = 0;
   public static final byte CENTER_X = 1;
   public static final byte CENTER_Y = 2;
   public static final byte CENTER_POSITION = 3;
   public static final int ID_INVALID = -1;
   public static final byte TYPE_WINDOW = 1;
   public static final byte TYPE_COLLECTION = 10;
   public static final byte TYPE_INTEGER = 16;
   protected boolean mIsVisible = true;
   protected boolean mIsMirrored = false;
   protected boolean mIsHighlighted = false;
   protected byte mType;
   protected int mPosX;
   protected int mPosY;
   protected int mWidth;
   protected int mHeight;
   protected int mId;
   protected HUDObject mParent;
   protected byte mPositionType;
   protected byte mTooltipType;
   protected IsometricObject mTargetObject;
   protected byte mCenterType = 0;

   protected HUDObject(byte type, int id) {
      this.mType = type;
      this.mId = id;
      this.mPositionType = -1;
      this.mTooltipType = -1;
   }

   protected HUDObject(byte type) {
      this.mType = type;
      this.mId = -1;
      this.mPositionType = -1;
      this.mTooltipType = -1;
   }

   public abstract void callback(int var1);

   public abstract void draw();

   public abstract void logicUpdate(int var1);

   public abstract void pointerEvent(TouchEvent var1);

   public void drawDebug() {
      this.drawDebug(255);
   }

   public void drawDebug(int color) {
   }

   public void freeImages() {
   }

   public int getX() {
      return this.mParent != null ? this.mParent.getX() + this.mPosX : this.mPosX;
   }

   public int getY() {
      return this.mParent != null ? this.mParent.getY() + this.mPosY : this.mPosY;
   }

   public int getFixedX() {
      int offset = 0;
      if ((this.mCenterType | 2) == 3) {
         offset = this.mWidth >> 1;
      }

      return this.getX() - offset;
   }

   public int getFixedY() {
      int offset = 0;
      if ((this.mCenterType - 2 & 0xFF) < 2) {
         offset = this.mHeight >> 1;
      }

      return this.getY() - offset;
   }

   public boolean isVisible() {
      return this.mIsVisible;
   }

   public boolean isMirrored() {
      return this.mIsMirrored;
   }

   public boolean isHighlighted() {
      return this.mIsHighlighted;
   }

   public void setHighlighted(boolean highlighted) {
      this.mIsHighlighted = highlighted;
   }

   public boolean isInside(TouchEvent event) {
      return this.isInside(event.getX(), event.getY());
   }

   public boolean isInside(int x, int y) {
      int left = this.getX();
      int top = this.getY();
      return left <= x && x <= this.mWidth + left && top <= y && y <= top + this.mHeight;
   }

   public void setId(int id) {
      this.mId = id;
   }

   public int getId() {
      return this.mId;
   }

   public void setType(byte type) {
      this.mType = type;
   }

   public byte getType() {
      return this.mType;
   }

   public void setTooltipType(byte type) {
      this.mTooltipType = type;
   }

   public int getTooltipType() {
      return this.mTooltipType;
   }

   public void setSizes(CollisionBox box) {
      this.mPosX = box.getX();
      this.mPosY = box.getY();
      this.mWidth = box.getWidth();
      this.mHeight = box.getHeight();
   }

   public void setSizes(int x, int y, int width, int height) {
      this.mPosX = x;
      this.mPosY = y;
      this.mWidth = width;
      this.mHeight = height;
   }

   public void setSize(int width, int height) {
      this.mWidth = width;
      this.mHeight = height;
   }

   public void setWidth(int width) {
      this.mWidth = width;
   }

   public void setHeight(int height) {
      this.mHeight = height;
   }

   public int getWidth() {
      return this.mWidth;
   }

   public int getHeight() {
      return this.mHeight;
   }

   public void setPosition(int x, int y) {
      this.mPosX = x;
      this.mPosY = y;
   }

   public void setPosition(CollisionBox box) {
      this.mPosX = box.getX();
      this.mPosY = box.getY();
   }

   public void setPositionX(int x) {
      this.mPosX = x;
   }

   public void setPositionY(int y) {
      this.mPosY = y;
   }

   public int getPositionX() {
      return this.mPosX;
   }

   public int getPositionY() {
      return this.mPosY;
   }

   public void changePositionY(int delta) {
      this.mPosY += delta;
   }

   public void setCenterType(byte type) {
      this.mCenterType = type;
   }

   public byte getCenterType() {
      return this.mCenterType;
   }

   public void setParent(HUDObject parent) {
      this.mParent = parent;
   }

   public HUDObject getParent() {
      return this.mParent;
   }

   public void setVisible(boolean visible) {
      this.mIsVisible = visible;
   }

   public void setMirror(boolean mirror) {
      this.mIsMirrored = mirror;
   }

   protected void createTooltip(int x, int y, int width, int height, int type) {
      if (TutorialFlow.isCompleted()) {
         boolean enabled = WindowTooltip.isEnableTooltips();
         if (type != -1 && enabled) {
            HUD.createTooltip(type, x, y, width, height);
         }
      }
   }

   protected void createTooltip(HUDImage image) {
      if (TutorialFlow.isCompleted() && WindowTooltip.isEnableTooltips() && this.mTooltipType != -1) {
         int parentX = 0;
         int parentY = 0;
         if (this.mParent != null && this.mParent.getType() != 3 && this.mTargetObject == null) {
            parentX = this.mParent.getX();
            parentY = this.mParent.getY();
         }

         int type = this.mTooltipType;
         int posX = this.mPosX;
         int pivotX = image.getPivotX();
         int posY = this.mPosY;
         int pivotY = image.getPivotY();
         int width = image.getWidth();
         int height = image.getHeight();
         HUD.createTooltip(type, posX + parentX - pivotX, posY + parentY - pivotY, width, height);
      }
   }
}
