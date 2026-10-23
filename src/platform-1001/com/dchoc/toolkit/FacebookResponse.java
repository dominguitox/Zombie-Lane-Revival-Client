package com.dchoc.toolkit;

public class FacebookResponse {
   public static final int RESPONSE_ERROR = -1;
   public static final int RESPONSE_OK = 0;
   private FacebookGraphAPIRequest request;
   private String responseDataString;
   private int responseState;

   public FacebookResponse(FacebookGraphAPIRequest var1, int var2) {
      this.request = var1;
      this.responseState = var2;
   }

   public FacebookGraphAPIRequest getRequest() {
      return this.request;
   }

   public int getResponseState() {
      return this.responseState;
   }

   public String getResponseString() {
      return this.responseDataString;
   }

   public void setResponseString(String var1) {
      this.responseDataString = var1;
   }
}
