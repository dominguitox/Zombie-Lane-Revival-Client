package com.dchoc.idead;

import com.dchoc.menuobject.MenuObject;
import com.dchoc.spriteobject.SpriteObject;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public interface FlowHandler {
   int BACK_EVENT = -3;
   int DEFAULT_EVENT = -2;
   int NO_EVENT = -1;
   int PROCESS_MENU_BRING_VISIBLE = 2;
   int PROCESS_MENU_BROUGHT_VISIBLE = 3;
   int PROCESS_MENU_CONTENT_ADDED = 1;
   int PROCESS_MENU_NEWLY_CREATED = 0;
   int TRANSITION_LOADING_SCREEN_BETWEEN = 2;
   int TRANSITION_STATE_IN = 1;
   int TRANSITION_STATE_OUT = 0;

   void controllerActivated();

   void doDraw(int var1, Graphics var2);

   void drawLoadingScreen(int var1);

   void drawMenu(int var1, Graphics var2, MenuObject var3);

   void drawPostLoadingScreenTransition(int var1, int var2, int var3, int var4, Graphics var5);

   void drawPostStateTransition(int var1, int var2, int var3, int var4, Graphics var5, MenuObject var6);

   boolean evaluateBranchCondition(int var1);

   void eventOccurred(int var1, int var2);

   int getDavinciLoadingPercentage(int var1);

   int getLoadingScreenTransitionDuration(int var1, int var2, int var3);

   int getStateGroupLoadingCount(int var1);

   int getStateLoadingCount(int var1);

   int getStateTransitionDuration(int var1, int var2, int var3);

   boolean isChildGroup(int var1, int var2);

   boolean isMenuNeeded(int var1);

   void keyEventOccurred(int var1, int var2, int var3);

   void keyEventOccurred(int var1, int var2, int var3, int var4, char[] var5);

   void licenseManagerActivated();

   void loadState(int var1, int var2);

   void loadStateGroup(int var1, int var2);

   int logicUpdate(int var1, int var2);

   void memoryWarning();

   void menuSetInputItem(int var1, MenuObject var2, int var3, int var4, String var5, String var6, SpriteObject var7, Image[] var8, int var9, int var10);

   void menuSetItem(int var1, MenuObject var2, int var3, int var4, String var5, SpriteObject var6, Image[] var7, int var8);

   void menuSetScreen(int var1, MenuObject var2, int var3, int var4, int var5);

   void menuSetSize(int var1, MenuObject var2);

   void menuSetSliderItem(int var1, MenuObject var2, int var3, String var4, SpriteObject var5, Image[] var6, int var7, int var8, int var9);

   void menuSetSoftkey(int var1, MenuObject var2, int var3, int var4);

   void menuSetStyle(int var1, MenuObject var2, int var3);

   void menuSetSwitchItem(
      int var1, MenuObject var2, int var3, String var4, SpriteObject var5, Image[] var6, String[] var7, SpriteObject var8, Image[] var9, int var10
   );

   void menuSetTitleBar(int var1, MenuObject var2, String var3, Image var4, int var5);

   void menuSetTitleBarDvc(int var1, MenuObject var2, String var3, SpriteObject var4, int var5);

   void pauseGame();

   void pointerEventOccurred(int var1, int var2, int var3, int var4);

   void processMenu(int var1, MenuObject var2, int var3);

   String processText(int var1, int var2, int var3);

   void resumeGame();

   void screenSizeChanged();

   void setLoading(int var1, boolean var2);

   void setLoadingScreenTransition(int var1, boolean var2);

   void setStateTransition(int var1, boolean var2);

   boolean shouldStoreStateToHistory(int var1);

   void switchState(int var1, int var2, MenuObject var3);

   void touchEventOccurred(int var1, int[][] var2, int[] var3);

   void unloadState(int var1, int var2);

   void unloadStateGroup(int var1, int var2);

   void updateLoadingScreenTransition(int var1, int var2, int var3, int var4);

   void updateStateTransition(int var1, int var2, int var3, int var4, MenuObject var5);
}
