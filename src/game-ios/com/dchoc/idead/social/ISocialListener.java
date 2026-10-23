package com.dchoc.idead.social;

import java.util.Vector;

public interface ISocialListener {
   int RESPONSE_ERROR = 1;
   int RESPONSE_OK = 0;
   int TYPE_FACEBOOK = 1;
   int TYPE_GAME_CENTER = 0;
   int TYPE_TWITTER = 2;

   void connect(int var1, int var2);

   void getAppFriends(int var1, Vector var2, int var3);

   void getFriends(int var1, Vector var2, int var3);

   void postToFeed(int var1, int var2);
}
