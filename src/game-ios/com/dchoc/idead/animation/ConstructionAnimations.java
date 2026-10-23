package com.dchoc.idead.animation;

import com.dchoc.idead.items.ConstructionItem;
import com.dchoc.idead.items.Item;

public class ConstructionAnimations extends Animations {
   public ConstructionAnimations() {
      this.mAnimation = -1;
   }

   @Override
   public void release() {
      ((ConstructionItem)this.mItem).unloadAnimation(this.mAnimation);
      this.mAnimation = -1;
   }

   @Override
   public void changeAnimation(int index, boolean looping) {
      if (this.mAnimation != index) {
         ConstructionItem item = (ConstructionItem)this.mItem;
         item.unloadAnimation(this.mAnimation);
         item.loadAnimation(index);
         this.mAnimation = index;
      }

      if (isValidAnimation(index, this.mAnimations)) {
         this.mAnimations[this.mAnimation].setAnimationFrame(0);
         this.mAnimations[this.mAnimation].setLooping(looping ? -1 : 1);
         this.clearMarks();
      }
   }

   @Override
   protected void loadAnimations(Item item) {
      if (this.mItem != item) {
         this.unloadAnimations();
         this.mItem = item;
         this.mAnimations = item.loadAnimations();
      }
   }

   @Override
   protected void unloadAnimations() {
   }
}
