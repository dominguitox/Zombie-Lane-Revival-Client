package com.dchoc.collection;

public class HashTableNode2 extends HashTableNode {
   private short mReferences = 0;

   public HashTableNode2(int hash, int key, Object value) {
      super(hash, key, value);
   }

   public void decreaseReferences() {
      if (this.mReferences != -32768) {
         this.mReferences--;
      }
   }

   public short getReferences() {
      return this.mReferences;
   }

   public void increaseReferences() {
      if (this.mReferences != 32767) {
         this.mReferences++;
      }
   }
}
