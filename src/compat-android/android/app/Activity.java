package android.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import java.lang.System.Logger.Level;
import zlport.stubs.NotPorted;

public class Activity extends ContextThemeWrapper {
   private Thread mUiThread;
   private Handler mHandler;

   final void attach(Context base) {
      this.attachBaseContext(base);
      this.mUiThread = Thread.currentThread();
   }

   protected void onCreate(Bundle savedInstanceState) {
   }

   protected void onResume() {
   }

   protected void onPause() {
   }

   protected void onDestroy() {
   }

   @Override
   public Object getSystemService(String name) {
      return super.getSystemService(name);
   }

   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      NotPorted.method("android.app.Activity#onActivityResult(int,int,Intent)");
   }

   protected Dialog onCreateDialog(int id) {
      NotPorted.method("android.app.Activity#onCreateDialog(int)");
      return null;
   }

   public boolean onKeyDown(int keyCode, KeyEvent event) {
      NotPorted.method("android.app.Activity#onKeyDown(int,KeyEvent)");
      return false;
   }

   public final void removeDialog(int id) {
      NotPorted.method("android.app.Activity#removeDialog(int)");
   }

   public final void runOnUiThread(Runnable action) {
      if (Thread.currentThread() == this.mUiThread) {
         action.run();
      } else {
         Handler handler;
         synchronized (this) {
            if (this.mHandler == null) {
               this.mHandler = new Handler(Looper.getMainLooper());
            }

            handler = this.mHandler;
         }

         handler.post(action);
      }
   }

   public final void showDialog(int id) {
      NotPorted.method("android.app.Activity#showDialog(int)");
   }

   @Override
   public void startActivity(Intent intent) {
      System.getLogger("zlport.android.ui").log(Level.INFO, "startActivity sem efeito no PC (FASE-04 E6): " + intent);
   }

   public void startActivityForResult(Intent intent, int requestCode) {
      NotPorted.method("android.app.Activity#startActivityForResult(Intent,int)");
   }
}
