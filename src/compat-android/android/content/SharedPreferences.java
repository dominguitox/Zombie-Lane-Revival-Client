package android.content;

import java.util.Map;

public interface SharedPreferences {
   SharedPreferences.Editor edit();

   Map getAll();

   boolean getBoolean(String var1, boolean var2);

   int getInt(String var1, int var2);

   long getLong(String var1, long var2);

   String getString(String var1, String var2);

   interface Editor {
      boolean commit();

      SharedPreferences.Editor putBoolean(String var1, boolean var2);

      SharedPreferences.Editor putInt(String var1, int var2);

      SharedPreferences.Editor putLong(String var1, long var2);

      SharedPreferences.Editor putString(String var1, String var2);
   }
}
