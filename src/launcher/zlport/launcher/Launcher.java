package zlport.launcher;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Looper;
import android.os.MessageQueue;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import zlport.android.AppContext;
import zlport.android.media.Audio;
import zlport.android.media.AudioOutput;
import zlport.android.media.RawResources;
import zlport.android.ui.Dialogs;
import zlport.audio.OpenAlOutput;
import zlport.j2me.Platform;
import zlport.j2me.WheelZoom;
import zlport.stubs.NotPorted;

public final class Launcher {
   static final double IDLE_WAIT_SECONDS = 0.05;
   static final Duration SHUTDOWN_TIMEOUT = Duration.ofSeconds(10L);
   static final Duration LOGIC_PARK_TIMEOUT = Duration.ofSeconds(1L);
   static final int MIN_WINDOW_WIDTH = 640;
   static final int MIN_WINDOW_HEIGHT = 360;
   private static final Logger LOG = System.getLogger("zlport.launcher");
   private final Config config;
   private final Launcher.FrameListener frameListener;
   private final Presentation.Size fixedLogical;
   private final AtomicBoolean stopRequested = new AtomicBoolean();
   private final AtomicBoolean failed = new AtomicBoolean();
   private final Instrumentation instrumentation = new Instrumentation();
   private volatile GlfwSurface surface;
   private volatile InputQueue input;
   private volatile OpenAlOutput audio;
   private WindowsDialogs dialogs;
   private boolean audioPaused;
   private boolean unfocusedFrozen;
   private boolean rawRegistered;
   private int framebufferWidth;
   private int framebufferHeight;
   private int windowWidth;
   private int windowHeight;
   private volatile long windowHandle;
   private Presentation.Size logical;
   private boolean resizeLogged;
   private int normalX;
   private int normalY;
   private int normalWidth;
   private int normalHeight;
   private boolean maximized;
   private boolean fullscreenWasMaximized;
   private Launcher.WindowState finalWindow;

   public Launcher(Config config) {
      this(config, (frame, width, height) -> {});
   }

   public Launcher(Config config, Launcher.FrameListener frameListener) {
      this(config, frameListener, null);
   }

   Launcher(Config config, Launcher.FrameListener frameListener, Presentation.Size fixedLogical) {
      this.config = config;
      this.frameListener = frameListener;
      this.fixedLogical = fixedLogical;
   }

   public void requestStop() {
      this.stopRequested.set(true);
      GlfwSurface s = this.surface;
      if (s != null) {
         s.midletDestroyed();
      }
   }

   public Launcher.Result run() {
      WheelZoom.setNotchFactor(this.config.preferences().wheelStep());
      GLFWErrorCallback errors = GLFWErrorCallback.create(
         (code, description) -> LOG.log(Level.ERROR, "GLFW " + code + ": " + GLFWErrorCallback.getDescription(description))
      );
      GLFW.glfwSetErrorCallback(errors);
      if (!GLFW.glfwInit()) {
         errors.free();
         throw new IllegalStateException("GLFW no se inicializó (¿hay una tarjeta gráfica con OpenGL?)");
      }

      MessageQueue queue = mainQueue();
      long window = 0L;
      GlThread glThread = null;
      UncaughtExceptionHandler previousHandler = Thread.getDefaultUncaughtExceptionHandler();
      long start = System.nanoTime();
      boolean ownDialogs = false;

      try {
         window = this.createWindow();
         this.windowHandle = window;
         this.logical = this.fixedLogical == null ? Presentation.logicalSize(this.framebufferWidth, this.framebufferHeight) : this.fixedLogical;
         LOG.log(
            Level.INFO,
            "El juego dibuja en "
               + this.logical.width()
               + "x"
               + this.logical.height()
               + (this.logical.width() == this.framebufferWidth && this.logical.height() == this.framebufferHeight ? "" : ", reducido a la ventana")
         );
         this.surface = new GlfwSurface(this.logical.width());
         GlfwSurface s = this.surface;
         this.updateRefreshRate(window);
         GLFW.glfwSetWindowIconifyCallback(window, (w, iconified) -> {
            s.setIconified(iconified);
            this.updateAudioPause();
         });
         this.input = new InputQueue(
            this.logical.width(), this.logical.height(), this.framebufferWidth, this.framebufferHeight, this.windowWidth, this.windowHeight
         );
         this.installInputCallbacks(window, this.input);
         if (this.config.visible() && Dialogs.presenter() == null) {
            this.dialogs = new WindowsDialogs(window);
            Dialogs.install(this.dialogs);
            ownDialogs = true;
         }

         if (this.config.visible() && this.config.audio() && !Audio.installed()) {
            this.audio = openAudio();
         }

         Platform.install(this.surface);
         queue.setWakeUp(this.surface::wakeMain);
         Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {
            LOG.log(Level.ERROR, "Excepción no controlada en el hilo '" + thread.getName() + "'; cerrando", e);
            this.failed.set(true);
            this.requestStop();
         });
         if (this.stopRequested.get()) {
            this.surface.midletDestroyed();
         }

         glThread = new GlThread(
            window, this.surface, this.logical, new Presentation.Size(this.framebufferWidth, this.framebufferHeight), this.config.glDebug(), this.frameListener
         );
         glThread.start();
         if (!glThread.awaitReady()) {
            throw new IllegalStateException("El hilo GL no preparó el contexto");
         }

         GlThread presenter = glThread;
         GLFW.glfwSetFramebufferSizeCallback(window, (w, fw, fh) -> this.resized(w, presenter));
         GLFW.glfwSetWindowSizeCallback(window, (w, ww, wh) -> this.resized(w, presenter));
         if (this.config.visible()) {
            GLFW.glfwShowWindow(window);
         }

         Activity midlet = this.startMidlet();
         if (midlet != null) {
            this.uiLoop(window, queue);
            this.shutdown(midlet, queue, glThread);
         }
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         LOG.log(Level.ERROR, "Lanzador interrumpido", e);
         this.failed.set(true);
      } catch (RuntimeException | Error e) {
         LOG.log(Level.ERROR, "Fallo en el lanzador", e);
         this.failed.set(true);
      } finally {
         if (window != 0L) {
            this.finalWindow = this.windowState(window);
         }

         boolean glStopped = this.stopGlThread(glThread);
         Thread.setDefaultUncaughtExceptionHandler(previousHandler);
         queue.setWakeUp(null);
         if (this.surface != null) {
            this.surface.close();
         }

         Platform.uninstall();
         if (ownDialogs) {
            Dialogs.uninstall();
         }

         OpenAlOutput a = this.audio;
         if (a != null) {
            Audio.uninstall();
            a.close();
            this.audio = null;
         }

         if (this.rawRegistered) {
            RawResources.clear();
            this.rawRegistered = false;
         }

         if (glStopped) {
            if (window != 0L) {
               Callbacks.glfwFreeCallbacks(window);
               GLFW.glfwDestroyWindow(window);
            }

            GLFW.glfwTerminate();
         }

         GLFW.glfwSetErrorCallback(null);
         errors.free();
      }

      double seconds = (System.nanoTime() - start) / 1.0E9;
      long frames = glThread == null ? 0L : glThread.frames();
      Set<String> notPorted = NotPorted.reported();
      LOG.log(
         Level.INFO,
         String.format("%d fotogramas en %.1f s (%.1f/s)%s", frames, seconds, frames / seconds, notPorted.isEmpty() ? "" : "; no portados usados: " + notPorted)
      );
      return new Launcher.Result(
         this.failed.get() ? 1 : 0,
         frames,
         seconds,
         glThread == null ? 0 : glThread.glErrors(),
         glThread == null ? Map.of() : glThread.offThreadGl(),
         notPorted,
         this.finalWindow
      );
   }

   private static MessageQueue mainQueue() {
      synchronized (Looper.class) {
         if (Looper.getMainLooper() == null) {
            Looper.prepareMainLooper();
         }
      }

      Looper main = Looper.getMainLooper();
      if (!main.isCurrentThread()) {
         throw new IllegalStateException(
            "La cola principal pertenece al hilo '"
               + main.getThread().getName()
               + "'; el lanzador debe ejecutarse en él, no en '"
               + Thread.currentThread().getName()
               + "'"
         );
      } else {
         return main.getQueue();
      }
   }

   private long createWindow() {
      GLFW.glfwDefaultWindowHints();
      GLFW.glfwWindowHint(131076, 0);
      GLFW.glfwWindowHint(131075, 1);
      GLFW.glfwWindowHint(139266, 2);
      GLFW.glfwWindowHint(139267, 1);
      long window = GLFW.glfwCreateWindow(this.config.width(), this.config.height(), "Zombie Lane", 0L, 0L);
      if (window == 0L) {
         throw new IllegalStateException("No se pudo crear la ventana con OpenGL 2.1");
      }

      GLFW.glfwSetWindowSizeLimits(window, 640, 360, -1, -1);
      Config.Placement placement = this.config.placement();
      int[] l = new int[1];
      int[] t = new int[1];
      int[] r = new int[1];
      int[] b = new int[1];
      GLFW.glfwGetWindowFrameSize(window, l, t, r, b);
      int[] w = new int[1];
      int[] h = new int[1];
      GLFW.glfwGetWindowSize(window, w, h);
      List<WindowGeometry.Area> work = workAreas();
      WindowGeometry.Area primary = work.isEmpty() ? new WindowGeometry.Area(0, 0, w[0], h[0]) : work.get(0);
      WindowGeometry.Point p = WindowGeometry.position(
         placement.x(), placement.y(), w[0], h[0], new WindowGeometry.Frame(l[0], t[0], r[0], b[0]), work, primary
      );
      GLFW.glfwSetWindowPos(window, p.x(), p.y());
      this.normalX = p.x();
      this.normalY = p.y();
      this.normalWidth = w[0];
      this.normalHeight = h[0];
      GLFW.glfwSetWindowPosCallback(window, (win, x, y) -> {
         this.trackNormal(win);
         this.updateRefreshRate(win);
      });
      GLFW.glfwSetWindowMaximizeCallback(window, (win, value) -> this.maximized = value);
      if (placement.fullscreen() && this.config.visible()) {
         this.enterFullscreen(window);
         this.fullscreenWasMaximized = placement.maximized();
      } else if (placement.maximized()) {
         GLFW.glfwMaximizeWindow(window);
         this.maximized = true;
      }

      GLFW.glfwGetFramebufferSize(window, w, h);
      this.framebufferWidth = w[0];
      this.framebufferHeight = h[0];
      GLFW.glfwGetWindowSize(window, w, h);
      this.windowWidth = w[0];
      this.windowHeight = h[0];
      LOG.log(
         Level.INFO,
         "Ventana "
            + this.windowWidth
            + "x"
            + this.windowHeight
            + (GLFW.glfwGetWindowMonitor(window) != 0L ? " en pantalla completa" : (this.maximized ? " maximizada" : " en " + p.x() + "," + p.y()))
            + ", búfer de imagen "
            + this.framebufferWidth
            + "x"
            + this.framebufferHeight
      );
      return window;
   }

   private void trackNormal(long window) {
      if (GLFW.glfwGetWindowMonitor(window) == 0L && GLFW.glfwGetWindowAttrib(window, 131080) != 1 && GLFW.glfwGetWindowAttrib(window, 131074) != 1) {
         int[] a = new int[1];
         int[] b = new int[1];
         GLFW.glfwGetWindowSize(window, a, b);
         if (a[0] > 0 && b[0] > 0) {
            this.normalWidth = a[0];
            this.normalHeight = b[0];
            GLFW.glfwGetWindowPos(window, a, b);
            this.normalX = a[0];
            this.normalY = b[0];
         }
      }
   }

   private void toggleFullscreen(long window) {
      if (GLFW.glfwGetWindowMonitor(window) == 0L) {
         this.enterFullscreen(window);
      } else {
         GLFW.glfwSetWindowMonitor(window, 0L, this.normalX, this.normalY, this.normalWidth, this.normalHeight, -1);
         if (this.fullscreenWasMaximized) {
            GLFW.glfwMaximizeWindow(window);
         }

         LOG.log(Level.INFO, "Pantalla completa: desactivada");
      }
   }

   private void enterFullscreen(long window) {
      this.fullscreenWasMaximized = this.maximized;
      if (this.maximized) {
         GLFW.glfwRestoreWindow(window);
      }

      long monitor = monitorOf(window);
      GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);
      if (mode == null) {
         LOG.log(Level.WARNING, "Pantalla completa: el monitor no informó el modo de vídeo");
      } else {
         GLFW.glfwSetWindowMonitor(window, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate());
         LOG.log(Level.INFO, "Pantalla completa: " + mode.width() + "x" + mode.height());
      }
   }

   private void updateRefreshRate(long window) {
      GlfwSurface s = this.surface;
      if (s != null) {
         long monitor = GLFW.glfwGetWindowMonitor(window);
         if (monitor == 0L) {
            monitor = monitorOf(window);
         }

         GLFWVidMode mode = monitor == 0L ? null : GLFW.glfwGetVideoMode(monitor);
         s.setRefreshRate(mode == null ? 0 : mode.refreshRate());
      }
   }

   private static long monitorOf(long window) {
      PointerBuffer monitors = GLFW.glfwGetMonitors();
      if (monitors != null && monitors.limit() != 0) {
         List<WindowGeometry.Area> areas = new ArrayList<>();
         int[] x = new int[1];
         int[] y = new int[1];

         for (int i = 0; i < monitors.limit(); i++) {
            long m = monitors.get(i);
            GLFW.glfwGetMonitorPos(m, x, y);
            GLFWVidMode mode = GLFW.glfwGetVideoMode(m);
            areas.add(new WindowGeometry.Area(x[0], y[0], mode == null ? 0 : mode.width(), mode == null ? 0 : mode.height()));
         }

         int[] w = new int[1];
         int[] h = new int[1];
         GLFW.glfwGetWindowPos(window, x, y);
         GLFW.glfwGetWindowSize(window, w, h);
         return monitors.get(WindowGeometry.monitorOf(new WindowGeometry.Area(x[0], y[0], w[0], h[0]), areas));
      } else {
         return GLFW.glfwGetPrimaryMonitor();
      }
   }

   private static List<WindowGeometry.Area> workAreas() {
      List<WindowGeometry.Area> areas = new ArrayList<>();
      PointerBuffer monitors = GLFW.glfwGetMonitors();
      if (monitors == null) {
         return areas;
      }

      int[] x = new int[1];
      int[] y = new int[1];
      int[] w = new int[1];
      int[] h = new int[1];

      for (int i = 0; i < monitors.limit(); i++) {
         GLFW.glfwGetMonitorWorkarea(monitors.get(i), x, y, w, h);
         areas.add(new WindowGeometry.Area(x[0], y[0], w[0], h[0]));
      }

      return areas;
   }

   private Launcher.WindowState windowState(long window) {
      boolean fullscreen = GLFW.glfwGetWindowMonitor(window) != 0L;
      if (!fullscreen) {
         this.trackNormal(window);
      }

      return new Launcher.WindowState(
         this.normalX, this.normalY, this.normalWidth, this.normalHeight, fullscreen ? this.fullscreenWasMaximized : this.maximized, fullscreen
      );
   }

   private void resized(long window, GlThread glThread) {
      int[] w = new int[1];
      int[] h = new int[1];
      GLFW.glfwGetFramebufferSize(window, w, h);
      int fw = w[0];
      int fh = h[0];
      GLFW.glfwGetWindowSize(window, w, h);
      if (fw > 0 && fh > 0 && w[0] > 0 && h[0] > 0) {
         this.trackNormal(window);
         glThread.setFramebufferSize(fw, fh);
         this.input.resize(fw, fh, w[0], h[0]);
         if (!this.resizeLogged && (fw != this.framebufferWidth || fh != this.framebufferHeight)) {
            this.resizeLogged = true;
            LOG.log(
               Level.INFO,
               "La ventana cambió a "
                  + fw
                  + "x"
                  + fh
                  + "; el juego sigue en "
                  + this.logical.width()
                  + "x"
                  + this.logical.height()
                  + ", escalado (el diseño del juego se calcula una vez), y se abrirá con el nuevo tamaño la próxima vez"
            );
         }
      }
   }

   long window() {
      return this.windowHandle;
   }

   private Activity startMidlet() {
      try {
         Class<? extends Activity> type = Class.forName(this.config.midlet()).asSubclass(Activity.class);
         this.rawRegistered = registerRawResources(type);
         Activity midlet = this.instrumentation.newActivity(type, new AppContext(this.config.dataDir()));
         LOG.log(Level.INFO, "MIDlet " + type.getName() + ", dados em " + this.config.dataDir().toAbsolutePath());
         this.instrumentation.callActivityOnCreate(midlet, null);
         this.instrumentation.callActivityOnResume(midlet);
         return midlet;
      } catch (InvocationTargetException e) {
         LOG.log(Level.ERROR, "Falló el constructor del MIDlet", e.getCause());
      } catch (ReflectiveOperationException e) {
         LOG.log(Level.ERROR, "MIDlet no válido: " + this.config.midlet(), e);
      } catch (RuntimeException | ExceptionInInitializerError e) {
         LOG.log(Level.ERROR, "El MIDlet falló al iniciar", e);
      }

      this.failed.set(true);
      return null;
   }

   private static OpenAlOutput openAudio() {
      try {
         Optional<OpenAlOutput> out = OpenAlOutput.open();
         if (out.isEmpty()) {
            LOG.log(Level.WARNING, "Sin salida de sonido: el juego sigue en silencio");
            return null;
         } else {
            Audio.install((AudioOutput)out.get());
            return out.get();
         }
      } catch (RuntimeException | LinkageError e) {
         LOG.log(Level.WARNING, "No se pudo abrir el sonido: el juego sigue en silencio", e);
         return null;
      }
   }

   private static boolean registerRawResources(Class<?> midlet) {
      try {
         RawResources.register(Class.forName(midlet.getPackageName() + ".R$raw", true, midlet.getClassLoader()));
         return true;
      } catch (ClassNotFoundException e) {
         return false;
      }
   }

   private void installInputCallbacks(long window, InputQueue in) {
      GLFW.glfwSetCursorPosCallback(window, (w, x, y) -> in.cursor(x, y));
      GLFW.glfwSetCursorEnterCallback(window, (w, entered) -> in.cursorEnter(entered));
      GLFW.glfwSetMouseButtonCallback(window, (w, button, action, mods) -> in.mouseButton(button, action));
      GLFW.glfwSetScrollCallback(window, (w, dx, dy) -> in.scroll(dy));
      GLFW.glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
         boolean altEnter = (key == 257 || key == 335) && (mods & 4) != 0;
         if (action != 1 || !this.config.visible() || key != 300 && !altEnter) {
            in.key(key, action);
         } else {
            this.toggleFullscreen(w);
         }
      });
      GLFW.glfwSetWindowFocusCallback(window, (w, focused) -> {
         in.focus(focused);
         this.focusChanged(focused);
      });
   }

   private void focusChanged(boolean focused) {
      if (this.config.visible() && this.config.preferences().pauseOnFocusLoss()) {
         WindowsDialogs d = this.dialogs;
         boolean freeze = !focused && (d == null || !d.anyOpen());
         if (freeze != this.unfocusedFrozen) {
            this.unfocusedFrozen = freeze;
            this.surface.setUnfocused(freeze);
            this.updateAudioPause();
            LOG.log(Level.INFO, freeze ? "Sin foco: juego congelado y sonido en pausa" : "Foco recuperado: el juego continúa");
         }
      }
   }

   private void updateAudioPause() {
      OpenAlOutput a = this.audio;
      boolean pause = this.surface.frozen();
      if (a != null && pause != this.audioPaused) {
         this.audioPaused = pause;
         if (pause) {
            a.pause();
         } else {
            a.resume();
         }
      }
   }

   private void uiLoop(long window, MessageQueue queue) {
      this.dispatch(queue);

      while (!this.surface.destroyed() && !this.stopRequested.get() && !GLFW.glfwWindowShouldClose(window)) {
         GLFW.glfwWaitEventsTimeout(0.05);
         this.dispatch(queue);
         this.deliverInput();
      }
   }

   private void deliverInput() {
      try {
         this.input.deliver(InputQueue.GAME);
      } catch (RuntimeException | Error e) {
         LOG.log(Level.ERROR, "Excepción no controlada en la entrada (hilo UI); cerrando", e);
         this.failed.set(true);
         this.requestStop();
      }
   }

   InputQueue input() {
      return this.input;
   }

   private void dispatch(MessageQueue queue) {
      try {
         queue.dispatchPending();
      } catch (RuntimeException | Error e) {
         LOG.log(Level.ERROR, "Excepción no controlada en el hilo UI; cerrando", e);
         this.failed.set(true);
         this.requestStop();
      }
   }

   private void shutdown(Activity midlet, MessageQueue queue, GlThread glThread) {
      LOG.log(Level.INFO, "encerrando");
      glThread.stopDrawing();
      this.awaitLogicParked();
      CountDownLatch done = new CountDownLatch(1);
      Thread ui = Thread.currentThread();
      Thread watchdog = new Thread(() -> {
         try {
            if (!done.await(SHUTDOWN_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)) {
               LOG.log(Level.ERROR, "El cierre superó " + SHUTDOWN_TIMEOUT.toSeconds() + " s; hilos:" + threadDump());
               this.failed.set(true);
               ui.interrupt();
            }
         } catch (InterruptedException var4x) {
         }
      }, "zl-vigia-encerramento");
      watchdog.setDaemon(true);
      watchdog.start();

      try {
         try {
            this.instrumentation.callActivityOnPause(midlet);
         } catch (RuntimeException e) {
            LOG.log(Level.ERROR, "onPause falhou", e);
            this.failed.set(true);
         }

         this.dispatch(queue);

         try {
            this.instrumentation.callActivityOnDestroy(midlet);
         } catch (RuntimeException e) {
            LOG.log(Level.ERROR, "onDestroy falhou", e);
            this.failed.set(true);
         }

         this.dispatch(queue);
      } finally {
         done.countDown();
         watchdog.interrupt();
         Thread.interrupted();
      }
   }

   private void awaitLogicParked() {
      long start = System.nanoTime();

      boolean parked;
      try {
         parked = this.surface.awaitPendingRender(LOGIC_PARK_TIMEOUT.toNanos());
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         parked = false;
      }

      long micros = (System.nanoTime() - start) / 1000L;
      if (parked) {
         LOG.log(Level.INFO, "Lógica estacionada en " + micros + " µs");
      } else {
         LOG.log(Level.WARNING, "La lógica no se detuvo en " + LOGIC_PARK_TIMEOUT.toMillis() + " ms (terminó o carga texturas); se continúa con onPause");
      }
   }

   private boolean stopGlThread(GlThread glThread) {
      if (glThread == null) {
         return true;
      }

      glThread.finish();

      try {
         glThread.join(SHUTDOWN_TIMEOUT.toMillis());
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }

      if (glThread.isAlive()) {
         LOG.log(Level.ERROR, "El hilo GL no terminó en " + SHUTDOWN_TIMEOUT.toSeconds() + " s; hilos:" + threadDump());
         this.failed.set(true);
         return false;
      } else {
         return true;
      }
   }

   private static String threadDump() {
      StringBuilder dump = new StringBuilder();
      Thread.getAllStackTraces().forEach((t, stack) -> {
         dump.append(System.lineSeparator()).append("  hilo '").append(t.getName()).append("' ").append(t.getState());

         for (StackTraceElement e : stack) {
            dump.append(System.lineSeparator()).append("      at ").append(e);
         }
      });
      return dump.toString();
   }

   @FunctionalInterface
   public interface FrameListener {
      void frameDrawn(long var1, int var3, int var4);

      default void framePresented(long frame, int width, int height) {
      }
   }

   public record Result(
      int exitCode, long frames, double seconds, int glErrors, Map<String, Integer> offThreadGl, Set<String> notPorted, Launcher.WindowState window
   ) {
   }

   public record WindowState(Integer x, Integer y, int width, int height, boolean maximized, boolean fullscreen) {
   }
}
