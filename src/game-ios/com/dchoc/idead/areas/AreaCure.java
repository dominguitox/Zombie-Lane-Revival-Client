package com.dchoc.idead.areas;

import com.dchoc.idead.animation.Animations;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.tracking.VisitingEvent;
import com.dchoc.idead.tuner.Tuner;

public class AreaCure extends Area {
   private VisitingEvent mTracking = new VisitingEvent();
   private WeaponItem mFlamethrower = ItemManager.getWeapon(149);

   public AreaCure(String recordName, String backupRecordName) {
      super(1, Tuner.getAdventureArea(5560), 80450, recordName, backupRecordName);
   }

   public WeaponItem getFlamethrower() {
      if (this.mFlamethrower.getAnimations() == null) {
         this.mFlamethrower.loadAnimations();
      }

      return this.mFlamethrower;
   }

   public int getFlamethrowerAnimation() {
      return 9;
   }

   public boolean useFlamethrower(IsometricObject object) {
      return object != null && object.getItemID() == 5596;
   }

   public boolean useFlamethrowerAnimation(PlayerCharacter player) {
      if (player != null && player.getAnimations() != null) {
         Animations animations = player.getAnimations();
         return player.getState() == 5 && animations.getAnimation() == 9;
      } else {
         return false;
      }
   }

   @Override
   public void enter() {
      this.mFlamethrower.loadAnimations();
   }

   @Override
   public void exit() {
      this.mFlamethrower.unloadAnimations();
   }

   @Override
   public boolean loadData(boolean withMissions) {
      this.mTracking.trackCureStarted();
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
