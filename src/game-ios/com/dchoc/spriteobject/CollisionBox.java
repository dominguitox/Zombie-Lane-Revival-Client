package com.dchoc.spriteobject;

public class CollisionBox {
   private short mHeight;
   private short mId;
   private short mWidth;
   private short mX;
   private short mY;

   public CollisionBox(int x, int y, int width, int height, int id) {
      this.mX = (short)x;
      this.mY = (short)y;
      this.mWidth = (short)width;
      this.mHeight = (short)height;
      this.mId = (short)id;
   }

   public int getHeight() {
      return this.mHeight;
   }

   public int getId() {
      return this.mId;
   }

   public int getWidth() {
      return this.mWidth;
   }

   public int getX() {
      return this.mX;
   }

   public int getY() {
      return this.mY;
   }
}
