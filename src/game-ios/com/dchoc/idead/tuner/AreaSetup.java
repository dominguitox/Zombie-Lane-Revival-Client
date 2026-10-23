package com.dchoc.idead.tuner;

import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class AreaSetup implements CsvIDs {
   private short mName;
   private byte mAreaWidth;
   private byte mAreaHeight;
   private int mMusic = -1;
   private int mBackground = -1;
   private int mSky = -1;
   private byte mPlayerStartX;
   private byte mPlayerStartY;
   private int[][] mShadows;
   private short[] mNonAccessibleAreas = null;
   private short[] mNonWalkableAreas = null;

   public AreaSetup(DChocByteArray data) throws EOFException {
      this.mName = data.readShort();
      this.mAreaWidth = data.readByte();
      this.mAreaHeight = data.readByte();
      this.mMusic = data.readInt();
      this.mBackground = data.readInt();
      this.mSky = data.readInt();
      this.mShadows = ToolkitHelpers.readIntValueList(data);
      this.mPlayerStartX = data.readByte();
      this.mPlayerStartY = data.readByte();
      this.mNonAccessibleAreas = ToolkitHelpers.readShortArray(data);
      this.mNonWalkableAreas = ToolkitHelpers.readShortArray(data);
   }

   public boolean isHome() {
      return this.mName == -1;
   }

   public int getName() {
      return this.mName;
   }

   public int getAreaWidth() {
      return this.mAreaWidth;
   }

   public int getAreaHeight() {
      return this.mAreaHeight;
   }

   public int getMusic() {
      return this.mMusic;
   }

   public int getBackground() {
      return this.mBackground;
   }

   public int[][] getShadows() {
      return this.mShadows;
   }

   public int getSky() {
      return this.mSky;
   }

   public int getPlayerStartX() {
      return this.mPlayerStartX;
   }

   public int getPlayerStartY() {
      return this.mPlayerStartY;
   }

   public short[] getNonAccessibleAreas() {
      return this.mNonAccessibleAreas;
   }

   public void setNonAccessibleAreas(short[] areas) {
      this.mNonAccessibleAreas = areas;
   }

   public short[] getNonWalkableAreas() {
      return this.mNonWalkableAreas;
   }

   public void setNonWalkableAreas(short[] areas) {
      this.mNonWalkableAreas = areas;
   }

   public void loadData(DChocByteArray data) throws EOFException {
      this.mName = data.readShort();
      this.mAreaWidth = data.readByte();
      this.mAreaHeight = data.readByte();
      this.mMusic = data.readInt();
      this.mBackground = data.readInt();
      this.mSky = data.readInt();
      this.mShadows = ToolkitHelpers.readIntValueList(data);
      this.mPlayerStartX = data.readByte();
      this.mPlayerStartY = data.readByte();
      this.mNonAccessibleAreas = ToolkitHelpers.readShortArray(data);
      this.mNonWalkableAreas = ToolkitHelpers.readShortArray(data);
   }
}
