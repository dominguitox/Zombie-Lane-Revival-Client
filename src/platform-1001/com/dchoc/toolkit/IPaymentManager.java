package com.dchoc.toolkit;

import javax.microedition.lcdui.Graphics;

public interface IPaymentManager {
   int STATE_DONE = 1;
   int STATE_NOT_FINISHED = 0;

   void doDraw(Graphics var1);

   boolean isInAppPaymentSupported();

   void keyEventOccurred(int var1, int var2);

   int logicUpdate(int var1);

   void pointerEventOccurred(int var1, int var2, int var3);

   void purchaseProduct(PurchaseRequest var1);

   void requestAvailableProducts();

   void restoreCompletedTransactions();

   void setPaymentListener(IPaymentListener var1);
}
