package com.dchoc.hud;

import com.dchoc.idead.touch.TouchEvent;
import java.util.Vector;

public class HUDSetOfSlots extends HUDItem {
   private Vector mSlots;
   private int mMaxNumOfSlots;

   public HUDSetOfSlots(int width, int maxNumOfSlots) {
      this.mWidth = width;
      this.mMaxNumOfSlots = maxNumOfSlots;
      this.mSlots = new Vector(this.mMaxNumOfSlots);
      this.mObjects = new HUDObject[this.mMaxNumOfSlots];
   }

   public Vector getItems() {
      return this.mSlots;
   }

   public boolean isFull() {
      return this.mSlots.size() == this.mMaxNumOfSlots;
   }

   public boolean isEmpty() {
      return this.mSlots.size() == 0;
   }

   public void addSlot(Object object, boolean secondPrice) {
      Vector slots = this.mSlots;
      slots.addElement(new HUDSlot(object, this, secondPrice));
      this.updatePositions();
   }

   public void addSlot(HUDSlot slot) {
      this.mSlots.addElement(slot);
      this.updatePositions();
   }

   public void removeSlot(int index) {
      this.mSlots.removeElementAt(index);
      this.updatePositions();
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.mSlots.size() >= 1) {
         int i = 0;

         do {
            ((HUDSlot)this.mSlots.elementAt(i)).pointerEvent(event);
         } while (++i < this.mSlots.size());
      }
   }

   public static void addSlotToSet(Vector sets, Object object, int width, int maxNumOfSlots, boolean secondPrice) {
      if (sets.size() == 0) {
         sets.addElement(new HUDSetOfSlots(width, maxNumOfSlots));
      }

      HUDSetOfSlots set = (HUDSetOfSlots)sets.lastElement();
      if (set.isFull()) {
         set = new HUDSetOfSlots(width, maxNumOfSlots);
         sets.addElement(set);
      }

      set.addSlot(object, secondPrice);
   }

   @Override
   public void draw() {
      super.draw();
      this.drawDebug(16711680);
   }

   @Override
   public void refresh() {
      if (this.mSlots.size() >= 1) {
         int i = 0;

         do {
            ((HUDSlot)this.mSlots.elementAt(i)).refresh();
         } while (++i < this.mSlots.size());
      }
   }

   public HUDTouchArea getParentTouchArea() {
      return this.mTouchAreaParent;
   }

   public int getMaxNumOfSlots() {
      return this.mMaxNumOfSlots;
   }

   public void updatePositions() {
      if (!this.isEmpty()) {
         int x = 0;
         int width = ((HUDObject)this.mSlots.elementAt(0)).getWidth();
         float max = this.mMaxNumOfSlots;
         float total = width * max;
         if (this.mSlots.size() > 0) {
            float space = (this.mWidth - total) / (max + 1.0F);
            int i = 0;

            do {
               int position = (int)(x + space * (i + 1));
               ((HUDObject)this.mSlots.elementAt(i)).setPosition(position, 0);
               x += width;
            } while (++i < this.mSlots.size());
         }

         if (this.mSlots.size() > 0) {
            int i = 0;

            do {
               this.mObjects[i] = (HUDObject)this.mSlots.elementAt(i);
            } while (++i < this.mSlots.size());
         }

         this.mHeight = ((HUDObject)this.mSlots.elementAt(0)).getHeight();
      }
   }
}
