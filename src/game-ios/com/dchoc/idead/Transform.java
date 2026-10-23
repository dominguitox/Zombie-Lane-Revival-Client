package com.dchoc.idead;

import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.math.Vec2;

public class Transform {
   private static Vec2 smTemplate;
   private static TransformMatrix smToScreen;
   private static TransformMatrix smToWorld;

   public static void init() {
      smToScreen = new TransformMatrix(40.0F, 26.0F, -10.0F, 26.0F, 0.0F, 0.0F);
      smToScreen.scale(IsometricTile.getTileScale(), IsometricTile.getTileScale());
      smToScreen.scale(1.0F / IsometricTile.getTileSize(), 1.0F / IsometricTile.getTileSize());
      smToWorld = smToScreen.clone();
      smToWorld.invert();
      smTemplate = new Vec2();
   }

   public static void toScreen(Vec2 point) {
      smToScreen.transform(point);
   }

   public static Vec2 toScreen(float x, float y) {
      smTemplate.set(x, y);
      smToScreen.transform(smTemplate);
      return smTemplate;
   }

   public static float toScreenX(float x, float y) {
      smTemplate.set(x, y);
      smToScreen.transform(smTemplate);
      return smTemplate.getX();
   }

   public static float toScreenY(float x, float y) {
      smTemplate.set(x, y);
      smToScreen.transform(smTemplate);
      return smTemplate.getY();
   }

   public static Vec2 toWorld(float x, float y) {
      smTemplate.set(x, y);
      smToWorld.transform(smTemplate);
      return smTemplate;
   }

   public static float toWorldX(float x, float y) {
      smTemplate.set(x, y);
      smToWorld.transform(smTemplate);
      return smTemplate.getX();
   }

   public static float toWorldY(float x, float y) {
      smTemplate.set(x, y);
      smToWorld.transform(smTemplate);
      return smTemplate.getY();
   }
}
