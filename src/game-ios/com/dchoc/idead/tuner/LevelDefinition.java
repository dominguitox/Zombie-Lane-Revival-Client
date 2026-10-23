package com.dchoc.idead.tuner;

import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.toolkit.DChocByteArray;
import java.io.EOFException;
import java.util.Vector;

public class LevelDefinition {
   private byte mLevel;
   private int mXpLimit;
   private byte mMaxZombie;
   private byte mMaxZombieRabbit;
   private byte mMaxZombieClownHome;
   private byte mMaxZombieClown;
   private byte mMaxZombieCityEngineer;
   private byte mMaxZombieDowntown;
   private byte mMaxZombieCrashSite;
   private byte mMaxZombieCure;
   private byte mMaxTrash;
   private byte mMaxTrashDowntown;
   private byte mMaxPaper;
   private byte mMaxDocNotes;
   private byte mMaxBrokenBush;
   private byte mMaxBrokenBushDowntown;
   private byte mMaxRubble;
   private byte mMaxRubbleDowntown;
   private byte mMaxGrass;
   private byte mMaxGrassDowntown;
   private byte mMaxOoze;
   private byte mMaxEnergyIncrease = 0;
   private short mCash = 0;
   private short mCoins = 0;

   public LevelDefinition(DChocByteArray data) throws EOFException {
      this.loadData(data);
   }

   public int getLevel() {
      return this.mLevel;
   }

   public int getXpLimit() {
      return this.mXpLimit;
   }

   public int getMaxZombie() {
      return this.mMaxZombie;
   }

   public int getMaxZombieRabbit() {
      return this.mMaxZombieRabbit;
   }

   public int getMaxZombieClownHome() {
      return this.mMaxZombieClownHome;
   }

   public int getMaxZombieClown() {
      return this.mMaxZombieClown;
   }

   public int getMaxZombieCityEngineer() {
      return this.mMaxZombieCityEngineer;
   }

   public int getMaxZombieDowntown() {
      return this.mMaxZombieDowntown;
   }

   public int getMaxZombieCrashSite() {
      Vector visibleAreas = IsometricUtils.getFloor().getVisibleAreas();
      return !visibleAreas.isEmpty() ? Math.min(visibleAreas.size() >> 1, this.mMaxZombieCrashSite) : this.mMaxZombieCrashSite;
   }

   public int getMaxZombieCure() {
      return this.mMaxZombieCure;
   }

   public int getMaxTrash() {
      return this.mMaxTrash;
   }

   public int getMaxTrashDowntown() {
      return this.mMaxTrashDowntown;
   }

   public int getMaxPaper() {
      return this.mMaxPaper;
   }

   public int getMaxDocNotes() {
      return this.mMaxDocNotes;
   }

   public int getMaxBrokenBush() {
      return this.mMaxBrokenBush;
   }

   public int getMaxBrokenBushDowntown() {
      return this.mMaxBrokenBushDowntown;
   }

   public int getMaxRubble() {
      return this.mMaxRubble;
   }

   public int getMaxRubbleDowntown() {
      return this.mMaxRubbleDowntown;
   }

   public int getMaxGrass() {
      return this.mMaxGrass;
   }

   public int getMaxGrassDowntown() {
      return this.mMaxGrassDowntown;
   }

   public int getMaxOoze() {
      return this.mMaxOoze;
   }

   public int getMaxEnergyIncrease() {
      return this.mMaxEnergyIncrease;
   }

   public int getCash() {
      return this.mCash;
   }

   public int getCoins() {
      return this.mCoins;
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mLevel = data.readByte();
      this.mXpLimit = data.readInt();
      this.mMaxZombie = data.readByte();
      this.mMaxZombieClownHome = data.readByte();
      this.mMaxZombieClown = data.readByte();
      this.mMaxTrash = data.readByte();
      this.mMaxPaper = data.readByte();
      this.mMaxBrokenBush = data.readByte();
      this.mMaxRubble = data.readByte();
      this.mMaxZombieRabbit = data.readByte();
      this.mMaxGrass = data.readByte();
      this.mMaxEnergyIncrease = data.readByte();
      this.mCash = data.readShort();
      this.mCoins = data.readShort();
      this.mMaxZombieCityEngineer = data.readByte();
      this.mMaxZombieDowntown = data.readByte();
      this.mMaxRubbleDowntown = data.readByte();
      this.mMaxGrassDowntown = data.readByte();
      this.mMaxBrokenBushDowntown = data.readByte();
      this.mMaxTrashDowntown = data.readByte();
      this.mMaxZombieCrashSite = data.readByte();
      this.mMaxZombieCure = data.readByte();
      this.mMaxDocNotes = data.readByte();
      this.mMaxOoze = data.readByte();
   }
}
