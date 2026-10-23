package com.dchoc.idead.objects;

import com.dchoc.idead.isometric.Camera;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.items.DecorationItem;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.spriteobject.SpriteObject;

public class DecorationObject extends StaticObject {
   public static final int ANIMATION_HORIZONTAL = 1;
   public static final int ANIMATION_VERTICAL = 0;
   public static final int ROTATION_HORIZONTAL = 1;
   public static final int ROTATION_VERTICAL = 0;
   public static final int STATE_IDLE = 0;
   protected int mRotation;
   protected Mission mShowAfterMission;
   protected int mState;

   public DecorationObject(IsometricScene scene) {
      super(scene, 4);
   }

   public void init(float x, float y, DecorationItem item) {
      super.init(x, y, item);
      Mission mission = MissionManager.getMission(item.getShowAfterMission());
      this.mRotation = 0;
      this.mShowAfterMission = mission;
      this.changeState(0);
   }

   public void changeState(int state) {
      if (state == 0) {
         this.mAnimations.changeAnimation(this.mRotation != 0 ? 1 : 0, true);
      }

      this.mState = state;
   }

   @Override
   public boolean doDraw(Camera camera) {
      return this.isHiddenByMission() ? false : super.doDraw(camera);
   }

   public boolean isHiddenByMission() {
      return this.mShowAfterMission != null ? !this.mShowAfterMission.isCompleted() : false;
   }

   public boolean isRotateable() {
      return ((DecorationItem)this.mItem).isRotatable();
   }

   public void setRotation(int rotation) {
      if (this.mRotation != rotation && this.isRotateable()) {
         this.mRotation = rotation;
         int sizeX = this.mRotation == 0 ? this.mItem.getTileSizeX() : this.mItem.getTileSizeY();
         int sizeY = this.mRotation == 0 ? this.mItem.getTileSizeY() : this.mItem.getTileSizeX();

         for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
               IsometricTile tile = this.mScene.getTile(this.mTile.getColumn() + i, this.mTile.getRow() - j);
               if (tile != null) {
                  tile.removeObject(this);
               }
            }
         }

         for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
               IsometricTile tile = this.mScene.getTile(this.mTile.getColumn() + i, this.mTile.getRow() - j);
               if (tile != null) {
                  tile.addObject(this);
               }
            }
         }

         if (sizeX > 1 || sizeY > 1) {
            this.mScene.setUpdateDeferredTiles();
         }

         this.changeState(this.mState);
      }
   }

   @Override
   public boolean isFloor() {
      return this.mItem.getLayer() == 1;
   }

   public int getRotation() {
      return this.mRotation;
   }

   public int getState() {
      return this.mState;
   }

   @Override
   public SpriteObject getCollision() {
      SpriteObject[] collisions = this.mItem.getCollisions();
      if (collisions == null) {
         return null;
      } else {
         return this.mRotation < collisions.length ? collisions[this.mRotation] : null;
      }
   }

   @Override
   public int getTileSizeX() {
      return this.mRotation != 0 ? this.mItem.getTileSizeY() : this.mItem.getTileSizeX();
   }

   @Override
   public int getTileSizeY() {
      return this.mRotation != 0 ? this.mItem.getTileSizeX() : this.mItem.getTileSizeY();
   }

   @Override
   public boolean isReleasable() {
      return true;
   }

   @Override
   public boolean isHiddenable() {
      return true;
   }

   @Override
   public boolean isPickupable() {
      return this.mScene.getState() != 0;
   }
}
