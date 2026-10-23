package android.content.pm;

import android.util.AndroidException;

public abstract class PackageManager {
   public abstract PackageInfo getPackageInfo(String var1, int var2) throws PackageManager.NameNotFoundException;

   public static class NameNotFoundException extends AndroidException {
      public NameNotFoundException() {
      }

      public NameNotFoundException(String name) {
         super(name);
      }
   }
}
