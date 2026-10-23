package com.dchoc.idead.servlets;

import java.util.Vector;

public class ServletsManager {
   private static Vector smRequests;
   private static Vector smResponses;

   public static void init() {
      smRequests = new Vector(16);
      smResponses = new Vector(16);
   }

   public static void addRequest(ServletRequest servletRequest) {
      if (servletRequest != null) {
         smRequests.addElement(servletRequest);
      }
   }

   public static void removeRequest(ServletRequest servletRequest) {
      if (servletRequest != null) {
         smRequests.removeElement(servletRequest);
      }
   }

   public static void processRequest(ServletRequest servletRequest) {
      if (servletRequest != null) {
         smResponses.addElement(servletRequest);
      }
   }

   public static int logicUpdate(int deltaTime) {
      if (!smResponses.isEmpty()) {
         ServletRequest servletRequest = (ServletRequest)smResponses.firstElement();
         servletRequest.processResponse();
         smResponses.removeElement(servletRequest);
      }

      return 0;
   }
}
