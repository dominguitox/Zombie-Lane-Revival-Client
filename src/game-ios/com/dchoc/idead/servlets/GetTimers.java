package com.dchoc.idead.servlets;

import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.Time;
import com.dchoc.idead.neighbors.NeighborGift;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.parser.IParser;
import com.dchoc.toolkit.ToolkitHelpers;
import com.dchoc.windows.WindowSocial;
import java.util.Vector;

public class GetTimers extends ServletRequestWithXml {
   public static final String ELEMENT_ACTION4_COUNTDOWN = "action4_countdown";
   public static final String ELEMENT_AVAILABLE_HIRE = "available_hire";
   public static final String ELEMENT_COUNTDOWN = "countdown";
   public static final String ELEMENT_GENERAL = "general";
   public static final String ELEMENT_GIFT = "gift";
   public static final String ELEMENT_GIFT_ID = "itemId";
   public static final String ELEMENT_HIRED_NEIGHBOR = "hired_neighbor";
   public static final String ELEMENT_ID = "id";
   public static final String ELEMENT_ITEM_ID = "item_id";
   public static final String ELEMENT_MAX_HIRE_PER_DAY = "maxHiresPerDay";
   public static final String ELEMENT_NEIGHBOR_ID = "neighbor_dcgid";
   public static final String ELEMENT_NEIGHBOR_ID2 = "neighbor_dcgId";
   public static final String ELEMENT_RECIEVER_ID = "receiver_dcgId";
   public static final String ELEMENT_SENDER_ID = "sender_dcgId";
   private String mNeighborId;
   private String mSenderId;
   private String mReceiverId;
   private int mItemId;
   private long mId;
   private long mCountdown;
   private boolean mGeneral;

   public GetTimers() {
      super("GetTimers", false);
   }

   public void init() {
      this.clearParameters();
   }

   @Override
   public void startDocument(IParser parser) {
      Vector neighbors = ((WindowSocial)WindowManager.getWindow((byte)18)).getNeighbors();

      for (int i = 0; i < neighbors.size(); i++) {
         NeighborProfile neighbor = (NeighborProfile)neighbors.elementAt(i);
         if (neighbor.getType() != 2514) {
            neighbor.setHired(false);
            if (neighbor != GameEngineUtils.getNeighbor()) {
               neighbor.setActionsLeft(1);
            }

            Vector gifts = neighbor.getGifts();

            for (int j = 0; j < gifts.size(); j++) {
               NeighborGift gift = (NeighborGift)gifts.elementAt(j);
               gift.setStatus(0);
               gift.setCountdown(Time.getTimestamp() - 1L);
            }
         }
      }

      PlayerProfile.setHireCountdown(Time.getTimestamp() - 1L);
      this.mGeneral = false;
   }

   @Override
   public void startElement(IParser parser, String tagName) {
      super.startElement(parser, tagName);
      if ("general".equals(tagName)) {
         this.mGeneral = true;
      }
   }

   @Override
   public void endElement(IParser parser, String tagName) {
      super.endElement(parser, tagName);
      if ("general".equals(tagName)) {
         this.mGeneral = false;
      }
   }

   @Override
   public void endElement(IParser parser, String tagName, String text) {
      if ("neighbor_dcgid".equals(tagName) || "neighbor_dcgId".equals(tagName)) {
         this.mNeighborId = text;
      } else if ("sender_dcgId".equals(tagName)) {
         this.mSenderId = text;
      } else if ("receiver_dcgId".equals(tagName)) {
         this.mReceiverId = text;
      } else if ("id".equals(tagName)) {
         this.mId = ToolkitHelpers.parseLong(text, 0L);
      } else if ("countdown".equals(tagName)) {
         this.mCountdown = ToolkitHelpers.parseLong(text, 0L) * 1000L;
         if (this.mGeneral) {
            PlayerProfile.setHireCountdown(Time.getTimestamp() + this.mCountdown);
         }
      } else if ("action4_countdown".equals(tagName)) {
         NeighborProfile neighbor = this.getNeighbor(this.mNeighborId);
         if (neighbor != null && neighbor.getActionsLeft() != 0) {
            neighbor.setActionsLeft(0);
         }
      } else if ("available_hire".equals(tagName)) {
         PlayerProfile.setHiresLeft(ToolkitHelpers.parseInt(text, 0));
      } else if ("maxHiresPerDay".equals(tagName)) {
         PlayerProfile.setHiresMax(ToolkitHelpers.parseInt(text, 0));
      } else if ("item_id".equals(tagName) || "itemId".equals(tagName)) {
         this.mItemId = ToolkitHelpers.parseInt(text, -1);
      } else if ("hired_neighbor".equals(tagName)) {
         NeighborProfile neighbor = this.getNeighbor(this.mNeighborId);
         if (neighbor != null) {
            neighbor.setHired(true);
         }
      } else if ("gift".equals(tagName)) {
         NeighborProfile neighbor = this.getNeighbor(this.mReceiverId);
         if (neighbor != null) {
            NeighborGift gift = neighbor.getGift(this.mItemId);
            if (gift != null) {
               gift.setStatus(4);
               gift.setCountdown(Time.getTimestamp() + this.mCountdown);
            }
         }
      }
   }

   @Override
   public void endDocument(IParser parser) {
      GameEngine.getInstance().setUpdateAccountInformation();
   }

   public NeighborProfile getNeighbor(String userId) {
      return ((WindowSocial)WindowManager.getWindow((byte)18)).getNeighbor(userId);
   }

   @Override
   public void debug(String message) {
      System.out.println("GetTimersRequest: " + message);
   }
}
