package android.content;

public interface DialogInterface {
   int BUTTON_POSITIVE = -1;
   int BUTTON_NEGATIVE = -2;

   void cancel();

   void dismiss();

   interface OnClickListener {
      void onClick(DialogInterface var1, int var2);
   }
}
