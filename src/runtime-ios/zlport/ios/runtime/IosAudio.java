package zlport.ios.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import zlport.android.media.Audio;
import zlport.android.media.AudioOutput;
import zlport.android.media.WavReader;
import zlport.android.media.AudioOutput.Buffer;
import zlport.android.media.AudioOutput.Voice;
import zlport.android.media.WavReader.Pcm;
import zlport.ios.Ios;

public final class IosAudio {
   private static final Logger LOG = System.getLogger("zlport.ios");
   private static final Map<String, Buffer> BUFFERS = new HashMap<>();
   private static final Set<String> MISSING = new HashSet<>();
   private static final List<IosEffectPlayer> EFFECTS = new ArrayList<>();
   private static AudioOutput cacheOutput;

   private IosAudio() {
   }

   static String fileName(String locator) {
      return locator.endsWith(".mp3") ? locator.substring(0, locator.length() - 4) + ".wav" : locator;
   }

   static synchronized Buffer buffer(String locator) {
      AudioOutput output = Audio.output();
      if (output != cacheOutput) {
         BUFFERS.clear();
         cacheOutput = output;
      }

      Buffer buffer = BUFFERS.get(locator);
      if (buffer == null && !MISSING.contains(locator)) {
         Pcm pcm;
         try (InputStream in = Ios.platform().openResource(fileName(locator))) {
            pcm = WavReader.read(in);
         } catch (IOException | RuntimeException e) {
            MISSING.add(locator);
            LOG.log(Level.WARNING, "som " + locator + " não tocado (" + fileName(locator) + "): " + e.getMessage());
            return null;
         }

         buffer = output.load(pcm.samples(), pcm.sampleRate());
         BUFFERS.put(locator, buffer);
         return buffer;
      } else {
         return buffer;
      }
   }

   static synchronized Voice newVoice() {
      return Audio.output().newVoice();
   }

   static synchronized void track(IosEffectPlayer player) {
      Iterator<IosEffectPlayer> it = EFFECTS.iterator();

      while (it.hasNext()) {
         if (it.next().reap()) {
            it.remove();
         }
      }

      EFFECTS.add(player);
   }

   static synchronized int trackedEffects() {
      return EFFECTS.size();
   }

   static synchronized void reset() {
      BUFFERS.clear();
      MISSING.clear();
      EFFECTS.clear();
      cacheOutput = null;
   }
}
