package com.dchoc.idead.player;

import com.dchoc.idead.items.WeaponItem;
import com.dchoc.iphonewrappers.SettingsWrapper;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Sonda;
import java.io.EOFException;
import java.util.Vector;

public class Profile implements CsvIDs {
   public static final int INITIAL_AVATARS_CAPACITY = 3;
   public static final int TYPE_NEIGHBOR = 1926;
   public static final int TYPE_PLAYER = 0;
   public static final int TYPE_ROB = 2514;
   protected String mName;
   protected String mUserID;
   protected String mFacebookID;
   protected String mGameCenterID;
   protected int mType;
   protected String mPictureUrl;
   protected int mLevel;
   protected Vector mAvatars;
   protected int mActiveAvatar;
   private boolean mUpdated;
   private int mVersion;

   public Profile(String name, int type) {
      this.mName = name;
      this.mType = type;
      this.mAvatars = new Vector(3);
      this.mActiveAvatar = 0;
      this.mFacebookID = null;
      this.mGameCenterID = null;
      this.mUserID = null;
      this.mPictureUrl = null;
      this.mLevel = 1;
      this.mUpdated = false;
      this.mVersion = -1;
   }

   public WeaponItem getActiveWeapon() {
      AvatarDescription avatar = this.getActiveAvatar();
      return avatar == null ? null : avatar.getWeapon();
   }

   public void setActiveWeapon(WeaponItem weapon) {
      if (weapon != null) {
         for (int i = 0; i < this.mAvatars.size(); i++) {
            ((AvatarDescription)this.mAvatars.elementAt(i)).equipWeapon(weapon);
         }
      }
   }

   public void setActiveAvatar(int character) {
      for (int i = 0; i < this.mAvatars.size(); i++) {
         if (((AvatarDescription)this.mAvatars.elementAt(i)).getCharacter() == character) {
            this.mActiveAvatar = i;
            return;
         }
      }
   }

   public AvatarDescription getActiveAvatar() {
      return this.mAvatars.isEmpty() ? null : (AvatarDescription)this.mAvatars.elementAt(this.mActiveAvatar);
   }

   public int getActiveCharacter() {
      AvatarDescription avatar = this.getActiveAvatar();
      return avatar == null ? -1 : avatar.getCharacter();
   }

   public AvatarDescription getAvatar(int character) {
      for (int i = 0; i < this.mAvatars.size(); i++) {
         AvatarDescription avatar = (AvatarDescription)this.mAvatars.elementAt(i);
         if (avatar.getCharacter() == character) {
            return avatar;
         }
      }

      return null;
   }

   public void addAvatar(AvatarDescription avatar) {
      if (avatar != null) {
         WeaponItem weapon = this.getActiveWeapon();
         if (avatar.getCharacter() != 43) {
            avatar.equipWeapon(weapon);
         }

         this.mAvatars.addElement(avatar);
      }
   }

   public void removeAvatars() {
      this.mAvatars = new Vector(3);
   }

   public Vector getAvatars() {
      return this.mAvatars;
   }

   public void setPictureUrl(String url) {
      this.mPictureUrl = url;
   }

   public String getName() {
      return this.mName;
   }

   public void setName(String name) {
      this.mName = name;
   }

   public String getUserID() {
      return this.mUserID;
   }

   public void setUserID(String userID) {
      this.mUserID = userID;
   }

   public String getFacebookID() {
      return this.mFacebookID;
   }

   public void setFacebookID(String facebookID) {
      this.mFacebookID = facebookID;
   }

   public String getGameCenterID() {
      return this.mGameCenterID;
   }

   public void setGameCenterID(String gameCenterID) {
      this.mGameCenterID = gameCenterID;
   }

   public int getType() {
      return this.mType;
   }

   public int getLevel() {
      return this.mLevel;
   }

   public void setType(int type) {
      this.mType = type;
   }

   public void setLevel(int level) {
      this.mLevel = level;
   }

   public boolean isUpdated() {
      return this.mUpdated;
   }

   public void resetUpdated() {
      this.mUpdated = false;
   }

   public int getVersion() {
      return this.mVersion;
   }

   public DChocByteArray save() {
      DChocByteArray data = DChocByteArray.createByteArrayForWriting(1024);
      data.writeUTF(SettingsWrapper.getVersion());
      data.writeBoolean(this.mName != null);
      if (this.mName != null) {
         data.writeUTF(this.mName);
      }

      data.writeBoolean(this.mPictureUrl != null);
      if (this.mPictureUrl != null) {
         data.writeUTF(this.mPictureUrl);
      }

      data.writeBoolean(this.mUserID != null);
      if (this.mUserID != null) {
         data.writeUTF(this.mUserID);
      }

      data.writeBoolean(this.mFacebookID != null);
      if (this.mFacebookID != null) {
         data.writeUTF(this.mFacebookID);
      }

      data.writeBoolean(this.mGameCenterID != null);
      if (this.mGameCenterID != null) {
         data.writeUTF(this.mGameCenterID);
      }

      data.writeShort(this.mLevel);
      data.writeByte(this.mActiveAvatar);
      data.writeByte(this.mAvatars.size());

      for (int i = 0; i < this.mAvatars.size(); i++) {
         ((AvatarDescription)this.mAvatars.elementAt(i)).saveData(data);
      }

      return data;
   }

   public boolean load(DChocByteArray data) {
      if (data == null) {
         return false;
      }

      try {
         this.mVersion = SettingsWrapper.parseVersion(data.readUTF());
         if (data.readBoolean()) {
            String value = data.readUTF();
            if (value != null) {
               this.mName = value;
            }
         }

         if (data.readBoolean()) {
            String value = data.readUTF();
            if (value != null) {
               this.mPictureUrl = value;
            }
         }

         if (data.readBoolean()) {
            String value = data.readUTF();
            if (value != null) {
               this.mUserID = value;
            }
         }

         if (data.readBoolean()) {
            String value = data.readUTF();
            if (value != null) {
               this.mFacebookID = value;
            }
         }

         if (data.readBoolean()) {
            String value = data.readUTF();
            if (value != null) {
               this.mGameCenterID = value;
            }
         }

         this.mUpdated = true;
         this.mLevel = data.readShort();
         this.mActiveAvatar = data.readByte();
         this.mAvatars.removeAllElements();
         int count = data.readByte();

         for (int i = 0; i < count; i++) {
            this.mAvatars.addElement(new AvatarDescription(data));
         }
      } catch (EOFException exception) {
         Sonda.probe("Profile.load (truncated record)", exception);
         return false;
      } catch (Exception exception) {
         Sonda.fail("Profile.load", exception);
      }

      return true;
   }
}
