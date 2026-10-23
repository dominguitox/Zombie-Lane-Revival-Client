package com.dchoc.toolkit;

import javax.microedition.lcdui.Graphics;

public interface IButton {
   int ALIGNMENT_BOTTOM = 32;
   int ALIGNMENT_CENTER = 67;
   int ALIGNMENT_HCENTER = 1;
   int ALIGNMENT_LEFT = 4;
   int ALIGNMENT_RIGHT = 8;
   int ALIGNMENT_TOP = 16;
   int ALIGNMENT_VCENTER = 66;
   int BUTTON_DISPATCH_EVENT = 6;
   int BUTTON_HIGHLIGHTED = 1;
   int BUTTON_IDLE = 0;
   int BUTTON_LOCKED_HIGHLIGHTED = 4;
   int BUTTON_LOCKED_IDLE = 3;
   int BUTTON_LOCKED_SELECTED = 5;
   int BUTTON_SELECTED = 2;
   int EVENT_DATA_EVENT_ID = 0;
   int EVENT_DATA_EVENT_TYPE = 1;
   int SHRINK_TO_FIT = -1;
   int STATE_NONE = -1;

   void destroy();

   void doDraw(Graphics var1);

   int getHeight();

   int getPivotX();

   int getPivotY();

   int getWidth();

   int getX();

   int getY();

   int[] logicUpdate(int var1);

   boolean pointerEventOccurred(int var1, int var2, int var3);

   void resetState();

   void setPosition(int var1, int var2);

   void setSize(int var1, int var2);
}
