package android.widget;

import android.content.Context;
import android.view.ViewGroup;
import zlport.stubs.NotPorted;

public class RelativeLayout extends ViewGroup {
   public RelativeLayout(Context context) {
      super((Context)null);
      NotPorted.method("android.widget.RelativeLayout#<init>(Context)");
   }

   public void setGravity(int gravity) {
      NotPorted.method("android.widget.RelativeLayout#setGravity(int)");
   }

   public static class LayoutParams extends ViewGroup.MarginLayoutParams {
      public LayoutParams(int w, int h) {
         super((ViewGroup.LayoutParams)null);
         NotPorted.method("android.widget.RelativeLayout$LayoutParams#<init>(int,int)");
      }
   }
}
