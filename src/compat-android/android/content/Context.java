package android.content;

import android.content.pm.PackageManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public abstract class Context {
   public abstract boolean deleteFile(String var1);

   public abstract ContentResolver getContentResolver();

   public abstract File getDatabasePath(String var1);

   public abstract File getFileStreamPath(String var1);

   public abstract PackageManager getPackageManager();

   public abstract String getPackageName();

   public abstract SharedPreferences getSharedPreferences(String var1, int var2);

   public abstract Object getSystemService(String var1);

   public abstract FileInputStream openFileInput(String var1) throws FileNotFoundException;

   public abstract FileOutputStream openFileOutput(String var1, int var2) throws FileNotFoundException;

   public abstract void startActivity(Intent var1);
}
