package android.app;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.List;
import zlport.android.ui.DialogPresenter;
import zlport.android.ui.Dialogs;
import zlport.stubs.NotPorted;

public class AlertDialog extends Dialog implements DialogInterface {
   static final int ANDROID_STRING_OK = 17039370;
   private CharSequence mTitle;
   private CharSequence mMessage;
   private CharSequence mPositiveText;
   private CharSequence mNegativeText;
   private DialogInterface.OnClickListener mPositive;
   private DialogInterface.OnClickListener mNegative;

   protected AlertDialog(Context context) {
      super(context);
   }

   public void setButton(CharSequence text, DialogInterface.OnClickListener listener) {
      NotPorted.method("android.app.AlertDialog#setButton(CharSequence,OnClickListener)");
   }

   @Override
   public void show() {
      DialogPresenter presenter = Dialogs.presenter();
      if (presenter != null && (this.mPositiveText != null || this.mNegativeText != null)) {
         this.setShowing(true);
         Handler ui = new Handler(Looper.getMainLooper());
         presenter.show(
            new DialogPresenter.Box(text(this.mTitle), text(this.mMessage), text(this.mPositiveText), text(this.mNegativeText), this.isCancelable()),
            which -> ui.post(() -> this.click(which))
         );
      } else {
         super.show();
      }
   }

   void click(int which) {
      if (this.isShowing()) {
         DialogInterface.OnClickListener listener = which == -1 ? this.mPositive : (which == -2 ? this.mNegative : null);
         if (listener != null) {
            listener.onClick(this, which);
         }

         this.dismiss();
      }
   }

   @Override
   String describeContent() {
      if (this.mTitle == null && this.mMessage == null && this.mPositiveText == null && this.mNegativeText == null) {
         return super.describeContent();
      }

      StringBuilder s = new StringBuilder();
      if (this.mTitle != null) {
         s.append(" \"").append(this.mTitle).append('"');
      }

      if (this.mMessage != null) {
         s.append(" \"").append(this.mMessage.toString().strip()).append('"');
      }

      List<String> buttons = new ArrayList<>();
      if (this.mPositiveText != null) {
         buttons.add(this.mPositiveText.toString());
      }

      if (this.mNegativeText != null) {
         buttons.add(this.mNegativeText.toString());
      }

      return buttons.isEmpty() ? s.toString() : s + " " + buttons;
   }

   private static String text(CharSequence c) {
      return c == null ? null : c.toString();
   }

   public static class Builder {
      private final Context mContext;
      private CharSequence mTitle;
      private CharSequence mMessage;
      private CharSequence mPositiveText;
      private CharSequence mNegativeText;
      private DialogInterface.OnClickListener mPositive;
      private DialogInterface.OnClickListener mNegative;
      private boolean mCancelable = true;

      public Builder(Context context) {
         this.mContext = context;
      }

      public AlertDialog create() {
         AlertDialog d = new AlertDialog(this.mContext);
         d.mTitle = this.mTitle;
         d.mMessage = this.mMessage;
         d.mPositiveText = this.mPositiveText;
         d.mNegativeText = this.mNegativeText;
         d.mPositive = this.mPositive;
         d.mNegative = this.mNegative;
         d.setCancelable(this.mCancelable);
         return d;
      }

      public AlertDialog.Builder setCancelable(boolean cancelable) {
         this.mCancelable = cancelable;
         return this;
      }

      public AlertDialog.Builder setMessage(CharSequence message) {
         this.mMessage = message;
         return this;
      }

      public AlertDialog.Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
         this.mNegativeText = text;
         this.mNegative = listener;
         return this;
      }

      public AlertDialog.Builder setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
         if (textId == 17039370) {
            this.mPositiveText = "OK";
         } else {
            NotPorted.method("android.app.AlertDialog$Builder#setPositiveButton(int,OnClickListener) com o recurso " + textId);
            this.mPositiveText = String.valueOf(textId);
         }

         this.mPositive = listener;
         return this;
      }

      public AlertDialog.Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
         this.mPositiveText = text;
         this.mPositive = listener;
         return this;
      }

      public AlertDialog.Builder setTitle(CharSequence title) {
         this.mTitle = title;
         return this;
      }

      public AlertDialog show() {
         AlertDialog d = this.create();
         d.show();
         return d;
      }
   }
}
