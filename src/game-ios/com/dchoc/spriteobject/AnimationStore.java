package com.dchoc.spriteobject;

public class AnimationStore {
   private static final int HASHTABLE_SIZE = 673;
   public static final int LOOP_OFF = 0;
   public static final int LOOP_ON = 1;
   private static AnimationStore singleton;
   private int[] mCurrentResourcesIDs;
   private FastHashTable mHashTableForSprites;
   private int mLoadingIndex;

   private AnimationStore() {
   }
}
