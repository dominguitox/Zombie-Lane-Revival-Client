package android.os;

public class Handler {
   final Looper mLooper;
   final MessageQueue mQueue;

   public Handler() {
      this.mLooper = Looper.myLooper();
      if (this.mLooper == null) {
         throw new RuntimeException("Can't create handler inside thread " + Thread.currentThread() + " that has not called Looper.prepare()");
      }

      this.mQueue = this.mLooper.mQueue;
   }

   public Handler(Looper looper) {
      this.mLooper = looper;
      this.mQueue = looper.mQueue;
   }

   public void handleMessage(Message msg) {
   }

   public void dispatchMessage(Message msg) {
      if (msg.callback != null) {
         msg.callback.run();
      } else {
         this.handleMessage(msg);
      }
   }

   public final Looper getLooper() {
      return this.mLooper;
   }

   public final Message obtainMessage() {
      Message msg = Message.obtain();
      msg.target = this;
      return msg;
   }

   public final Message obtainMessage(int what) {
      Message msg = this.obtainMessage();
      msg.what = what;
      return msg;
   }

   public final boolean post(Runnable r) {
      Message msg = Message.obtain();
      msg.callback = r;
      return this.sendMessage(msg);
   }

   public final boolean sendMessage(Message msg) {
      msg.target = this;
      return this.mQueue.enqueueMessage(msg);
   }

   @Override
   public String toString() {
      return "Handler (" + this.getClass().getName() + ") {" + Integer.toHexString(System.identityHashCode(this)) + "}";
   }
}
