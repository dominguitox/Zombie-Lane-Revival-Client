package com.dchoc.idead.servlets;

import com.dchoc.idead.items.Item;
import com.dchoc.idead.neighbors.NeighborGift;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.parser.IParser;

public class AcceptGift extends ServletRequestWithXml {
   public static final String ELEMENT_SUCCESS = "success";
   private NeighborGift mGift;
   private boolean mSucceed;
   private boolean mAccept;

   public AcceptGift() {
      super("AcceptGift", true);
   }

   public void init(NeighborGift gift, boolean accept) {
      this.clearParameters();
      this.mGift = gift;
      this.mAccept = accept;
      this.mSucceed = false;
      NeighborProfile neighbor = gift.getNeighbor();
      Item item = this.mGift.getGiftItem();
      StringBuffer sb = new StringBuffer();
      sb.append(item.getID());
      sb.append(",");
      sb.append(neighbor.getUserID());
      this.addParameter("gifts", sb.toString());
      if (!accept) {
         this.addParameter("cancel", null);
      }
   }

   public boolean isSucceed() {
      return this.mSucceed;
   }

   @Override
   public void endDocument(IParser parser) {
      if (this.mSucceed) {
         this.mGift.setStatus(this.mAccept ? 1 : 3);
      }
   }

   @Override
   public void endElement(IParser parser, String tag, String text) {
      if (tag.equals("success")) {
         NeighborProfile neighbor = this.mGift.getNeighbor();
         Item item = this.mGift.getGiftItem();
         this.mSucceed = text.startsWith(Integer.toString(item.getID())) && text.endsWith(neighbor.getUserID());
      }
   }

   @Override
   public void debug(String message) {
      System.out.println("AcceptGift: " + message);
   }
}
