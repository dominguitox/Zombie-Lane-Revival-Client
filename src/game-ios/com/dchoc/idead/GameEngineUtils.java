package com.dchoc.idead;

import com.dchoc.idead.areas.Area;
import com.dchoc.idead.areas.AreaRob;
import com.dchoc.idead.neighbors.NeighborProfile;

public class GameEngineUtils {
   public static boolean isAtAdventureArea() {
      return GameEngine.getInstance().isVisitingAdventureArea();
   }

   public static boolean isAtNeigborsPlace() {
      return GameEngine.getInstance().isVisitingNeighbor();
   }

   public static boolean isAtHome() {
      return GameEngine.getInstance().isVisitingNeighbor() ? false : !GameEngine.getInstance().isVisitingAdventureArea();
   }

   public static NeighborProfile getNeighbor() {
      return GameEngine.getInstance().getNeighbor();
   }

   public static Area getArea() {
      GameEngine engine = GameEngine.getInstance();
      Area area = engine.getArea();
      return area != null ? area : engine.getHome();
   }

   public static int getAreaName() {
      return getArea().getName();
   }

   public static int getAreaMusic() {
      Area area = getArea();
      return area.getMusic() == -1 ? 3 : area.getMusic();
   }

   public static AreaRob getRob() {
      return GameEngine.getInstance().getRob();
   }

   public static boolean isQuickDraw() {
      GameEngine engine = GameEngine.getInstance();
      return engine == null || engine.isQuickDraw();
   }

   public static boolean isConnected() {
      GameEngine engine = GameEngine.getInstance();
      return engine != null && engine.isConnected();
   }

   public static boolean isLoaded() {
      GameEngine engine = GameEngine.getInstance();
      return engine != null && engine.isLoaded();
   }

   public static long getPlayTime() {
      GameEngine engine = GameEngine.getInstance();
      return engine == null ? 0L : engine.getPlayTime();
   }

   public static int getTimer() {
      GameEngine engine = GameEngine.getInstance();
      return engine == null ? 0 : engine.getTimer();
   }
}
