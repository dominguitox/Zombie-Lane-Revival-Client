package com.dchoc.iphonewrappers;

import com.dchoc.toolkit.ToolkitHelpers;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.midlet.MIDlet;
import zlport.ios.Ios;

public class SettingsWrapper {
   public static final String ANDROID_PLATFORM = "4";
   public static final boolean ASSET_SERVER_FETCH_ZIPPED_PACKAGES_ON_STARTUP = true;
   public static final int ENV_DEV = 0;
   public static final int ENV_AWS = 1;
   public static final int ENV_PROD = 2;
   public static final int INVALID_VERSION = -1;
   private static String smAssetServerURL;
   private static int smEnvironment;
   private static String smServerURL;
   private static String smVersion;

   public static void init() {
      smEnvironment = 0;
      smServerURL = "http://idead-stage.dchoc.com:8080/dead/";
      smAssetServerURL = "http://dcassets2-a.cdngc.net/idead/";
      smVersion = "1_4_3";
      String type = Ios.platform().infoPlistString("ServerType");
      if ("AWS".equals(type)) {
         smServerURL = "http://idead-stage.dchoc.com:8080/dead/";
         smAssetServerURL = "http://dcassets2-a.cdngc.net/idead/";
         smEnvironment = 1;
      } else {
         type = Ios.platform().infoPlistString("ServerType");
         if ("PROD".equals(type)) {
            smServerURL = "http://idead.dchoc.com/dead/";
            smAssetServerURL = "http://dcassets2-a.cdngc.net/idead/";
            smEnvironment = 2;
         }
      }

      boolean test = Ios.platform().infoPlistBoolean("ServerAssetsTestEnvironment");
      if (test) {
         smVersion = "Stage/" + Ios.platform().infoPlistString("ServerAssetVersionStage");
      } else {
         smVersion = Ios.platform().infoPlistString("ServerAssetVersion");
      }

      String version = Ios.platform().infoPlistString("CFBundleVersion");
      Ios.platform().setPreference(version, "version");
      Ios.platform().synchronizePreferences();
      if (smEnvironment == 1 || smEnvironment == 2) {
         smAssetServerURL = "http://dcassets2-a.cdngc.net/idead/" + smVersion + "/";
      }

      smServerURL = System.getProperty("zlport.server.url", "http://127.0.0.1:6767/dead/");
      if ((!smServerURL.startsWith("http://") && !smServerURL.startsWith("https://")) || !smServerURL.endsWith("/")) {
         throw new IllegalArgumentException("zlport.server.url must be an HTTP(S) base URL ending in /");
      }
   }

   public static String getVersion() {
      return smVersion;
   }

   public static String getServerURL() {
      return smServerURL;
   }

   public static String getAssetServerURL() {
      return smAssetServerURL;
   }

   public static int getEnvironment() {
      return smEnvironment;
   }

   public static int parseVersion(String text) {
      if (text != null) {
         StringBuffer digits = new StringBuffer(3);

         for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (Character.isDigit((byte)character)) {
               digits.append(character);
            }
         }

         if (digits.length() != 0) {
            return ToolkitHelpers.parseInt(digits.toString(), -1);
         }
      }

      return -1;
   }

   public static void openAppStore() throws ConnectionNotFoundException {
      String url = Ios.platform().infoPlistString("AppStoreUrl");
      MIDlet midlet = MIDlet.getMIDletInstance();
      midlet.platformRequest(url);
   }

   public static void copyText(String text) {
      if (text != null) {
         Ios.platform().copyText(text);
      }
   }

   public static void setUserID(String userId) {
      Ios.platform().setPreference(userId, "userID");
      Ios.platform().synchronizePreferences();
   }

   public static String getCountryCode() {
      return Ios.platform().countryCode();
   }
}
