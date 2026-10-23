package com.dchoc.idead;

import com.dchoc.math.Vec2;

public class TransformMatrix {
   private float mA;
   private float mB;
   private float mC;
   private float mD;
   private float mTX;
   private float mTY;

   public TransformMatrix(float a, float b, float c, float d, float tx, float ty) {
      this.mA = a;
      this.mB = b;
      this.mC = c;
      this.mD = d;
      this.mTX = tx;
      this.mTY = ty;
   }

   public TransformMatrix clone() {
      return new TransformMatrix(this.mA, this.mB, this.mC, this.mD, this.mTX, this.mTY);
   }

   public void identity() {
      this.mA = 1.0F;
      this.mB = 0.0F;
      this.mC = 0.0F;
      this.mD = 1.0F;
      this.mTX = 0.0F;
      this.mTY = 0.0F;
   }

   public void invert() {
      float a = this.mA;
      float b = this.mB;
      float c = this.mC;
      float d = this.mD;
      float tx = this.mTX;
      float ty = this.mTY;
      float det = a * d - b * c;
      this.mA = d / det;
      this.mB = -b / det;
      this.mC = -c / det;
      this.mD = a / det;
      this.mTX = (c * ty - d * tx) / det;
      this.mTY = -(a * ty - b * tx) / det;
   }

   public void rotate(float angle) {
      float a = this.mA;
      float b = this.mB;
      float c = this.mC;
      float d = this.mD;
      float tx = this.mTX;
      float ty = this.mTY;
      float sin = (float)Math.sin(angle);
      float cos = (float)Math.cos(angle);
      this.mA = a * cos - b * sin;
      this.mB = a * sin + b * cos;
      this.mC = c * cos - d * sin;
      this.mD = c * sin + d * cos;
      this.mTX = tx * cos - ty * sin;
      this.mTY = tx * sin + ty * cos;
   }

   public void scale(float sx, float sy) {
      this.mA *= sx;
      this.mB *= sy;
      this.mC *= sx;
      this.mD *= sy;
      this.mTX *= sx;
      this.mTY *= sy;
   }

   public void transform(Vec2 vec2) {
      float x = vec2.getX();
      float y = vec2.getY();
      vec2.set(this.mA * x + this.mB * y, this.mC * x + this.mD * y);
   }

   public void translate(float dx, float dy) {
      this.mTX += dx;
      this.mTY += dy;
   }
}
