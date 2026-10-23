package com.dchoc.collection;

import com.dchoc.math.Rect2;
import java.util.Vector;

public class Quadtree {
   public static final int INITIAL_QUERY_CAPACITY = 256;
   private Vector mQuery = new Vector(256);
   private QuadtreeNode mRoot;

   public Quadtree(float x, float y, float width, float height) {
      this.mRoot = new QuadtreeNode(null, null, x, y, width, height);
   }

   public void add(QuadtreeObject object) {
      QuadtreeNode node = object.getNode();
      if (node != null) {
         node.removeObject(object);
      }

      this.mRoot.add(object);
   }

   public void doDraw(float x, float y) {
      this.mRoot.doDraw(x, y);
   }

   public Vector query(float x, float y) {
      this.mQuery.removeAllElements();
      this.query(x, y, this.mQuery);
      return this.mQuery;
   }

   public Vector query(Rect2 rect) {
      this.mQuery.removeAllElements();
      this.query(rect, this.mQuery);
      return this.mQuery;
   }

   public void query(float x, float y, Vector result) {
      this.mRoot.query(x, y, result);
   }

   public void query(Rect2 rect, Vector result) {
      this.mRoot.query(rect, result);
   }

   public Vector queryAll() {
      this.mQuery.removeAllElements();
      this.queryAll(this.mQuery);
      return this.mQuery;
   }

   public void queryAll(Vector result) {
      this.mRoot.getObjects(result);
   }

   public void remove(QuadtreeObject object) {
      QuadtreeNode node = object.getNode();
      if (node != null) {
         node.removeObject(object);
      }
   }

   public void removeAll() {
      this.mRoot.removeAllObjects();
   }

   public void update(QuadtreeObject object) {
      QuadtreeNode node = object.getNode();
      if (node != null) {
         node.updateObject(object);
      }
   }
}
