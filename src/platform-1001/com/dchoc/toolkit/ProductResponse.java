package com.dchoc.toolkit;

public class ProductResponse {
   private Product[] products;
   private boolean success;

   public ProductResponse(Product[] var1, boolean var2) {
      this.products = var1;
      this.success = var2;
   }

   public Product[] getProducts() {
      return this.products;
   }

   public boolean isSuccess() {
      return this.success;
   }
}
