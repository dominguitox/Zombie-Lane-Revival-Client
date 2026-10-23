package com.dchoc.idead.areas;

import com.dchoc.hud.HUD;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.Profiler;
import com.dchoc.idead.fixes.Fixes;
import com.dchoc.idead.isometric.SceneLoader;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.player.VisitingRewards;
import com.dchoc.idead.servlets.VisitNeighbor;
import com.dchoc.idead.tracking.VisitingEvent;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.toolkit.DChocByteArray;

public class AreaNeighbor extends Area {
   private VisitNeighbor mVisitNeighbor = new VisitNeighbor();
   private VisitingEvent mTracking = new VisitingEvent();

   public AreaNeighbor() {
      super(2, Tuner.getNeighborArea(), -1, null, null);
   }

   public void startLoadingData(NeighborProfile neighbor) {
      Profiler.startTimer(14);
      this.mVisitNeighbor.init(neighbor);
      this.mVisitNeighbor.execute();
   }

   public boolean isDataLoaded() {
      return this.mVisitNeighbor.isResponseRecieved();
   }

   @Override
   public boolean loadData(boolean withMissions) {
      Profiler.stopTimer(14);
      this.mLoadSucceed = false;
      NeighborProfile neighbor = this.mVisitNeighbor.getProfile();
      if (neighbor != null) {
         this.mTracking.trackStarted(neighbor.getUserID());
      }

      if (this.isDataLoaded() && neighbor != null) {
         byte[] avatar = this.mVisitNeighbor.getAvatarData();
         if (avatar != null) {
            neighbor.load(DChocByteArray.createByteArrayForReading(avatar, 0, avatar.length));
         }

         byte[] gamefield = this.mVisitNeighbor.getGamefieldData();
         boolean loaded;
         if (gamefield == null) {
            loaded = this.mLoadSucceed;
         } else {
            loaded = SceneLoader.load(DChocByteArray.createByteArrayForReading(gamefield, 0, gamefield.length), true, neighbor.getPlatform());
            this.mLoadSucceed = loaded;
         }

         if (loaded) {
            GameEngine.getInstance().setNeighbor(neighbor);
            HUD.getInGame().getFriendsPlace().setNeighbor(neighbor);
            MissionManager.updateNeighborMissionSetups();
            MissionManager.updateDelayedMissionSetups();
            if (this.mVisitNeighbor.hasRewards()) {
               VisitingRewards rewards = PlayerProfile.getVisitingRewards();
               HUD.showVisitNeighbor(neighbor.getName(), rewards.getXpReward(), rewards.getCoinReward(), rewards.getEnergyReward(), rewards.getDuctTapeReward());
               MissionManager.updateCounter(123, neighbor.getType(), 1);
               MissionManager.updateCounter(123, 80, 1);
               PlayerProfile.addVisitingRewards(rewards);
            }
         }

         Fixes.fixMissingCharacters(this.mSetup);
      }

      if (this.mLoadSucceed) {
         this.mTracking.trackLoaded();
      } else {
         this.mTracking.trackError();
      }

      this.mVisitNeighbor.resetData();
      return this.mLoadSucceed;
   }

   @Override
   public boolean saveData() {
      if (!GameEngineUtils.isLoaded()) {
         return false;
      }

      if (!this.mLoadSucceed) {
         return true;
      }

      this.mTracking.trackCompleted();
      return true;
   }
}
