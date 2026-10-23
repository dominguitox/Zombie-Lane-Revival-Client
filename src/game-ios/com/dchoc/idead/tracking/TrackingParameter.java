package com.dchoc.idead.tracking;

public class TrackingParameter {
   public static final int TYPE_INT = 0;
   public static final int TYPE_STRING = 1;
   public static final int TYPE_VALUE_NOT_SET = -1;
   private int mDataType = -1;
   private int mIntValue;
   private String mName;
   private String mStringValue;

   public TrackingParameter(String name, int value) {
      this.mName = name;
      this.mIntValue = value;
      this.mDataType = 0;
   }

   public TrackingParameter(String name, String value) {
      this.mName = name;
      if (value != null && !value.equals("")) {
         this.mStringValue = value;
         this.mDataType = 1;
      }
   }

   public void clear() {
      this.mDataType = -1;
   }

   public int getDataType() {
      return this.mDataType;
   }

   public int getIntValue() {
      return this.mIntValue;
   }

   public String getName() {
      return this.mName;
   }

   public String getStringValue() {
      return this.mStringValue;
   }

   public void setValue(int value) {
      this.mIntValue = value;
      this.mDataType = 0;
   }

   public void setValue(String value) {
      if (value != null && !value.equals("")) {
         this.mStringValue = value;
         this.mDataType = 1;
      }
   }
}
