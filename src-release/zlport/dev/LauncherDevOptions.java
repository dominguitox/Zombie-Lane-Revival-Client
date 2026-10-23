package zlport.dev;

/**
 * No-op {@link LauncherDevOptions} for the player edition.
 *
 * <p>This is the implementation used by {@code tools/build-client.ps1}. The
 * development tree keeps the real implementation in {@code src-dev}; that
 * directory is intentionally absent from this repository and must never be
 * added back. The pre-game launcher's dev-only shared-missions controls are
 * therefore not part of the player binary. Keep the public surface identical
 * to the dev implementation.</p>
 */
public final class LauncherDevOptions {
   private LauncherDevOptions() {
   }

   /** Adds nothing in the player edition. */
   public static void addSharedMissionsField(java.awt.Container advanced, Object constraints, String seedValue) {
   }

   /** Always empty in the player edition. */
   public static String sharedMissionsDirectory() {
      return "";
   }
}
