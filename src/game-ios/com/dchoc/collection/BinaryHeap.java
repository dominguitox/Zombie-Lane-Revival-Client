package com.dchoc.collection;

public class BinaryHeap {
   private BinaryHeapObject[] mObjects;
   private int mSize = 0;

   public BinaryHeap(int capacity) {
      this.mObjects = new BinaryHeapObject[capacity];
   }

   public void add(BinaryHeapObject object) {
      if (object != null) {
         if (this.mObjects.length <= this.mSize) {
            this.extend(this.mObjects.length / 2);
         }

         this.mSize++;
         this.mObjects[0] = object;

         int i;
         for (i = this.mSize; object.compare(this.mObjects[i / 2]) < 0; i /= 2) {
            this.mObjects[i / 2].setHeap(i);
            this.mObjects[i] = this.mObjects[i / 2];
         }

         this.mObjects[i] = object;
         object.setHeap(i);
      }
   }

   public void extend(int count) {
      BinaryHeapObject[] objects = new BinaryHeapObject[this.mObjects.length + count];

      for (int i = 0; i < this.mObjects.length; i++) {
         objects[i] = this.mObjects[i];
      }

      this.mObjects = objects;
   }

   public BinaryHeapObject get(int index) {
      return index > 0 && index < this.mObjects.length ? this.mObjects[index] : null;
   }

   public int getLeftChild(int index) {
      int child = index * 2;
      return child < this.mObjects.length && this.mObjects[child] != null ? child : -1;
   }

   public int getRightChild(int index) {
      int child = index * 2 + 1;
      return child < this.mObjects.length && this.mObjects[child] != null ? child : -1;
   }

   public boolean isEmpty() {
      return this.mSize == 0;
   }

   public void percolateUp(BinaryHeapObject object) {
      if (object != null) {
         for (int i = object.getHeap(); i > 1 && this.mObjects[i].compare(this.mObjects[i / 2]) < 0; i /= 2) {
            BinaryHeapObject moved = this.mObjects[i];
            this.mObjects[i / 2].setHeap(i);
            this.mObjects[i] = this.mObjects[i / 2];
            moved.setHeap(i / 2);
         }
      }
   }

   public void removeAll(boolean clear) {
      this.mSize = 0;
      if (clear) {
         for (int i = 0; i < this.mObjects.length; i++) {
            this.mObjects[i] = null;
         }
      }
   }

   public BinaryHeapObject removeFirst() {
      if (this.mSize == 0) {
         return null;
      }

      BinaryHeapObject first = this.mObjects[1];
      this.mObjects[this.mSize].setHeap(1);
      this.mObjects[1] = this.mObjects[this.mSize];
      this.mObjects[this.mSize] = null;
      this.mSize--;
      int hole = 1;

      while (hole * 2 <= this.mSize) {
         int i = hole * 2;
         if (i != this.mSize && this.mObjects[i].compare(this.mObjects[i + 1]) > 0) {
            i++;
         }

         if (this.mObjects[hole].compare(this.mObjects[i]) <= 0) {
            break;
         }

         BinaryHeapObject moved = this.mObjects[hole];
         this.mObjects[i].setHeap(hole);
         this.mObjects[hole] = this.mObjects[i];
         moved.setHeap(i);
         this.mObjects[i] = moved;
         hole = i;
      }

      return first;
   }

   public int size() {
      return this.mSize;
   }
}
