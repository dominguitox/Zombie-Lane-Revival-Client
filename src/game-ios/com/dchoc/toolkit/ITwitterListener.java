package com.dchoc.toolkit;

public interface ITwitterListener {
   int EVENT_LOGIN = 0;
   int EVENT_LOGOUT = 1;
   int EVENT_UPDATE_STATUS = 2;
   int STATUS_LOGIN_FAILED = 3;
   int STATUS_NETWORK_ERROR = 1;
   int STATUS_NOT_LOGGED_IN = 2;
   int STATUS_OK = 0;

   void responseReceived(int var1, int var2);
}
