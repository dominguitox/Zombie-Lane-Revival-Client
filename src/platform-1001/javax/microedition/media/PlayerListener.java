package javax.microedition.media;

public interface PlayerListener {
   String BUFFERING_STARTED = "bufferingStarted";
   String BUFFERING_STOPPED = "bufferingStopped";
   String CLOSED = "closed";
   String DEVICE_AVAILABLE = "deviceAvailable";
   String DEVICE_UNAVAILABLE = "deviceUnavailable";
   String DURATION_UPDATED = "durationUpdated";
   String END_OF_MEDIA = "endOfMedia";
   String ERROR = "error";
   String RECORD_ERROR = "recordError";
   String RECORD_STARTED = "recordStarted";
   String RECORD_STOPPED = "recordStopped";
   String SIZE_CHANGED = "sizeChanged";
   String STARTED = "started";
   String STOPPED = "stopped";
   String STOPPED_AT_TIME = "stoppedAtTime";
   String VOLUME_CHANGED = "volumeChanged";

   void playerUpdate(Player var1, String var2, Object var3);
}
