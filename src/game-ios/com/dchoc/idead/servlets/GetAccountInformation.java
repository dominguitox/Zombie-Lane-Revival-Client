package com.dchoc.idead.servlets;

import com.dchoc.hud.WindowManager;
import com.dchoc.idead.Game;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.Time;
import com.dchoc.idead.commands.ActionCommand;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.GiftItem;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.neighbors.NeighborGift;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.network.Base64;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.parser.IParser;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import com.dchoc.windows.WindowSocial;

public class GetAccountInformation extends ServletRequestWithXml {
   public static final String ELEMENT_ACTION = "action";
   public static final String ELEMENT_AMOUNT = "amount";
   public static final String ELEMENT_AVATAR = "avatar";
   public static final String ELEMENT_DAILY_REWARD = "daily_reward";
   public static final String ELEMENT_FLAG = "flag";
   public static final String ELEMENT_FRIEND_CODE = "friend_code";
   public static final String ELEMENT_GIFT = "gift";
   public static final String ELEMENT_GIFT_ID = "itemId";
   public static final String ELEMENT_HIRED_NEIGHBOR = "hired_neighbor";
   public static final String ELEMENT_ID = "id";
   public static final String ELEMENT_ITEM_ID = "item_id";
   public static final String ELEMENT_KEY = "key";
   public static final String ELEMENT_LAST_VERSION = "last_version";
   public static final String ELEMENT_NEIGHBOR = "neighbor";
   public static final String ELEMENT_NEIGHBOR_AVATAR = "neighbor_avatar";
   public static final String ELEMENT_NEIGHBOR_ID = "neighbor_dcgid";
   public static final String ELEMENT_NEIGHBOR_ID2 = "neighbor_dcgId";
   public static final String ELEMENT_NEXT_DAILY_REWARD = "next_daily_reward";
   public static final String ELEMENT_OPTION = "option";
   public static final String ELEMENT_PLATFORM = "platform";
   public static final String ELEMENT_RECIEVER_ID = "receiver_dcgId";
   public static final String ELEMENT_REWARDS = "rewards";
   public static final String ELEMENT_SENDER_ID = "sender_dcgId";
   public static final String ELEMENT_SENDPUSH_HIRE = "sendpush_hire";
   public static final String ELEMENT_STATUS = "status";
   public static final String ELEMENT_UPDATED_HEALTH = "updated_health";
   public static final String ELEMENT_USER_ID = "dcgId";
   public static final String ELEMENT_VALUE = "value";
   private String mNeighborId;
   private String mSenderId;
   private String mReceiverId;
   private int mItemId;
   private int mUpdatedHealth;
   private int mAmount;
   private int mPlatform;
   private int mLastVersion;
   private long mId;
   private String mNeighborAvatar;
   private String mKey;
   private String mValue;
   private int mDailyRewardFlag;
   private boolean mIsRewards;

   public GetAccountInformation() {
      super("GetAccountInformation", false);
   }

   public void init() {
      this.clearParameters();
      this.mDailyRewardFlag = -1;
      this.mAmount = 1;
      this.mLastVersion = -1;
      this.mIsRewards = false;
      this.mPlatform = 3;
   }

   @Override
   public void startElement(IParser parser, String tagName) {
      super.startElement(parser, tagName);
      if (tagName.equals("rewards")) {
         this.mIsRewards = true;
      }
   }

   @Override
   public void endElement(IParser parser, String tagName) {
      super.endElement(parser, tagName);
      if (tagName.equals("rewards")) {
         this.mIsRewards = false;
      }
   }

   @Override
   public void endElement(IParser parser, String tagName, String text) {
      if (tagName.equals("dcgId") || tagName.equals("neighbor_dcgid") || tagName.equals("neighbor_dcgId")) {
         this.mNeighborId = text;
      } else if (tagName.equals("amount")) {
         this.mAmount = ToolkitHelpers.parseInt(text, 1);
      } else if (tagName.equals("friend_code")) {
         PlayerProfile.setFriendCode(text);
      } else if (tagName.equals("sender_dcgId")) {
         this.mSenderId = text;
      } else if (tagName.equals("last_version")) {
         this.mLastVersion = ToolkitHelpers.parseInt(text, -1);
      } else if (tagName.equals("receiver_dcgId")) {
         this.mReceiverId = text;
      } else if (tagName.equals("id")) {
         this.mId = ToolkitHelpers.parseLong(text, 0L);
      } else if (tagName.equals("value")) {
         this.mValue = text;
      } else if (tagName.equals("key")) {
         this.mKey = text;
      } else if (tagName.equals("flag")) {
         if (this.mKey.equals("daily_reward")) {
            this.mDailyRewardFlag = ToolkitHelpers.parseInt(this.mValue, -1);
         }
      } else if (tagName.equals("option")) {
         if (this.mKey.equals("sendpush_hire")) {
            Game.getInstance().setNeighborActions(this.mValue.equals("true"));
         }
      } else if (tagName.equals("item_id") || tagName.equals("itemId")) {
         this.mItemId = ToolkitHelpers.parseInt(text, -1);
      } else if (tagName.equals("updated_health")) {
         this.mUpdatedHealth = ToolkitHelpers.parseInt(text, 1);
      } else if (tagName.equals("neighbor_avatar")) {
         this.mNeighborAvatar = text;
      } else if (tagName.equals("platform")) {
         this.mPlatform = ToolkitHelpers.parseInt(text, 3);
      } else if (tagName.equals("next_daily_reward")) {
         long raw = ToolkitHelpers.parseLong(text, 0L);
         long ms = raw * 1000L;
         if (ms > 0L) {
            long ts = Time.getTimestampAfter(ms);
            PlayerProfile.setNextDailyRewardTime(ts);
         }
      } else if (tagName.equals("neighbor")) {
         NeighborProfile neighbor = this.getNeighbor(this.mNeighborId);
         if (neighbor == null) {
            neighbor = new NeighborProfile("Neighbor", this.mNeighborId, 1, this.mPlatform, 1926);
         }

         loadAvatarData(neighbor, this.mNeighborAvatar);
         ((WindowSocial)WindowManager.getWindow((byte)18)).addNeighbor(neighbor);
         this.mPlatform = 3;
      } else if (tagName.equals("action")) {
         NeighborProfile neighbor = this.getNeighbor(this.mNeighborId);
         if (neighbor != null && neighbor.getCommand(this.mId) == null) {
            ActionCommand action = new ActionCommand(this.mItemId, this.mUpdatedHealth, this.mId);
            neighbor.addCommand(action);
         }
      } else if (tagName.equals("gift") && this.getGift(this.mId) == null) {
         GiftItem giftItem = ItemManager.getGift(this.mItemId);
         if (giftItem != null) {
            NeighborProfile sender = this.getNeighbor(this.mSenderId);
            if (sender == null) {
               sender = new NeighborProfile(Toolkit.getText(2236), this.mSenderId, 1, 3, 1926);
            }

            NeighborGift gift = new NeighborGift(sender, giftItem, this.mId, this.mAmount);
            gift.setStatus(2);
            gift.setReward(this.mIsRewards);
            ((WindowSocial)WindowManager.getWindow((byte)18)).addGift(gift);
         }
      }
   }

   @Override
   public void endDocument(IParser parser) {
      IsometricUtils.getScene().setUpdateNeighbors();
      if (this.mDailyRewardFlag > 0 && TutorialFlow.isCompleted()) {
         CollectDailyRewards collectDailyRewards = new CollectDailyRewards();
         collectDailyRewards.init(this.mDailyRewardFlag);
         collectDailyRewards.execute();
      }

      GameEngine.getInstance().setGetTimers();
   }

   public NeighborProfile getNeighbor(String userId) {
      return userId != null ? ((WindowSocial)WindowManager.getWindow((byte)18)).getNeighbor(userId) : null;
   }

   public NeighborGift getGift(long id) {
      return id != -1L ? ((WindowSocial)WindowManager.getWindow((byte)18)).getGift(id) : null;
   }

   private static void loadAvatarData(NeighborProfile neighbor, String avatarData) {
      if (avatarData != null && !avatarData.equals("null") && avatarData.length() > 0) {
         byte[] bytes = Base64.decode(avatarData);
         if (bytes != null) {
            DChocByteArray byteArray = DChocByteArray.createByteArrayForReading(bytes, 0, bytes.length);
            neighbor.load(byteArray);
         }
      }
   }

   @Override
   public void debug(String message) {
      System.out.println("GetAccountInformation: " + message);
   }
}
