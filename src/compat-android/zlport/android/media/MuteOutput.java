package zlport.android.media;

final class MuteOutput implements AudioOutput {
   static final MuteOutput INSTANCE = new MuteOutput();

   private MuteOutput() {
   }

   @Override
   public AudioOutput.Buffer load(short[] mono, int sampleRate) {
      return new MuteOutput.MuteBuffer();
   }

   @Override
   public AudioOutput.Voice newVoice() {
      return new MuteOutput.MuteVoice();
   }

   private static final class MuteBuffer implements AudioOutput.Buffer {
   }

   private static final class MuteVoice implements AudioOutput.Voice {
      private volatile boolean playing;

      @Override
      public void play(AudioOutput.Buffer buffer, float gain, float rate, int loop) {
         this.playing = true;
      }

      @Override
      public void stop() {
         this.playing = false;
      }

      @Override
      public boolean playing() {
         return this.playing;
      }

      @Override
      public void setGain(float gain) {
      }

      @Override
      public void setLooping(boolean looping) {
      }

      @Override
      public void release() {
         this.playing = false;
      }
   }
}
