package android.os;

import zlport.stubs.NotPorted;

public final class Message {
   public int arg1;
   public int what;
   Handler target;
   Runnable callback;
   boolean inUse;
   private Bundle data;

   public void copyFrom(Message o) {
      this.what = o.what;
      this.arg1 = o.arg1;
      if (o.data != null) {
         NotPorted.method("android.os.Message#copyFrom(Message) com Bundle");
      }
   }

   public Bundle getData() {
      if (this.data == null) {
         this.data = new Bundle();
      }

      return this.data;
   }

   public Handler getTarget() {
      return this.target;
   }

   public Runnable getCallback() {
      return this.callback;
   }

   public static Message obtain() {
      return new Message();
   }

   public Bundle peekData() {
      return this.data;
   }

   public void setData(Bundle data) {
      this.data = data;
   }

   @Override
   public String toString() {
      return "{ what="
         + this.what
         + " arg1="
         + this.arg1
         + (this.callback != null ? " callback=" + this.callback.getClass().getName() : "")
         + (this.target != null ? " target=" + this.target.getClass().getName() : "")
         + " }";
   }
}
