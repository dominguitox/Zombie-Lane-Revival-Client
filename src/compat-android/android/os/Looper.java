package android.os;

import zlport.stubs.NotPorted;

public final class Looper {
   private static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();
   private static Looper sMainLooper;
   final MessageQueue mQueue;
   final Thread mThread = Thread.currentThread();

   private Looper() {
      this.mQueue = new MessageQueue(this.mThread);
   }

   public static void prepareMainLooper() {
      synchronized (Looper.class) {
         if (sMainLooper != null) {
            throw new IllegalStateException("The main Looper has already been prepared.");
         }

         sMainLooper = new Looper();
         sThreadLocal.set(sMainLooper);
      }
   }

   public static Looper getMainLooper() {
      synchronized (Looper.class) {
         return sMainLooper;
      }
   }

   public static Looper myLooper() {
      return sThreadLocal.get();
   }

   public MessageQueue getQueue() {
      return this.mQueue;
   }

   public Thread getThread() {
      return this.mThread;
   }

   public boolean isCurrentThread() {
      return Thread.currentThread() == this.mThread;
   }

   public static void loop() {
      NotPorted.method("android.os.Looper#loop()");
   }

   public static void prepare() {
      NotPorted.method("android.os.Looper#prepare()");
   }

   @Override
   public String toString() {
      return "Looper (" + this.mThread.getName() + ", tid " + this.mThread.threadId() + ") {" + Integer.toHexString(System.identityHashCode(this)) + "}";
   }
}
