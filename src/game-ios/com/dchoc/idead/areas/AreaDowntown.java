package com.dchoc.idead.areas;

import com.dchoc.idead.tracking.VisitingEvent;
import com.dchoc.idead.tuner.Tuner;

public class AreaDowntown extends Area {
   private VisitingEvent mTracking = new VisitingEvent();

   public AreaDowntown(String recordName, String backupRecordName) {
      super(1, Tuner.getAdventureArea(4054), 80448, recordName, backupRecordName);
   }

   @Override
   public boolean loadData(boolean withMissions) {
      this.mTracking.trackDowntownStarted();
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
