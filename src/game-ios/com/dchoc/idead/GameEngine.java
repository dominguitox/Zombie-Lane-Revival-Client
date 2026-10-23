package com.dchoc.idead;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUD;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.actions.ActionsManager;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.areas.Area;
import com.dchoc.idead.areas.AreaCircus;
import com.dchoc.idead.areas.AreaCrashsite;
import com.dchoc.idead.areas.AreaCure;
import com.dchoc.idead.areas.AreaDowntown;
import com.dchoc.idead.areas.AreaHome;
import com.dchoc.idead.areas.AreaNeighbor;
import com.dchoc.idead.areas.AreaRob;
import com.dchoc.idead.events.EventManager;
import com.dchoc.idead.events.TimedEvent;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.SceneLoader;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.notifications.NotificationManager;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.player.Profile;
import com.dchoc.idead.servlets.CreateNewSession;
import com.dchoc.idead.servlets.GetAccountInformation;
import com.dchoc.idead.servlets.GetCustomMissions;
import com.dchoc.idead.servlets.GetSessionStatus;
import com.dchoc.idead.servlets.GetTimers;
import com.dchoc.idead.servlets.ServletsManager;
import com.dchoc.idead.servlets.SetOption;
import com.dchoc.idead.servlets.UpdateAccountInformation;
import com.dchoc.idead.social.SocialFacebook;
import com.dchoc.idead.social.SocialGameCenter;
import com.dchoc.idead.social.SocialTwitter;
import com.dchoc.idead.spawning.SpawningManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.touch.TouchManager;
import com.dchoc.idead.tracking.ABTestManager;
import com.dchoc.idead.tracking.CrashedEvent;
import com.dchoc.idead.tracking.LoadingTimeEvent;
import com.dchoc.idead.tracking.SessionStartEvent;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.iphonewrappers.ActivityWrapper;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.iphonewrappers.MovieWrapper;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.opengl.OGL;
import com.dchoc.playhaven.PlayHaven;
import com.dchoc.spriteobject.ServerAssetLoader;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.Window;
import com.dchoc.windows.WindowNpcDialog;
import com.dchoc.windows.WindowSocial;
import com.dchoc.windows.WindowTraveling;
import zlport.ios.Ios;

public class GameEngine {
   public static final int LOADING_COUNT = 100;
   public static final String RMS_PLAYER_PROFILE = "RMS_PROFILE";
   public static final String RMS_HOME = "RMS_GAMEFIELD";
   public static final String RMS_CIRCUS = "RMS_CIRCUS";
   public static final String RMS_DOWNTOWN = "RMS_DOWNTOWN";
   public static final String RMS_CRASHSITE = "RMS_CRASHSITE";
   public static final String RMS_CURE = "RMS_CURE";
   public static final String RMS_MISSIONS = "RMS_MISSIONS";
   public static final String RMS_AB_TEST = "RMS_AB_TEST";
   public static final String RMS_JAILBREAK = "RMS_JAILBREAK";
   public static final String RMS_ROBSPLACE = "RMS_ROBSPLACE";
   public static final String RMS_BACKUP_PLAYER_PROFILE = "RMS_PROFILE_BACKUP";
   public static final String RMS_BACKUP_HOME = "RMS_GAMEFIELD_BACKUP";
   public static final String RMS_BACKUP_CIRCUS = "RMS_CIRCUS_BACKUP";
   public static final String RMS_BACKUP_DOWNTOWN = "RMS_DOWNTOWN_BACKUP";
   public static final String RMS_BACKUP_CRASHSITE = "RMS_CRASHSITE_BACKUP";
   public static final String RMS_BACKUP_CURE = "RMS_CURE_BACKUP";
   public static final String RMS_BACKUP_MISSIONS = "RMS_MISSIONS_BACKUP";
   private static final int ACCOUNT_SYNC_INTERVAL_MS = 15000;
   private static GameEngine smInstance;
   private IsometricScene mScene;
   private short mIntroDelayFrames;
   private Game mGame;
   private AreaHome mHome;
   private AreaCircus mCircus;
   private AreaDowntown mDowntown;
   private AreaCrashsite mCrashsite;
   private AreaCure mCure;
   private AreaRob mRob;
   private AreaNeighbor mNeighborArea;
   private Area mCurrentArea;
   private Area mNextArea;
   private Area mTravelArea;
   private NeighborProfile mTravelNeighbor;
   private int mLoadingStep;
   private int mState;
   private int mPreviousState;
   private TouchManager mTouchManager;
   private int mTimer;
   private NeighborProfile mNeighbor;
   private SocialFacebook mFacebook;
   private SocialGameCenter mGameCenter;
   private SocialTwitter mTwitter;
   private CreateNewSession mCreateNewSessionServlet;
   private GetAccountInformation mGetAccountInformationServlet;
   private GetCustomMissions mGetCustomMissionsServlet;
   private GetSessionStatus mGetSessionStatusServlet;
   private UpdateAccountInformation mUpdateAccountInformationServlet;
   private GetTimers mGetTimersServlet;
   private SetOption mSetOptionServlet;
   private long mResumeTimestamp;
   private boolean mUpdatePaymentManager;
   private boolean mSaveGameState;
   private boolean mGetAccountInformation;
   private boolean mGetTimers;
   private boolean mGetCustomMissions;
   private boolean mGetSessionStatus;
   private boolean mServerReachable;
   private boolean mSessionStatusUnsupported;
   private boolean mCreateNewSession;
   private boolean mSetOption;
   private boolean mUpdateAccountInformation;
   private boolean mConnectGameCenter;
   private boolean mTransactionActive;
   private int mInGameMarketMode;
   private int mRetryTimer;
   private int mMemoryTimer;
   private int mAccountSyncTimer;
   private String mSessionID;
   private boolean mRunGarbageCollection;
   private boolean mVibrateAfterLoading;
   private boolean mFirstAreaVisit;
   private int mIntroTransitionTimer;
   private boolean mQuickDraw;
   private int mSessionPollTimer;
   private LoadingTimeEvent mLoadingTimeEvent;

   public GameEngine(Game game) {
      this.mGame = game;
      this.mResumeTimestamp = Time.getTimestamp();
      smInstance = this;
      this.mTouchManager = new TouchManager();
      this.mScene = new IsometricScene();
      Transform.init();
      ServletsManager.init();
      NotificationManager.init();
      ActionsManager.init();
      SceneLoader.init();
      EventManager.init();
      this.mTimer = 0;
      this.mQuickDraw = false;
      this.mSaveGameState = false;
      this.mGetAccountInformation = false;
      this.mGetTimers = false;
      this.mGetCustomMissions = false;
      this.mGetSessionStatus = false;
      this.mServerReachable = true;
      this.mSessionStatusUnsupported = false;
      this.mSessionPollTimer = 0;
      this.mUpdateAccountInformation = false;
      this.mSetOption = false;
      this.mConnectGameCenter = true;
      this.mCreateNewSession = false;
      this.mTransactionActive = false;
      this.mRetryTimer = 0;
      this.mMemoryTimer = 0;
      this.mAccountSyncTimer = 0;
      this.mSessionID = null;
      this.mRunGarbageCollection = false;
      this.mVibrateAfterLoading = false;
      this.mFirstAreaVisit = true;
      this.mCurrentArea = null;
      this.mNextArea = null;
      this.mGetAccountInformationServlet = new GetAccountInformation();
      this.mCreateNewSessionServlet = new CreateNewSession();
      this.mGetCustomMissionsServlet = new GetCustomMissions();
      this.mGetSessionStatusServlet = new GetSessionStatus();
      this.mSetOptionServlet = new SetOption();
      this.mGetTimersServlet = new GetTimers();
      this.mUpdateAccountInformationServlet = new UpdateAccountInformation();
      this.mLoadingTimeEvent = new LoadingTimeEvent();
      this.changeState(0);
   }

   public static GameEngine getInstance() {
      return smInstance;
   }

   public void load(int step) {
      switch (step) {
         case 0:
            this.mLoadingTimeEvent.startLoadingTime();
            ActivityWrapper.show(Toolkit.getScreenWidth() >> 1, Toolkit.getScreenHeight() >> 1);
            Tuner.init();
            break;
         case 10:
            this.mFacebook = new SocialFacebook();
            this.mGameCenter = new SocialGameCenter();
            this.mTwitter = new SocialTwitter();
            break;
         case 20:
            AnimationsManager.init();
            break;
         case 30:
            ItemManager.init();
            break;
         case 50:
            PlayerProfile.init();
            ABTestManager.load();
            this.mCreateNewSessionServlet.init();
            this.mCreateNewSessionServlet.execute();
            this.mScene.init();
            break;
         case 60:
            SpawningManager.init();
            MissionManager.init();
            break;
         case 70:
            HUD.init();
            break;
         case 80:
            this.mHome = new AreaHome("RMS_GAMEFIELD", "RMS_GAMEFIELD_BACKUP");
            this.mCircus = new AreaCircus("RMS_CIRCUS", "RMS_CIRCUS_BACKUP");
            this.mDowntown = new AreaDowntown("RMS_DOWNTOWN", "RMS_DOWNTOWN_BACKUP");
            this.mCure = new AreaCure("RMS_CURE", "RMS_CURE_BACKUP");
            this.mCrashsite = new AreaCrashsite("RMS_CRASHSITE", "RMS_CRASHSITE_BACKUP");
            this.mRob = new AreaRob("RMS_ROBSPLACE");
            this.mNeighborArea = new AreaNeighbor();
            this.mCurrentArea = this.mHome;
            this.mHome.enter();
            this.mCurrentArea.loadData(true);
            break;
         case 85:
            HUD.initializePayments();
            this.mUpdatePaymentManager = true;
            break;
         case 90:
            TutorialFlow.init(this.mScene);
            this.mRob.tryToUnlock();
            WindowNpcDialog.load();
            this.setGetSessionStatus();
            break;
         case 99:
            new SessionStartEvent().trackEvent();
            if (this.mGame.crashedEnabled()) {
               new CrashedEvent().trackEvent();
            } else {
               this.mGame.setCrashed(true);
            }

            ActivityWrapper.startFrameTimer();
            if (this.mGame.isShowIntro()) {
               this.changeState(8);
            } else {
               this.mLoadingTimeEvent.trackInitialLoadingTime();
               this.changeState(2);
            }
      }

      this.mLoadingStep = step;
   }

   public void travelToHome() {
      this.saveAndTravelToArea(this.mHome, null);
   }

   public void travelToCircus() {
      this.saveAndTravelToArea(this.mCircus, null);
   }

   public void travelToDowntown() {
      this.saveAndTravelToArea(this.mDowntown, null);
   }

   public void travelToCrashSite() {
      this.saveAndTravelToArea(this.mCrashsite, null);
   }

   public void travelToCure() {
      this.saveAndTravelToArea(this.mCure, null);
   }

   public void travelToNeighbor(NeighborProfile neighbor) {
      if (neighbor != null) {
         this.saveAndTravelToArea(neighbor.getType() == 2514 ? this.mRob : this.mNeighborArea, neighbor);
      }
   }

   public void travelToArea(Area area, NeighborProfile neighbor) {
      if (area != null) {
         HUD.HIDE_INGAME = true;
         if (!WindowManager.isWindowOpened((byte)24)) {
            WindowManager.closeWindow();
         }

         ((WindowTraveling)WindowManager.getWindow((byte)24)).setArea(area, neighbor);
         WindowManager.openWindow((byte)24);
      }
   }

   public void saveAndTravelToArea(Area area, NeighborProfile neighbor) {
      this.mTravelArea = area;
      this.mTravelNeighbor = neighbor;
      this.changeState(9);
   }

   public void loadArea(Area area, NeighborProfile neighbor) {
      if (area != null) {
         this.mNextArea = area;
         this.mNeighbor = neighbor;
      }
   }

   public Area getArea() {
      if (this.mState == 1) {
         return this.mNextArea != null ? this.mNextArea : this.mCurrentArea;
      } else {
         return this.mCurrentArea;
      }
   }

   public Area getArea(int name) {
      switch (name) {
         case 3818:
            return this.mCircus;
         case 4054:
            return this.mDowntown;
         case 4745:
            return this.mCrashsite;
         case 5560:
            return this.mCure;
         default:
            return this.mCurrentArea;
      }
   }

   public int logicUpdate(int dt) {
      this.updateFps(dt);
      this.mTimer += dt;
      ActivityWrapper.updateFrameTime();
      iWrapper.trySwitchMusic();
      if (this.mSaveGameState) {
         this.saveGameState();
         this.mSaveGameState = false;
         DCiDead.setActivityState(-1, false);
      }

      label216: {
         switch (this.mState) {
            case 0:
               int step = this.mLoadingStep++;
               this.load(step);
               return -1;
            case 1:
               if (this.mNextArea == this.mNeighborArea && !this.mNeighborArea.isDataLoaded()) {
                  ServletsManager.logicUpdate(dt);
                  return -1;
               }

               boolean loaded = this.mNextArea.loadData(false);
               if (this.mCurrentArea != null) {
                  this.mCurrentArea.exit();
               }

               Area area = null;
               if (this.mNextArea != null) {
                  this.mNextArea.enter();
                  area = this.mNextArea;
               }

               this.mCurrentArea = area;
               this.mNextArea = null;
               if (!loaded && area != this.mHome) {
                  EventManager.addEvent(new TimedEvent(150, 1, null));
                  this.loadArea(this.mHome, null);
                  return -1;
               }

               int type = area.getType();
               this.changeState(type == 1 ? 6 : (type == 2 ? 4 : 2));
               return -1;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            default:
               break;
            case 7:
               if (MovieWrapper.isMovieFinished()) {
                  this.mGame.setShowIntro(false);
                  this.changeState(2);
                  this.mIntroTransitionTimer = 3000;
                  TutorialFlow.changeState(0);
                  return -1;
               }
               break;
            case 8:
               this.mIntroDelayFrames++;
               if (this.mIntroDelayFrames <= 9) {
                  return -1;
               }

               this.changeState(this.mGame.isShowIntro() ? 7 : 2);
               this.mLoadingTimeEvent.trackInitialLoadingTime();
               return -1;
            case 9:
               if (this.mCurrentArea == null || !this.mCurrentArea.isLoadSucceed()) {
                  break label216;
               }

               if (DCiDead.getActivityState() == -1) {
                  this.setSaveGameState(false);
                  return -1;
               }

               if (DCiDead.getActivityState() == 1) {
                  this.savePlayerProfile();
                  this.mCurrentArea.saveData();
                  DCiDead.setActivityState(-1, false);
                  break label216;
               }
         }

         if (this.mIntroTransitionTimer >= 1 && this.mState != 7) {
            this.mIntroTransitionTimer -= dt;
         }

         this.mMemoryTimer += dt;
         this.mAccountSyncTimer += dt;
         if (this.mAccountSyncTimer >= ACCOUNT_SYNC_INTERVAL_MS) {
            this.mAccountSyncTimer = 0;
            this.syncAccountSave();
         }
         DCiDead.freeLoadingScreen();
         this.mTouchManager.logicUpdate(dt);
         this.mFacebook.logicUpdate(dt);
         this.mGameCenter.logicUpdate(dt);
         this.mTwitter.logicUpdate(dt);
         if (DCiDead.getActivityState() != 0 && this.mTouchManager.hasEvents()) {
            do {
               TouchEvent event = this.mTouchManager.popEvent();
               if (!event.isConsumed()) {
                  HUD.pointerEvent(event);
               }

               if (HUD.isSceneActive() && !event.isConsumed()) {
                  this.mScene.processTouchEvent(event);
               }

               this.mTouchManager.pushEvent(event);
            } while (this.mTouchManager.hasEvents());
         }

         ServletsManager.logicUpdate(dt);
         PlayerProfile.logicUpdate(dt);
         NotificationManager.logicUpdate(dt);
         if (this.mState == 2 || this.mState == 4 || this.mState == 6) {
            this.mSessionPollTimer += dt;
            if (this.mSessionPollTimer >= 3000) {
               this.mSessionPollTimer = 0;
               this.setGetSessionStatus();
            }
         }
         if (DCiDead.getActivityState() != 0) {
            AnimationsManager.logicUpdate(dt);
         }

         if (HUD.isSceneActive() && DCiDead.getActivityState() != 0) {
            MissionManager.logicUpdate(dt);
            EventManager.logicUpdate(dt);
            SpawningManager.logicUpdate(dt);
            this.mScene.logicUpdate(dt);
         }

         if (TutorialFlow.isCompleted()) {
            if (this.mConnectGameCenter) {
               this.mGameCenter.connect();
               this.mConnectGameCenter = false;
            }
         } else if (DCiDead.getActivityState() != 0) {
            TutorialFlow.logicUpdate(dt);
         }

         if (DCiDead.getActivityState() != 0) {
            HUD.logicUpdate(dt);
            Window social = WindowManager.getWindow((byte)18);
            if (social != null) {
               ((WindowSocial)social).updateApplicationFriends(dt);
            }
         }

         if (this.mCreateNewSession && !this.mCreateNewSessionServlet.isWaitingForResponse()) {
            this.mCreateNewSessionServlet.init();
            this.mCreateNewSessionServlet.execute();
            this.mCreateNewSession = false;
         } else if (this.mGetAccountInformation && !this.mGetAccountInformationServlet.isWaitingForResponse()) {
            ServerAssetLoader.resumeLoading();
            this.mGetAccountInformationServlet.init();
            this.mGetAccountInformationServlet.execute();
            this.mGetAccountInformation = false;
         } else if (this.mGetSessionStatus && !this.mGetSessionStatusServlet.isWaitingForResponse()) {
            this.mGetSessionStatusServlet.init();
            this.mGetSessionStatusServlet.execute();
            this.mGetSessionStatus = false;
         } else if (this.mGetCustomMissions && !this.mGetCustomMissionsServlet.isWaitingForResponse()) {
            this.mGetCustomMissionsServlet.init();
            this.mGetCustomMissionsServlet.execute();
            this.mGetCustomMissions = false;
         } else if (this.mGetTimers && !this.mGetTimersServlet.isWaitingForResponse()) {
            this.mGetTimersServlet.init();
            this.mGetTimersServlet.execute();
            this.mGetTimers = false;
            if (DCiDead.getActivityState() == 0) {
               DCiDead.setActivityState(-1, false);
            }
         } else if (this.mSetOption && !this.mSetOptionServlet.isWaitingForResponse()) {
            this.mSetOptionServlet.init();
            this.mSetOptionServlet.execute();
            this.mSetOption = false;
         } else if (!this.isConnected() && this.mState != 0) {
            this.mRetryTimer += dt;
            if (this.mRetryTimer > 45000 && !this.mCreateNewSessionServlet.isWaitingForResponse()) {
               this.mCreateNewSessionServlet.init();
               this.mCreateNewSessionServlet.execute();
               this.mRetryTimer = 0;
            }

            if (DCiDead.getActivityState() == 0 && this.mRetryTimer > 10000) {
               DCiDead.setActivityState(-1, false);
            }
         } else if (this.mUpdateAccountInformation
            && this.mUpdateAccountInformationServlet.isExecutable()
            && !this.mUpdateAccountInformationServlet.isWaitingForResponse()) {
            this.mUpdateAccountInformationServlet.init();
            this.mUpdateAccountInformationServlet.execute();
            this.mUpdateAccountInformation = false;
         }

         if (Integer.compareUnsigned(this.mState, 6) <= 0
            && (1 << this.mState & 84) != 0
            && DCiDead.getActivityState() == -1
            && !ActivityWrapper.isFrameUpdateIndicatorVisible()) {
            if (this.mLoadingTimeEvent.isTrackResumeLoading()) {
               this.mLoadingTimeEvent.trackResumeLoadingTime();
            }

            if (this.mVibrateAfterLoading) {
               this.mVibrateAfterLoading = false;
               Toolkit.vibrate(500);
            }
         }

         return -1;
      }

      this.travelToArea(this.mTravelArea, this.mTravelNeighbor);
      this.changeState(this.mPreviousState);
      return -1;
   }

   public void changeState(int state) {
      if (this.mState != state) {
         if (this.mState == 2) {
            NotificationManager.createSceneNotifications();
         }

         iWrapper.enableSoundFX(Integer.compareUnsigned(state, 6) <= 0 && (1 << state & 84) != 0);
         if (Integer.compareUnsigned(state, 7) > 0 || (1 << state & 212) == 0) {
            ActivityWrapper.show(Toolkit.getScreenWidth() >> 1, Toolkit.getScreenHeight() >> 1);
         } else if (!WindowManager.isWindowOpened((byte)24)) {
            ActivityWrapper.hide();
         }

         this.mTouchManager.setIgnoreTouchEvents(Integer.compareUnsigned(state, 2) < 0 || state == 9);
         switch (state) {
            case 0:
               this.mLoadingStep = 0;
               break;
            case 1:
               this.mVibrateAfterLoading = true;
               this.mLoadingTimeEvent.startLoadingTime();
               HUD.closeTooltips(false);
               HUD.closeHealthBars(true, false);
               if (this.mNextArea == this.mNeighborArea) {
                  this.mNeighborArea.startLoadingData(this.mNeighbor);
               }
               break;
            case 2:
            case 4:
            case 6:
               HUD.changeState(state == 2 ? 1 : (state == 4 ? 3 : 2));
               if (GameEngineUtils.getAreaMusic() != iWrapper.getMusic()) {
                  iWrapper.switchMusic(GameEngineUtils.getAreaMusic());
               }

               this.mScene.getFloor().updateEdges();
               if (state == 2) {
                  this.mScene.setUpdateNeighbors();
               }

               if (this.mState == 1) {
                  this.mLoadingTimeEvent.trackAreaLoadingTime();
               }

               if (state == 4 || state == 6) {
                  if (this.mFirstAreaVisit) {
                     PlayHaven.triggerLoad();
                  }

                  this.mFirstAreaVisit = false;
               }

               if (state == 2) {
                  if (this.mState != 9) {
                     PlayHaven.triggerReturnHome();
                  }

                  this.mFirstAreaVisit = false;
               }
            case 3:
            case 5:
            case 8:
            default:
               break;
            case 7:
               this.mIntroTransitionTimer = 3000;
               MovieWrapper.playMovie("intro_iphone.mov");
               break;
            case 9:
               this.mPreviousState = this.mState;
               HUD.closeTooltips(false);
               HUD.closeHealthBars(true, false);
         }

         this.mState = state;
         if (this.mRunGarbageCollection) {
            System.gc();
         }

         this.mRunGarbageCollection = state != 8 && state != 7;
      }
   }

   public boolean isVisitingNeighbor() {
      return this.mState == 4 || this.mState == 1 && this.mNextArea.getType() == 2;
   }

   public boolean isVisitingAdventureArea() {
      return this.mState == 6 || this.mState == 1 && this.mNextArea.getType() == 1;
   }

   public void doDraw() {
      if (Integer.compareUnsigned(this.mState, 2) >= 0) {
         if (this.mState == 7) {
            this.drawIntroTransition();
         } else if (this.mState == 8) {
            DCiDead.drawLoadingScreen(100);
            return;
         }

         if (this.mQuickDraw) {
            this.mQuickDraw = false;
         } else {
            this.mScene.doDraw();
            HUD.drawHUD();
            this.doPostDraw();
            if (this.mLoadingStep > 98 && TutorialFlow.isCompleted() && this.mState == 2 && WindowManager.isEmpty()) {
               Ios.platform().displayCustomizerPopup();
            }
         }
      }
   }

   public void doPostDraw() {
      Toolkit.getScreenWidth();
      Toolkit.getScreenHeight();
      this.mScene.doPostDraw();
      this.drawIntroTransition();
   }

   public void keyEventOccurred(int p0, int p1, int p2) {
      Toolkit.getToolkitGameAction(p1);
      Window social = WindowManager.getWindow((byte)18);
      if (social != null) {
         ((WindowSocial)social).keyEventOccured(p0, p1, p2);
      }
   }

   public void touchEventOccurred(int[][] events, int[] unused) {
      this.mTouchManager.touchEventOccurred(events, unused);
   }

   public void pauseGame() {
      iWrapper.stopMusic();
      if (this.isLoaded() && !this.isTransactionActive()) {
         HUD.resetState();
         this.setSaveGameState(true);
         NotificationManager.createNotifications();
      }

      this.mQuickDraw = true;
      this.mGame.setCrashed(false);
      ActivityWrapper.stopFrameTimer();
   }

   public void resumeGame() {
      this.mResumeTimestamp = Time.getTimestamp();
      this.mLoadingTimeEvent.startLoadingTime();
      this.mLoadingTimeEvent.enableTrackResumeLoading();
      boolean transaction = this.isTransactionActive();
      this.mTransactionActive = false;
      NotificationManager.removeNotifications();
      this.mGame.setCrashed(true);
      if (this.mState != 0) {
         if (this.mState == 7) {
            MovieWrapper.resumeMovie();
         } else {
            iWrapper.playMusic(GameEngineUtils.getAreaMusic());
         }
      }

      if (!transaction) {
         this.setCreateNewSession();
         DCiDead.setActivityState(0, true);
      }

      ActivityWrapper.startFrameTimer();
      if (this.isLoaded()) {
         PlayerProfile.setResumeCount(PlayerProfile.getResumeCount() + 1);
         PlayHaven.triggerSession();
         PlayHaven.triggerResume();
      }

      this.mVibrateAfterLoading = true;
   }

   public void memoryWarning() {
      if (this.mMemoryTimer > 150000) {
         this.setSaveGameState(false);
         this.mMemoryTimer = 0;
      }

      this.mScene.setClearPool();
   }

   public void setNeighbor(NeighborProfile neighbor) {
      this.mNeighbor = neighbor;
   }

   public NeighborProfile getNeighbor() {
      return this.mNeighbor;
   }

   public int getState() {
      return this.mState;
   }

   public int getLoadingCount() {
      return 100;
   }

   public int getLoadingStep() {
      return this.mLoadingStep;
   }

   public IsometricScene getScene() {
      return this.mScene;
   }

   public AreaHome getHome() {
      return this.mHome;
   }

   public AreaRob getRob() {
      return this.mRob;
   }

   public SocialFacebook getFacebook() {
      return this.mFacebook;
   }

   public SocialGameCenter getGameCenter() {
      return this.mGameCenter;
   }

   public SocialTwitter getTwitter() {
      return this.mTwitter;
   }

   public void setSessionID(String sessionID) {
      this.mSessionID = sessionID;
   }

   public String getSessionID() {
      return this.mSessionID;
   }

   public boolean isConnected() {
      if (!PlayerProfile.isInitialized()) {
         return false;
      }

      Profile profile = PlayerProfile.getProfile();
      return this.mSessionID != null && profile.getUserID() != null;
   }

   public boolean isQuickDraw() {
      return this.mQuickDraw;
   }

   public boolean isLoaded() {
      return PlayerProfile.isInitialized() && this.mState != 0;
   }

   public boolean isPlaying(boolean any) {
      return Integer.compareUnsigned(this.mState, 6) <= 0 && (1 << this.mState & 84) != 0 ? any || DCiDead.getActivityState() == -1 : false;
   }

   public long getPlayTime() {
      return Time.getTimestampDelta(this.mResumeTimestamp);
   }

   public int getTimer() {
      return this.mTimer;
   }

   public void setCreateNewSession() {
      if (!this.mCreateNewSessionServlet.isWaitingForResponse()) {
         this.mCreateNewSession = true;
      }

      this.mRetryTimer = 0;
      this.mSessionID = null;
      this.mSessionStatusUnsupported = false;
      this.mServerReachable = true;
      this.mCreateNewSessionServlet.resetShowOfflineMode();
   }

   public void setGetTimers() {
      if (!this.mGetTimersServlet.isWaitingForResponse()) {
         this.mGetTimers = true;
      }
   }

   public void setGetCustomMissions() {
      if (!this.mGetCustomMissionsServlet.isWaitingForResponse()) {
         this.mGetCustomMissions = true;
      }
   }

   public void setGetSessionStatus() {
      if (this.mSessionStatusUnsupported) {
         this.setGetCustomMissions();
         return;
      }
      if (this.isConnected() && !this.mGetSessionStatusServlet.isWaitingForResponse()) {
         this.mGetSessionStatus = true;
      }
   }

   public void setSessionStatusUnsupported() {
      if (!this.mSessionStatusUnsupported) {
         this.mSessionStatusUnsupported = true;
         System.out.println("GetSessionStatus is not supported by the server; using the legacy mission poll.");
      }
   }

   public void setServerReachable(boolean reachable) {
      this.mServerReachable = reachable;
   }

   public boolean isServerReachable() {
      return this.mServerReachable;
   }

   public void setSetOption() {
      if (!this.mSetOptionServlet.isWaitingForResponse()) {
         this.mSetOption = true;
      }
   }

   public void setUpdateAccountInformation() {
      if (!this.mUpdateAccountInformationServlet.isWaitingForResponse()) {
         this.mUpdateAccountInformation = true;
      }
   }

   public UpdateAccountInformation getUpdateAccountInformation() {
      return this.mUpdateAccountInformationServlet;
   }

   public void setGetAccountInformation() {
      if (!this.mGetAccountInformationServlet.isWaitingForResponse()) {
         this.mGetAccountInformation = true;
      }
   }

   public void setTransactionActive() {
      this.mTransactionActive = true;
   }

   public int getInGameMarketMode() {
      return this.mInGameMarketMode;
   }

   public void setInGameMarketMode(int mode) {
      this.mInGameMarketMode = mode;
   }

   public boolean isTransactionActive() {
      return this.mTransactionActive && WindowManager.isWindowOpened((byte)22);
   }

   public void setSaveGameState(boolean now) {
      if (now) {
         this.saveGameState();
      } else {
         DCiDead.setActivityState(1, false);
         this.mSaveGameState = true;
      }
   }

   public boolean loadPlayerProfile() {
      DChocByteArray data = Toolkit.readRecordByteArray("RMS_PROFILE");
      if (data != null && PlayerProfile.load(data)) {
         Toolkit.writeRecordByteArray("RMS_PROFILE_BACKUP", data, 0);
         this.mUpdateAccountInformationServlet.setAvatar(data);
         return true;
      } else {
         data = Toolkit.readRecordByteArray("RMS_PROFILE_BACKUP");
         return data != null && PlayerProfile.load(data);
      }
   }

   public DChocByteArray savePlayerProfile() {
      ABTestManager.save();
      DChocByteArray data = PlayerProfile.save();
      if (data != null) {
         Toolkit.writeRecordByteArray("RMS_PROFILE", data, 0);
         this.mUpdateAccountInformationServlet.setAvatar(data);
      }

      return data;
   }

   public void updateFps(int dt) {
   }

   private void drawIntroTransition() {
      if (this.mIntroTransitionTimer >= 1) {
         float timer = this.mIntroTransitionTimer;
         float alpha = timer * timer / 3000.0F / 3000.0F;
         boolean hdWidth = DeviceWrapper.useHDGraphics();
         int screenWidth = Toolkit.getScreenWidth();
         boolean hdHeight = DeviceWrapper.useHDGraphics();
         int screenHeight = Toolkit.getScreenHeight();
         OGL.setAlpha(alpha);
         OGL.setColorRGB(0);
         int height = screenWidth * (hdWidth ? 540 : 270) / (hdHeight ? 960 : 480);
         int y = screenHeight - height >> 1;
         OGL.fillRect(0, 0, Toolkit.getScreenWidth(), y);
         OGL.fillRect(0, height + y, Toolkit.getScreenWidth(), height + y);
         OGL.setColorRGB(0);
         OGL.fillRect(0, y, Toolkit.getScreenWidth(), height);
         OGL.setAlpha(1.0F);
      }
   }

   public void saveGameState() {
      if (!TutorialFlow.isCompleted()) {
         ABTestManager.save();
      } else {
         this.mMemoryTimer = 0;
         if (this.mCurrentArea != null) {
            this.savePlayerProfile();
            if ((this.mState | 4) == 6) {
               this.mCurrentArea.saveData();
               this.setUpdateAccountInformation();
            }
         }
      }
   }

   private void syncAccountSave() {
      if (Boolean.getBoolean("zlport.server.mode") && TutorialFlow.isCompleted()
         && this.isLoaded() && this.mCurrentArea != null && (this.mState | 4) == 6) {
         this.savePlayerProfile();
         this.mCurrentArea.saveData();
         this.setUpdateAccountInformation();
      }
   }
}
