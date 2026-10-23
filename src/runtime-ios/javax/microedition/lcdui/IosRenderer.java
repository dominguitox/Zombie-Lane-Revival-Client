package javax.microedition.lcdui;

import com.dchoc.opengl.OGLHelpers;
import com.dchoc.toolkit.DChocMIDlet;
import com.dchoc.toolkit.Toolkit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import zlport.ios.runtime.IosHoverCollect;
import zlport.ios.runtime.IosWheelZoom;
import zlport.j2me.Platform;
import zlport.j2me.Renderer;
import zlport.j2me.Touch;

public final class IosRenderer implements Renderer {
   private static final int GL_ALPHA_TEST = 3008;
   private static final int GL_LINE_SMOOTH = 2848;
   private static final int GL_SCISSOR_TEST = 3089;
   private static final int GL_TEXTURE_ENV = 8960;
   private static final int GL_TEXTURE_ENV_MODE = 8704;
   private static final int GL_REPLACE = 7681;
   private static final int MAX_TOUCHES = 5;
   private static final long FRAME_INTERVAL_NANOS = 16666667L;
   private static final long PACE_SLACK_NANOS = 1500000L;
   private static final long PARK_MARGIN_NANOS = 2000000L;
   private static final int VSYNC_MAX_RATE = 61;
   private static final IosRenderer.PaceClock SYSTEM_CLOCK = new IosRenderer.PaceClock() {
      @Override
      public long nanoTime() {
         return System.nanoTime();
      }

      @Override
      public void park(long nanos) {
         LockSupport.parkNanos(nanos);
      }

      @Override
      public void spin() {
         Thread.yield();
      }
   };
   private static final ThreadLocal<GL11> CURRENT_GL = new ThreadLocal<>();
   private final Displayable displayable;
   private final IosRenderer.PaceClock clock;
   private final AtomicBoolean repaint = new AtomicBoolean();
   private final ArrayDeque<IosRenderer.PendingTouch> touches = new ArrayDeque<>();
   private final ArrayDeque<IosRenderer.PendingTouch> multiTouches = new ArrayDeque<>();
   private final int[][] multiTouchArray = new int[1][4];
   private final int[] gestureArray = new int[4];
   private volatile GL11 gl;
   private volatile int height;
   private int width;
   private boolean frameStarted;
   private long previousFrameStart;

   public IosRenderer(Displayable displayable) {
      this(displayable, SYSTEM_CLOCK);
   }

   IosRenderer(Displayable displayable, IosRenderer.PaceClock clock) {
      this.displayable = Objects.requireNonNull(displayable);
      this.clock = Objects.requireNonNull(clock);
   }

   public GL11 graphicsContext() {
      return this.gl;
   }

   public int surfaceHeight() {
      return this.height;
   }

   public static GL11 currentGl() {
      GL11 current = CURRENT_GL.get();
      if (current == null) {
         throw new IllegalStateException("Esta thread ainda não recebeu o contexto GL do jogo");
      } else {
         return current;
      }
   }

   public void doDraw() {
      this.repaint.set(true);
      Platform.surface().requestRender();
   }

   public boolean touch(Touch event) {
      if (event.getPointerCount() != 1) {
         return false;
      }

      int type = switch (event.getAction() & 0xFF) {
         case 0 -> 1;
         case 1 -> 4;
         case 2 -> 2;
         case 3 -> 3;
         default -> 0;
      };
      if (type == 0) {
         return false;
      }

      IosRenderer.PendingTouch copy = new IosRenderer.PendingTouch(type, (int)event.getX(), (int)event.getY());
      synchronized (this.touches) {
         this.multiTouches.addLast(copy);
         if (this.touches.size() < 5) {
            this.touches.addLast(copy);
         }
      }

      Platform.surface().requestRender();
      return true;
   }

   public void onSurfaceCreated(GL10 context) {
      this.bind(context);
   }

   public void onSurfaceChanged(GL10 context, int newWidth, int newHeight) {
      GL11 current = this.bind(context);
      this.width = newWidth;
      this.height = newHeight;
      current.glViewport(0, 0, this.width, this.height);
   }

   public void onDrawFrame(GL10 context) {
      this.paceFrame();
      GL11 current = this.bind(context);
      this.flushMultiTouches();
      if (this.repaint.getAndSet(false)) {
         this.paint(current);
      }

      this.flushTouches();
      Display.runNextSerialCall();
      IosWheelZoom.apply(System.nanoTime());
      IosHoverCollect.apply();
      if (Display.hasSerialCalls() || this.repaint.get() || this.hasTouches() || IosWheelZoom.pending() || IosHoverCollect.pending()) {
         Platform.surface().requestRender();
      }
   }

   public void pumpTextures(GL10 context) {
      this.bind(context);
      if (Display.hasSerialCalls() || this.hasTouches()) {
         Platform.surface().requestRender();
      }
   }

   private GL11 bind(GL10 context) {
      GL11 current = Objects.requireNonNull(context);
      if (this.gl != current || CURRENT_GL.get() != current) {
         OGLHelpers.setGL(current);
      }

      CURRENT_GL.set(current);
      this.gl = current;
      return current;
   }

   private void paint(GL11 current) {
      GLGraphics graphics = GLGraphics.getInstance();
      graphics.setGl(current);

      try {
         current.glViewport(0, 0, this.width, this.height);
         current.glDisable(3008);
         current.glTexEnvi(8960, 8704, 7681);
         current.glEnable(2848);
         current.glDisable(3089);
         if (this.displayable instanceof Toolkit toolkit) {
            toolkit.paint(graphics);
         } else {
            this.displayable.paint(graphics);
         }
      } finally {
         current.glPopMatrix();
      }
   }

   private void flushTouches() {
      List<IosRenderer.PendingTouch> pending;
      synchronized (this.touches) {
         pending = new ArrayList<>(this.touches);
         this.touches.clear();
      }

      for (IosRenderer.PendingTouch event : pending) {
         switch (event.type()) {
            case 1:
               this.displayable.pointerPressed(event.x(), event.y());
               break;
            case 2:
               this.displayable.pointerDragged(event.x(), event.y());
               break;
            case 3:
            case 4:
               this.displayable.pointerReleased(event.x(), event.y());
               break;
            default:
               throw new IllegalStateException("Tipo de toque não reconhecido: " + event.type());
         }
      }
   }

   private void flushMultiTouches() {
      List<IosRenderer.PendingTouch> pending;
      synchronized (this.touches) {
         pending = new ArrayList<>(this.multiTouches);
         this.multiTouches.clear();
      }

      DChocMIDlet midlet = DChocMIDlet.getInstance();
      if (midlet != null) {
         for (IosRenderer.PendingTouch event : pending) {
            int[] touch = this.multiTouchArray[0];
            touch[0] = event.x();
            touch[1] = event.y();

            touch[2] = switch (event.type()) {
               case 1 -> 0;
               case 2 -> 2;
               default -> 1;
            };
            touch[3] = 0;
            Arrays.fill(this.gestureArray, 0);
            midlet.touchEventOccurred(this.multiTouchArray, this.gestureArray);
         }
      }
   }

   private boolean hasTouches() {
      synchronized (this.touches) {
         return !this.touches.isEmpty() || !this.multiTouches.isEmpty();
      }
   }

   static boolean needsPacing(int refreshRate) {
      return refreshRate <= 0 || refreshRate > 61;
   }

   private void paceFrame() {
      long now = this.clock.nanoTime();
      if (this.frameStarted && needsPacing(Platform.surface().refreshRate())) {
         long deadline = this.previousFrameStart + 16666667L - 1500000L;
         boolean interrupted = false;

         for (long remaining = deadline - now; remaining > 0L; remaining = deadline - now) {
            if (remaining > 2000000L) {
               this.clock.park(remaining - 2000000L);
               interrupted |= Thread.interrupted();
            } else {
               this.clock.spin();
            }

            now = this.clock.nanoTime();
         }

         if (interrupted) {
            Thread.currentThread().interrupt();
         }
      }

      this.previousFrameStart = now;
      this.frameStarted = true;
   }

   interface PaceClock {
      long nanoTime();

      void park(long var1);

      void spin();
   }

   private record PendingTouch(int type, int x, int y) {
   }
}
