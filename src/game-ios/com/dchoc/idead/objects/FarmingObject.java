package com.dchoc.idead.objects;

import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.Time;
import com.dchoc.idead.actions.Action;
import com.dchoc.idead.characters.ActionCharacter;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.items.FarmingItem;
import com.dchoc.windows.WindowWitheredCrops;

public class FarmingObject extends StaticObject {
   private boolean mAttacked;
   private boolean mDestroyed;
   private long mDestructTimestamp;
   private boolean mHarvested;
   private boolean mRevived;
   private long mTimestamp;
   private boolean mWithered;

   public FarmingObject(IsometricScene scene) {
      super(scene, 6);
   }

   public void init(float x, float y, FarmingItem item) {
      super.init(x, y, item);
      this.mAnimations.setAnimationFrame(0);
      this.mWithered = false;
      this.mDestroyed = false;
      this.mAttacked = false;
      this.mRevived = false;
      this.mHarvested = false;
   }

   @Override
   public int logicUpdate(int delta) {
      int result = super.logicUpdate(delta);
      if (result != 0) {
         return result;
      }

      if (!this.isAlive()) {
         return 1;
      }

      FarmingItem item = (FarmingItem)this.mItem;
      if (item.canWither() && !this.isWithered() && !this.isRevived()) {
         long elapsed = Time.getTimestampDelta(this.mTimestamp);
         int witherTime = item.getWitherTime();
         int growingTime = 0;
         if (item.canGrow()) {
            growingTime = item.getGrowingTime();
         }

         this.mWithered = elapsed > growingTime + witherTime;
      }

      if (this.mAttacked && !this.mDestroyed) {
         long elapsed = Time.getTimestampDelta(this.mDestructTimestamp);
         this.mDestroyed = elapsed > 3600000L;
         if (elapsed > 3600000L) {
            this.setAttacked(false);
         }
      }

      return 0;
   }

   public boolean isAdult() {
      if (this.isWithered()) {
         return false;
      } else {
         return !this.isGrowing() ? true : this.mHarvested;
      }
   }

   public boolean isAlive() {
      return !this.mHarvested ? true : this.mItem.getSubType() == 1938;
   }

   public boolean isAutomatic() {
      return this.mItem.getSubType() == 1938;
   }

   @Override
   public boolean canInteractWith(ActionCharacter actor) {
      return !this.isGrowing() ? true : GameEngine.getInstance().isVisitingNeighbor();
   }

   @Override
   public Action interactWith(ActionCharacter actor) {
      GameEngine engine = GameEngine.getInstance();
      if ((this.isWithered() || this.isDestroyed()) && actor.isPlayer() && !engine.isVisitingNeighbor()) {
         ((WindowWitheredCrops)WindowManager.getWindow((byte)28)).setTarget(this);
         WindowManager.openWindow((byte)28);
         return null;
      } else {
         return actor.addAction(5, this);
      }
   }

   public void setWithered(boolean withered) {
      this.mWithered = withered;
   }

   public boolean isWithered() {
      if (!((FarmingItem)this.mItem).canWither()) {
         return false;
      } else {
         return !this.mWithered ? false : !this.mRevived;
      }
   }

   public boolean isDestroyed() {
      return this.mDestroyed;
   }

   public void setDestroyed(boolean destroyed) {
      this.mDestroyed = destroyed;
   }

   public boolean isAttacked() {
      return this.mAttacked;
   }

   public void setAttacked(boolean attacked) {
      this.mAttacked = attacked;
      FarmingItem item = (FarmingItem)this.mItem;
      if (item.getSubType() == 1938 || !item.canWither()) {
         this.mAttacked = false;
      }
   }

   public void attacked() {
      if (!this.mAttacked) {
         this.setAttacked(true);
         this.mDestructTimestamp = Time.getTimestamp();
      }
   }

   public boolean isGrowing() {
      if (this.isRevived()) {
         return false;
      } else if (this.isDestroyed()) {
         return false;
      } else {
         FarmingItem item = (FarmingItem)this.mItem;
         if (!item.canGrow()) {
            return false;
         } else {
            return this.isWithered() ? false : Time.getTimestampDelta(this.mTimestamp) <= item.getGrowingTime();
         }
      }
   }

   public void setInstaGrow() {
      if (this.isGrowing()) {
         long elapsed = Time.getTimestampDelta(this.mTimestamp);
         int growingTime = ((FarmingItem)this.mItem).getGrowingTime();
         this.mTimestamp += elapsed - growingTime;
      }
   }

   public long getGrowingTimeLeft() {
      if (!this.isGrowing()) {
         return 0L;
      }

      long elapsed = Time.getTimestampDelta(this.mTimestamp);
      return ((FarmingItem)this.mItem).getGrowingTime() - elapsed;
   }

   public long getGrowingTimeElapsed() {
      return Time.getTimestampDelta(this.mTimestamp);
   }

   public int getGrowingTimeMax() {
      return ((FarmingItem)this.mItem).getGrowingTime();
   }

   public void harvest() {
      if (this.mItem.getSubType() == 1938) {
         this.resetTimestamp();
      }

      this.mRevived = false;
      this.mHarvested = true;
   }

   public void setHarvested(boolean harvested) {
      this.mHarvested = harvested;
   }

   public boolean isHarvested() {
      return this.mHarvested;
   }

   public void resetTimestamp() {
      this.mTimestamp = Time.getTimestamp();
   }

   public void resetDestructTimestamp() {
      this.mDestructTimestamp = Time.getTimestamp();
   }

   public void setTimestamp(long timestamp) {
      this.mTimestamp = timestamp;
   }

   public long getTimestamp() {
      return this.mTimestamp;
   }

   public void setDestructTimestamp(long timestamp) {
      this.mDestructTimestamp = timestamp;
   }

   public long getDestructTimestamp() {
      return this.mDestructTimestamp;
   }

   public void setRevived(boolean revived) {
      this.mRevived = revived;
      if (revived) {
         this.mWithered = false;
         this.mDestroyed = false;
      }
   }

   public boolean isRevived() {
      return this.mRevived;
   }

   @Override
   public boolean isHiddenable() {
      return true;
   }

   @Override
   protected void changeTile(IsometricTile tile) {
      super.changeTile(tile);
      this.setAttacked(false);
   }

   @Override
   protected boolean showHealthBar() {
      if (this.isGrowing()) {
         return false;
      } else {
         return this.isWithered() ? false : !this.isDestroyed();
      }
   }

   @Override
   protected boolean showTooltip(boolean tooltip) {
      return GameEngine.getInstance().isVisitingNeighbor() ? true : !this.isGrowing();
   }

   @Override
   protected void updateAnimations(int delta) {
      if (this.mAnimations != null) {
         FarmingItem item = (FarmingItem)this.mItem;
         int count = this.mAnimations.getAnimationFrameCount();
         boolean canWither = item.canWither();
         int harvested = Math.max(0, count - 2);
         int adult = count - 2;
         if (!canWither) {
            adult = count - 1;
         }

         if (this.isWithered() || this.mDestroyed) {
            this.mAnimations.setAnimationFrame(count - 1);
         } else if (!this.isGrowing()) {
            this.mAnimations.setAnimationFrame(adult);
         } else if (this.mHarvested) {
            this.mAnimations.setAnimationFrame(harvested);
         } else {
            long elapsed = Time.getTimestampDelta(this.mTimestamp);
            this.mAnimations.setAnimationFrame((int)(elapsed * adult / item.getGrowingTime()));
         }
      }
   }
}
