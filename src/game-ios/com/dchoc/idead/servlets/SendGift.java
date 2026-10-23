package com.dchoc.idead.servlets;

import com.dchoc.idead.GameEngine;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.neighbors.NeighborGift;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.parser.IParser;

public class SendGift extends ServletRequestWithXml {
   public static final String ELEMENT_SUCCESS = "success";
   private NeighborGift mGift;
   private boolean mSucceed;

   public SendGift() {
      super("SendGift", true);
   }

   public void init(NeighborGift gift) {
      this.clearParameters();
      this.mGift = gift;
      this.mSucceed = false;
      NeighborProfile neighbor = gift.getNeighbor();
      Item item = this.mGift.getGiftItem();
      this.addParameter("neighbors", neighbor.getUserID());
      this.addParameter("gift", Integer.toString(item.getID()));
   }

   public boolean isSucceed() {
      return this.mSucceed;
   }

   @Override
   public void endDocument(IParser parser) {
      if (this.mSucceed) {
         this.mGift.setStatus(4);
         this.mGift.setCountdown(0L);
      }

      GameEngine.getInstance().setGetTimers();
   }

   @Override
   public void endElement(IParser parser, String tag, String text) {
      if (tag.equals("success")) {
         this.mSucceed = text.equals(this.mGift.getNeighbor().getUserID());
      }
   }

   @Override
   public void debug(String message) {
      System.out.println("SendGift: " + message);
   }
}
