package android.content;

import android.content.pm.PackageManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ContextWrapper extends Context {
   private Context mBase;

   public ContextWrapper(Context base) {
      this.mBase = base;
   }

   protected void attachBaseContext(Context base) {
      if (this.mBase != null) {
         throw new IllegalStateException("Base context already set");
      }

      this.mBase = base;
   }

   public Context getBaseContext() {
      return this.mBase;
   }

   @Override
   public boolean deleteFile(String name) {
      return this.mBase.deleteFile(name);
   }

   @Override
   public ContentResolver getContentResolver() {
      return this.mBase.getContentResolver();
   }

   @Override
   public File getDatabasePath(String name) {
      return this.mBase.getDatabasePath(name);
   }

   @Override
   public File getFileStreamPath(String name) {
      return this.mBase.getFileStreamPath(name);
   }

   @Override
   public PackageManager getPackageManager() {
      return this.mBase.getPackageManager();
   }

   @Override
   public String getPackageName() {
      return this.mBase.getPackageName();
   }

   @Override
   public SharedPreferences getSharedPreferences(String name, int mode) {
      return this.mBase.getSharedPreferences(name, mode);
   }

   @Override
   public Object getSystemService(String name) {
      return this.mBase.getSystemService(name);
   }

   @Override
   public FileInputStream openFileInput(String name) throws FileNotFoundException {
      return this.mBase.openFileInput(name);
   }

   @Override
   public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
      return this.mBase.openFileOutput(name, mode);
   }

   @Override
   public void startActivity(Intent intent) {
      this.mBase.startActivity(intent);
   }
}
