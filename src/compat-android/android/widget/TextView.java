package android.widget;

import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import zlport.stubs.NotPorted;

public class TextView extends View {
   private CharSequence mText = "";

   public TextView(Context context) {
      super(context);
   }

   public CharSequence getText() {
      return this.mText;
   }

   public void setFilters(InputFilter[] filters) {
   }

   public void setGravity(int gravity) {
   }

   public void setInputType(int type) {
      NotPorted.method("android.widget.TextView#setInputType(int)");
   }

   public void setMaxLines(int maxLines) {
   }

   public void setMaxWidth(int maxPixels) {
   }

   public void setMinWidth(int minPixels) {
   }

   public final void setText(CharSequence text) {
      this.mText = text == null ? "" : text;
   }

   public void setTextColor(int color) {
   }

   public void setTextSize(float size) {
   }
}
