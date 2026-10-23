package com.dchoc.idead.areas;

import com.dchoc.idead.GameEngine;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.fixes.Fixes;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.isometric.SceneLoader;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.servlets.UpdateAccountInformation;
import com.dchoc.idead.tuner.AreaSetup;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;

public class Area {
   protected int mType;
   protected AreaSetup mSetup;
   protected String mRecordName;
   protected String mBackupRecordName;
   protected int mResource;
   protected boolean mLoadSucceed;

   public Area(int type, AreaSetup setup, int resource, String recordName, String backupRecordName) {
      this.mType = type;
      this.mSetup = setup;
      this.mRecordName = recordName;
      this.mBackupRecordName = backupRecordName;
      this.mResource = resource;
      this.mLoadSucceed = true;
   }

   public AreaSetup getSetup() {
      return this.mSetup;
   }

   public int getType() {
      return this.mType;
   }

   public int getName() {
      return this.mSetup.getName();
   }

   public int getMusic() {
      return this.mSetup.getMusic();
   }

   public int getBackground() {
      return this.mSetup.getBackground();
   }

   public int getSky() {
      return this.mSetup.getSky();
   }

   public int[][] getShadows() {
      return this.mSetup.getShadows();
   }

   public int getWidth() {
      return this.mSetup.getAreaWidth();
   }

   public int getHeight() {
      return this.mSetup.getAreaHeight();
   }

   public short[] getNonAccessibleAreas() {
      return this.mSetup.getNonAccessibleAreas();
   }

   public short[] getNonWalkableAreas() {
      return this.mSetup.getNonWalkableAreas();
   }

   public boolean isLoadSucceed() {
      return this.mLoadSucceed;
   }

   public void enter() {
   }

   public void exit() {
   }

   public boolean loadData(boolean withMissions) {
      IsometricUtils.getScene();
      GameEngine engine = GameEngine.getInstance();
      UpdateAccountInformation update = engine.getUpdateAccountInformation();
      DChocByteArray data = Toolkit.readRecordByteArray(this.mRecordName);
      boolean failed = true;
      if (data != null) {
         failed = !SceneLoader.load(data, true);
         if (!failed && !withMissions && this.mSetup.getName() != -1) {
            update.setGamefield(data, this.mSetup.getName());
            engine.setUpdateAccountInformation();
         }
      }

      boolean loadBackup;
      if (withMissions) {
         DChocByteArray missions = Toolkit.readRecordByteArray("RMS_MISSIONS");
         if (!failed) {
            if (missions != null && MissionManager.load(missions)) {
               Toolkit.writeRecordByteArray(this.mBackupRecordName, data, 0);
               Toolkit.writeRecordByteArray("RMS_MISSIONS_BACKUP", missions, 0);
               update.setMissions(missions);
               update.setGamefield(data, this.mSetup.getName());
               engine.setUpdateAccountInformation();
               loadBackup = false;
            } else {
               System.err.println("Area: no se pudieron cargar las misiones; se conserva el gamefield descargado");
               Toolkit.writeRecordByteArray(this.mBackupRecordName, data, 0);
               if (missions != null) {
                  Toolkit.writeRecordByteArray("RMS_MISSIONS_BACKUP", missions, 0);
               }

               loadBackup = false;
            }
         } else {
            loadBackup = true;
         }
      } else {
         loadBackup = failed;
      }

      if (loadBackup) {
         DChocByteArray backup = Toolkit.readRecordByteArray(this.mBackupRecordName);
         if (backup == null || !SceneLoader.load(backup, true)) {
            SceneLoader.load(Toolkit.getResourceByteArray(this.mResource), false);
         }

         if (withMissions) {
            DChocByteArray missions = Toolkit.readRecordByteArray("RMS_MISSIONS_BACKUP");
            if (missions != null) {
               MissionManager.load(missions);
            }
         }
      }

      MissionManager.updateCounter(3860, this.mSetup.getName(), 1);
      MissionManager.updateCounter(3860, 80, 1);
      MissionManager.updateDelayedMissionSetups();
      Fixes.fixMissingCharacters(this.mSetup);
      if (this.mType != 2 && GameEngineUtils.isLoaded()) {
         Fixes.fixMissionSetups();
      }

      return true;
   }

   public boolean saveData() {
      if (!GameEngineUtils.isLoaded()) {
         return false;
      } else if (MissionManager.isLoadIncomplete()) {
         System.err.println("Area: no se guarda el estado porque las misiones no se cargaron por completo");
         return false;
      } else {
         GameEngine engine = GameEngine.getInstance();
         UpdateAccountInformation update = engine.getUpdateAccountInformation();
         DChocByteArray missions = MissionManager.save();
         DChocByteArray scene = SceneLoader.saveCurrent();
         if (scene != null && missions != null) {
            Toolkit.writeRecordByteArray(this.mRecordName, scene, 0);
            Toolkit.writeRecordByteArray("RMS_MISSIONS", missions, 0);
            update.setMissions(missions);
            update.setGamefield(scene, this.mSetup.getName());
            engine.setUpdateAccountInformation();
            return true;
         } else {
            return true;
         }
      }
   }
}
