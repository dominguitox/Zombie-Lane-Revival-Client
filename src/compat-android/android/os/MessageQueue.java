package android.os;

import java.util.ArrayDeque;

public final class MessageQueue {
   private final Thread mThread;
   private final ArrayDeque<Message> mMessages = new ArrayDeque<>();
   private volatile Runnable mWakeUp;

   MessageQueue(Thread thread) {
      this.mThread = thread;
   }

   boolean enqueueMessage(Message msg) {
      synchronized (this) {
         if (msg.inUse) {
            throw new IllegalStateException(msg + " This message is already in use.");
         }

         msg.inUse = true;
         this.mMessages.add(msg);
      }

      Runnable wakeUp = this.mWakeUp;
      if (wakeUp != null) {
         wakeUp.run();
      }

      return true;
   }

   public boolean isIdle() {
      synchronized (this) {
         return this.mMessages.isEmpty();
      }
   }

   public void setWakeUp(Runnable wakeUp) {
      this.mWakeUp = wakeUp;
   }

   public int dispatchPending() {
      if (Thread.currentThread() != this.mThread) {
         throw new IllegalStateException("A fila de " + this.mThread.getName() + " só pode ser consumida por ela, não por " + Thread.currentThread().getName());
      }

      int pending;
      synchronized (this) {
         pending = this.mMessages.size();
      }

      for (int i = 0; i < pending; i++) {
         Message msg;
         synchronized (this) {
            msg = this.mMessages.poll();
         }

         msg.target.dispatchMessage(msg);
      }

      return pending;
   }
}
