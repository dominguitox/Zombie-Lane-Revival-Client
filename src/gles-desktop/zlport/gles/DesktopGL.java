package zlport.gles;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

public final class DesktopGL implements GL11, GL11Ext {
   public static final String EXTENSIONS = "";
   static final int GL_EXTENSIONS = 7939;
   static final int GL_TEXTURE_ENV_COLOR = 8705;
   static final String DIRECT_BUFFER_REQUIRED = "Must use a native order direct Buffer";
   private static final Logger LOG = System.getLogger("zlport.gles");
   private final Thread owner;
   private final ThreadLocal<Boolean> offThreadReported = ThreadLocal.withInitial(() -> false);
   private final ClientArrays arrays = new ClientArrays();

   DesktopGL(Thread owner) {
      this.owner = owner;
   }

   public static DesktopGL forCurrentContext() {
      GLCapabilities caps = GL.getCapabilities();
      if (!caps.OpenGL15) {
         throw new IllegalStateException("O port exige OpenGL 1.5 ou mais (perfil de compatibilidade).");
      } else {
         return new DesktopGL(Thread.currentThread());
      }
   }

   public Thread owner() {
      return this.owner;
   }

   private boolean current(String call) {
      if (Thread.currentThread() == this.owner) {
         return true;
      }

      if (!this.offThreadReported.get()) {
         this.offThreadReported.set(true);
         LOG.log(
            Level.WARNING,
            () -> call + " na thread '" + Thread.currentThread().getName() + "', sem contexto GL: ignorada, como no Android (registrado uma vez por thread)"
         );
      }

      return false;
   }

   private static void requireDirect(Buffer pointer) {
      if (!pointer.isDirect()) {
         throw new IllegalArgumentException("Must use a native order direct Buffer");
      }
   }

   private static void checkArray(int[] array, String name, int offset, int n) {
      if (array == null) {
         throw new IllegalArgumentException(name + " == null");
      }

      if (offset < 0) {
         throw new IllegalArgumentException("offset < 0");
      }

      if (array.length - offset < n) {
         throw new IllegalArgumentException("length - offset < n < needed");
      }
   }

   private static void checkBuffer(Buffer buffer, String name, int n) {
      if (buffer == null) {
         throw new IllegalArgumentException(name + " == null");
      }

      if (buffer.remaining() < n) {
         throw new IllegalArgumentException("remaining() < n < needed");
      }
   }

   private static ByteBuffer direct(Buffer buffer) {
      return buffer.isDirect() ? null : Buffers.directCopy(buffer);
   }

   private static long addressOf(Buffer buffer, ByteBuffer copy) {
      return copy != null ? MemoryUtil.memAddress(copy) : Buffers.address(buffer);
   }

   public void glActiveTexture(int texture) {
      if (this.current("glActiveTexture")) {
         this.arrays.activeTexture(texture);
         GL13.glActiveTexture(texture);
      }
   }

   public void glBindTexture(int target, int texture) {
      if (this.current("glBindTexture")) {
         org.lwjgl.opengl.GL11.glBindTexture(target, texture);
      }
   }

   public void glBlendFunc(int sfactor, int dfactor) {
      if (this.current("glBlendFunc")) {
         org.lwjgl.opengl.GL11.glBlendFunc(sfactor, dfactor);
      }
   }

   public void glClear(int mask) {
      if (this.current("glClear")) {
         org.lwjgl.opengl.GL11.glClear(mask);
      }
   }

   public void glClearColor(float red, float green, float blue, float alpha) {
      if (this.current("glClearColor")) {
         org.lwjgl.opengl.GL11.glClearColor(red, green, blue, alpha);
      }
   }

   public void glColor4f(float red, float green, float blue, float alpha) {
      if (this.current("glColor4f")) {
         org.lwjgl.opengl.GL11.glColor4f(red, green, blue, alpha);
      }
   }

   public void glDisable(int cap) {
      if (this.current("glDisable")) {
         this.arrays.enable(cap, false);
         org.lwjgl.opengl.GL11.glDisable(cap);
      }
   }

   public void glDisableClientState(int array) {
      if (this.current("glDisableClientState")) {
         this.arrays.enableClientState(array, false);
         org.lwjgl.opengl.GL11.glDisableClientState(array);
      }
   }

   public void glEnable(int cap) {
      if (this.current("glEnable")) {
         this.arrays.enable(cap, true);
         org.lwjgl.opengl.GL11.glEnable(cap);
      }
   }

   public void glEnableClientState(int array) {
      if (this.current("glEnableClientState")) {
         this.arrays.enableClientState(array, true);
         org.lwjgl.opengl.GL11.glEnableClientState(array);
      }
   }

   public int glGetError() {
      return this.current("glGetError") ? org.lwjgl.opengl.GL11.glGetError() : 0;
   }

   public String glGetString(int name) {
      if (!this.current("glGetString")) {
         return null;
      } else {
         return name == 7939 ? "" : org.lwjgl.opengl.GL11.glGetString(name);
      }
   }

   public void glHint(int target, int mode) {
      if (this.current("glHint")) {
         org.lwjgl.opengl.GL11.glHint(target, mode);
      }
   }

   public void glLineWidth(float width) {
      if (this.current("glLineWidth")) {
         org.lwjgl.opengl.GL11.glLineWidth(width);
      }
   }

   public void glScissor(int x, int y, int width, int height) {
      if (this.current("glScissor")) {
         org.lwjgl.opengl.GL11.glScissor(x, y, width, height);
      }
   }

   public void glViewport(int x, int y, int width, int height) {
      if (this.current("glViewport")) {
         org.lwjgl.opengl.GL11.glViewport(x, y, width, height);
      }
   }

   public void glLoadIdentity() {
      if (this.current("glLoadIdentity")) {
         org.lwjgl.opengl.GL11.glLoadIdentity();
      }
   }

   public void glMatrixMode(int mode) {
      if (this.current("glMatrixMode")) {
         org.lwjgl.opengl.GL11.glMatrixMode(mode);
      }
   }

   public void glOrthof(float left, float right, float bottom, float top, float zNear, float zFar) {
      if (this.current("glOrthof")) {
         org.lwjgl.opengl.GL11.glOrtho(left, right, bottom, top, zNear, zFar);
      }
   }

   public void glPopMatrix() {
      if (this.current("glPopMatrix")) {
         org.lwjgl.opengl.GL11.glPopMatrix();
      }
   }

   public void glPushMatrix() {
      if (this.current("glPushMatrix")) {
         org.lwjgl.opengl.GL11.glPushMatrix();
      }
   }

   public void glRotatef(float angle, float x, float y, float z) {
      if (this.current("glRotatef")) {
         org.lwjgl.opengl.GL11.glRotatef(angle, x, y, z);
      }
   }

   public void glScalef(float x, float y, float z) {
      if (this.current("glScalef")) {
         org.lwjgl.opengl.GL11.glScalef(x, y, z);
      }
   }

   public void glTranslatef(float x, float y, float z) {
      if (this.current("glTranslatef")) {
         org.lwjgl.opengl.GL11.glTranslatef(x, y, z);
      }
   }

   public void glColorPointer(int size, int type, int stride, Buffer pointer) {
      requireDirect(pointer);
      if (this.current("glColorPointer")) {
         this.arrays.clientPointer(32886, size, type, stride, pointer);
         org.lwjgl.opengl.GL11.nglColorPointer(size, type, stride, Buffers.address(pointer));
      }
   }

   public void glTexCoordPointer(int size, int type, int stride, Buffer pointer) {
      requireDirect(pointer);
      if (this.current("glTexCoordPointer")) {
         this.arrays.clientPointer(32888, size, type, stride, pointer);
         org.lwjgl.opengl.GL11.nglTexCoordPointer(size, type, stride, Buffers.address(pointer));
      }
   }

   public void glVertexPointer(int size, int type, int stride, Buffer pointer) {
      requireDirect(pointer);
      if (this.current("glVertexPointer")) {
         this.arrays.clientPointer(32884, size, type, stride, pointer);
         org.lwjgl.opengl.GL11.nglVertexPointer(size, type, stride, Buffers.address(pointer));
      }
   }

   public void glTexCoordPointer(int size, int type, int stride, int offset) {
      if (this.current("glTexCoordPointer")) {
         this.arrays.bufferObjectPointer(32888);
         org.lwjgl.opengl.GL11.glTexCoordPointer(size, type, stride, offset);
      }
   }

   public void glVertexPointer(int size, int type, int stride, int offset) {
      if (this.current("glVertexPointer")) {
         this.arrays.bufferObjectPointer(32884);
         org.lwjgl.opengl.GL11.glVertexPointer(size, type, stride, offset);
      }
   }

   public void glDrawArrays(int mode, int first, int count) {
      if (this.current("glDrawArrays")) {
         boolean skipTexCoords = this.arrays.prepareDraw(first, count);
         if (skipTexCoords) {
            org.lwjgl.opengl.GL11.glDisableClientState(32888);
         }

         org.lwjgl.opengl.GL11.glDrawArrays(mode, first, count);
         if (skipTexCoords) {
            org.lwjgl.opengl.GL11.glEnableClientState(32888);
         }
      }
   }

   public void glDrawTexfOES(float x, float y, float z, float width, float height) {
      if (this.current("glDrawTexfOES")) {
         int[] viewport = new int[4];
         org.lwjgl.opengl.GL11.glGetIntegerv(2978, viewport);
         int matrixMode = org.lwjgl.opengl.GL11.glGetInteger(2976);
         org.lwjgl.opengl.GL11.glMatrixMode(5889);
         org.lwjgl.opengl.GL11.glPushMatrix();
         org.lwjgl.opengl.GL11.glLoadIdentity();
         org.lwjgl.opengl.GL11.glOrtho(viewport[0], viewport[0] + viewport[2], viewport[1], viewport[1] + viewport[3], -1.0, 1.0);
         org.lwjgl.opengl.GL11.glMatrixMode(5888);
         org.lwjgl.opengl.GL11.glPushMatrix();
         org.lwjgl.opengl.GL11.glLoadIdentity();
         org.lwjgl.opengl.GL11.glBegin(7);
         org.lwjgl.opengl.GL11.glTexCoord2f(0.0F, 0.0F);
         org.lwjgl.opengl.GL11.glVertex2f(x, y);
         org.lwjgl.opengl.GL11.glVertex2f(x + width, y);
         org.lwjgl.opengl.GL11.glVertex2f(x + width, y + height);
         org.lwjgl.opengl.GL11.glVertex2f(x, y + height);
         org.lwjgl.opengl.GL11.glEnd();
         org.lwjgl.opengl.GL11.glPopMatrix();
         org.lwjgl.opengl.GL11.glMatrixMode(5889);
         org.lwjgl.opengl.GL11.glPopMatrix();
         org.lwjgl.opengl.GL11.glMatrixMode(matrixMode);
      }
   }

   public void glGenTextures(int n, int[] textures, int offset) {
      checkArray(textures, "textures", offset, n);
      if (this.current("glGenTextures")) {
         int[] names = new int[n];
         org.lwjgl.opengl.GL11.glGenTextures(names);
         System.arraycopy(names, 0, textures, offset, n);
      }
   }

   public void glGenTextures(int n, IntBuffer textures) {
      checkBuffer(textures, "textures", n);
      if (this.current("glGenTextures")) {
         int[] names = new int[n];
         org.lwjgl.opengl.GL11.glGenTextures(names);
         textures.duplicate().put(names);
      }
   }

   public void glDeleteTextures(int n, int[] textures, int offset) {
      checkArray(textures, "textures", offset, n);
      if (this.current("glDeleteTextures")) {
         org.lwjgl.opengl.GL11.glDeleteTextures(Arrays.copyOfRange(textures, offset, offset + n));
      }
   }

   public void glDeleteTextures(int n, IntBuffer textures) {
      checkBuffer(textures, "textures", n);
      if (this.current("glDeleteTextures")) {
         int[] names = new int[n];
         textures.duplicate().get(names);
         org.lwjgl.opengl.GL11.glDeleteTextures(names);
      }
   }

   public void glTexEnvfv(int target, int pname, FloatBuffer params) {
      if (params == null) {
         throw new IllegalArgumentException("params == null");
      }

      int needed = pname == 8705 ? 4 : 1;
      if (params.remaining() < needed) {
         throw new IllegalArgumentException("remaining() < needed");
      }

      if (this.current("glTexEnvfv")) {
         ByteBuffer copy = direct(params);
         org.lwjgl.opengl.GL11.nglTexEnvfv(target, pname, addressOf(params, copy));
      }
   }

   public void glTexEnvi(int target, int pname, int param) {
      if (this.current("glTexEnvi")) {
         org.lwjgl.opengl.GL11.glTexEnvi(target, pname, param);
      }
   }

   public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels) {
      if (pixels != null) {
         long needed = DesktopGL.TexelSizes.imageBytes(width, height, format, type);
         if (needed > 0L && Buffers.remainingBytes(pixels) < needed) {
            throw new IllegalArgumentException("remaining() < needed");
         }
      }

      if (this.current("glTexImage2D")) {
         ByteBuffer copy = pixels == null ? null : direct(pixels);
         long address = pixels == null ? 0L : addressOf(pixels, copy);
         org.lwjgl.opengl.GL11.nglTexImage2D(target, level, internalformat, width, height, border, format, type, address);
      }
   }

   public void glTexParameterx(int target, int pname, int param) {
      if (this.current("glTexParameterx")) {
         org.lwjgl.opengl.GL11.glTexParameteri(target, pname, param);
      }
   }

   public void glTexParameteri(int target, int pname, int param) {
      if (this.current("glTexParameteri")) {
         org.lwjgl.opengl.GL11.glTexParameteri(target, pname, param);
      }
   }

   public void glBindBuffer(int target, int buffer) {
      if (this.current("glBindBuffer")) {
         GL15.glBindBuffer(target, buffer);
      }
   }

   public void glBufferData(int target, int size, Buffer data, int usage) {
      if (data != null && Buffers.remainingBytes(data) < size) {
         throw new IllegalArgumentException("remaining() < size < needed");
      }

      if (this.current("glBufferData")) {
         ByteBuffer copy = data == null ? null : direct(data);
         long address = data == null ? 0L : addressOf(data, copy);
         GL15.nglBufferData(target, size, address, usage);
      }
   }

   public void glGenBuffers(int n, int[] buffers, int offset) {
      checkArray(buffers, "buffers", offset, n);
      if (this.current("glGenBuffers")) {
         int[] names = new int[n];
         GL15.glGenBuffers(names);
         System.arraycopy(names, 0, buffers, offset, n);
      }
   }

   static final class TexelSizes {
      private TexelSizes() {
      }

      static int bytesPerPixel(int format, int type) {
         return switch (type) {
            case 5121 -> {
               switch (format) {
                  case 6406:
                  case 6409:
                     yield 1;
                  case 6407:
                     yield 3;
                  case 6408:
                     yield 4;
                  case 6410:
                     yield 2;
                  default:
                     yield -1;
               }
            }
            case 32819, 32820, 33635 -> 2;
            default -> -1;
         };
      }

      static long imageBytes(int width, int height, int format, int type) {
         int bpp = bytesPerPixel(format, type);
         if (bpp >= 0 && width > 0 && height > 0) {
            long row = (long)width * bpp;
            long alignedRow = row + 3L & -4L;
            return alignedRow * (height - 1) + row;
         } else {
            return -1L;
         }
      }
   }
}
