package android.app;

import android.content.Context;
import zlport.stubs.NotPorted;

public class ProgressDialog extends AlertDialog {
   private CharSequence mMessage;

   public ProgressDialog(Context context) {
      super(context);
   }

   public void setMessage(CharSequence message) {
      this.mMessage = message;
   }

   @Override
   String describeContent() {
      return this.mMessage == null ? super.describeContent() : " \"" + this.mMessage + "\"";
   }

   public void setProgress(int value) {
      NotPorted.method("android.app.ProgressDialog#setProgress(int)");
   }

   public void setProgressStyle(int style) {
      NotPorted.method("android.app.ProgressDialog#setProgressStyle(int)");
   }

   public static ProgressDialog show(Context context, CharSequence title, CharSequence message, boolean indeterminate) {
      NotPorted.method("android.app.ProgressDialog#show(Context,CharSequence,CharSequence,boolean)");
      return null;
   }
}
