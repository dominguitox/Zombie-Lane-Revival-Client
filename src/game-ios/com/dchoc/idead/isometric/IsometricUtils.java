package com.dchoc.idead.isometric;

import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.areas.Area;
import com.dchoc.idead.characters.ActionCharacter;
import com.dchoc.idead.characters.CharacterObject;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.characters.ZombieCharacter;
import com.dchoc.idead.items.DestructibleItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.objects.DecorationObject;
import com.dchoc.idead.objects.InteractiveObject;
import com.dchoc.idead.objects.ObjectWithHealth;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import java.util.Vector;

public class IsometricUtils {
   public static final int INITIAL_VECTOR_CAPACITY = 128;
   public static final int POSITION_BOTTOM = 1;
   public static final int POSITION_LEFT = 2;
   public static final int POSITION_NONE = -1;
   public static final int POSITION_RIGHT = 3;
   public static final int POSITION_TOP = 0;
   private static int[] smBarLayoutValues = new int[6];
   private static int smBottomBorder;
   private static boolean smIgnoreAccessible;
   private static boolean smIgnoreCrops;
   private static boolean smIgnoreFences;
   private static boolean smIgnoreFloor;
   private static boolean smIgnoreLoot;
   private static boolean smIgnorePlayer;
   private static boolean smIgnoreZombies;
   private static int[] smPositionTypeXY;
   private static IsometricScene smScene;
   private static int smTopBorder;
   private static Vector smVector;

   public static void init(IsometricScene scene) {
      smScene = scene;
      smVector = new Vector(128);
      smPositionTypeXY = new int[3];
      CollisionBox box = AnimationsManager.loadShared(65551).getCollisionBox(1);
      int top = box.getY();
      smTopBorder = box.getHeight() + top;
      box = AnimationsManager.loadShared(65597).getCollisionBox(99);
      int screenHeight = Toolkit.getScreenHeight();
      smBottomBorder = box.getY() + screenHeight;
   }

   public static void fastRemove(int index, Vector vector) {
      int last = vector.size() - 1;
      if (last != index) {
         vector.setElementAt(vector.elementAt(last), index);
      }

      vector.removeElementAt(last);
   }

   public static int calculateDistance(IsometricObject from, IsometricObject to) {
      if (from != null && from.getTile() != null) {
         IsometricTile tile = from.getTile();
         return calculateDistance(tile.getColumn(), tile.getRow(), to);
      } else {
         return Integer.MAX_VALUE;
      }
   }

   public static int calculateDistance(int column, int row, IsometricObject object) {
      if (object != null && object.getTile() != null) {
         IsometricTile from = smScene.getTile(column, row);
         IsometricTile tile = object.getTile();
         if (from == null) {
            return Integer.MAX_VALUE;
         }

         int distance = Integer.MAX_VALUE;

         for (int i = 0; i < object.getTileSizeX(); i++) {
            for (int j = 0; j < object.getTileSizeY(); j++) {
               int dx = Math.abs(tile.getColumn() + i - from.getColumn());
               int dy = Math.abs(tile.getRow() - from.getRow() - j);
               distance = Math.min(distance, Math.max(dx, dy));
            }
         }

         return distance;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   public static IsometricTile findClosestTile(IsometricObject from, IsometricObject target, int steps, boolean flagA, boolean flagB) {
      if (from != null && target != null) {
         IsometricTile fromTile = from.getTile();
         IsometricTile targetTile = target.getTile();
         if (fromTile != null && targetTile != null) {
            float x = from.getX();
            float y = from.getY();
            int column = targetTile.getColumn();
            int row = targetTile.getRow();
            int best = Integer.MAX_VALUE;
            boolean found = false;
            IsometricTile closest = null;

            for (int side = 0; side < 4; side++) {
               int lastRow = row + 1;
               int firstColumn;
               int lastColumn;
               int firstRow;
               if (side == 0) {
                  firstRow = row - target.getTileSizeY();
                  firstColumn = column - 1;
                  lastColumn = column - 1;
               } else if (side == 1) {
                  firstColumn = target.getTileSizeX() + column;
                  lastColumn = target.getTileSizeX() + column;
                  firstRow = row - target.getTileSizeY();
               } else {
                  lastColumn = target.getTileSizeX() + column - 1;
                  firstColumn = column;
                  firstRow = row + 1;
                  if (side == 2) {
                     firstRow = row - target.getTileSizeY();
                     lastRow = row - target.getTileSizeY();
                  }
               }

               for (int c = firstColumn; c <= lastColumn; c++) {
                  for (int r = firstRow; r <= lastRow; r++) {
                     IsometricTile tile = smScene.getTile(c, r);
                     if (tile != null) {
                        Vector path = smScene.getPathfinder().findPath(x, y, tile.getX(), tile.getY(), flagA, flagB);
                        if (path != null && path.size() < best) {
                           smVector.removeAllElements();

                           for (int i = 0; i < path.size(); i++) {
                              smVector.addElement(path.elementAt(i));
                           }

                           best = path.size();
                           found = true;
                           closest = tile;
                        }
                     }
                  }
               }
            }

            if (closest == null || !found) {
               return null;
            } else if (steps == 1 || smVector.isEmpty()) {
               return closest;
            } else {
               return steps - 2 < smVector.size() ? (IsometricTile)smVector.elementAt(steps - 2) : (IsometricTile)smVector.lastElement();
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static IsometricTile calculateNextTileForZombie(ZombieCharacter zombie, int column, int row) {
      smIgnoreAccessible = true;
      smIgnoreFences = false;
      smIgnoreFloor = true;
      smIgnoreLoot = true;
      smIgnoreZombies = false;
      smIgnorePlayer = false;
      smIgnoreCrops = true;
      int best = 0;
      Vector turrets = smScene.getTurrets();
      smVector.removeAllElements();
      IsometricTile bestTile = null;

      for (int c = column - 1; c <= column + 1; c++) {
         for (int r = row - 1; r <= row + 1; r++) {
            if (c != column || r != row) {
               IsometricTile tile = smScene.getTile(c, r);
               if (isValidTile(null, smScene.getTile(c, r))) {
                  int nearest = Integer.MAX_VALUE;
                  if (turrets.size() < 1) {
                     smVector.addElement(tile);
                  } else {
                     for (int i = 0; i < turrets.size(); i++) {
                        IsometricObject turret = (IsometricObject)turrets.elementAt(i);
                        nearest = Math.min(nearest, calculateDistance(c, r, turret));
                     }

                     if (nearest > 3) {
                        smVector.addElement(tile);
                     }
                  }

                  if (best < nearest) {
                     best = nearest;
                     bestTile = tile;
                  }
               }
            }
         }
      }

      return !smVector.isEmpty() ? (IsometricTile)smVector.elementAt(ToolkitHelpers.getRandomInt(0, smVector.size() - 1)) : bestTile;
   }

   public static boolean isValidTile(IsometricObject object, IsometricTile tile) {
      if (tile != null && !tile.isValid(object)) {
         boolean blocked = !smIgnoreAccessible && !tile.isAccessible();
         boolean zombie = !smIgnoreZombies && tile.hasZombie();
         boolean player = !smIgnorePlayer && tile.hasPlayer();
         boolean fence = !smIgnoreFences && tile.hasFence() && tile.getFence() != object;
         boolean crop = !smIgnoreCrops && tile.hasCrop() && tile.getCrop() != object;
         if (!blocked && !fence && !crop && !zombie && !player) {
            Vector objects = tile.getObjects();

            for (int i = 0; i < objects.size(); i++) {
               IsometricObject other = (IsometricObject)objects.elementAt(i);
               if (other != null && other != object) {
                  boolean floor = other.isFloor();
                  if (!smIgnoreFloor || !floor) {
                     if (other.getType() == 4) {
                        return false;
                     }

                     if (other.getType() == 3 && !smIgnoreLoot) {
                        return false;
                     }
                  }
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean isInsideBox(float x, float y, CollisionBox box) {
      if (box == null) {
         return false;
      } else if (!(box.getX() <= x)) {
         return false;
      } else if (!(x <= box.getX() + box.getWidth())) {
         return false;
      } else {
         return !(box.getY() <= y) ? false : y <= box.getY() + box.getHeight();
      }
   }

   public static boolean isInsidePolygon(float x, float y, float[] triangles) {
      if (triangles == null) {
         return false;
      }

      for (int i = 0; i < triangles.length; i += 6) {
         float x1 = triangles[i];
         float y1 = triangles[i + 1];
         float x2 = triangles[i + 2];
         float y2 = triangles[i + 3];
         float x3 = triangles[i + 4];
         float y3 = triangles[i + 5];
         float a = (y - y1) * (x2 - x1) - (x - x1) * (y2 - y1);
         float c = (y - y2) * (x3 - x2) - (x - x2) * (y3 - y2);
         if (a * c > 0.0F) {
            float b = (x1 - x3) * (y - y3) - (x - x3) * (y1 - y3);
            if (c * b > 0.0F) {
               return true;
            }
         }
      }

      return false;
   }

   public static IsometricTile getTileAsRangedAttack(ActionCharacter actor, IsometricObject target) {
      WeaponItem weapon = actor.getWeapon();
      if (weapon != null && weapon.getRange() > 1) {
         IsometricTile own = actor.getTile();
         IsometricTile other = target.getTile();
         if (own.getColumn() >= other.getColumn() - 1
            && own.getColumn() <= other.getColumn() + 1
            && own.getRow() >= other.getRow() - 1
            && own.getRow() <= other.getRow() + 1) {
            smIgnoreFloor = true;
            smIgnoreAccessible = true;
            smIgnoreFences = false;
            smIgnoreCrops = false;
            smIgnoreLoot = false;
            smIgnoreZombies = false;
            smIgnorePlayer = true;
            boolean columnAtLeast = own.getColumn() >= other.getColumn();
            boolean columnAbove = columnAtLeast && own.getColumn() > other.getColumn();
            boolean rowAtLeast = own.getRow() >= other.getRow();
            boolean rowAbove = rowAtLeast && own.getRow() > other.getRow();
            int type;
            if (columnAbove) {
               if (!rowAtLeast) {
                  type = 4;
               } else if (rowAbove) {
                  type = 5;
               } else {
                  type = 3;
               }
            } else if (columnAtLeast) {
               if (!rowAtLeast) {
                  type = 6;
               } else {
                  if (!rowAbove) {
                     return null;
                  }

                  type = 7;
               }
            } else if (!rowAtLeast) {
               type = 1;
            } else if (rowAbove) {
               type = 2;
            } else {
               type = 0;
            }

            return getTileFromRangedAttackSearchType(type, own);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static IsometricTile getTileFromRangedAttackSearchType(int type, IsometricTile tile) {
      IsometricScene scene = smScene;
      IsometricTile candidate = null;

      for (int step = 1; step <= 3; step++) {
         int dx = 0;
         int dy = 0;
         boolean search = true;
         switch (type) {
            case 0:
               dx = -1;
               dy = step == 1 ? 0 : (step == 2 ? -1 : 1);
               break;
            case 1:
               dx = step == 3 ? 0 : -1;
               dy = step == 2 ? 0 : -1;
               break;
            case 2:
               dx = step == 3 ? 0 : -1;
               dy = step == 2 ? 0 : 1;
               break;
            case 3:
               dx = 1;
               dy = step == 1 ? 0 : (step == 2 ? -1 : 1);
               break;
            case 4:
               dx = step == 3 ? 0 : 1;
               dy = step == 2 ? 0 : -1;
               break;
            case 5:
               dx = step == 3 ? 0 : 1;
               dy = step == 2 ? 0 : 1;
               break;
            case 6:
               dx = step == 1 ? 0 : (step == 2 ? -1 : 1);
               dy = -1;
               break;
            case 7:
               dx = step == 1 ? 0 : (step == 2 ? -1 : 1);
               dy = 1;
               break;
            default:
               search = false;
         }

         if (search) {
            candidate = scene.getTile(tile.getColumn() + dx, tile.getRow() + dy);
         }

         if (candidate != null && isValidTile(null, candidate)) {
            return candidate;
         }
      }

      return null;
   }

   public static IsometricTile getTileAsSpiralSearch(IsometricTile tile, int start, int max, int sizeX, int sizeY, boolean centered, boolean ignoreFloor) {
      smIgnoreFloor = ignoreFloor;
      smIgnoreAccessible = true;
      smIgnoreFences = false;
      smIgnoreCrops = false;
      smIgnoreLoot = false;
      smIgnoreZombies = false;
      smIgnorePlayer = false;
      int column = tile.getColumn();
      int row = tile.getRow();
      int baseRow = centered ? row + sizeY - 1 : row;
      int innerColumn = centered ? column + 2 - start - sizeX : column;
      int outerColumn = centered ? column + start + sizeX - 2 : column;
      int stepX = centered ? 1 : sizeX;
      int stepY = centered ? 1 : sizeY;
      int ring = start;

      do {
         for (int i = 0; i <= ring; i++) {
            IsometricTile candidate = smScene.getTile(column + ring * stepX, baseRow + i * sizeY);
            if (isValidTile(null, candidate, sizeX, sizeY)) {
               return candidate;
            }
         }

         for (int j = ring; j >= -ring; j--) {
            IsometricTile candidate = smScene.getTile(j * sizeX + innerColumn, baseRow + ring * sizeY);
            if (isValidTile(null, candidate, sizeX, sizeY)) {
               return candidate;
            }
         }

         int leftColumn = centered ? 1 - sizeX + column - ring : column - ring * sizeX;

         for (int j = ring; j >= -ring; j--) {
            IsometricTile candidate = smScene.getTile(leftColumn, baseRow + j * sizeY);
            if (isValidTile(null, candidate, sizeX, sizeY)) {
               return candidate;
            }
         }

         for (int j = -ring; j <= ring; j++) {
            IsometricTile candidate = smScene.getTile(j * sizeX + outerColumn, row - ring * stepY);
            if (isValidTile(null, candidate, sizeX, sizeY)) {
               return candidate;
            }
         }

         for (int j = -ring + 1; j <= 0; j++) {
            IsometricTile candidate = smScene.getTile(ring * sizeX + innerColumn, baseRow + j * sizeY);
            if (isValidTile(null, candidate, sizeX, sizeY)) {
               return candidate;
            }
         }
      } while (++ring <= max);

      return null;
   }

   public static IsometricTile getTile(int column, int row) {
      return smScene.getTile(column, row);
   }

   public static IsometricTile getTile(Item item, int column, int row) {
      IsometricTile tile = smScene.getTile(column, row);
      smIgnoreFences = false;
      smIgnoreAccessible = true;
      smIgnoreLoot = false;
      smIgnoreFloor = true;
      smIgnoreZombies = false;
      smIgnoreCrops = false;
      smIgnorePlayer = false;
      if (item.getType() == 50) {
         DestructibleItem destructible = (DestructibleItem)item;
         if (destructible.isZombie()) {
            smIgnoreAccessible = false;
         } else if (destructible.isGarbage()) {
            smIgnoreFloor = false;
         }
      }

      if (isValidTile(null, tile, item.getTileSizeX(), item.getTileSizeY())) {
         return tile;
      }

      for (int ring = 1; Math.max(smScene.getColumns() - column, smScene.getRows() - row) > ring; ring++) {
         int side = ring * 2;

         for (int i = 0; i < side; i++) {
            IsometricTile candidate = smScene.getTile(column - ring, row - ring + i);
            if (isValidTile(null, candidate, item.getTileSizeX(), item.getTileSizeY())) {
               return candidate;
            }
         }

         for (int i = 0; i < side; i++) {
            IsometricTile candidate = smScene.getTile(column - ring + i, row - ring + side);
            if (isValidTile(null, candidate, item.getTileSizeX(), item.getTileSizeY())) {
               return candidate;
            }
         }

         for (int i = 0; i < side; i++) {
            IsometricTile candidate = smScene.getTile(side + column - ring, row + ring - i);
            if (isValidTile(null, candidate, item.getTileSizeX(), item.getTileSizeY())) {
               return candidate;
            }
         }

         for (int i = 0; i < side; i++) {
            IsometricTile candidate = smScene.getTile(column + ring - i, row - ring);
            if (isValidTile(null, candidate, item.getTileSizeX(), item.getTileSizeY())) {
               return candidate;
            }
         }
      }

      return null;
   }

   public static boolean isValidTile(IsometricObject object, IsometricTile tile, int sizeX, int sizeY) {
      if (tile == null) {
         return false;
      }

      if (sizeX == 1 && sizeY == 1) {
         return isValidTile(object, tile);
      }

      int column = tile.getColumn();
      int row = tile.getRow();

      for (int i = 0; i < sizeX; i++) {
         for (int j = 0; j < sizeY; j++) {
            if (!isValidTile(object, smScene.getTile(column + i, row - j))) {
               return false;
            }
         }
      }

      return true;
   }

   public static IsometricTile getRandomTile(Item item) {
      if (item == null) {
         return null;
      }

      smIgnoreAccessible = true;
      smIgnoreFences = false;
      smIgnoreCrops = false;
      smIgnoreLoot = false;
      smIgnoreFloor = true;
      smIgnoreZombies = false;
      smIgnorePlayer = false;
      if (item.getType() == 50) {
         DestructibleItem destructible = (DestructibleItem)item;
         if (destructible.isZombie()) {
            smIgnoreAccessible = false;
         } else if (destructible.isGarbage()) {
            smIgnoreFloor = false;
         }
      }

      int sizeX = item.getTileSizeX();
      int sizeY = item.getTileSizeY();
      int columns = smScene.getColumns();
      int lastColumn = columns - item.getTileSizeX();
      int rows = smScene.getRows();
      int lastRow = rows - item.getTileSizeY();
      IsometricFloor floor = smScene.getFloor();
      Vector areas = floor.getVisibleAreas();
      int areaSize = floor.getVisibleAreaSize();
      int attempts;
      if (areas.isEmpty()) {
         attempts = smScene.getColumns() * smScene.getRows();
      } else {
         attempts = areaSize * areaSize * areas.size();
      }

      for (int i = 0; i < attempts; i++) {
         IsometricTile tile;
         if (!smIgnoreAccessible && !areas.isEmpty()) {
            Integer value = (Integer)areas.elementAt(ToolkitHelpers.getRandomInt(0, areas.size() - 1));
            Area area = GameEngineUtils.getArea();
            int first = value;
            int firstWidth = area.getWidth();
            int areaRow = value / (area.getWidth() / areaSize);
            int areaColumn = first % (firstWidth / areaSize);
            tile = smScene.getTile(
               ToolkitHelpers.getRandomInt(areaColumn * areaSize, Math.min(areaColumn * areaSize + areaSize, lastColumn)),
               ToolkitHelpers.getRandomInt(areaRow * areaSize, Math.min(areaRow * areaSize + areaSize, lastRow))
            );
         } else {
            tile = smScene.getTile(ToolkitHelpers.getRandomInt(0, lastColumn), ToolkitHelpers.getRandomInt(0, lastRow));
         }

         if (isValidTile(null, tile, sizeX, sizeY)) {
            return tile;
         }
      }

      return null;
   }

   public static boolean isPositionValidAt(float x, float y, Item item, IsometricObject object) {
      if (item == null) {
         return false;
      }

      smIgnoreAccessible = true;
      smIgnoreFences = false;
      smIgnoreCrops = false;
      smIgnoreLoot = false;
      smIgnoreFloor = true;
      smIgnoreZombies = false;
      smIgnorePlayer = false;
      if (item.getType() == 1104) {
         smIgnoreFloor = false;
      } else if (item.getType() == 405 && item.getLayer() == 1) {
         smIgnoreFloor = false;
         smIgnorePlayer = true;
      }

      IsometricTile tile = smScene.getTileAt(x, y);
      if (tile == null) {
         return false;
      }

      int sizeX;
      int sizeY;
      if (object == null) {
         sizeX = item.getTileSizeX();
         sizeY = item.getTileSizeY();
      } else {
         sizeX = object.getTileSizeX();
         sizeY = object.getTileSizeY();
      }

      return isValidTile(object, tile, sizeX, sizeY);
   }

   public static IsometricObject getInteractableObjectByItem(ActionCharacter actor, Item item) {
      if (actor != null && item != null) {
         int group;
         switch (item.getType()) {
            case 50:
               group = ((DestructibleItem)item).isZombie() ? 2 : 3;
               break;
            case 147:
               group = 0;
               break;
            case 405:
               group = 4;
               break;
            case 837:
               group = 1;
               break;
            case 1104:
               group = 5;
               break;
            case 1583:
               group = 6;
               break;
            default:
               return null;
         }

         Vector objects = smScene.getObjects(group);
         smVector.removeAllElements();

         for (int i = 0; i < objects.size(); i++) {
            IsometricObject object = (IsometricObject)objects.elementAt(i);
            if (object.getItem() == item && object != null && ((InteractiveObject)object).canInteractWith(actor)) {
               smVector.addElement(object);
            }
         }

         return smVector.isEmpty() ? null : (IsometricObject)smVector.elementAt(ToolkitHelpers.getRandomInt(0, smVector.size() - 1));
      } else {
         return null;
      }
   }

   public static boolean canInteractWith(ActionCharacter actor, IsometricObject object) {
      return actor != null && object != null && ((InteractiveObject)object).canInteractWith(actor);
   }

   public static int[] getPositionTypeXY(
      IsometricObject object, int position, int width, int height, int marginX, int marginY, boolean horizontal, boolean vertical, int forced
   ) {
      return getPositionTypeXY(object, position, -1, width, height, marginX, marginY, 0, 0, horizontal, vertical, forced);
   }

   public static int[] getPositionTypeXY(
      IsometricObject object,
      int position,
      int attached,
      int width,
      int height,
      int marginX,
      int marginY,
      int attachedWidth,
      int attachedHeight,
      boolean horizontal,
      boolean vertical,
      int forced
   ) {
      if (object != null && smScene != null) {
         Camera camera = smScene.getCamera();
         if (camera == null) {
            return null;
         }

         float scale = smScene.getScale();
         float screenX = scale * (object.getScreenX() - camera.getX());
         float screenY = scale * (object.getScreenY() - camera.getY());
         int spriteWidth = 0;
         int spriteHeight = 0;
         int pivotX = 0;
         int pivotY = 0;
         SpriteObject sprite = object.getCollision();
         if (sprite == null || object.getType() == 6) {
            SpriteObject[] animations = object.getItem().getAnimations();
            sprite = animations == null ? null : animations[0];
         }

         if (sprite != null) {
            spriteWidth = (int)(scale * sprite.getWidth());
            spriteHeight = (int)(scale * sprite.getHeight());
            pivotX = (int)(scale * sprite.getPivotX());
            pivotY = (int)(scale * sprite.getPivotY());
         }

         int left = (int)(screenX - pivotX);
         int top = (int)(screenY - pivotY);
         int halfWidth = spriteWidth >> 1;
         int halfHeight = spriteHeight >> 1;
         int centerX = left + halfWidth;
         int centerY = top + halfHeight;
         if (forced == -1 && position == -1) {
            for (int type = 0; type < 4; type++) {
               if ((type < 2 || !vertical)
                  && (!horizontal || type >= 2)
                  && checkPositionTypeXY(
                     type,
                     attached,
                     spriteWidth,
                     spriteHeight,
                     width,
                     height,
                     marginX,
                     marginY,
                     attachedWidth,
                     attachedHeight,
                     centerX,
                     centerY,
                     halfWidth,
                     halfHeight,
                     10,
                     10,
                     false
                  )) {
                  return smPositionTypeXY;
               }
            }

            int[] result = smPositionTypeXY;
            if (!vertical && horizontal) {
               int screenWidth = Toolkit.getScreenWidth();
               result[1] = centerY;
               if (centerX > screenWidth / 2) {
                  int extra = attached == 2 ? -5 - marginX - attachedWidth : -marginX;
                  result[0] = left - 10 + extra - width / 2;
                  result[2] = 2;
               } else {
                  int x = halfWidth + centerX + 10;
                  if (attached == 3) {
                     x += marginX + attachedWidth + width / 2 + 5;
                  } else {
                     x += marginX + width / 2;
                  }

                  result[0] = x;
                  result[2] = 3;
               }
            } else {
               int screenHeight = Toolkit.getScreenHeight();
               result[0] = centerX;
               if (centerY > screenHeight / 2) {
                  int extra = attached == 0 ? -5 - marginY - attachedHeight : -marginY;
                  result[1] = top - 10 + extra - height / 2;
                  result[2] = 0;
               } else {
                  int y = halfHeight + centerY + 10;
                  if (attached == 1) {
                     y += marginY + attachedHeight + height / 2 + 5;
                  } else {
                     y += marginY + height / 2;
                  }

                  result[1] = y;
                  result[2] = 1;
               }
            }

            return result;
         } else {
            checkPositionTypeXY(
               forced != -1 ? forced : position,
               attached,
               spriteWidth,
               spriteHeight,
               width,
               height,
               marginX,
               marginY,
               attachedWidth,
               attachedHeight,
               centerX,
               centerY,
               halfWidth,
               halfHeight,
               10,
               10,
               true
            );
            return smPositionTypeXY;
         }
      } else {
         return null;
      }
   }

   private static boolean checkPositionTypeXY(
      int position,
      int attached,
      int spriteWidth,
      int spriteHeight,
      int width,
      int height,
      int marginX,
      int marginY,
      int attachedWidth,
      int attachedHeight,
      int centerX,
      int centerY,
      int halfWidth,
      int halfHeight,
      int gapX,
      int gapY,
      boolean force
   ) {
      if (position == 0 || position == 1) {
         smPositionTypeXY[0] = centerX;
      } else if (position == 2 || position == 3) {
         smPositionTypeXY[1] = centerY;
      }

      switch (position) {
         case 0:
            int extra = attached == 0 ? -5 - marginY - attachedHeight : -marginY;
            int y = centerY - halfHeight - gapY + extra - height / 2;
            smPositionTypeXY[1] = y;
            if (!force && y - height / 2 < smTopBorder) {
               return false;
            }

            smPositionTypeXY[2] = 0;
            return true;
         case 1:
            int y = halfHeight + centerY + gapY;
            if (attached == 1) {
               y += attachedHeight + marginY + height / 2 + 5;
            } else {
               y += marginY + height / 2;
            }

            smPositionTypeXY[1] = y;
            if (!force && smBottomBorder < y + height / 2) {
               return false;
            }

            smPositionTypeXY[2] = 1;
            return true;
         case 2:
            int extra = attached == 2 ? -5 - marginX - attachedWidth : -marginX;
            int x = centerX - halfWidth - gapX + extra - width / 2;
            smPositionTypeXY[0] = x;
            if (!force && x - width / 2 < 0) {
               return false;
            }

            smPositionTypeXY[2] = 2;
            return true;
         case 3:
            int x = halfWidth + centerX + gapX;
            if (attached == 3) {
               x += attachedWidth + marginX + width / 2 + 5;
            } else {
               x += marginX + width / 2;
            }

            smPositionTypeXY[0] = x;
            if (!force && Toolkit.getScreenWidth() < x + width / 2) {
               return false;
            }

            smPositionTypeXY[2] = 3;
            return true;
         default:
            return false;
      }
   }

   public static IsometricScene getScene() {
      return smScene;
   }

   public static IsometricFloor getFloor() {
      return smScene.getFloor();
   }

   public static PlayerCharacter getPlayer() {
      return smScene == null ? null : smScene.getPlayer();
   }

   public static void drawBar(
      SpriteObject bar,
      int x,
      int y,
      int leftWidth,
      int rightWidth,
      int centerWidth,
      int centerX,
      int areaWidth,
      int maxWidth,
      int width,
      float scale,
      boolean scaled,
      boolean background
   ) {
      int height = bar.getHeight();
      if (scaled) {
         int scaledHeight = (int)(height * scale);
         int scaledWidth = (int)(width * scale);
         areaWidth = (int)(areaWidth * scale);
         centerX = (int)(centerX * scale);
         centerWidth = (int)(centerWidth * scale);
         leftWidth = (int)(leftWidth * scale);
         rightWidth = (int)(rightWidth * scale);
         y += (height - scaledHeight) / 2;
         x += (width - scaledWidth) / 2;
         height = scaledHeight;
         width = scaledWidth;
      }

      if (leftWidth != 0 && rightWidth != 0 && centerWidth != 0) {
         int middle;
         if (background) {
            middle = width - rightWidth;
         } else {
            middle = maxWidth - rightWidth;
            if (width <= maxWidth - rightWidth) {
               middle = width;
            }
         }

         middle -= leftWidth;
         if (middle < 0) {
            width -= middle;
            middle = 0;
         }

         if (middle != 0) {
            int count = 1;
            int step = middle;
            if (centerWidth < middle) {
               count = middle / centerWidth;
               if (middle % centerWidth > 0) {
                  count++;
               }

               step = centerWidth;
            }

            int pieceX = x + leftWidth;

            for (int i = 0; i < count; i++) {
               OGL.setClip(pieceX, y, step, height);
               bar.draw(pieceX - centerX, y);
               pieceX += step;
            }

            if (background) {
               OGL.setClip(x, y, leftWidth, height);
               bar.draw(x, y);
               OGL.setClip(x + width - rightWidth, y, bar.getWidth(), height);
               bar.draw(x + width - areaWidth, y);
            } else {
               OGL.setClip(x, y, width < leftWidth ? width : leftWidth, height);
               bar.draw(x, y);
               if (width > maxWidth - rightWidth) {
                  int rightX = x + (maxWidth - areaWidth);
                  OGL.setClip(centerWidth + leftWidth + rightX, y, width - (middle + leftWidth), height);
                  bar.draw(rightX, y);
               }
            }

            OGL.setClip(0, 0, Toolkit.getScreenWidth(), Toolkit.getScreenHeight());
         }
      }
   }

   public static int[] calculateBarLayout(SpriteObject bar) {
      bar.setAnimationFrame(0);
      CollisionBox center = bar.getCollisionBox(0);
      CollisionBox area = bar.getCollisionBox(1);
      int width = area.getWidth();
      int height = area.getHeight();
      int left;
      int centerWidth;
      if (center == null) {
         left = width / 3 - bar.getPivotX();
         centerWidth = width - 2 * (width / 3);
      } else {
         left = center.getX();
         centerWidth = center.getWidth();
      }

      int[] values = smBarLayoutValues;
      values[0] = width;
      values[1] = height;
      values[2] = left;
      values[3] = centerWidth;
      values[4] = left;
      values[5] = width - centerWidth - left;
      return values;
   }

   public static boolean isObjectRotateable(IsometricObject object) {
      return object != null && object.getType() == 4 ? ((DecorationObject)object).isRotateable() : false;
   }

   public static void rotateObject(IsometricObject object) {
      if (object != null && object.getType() == 4) {
         DecorationObject decoration = (DecorationObject)object;
         if (decoration.isRotateable()) {
            decoration.setRotation(decoration.getRotation() == 0 ? 1 : 0);
         }
      }
   }

   public static int getObjectRotation(IsometricObject object) {
      return object != null && object.getType() == 4 && ((DecorationObject)object).isRotateable() ? ((DecorationObject)object).getRotation() : 0;
   }

   public static void setObjectRotation(IsometricObject object, int rotation) {
      if (object != null && object.getType() == 4) {
         DecorationObject decoration = (DecorationObject)object;
         if (decoration.isRotateable()) {
            decoration.setRotation(rotation);
         }
      }
   }

   public static void setObjectHealth(IsometricObject object, int health, boolean real) {
      if (object != null) {
         if (object.getType() != 3 && object.getType() != 5) {
            if (object.getType() == 2) {
               ((CharacterObject)object).setHealth(health);
            }
         } else if (real) {
            ((ObjectWithHealth)object).setRealHealth(health);
         } else {
            ((ObjectWithHealth)object).setHealth(health);
         }
      }
   }

   public static IsometricObject getObjectWithID(int type, int id) {
      Vector objects = smScene.getObjects(switch (type) {
         case 50 -> 3;
         case 147 -> 0;
         case 405 -> 4;
         case 837 -> 1;
         case 1104 -> 5;
         case 1583 -> 6;
         default -> -1;
      });
      if (objects == null) {
         return null;
      }

      for (int i = 0; i < objects.size(); i++) {
         IsometricObject object = (IsometricObject)objects.elementAt(i);
         if (object.getID() == id) {
            return object;
         }
      }

      return null;
   }
}
