package com.dchoc.idead.servlets;

import com.dchoc.hud.HUD;
import com.dchoc.idead.commands.ActionCommand;
import com.dchoc.idead.commands.CommandBase;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.parser.IParser;
import com.dchoc.toolkit.ToolkitHelpers;
import java.util.Vector;

public class AcceptNeighborActionsRequest extends ServletRequestWithXml {
   public static final String ELEMENT_ID = "id";
   private NeighborProfile mNeighbor;
   private boolean mAccept;

   public AcceptNeighborActionsRequest() {
      super("AcceptNeighborActions", true);
   }

   public void init(NeighborProfile neighbor, boolean accept) {
      this.clearParameters();
      this.mNeighbor = neighbor;
      this.mAccept = accept;
      this.addParameter("neighbor_dcgid", neighbor.getUserID());
      if (!accept) {
         this.addParameter("cancel", null);
      }
   }

   @Override
   public void endDocument(IParser parser) {
      Vector commands = this.mNeighbor.getCommands();

      for (int i = 0; i < commands.size(); i++) {
         CommandBase command = (CommandBase)commands.elementAt(i);
         if (command.getType() == 0 && !command.isAccepted()) {
            return;
         }
      }

      HUD.getInGame().removeAcceptHelp(this.mNeighbor);
   }

   @Override
   public void endElement(IParser parser, String tag, String text) {
      if (tag.equals("id")) {
         long id = ToolkitHelpers.parseLong(text, -1L);
         if (!this.mAccept) {
            this.mNeighbor.removeCommand(id);
         } else {
            CommandBase command = this.mNeighbor.getCommand(id);
            if (command != null && command.getType() == 0) {
               ((ActionCommand)command).setAccepted(true);
            }
         }
      }
   }

   @Override
   public void debug(String message) {
      System.out.println("AcceptNeighborActions: " + message);
   }
}
