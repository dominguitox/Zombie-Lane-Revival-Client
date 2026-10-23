package com.dchoc.math;

public class Rect2 {
   protected float mHeight;
   protected float mWidth;
   protected float mX;
   protected float mY;

   public Rect2() {
      this.set(0.0F, 0.0F, 0.0F, 0.0F);
   }

   public Rect2(float x, float y, float width, float height) {
      this.set(x, y, width, height);
   }

   public static boolean isPointInside(float px, float py, float x, float y, float width, float height) {
      return !(px < x) && !(x + width <= px) && !(py < y) ? py < y + height : false;
   }

   public static boolean isRectangleInside(float x1, float y1, float w1, float h1, float x2, float y2, float right2, float bottom2) {
      return x1 < x2 && !(x1 + w1 <= right2) && y1 < y2 ? bottom2 < y1 + h1 : false;
   }

   public static boolean isRectangleIntersecting(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
      return x2 <= x1 + w1 && x1 < x2 + w2 && y2 <= y1 + h1 ? y1 < y2 + h2 : false;
   }

   public float getHeight() {
      return this.mHeight;
   }

   public float getWidth() {
      return this.mWidth;
   }

   public float getX() {
      return this.mX;
   }

   public float getY() {
      return this.mY;
   }

   public boolean isInside(float px, float py) {
      return !(px < this.mX) && !(this.mX + this.mWidth <= px) && !(py < this.mY) ? py < this.mY + this.mHeight : false;
   }

   public boolean isInside(float x, float y, float width, float height) {
      return this.mX <= x && x + width < this.mX + this.mWidth && this.mY <= y ? y + height < this.mY + this.mHeight : false;
   }

   public boolean isInside(Rect2 rect) {
      return this.isInside(rect.mX, rect.mY, rect.mWidth, rect.mHeight);
   }

   public boolean isInside(Vec2 point) {
      return this.isInside(point.mX, point.mY);
   }

   public boolean isInside(Vec2 pos, Vec2 size) {
      return this.isInside(pos.mX, pos.mY, size.mX, size.mY);
   }

   public boolean isIntersecting(float x, float y, float width, float height) {
      return this.mX <= x + width && x < this.mX + this.mWidth && this.mY <= y + height ? y < this.mY + this.mHeight : false;
   }

   public boolean isIntersecting(Rect2 rect) {
      return this.isIntersecting(rect.mX, rect.mY, rect.mWidth, rect.mHeight);
   }

   public boolean isIntersecting(Vec2 pos, Vec2 size) {
      return this.isIntersecting(pos.mX, pos.mY, size.mX, size.mY);
   }

   public void set(float x, float y, float width, float height) {
      this.mX = x;
      this.mY = y;
      this.mWidth = width;
      this.mHeight = height;
   }

   @Override
   public String toString() {
      return "X: " + this.mX + " Y:" + this.mY + " Width: " + this.mWidth + " Height: " + this.mHeight;
   }
}
