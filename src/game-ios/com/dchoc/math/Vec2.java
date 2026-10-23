package com.dchoc.math;

public class Vec2 {
   public float mX = 0.0F;
   public float mY = 0.0F;

   public void add(float x, float y) {
      this.mX += x;
      this.mY += y;
   }

   public void add(Vec2 vec2) {
      this.mX = this.mX + vec2.mX;
      this.mY = this.mY + vec2.mY;
   }

   public float distance(float x, float y) {
      float dx = x - this.mX;
      float dy = y - this.mY;
      return (float)Math.sqrt(dx * dx + dy * dy);
   }

   public float distance(Vec2 vec2) {
      return this.distance(vec2.mX, vec2.mY);
   }

   public void div(float f) {
      this.mX /= f;
      this.mY /= f;
   }

   public float dot(Vec2 vec2) {
      return this.mX * vec2.mX + this.mY * vec2.mY;
   }

   public float getX() {
      return this.mX;
   }

   public float getY() {
      return this.mY;
   }

   public float length() {
      return (float)Math.sqrt(this.mX * this.mX + this.mY * this.mY);
   }

   public void lerp(Vec2 vec2, float t) {
      this.mX = this.mX + (vec2.mX - this.mX) * t;
      this.mY = this.mY + (vec2.mY - this.mY) * t;
   }

   public void mul(float f) {
      this.mX *= f;
      this.mY *= f;
   }

   public boolean normalize() {
      float len2 = this.mX * this.mX + this.mY * this.mY;
      if (len2 < 1.0E-5F) {
         return false;
      }

      float inv = (float)(1.0 / Math.sqrt(len2));
      this.mX *= inv;
      this.mY *= inv;
      return true;
   }

   public void rotate(float angle) {
      float s = (float)Math.sin(angle);
      float c = (float)Math.cos(angle);
      float x = this.mX * c - this.mY * s;
      float y = s * this.mX + c * this.mY;
      this.mX = x;
      this.mY = y;
   }

   public void set(float x, float y) {
      this.mX = x;
      this.mY = y;
   }

   public void set(Vec2 vec2) {
      this.set(vec2.mX, vec2.mY);
   }

   public void sub(float x, float y) {
      this.mX -= x;
      this.mY -= y;
   }

   public void sub(int x, int y) {
      this.mX -= x;
      this.mY -= y;
   }

   public void sub(Vec2 vec2) {
      this.mX = this.mX - vec2.mX;
      this.mY = this.mY - vec2.mY;
   }

   public void sub(Vec2 a, Vec2 b) {
      this.mX = a.mX - b.mX;
      this.mY = a.mY - b.mY;
   }

   @Override
   public String toString() {
      return "X: " + this.mX + " Y:" + this.mY;
   }
}
