package com.dchoc.idead.items;

import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class FenceItem extends DestructibleItem {
   private byte mMaxHealth;
   private byte mDamageProbability;
   private short mDestrcutibleItem;

   protected FenceItem(DChocByteArray data) throws EOFException {
      this.mType = 1104;
      this.mSubType = -1;
      this.loadData(data);
   }

   @Override
   public int getMaxHealth() {
      return this.mMaxHealth;
   }

   public int getDamageProbability() {
      return this.mDamageProbability;
   }

   public int getDestrcutibleItem() {
      return this.mDestrcutibleItem;
   }

   @Override
   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mLayer = data.readByte();
      this.mTitle = data.readShort();
      this.mDescription = data.readShort();
      this.mAction = data.readShort();
      this.mDestrcutibleItem = data.readShort();
      data.readByte();
      this.mPrice = data.readInt();
      this.mCurrency = data.readShort();
      data.readInt();
      data.readShort();
      data.readBoolean();
      this.mTileSizeX = data.readByte();
      this.mTileSizeY = data.readByte();
      this.mTileSizeZ = data.readByte();
      this.mWalkable = data.readBoolean();
      this.mMaxHealth = data.readByte();
      this.mDamageProbability = data.readByte();
      this.mRewardXp = data.readShort();
      data.readByte();
      data.readShort();
      this.mIconAnimationID = data.readInt();
      this.mAnimationIDs = ToolkitHelpers.readResourceIDs(data, true);
      this.mCollisionIDs = ToolkitHelpers.readResourceIDs(data, true);
   }
}
