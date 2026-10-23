package com.dchoc.idead.missions;

import com.dchoc.idead.GameEngine;

/**
 * Admin/development helper for custom missions published by the local server.
 *
 * <p>
 * Custom missions are downloaded on startup and injected hidden, so a holiday
 * or event mission can ship with the server without appearing in the mission
 * HUD
 * until it is explicitly shown. {@link #requestRefresh()} asks the server for
 * the
 * latest set of custom missions; {@link #show(int)} makes an already-loaded
 * custom mission appear immediately as a new mission, without restarting the
 * game.
 */
public final class CustomMissions {
   private CustomMissions() {
   }

   /** Ask the server for the current custom missions and inject them (hidden). */
   public static void requestRefresh() {
      GameEngine engine = GameEngine.getInstance();
      if (engine != null) {
         engine.setGetSessionStatus();
      }
   }

   /**
    * Make a custom mission visible in the running game.
    *
    * @return true when a mission with that id is loaded and was shown.
    */
   public static boolean show(int missionId) {
      return MissionManager.showCustomMission(missionId);
   }
}
