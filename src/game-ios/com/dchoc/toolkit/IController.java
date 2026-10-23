package com.dchoc.toolkit;

import javax.microedition.lcdui.Graphics;

public interface IController {
   int ACCELEROMETER_EVENT_SWEEP_X = 2000;
   int ACCELEROMETER_EVENT_SWEEP_Y = 2001;
   int ACCELEROMETER_EVENT_SWEEP_Z = 2002;
   int CONTROLLER_BUTTON_1 = 0;
   int CONTROLLER_BUTTON_2 = 1;
   int CONTROLLER_BUTTON_3 = 2;
   int CONTROLLER_BUTTON_4 = 3;
   int CONTROLLER_DIRECTIONAL_1 = 4;
   int CONTROLLER_DIRECTIONAL_2 = 5;
   int CONTROLLER_EVENT_ACTION_1 = -1004;
   int CONTROLLER_EVENT_ACTION_2 = -1005;
   int CONTROLLER_EVENT_ACTION_3 = -1006;
   int CONTROLLER_EVENT_ACTION_4 = -1007;
   int CONTROLLER_EVENT_DOWN = -1001;
   int CONTROLLER_EVENT_LEFT = -1002;
   int CONTROLLER_EVENT_RIGHT = -1003;
   int CONTROLLER_EVENT_UP = -1000;
   int STATE_ENTER_ACTIVATE_FROM_MENU = 3;
   int STATE_ENTER_APP_START = 2;
   int STATE_ENTER_DISABLE_CONTROLLER = 5;
   int STATE_ENTER_ENABLE_CONTROLLER = 4;
   int STATE_RETURN_FINISHED = 1;
   int STATE_RETURN_NOT_FINISHED = 0;

   void doDraw(Graphics var1);

   String getControllerAboutText();

   String getControllerMenuItemLabel();

   void initMenus();

   boolean isControllerEnabled();

   boolean isControllerSupported();

   void keyEventOccurred(int var1, int var2);

   int logicUpdate(int var1);

   void pointerEventOccurred(int var1, int var2, int var3);

   boolean setState(int var1);
}
