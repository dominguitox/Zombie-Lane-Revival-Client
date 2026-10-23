package com.dchoc.toolkit;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public interface IMenu {
   int ALIGNMENT_HCENTER = 1;
   int ALIGNMENT_LEFT = 0;
   int ALIGNMENT_TOP = 0;
   int ALIGNMENT_VCENTER = 2;
   int EVENT_ITEM = 1;
   int EVENT_SOFTKEY = 0;
   int INPUT_TYPE_EMAIL = 4;
   int INPUT_TYPE_LOCALIZED_TEXT = 4;
   int INPUT_TYPE_LONG_TEXT = 1;
   int INPUT_TYPE_NUMBER = 3;
   int INPUT_TYPE_PHONE_NUMBER = 2;
   int INPUT_TYPE_SHORT_TEXT = 0;
   int INPUT_TYPE_TEXT = 1;
   int INPUT_TYPE_USERNAME = 0;
   int ITEM_TYPE_EMPTY = -1;
   int ITEM_TYPE_IMAGE = 1;
   int ITEM_TYPE_SELECTION = 0;
   int ITEM_TYPE_TEXT = 1;
   int NO_PAGING = -1;
   int NO_SOFTKEY = -1;
   int SCREEN_TYPE_MENU_SCREEN = 0;
   int SCREEN_TYPE_TEXT_BOX = 2;
   int SCREEN_TYPE_TEXT_SCREEN = 1;
   int SOFTKEY_MIDDLE = 2;
   int SOFTKEY_NEGATIVE = 1;
   int SOFTKEY_POSITIVE = 0;

   void destroyItem(int var1);

   void doDraw(Graphics var1, int var2, int var3);

   void drawLoadingScreen(Graphics var1, String[] var2, int var3);

   int getItemIntValue(int var1);

   String getItemStringValue(int var1);

   int getSelectedItem();

   void keyEventOccurred(int var1, int var2);

   int[] logicUpdate(int var1);

   void pointerEventOccurred(int var1, int var2, int var3);

   void releaseScreen();

   void setInputItem(int var1, int var2, String var3, String var4, Image[] var5, int var6, int var7);

   void setItem(int var1, int var2, String var3, Image[] var4, int var5);

   void setScreen(int var1, int var2, int var3);

   void setSelectedItem(int var1);

   void setSize(int var1, int var2);

   void setSoftkey(int var1, int var2);

   void setTitleBar(String var1, Image var2, int var3);

   void setVisible();
}
