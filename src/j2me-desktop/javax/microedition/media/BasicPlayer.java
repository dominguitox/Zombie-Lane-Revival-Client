package javax.microedition.media;

import java.io.IOException;
import java.io.InputStream;
import zlport.j2me.Platform;
import zlport.stubs.NotPorted;

public class BasicPlayer implements Player {
   private final Player delegate;

   public BasicPlayer(InputStream p0) throws IOException {
      NotPorted.method("javax.microedition.media.BasicPlayer#<init>(InputStream)");
      this.delegate = null;
   }

   public BasicPlayer(String p0) throws IOException {
      this.delegate = Platform.driver().createPlayer(p0);
   }

   public void addPlayerListener(PlayerListener p0) {
      this.delegate.addPlayerListener(p0);
   }

   public void close() {
      this.delegate.close();
   }

   public void deallocate() {
      this.delegate.deallocate();
   }

   public String getContentType() {
      return this.delegate.getContentType();
   }

   public Control getControl(String p0) {
      return this.delegate.getControl(p0);
   }

   public Control[] getControls() {
      return this.delegate.getControls();
   }

   public long getDuration() {
      return this.delegate.getDuration();
   }

   public long getMediaTime() {
      return this.delegate.getMediaTime();
   }

   public int getState() {
      return this.delegate.getState();
   }

   public TimeBase getTimeBase() {
      return this.delegate.getTimeBase();
   }

   public void prefetch() throws MediaException {
      this.delegate.prefetch();
   }

   public void realize() throws MediaException {
      this.delegate.realize();
   }

   public void removePlayerListener(PlayerListener p0) {
      this.delegate.removePlayerListener(p0);
   }

   public void setLoopCount(int p0) {
      this.delegate.setLoopCount(p0);
   }

   public long setMediaTime(long p0) throws MediaException {
      return this.delegate.setMediaTime(p0);
   }

   public void setTimeBase(TimeBase p0) throws MediaException {
      this.delegate.setTimeBase(p0);
   }

   public void start() throws MediaException {
      this.delegate.start();
   }

   public void stop() throws MediaException {
      this.delegate.stop();
   }
}
