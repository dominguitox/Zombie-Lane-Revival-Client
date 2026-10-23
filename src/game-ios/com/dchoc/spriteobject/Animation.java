package com.dchoc.spriteobject;

import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.toolkit.IByteArray;
import com.dchoc.toolkit.Toolkit;
import java.io.IOException;

public class Animation implements ServerAssetListener {
   public static final int ALIGNMENT_BOTTOM = 8;
   public static final int ALIGNMENT_HCENTER = 16;
   public static final int ALIGNMENT_LEFT = 2;
   public static final int ALIGNMENT_RIGHT = 32;
   public static final int ALIGNMENT_TOP = 1;
   public static final int ALIGNMENT_VCENTER = 4;
   private static final int[] INT_ARRAY_SIZE_1 = new int[1];
   private static final int[] MAX_DURATION_FOR_FRAME = new int[]{Integer.MAX_VALUE};
   private static AnimationFrame[] smEmptyFrame;
   private int mAlignment;
   private int mAnimationDuration = Integer.MAX_VALUE;
   private int[] mBundleDependencies;
   private boolean mContinuousNestedAnimations;
   private boolean mCustomScale;
   private float mCustomScaleValue = 1.0F;
   private AnimationFrame[] mFrame;
   private int[] mFrameDuration;
   private int[] mFrameReference;
   private int[] mFrameStartTime;
   private int mGfxProfile;
   private int mHDRid = -1;
   private int mHeight;
   private boolean mLoopPreference;
   private int mResourceID;
   private boolean mSwapAnimation;
   private TimelineChannel[] mTimeline;
   private int mTimelineHeight;
   private int mTimelineWidth;
   private int mTimelineX;
   private int mTimelineY;
   private int mWidth;
   private int mX;
   private int mY;

   private Animation() {
   }

   public Animation(int resourceId, boolean unused, int gfxProfile) throws IOException {
      this(resourceId, true, gfxProfile, true);
   }

   public Animation(int resourceId, boolean unused, int gfxProfile, boolean bundleListener) throws IOException {
      this.createAnimation(resourceId, unused, gfxProfile, bundleListener);
   }

   private void createAnimation(int resourceId, boolean unused, int gfxProfile, boolean bundleListener) throws IOException {
      this.mResourceID = resourceId;
      this.mGfxProfile = gfxProfile;
      if (resourceId == -1) {
         this.createEmptyAnimation();
      } else {
         IByteArray chosen = Toolkit.getResourceByteArray(resourceId);
         IByteArray alternate = null;
         int localId = resourceId;
         boolean hdRequested = false;
         int id;
         if (chosen.readByte() == 1) {
            int count = chosen.readInt();
            int hdRid = -1;
            int sdRid = -1;

            for (int i = 0; i < count; i++) {
               int type = chosen.readInt();
               int value = chosen.readInt();
               if (type == 2) {
                  hdRid = value;
               } else if (type == 1) {
                  sdRid = value;
               }
            }

            this.mHDRid = hdRid;
            if (DeviceWrapper.isVGA()) {
               id = sdRid == -1 ? resourceId : sdRid;
            } else if (gfxProfile == 1) {
               id = resourceId;
            } else {
               boolean useHD = DeviceWrapper.useHDGraphics();
               hdRequested = gfxProfile == 2 || useHD;
               id = hdRequested ? hdRid : resourceId;
            }

            if (id == resourceId) {
               alternate = null;
            } else {
               IByteArray alt = Toolkit.getResourceByteArray(id);
               alt.readByte();
               alternate = chosen;
               chosen = alt;
            }
         } else {
            localId = -1;
            id = resourceId;
            alternate = null;
         }

         int bundleId = chosen.readInt();
         if (DavinciUtilities.getBundleState(bundleId) == 0) {
            if (bundleListener) {
               this.addAsBundleListener(bundleId);
            } else {
               ServerAssetLoader.requestBundle(bundleId, null);
            }

            if (alternate != null) {
               alternate.readInt();
               chosen = alternate;
            }

            id = localId;
            if (hdRequested) {
               this.mCustomScale = true;
               this.mCustomScaleValue = 2.0F;
            }
         }

         this.initAnimation(chosen, true, id);
      }
   }

   private void createEmptyAnimation() {
      if (smEmptyFrame == null) {
         smEmptyFrame = new AnimationFrame[1];
         smEmptyFrame[0] = new AnimationFrame(true, this);
      }

      this.mFrame = smEmptyFrame;
      this.mFrameDuration = MAX_DURATION_FOR_FRAME;
      this.mFrameStartTime = INT_ARRAY_SIZE_1;
      this.mFrameReference = INT_ARRAY_SIZE_1;
   }

   private boolean initAnimation(IByteArray data, boolean unused, int id) throws IOException {
      int flags = data.readUnsignedByte();
      int count = data.readUnsignedByte();
      boolean byteCoords = (flags & 1) == 0;
      boolean byteDurations = (flags & 2) == 0;
      this.mFrameDuration = new int[count];
      this.mFrameStartTime = new int[count];
      if (count > 1) {
         this.mFrameReference = new int[count];
      }

      this.mFrame = new AnimationFrame[count];
      this.mLoopPreference = data.readBoolean();
      this.mContinuousNestedAnimations = data.readBoolean();
      if (byteCoords) {
         this.mX = data.readByte();
         this.mY = data.readByte();
         this.mWidth = data.readUnsignedByte();
         this.mHeight = data.readUnsignedByte();
      } else {
         this.mX = data.readShort();
         this.mY = data.readShort();
         this.mWidth = data.readShort();
         this.mHeight = data.readShort();
      }

      this.mAlignment = data.readUnsignedByte();

      for (int i = 0; i < count; i++) {
         int reference = data.readUnsignedByte();
         if (count > 1) {
            this.mFrameReference[i] = reference;
         }

         if (!this.isFrameReference(i)) {
            this.mFrame[i] = new AnimationFrame(false, this);
         }
      }

      boolean hasZeroDuration = false;
      int start = 0;

      for (int i = 0; i < count; i++) {
         int duration = byteDurations ? data.readUnsignedByte() : data.readShort() & 65535;
         if (duration == 0) {
            duration = Integer.MAX_VALUE;
            hasZeroDuration = true;
         }

         this.mFrameDuration[i] = duration;
         this.mFrameStartTime[i] = start;
         start += duration;
      }

      if (count < 1) {
         this.mAnimationDuration = 0;
      } else if (!hasZeroDuration) {
         this.mAnimationDuration = this.mFrameStartTime[count - 1] + this.mFrameDuration[count - 1];
      }

      boolean hasTimeline = data.readBoolean();
      this.mTimelineX = this.mX;
      this.mTimelineY = this.mY;
      this.mTimelineWidth = this.mWidth;
      this.mTimelineHeight = this.mHeight;
      if (hasTimeline) {
         if (byteCoords) {
            this.mTimelineX = data.readByte();
            this.mTimelineY = data.readByte();
            this.mTimelineWidth = data.readUnsignedByte();
            this.mTimelineHeight = data.readUnsignedByte();
         } else {
            this.mTimelineX = data.readShort();
            this.mTimelineY = data.readShort();
            this.mTimelineWidth = data.readShort();
            this.mTimelineHeight = data.readShort();
         }

         this.mTimeline = new TimelineChannel[6];

         for (int type = 0; type < 6; type++) {
            this.mTimeline[type] = new TimelineChannel(type, this.getDuration());
            this.mTimeline[type].load(data, byteCoords);
         }
      }

      boolean ok = true;

      for (int i = 0; i < count; i++) {
         if (!this.isFrameReference(i)) {
            this.mFrame[i].setParent(id);
            if (!this.mFrame[i].loadFrameData(data, byteCoords, true, this.mCustomScaleValue)) {
               ok = false;
            }
         }
      }

      if (count > 1) {
         for (int i = 0; i < count; i++) {
            if (this.isFrameReference(i)) {
               this.mFrame[i] = this.mFrame[this.mFrameReference[i]];
            }
         }
      }

      return ok;
   }

   public void swapToHD(Animation source) {
      this.mFrameDuration = source.mFrameDuration;
      this.mFrameStartTime = source.mFrameStartTime;
      this.mFrameReference = source.mFrameReference;
      this.mAnimationDuration = source.mAnimationDuration;
      this.mLoopPreference = source.mLoopPreference;
      this.mContinuousNestedAnimations = source.mContinuousNestedAnimations;
      this.mX = source.mX;
      this.mY = source.mY;
      this.mWidth = source.mWidth;
      this.mHeight = source.mHeight;
      this.mTimelineX = source.mTimelineX;
      this.mTimelineY = source.mTimelineY;
      this.mTimelineWidth = source.mTimelineWidth;
      this.mTimelineHeight = source.mTimelineHeight;
      this.mAlignment = source.mAlignment;
      this.mCustomScale = source.mCustomScale;
      this.mCustomScaleValue = source.mCustomScaleValue;
      this.mResourceID = source.mResourceID;
      this.mTimeline = source.mTimeline;
      int count = source.mFrame.length;
      this.mFrame = new AnimationFrame[count];

      for (int i = 0; i < count; i++) {
         if (!this.isFrameReference(i)) {
            this.mFrame[i] = source.mFrame[i].clone(this);
         }
      }

      if (count > 1) {
         for (int i = 0; i < count; i++) {
            if (this.isFrameReference(i)) {
               this.mFrame[i] = this.mFrame[this.mFrameReference[i]];
            }
         }
      }

      this.mHDRid = -1;
   }

   public void addAsBundleListener(int bundleId) {
      if (this.mBundleDependencies == null) {
         this.mBundleDependencies = new int[]{-1};
      }

      int slot = -1;

      for (int i = 0; i < this.mBundleDependencies.length; i++) {
         if (this.mBundleDependencies[i] == -1) {
            slot = i;
            break;
         }
      }

      if (slot >= 0) {
         this.mBundleDependencies[slot] = bundleId;
      } else {
         int[] grown = new int[this.mBundleDependencies.length * 2];
         System.arraycopy(this.mBundleDependencies, 0, grown, 0, this.mBundleDependencies.length);
         grown[this.mBundleDependencies.length] = bundleId;

         for (int i = this.mBundleDependencies.length + 1; i < grown.length; i++) {
            grown[i] = -1;
         }

         this.mBundleDependencies = grown;
      }

      ServerAssetLoader.requestBundle(bundleId, this);
   }

   public void addAsBundleListener(int[] bundleIds) {
      if (bundleIds != null) {
         for (int i = 0; i < bundleIds.length; i++) {
            if (bundleIds[i] != -1) {
               this.addAsBundleListener(bundleIds[i]);
            }
         }
      }
   }

   @Override
   public void assetBundleLoaded(int bundleId, boolean loaded) {
      if (loaded) {
         boolean allLoaded = true;
         if (this.mBundleDependencies != null) {
            for (int i = 0; i < this.mBundleDependencies.length; i++) {
               if (this.mBundleDependencies[i] == bundleId) {
                  this.mBundleDependencies[i] = -1;
               } else if (this.mBundleDependencies[i] != -1) {
                  allLoaded = false;
               }
            }

            if (!allLoaded) {
               return;
            }
         }

         this.mSwapAnimation = true;
      }
   }

   public int checkHD() {
      return this.mSwapAnimation ? this.mHDRid : -1;
   }

   public Animation clone() {
      Animation copy = new Animation();
      copy.mFrameDuration = this.mFrameDuration;
      copy.mFrameStartTime = this.mFrameStartTime;
      copy.mFrameReference = this.mFrameReference;
      copy.mAnimationDuration = this.mAnimationDuration;
      copy.mLoopPreference = this.mLoopPreference;
      copy.mContinuousNestedAnimations = this.mContinuousNestedAnimations;
      copy.mX = this.mX;
      copy.mY = this.mY;
      copy.mWidth = this.mWidth;
      copy.mHeight = this.mHeight;
      copy.mAlignment = this.mAlignment;
      copy.mCustomScale = this.mCustomScale;
      copy.mCustomScaleValue = this.mCustomScaleValue;
      copy.mHDRid = this.mHDRid;
      copy.mSwapAnimation = this.mSwapAnimation;
      copy.mResourceID = this.mResourceID;
      copy.mTimelineX = this.mTimelineX;
      copy.mTimelineY = this.mTimelineY;
      copy.mTimelineWidth = this.mTimelineWidth;
      copy.mTimelineHeight = this.mTimelineHeight;
      copy.mTimeline = this.mTimeline;
      int count = this.mFrame == null ? 0 : this.mFrame.length;
      copy.mFrame = new AnimationFrame[count];

      for (int i = 0; i < count; i++) {
         if (!this.isFrameReference(i)) {
            copy.mFrame[i] = this.mFrame[i].clone(copy);
         }
      }

      if (count > 1) {
         for (int i = 0; i < count; i++) {
            if (this.isFrameReference(i)) {
               copy.mFrame[i] = copy.mFrame[this.mFrameReference[i]];
            }
         }
      }

      if (this.mBundleDependencies != null) {
         copy.mBundleDependencies = new int[this.mBundleDependencies.length];

         for (int i = 0; i < this.mBundleDependencies.length; i++) {
            copy.mBundleDependencies[i] = this.mBundleDependencies[i];
         }
      }

      return copy;
   }

   public boolean continuousNestedAnimations() {
      return this.mContinuousNestedAnimations;
   }

   public void doDraw(float x, float y, int flags, int elapsedTime) {
      int duration = this.getDuration();
      int time = elapsedTime;
      if (duration <= elapsedTime) {
         time = elapsedTime % duration;
      }

      int frameIndex = this.getFrameInTime(time);
      int frameTime = elapsedTime;
      if (!this.continuousNestedAnimations()) {
         frameTime = time - this.getFrameStartTime(frameIndex);
      }

      AnimationFrame frame = this.getFrame(frameIndex);
      if (frame != null) {
         frame.doDraw(x, y, flags, frameTime);
      }
   }

   public void freeResources() {
      DavinciUtilities.modRefCounters(this, -1);
   }

   public int getAlignment() {
      return this.mAlignment;
   }

   public int[] getBundleDependencies() {
      return this.mBundleDependencies;
   }

   public float getCustomScaleValue() {
      return this.mCustomScaleValue;
   }

   public int getDuration() {
      return this.mAnimationDuration;
   }

   public AnimationFrame getFrame(int index) {
      return this.mFrame != null && index >= 0 && index < this.mFrame.length ? this.mFrame[index] : null;
   }

   public int getFrameColorModification(int frame) {
      return this.hasTimeline() ? this.getTimelineValue(0, this.getFrameStartTime(frame), this.isLoopingPreferenced()) : -8355712;
   }

   public int getFrameCount() {
      return this.mFrame == null ? 0 : this.mFrame.length;
   }

   public int getFrameDuration(int index) {
      return index >= 0 && index < this.mFrameDuration.length ? this.mFrameDuration[index] : 0;
   }

   public int getFrameInTime(int time) {
      int index = this.mFrameDuration.length;

      while (--index >= 0) {
         if (time >= this.mFrameStartTime[index]) {
            return index;
         }
      }

      return -1;
   }

   public int getFrameStartTime(int index) {
      return index >= 0 && index < this.mFrameStartTime.length ? this.mFrameStartTime[index] : 0;
   }

   public int getGfxProfile() {
      return this.mGfxProfile;
   }

   public int getHeight() {
      return (int)(this.mTimelineHeight * this.mCustomScaleValue);
   }

   public int getResourceID() {
      return this.mResourceID;
   }

   public int getTimelineValue(int channel, int time, boolean loop) {
      int duration = this.getDuration();
      int t = time;
      if (duration <= time) {
         if (loop) {
            t = time % duration;
         } else {
            t = duration - 1;
         }
      }

      int value = this.mTimeline[channel].getValue(t, loop);
      return !this.mCustomScale || channel != 3 && channel != 4 && channel != 1 && channel != 2 ? value : (int)(value * this.mCustomScaleValue);
   }

   public int getWidth() {
      return (int)(this.mTimelineWidth * this.mCustomScaleValue);
   }

   public int getWithoutTimelineHeight() {
      return this.mHeight;
   }

   public int getWithoutTimelineWidth() {
      return this.mWidth;
   }

   public int getWithoutTimelineX() {
      return this.mX;
   }

   public int getWithoutTimelineY() {
      return this.mY;
   }

   public int getX() {
      return (int)(this.mTimelineX * this.mCustomScaleValue);
   }

   public int getY() {
      return (int)(this.mTimelineY * this.mCustomScaleValue);
   }

   public boolean hasNestedAnimations(int frame) {
      return this.getFrame(frame).hasNestedAnimations();
   }

   public boolean hasTimeline() {
      if (this.mTimeline == null) {
         return false;
      }

      for (int i = this.mTimeline.length - 1; i >= 0; i--) {
         if (this.mTimeline[i].getEventCount() >= 1) {
            return true;
         }
      }

      return false;
   }

   public boolean isCustomScale() {
      return this.mCustomScale;
   }

   public boolean isFrameReference(int index) {
      return this.getFrameCount() >= 2 && this.mFrameReference[index] != 255;
   }

   public boolean isLoopingPreferenced() {
      return this.mLoopPreference;
   }

   public void setCustomScaleValue(float value) {
      this.mCustomScale = value != 1.0F;
      this.mCustomScaleValue = value;
   }
}
