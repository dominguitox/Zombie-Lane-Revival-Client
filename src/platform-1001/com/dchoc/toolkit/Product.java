package com.dchoc.toolkit;

import android.util.Log;
import java.math.BigDecimal;

public class Product {
   public static final int PRICE_MULTIPLIER = 1000;
   private int dchocId;
   private String description;
   private DChocImage icon;
   private int iconRID;
   private int price;
   private String priceString;
   private String thirdPartyId;
   private String title;
   private byte type;

   public Product(int var1, String var2, byte var3, String var4, String var5, DChocImage var6, int var7, int var8, String var9) {
      this.dchocId = var1;
      this.thirdPartyId = var2;
      this.title = var4;
      this.description = var5;
      this.icon = var6;
      this.iconRID = var7;
      this.price = var8;
      this.priceString = var9;
      this.type = var3;
   }

   public int getDchocId() {
      return this.dchocId;
   }

   public String getDescription() {
      return this.description;
   }

   public DChocImage getIcon() {
      return this.icon;
   }

   public int getIconRid() {
      return this.iconRID;
   }

   public int getPrice() {
      return this.price;
   }

   public BigDecimal getPriceInBigDecimal(int var1) {
      Log.i("AJ", "Product#getPriceInBigDecimal price " + var1);
      new BigDecimal(var1);
      int var2 = var1 % 1000 / 10;
      var1 /= 1000;
      return new BigDecimal("" + var1 + "." + var2);
   }

   public String getPriceString() {
      int var1 = this.price % 1000 / 10;
      int var2 = this.price / 1000;
      return "$" + var2 + "." + var1;
   }

   public String getThirdPartyId() {
      return this.thirdPartyId;
   }

   public String getTitle() {
      return this.title;
   }

   public byte getType() {
      return this.type;
   }

   public void setDescription(String var1) {
      this.description = var1;
   }

   public void setIcon(DChocImage var1) {
      this.icon = var1;
   }

   public void setPrice(int var1) {
      this.price = var1;
   }

   public void setPriceString(String var1) {
      this.priceString = var1;
   }

   public void setTitle(String var1) {
      this.title = var1;
   }
}
