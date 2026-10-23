package com.dchoc.idead.tracking;

import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.player.Profile;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.iphonewrappers.SettingsWrapper;
import com.dchoc.toolkit.CsvIDs;
import java.util.LinkedHashMap;
import java.util.Map;
import zlport.ios.Ios;

public class Event {
   public static final int CRM_APP_ID = 45;
   public static final int CRM_P_ID = 4;
   public static final int PARAMS_COUNT = 36;
   public static final int PARAM_1 = 24;
   public static final int PARAM_2 = 25;
   public static final int PARAM_3 = 26;
   public static final int PARAM_4 = 27;
   public static final int PARAM_5 = 28;
   public static final int PARAM_APP_FRIENDS = 20;
   public static final int PARAM_APP_ID = 3;
   public static final int PARAM_APP_NEIGHBORS = 21;
   public static final int PARAM_APP_UID = 4;
   public static final int PARAM_CLIENT_IP = 16;
   public static final int PARAM_ENV = 0;
   public static final int PARAM_FRIENDS = 19;
   public static final int PARAM_GC_BALANCE = 31;
   public static final int PARAM_GC_DELTA = 30;
   public static final int PARAM_GROUP = 7;
   public static final int PARAM_IS_FAN = 22;
   public static final int PARAM_LEVEL = 5;
   public static final int PARAM_PC_BALANCE = 33;
   public static final int PARAM_PC_DELTA = 32;
   public static final int PARAM_PRODUCT = 17;
   public static final int PARAM_PRODUCT_DETAIL = 18;
   public static final int PARAM_P_ID = 1;
   public static final int PARAM_P_UID = 2;
   public static final int PARAM_RC_DELTA = 34;
   public static final int PARAM_RECIPIENT_P_UIDS = 35;
   public static final int PARAM_REF = 11;
   public static final int PARAM_REF_APP_ID = 14;
   public static final int PARAM_REF_APP_UID = 15;
   public static final int PARAM_REF_P_ID = 12;
   public static final int PARAM_REF_P_UID = 13;
   public static final int PARAM_SESSION_COUNT = 6;
   public static final int PARAM_SRC = 10;
   public static final int PARAM_TYPE = 8;
   public static final int PARAM_USER_ACTION = 9;
   public static final int PARAM_VALUE = 23;
   public static final int PARAM_VERSION = 29;
   private static final String[] smParameterNames = new String[]{
      "env",
      "p_id",
      "p_uid",
      "app_id",
      "app_uid",
      "level",
      "session_count",
      "group",
      "type",
      "user_action",
      "src",
      "ref",
      "ref_p_id",
      "ref_p_uid",
      "ref_app_id",
      "ref_app_uid",
      "client_ip",
      "product",
      "product_detail",
      "friends",
      "app_friends",
      "app_neighbors",
      "is_fan",
      "value",
      "param_1",
      "param_2",
      "param_3",
      "param_4",
      "param_5",
      "version",
      "game_currency_delta",
      "game_currency_balance",
      "paid_currency_delta",
      "paid_currency_balance",
      "real_currency_delta",
      "recipient_p_uids"
   };
   static final String[] smProductValues = new String[]{"Success", "Failure", "Exists"};
   public static int smCrmEnv = 0;
   private static boolean smInitialized;
   private static String smUDID;
   private static TrackingParameter smAmountOfNeighbors;
   private static TrackingParameter smAppIdParameter;
   private static TrackingParameter smAppUIdPatameter;
   private static TrackingParameter smEnvParameter;
   private static TrackingParameter smLevelParameter;
   private static TrackingParameter smPIdParameter;
   private static TrackingParameter smSessionCountParameter;
   private String mGroup;
   private String mName;
   private TrackingParameter[] mParameters = new TrackingParameter[36];
   private String mType;

   public Event(int group, int type, int action) {
      this.setGroup(group);
      this.setType(type);
      if (action != -1) {
         this.setAction(action);
      }

      getRequiredParameters(this.mParameters);
   }

   public static void close() {
      if (smInitialized) {
      }
   }

   public void addJailbrokenInfo() {
      Profile profile = PlayerProfile.getProfile();
      if (profile != null) {
         int version = profile.getVersion();
         String userID = profile.getUserID();
         if (version != -1 && userID != null) {
            if (DeviceWrapper.isJailBroken() == 1) {
               this.setParameter(25, TrackingEvents.getUserAction(56));
            }

            this.setParameter(26, version);
         }
      }
   }

   public void clearParameters() {
      for (int i = 0; i < this.mParameters.length; i++) {
         if (this.mParameters[i] != null) {
            this.mParameters[i].clear();
         }
      }
   }

   public String getAction() {
      return this.mName;
   }

   public String getGroup() {
      return this.mGroup;
   }

   public String getParamName(int index) {
      return smParameterNames[index];
   }

   public String getParamsString() {
      String str = "Event:";

      for (int i = 0; i < this.mParameters.length; i++) {
         TrackingParameter parameter = this.mParameters[i];
         if (parameter != null && parameter.getDataType() != -1) {
            String head = str + parameter.getName() + ":";
            if (parameter.getDataType() == 0) {
               str = head + parameter.getIntValue() + ", ";
            } else {
               str = head + parameter.getStringValue() + ", ";
            }
         }
      }

      return str;
   }

   public static String[] getParameterNames() {
      return smParameterNames;
   }

   public static void getRequiredParameters(TrackingParameter[] parameters) {
      if (smLevelParameter == null) {
         smEnvParameter = new TrackingParameter(smParameterNames[0], smCrmEnv);
         smPIdParameter = new TrackingParameter(smParameterNames[1], 4);
         smAppIdParameter = new TrackingParameter(smParameterNames[3], 45);
         Profile profile = PlayerProfile.getProfile();
         String uid = profile == null ? getUID() : profile.getUserID();
         smAppUIdPatameter = new TrackingParameter(smParameterNames[4], uid);
         smLevelParameter = new TrackingParameter(smParameterNames[5], 0);
         smSessionCountParameter = new TrackingParameter(smParameterNames[6], PlayerProfile.getSessionCount());
         smAmountOfNeighbors = new TrackingParameter(smParameterNames[21], PlayerProfile.getAcceptedNeighborCount());
      }

      updateFixedParameters();
      parameters[0] = smEnvParameter;
      parameters[1] = smPIdParameter;
      parameters[3] = smAppIdParameter;
      parameters[4] = smAppUIdPatameter;
      parameters[5] = smLevelParameter;
      parameters[6] = smSessionCountParameter;
      parameters[21] = smAmountOfNeighbors;
   }

   public String getType() {
      return this.mType;
   }

   public static String getUID() {
      if (smInitialized && smUDID == null) {
         String id = DeviceWrapper.getAdvertisingId();
         if (id == null) {
            return smUDID;
         }

         smUDID = id;
      }

      return smUDID;
   }

   public static void initialize() {
      if (!smInitialized) {
         smCrmEnv = SettingsWrapper.getEnvironment();
         initializeCustomizer();
         smInitialized = true;
      }
   }

   private static void initializeCustomizer() {
      Ios.platform().initializeCustomizer();
   }

   public void removeParameter(int index) {
      this.mParameters[index] = null;
   }

   public void setAction(int action) {
      this.mName = TrackingEvents.getUserAction(action);
      this.setParameter(9, this.mName);
   }

   public void setGroup(int group) {
      this.mGroup = TrackingEvents.getEventGroup(group);
      this.setParameter(7, this.mGroup);
   }

   public void setParameter(int index, int value) {
      this.mParameters[index] = new TrackingParameter(smParameterNames[index], value);
   }

   public void setParameter(int index, String value) {
      this.mParameters[index] = new TrackingParameter(smParameterNames[index], value);
   }

   public void setParameterCSV(int index, int csvId) {
      if (csvId >= 0 && csvId < CsvIDs.CSV_ID_NAMES.length) {
         this.mParameters[index] = new TrackingParameter(smParameterNames[index], CsvIDs.CSV_ID_NAMES[csvId]);
      } else if (csvId != -1) {
         this.mParameters[index] = new TrackingParameter(smParameterNames[index], Integer.toString(csvId));
      }
   }

   public void setType(int type) {
      this.mType = TrackingEvents.getEventType(type);
      this.setParameter(8, this.mType);
   }

   public void trackEvent() {
      this.trackEvent(true);
   }

   public void trackEvent(boolean apsalar) {
      updateFixedParameters();
      this.addJailbrokenInfo();
      if (apsalar) {
         ApsalarManager.trackEvent(this.mParameters);
      }
      this.logPlayerAction();
   }

   /** Copia el evento al registro de acciones de la alpha (sin contraseñas). */
   private void logPlayerAction() {
      try {
         Map<String, String> details = new LinkedHashMap<>();
         details.put("event", "tracking");
         if (this.mGroup != null && !this.mGroup.isEmpty()) {
            details.put("group", this.mGroup);
         }
         if (this.mType != null && !this.mType.isEmpty()) {
            details.put("type", this.mType);
         }
         if (this.mName != null && !this.mName.isEmpty()) {
            details.put("action", this.mName);
         }
         for (int index = 0; index < this.mParameters.length; index++) {
            TrackingParameter parameter = this.mParameters[index];
            if (parameter == null || parameter.getDataType() == -1) {
               continue;
            }
            String value = parameter.getDataType() == 0
               ? Integer.toString(parameter.getIntValue())
               : parameter.getStringValue();
            if (value != null && !value.isEmpty()) {
               details.put(smParameterNames[index], value);
            }
         }
         Ios.platform().logPlayerAction(details);
      } catch (IllegalStateException ignored) {
         // El tracking empieza antes de que el runtime esté instalado.
      }
   }

   public static void updateFixedParameters() {
      String udid = getUID();
      int level = 0;
      int sessionCount = 0;
      int amountOfNeighbors = 0;
      Profile profile = PlayerProfile.getProfile();
      if (profile != null) {
         level = PlayerProfile.getLevel();
         udid = profile.getUserID();
         sessionCount = PlayerProfile.getSessionCount();
         amountOfNeighbors = PlayerProfile.getAcceptedNeighborCount();
      }

      smLevelParameter.setValue(level);
      smAppUIdPatameter.setValue(udid);
      smSessionCountParameter.setValue(sessionCount);
      smAmountOfNeighbors.setValue(amountOfNeighbors);
   }
}
