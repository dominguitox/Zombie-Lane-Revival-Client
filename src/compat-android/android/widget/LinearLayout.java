package android.widget;

import android.content.Context;
import android.view.ViewGroup;

public class LinearLayout extends ViewGroup {
   public LinearLayout(Context context) {
      super(context);
   }

   public void setGravity(int gravity) {
   }

   public void setOrientation(int orientation) {
   }

   public static class LayoutParams extends ViewGroup.MarginLayoutParams {
      public LayoutParams(int width, int height) {
         super(width, height);
      }
   }
}
