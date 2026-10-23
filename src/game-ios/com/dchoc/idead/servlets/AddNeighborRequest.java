package com.dchoc.idead.servlets;

import com.dchoc.idead.GameEngine;
import com.dchoc.idead.social.SocialFriend;
import com.dchoc.parser.IParser;

public class AddNeighborRequest extends ServletRequestWithXml {
   public static final String ELEMENT_ADDED_NEIGHBOR = "added_neighbor";
   public static final String ELEMENT_NEIGHBOR_ID = "neighborDcgId";
   public static final String ELEMENT_SUCCESS = "success";
   private boolean mSucceed = false;
   private boolean mAdded = false;
   private String mNeighborID;
   private boolean mAlreadyNeighbor;
   private SocialFriend mSocialFriend;

   public AddNeighborRequest() {
      super("AddNeighborRequest", false);
   }

   public void init(SocialFriend friend) {
      this.clearParameters();
      this.mSocialFriend = friend;
      this.mNeighborID = null;
      this.mSucceed = false;
      this.mAdded = false;
      this.mAlreadyNeighbor = false;
      StringBuffer sb = new StringBuffer();
      if (!friend.getFriendCode().equals("")) {
         sb.append("1");
      } else if (friend.getFacebookID() == null) {
         sb.append("6");
      } else {
         sb.append("2");
      }

      sb.append(",");
      if (!friend.getFriendCode().equals("")) {
         sb.append(friend.getFriendCode());
      } else if (friend.getFacebookID() == null) {
         sb.append(friend.getGameCenterID());
      } else {
         sb.append(friend.getFacebookID());
      }

      this.addParameter("neighbors", sb.toString());
   }

   public boolean isSucceed() {
      return this.mSucceed;
   }

   public boolean isAdded() {
      return this.mAdded;
   }

   public boolean isAlreadyNeighbor() {
      return this.mAlreadyNeighbor;
   }

   @Override
   public void endDocument(IParser parser) {
      if (this.mNeighborID == null) {
         this.mAdded = false;
      } else {
         this.mAdded = this.mNeighborID.length() > 1;
         if (this.mAdded) {
            this.mSocialFriend.setAdded(true);
            GameEngine.getInstance().setGetAccountInformation();
         }
      }
   }

   @Override
   public void endElement(IParser parser, String tag, String text) {
      if (tag.equals("neighborDcgId")) {
         this.mNeighborID = text;
      } else if (tag.equals("success")) {
         this.mSucceed = text.equals("true");
      } else if (tag.equals("added_neighbor")) {
         this.mAlreadyNeighbor = true;
      }

      if (tag.equals("response_code") && text.equals("3")) {
         this.mSucceed = true;
      }
   }

   @Override
   public void debug(String message) {
      System.out.println("AddNeighborRequest: " + message);
   }
}
