package com.dchoc.idead;

import com.dchoc.menuobject.MenuObject;
import com.dchoc.spriteobject.Animation;
import com.dchoc.spriteobject.DavinciUtilities;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;
import java.io.EOFException;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class FlowProcessor {
   private static final int[] INT_ARRAY_SIZE_0 = new int[0];
   public static final int NO_LOADING = -1;
   private static Hashtable smMenuResourceHashtable;
   private int mBranchCount;
   private int mBranchStartIndex;
   private int[] mCurrentBranch;
   private int[] mCurrentGameScreen;
   private MenuObject mCurrentMenu;
   private int mCurrentTransition;
   private boolean mDavinciLoading;
   private int mDavinciLoadingPercentage;
   private short[] mEventTargets;
   private long mFinalLoadingStepStartTime;
   private byte[] mFlowBinary;
   private FlowHandler mFlowHandler;
   private int mFlowTransitionState;
   private int mGameScreenCount;
   private int mGameScreenStartIndex;
   private boolean mGoingForward;
   private boolean mInFinalLoadingStep;
   private int[][] mLoadedBranches;
   private int[][] mLoadedGameScreens;
   private MenuObject[] mLoadedMenus;
   private int mLoadingCounter;
   private int mMaxLoadingCount;
   private int mMenuScreenCount;
   private byte[] mMenuSelectedItems;
   private short[][] mMenuSoftkeyEvents;
   private short[] mMenusToLoad;
   private boolean mReloadStateGroup;
   private boolean[] mStateGroupInMemory;
   private int mStateGroupLoadingCount;
   private byte[] mStateGroups;
   private short[] mStateHistory;
   private int mStateHistoryPointer;
   private int mStateLoadingCount;
   private short[] mStateOffsets;
   private boolean mStoreStateToHistory;
   private int mTransitionDuration;
   private int mTransitionStateA;
   private int mTransitionStateB;
   private int mTransitionTime;
   private int mCurrentState = -1;
   private int mNextState = -1;

   public FlowProcessor(int i, FlowHandler flowHandler) {
      try {
         this.initializeFlow(i, flowHandler);
      } catch (EOFException exception) {
         System.err.println(exception);
      }
   }

   private void activateBranch(int i) {
      this.mCurrentBranch = this.mLoadedBranches[i - this.mBranchStartIndex];
      this.mStoreStateToHistory = false;
   }

   private void activateGameScreen(int i) {
      this.mCurrentGameScreen = this.mLoadedGameScreens[i - this.mGameScreenStartIndex];
      this.mStoreStateToHistory = this.mFlowHandler.shouldStoreStateToHistory(i);
   }

   private void activateMenuScreen(int i) {
      this.mCurrentMenu = this.mLoadedMenus[i];
      if (this.mGoingForward) {
         this.mCurrentMenu.setSelectedItem(0);
      } else {
         this.mCurrentMenu.setSelectedItem(this.mMenuSelectedItems[this.mCurrentState] & 255);
      }

      this.mFlowHandler.processMenu(i, this.mCurrentMenu, 2);
      this.mCurrentMenu.setVisible();
      this.mFlowHandler.processMenu(i, this.mCurrentMenu, 3);
      this.mStoreStateToHistory = this.mFlowHandler.shouldStoreStateToHistory(i);
   }

   private void activateNextState() {
      byte group = this.mStateGroups[this.mNextState];
      if (group != -1) {
         this.mStateGroupInMemory[group] = true;
      }

      smMenuResourceHashtable = null;
      this.mFlowHandler.setLoading(this.mNextState, false);
      int previous = this.mCurrentState;
      this.mCurrentState = this.mNextState;
      this.mNextState = -1;

      for (int index = this.mStateHistoryPointer - 1; index >= 0; index--) {
         if (this.mStateHistory[index] == this.mCurrentState) {
            this.mStateHistoryPointer = index;
            break;
         }
      }

      this.activateState(this.mCurrentState);
      this.mFlowHandler.switchState(previous, this.mCurrentState, this.mCurrentMenu);
      this.mFlowHandler.logicUpdate(this.mCurrentState, 0);
   }

   private void activateNextTransitionState() {
      int state = this.mFlowTransitionState;

      while (true) {
         switch (state) {
            case 1:
               this.mFlowHandler.setStateTransition(this.mCurrentTransition, true);
               if (this.mCurrentState != -1) {
                  this.releaseCurrentState();
               }
               break;
            case 2:
               this.mFlowHandler.setLoadingScreenTransition(this.mCurrentTransition, true);
            case 3:
            default:
               break;
            case 4:
               this.activateNextState();
               this.mFlowHandler.setLoadingScreenTransition(this.mCurrentTransition, true);
               break;
            case 5:
               this.setFlowTransitionState(0);
               this.mFlowHandler.setStateTransition(this.mCurrentTransition, true);
               return;
         }

         int next = state + 1;
         if (this.isTransitionNeeded(next, this.mTransitionStateA, this.mTransitionStateB)) {
            switch (state) {
               case 0:
               case 4:
                  this.mFlowHandler.setStateTransition(this.mCurrentTransition, false);
                  break;
               case 1:
               case 3:
                  this.mFlowHandler.setLoadingScreenTransition(this.mCurrentTransition, false);
                  break;
               case 2:
                  this.mFlowHandler.setLoading(this.mNextState, true);
                  this.mLoadingCounter = 0;
                  this.mDavinciLoading = false;
                  Toolkit.removeAllSoftKeys();
            }

            this.setFlowTransitionState(next);
            return;
         }

         state = next;
      }
   }

   private void activateState(int i) {
      if (this.isMenuState(i)) {
         this.activateMenuScreen(i);
      } else if (this.isGameState(i)) {
         this.activateGameScreen(i);
      } else {
         this.activateBranch(i);
      }
   }

   private void changeState() {
      this.mTransitionStateA = this.mCurrentState;

      while (this.isBranchState(this.mNextState)) {
         this.mCurrentState = this.mNextState;
         this.activateBranch(this.mNextState);
         this.processBranch(this.mNextState);
      }

      this.mCurrentState = this.mTransitionStateA;
      this.mTransitionStateB = this.mNextState;
      this.createLoadingLists();
      this.activateNextTransitionState();
   }

   private void clearCache() {
      this.mReloadStateGroup = true;
      if (this.mNextState == -1) {
         this.mNextState = this.mCurrentState;
      }

      if (this.mFlowTransitionState != 0) {
         this.mFlowTransitionState = 1;
         this.changeState();
      }
   }

   private void createLoadingLists() {
      this.mStateGroupLoadingCount = 0;
      byte b = this.mStateGroups[this.mNextState];
      byte b2 = !this.mReloadStateGroup && this.mCurrentState != -1 ? this.mStateGroups[this.mCurrentState] : -1;
      if (b != -1 && b != b2) {
         if (!this.mFlowHandler.isChildGroup(b, b2)) {
            this.mStateGroupLoadingCount = this.mFlowHandler.getStateGroupLoadingCount(b);
         }

         Vector vector = new Vector();

         for (int i2 = 0; i2 < this.mStateGroups.length; i2++) {
            if (this.mStateGroups[i2] == b
               && this.isMenuState(i2)
               && (this.mLoadedMenus[i2] == null || this.mReloadStateGroup)
               && this.mFlowHandler.isMenuNeeded(i2)) {
               vector.addElement((short)i2);
            }
         }

         int size = vector.size();
         this.mMenusToLoad = new short[size];

         for (int i3 = 0; i3 < size; i3++) {
            this.mMenusToLoad[i3] = (Short)vector.elementAt(i3);
         }
      } else {
         int i = this.mNextState;
         if (this.isMenuState(i) && this.mLoadedMenus[i] == null) {
            this.mMenusToLoad = new short[]{(short)i};
         } else {
            this.mMenusToLoad = new short[0];
         }
      }

      this.mStateLoadingCount = this.mFlowHandler.getStateLoadingCount(this.mNextState);
      this.mMaxLoadingCount = this.mStateGroupLoadingCount + this.mStateLoadingCount + this.mMenusToLoad.length;
      this.mDavinciLoadingPercentage = this.mFlowHandler.getDavinciLoadingPercentage(this.mNextState);
   }

   private void deactivateState() {
      this.mCurrentMenu = null;
      this.mCurrentGameScreen = null;
      this.mCurrentBranch = null;
      Toolkit.removeAllSoftKeys();
   }

   private boolean ignoreControlEvents() {
      return this.mFlowTransitionState != 0 && this.mFlowTransitionState != 5;
   }

   private void initializeFlow(int i, FlowHandler flowHandler) throws EOFException {
      this.mFlowHandler = flowHandler;
      this.mFlowBinary = Toolkit.getResourceBytes(i);
      DChocByteArray dChocByteArrayCreateByteArrayForReading = DChocByteArray.createByteArrayForReading(this.mFlowBinary, 0, this.mFlowBinary.length);
      int unsignedByte = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      this.mEventTargets = new short[unsignedByte];

      for (int i2 = 0; i2 < unsignedByte; i2++) {
         this.mEventTargets[i2] = dChocByteArrayCreateByteArrayForReading.readShort();
      }

      this.mStateGroupInMemory = new boolean[dChocByteArrayCreateByteArrayForReading.readUnsignedByte()];
      this.mMenuScreenCount = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      this.mGameScreenStartIndex = this.mMenuScreenCount;
      this.mGameScreenCount = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      this.mBranchStartIndex = this.mGameScreenStartIndex + this.mGameScreenCount;
      int unsignedByte2 = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      int i3 = this.mMenuScreenCount + this.mGameScreenCount + unsignedByte2;
      this.mStateGroups = new byte[i3];

      for (int i4 = 0; i4 < i3; i4++) {
         this.mStateGroups[i4] = dChocByteArrayCreateByteArrayForReading.readByte();
      }

      this.mStateOffsets = new short[i3];

      for (int i5 = 0; i5 < i3; i5++) {
         this.mStateOffsets[i5] = dChocByteArrayCreateByteArrayForReading.readShort();
      }

      this.mNextState = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      this.mLoadedMenus = new MenuObject[this.mMenuScreenCount];
      this.mMenuSoftkeyEvents = new short[this.mMenuScreenCount][];
      this.mLoadedGameScreens = new int[this.mGameScreenCount][];
      this.mLoadedBranches = new int[unsignedByte2][];
      this.mMenuSelectedItems = new byte[this.mMenuScreenCount];
      int i6 = this.mMenuScreenCount;

      for (int i7 = 0; i7 < this.mGameScreenCount; i7++) {
         this.loadGameScreen(i6);
         i6++;
      }

      for (int i8 = 0; i8 < unsignedByte2; i8++) {
         this.loadBranch(i6);
         i6++;
      }

      this.mBranchCount = unsignedByte2;
      this.mStateHistory = new short[this.mMenuScreenCount + unsignedByte2 + this.mGameScreenCount];
      this.mStateHistoryPointer = 0;
      this.mStoreStateToHistory = false;
   }

   private boolean isBranchState(int i) {
      int i2 = this.mGameScreenStartIndex + this.mGameScreenCount;
      return i >= i2 && i < i2 + this.mBranchCount;
   }

   private boolean isFinalLoadingStepDelayDone() {
      long jCurrentTimeMillis = System.currentTimeMillis();
      if (!this.mInFinalLoadingStep) {
         this.mInFinalLoadingStep = true;
         this.mFinalLoadingStepStartTime = jCurrentTimeMillis;
      }

      if ((int)(jCurrentTimeMillis - this.mFinalLoadingStepStartTime) < 150) {
         return false;
      }

      this.mInFinalLoadingStep = false;
      return true;
   }

   private boolean isGameState(int i) {
      return i >= this.mGameScreenStartIndex && i < this.mGameScreenStartIndex + this.mGameScreenCount;
   }

   private boolean isLoadingScreen() {
      return this.mFlowTransitionState >= 2 && this.mFlowTransitionState <= 4;
   }

   private boolean isMenuState(int i) {
      return i < this.mMenuScreenCount;
   }

   private boolean isTransitionNeeded(int i, int i2, int i3) {
      if (i == 3) {
         return this.loadingScreenNeeded();
      }

      if (i2 == i3 && !this.loadingScreenNeeded()) {
         return false;
      }

      switch (i) {
         case 1:
            if (i2 == -1) {
               return false;
            }

            this.mCurrentTransition = 0;
            break;
         case 2:
            if (i2 != -1 && this.loadingScreenNeeded()) {
               this.mCurrentTransition = 3;
               this.mTransitionDuration = this.mFlowHandler.getLoadingScreenTransitionDuration(this.mCurrentTransition, i2, i3);
               return this.mTransitionDuration > 0;
            }

            return false;
         case 3:
         default:
            return false;
         case 4:
            if (this.mFlowTransitionState != 3) {
               return false;
            }

            this.mCurrentTransition = 2;
            this.mTransitionDuration = this.mFlowHandler.getLoadingScreenTransitionDuration(this.mCurrentTransition, i2, i3);
            return this.mTransitionDuration > 0;
         case 5:
            this.mCurrentTransition = 1;
      }

      if (this.loadingScreenNeeded()) {
         this.mCurrentTransition |= 2;
      }

      this.mTransitionDuration = this.mFlowHandler.getStateTransitionDuration(this.mCurrentTransition, i2, i3);
      return this.mTransitionDuration > 0;
   }

   private void loadBranch(int i) throws EOFException {
      short s = this.mStateOffsets[this.mMenuScreenCount + this.mGameScreenCount + (i - this.mBranchStartIndex)];
      DChocByteArray dChocByteArrayCreateByteArrayForReading = DChocByteArray.createByteArrayForReading(this.mFlowBinary, s, this.mFlowBinary.length - s);
      int first = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      int second = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      this.mLoadedBranches[i - this.mBranchStartIndex] = new int[]{first == 255 ? -1 : first, second == 255 ? -1 : second};
   }

   private void loadGameScreen(int i) throws EOFException {
      int[] iArr = INT_ARRAY_SIZE_0;
      short s = this.mStateOffsets[this.mMenuScreenCount + (i - this.mGameScreenStartIndex)];
      DChocByteArray dChocByteArrayCreateByteArrayForReading = DChocByteArray.createByteArrayForReading(this.mFlowBinary, s, this.mFlowBinary.length - s);
      int unsignedByte = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      if (unsignedByte > 0) {
         iArr = new int[unsignedByte];

         for (int i2 = 0; i2 < unsignedByte; i2++) {
            int event = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
            iArr[i2] = event == 255 ? -1 : event;
         }
      }

      this.mLoadedGameScreens[i - this.mGameScreenStartIndex] = iArr;
   }

   private static void loadImagesOrAnimations(DChocByteArray dChocByteArray, int i, Image[][] imageArr, SpriteObject[] spriteObjectArr) throws EOFException {
      imageArr[0] = null;
      spriteObjectArr[0] = null;
      int[] resources = new int[i];
      byte[] types = new byte[i];
      boolean animations = false;
      boolean resourcesPresent = false;

      for (int index = 0; index < i; index++) {
         resources[index] = dChocByteArray.readInt();
         types[index] = dChocByteArray.readByte();
         animations |= types[index] == 2 || types[index] == 3;
         resourcesPresent |= resources[index] != -1;
      }

      if (resourcesPresent && animations) {
         Animation[] loaded = new Animation[i];

         for (int index = 0; index < i; index++) {
            Integer key = resources[index];
            Animation animation = (Animation)smMenuResourceHashtable.get(key);
            if (animation == null) {
               animation = DavinciUtilities.loadAnimation(resources[index]);
               smMenuResourceHashtable.put(key, animation);
            }

            loaded[index] = animation;
         }

         spriteObjectArr[0] = null;
      }
   }

   private void loadMenu(int i) throws EOFException {
      if (smMenuResourceHashtable == null) {
         smMenuResourceHashtable = new Hashtable();
      }

      short s = this.mStateOffsets[i];
      DChocByteArray dChocByteArrayCreateByteArrayForReading = DChocByteArray.createByteArrayForReading(this.mFlowBinary, s, this.mFlowBinary.length - s);
      int unsignedByte = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      Vector vector = new Vector();
      Image[][] imageArr = new Image[1][];
      SpriteObject[] spriteObjectArr = new SpriteObject[1];
      int unsignedByte2 = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      int unsignedByte3 = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      MenuObject menuObject = new MenuObject();
      this.mFlowHandler.processMenu(i, menuObject, 0);
      this.mFlowHandler.menuSetScreen(i, menuObject, unsignedByte2, unsignedByte, unsignedByte3);
      this.mFlowHandler.menuSetStyle(i, menuObject, dChocByteArrayCreateByteArrayForReading.readUnsignedByte());
      String strProcessText = this.mFlowHandler.processText(i, dChocByteArrayCreateByteArrayForReading.readInt(), -1);
      loadImagesOrAnimations(dChocByteArrayCreateByteArrayForReading, 1, imageArr, spriteObjectArr);
      Image image = imageArr[0] == null ? null : imageArr[0][0];
      SpriteObject spriteObject = spriteObjectArr[0];
      int unsignedByte4 = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      if (strProcessText != null || spriteObject != null || image != null) {
         if (spriteObject != null) {
            this.mFlowHandler.menuSetTitleBarDvc(i, menuObject, strProcessText, spriteObject, unsignedByte4);
         } else {
            this.mFlowHandler.menuSetTitleBar(i, menuObject, strProcessText, image, unsignedByte4);
         }
      }

      byte b = dChocByteArrayCreateByteArrayForReading.readByte();
      int unsignedByte5 = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      if (unsignedByte5 != 255) {
         vector.addElement(Integer.valueOf(b));
         vector.addElement(unsignedByte5);
      }

      this.mFlowHandler.menuSetSoftkey(i, menuObject, b, 0);
      byte b2 = dChocByteArrayCreateByteArrayForReading.readByte();
      int unsignedByte6 = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
      if (unsignedByte6 != 255) {
         vector.addElement(Integer.valueOf(b2));
         vector.addElement(unsignedByte6);
      }

      this.mFlowHandler.menuSetSoftkey(i, menuObject, b2, 1);

      for (int i2 = 0; i2 < unsignedByte; i2++) {
         int unsignedByte7 = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
         switch (unsignedByte7) {
            case 0:
               String strProcessText2 = this.mFlowHandler.processText(i, dChocByteArrayCreateByteArrayForReading.readInt(), i2);
               loadImagesOrAnimations(dChocByteArrayCreateByteArrayForReading, 3, imageArr, spriteObjectArr);
               int selectionEvent = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
               this.mFlowHandler
                  .menuSetItem(i, menuObject, i2, unsignedByte7, strProcessText2, spriteObjectArr[0], imageArr[0], selectionEvent == 255 ? -1 : selectionEvent);
               break;
            case 1:
               String strProcessText3 = this.mFlowHandler.processText(i, dChocByteArrayCreateByteArrayForReading.readInt(), i2);
               loadImagesOrAnimations(dChocByteArrayCreateByteArrayForReading, 4, imageArr, spriteObjectArr);
               Image[] imageArr2 = imageArr[0];
               SpriteObject spriteObject2 = spriteObjectArr[0];
               int unsignedByte8 = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
               String[] strArr = new String[unsignedByte8];

               for (int i3 = 0; i3 < unsignedByte8; i3++) {
                  strArr[i3] = this.mFlowHandler.processText(i, dChocByteArrayCreateByteArrayForReading.readInt(), i2);
               }

               loadImagesOrAnimations(dChocByteArrayCreateByteArrayForReading, unsignedByte8, imageArr, spriteObjectArr);
               int switchEvent = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
               this.mFlowHandler
                  .menuSetSwitchItem(
                     i,
                     menuObject,
                     i2,
                     strProcessText3,
                     spriteObject2,
                     imageArr2,
                     strArr,
                     spriteObjectArr[0],
                     imageArr[0],
                     switchEvent == 255 ? -1 : switchEvent
                  );
               break;
            case 2:
               String sliderText = this.mFlowHandler.processText(i, dChocByteArrayCreateByteArrayForReading.readInt(), i2);
               loadImagesOrAnimations(dChocByteArrayCreateByteArrayForReading, 4, imageArr, spriteObjectArr);
               SpriteObject sliderSprite = spriteObjectArr[0];
               Image[] sliderImages = imageArr[0];
               int min = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
               int max = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
               int sliderEvent = dChocByteArrayCreateByteArrayForReading.readUnsignedByte();
               this.mFlowHandler.menuSetSliderItem(i, menuObject, i2, sliderText, sliderSprite, sliderImages, min, max, sliderEvent == 255 ? -1 : sliderEvent);
               break;
            case 3:
               dChocByteArrayCreateByteArrayForReading.skip(26);
               break;
            case 4:
               String strProcessText4 = this.mFlowHandler.processText(i, dChocByteArrayCreateByteArrayForReading.readInt(), i2);
               loadImagesOrAnimations(dChocByteArrayCreateByteArrayForReading, 1, imageArr, spriteObjectArr);
               this.mFlowHandler.menuSetItem(i, menuObject, i2, 1, strProcessText4, spriteObjectArr[0], imageArr[0], -1);
         }
      }

      this.mFlowHandler.processMenu(i, menuObject, 1);
      this.mFlowHandler.menuSetSize(i, menuObject);
      menuObject.setSelectedItem(0);
      this.mLoadedMenus[i] = menuObject;
      if (vector.size() > 0) {
         short[] sArr = new short[vector.size()];
         int i4 = 0;

         while (true) {
            int i5 = i4;
            if (i5 >= vector.size()) {
               this.mMenuSoftkeyEvents[i] = sArr;
               return;
            }

            sArr[i5] = (short)((Integer)vector.elementAt(i5)).intValue();
            i4 = i5 + 1;
         }
      }
   }

   private boolean loadingScreenNeeded() {
      return this.mMaxLoadingCount > 0;
   }

   private void processBranch(int i) {
      if (this.mFlowHandler.evaluateBranchCondition(i)) {
         this.processEvent(this.mCurrentBranch[0]);
      } else {
         this.processEvent(this.mCurrentBranch[1]);
      }
   }

   private void processEvent(int i) {
      short s = this.mStateHistoryPointer > 0 ? this.mStateHistory[this.mStateHistoryPointer - 1] : -1;
      if (i != -1) {
         if (i == -2) {
            if (this.mCurrentGameScreen != null && this.mCurrentGameScreen.length > 0) {
               this.processEvent(this.mCurrentGameScreen[0]);
            }
         } else if (i == -3) {
            this.mGoingForward = false;
            this.mNextState = s;
            this.mStateHistoryPointer--;
         } else {
            if (i >= 0) {
               short s2 = this.mEventTargets[i];
               if (s2 != -1) {
                  this.mGoingForward = true;
                  this.mNextState = s2;
                  if (this.isMenuState(this.mCurrentState)) {
                     this.mMenuSelectedItems[this.mCurrentState] = (byte)this.mCurrentMenu.getSelectedItem();
                  }

                  if (s == s2) {
                     this.mGoingForward = false;
                     this.mStateHistoryPointer--;
                  } else if (this.mStoreStateToHistory) {
                     short[] sArr = this.mStateHistory;
                     int i2 = this.mStateHistoryPointer++;
                     sArr[i2] = (short)this.mCurrentState;
                  }
               }

               this.mFlowHandler.eventOccurred(this.mCurrentState, i);
            }
         }
      }
   }

   private void processSoftkeyEvent(int i) {
      if (i != -1 && Toolkit.getSoftKeyType(i) == 2) {
         this.processEvent(-3);
      }
   }

   private void releaseCurrentState() {
      if (this.mReloadStateGroup) {
         this.mReloadStateGroup = false;
         int i = this.mMenuScreenCount;

         while (--i >= 0) {
            this.removeFromMenuCache(i);
         }

         smMenuResourceHashtable = null;
         this.mCurrentMenu = null;
      }

      byte b = this.mStateGroups[this.mNextState];
      byte b2 = this.mCurrentState != -1 ? this.mStateGroups[this.mCurrentState] : -1;
      if (b2 != -1) {
         if (b2 != b && !this.mFlowHandler.isChildGroup(b2, b)) {
            int length = this.mStateGroupInMemory.length;

            while (--length >= 0) {
               if (this.mStateGroupInMemory[length] && length != b) {
                  this.mFlowHandler.unloadStateGroup(length, b);
                  int length2 = this.mStateGroups.length;

                  while (--length2 >= 0) {
                     if (this.mStateGroups[length2] == length && this.isMenuState(length2)) {
                        this.removeFromMenuCache(length2);
                     }
                  }

                  this.mStateGroupInMemory[length] = false;
               }
            }
         }
      } else if (this.isMenuState(this.mCurrentState)) {
         this.removeFromMenuCache(this.mCurrentState);
      }

      this.mFlowHandler.unloadState(this.mCurrentState, this.mNextState);
      this.deactivateState();
   }

   private void removeFromMenuCache(int i) {
      if (this.mLoadedMenus[i] != null) {
         this.mLoadedMenus[i].releaseScreen();
         this.mLoadedMenus[i] = null;
      }
   }

   private void setFlowTransitionState(int i) {
      this.mTransitionTime = 0;
      this.mFlowTransitionState = i;
   }

   private void updateLoading() {
      if (this.mLoadingCounter >= this.mMaxLoadingCount) {
         if (this.isFinalLoadingStepDelayDone()) {
            this.activateNextTransitionState();
         }
      } else {
         int start = (int)System.currentTimeMillis();

         do {
            if (!this.mDavinciLoading) {
               if (this.mLoadingCounter < this.mStateGroupLoadingCount) {
                  this.mFlowHandler.loadStateGroup(this.mStateGroups[this.mNextState], this.mLoadingCounter);
               } else {
                  int step = this.mLoadingCounter - this.mStateGroupLoadingCount;
                  if (step < this.mStateLoadingCount) {
                     this.mFlowHandler.loadState(this.mNextState, step);
                  } else {
                     try {
                        this.loadMenu(this.mMenusToLoad[step - this.mStateLoadingCount]);
                     } catch (EOFException exception) {
                        System.err.println(exception);
                     }
                  }
               }
            }

            this.mLoadingCounter++;
         } while (this.mLoadingCounter < this.mMaxLoadingCount && (int)System.currentTimeMillis() - start < 50);
      }
   }

   public void controllerActivated() {
      this.mFlowHandler.controllerActivated();
   }

   public void doDraw(Graphics graphics) {
      if (this.isLoadingScreen()) {
         this.mFlowHandler.drawLoadingScreen(this.getLoadingPercentage());
      } else {
         this.mFlowHandler.doDraw(this.mCurrentState, graphics);
         if (this.mCurrentMenu != null) {
            this.mFlowHandler.drawMenu(this.mCurrentState, graphics, this.mCurrentMenu);
         }
      }
   }

   public void doPostDraw(Graphics graphics) {
      switch (this.mFlowTransitionState) {
         case 1:
         case 5:
            this.mFlowHandler
               .drawPostStateTransition(
                  this.mCurrentTransition, this.mTransitionStateA, this.mTransitionStateB, this.mTransitionTime, graphics, this.mCurrentMenu
               );
            break;
         case 2:
         case 4:
            this.mFlowHandler
               .drawPostLoadingScreenTransition(this.mCurrentTransition, this.mTransitionStateA, this.mTransitionStateB, this.mTransitionTime, graphics);
         case 3:
      }
   }

   public int getLoadingPercentage() {
      switch (this.mFlowTransitionState) {
         case 2:
            return 0;
         case 3:
            if (this.mInFinalLoadingStep) {
               return 100;
            }

            int normal = 100 - this.mDavinciLoadingPercentage;
            return this.mDavinciLoading
               ? normal + this.mDavinciLoadingPercentage * this.mLoadingCounter / this.mMaxLoadingCount
               : normal * this.mLoadingCounter / this.mMaxLoadingCount;
         case 4:
            return 100;
         default:
            return -1;
      }
   }

   public void keyEventOccurred(int i, int i2) {
      if (!this.ignoreControlEvents()) {
         if (this.mCurrentMenu != null) {
            this.mCurrentMenu.keyEventOccurred(i, i2);
         }

         if (this.mFlowHandler != null) {
            this.mFlowHandler.keyEventOccurred(this.mCurrentState, i, i2);
         }
      }
   }

   public void keyEventOccurred(int i, int i2, int i3, char[] cArr) {
      if (!this.ignoreControlEvents() && this.mFlowHandler != null) {
         this.mFlowHandler.keyEventOccurred(this.mCurrentState, i, i2, i3, cArr);
      }
   }

   public void languageChanged() {
      this.clearCache();
   }

   public void licenseManagerActivated() {
      this.mFlowHandler.licenseManagerActivated();
   }

   public void logicUpdate(int i) {
      switch (this.mFlowTransitionState) {
         case 0:
            if (this.mNextState != -1) {
               this.changeState();
               return;
            }
            break;
         case 3:
            this.updateLoading();
            return;
         default:
            if (this.mFlowTransitionState != 1 && this.mFlowTransitionState != 5) {
               this.mFlowHandler.updateLoadingScreenTransition(this.mCurrentTransition, this.mTransitionStateA, this.mTransitionStateB, i);
            } else {
               this.mFlowHandler.updateStateTransition(this.mCurrentTransition, this.mTransitionStateA, this.mTransitionStateB, i, this.mCurrentMenu);
            }

            this.mTransitionTime += i;
            if (this.mTransitionTime >= this.mTransitionDuration) {
               this.mTransitionTime = this.mTransitionDuration;
               this.activateNextTransitionState();
               return;
            }

            if (this.mFlowTransitionState != 5) {
               return;
            }
      }

      int i2 = 0;
      this.processEvent(this.mFlowHandler.logicUpdate(this.mCurrentState, i));
      if (this.mCurrentMenu != null) {
         int[] iArrLogicUpdate = this.mCurrentMenu.logicUpdate(i);
         this.mMenuSelectedItems[this.mCurrentState] = (byte)this.mCurrentMenu.getSelectedItem();
         if (iArrLogicUpdate != null) {
            if (iArrLogicUpdate[0] == 1) {
               this.processEvent(iArrLogicUpdate[1]);
               return;
            }

            if (iArrLogicUpdate[0] == 0) {
               short[] sArr = this.mMenuSoftkeyEvents[this.mCurrentState];
               boolean z;
               if (sArr != null) {
                  z = false;

                  while (i2 < sArr.length) {
                     int i3 = i2 + 1;
                     short s = sArr[i2];
                     i2 = i3 + 1;
                     short s2 = sArr[i3];
                     if (s == iArrLogicUpdate[1]) {
                        this.processEvent(s2);
                        z = true;
                     }
                  }
               } else {
                  z = false;
               }

               if (z) {
                  return;
               }

               this.processSoftkeyEvent(iArrLogicUpdate[1]);
            }
         }
      }
   }

   public void memoryWarning() {
      if (!this.isLoadingScreen()) {
         this.mFlowHandler.memoryWarning();
      }
   }

   public void pause() {
      if (!this.isLoadingScreen()) {
         if (this.mCurrentMenu != null) {
            this.mCurrentMenu.stopScrolling();
         }

         this.mFlowHandler.pauseGame();
      }
   }

   public void pointerEventOccurred(int i, int i2, int i3) {
      if (!this.ignoreControlEvents()) {
         if (this.mCurrentMenu != null) {
            this.mCurrentMenu.pointerEventOccurred(i, i2, i3);
         }

         if (this.mFlowHandler != null) {
            this.mFlowHandler.pointerEventOccurred(this.mCurrentState, i, i2, i3);
         }
      }
   }

   public void resume() {
      if (!this.isLoadingScreen()) {
         if (this.mCurrentMenu != null) {
            this.mCurrentMenu.stopScrolling();
         }

         this.mFlowHandler.resumeGame();
      }
   }

   public void screenSizeChanged() {
      this.clearCache();
      this.mFlowHandler.screenSizeChanged();
   }

   public void touchEventOccurred(int[][] iArr, int[] iArr2) {
      if (!this.ignoreControlEvents() && this.mFlowHandler != null) {
         this.mFlowHandler.touchEventOccurred(this.mCurrentState, iArr, iArr2);
      }
   }
}
