package com.dchoc.idead.pathfinding;

import com.dchoc.collection.BinaryHeap;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.areas.Area;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import java.util.Vector;

public class Pathfinder {
   public static final boolean ALLOW_DIAGONAL_MOVES = true;
   public static final int DIAGONAL_COST = 14;
   public static final int INITIAL_PATH_CAPACITY = 48;
   public static final int INITIAL_TILE_CAPACITY = 24;
   public static final int VERTICAL_COST = 10;
   private static Pathfinder smInstance;
   private IsometricScene mScene;
   private Vector mPath;
   private BinaryHeap mOpen;
   private Vector mAccessible;
   private short mPathID;
   private short mAccessibleID;

   public Pathfinder(IsometricScene scene) {
      this.mScene = scene;
      smInstance = this;
      this.mPathID = 0;
      this.mAccessibleID = 0;
      this.mPath = new Vector(48);
      this.mOpen = new BinaryHeap(scene.getColumns() * scene.getRows() / 2);
      this.mAccessible = new Vector(scene.getColumns() * scene.getRows());
   }

   public static Pathfinder getInstance() {
      return smInstance;
   }

   public int getPathID() {
      return this.mPathID;
   }

   public int getAccessibleID() {
      return this.mAccessibleID;
   }

   public void addToOpen(PathNode node, int g, int h, PathNode parent) {
      node.setCost((short)g, (short)h);
      node.setParent(parent);
      this.mOpen.add(node);
      node.setOpen(true);
   }

   public PathNode removeFromOpen() {
      PathNode node = (PathNode)this.mOpen.removeFirst();
      node.setOpen(false);
      return node;
   }

   public int calculateHeurestic(PathNode node1, PathNode node2) {
      if (node1.getColumn() != node2.getColumn() && node1.getRow() != node2.getRow()) {
         int dx = Math.abs(node2.getColumn() - node1.getColumn());
         int dy = Math.abs(node2.getRow() - node1.getRow());
         return (dy + dx) * 10 + 14;
      } else {
         int dx = Math.abs(node2.getColumn() - node1.getColumn());
         int dy = Math.abs(node2.getRow() - node1.getRow());
         return (dy + dx) * 10 + 10;
      }
   }

   public Vector findPath(float x1, float y1, float x2, float y2, boolean allowFence, boolean allowZombie) {
      this.mPath.removeAllElements();
      return this.findPath(x1, y1, x2, y2, this.mPath, allowFence, allowZombie) ? this.mPath : null;
   }

   public boolean findPath(float x1, float y1, float x2, float y2, Vector path, boolean allowFence, boolean allowZombie) {
      IsometricTile startTile = this.mScene.getTileAt(x1, y1);
      IsometricTile endTile = this.mScene.getTileAt(x2, y2);
      if (startTile == endTile) {
         return true;
      }

      if (startTile != null && endTile != null) {
         if (endTile.isOccupied()) {
            return false;
         }

         if (!endTile.isWalkable()) {
            return false;
         }

         this.mOpen.removeAll(false);
         this.mPathID++;
         startTile.setPathID(this.mPathID);
         endTile.setPathID(this.mPathID);
         this.addToOpen(startTile, 0, this.calculateHeurestic(startTile, endTile), null);

         while (!this.mOpen.isEmpty()) {
            PathNode current = this.removeFromOpen();
            if (current.getColumn() == endTile.getColumn() && current.getRow() == endTile.getRow()) {
               for (PathNode parent = current; parent != null; parent = parent.getParent()) {
                  if (parent != startTile && parent != endTile) {
                     path.addElement(parent);
                  }
               }

               return true;
            }

            int dx = -1;

            do {
               int dy = -1;

               do {
                  if (dx != 0 || dy != 0) {
                     int curCol = current.getColumn();
                     int curRow = current.getRow();
                     IsometricTile neighbor = this.mScene.getTile(curCol + dx, curRow + dy);
                     if (neighbor != null) {
                        neighbor.setPathID(this.mPathID);
                        if (!neighbor.isOccupied()
                           && !neighbor.isClosed()
                           && neighbor.isWalkable()
                           && (allowFence || !neighbor.hasFence())
                           && (allowZombie || !neighbor.hasZombie())) {
                           int curColCheck = current.getColumn();
                           short moveCost = 10;
                           if (curCol + dx != curColCheck) {
                              int curRowCheck = current.getRow();
                              if (curRow + dy != curRowCheck) {
                                 moveCost = 14;
                              }
                           }

                           if (neighbor.isOpen()) {
                              short g = (short)(moveCost + current.getG());
                              if (g < neighbor.getG()) {
                                 neighbor.setCost(g, (short)neighbor.getH());
                                 neighbor.setParent(current);
                                 this.mOpen.percolateUp(neighbor);
                              }
                           } else {
                              this.addToOpen(neighbor, moveCost, this.calculateHeurestic(neighbor, endTile), current);
                           }
                        }
                     }
                  }
               } while (++dy < 2);
            } while (++dx < 2);

            current.setClosed(true);
         }

         return false;
      } else {
         return false;
      }
   }

   public void updateAccessibleNodes() {
      this.mAccessibleID++;
      Area area = GameEngineUtils.getArea();
      short[] nonAccessible = area.getNonAccessibleAreas();
      if (nonAccessible != null) {
         short extentShort = nonAccessible[0];
         int extent = extentShort;

         for (int i = 1; i < nonAccessible.length; i++) {
            short val = nonAccessible[i];
            int colBlock = val % (area.getWidth() / extent);
            int rowBlock = val / (area.getWidth() / extent);
            if (extentShort > 0) {
               for (int dx = 0; dx < extent; dx++) {
                  int row = extent * rowBlock;

                  for (int dy = 0; dy < extent; dy++) {
                     IsometricTile tile = this.mScene.getTile(dx + colBlock * extent, row);
                     if (tile != null) {
                        tile.setAccessibleID(this.mAccessibleID);
                        tile.setAccessible(false);
                     }

                     row++;
                  }
               }
            }
         }
      }

      if (0 < this.mScene.getColumns()) {
         int i = 0;

         do {
            IsometricTile tileTop = this.mScene.getTile(i, 0);
            if (tileTop.getAccessibleID() != this.mAccessibleID) {
               tileTop.setAccessibleID(this.mAccessibleID);
               tileTop.setAccessible(!tileTop.hasFence() && !tileTop.isOccupied() && tileTop.isWalkable());
               if (tileTop.isAccessible()) {
                  this.mAccessible.addElement(tileTop);
               }
            }

            IsometricTile tileBottom = this.mScene.getTile(i, this.mScene.getRows() - 1);
            if (tileBottom.getAccessibleID() != this.mAccessibleID) {
               tileBottom.setAccessibleID(this.mAccessibleID);
               tileBottom.setAccessible(!tileBottom.hasFence() && !tileBottom.isOccupied() && tileBottom.isWalkable());
               if (tileBottom.isAccessible()) {
                  this.mAccessible.addElement(tileBottom);
               }
            }
         } while (++i < this.mScene.getColumns());
      }

      if (0 < this.mScene.getRows()) {
         int i = 0;

         do {
            IsometricTile tileLeft = this.mScene.getTile(0, i);
            if (tileLeft.getAccessibleID() != this.mAccessibleID) {
               tileLeft.setAccessibleID(this.mAccessibleID);
               tileLeft.setAccessible(!tileLeft.hasFence() && !tileLeft.isOccupied() && tileLeft.isWalkable());
               if (tileLeft.isAccessible()) {
                  this.mAccessible.addElement(tileLeft);
               }
            }

            IsometricTile tileRight = this.mScene.getTile(this.mScene.getColumns() - 1, i);
            if (tileRight.getAccessibleID() != this.mAccessibleID) {
               tileRight.setAccessibleID(this.mAccessibleID);
               tileRight.setAccessible(!tileRight.hasFence() && !tileRight.isOccupied() && tileRight.isWalkable());
               if (tileRight.isAccessible()) {
                  this.mAccessible.addElement(tileRight);
               }
            }
         } while (++i < this.mScene.getRows());
      }

      while (!this.mAccessible.isEmpty()) {
         IsometricTile current = (IsometricTile)this.mAccessible.lastElement();
         this.mAccessible.removeElementAt(this.mAccessible.size() - 1);
         int col = current.getColumn();
         int row = current.getRow();

         for (int dir = 0; dir < 4; dir++) {
            int neighborCol = col;
            int neighborRow = row;
            switch (dir) {
               case 0:
                  neighborRow = row - 1;
                  break;
               case 1:
                  neighborRow = row + 1;
                  break;
               case 2:
                  neighborCol = col - 1;
                  break;
               case 3:
                  neighborCol = col + 1;
            }

            IsometricTile neighbor = this.mScene.getTile(neighborCol, neighborRow);
            if (neighbor != null && neighbor.getAccessibleID() != this.mAccessibleID) {
               neighbor.setAccessibleID(this.mAccessibleID);
               boolean acc = false;
               if (!neighbor.hasFence() && !neighbor.isOccupied()) {
                  acc = current.isWalkable();
               }

               neighbor.setAccessible(acc);
               if (acc) {
                  this.mAccessible.addElement(neighbor);
               }
            }
         }
      }
   }
}
