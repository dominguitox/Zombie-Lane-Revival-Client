package com.dchoc.toolkit;

public class FacebookLoginResponse extends FacebookResponse {
   public static String LOGIN_FAILURE = "login_failure";
   public static String LOGIN_SUCESS = "login_success";

   public FacebookLoginResponse(FacebookGraphAPIRequest var1, int var2) {
      super(var1, var2);
   }
}
