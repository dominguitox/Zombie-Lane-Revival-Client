package zlport.j2me;

import java.util.Iterator;
import java.util.Objects;
import java.util.ServiceLoader;

public final class Platform {
   private static volatile Surface surface;
   private static volatile GameDriver driver;

   private Platform() {
   }

   public static void install(Surface s) {
      surface = Objects.requireNonNull(s);
   }

   public static synchronized void uninstall() {
      surface = null;
      driver = null;
   }

   public static GameDriver driver() {
      GameDriver d = driver;
      if (d == null) {
         synchronized (Platform.class) {
            d = driver;
            if (d == null) {
               Iterator<GameDriver> drivers = ServiceLoader.load(GameDriver.class).iterator();
               d = drivers.hasNext() ? drivers.next() : new Driver1001();
               driver = d;
            }
         }
      }

      return d;
   }

   public static synchronized void installDriver(GameDriver d) {
      driver = Objects.requireNonNull(d);
   }

   public static Surface surface() {
      Surface s = surface;
      if (s == null) {
         throw new IllegalStateException("Nenhuma superfície instalada: o launcher chama Platform.install antes de criar o MIDlet.");
      } else {
         return s;
      }
   }
}
