package com.dchoc.toolkit;

public class PaymentTransaction {
   public static final int STATUS_OK = 0;
   public static final int STATUS_ERROR = 1;
   public static final int STATUS_LOGIN_FAILED = 2;
   public static final int STATUS_USER_CANCELLED = 3;
   public static final int STATUS_RESTORE_FAILED = 4;
   private final boolean success;
   private final PurchaseRequest request;
   private final int status;
   private final int validationStatus;

   public PaymentTransaction(PurchaseRequest request, boolean success, int status, int validationStatus) {
      this.request = request;
      this.success = success;
      this.status = status;
      this.validationStatus = validationStatus;
   }

   public boolean isSuccess() {
      return this.success;
   }

   public int getStatus() {
      return this.status;
   }

   public int getValidationStatus() {
      return this.validationStatus;
   }

   public PurchaseRequest getPurchaseRequest() {
      return this.request;
   }
}
