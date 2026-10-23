package zlport.android.media;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class RawResources {
   private static volatile Map<Integer, String> names = Map.of();
   private static volatile Function<String, InputStream> opener = RawResources::classpath;

   private RawResources() {
   }

   public static void register(Class<?> rRaw) {
      Map<Integer, String> tabela = new HashMap<>();

      for (Field campo : rRaw.getFields()) {
         int modificadores = campo.getModifiers();
         if (Modifier.isStatic(modificadores) && Modifier.isFinal(modificadores) && campo.getType() == int.class) {
            try {
               tabela.put(campo.getInt(null), campo.getName());
            } catch (IllegalAccessException e) {
               throw new IllegalStateException("campo " + campo.getName() + " de " + rRaw.getName() + " inacessível", e);
            }
         }
      }

      register(tabela, RawResources::classpath);
   }

   public static void register(Map<Integer, String> table, Function<String, InputStream> open) {
      names = Map.copyOf(table);
      opener = Objects.requireNonNull(open);
   }

   public static void clear() {
      names = Map.of();
      opener = RawResources::classpath;
   }

   public static String name(int id) {
      return names.get(id);
   }

   public static WavReader.Pcm read(int id) {
      String name = names.get(id);
      if (name == null) {
         throw new IllegalStateException("res/raw 0x" + Integer.toHexString(id) + " sem nome (tabela R$raw não registrada?)");
      }

      try (InputStream in = opener.apply(name)) {
         if (in == null) {
            throw new IllegalStateException("res/raw/" + name + ".wav não encontrado");
         } else {
            return WavReader.read(in);
         }
      } catch (IOException e) {
         throw new UncheckedIOException("res/raw/" + name + ".wav", e);
      }
   }

   private static InputStream classpath(String name) {
      return RawResources.class.getResourceAsStream("/res/raw/" + name + ".wav");
   }
}
