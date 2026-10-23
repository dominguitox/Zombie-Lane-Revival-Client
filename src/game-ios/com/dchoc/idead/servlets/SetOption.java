package com.dchoc.idead.servlets;

import com.dchoc.idead.Game;

public class SetOption extends ServletRequest {
   public SetOption() {
      super("SetOption", false, false);
   }

   public void init() {
      this.clearParameters();
      Game game = Game.getInstance();
      StringBuffer sb = new StringBuffer();
      sb.append("sendpush_hire");
      sb.append(",");
      sb.append(game.neighborActionsEnabled() ? "true" : "false");
      sb.append("|");
      sb.append("sendpush_visit");
      sb.append(",");
      sb.append(game.neighborActionsEnabled() ? "true" : "false");
      sb.append("|");
      sb.append("sendpush_sendgift");
      sb.append(",");
      sb.append(game.neighborActionsEnabled() ? "true" : "false");
      this.addParameter("options", sb.toString());
   }

   @Override
   public void debug(String message) {
      System.out.println("SetOption: " + message);
   }
}
