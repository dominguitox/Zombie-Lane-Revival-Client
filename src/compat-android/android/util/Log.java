package android.util;

import java.lang.System.Logger.Level;

public final class Log {
   Log() {
   }

   public static int d(String tag, String msg) {
      return println(Level.DEBUG, tag, msg);
   }

   public static int e(String tag, String msg) {
      return println(Level.ERROR, tag, msg);
   }

   public static int i(String tag, String msg) {
      return println(Level.INFO, tag, msg);
   }

   public static int v(String tag, String msg) {
      return println(Level.TRACE, tag, msg);
   }

   private static int println(Level level, String tag, String msg) {
      if (msg == null) {
         throw new NullPointerException("println needs a message");
      }

      System.getLogger(tag == null ? "android" : "android." + tag).log(level, msg);
      return msg.length() + 1;
   }
}
