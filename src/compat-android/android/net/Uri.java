package android.net;

import java.util.Objects;

public abstract class Uri {
   Uri() {
   }

   public static Uri parse(String uriString) {
      return new Uri.StringUri(Objects.requireNonNull(uriString, "uriString"));
   }

   @Override
   public abstract String toString();

   private static final class StringUri extends Uri {
      private final String text;

      StringUri(String text) {
         this.text = text;
      }

      @Override
      public String toString() {
         return this.text;
      }
   }
}
