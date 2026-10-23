package com.dchoc.idead.tracking;

public class SocialLoginEvent extends Event {
   public static final int POST_TYPE_INVITE = 0;
   public static final int POST_TYPE_MISSION = 1;
   public static final int SNS_GAMECENTER = 0;
   public static final int SNS_FACEBOOK = 1;
   public static final int SNS_TWITTER = 2;
   private static final String[] SNS_NAMES = new String[]{"Game Center", "Facebook", "Twitter"};
   private static final String[] POST_TYPES = new String[]{"Invite", "Mission"};
   private String mPost = "";

   public SocialLoginEvent(int sns) {
      super(3, 22, 2);
      this.setParameter(17, SNS_NAMES[sns]);
   }

   public void trackStarted() {
      this.setAction(2);
      this.trackEvent();
   }

   public void trackCompleted(String detail) {
      this.setAction(8);
      if (detail != null) {
         this.setParameter(18, detail);
      }

      this.trackEvent();
   }

   public void trackError() {
      this.setAction(10);
      this.trackEvent();
   }

   public void trackFriends(int count) {
      this.setAction(27);
      this.setParameter(18, count);
      this.trackEvent();
   }

   public void trackAppFriends(int count) {
      this.setAction(28);
      this.setParameter(18, count);
      this.trackEvent();
      this.setParameter(18, count);
   }

   public void trackPostToFeedStarted(int postType) {
      this.mPost = POST_TYPES[postType];
      this.setType(23);
      this.setAction(2);
      this.setParameter(18, this.mPost);
      this.trackEvent();
   }

   public void trackPostToFeedCompleted() {
      this.setType(23);
      this.setAction(8);
      this.setParameter(18, this.mPost);
      this.trackEvent();
   }

   public void trackPostToFeedError() {
      this.setType(23);
      this.setAction(10);
      this.setParameter(18, this.mPost);
      this.trackEvent();
   }

   public void trackPostToFeedCancelled() {
      this.setType(23);
      this.setAction(4);
      this.setParameter(18, this.mPost);
      this.trackEvent();
   }

   @Override
   public void trackEvent() {
      super.trackEvent();
      this.removeParameter(18);
      this.setAction(30);
      this.setType(22);
   }
}
