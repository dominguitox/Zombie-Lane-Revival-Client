package com.dchoc.windows;

import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDImage;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tracking.GeneralPurchaseEvent;
import com.dchoc.iphonewrappers.ActivityWrapper;
import com.dchoc.iphonewrappers.UIWrapper;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.IPaymentListener;
import com.dchoc.toolkit.IPaymentManager;
import com.dchoc.toolkit.PaymentTransaction;
import com.dchoc.toolkit.Product;
import com.dchoc.toolkit.ProductCatalog;
import com.dchoc.toolkit.ProductResponse;
import com.dchoc.toolkit.PurchaseRequest;
import com.dchoc.toolkit.Toolkit;
import java.util.Map;
import java.util.Vector;
import zlport.ios.Ios;

public class WindowStarterPack extends Window implements IPaymentListener {
   private HUDImage mPackImage;
   private HUDButton mBuyButton;
   private Vector productResponses;
   private Vector paymentTransactions;
   private boolean mPurchased;
   private Product mStarterPackProduct;
   private GeneralPurchaseEvent mPurchaseEvent;
   private boolean mPaymentInProgress = false;

   public WindowStarterPack() {
      this.mBackground = AnimationsManager.loadShared(74619);
      this.mCollisions = AnimationsManager.loadShared(80401);
      this.productResponses = new Vector(1);
      this.paymentTransactions = new Vector(1);
      this.initCollisions();
      this.constructCloseButton(1);
      CollisionBox imageBox = this.getCollisionBox(1);
      this.mPackImage = new HUDImage(80403, imageBox);
      CollisionBox buttonBox = this.getCollisionBox(2);
      this.mBuyButton = new HUDButton(2, buttonBox, 2);
      this.mBuyButton.setText(133);
      this.mObjects.addObject(this.mPackImage);
      this.mObjects.addObject(this.mBuyButton);
      this.mObjects.addObject(this.mButtonClose);
   }

   @Override
   public void draw() {
      super.draw();
      if (this.mState != 1) {
         OGL.setClip(0, 0, Toolkit.getScreenWidth(), Toolkit.getScreenHeight());
         if (this.mPaymentInProgress) {
            OGL.setAlpha(0.3F);
            OGL.setColorRGB(0);
            OGL.fillRect(0, 0, Toolkit.getScreenWidth(), Toolkit.getScreenHeight());
            OGL.setAlpha(1.0F);
         }
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (!this.mPaymentInProgress) {
         super.pointerEvent(event);
      }
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      if (this.mState == 0) {
         if (this.mPaymentInProgress) {
            Toolkit.getUsedPaymentManager().logicUpdate(delta);
         }

         this.mPurchased = false;
         if (this.paymentTransactions.size() > 0) {
            int i = 0;

            do {
               this.processTransaction((PaymentTransaction)this.paymentTransactions.elementAt(i));
            } while (++i < this.paymentTransactions.size());
         }

         this.productResponses.removeAllElements();
         this.paymentTransactions.removeAllElements();
         if (this.mPurchased) {
            this.close();
         }
      }
   }

   @Override
   public void open() {
      super.open();
      this.mStarterPackProduct = ProductCatalog.get().createProduct("com.digitalchocolate.idead.starterpack1");
      this.mPurchaseEvent = new GeneralPurchaseEvent(this.mStarterPackProduct.getThirdPartyId(), 0);
      this.requestPurchasePack();
   }

   @Override
   public void close() {
      super.close();
      ((WindowMarket)WindowManager.getWindow((byte)22)).changeToThisPaymentListener();
      HUDImage image = null;
      if (this.mPurchased) {
         image = this.mPackImage;
      }

      if (this.mPurchased && image != null) {
         image.freeImages();
      }
   }

   @Override
   public void callback(int id) {
      if (id != 2) {
         if (id == 1) {
            WindowManager.closeWindow();
         }
      } else {
         if (this.isNetworkAvailable()) {
            Ios.platform().logEvent("S_PACK1_BUY_CLICK", Map.of("Level", Integer.toString(PlayerProfile.getLevel())));
            this.startPurchase(this.mStarterPackProduct, this.mPurchaseEvent);
         } else {
            String title = Toolkit.getText(4321);
            String message = Toolkit.getText(4322);
            String button = Toolkit.getText(54);
            UIWrapper.showAlertView(title, message, button);
         }
      }
   }

   public boolean isNetworkAvailable() {
      return Ios.platform().httpHeadHasResponse("http://www.apple.com/");
   }

   public Product getStarterPackProduct() {
      return this.mStarterPackProduct;
   }

   public void requestPurchasePack() {
      IPaymentManager manager = Toolkit.getUsedPaymentManager();
      manager.setPaymentListener(this);
      manager.requestAvailableProducts();
      manager.logicUpdate(0);
   }

   public void startPurchase(Product product, GeneralPurchaseEvent event) {
      GameEngine.getInstance().setTransactionActive();
      PurchaseRequest request = new PurchaseRequest(product, 1);
      Toolkit.getUsedPaymentManager().purchaseProduct(request);
      this.mPurchaseEvent = event;
      event.trackStarted();
      this.mPaymentInProgress = true;
      int width = Toolkit.getScreenWidth();
      int height = Toolkit.getScreenHeight();
      ActivityWrapper.show(width >> 1, height >> 1);
   }

   public void availableProductsReceived(ProductResponse response) {
      this.productResponses.addElement(response);
   }

   public void productPurchased(PaymentTransaction transaction) {
      this.paymentTransactions.addElement(transaction);
   }

   public void processTransaction(PaymentTransaction transaction) {
      this.mPaymentInProgress = false;
      ActivityWrapper.hide();
      if (this.mPurchaseEvent == null && transaction != null) {
         String title = transaction.getPurchaseRequest().getProduct().getTitle();
         this.mPurchaseEvent = new GeneralPurchaseEvent(title, 0);
      }

      int status = 1;
      if (transaction != null) {
         int validation = transaction.getValidationStatus();
         if (validation == 3) {
            if (this.mPurchaseEvent != null) {
               this.mPurchaseEvent.trackValidationError();
            }
         } else if (validation == 2) {
            if (this.mPurchaseEvent != null) {
               this.mPurchaseEvent.trackValidationFailed();
            }
         } else if (validation == 1 && this.mPurchaseEvent != null) {
            this.mPurchaseEvent.trackValidationOK();
         }

         status = transaction.getStatus();
      }

      switch (status) {
         case 0:
            PlayerProfile.addInAppPurchaseStarterPack(45, 1000, 30, 1);
            if (!this.mPurchased) {
               String titlex = this.mStarterPackProduct.getTitle();
               String messagex = Toolkit.getText(2446);
               String buttonx = Toolkit.getText(54);
               UIWrapper.showAlertView(titlex, messagex, buttonx);
               this.mPurchased = true;
            }

            Ios.platform().logEvent("S_PACK1_BUY_SUCCESS", Map.of("Level", Integer.toString(PlayerProfile.getLevel())));
            if (this.mPurchaseEvent != null) {
               this.mPurchaseEvent.trackCompleted();
            }
            break;
         case 1:
            String title = Toolkit.getText(2752);
            String message = Toolkit.getText(2753);
            String button = Toolkit.getText(54);
            UIWrapper.showAlertView(title, message, button);
            Ios.platform().logEvent("S_PACK1_BUY_ERROR", Map.of("Level", Integer.toString(PlayerProfile.getLevel())));
            if (this.mPurchaseEvent != null) {
               this.mPurchaseEvent.trackError();
            }
            break;
         case 2:
            Ios.platform().logEvent("S_PACK1_BUY_LOGIN_FAIL", Map.of("Level", Integer.toString(PlayerProfile.getLevel())));
            if (this.mPurchaseEvent != null) {
               this.mPurchaseEvent.trackError();
            }
            break;
         case 3:
            Ios.platform().logEvent("S_PACK1_BUY_CANCEL", Map.of("Level", Integer.toString(PlayerProfile.getLevel())));
            if (this.mPurchaseEvent != null) {
               this.mPurchaseEvent.trackCancelled();
            }
      }
   }
}
