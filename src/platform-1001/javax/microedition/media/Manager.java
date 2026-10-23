package javax.microedition.media;

import java.io.IOException;
import java.io.InputStream;

public class Manager {
   private static BasicPlayer basicPlayer;

   public static Player createPlayer(InputStream var0, String var1) throws IOException, MediaException {
      basicPlayer = new BasicPlayer(var0);
      return basicPlayer;
   }

   public static Player createPlayer(String var0) throws IOException, MediaException {
      return new BasicPlayer(var0);
   }

   public static String[] getSupportedContentTypes(String var0) {
      throw new RuntimeException("method not supported");
   }

   public static String[] getSupportedProtocols(String var0) {
      throw new RuntimeException("method not supported");
   }

   public static void playTone(int var0, int var1, int var2) throws MediaException {
      throw new RuntimeException("method not supported");
   }
}
