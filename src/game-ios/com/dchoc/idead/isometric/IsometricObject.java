package com.dchoc.idead.isometric;

import com.dchoc.collection.QuadtreeNode;
import com.dchoc.collection.QuadtreeObject;
import com.dchoc.hud.HUD;
import com.dchoc.hud.HealthBar;
import com.dchoc.idead.Transform;
import com.dchoc.idead.animation.Animations;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.animation.CharacterAnimations;
import com.dchoc.idead.animation.ConstructionAnimations;
import com.dchoc.idead.animation.PlayerAnimations;
import com.dchoc.idead.animation.ZombieAnimations;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.objects.ConstructionObject;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.math.Rect2;
import com.dchoc.math.Vec2;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.Animation;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;

public class IsometricObject implements QuadtreeObject {
   public static final int HIGHLIGHT_STATE_END_COUNT = 1;
   public static final int HIGHLIGHT_STATE_NONE = -1;
   public static final int HIGHLIGHT_STATE_START_COUNT = 0;
   public static final int ID_INVALID = -1;
   public static final int TYPES_COUNT = 9;
   public static final int TYPE_CONSTRUCTION = 0;
   public static final int TYPE_DECORATION = 4;
   public static final int TYPE_FARMING = 6;
   public static final int TYPE_FENCE = 5;
   public static final int TYPE_INVALID = -1;
   public static final int TYPE_LOOT = 3;
   public static final int TYPE_NEIGHBOR = 7;
   public static final int TYPE_PLAYER = 1;
   public static final int TYPE_ZOMBIE = 2;
   private static boolean smAnimationLoaded;
   private static SpriteObject smFixAnimation;
   private static SpriteObject smGreenGlowAnimation;
   private static SpriteObject smRedGlowAnimation;
   private static SpriteObject smSpeedBoosterAnimation;
   private short mAlertedId;
   protected Animations mAnimations;
   private short mDrawId;
   protected int mFixTimer;
   private boolean mFixing;
   protected int mGfxProfile = DeviceWrapper.getGfxProfile();
   private boolean mHidden;
   private int mHighlightColor;
   protected int mHighlightState;
   protected int mHighlightTimer;
   protected boolean mHighlighted;
   protected int mID;
   private int mIndex;
   public Item mItem;
   private QuadtreeNode mNode;
   public Vec2 mPosition;
   protected Item mRewardItem;
   protected IsometricScene mScene;
   protected SpriteObject mShadowAnimation;
   protected boolean mSpeedBoosted;
   protected IsometricTile mTile;
   protected Vec2 mToScreen;
   protected int mTouchX;
   protected int mTouchY;
   protected int mType;
   private short mVisibleId;

   public IsometricObject(IsometricScene scene, int type) {
      this.mScene = scene;
      this.mType = type;
      this.mAnimations = this.createAnimations(type);
      this.mPosition = new Vec2();
      this.mToScreen = new Vec2();
      this.mHighlightState = -1;
      if (!smAnimationLoaded) {
         smFixAnimation = AnimationsManager.loadShared(66349, this.mGfxProfile);
         smSpeedBoosterAnimation = AnimationsManager.loadShared(78981, this.mGfxProfile);
         smRedGlowAnimation = AnimationsManager.loadShared(75059);
         smGreenGlowAnimation = AnimationsManager.loadShared(75031);
         smAnimationLoaded = true;
      }
   }

   public void init(float x, float y, Item item) {
      this.mID = -1;
      this.mHighlighted = false;
      this.mHidden = false;
      this.mAlertedId = 0;
      this.mDrawId = 0;
      this.mVisibleId = 0;
      this.mItem = item;
      this.mRewardItem = null;
      this.mFixing = false;
      this.mFixTimer = 0;
      this.mSpeedBoosted = false;
      this.mTile = null;
      this.mAnimations.init(this);
      this.setAnimationShadow(item.getShadowAnimationID());
      this.setPosition(x, y);
   }

   public int logicUpdate(int delta) {
      this.mFixTimer += delta;
      if (this.mFixing && this.mFixTimer > smFixAnimation.getAnimationLength()) {
         this.mFixing = false;
      }

      this.updateAnimations(delta);
      if (this.mHighlightState == 1) {
         this.mHighlightTimer -= delta;
         if (this.mHighlightTimer <= 0) {
            this.changeHighlightState(-1);
         }
      } else if (this.mHighlightState == 0) {
         this.mHighlightTimer -= delta;
         if (this.mHighlightTimer <= 0) {
            this.changeHighlightState(1);
         }
      }

      return 0;
   }

   public boolean isFixing() {
      return this.mFixing;
   }

   public void setFixing(boolean fixing) {
      if (fixing) {
         this.mFixTimer = 0;
      }

      this.mFixing = fixing;
   }

   public boolean doDraw(Camera camera) {
      float screenX = this.mToScreen.getX();
      float cameraX = camera.getX();
      float screenY = this.mToScreen.getY();
      float cameraY = camera.getY();
      if (!this.mScene.getFloor().isObjectVisible(this)) {
         return false;
      }

      float x = screenX - cameraX;
      float y = screenY - cameraY;
      this.mHidden = this.checkIfHidden();
      if (this.mShadowAnimation != null) {
         if (this.isHidden()) {
            OGL.pushParameters();
            OGL.setColorModification(1147166816);
         }

         this.mShadowAnimation.draw(x, y);
         if (this.isHidden()) {
            OGL.popParameters();
         }
      }

      if (this.isHidden()) {
         this.drawGrid(camera, -2139062144);
      } else if (this.mHighlighted && this.mScene.getState() == 2 && this.mScene.getSelectedObject() == this) {
         this.drawGrid(camera, -2139062144);
      }

      if (smSpeedBoosterAnimation != null && this.mSpeedBoosted) {
         smSpeedBoosterAnimation.draw(x, y);
      }

      if (this.isAlerted() && !this.mHighlighted) {
         OGL.pushParameters();
         OGL.setColorModification(this.isHidden() ? -2130771968 : -65536);
         float width = this.mAnimations.getAnimationFrameWidth();
         float height = this.mAnimations.getAnimationFrameHeight();
         float scaleY = (height + 5.0F) / height;
         OGL.setScale((width + 5.0F) / width, scaleY);
         this.mAnimations.doDraw(x, y + height * (scaleY - 1.0F) * 0.5F);
         OGL.popParameters();
      }

      if (this.mHighlighted) {
         OGL.pushParameters();
         OGL.setColorModification(this.getHighlightColor());
      } else if (this.isHidden()) {
         OGL.pushParameters();
         OGL.setColorModification(1147166816);
      }

      this.mAnimations.doDraw(x, y);
      if (this.mHighlighted) {
         OGL.popParameters();
      } else if (this.isHidden()) {
         OGL.popParameters();
      }

      if (this.mFixing) {
         smFixAnimation.setElapsedTime(this.mFixTimer);
         smFixAnimation.draw(x, y);
      }

      return true;
   }

   public void touchEventOccured(TouchEvent event) {
   }

   public void remove() {
      int sizeX = this.getTileSizeX();
      int sizeY = this.getTileSizeY();
      if (this.mTile != null) {
         for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
               IsometricTile tile = this.mScene.getTile(this.mTile.getColumn() + i, this.mTile.getRow() - j);
               if (tile != null) {
                  tile.removeObject(this);
               }
            }
         }
      }

      this.mTile = null;
   }

   public void setPosition(float x, float y) {
      boolean changed = x != this.mPosition.getX() || y != this.mPosition.getY();
      this.mPosition.set(x, y);
      IsometricTile tile = this.mScene.getTileAt(x, y);
      if (tile != this.mTile) {
         this.changeTile(tile);
      }

      this.mToScreen.set(x, y);
      Transform.toScreen(this.mToScreen);
      if (changed) {
         this.mScene.getObjectsTree().update(this);
      }
   }

   public void setHighlighted(boolean highlighted) {
      this.mHighlighted = highlighted;
      this.mHighlightColor = -8355712;
   }

   public void setHighlighted(boolean highlighted, int color) {
      this.mHighlighted = highlighted;
      this.mHighlightColor = color;
   }

   public boolean isHighlighted() {
      return this.mHighlighted;
   }

   public IsometricObject getDeferringObject() {
      return this.mTile != null ? this.mTile.getDeferring(this.mItem.getLayer()) : null;
   }

   public boolean isDeferring() {
      return this.getTileSizeX() > 1 || this.getTileSizeY() > 1;
   }

   public Animations getAnimations() {
      return this.mAnimations;
   }

   public SpriteObject getCollision() {
      SpriteObject[] collisions = this.mItem.getCollisions();
      return collisions != null && collisions.length > 0 ? collisions[0] : null;
   }

   public CollisionBox getCollisionBox() {
      SpriteObject collision = this.getCollision();
      return collision == null ? null : collision.getCollisionBox(1);
   }

   public float[] getCollisionPolygon() {
      SpriteObject collision = this.getCollision();
      return collision == null ? null : collision.getCollisionPolygon(0);
   }

   public int getType() {
      return this.mType;
   }

   public void setID(int id) {
      this.mID = id;
   }

   public int getID() {
      return this.mID;
   }

   public Item getItem() {
      return this.mItem;
   }

   public int getItemType() {
      return this.mItem == null ? 0 : this.mItem.getType();
   }

   public int getItemSubType() {
      return this.mItem == null ? 0 : this.mItem.getSubType();
   }

   public int getItemID() {
      return this.mItem == null ? 0 : this.mItem.getID();
   }

   public void setItem(Item item) {
      this.mItem = item;
   }

   public void setRewardItem(Item item) {
      this.mRewardItem = item;
   }

   public Item getRewardItem() {
      return this.mRewardItem;
   }

   public int getLayer() {
      return this.mItem.getLayer();
   }

   public float getX() {
      return this.mPosition.getX();
   }

   public float getY() {
      return this.mPosition.getY();
   }

   public float getScreenX() {
      return this.mToScreen.getX();
   }

   public float getScreenY() {
      return this.mToScreen.getY();
   }

   public boolean isWalkable() {
      return this.mItem.isWalkable();
   }

   public IsometricTile getTile() {
      return this.mTile;
   }

   public boolean isValid() {
      return this.mItem != null && this.mTile != null;
   }

   public void setDrawId(short drawId) {
      this.mDrawId = drawId;
   }

   public short getDrawId() {
      return this.mDrawId;
   }

   public void setVisibleId(short visibleId) {
      this.mVisibleId = visibleId;
   }

   public boolean isVisible() {
      return this.mVisibleId == (short)this.mScene.getVisibleID();
   }

   public void setHidden(boolean hidden) {
      this.mHidden = hidden;
   }

   public boolean isHidden() {
      return this.mHidden;
   }

   public void setAlertedId(short alertedId) {
      this.mAlertedId = alertedId;
   }

   public boolean isAlerted() {
      return this.mAlertedId == (short)this.mScene.getAlertedID();
   }

   public int getTileSizeX() {
      return this.mItem.getTileSizeX();
   }

   public int getTileSizeY() {
      return this.mItem.getTileSizeY();
   }

   public int getTileSizeZ() {
      return this.mItem.getTileSizeZ();
   }

   @Override
   public float getBoundsX() {
      float x = this.mToScreen.getX();
      return x + this.mItem.getBounds().getX();
   }

   @Override
   public float getBoundsY() {
      float y = this.mToScreen.getY();
      return y + this.mItem.getBounds().getY();
   }

   @Override
   public float getBoundsWidth() {
      return this.mItem.getBounds().getWidth();
   }

   @Override
   public float getBoundsHeight() {
      return this.mItem.getBounds().getHeight();
   }

   @Override
   public void setNode(QuadtreeNode node, int index) {
      this.mNode = node;
      this.mIndex = index;
   }

   @Override
   public QuadtreeNode getNode() {
      return this.mNode;
   }

   @Override
   public int getIndex() {
      return this.mIndex;
   }

   public boolean isFloor() {
      return false;
   }

   public boolean isReleasable() {
      return false;
   }

   public boolean isHiddenable() {
      return false;
   }

   public boolean isPickupable() {
      return true;
   }

   public Animations createAnimations(int type) {
      switch (type) {
         case 0:
            return new ConstructionAnimations();
         case 1:
         case 7:
            return new PlayerAnimations(0);
         case 2:
            return new ZombieAnimations(0);
         case 3:
         case 4:
         case 5:
         case 6:
         default:
            return new Animations();
         case 8:
            return new CharacterAnimations(1);
      }
   }

   public void setAnimationShadow(int animation) {
      if (animation != 0) {
         if (this.mShadowAnimation == null || this.mShadowAnimation.getCurrentAnimationData().getResourceID() != animation) {
            this.mShadowAnimation = AnimationsManager.loadShared(animation, this.mGfxProfile);
            if (this.mShadowAnimation != null) {
               this.mShadowAnimation.setAnimation(0, -1, false);
            }
         }
      }
   }

   protected void changeHighlightState(int state) {
      PlayerCharacter player = this.mScene.getPlayer();
      if (state == 0) {
         this.mHighlightTimer = 200;
         this.mHighlightState = state;
      } else {
         if (state == 1) {
            this.setHighlighted(true);
            this.mHighlightTimer = 1000;
            if (this.showHealthBar()) {
               HealthBar bar = HUD.getHealthBar(this);
               if (bar == null || bar.getTouchType() != 1 && bar.getTouchType() != 2) {
                  HUD.createHealthBar(this, 5, false);
               }
            }
         } else if (state == -1) {
            if (player.hasActionWith(this)) {
               if (this.getType() == 0 && ((ConstructionObject)this).isCompleted()) {
                  this.setHighlighted(false);
               }
            } else {
               HealthBar bar = HUD.getHealthBar(this);
               if (bar == null || bar.getTouchType() != 2) {
                  this.setHighlighted(false);
               }
            }
         }

         this.mHighlightState = state;
      }
   }

   protected void updateAnimations(int delta) {
      if (this.mAnimations != null) {
         this.mAnimations.logicUpdate(delta);
      }

      if (this.mShadowAnimation != null) {
         this.mShadowAnimation.logicUpdate(delta);
      }

      if (smSpeedBoosterAnimation != null && this.mSpeedBoosted) {
         smSpeedBoosterAnimation.logicUpdate(delta);
      }
   }

   protected boolean showHealthBar() {
      return true;
   }

   protected boolean showTooltip(boolean tooltip) {
      return !tooltip;
   }

   protected void changeTile(IsometricTile tile) {
      int sizeX = this.getTileSizeX();
      int sizeY = this.getTileSizeY();
      if (this.mTile != null) {
         for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
               IsometricTile old = this.mScene.getTile(this.mTile.getColumn() + i, this.mTile.getRow() - j);
               if (old != null) {
                  old.removeObject(this);
               }
            }
         }
      }

      if (tile != null) {
         for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
               IsometricTile target = this.mScene.getTile(tile.getColumn() + i, tile.getRow() - j);
               if (target != null) {
                  target.addObject(this);
               }
            }
         }
      }

      if (sizeX > 1 || sizeY >= 2) {
         this.mScene.setUpdateDeferredTiles();
      }

      if (this.mType == 5) {
         this.mScene.setUpdateFences();
      }

      this.mTile = tile;
   }

   public int getHighlightColor() {
      if (this.mHighlightColor == -8355712) {
         return -1151311776;
      }

      SpriteObject glow;
      if (this.mHighlightColor == -1140891552) {
         glow = smRedGlowAnimation;
      } else {
         if (this.mHighlightColor != -1151271072) {
            return this.mHighlightColor;
         }

         glow = smGreenGlowAnimation;
      }

      if (glow != null) {
         Animation data = glow.getCurrentAnimationData();
         if (data != null && data.hasTimeline()) {
            return data.getTimelineValue(0, AnimationsManager.getTimer(), true);
         }
      }

      return this.mHighlightColor;
   }

   public void drawGrid(Camera camera, int color) {
      if (this.mTile != null) {
         float half = IsometricTile.getTilesSizeHalf();
         float lineWidth = IsometricTile.getLineWidth() * this.mScene.getScale();
         float width = lineWidth > 1.0F ? lineWidth : 1.0F;
         int sizeX = this.getTileSizeX();
         int sizeY = this.getTileSizeY();
         float stepXX = Transform.toScreenX(IsometricTile.getTileSize(), 0.0F);
         float stepXY = Transform.toScreenY(IsometricTile.getTileSize(), 0.0F);
         float stepYX = Transform.toScreenX(0.0F, IsometricTile.getTileSize());
         float stepYY = Transform.toScreenY(0.0F, IsometricTile.getTileSize());
         float startX = Transform.toScreenX(this.mPosition.getX() - half, half + this.mPosition.getY()) - camera.getX();
         float startY = Transform.toScreenY(this.mPosition.getX() - half, half + this.mPosition.getY()) - camera.getY();

         for (int i = 0; i < sizeY + 1; i++) {
            OGL.setColorARGB(color);
            float x1 = startX - stepYX * i;
            float y1 = startY - stepYY * i;
            OGL.drawLineEx(x1, y1, sizeX * stepXX + x1, sizeX * stepXY + y1, width);
         }

         for (int i = 0; i < sizeX + 1; i++) {
            OGL.setColorARGB(color);
            float x1 = startX + stepXX * i;
            float y1 = startY + stepXY * i;
            OGL.drawLineEx(x1, y1, x1 - sizeY * stepYX, y1 - sizeY * stepYY, width);
         }
      }
   }

   public boolean checkIfHidden() {
      if (!this.isHiddenable()) {
         return false;
      }

      if (this.getTileSizeZ() < 2) {
         return false;
      }

      IsometricObject target = this.mScene.getPlayer();
      if (HUD.getState() == 9 && this.mScene.getSelectedObject() != null) {
         target = this.mScene.getSelectedObject();
      }

      if (target.getDrawId() != (short)this.mScene.getDrawID()) {
         return false;
      }

      float targetX = target.getBoundsX();
      float targetY = target.getBoundsY();
      float targetWidth = target.getBoundsWidth();
      float targetHeight = target.getBoundsHeight();
      float x = this.getBoundsX();
      float y = this.getBoundsY();
      float width = this.getBoundsWidth();
      float height = this.getBoundsHeight();
      return Rect2.isRectangleIntersecting(x, y, width, height, targetX, targetY, targetWidth, targetHeight);
   }
}
