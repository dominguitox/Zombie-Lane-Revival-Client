package zlport.gles;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

public final class CheckingGL implements InvocationHandler {
   public static final String DEBUG_PROPERTY = "zlport.gl.debug";
   private static final Logger LOG = System.getLogger("zlport.gles");
   private static final int LOGGED_WITH_STACK = 20;
   private final GL11 delegate;
   private final Thread glThread;
   private final GL11 proxy;
   private final AtomicInteger errors = new AtomicInteger();
   private final Map<String, AtomicInteger> offThread = new ConcurrentHashMap<>();

   private CheckingGL(GL11 delegate, Thread glThread) {
      this.delegate = delegate;
      this.glThread = glThread;
      this.proxy = (GL11)Proxy.newProxyInstance(CheckingGL.class.getClassLoader(), new Class[]{GL11.class, GL11Ext.class}, this);
   }

   public static CheckingGL wrap(GL11 delegate, Thread glThread) {
      if (!(delegate instanceof GL11Ext)) {
         throw new IllegalArgumentException("O GL envolvido precisa implementar GL11Ext: " + delegate);
      } else {
         return new CheckingGL(delegate, glThread);
      }
   }

   public GL11 gl() {
      return this.proxy;
   }

   public int errorCount() {
      return this.errors.get();
   }

   public Map<String, Integer> offThreadCalls() {
      Map<String, Integer> snapshot = new TreeMap<>();
      this.offThread.forEach((call, count) -> snapshot.put(call, count.get()));
      return snapshot;
   }

   @Override
   public Object invoke(Object self, Method method, Object[] args) throws Throwable {
      if (method.getDeclaringClass() == Object.class) {
         return switch (method.getName()) {
            case "equals" -> self == args[0];
            case "hashCode" -> System.identityHashCode(self);
            default -> "CheckingGL(" + this.delegate + ")";
         };
      } else {
         boolean onGlThread = Thread.currentThread() == this.glThread;
         if (!onGlThread) {
            this.offThread.computeIfAbsent(Thread.currentThread().getName() + ": " + method.getName(), k -> new AtomicInteger()).incrementAndGet();
         }

         Object result;
         try {
            result = method.invoke(this.delegate, args);
         } catch (InvocationTargetException e) {
            throw e.getCause();
         }

         if (onGlThread && !method.getName().equals("glGetError")) {
            this.check(method, args);
         }

         return result;
      }
   }

   private void check(Method method, Object[] args) {
      int error = this.delegate.glGetError();

      for (int guard = 0; error != 0 && guard < 8; guard++) {
         int count = this.errors.incrementAndGet();
         String message = "erro de GL " + name(error) + " depois de " + method.getName() + describe(args);
         if (count <= 20) {
            LOG.log(Level.ERROR, message, new Throwable("pilha da chamada"));
         } else {
            LOG.log(Level.ERROR, message);
         }

         error = this.delegate.glGetError();
      }
   }

   static String describe(Object[] args) {
      StringJoiner out = new StringJoiner(", ", "(", ")");

      for (Object arg : args == null ? new Object[0] : args) {
         out.add(switch (arg) {
            case null -> "null";
            case int[] a -> Arrays.toString(a);
            case float[] a -> Arrays.toString(a);
            case Buffer b -> b.getClass().getSimpleName() + "[pos=" + b.position() + " rem=" + b.remaining() + "]";
            default -> String.valueOf(arg);
         });
      }

      return out.toString();
   }

   static String name(int error) {
      String name = switch (error) {
         case 1280 -> "GL_INVALID_ENUM";
         case 1281 -> "GL_INVALID_VALUE";
         case 1282 -> "GL_INVALID_OPERATION";
         case 1283 -> "GL_STACK_OVERFLOW";
         case 1284 -> "GL_STACK_UNDERFLOW";
         case 1285 -> "GL_OUT_OF_MEMORY";
         default -> "desconhecido";
      };
      return name + " (0x" + Integer.toHexString(error) + ")";
   }
}
