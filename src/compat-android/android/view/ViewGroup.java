package android.view;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import zlport.stubs.NotPorted;

public abstract class ViewGroup extends View {
   private final List<View> mChildren = new ArrayList<>();

   public ViewGroup(Context context) {
      super(context);
   }

   public void addView(View child) {
      NotPorted.method("android.view.ViewGroup#addView(View)");
   }

   public void addView(View child, ViewGroup.LayoutParams params) {
      child.setLayoutParams(params);
      this.mChildren.add(child);
   }

   public int getChildCount() {
      return this.mChildren.size();
   }

   public View getChildAt(int index) {
      return index >= 0 && index < this.mChildren.size() ? this.mChildren.get(index) : null;
   }

   public static class LayoutParams {
      public int width;
      public int height;

      public LayoutParams(ViewGroup.LayoutParams source) {
         if (source != null) {
            this.width = source.width;
            this.height = source.height;
         }
      }

      public LayoutParams(int width, int height) {
         this.width = width;
         this.height = height;
      }
   }

   public static class MarginLayoutParams extends ViewGroup.LayoutParams {
      public int leftMargin;
      public int topMargin;

      public MarginLayoutParams(ViewGroup.LayoutParams source) {
         super(source);
      }

      public MarginLayoutParams(int width, int height) {
         super(width, height);
      }
   }
}
