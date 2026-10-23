package android.app;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import zlport.stubs.NotPorted;

public class Dialog implements DialogInterface {
   private static final Logger LOG = System.getLogger("zlport.android.ui");
   private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();
   private final Context mContext;
   private View mContentView;
   private boolean mCancelable = true;
   private boolean mShowing;

   public Dialog(Context context) {
      this.mContext = context;
   }

   public Dialog(Context context, int themeResId) {
      this.mContext = context;
   }

   public final Context getContext() {
      return this.mContext;
   }

   @Override
   public void cancel() {
      NotPorted.method("android.app.Dialog#cancel()");
   }

   @Override
   public void dismiss() {
      this.mShowing = false;
   }

   public boolean isShowing() {
      return this.mShowing;
   }

   void setShowing(boolean showing) {
      this.mShowing = showing;
   }

   public final boolean requestWindowFeature(int featureId) {
      return true;
   }

   public void setCancelable(boolean flag) {
      this.mCancelable = flag;
   }

   boolean isCancelable() {
      return this.mCancelable;
   }

   public void setContentView(View view) {
      this.mContentView = view;
   }

   public void show() {
      this.mShowing = true;
      String what = this.describe();
      if (REPORTED.add(what)) {
         LOG.log(Level.INFO, "diálogo nativo não exibido no PC (FASE-03 E5): " + what);
      }
   }

   String describe() {
      return this.getClass().getSimpleName() + this.describeContent() + (this.mCancelable ? "" : " (não cancelável)");
   }

   String describeContent() {
      List<String> parts = new ArrayList<>();
      collect(this.mContentView, parts);
      return parts.isEmpty() ? "" : " " + parts;
   }

   private static void collect(View view, List<String> parts) {
      if (view instanceof EditText) {
         parts.add("campo de texto");
      } else if (view instanceof TextView t && !t.getText().toString().isBlank()) {
         parts.add("\"" + t.getText().toString().strip() + "\"");
      }

      if (view instanceof ViewGroup g) {
         for (int i = 0; i < g.getChildCount(); i++) {
            collect(g.getChildAt(i), parts);
         }
      }
   }
}
