package com.dchoc.toolkit;

public interface IHTTPResponseListener {
   void percentageDataReceived(int var1);

   void responseCodeReceived(int var1);

   void responseReceived(HTTPResponse var1);
}
