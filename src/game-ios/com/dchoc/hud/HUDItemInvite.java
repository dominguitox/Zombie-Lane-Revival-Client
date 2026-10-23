package com.dchoc.hud;

import com.dchoc.idead.social.SocialFriend;
import com.dchoc.windows.WindowSocial;

public class HUDItemInvite extends HUDItemMailbox {
   private SocialFriend mSocialFriend = null;

   public HUDItemInvite() {
      super((byte)21);
      this.mTextDescription.setSizes(this.mCollisions.getCollisionBox(7));
   }

   public void setSocialFriend(SocialFriend friend) {
      if (friend != null) {
         this.mSocialFriend = friend;
         this.setValues(friend.getName(), 107, false, friend.getSocialIconType());
         this.setButtons(true, false, false);
         this.mTextDescription.setText(2328, 3, this.mSocialFriend.getName(), -1);
         this.mButtonAccept.setEnabled(this.mSocialFriend != null);
      }
   }

   public SocialFriend getSocialFriend() {
      return this.mSocialFriend;
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      this.setAccepted(this.mSocialFriend != null ? this.mSocialFriend.isAdded() : false);
   }

   @Override
   public void callback(int id) {
      if (id == 0) {
         ((WindowSocial)WindowManager.getWindow((byte)18)).postInvite(this.mSocialFriend);
      }
   }

   @Override
   public String getStringForSort() {
      return this.mSocialFriend.getName();
   }
}
