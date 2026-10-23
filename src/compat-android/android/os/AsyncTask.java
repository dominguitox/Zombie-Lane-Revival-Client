package android.os;

import zlport.stubs.NotPorted;

public abstract class AsyncTask {
   public AsyncTask() {
      NotPorted.method("android.os.AsyncTask#<init>()");
   }

   protected abstract Object doInBackground(Object... var1);

   public final AsyncTask execute(Object... params) {
      NotPorted.method("android.os.AsyncTask#execute(Object[])");
      return null;
   }

   protected void onPostExecute(Object result) {
      NotPorted.method("android.os.AsyncTask#onPostExecute(Object)");
   }
}
