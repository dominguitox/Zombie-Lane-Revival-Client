package com.dchoc.hud;

import com.dchoc.idead.characters.NeighborCharacter;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.WindowTooltip;
import java.util.Vector;
import zlport.ios.Ios;

public class HUDGame extends HUDCollection {
   private HUDCounter mCashCounter;
   private HUDCounter mCoinsCounter;
   private HUDCounter mBricksCounter;
   private HUDCounter mFoodCounter;
   private HUDButton mButtonFreeEnergy;
   private HUDButton mButtonImpactVideo;
   private HUDButton mButtonImpactInfo;
   private HUDButton mButtonStarterPack;
   private HUDProgressbarEnergy mEnergyProgressbar;
   private HUDProgressbarExperience mExperienceProgressbar;
   private HUDCombo mComboMeter;
   private HUDCollectionBar mCollectionBar;
   private HUDQuickRipenTab mQuickRipenTab;
   private HUDMissions mMissions;
   private HUDFoldable mFoldable;
   private HUDPaperDoll mPaperDoll;
   private HUDFriendsPlace mFriendsPlace;
   private HUDEdit mEdit;
   private HUDCollection mAcceptHelp;
   private IsometricObject mSpeechBubbleObject;
   private int mSpeechBubbleDuration;
   private int mState;

   protected HUDGame() {
      int screenWidth = Toolkit.getScreenWidth();
      int screenHeight = Toolkit.getScreenHeight();
      this.mAcceptHelp = new HUDCollection();
      this.addObject(this.mAcceptHelp);
      this.mFoodCounter = new HUDCounter(2, 65565, 0, 0, (byte)0);
      this.mBricksCounter = new HUDCounter(3, 65551, 0, 0, (byte)1);
      this.mCashCounter = new HUDCounter(1, 65561, screenWidth, 0, (byte)6);
      boolean impact = Ios.platform().isImpactSupported();
      screenHeight >>= 1;
      if (impact) {
         this.mButtonImpactVideo = new HUDButton(56001, screenWidth, screenHeight, 66245, (byte)-1);
         this.mButtonImpactVideo.setText(PlayerProfile.getFlurryText());
         this.mButtonImpactInfo = new HUDButton(56002, screenWidth, screenHeight, 71585, (byte)-1);
         this.mButtonImpactVideo.setPosition(screenWidth - this.mButtonImpactVideo.getWidth(), screenHeight);
         int videoWidth = this.mButtonImpactVideo.getWidth();
         int infoWidth = this.mButtonImpactInfo.getWidth();
         int infoHeight = this.mButtonImpactInfo.getHeight();
         this.mButtonImpactInfo.setPosition(infoWidth + (screenWidth - videoWidth), infoHeight + screenHeight);
      }

      this.mCoinsCounter = new HUDCounter(0, 65553, screenWidth, 0, (byte)5);
      this.addObject(this.mFoodCounter);
      this.addObject(this.mBricksCounter);
      this.addObject(this.mCashCounter);
      if (Ios.platform().isImpactSupported()) {
         this.addObject(this.mButtonImpactVideo);
         this.mButtonImpactVideo.setVisible(true);
         this.addObject(this.mButtonImpactInfo);
         this.mButtonImpactInfo.setVisible(true);
      }

      this.addObject(this.mCoinsCounter);
      this.mButtonStarterPack = new HUDButton(56003, screenWidth, screenHeight, 80405);
      int starterWidth = this.mButtonStarterPack.getWidth();
      int videoHeight = this.mButtonImpactVideo.getHeight();
      this.mButtonStarterPack.setPosition(screenWidth - starterWidth, screenHeight - videoHeight);
      this.addObject(this.mButtonStarterPack);
      this.mButtonStarterPack.setVisible(true);
      this.mEnergyProgressbar = new HUDProgressbarEnergy(65557, 65569, 71585);
      this.mExperienceProgressbar = new HUDProgressbarExperience(65555, 65567, 65573);
      this.addObject(this.mEnergyProgressbar);
      this.addObject(this.mExperienceProgressbar);
      this.mComboMeter = new HUDCombo(Toolkit.getScreenWidth() >> 1, 0);
      this.addObject(this.mComboMeter);
      this.mCollectionBar = new HUDCollectionBar();
      this.addObject(this.mCollectionBar);
      this.mQuickRipenTab = new HUDQuickRipenTab();
      this.mQuickRipenTab.init(this.mEnergyProgressbar);
      this.addObject(this.mQuickRipenTab);
      this.mMissions = new HUDMissions();
      this.addObject(this.mMissions);
      this.mFoldable = new HUDFoldable();
      this.addObject(this.mFoldable);
      this.mPaperDoll = new HUDPaperDoll();
      this.addObject(this.mPaperDoll);
      this.mFriendsPlace = new HUDFriendsPlace();
      this.addObject(this.mFriendsPlace);
      this.mEdit = new HUDEdit();
      this.addObject(this.mEdit);
      this.mSpeechBubbleObject = null;
      this.mSpeechBubbleDuration = 0;
      this.mCashCounter.setAnimated(true);
      this.mCoinsCounter.setAnimated(true);
      this.mBricksCounter.setAnimated(true);
      this.mFoodCounter.setAnimated(true);
      this.changeState(0);
   }

   public int getState() {
      return this.mState;
   }

   public void changeState(int state) {
      this.setAllVisible(true);
      if (Integer.compareUnsigned(state - 3, 5) < 0) {
         this.mAcceptHelp.setVisible(false);
         this.mFoldable.setVisible(false);
         this.mFriendsPlace.setVisible(false);
         this.mPaperDoll.setVisible(false);
         this.mCashCounter.setVisible(state == 5);
         this.mCoinsCounter.setVisible(state == 5);
         this.mFoodCounter.setVisible(false);
         this.mBricksCounter.setVisible(false);
         this.mExperienceProgressbar.setVisible(false);
         this.mEnergyProgressbar.setVisible(false);
         this.mComboMeter.setVisible(false);
         this.mMissions.setVisible(false);
         this.cancelPlayerActions();
         this.mEdit.changeState(state);
      } else {
         if (Integer.compareUnsigned(state, 2) > 0) {
            this.mState = state;
            return;
         }

         this.mAcceptHelp.setVisible(state == 0);
         this.mFoldable.setVisible(Integer.compareUnsigned(state, 2) < 0);
         this.mFriendsPlace.setVisible(state == 2);
         this.mEdit.setVisible(false);
         if (state == 0) {
            this.mFoldable.setAsHome();
         } else if (state == 1) {
            this.mFoldable.setAsAdventureArea();
         }

         this.mEnergyProgressbar.setMode(Integer.compareUnsigned(state, 1) > 0 ? 1 : 0);
         IsometricUtils.getScene().changeState(0);
      }

      this.mState = state;
   }

   public void resetState() {
      if (Integer.compareUnsigned(this.mState, 3) >= 0) {
         if (this.mEdit.isVisible()) {
            this.mEdit.cancelCurrentState();
         }

         WindowManager.forceCloseWindow();
         HUD.changeState(1);
      }
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      if (-1 < this.mSpeechBubbleDuration) {
         this.mSpeechBubbleDuration -= delta;
         if (this.mSpeechBubbleDuration < 0) {
            WindowTooltip tooltip = HUD.getTooltip(this.mSpeechBubbleObject);
            if (tooltip != null) {
               tooltip.closeTooltip(true);
            }
         }
      }

      this.mExperienceProgressbar.setValue(PlayerProfile.getXp());
      this.mPaperDoll.logicUpdate(delta);
      if (PlayerProfile.isTutorialComplete() && 2 < PlayerProfile.getLevel() && PlayerProfile.getLevel() < 16 && !PlayerProfile.isStarterPack1Purchased()) {
         this.mButtonStarterPack.setVisible(true);
      } else {
         this.mButtonStarterPack.setVisible(false);
      }
   }

   public void showSpeechBubble(IsometricObject target, int text, int duration) {
      if (target != null) {
         this.mSpeechBubbleObject = target;
         HUD.initSceneTooltip(target, HUD.getNextTooltip(), null, 0, text, null);
         this.mSpeechBubbleDuration = duration;
      }
   }

   public void addAcceptHelp(NeighborCharacter neighbor) {
      if (neighbor != null) {
         Vector objects = this.mAcceptHelp.getObjects();
         int i = 0;

         NeighborCharacter current;
         do {
            if (objects.size() <= i) {
               HUDAcceptHelp help = new HUDAcceptHelp();
               help.init();
               help.setNeighbor(neighbor);
               this.mAcceptHelp.addObject(help);
               return;
            }

            current = ((HUDAccept)objects.elementAt(i)).getNeighbor();
            i++;
         } while (current != neighbor);
      }
   }

   public void removeAcceptHelp(NeighborProfile profile) {
      Vector objects = this.mAcceptHelp.getObjects();
      int i = -1;

      while (objects.size() > ++i) {
         NeighborProfile current = ((HUDAccept)objects.elementAt(i)).getNeighbor().getProfile();
         if (current == profile) {
            objects.removeElementAt(i);
            return;
         }
      }
   }

   public void removeAcceptHelps() {
      this.mAcceptHelp.removeObjects();
   }

   public HUDMissions getMissions() {
      return this.mMissions;
   }

   public HUDCollectionBar getCollectionBar() {
      return this.mCollectionBar;
   }

   public HUDCombo getCombo() {
      return this.mComboMeter;
   }

   public HUDFoldable getFoldable() {
      return this.mFoldable;
   }

   public HUDFriendsPlace getFriendsPlace() {
      return this.mFriendsPlace;
   }

   public HUDProgressbarEnergy getEnergyProgressbar() {
      return this.mEnergyProgressbar;
   }

   public HUDProgressbarExperience getXPProgressbar() {
      return this.mExperienceProgressbar;
   }

   public HUDCounter getCoinsCounter() {
      return this.mCoinsCounter;
   }

   public HUDCounter getBricksCounter() {
      return this.mBricksCounter;
   }

   public HUDCounter getFoodCounter() {
      return this.mFoodCounter;
   }

   public HUDEdit getEdit() {
      return this.mEdit;
   }

   public HUDPaperDoll getPaperDoll() {
      return this.mPaperDoll;
   }

   public HUDCounter getCashCounter() {
      return this.mCashCounter;
   }

   public void openQuickRipenTab(IsometricObject target) {
      this.mQuickRipenTab.setObject(target);
   }

   public HUDQuickRipenTab getQuickRipenTab() {
      return this.mQuickRipenTab;
   }

   public void cancelPlayerActions() {
      PlayerCharacter player = IsometricUtils.getPlayer();
      if (player != null) {
         player.cancelActions(true);
      }
   }

   @Override
   public void callback(int id) {
      if (id == 56003) {
         WindowManager.openWindow((byte)35);
      } else {
         if (id == 56001) {
            if (!Ios.platform().showImpactVideoAds()) {
               return;
            }

            Toolkit.stopMusic();
            Toolkit.stopSoundEffect();
         }
      }
   }
}
