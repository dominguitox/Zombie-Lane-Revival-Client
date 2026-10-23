package com.dchoc.collection;

public class HashTable {
   public static final int HASH_VALUE = Integer.MAX_VALUE;
   public static final float LOAD_FACTOR = 0.75F;
   private HashTableNode[] mNodes;
   private int mSize = 0;
   private int mThreshold;

   public HashTable(int capacity) {
      this.mNodes = new HashTableNode[capacity];
      this.mThreshold = (int)(capacity * 0.75F);
   }

   private void rehash() {
      HashTableNode[] oldNodes = this.mNodes;
      int length = oldNodes.length * 2 + 1;
      this.mNodes = new HashTableNode[length];
      this.mThreshold = (int)(length * 0.75F);

      for (HashTableNode bucket : oldNodes) {
         HashTableNode node = bucket;

         while (node != null) {
            HashTableNode next = node.getNext();
            int index = (node.getHash() & 2147483647) % this.mNodes.length;
            node.setNext(this.mNodes[index]);
            this.mNodes[index] = node;
            node = next;
         }
      }
   }

   public HashTableNode add(int hash, Object value) {
      if (value == null) {
         return null;
      }

      int index = (hash & 2147483647) % this.mNodes.length;

      for (HashTableNode node = this.mNodes[index]; node != null; node = node.getNext()) {
         if (node.getHash() == hash && node.getKey() == hash) {
            node.setValue(value);
            return node;
         }
      }

      if (this.mThreshold <= this.mSize) {
         this.rehash();
         return this.add(hash, value);
      } else {
         HashTableNode created = this.createNode(hash, hash, value);
         created.setNext(this.mNodes[index]);
         this.mNodes[index] = created;
         this.mSize++;
         return created;
      }
   }

   public HashTableNode add(Object key, Object value) {
      return this.add(key.hashCode(), value);
   }

   public boolean contains(Object value) {
      if (value != null && this.mSize != 0) {
         for (int i = 0; i < this.mNodes.length; i++) {
            for (HashTableNode node = this.mNodes[i]; node != null; node = node.getNext()) {
               if (node.getValue().equals(value)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   protected HashTableNode createNode(int hash, int key, Object value) {
      return new HashTableNode(hash, key, value);
   }

   public Object get(int key) {
      HashTableNode node = this.getNode(key);
      return node != null ? node.getValue() : null;
   }

   public Object get(Object key) {
      return this.get(key.hashCode());
   }

   public HashTableNode getNode(int key) {
      if (this.mSize == 0) {
         return null;
      }

      for (HashTableNode node = this.mNodes[(key & 2147483647) % this.mNodes.length]; node != null; node = node.getNext()) {
         if (node.getKey() == key) {
            return node;
         }
      }

      return null;
   }

   public HashTableNode[] getNodes() {
      return this.mNodes;
   }

   public boolean isEmpty() {
      return this.mSize == 0;
   }

   public void removeAll() {
   }

   public void remove(int key) {
      if (this.mSize != 0) {
         int index = (key & 2147483647) % this.mNodes.length;
         HashTableNode previous = null;

         for (HashTableNode node = this.mNodes[index]; node != null; node = node.getNext()) {
            if (node.getHash() == key && node.getKey() == key) {
               if (previous != null) {
                  previous.setNext(node.getNext());
               } else {
                  this.mNodes[index] = node.getNext();
               }

               this.mSize--;
            }

            previous = node;
         }
      }
   }

   public void remove(Object key) {
      this.remove(key.hashCode());
   }

   public int size() {
      return this.mSize;
   }

   @Override
   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("{");
      boolean first = true;

      for (int i = 0; i < this.mNodes.length; i++) {
         for (HashTableNode node = this.mNodes[i]; node != null; node = node.getNext()) {
            if (!first) {
               buffer.append(", ");
            }

            first = false;
            buffer.append("[").append(node.getKey()).append(", ").append(node.getValue()).append("]");
         }
      }

      buffer.append("}");
      return buffer.toString();
   }
}
