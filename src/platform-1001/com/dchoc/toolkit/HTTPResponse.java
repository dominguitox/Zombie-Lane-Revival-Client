package com.dchoc.toolkit;

public class HTTPResponse {
   public static final int RESPONSE_CODE_ERROR = -1;
   public static final int RESPONSE_CODE_OK = 200;
   private DChocByteArray data;
   private int responseCode;

   public HTTPResponse(int var1, DChocByteArray var2) {
      this.responseCode = var1;
      this.data = var2;
   }

   public DChocByteArray getData() {
      return this.data;
   }

   public int getResponseCode() {
      return this.responseCode;
   }
}
