package com.dchoc.toolkit;

import com.dchoc.iphonewrappers.SoundsPlayer;
import com.dchoc.menuobject.MenuObject;
import java.util.Arrays;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

public abstract class DChocMIDlet extends MIDlet implements Runnable {
   public static final int APP_EVENT_PAUSE = 0;
   public static final int APP_EVENT_RESUME = 1;
   public static final int APP_EVENT_START = 2;
   public static final int APP_EVENT_EXIT = 3;
   public static final int APP_EVENT_ACTIVATE_CONTROLLER = 7;
   public static final int APP_EVENT_ACTIVATE_LICENSE_MANAGER = 5;
   public static final int APP_EVENT_MEMORY_WARNING = 8;
   public static final int APP_EVENT_MOUSE_ENTERED = 5;
   public static final int APP_EVENT_MOUSE_EXITED = 6;
   public static final int APP_EVENT_SCREEN_SIZE_CHANGED = 4;
   public static final int GESTURE_FIXED_POINT_ACCURACY = 10;
   public static final int GESTURE_INDEX_MULTI_DRAG_DELTA_X = 0;
   public static final int GESTURE_INDEX_MULTI_DRAG_DELTA_Y = 1;
   public static final int GESTURE_INDEX_ROTATION = 3;
   public static final int GESTURE_INDEX_SCALE = 2;
   public static final int LETTERBOX_BOTTOM = 1;
   public static final int LETTERBOX_LEFT = 2;
   public static final int LETTERBOX_RIGHT = 3;
   public static final int LETTERBOX_TOP = 0;
   public static final int TOUCH_EVENT_DRAGGED = 2;
   public static final int TOUCH_EVENT_PRESSED = 0;
   public static final int TOUCH_EVENT_RELEASED = 1;
   public static final int TOUCH_INDEX_EVENT = 2;
   public static final int TOUCH_INDEX_ID = 3;
   public static final int TOUCH_INDEX_X = 0;
   public static final int TOUCH_INDEX_Y = 1;
   private static DChocMIDlet sm_instance;
   private static Thread sm_mainLoop;
   protected boolean m_exitApplication;
   protected boolean m_appStarted;
   private long m_previousTime;
   private int m_currentDeltaTimeTableIndex;
   private int m_cumulativeDeltaTime;
   private boolean m_initialized;
   private boolean m_beingDestroyed;
   private boolean m_appPaused;
   private boolean m_resumeApplication;
   private boolean m_callSeriallyCalled;
   private long m_previousRunTime;
   private int[] m_deltaTimeTable;

   protected DChocMIDlet() {
      sm_instance = this;
      Toolkit.create(this);
   }

   public static DChocMIDlet getInstance() {
      return sm_instance;
   }

   public abstract void appEventOccurred(int var1);

   public abstract void doDraw(Graphics var1);

   public abstract void keyEventOccurred(int var1, int var2);

   public abstract void logicUpdate(int var1);

   public abstract void pointerEventOccurred(int var1, int var2, int var3);

   public void doPostDraw(Graphics graphics) {
   }

   public void touchEventOccurred(int[][] touches, int[] gestures) {
   }

   public void controllerEventOccurred(int type, int id, int x, int y, int value) {
   }

   public String getApplicationID() {
      return "4";
   }

   public String getApplicationName() {
      return Toolkit.getText(2236);
   }

   public IMenu getNewMenuObject() {
      return new MenuObject();
   }

   public int getRealDeltaTime() {
      int index = this.m_currentDeltaTimeTableIndex - 1;
      return this.m_deltaTimeTable[index < 0 ? 7 : index];
   }

   public static void setCurrent(Displayable displayable) {
      Display.getDisplay(sm_instance).setCurrent(displayable);
   }

   public void vibrate(int duration) {
      Display.getDisplay(this).vibrate(duration);
   }

   @Override
   public void run() {
      try {
         if (!this.m_initialized) {
            this.m_deltaTimeTable = new int[8];
            this.resetTimer();
            Toolkit.initialize();
            this.appEventOccurred(2);
            this.m_initialized = true;
            this.m_previousRunTime = System.currentTimeMillis();
         }

         if (this.m_resumeApplication) {
            this.appEventOccurred(1);
            Toolkit.toolkitEventOccurred(1);
            this.m_appPaused = false;
            this.m_resumeApplication = false;
            Toolkit.doRepaint();
         }

         if (!this.m_appPaused && !this.m_exitApplication) {
            Toolkit.doRepaint();
            long now = System.currentTimeMillis();
            int delta = (int)(now - this.m_previousRunTime);
            this.m_previousRunTime = now;
            this.logicUpdate(delta);
            Toolkit.toolkitEventOccurred(2);
         }

         Thread.yield();
      } catch (Throwable throwable) {
         throwable.printStackTrace();
         System.err.println(throwable);
      }

      if (!this.m_exitApplication && !this.m_appPaused) {
         Display.getDisplay(this).callSerially(this);
      } else {
         synchronized (this) {
            this.m_callSeriallyCalled = false;
         }
      }

      if (this.m_exitApplication && !this.m_beingDestroyed) {
         SoundsPlayer.stopSoundEffect();
         SoundsPlayer.stopMusic();

         try {
            this.appEventOccurred(3);
         } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.err.println(throwable);
         }

         this.notifyDestroyed();
      }
   }

   protected void destroyApp(boolean unconditional) {
      SoundsPlayer.stopSoundEffect();
      SoundsPlayer.stopMusic();
      this.m_beingDestroyed = unconditional;
      this.m_exitApplication = true;
      if (unconditional) {
         this.appEventOccurred(3);

         try {
            if (sm_mainLoop != null) {
               sm_mainLoop.join();
               sm_mainLoop = null;
            }
         } catch (Exception exception) {
            System.err.println(exception);
         }
      }
   }

   public void pauseApp() {
      if (!this.m_appPaused) {
         Toolkit.pauseMusic();
         SoundsPlayer.stopSoundEffect();
         this.appEventOccurred(0);
         Toolkit.toolkitEventOccurred(0);
         this.m_appPaused = true;
      }
   }

   protected void startApp() {
      if (!this.m_appStarted) {
         Toolkit.setVisible(true);
         synchronized (this) {
            if (!this.m_callSeriallyCalled) {
               this.m_callSeriallyCalled = true;
               Display.getDisplay(this).callSerially(this);
            }
         }

         this.m_appStarted = true;
      } else if (this.m_appPaused) {
         this.m_previousTime = System.currentTimeMillis();
         this.m_resumeApplication = true;
         synchronized (this) {
            if (this.m_appPaused && !this.m_callSeriallyCalled) {
               this.m_callSeriallyCalled = true;
               Display.getDisplay(this).callSerially(this);
            }
         }
      }
   }

   public void resetTimer() {
      this.m_previousTime = System.currentTimeMillis();
      this.m_currentDeltaTimeTableIndex = 0;
      Arrays.fill(this.m_deltaTimeTable, 40);
      this.m_cumulativeDeltaTime = 320;
   }

   protected int updateTimer() {
      long now = System.currentTimeMillis();
      int delta = (int)(now - this.m_previousTime);
      this.m_previousTime = now;
      this.m_cumulativeDeltaTime = this.m_cumulativeDeltaTime - Math.min(this.m_deltaTimeTable[this.m_currentDeltaTimeTableIndex], 200);
      this.m_cumulativeDeltaTime = this.m_cumulativeDeltaTime + Math.min(delta, 200);
      this.m_deltaTimeTable[this.m_currentDeltaTimeTableIndex] = delta;
      this.m_currentDeltaTimeTableIndex = this.m_currentDeltaTimeTableIndex + 1 & 7;
      return this.m_cumulativeDeltaTime >> 3;
   }
}
