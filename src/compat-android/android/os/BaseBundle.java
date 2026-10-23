package android.os;

import zlport.stubs.NotPorted;

public class BaseBundle {
   BaseBundle() {
      NotPorted.method("android.os.BaseBundle#<init>()");
   }

   public int getInt(String key) {
      NotPorted.method("android.os.BaseBundle#getInt(String)");
      return 0;
   }

   public long getLong(String key, long defaultValue) {
      NotPorted.method("android.os.BaseBundle#getLong(String,long)");
      return 0L;
   }

   public String getString(String key) {
      NotPorted.method("android.os.BaseBundle#getString(String)");
      return null;
   }

   public void putInt(String key, int value) {
      NotPorted.method("android.os.BaseBundle#putInt(String,int)");
   }
}
