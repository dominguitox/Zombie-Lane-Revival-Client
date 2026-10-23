package zlport.launcher;

import java.util.Arrays;

/**
 * In-memory hand-off for the optional password typed in the pre-game launcher.
 *
 * <p>The password exists only here: it is never written to
 * {@code launcher.properties} or {@code zombie-lane.properties}, never printed
 * to any log, never placed on the command line and never put into a system
 * property. The signature keeps it out of generated record {@code toString}
 * output by design.</p>
 *
 * <p>Current use: the launcher dialog stages the typed password, authenticates
 * with {@link ServerConnection#authenticate} (a form POST to the server's
 * {@code /auth/register} or {@code /auth/login}), and the staged copy is left
 * for the game handshake, where the {@code CreateNewSession} request consumes
 * it with {@link #takeAsString()} and the server verifies the salted hash.
 * The password therefore travels twice in an HTTP request body over plain
 * HTTP: loopback or a trusted LAN only (the launcher UI says so).</p>
 */
public final class PendingCredentials {
   private static volatile char[] password;

   private PendingCredentials() {
   }

   /** Stages the password for this JVM run, replacing any previous value. */
   public static void stage(char[] chars) {
      char[] previous = password;
      password = chars == null || chars.length == 0 ? null : Arrays.copyOf(chars, chars.length);
      if (previous != null) {
         Arrays.fill(previous, '\0');
      }
   }

   /**
    * The staged password, or null. The array is a copy: clearing the caller's
    * copy does not affect the staged value.
    */
   static char[] take() {
       char[] chars = password;
       return chars == null ? null : Arrays.copyOf(chars, chars.length);
    }

    /** Consumes the staged password for the legacy session bootstrap; erases stored chars. */
    public static String takeAsString() {
       char[] chars = password;
       password = null;
       if (chars == null) return null;
       try {
          return new String(chars);
       } finally {
          Arrays.fill(chars, '\0');
       }
    }
}
