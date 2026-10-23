package com.dchoc.idead.servlets;

import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.network.Base64;
import com.dchoc.parser.IParser;
import com.dchoc.toolkit.ToolkitHelpers;

public class VisitNeighbor extends ServletRequestWithXml {
   public static final String ELEMENT_ACTIONS_LEFT = "actions_left";
   public static final String ELEMENT_AVATAR = "avatar";
   public static final String ELEMENT_GAMEFIELD = "gamefield";
   public static final String ELEMENT_REWARDS = "rewards";
   public static final String ELEMENT_SUCCESS = "success";
   private String mRewards;
   private NeighborProfile mNeighbor;
   private byte[] mGamefieldData;
   private byte[] mAvatarData;

   public VisitNeighbor() {
      super("VisitNeighbor", false);
   }

   public NeighborProfile getProfile() {
      return this.mNeighbor;
   }

   public byte[] getAvatarData() {
      return this.mAvatarData;
   }

   public byte[] getGamefieldData() {
      return this.mGamefieldData;
   }

   public boolean hasRewards() {
      return this.mRewards != null;
   }

   public void resetData() {
      this.mGamefieldData = null;
      this.mAvatarData = null;
      this.mRewards = null;
   }

   public void init(NeighborProfile neighbor) {
      this.clearParameters();
      this.mRewards = null;
      this.mNeighbor = neighbor;
      this.mGamefieldData = null;
      this.mAvatarData = null;
      if (neighbor != null) {
         this.addParameter("neighbor_dcgid", neighbor.getUserID());
      }
   }

   @Override
   public void endElement(IParser parser, String tag, String text) {
      if (tag.equals("gamefield")) {
         this.mGamefieldData = Base64.decode(text);
      } else if (tag.equals("avatar")) {
         this.mAvatarData = Base64.decode(text);
      } else if (tag.equals("actions_left")) {
         if (this.mNeighbor != null) {
            this.mNeighbor.setActionsLeft(ToolkitHelpers.parseInt(text, 0));
         }
      } else if (tag.equals("rewards")) {
         this.mRewards = text;
      }
   }

   @Override
   public void debug(String message) {
      System.out.println("VisitNeighbor: " + message);
   }
}
