package android.view;

import android.content.ContextWrapper;
import android.content.res.Resources;

public class ContextThemeWrapper extends ContextWrapper {
   private Resources mResources;

   public ContextThemeWrapper() {
      super(null);
   }

   public Resources getResources() {
      synchronized (this) {
         if (this.mResources == null) {
            this.mResources = new Resources(null, null, null);
         }

         return this.mResources;
      }
   }
}
