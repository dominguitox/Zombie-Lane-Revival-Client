package com.dchoc.idead.pathfinding;

import com.dchoc.collection.BinaryHeapObject;

public class PathNode implements BinaryHeapObject {
   protected byte mColumn;
   protected byte mRow;
   protected boolean mOccupied;
   private boolean mAccessible;
   private boolean mWalkable;
   private short mG;
   private short mH;
   private short mPathID;
   private short mAccessibleID;
   private int mHeap;
   private boolean mOpen;
   private boolean mClosed;
   private PathNode mParent;

   public PathNode(byte column, byte row) {
      this.mColumn = column;
      this.mRow = row;
      this.mAccessibleID = 0;
      this.mPathID = 0;
      this.mHeap = -1;
      this.mOccupied = false;
      this.mAccessible = false;
      this.mWalkable = true;
   }

   public void setPathID(short id) {
      if (this.mPathID != id) {
         this.mPathID = id;
         this.mH = 32767;
         this.mG = 32767;
         this.mHeap = -1;
         this.mClosed = false;
         this.mOpen = false;
         this.mParent = null;
      }
   }

   public int getPathID() {
      return this.mPathID;
   }

   public void setAccessibleID(short id) {
      if (this.mAccessibleID != id) {
         this.mAccessibleID = id;
         this.mAccessible = false;
      }
   }

   public int getAccessibleID() {
      return this.mAccessibleID;
   }

   public void setAccessible(boolean accessible) {
      this.mAccessible = accessible;
   }

   public boolean isAccessible() {
      return this.mAccessibleID != Pathfinder.getInstance().getAccessibleID() ? false : this.mAccessible;
   }

   public void setOccupied(boolean occupied) {
      this.mOccupied = occupied;
   }

   public boolean isOccupied() {
      return this.mOccupied;
   }

   public void setWalkable(boolean walkable) {
      this.mWalkable = walkable;
   }

   public boolean isWalkable() {
      return this.mWalkable;
   }

   public void setCost(short g, short h) {
      this.mG = g;
      this.mH = h;
   }

   public int getCost() {
      return (short)(this.mH + this.mG);
   }

   public int getG() {
      return this.mG;
   }

   public int getH() {
      return this.mH;
   }

   public void setOpen(boolean open) {
      this.mOpen = open;
   }

   public boolean isOpen() {
      return this.mOpen;
   }

   public void setClosed(boolean closed) {
      this.mClosed = closed;
   }

   public boolean isClosed() {
      return this.mClosed;
   }

   public void setParent(PathNode parent) {
      this.mParent = parent;
   }

   public PathNode getParent() {
      return this.mParent;
   }

   public int getColumn() {
      return this.mColumn;
   }

   public int getRow() {
      return this.mRow;
   }

   @Override
   public void setHeap(int heap) {
      this.mHeap = heap;
   }

   @Override
   public int getHeap() {
      return this.mHeap;
   }

   @Override
   public int compare(BinaryHeapObject o) {
      return this.getCost() - ((PathNode)o).getCost();
   }
}
