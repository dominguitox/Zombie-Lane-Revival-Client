package com.dchoc.toolkit;

public class UserProfile {
   private String id;
   private String name;
   private String pictureUrl;

   public UserProfile(String id, String name, String pictureUrl) {
      this.id = id;
      this.name = name;
      this.pictureUrl = pictureUrl;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getPictureUrl() {
      return this.pictureUrl;
   }
}
