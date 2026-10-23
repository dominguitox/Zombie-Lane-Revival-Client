package com.dchoc;

import com.dchoc.toolkit.Product;

public class PurchaseRequest {
   private Product product;
   private int quantity;

   public PurchaseRequest(Product var1, int var2) {
      this.product = var1;
      this.quantity = var2;
   }

   public Product getProduct() {
      return this.product;
   }

   public int getQuantity() {
      return this.quantity;
   }
}
