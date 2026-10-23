package com.dchoc.idead.spawning;

import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.Item;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class LocationDefinition {
   public static final int LOCATION_INVALID = -1;
   private short mID = -1;
   private byte mX;
   private byte mY;
   private byte mWidth;
   private byte mHeight;
   private short mGamefield;

   public LocationDefinition(DChocByteArray dChocByteArray) throws EOFException {
      this.loadData(dChocByteArray);
   }

   public int getID() {
      return this.mID;
   }

   public int getGamefield() {
      return this.mGamefield;
   }

   public IsometricTile calculateTile(Item item) {
      if (this.mX < 0 && this.mY < 0) {
         return IsometricUtils.getRandomTile(item);
      }

      if (this.mWidth < 0 && this.mHeight < 0) {
         return IsometricUtils.getTile(this.mX, this.mY);
      }

      int x = 0;
      if (this.mX >= 0) {
         x = ToolkitHelpers.getRandomInt(this.mX, this.mX + this.mWidth);
      }

      int y = 0;
      if (this.mY >= 0) {
         y = ToolkitHelpers.getRandomInt(this.mY, this.mY + this.mHeight);
      }

      return IsometricUtils.getTile(item, x, y);
   }

   protected void loadData(DChocByteArray dChocByteArray) throws EOFException {
      this.mID = dChocByteArray.readShort();
      this.mX = dChocByteArray.readByte();
      this.mY = dChocByteArray.readByte();
      this.mWidth = dChocByteArray.readByte();
      this.mHeight = dChocByteArray.readByte();
      dChocByteArray.readBoolean();
      this.mGamefield = dChocByteArray.readShort();
   }
}
