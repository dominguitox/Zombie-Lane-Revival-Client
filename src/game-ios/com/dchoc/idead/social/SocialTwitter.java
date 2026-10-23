package com.dchoc.idead.social;

import com.dchoc.idead.tracking.SocialLoginEvent;
import java.util.function.IntConsumer;
import zlport.ios.Ios;

public class SocialTwitter {
   private static ISocialListener smListener;
   private SocialLoginEvent mTracking;

   public SocialTwitter() {
      smListener = null;
      this.mTracking = new SocialLoginEvent(2);
   }

   public int logicUpdate(int dt) {
      return 0;
   }

   public void setListener(ISocialListener listener) {
      smListener = listener;
   }

   public void dummyMethod() {
   }

   public void postToFeed(String text, String url) {
      this.mTracking.trackPostToFeedStarted(1);
      if (Ios.platform().canSendTweet()) {
         Ios.platform().presentTweetComposer(text, url, new IntConsumer() {
            @Override
            public void accept(int result) {
               SocialTwitter.this.tweetFinished(result);
            }
         });
      }
   }

   public boolean isAvailable() {
      return Ios.platform().canSendTweet();
   }

   private void tweetFinished(int result) {
      if (result == 0) {
         this.mTracking.trackPostToFeedCancelled();
         if (smListener != null) {
            smListener.postToFeed(1, 2);
         }
      } else if (result == 1) {
         this.mTracking.trackPostToFeedCompleted();
         if (smListener != null) {
            smListener.postToFeed(0, 2);
         }
      } else {
         this.mTracking.trackPostToFeedError();
      }
   }
}
