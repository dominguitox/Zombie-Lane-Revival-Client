package com.dchoc.hud;

import com.dchoc.toolkit.Toolkit;

public class ReplaceParameters {
   private static String[] smReplace1 = new String[1];
   private static String[] smReplace2 = new String[2];
   private static String[] smReplace3 = new String[3];
   private String mParameter1 = null;
   private String mParameter2 = null;
   private String mParameter3 = null;
   private short mParameterId1 = -1;
   private short mParameterId2 = -1;
   private short mParameterId3 = -1;
   private byte mParameterCount = 1;

   public void init(String parameter, int id) {
      this.clear();
      this.mParameter1 = parameter;
      this.mParameterId1 = (short)id;
      this.mParameterCount = 1;
   }

   public void init(String parameter1, int id1, String parameter2, int id2) {
      this.clear();
      this.mParameter1 = parameter1;
      this.mParameterId1 = (short)id1;
      this.mParameter2 = parameter2;
      this.mParameterId2 = (short)id2;
      this.mParameterCount = 2;
   }

   public void init(String parameter1, int id1, String parameter2, int id2, String parameter3, int id3) {
      this.clear();
      this.mParameter1 = parameter1;
      this.mParameterId1 = (short)id1;
      this.mParameter2 = parameter2;
      this.mParameterId2 = (short)id2;
      this.mParameter3 = parameter3;
      this.mParameterId3 = (short)id3;
      this.mParameterCount = 3;
   }

   public String constructText(int id) {
      if (this.mParameterId1 != -1) {
         this.mParameter1 = Toolkit.getText(this.mParameterId1);
      }

      byte count = this.mParameterCount;
      if (1 < count) {
         if (this.mParameterId2 != -1) {
            this.mParameter2 = Toolkit.getText(this.mParameterId2);
            count = this.mParameterCount;
         }

         if (2 < count && this.mParameterId3 != -1) {
            this.mParameter3 = Toolkit.getText(this.mParameterId3);
         }
      }

      String text = Toolkit.getText(id);
      if (text == null) {
         return null;
      }

      if (2 < this.mParameterCount) {
         smReplace3[0] = this.mParameter1;
         smReplace3[1] = this.mParameter2;
         smReplace3[2] = this.mParameter3;
         return Toolkit.replaceParameters(text, smReplace3);
      }

      String[] replace = smReplace1;
      if (this.mParameterCount < 2) {
         smReplace1[0] = this.mParameter1;
      } else {
         smReplace2[0] = this.mParameter1;
         smReplace2[1] = this.mParameter2;
         replace = smReplace2;
      }

      return Toolkit.replaceParameters(text, replace);
   }

   public static String replace(String text, String parameter1, String parameter2, String parameter3) {
      smReplace3[0] = parameter1;
      smReplace3[1] = parameter2;
      smReplace3[2] = parameter3;
      return Toolkit.replaceParameters(text, smReplace3);
   }

   public static String replace(String text, String parameter) {
      smReplace1[0] = parameter;
      return Toolkit.replaceParameters(text, smReplace1);
   }

   public void clear() {
      this.mParameter1 = null;
      this.mParameter2 = null;
      this.mParameter3 = null;
      this.mParameterId1 = -1;
      this.mParameterId2 = -1;
      this.mParameterId3 = -1;
      this.mParameterCount = 1;
   }
}
