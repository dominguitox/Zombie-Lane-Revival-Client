package com.dchoc.toolkit;

import javax.microedition.lcdui.Font;

public class Debugger {
   public static final boolean ASSERT_ON = false;
   private static final int BUFFER_SIZE = 5;
   private static final int CLEAR_TIME = 10000;
   public static final boolean DEBUG = false;
   private static final int DEBUGGER_TEXT_BACKGROUND_COLOR = 0;
   private static final int DEBUGGER_TEXT_COLOR = 16777215;
   public static final boolean DEBUG_TOOLKIT = false;
   public static final boolean EXCEPTION_ON = false;
   public static final boolean PP_VERBOSE_ON = false;
   public static final boolean PROFILER_ON = false;
   public static final boolean USE_RMS = false;
   public static final boolean VERBOSE_ON = false;
   private static String[] sm_buffer = new String[5];
   private static int sm_bufferIndex = 0;
   private static long sm_clearTimer = 10000L;
   private static Font sm_font = Font.getFont(32, 0, 8);
   private static String sm_heapUsage = "";
   private static int sm_heartBeat = 0;
   private static long sm_prevTime = 0L;
   private static boolean sm_read = false;
   private static long sm_timer;
   private static String sm_timerString = "";
}
