package android.view.inputmethod;

import android.os.IBinder;
import android.view.View;
import zlport.stubs.NotPorted;

public final class InputMethodManager {
   InputMethodManager() {
      NotPorted.method("android.view.inputmethod.InputMethodManager#<init>()");
   }

   public boolean hideSoftInputFromWindow(IBinder windowToken, int flags) {
      NotPorted.method("android.view.inputmethod.InputMethodManager#hideSoftInputFromWindow(IBinder,int)");
      return false;
   }

   public boolean showSoftInput(View view, int flags) {
      NotPorted.method("android.view.inputmethod.InputMethodManager#showSoftInput(View,int)");
      return false;
   }
}
