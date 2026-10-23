package com.dchoc.idead.objects;

import com.dchoc.idead.actions.Action;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.characters.ActionCharacter;
import com.dchoc.idead.isometric.Camera;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.items.DestructibleItem;
import com.dchoc.idead.tutorial.TutorialFlow;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.spriteobject.SpriteObject;

public class LootObject extends ObjectWithHealth {
   private static boolean smAnimationLoaded;
   private static SpriteObject smSpeechBubbleAnimation;
   private int mSpawnID;

   public LootObject(IsometricScene scene) {
      super(scene, 3);
      if (!smAnimationLoaded) {
         smSpeechBubbleAnimation = AnimationsManager.loadShared(67309, DeviceWrapper.getGfxProfile());
         smAnimationLoaded = true;
      }
   }

   @Override
   public void init(float x, float y, DestructibleItem item) {
      super.init(x, y, item);
      this.mSpawnID = -1;
   }

   @Override
   public boolean canInteractWith(ActionCharacter actor) {
      DestructibleItem item = (DestructibleItem)this.mItem;
      if (!TutorialFlow.isCompleted() && TutorialFlow.getState() == 0 && item.getSubType() == 1990) {
         return false;
      } else {
         return this.isAlive() ? !item.isIndestructible() : false;
      }
   }

   @Override
   public Action interactWith(ActionCharacter actor) {
      return this.canInteractWith(actor) ? actor.addAction(4, this) : null;
   }

   @Override
   public int logicUpdate(int delta) {
      int result = super.logicUpdate(delta);
      if (result != 0) {
         return result;
      } else if (!this.isAlive() && !this.isFixable()) {
         return this.isAnimationFinished() ? 1 : 0;
      } else {
         return 0;
      }
   }

   @Override
   public boolean doDraw(Camera camera) {
      if (!super.doDraw(camera)) {
         return false;
      }

      float x = this.mToScreen.getX() - camera.getX();
      float y = this.mToScreen.getY() - camera.getY();
      DestructibleItem item = (DestructibleItem)this.mItem;
      if (item.getSubType() == 1990) {
         if (this.isAlerted()) {
            smSpeechBubbleAnimation.draw(x, y);
         }

         if (!TutorialFlow.isCompleted() && TutorialFlow.getState() == 5) {
            TutorialFlow.drawArrow((int)x, (int)y, 3);
            return true;
         }

         if (!TutorialFlow.isCompleted()) {
            return true;
         }

         if (this.isAlerted()) {
            return true;
         }

         if (item.isIndestructible()) {
            return true;
         }

         TutorialFlow.drawArrow((int)x, (int)y - (DeviceWrapper.useHDGraphics() ? 30 : 15), 3);
         return true;
      } else {
         if (item.getSubType() == 49 && !TutorialFlow.isCompleted() && TutorialFlow.getState() == 1) {
            TutorialFlow.drawArrow((int)x, (int)y, 1);
         }

         return true;
      }
   }

   @Override
   public boolean isAlerted() {
      return !TutorialFlow.isCompleted() && TutorialFlow.getState() == 1 && this.isNPC() ? true : super.isAlerted();
   }

   public boolean isNPC() {
      return ((DestructibleItem)this.mItem).isNPC();
   }

   public boolean isFixable() {
      return ((DestructibleItem)this.mItem).isFixable();
   }

   @Override
   public boolean isHiddenable() {
      int id = this.mItem.getID();
      return id == 4056 || id == 5581 || id == 5590;
   }

   public void setSpawnID(int spawnId) {
      this.mSpawnID = spawnId;
   }

   public int getSpawnID() {
      return this.mSpawnID;
   }

   @Override
   public SpriteObject getCollision() {
      SpriteObject[] collisions = this.mItem.getCollisions();
      if (collisions == null) {
         return null;
      } else if (this.mAnimations.getAnimation() < collisions.length) {
         return collisions[this.mAnimations.getAnimation()];
      } else {
         return collisions.length > 0 ? collisions[0] : null;
      }
   }

   @Override
   public boolean isPickupable() {
      return !((DestructibleItem)this.mItem).isIndestructible();
   }

   @Override
   protected boolean showHealthBar() {
      return this.isNPC() ? false : !((DestructibleItem)this.mItem).isIndestructible();
   }

   @Override
   protected boolean showTooltip(boolean tooltip) {
      return this.isNPC() ? false : !((DestructibleItem)this.mItem).isIndestructible();
   }
}
