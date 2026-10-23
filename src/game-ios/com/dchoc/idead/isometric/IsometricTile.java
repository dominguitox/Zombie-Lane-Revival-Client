package com.dchoc.idead.isometric;

import com.dchoc.idead.objects.FarmingObject;
import com.dchoc.idead.objects.FenceObject;
import com.dchoc.idead.pathfinding.PathNode;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.toolkit.ToolkitHelpers;
import java.util.Vector;

public class IsometricTile extends PathNode {
   public static final int INITIAL_OBJECTS_CAPACITY = 2;
   private FarmingObject mCrop;
   private IsometricObject mDeferring;
   private FenceObject mFence = null;
   private Vector mObjects;
   private IsometricObject mOccupying;
   private boolean mPlayer;
   private boolean mZombie;

   public IsometricTile(byte column, byte row) {
      super(column, row);
      this.mCrop = null;
      this.mZombie = false;
      this.mPlayer = false;
      this.mDeferring = null;
      this.mOccupying = null;
      this.mObjects = new Vector(2);
   }

   public static float getTileScale() {
      return DeviceWrapper.useHDIngameGraphics() ? 1.3F : 0.65F;
   }

   public static float getTileSize() {
      return DeviceWrapper.useHDIngameGraphics() ? 96.0F : 48.0F;
   }

   public static float getTilesSizeHalf() {
      return DeviceWrapper.useHDIngameGraphics() ? 48.0F : 24.0F;
   }

   public static float getLineWidth() {
      return DeviceWrapper.useHDIngameGraphics() ? 6.0F : 3.0F;
   }

   public void updateObjects() {
      this.mOccupied = false;
      this.mZombie = false;
      this.mPlayer = false;
      IsometricObject previous = this.mOccupying;
      this.mOccupying = null;
      this.mFence = null;
      this.mCrop = null;

      for (int i = 0; i < this.mObjects.size(); i++) {
         this.updateStatus((IsometricObject)this.mObjects.elementAt(i), previous);
      }
   }

   public boolean hasObjects() {
      return !this.mObjects.isEmpty();
   }

   public Vector getObjects() {
      return this.mObjects;
   }

   public boolean containsObject(IsometricObject object) {
      return this.mObjects.contains(object);
   }

   public void addObject(IsometricObject object) {
      if (object != null) {
         this.mObjects.addElement(object);
         this.updateStatus(object, this.mOccupying);
      }
   }

   public void removeObject(IsometricObject object) {
      if (object != null) {
         ToolkitHelpers.fastRemove(object, this.mObjects);
         this.updateObjects();
      }
   }

   public void removeAllObjects() {
      this.mOccupied = false;
      this.mDeferring = null;
      this.mOccupying = null;
      this.mCrop = null;
      this.mFence = null;
      this.mZombie = false;
      this.mPlayer = false;
      this.mObjects.removeAllElements();
   }

   public boolean isValid(IsometricObject object) {
      if (this.mOccupied && object == null) {
         return true;
      }

      for (int i = 0; i < this.mObjects.size(); i++) {
         IsometricObject other = (IsometricObject)this.mObjects.elementAt(i);
         if (other != object && !other.isWalkable()) {
            return true;
         }
      }

      return false;
   }

   public boolean hasFence() {
      return this.mFence != null;
   }

   public FenceObject getFence() {
      return this.mFence;
   }

   public boolean hasCrop() {
      return this.mCrop != null;
   }

   public FarmingObject getCrop() {
      return this.mCrop;
   }

   public boolean hasZombie() {
      return this.mZombie;
   }

   public boolean hasPlayer() {
      return this.mPlayer;
   }

   public void setDeferring(IsometricObject object) {
      this.mDeferring = object;
   }

   public IsometricObject getDeferring(int layer) {
      return layer == 2 ? this.mDeferring : null;
   }

   public IsometricObject getOccupying() {
      return this.mOccupying;
   }

   public float getX() {
      int column = this.mColumn;
      float size = DeviceWrapper.useHDIngameGraphics() ? 96.0F : 48.0F;
      float half = DeviceWrapper.useHDIngameGraphics() ? 48.0F : 24.0F;
      return column * size + half;
   }

   public float getY() {
      int row = this.mRow;
      float size = DeviceWrapper.useHDIngameGraphics() ? 96.0F : 48.0F;
      float half = DeviceWrapper.useHDIngameGraphics() ? 48.0F : 24.0F;
      return row * size + half;
   }

   public void updateStatus(IsometricObject object, IsometricObject previous) {
      if (object != null) {
         if (!object.isWalkable()) {
            this.mOccupied = true;
            if (this.mOccupying == null || object == previous) {
               this.mOccupying = object;
            }
         }

         switch (object.getType()) {
            case 1:
            case 7:
               this.mPlayer = true;
               break;
            case 2:
               this.mZombie = true;
            case 3:
            case 4:
            default:
               break;
            case 5:
               if (this.mFence == null) {
                  this.mFence = (FenceObject)object;
               }
               break;
            case 6:
               if (this.mCrop == null) {
                  this.mCrop = (FarmingObject)object;
               }
         }
      }
   }
}
