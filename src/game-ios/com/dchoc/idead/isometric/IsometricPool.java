package com.dchoc.idead.isometric;

import com.dchoc.idead.animation.Animations;
import com.dchoc.idead.items.Item;
import com.dchoc.toolkit.ToolkitHelpers;
import java.util.Vector;

public class IsometricPool {
   private Vector mObjects = new Vector(768);

   public IsometricObject pop(int type, Item item) {
      if (!this.mObjects.isEmpty() && this.mObjects.size() >= 1) {
         int fallback = -1;

         for (int i = 0; i < this.mObjects.size(); i++) {
            IsometricObject object = (IsometricObject)this.mObjects.elementAt(i);
            if (object.getType() == type) {
               if (item == null) {
                  return this.popObject(i);
               }

               fallback = i;
               if (object.getAnimations().getItem() == item) {
                  return this.popObject(i);
               }
            }
         }

         return fallback != -1 ? this.popObject(fallback) : null;
      } else {
         return null;
      }
   }

   public void push(IsometricObject object, boolean release) {
      if (object != null) {
         if (release) {
            this.releaseAnimations(object);
         }

         this.mObjects.addElement(object);
      }
   }

   public void releaseAllBut(Vector items) {
      for (int i = 0; i < this.mObjects.size(); i++) {
         IsometricObject object = (IsometricObject)this.mObjects.elementAt(i);
         Animations animations = object.getAnimations();
         if (animations.getItem() != null && !items.contains(animations.getItem())) {
            this.releaseAnimations(object);
         }
      }
   }

   public void releaseAll(boolean remove) {
      for (int i = 0; i < this.mObjects.size(); i++) {
         this.releaseAnimations((IsometricObject)this.mObjects.elementAt(i));
      }

      if (remove) {
         this.mObjects.removeAllElements();
      }
   }

   public IsometricObject popObject(int index) {
      IsometricObject object = (IsometricObject)this.mObjects.elementAt(index);
      ToolkitHelpers.fastRemove(index, this.mObjects);
      return object;
   }

   public void releaseAnimations(IsometricObject object) {
      if (object != null) {
         object.getAnimations().release();
      }
   }
}
