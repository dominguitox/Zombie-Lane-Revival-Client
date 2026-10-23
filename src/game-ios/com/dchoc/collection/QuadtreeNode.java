package com.dchoc.collection;

import com.dchoc.math.Rect2;
import com.dchoc.opengl.OGL;
import java.util.Vector;

public class QuadtreeNode {
   public static final int DEFAULT_NODE_CAPACITY = 8;
   public static final float MIN_NODE_HEIGHT = 96.0F;
   public static final float MIN_NODE_WIDTH = 96.0F;
   private Rect2 mArea;
   private QuadtreeNode[] mChilds;
   private Vector mObjects;
   private QuadtreeNode mParent;
   private QuadtreeNode mRoot;

   public QuadtreeNode(QuadtreeNode parent, QuadtreeNode root, float x, float y, float width, float height) {
      this.mParent = parent;
      if (root == null) {
         this.mRoot = this;
      } else {
         this.mRoot = root;
      }

      this.mArea = new Rect2(x, y, width, height);
      this.mObjects = new Vector(8);
   }

   private void createChilds() {
      float width = this.mArea.getWidth() / 2.0F;
      float height = this.mArea.getHeight() / 2.0F;
      if (!(width < 96.0F) && !(height < 96.0F)) {
         float x = this.mArea.getX();
         float y = this.mArea.getY();
         this.mChilds = new QuadtreeNode[4];
         this.mChilds[0] = new QuadtreeNode(this, this.mRoot, x, y, width, height);
         this.mChilds[1] = new QuadtreeNode(this, this.mRoot, x + width, y, width, height);
         this.mChilds[2] = new QuadtreeNode(this, this.mRoot, x, y + height, width, height);
         this.mChilds[3] = new QuadtreeNode(this, this.mRoot, x + width, y + height, width, height);
      }
   }

   public void add(QuadtreeObject object) {
      if (object != null) {
         float boundsX = object.getBoundsX();
         float boundsY = object.getBoundsY();
         float boundsWidth = object.getBoundsWidth();
         float boundsHeight = object.getBoundsHeight();
         if (this.mParent == null && !this.mArea.isInside(boundsX, boundsY, boundsWidth, boundsHeight)) {
            this.addObject(object);
         } else {
            if (this.mChilds == null) {
               this.createChilds();
            }

            if (this.mChilds != null) {
               for (int i = 0; i < this.mChilds.length; i++) {
                  if (this.mChilds[i].mArea.isInside(boundsX, boundsY, boundsWidth, boundsHeight)) {
                     this.mChilds[i].add(object);
                     return;
                  }
               }
            }

            this.addObject(object);
         }
      }
   }

   public void addObject(QuadtreeObject object) {
      if (object != null) {
         this.mObjects.addElement(object);
         object.setNode(this, this.mObjects.size() - 1);
      }
   }

   public void doDraw(float x, float y) {
      OGL.setColorRGB(16711680);
      OGL.drawRect((int)(this.mArea.getX() + x), (int)(this.mArea.getY() + y), (int)this.mArea.getWidth(), (int)this.mArea.getHeight());
      OGL.setColorRGB(255);

      for (int i = 0; i < this.mObjects.size(); i++) {
         QuadtreeObject object = (QuadtreeObject)this.mObjects.elementAt(i);
         OGL.drawRect((int)(object.getBoundsX() + x), (int)(object.getBoundsY() + y), (int)object.getBoundsWidth(), (int)object.getBoundsHeight());
      }

      if (this.mChilds != null) {
         for (int i = 0; i < this.mChilds.length; i++) {
            this.mChilds[i].doDraw(x, y);
         }
      }
   }

   public void getObjects(BinaryHeap heap) {
      if (this.mChilds != null) {
         for (int i = 0; i < this.mChilds.length; i++) {
            this.mChilds[i].getObjects(heap);
         }
      }

      for (int i = 0; i < this.mObjects.size(); i++) {
         heap.add((BinaryHeapObject)this.mObjects.elementAt(i));
      }
   }

   public void getObjects(Vector result) {
      if (this.mChilds != null) {
         for (int i = 0; i < this.mChilds.length; i++) {
            this.mChilds[i].getObjects(result);
         }
      }

      for (int i = 0; i < this.mObjects.size(); i++) {
         result.addElement(this.mObjects.elementAt(i));
      }
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public boolean isRoot() {
      return this.mParent != null;
   }

   public void query(float x, float y, Vector result) {
      if (!this.mObjects.isEmpty() || this.mChilds != null) {
         for (int i = 0; i < this.mObjects.size(); i++) {
            QuadtreeObject object = (QuadtreeObject)this.mObjects.elementAt(i);
            if (Rect2.isPointInside(x, y, object.getBoundsX(), object.getBoundsY(), object.getBoundsWidth(), object.getBoundsHeight())) {
               result.addElement(object);
            }
         }

         if (this.mChilds != null) {
            for (int i = 0; i < this.mChilds.length; i++) {
               QuadtreeNode child = this.mChilds[i];
               if (!child.isEmpty() && child.mArea.isInside(x, y)) {
                  child.query(x, y, result);
                  return;
               }
            }
         }
      }
   }

   public void query(Rect2 rect, BinaryHeap heap) {
      if (!this.mObjects.isEmpty() || this.mChilds != null) {
         for (int i = 0; i < this.mObjects.size(); i++) {
            QuadtreeObject object = (QuadtreeObject)this.mObjects.elementAt(i);
            if (rect.isIntersecting(object.getBoundsX(), object.getBoundsY(), object.getBoundsWidth(), object.getBoundsHeight())) {
               heap.add((BinaryHeapObject)object);
            }
         }

         if (this.mChilds != null) {
            for (int i = 0; i < this.mChilds.length; i++) {
               QuadtreeNode child = this.mChilds[i];
               if (!child.isEmpty()) {
                  if (child.mArea.isInside(rect)) {
                     child.query(rect, heap);
                     return;
                  }

                  if (rect.isInside(child.mArea)) {
                     child.getObjects(heap);
                  } else if (child.mArea.isIntersecting(rect)) {
                     child.query(rect, heap);
                  }
               }
            }
         }
      }
   }

   public void query(Rect2 rect, Vector result) {
      if (!this.mObjects.isEmpty() || this.mChilds != null) {
         for (int i = 0; i < this.mObjects.size(); i++) {
            QuadtreeObject object = (QuadtreeObject)this.mObjects.elementAt(i);
            if (rect.isIntersecting(object.getBoundsX(), object.getBoundsY(), object.getBoundsWidth(), object.getBoundsHeight())) {
               result.addElement(object);
            }
         }

         if (this.mChilds != null) {
            for (int i = 0; i < this.mChilds.length; i++) {
               QuadtreeNode child = this.mChilds[i];
               if (!child.isEmpty()) {
                  if (child.mArea.isInside(rect)) {
                     child.query(rect, result);
                     return;
                  }

                  if (rect.isInside(child.mArea)) {
                     child.getObjects(result);
                  } else if (child.mArea.isIntersecting(rect)) {
                     child.query(rect, result);
                  }
               }
            }
         }
      }
   }

   public void removeAllObjects() {
      for (int i = 0; i < this.mObjects.size(); i++) {
         ((QuadtreeObject)this.mObjects.elementAt(i)).setNode(null, -1);
      }

      this.mObjects.removeAllElements();
      if (this.mChilds != null) {
         for (int i = 0; i < this.mChilds.length; i++) {
            this.mChilds[i].removeAllObjects();
         }
      }
   }

   public void removeObject(QuadtreeObject object) {
      int index;
      if (object != null && !this.mObjects.isEmpty() && (index = object.getIndex()) != -1) {
         if (index != this.mObjects.size() - 1) {
            QuadtreeObject last = (QuadtreeObject)this.mObjects.elementAt(this.mObjects.size() - 1);
            this.mObjects.setElementAt(last, index);
            last.setNode(this, index);
         }

         this.mObjects.removeElementAt(this.mObjects.size() - 1);
         object.setNode(null, -1);
      }
   }

   public int size() {
      int size = this.mObjects.size();
      if (this.mChilds != null) {
         for (int i = 0; i < this.mChilds.length; i++) {
            size += this.mChilds[i].size();
         }
      }

      return size;
   }

   public void updateObject(QuadtreeObject object) {
      if (object != null) {
         float boundsX = object.getBoundsX();
         float boundsY = object.getBoundsY();
         float boundsWidth = object.getBoundsWidth();
         float boundsHeight = object.getBoundsHeight();
         if (!this.mArea.isInside(boundsX, boundsY, boundsWidth, boundsHeight)) {
            if (this.mParent != null) {
               this.removeObject(object);
               this.mRoot.add(object);
            }
         } else {
            if (this.mChilds == null) {
               this.createChilds();
            }

            if (this.mChilds != null) {
               for (int i = 0; i < this.mChilds.length; i++) {
                  if (this.mChilds[i].mArea.isInside(boundsX, boundsY, boundsWidth, boundsHeight)) {
                     this.removeObject(object);
                     this.mChilds[i].add(object);
                     return;
                  }
               }
            }
         }
      }
   }
}
