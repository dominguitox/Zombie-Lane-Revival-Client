package com.dchoc.toolkit;

public interface IPaymentListener {
   void availableProductsReceived(ProductResponse var1);

   void productPurchased(PaymentTransaction var1);
}
