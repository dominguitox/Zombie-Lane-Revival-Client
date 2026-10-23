package com.dchoc.toolkit;

public class FacebookPost {
   private String actionLinkText;
   private String caption;
   private String imageSrcUrl;
   private String linkUrl;
   private String targetUserId;
   private String title;

   public FacebookPost(String var1, String var2, String var3, String var4, String var5, String var6) {
      this.title = var1;
      this.caption = var2;
      this.linkUrl = var3;
      this.imageSrcUrl = var4;
      this.actionLinkText = var5;
      this.targetUserId = var6;
   }

   public String getActionLinkText() {
      return this.actionLinkText;
   }

   public String getCaption() {
      return this.caption;
   }

   public String getImageSrcUrl() {
      return this.imageSrcUrl;
   }

   public String getLinkUrl() {
      return this.linkUrl;
   }

   public String getTargetUserId() {
      return this.targetUserId;
   }

   public String getTitle() {
      return this.title;
   }

   public void setActionLinkText(String var1) {
      this.actionLinkText = var1;
   }

   public void setCaption(String var1) {
      this.caption = var1;
   }

   public void setImageSrcUrl(String var1) {
      this.imageSrcUrl = var1;
   }

   public void setLinkUrl(String var1) {
      this.linkUrl = var1;
   }

   public void setTargetUserId(String var1) {
      this.targetUserId = var1;
   }

   public void setTitle(String var1) {
      this.title = var1;
   }
}
