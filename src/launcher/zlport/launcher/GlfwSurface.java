package zlport.launcher;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.lwjgl.glfw.GLFW;
import zlport.j2me.Renderer;
import zlport.j2me.Surface;

final class GlfwSurface implements Surface {
   private final int displayWidth;
   private volatile Renderer renderer;
   private volatile boolean iconified;
   private volatile boolean unfocused;
   private volatile int refreshRate;
   private final AtomicBoolean renderRequested = new AtomicBoolean();
   private final AtomicBoolean destroyed = new AtomicBoolean();
   private final Object glSignal = new Object();
   private final Object drawGate = new Object();
   private boolean glWork;
   private boolean closed;
   private boolean drawing = true;

   GlfwSurface(int displayWidth) {
      this.displayWidth = displayWidth;
   }

   public void setRenderer(Renderer r) {
      this.renderer = r;
      this.wakeGl();
   }

   public void requestRender() {
      this.renderRequested.set(true);
      this.wakeGl();
   }

   public int displayWidth() {
      return this.displayWidth;
   }

   public int refreshRate() {
      return this.refreshRate;
   }

   public void midletDestroyed() {
      if (this.destroyed.compareAndSet(false, true)) {
         this.wakeMain();
      }
   }

   synchronized void wakeMain() {
      if (!this.closed) {
         GLFW.glfwPostEmptyEvent();
      }
   }

   void wakeGl() {
      synchronized (this.glSignal) {
         this.glWork = true;
         this.glSignal.notifyAll();
      }
   }

   void awaitGlWork(double seconds) {
      long deadline = System.nanoTime() + (long)(seconds * 1.0E9);
      synchronized (this.glSignal) {
         long remaining;
         try {
            while (!this.glWork && (remaining = deadline - System.nanoTime()) > 0L) {
               TimeUnit.NANOSECONDS.timedWait(this.glSignal, remaining);
            }
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }

         this.glWork = false;
      }
   }

   synchronized void close() {
      this.closed = true;
   }

   Renderer renderer() {
      return this.renderer;
   }

   boolean awaitPendingRender(long timeoutNanos) throws InterruptedException {
      long deadline = System.nanoTime() + timeoutNanos;
      synchronized (this.glSignal) {
         long remaining;
         while (!this.renderRequested.get() && (remaining = deadline - System.nanoTime()) > 0L) {
            TimeUnit.NANOSECONDS.timedWait(this.glSignal, remaining);
         }

         return this.renderRequested.get();
      }
   }

   boolean takeRenderRequest() {
      synchronized (this.drawGate) {
         return this.drawing && this.renderRequested.getAndSet(false);
      }
   }

   void stopDrawing() {
      synchronized (this.drawGate) {
         this.drawing = false;
      }
   }

   void requestFirstFrame() {
      this.renderRequested.set(true);
   }

   boolean destroyed() {
      return this.destroyed.get();
   }

   void setIconified(boolean value) {
      this.iconified = value;
      this.wakeGl();
   }

   void setRefreshRate(int value) {
      this.refreshRate = Math.max(0, value);
   }

   void setUnfocused(boolean value) {
      this.unfocused = value;
      this.wakeGl();
   }

   boolean frozen() {
      return this.iconified || this.unfocused;
   }
}
