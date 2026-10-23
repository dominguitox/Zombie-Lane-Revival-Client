package android.graphics.drawable;

import android.graphics.Rect;

public abstract class Drawable {
   private Rect mBounds = new Rect(0, 0, 0, 0);

   public abstract void setAlpha(int var1);

   public final Rect getBounds() {
      return this.mBounds;
   }

   public void setBounds(Rect bounds) {
      this.mBounds = bounds;
   }
}
