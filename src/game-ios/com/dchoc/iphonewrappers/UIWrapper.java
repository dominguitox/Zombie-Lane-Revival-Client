package com.dchoc.iphonewrappers;

import zlport.ios.Ios;

public class UIWrapper {
   public static void showAlertView(String title, String message, String cancelButtonTitle) {
      Ios.platform().showAlertView(title, message, cancelButtonTitle);
   }
}
