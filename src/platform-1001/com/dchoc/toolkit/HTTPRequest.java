package com.dchoc.toolkit;

import java.util.Hashtable;

public class HTTPRequest {
   private DChocByteArray data;
   private Hashtable headers;
   private String url;

   public HTTPRequest(String var1) {
      this.url = var1;
      this.headers = new Hashtable();
   }

   public DChocByteArray getData() {
      return this.data;
   }

   public Hashtable getHeaders() {
      return this.headers;
   }

   public String getURL() {
      return this.url;
   }

   public void setData(DChocByteArray var1) {
      this.data = var1;
   }

   public void setHeaderField(String var1, String var2) {
      this.headers.put(var1, var2);
   }
}
