package com.dchoc.toolkit;

import java.io.EOFException;
import java.util.Hashtable;
import java.util.Vector;
import zlport.ios.Ios;

public final class ProductCatalog {
   private static ProductCatalog m_singleton;
   private Vector mStoreProductIds;
   private Hashtable mProducts;
   private Product mStarterPack;

   private ProductCatalog() {
   }

   public static ProductCatalog get() {
      if (m_singleton == null) {
         ProductCatalog catalog = new ProductCatalog();

         try {
            catalog.initialize();
         } catch (EOFException e) {
            throw new IllegalStateException(e);
         }

         m_singleton = catalog;
      }

      return m_singleton;
   }

   private void initialize() throws EOFException {
      DChocByteArray data = Toolkit.getResourceByteArray(75);
      if (data != null) {
         int count = data.readInt();
         Vector ids = new Vector(count);
         this.mProducts = new Hashtable(count);
         String bundle = Ios.platform().infoPlistString("CFBundleIdentifier");

         for (int i = 0; i < count; i++) {
            int id = data.readInt();
            String thirdPartyId = bundle + "." + data.readUTF();
            byte type = data.readByte();
            String title = Toolkit.getText(data.readInt());
            String description = Toolkit.getText(data.readInt());
            int iconRid = data.readInt();
            int price = data.readInt();
            String priceString = data.readUTF();
            Product product = new Product(id, thirdPartyId, type, title, description, null, iconRid, price, priceString);
            ids.addElement(thirdPartyId);
            if (thirdPartyId.equals("com.digitalchocolate.idead.starterpack1")) {
               this.mStarterPack = product;
            }

            this.mProducts.put(thirdPartyId, product);
         }

         this.mStoreProductIds = ids;
      } else {
         this.mStoreProductIds = new Vector(0);
      }

      if (this.mProducts == null) {
         this.mProducts = new Hashtable(10);
      }
   }

   public Product createProduct(String thirdPartyId) {
      Product product = (Product)this.mProducts.get(thirdPartyId);
      if (product == null && thirdPartyId.equals("com.digitalchocolate.idead.starterpack1")) {
         product = this.mStarterPack;
      }

      return product;
   }
}
