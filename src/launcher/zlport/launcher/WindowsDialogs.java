package zlport.launcher;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.windows.User32;
import zlport.android.ui.DialogPresenter;
import zlport.android.ui.DialogPresenter.Box;

final class WindowsDialogs implements DialogPresenter {
   private static final Logger LOG = System.getLogger("zlport.launcher");
   static final int MB_OK = 0;
   static final int MB_YESNO = 4;
   static final int MB_ICONQUESTION = 32;
   static final int MB_ICONINFORMATION = 64;
   static final int MB_ICONERROR = 16;
   static final int MB_SETFOREGROUND = 65536;
   static final int IDOK = 1;
   static final int IDYES = 6;
   static final int IDNO = 7;
   private final long owner;
   private final long messageBoxW;
   private final AtomicInteger open = new AtomicInteger();

   WindowsDialogs(long glfwWindow) {
      this.owner = GLFWNativeWin32.glfwGetWin32Window(glfwWindow);
      this.messageBoxW = User32.getLibrary().getFunctionAddress("MessageBoxW");
   }

   boolean anyOpen() {
      return this.open.get() > 0;
   }

   public void show(Box box, IntConsumer answer) {
      Thread t = new Thread(() -> {
         this.open.incrementAndGet();

         int result;
         try {
            result = this.messageBox(box);
         } finally {
            this.open.decrementAndGet();
         }
         int which = switch (result) {
            case 1, 6 -> -1;
            case 7 -> -2;
            default -> 0;
         };
         LOG.log(Level.INFO, "caixa nativa \"" + box.title() + "\": " + (which == -1 ? box.positive() : (which == -2 ? box.negative() : "sem resposta")));
         if (which != 0) {
            answer.accept(which);
         } else {
            LOG.log(Level.ERROR, "MessageBoxW devolveu " + result + " para \"" + box.title() + "\"");
         }
      }, "zl-caixa");
      t.setDaemon(true);
      t.start();
   }

   private int messageBox(Box box) {
      int type = (box.negative() != null ? 36 : 64) | 65536;
      String message = box.message() == null ? "" : box.message().strip();
      String title = box.title() == null ? "" : box.title();
      MemoryStack s = MemoryStack.stackPush();

      int var6;
      try {
         var6 = JNI.callPPPI(this.owner, MemoryUtil.memAddress(s.UTF16(message)), MemoryUtil.memAddress(s.UTF16(title)), type, this.messageBoxW);
      } catch (Throwable var9) {
         if (s != null) {
            try {
               s.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (s != null) {
         s.close();
      }

      return var6;
   }

   static void showError(String title, String message) {
      long messageBoxW = User32.getLibrary().getFunctionAddress("MessageBoxW");
      MemoryStack s = MemoryStack.stackPush();

      try {
         JNI.callPPPI(0L, MemoryUtil.memAddress(s.UTF16(message)), MemoryUtil.memAddress(s.UTF16(title)), 65552, messageBoxW);
      } catch (Throwable var8) {
         if (s != null) {
            try {
               s.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (s != null) {
         s.close();
      }
   }
}
