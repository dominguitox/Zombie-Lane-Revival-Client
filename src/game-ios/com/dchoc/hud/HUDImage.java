package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;

public class HUDImage extends HUDObject {
   private static final int NO_COLOR_MODIFICATION = -8355712;
   private int mBoxWidth;
   private int mBoxHeight;
   private SpriteObject mSprite;
   private int mImageId;
   private CollisionBox mClipBox;
   private CollisionBox mScaleBox;
   private float mScaleFactor;
   private int mColorModification;
   private int mFrame;
   private boolean mUpdateEnabled;

   public HUDImage() {
      super((byte)4);
      this.mSprite = null;
      this.mImageId = -1;
      this.mFrame = -1;
      this.mUpdateEnabled = true;
      super.setPosition(0, 0);
      this.mBoxWidth = 0;
      this.mBoxHeight = 0;
      this.mScaleFactor = -1.0F;
      this.mColorModification = -8355712;
   }

   public HUDImage(int image) {
      super((byte)4);
      this.mSprite = AnimationsManager.loadShared(image);
      this.mImageId = image;
      this.mFrame = -1;
      this.mUpdateEnabled = true;
      this.setPosition(0, 0);
      this.mBoxWidth = 0;
      this.mBoxHeight = 0;
      this.mScaleFactor = -1.0F;
      this.mColorModification = -8355712;
   }

   public HUDImage(int image, int x, int y) {
      super((byte)4);
      this.mSprite = AnimationsManager.loadShared(image);
      this.mImageId = image;
      this.mFrame = -1;
      this.mUpdateEnabled = true;
      this.setPosition(x, y);
      this.mBoxWidth = 0;
      this.mBoxHeight = 0;
      this.mScaleFactor = -1.0F;
      this.mColorModification = -8355712;
   }

   public HUDImage(int image, CollisionBox box) {
      super((byte)4);
      int x = box.getX();
      int y = box.getY();
      this.mSprite = AnimationsManager.loadShared(image);
      this.mImageId = image;
      this.mFrame = -1;
      this.mUpdateEnabled = true;
      this.setPosition(x, y);
      if (box == null) {
         this.mBoxWidth = 0;
         this.mBoxHeight = 0;
      } else {
         this.mBoxWidth = box.getWidth();
         this.mBoxHeight = box.getHeight();
      }

      this.mScaleFactor = -1.0F;
      this.mColorModification = -8355712;
   }

   public void setImage(int image) {
      this.setImage(image, true);
   }

   public void setImage(int image, boolean load) {
      if (this.mImageId != image) {
         this.freeImages();
      }

      this.mImageId = image;
      if (load) {
         this.loadImages();
      }
   }

   public void setScaleRatio(float ratio) {
      this.mScaleFactor = ratio;
   }

   public float getScaleRatio() {
      return this.mScaleFactor;
   }

   public void setSemitransparent(boolean semitransparent) {
      if (semitransparent) {
         this.setAlpha(0.2F);
      } else {
         this.mColorModification = -8355712;
      }
   }

   public void setAlpha(float alpha) {
      this.mColorModification = (int)(alpha * 255.0F) << 24 | 8421504;
   }

   public void setAnimationFrame(int frame) {
      this.mFrame = frame;
   }

   public void setAnimation(int animation, int loops, boolean reversed) {
      if (this.mSprite != null) {
         this.mSprite.setAnimation(animation, loops, reversed);
      }
   }

   public void enableUpdate(boolean enable) {
      this.mUpdateEnabled = enable;
   }

   public CollisionBox getCollisionBox(int id) {
      if (this.mSprite == null) {
         return null;
      }

      this.mSprite.setAnimationFrame(0);
      return this.mSprite.getCollisionBox(id);
   }

   public void setClipBox(CollisionBox box) {
      this.mClipBox = box;
   }

   public void setScaleBox(CollisionBox box) {
      this.mScaleBox = box;
      if (box != null && this.mSprite != null && this.mSprite.getCollisionBox(0) != null) {
         CollisionBox frame = this.mSprite.getCollisionBox(0);
         if (frame.getWidth() > this.mScaleBox.getWidth() || frame.getHeight() > this.mScaleBox.getHeight()) {
            float scaleWidth = this.mScaleBox.getWidth();
            float frameWidth = frame.getWidth();
            float scaleHeight = this.mScaleBox.getHeight();
            float frameHeight = frame.getHeight();
            this.setScaleRatio(Math.min(scaleWidth / frameWidth, scaleHeight / frameHeight));
         }
      }
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.mSprite != null && this.mUpdateEnabled) {
         this.mSprite.logicUpdate(delta);
      }
   }

   @Override
   public void draw() {
      if (this.mIsVisible && this.mSprite != null) {
         int x = this.getX();
         int y = this.getY();
         byte center = this.mCenterType;
         if (center == 3) {
            y += this.mBoxHeight >> 1;
            x += this.mBoxWidth >> 1;
         } else if (center == 2) {
            y += this.mBoxHeight >> 1;
         } else if (center == 1) {
            x += this.mBoxWidth >> 1;
         }

         if (this.mColorModification != -8355712) {
            OGL.pushParameters();
            OGL.setColorModification(this.mColorModification);
         }

         if (0.0F < this.mScaleFactor) {
            OGL.pushParameters();
            OGL.getParameters().setScale(this.mScaleFactor, this.mScaleFactor);
         }

         int frame = this.mFrame;
         SpriteObject sprite = this.mSprite;
         if (frame == -1) {
            sprite.setElapsedTime(AnimationsManager.getTimer());
         } else if (frame < sprite.getFrameCount()) {
            this.mSprite.setAnimationFrame(this.mFrame);
         }

         int clipX = OGL.getClipX();
         int clipY = OGL.getClipY();
         int clipWidth = OGL.getClipWidth();
         int clipHeight = OGL.getClipHeight();
         boolean visible = true;
         if (this.mClipBox != null) {
            int baseX = x;
            int baseY = y;
            if (this.mParent != null) {
               baseX = this.mParent.getX();
               if (this.mParent != null) {
                  baseY = this.mParent.getY();
               }
            }

            int left = Math.max(clipX, this.mClipBox.getX() + baseX);
            int top = Math.max(clipY, this.mClipBox.getY() + baseY);
            int boxX = this.mClipBox.getX();
            int right = Math.min(clipWidth + clipX, baseX + boxX + this.mClipBox.getWidth());
            int boxY = this.mClipBox.getY();
            int bottom = Math.min(clipHeight + clipY, boxY + baseY + this.mClipBox.getHeight());
            int width = right - left;
            int height = bottom - top;
            if (width >= 1 && height >= 1) {
               OGL.setClip(left, top, width, height);
            } else {
               visible = false;
            }
         }

         if (visible) {
            this.mSprite.draw(x, y);
         }

         if (this.mClipBox != null) {
            OGL.setClip(clipX, clipY, clipWidth, clipHeight);
         }

         if (0.0F < this.mScaleFactor) {
            OGL.popParameters();
         }

         if (this.mColorModification != -8355712) {
            OGL.popParameters();
         }
      }
   }

   @Override
   public void drawDebug() {
      super.drawDebug();
      if (this.mScaleBox != null) {
         OGL.setColorRGB(16711935);
         OGL.drawRect(this.getX(), this.getY(), this.mScaleBox.getWidth(), this.mScaleBox.getHeight());
      }

      if (this.mClipBox != null) {
         OGL.setColorRGB(16776960);
         OGL.drawRect(this.getX(), this.getY(), this.mClipBox.getWidth(), this.mClipBox.getHeight());
      }
   }

   @Override
   public void freeImages() {
      AnimationsManager.unloadShared(this.mImageId, this.mSprite);
      this.mSprite = null;
   }

   public void loadImages() {
      if (this.mSprite == null) {
         this.mSprite = AnimationsManager.loadShared(this.mImageId);
      }
   }

   @Override
   public int getWidth() {
      return this.mSprite == null ? -1 : this.mSprite.getWidth();
   }

   @Override
   public int getHeight() {
      return this.mSprite == null ? -1 : this.mSprite.getHeight();
   }

   public int getPivotX() {
      return this.mSprite == null ? -1 : this.mSprite.getPivotX();
   }

   public int getPivotY() {
      return this.mSprite == null ? -1 : this.mSprite.getPivotY();
   }

   public int getFrameCount() {
      return this.mSprite == null ? -1 : this.mSprite.getFrameCount();
   }

   public boolean isEmpty() {
      return this.mSprite == null;
   }

   @Override
   public void pointerEvent(TouchEvent event) {
   }

   @Override
   public void callback(int id) {
   }

   public void init(int image, int x, int y, CollisionBox box, int frame, boolean load) {
      SpriteObject sprite = null;
      if (load) {
         sprite = AnimationsManager.loadShared(image);
      }

      this.mFrame = frame;
      this.mSprite = sprite;
      this.mImageId = image;
      this.mUpdateEnabled = true;
      this.setPosition(x, y);
      if (box == null) {
         this.mBoxWidth = 0;
         this.mBoxHeight = 0;
      } else {
         this.mBoxWidth = box.getWidth();
         this.mBoxHeight = box.getHeight();
      }

      this.mScaleFactor = -1.0F;
      this.mColorModification = -8355712;
   }
}
