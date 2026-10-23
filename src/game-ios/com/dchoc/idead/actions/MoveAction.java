package com.dchoc.idead.actions;

import com.dchoc.toolkit.Toolkit;

public class MoveAction extends Action {
   private float mTargetX;
   private float mTargetY;

   public MoveAction() {
      super((byte)1);
   }

   @Override
   public void start() {
      super.start();
      if (!this.mActor.moveTo(this.mTargetX, this.mTargetY) && this.mActor.isPlayer()) {
         this.addFloater(Toolkit.getText(1999));
      }
   }

   @Override
   public boolean isCompleted() {
      return this.mActor.getState() == 0;
   }

   @Override
   public void cancel() {
      this.closeTooltip();
      if (this.mStarted) {
         this.mActor.stop();
      }
   }

   @Override
   public boolean isCancelable() {
      return true;
   }

   public void setTarget(float x, float y) {
      this.mTargetX = x;
      this.mTargetY = y;
      this.mTarget = null;
   }

   public float getTargetX() {
      return this.mTargetX;
   }

   public float getTargetY() {
      return this.mTargetY;
   }
}
