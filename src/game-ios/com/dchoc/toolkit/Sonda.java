package com.dchoc.toolkit;

import java.io.EOFException;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The load paths of this game use exceptions as regular signals (end of
 * stream, missing optional resource, record store not created yet).
 * Printing each of them fills the console and the log file with non-serious
 * "errors". This class counts them instead: it prints a signature once
 * (up to {@value #MAX_WARNS} signatures) and summarizes on demand with
 * {@link #summarize}.
 */
public final class Sonda {
   private static final System.Logger LOG = System.getLogger("zlport.probe");
   private static final int MAX_WARNS = 5;
   private static final Map<String, Integer> PROBES = new LinkedHashMap<String, Integer>();
   private static final Map<String, Integer> FAILURES = new LinkedHashMap<String, Integer>();
   private static int warns;
   private static int totalProbes;
   private static int totalFailures;

   private Sonda() { }

   /** Benign probe: counts and prints only the first time per signature. */
   public static void probe(String context, Throwable error) {
      if (error == null) {
         return;
      }
      probe(context, message(error));
   }

   public static void probe(String context, String reason) {
      String signature = context + " -> " + reason;
      synchronized (Sonda.class) {
         totalProbes++;
         Integer count = PROBES.merge(signature, Integer.valueOf(1), Integer::sum);
         if (count.intValue() == 1 && warns < MAX_WARNS) {
            warns++;
            LOG.log(Level.DEBUG, "probe ({0}): {1}", Integer.valueOf(warns), signature);
         }
      }
   }

   /** Ignore EOF used by table readers as their normal end-of-data marker. */
   public static void probeNonEof(String context, Throwable error) {
      if (!(error instanceof EOFException)) {
         probe(context, error);
      }
   }

   /** Real failure: printed once with the stack trace, then every 100 repeats. */
   public static void fail(String context, Throwable error) {
      if (error == null) {
         return;
      }
      String reason = message(error);
      String signature = context + " -> " + reason;
      synchronized (Sonda.class) {
         totalFailures++;
         Integer count = FAILURES.merge(signature, Integer.valueOf(1), Integer::sum);
         if (count.intValue() == 1) {
            LOG.log(Level.ERROR, signature, error);
         } else if (count.intValue() % 100 == 0) {
            LOG.log(Level.ERROR, "{0} (x{1})", signature, count);
         }
      }
   }

   private static String message(Throwable error) {
      String reason = error.getMessage();
      return reason == null || reason.isEmpty() ? error.getClass().getSimpleName() : reason;
   }

   /** Summary on demand (end of the boot load, shutdown): totals and top signatures. */
   public static void summarize(String phase) {
      List<Map.Entry<String, Integer>> probes;
      List<Map.Entry<String, Integer>> failures;
      int total;
      synchronized (Sonda.class) {
         if (totalProbes + totalFailures == 0) {
            return;
         }
         total = totalProbes + totalFailures;
         probes = new ArrayList<Map.Entry<String, Integer>>(PROBES.entrySet());
         failures = new ArrayList<Map.Entry<String, Integer>>(FAILURES.entrySet());
         PROBES.clear();
         FAILURES.clear();
         totalProbes = 0;
         totalFailures = 0;
         warns = 0;
      }

      LOG.log(Level.INFO, "Probes and failures during {0}: {1} events across {2} signatures (list below)",
         phase, Integer.valueOf(total), Integer.valueOf(probes.size() + failures.size()));
      reportGroup(failures, "failure", false);
      reportGroup(probes, "probe", true);
   }

   private static void reportGroup(List<Map.Entry<String, Integer>> entries, String kind, boolean capAtEight) {
      entries.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()));
      int shown = Math.min(capAtEight ? 8 : entries.size(), entries.size());
      for (int index = 0; index < shown; index++) {
         Map.Entry<String, Integer> entry = entries.get(index);
         LOG.log(Level.INFO, "  {0} x{1}: {2}", kind, entry.getValue(), entry.getKey());
      }
      if (entries.size() > shown) {
         LOG.log(Level.INFO, "  ... and {0} more {1} signatures", Integer.valueOf(entries.size() - shown), kind);
      }
   }
}
