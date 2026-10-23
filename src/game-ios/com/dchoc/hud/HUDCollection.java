package com.dchoc.hud;

import com.dchoc.idead.touch.TouchEvent;
import java.util.Vector;

public class HUDCollection extends HUDObject {
   public static final int INITIAL_OBJECTS_CAPACITY = 7;
   protected Vector mObjects = new Vector(7);

   public HUDCollection() {
      super((byte)10);
   }

   public void addObject(HUDObject object) {
      this.mObjects.addElement(object);
      object.setParent(this);
   }

   public void removeObject(HUDObject object) {
      this.mObjects.removeElement(object);
   }

   public void removeObjects() {
      this.mObjects.removeAllElements();
   }

   public void removeObject(int id) {
      if (this.mObjects.size() > 0) {
         int i = 0;

         do {
            HUDObject object = (HUDObject)this.mObjects.elementAt(i);
            if (object.getId() != -1 && object.getId() == id) {
               this.mObjects.removeElementAt(i);
               return;
            }
         } while (++i < this.mObjects.size());
      }
   }

   public HUDObject getObject(int id) {
      if (this.mObjects.size() > 0) {
         int i = 0;

         do {
            HUDObject object = (HUDObject)this.mObjects.elementAt(i);
            if (object.getId() != -1 && object.getId() == id) {
               return object;
            }
         } while (++i < this.mObjects.size());
      }

      return null;
   }

   public Vector getObjects() {
      return this.mObjects;
   }

   public void setObjectVisible(int id, boolean visible) {
      HUDObject object = this.getObject(id);
      if (object != null) {
         object.setVisible(visible);
      }
   }

   public void setObjectEnabled(int id, boolean enabled) {
      HUDObject object = this.getObject(id);
      if (object != null) {
         ((HUDButton)object).setEnabled(enabled);
      }
   }

   public void setAllVisible(boolean visible) {
      if (this.mObjects.size() >= 1) {
         int i = 0;

         do {
            ((HUDObject)this.mObjects.elementAt(i)).setVisible(visible);
         } while (++i < this.mObjects.size());
      }
   }

   @Override
   public void callback(int id) {
      if (this.mParent != null) {
         this.mParent.callback(id);
      }
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         if (this.mObjects.size() < 1) {
            return;
         }

         int i = 0;

         do {
            HUDObject object = (HUDObject)this.mObjects.elementAt(i);
            if (object.isVisible()) {
               object.draw();
            }
         } while (++i < this.mObjects.size());
      }
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.mObjects.size() >= 1) {
         int i = 0;

         do {
            ((HUDObject)this.mObjects.elementAt(i)).logicUpdate(delta);
         } while (++i < this.mObjects.size());
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible()) {
         int i = this.mObjects.size();

         do {
            if (i < 1) {
               return;
            }

            HUDObject object = (HUDObject)this.mObjects.elementAt(--i);
            if (object.isVisible()) {
               object.pointerEvent(event);
            }
         } while (!event.isConsumed());
      }
   }

   @Override
   public void freeImages() {
      if (this.mObjects.size() >= 1) {
         int i = 0;

         do {
            ((HUDObject)this.mObjects.elementAt(i)).freeImages();
         } while (++i < this.mObjects.size());
      }
   }
}
