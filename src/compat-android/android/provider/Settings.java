package android.provider;

import android.content.ContentResolver;
import zlport.stubs.NotPorted;

public final class Settings {
   public Settings() {
      NotPorted.method("android.provider.Settings#<init>()");
   }

   public static class NameValueTable {
      public NameValueTable() {
         NotPorted.method("android.provider.Settings$NameValueTable#<init>()");
      }
   }

   public static final class System extends Settings.NameValueTable {
      public System() {
         NotPorted.method("android.provider.Settings$System#<init>()");
      }

      public static int getInt(ContentResolver cr, String name, int def) {
         NotPorted.method("android.provider.Settings$System#getInt(ContentResolver,String,int)");
         return 0;
      }
   }
}
