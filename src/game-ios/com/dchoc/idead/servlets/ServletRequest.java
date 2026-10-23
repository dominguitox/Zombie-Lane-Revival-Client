package com.dchoc.idead.servlets;

import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.network.MD5;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.iphonewrappers.SettingsWrapper;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.DChocHTTPConnection;
import com.dchoc.toolkit.HTTPRequest;
import com.dchoc.toolkit.HTTPResponse;
import com.dchoc.toolkit.IHTTPResponseListener;
import java.util.Vector;
import zlport.ios.Ios;

public class ServletRequest implements IHTTPResponseListener {
   public static final String EMPTY_STRING = new String("");
   public static final String PARAMS_SEPARATOR = "&";
   public static final String SIGNATURE_PREFIX = "&sig=";
   public static final int STATE_NONE = 0;
   public static final int STATE_WAITING_FOR_RESPONSE = 1;
   public static final int STATE_RESPONSE_RECIEVED = 2;
   public static final String TARGET_SEPARATOR = "?";
   public static final String VALUE_SEPARATOR = "=";
   private DChocHTTPConnection mConnection;
   protected HTTPResponse mResponse;
   private int mParametersLength;
   private String mServiceName;
   private Vector mParameters;
   private boolean mUrlEncode;
   private int mState;
   private boolean mAddUserInfo;
   private boolean mShowOffline;

   public ServletRequest(String serviceName, boolean showOffline, boolean urlEncode) {
      this.mServiceName = serviceName;
      this.mShowOffline = showOffline;
      this.mUrlEncode = urlEncode;
      this.mParameters = new Vector(8);
      this.mParametersLength = 0;
      this.mResponse = null;
      this.mState = 0;
      this.mAddUserInfo = true;
   }

   public void execute() {
      String serverUrl = SettingsWrapper.getServerURL();
      String sessionId = null;
      if (this.mAddUserInfo) {
         sessionId = GameEngine.getInstance().getSessionID();
         String userId = PlayerProfile.getProfile().getUserID();
         if (userId != null) {
            this.addParameter("dcgid", userId);
         }
      }

      this.addParameter("ver", SettingsWrapper.getVersion());
      this.addParameter("platform", "3");
      String params = this.createParamtersString();
      StringBuffer sb = new StringBuffer(this.mParametersLength + 21);
      sb.append(params);
      String hashInput;
      if (sessionId != null) {
         hashInput = params + sessionId + "BRRRRAAAAAIIIIIIIINNNNNNZZZZZZZZZZ!!!";
      } else {
         hashInput = "BRRRRAAAAAIIIIIIIINNNNNNZZZZZZZZZZ!!!";
      }

      String sig = MD5.create(hashInput);
      sb.append("&sig=");
      sb.append(sig);
      HTTPRequest request = new HTTPRequest(serverUrl + this.mServiceName);
      DChocByteArray postData = DChocByteArray.createByteArrayForWriting();
      String paramString = sb.toString();
      if (this.mUrlEncode) {
         paramString = this.encodeString(paramString);
      }

      postData.writeChars(paramString);
      request.setData(postData);
      this.mState = 1;
      ServletsManager.addRequest(this);
      this.mConnection = new DChocHTTPConnection();
      this.mConnection.setResponseListener(this);
      this.mConnection.sendRequest(request);
      this.debug("Request sent to " + request.getURL());
   }

   public void responseReceived(HTTPResponse response) {
      this.mResponse = response;
      ServletsManager.processRequest(this);
   }

   public void processResponse() {
      this.mState = 2;
      if (this.mResponse != null) {
         this.debug("Received response " + this.mResponse.getResponseCode());
      }

      if (this.mShowOffline && (this.mResponse == null || this.mResponse.getResponseCode() != HTTPResponse.RESPONSE_CODE_OK)) {
         WindowManager.openNPCDialog((byte)3);
      }

      ServletsManager.removeRequest(this);
   }

   public boolean isWaitingForResponse() {
      return this.mState == 1;
   }

   public boolean isResponseRecieved() {
      return this.mState == 2;
   }

   public void setAddUserInfo(boolean addUserInfo) {
      this.mAddUserInfo = addUserInfo;
   }

   public boolean isAddingUserInfo() {
      return this.mAddUserInfo;
   }

   public int getState() {
      return this.mState;
   }

   public HTTPResponse getResponse() {
      return this.mResponse;
   }

   public String getServiceName() {
      return this.mServiceName;
   }

   public void debug(String message) {
      System.out.println("ServletRequest: " + message);
   }

   protected void addParameter(String key, String value) {
      String val = value == null ? EMPTY_STRING : value;
      this.mParameters.addElement(key);
      this.mParameters.addElement(val);
      this.mParametersLength = this.mParametersLength + key.length() + val.length() + 2;
   }

   protected void clearParameters() {
      this.mParameters.removeAllElements();
      this.mParametersLength = 0;
   }

   public String encodeString(String text) {
      return Ios.platform().stringByAddingPercentEscapes(text);
   }

   public String createParamtersString() {
      int pairs = this.mParameters.size() / 2;
      if (pairs > 1) {
         for (int i = 0; i < pairs - 1; i++) {
            String keyI = (String)this.mParameters.elementAt(i * 2);

            for (int j = i + 1; j < pairs; j++) {
               String keyJ = (String)this.mParameters.elementAt(j * 2);
               if (keyJ.compareTo(keyI) < 0) {
                  this.mParameters.setElementAt(keyJ, i * 2);
                  this.mParameters.setElementAt(keyI, j * 2);
                  Object valI = this.mParameters.elementAt(i * 2 + 1);
                  Object valJ = this.mParameters.elementAt(j * 2 + 1);
                  this.mParameters.setElementAt(valJ, i * 2 + 1);
                  this.mParameters.setElementAt(valI, j * 2 + 1);
                  keyI = keyJ;
               }
            }
         }
      }

      StringBuffer sb = new StringBuffer(this.mParametersLength);
      if (0 < this.mParameters.size()) {
         int i = 0;

         do {
            String elem = (String)this.mParameters.elementAt(i);
            if ((i & 1) == 0) {
               if (i > 1) {
                  sb.append("&");
               }

               sb.append(elem);
               String nextVal = (String)this.mParameters.elementAt(i + 1);
               if (nextVal != EMPTY_STRING) {
                  sb.append("=");
               }
            } else if (elem != EMPTY_STRING) {
               sb.append(elem);
            }
         } while (++i < this.mParameters.size());
      }

      return sb.toString();
   }

   public void percentageDataReceived(int percentage) {
   }

   public void responseCodeReceived(int responseCode) {
   }
}
