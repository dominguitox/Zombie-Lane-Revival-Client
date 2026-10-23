package com.dchoc.toolkit;

import java.io.EOFException;
import java.util.Random;
import java.util.Vector;

public class ToolkitHelpers {
   public static final int MIN_RESOURCE_ID = 32;
   private static final String[] smParam = new String[1];
   private static final String[] smParams = new String[2];
   private static Random smRandom;
   private static String[] smNumbers;

   public static void init() {
      smRandom = new Random(System.currentTimeMillis());
      smNumbers = new String[100];

      for (int i = 0; i < smNumbers.length; i++) {
         smNumbers[i] = i < 10 ? "0" + i : Integer.toString(i);
      }
   }

   public static String getNumber(int number) {
      return smNumbers[number < 100 ? Math.max(number, 0) : 99];
   }

   public static void fastRemove(int index, Vector values) {
      int last = values.size() - 1;
      if (index != last) {
         values.setElementAt(values.elementAt(last), index);
      }

      values.removeElementAt(last);
   }

   public static void fastRemove(Object value, Vector values) {
      int index = values.indexOf(value);
      if (index != -1) {
         fastRemove(index, values);
      }
   }

   public static short[] readShortArray(DChocByteArray data) throws EOFException {
      int count = data.readInt();
      if (count <= 0) {
         return null;
      }

      short[] values = new short[count];

      for (int i = 0; i < count; i++) {
         values[i] = data.readShort();
      }

      return values;
   }

   public static int[] readIntArray(DChocByteArray data) throws EOFException {
      int count = data.readInt();
      if (count <= 0) {
         return null;
      }

      int[] values = new int[count];

      for (int i = 0; i < count; i++) {
         values[i] = data.readInt();
      }

      return values;
   }

   public static int[][] readIntValueList(DChocByteArray data) throws EOFException {
      int count = data.readInt();
      if (count <= 0) {
         return null;
      }

      int[][] values = new int[count][2];

      for (int i = 0; i < count; i++) {
         values[i][0] = data.readInt();
         values[i][1] = data.readInt();
      }

      return values;
   }

   public static int[] readResourceIDs(DChocByteArray data, boolean multiple) throws EOFException {
      int first = data.readInt();
      if (!multiple) {
         return first == -1 ? null : new int[]{first};
      }

      if (first <= 0) {
         return null;
      }

      int[] resources = new int[first];

      for (int i = 0; i < first; i++) {
         resources[i] = data.readInt();
      }

      return resources;
   }

   public static void writeLong(long value, DChocByteArray data) {
      data.writeInt((int)(value >>> 32));
      data.writeInt((int)value);
   }

   public static long readLong(DChocByteArray data) throws EOFException {
      long high = data.readInt();
      long low = data.readInt() & 4294967295L;
      return high << 32 | low;
   }

   public static float getRandomFloat() {
      return smRandom.nextFloat();
   }

   public static float getRandomFloat(float minimum, float maximum) {
      return minimum + (maximum - minimum) * smRandom.nextFloat();
   }

   public static int getRandomInt(int minimum, int maximum) {
      return minimum >= maximum ? minimum : Math.abs(smRandom.nextInt(maximum + 1 - minimum) + minimum);
   }

   public static int parseInt(String value, int fallback) {
      try {
         return Integer.parseInt(value);
      } catch (Exception exception) {
         return fallback;
      }
   }

   public static long parseLong(String value, long fallback) {
      try {
         return Long.parseLong(value);
      } catch (Exception exception) {
         return fallback;
      }
   }

   public static String formatTime(int minutes, boolean localized) {
      int weeks = minutes / 10080;
      int weekRemainder = minutes - weeks * 10080;
      int days = weekRemainder / 1440;
      if (minutes >= 10080) {
         int weekText = Integer.toUnsignedLong(minutes - 10080) < 10080L ? 2810 : 2809;
         String result = formatUnit(weeks, weekText);
         if (weekRemainder >= 1440) {
            int dayText = Integer.toUnsignedLong(weekRemainder - 1440) < 1440L ? 2811 : 24;
            result = result + " " + formatUnit(days, dayText);
         }

         return result;
      } else {
         int dayRemainder = weekRemainder - days * 1440;
         int hours = dayRemainder / 60;
         int hourText = Integer.toUnsignedLong(weekRemainder - 1440) < 1440L ? 2278 : 26;
         if (weekRemainder >= 1440) {
            int dayText = Integer.toUnsignedLong(weekRemainder - 1440) < 1440L ? 2811 : 24;
            String result = formatUnit(days, dayText);
            if (dayRemainder >= 60) {
               result = result + " " + formatUnit(hours, hourText);
            }

            return result;
         } else {
            int remainingMinutes = dayRemainder - hours * 60;
            if (!localized) {
               smParams[0] = Integer.toString(hours);
               smParams[1] = Integer.toString(remainingMinutes);
               return Toolkit.replaceParameters(Toolkit.getText(30), smParams);
            } else {
               String hourString = dayRemainder > 59 ? formatUnit(hours, hourText) : null;
               String minuteString = formatUnit(remainingMinutes, remainingMinutes == 1 ? 2279 : 27);
               return hourString == null ? minuteString : hourString + " " + minuteString;
            }
         }
      }
   }

   private static String formatUnit(int value, int text) {
      smParam[0] = Integer.toString(value);
      return Toolkit.replaceParameters(Toolkit.getText(text), smParam);
   }
}
