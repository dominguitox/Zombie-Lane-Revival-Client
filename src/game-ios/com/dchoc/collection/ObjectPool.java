package com.dchoc.collection;

import java.util.Vector;

public class ObjectPool {
   protected Vector mPool;

   public ObjectPool(int capacity) {
      this.mPool = new Vector(capacity);
   }

   public void add(Object obj) {
      this.mPool.addElement(obj);
   }

   public int capacity() {
      return this.mPool.capacity();
   }

   public Object create() {
      if (this.mPool.isEmpty()) {
         return null;
      }

      Object last = this.mPool.lastElement();
      this.mPool.removeElementAt(this.mPool.size() - 1);
      return last;
   }

   public boolean isEmpty() {
      return this.mPool.isEmpty();
   }

   public int size() {
      return this.mPool.size();
   }
}
