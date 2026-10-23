package com.dchoc.iphonewrappers;

import zlport.ios.Ios;

public class FileWrapper {
   public static final int DIR_HIERARCHY_IN_EXTERNAL_STORAGE = 1;
   public static final int FILE_HIERARCHY = 0;
   public static final String FILE_SEPARATOR = "--";
   public static final String FLAT_FILE_HIERARCHY_PREFIX_STR = "flat--";
   public static final int FLAT_HIERARCHY_IN_PRIVATE_APP_SPACE = 0;

   public static boolean fileExists(String name) {
      return Ios.platform().cacheFileExists(name);
   }

   public static void removeAllFiles() {
      Ios.platform().removeAllCacheFiles();
   }
}
