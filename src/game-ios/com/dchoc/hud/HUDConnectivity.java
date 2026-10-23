package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.social.SocialFacebook;
import com.dchoc.idead.social.SocialFriend;
import com.dchoc.idead.social.SocialGameCenter;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tracking.FriendCodeEvent;
import com.dchoc.iphonewrappers.ActivityWrapper;
import com.dchoc.iphonewrappers.SettingsWrapper;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.WindowSocial;

public class HUDConnectivity extends HUDObject {
   public static final int BUTTON_ADD_FRIEND_BY_FRIENDCODE = 4;
   public static final int BUTTON_FACEBOOK = 0;
   public static final int BUTTON_GAME_CENTER = 1;
   public static final int BUTTON_SHARE_TO_FACEBOOK = 3;
   public static final int COLLISION_BUTTON_1 = 12;
   public static final int COLLISION_BUTTON_2 = 13;
   public static final int COLLISION_BUTTON_3 = 14;
   public static final int COLLISION_CONNECTING_1 = 9;
   public static final int COLLISION_CONNECTING_2 = 10;
   public static final int COLLISION_CONNECTING_3 = 11;
   public static final int COLLISION_DELIMETER_1 = 20;
   public static final int COLLISION_DELIMETER_2 = 21;
   public static final int COLLISION_IMAGE_1 = 1;
   public static final int COLLISION_IMAGE_2 = 2;
   public static final int COLLISION_IMAGE_3 = 3;
   public static final int COLLSION_TEXT_1 = 5;
   public static final int COLLSION_TEXT_2 = 6;
   public static final int COLLSION_TEXT_3 = 7;
   public static final int COLLSION_TEXT_5 = 23;
   public static final int COLLSION_TEXT_6 = 24;
   private static final String HYPHEN_DELIMETER = "-";
   private HUDCollection mCollection;
   private HUDButton mButtonFacebook;
   private HUDButton mButtonGameCenter;
   private HUDButton mButtonShareFriendcode;
   private HUDButton mButtonAddFriend;
   private HUDImage mImageFacebook;
   private HUDImage mImageGameCenter;
   private HUDImage mImageDchoc;
   private HUDAutoTextField mTextFacebook;
   private HUDAutoTextField mTextGameCenter;
   private HUDAutoTextField mTextMyFriendCode;
   private HUDAutoTextField mTextEnterFriendcode;
   private HUDAutoTextField mTextFriendcodeResponseMessage;
   private HUDAutoTextField mTextHyphen1;
   private HUDAutoTextField mTextHyphen2;
   private Rectangle mDelimeter1;
   private Rectangle mDelimeter2;
   private SpriteObject mLayout;
   private SocialFriend mSocialFriend;
   private FriendCodeEvent mFriendCodeEvent;
   private boolean mTrackFriendCode;
   private HUDInputField[] mInputFields;

   public HUDConnectivity(int x, int y) {
      super((byte)-1);
      this.setPosition(x, y);
      this.mLayout = AnimationsManager.loadShared(74837);
      this.mCollection = new HUDCollection();
      this.mCollection.setParent(this);
      CollisionBox box = this.mLayout.getCollisionBox(1);
      this.mImageFacebook = new HUDImage(74821, box.getX() + (box.getWidth() >> 1), box.getY() + (box.getHeight() >> 1));
      this.mCollection.addObject(this.mImageFacebook);
      box = this.mLayout.getCollisionBox(2);
      this.mImageGameCenter = new HUDImage(74823, box.getX() + (box.getWidth() >> 1), box.getY() + (box.getHeight() >> 1));
      this.mCollection.addObject(this.mImageGameCenter);
      box = this.mLayout.getCollisionBox(3);
      this.mImageDchoc = new HUDImage(74819, box.getX() + (box.getWidth() >> 1), box.getY() + (box.getHeight() >> 1));
      this.mCollection.addObject(this.mImageDchoc);
      this.mTextFacebook = new HUDAutoTextField(this.mLayout.getCollisionBox(5));
      this.mTextFacebook.setCentered(false, true);
      this.mTextFacebook.setSplitUsingWidth(true);
      this.mCollection.addObject(this.mTextFacebook);
      this.mTextGameCenter = new HUDAutoTextField(this.mLayout.getCollisionBox(6));
      this.mTextGameCenter.setCentered(false, true);
      this.mTextGameCenter.setSplitUsingWidth(true);
      this.mCollection.addObject(this.mTextGameCenter);
      this.mTextMyFriendCode = new HUDAutoTextField(this.mLayout.getCollisionBox(7));
      this.mTextMyFriendCode.setCentered(false, true);
      this.mTextMyFriendCode.setScaleToFit(true);
      this.mCollection.addObject(this.mTextMyFriendCode);
      this.mTextEnterFriendcode = new HUDAutoTextField(this.mLayout.getCollisionBox(23));
      this.mTextEnterFriendcode.setCentered(true, true);
      this.mTextEnterFriendcode.setSplitUsingWidth(true);
      this.mTextEnterFriendcode.setScaleToFit(true);
      this.mTextEnterFriendcode.searchScale(true);
      this.mTextEnterFriendcode.setText(2737, 3);
      this.mCollection.addObject(this.mTextEnterFriendcode);
      this.mTextFriendcodeResponseMessage = new HUDAutoTextField(this.mLayout.getCollisionBox(24));
      this.mTextFriendcodeResponseMessage.setCentered(true, true);
      this.mTextFriendcodeResponseMessage.setSplitUsingWidth(true);
      this.mCollection.addObject(this.mTextFriendcodeResponseMessage);
      box = this.mLayout.getCollisionBox(12);
      this.mButtonFacebook = new HUDButton(0, box.getX(), box.getY(), 66245);
      this.mCollection.addObject(this.mButtonFacebook);
      box = this.mLayout.getCollisionBox(13);
      this.mButtonGameCenter = new HUDButton(1, box.getX(), box.getY(), 66245);
      this.mCollection.addObject(this.mButtonGameCenter);
      box = this.mLayout.getCollisionBox(14);
      this.mButtonShareFriendcode = new HUDButton(3, box.getX(), box.getY(), 66245);
      this.mButtonShareFriendcode.setIcon(-1);
      this.mButtonShareFriendcode.setText(106);
      this.mButtonShareFriendcode.setEnabled(true);
      this.mCollection.addObject(this.mButtonShareFriendcode);
      box = this.mLayout.getCollisionBox(15);
      this.mButtonAddFriend = new HUDButton(4, box.getX(), box.getY(), 66245);
      this.mButtonAddFriend.setText(2738);
      this.mCollection.addObject(this.mButtonAddFriend);
      box = new CollisionBox(
         this.mLayout.getCollisionBox(30).getX(),
         this.mLayout.getCollisionBox(30).getY(),
         this.mLayout.getCollisionBox(31).getX() + this.mLayout.getCollisionBox(31).getWidth() - this.mLayout.getCollisionBox(30).getX(),
         this.mLayout.getCollisionBox(31).getHeight(),
         1
      );
      this.mTextHyphen1 = new HUDAutoTextField(box);
      this.mTextHyphen1.setText("-", 4);
      this.mTextHyphen1.setCentered(true, true);
      this.mCollection.addObject(this.mTextHyphen1);
      box = new CollisionBox(
         this.mLayout.getCollisionBox(31).getX(),
         this.mLayout.getCollisionBox(31).getY(),
         this.mLayout.getCollisionBox(32).getX() + this.mLayout.getCollisionBox(32).getWidth() - this.mLayout.getCollisionBox(31).getX(),
         this.mLayout.getCollisionBox(32).getHeight(),
         1
      );
      this.mTextHyphen2 = new HUDAutoTextField(box);
      this.mTextHyphen2.setText("-", 4);
      this.mTextHyphen2.setCentered(true, true);
      this.mCollection.addObject(this.mTextHyphen2);
      box = this.mLayout.getCollisionBox(20);
      if (box != null) {
         this.mDelimeter1 = new Rectangle(box, this, 12429940);
         this.mCollection.addObject(this.mDelimeter1);
      }

      box = this.mLayout.getCollisionBox(21);
      if (box != null) {
         this.mDelimeter2 = new Rectangle(box, this, 12429940);
         this.mCollection.addObject(this.mDelimeter2);
      }

      this.mInputFields = new HUDInputField[3];

      for (int i = 0; i < 3; i++) {
         this.mInputFields[i] = new HUDInputField(this.mLayout.getCollisionBox(i + 30));
         this.mCollection.addObject(this.mInputFields[i]);
      }

      this.mFriendCodeEvent = new FriendCodeEvent();
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         this.mCollection.draw();
      }
   }

   public void updateStatus() {
      GameEngine engine = GameEngine.getInstance();
      SocialFacebook facebook = engine.getFacebook();
      SocialGameCenter gameCenter = engine.getGameCenter();
      HUDAutoTextField text = this.mTextFacebook;
      if (facebook.isConnected()) {
         text.setText(2415, 4, facebook.getName(), -1);
      } else {
         text.setText(2416, 4);
      }

      boolean connected = gameCenter.isConnected();
      text = this.mTextGameCenter;
      if (connected) {
         text.setText(2415, 4, gameCenter.getName(), -1);
      } else {
         text.setText(2416, 4);
      }

      String code = PlayerProfile.getFriendCode();
      if (!code.equals("") && code.length() == 9) {
         String friendCode = Toolkit.getText(2744) + " " + code.substring(0, 3) + "-" + code.substring(3, 6) + "-" + code.substring(6, 9);
         text = this.mTextMyFriendCode;
         text.setText(friendCode, DCiDead.getFont(4));
      } else {
         connected = engine.isConnected();
         text = this.mTextMyFriendCode;
         if (!connected) {
            text.setText(2416, DCiDead.getFont(4));
         } else {
            text.setText(2414, DCiDead.getFont(4));
         }

         this.mTextMyFriendCode.setScaleToFit(connected);
      }

      boolean visible = engine.isConnected();
      this.mInputFields[0].setVisible(visible);
      this.mInputFields[1].setVisible(visible);
      this.mInputFields[2].setVisible(visible);
      this.mTextHyphen1.setVisible(visible);
      this.mTextHyphen2.setVisible(visible);
      this.mButtonShareFriendcode.setVisible(visible);
      this.mButtonAddFriend.setVisible(visible);
      this.mTextFriendcodeResponseMessage.setVisible(visible);
      this.mTextEnterFriendcode.setVisible(visible);
   }

   public void updateActivityIndicators(boolean show) {
      if (show) {
         WindowSocial window = (WindowSocial)WindowManager.getWindow((byte)18);
         if (window.isConnectingFacebook()) {
            CollisionBox box = this.mLayout.getCollisionBox(9);
            ActivityWrapper.show(1, this.getX() + box.getX() + (box.getWidth() >> 1), this.getY() + box.getY() + (box.getHeight() >> 1));
         } else {
            ActivityWrapper.hide(1);
         }

         if (window.isConnectingGameCenter()) {
            CollisionBox box = this.mLayout.getCollisionBox(10);
            ActivityWrapper.show(2, this.getX() + box.getX() + (box.getWidth() >> 1), this.getY() + box.getY() + (box.getHeight() >> 1));
         } else {
            ActivityWrapper.hide(2);
         }

         if (this.mSocialFriend != null && this.mSocialFriend.isInviting()) {
            CollisionBox box = this.mLayout.getCollisionBox(11);
            ActivityWrapper.show(3, this.getX() + box.getX() + (box.getWidth() >> 1), this.getY() + box.getY() + (box.getHeight() >> 1));
            return;
         }
      } else {
         ActivityWrapper.hide(1);
         ActivityWrapper.hide(2);
      }

      ActivityWrapper.hide(3);
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.isVisible()) {
         this.mCollection.logicUpdate(delta);
         GameEngine engine = GameEngine.getInstance();
         SocialFacebook facebook = engine.getFacebook();
         SocialGameCenter gameCenter = engine.getGameCenter();
         WindowSocial window = (WindowSocial)WindowManager.getWindow((byte)18);
         if (window.isConnectingFacebook()) {
            this.mButtonFacebook.changeStyle(3);
            this.mButtonFacebook.setText(2422);
         } else if (facebook.isConnected()) {
            this.mButtonFacebook.changeStyle(6);
            this.mButtonFacebook.setText(2421);
         } else {
            this.mButtonFacebook.changeStyle(3);
            this.mButtonFacebook.setText(2420);
         }

         if (window.isConnectingGameCenter()) {
            this.mButtonGameCenter.changeStyle(3);
            this.mButtonGameCenter.setText(2422);
         } else if (gameCenter.isConnected()) {
            this.mButtonGameCenter.changeStyle(6);
            this.mButtonGameCenter.setText(2421);
         } else {
            this.mButtonGameCenter.changeStyle(3);
            this.mButtonGameCenter.setText(2420);
         }

         this.updateActivityIndicators(true);
         if (this.mInputFields[0].isFull() && this.mInputFields[1].isFull() && this.mInputFields[2].isFull()) {
            this.mButtonAddFriend.setEnabled(true);
         } else {
            this.mButtonAddFriend.setEnabled(false);
         }

         if (this.mSocialFriend != null) {
            if (this.mSocialFriend.isInviting()) {
               this.mTextFriendcodeResponseMessage.setText(2739, 3);
            } else if (this.mSocialFriend.isInviteAdded()) {
               this.mTextFriendcodeResponseMessage.setText(2740, 3);
               this.highlightInputfields(1);
            } else if (this.mSocialFriend.isAlreadyNeighbor()) {
               this.mTextFriendcodeResponseMessage.setText(2742, 3);
               this.highlightInputfields(2);
            } else if (this.mSocialFriend.isInviteFailed()) {
               this.mTextFriendcodeResponseMessage.setText(2741, 3);
               this.highlightInputfields(2);
            }

            if (this.mTrackFriendCode) {
               if (this.mSocialFriend.isInviteAdded()) {
                  this.mFriendCodeEvent.trackFriendCode(0);
                  this.mTrackFriendCode = false;
               }

               if (this.mSocialFriend.isAlreadyNeighbor()) {
                  this.mFriendCodeEvent.trackFriendCode(2);
                  this.mTrackFriendCode = false;
               }

               if (this.mSocialFriend.isInviteFailed()) {
                  this.mFriendCodeEvent.trackFriendCode(1);
                  this.mTrackFriendCode = false;
               }
            }
         }

         boolean focus = this.mInputFields[0].hasFocus() || this.mInputFields[1].hasFocus() || this.mInputFields[2].hasFocus();
         int y = Toolkit.getScreenHeight() - window.getHeight() >> 1;
         if (focus) {
            window.setCurrentPosY(y - (Toolkit.getScreenHeight() >> 1));
            this.highlightInputfields(0);
            this.mSocialFriend = null;
         } else {
            window.setCurrentPosY(y);
         }

         Toolkit.getUsedVirtualKeypad().setVisible(focus);
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible()) {
         this.mCollection.pointerEvent(event);
      }
   }

   public void keyEventOccured(int key, int action, int type) {
      HUDInputField focused = null;
      int index = -1;

      for (int i = 0; i < (this.mInputFields == null ? 0 : this.mInputFields.length); i++) {
         this.mInputFields[i].keyEventOccured(key, action, type);
         if (this.mInputFields[i].hasFocus()) {
            this.highlightInputfields(0);
            focused = this.mInputFields[i];
            index = i;
         }
      }

      if (focused != null && focused.isFull()) {
         focused.setFocus(false);
         if (index < 2) {
            this.mInputFields[index + 1].setFocus(true);
         }
      }
   }

   @Override
   public void callback(int id) {
      WindowSocial window = (WindowSocial)WindowManager.getWindow((byte)18);
      switch (id) {
         case 0:
            if (!window.isConnectingFacebook()) {
               window.getFacebookFriends();
            }
            break;
         case 1:
            if (!window.isConnectingGameCenter()) {
               window.getGameCenterFriends();
            }
         case 2:
         default:
            break;
         case 3:
            String[] parameters = new String[]{PlayerProfile.getFriendCode()};
            SettingsWrapper.copyText(Toolkit.replaceParameters(Toolkit.getText(2756), parameters));
            WindowManager.closeWindow();
            WindowManager.openNPCDialog((byte)13);
            break;
         case 4:
            String code = this.mInputFields[0].getText() + this.mInputFields[1].getText() + this.mInputFields[2].getText();
            String own = PlayerProfile.getFriendCode();
            if (own != null && !own.equals("") && own.equals(code)) {
               this.mTextFriendcodeResponseMessage.setText(2743, 3);
               this.mSocialFriend = null;
            } else {
               this.mSocialFriend = new SocialFriend(code);
               this.mSocialFriend.invite();
               this.mTrackFriendCode = true;
            }
      }
   }

   public void highlightInputfields(int mode) {
      for (int i = 0; i < (this.mInputFields == null ? 0 : this.mInputFields.length); i++) {
         this.mInputFields[i].setHighlightMode(mode);
      }
   }
}
