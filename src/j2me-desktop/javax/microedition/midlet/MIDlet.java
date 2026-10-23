package javax.microedition.midlet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import java.io.IOException;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.sensor.AccelerometerConnection;
import zlport.j2me.Platform;
import zlport.stubs.NotPorted;

public abstract class MIDlet extends Activity {
   private static MIDlet instance;
   private static volatile boolean mPaused;
   private static final boolean PAUSEABLE = true;
   private AccelerometerConnection accelerometerConnection;

   public MIDlet() {
      instance = this;
   }

   public static int getHeight() {
      return Platform.driver().surfaceHeight();
   }

   public static final MIDlet getMIDletInstance() {
      return instance;
   }

   public static int getWidth() {
      return Platform.surface().displayWidth();
   }

   public static boolean isPaused() {
      return mPaused;
   }

   protected abstract void destroyApp(boolean var1) throws MIDletStateChangeException;

   public final String getAppProperty(String key) {
      return null;
   }

   public final void notifyDestroyed() {
      Platform.surface().midletDestroyed();
   }

   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.i("LCM", "MIDlet.onCreate");
   }

   protected void onDestroy() {
      Log.i("LCM", "MIDlet.onDestroy");
      Log.e("AJ", "MIDLet$onDestroy");
      super.onDestroy();

      try {
         if (this.accelerometerConnection != null) {
            try {
               this.accelerometerConnection.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }

         Platform.driver().destroy();
         this.destroyApp(true);
      } catch (MIDletStateChangeException e) {
         e.printStackTrace();
      }
   }

   protected void onPause() {
      Log.i("LCM", "MIDlet.onPause");
      mPaused = true;
      Platform.driver().pause(this);
      super.onPause();
   }

   protected void onResume() {
      Log.i("LCM", "MIDlet.onResume");
      mPaused = false;

      try {
         this.startApp();
      } catch (MIDletStateChangeException var2) {
      }

      super.onResume();
   }

   protected abstract void pauseApp();

   public final boolean platformRequest(String url) throws ConnectionNotFoundException {
      NotPorted.method("javax.microedition.midlet.MIDlet#platformRequest(String)");
      return false;
   }

   public void setAccelerometerConnection(AccelerometerConnection connection) {
      this.accelerometerConnection = connection;
   }

   protected abstract void startApp() throws MIDletStateChangeException;
}
