package com.dchoc.idead.servlets;

import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.parser.IParser;
import com.dchoc.toolkit.HTTPResponse;

public class CollectDailyRewards extends ServletRequestWithXml {
   public static final String ELEMENT_RESPONSE_CODE = "response_code";
   private int mResponseCode;
   private int mDay;

   public CollectDailyRewards() {
      super("CollectDailyRewards", false);
   }

   public void init(int day) {
      this.clearParameters();
      this.mResponseCode = 0;
      this.mDay = day;
   }

   @Override
   public void processResponse() {
      super.processResponse();
      if (this.mResponse != null && this.mResponse.getResponseCode() == HTTPResponse.RESPONSE_CODE_OK && this.mResponseCode == 0) {
         PlayerProfile.addDailyReward(this.mDay - 1);
      }
   }

   @Override
   public void endElement(IParser parser, String tag, String text) {
      if (tag.equals("response_code")) {
         this.mResponseCode = Integer.parseInt(text);
      }
   }

   @Override
   public void debug(String message) {
      System.out.println("CollectDailyRewards: " + message);
   }
}
