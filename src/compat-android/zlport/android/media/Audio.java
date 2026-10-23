package zlport.android.media;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Audio {
   private static AudioOutput output;
   private static final Map<Integer, AudioOutput.Buffer> BUFFERS = new HashMap<>();

   private Audio() {
   }

   public static synchronized void install(AudioOutput o) {
      output = Objects.requireNonNull(o);
      BUFFERS.clear();
   }

   public static synchronized void uninstall() {
      output = null;
      BUFFERS.clear();
   }

   public static synchronized AudioOutput output() {
      return output != null ? output : MuteOutput.INSTANCE;
   }

   public static synchronized boolean installed() {
      return output != null;
   }

   public static synchronized AudioOutput.Buffer buffer(int resId) {
      AudioOutput.Buffer b = BUFFERS.get(resId);
      if (b == null) {
         WavReader.Pcm pcm = RawResources.read(resId);
         b = output().load(pcm.samples(), pcm.sampleRate());
         BUFFERS.put(resId, b);
      }

      return b;
   }
}
