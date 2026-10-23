package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.characters.NeighborCharacter;
import com.dchoc.idead.isometric.Camera;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tracking.ButtonPressedEvent;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.spriteobject.CollisionBox;

public class HUDAccept extends HUDObject {
   protected NeighborCharacter mNeighbor;
   protected HUDButton mButtonAccept;
   protected HUDButton mButtonCancel;
   protected ButtonPressedEvent mTrackAccept;
   protected ButtonPressedEvent mTrackCancel;
   protected HUDImage mBackground;
   protected HUDAutoTextField mTitle;
   protected HUDAutoTextField mDescription;
   protected HUDCollection mCollection;
   protected HUDImage mNetworkIcon;
   protected HUDImage mPortrait;
   protected HUDImage mArrow;
   protected SlotBackground mSlotBackground;
   protected float mScreenX;
   protected float mScreenY;
   protected int mTargetX;
   protected int mTargetY;
   protected int mMargin;

   public HUDAccept() {
      super((byte)-1);
   }

   public void init() {
      this.setPosition(0, 0);
      HUDCollection collection = new HUDCollection();
      this.mCollection = collection;
      collection.setParent(this);
      this.mCollection.addObject(this.mBackground);
      SlotBackground slot = new SlotBackground(this.mBackground.getCollisionBox(0));
      this.mSlotBackground = slot;
      this.mCollection.addObject(slot);
      HUDImage arrow = new HUDImage(74841, this.mBackground.getCollisionBox(11));
      this.mArrow = arrow;
      this.mCollection.addObject(arrow);
      CollisionBox box = this.mBackground.getCollisionBox(1);
      HUDImage portrait = new HUDImage(-1, box.getX(), box.getY());
      this.mPortrait = portrait;
      this.mCollection.addObject(portrait);
      box = this.mBackground.getCollisionBox(6);
      HUDImage icon = new HUDImage(-1, box.getX(), box.getY());
      this.mNetworkIcon = icon;
      this.mCollection.addObject(icon);
      this.mTitle.setCentered(true, true);
      this.mTitle.setScaleToFit(true);
      this.mTitle.searchScale(true);
      this.mTitle.setSplitUsingWidth(true);
      this.mCollection.addObject(this.mTitle);
      this.mCollection.addObject(this.mDescription);
      box = this.mBackground.getCollisionBox(4);
      HUDButton accept = new HUDButton(0, box.getX(), box.getY(), 74709);
      this.mButtonAccept = accept;
      this.mCollection.addObject(accept);
      box = this.mBackground.getCollisionBox(5);
      HUDButton cancel = new HUDButton(1, box.getX(), box.getY(), 71017);
      this.mButtonCancel = cancel;
      this.mCollection.addObject(cancel);
      this.mNeighbor = null;
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         this.mCollection.draw();
      }
   }

   public void setNeighbor(NeighborCharacter neighbor) {
      if (neighbor == null) {
         this.mNeighbor = null;
      } else {
         this.mNeighbor = neighbor;
         NeighborProfile profile = neighbor.getProfile();
         HUDAutoTextField title = this.mTitle;
         title.setText(profile.getName(), DCiDead.getFont(2));
         if (profile.getType() == 2514) {
            this.mPortrait.setImage(74677);
            this.mNetworkIcon.setVisible(false);
         } else {
            this.mPortrait.setImage(78491);
            this.mNetworkIcon.setVisible(true);
            int image;
            if (profile.getFacebookID() == null) {
               image = profile.getGameCenterID() == null ? 77777 : 77783;
            } else {
               image = 74679;
            }

            this.mNetworkIcon.setImage(image);
         }

         this.mMargin = 0;
         this.mScreenX = -1.0F;
         this.mScreenY = -1.0F;
         this.updatePosition(false);
         this.updateMargin();
      }
   }

   public NeighborCharacter getNeighbor() {
      return this.mNeighbor;
   }

   @Override
   public void logicUpdate(int delta) {
      this.mCollection.logicUpdate(delta);
      if (this.mNeighbor != null) {
         this.updatePosition(true);
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible()) {
         if (event.getType() != 1) {
            this.mCollection.pointerEvent(event);
         }
      }
   }

   @Override
   public void callback(int id) {
   }

   public void updatePosition(boolean withMargin) {
      IsometricScene scene = IsometricUtils.getScene();
      Camera camera = scene.getCamera();
      float scale = scene.getScale();
      float x = this.mNeighbor.getScreenX();
      float cameraX = camera.getX();
      float y = this.mNeighbor.getScreenY();
      float cameraY = camera.getY();
      float screenX = scale * (x - cameraX);
      float screenY = scale * (y - cameraY);
      if (screenX != this.mScreenX || screenY != this.mScreenY) {
         this.mScreenX = screenX;
         this.mScreenY = screenY;
         this.updatePositionWithTarget(withMargin);
      }
   }

   public void updatePositionWithTarget(boolean withMargin) {
      CollisionBox bubble = this.mBackground.getCollisionBox(10);
      int bubbleY = bubble.getY();
      int bubbleWidth = bubble.getWidth();
      int bubbleHeight = bubble.getHeight();
      CollisionBox arrow = this.mBackground.getCollisionBox(11);
      int arrowY = arrow.getY();
      int arrowWidth = arrow.getWidth();
      int arrowHeight = arrow.getHeight();
      int[] position = IsometricUtils.getPositionTypeXY(this.mNeighbor, -1, bubbleWidth, bubbleHeight, arrowWidth, arrowHeight, false, true, 0);
      if (position != null) {
         this.mTargetX = position[0];
         this.mTargetY = position[1];
         int x = this.mTargetX - (arrow.getX() + (arrow.getWidth() >> 1));
         this.mTargetX = x;
         this.mTargetY += bubbleY + bubbleHeight - arrowY - arrowHeight;
         if (withMargin) {
            this.mTargetX = x + this.mMargin;
         }

         this.setPosition(this.mTargetX, this.mTargetY);
      }
   }

   public void updateMargin() {
      CollisionBox bubble = this.mBackground.getCollisionBox(10);
      IsometricScene scene = IsometricUtils.getScene();
      Camera camera = scene.getCamera();
      float scale = scene.getScale();
      boolean hd = DeviceWrapper.useHDIngameGraphics();
      float width = bubble.getWidth();
      float limit = hd ? 4490.0F : 2245.0F;
      float right = limit - width;
      float bound = scale * (right - camera.getX());
      int x = this.mTargetX;
      int margin;
      if (x <= bound) {
         margin = this.mMargin;
      } else {
         margin = (int)(bound - x);
         this.mMargin = margin;
      }

      this.setPosition(x + margin, this.mTargetY);
      CollisionBox arrowBox = this.mBackground.getCollisionBox(11);
      this.mArrow.setPosition(arrowBox);
      if (this.mMargin != 0) {
         float screenX = this.mScreenX;
         int myX = this.getX();
         int arrowWidth = arrowBox.getWidth();
         CollisionBox slot = this.mBackground.getCollisionBox(0);
         int slotX = slot.getX();
         int slotWidth = slot.getWidth();
         arrowBox = this.mBackground.getCollisionBox(11);
         int arrowWidth2 = arrowBox.getWidth();
         int arrowX = (int)screenX - myX - (arrowWidth >> 1);
         if (slotWidth + slotX < arrowWidth2 + arrowX) {
            arrowX = slotWidth + slotX - arrowBox.getWidth();
         }

         this.mArrow.setPositionX(arrowX);
      }
   }
}
