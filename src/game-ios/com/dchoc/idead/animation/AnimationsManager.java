package com.dchoc.idead.animation;

import com.dchoc.collection.HashTable2;
import com.dchoc.collection.HashTableNode2;
import com.dchoc.spriteobject.SpriteObject;

public class AnimationsManager {
   public static final int INITIAL_SHARED_ANIMATIONS_CAPACITY = 2048;
   private static int mTimer;
   private static HashTable2 smSharedAnimations;

   public static void init() {
      smSharedAnimations = new HashTable2(2048);
   }

   public static int logicUpdate(int delta) {
      mTimer += delta;
      return 0;
   }

   public static int getTimer() {
      return mTimer;
   }

   public static SpriteObject[] load(int[] resources, int profile) {
      if (resources == null) {
         return null;
      }

      SpriteObject[] animations = new SpriteObject[resources.length];

      for (int i = 0; i < resources.length; i++) {
         if (resources[i] != -1) {
            animations[i] = new SpriteObject(resources[i], true, profile);
         }
      }

      return animations;
   }

   public static SpriteObject load(int resource, int profile) {
      return resource == -1 ? null : new SpriteObject(resource, true, profile);
   }

   public static SpriteObject[] load(int[] resources) {
      return load(resources, 0);
   }

   public static SpriteObject load(int resource) {
      return resource == -1 ? null : new SpriteObject(resource, true, 0);
   }

   public static void unload(SpriteObject[] animations) {
      if (animations != null) {
         for (int i = 0; i < animations.length; i++) {
            if (animations[i] != null) {
               animations[i].freeResources();
            }

            animations[i] = null;
         }
      }
   }

   public static SpriteObject loadShared(int resource, int profile) {
      HashTableNode2 node = (HashTableNode2)smSharedAnimations.getNode(resource);
      if (node == null) {
         if (resource == -1) {
            return null;
         }

         SpriteObject animation = new SpriteObject(resource, true, profile);
         node = (HashTableNode2)smSharedAnimations.add(resource, animation);
      }

      node.increaseReferences();
      return (SpriteObject)node.getValue();
   }

   public static SpriteObject loadShared(int resource) {
      return loadShared(resource, 0);
   }

   public static SpriteObject[] loadShared(int[] resources, int profile) {
      if (resources == null) {
         return null;
      }

      SpriteObject[] animations = new SpriteObject[resources.length];

      for (int i = 0; i < resources.length; i++) {
         animations[i] = loadShared(resources[i], profile);
      }

      return animations;
   }

   public static void unloadShared(int[] resources, SpriteObject[] animations) {
      if (animations != null && resources != null) {
         for (int i = 0; i < animations.length; i++) {
            SpriteObject animation = animations[i];
            if (animation != null && resources[i] != -1) {
               int resource = resources[i];
               HashTableNode2 node = (HashTableNode2)smSharedAnimations.getNode(resource);
               if (node != null) {
                  node.decreaseReferences();
                  if (node.getReferences() == 0) {
                     animation.freeResources();
                     smSharedAnimations.remove(resource);
                  }
               }
            }
         }
      }
   }

   public static void unloadShared(int resource, SpriteObject animation) {
      if (animation != null && resource != -1) {
         HashTableNode2 node = (HashTableNode2)smSharedAnimations.getNode(resource);
         if (node != null) {
            node.decreaseReferences();
            if (node.getReferences() == 0) {
               animation.freeResources();
               smSharedAnimations.remove(resource);
            }
         }
      }
   }
}
