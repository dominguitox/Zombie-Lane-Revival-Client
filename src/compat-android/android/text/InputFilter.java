package android.text;

import zlport.stubs.NotPorted;

public interface InputFilter {
   CharSequence filter(CharSequence var1, int var2, int var3, Spanned var4, int var5, int var6);

   class LengthFilter implements InputFilter {
      private final int mMax;

      public LengthFilter(int max) {
         this.mMax = max;
      }

      public int getMax() {
         return this.mMax;
      }

      @Override
      public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
         NotPorted.method("android.text.InputFilter$LengthFilter#filter(CharSequence,int,int,Spanned,int,int)");
         return null;
      }
   }
}
