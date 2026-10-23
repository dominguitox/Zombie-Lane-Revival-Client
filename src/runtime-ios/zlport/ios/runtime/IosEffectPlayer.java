package zlport.ios.runtime;

import java.util.ArrayList;
import java.util.List;
import javax.microedition.media.Control;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.TimeBase;
import zlport.android.media.AudioOutput.Buffer;
import zlport.android.media.AudioOutput.Voice;
import zlport.stubs.NotPorted;

final class IosEffectPlayer implements Player {
   private final String locator;
   private final List<PlayerListener> listeners = new ArrayList<>();
   private Voice voice;
   private int state = 100;
   private boolean looping;
   private boolean started;

   IosEffectPlayer(String locator) {
      this.locator = locator;
      this.voice = IosAudio.newVoice();
      IosAudio.track(this);
   }

   public void setLoopCount(int count) {
      this.looping = count == -1;
      if (this.voice != null) {
         this.voice.setLooping(this.looping);
      }
   }

   public void start() {
      Buffer buffer = IosAudio.buffer(this.locator);
      if (buffer != null) {
         if (this.voice == null) {
            this.voice = IosAudio.newVoice();
            IosAudio.track(this);
         }

         this.voice.play(buffer, 1.0F, 1.0F, this.looping ? -1 : 0);
         this.state = 400;
         this.started = true;
      }
   }

   public void stop() {
      if (this.voice != null) {
         this.voice.stop();
      }
   }

   public void close() {
      this.stop();
      this.releaseVoice();
      this.state = 0;
   }

   public int getState() {
      if (this.state == 400 && (this.voice == null || !this.voice.playing())) {
         this.state = 300;
      }

      return this.state;
   }

   public void realize() {
      IosAudio.buffer(this.locator);
      this.state = 200;
   }

   public void prefetch() {
      IosAudio.buffer(this.locator);
      this.state = 300;
   }

   public void deallocate() {
      this.stop();
   }

   public void addPlayerListener(PlayerListener listener) {
      if (!this.listeners.contains(listener)) {
         this.listeners.add(listener);
      }
   }

   public void removePlayerListener(PlayerListener listener) {
   }

   public String getContentType() {
      return null;
   }

   public long getDuration() {
      return 0L;
   }

   public long getMediaTime() {
      return 0L;
   }

   public long setMediaTime(long time) {
      return 0L;
   }

   public Control getControl(String type) {
      NotPorted.method("zlport.ios.runtime.IosEffectPlayer#getControl(String)");
      return null;
   }

   public Control[] getControls() {
      NotPorted.method("zlport.ios.runtime.IosEffectPlayer#getControls()");
      return null;
   }

   public TimeBase getTimeBase() {
      NotPorted.method("zlport.ios.runtime.IosEffectPlayer#getTimeBase()");
      return null;
   }

   public void setTimeBase(TimeBase base) {
      NotPorted.method("zlport.ios.runtime.IosEffectPlayer#setTimeBase(TimeBase)");
   }

   boolean reap() {
      if (this.voice == null) {
         return true;
      } else if (this.started && !this.looping && !this.voice.playing()) {
         this.releaseVoice();
         return true;
      } else {
         return false;
      }
   }

   boolean hasVoice() {
      return this.voice != null;
   }

   private void releaseVoice() {
      if (this.voice != null) {
         this.voice.release();
         this.voice = null;
      }
   }
}
