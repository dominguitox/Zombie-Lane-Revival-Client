package com.dchoc.windows;

import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDConnectToAppstore;
import com.dchoc.hud.HUDCounter;
import com.dchoc.hud.HUDItem;
import com.dchoc.hud.HUDMenuPanel;
import com.dchoc.hud.HUDSetOfSlots;
import com.dchoc.hud.HUDTouchArea;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.DecorationItem;
import com.dchoc.idead.items.EdibleItem;
import com.dchoc.idead.items.FarmingItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tracking.BuyCurrencyEvent;
import com.dchoc.iphonewrappers.ActivityWrapper;
import com.dchoc.iphonewrappers.UIWrapper;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.opengl.OGL;
import com.dchoc.playhaven.PlayHaven;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.IPaymentListener;
import com.dchoc.toolkit.IPaymentManager;
import com.dchoc.toolkit.PaymentTransaction;
import com.dchoc.toolkit.Product;
import com.dchoc.toolkit.ProductResponse;
import com.dchoc.toolkit.PurchaseRequest;
import com.dchoc.toolkit.Toolkit;
import java.io.EOFException;
import java.util.Vector;
import zlport.ios.Ios;

public class WindowMarket extends Window implements IPaymentListener {
   private static final String[] MODE_NAMES = new String[]{"Energy", "Buildings", "Farming", "Weapons", "Decorations", "Boosters", "Cash", "Coins"};
   private static final int[] INAPP_AMOUNTS = new int[]{1000, 5000, 10000, 30000, 100000, 10, 50, 100, 300, 1000};
   private static final int[] PRODUCT_ORDER = new int[]{1, 0, 2, 3, 4, 6, 5, 7, 8, 9, 10};
   int WIDTH_OF_TOUCH_AREAS;
   private boolean mPaymentInProgress;
   private boolean mFetchingProducts;
   private HUDMenuPanel mMenu;
   private HUDAutoTextField smTitle;
   private HUDCounter mCurrencyCash;
   private HUDCounter mCurrencyCoins;
   private HUDCounter mCurrencyFood;
   private HUDConnectToAppstore mConnectToAppstore;
   private RoundRectangle mRectangle;
   private int mMarketMode;
   private int mPreviousMarketMode;
   private BuyCurrencyEvent mInAppEvent;
   private Vector productResponses;
   private Vector paymentTransactions;
   private boolean mTransactionConfirmationShown;
   private HUDTouchArea[] mMarketArea;

   public WindowMarket() {
      this.mCollisions = AnimationsManager.loadShared(74739);
      this.mBackground = AnimationsManager.loadShared(74619);
      this.initCollisions();
      this.constructCloseButton(36000);
      if (Ios.platform().isImpactSupported()) {
         this.constructImpactVideoButtonInMarket(56000);
      }

      this.productResponses = new Vector(8);
      this.paymentTransactions = new Vector(8);
      this.mRectangle = new RoundRectangle(0);
      CollisionBox box6 = this.mCollisions.getCollisionBox(6);
      this.mRectangle.setSizes(box6);
      this.mMenu = new HUDMenuPanel(0);
      CollisionBox box7 = this.mCollisions.getCollisionBox(7);
      this.mMenu.setPosition(box6.getX(), box7.getY() + (box7.getHeight() - this.mMenu.getHeight()) / 2);
      HUDAutoTextField title = new HUDAutoTextField(this.getCollisionBox(30));
      this.smTitle = title;
      title.setCentered(true, true);
      this.smTitle.setText(102, 5);
      this.mCurrencyCash = new HUDCounter(1, 66249, this.mCollisions.getCollisionBox(21));
      this.mCurrencyCoins = new HUDCounter(0, 66223, this.mCollisions.getCollisionBox(22));
      this.mCurrencyFood = new HUDCounter(2, 66215, this.mCollisions.getCollisionBox(23));
      this.WIDTH_OF_TOUCH_AREAS = box7.getWidth();
      int[] resources = new int[]{80479, 80480, 80481, 80482, 80483, 80484};
      Vector[] sets = new Vector[resources.length];

      for (int i = 0; i < resources.length; i++) {
         try {
            DChocByteArray data = Toolkit.getResourceByteArray(resources[i]);
            sets[i] = new Vector(16);

            while (true) {
               int id = data.readShort();
               Item item = ItemManager.getItem(data.readShort(), id);
               if (item != null) {
                  int resource = resources[i];
                  if (resource == 80481 || resource == 80482 || resource == 80479) {
                     boolean newItem = data.readBoolean();
                     if (item.getType() == 1583) {
                        ((FarmingItem)item).setNewItem(newItem);
                     } else if (item.getType() == 101) {
                        ((EdibleItem)item).setNewItem(newItem);
                     } else if (item.getType() == 1907) {
                        ((WeaponItem)item).setNewItem(newItem);
                     }
                  } else if (resource == 80483) {
                     boolean newItem = data.readBoolean();
                     boolean saleItem = data.readBoolean();
                     if (item.getType() == 405) {
                        ((DecorationItem)item).setNewItem(newItem);
                        ((DecorationItem)item).setSaleItem(saleItem);
                     }
                  }

                  HUDSetOfSlots.addSlotToSet(sets[i], item, this.WIDTH_OF_TOUCH_AREAS, 3, false);
                  if (item.getSecondPrice() > 0) {
                     HUDSetOfSlots.addSlotToSet(sets[i], item, this.WIDTH_OF_TOUCH_AREAS, 3, true);
                  }
                  continue;
               }
            }
         } catch (Exception exception) {
            if (!(exception instanceof EOFException)) {
               exception.printStackTrace();
            }

            System.err.println(exception);
         }
      }

      this.mMarketArea = new HUDTouchArea[Ios.platform().isImpactSupported() ? 9 : 8];
      CollisionBox area = this.mCollisions.getCollisionBox(7);

      for (int i = 0; i < resources.length; i++) {
         this.mMarketArea[i] = new HUDTouchArea(false, true, true, area, sets[i], false);
      }

      this.mMarketArea[6] = new HUDTouchArea(false, true, true, area, new Vector(), false);
      this.mMarketArea[7] = new HUDTouchArea(false, true, true, area, new Vector(), false);
      if (Ios.platform().isImpactSupported()) {
         this.mMarketArea[8] = new HUDTouchArea(false, true, true, area, new Vector(), false);
      }

      this.mConnectToAppstore = new HUDConnectToAppstore();
      int dx = area.getWidth() - this.mConnectToAppstore.getWidth();
      int dy = area.getHeight() - this.mConnectToAppstore.getHeight();
      this.mConnectToAppstore.setPosition(dx / 2 + area.getX(), dy / 2 + area.getY());

      for (int i = 0; i < (this.mMarketArea == null ? 0 : this.mMarketArea.length); i++) {
         if (this.mMarketArea[i] != null) {
            this.mMarketArea[i].setScrollbar(true);
            this.mMarketArea[i].setFadingBorder(true);
         }
      }

      this.mObjects.addObject(this.smTitle);
      this.mObjects.addObject(this.mButtonClose);
      this.mObjects.addObject(this.mRectangle);
      this.mObjects.addObject(this.mMarketArea[0]);
      this.mObjects.addObject(this.mMarketArea[1]);
      this.mObjects.addObject(this.mMarketArea[2]);
      this.mObjects.addObject(this.mMarketArea[3]);
      this.mObjects.addObject(this.mMarketArea[4]);
      this.mObjects.addObject(this.mMarketArea[5]);
      this.mObjects.addObject(this.mMarketArea[6]);
      this.mObjects.addObject(this.mMarketArea[7]);
      if (Ios.platform().isImpactSupported()) {
         this.mObjects.addObject(this.mMarketArea[8]);
      }

      this.mObjects.addObject(this.mMenu);
      this.mObjects.addObject(this.mCurrencyCash);
      this.mObjects.addObject(this.mCurrencyCoins);
      this.mObjects.addObject(this.mCurrencyFood);
      this.mObjects.addObject(this.mConnectToAppstore);
      if (Ios.platform().isImpactSupported()) {
         this.mObjects.addObject(this.mButtonImpactVideo);
      }
   }

   @Override
   public void callback(int id) {
      if (id == 56000) {
         WindowManager.closeWindow();
         super.mMarketMode = 7;
         if (Ios.platform().showImpactVideoAds()) {
            Toolkit.stopMusic();
            Toolkit.stopSoundEffect();
         }
      } else if (id == 36000) {
         WindowManager.closeWindow();
      } else if (id >= 0 && id <= 8 && this.mMarketMode != id) {
         this.setMode(id);
         this.trackTab();
      }
   }

   @Override
   public void draw() {
      super.draw();
      this.setMarketMode(this.mMarketMode);
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

   public void refreshMarket() {
      for (int i = 0; i < (this.mMarketArea == null ? 0 : this.mMarketArea.length); i++) {
         if (this.mMarketArea[i] != null) {
            Vector items = this.mMarketArea[i].getItems();
            if (items.size() > 0) {
               int j = 0;

               while (true) {
                  ((HUDItem)items.elementAt(j)).refresh();
                  if (++j >= items.size()) {
                     break;
                  }
               }
            }
         }
      }
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      if (this.mState == 0) {
         if (this.mPaymentInProgress || this.mFetchingProducts) {
            Toolkit.getUsedPaymentManager().logicUpdate(delta);
         }

         if (this.productResponses.size() > 0) {
            int i = 0;

            do {
               this.processProductResponse((ProductResponse)this.productResponses.elementAt(i));
            } while (++i < this.productResponses.size());
         }

         this.mTransactionConfirmationShown = false;
         if (this.paymentTransactions.size() > 0) {
            int i = 0;

            do {
               this.processTransaction((PaymentTransaction)this.paymentTransactions.elementAt(i));
            } while (++i < this.paymentTransactions.size());
         }

         this.productResponses.removeAllElements();
         this.paymentTransactions.removeAllElements();
         if (this.mTransactionConfirmationShown) {
            this.close();
         }
      }
   }

   public Item getItemWithPrice(int currency, int balance, Vector exclude) {
      int i = 0;

      while (i < (this.mMarketArea == null ? 0 : this.mMarketArea.length)) {
         i++;
      }

      return null;
   }

   public void setMode(int mode) {
      this.mMarketMode = mode;

      for (int i = 0; i < (this.mMarketArea == null ? 0 : this.mMarketArea.length); i++) {
         if (this.mMarketArea[i] != null) {
            this.mMarketArea[i].setVisible(false);
         }
      }

      this.mMarketArea[this.mMarketMode].resetPosition();
      this.mMarketArea[this.mMarketMode].setVisible(true);
      this.updateConnectToAppstoreLabel();
      if (this.mFetchingProducts) {
         ActivityWrapper.show(Toolkit.getScreenWidth() >> 1, Toolkit.getScreenHeight() >> 1);
      } else {
         ActivityWrapper.hide();
      }

      this.mMenu.select(this.mMarketMode);
   }

   public void availableProductsReceived(ProductResponse response) {
      this.productResponses.addElement(response);
   }

   public void productPurchased(PaymentTransaction transaction) {
      this.paymentTransactions.addElement(transaction);
   }

   public void startPurchase(Product product, BuyCurrencyEvent event) {
      GameEngine.getInstance().setTransactionActive();
      PurchaseRequest request = new PurchaseRequest(product, 1);
      Toolkit.getUsedPaymentManager().purchaseProduct(request);
      this.mInAppEvent = event;
      event.trackStarted();
      this.mPaymentInProgress = true;
      ActivityWrapper.show(Toolkit.getScreenWidth() >> 1, Toolkit.getScreenHeight() >> 1);
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (!this.mPaymentInProgress) {
         super.pointerEvent(event);
      }
   }

   public void changeToThisPaymentListener() {
      IPaymentManager manager = Toolkit.getUsedPaymentManager();
      manager.setPaymentListener(this);
      manager.requestAvailableProducts();
      manager.logicUpdate(0);
   }

   public void requestCashAndCoinds() {
      if (this.mMarketArea[6].getItems().size() == 0 || this.mMarketArea[7].getItems().size() == 0) {
         if (!this.mFetchingProducts) {
            IPaymentManager manager = Toolkit.getUsedPaymentManager();
            manager.setPaymentListener(this);
            manager.requestAvailableProducts();
            this.mFetchingProducts = true;
            manager.logicUpdate(0);
         }
      }
   }

   @Override
   public void open() {
      super.open();
      this.setMode(0);
      this.requestCashAndCoinds();
      this.refreshMarket();
   }

   @Override
   public void close() {
      super.close();
      if (this.mFetchingProducts) {
         ActivityWrapper.hide();
         iWrapper.stopLoadingAnim();
      }
   }

   public static boolean isCashProduct(int id) {
      return id >= 0 && id < 11 ? (992 >>> id & 1) != 0 : true;
   }

   public static int getProductAmount(int id) {
      return INAPP_AMOUNTS[id];
   }

   public void savePreviousMode() {
      this.mPreviousMarketMode = this.mMarketMode;
   }

   public void setPreviouMode() {
      this.setMode(this.mPreviousMarketMode);
   }

   @Override
   public String getActiveAreaName() {
      return MODE_NAMES[this.mMarketMode];
   }

   @Override
   public void setState(byte state) {
      super.setState(state);
      if (state == 1) {
         PlayHaven.triggerCloseStore();
      } else if (state == 0) {
         PlayHaven.triggerOpenStore();
      }
   }

   public void updateConnectToAppstoreLabel() {
      if (this.mMarketMode == 6 || this.mMarketMode == 7) {
         Vector items = this.mMarketArea[this.mMarketMode].getItems();
         if (items.size() == 0 && !this.mFetchingProducts) {
            this.mConnectToAppstore.setVisible(true);
            return;
         }
      }

      this.mConnectToAppstore.setVisible(false);
   }

   public void processProductResponse(ProductResponse response) {
      this.mFetchingProducts = false;
      iWrapper.stopLoadingAnim();
      if (response != null && response.isSuccess()) {
         Product[] products = response.getProducts();
         Vector cash = new Vector(products == null ? 0 : products.length);
         Vector coins = new Vector(products == null ? 0 : products.length);
         this.sortProducts(products);

         for (int i = 0; i < (products == null ? 0 : products.length); i++) {
            int id = products[i].getDchocId();
            if (id >= 0 && id < 11) {
               if ((992 >>> id & 1) != 0) {
                  HUDSetOfSlots.addSlotToSet(cash, products[i], this.WIDTH_OF_TOUCH_AREAS, 3, false);
               } else if (products[i].getDchocId() < 5) {
                  HUDSetOfSlots.addSlotToSet(coins, products[i], this.WIDTH_OF_TOUCH_AREAS, 3, false);
               }
            } else {
               HUDSetOfSlots.addSlotToSet(cash, products[i], this.WIDTH_OF_TOUCH_AREAS, 3, false);
            }
         }

         this.mMarketArea[7].removeItems();
         this.mMarketArea[6].removeItems();
         this.mMarketArea[7].addItems(cash);
         this.mMarketArea[6].addItems(coins);
      }

      this.updateConnectToAppstoreLabel();
      ActivityWrapper.hide();
   }

   public void sortProducts(Product[] products) {
      Product[] sorted = new Product[products == null ? 0 : products.length];

      for (int i = 0; i < (products == null ? 0 : products.length); i++) {
         int id = products[i].getDchocId();

         for (int j = 0; j < PRODUCT_ORDER.length; j++) {
            if (id == PRODUCT_ORDER[j]) {
               sorted[j] = products[i];
               break;
            }
         }
      }

      for (int i = 0; i < (products == null ? 0 : products.length); i++) {
         products[i] = sorted[i];
      }
   }

   public void processTransaction(PaymentTransaction transaction) {
      this.mPaymentInProgress = false;
      ActivityWrapper.hide();
      if (this.mInAppEvent == null && transaction != null) {
         String title = transaction.getPurchaseRequest().getProduct().getTitle();
         int id = transaction.getPurchaseRequest().getProduct().getDchocId();
         int type = 1;
         if (id < 0 || id >= 11 || (992 >>> id & 1) != 0) {
            type = 0;
         }

         int price = transaction.getPurchaseRequest().getProduct().getPrice();
         int amount = INAPP_AMOUNTS[id];
         this.mInAppEvent = new BuyCurrencyEvent(title, type, amount, price);
      }

      if (transaction == null) {
         UIWrapper.showAlertView(Toolkit.getText(2752), Toolkit.getText(2753), Toolkit.getText(54));
         if (this.mInAppEvent != null) {
            this.mInAppEvent.trackError();
         }
      } else {
         int validation = transaction.getValidationStatus();
         if (validation == 3) {
            if (this.mInAppEvent != null) {
               this.mInAppEvent.trackValidationError();
            }
         } else if (validation == 2) {
            if (this.mInAppEvent != null) {
               this.mInAppEvent.trackValidationFailed();
            }
         } else if (validation == 1 && this.mInAppEvent != null) {
            this.mInAppEvent.trackValidationOK();
         }

         switch (transaction.getStatus()) {
            case 0:
               int id = transaction.getPurchaseRequest().getProduct().getDchocId();
               int amount = INAPP_AMOUNTS[id];
               boolean cash = id < 0 || id >= 11 || (992 >>> id & 1) != 0;
               if (!cash) {
                  PlayerProfile.addInAppPurchaseCoins(amount);
               } else {
                  PlayerProfile.addInAppPurchaseCash(amount);
               }

               if (!this.mTransactionConfirmationShown) {
                  WindowTransactionConfirmation window = (WindowTransactionConfirmation)WindowManager.getWindow((byte)27);
                  window.setTransaction(amount, cash ? 7 : 9);
                  WindowManager.openWindow((byte)27);
                  this.mTransactionConfirmationShown = true;
               }

               if (this.mInAppEvent != null) {
                  this.mInAppEvent.trackCompleted();
               }
               break;
            case 1:
               UIWrapper.showAlertView(Toolkit.getText(2752), Toolkit.getText(2753), Toolkit.getText(54));
               if (this.mInAppEvent != null) {
                  this.mInAppEvent.trackError();
               }
               break;
            case 2:
               if (this.mInAppEvent != null) {
                  this.mInAppEvent.trackError();
               }
               break;
            case 3:
               if (this.mInAppEvent != null) {
                  this.mInAppEvent.trackCancelled();
               }
         }
      }
   }
}
