package zlport.launcher;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.opengles.GL11;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import zlport.gles.CheckingGL;
import zlport.gles.DesktopGL;
import zlport.j2me.Renderer;

final class GlThread extends Thread {
   private static final Logger LOG = System.getLogger("zlport.launcher");
   private final long window;
   private final GlfwSurface surface;
   private final Presentation.Size logical;
   private volatile Presentation.Size framebuffer;
   private final boolean glDebug;
   private final Launcher.FrameListener frameListener;
   private final CountDownLatch ready = new CountDownLatch(1);
   private final float[] clearColor = new float[4];
   private volatile boolean finishing;
   private volatile boolean contextReady;
   private volatile long frames;
   private volatile CheckingGL checking;
   private GL11 gl;
   private Renderer current;
   private boolean surfaceCreated;
   private int fbo;
   private int colorBuffer;
   private int depthStencilBuffer;

   GlThread(long window, GlfwSurface surface, Presentation.Size logical, Presentation.Size framebuffer, boolean glDebug, Launcher.FrameListener frameListener) {
      super("GLThread");
      this.window = window;
      this.surface = surface;
      this.logical = logical;
      this.framebuffer = framebuffer;
      this.glDebug = glDebug;
      this.frameListener = frameListener;
   }

   @Override
   public void run() {
      try {
         GLFW.glfwMakeContextCurrent(this.window);
         GLCapabilities caps = GL.createCapabilities();
         LOG.log(Level.INFO, org.lwjgl.opengl.GL11.glGetString(7938) + " (" + org.lwjgl.opengl.GL11.glGetString(7937) + ")");
         if (!caps.OpenGL30 && !caps.GL_ARB_framebuffer_object) {
            throw new IllegalStateException("La tarjeta gráfica no tiene framebuffer object (OpenGL 3.0), necesario para la presentación escalada (FASE-06 §4.1)");
         }

         GLFW.glfwSwapInterval(1);
         this.createFramebuffer();
         DesktopGL desktop = DesktopGL.forCurrentContext();
         if (this.glDebug) {
            this.checking = CheckingGL.wrap(desktop, this);
            this.gl = this.checking.gl();
            LOG.log(Level.INFO, "GL en modo de depuración: glGetError después de cada llamada");
         } else {
            this.gl = desktop;
         }

         this.contextReady = true;
         this.ready.countDown();

         while (!this.finishing) {
            this.serve();
         }
      } finally {
         this.ready.countDown();
         if (this.fbo != 0) {
            this.deleteFramebuffer();
         }

         GL.setCapabilities(null);
         GLFW.glfwMakeContextCurrent(0L);
      }
   }

   private void serve() {
      Renderer r = this.surface.renderer();
      if (r != this.current) {
         this.current = r;
         this.surfaceCreated = false;
      }

      if (this.current != null && !this.surfaceCreated) {
         this.current.onSurfaceCreated(this.gl);
         this.current.onSurfaceChanged(this.gl, this.logical.width(), this.logical.height());
         this.surfaceCreated = true;
         this.surface.requestFirstFrame();
      }

      if (this.current != null && !this.surface.frozen() && this.surface.takeRenderRequest()) {
         this.current.onDrawFrame(this.gl);
         this.frames++;
         this.frameListener.frameDrawn(this.frames, this.logical.width(), this.logical.height());
         this.present();
         GLFW.glfwSwapBuffers(this.window);
      } else {
         if (this.current != null && this.surfaceCreated) {
            this.current.pumpTextures(this.gl);
         }

         this.surface.awaitGlWork(0.05);
      }
   }

   boolean awaitReady() throws InterruptedException {
      this.ready.await();
      return this.contextReady;
   }

   void stopDrawing() {
      this.surface.stopDrawing();
      this.surface.wakeGl();
   }

   void finish() {
      this.finishing = true;
      this.surface.wakeGl();
   }

   long frames() {
      return this.frames;
   }

   int glErrors() {
      CheckingGL c = this.checking;
      return c == null ? 0 : c.errorCount();
   }

   Map<String, Integer> offThreadGl() {
      CheckingGL c = this.checking;
      return c == null ? Map.of() : c.offThreadCalls();
   }

   private void createFramebuffer() {
      int w = this.logical.width();
      int h = this.logical.height();
      this.fbo = GL30.glGenFramebuffers();
      this.colorBuffer = GL30.glGenRenderbuffers();
      this.depthStencilBuffer = GL30.glGenRenderbuffers();
      GL30.glBindRenderbuffer(36161, this.colorBuffer);
      GL30.glRenderbufferStorage(36161, 32856, w, h);
      GL30.glBindRenderbuffer(36161, this.depthStencilBuffer);
      GL30.glRenderbufferStorage(36161, 35056, w, h);
      GL30.glBindRenderbuffer(36161, 0);
      GL30.glBindFramebuffer(36160, this.fbo);
      GL30.glFramebufferRenderbuffer(36160, 36064, 36161, this.colorBuffer);
      GL30.glFramebufferRenderbuffer(36160, 33306, 36161, this.depthStencilBuffer);
      int status = GL30.glCheckFramebufferStatus(36160);
      if (status != 36053) {
         throw new IllegalStateException("O FBO de " + w + "x" + h + " ficou incompleto: 0x" + Integer.toHexString(status));
      }
   }

   private void deleteFramebuffer() {
      GL30.glBindFramebuffer(36160, 0);
      GL30.glDeleteFramebuffers(this.fbo);
      GL30.glDeleteRenderbuffers(this.colorBuffer);
      GL30.glDeleteRenderbuffers(this.depthStencilBuffer);
      this.fbo = 0;
   }

   private void present() {
      Presentation.Size target = this.framebuffer;
      int lw = this.logical.width();
      int lh = this.logical.height();
      Presentation.Rect r = Presentation.fit(lw, lh, target.width(), target.height());
      boolean scissor = org.lwjgl.opengl.GL11.glIsEnabled(3089);
      org.lwjgl.opengl.GL11.glGetFloatv(3106, this.clearColor);
      if (scissor) {
         org.lwjgl.opengl.GL11.glDisable(3089);
      }

      GL30.glBindFramebuffer(36008, this.fbo);
      GL30.glBindFramebuffer(36009, 0);
      org.lwjgl.opengl.GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
      org.lwjgl.opengl.GL11.glClear(16384);
      int y0 = target.height() - r.y() - r.height();
      boolean exact = r.width() == lw && r.height() == lh;
      GL30.glBlitFramebuffer(0, 0, lw, lh, r.x(), y0, r.x() + r.width(), y0 + r.height(), 16384, exact ? 9728 : 9729);
      GL30.glBindFramebuffer(36008, 0);
      this.frameListener.framePresented(this.frames, target.width(), target.height());
      GL30.glBindFramebuffer(36160, this.fbo);
      org.lwjgl.opengl.GL11.glClearColor(this.clearColor[0], this.clearColor[1], this.clearColor[2], this.clearColor[3]);
      if (scissor) {
         org.lwjgl.opengl.GL11.glEnable(3089);
      }
   }

   void setFramebufferSize(int width, int height) {
      if (width > 0 && height > 0) {
         this.framebuffer = new Presentation.Size(width, height);
      }
   }
}
