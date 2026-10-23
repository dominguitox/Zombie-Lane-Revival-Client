package com.dchoc.opengl;

public class OGLParameters {
   private float mAngle;
   private int mColorModification;
   private float mPivotX;
   private float mPivotY;
   private float mScaleX;
   private float mScaleY;

   public OGLParameters() {
      this.mColorModification = -8355712;
      this.mAngle = 0.0F;
      this.mScaleX = 1.0F;
      this.mScaleY = 1.0F;
      this.mPivotX = 0.0F;
      this.mPivotY = 0.0F;
   }

   public OGLParameters(OGLParameters other) {
      this.set(other);
   }

   public float getAngle() {
      return this.mAngle;
   }

   public int getColorModification() {
      return this.mColorModification;
   }

   public float getPivotX() {
      return this.mPivotX;
   }

   public float getPivotY() {
      return this.mPivotY;
   }

   public float getScaleX() {
      return this.mScaleX;
   }

   public float getScaleY() {
      return this.mScaleY;
   }

   public boolean isScaling() {
      return this.mScaleX != 1.0F ? true : this.mScaleY != 1.0F;
   }

   public void set(OGLParameters other) {
      this.mColorModification = other.mColorModification;
      this.mAngle = other.mAngle;
      this.mScaleX = other.mScaleX;
      this.mScaleY = other.mScaleY;
      this.mPivotX = other.mPivotX;
      this.mPivotY = other.mPivotY;
   }

   public void setAlpha(float alpha) {
      this.setColorModification((int)(255.0F * alpha) << 24 | 8421504);
   }

   public void setAngle(float angle) {
      this.mAngle = angle;
   }

   public void setAngleInt(int angle) {
      this.mAngle = angle * 0.005493164F;
   }

   public void setColorModification(int colorModification) {
      this.mColorModification = colorModification;
   }

   public void setPivot(float x, float y) {
      this.mPivotX = x;
      this.mPivotY = y;
   }

   public void setScale(float x, float y) {
      this.mScaleX = x;
      this.mScaleY = y;
   }

   public void setScaleInt(int x, int y) {
      this.setScale(x * 9.765625E-4F, y * 9.765625E-4F);
   }
}
