package com.dchoc.toolkit;

public class FacebookGraphAPIRequest {
   private String requestConnection;
   private String requestId;

   public FacebookGraphAPIRequest(String var1, String var2) {
      this.requestId = var1;
      this.requestConnection = var2;
   }

   public String getRequestConnection() {
      return this.requestConnection;
   }

   public String getRequestId() {
      return this.requestId;
   }
}
