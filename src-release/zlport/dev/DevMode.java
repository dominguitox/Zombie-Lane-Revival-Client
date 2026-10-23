package zlport.dev;

/**
 * No-op {@link DevMode} for the player edition.
 *
 * <p>This is the implementation used by {@code tools/build-client.ps1}. The
 * development tree keeps the real implementation in {@code src-dev}; that
 * directory is intentionally absent from this repository and must never be
 * added back. Keep the public surface identical to the dev implementation so
 * client code can call it unconditionally.</p>
 */
public final class DevMode {
   private DevMode() {
   }

   /** Always false in the player edition. */
   public static boolean enabled() {
      return false;
   }

   /** Always empty in the player edition. */
   public static String banner() {
      return "";
   }
}
