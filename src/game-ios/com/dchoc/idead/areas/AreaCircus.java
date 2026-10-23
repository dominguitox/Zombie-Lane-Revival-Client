package com.dchoc.idead.areas;

import com.dchoc.idead.animation.Animations;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.tracking.VisitingEvent;
import com.dchoc.idead.tuner.Tuner;

public class AreaCircus extends Area {
   private VisitingEvent mTracking = new VisitingEvent();
   private WeaponItem mMallet = ItemManager.getWeapon(4052);

   public AreaCircus(String recordName, String backupRecordName) {
      super(1, Tuner.getAdventureArea(3818), 80447, recordName, backupRecordName);
   }

   public WeaponItem getMallet() {
      if (this.mMallet.getAnimations() == null) {
         this.mMallet.loadAnimations();
      }

      return this.mMallet;
   }

   public int getMalletAnimation() {
      return 3;
   }

   public boolean useMallet(IsometricObject object) {
      if (object == null) {
         return false;
      }

      int subType = object.getItemSubType();
      return subType == 3790 || Integer.compareUnsigned(subType - 3797, 3) < 0;
   }

   public boolean useMalletAnimation(PlayerCharacter player) {
      if (player != null && player.getAnimations() != null) {
         Animations animations = player.getAnimations();
         return player.getState() == 5 && animations.getAnimation() == 3;
      } else {
         return false;
      }
   }

   @Override
   public void enter() {
      this.mMallet.loadAnimations();
   }

   @Override
   public void exit() {
      this.mMallet.unloadAnimations();
   }

   @Override
   public boolean loadData(boolean withMissions) {
      this.mTracking.trackCircusStarted();
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
