package com.dchoc.idead.servlets;

import com.dchoc.idead.network.Base64;
import com.dchoc.toolkit.DChocByteArray;

public class UpdateAccountInformation extends ServletRequest {
   private DChocByteArray mAvatar = null;
   private DChocByteArray mGamefield = null;
   private int mGamefieldAreaId = -1;
   private DChocByteArray mMissions = null;

   public UpdateAccountInformation() {
      super("UpdateAccountInformation", false, false);
   }

   public void setAvatar(DChocByteArray avatar) {
      this.mAvatar = avatar;
   }

   public void setGamefield(DChocByteArray gamefield, int areaId) {
      this.mGamefield = gamefield;
      this.mGamefieldAreaId = areaId;
   }

   public void setMissions(DChocByteArray missions) {
      this.mMissions = missions;
   }

   public boolean isExecutable() {
      if (this.mAvatar == null) {
         if (this.mGamefield == null) {
            return false;
         }
      } else {
         if (this.mGamefield == null) {
            return false;
         }

         if (this.mMissions != null) {
            return true;
         }
      }

      return this.mGamefieldAreaId != -1;
   }

   public void init() {
      this.clearParameters();
      if (this.mAvatar != null) {
         this.addParameter("avatar", Base64.encode(this.mAvatar.getBytes()));
      }

      if (this.mGamefield != null) {
         this.addParameter("gamefield", Base64.encode(this.mGamefield.getBytes()));
      }

      if (this.mMissions != null) {
         this.addParameter("missions", Base64.encode(this.mMissions.getBytes()));
      }

      if (this.mGamefield != null) {
         String areaStr = "home";
         if (this.mGamefieldAreaId == 3818) {
            areaStr = "circus";
         } else if (this.mGamefieldAreaId == 4054) {
            areaStr = "downtown";
         } else if (this.mGamefieldAreaId == 4745) {
            areaStr = "crashsite";
         } else if (this.mGamefieldAreaId == 5560) {
            areaStr = "cure";
         }

         this.addParameter("area", areaStr);
      }

      this.mAvatar = null;
      this.mGamefield = null;
      this.mMissions = null;
   }

   @Override
   public void debug(String message) {
      System.out.println("UpdateAccountInformation: " + message);
   }
}
