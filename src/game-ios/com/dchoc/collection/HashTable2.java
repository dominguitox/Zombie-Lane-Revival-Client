package com.dchoc.collection;

public class HashTable2 extends HashTable {
   public HashTable2(int capacity) {
      super(capacity);
   }

   @Override
   protected HashTableNode createNode(int hash, int key, Object value) {
      return new HashTableNode2(hash, key, value);
   }
}
