package com.dchoc.idead.items;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class PlayerItem extends Item {
   private short mGender;
   private int[] mTorsoAnimationIDs;
   private SpriteObject[] mTorsoAnimations;

   public PlayerItem(DChocByteArray data) throws EOFException {
      this.mType = 837;
      this.mWalkable = true;
      this.loadData(data);
   }

   public int getGender() {
      return this.mGender;
   }

   public void unloadTorsoAnimations() {
      AnimationsManager.unload(this.mTorsoAnimations);
      this.mTorsoAnimations = null;
   }

   public SpriteObject[] getTorsoAnimations() {
      if (this.mTorsoAnimations == null) {
         int profile = DeviceWrapper.isIPAD1() ? 1 : (DeviceWrapper.isIPOD4() ? 1 : 0);
         this.mTorsoAnimations = AnimationsManager.load(this.mTorsoAnimationIDs, profile);
      }

      return this.mTorsoAnimations;
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mLayer = data.readByte();
      this.mGender = data.readShort();
      this.mTorsoAnimationIDs = ToolkitHelpers.readResourceIDs(data, true);
      this.mAnimationIDs = ToolkitHelpers.readResourceIDs(data, true);
      this.mCollisionIDs = ToolkitHelpers.readResourceIDs(data, false);
      this.mShadowAnimation = data.readInt();
   }
}
