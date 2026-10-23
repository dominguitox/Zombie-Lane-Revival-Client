package zlport.android.media;

public interface AudioOutput {
   AudioOutput.Buffer load(short[] var1, int var2);

   AudioOutput.Voice newVoice();

   interface Buffer {
   }

   interface Voice {
      void play(AudioOutput.Buffer var1, float var2, float var3, int var4);

      void stop();

      boolean playing();

      void setGain(float var1);

      void setLooping(boolean var1);

      void release();
   }
}
