package com.dchoc.idead.areas;

import com.dchoc.hud.HUD;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.Time;
import com.dchoc.idead.fixes.Fixes;
import com.dchoc.idead.isometric.SceneLoader;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.neighbors.NeighborRob;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.player.VisitingRewards;
import com.dchoc.idead.tracking.ErrorEvent;
import com.dchoc.idead.tracking.VisitingEvent;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import com.dchoc.windows.WindowSocial;
import java.io.EOFException;
import zlport.ios.Ios;

public class AreaRob extends Area {
   private NeighborRob mProfile = new NeighborRob();
   private long mVisitTime = 0L;
   private long mHireCountdown = 0L;
   private VisitingEvent mTracking = new VisitingEvent();

   public AreaRob(String recordName) {
      super(2, Tuner.getTutorArea(), 80446, recordName, null);
      this.loadRobsData();
   }

   public void tryToUnlock() {
      Mission mission = MissionManager.getMission(Tuner.getMissionToUnlockVisitRob());
      if (mission.isCompleted() || mission.getState() == 2) {
         this.unlock();
      }
   }

   public void unlock() {
      ((WindowSocial)WindowManager.getWindow((byte)18)).addNeighbor(this.mProfile);
   }

   public void setHireCountdown(long countdown) {
      this.mHireCountdown = countdown;
   }

   public long getHireCountdownLeft() {
      return this.mHireCountdown != 0L ? Time.getCoundownLeft(this.mHireCountdown) : -1L;
   }

   public NeighborRob getProfile() {
      return this.mProfile;
   }

   @Override
   public boolean loadData(boolean withMissions) {
      this.mTracking.trackTutorStarted();
      this.mLoadSucceed = SceneLoader.load(Toolkit.getResourceByteArray(this.mResource), false);
      if (this.mLoadSucceed) {
         HUD.getInGame().getFriendsPlace().setNeighbor(this.mProfile);
         MissionManager.updateNeighborMissionSetups();
         MissionManager.updateDelayedMissionSetups();
         this.updateRobsData();
         this.mTracking.trackLoaded();
      } else {
         this.mTracking.trackError();
      }

      Fixes.fixMissingCharacters(this.mSetup);
      return this.mLoadSucceed;
   }

   @Override
   public boolean saveData() {
      if (!GameEngineUtils.isLoaded()) {
         return false;
      }

      if (this.mLoadSucceed) {
         this.mTracking.trackCompleted();
      }

      return this.saveRobsData();
   }

   public boolean saveRobsData() {
      DChocByteArray data = this.mProfile.save();
      data.writeInt(this.mProfile.getActionsLeft());
      ToolkitHelpers.writeLong(this.mProfile.getActionsTime(), data);
      ToolkitHelpers.writeLong(this.mVisitTime, data);
      ToolkitHelpers.writeLong(this.mHireCountdown, data);
      Toolkit.writeRecordByteArray(this.mRecordName, data, 0);
      return true;
   }

   public void updateRobsData() {
      long interval = visitInterval();
      long actionsTime = this.mProfile.getActionsTime();
      if (actionsTime == 0L || Time.getTimestampDelta(actionsTime) >= interval) {
         this.mProfile.setActionsLeft(5);
      }

      if (this.mVisitTime == 0L || Time.getTimestampDelta(this.mVisitTime) >= interval) {
         VisitingRewards rewards = PlayerProfile.getVisitingRewards();
         HUD.showVisitNeighbor(this.mProfile.getName(), rewards.getXpReward(), rewards.getCoinReward(), rewards.getEnergyReward(), rewards.getDuctTapeReward());
         PlayerProfile.addVisitingRewards(rewards);
         MissionManager.updateCounter(123, this.mProfile.getType(), 1);
         MissionManager.updateCounter(123, 80, 1);
         this.mVisitTime = Time.getTimestamp();
      }
   }

   private static long visitInterval() {
      Long override = Ios.platform().neighborIntervalOverride();
      return override != null && override > 0L ? override : 86400000L;
   }

   public boolean loadRobsData() {
      DChocByteArray data = Toolkit.readRecordByteArray(this.mRecordName);
      if (data != null) {
         try {
            this.mProfile.load(data);
            this.mProfile.setActionsLeft(data.readInt());
            this.mProfile.setActionsTime(Fixes.fixTimestamp(ToolkitHelpers.readLong(data)));
            this.mVisitTime = Fixes.fixTimestamp(ToolkitHelpers.readLong(data));
            this.mHireCountdown = ToolkitHelpers.readLong(data);
         } catch (Exception exception) {
            if (exception instanceof EOFException) {
               new ErrorEvent().trackLoadError("Place.loadData", exception.getMessage(), this.mProfile.getName());
            }

            System.err.println(exception);
         }
      }

      return true;
   }
}
