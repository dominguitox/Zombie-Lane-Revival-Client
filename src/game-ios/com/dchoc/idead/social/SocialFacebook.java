package com.dchoc.idead.social;

import com.dchoc.idead.GameEngine;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.player.Profile;
import com.dchoc.idead.servlets.BindPlatformAccount;
import com.dchoc.idead.tracking.SocialLoginEvent;
import com.dchoc.toolkit.FacebookAppFriendResponse;
import com.dchoc.toolkit.FacebookFriendResponse;
import com.dchoc.toolkit.FacebookPost;
import com.dchoc.toolkit.FacebookPostToFeedResponse;
import com.dchoc.toolkit.FacebookResponse;
import com.dchoc.toolkit.IFaceBookResponseListener;
import com.dchoc.toolkit.IFacebookConnection;
import com.dchoc.toolkit.UserProfile;
import java.util.Vector;
import java.util.function.Consumer;
import zlport.ios.Ios;

public class SocialFacebook implements IFaceBookResponseListener {
   public static final int INITIAL_FRIENDS_CAPACITY = 32;
   public static final int STATE_CONNECT = 1;
   public static final int STATE_GET_APP_FRIENDS = 3;
   public static final int STATE_GET_FRIENDS = 2;
   public static final int STATE_NONE = 0;
   public static final int STATE_POST_TO_FEED = 4;
   private boolean mConnected = false;
   private UserProfile mUserProfile;
   private ISocialListener mListener;
   private IFacebookConnection mConnection = null;
   private boolean mBindPlatformAccount;
   private SocialLoginEvent mTracking;

   public SocialFacebook() {
      this.mUserProfile = null;
      this.mListener = null;
      this.mBindPlatformAccount = true;
      this.mTracking = new SocialLoginEvent(1);
   }

   public int logicUpdate(int dt) {
      if (GameEngine.getInstance().isConnected() && this.isConnected() && this.mBindPlatformAccount && this.mUserProfile != null) {
         new BindPlatformAccount("2", this.mUserProfile.getId(), this.mUserProfile.getName()).execute();
         this.mBindPlatformAccount = false;
      }

      return 0;
   }

   public void setListener(ISocialListener listener) {
      this.mListener = listener;
   }

   public void connect() {
      this.mConnected = false;
      this.mUserProfile = null;
      this.mConnection = Ios.platform().facebookConnection("162387983821904", "", new Consumer<FacebookResponse>() {
         public void accept(FacebookResponse response) {
            SocialFacebook.this.responseReceived(response);
         }
      });
      this.mConnection.login();
      this.mTracking.trackStarted();
   }

   public boolean isConnected() {
      return this.mConnected;
   }

   public void getFriends() {
      if (this.mConnection != null) {
         this.mConnection.getFriends();
      }
   }

   public void getAppFriends() {
      if (this.mConnection != null) {
         this.mConnection.getAppFriends();
      }
   }

   public void postToFeed(FacebookPost post, int postType) {
      if (post != null) {
         this.mConnection.postToFeed(post);
         this.mTracking.trackPostToFeedStarted(postType);
      }
   }

   public String getName() {
      return this.mUserProfile != null ? this.mUserProfile.getName() : null;
   }

   public String getUserID() {
      return this.mUserProfile != null ? this.mUserProfile.getId() : null;
   }

   public void responseReceived(FacebookResponse response) {
      int error = response.getResponseState() != 0 ? 1 : 0;
      switch (this.getResponseType(response)) {
         case 1:
            if (error == 0) {
               this.updateUserProfle(this.mConnection.getCurrentUser());
               this.mConnected = true;
               this.mTracking.trackCompleted(this.mConnection.getCurrentUser().getId());
            } else {
               this.mTracking.trackError();
            }

            if (this.mListener != null) {
               this.mListener.connect(error, 1);
            }
            break;
         case 2:
            Vector friends = new Vector(32);
            if (error == 0) {
               this.updateUserProfle(this.mConnection.getCurrentUser());
               this.readFriends(((FacebookFriendResponse)response).getFriends(), friends);
               this.mConnected = true;
               this.mTracking.trackFriends(friends.size());
            }

            if (this.mListener != null) {
               this.mListener.getFriends(error, friends, 1);
            }
            break;
         case 3:
            Vector friends = new Vector(32);
            if (error == 0) {
               this.updateUserProfle(this.mConnection.getCurrentUser());
               this.readFriends(((FacebookAppFriendResponse)response).getFriends(), friends);
               this.mConnected = true;
               this.mTracking.trackAppFriends(friends.size());
            }

            if (this.mListener != null) {
               this.mListener.getAppFriends(error, friends, 1);
            }
            break;
         case 4:
            if (error == 0) {
               this.mTracking.trackPostToFeedCompleted();
            } else {
               this.mTracking.trackPostToFeedError();
            }
      }
   }

   private Vector readFriends(UserProfile[] profiles, Vector friends) {
      if (profiles != null) {
         for (int i = 0; i < profiles.length && i < 250; i++) {
            friends.addElement(new SocialFriend(profiles[i]));
         }
      }

      return friends;
   }

   private void updateUserProfle(UserProfile userProfile) {
      if (userProfile != null) {
         this.mUserProfile = userProfile;
         if (PlayerProfile.isInitialized()) {
            Profile profile = PlayerProfile.getProfile();
            profile.setName(this.mUserProfile.getName());
            profile.setPictureUrl(this.mUserProfile.getPictureUrl());
            profile.setFacebookID(this.mUserProfile.getId());
         }

         this.mBindPlatformAccount = true;
      }
   }

   private int getResponseType(FacebookResponse response) {
      if (response == null) {
         return 1;
      } else if (response instanceof FacebookAppFriendResponse) {
         return 3;
      } else if (response instanceof FacebookFriendResponse) {
         return 2;
      } else {
         return response instanceof FacebookPostToFeedResponse ? 4 : 1;
      }
   }
}
