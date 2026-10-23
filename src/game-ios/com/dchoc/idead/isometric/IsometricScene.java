package com.dchoc.idead.isometric;

import com.dchoc.DCiDead;
import com.dchoc.collection.Quadtree;
import com.dchoc.hud.HUD;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.Profiler;
import com.dchoc.idead.Time;
import com.dchoc.idead.Transform;
import com.dchoc.idead.areas.Area;
import com.dchoc.idead.characters.CharacterObject;
import com.dchoc.idead.characters.NPCCharacter;
import com.dchoc.idead.characters.NeighborCharacter;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.characters.ZombieCharacter;
import com.dchoc.idead.commands.ActionCommand;
import com.dchoc.idead.commands.CommandBase;
import com.dchoc.idead.items.ConstructionItem;
import com.dchoc.idead.items.DecorationItem;
import com.dchoc.idead.items.DestructibleItem;
import com.dchoc.idead.items.FarmingItem;
import com.dchoc.idead.items.FenceItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.items.PlayerItem;
import com.dchoc.idead.items.ZombieItem;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.objects.ConstructionObject;
import com.dchoc.idead.objects.DecorationObject;
import com.dchoc.idead.objects.FarmingObject;
import com.dchoc.idead.objects.FenceObject;
import com.dchoc.idead.objects.LootObject;
import com.dchoc.idead.objects.LootRewardManager;
import com.dchoc.idead.objects.ObjectWithHealth;
import com.dchoc.idead.pathfinding.Pathfinder;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.math.Vec2;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import com.dchoc.windows.WindowSocial;
import java.util.Vector;

public class IsometricScene {
   public static final int STATE_NONE = 0;
   public static final int STATE_SELECT_OBJECT = 1;
   public static final int STATE_MOVE_SELECTED_OBJECT = 2;
   public static final int INITIAL_OBJECTS_CAPACITY = 48;
   public static final int INITIAL_ALERTED_ZOMBIES_CAPACITY = 8;
   public static final int INITIAL_FLOATERS_CAPACITY = 8;
   public static final int INITIAL_FEEDBACKS_CAPACITY = 8;
   private int mColumns;
   private int mRows;
   private float mWidth;
   private float mHeight;
   private float mScale;
   private IsometricFloor mFloor;
   private Camera mCamera;
   private Pathfinder mPathfinder;
   private Quadtree mObjectsTree;
   private Vector mObjectsQuery;
   private IsometricPool mPool;
   private boolean mUpdateFences;
   private boolean mUpdateNeighbors;
   private short mDrawID;
   private short mVisibleID;
   private short mHiddenID;
   private Vector mDeferredObjects;
   private boolean mUpdateDeferredTiles;
   private short mAlertedID;
   private PlayerCharacter mPlayer;
   private LootRewardManager mLootRewards;
   private IsometricObject mSelectedObject;
   private IsometricTile mSelectedObjectTile;
   private boolean mSelectedObjectAtValidPosition;
   private boolean mSelectedObjectPressed;
   private Vec2 mSelectedObjectDelta;
   private Vector mAlertedZombies;
   private boolean mUpdateAlertedObjects;
   private Vector mTurrets;
   private boolean mUpdateEdges;
   private boolean mClearPool;
   private Vector mFloaters;
   private Vector mFeedbacks;
   private int mState;
   private long mUpdateBreakbleObjects;
   private int mGestureX;
   private int mGestureY;
   private IsometricObject mHighlightObject;
   private int mTouchIdleTimer;
   private int mOutsideViewpointTimer;
   private IsometricTile[][] mTiles;
   private Vector[] mObjects;

   public void init() {
      int screenWidth = Toolkit.getScreenWidth();
      int screenHeight = Toolkit.getScreenHeight();
      IsometricUtils.init(this);
      this.mTiles = new IsometricTile[50][];

      for (int column = 0; column < 50; column++) {
         this.mTiles[column] = new IsometricTile[50];
      }

      for (int row = 0; row < 50; row++) {
         for (int column = 0; column < 50; column++) {
            this.mTiles[column][row] = new IsometricTile((byte)column, (byte)row);
         }
      }

      this.mColumns = 50;
      this.mRows = 50;
      this.mWidth = IsometricTile.getTileSize() * 50.0F;
      this.mHeight = IsometricTile.getTileSize() * 50.0F;
      this.mScale = 1.0F;
      this.mFloor = new IsometricFloor(this);
      this.mCamera = new Camera(0.0F, 0.0F, screenWidth, screenHeight);
      boolean hd = DeviceWrapper.useHDIngameGraphics();
      this.mCamera.setBounds(hd ? -200.0F : -100.0F, hd ? -950.0F : -475.0F, hd ? 4690.0F : 2345.0F, hd ? 2940.0F : 1470.0F);
      this.mDrawID = 1;
      this.mVisibleID = 1;
      this.mHiddenID = 1;
      this.mAlertedID = 1;
      this.mPathfinder = new Pathfinder(this);
      this.mLootRewards = new LootRewardManager(this);
      this.mPool = new IsometricPool();
      this.mObjects = new Vector[9];

      for (int type = 0; type < this.mObjects.length; type++) {
         this.mObjects[type] = new Vector(48);
      }

      this.mAlertedZombies = new Vector(8);
      this.mTurrets = new Vector(4);
      this.mFloaters = new Vector(8);
      this.mFeedbacks = new Vector(8);
      this.mObjectsTree = new Quadtree(0.0F, hd ? -650.0F : -325.0F, hd ? 4290.0F : 2145.0F, hd ? 2340.0F : 1170.0F);
      this.mObjectsQuery = new Vector(256);
      this.mDeferredObjects = new Vector(256);
      this.mSelectedObjectDelta = new Vec2();
      this.mState = 0;
      this.mUpdateBreakbleObjects = Time.getTimestamp();
      this.mSelectedObjectAtValidPosition = false;
      this.mSelectedObjectTile = null;
      this.mGestureX = Integer.MAX_VALUE;
      this.mGestureY = Integer.MAX_VALUE;
      this.mClearPool = false;
      this.setScale(Tuner.getInitialZoom());
   }

   public int logicUpdate(int delta) {
      this.mCamera.logicUpdate(delta);
      this.mFloor.logicUpdate(delta);
      this.mLootRewards.logicUpdate(delta);
      if (this.mState == 0) {
         for (int type = 0; this.mObjects != null && type < this.mObjects.length; type++) {
            int index = 0;

            while (!this.mObjects[type].isEmpty() && index < this.mObjects[type].size()) {
               IsometricObject object = (IsometricObject)this.mObjects[type].elementAt(index);
               if (object.logicUpdate(delta) == 1) {
                  ToolkitHelpers.fastRemove(index, this.mObjects[type]);
                  this.removeObject(object, false);
               } else {
                  index++;
               }
            }
         }

         this.updateBreakableObjects();
      }

      if (this.mSelectedObject != null && (HUD.getState() == 6 || HUD.getState() == 8)) {
         this.mSelectedObject.setHighlighted(true);
      }

      for (int i = 0; i < this.mFeedbacks.size(); i++) {
         ((Feedback)this.mFeedbacks.elementAt(i)).logicUpdate(delta);
      }

      for (int i = 0; i < this.mFloaters.size(); i++) {
         ((Floater)this.mFloaters.elementAt(i)).logicUpdate(delta);
      }

      if (this.mUpdateDeferredTiles) {
         this.updateDeferredTiles();
         this.mUpdateDeferredTiles = false;
      }

      if (this.mUpdateFences) {
         this.updateFences();
         this.mUpdateFences = false;
      }

      if (this.mUpdateNeighbors && TutorialFlow.isCompleted()) {
         this.updateNeighbors();
         this.mUpdateNeighbors = false;
      }

      if (this.mUpdateAlertedObjects) {
         this.updateAlertedObjects();
         this.mUpdateAlertedObjects = false;
      }

      if (this.mUpdateEdges) {
         this.mFloor.updateEdges();
         this.mUpdateEdges = false;
      }

      if (this.mClearPool && !GameEngineUtils.isQuickDraw()) {
         this.mPool.releaseAll(true);
         this.mClearPool = false;
      }

      if (this.mTouchIdleTimer >= 0) {
         this.mTouchIdleTimer += delta;
      }

      if (this.mPlayer != null && this.mPlayer.getDrawId() == this.mDrawID) {
         this.mOutsideViewpointTimer = 0;
      } else {
         this.mOutsideViewpointTimer += delta;
      }

      if (this.mState == 0
         && this.mCamera.isFreeScroll()
         && TutorialFlow.isCenterCameraActive()
         && GameEngine.getInstance().getState() == 2
         && this.mPlayer != null) {
         MissionManager.updateCounter(2733, 80, 1);
      }

      return 0;
   }

   public void changeState(int state) {
      if (state != 2 && this.mSelectedObject != null) {
         this.mSelectedObject.setHighlighted(false);
         this.mSelectedObjectTile = null;
         this.mSelectedObjectAtValidPosition = false;
         this.mSelectedObject = null;
      }

      this.mState = state;
   }

   public int getState() {
      return this.mState;
   }

   public void doDraw() {
      Toolkit.getScreenWidth();
      Toolkit.getScreenHeight();
      this.mDrawID++;
      this.mVisibleID++;
      this.mFloor.doDrawSky();
      OGL.setScreenScale(this.mScale, this.mScale);
      this.mFloor.doDraw(this.mCamera);
      if (!WindowManager.isFullscreenWindowVisible()) {
         this.mObjectsQuery.removeAllElements();
         this.mObjectsTree.query(this.mCamera, this.mObjectsQuery);

         for (int i = 0; i < this.mObjectsQuery.size(); i++) {
            ((IsometricObject)this.mObjectsQuery.elementAt(i)).setVisibleId(this.mVisibleID);
         }

         sortByPositionSlow(this.mObjectsQuery);
         this.clearDeferredObjects();

         for (int i = 0; i < this.mObjectsQuery.size(); i++) {
            IsometricObject object = (IsometricObject)this.mObjectsQuery.elementAt(i);
            IsometricObject deferrer = object.getDeferringObject();
            if (deferrer != null && !deferrer.isVisible()) {
               deferrer.setDrawId(this.mDrawID);
            }

            if (deferrer != null && deferrer.getDrawId() != this.mDrawID) {
               this.addDefferdObject(object);
            } else {
               object.doDraw(this.mCamera);
               object.setDrawId(this.mDrawID);
               if (object.isDeferring() && !this.mDeferredObjects.isEmpty()) {
                  this.drawDeferredRecursive(object);
               }
            }
         }

         this.mLootRewards.doDraw(this.mCamera);

         for (int i = 0; i < this.mFeedbacks.size(); i++) {
            ((Feedback)this.mFeedbacks.elementAt(i)).doDraw(this.mCamera);
         }

         OGL.setScreenScale(1.0F, 1.0F);

         for (int i = 0; i < this.mFloaters.size(); i++) {
            ((Floater)this.mFloaters.elementAt(i)).doDraw(this.mCamera);
         }
      }

      OGL.setScreenScale(1.0F, 1.0F);
   }

   public static void sortByPositionSlow(Vector objects) {
      for (int i = 0; i < objects.size() - 1; i++) {
         IsometricObject first = (IsometricObject)objects.elementAt(i);

         for (int j = i + 1; j < objects.size(); j++) {
            IsometricObject next = (IsometricObject)objects.elementAt(j);
            if (isObjectFrontOf(first, next)) {
               objects.setElementAt(next, i);
               objects.setElementAt(first, j);
               first = next;
            }
         }
      }
   }

   private void drawDebug() {
      float cameraX = this.mCamera.getX();
      float cameraY = this.mCamera.getY();
      this.mObjectsTree.doDraw(-cameraX, -cameraY);

      for (int column = 0; column < this.mColumns; column++) {
         for (int row = 0; row < this.mRows; row++) {
            IsometricTile tile = this.getTile(column, row);
            Vec2 screen = Transform.toScreen(tile.getX(), tile.getY());
            screen.getX();
            this.mCamera.getX();
            screen.getY();
            this.mCamera.getY();
         }
      }
   }

   public void addFloater(String text, float x, float y) {
      Floater oldest = null;
      ImageFont font = DCiDead.getFont(0);

      for (int i = 0; i < this.mFloaters.size(); i++) {
         Floater floater = (Floater)this.mFloaters.elementAt(i);
         if (!floater.isActive()) {
            floater.init(text, font, x, y);
            return;
         }

         if (oldest == null || floater.getElapsedTime() > oldest.getElapsedTime()) {
            oldest = floater;
         }
      }

      if (this.mFloaters.size() > 7) {
         oldest.init(text, font, x, y);
      } else {
         Floater floater = new Floater();
         floater.init(text, font, x, y);
         this.mFloaters.addElement(floater);
      }
   }

   public void addFeedback(float x, float y) {
      Feedback oldest = null;

      for (int i = 0; i < this.mFeedbacks.size(); i++) {
         Feedback feedback = (Feedback)this.mFeedbacks.elementAt(i);
         if (!feedback.isActive()) {
            feedback.init(x, y);
            return;
         }

         if (oldest == null || feedback.getElapsedTime() > oldest.getElapsedTime()) {
            oldest = feedback;
         }
      }

      if (this.mFeedbacks.size() >= 8) {
         oldest.init(x, y);
      } else {
         Feedback feedback = new Feedback();
         feedback.init(x, y);
         this.mFeedbacks.addElement(feedback);
      }
   }

   public void drawGrid(IsometricObject object) {
      float x = object.getX();
      float y = object.getY();

      for (int i = 0; i < 11; i++) {
         float tileSize = IsometricTile.getTileSize();
         float lineX = x + i * tileSize;
         float endY = y + tileSize * 10.0F;
         float sx = Transform.toScreenX(lineX, y);
         float sy = Transform.toScreenY(lineX, y);
         float ex = Transform.toScreenX(lineX, endY);
         float ey = Transform.toScreenY(lineX, endY);
         OGL.setColorRGB(65280);
         float cameraX1 = this.mCamera.getX();
         float cameraY1 = this.mCamera.getY();
         float cameraX2 = this.mCamera.getX();
         float cameraY2 = this.mCamera.getY();
         OGL.drawLine((int)(sx - cameraX1), (int)(sy - cameraY1), (int)(ex - cameraX2), (int)(ey - cameraY2));
      }
   }

   public void addDefferdObject(IsometricObject object) {
      this.mDeferredObjects.addElement(object);
   }

   public void clearDeferredObjects() {
      this.mDeferredObjects.removeAllElements();
   }

   public static boolean isObjectFrontOf(IsometricObject first, IsometricObject second) {
      if (first != null && second != null) {
         if (first.getLayer() > second.getLayer()) {
            return true;
         }

         int firstLayer = first.getLayer();
         int secondLayer = second.getLayer();
         if (secondLayer <= firstLayer) {
            if (!(first.getY() <= second.getY())) {
               return true;
            }

            if (first.getY() == second.getY()) {
               return first.getX() <= second.getX();
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void doPostDraw() {
      Toolkit.getScreenWidth();
      Toolkit.getScreenHeight();
   }

   public int getTouchIdleTimer() {
      return this.mTouchIdleTimer;
   }

   public int getOutsideViewpointTimer() {
      return this.mOutsideViewpointTimer;
   }

   public void processTouchEvent(TouchEvent event) {
      if (!WindowManager.isNPCDialogOpeningOrClosing()) {
         this.mLootRewards.processToucEvent(event);
         if (!event.isConsumed()) {
            float cameraX = this.mCamera.getX();
            int touchX = event.getX();
            float scaleX = this.mScale;
            float cameraY = this.mCamera.getY();
            int touchY = event.getY();
            float x = cameraX + touchX * (1.0F / scaleX);
            float y = cameraY + touchY * (1.0F / this.mScale);
            switch (event.getType()) {
               case 0:
                  this.mTouchIdleTimer = -1;
                  this.mCamera.setFreeScroll(false);
                  if (this.mState != 0) {
                     if (this.mState == 2) {
                        IsometricObject object = this.getObjectAt(x, y, true, true, false);
                        if (object != null && object == this.mSelectedObject) {
                           this.mSelectedObjectPressed = true;
                           float worldX = Transform.toWorldX(x, y);
                           float worldY = Transform.toWorldY(x, y);
                           float tileX = this.mSelectedObjectTile.getX();
                           float tileY = this.mSelectedObjectTile.getY();
                           this.mSelectedObjectDelta.set(worldX - tileX, worldY - tileY);
                        }
                     }
                  } else {
                     this.mHighlightObject = this.getObjectAt(x, y, true, true, true);
                     if (this.mHighlightObject != null) {
                        if (!this.mHighlightObject.isFloor()) {
                           this.mHighlightObject.touchEventOccured(event);
                        }

                        if (this.mHighlightObject != null && this.mHighlightObject.getType() == 2) {
                           this.mCamera.setFreeScroll(false);
                        }
                     }
                  }
                  break;
               case 1:
                  if (this.mState == 2 && this.mSelectedObjectPressed) {
                     float worldX = Transform.toWorldX(x, y);
                     float worldY = Transform.toWorldY(x, y);
                     float dx = this.mSelectedObjectDelta.getX();
                     float dy = this.mSelectedObjectDelta.getY();
                     IsometricTile tile = this.getTileAt(worldX - dx, worldY - dy);
                     if (tile != null && tile != this.mSelectedObjectTile) {
                        if (this.mSelectedObject != null) {
                           this.mSelectedObject.setPosition(tile.getX(), tile.getY());
                           this.mSelectedObjectTile = tile;
                        }

                        this.checkSelectedObjectAtValidPosition();
                        this.mCamera.flyTo(this.mSelectedObject);
                     }
                  } else {
                     if (this.mHighlightObject != null) {
                        this.mHighlightObject.touchEventOccured(event);
                     }

                     int dx = event.getDeltaX();
                     float currentScaleX = this.mScale;
                     int dy = event.getDeltaY();
                     float currentScaleY = this.mScale;
                     if (TutorialFlow.isCompleted()) {
                        this.mCamera.move(-dx * (1.0F / currentScaleX), -dy * (1.0F / currentScaleY));
                     }
                  }
                  break;
               case 2:
                  this.mTouchIdleTimer = 0;
                  this.mCamera.setFreeScroll(true);
                  this.mGestureX = Integer.MAX_VALUE;
                  this.mGestureY = Integer.MAX_VALUE;
                  this.mSelectedObjectPressed = false;
                  if (this.mHighlightObject != null) {
                     this.mHighlightObject.touchEventOccured(event);
                  }
                  break;
               case 3:
                  if (this.mState == 0) {
                     IsometricObject object = this.getObjectAt(x, y, true, true, true);
                     boolean floorTouch = object == null || object.isFloor();
                     if (!floorTouch && (object.isHidden() || object.getType() == 4)) {
                        float worldX = Transform.toWorldX(x, y);
                        float worldY = Transform.toWorldY(x, y);
                        IsometricTile tile = this.getTileAt(worldX, worldY);
                        if (tile != null && !tile.containsObject(object)) {
                           floorTouch = true;
                        }
                     }

                     if (floorTouch) {
                        if (this.mFloor.getSideCollisionIndex(event) == -1) {
                           if (this.mFloor.isVisibleAt(x, y)) {
                              Vec2 world = Transform.toWorld(x, y);
                              this.mPlayer.addMoveAction(world.getX(), world.getY());
                           } else {
                              String text = Toolkit.getText(1999);
                              this.addFloater(text, this.mPlayer.getScreenX(), this.mPlayer.getScreenY());
                           }

                           this.addFeedback(x, y);
                        }
                     } else {
                        object.touchEventOccured(event);
                        if (object.getType() == 1 && TutorialFlow.isCompleted()) {
                           AvatarDescription avatar = ((PlayerCharacter)object).getAvatar();
                           if (PlayerProfile.getActiveAvatar() != avatar) {
                              PlayerProfile.setActiveAvatar(avatar.getCharacter());
                           }
                        }
                     }
                  } else if (this.mState == 1) {
                     IsometricObject object = this.getObjectAt(x, y, true, true, false);
                     if (object != null) {
                        if (HUD.getState() != 8) {
                           int type = object.getType();
                           if ((type & -3) == 4 || type == 5 || type == 0) {
                              this.setSelectedObject(object);
                              this.mSelectedObjectAtValidPosition = true;
                              this.mSelectedObject.setHighlighted(true, -1151271072);
                              this.centerToObject(object);
                              HUD.changeState(9);
                           }
                        } else {
                           HUD.getInGame().getEdit().calculateHireObjects(object);
                        }
                     }
                  } else if (this.mState == 2) {
                     float worldX = Transform.toWorldX(x, y);
                     float worldY = Transform.toWorldY(x, y);
                     IsometricTile tile = this.getEditMoveTileAt(worldX, worldY);
                     if (tile != null && tile != this.mSelectedObjectTile && this.mSelectedObject != null) {
                        float tileX = tile.getX();
                        float tileY = tile.getY();
                        Item item = this.mSelectedObject.getItem();
                        this.mSelectedObjectAtValidPosition = IsometricUtils.isPositionValidAt(tileX, tileY, item, this.mSelectedObject);
                        this.mSelectedObject.setHighlighted(true, this.mSelectedObjectAtValidPosition ? -1151271072 : -1140891552);
                        this.mSelectedObject.setPosition(tile.getX(), tile.getY());
                        this.mSelectedObjectTile = tile;
                        this.mCamera.flyTo(this.mSelectedObject);
                     }
                  }
                  break;
               case 4:
                  if (this.mHighlightObject != null) {
                     this.mHighlightObject.touchEventOccured(event);
                     this.mHighlightObject = null;
                  }

                  int dx = Math.abs(event.getX() - event.getInitialX());
                  int dy = Math.abs(event.getY() - event.getInitialY());
                  if (TutorialFlow.isCompleted() && this.mGestureX != Integer.MAX_VALUE && this.mGestureY != Integer.MAX_VALUE) {
                     int previous = (int)transxSqrt(this.mGestureX * this.mGestureX + this.mGestureY * this.mGestureY);
                     int current = (int)transxSqrt(dx * dx + dy * dy);
                     this.updateScale(current - previous);
                  }

                  this.mGestureX = dx;
                  this.mGestureY = dy;
                  break;
               case 5:
                  if (this.mState == 0 && this.mHighlightObject != null) {
                     this.mHighlightObject.touchEventOccured(event);
                  }
            }

            if (!event.isConsumed()) {
               this.mFloor.touchEventOccured(event);
            }
         }
      }
   }

   private static double transxSqrt(double value) {
      float x = (float)value;
      float y = Float.intBitsToFloat(1597463007 - (Float.floatToRawIntBits(x) >> 1));
      if (y != 0.0F) {
         float half = x * 0.5F;
         int i = 1;

         while (true) {
            float next = y * (1.5F - y * (half * y));
            if (y == next || i >= 100) {
               y = next;
               break;
            }

            y = next;
            i++;
         }
      }

      return y * value;
   }

   public IsometricObject createObject(Item item, float x, float y) {
      if (item == null) {
         return null;
      }

      return switch (item.getType()) {
         case 50 -> ((DestructibleItem)item).isZombie() ? this.createZombie((ZombieItem)item, x, y) : this.createLoot((DestructibleItem)item, x, y);
         case 147 -> this.createConstruction((ConstructionItem)item, x, y);
         case 405 -> this.createDecoration((DecorationItem)item, x, y);
         case 1104 -> this.createFence((FenceItem)item, x, y);
         case 1583 -> this.createFarming((FarmingItem)item, x, y);
         default -> null;
      };
   }

   public PlayerCharacter createPlayer(AvatarDescription avatar, float x, float y, boolean activate) {
      if (avatar == null) {
         return null;
      }

      Profiler.startTimer(17);
      PlayerItem item = ItemManager.getPlayer(avatar.getGender());
      PlayerCharacter player = (PlayerCharacter)this.createObject(1, item);
      player.init(x, y, item, avatar);
      this.addObject(player);
      if (activate) {
         if (this.mPlayer != null) {
            this.mPlayer.cancelActions(true);
         }

         this.mPlayer = player;
         this.centerToObject(player);
      }

      Profiler.stopTimer(17);
      return player;
   }

   public ZombieCharacter createZombie(ZombieItem item, float x, float y) {
      if (item == null) {
         return null;
      }

      Profiler.startTimer(18);
      ZombieCharacter zombie = (ZombieCharacter)this.createObject(2, item);
      zombie.init(x, y, item);
      this.addObject(zombie);
      Profiler.stopTimer(18);
      return zombie;
   }

   public LootObject createLoot(DestructibleItem item, float x, float y) {
      if (item == null) {
         return null;
      }

      Profiler.startTimer(19);
      LootObject loot = (LootObject)this.createObject(3, item);
      loot.init(x, y, item);
      this.addObject(loot);
      if (item.isTurrent()) {
         this.mTurrets.addElement(loot);
      }

      Profiler.stopTimer(19);
      return loot;
   }

   public DecorationObject createDecoration(DecorationItem item, float x, float y) {
      if (item == null) {
         return null;
      }

      Profiler.startTimer(20);
      DecorationObject decoration = (DecorationObject)this.createObject(4, item);
      decoration.init(x, y, item);
      this.addObject(decoration);
      Profiler.stopTimer(20);
      return decoration;
   }

   public ConstructionObject createConstruction(ConstructionItem item, float x, float y) {
      if (item == null) {
         return null;
      }

      Profiler.startTimer(16);
      ConstructionObject construction = (ConstructionObject)this.createObject(0, item);
      construction.init(x, y, item);
      this.addObject(construction);
      Profiler.stopTimer(16);
      return construction;
   }

   public ConstructionObject createConstruction(ConstructionItem item, float x, float y, int stage, int clicks) {
      if (item == null) {
         return null;
      }

      Profiler.startTimer(16);
      ConstructionObject construction = (ConstructionObject)this.createObject(0, item);
      construction.init(x, y, item, stage, clicks);
      this.addObject(construction);
      Profiler.stopTimer(16);
      return construction;
   }

   public FenceObject createFence(FenceItem item, float x, float y) {
      if (item == null) {
         return null;
      }

      Profiler.startTimer(21);
      FenceObject fence = (FenceObject)this.createObject(5, item);
      fence.init(x, y, item);
      this.addObject(fence);
      Profiler.stopTimer(21);
      return fence;
   }

   public FarmingObject createFarming(FarmingItem item, float x, float y) {
      if (item == null) {
         return null;
      }

      Profiler.startTimer(22);
      FarmingObject farming = (FarmingObject)this.createObject(6, item);
      farming.init(x, y, item);
      this.addObject(farming);
      Profiler.stopTimer(22);
      return farming;
   }

   public NeighborCharacter createNeighbor(NeighborProfile profile, int character, float x, float y) {
      if (profile != null && profile.getAvatar(character) != null) {
         Profiler.startTimer(23);
         AvatarDescription avatar = profile.getAvatar(character);
         PlayerItem item = ItemManager.getPlayer(avatar.getGender());
         NeighborCharacter neighbor = (NeighborCharacter)this.createObject(7, item);
         neighbor.init(x, y, item, profile, character);
         this.addObject(neighbor);
         Profiler.stopTimer(23);
         return neighbor;
      } else {
         return null;
      }
   }

   public NPCCharacter createNPC(NeighborProfile profile, int playerItemId, float x, float y) {
      if (profile == null) {
         return null;
      }

      Profiler.startTimer(24);
      PlayerItem item = ItemManager.getPlayer(playerItemId);
      NPCCharacter npc = (NPCCharacter)this.createObject(8, item);
      npc.init(x, y, item, profile);
      this.addObject(npc);
      Profiler.stopTimer(24);
      return npc;
   }

   public IsometricObject findObject(int type, int id) {
      for (int i = 0; i < this.mObjects[type].size(); i++) {
         IsometricObject object = (IsometricObject)this.mObjects[type].elementAt(i);
         if (object.getID() == id) {
            return object;
         }
      }

      return null;
   }

   public void addObject(IsometricObject object) {
      if (object != null) {
         this.mObjectsTree.add(object);
         this.mObjects[object.getType()].addElement(object);
      }
   }

   public void removeObject(IsometricObject object, boolean removeFromGroup) {
      if (object != null) {
         object.remove();
         if (object.isDeferring()) {
            this.setUpdateDeferredTiles();
         }

         if (object.getType() == 7) {
            this.setUpdateNeighbors();
         } else if (object.getType() == 2) {
            this.setUpdateAlertedObjects();
            HUD.closeHealthBar(object);
         } else if (object.getType() == 3 && ((DestructibleItem)object.getItem()).isTurrent()) {
            this.mTurrets.removeElement(object);
         }

         this.mObjectsTree.remove(object);
         if (removeFromGroup) {
            this.mObjects[object.getType()].removeElement(object);
         }

         this.mPool.push(object, object.isReleasable());
      }
   }

   public void clear(boolean release) {
      if (this.mPlayer != null) {
         this.mPlayer.cancelActions(true);
         this.mPlayer = null;
      }

      for (int row = 0; row < 50; row++) {
         for (int column = 0; column < 50; column++) {
            this.mTiles[column][row].removeAllObjects();
            this.mTiles[column][row].setWalkable(true);
         }
      }

      for (int type = 0; this.mObjects != null && type < this.mObjects.length; type++) {
         for (int i = 0; i < this.mObjects[type].size(); i++) {
            this.mPool.push((IsometricObject)this.mObjects[type].elementAt(i), release);
         }
      }

      for (int type = 0; this.mObjects != null && type < this.mObjects.length; type++) {
         this.mObjects[type].removeAllElements();
      }

      this.mObjectsTree.removeAll();
      this.mAlertedZombies.removeAllElements();
      this.mFeedbacks.removeAllElements();
      this.mFloaters.removeAllElements();
      this.mTurrets.removeAllElements();
      ZombieCharacter.resetFollowers();
      this.mLootRewards.clearRewards();
      HUD.getInGame().removeAcceptHelps();
      this.setUpdateDeferredTiles();
      this.setUpdateFences();
      this.setUpdateAlertedObjects();
   }

   public void setSelectedObject(IsometricObject object) {
      if (this.mSelectedObject != null) {
         this.mSelectedObject.setHighlighted(false);
      }

      this.mSelectedObject = object;
      if (object != null) {
         this.mSelectedObjectTile = object.getTile();
      }
   }

   public IsometricObject getSelectedObject() {
      return this.mSelectedObject;
   }

   public boolean isSelectedObjectAtValidPosition() {
      return this.mSelectedObjectAtValidPosition;
   }

   public void checkSelectedObjectAtValidPosition() {
      if (this.mSelectedObject != null) {
         float x = this.mSelectedObject.getX();
         float y = this.mSelectedObject.getY();
         Item item = this.mSelectedObject.getItem();
         this.mSelectedObjectAtValidPosition = IsometricUtils.isPositionValidAt(x, y, item, this.mSelectedObject);
         this.mSelectedObject.setHighlighted(true, this.mSelectedObjectAtValidPosition ? -1151271072 : -1140891552);
      }
   }

   public void centerToObject(IsometricObject object) {
      if (object != null) {
         float x = object.getBoundsX();
         float width = object.getBoundsWidth();
         float y = object.getBoundsY();
         float height = object.getBoundsHeight();
         float cameraWidth = this.mCamera.getWidth();
         float cameraHeight = this.mCamera.getHeight();
         this.mCamera.moveTo(x + width * 0.5F - cameraWidth * 0.5F, y + height * 0.5F - cameraHeight * 0.5F);
      }
   }

   public void centerToObjectWithBounds(IsometricObject object) {
      if (object != null) {
         float x = object.getBoundsX();
         float width = object.getBoundsWidth();
         float y = object.getBoundsY();
         float height = object.getBoundsHeight();
         float cameraWidth = this.mCamera.getWidth();
         float cameraHeight = this.mCamera.getHeight();
         this.mCamera.moveToWithBounds(x + width * 0.5F - cameraWidth * 0.5F, y + height * 0.5F - cameraHeight * 0.5F);
      }
   }

   public void centerToTile(IsometricTile tile, float scale) {
      if (tile != null) {
         this.setScale(scale);
         Vec2 screen = Transform.toScreen(tile.getX(), tile.getY());
         float x = screen.getX();
         float width = this.mCamera.getWidth();
         float y = screen.getY();
         float height = this.mCamera.getHeight();
         this.mCamera.moveTo(x - width * 0.5F, y - height * 0.5F);
      }
   }

   public void updateScale(int delta) {
      int width = Toolkit.getScreenWidth();
      int height = Toolkit.getScreenHeight();
      float candidate = delta * 0.005F + this.mScale;
      float min = Tuner.getMinZoom();
      float max = Tuner.getMaxZoom();
      float scale = candidate;
      if (candidate < min) {
         scale = min;
      } else if (candidate > max) {
         scale = max;
      }

      float x = this.mCamera.getX();
      float oldWidth = this.mCamera.getWidth();
      float y = this.mCamera.getY();
      float oldHeight = this.mCamera.getHeight();
      float inverse = 1.0F / scale;
      float newWidth = width * inverse;
      float newHeight = height * inverse;
      this.mCamera.set(x - (newWidth - oldWidth) * 0.5F, y - (newHeight - oldHeight) * 0.5F, newWidth, newHeight);
      this.mScale = scale;
   }

   public void setScale(float scale) {
      int width = Toolkit.getScreenWidth();
      int height = Toolkit.getScreenHeight();
      float min = Tuner.getMinZoom();
      float max = Tuner.getMaxZoom();
      if (min > scale) {
         scale = min;
      } else if (max < scale) {
         scale = max;
      }

      float x = this.mCamera.getX();
      float oldWidth = this.mCamera.getWidth();
      float y = this.mCamera.getY();
      float oldHeight = this.mCamera.getHeight();
      float inverse = 1.0F / scale;
      float newWidth = width * inverse;
      float newHeight = height * inverse;
      this.mCamera.set(x - (newWidth - oldWidth) * 0.5F, y - (newHeight - oldHeight) * 0.5F, newWidth, newHeight);
      this.mScale = scale;
   }

   public void setClearPool() {
      this.mClearPool = true;
   }

   public void setUpdateFences() {
      this.mUpdateFences = true;
   }

   public void updateWalkableTiles() {
      Area area = GameEngineUtils.getArea();
      short[] regions = area.getNonWalkableAreas();
      if (regions != null) {
         int extent = regions[0];

         for (int i = 1; i < regions.length; i++) {
            int column = regions[i] % (area.getWidth() / extent);
            int row = regions[i] / (area.getWidth() / extent);

            for (int dx = 0; dx < extent; dx++) {
               for (int dy = 0; dy < extent; dy++) {
                  IsometricTile tile = this.getTile(dx + column * extent, row * extent + dy);
                  if (tile != null) {
                     tile.setWalkable(false);
                  }
               }
            }
         }
      }
   }

   public void updateAccessibleTiles() {
      this.mPathfinder.updateAccessibleNodes();
   }

   public void setUpdateBreakableObjects(long timestamp) {
      this.mUpdateBreakbleObjects = timestamp;
   }

   public long getUpdateBreakableObjects() {
      return this.mUpdateBreakbleObjects;
   }

   public void setUpdateNeighbors() {
      this.mUpdateNeighbors = true;
   }

   public void setUpdateEdges() {
      this.mUpdateEdges = true;
   }

   public void setUpdateDeferredTiles() {
      this.mUpdateDeferredTiles = true;
   }

   public void updateDeferredTiles() {
      for (int row = 0; row < this.mRows; row++) {
         for (int column = this.mColumns - 1; column >= 0; column--) {
            this.mTiles[column][row].setDeferring(null);
         }
      }

      for (int type = 0; this.mObjects != null && type < this.mObjects.length; type++) {
         if (type > 7 || (1 << type & 162) == 0) {
            for (int i = 0; i < this.mObjects[type].size(); i++) {
               IsometricObject object = (IsometricObject)this.mObjects[type].elementAt(i);
               if (object.getLayer() == 2) {
                  int width = object.getTileSizeX();
                  int height = object.getTileSizeY();
                  if ((width >= 2 || height >= 2) && object.getTile() != null) {
                     int column = object.getTile().getColumn();
                     int row = object.getTile().getRow();
                     int endRow = row - height;
                     if (row >= 0 && height > 0) {
                        while (true) {
                           IsometricObject occupying = null;

                           for (int x = column - 1; x >= 0; x--) {
                              IsometricTile tile = this.mTiles[x][row];
                              boolean occupied = tile.isOccupied();
                              if (occupied && occupying == null) {
                                 if (tile.getOccupying().isDeferring()) {
                                    occupying = tile.getOccupying();
                                 }
                              } else if (occupying != null && (!occupied || tile.getOccupying() != occupying)) {
                                 break;
                              }

                              tile.setDeferring(object);
                           }

                           row--;
                           if (row < 0 || row <= endRow) {
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void addAlertedZombie(ZombieCharacter zombie) {
      if (!this.mAlertedZombies.contains(zombie)) {
         if (zombie.isGuarding()) {
            zombie.playAlertSound();
         }

         this.mAlertedZombies.addElement(zombie);
         MissionManager.updateCounter(2971, 80, 1);
      }

      this.setUpdateAlertedObjects();
   }

   public void removeAlertedZombie(ZombieCharacter zombie) {
      this.mAlertedZombies.removeElement(zombie);
      this.setUpdateAlertedObjects();
   }

   public Vector getAlertedZombies() {
      return this.mAlertedZombies;
   }

   public void setUpdateAlertedObjects() {
      this.mUpdateAlertedObjects = true;
   }

   public void updateAlertedObjects() {
      this.mAlertedID++;
      int itemCount = PlayerProfile.getInventory().getItemCount(2056);

      for (int i = 0; i < this.mAlertedZombies.size(); i++) {
         ZombieCharacter zombie = (ZombieCharacter)this.mAlertedZombies.elementAt(i);
         if (zombie.getHealth() > 0) {
            zombie.setAlertedId(this.mAlertedID);
            IsometricTile tile = zombie.getTile();
            if (tile != null) {
               int column = tile.getColumn();
               int row = tile.getRow();
               int range = zombie.getAlertRange() >> (itemCount > 0 ? 1 : 0);

               for (int dx = -range; dx < range; dx++) {
                  for (int dy = -range; dy < range; dy++) {
                     IsometricTile nearby = this.getTile(dx + column, dy + row);
                     if (nearby != null) {
                        Vector objects = nearby.getObjects();

                        for (int j = 0; j < objects.size(); j++) {
                           IsometricObject object = (IsometricObject)objects.elementAt(j);
                           int type = object.getType();
                           if (type != 1 && type != 2 && type != 7) {
                              object.setAlertedId(this.mAlertedID);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public IsometricObject getClosestObject(Vector objects, boolean excludePlayer, boolean ignoreFloor) {
      if (objects.isEmpty()) {
         return null;
      }

      IsometricObject best = (IsometricObject)objects.firstElement();

      for (int i = 1; i < objects.size(); i++) {
         IsometricObject candidate = (IsometricObject)objects.elementAt(i);
         int bestType = best.getType();
         int candidateType = candidate.getType();
         if ((!excludePlayer || candidate != this.mPlayer) && !this.mPlayer.hasActionWith(candidate)) {
            if (ignoreFloor) {
               if (candidate.isFloor()) {
                  continue;
               }

               if (best.isFloor()) {
                  best = candidate;
                  continue;
               }
            }

            if (candidateType == 2) {
               if (bestType != 2) {
                  best = candidate;
                  continue;
               }

               if (best.getY() < candidate.getY()) {
                  best = candidate;
                  continue;
               }
            }

            if (candidate.isPickupable() && !best.isPickupable() && bestType != 2) {
               best = candidate;
            } else if (bestType != 2 && candidate.isPickupable()) {
               if (excludePlayer && best == this.mPlayer) {
                  best = candidate;
               } else if (best.isDeferring() && candidate.getDeferringObject() == best) {
                  best = candidate;
               } else {
                  boolean candidateInFront = best.getY() < candidate.getY() || best.getY() == candidate.getY() && best.getX() > candidate.getX();
                  if (candidateInFront && !candidate.isHidden()) {
                     best = candidate;
                  } else if (best.isHidden()) {
                     best = candidate;
                  }
               }
            }
         }
      }

      return best;
   }

   public IsometricObject getObjectAt(float x, float y, boolean excludePlayer, boolean includeDead, boolean ignoreFloor) {
      Vector objects = this.mObjectsTree.query(x, y);
      int i = 0;

      while (i < objects.size()) {
         IsometricObject object = (IsometricObject)objects.elementAt(i);
         if (!includeDead) {
            int type = object.getType();
            boolean alive = true;
            if (type == 5 || type == 3) {
               alive = ((ObjectWithHealth)object).isAlive();
            } else if (type == 2) {
               alive = ((CharacterObject)object).isAlive();
            }

            if (!alive) {
               IsometricUtils.fastRemove(i, objects);
               continue;
            }
         }

         if (object.getType() == 4) {
            if (((DecorationObject)object).isHiddenByMission()) {
               IsometricUtils.fastRemove(i, objects);
               continue;
            }
         } else if (object.getType() == 0 && ((ConstructionObject)object).isHiddenByMission()) {
            IsometricUtils.fastRemove(i, objects);
            continue;
         }

         if (!this.mFloor.isObjectVisible(object)) {
            IsometricUtils.fastRemove(i, objects);
         } else {
            float objectX = object.getScreenX();
            float objectY = object.getScreenY();
            float relativeX = x - objectX;
            float relativeY = y - objectY;
            CollisionBox box = object.getCollisionBox();
            boolean outside = box != null && !IsometricUtils.isInsideBox(relativeX, relativeY, box);
            float[] polygon = object.getCollisionPolygon();
            if (polygon != null) {
               if (!IsometricUtils.isInsidePolygon(relativeX, relativeY, polygon) || outside) {
                  IsometricUtils.fastRemove(i, objects);
                  continue;
               }
            } else if (outside) {
               IsometricUtils.fastRemove(i, objects);
               continue;
            }

            i++;
         }
      }

      return this.getClosestObject(objects, excludePlayer, ignoreFloor);
   }

   public void setPlayer(PlayerCharacter player) {
      this.mPlayer = player;
      if (player != null) {
         player.eventRecieved(6);
      }
   }

   public PlayerCharacter getPlayer() {
      return this.mPlayer;
   }

   public PlayerCharacter getPlayer(AvatarDescription avatar) {
      if (avatar != null) {
         for (int i = 0; i < this.mObjects[1].size(); i++) {
            PlayerCharacter player = (PlayerCharacter)this.mObjects[1].elementAt(i);
            if (player.getAvatar() == avatar) {
               return player;
            }
         }
      }

      return null;
   }

   public IsometricPool getPool() {
      return this.mPool;
   }

   public IsometricFloor getFloor() {
      return this.mFloor;
   }

   public LootRewardManager getLootRewards() {
      return this.mLootRewards;
   }

   public Vector getTurrets() {
      return this.mTurrets;
   }

   public float getScale() {
      return this.mScale;
   }

   public IsometricTile getTile(int column, int row) {
      return column >= 0 && column < this.mColumns && row >= 0 && row < this.mRows ? this.mTiles[column][row] : null;
   }

   public IsometricTile getTileAt(float x, float y) {
      if (0.0F <= x && x < this.mWidth && 0.0F <= y && y < this.mHeight) {
         float sizeX = IsometricTile.getTileSize();
         float sizeY = IsometricTile.getTileSize();
         return this.getTile((int)(x / sizeX), (int)(y / sizeY));
      } else {
         return null;
      }
   }

   public IsometricTile getEditMoveTileAt(float x, float y) {
      if (!(x < 0.0F) && !(this.mWidth <= x)) {
         int row = 0;
         if (0.0F < y) {
            if (this.mHeight <= y) {
               row = (this.mTiles[0] == null ? 0 : this.mTiles[0].length) - 1;
            } else {
               row = (int)(y / IsometricTile.getTileSize());
               if (row > 0) {
                  row--;
               }
            }
         }

         return this.getTile((int)(x / IsometricTile.getTileSize()), row);
      } else {
         return null;
      }
   }

   public IsometricTile getTileAtCenterOfScreen() {
      int width = Toolkit.getScreenWidth();
      int height = Toolkit.getScreenHeight();
      float cameraX = this.mCamera.getX();
      float scaleX = this.mScale;
      float cameraY = this.mCamera.getY();
      float y = cameraY + (height >> 1) * (1.0F / this.mScale);
      float x = cameraX + (width >> 1) * (1.0F / scaleX);
      float worldX = Transform.toWorldX(x, y);
      float worldY = Transform.toWorldY(x, y);
      int column = this.getClosestTileX(worldX);
      int row = this.getClosestTileY(worldY);
      return this.getTile(column, row);
   }

   public int getClosestTileX(float x) {
      int column = (int)(x / IsometricTile.getTileSize());
      if (column < 0) {
         return 0;
      }

      if (column >= this.mColumns) {
         column = this.mColumns - 1;
      }

      return column;
   }

   public int getClosestTileY(float y) {
      int row = (int)(y / IsometricTile.getTileSize());
      if (row < 0) {
         return 0;
      }

      if (row >= this.mRows) {
         row = this.mRows - 1;
      }

      return row;
   }

   public int getColumns() {
      return this.mColumns;
   }

   public int getRows() {
      return this.mRows;
   }

   public float getWidth() {
      return this.mWidth;
   }

   public float getHeight() {
      return this.mHeight;
   }

   public Camera getCamera() {
      return this.mCamera;
   }

   public Vector[] getObjects() {
      return this.mObjects;
   }

   public Vector getObjects(int type) {
      return type == -1 ? null : this.mObjects[type];
   }

   public Quadtree getObjectsTree() {
      return this.mObjectsTree;
   }

   public Pathfinder getPathfinder() {
      return this.mPathfinder;
   }

   public int getDrawID() {
      return this.mDrawID;
   }

   public int getVisibleID() {
      return this.mVisibleID;
   }

   public short getHiddenID() {
      return this.mHiddenID;
   }

   public int getAlertedID() {
      return this.mAlertedID;
   }

   private void drawDeferredRecursive(IsometricObject unusedObject) {
      for (int i = 0; i < this.mDeferredObjects.size(); i++) {
         IsometricObject object = (IsometricObject)this.mDeferredObjects.elementAt(i);
         if (object.getDrawId() != this.mDrawID) {
            IsometricObject deferrer = object.getDeferringObject();
            if (deferrer.getDrawId() == this.mDrawID) {
               object.doDraw(this.mCamera);
               object.setDrawId(this.mDrawID);
               if (object.isDeferring()) {
                  this.drawDeferredRecursive(object);
               }
            }
         }
      }
   }

   public IsometricObject createObject(int type, Item item) {
      IsometricObject object = this.mPool.pop(type, item);
      if (object != null) {
         return object;
      }

      return switch (type) {
         case 0 -> new ConstructionObject(this);
         case 1 -> new PlayerCharacter(this);
         case 2 -> new ZombieCharacter(this);
         case 3 -> new LootObject(this);
         case 4 -> new DecorationObject(this);
         case 5 -> new FenceObject(this);
         case 6 -> new FarmingObject(this);
         case 7 -> new NeighborCharacter(this);
         case 8 -> new NPCCharacter(this);
         default -> null;
      };
   }

   private void updateFences() {
      Vector fences = this.mObjects[5];

      for (int i = 0; i < this.mObjects[5].size(); i++) {
         ((FenceObject)fences.elementAt(i)).updateEdges();
      }
   }

   private void updateBreakableObjects() {
      long delta = Time.getTimestampDelta(this.mUpdateBreakbleObjects);
      if (delta >= 86400000L) {
         int days = (int)(delta / 86400000L);
         Vector fences = this.mObjects[5];
         int daily = Math.min(PlayerProfile.getCurrentLevel().getMaxZombie() * 50 / 100, 20);
         int quota = Math.min(days * daily, 50);
         int[] damage = new int[fences.size()];
         int columns = this.getColumns();
         int rows = this.getRows();
         boolean empty = fences.isEmpty();
         if (quota > 0 && !empty && rows * columns > 0) {
            int attempt = 1;

            do {
               int index = ToolkitHelpers.getRandomInt(0, fences.size() - 1);
               FenceObject fence = (FenceObject)fences.elementAt(index);
               FenceItem item = (FenceItem)fence.getItem();
               int health = fence.getHealth();
               if (health - damage[index] > 0 && damage[index] < 2) {
                  int probability = item.getDamageProbability();
                  int random = ToolkitHelpers.getRandomInt(0, 100);
                  if (probability < random) {
                     damage[index]++;
                     quota--;
                  }
               }

               empty = fences.isEmpty();
            } while (quota > 0 && !empty && attempt++ < rows * columns);
         }

         for (int i = 0; i < fences.size(); i++) {
            FenceObject fence = (FenceObject)fences.elementAt(i);
            fence.reduceHealth(damage[i]);
            if (!fence.isAlive()) {
               FenceItem item = (FenceItem)fence.getItem();
               DestructibleItem lootItem = ItemManager.getDestructible(item.getDestrcutibleItem());
               this.createLoot(lootItem, fence.getX(), fence.getY());
            }
         }

         this.mUpdateBreakbleObjects = Time.getTimestamp();
      }
   }

   public void updateNeighbors() {
      if (GameEngineUtils.isAtHome()) {
         this.mObjectsQuery.removeAllElements();

         for (int i = 0; i < this.mObjects[7].size(); i++) {
            NeighborCharacter neighbor = (NeighborCharacter)this.mObjects[7].elementAt(i);
            NeighborProfile profile = neighbor.getProfile();
            if (neighbor.hasActions() || profile.hasCommands()) {
               this.mObjectsQuery.addElement(neighbor);
            }
         }

         WindowSocial window = (WindowSocial)WindowManager.getWindow((byte)18);
         Vector neighbors = window.getNeighbors();

         for (int i = 0; i < neighbors.size(); i++) {
            NeighborProfile profile = (NeighborProfile)neighbors.elementAt(i);
            if (this.mObjectsQuery.size() > 0) {
               return;
            }

            if (!this.mObjectsQuery.contains(profile) && profile.hasCommands()) {
               Vector commands = profile.getCommands();
               IsometricObject target = null;

               for (int j = 0; j < commands.size(); j++) {
                  CommandBase command = (CommandBase)commands.elementAt(j);
                  if (command.getType() == 0) {
                     target = ((ActionCommand)command).updateTarget(this.mPlayer);
                     break;
                  }
               }

               IsometricTile tile = null;
               if (target != null) {
                  tile = IsometricUtils.getTileAsSpiralSearch(target.getTile(), 1, 50, 1, 1, true, true);
               }

               if (tile == null) {
                  tile = IsometricUtils.getTileAsSpiralSearch(this.getTile(25, 27), 2, 50, 1, 1, true, true);
                  if (tile == null) {
                     tile = this.getTileAtCenterOfScreen();
                  }
               }

               NeighborCharacter character;
               if (profile.getType() == 2514) {
                  character = this.createNPC(profile, 17, tile.getX(), tile.getY());
               } else {
                  int activeCharacter = profile.getActiveCharacter();
                  character = this.createNeighbor(profile, activeCharacter, tile.getX(), tile.getY());
               }

               HUD.getInGame().addAcceptHelp(character);
               this.mObjectsQuery.addElement(profile);
            }
         }
      }
   }
}
