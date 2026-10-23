package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUD;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDConnectivity;
import com.dchoc.hud.HUDItem;
import com.dchoc.hud.HUDItemAcceptGift;
import com.dchoc.hud.HUDItemAcceptNeighbor;
import com.dchoc.hud.HUDItemEmptyInvite;
import com.dchoc.hud.HUDItemEmptyMailbox;
import com.dchoc.hud.HUDItemEmptyNeighbors;
import com.dchoc.hud.HUDItemInvite;
import com.dchoc.hud.HUDItemMailbox;
import com.dchoc.hud.HUDItemNeighbor;
import com.dchoc.hud.HUDRedCounter;
import com.dchoc.hud.HUDTab;
import com.dchoc.hud.HUDTouchArea;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.commands.CommandBase;
import com.dchoc.idead.Time;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.neighbors.NeighborGift;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.social.ISocialListener;
import com.dchoc.idead.social.SocialFacebook;
import com.dchoc.idead.social.SocialFriend;
import com.dchoc.idead.social.SocialGameCenter;
import com.dchoc.playhaven.PlayHaven;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.FacebookPost;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import java.util.Set;
import java.util.Vector;
import zlport.ios.Ios;
import zlport.ios.LocalProfile;

public class WindowSocial extends Window implements ISocialListener {
   public static final int APPLICATION_FRIENDS_INITIAL_CAPACITY = 16;
   public static final int BUTTON_CLOSE = 0;
   public static final int BUTTON_CONNECTIVITY = 4;
   public static final int BUTTON_INVITE = 3;
   public static final int BUTTON_MAILBOX = 1;
   public static final int BUTTON_NEIGHBORS = 2;
   public static final int COLLISION_BACKGROUND = 2;
   public static final int COLLISION_CLOSE = 0;
   public static final int COLLISION_TITLE = 1;
   public static final int COLLISION_TOUCH_AREA = 3;
   public static final int FRIENDS_INITIAL_CAPACIITY = 16;
   public static final int GIFTS_INITIAL_CAPACITY = 32;
   public static final int INVITE_INITIAL_CAPACITY = 16;
   public static final int MAILBOX_INITIAL_CAPACITY = 16;
   public static final int NEIGHBORS_INITIAL_CAPACITY = 16;
   private static final String[] TAB_NAMES = new String[]{"tab_mailbox", "tab_neighbors", "tab_invite", "tab_connectivity"};
   private static final String LINK_URL = "http://itunes.apple.com/us/app/zombie-lane/id444441750?mt=8";
   private static final String IMAGE_URL = "http://dcassets2-a.cdngc.net/idead/STATIC/dchoc_zombielane.png";
   private static final String CAPTION = "Zombie Lane (TM)";
   public String mActiveAreaName;
   private HUDAutoTextField mTextTitle;
   private RoundRectangle mRectangleBackground;
   private HUDItemEmptyMailbox mEmptyMailbox;
   private HUDItemEmptyInvite mEmptyInvite;
   private HUDItemEmptyNeighbors mEmptyNeighbors;
   private HUDTouchArea mAreaMailbox;
   private HUDTouchArea mAreaNeighbors;
   private HUDTouchArea mAreaInvite;
   private HUDConnectivity mConnectivity;
   private HUDRedCounter mCounterMessages;
   private Vector mFacebookFriends;
   private Vector mFacebookAppFriends;
   private Vector mGameCenterFriends;
   private boolean mUpdatingFacebookFriends;
   private boolean mUpdatingFacebookAppFriends;
   private boolean mUpdatingGameCenterFriends;
   private boolean mSuppressPlayHaven;
   private boolean mConnectionTimeoutActive;
   private long mFacebookTimeoutStart;
   private long mGameCenterTimeoutStart;
   private int mConnectStartTimer;
   private int mNewMessageCount;
   private Vector mNeighbors;
   private Vector mGifts;
   private Vector mApplicationFriends;
   private FacebookPost mPendingPost;
   private int mPendingPostType;
   private long mRetryTimer;
   private HUDTab[] mTabs;

   public WindowSocial() {
      this.mCollisions = AnimationsManager.loadShared(74785);
      this.mBackground = AnimationsManager.loadShared(74619);
      this.initCollisions();
      this.constructCloseButton(0);
      this.mRectangleBackground = new RoundRectangle(0);
      this.mRectangleBackground.setSizes(this.mCollisions.getCollisionBox(2));
      this.mTextTitle = new HUDAutoTextField(this.mCollisions.getCollisionBox(1));
      this.mTextTitle.setCentered(true, true);
      this.mTextTitle.setText(2315, DCiDead.getFont(5));
      this.mTabs = new HUDTab[]{
         new HUDTab(1, this.mCollisions.getCollisionBox(8), this.mCollisions.getCollisionBox(4), 1836),
         new HUDTab(2, this.mCollisions.getCollisionBox(9), this.mCollisions.getCollisionBox(5), 1832),
         new HUDTab(3, this.mCollisions.getCollisionBox(10), this.mCollisions.getCollisionBox(6), 1834),
         new HUDTab(4, this.mCollisions.getCollisionBox(11), this.mCollisions.getCollisionBox(7), 2413)
      };

      for (int i = 0; i < (this.mTabs == null ? 0 : this.mTabs.length); i++) {
         this.mTabs[i].setNeighbors(this.mTabs);
      }

      this.mEmptyMailbox = new HUDItemEmptyMailbox();
      this.mEmptyInvite = new HUDItemEmptyInvite();
      this.mEmptyNeighbors = new HUDItemEmptyNeighbors();
      CollisionBox box = this.mCollisions.getCollisionBox(3);
      this.mConnectivity = new HUDConnectivity(box.getX(), box.getY());
      Vector items = new Vector(16);
      items.addElement(this.mEmptyMailbox);
      this.mAreaMailbox = new HUDTouchArea(false, true, false, this.mCollisions.getCollisionBox(3), items, true);
      this.mAreaMailbox.setScrollbar(true);
      this.mAreaMailbox.setFadingBorder(true);
      items = new Vector(16);
      items.addElement(this.mEmptyNeighbors);
      this.mAreaNeighbors = new HUDTouchArea(true, false, true, this.mCollisions.getCollisionBox(3), items, true);
      this.mAreaNeighbors.setScrollbar(true);
      this.mAreaNeighbors.setFadingBorder(true);
      this.mAreaNeighbors.setSorted(true);
      items = new Vector(16);
      items.addElement(this.mEmptyInvite);
      this.mAreaInvite = new HUDTouchArea(true, true, false, this.mCollisions.getCollisionBox(3), items, true);
      this.mAreaInvite.setScrollbar(true);
      this.mAreaInvite.setFadingBorder(true);
      this.mAreaInvite.setSortByStrings();
      this.mAreaInvite.setSorted(true);
      this.mCounterMessages = new HUDRedCounter();
      this.mObjects.addObject(this.mButtonClose);
      this.mObjects.addObject(this.mRectangleBackground);
      this.mObjects.addObject(this.mTextTitle);
      this.mObjects.addObject(this.mTabs[0]);
      this.mObjects.addObject(this.mTabs[1]);
      this.mObjects.addObject(this.mTabs[2]);
      this.mObjects.addObject(this.mTabs[3]);
      this.mObjects.addObject(this.mCounterMessages);
      this.mObjects.addObject(this.mAreaMailbox);
      this.mObjects.addObject(this.mAreaNeighbors);
      this.mObjects.addObject(this.mAreaInvite);
      this.mObjects.addObject(this.mConnectivity);
      GameEngine engine = GameEngine.getInstance();
      SocialFacebook facebook = engine.getFacebook();
      SocialGameCenter gameCenter = engine.getGameCenter();
      facebook.setListener(this);
      gameCenter.setListener(this);
      this.mNeighbors = new Vector(16);
      this.mGifts = new Vector(32);
      this.mApplicationFriends = new Vector(16);
      this.mFacebookAppFriends = new Vector(16);
      this.mGameCenterFriends = new Vector(16);
      this.mFacebookFriends = new Vector(16);
      this.mPendingPost = null;
      this.mRetryTimer = 0L;
      this.mNewMessageCount = 0;
      this.mConnectionTimeoutActive = false;
      this.mUpdatingFacebookFriends = false;
      this.mUpdatingFacebookAppFriends = false;
      this.mUpdatingGameCenterFriends = false;
      this.mSuppressPlayHaven = false;
      this.showMailbox();
   }

   @Override
   public void open() {
      super.open();
      for (LocalProfile profile : Ios.platform().localProfiles()) {
         if (this.getNeighbor(profile.id()) == null) {
            NeighborProfile neighbor = new NeighborProfile(profile.name(), profile.id(), 1, 3, 1926);
            PlayerProfile.acceptNeighbor(neighbor);
            this.addNeighbor(neighbor);
         }
      }

      if (this.isConnectingFacebook() || this.isConnectingGameCenter()) {
         this.updateConnectionTimeout(0);
      }

      this.mConnectivity.updateStatus();
      Vector items = this.mAreaNeighbors.getItems();

      for (int i = 0; i < items.size(); i++) {
         HUDItem item = (HUDItem)items.elementAt(i);
         if (item != this.mEmptyNeighbors) {
            HUDItemNeighbor neighborItem = (HUDItemNeighbor)item;
            NeighborProfile neighbor = neighborItem.getNeighbor();
            neighbor.updateGifts();
            neighborItem.setNeighbor(neighbor);
         }
      }

      if (HUD.getState() == 3) {
         this.showNeighbors(GameEngineUtils.getNeighbor());
      } else {
         if (this.mNewMessageCount > 0) {
            this.showMailbox();
         }
      }
   }

   public void showMailbox() {
      this.mTabs[0].press();
      this.mActiveAreaName = TAB_NAMES[0];
      this.setAreaVisible(true, false, false, false);
      this.trackTab();
   }

   public void showNeighbors(NeighborProfile neighbor) {
      this.mTabs[1].press();
      this.mActiveAreaName = TAB_NAMES[1];
      this.setAreaVisible(false, true, false, false);
      this.trackTab();
      if (neighbor != null) {
         Vector items = this.mAreaNeighbors.getItems();

         for (int i = 0; i < items.size(); i++) {
            HUDItem item = (HUDItem)items.elementAt(i);
            if (item != this.mEmptyNeighbors && ((HUDItemNeighbor)item).getNeighbor() == neighbor) {
               this.mAreaNeighbors.setPage(i);
               return;
            }
         }
      }
   }

   public void showInvite() {
      this.mTabs[2].press();
      this.mActiveAreaName = TAB_NAMES[2];
      this.setAreaVisible(false, false, true, false);
      this.trackTab();
      ((WindowNpcDialog)WindowManager.getWindow((byte)19)).checkConnectionDialog();
   }

   public void showConnectivity() {
      this.mTabs[3].press();
      this.mActiveAreaName = TAB_NAMES[3];
      this.setAreaVisible(false, false, false, true);
      this.trackTab();
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      this.updateConnectionTimeout(delta);
      if (!this.mUpdatingFacebookAppFriends && !this.mFacebookAppFriends.isEmpty()) {
         this.addAppFriends(this.mFacebookAppFriends);
      }

      if (!this.mUpdatingFacebookFriends && !this.mFacebookFriends.isEmpty()) {
         this.addFriends(this.mFacebookFriends);
      }

      if (!this.mUpdatingGameCenterFriends && !this.mGameCenterFriends.isEmpty()) {
         this.addAppFriends(this.mGameCenterFriends);
      }

      this.updateMailbox(delta);
      this.updateInvites(delta);
      this.mEmptyMailbox.logicUpdate(delta);
      this.mEmptyInvite.logicUpdate(delta);
      this.mEmptyNeighbors.logicUpdate(delta);
      this.updateCounterPosition();
   }

   public void updateApplicationFriends(int delta) {
      this.mRetryTimer += delta;
      boolean retry = this.mRetryTimer > 30000L;
      int inviting = 0;
      int i = 0;

      while (i < this.mApplicationFriends.size()) {
         SocialFriend friend = (SocialFriend)this.mApplicationFriends.elementAt(i);
         if (!friend.isAdded() && (friend.isInviting() || !friend.isInviteAdded() && !friend.isInviteFailed())) {
            if (friend.isInviting()) {
               inviting++;
            }

            i++;
         } else {
            ToolkitHelpers.fastRemove(i, this.mApplicationFriends);
         }
      }

      if (GameEngineUtils.isConnected()) {
         if (this.mApplicationFriends.size() > 0 && inviting < 2) {
            i = 0;

            do {
               SocialFriend friend = (SocialFriend)this.mApplicationFriends.elementAt(i);
               if (!friend.isInviting() && !friend.isInviteAdded() && !friend.isInviteFailed()) {
                  friend.invite();
                  inviting++;
               }
            } while (++i < this.mApplicationFriends.size() && inviting < 2);
         }

         if (this.mApplicationFriends.size() > 0 && inviting < 2) {
            i = 0;

            do {
               SocialFriend friend = (SocialFriend)this.mApplicationFriends.elementAt(i);
               if (!friend.isInviting() && retry) {
                  this.mRetryTimer = 0L;
                  friend.invite();
                  inviting++;
               }
            } while (++i < this.mApplicationFriends.size() && inviting < 2);
         }
      }
   }

   public void addAppFriends(Vector friends) {
      if (friends != null && !friends.isEmpty()) {
         for (int i = 0; i < friends.size(); i++) {
            SocialFriend friend = (SocialFriend)friends.elementAt(i);
            if (!this.isNeighbor(friend)) {
               this.isInviteAdded(friend, true);
               this.mApplicationFriends.addElement(friend);
            }
         }

         friends.removeAllElements();
      }
   }

   public void addFriends(Vector friends) {
      if (friends != null && !friends.isEmpty()) {
         for (int i = 0; i < friends.size(); i++) {
            SocialFriend friend = (SocialFriend)friends.elementAt(i);
            if (!this.isNeighbor(friend) && !this.isInviteAdded(friend, false)) {
               HUDItemInvite item = new HUDItemInvite();
               item.setSocialFriend(friend);
               this.addInviteItem(item);
            }
         }

         friends.removeAllElements();
      }
   }

   public boolean isNeighbor(SocialFriend friend) {
      for (int i = 0; i < this.mNeighbors.size(); i++) {
         if (((NeighborProfile)this.mNeighbors.elementAt(i)).compare(friend)) {
            return true;
         }
      }

      for (int i = 0; i < this.mApplicationFriends.size(); i++) {
         if (((SocialFriend)this.mApplicationFriends.elementAt(i)).compare(friend)) {
            return true;
         }
      }

      return false;
   }

   public void addNeighbor(NeighborProfile neighbor) {
      if (neighbor != null) {
         if (this.getNeighbor(neighbor.getUserID()) == null) {
            for (int i = 0; i < this.mApplicationFriends.size(); i++) {
               SocialFriend friend = (SocialFriend)this.mApplicationFriends.elementAt(i);
               if (neighbor.compare(friend)) {
                  friend.setAdded(true);
                  break;
               }
            }

            Vector items = this.mAreaInvite.getItems();

            for (int i = 0; i < items.size(); i++) {
               if (items.elementAt(i) != this.mEmptyInvite) {
                  HUDItemInvite invite = (HUDItemInvite)items.elementAt(i);
                  if (neighbor.compare(invite.getSocialFriend())) {
                     invite.setAccepted(true);
                     break;
                  }
               }
            }

            HUDItemNeighbor item = new HUDItemNeighbor();
            item.setNeighbor(neighbor);
            if (this.mAreaNeighbors.getItems().size() == 1) {
               this.mAreaNeighbors.removeItem(this.mEmptyNeighbors);
            }

            this.mAreaNeighbors.addItem(item);
            this.mNeighbors.removeAllElements();
            items = this.mAreaNeighbors.getItems();

            for (int i = 0; i < items.size(); i++) {
               HUDItemNeighbor neighborItem = (HUDItemNeighbor)items.elementAt(i);
               this.mNeighbors.addElement(neighborItem.getNeighbor());
            }

            if (!PlayerProfile.isNeighborAccepted(neighbor)) {
               HUDItemAcceptNeighbor accept = new HUDItemAcceptNeighbor();
               accept.setNeighbor(neighbor);
               this.addMailboxItem(accept);
            }
         }
      }
   }

   public NeighborProfile getNeighbor(String userId) {
      if (userId == null) {
         return null;
      }

      for (int i = 0; i < this.mNeighbors.size(); i++) {
         NeighborProfile neighbor = (NeighborProfile)this.mNeighbors.elementAt(i);
         if (userId.equals(neighbor.getUserID())) {
            return neighbor;
         }
      }

      return null;
   }

   public Vector getNeighbors() {
      return this.mNeighbors;
   }

   public void addGift(NeighborGift gift) {
      if (gift != null) {
         if (this.getGift(gift.getID()) == null) {
            this.mGifts.addElement(gift);
            HUDItemAcceptGift item = new HUDItemAcceptGift();
            item.setGift(gift);
            this.addMailboxItem(item);
         }
      }
   }

   public void syncPendingGifts(Set<Long> pendingIds) {
      if (pendingIds == null) {
         return;
      }
      for (int i = this.mGifts.size() - 1; i >= 0; i--) {
         NeighborGift gift = (NeighborGift)this.mGifts.elementAt(i);
         if (gift.getID() == -1L || pendingIds.contains(gift.getID())) {
            continue;
         }
         this.mGifts.removeElementAt(i);
         this.removeGiftMailboxItem(gift);
      }
   }

   public boolean syncPendingActions(Set<Long> pendingIds) {
      if (pendingIds == null) {
         return false;
      }
      boolean changed = false;
      for (int n = 0; n < this.mNeighbors.size(); n++) {
         NeighborProfile neighbor = (NeighborProfile)this.mNeighbors.elementAt(n);
         Vector commands = neighbor.getCommands();
         for (int i = commands.size() - 1; i >= 0; i--) {
            CommandBase command = (CommandBase)commands.elementAt(i);
            if (command.getType() == 0 && !command.isAccepted() && !pendingIds.contains(command.getID())) {
               neighbor.removeCommand(command.getID());
               changed = true;
            }
         }
      }
      return changed;
   }

   private void removeGiftMailboxItem(NeighborGift gift) {
      Vector items = this.mAreaMailbox.getItems();
      for (int i = 0; i < items.size(); i++) {
         HUDItem item = (HUDItem)items.elementAt(i);
         if (item instanceof HUDItemAcceptGift && ((HUDItemAcceptGift)item).getGift() == gift) {
            this.mAreaMailbox.removeItem(item);
            if (this.mNewMessageCount > 0) {
               this.mNewMessageCount--;
            }
            if (this.mAreaMailbox.getItems().isEmpty()) {
               this.mAreaMailbox.addItem(this.mEmptyMailbox);
            }
            return;
         }
      }
   }

   public NeighborGift getGift(long id) {
      if (id == -1L) {
         return null;
      }

      for (int i = 0; i < this.mGifts.size(); i++) {
         NeighborGift gift = (NeighborGift)this.mGifts.elementAt(i);
         if (gift.getID() == id) {
            return gift;
         }
      }

      return null;
   }

   public void setSuppressPlayHaven() {
      this.mSuppressPlayHaven = true;
   }

   public boolean isSuppressPlayHaven() {
      return this.mSuppressPlayHaven;
   }

   public int getNewMessageCount() {
      return this.mNewMessageCount;
   }

   public int getActionsLeft() {
      int actions = 0;

      for (int i = 0; i < this.mNeighbors.size(); i++) {
         actions += ((NeighborProfile)this.mNeighbors.elementAt(i)).getActionsLeft();
      }

      return actions;
   }

   public int getGiftsLeft() {
      Vector items = this.mAreaMailbox.getItems();
      int gifts = 0;

      for (int i = 0; i < items.size(); i++) {
         if (((HUDItem)items.elementAt(i)).getType() == 18) {
            gifts++;
         }
      }

      return gifts;
   }

   public void postInvite(SocialFriend friend) {
      if (friend != null && friend.getFacebookID() != null) {
         String title = Toolkit.getText(2296);
         String text = Toolkit.getText(2300);
         this.executePost(
            new FacebookPost(
               title,
               text,
               "http://itunes.apple.com/us/app/zombie-lane/id444441750?mt=8",
               "http://dcassets2-a.cdngc.net/idead/STATIC/dchoc_zombielane.png",
               "Zombie Lane (TM)",
               friend.getFacebookID()
            ),
            0
         );
      }
   }

   public void postTwitter(String text) {
      if (text != null) {
         GameEngine.getInstance().getTwitter().postToFeed(text, null);
      }
   }

   public void postFacebook(String title, String text, String image, int type) {
      if (title != null && text != null) {
         String target = PlayerProfile.getProfile().getFacebookID();
         if (image == null) {
            image = "http://dcassets2-a.cdngc.net/idead/STATIC/dchoc_zombielane.png";
         }

         this.executePost(new FacebookPost(title, text, "http://itunes.apple.com/us/app/zombie-lane/id444441750?mt=8", image, "Zombie Lane (TM)", target), type);
      }
   }

   public void clearPendingPost() {
      this.mPendingPost = null;
   }

   @Override
   public void callback(int id) {
      switch (id) {
         case 0:
            WindowManager.closeWindow();
            this.mConnectivity.updateActivityIndicators(false);
            break;
         case 1:
            this.showMailbox();
            break;
         case 2:
            this.showNeighbors(null);
            break;
         case 3:
            this.showInvite();
            break;
         case 4:
            this.showConnectivity();
      }

      this.updateCounterPosition();
   }

   public boolean isConnectingFacebook() {
      return this.mUpdatingFacebookFriends || this.mUpdatingFacebookAppFriends;
   }

   public boolean isConnectingGameCenter() {
      return this.mUpdatingGameCenterFriends;
   }

   public void getFacebookFriends() {
      SocialFacebook facebook = GameEngine.getInstance().getFacebook();
      this.mFacebookFriends.removeAllElements();
      this.mFacebookAppFriends.removeAllElements();
      this.mUpdatingFacebookFriends = true;
      this.mUpdatingFacebookAppFriends = true;
      this.mConnectionTimeoutActive = false;
      this.mConnectStartTimer = GameEngineUtils.getTimer();
      facebook.connect();
   }

   public void getGameCenterFriends() {
      SocialGameCenter gameCenter = GameEngine.getInstance().getGameCenter();
      this.mGameCenterFriends.removeAllElements();
      this.mUpdatingGameCenterFriends = true;
      this.mConnectionTimeoutActive = false;
      this.mConnectStartTimer = GameEngineUtils.getTimer();
      gameCenter.connect();
   }

   @Override
   public void connect(int result, int network) {
      GameEngine engine = GameEngine.getInstance();
      SocialFacebook facebook = engine.getFacebook();
      SocialGameCenter gameCenter = engine.getGameCenter();
      if (network == 0) {
         if (result == 0) {
            gameCenter.getAppFriends();
         } else {
            this.debug("Failed to connect to GameCenter");
         }
      } else if (network == 1) {
         if (result == 0) {
            if (this.mPendingPost != null) {
               this.executePost(this.mPendingPost, this.mPendingPostType);
            }

            facebook.getAppFriends();
         } else {
            this.debug("Failed to connect to Facebook");
         }
      }

      this.mConnectivity.updateStatus();
   }

   public void cancelConnect(boolean facebook, boolean gameCenter) {
      if (facebook) {
         this.mUpdatingFacebookFriends = false;
         this.mUpdatingFacebookAppFriends = false;
      } else if (gameCenter) {
         this.mUpdatingGameCenterFriends = false;
      }

      this.mConnectivity.updateStatus();
      this.mConnectionTimeoutActive = false;
   }

   @Override
   public void getFriends(int result, Vector friends, int network) {
      if (network == 1) {
         if (result == 0) {
            if (friends != null && this.mFacebookFriends != null && friends.size() > 0) {
               Vector copy = this.mFacebookFriends;

               for (int i = 0; i < friends.size(); i++) {
                  Object friend = friends.elementAt(i);
                  if (friend != null) {
                     copy.addElement(friend);
                  }
               }
            }
         } else {
            this.debug("Failed to get Facebook friends");
         }

         this.mUpdatingFacebookFriends = false;
         if (!this.mUpdatingFacebookAppFriends && !this.mUpdatingGameCenterFriends) {
            this.mConnectionTimeoutActive = false;
         }
      }

      this.mConnectivity.updateStatus();
   }

   @Override
   public void getAppFriends(int result, Vector friends, int network) {
      if (network == 0) {
         if (result == 0) {
            if (friends != null && this.mGameCenterFriends != null && friends.size() > 0) {
               Vector copy = this.mGameCenterFriends;

               for (int i = 0; i < friends.size(); i++) {
                  Object friend = friends.elementAt(i);
                  if (friend != null) {
                     copy.addElement(friend);
                  }
               }
            }
         } else {
            this.debug("Failed to get GameCenter application friends");
         }

         this.mUpdatingGameCenterFriends = false;
         if (!this.mUpdatingFacebookFriends && !this.mUpdatingFacebookAppFriends) {
            this.mConnectionTimeoutActive = false;
         }
      } else if (network == 1) {
         if (result == 0) {
            if (friends != null && this.mFacebookAppFriends != null && friends.size() > 0) {
               Vector copy = this.mFacebookAppFriends;

               for (int i = 0; i < friends.size(); i++) {
                  Object friend = friends.elementAt(i);
                  if (friend != null) {
                     copy.addElement(friend);
                  }
               }
            }
         } else {
            this.debug("Failed to get Facebook application friends");
         }

         this.mUpdatingFacebookAppFriends = false;
         if (!this.mUpdatingFacebookFriends && !this.mUpdatingGameCenterFriends) {
            this.mConnectionTimeoutActive = false;
         }

         GameEngine.getInstance().getFacebook().getFriends();
      }

      this.mConnectivity.updateStatus();
   }

   @Override
   public void postToFeed(int result, int type) {
   }

   public void debug(String message) {
      System.out.println("WindowSocial: " + message);
   }

   @Override
   public String getActiveAreaName() {
      return this.mActiveAreaName;
   }

   public HUDTouchArea getMailbox() {
      return this.mAreaMailbox;
   }

   public void keyEventOccured(int key, int action, int type) {
      if (this.mConnectivity != null) {
         this.mConnectivity.keyEventOccured(key, action, type);
      }
   }

   @Override
   public void setState(byte state) {
      if (state == 1) {
         if (!this.mSuppressPlayHaven) {
            PlayHaven.triggerCloseSocialMenu();
         }

         this.mSuppressPlayHaven = false;
      } else if (state == 0) {
         if (!this.mConnectivity.isVisible()) {
            this.mConnectivity.updateActivityIndicators(false);
         }

         if (!this.mSuppressPlayHaven) {
            PlayHaven.triggerOpenSocialMenu();
         }

         this.mSuppressPlayHaven = false;
      }

      super.setState(state);
   }

   public void setAreaVisible(boolean mailbox, boolean neighbors, boolean invite, boolean connectivity) {
      this.mAreaMailbox.setVisible(mailbox);
      this.mAreaNeighbors.setVisible(neighbors);
      this.mAreaInvite.setVisible(invite);
      this.mConnectivity.setVisible(connectivity);
      if (!connectivity) {
         this.mConnectivity.updateActivityIndicators(false);
      }
   }

   public void updateConnectionTimeout(int delta) {
      if (!this.mConnectionTimeoutActive) {
         if (this.mUpdatingFacebookFriends || this.mUpdatingFacebookAppFriends || this.mUpdatingGameCenterFriends) {
            int timer = GameEngineUtils.getTimer();
            if (this.mConnectStartTimer + 3000L <= timer) {
               this.mConnectionTimeoutActive = true;
               if (this.mUpdatingFacebookFriends || this.mUpdatingFacebookAppFriends) {
                  this.mFacebookTimeoutStart = Time.getTimestamp();
               }

               if (!this.mUpdatingGameCenterFriends) {
                  return;
               }

               this.mGameCenterTimeoutStart = Time.getTimestamp();
            }
         }
      } else {
         if ((this.mUpdatingFacebookFriends || this.mUpdatingFacebookAppFriends) && Time.getTimestampDelta(this.mFacebookTimeoutStart) > 30000L) {
            this.cancelConnect(true, false);
         }

         if (this.mUpdatingGameCenterFriends && Time.getTimestampDelta(this.mGameCenterTimeoutStart) > 30000L) {
            this.cancelConnect(false, true);
         }
      }
   }

   public void updateMailbox(int delta) {
      Vector items = this.mAreaMailbox.getItems();

      for (int i = 0; i < items.size(); i++) {
         HUDItem item = (HUDItem)items.elementAt(i);
         if (item != this.mEmptyMailbox && ((HUDItemMailbox)item).isAccepted()) {
            this.removeMailboxItem(item);
            return;
         }
      }
   }

   public void updateInvites(int delta) {
      Vector items = this.mAreaInvite.getItems();

      for (int i = 0; i < items.size(); i++) {
         HUDItem item = (HUDItem)items.elementAt(i);
         if (item != this.mEmptyInvite) {
            HUDItemInvite invite = (HUDItemInvite)item;
            SocialFriend friend = invite.getSocialFriend();
            if (friend != null && friend.isAdded()) {
               this.removeInviteItem(invite);
               return;
            }
         }
      }
   }

   public void addInviteItem(HUDItemInvite item) {
      if (this.mAreaInvite.getItems().size() == 1) {
         this.mAreaInvite.removeItem(this.mEmptyInvite);
      }

      this.mAreaInvite.addItem(item);
   }

   public void removeInviteItem(HUDItemInvite item) {
      this.mAreaInvite.removeItem(item);
      if (this.mAreaInvite.getItems().isEmpty()) {
         this.mAreaInvite.addItem(this.mEmptyInvite);
      }
   }

   public boolean isInviteAdded(SocialFriend friend, boolean remove) {
      Vector items = this.mAreaInvite.getItems();

      for (int i = 0; i < items.size(); i++) {
         if (items.elementAt(i) != this.mEmptyInvite) {
            HUDItemInvite invite = (HUDItemInvite)items.elementAt(i);
            if (friend.compare(invite.getSocialFriend())) {
               if (remove) {
                  this.removeInviteItem(invite);
               }

               return true;
            }
         }
      }

      return false;
   }

   public void addMailboxItem(HUDItem item) {
      if (item != null) {
         if (this.mAreaMailbox.getItems().size() == 1) {
            this.mAreaMailbox.removeItem(this.mEmptyMailbox);
         }

         this.mAreaMailbox.addItem(item);
         this.mNewMessageCount++;
      }
   }

   public void removeMailboxItem(HUDItem item) {
      if (item != null) {
         this.mAreaMailbox.removeItem(item);
         this.mNewMessageCount--;
         if (this.mAreaMailbox.getItems().isEmpty()) {
            this.mAreaMailbox.addItem(this.mEmptyMailbox);
         }

         if (item.getType() == 18) {
            if (this.getGiftsLeft() == 0) {
               GameEngine.getInstance().setGetAccountInformation();
            }
         }
      }
   }

   public void executePost(FacebookPost post, int type) {
      SocialFacebook facebook = GameEngine.getInstance().getFacebook();
      this.mPendingPost = null;
      if (facebook.isConnected()) {
         if (post.getTargetUserId() == null) {
            post.setTargetUserId(facebook.getUserID());
         }

         facebook.postToFeed(post, type);
      } else {
         this.mPendingPost = post;
         this.mPendingPostType = type;
         this.getFacebookFriends();
      }
   }

   public void updateCounterPosition() {
      CollisionBox box = this.mCollisions.getCollisionBox(this.mTabs[0].isPressed() ? 8 : 4);
      HUDRedCounter counter = this.mCounterMessages;
      counter.setPosition(box.getX() + box.getWidth(), box.getY() + this.mCounterMessages.getHeight() / 2);
      this.mCounterMessages.setValue(this.mNewMessageCount);
   }
}
