package com.dchoc.hud;

public class HUDButtonWithOrangeLabel extends HUDButton {
   private HUDOrangeLabel mOrangeLabel;

   public HUDButtonWithOrangeLabel(int id, int x, int y, int resource, byte tooltip) {
      super(id, x, y, resource, tooltip);
      HUDOrangeLabel label = new HUDOrangeLabel(this, false);
      this.mOrangeLabel = label;
      label.setMode(5);
      this.mOrangeLabel.setVisible(false);
      label = this.mOrangeLabel;
      int labelWidth = label.getWidth();
      int width = this.getWidth();
      int height = this.getHeight();
      label.setPosition(-(labelWidth + width), -(height / 2));
      this.mOrangeLabel.setParent(this);
   }

   @Override
   public boolean isInside(int x, int y) {
      return super.isInside(x, y) || this.mOrangeLabel.isInside(x, y);
   }

   @Override
   public void draw() {
      super.draw();
      HUDOrangeLabel label = this.mOrangeLabel;
      label.setHighlighted(this.isPressed());
      this.mOrangeLabel.draw();
      this.mOrangeLabel.drawDebug();
   }

   public void setOrangeLabelVisible(boolean visible) {
      this.mOrangeLabel.setVisible(visible);
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      this.mOrangeLabel.logicUpdate(delta);
   }
}
