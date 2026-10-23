package javax.microedition.media;

public interface Player extends Controllable {
   int CLOSED = 0;
   int PREFETCHED = 300;
   int REALIZED = 200;
   int STARTED = 400;
   int TIME_UNKNOWN = -1;
   int UNREALIZED = 100;

   void addPlayerListener(PlayerListener var1);

   void close();

   void deallocate();

   String getContentType();

   long getDuration();

   long getMediaTime();

   int getState();

   TimeBase getTimeBase();

   void prefetch() throws MediaException;

   void realize() throws MediaException;

   void removePlayerListener(PlayerListener var1);

   void setLoopCount(int var1);

   long setMediaTime(long var1) throws MediaException;

   void setTimeBase(TimeBase var1) throws MediaException;

   void start() throws MediaException;

   void stop() throws MediaException;
}
