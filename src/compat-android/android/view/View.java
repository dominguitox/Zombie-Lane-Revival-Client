package android.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import zlport.stubs.NotPorted;

public class View {
   private final Context mContext;
   private ViewGroup.LayoutParams mLayoutParams;

   public View(Context context) {
      this.mContext = context;
   }

   public final Context getContext() {
      return this.mContext;
   }

   public final View findViewById(int id) {
      NotPorted.method("android.view.View#findViewById(int)");
      return null;
   }

   public IBinder getApplicationWindowToken() {
      NotPorted.method("android.view.View#getApplicationWindowToken()");
      return null;
   }

   public ViewGroup.LayoutParams getLayoutParams() {
      return this.mLayoutParams;
   }

   public IBinder getWindowToken() {
      NotPorted.method("android.view.View#getWindowToken()");
      return null;
   }

   public void setBackgroundDrawable(Drawable background) {
   }

   public void setLayoutParams(ViewGroup.LayoutParams params) {
      this.mLayoutParams = params;
   }

   public void setOnClickListener(View.OnClickListener l) {
   }

   public void setOnKeyListener(View.OnKeyListener l) {
   }

   public interface OnClickListener {
      void onClick(View var1);
   }

   public interface OnKeyListener {
      boolean onKey(View var1, int var2, KeyEvent var3);
   }
}
