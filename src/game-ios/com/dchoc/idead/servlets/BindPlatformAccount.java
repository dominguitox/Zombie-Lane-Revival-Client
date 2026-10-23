package com.dchoc.idead.servlets;

public class BindPlatformAccount extends ServletRequest {
   public static final String VALID_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";

   public BindPlatformAccount(String pid, String puid, String nameArg) {
      super("BindPlatformAccount", false, true);
      String name = this.normalizeName(nameArg);
      this.addParameter("pid", pid);
      this.addParameter("puid", puid);
      this.addParameter("name", name);
   }

   public String normalizeName(String s) {
      if (s == null) {
         return null;
      }

      StringBuffer sb = new StringBuffer(s.length());

      for (int i = 0; i < s.length(); i++) {
         if ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ".indexOf(s.charAt(i)) != -1) {
            sb.append(s.charAt(i));
         }
      }

      return sb.toString();
   }

   @Override
   public void debug(String message) {
      System.out.println("BindPlatformAccount: " + message);
   }
}
