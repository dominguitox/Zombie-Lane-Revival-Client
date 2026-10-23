package com.dchoc.spriteobject;

import com.dchoc.idead.tracking.ErrorEvent;
import com.dchoc.opengl.OGL;

public class SpriteObject {
   public static int ANIMATION_SPEED_X1 = 1024;
   public static int TIME_ACCURACY = 10;
   public static final int LOOP_ANIMATION_PREFERENCE = -2;
   public static final int LOOP_FOREVER = -1;
   public static final boolean CACHE_GRAPHICS_BUFFERS = false;
   private static int smNestingLevel;
   private Animation mCurrentAnimation;
   private int mCurrentFrameIndex;
   private int mElapsedTime;
   private boolean mForceEachFrame;
   private int mLoopCount;
   private int mLoopCounter;
   private boolean mReversed;
   private int mLoopedCount;
   private int mAnimationSpeed = ANIMATION_SPEED_X1;

   public SpriteObject() {
   }

   public SpriteObject(Animation animation) {
      this.set(animation, -2);
   }

   public SpriteObject(int resourceId, boolean immediate, int profile) {
      this(DavinciUtilities.loadAnimation(resourceId, immediate, profile));
   }

   public SpriteObject clone() {
      SpriteObject copy = new SpriteObject();
      int loops = this.mLoopCount == -2 ? -2 : (this.mLoopCount == -1 ? -1 : 1);
      copy.set(this.mCurrentAnimation, loops);
      return copy;
   }

   public void copyTo(SpriteObject target) {
      target.set(this.mCurrentAnimation, this.mLoopCount);
   }

   public void set(Animation animation, int loops) {
      if (this.mCurrentAnimation != null) {
         this.mCurrentAnimation.freeResources();
      }

      DavinciUtilities.modRefCounters(animation, 1);
      this.mCurrentAnimation = animation;
      this.setAnimation(0, loops, this.mForceEachFrame);
   }

   public void setAnimationCustomScale(float scale) {
      if (this.mCurrentAnimation != null) {
         this.mCurrentAnimation.setCustomScaleValue(scale);
      }
   }

   private void checkHD() {
      if (this.mCurrentAnimation != null) {
         int resourceId = this.mCurrentAnimation.checkHD();
         if (resourceId != -1) {
            try {
               Animation animation = new Animation(resourceId, true, 0);
               DavinciUtilities.modRefCounters(animation, 1);
               int index = this.getCurrentAnimationIndex();
               this.set(animation, this.mLoopCount);
               if (index != 0) {
                  this.setAnimation(index, this.mLoopCount, this.mForceEachFrame);
               }

               DavinciUtilities.modRefCounters(animation, -1);
            } catch (Exception exception) {
               new ErrorEvent().trackDavinciError("SpriteObject.checkHD", exception.getMessage(), Integer.toString(resourceId));
               System.err.println(exception);
            }
         }
      }
   }

   public void setAnimation(int index, int loops, boolean forceEachFrame) {
      this.mLoopCount = loops;
      this.mForceEachFrame = forceEachFrame;
      this.setAnimationFrame(this.mReversed ? this.getFrameCount() - 1 : 0);
   }

   public void setAnimationFrame(int index) {
      this.mCurrentFrameIndex = index;
      this.mLoopedCount = 0;
      this.mLoopCounter = this.mLoopCount == -2 ? (this.isLoopingPreferenced() ? -1 : 1) : this.mLoopCount;
      this.mElapsedTime = this.mReversed ? (this.mCurrentAnimation.getFrameDuration(index) << TIME_ACCURACY) - 1 : 0;
   }

   public void setElapsedTime(int time) {
      if (time >= 0) {
         int duration = this.getAnimationLength();
         boolean looping = this.isLooping();
         if (duration == 0) {
            duration = 1;
         }

         int delta = looping ? time % duration : Math.min(time, duration);
         this.setAnimationFrame(0);
         boolean forceEachFrame = this.mForceEachFrame;
         int speed = this.mAnimationSpeed;
         this.mForceEachFrame = false;
         this.mAnimationSpeed = ANIMATION_SPEED_X1;
         this.logicUpdate(delta);
         this.mAnimationSpeed = speed;
         this.mForceEachFrame = forceEachFrame;
         if (looping) {
            this.mLoopedCount = time / duration;
         }
      }
   }

   public void logicUpdate(int delta) {
      if (this.mLoopCounter != 0) {
         if (delta > 0) {
            delta *= this.mAnimationSpeed;
            if (delta == 0 && this.mAnimationSpeed > 0) {
               delta = 1;
            }
         }

         if (this.mCurrentAnimation != null) {
            if (!this.mReversed) {
               int duration = this.mCurrentAnimation.getFrameDuration(this.mCurrentFrameIndex);
               this.mElapsedTime += delta;
               if (this.mElapsedTime < 0) {
                  this.mElapsedTime += Integer.MAX_VALUE;
               }

               for (; duration <= this.mElapsedTime >> TIME_ACCURACY; duration = this.mCurrentAnimation.getFrameDuration(this.mCurrentFrameIndex)) {
                  int fixedDuration = duration << TIME_ACCURACY;
                  this.mElapsedTime -= fixedDuration;
                  if (this.mForceEachFrame) {
                     this.mElapsedTime = 0;
                  }

                  int count = this.mCurrentAnimation.getFrameCount();
                  this.mCurrentFrameIndex++;
                  if (this.mCurrentFrameIndex >= count) {
                     if (this.mLoopCounter > 0) {
                        this.mLoopCounter--;
                     }

                     if (this.mLoopCounter == 0) {
                        this.mCurrentFrameIndex = count - 1;
                        this.mElapsedTime = fixedDuration - 1;
                        return;
                     }

                     this.mCurrentFrameIndex = 0;
                     this.mLoopedCount++;
                  }
               }
            } else {
               this.mElapsedTime -= delta;

               while (this.mElapsedTime < 0) {
                  this.mCurrentFrameIndex--;
                  if (this.mCurrentFrameIndex == -1) {
                     if (this.mLoopCounter > 0) {
                        this.mLoopCounter--;
                     }

                     if (this.mLoopCounter == 0) {
                        this.mCurrentFrameIndex = 0;
                        this.mElapsedTime = 0;
                        return;
                     }

                     this.mCurrentFrameIndex = this.mCurrentAnimation.getFrameCount() - 1;
                     this.mLoopedCount++;
                  }

                  int duration = this.mCurrentAnimation.getFrameDuration(this.mCurrentFrameIndex);
                  if (duration != Integer.MAX_VALUE) {
                     duration <<= TIME_ACCURACY;
                  }

                  this.mElapsedTime += duration;
                  if (this.mForceEachFrame) {
                     this.mElapsedTime = duration - 1;
                  }
               }
            }
         }
      }
   }

   public void draw(int x, int y) {
      this.draw(x, y, 0);
   }

   public void draw(float x, float y) {
      this.draw(x, y, 0);
   }

   public void draw(float x, float y, int flags) {
      this.draw(x, y, flags, false);
   }

   public void draw(float x, float y, int flags, boolean nested) {
      if (nested) {
         if (smNestingLevel > 24) {
            return;
         }

         smNestingLevel++;
      } else {
         smNestingLevel = 0;
      }

      this.checkHD();
      int time = this.getElapsedTime();
      boolean looping = this.isLooping();
      boolean timeline = this.hasAnimationTimeline();
      Animation animation = this.mCurrentAnimation;
      if (animation == null) {
         endNesting(nested);
      } else {
         int modification = timeline ? animation.getTimelineValue(0, time, looping) : -8355712;
         int alpha = modification >>> 24;
         if (timeline && alpha == 0) {
            endNesting(nested);
         } else {
            if (animation.isCustomScale()) {
               float scaleX = OGL.getScaleX();
               float scaleY = OGL.getScaleY();
               float customScale = animation.getCustomScaleValue();
               if (OGL.pushParameters() == null) {
                  endNesting(nested);
                  return;
               }

               OGL.setScale(scaleX * customScale, scaleY * customScale);
            }

            if (timeline) {
               if (OGL.pushParameters() == null) {
                  if (this.mCurrentAnimation.isCustomScale()) {
                     OGL.popParameters();
                  }

                  endNesting(nested);
                  return;
               }

               x += this.getTimelineX();
               y += this.getTimelineY();
               int angle = animation.getTimelineValue(5, time, looping);
               int scaleX = animation.getTimelineValue(3, time, looping);
               int scaleY = animation.getTimelineValue(4, time, looping);
               int combinedAlpha = Math.max(alpha + (OGL.getParameters().getColorModification() >>> 24), 255);
               OGL.setPivot(x, y);
               OGL.setAngleInt(angle);
               OGL.setScaleInt(scaleX, scaleY);
               OGL.setColorModification(modification & 16777215 | combinedAlpha << 24);
               animation.doDraw(x, y, flags, time);
               OGL.popParameters();
            } else {
               animation.doDraw(x, y, flags, time);
            }

            if (this.mCurrentAnimation.isCustomScale()) {
               OGL.popParameters();
            }

            endNesting(nested);
         }
      }
   }

   private static void endNesting(boolean nested) {
      if (nested) {
         smNestingLevel--;
      }
   }

   public void drawAligned(int x, int y, int width, int height) {
      if (this.mCurrentAnimation != null) {
         int alignment = this.mCurrentAnimation.getAlignment();
         if ((alignment & 8) != 0) {
            y += height;
         } else if ((alignment & 4) != 0) {
            y += height >> 1;
         }

         if ((alignment & 32) != 0) {
            x += width;
         } else if ((alignment & 16) != 0) {
            x += width >> 1;
         }
      }

      this.draw(x, y);
   }

   public void freeResources() {
      if (this.mCurrentAnimation != null) {
         this.mCurrentAnimation.freeResources();
         DavinciUtilities.releaseUnreferencedImages();
      }

      this.mCurrentAnimation = null;
   }

   public int getAnimationCount() {
      return this.mCurrentAnimation == null ? 0 : 1;
   }

   public int getAnimationLength() {
      return this.mCurrentAnimation == null ? 0 : this.mCurrentAnimation.getDuration();
   }

   public Animation getCurrentAnimationData() {
      return this.mCurrentAnimation;
   }

   public int getCurrentAnimationResourceID() {
      return this.mCurrentAnimation == null ? -1 : this.mCurrentAnimation.getResourceID();
   }

   public AnimationFrame getCurrentAnimationFrame(int index) {
      return this.mCurrentAnimation != null && index + 1 <= this.mCurrentAnimation.getFrameCount() ? this.mCurrentAnimation.getFrame(index) : null;
   }

   public int[][] getRenderables() {
      AnimationFrame frame = this.getCurrentAnimationFrame(this.mCurrentFrameIndex);
      return frame == null ? null : frame.getRenderables();
   }

   public int[] getColors() {
      AnimationFrame frame = this.getCurrentAnimationFrame(this.mCurrentFrameIndex);
      return frame == null ? null : frame.getColors();
   }

   public float[][] getPolygons() {
      AnimationFrame frame = this.getCurrentAnimationFrame(this.mCurrentFrameIndex);
      return frame == null ? null : frame.getPolygons();
   }

   public float[] getCollisionPolygon(int index) {
      AnimationFrame frame = this.getCurrentAnimationFrame(index);
      return frame == null ? null : frame.getCollisionPolygon();
   }

   public CollisionBox[] getCollisionBoxes() {
      AnimationFrame frame = this.getCurrentAnimationFrame(this.mCurrentFrameIndex);
      return frame == null ? null : frame.getCollisionBoxes();
   }

   public CollisionBox getCollisionBox(int id) {
      AnimationFrame frame = this.getCurrentAnimationFrame(this.mCurrentFrameIndex);
      return frame == null ? null : frame.getCollisionBox(id);
   }

   public int getElapsedTime() {
      return this.mCurrentAnimation == null ? 0 : this.mCurrentAnimation.getFrameStartTime(this.mCurrentFrameIndex) + (this.mElapsedTime >> TIME_ACCURACY);
   }

   public int getCurrentAnimationIndex() {
      return 0;
   }

   public int getCurrentFrameIndex() {
      return this.mCurrentFrameIndex;
   }

   public int getFrameCount() {
      return this.mCurrentAnimation == null ? 0 : this.mCurrentAnimation.getFrameCount();
   }

   public int getCurrentFrameAlpha() {
      return this.getCurrentFrameColorModification() >>> 24;
   }

   public int getCurrentFrameColorModification() {
      return this.hasAnimationTimeline() ? this.mCurrentAnimation.getTimelineValue(0, this.getElapsedTime(), this.isLooping()) : -8355712;
   }

   public boolean hasAnimationTimeline() {
      return this.mCurrentAnimation != null && this.mCurrentAnimation.hasTimeline();
   }

   public int getTimelineX() {
      return this.hasAnimationTimeline() ? this.mCurrentAnimation.getTimelineValue(1, this.getElapsedTime(), this.isLooping()) : 0;
   }

   public int getTimelineY() {
      return this.hasAnimationTimeline() ? this.mCurrentAnimation.getTimelineValue(2, this.getElapsedTime(), this.isLooping()) : 0;
   }

   public int getPivotX() {
      return this.mCurrentAnimation == null ? 0 : this.mCurrentAnimation.getX();
   }

   public int getPivotY() {
      return this.mCurrentAnimation == null ? 0 : this.mCurrentAnimation.getY();
   }

   public int getWidth() {
      return this.mCurrentAnimation == null ? 0 : this.mCurrentAnimation.getWidth();
   }

   public int getHeight() {
      return this.mCurrentAnimation == null ? 0 : this.mCurrentAnimation.getHeight();
   }

   public int getFrameWidth(int index) {
      AnimationFrame frame = this.getCurrentAnimationFrame(index);
      return frame == null ? 0 : frame.getWidth();
   }

   public int getFrameHeight(int index) {
      AnimationFrame frame = this.getCurrentAnimationFrame(index);
      return frame == null ? 0 : frame.getHeight();
   }

   public int getFrameWidth() {
      return this.mCurrentFrameIndex < 0 ? 0 : this.getFrameWidth(this.mCurrentFrameIndex);
   }

   public int getFrameHeight() {
      return this.mCurrentFrameIndex < 0 ? 0 : this.getFrameHeight(this.mCurrentFrameIndex);
   }

   public boolean isFinishedAnimation() {
      return this.mLoopCounter == 0;
   }

   public boolean isLooping() {
      return this.mLoopCounter == -1 || this.mLoopCounter > 1;
   }

   public boolean isLoopingPreferenced() {
      return this.mCurrentAnimation != null && this.mCurrentAnimation.isLoopingPreferenced();
   }

   public void setAnimationSpeed(int speed) {
      this.mAnimationSpeed = speed;
   }

   public void setReversedPlayback(boolean reversed) {
      this.mReversed = reversed;
   }

   public void setLooping(int loops) {
      int time = this.getElapsedTime();
      this.setAnimation(this.getCurrentAnimationIndex(), loops, this.mForceEachFrame);
      this.setElapsedTime(time);
   }

   public int getLoopedCount() {
      return this.mLoopedCount;
   }

   public int[] getBundleDependencies() {
      return this.mCurrentAnimation.getBundleDependencies();
   }
}
