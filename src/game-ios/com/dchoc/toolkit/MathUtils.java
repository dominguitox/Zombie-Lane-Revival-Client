package com.dchoc.toolkit;

public class MathUtils {
   public static final int ANGLE_ACCURACY = 8;
   public static final int SIN_ACCURACY = 15;
   private static int randSeed;
   private static int[] smSinTable;

   public static int cos(int i) {
      return sin(i + 16384);
   }

   public static int random(int i) {
      randSeed = randSeed * 1103515245 + 12345;
      return (randSeed >>> 15) * i >>> 17;
   }

   public static int sin(int i) {
      if (smSinTable == null) {
         precalcSinTable();
      }

      int frac = i & 0xFF;
      int idx = i >> 8;
      return (256 - frac) * smSinTable[idx & 0xFF] + smSinTable[idx + 1 & 0xFF] * frac >> 8;
   }

   private static void precalcSinTable() {
      int[] table = new int[256];
      smSinTable = table;
      int i = 0;
      int value = 0;
      int step = 32768;

      while (i < 64) {
         table[i] = value;
         int next = step - (value * 804 >> 15);
         value += next * 804 >> 15;
         i++;
         step = next;
      }

      table[64] = 32768;

      for (int k = 65; k <= 128; k++) {
         table[k] = table[128 - k];
      }

      for (int k = 129; k < 256; k++) {
         table[k] = -table[k - 128];
      }
   }
}
