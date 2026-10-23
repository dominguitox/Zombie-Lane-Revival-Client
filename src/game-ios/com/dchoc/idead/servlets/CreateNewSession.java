package com.dchoc.idead.servlets;

import com.dchoc.idead.GameEngine;
import com.dchoc.idead.Time;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.player.Profile;
import com.dchoc.idead.tracking.Event;
import com.dchoc.iphonewrappers.SettingsWrapper;
import com.dchoc.parser.IParser;
import com.dchoc.toolkit.HTTPResponse;
import com.dchoc.toolkit.Toolkit;
import zlport.ios.Ios;

public class CreateNewSession extends ServletRequestWithXml {
   public static final String ELEMENT_SESSION_ID = "sessionId";
   public static final String ELEMENT_USER_ID = "dcgId";
   public static final String EMPTY_UID = "";
   private static int smShowOfflineMode;

   public CreateNewSession() {
      super("CreateNewSession", false);
      this.setAddUserInfo(false);
      smShowOfflineMode = 0;
   }

   public void init() {
      this.clearParameters();
      String userId = null;
      Profile profile = PlayerProfile.getProfile();
      if (profile != null) {
         userId = profile.getUserID();
      }

      String uid = System.getProperty("zlport.account.id", Event.getUID());
      String password = zlport.launcher.PendingCredentials.takeAsString();
      String tz = Integer.toString((int)(Time.getTimeZone() / 3600000L));
      String language = Toolkit.getSelectedLanguageCode();
      String country = SettingsWrapper.getCountryCode();
      this.addParameter("pid", "3");
      this.addParameter("username", uid != null ? uid : "");
      this.addParameter("password", password != null ? password : "");
      this.addParameter("puid", uid != null ? uid : "");
      this.addParameter("token", uid != null ? uid : "");
      if (userId != null) {
         this.addParameter("dcgid", userId);
      }

      this.addParameter("tz", tz);
      this.addParameter("lang", language);
      if (country != null) {
         this.addParameter("country", country);
      }
   }

   @Override
   public void processResponse() {
      super.processResponse();
      if (this.mResponse.getResponseCode() != HTTPResponse.RESPONSE_CODE_OK && smShowOfflineMode == 0) {
         smShowOfflineMode = 1;
      }
   }

   @Override
   public void endElement(IParser parser, String tagName, String text) {
      if ("dcgId".equals(tagName)) {
         if (PlayerProfile.isInitialized()) {
            Profile profile = PlayerProfile.getProfile();
            if (profile.getUserID() == null) {
               Ios.platform().uploadRegistrationEvent(text);
            }

            profile.setUserID(text);
            SettingsWrapper.setUserID(text);
            GameEngine.getInstance().setGetAccountInformation();
         } else {
            this.debug("Failed to set player user ID!");
         }
      } else if ("sessionId".equals(tagName)) {
         GameEngine.getInstance().setSessionID(text);
      }
   }

   public void resetShowOfflineMode() {
      smShowOfflineMode = 0;
   }

   public static boolean showOfflineMode() {
      int val = smShowOfflineMode;
      if (val == 1) {
         smShowOfflineMode = -1;
      }

      return val == 1;
   }

   @Override
   public void debug(String message) {
      System.out.println("CreateNewSession: " + message);
   }
}
