package com.dchoc.idead.servlets;

import com.dchoc.toolkit.Toolkit;

public class UpdateDeviceToken extends ServletRequest {
   public UpdateDeviceToken(String pid, String deviceToken) {
      super("UpdateDeviceToken", false, false);
      this.addParameter("pid", pid);
      this.addParameter("device_token", deviceToken);
      this.addParameter("udid", String.valueOf(Toolkit.getMacAddress() != null));
   }

   @Override
   public void debug(String message) {
      System.out.println("UpdateDeviceToken: " + message);
   }
}
