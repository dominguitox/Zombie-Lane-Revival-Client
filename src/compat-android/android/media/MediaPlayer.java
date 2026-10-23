package android.media;

import android.content.Context;
import zlport.android.media.Audio;
import zlport.android.media.AudioOutput;

public class MediaPlayer {
   private final AudioOutput.Voice mVoice;
   private final AudioOutput.Buffer mBuffer;
   private float mGain = 1.0F;
   private boolean mLooping;
   private boolean mReleased;

   public MediaPlayer() {
      this(null);
   }

   private MediaPlayer(AudioOutput.Buffer buffer) {
      this.mBuffer = buffer;
      this.mVoice = Audio.output().newVoice();
   }

   public static MediaPlayer create(Context context, int resid) {
      return new MediaPlayer(Audio.buffer(resid));
   }

   public synchronized boolean isPlaying() {
      this.checkAlive();
      return this.mVoice.playing();
   }

   public synchronized void release() {
      if (!this.mReleased) {
         this.mReleased = true;
         this.mVoice.stop();
         this.mVoice.release();
      }
   }

   public synchronized void setLooping(boolean looping) {
      this.checkAlive();
      this.mLooping = looping;
      this.mVoice.setLooping(looping);
   }

   public synchronized boolean isLooping() {
      this.checkAlive();
      return this.mLooping;
   }

   public synchronized void setVolume(float leftVolume, float rightVolume) {
      this.checkAlive();
      this.mGain = SoundPool.clamp((leftVolume + rightVolume) / 2.0F, 0.0F, 1.0F);
      this.mVoice.setGain(this.mGain);
   }

   public synchronized void start() throws IllegalStateException {
      this.checkAlive();
      if (this.mBuffer == null) {
         throw new IllegalStateException("MediaPlayer sem fonte de dados");
      }

      if (!this.mVoice.playing()) {
         this.mVoice.play(this.mBuffer, this.mGain, 1.0F, this.mLooping ? -1 : 0);
      }
   }

   public synchronized void stop() throws IllegalStateException {
      this.checkAlive();
      this.mVoice.stop();
   }

   private void checkAlive() {
      if (this.mReleased) {
         throw new IllegalStateException("MediaPlayer já liberado (release)");
      }
   }
}
