package com.dchoc.toolkit;

public interface IHTTPConnection {
   void sendRequest(HTTPRequest var1);

   void setResponseListener(IHTTPResponseListener var1);
}
