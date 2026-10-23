package com.dchoc.idead.isometric;

import com.dchoc.hud.HUD;
import com.dchoc.hud.HUDMissionIcon;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.Transform;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.areas.Area;
import com.dchoc.idead.events.EventManager;
import com.dchoc.idead.events.TimedEvent;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tuner.EdgeAreaDefinition;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.math.Rect2;
import com.dchoc.math.Vec2;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;
import java.util.Vector;

public class IsometricFloor {
   private static final int[] SIDES = new int[]{3884, 3881, 5528, 3888, 3892};
   private static final float[] POLYGON_GRADIENT = new float[]{
      0.0F, 0.0F, 0.0F, 1.0F, 0.56F, 0.67F, 0.05F, 0.0F, 0.56F, 0.67F, 0.05F, 0.0F, 0.56F, 0.67F, 0.05F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F
   };
   private EdgeAreaDefinition[] mEdgeAreas;
   private SpriteObject[] mEdgeAnimations;
   private SpriteObject[] mEdgeCollisions;
   private SpriteObject mEdgePositions;
   protected SpriteObject mFloor;
   private SpriteObject[] mOldEdgeAnimations;
   private IsometricScene mScene;
   private SpriteObject mShadow;
   private SpriteObject mSky;
   private Vector mVisibleAreas;

   public IsometricFloor(IsometricScene scene) {
      this.mScene = scene;
      int profile = DeviceWrapper.useHDIngameGraphics() ? 2 : 1;
      this.mFloor = AnimationsManager.loadShared(65647, profile);
      this.mEdgePositions = AnimationsManager.load(78995, profile);
      this.mSky = AnimationsManager.loadShared(65701);
      this.mSky.setAnimationCustomScale(1.0F);
      this.mShadow = null;
      this.mEdgeAnimations = new SpriteObject[5];
      this.mEdgeCollisions = new SpriteObject[5];
      this.mOldEdgeAnimations = new SpriteObject[5];
      this.mEdgeAreas = new EdgeAreaDefinition[5];
      this.mVisibleAreas = new Vector(25);
   }

   public void updateEdges() {
      boolean hd = DeviceWrapper.useHDIngameGraphics();
      Area area = GameEngineUtils.getArea();
      this.updateSky(area.getSky());
      this.updateBackground(area.getBackground());
      this.updateShadow(area.getShadows());
      this.updateVisibleAreas();

      for (int i = 0; i < this.mEdgeAnimations.length; i++) {
         this.mOldEdgeAnimations[i] = this.mEdgeAnimations[i];
         this.mEdgeAnimations[i] = null;
      }

      int profile = hd ? 2 : 1;

      for (int i = 0; i < this.mEdgeAreas.length; i++) {
         this.mEdgeAreas[i] = null;
      }

      Vector definitions = Tuner.getEdgeAreas();

      for (int i = 0; i < definitions.size(); i++) {
         EdgeAreaDefinition definition = (EdgeAreaDefinition)definitions.elementAt(i);
         if (definition.getAreaName() == area.getName()) {
            int side = definition.getSide();
            int index = -1;

            for (int j = 0; j < SIDES.length; j++) {
               if (SIDES[j] == side) {
                  index = j;
                  break;
               }
            }

            if (index >= 0) {
               this.mEdgeAreas[index] = definition;
               int language = Toolkit.getSelectedLanguageIndex();
               int animation = language == 0 ? definition.getAnimationUnlockedEN() : definition.getAnimationUnlocked();
               if (definition.isLockedByLevel()) {
                  animation = language == 0 ? definition.getAnimationLockedEN() : definition.getAnimationLocked();
               }

               this.mEdgeAnimations[index] = AnimationsManager.loadShared(animation, profile);
               if (this.mEdgeAnimations[index] != null) {
                  this.mEdgeCollisions[index] = AnimationsManager.loadShared(definition.getCollision(), profile);
               }
            }
         }
      }

      for (int i = 0; i < this.mOldEdgeAnimations.length; i++) {
         SpriteObject old = this.mOldEdgeAnimations[i];
         if (old != null) {
            AnimationsManager.unloadShared(old.getCurrentAnimationResourceID(), old);
         }
      }
   }

   public Vector getVisibleAreas() {
      return this.mVisibleAreas;
   }

   public int getVisibleAreaCount() {
      return 100;
   }

   public int getVisibleAreaSize() {
      return 5;
   }

   public int logicUpdate(int delta) {
      if (this.mFloor != null) {
         this.mFloor.logicUpdate(delta);
      }

      for (int i = 0; i < this.mEdgeAnimations.length; i++) {
         if (this.mEdgeAnimations[i] != null) {
            this.mEdgeAnimations[i].logicUpdate(delta);
         }
      }

      return 0;
   }

   public void doDrawSky() {
      if (this.mSky != null) {
         int width = Toolkit.getScreenWidth();
         int height = Toolkit.getScreenHeight();
         int skyWidth = this.mSky.getWidth();
         int scaleWidth = this.mSky.getWidth();
         width += skyWidth * 4;
         int skyHeight = this.mSky.getHeight();
         OGL.pushParameters();
         OGL.setScale((float)width / scaleWidth, (float)height / skyHeight);
         this.mSky.draw(skyWidth * -2, 0);
         OGL.popParameters();
      }
   }

   public void doDraw(Camera camera) {
      float cameraX = camera.getX();
      float cameraY = camera.getY();
      if (this.mFloor != null) {
         this.mFloor.draw(-cameraX, -cameraY);
      }

      if (this.mShadow != null) {
         OGL.setPolygonGradientHack(POLYGON_GRADIENT, -65536);
         this.mShadow.draw(-cameraX, -cameraY);
         OGL.resetPolygonGradientHack();
      }

      if (this.mEdgePositions != null) {
         for (int i = 0; i < this.mEdgeAnimations.length; i++) {
            if (this.mEdgeAnimations[i] != null && this.mEdgeAreas[i] != null && this.mEdgeAreas[i].isVisible()) {
               CollisionBox box = this.mEdgePositions.getCollisionBox(i);
               SpriteObject animation = this.mEdgeAnimations[i];
               animation.draw(box.getX() - cameraX, box.getY() - cameraY);
            }
         }
      }
   }

   public void drawDebug(Camera camera) {
      float cameraX = camera.getX();
      float cameraY = camera.getY();
      if (this.mShadow != null) {
         CollisionBox[] boxes = this.mShadow.getCollisionBoxes();
         if (boxes != null) {
            int offsetX = (int)(-cameraX);
            int offsetY = (int)(-cameraY);

            for (int i = 0; i < boxes.length; i++) {
               if (boxes[i] != null) {
                  OGL.setColorRGB(boxes[i].getId() == 0 ? 16776960 : 16711935);
                  int x = boxes[i].getX();
                  int y = boxes[i].getY();
                  OGL.drawRect(offsetX + x, offsetY + y, boxes[i].getWidth(), boxes[i].getHeight());
               }
            }

            if (!this.mVisibleAreas.isEmpty()) {
               int size = this.getVisibleAreaSize();

               for (int i = 0; i < this.mVisibleAreas.size(); i++) {
                  Integer value = (Integer)this.mVisibleAreas.elementAt(i);
                  Area area = GameEngineUtils.getArea();
                  int column = value % (area.getWidth() / size);
                  int row = value / (area.getWidth() / size);

                  for (int j = 0; j < size; j++) {
                     for (int k = 0; k < size; k++) {
                        IsometricTile tile = this.mScene.getTile(column * size + j, size * row + k);
                        if (tile != null) {
                           Vec2 point = Transform.toScreen(tile.getX(), tile.getY());
                           float x = point.getX() - camera.getX();
                           float y = point.getY() - camera.getY();
                           OGL.setColorRGB(16777215);
                           OGL.drawRect((int)x - 7, (int)y - 7, 14, 14);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public boolean isObjectVisible(IsometricObject object) {
      if (this.mShadow != null && object != null) {
         int itemID = object.getItemID();
         if (itemID != 5115 && itemID != 5119 && itemID != 5123) {
            CollisionBox[] boxes = this.mShadow.getCollisionBoxes();
            if (boxes != null && boxes.length != 0) {
               float x = object.getBoundsX();
               float y = object.getBoundsY();
               float width = object.getBoundsWidth();
               float height = object.getBoundsHeight();
               float right = x + width;
               float bottom = y + height;

               for (int i = 0; i < boxes.length; i++) {
                  if (boxes[i] != null) {
                     int boxX = boxes[i].getX();
                     int boxY = boxes[i].getY();
                     int boxWidth = boxes[i].getWidth();
                     int boxHeight = boxes[i].getHeight();
                     if (boxes[i].getId() == 0 && Rect2.isRectangleInside(boxX, boxY, boxWidth, boxHeight, x, y, right, bottom)) {
                        return false;
                     }

                     if (boxes[i].getId() == 1 && Rect2.isRectangleIntersecting(boxX, boxY, boxWidth, boxHeight, x, y, width, height)) {
                        return false;
                     }
                  }
               }

               return true;
            } else {
               return true;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public boolean isTileVisible(IsometricTile tile) {
      if (tile == null) {
         return true;
      }

      float x = Transform.toScreenX(tile.getX(), tile.getY());
      float y = Transform.toScreenY(tile.getX(), tile.getY());
      return this.isVisibleAt(x, y);
   }

   public boolean isVisibleAt(float x, float y) {
      if (this.mShadow == null) {
         return true;
      }

      int[][] renderables = this.mShadow.getRenderables();
      float[][] polygons = this.mShadow.getPolygons();
      int[] colors = this.mShadow.getColors();

      for (int i = 0; renderables != null && i < renderables.length; i++) {
         if (colors[i] != -65536) {
            int[] renderable = renderables[i];
            int type = renderable[0];
            boolean hit;
            if (type != 8 && type != 6) {
               if (type != 2) {
                  continue;
               }

               hit = Rect2.isPointInside(x, y, renderable[1], renderable[2], renderable[3], renderable[4]);
            } else {
               hit = IsometricUtils.isInsidePolygon(x, y, polygons[i]);
            }

            if (hit) {
               return false;
            }
         }
      }

      return true;
   }

   public void touchEventOccured(TouchEvent event) {
      if (event.getType() == 3) {
         int index = this.getSideCollisionIndex(event);
         if (index >= 0) {
            if (this.doAction(index)) {
               event.setConsumed(true);
            }
         }
      }
   }

   public int getSideCollisionIndex(TouchEvent event) {
      for (int i = 0; this.mEdgeCollisions != null && i < this.mEdgeCollisions.length; i++) {
         if (this.mEdgeCollisions[i] != null
            && this.mEdgeCollisions[i].getCollisionPolygon(0) != null
            && this.mEdgeAreas[i] != null
            && this.mEdgeAreas[i].isVisible()) {
            float[] polygon = this.mEdgeCollisions[i].getCollisionPolygon(0);
            Camera camera = this.mScene.getCamera();
            float cameraX = camera.getX();
            int touchX = event.getX();
            float scaleX = this.mScene.getScale();
            float cameraY = camera.getY();
            int touchY = event.getY();
            float scaleY = this.mScene.getScale();
            float x = cameraX + touchX * (1.0F / scaleX);
            float y = cameraY + touchY * (1.0F / scaleY);
            if (IsometricUtils.isInsidePolygon(x, y, polygon)) {
               return i;
            }
         }
      }

      return -1;
   }

   public static boolean showUnlockMission(int mission) {
      Vector icons = HUD.getInGame().getMissions().getMissions();

      for (int i = 0; i < icons.size(); i++) {
         HUDMissionIcon icon = (HUDMissionIcon)icons.elementAt(i);
         Mission current = icon.getMission();
         if (current != null && current.getID() == mission) {
            icon.doAction();
            return true;
         }
      }

      return false;
   }

   public void updateBackground(int animation) {
      int profile = DeviceWrapper.useHDIngameGraphics() ? 2 : 1;
      SpriteObject floor = AnimationsManager.loadShared(animation, profile);
      if (floor == null) {
         this.mFloor = AnimationsManager.loadShared(65647, profile);
      } else {
         if (this.mFloor != null) {
            AnimationsManager.unloadShared(this.mFloor.getCurrentAnimationResourceID(), this.mFloor);
         }

         this.mFloor = floor;
      }
   }

   public void updateSky(int animation) {
      SpriteObject sky = AnimationsManager.loadShared(animation);
      if (sky == null) {
         sky = AnimationsManager.loadShared(65701);
      } else if (this.mSky != null) {
         AnimationsManager.unloadShared(this.mSky.getCurrentAnimationResourceID(), this.mSky);
      }

      this.mSky = sky;
      if (sky != null) {
         sky.setAnimationCustomScale(1.0F);
      }
   }

   public void updateShadow(int[][] shadows) {
      int profile = DeviceWrapper.useHDIngameGraphics() ? 2 : 1;
      int animation = -1;
      if (shadows != null) {
         for (int i = 0; i < shadows.length; i++) {
            Mission mission = MissionManager.getMission(shadows[i][0]);
            if (mission != null && !mission.isCompleted()) {
               break;
            }

            animation = shadows[i][1];
         }
      }

      SpriteObject shadow = AnimationsManager.loadShared(animation, profile);
      if (this.mShadow != null) {
         AnimationsManager.unloadShared(this.mShadow.getCurrentAnimationResourceID(), this.mShadow);
      }

      this.mShadow = shadow;
   }

   public void updateVisibleAreas() {
      this.mVisibleAreas.removeAllElements();
      if (this.mShadow != null) {
         Area area = GameEngineUtils.getArea();
         int size = this.getVisibleAreaSize();
         int count = this.getVisibleAreaCount();

         for (int i = 0; i < count; i++) {
            int width = area.getWidth();
            int width2 = area.getWidth();
            int row = i / (width2 / size);
            int column = i % (width / size);
            IsometricTile first = this.mScene.getTile(column * size, row * size);
            IsometricTile last = this.mScene.getTile(column * size + size, row * size + size);
            if (this.isTileVisible(first) && this.isTileVisible(last)) {
               this.mVisibleAreas.addElement(i);
            }
         }
      }
   }

   public boolean doAction(int index) {
      EdgeAreaDefinition definition = this.mEdgeAreas[index];
      if (definition == null) {
         return false;
      }

      int event;
      int[] arguments;
      if (definition.isLockedByMission() && !definition.isLockedByLevel()) {
         event = definition.getLockedEvent();
         if (event == -1) {
            return false;
         }

         Mission mission = definition.getUnlockMission();
         arguments = new int[1];
         arguments[0] = mission.getID();
      } else {
         event = definition.getUnlockedEvent();
         if (definition.isLockedByLevel()) {
            event = definition.getLockedEvent();
         }

         if (event == -1) {
            return false;
         }

         arguments = new int[]{-1};
      }

      EventManager.addEvent(new TimedEvent(0, event, arguments));
      return true;
   }
}
