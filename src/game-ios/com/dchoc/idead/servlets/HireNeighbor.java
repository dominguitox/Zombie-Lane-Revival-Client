package com.dchoc.idead.servlets;

import com.dchoc.hud.HUD;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.parser.IParser;

public class HireNeighbor extends ServletRequestWithXml {
   public static final String ELEMENT_SUCCESS = "success";
   private NeighborProfile mNeighbor;
   private boolean mSucceed;

   public HireNeighbor() {
      super("HireNeighbor", true);
   }

   public void init(NeighborProfile neighbor) {
      this.clearParameters();
      this.mNeighbor = neighbor;
      this.mSucceed = false;
      this.addParameter("neighbors", neighbor.getUserID());
   }

   public boolean isSucceed() {
      return this.mSucceed;
   }

   @Override
   public void endDocument(IParser parser) {
      if (this.mSucceed) {
         HUD.getInGame().getEdit().hireAccepted();
      }

      GameEngine.getInstance().setGetTimers();
   }

   @Override
   public void endElement(IParser parser, String tag, String text) {
      if (tag.equals("success")) {
         this.mSucceed = text.equals(this.mNeighbor.getUserID());
      }
   }

   @Override
   public void debug(String message) {
      System.out.println("HireNeighbor: " + message);
   }
}
