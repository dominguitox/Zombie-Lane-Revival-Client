package zlport.android;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.System.Logger.Level;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import zlport.stubs.NotPorted;

public final class AppContext extends Context {
   public static final String PACKAGE_NAME = "com.zombielane.dchoconline";
   public static final String VERSION_NAME = "1.0.01";
   private final Path dataDir;
   private final Map<String, FileSharedPreferences> preferences = new HashMap<>();
   private final Map<String, Object> services = new HashMap<>();
   private final PackageManager packageManager = new AppContext.AppPackageManager();

   public AppContext(Path dataDir) {
      this.dataDir = dataDir.toAbsolutePath();
   }

   public Path dataDir() {
      return this.dataDir;
   }

   public File getFilesDir() {
      File dir = this.dataDir.resolve("files").toFile();
      if (!dir.isDirectory() && !dir.mkdirs() && !dir.isDirectory()) {
         System.getLogger("zlport.android").log(Level.WARNING, "não foi possível criar o diretório de arquivos " + dir);
      }

      return dir;
   }

   @Override
   public File getDatabasePath(String name) {
      if (name.indexOf(47) < 0 && name.indexOf(92) < 0) {
         return this.dataDir.resolve("databases").resolve(name).toFile();
      } else {
         throw new IllegalArgumentException("File " + name + " contains a path separator");
      }
   }

   @Override
   public File getFileStreamPath(String name) {
      if (name.indexOf(47) < 0 && name.indexOf(92) < 0) {
         return new File(this.getFilesDir(), name);
      } else {
         throw new IllegalArgumentException("File " + name + " contains a path separator");
      }
   }

   @Override
   public FileInputStream openFileInput(String name) throws FileNotFoundException {
      return new FileInputStream(this.getFileStreamPath(name));
   }

   @Override
   public PackageManager getPackageManager() {
      return this.packageManager;
   }

   @Override
   public String getPackageName() {
      return "com.zombielane.dchoconline";
   }

   @Override
   public SharedPreferences getSharedPreferences(String name, int mode) {
      if (name.indexOf(47) < 0 && name.indexOf(92) < 0) {
         synchronized (this.preferences) {
            return this.preferences.computeIfAbsent(name, n -> new FileSharedPreferences(this.dataDir.resolve("shared_prefs").resolve(n + ".properties")));
         }
      } else {
         throw new IllegalArgumentException("File " + name + " contains a path separator");
      }
   }

   @Override
   public Object getSystemService(String name) {
      synchronized (this.services) {
         return this.services.computeIfAbsent(name, n -> {
            return switch (n) {
               case "phone" -> new TelephonyManager();
               case "wifi" -> new WifiManager();
               case "audio" -> new AudioManager();
               case "connectivity" -> new ConnectivityManager();
               default -> null;
            };
         });
      }
   }

   @Override
   public boolean deleteFile(String name) {
      NotPorted.method("android.content.Context#deleteFile(String)");
      return false;
   }

   @Override
   public ContentResolver getContentResolver() {
      NotPorted.method("android.content.Context#getContentResolver()");
      return null;
   }

   @Override
   public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
      NotPorted.method("android.content.Context#openFileOutput(String,int)");
      return null;
   }

   @Override
   public void startActivity(Intent intent) {
      NotPorted.method("android.content.Context#startActivity(Intent)");
   }

   private static final class AppPackageManager extends PackageManager {
      @Override
      public PackageInfo getPackageInfo(String name, int flags) throws PackageManager.NameNotFoundException {
         if (!"com.zombielane.dchoconline".equals(name)) {
            throw new PackageManager.NameNotFoundException(name);
         }

         PackageInfo info = new PackageInfo();
         info.versionName = "1.0.01";
         return info;
      }
   }
}
