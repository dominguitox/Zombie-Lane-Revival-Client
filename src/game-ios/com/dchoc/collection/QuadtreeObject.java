package com.dchoc.collection;

public interface QuadtreeObject {
   float getBoundsHeight();

   float getBoundsWidth();

   float getBoundsX();

   float getBoundsY();

   int getIndex();

   QuadtreeNode getNode();

   void setNode(QuadtreeNode var1, int var2);
}
