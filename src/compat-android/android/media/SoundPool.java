package android.media;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import zlport.android.media.Audio;
import zlport.android.media.AudioOutput;

public class SoundPool {
   private final AudioOutput.Voice[] mVoices;
   private final int[] mStreamIds;
   private final int[] mPriorities;
   private final long[] mStartOrder;
   private final Map<Integer, AudioOutput.Buffer> mSamples = new HashMap<>();
   private long mOrder;
   private int mNextSampleId;
   private int mNextStreamId;
   private boolean mReleased;

   public SoundPool(int maxStreams, int streamType, int srcQuality) {
      if (maxStreams <= 0) {
         throw new IllegalArgumentException("maxStreams deve ser positivo: " + maxStreams);
      }

      AudioOutput out = Audio.output();
      this.mVoices = new AudioOutput.Voice[maxStreams];

      for (int i = 0; i < maxStreams; i++) {
         this.mVoices[i] = out.newVoice();
      }

      this.mStreamIds = new int[maxStreams];
      this.mPriorities = new int[maxStreams];
      this.mStartOrder = new long[maxStreams];
   }

   public synchronized int load(Context context, int resId, int priority) {
      if (this.mReleased) {
         return 0;
      }

      AudioOutput.Buffer buffer = Audio.buffer(resId);
      int id = ++this.mNextSampleId;
      this.mSamples.put(id, buffer);
      return id;
   }

   public final synchronized int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) {
      AudioOutput.Buffer buffer = this.mSamples.get(soundID);
      if (!this.mReleased && buffer != null) {
         int channel = this.allocate(priority);
         if (channel < 0) {
            return 0;
         }

         this.mVoices[channel].play(buffer, clamp((leftVolume + rightVolume) / 2.0F, 0.0F, 1.0F), clamp(rate, 0.5F, 2.0F), loop);
         this.mStreamIds[channel] = ++this.mNextStreamId;
         this.mPriorities[channel] = priority;
         this.mStartOrder[channel] = ++this.mOrder;
         return this.mStreamIds[channel];
      } else {
         return 0;
      }
   }

   private int allocate(int priority) {
      int best = -1;

      for (int i = 0; i < this.mVoices.length; i++) {
         if (!this.mVoices[i].playing()) {
            return i;
         }

         if (this.mPriorities[i] <= priority
            && (
               best < 0
                  || this.mPriorities[i] < this.mPriorities[best]
                  || this.mPriorities[i] == this.mPriorities[best] && this.mStartOrder[i] < this.mStartOrder[best]
            )) {
            best = i;
         }
      }

      return best;
   }

   public final synchronized void release() {
      if (!this.mReleased) {
         this.mReleased = true;
         this.mSamples.clear();

         for (AudioOutput.Voice v : this.mVoices) {
            v.stop();
            v.release();
         }
      }
   }

   public final synchronized void stop(int streamID) {
      if (!this.mReleased) {
         for (int i = 0; i < this.mVoices.length; i++) {
            if (this.mStreamIds[i] == streamID && this.mVoices[i].playing()) {
               this.mVoices[i].stop();
            }
         }
      }
   }

   static float clamp(float v, float lo, float hi) {
      return Float.isNaN(v) ? lo : Math.max(lo, Math.min(hi, v));
   }
}
