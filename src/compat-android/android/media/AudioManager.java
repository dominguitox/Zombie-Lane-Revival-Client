package android.media;

public class AudioManager {
   private static final int[] DEFAULT_VOLUME = new int[]{4, 7, 5, 11, 6, 5};
   private static final int[] MAX_VOLUME = new int[]{5, 7, 7, 15, 7, 7};

   public int getStreamMaxVolume(int streamType) {
      return MAX_VOLUME[checkStream(streamType)];
   }

   public int getStreamVolume(int streamType) {
      return DEFAULT_VOLUME[checkStream(streamType)];
   }

   public void unloadSoundEffects() {
   }

   private static int checkStream(int streamType) {
      if (streamType >= 0 && streamType < MAX_VOLUME.length) {
         return streamType;
      } else {
         throw new IllegalArgumentException("Bad stream type " + streamType);
      }
   }
}
