package com.dchoc.idead.servlets;

import com.dchoc.idead.items.Item;
import com.dchoc.idead.neighbors.NeighborProfile;

public class AddNeighborActions extends ServletRequest {
   public AddNeighborActions(NeighborProfile neighbor, Item item, int action) {
      super("AddNeighborActions", false, false);
      StringBuffer sb = new StringBuffer();
      sb.append(item.getID());
      sb.append(",");
      sb.append(action);
      sb.append(",");
      sb.append(System.currentTimeMillis() + 86400000L);
      this.addParameter("neighbor_dcgid", neighbor.getUserID());
      this.addParameter("actions", sb.toString());
   }

   @Override
   public void debug(String message) {
      System.out.println("AddNeighborActions: " + message);
   }
}
