package com.dchoc.idead;

import java.util.TimeZone;

public class Time {
   public static long fromHours(int hours) {
      return hours * 3600000L;
   }

   public static long fromSeconds(long seconds) {
      return 1000L * seconds;
   }

   public static long getCoundownLeft(long timestamp) {
      return timestamp - System.currentTimeMillis();
   }

   public static long getDate(long timestamp) {
      return timestamp / 86400000L;
   }

   public static long getDateTime(long timestamp) {
      return timestamp % 86400000L;
   }

   public static long getTimeZone() {
      int seconds = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 1000;
      return seconds * 1000;
   }

   public static long getTimestamp() {
      return System.currentTimeMillis();
   }

   public static long getTimestampAfter(long delta) {
      return getTimestamp() + delta;
   }

   public static long getTimestampDelta(long timestamp) {
      return getTimestamp() - timestamp;
   }
}
