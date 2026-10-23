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

final class IosMusicPlayer implements Player {
   private final String locator;
   private final List<PlayerListener> listeners = new ArrayList<>();
   private Voice voice;
   private int state = 100;
   private int loopCount = 1;

   IosMusicPlayer(String locator) {
      this.locator = locator;
      this.voice = IosAudio.newVoice();
   }

   public void setLoopCount(int count) {
      this.loopCount = count;
   }

   public void start() {
      Buffer buffer = IosAudio.buffer(this.locator);
      if (buffer != null && this.voice != null) {
         this.voice.play(buffer, 1.0F, 1.0F, this.loopCount == -1 ? -1 : Math.max(0, this.loopCount - 1));
         this.state = 400;
      }
   }

   public void stop() {
      if (this.voice != null) {
         this.voice.stop();
      }
   }

   public void close() {
      this.stop();
      if (this.voice != null) {
         this.voice.release();
         this.voice = null;
      }

      this.state = 0;
   }

   public int getState() {
      if (this.state == 400 && (this.voice == null || !this.voice.playing())) {
         this.state = 200;
      }

      return this.state;
   }

   public void realize() {
      this.state = 200;
   }

   public void prefetch() {
      this.state = 300;
   }

   public void deallocate() {
   }

   public void addPlayerListener(PlayerListener listener) {
      if (!this.listeners.contains(listener)) {
         this.listeners.add(listener);
      }
   }

   public void removePlayerListener(PlayerListener listener) {
      this.listeners.remove(listener);
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
      NotPorted.method("zlport.ios.runtime.IosMusicPlayer#getControl(String)");
      return null;
   }

   public Control[] getControls() {
      NotPorted.method("zlport.ios.runtime.IosMusicPlayer#getControls()");
      return null;
   }

   public TimeBase getTimeBase() {
      NotPorted.method("zlport.ios.runtime.IosMusicPlayer#getTimeBase()");
      return null;
   }

   public void setTimeBase(TimeBase base) {
      NotPorted.method("zlport.ios.runtime.IosMusicPlayer#setTimeBase(TimeBase)");
   }

   boolean hasVoice() {
      return this.voice != null;
   }
}
