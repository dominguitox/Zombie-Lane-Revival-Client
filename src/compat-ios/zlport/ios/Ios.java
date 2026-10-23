package zlport.ios;

import java.util.Objects;

public final class Ios {
   private static volatile IosPlatform installedPlatform;

   private Ios() {
   }

   public static void install(IosPlatform platform) {
      installedPlatform = Objects.requireNonNull(platform, "IosPlatform no puede ser nula");
   }

   public static void uninstall() {
      installedPlatform = null;
   }

   public static IosPlatform platform() {
      IosPlatform platform = installedPlatform;
      if (platform == null) {
         throw new IllegalStateException("No hay ninguna IosPlatform instalada; runtime-ios la instala antes de crear el MIDlet");
      } else {
         return platform;
      }
   }
}
