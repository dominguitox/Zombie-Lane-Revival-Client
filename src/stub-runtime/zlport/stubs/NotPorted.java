package zlport.stubs;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public final class NotPorted {
   public static final String MODE_PROPERTY = "zlport.stubs";
   private static final Logger LOG = System.getLogger("zlport.stubs");
   private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();

   private NotPorted() {
   }

   public static void method(String signature) {
      boolean first = REPORTED.add(signature);
      if (lenient()) {
         if (first) {
            LOG.log(Level.WARNING, "não portado (modo lenient, seguindo): " + signature);
         }
      } else {
         if (first) {
            LOG.log(Level.ERROR, "não portado: " + signature);
         }

         throw new NotPortedException(signature);
      }
   }

   public static SortedSet<String> reported() {
      return Collections.unmodifiableSortedSet(new TreeSet<>(REPORTED));
   }

   public static <T> T placeholder(T value) {
      return value;
   }

   static boolean lenient() {
      String mode = System.getProperty("zlport.stubs", "strict");

      return switch (mode) {
         case "strict" -> false;
         case "lenient" -> true;
         default -> throw new IllegalStateException("Valor inválido para -Dzlport.stubs: '" + mode + "' (use strict ou lenient)");
      };
   }
}
