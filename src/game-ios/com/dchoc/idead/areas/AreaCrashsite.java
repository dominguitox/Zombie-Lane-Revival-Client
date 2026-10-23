package com.dchoc.idead.areas;

import com.dchoc.idead.tracking.VisitingEvent;
import com.dchoc.idead.tuner.Tuner;

public class AreaCrashsite extends Area {
   private VisitingEvent mTracking = new VisitingEvent();

   public AreaCrashsite(String recordName, String backupRecordName) {
      super(1, Tuner.getAdventureArea(4745), 80449, recordName, backupRecordName);
   }

   @Override
   public boolean loadData(boolean withMissions) {
      this.mTracking.trackCrashSiteStarted();
      boolean loaded = super.loadData(withMissions);
      if (loaded) {
         this.mTracking.trackLoaded();
      } else {
         this.mTracking.trackError();
      }

      return loaded;
   }

   @Override
   public boolean saveData() {
      boolean saved = super.saveData();
      if (this.mLoadSucceed) {
         this.mTracking.trackCompleted();
      }

      return saved;
   }
}
