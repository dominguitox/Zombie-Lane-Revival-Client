package com.dchoc.idead.tracking;

import com.dchoc.idead.tuner.Tuner;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Sonda;
import com.dchoc.toolkit.Toolkit;

public class ABTestManager {
   static boolean smEnableABEvent;
   static byte smABValuesFlag;

   public static void enableEvent() {
      smEnableABEvent = true;
      smABValuesFlag = 1;
   }

   public static void save() {
      DChocByteArray out = DChocByteArray.createByteArrayForWriting();
      out.writeByte(smABValuesFlag);
      out.writeInt((int)Tuner.getPlayerSpeed(false));
      Toolkit.writeRecordByteArray("RMS_AB_TEST", out, 0);
   }

   public static void load() {
      DChocByteArray in = Toolkit.readRecordByteArray("RMS_AB_TEST");
      if (in != null) {
         try {
            smABValuesFlag = in.readByte();
            int speed = in.readInt();
            if (speed > 0) {
               Tuner.setPlayerSpeed(speed);
            }
         } catch (Exception e) {
            Sonda.probe("ABTestManager.load (RMS_AB_TEST)", e);
         }
      }
   }
}
