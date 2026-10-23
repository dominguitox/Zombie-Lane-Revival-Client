package com.dchoc.iphonewrappers;

import com.dchoc.toolkit.Toolkit;
import java.io.File;
import zlport.ios.Ios;

public class DeviceWrapper {
   public static final boolean LOG_ASSET_LOADING = true;
   public static final boolean LOG_PACKAGE_LOADING = true;
   public static final boolean LOG_SCREEN_DIMENSIONS = false;
   public static final boolean LOG_TEXTURE_LOADING = false;
   public static final boolean TEST_TEXTURES = false;
   public static final boolean TEST_TEXTURE_LOADING_AND_UNLOADING = false;
   public static final int UDID_LENGTH = 40;
   private static final String[] JAILBROKEN_APP_PATHS = new String[]{
      "/bin/bash",
      "/Applications/Cydia.app",
      "/Applications/limera1n.app",
      "/Applications/greenpois0n.app",
      "/Applications/blackra1n.app",
      "/Applications/blacksn0w.app",
      "/Applications/redsn0w.app"
   };
   private static boolean smSupportVga;
   private static boolean smInitialized;
   private static boolean smIPad1;
   private static boolean smIPod4;
   private static int smGfxProfile = -1;
   private static String advId;

   public static void init() {
      smIPod4 = false;
      smIPad1 = false;
      if (Toolkit.getScreenWidth() == 960 || Toolkit.getScreenHeight() == 960) {
         smIPod4 = !Ios.platform().canOpenUrl("tel://");
      }

      if (Toolkit.getScreenWidth() == 1024 || Toolkit.getScreenHeight() == 1024 || Toolkit.getScreenWidth() == 2048 || Toolkit.getScreenHeight() == 2048) {
         smIPad1 = !Ios.platform().isImageSourceAvailable(1);
      }

      smInitialized = true;
   }

   public static boolean isIPHONE4() {
      return Toolkit.getScreenWidth() == 960 || Toolkit.getScreenHeight() == 960;
   }

   public static boolean isIPAD() {
      return Toolkit.getScreenWidth() == 1024 || Toolkit.getScreenHeight() == 1024 || Toolkit.getScreenWidth() == 2048 || Toolkit.getScreenHeight() == 2048;
   }

   public static boolean isIPHONE5() {
      return Toolkit.getScreenWidth() == 1136;
   }

   public static boolean isIPOD4() {
      if (!smInitialized) {
         init();
      }

      return smIPod4;
   }

   public static boolean isIPAD1() {
      if (!smInitialized) {
         init();
      }

      return smIPad1;
   }

   public static boolean isVGA() {
      return smSupportVga && Toolkit.getScreenWidth() > 799 && Toolkit.getScreenWidth() < 1024;
   }

   public static boolean useHDGraphics() {
      return Toolkit.getScreenWidth() > 959;
   }

   public static boolean useHDIngameGraphics() {
      return useHDGraphics() && !isIPAD1() && !isIPOD4();
   }

   public static int getGfxProfile() {
      if (smGfxProfile == -1) {
         smGfxProfile = useHDIngameGraphics() ? 2 : 1;
      }

      return smGfxProfile;
   }

   public static int isJailBroken() {
      for (String path : JAILBROKEN_APP_PATHS) {
         if (new File(path).exists()) {
            return 1;
         }
      }

      return 0;
   }

   public static boolean isIOS3() {
      return Ios.platform().compareSystemVersion("4.0", 64) == -1;
   }

   public static void checkVGASupport() {
      if (containsTextInList("null", "vga") || containsTextInList("ipad", "vga")) {
         smSupportVga = true;
      }
   }

   private static boolean containsTextInList(String text, String target) {
      if (text == null) {
         return false;
      }

      String item = "";

      for (int i = 0; i < text.length(); i++) {
         char character = text.charAt(i);
         if (character == ',') {
            if (item.equals(target)) {
               return true;
            }

            item = "";
         } else {
            item = item + character;
         }
      }

      return item.equals(target);
   }

   public static String getAdvertisingId() {
      String stored = Ios.platform().preference("AdvertisingId");
      String identifier;
      if (stored == null) {
         identifier = Ios.platform().advertisingIdentifier();
         Ios.platform().setPreference(identifier, "AdvertisingId");
         Ios.platform().synchronizePreferences();
      } else {
         identifier = Ios.platform().preference("AdvertisingId");
      }

      advId = identifier;
      return advId;
   }
}
