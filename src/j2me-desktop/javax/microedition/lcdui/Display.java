package javax.microedition.lcdui;

import android.widget.RelativeLayout;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.microedition.midlet.MIDlet;

public class Display {
   private static final ConcurrentLinkedQueue<Runnable> SERIAL_CALLS = new ConcurrentLinkedQueue<>();
   private static Displayable displayable;
   private static Display instance;
   public static RelativeLayout mainLayout;
   private static MIDlet theMidlet;

   private Display() {
   }

   public static Display getDisplay(MIDlet midlet) {
      theMidlet = midlet;
      return getInstance();
   }

   private static Display getInstance() {
      if (instance == null) {
         instance = new Display();
      }

      return instance;
   }

   public Displayable getCurrent() {
      return displayable;
   }

   public void setCurrent(Displayable next) {
      next.setView(theMidlet);
      displayable = next;
   }

   public boolean vibrate(int duration) {
      return false;
   }

   public void callSerially(Runnable r) {
      SERIAL_CALLS.add(r);
   }

   public static boolean runNextSerialCall() {
      Runnable r = SERIAL_CALLS.poll();
      if (r == null) {
         return false;
      }

      r.run();
      return true;
   }

   public static boolean hasSerialCalls() {
      return !SERIAL_CALLS.isEmpty();
   }

   public static void runSerialCalls() {
      for (int pending = SERIAL_CALLS.size(); pending > 0; pending--) {
         Runnable r = SERIAL_CALLS.poll();
         if (r == null) {
            return;
         }

         r.run();
      }
   }
}
