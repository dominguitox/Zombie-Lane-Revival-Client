package com.dchoc.idead.areas;

import com.dchoc.idead.tuner.Tuner;

public class AreaHome extends Area {
   public AreaHome(String recordName, String backupRecordName) {
      super(0, Tuner.getHomeArea(), 80445, recordName, backupRecordName);
   }
}
