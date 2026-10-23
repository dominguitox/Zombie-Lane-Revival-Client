package com.dchoc.collection;

public class HashTableNode {
   private int mHash;
   private int mKey;
   private HashTableNode mNext = null;
   private Object mValue;

   public HashTableNode(int hash, int key, Object value) {
      this.mHash = hash;
      this.mKey = key;
      this.mValue = value;
   }

   public int getHash() {
      return this.mHash;
   }

   public int getKey() {
      return this.mKey;
   }

   public HashTableNode getNext() {
      return this.mNext;
   }

   public Object getValue() {
      return this.mValue;
   }

   public boolean hashNext() {
      return this.mNext != null;
   }

   public void setKey(int key) {
      this.mKey = key;
   }

   public void setNext(HashTableNode next) {
      this.mNext = next;
   }

   public void setValue(Object value) {
      this.mValue = value;
   }
}
