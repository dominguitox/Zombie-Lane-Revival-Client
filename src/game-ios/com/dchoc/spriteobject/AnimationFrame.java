package com.dchoc.spriteobject;

import com.dchoc.hud.WindowManager;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.opengl.OGL;
import com.dchoc.opengl.OGLRegion;
import com.dchoc.toolkit.IByteArray;
import java.io.IOException;

public class AnimationFrame {
   public static final int MAX_DURATION = Integer.MAX_VALUE;
   private static final CollisionBox[] COLLISION_BOX_COUNT_0 = new CollisionBox[0];
   private static final int[] COLOR_COUNT_0 = new int[0];
   private static final ParticleEmitter[] PARTICLE_EMITTER_COUNT_0 = new ParticleEmitter[0];
   private static final float[][] POLYGON_COUNT_0 = new float[0][];
   private static final int[][] RENDERABLE_COUNT_0 = new int[0][];
   private static final int[] SCALED_NESTINGS = new int[]{
      80201,
      67379,
      67371,
      67381,
      67377,
      67365,
      70431,
      70325,
      70429,
      70523,
      70251,
      70499,
      70249,
      70261,
      70291,
      70397,
      70437,
      70463,
      70423,
      70247,
      70447,
      70213,
      70289,
      70269,
      70319,
      70329,
      70585,
      70575,
      75205,
      75229,
      79983,
      79973,
      79993,
      79977,
      80001,
      79925,
      79939,
      79917,
      79929,
      79955,
      79933
   };
   private static final int[] smImageCacheReturnValue = new int[2];
   private int mParentAnimRid;
   private int mNestingIndex;
   private short mWidth;
   private short mHeight;
   private Animation mParentAnimation;
   private byte mCollisionPolygon = -1;
   private CollisionBox[] mCollisionBox;
   private int[][] mRenderable;
   private int[] mColors;
   private float[][] mPolygon;
   private ParticleEmitter[] mParticleEmitter;

   public AnimationFrame(boolean withEmptyData, Animation animation) {
      if (withEmptyData) {
         this.mCollisionBox = COLLISION_BOX_COUNT_0;
         this.mRenderable = RENDERABLE_COUNT_0;
         this.mColors = COLOR_COUNT_0;
         this.mPolygon = POLYGON_COUNT_0;
         this.mParticleEmitter = PARTICLE_EMITTER_COUNT_0;
      }

      this.mParentAnimation = animation;
   }

   public AnimationFrame clone(Animation animation) {
      AnimationFrame copy = new AnimationFrame(false, animation);
      copy.mColors = this.mColors;
      copy.mCollisionPolygon = this.mCollisionPolygon;
      copy.mWidth = this.mWidth;
      copy.mHeight = this.mHeight;
      copy.mCollisionBox = this.mCollisionBox;
      copy.mRenderable = this.mRenderable;
      copy.mPolygon = this.mPolygon;
      if (this.mParticleEmitter != null) {
         int count = this.getParticleEmitterCount();
         copy.mParticleEmitter = new ParticleEmitter[count];

         for (int i = 0; i < count; i++) {
            copy.mParticleEmitter[i] = this.mParticleEmitter[i].clone();
         }
      }

      return copy;
   }

   public void setParent(int parentAnimRid) {
      this.mParentAnimRid = parentAnimRid;
   }

   public int getParent() {
      return this.mParentAnimRid;
   }

   public int getWidth() {
      return this.mWidth;
   }

   public int getHeight() {
      return this.mHeight;
   }

   public boolean loadFrameData(IByteArray data, boolean byteCoords, boolean hd, float scale) throws IOException {
      boolean ok = true;
      if (byteCoords) {
         this.mWidth = (short)data.readUnsignedByte();
         this.mHeight = (short)data.readUnsignedByte();
      } else {
         this.mWidth = data.readShort();
         this.mHeight = data.readShort();
      }

      this.mWidth = (short)(this.mWidth * scale);
      this.mHeight = (short)(this.mHeight * scale);
      int boxCount = data.readUnsignedByte();
      if (boxCount == 0) {
         this.mCollisionBox = COLLISION_BOX_COUNT_0;
      } else {
         this.mCollisionBox = new CollisionBox[boxCount];

         for (int i = 0; i < boxCount; i++) {
            int id = data.readUnsignedByte();
            int x;
            int y;
            int w;
            int h;
            if (byteCoords) {
               x = data.readByte();
               y = data.readByte();
               w = data.readUnsignedByte();
               h = data.readUnsignedByte();
            } else {
               x = data.readShort();
               y = data.readShort();
               w = data.readShort();
               h = data.readShort();
            }

            this.mCollisionBox[i] = new CollisionBox((int)(x * scale), (int)(y * scale), (int)(w * scale), (int)(h * scale), id);
         }
      }

      int count = data.readShort() & '\uffff';
      if (count == 0) {
         this.mRenderable = RENDERABLE_COUNT_0;
         this.mColors = COLOR_COUNT_0;
         this.mPolygon = POLYGON_COUNT_0;
      } else {
         this.mRenderable = new int[count][];
         this.mColors = new int[count];
         this.mPolygon = new float[count][];

         for (int i = 0; i < count; i++) {
            int type = data.readUnsignedByte();
            int color = 0;
            float[] polygon = null;
            int[] element;
            switch (type) {
               case 1:
                  element = new int[5];
                  if (byteCoords) {
                     element[1] = data.readByte();
                     element[2] = data.readByte();
                  } else {
                     element[1] = data.readShort();
                     element[2] = data.readShort();
                  }

                  int packed = data.readUnsignedByte();
                  int imageId = data.readInt();
                  int imageIndex = data.readUnsignedByte();
                  if (DavinciUtilities.loadImageIntoCache(imageId, imageIndex, packed & 15, (packed >> 4) + 1, hd, smImageCacheReturnValue)) {
                     element[3] = (short)smImageCacheReturnValue[0];
                     element[4] = (short)smImageCacheReturnValue[1];
                  } else {
                     ok = false;
                  }
                  break;
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
               case 8:
               default:
                  color = data.readInt();
                  float animationScale = this.mParentAnimation.getCustomScaleValue();
                  if (type != 6 && type != 8) {
                     element = new int[5];
                     if (byteCoords) {
                        element[1] = (short)(data.readByte() * animationScale);
                        element[2] = (short)(data.readByte() * animationScale);
                        if (type == 4) {
                           element[3] = (short)(data.readByte() * animationScale);
                           element[4] = (short)(data.readByte() * animationScale);
                        } else {
                           element[3] = (short)(data.readUnsignedByte() * animationScale);
                           element[4] = (short)(data.readUnsignedByte() * animationScale);
                        }
                     } else {
                        element[1] = (short)(data.readShort() * animationScale);
                        element[2] = (short)(data.readShort() * animationScale);
                        element[3] = (short)(data.readShort() * animationScale);
                        element[4] = (short)(data.readShort() * animationScale);
                     }
                  } else {
                     element = new int[2];
                     int pointCount = data.readShort();
                     element[1] = pointCount;
                     polygon = new float[pointCount * 2];

                     for (int k = 0; k < polygon.length; k++) {
                        if (byteCoords) {
                           polygon[k] = data.readByte() * animationScale;
                        } else {
                           polygon[k] = data.readShort() * animationScale;
                        }
                     }
                  }
                  break;
               case 7:
                  element = new int[5];
                  if (byteCoords) {
                     element[1] = data.readByte();
                     element[2] = data.readByte();
                     element[3] = data.readUnsignedByte();
                     element[4] = data.readUnsignedByte();
                  } else {
                     element[1] = data.readShort();
                     element[2] = data.readShort();
                     element[3] = data.readShort();
                     element[4] = data.readShort();
                  }
                  break;
               case 9:
                  element = new int[5];
                  if (byteCoords) {
                     element[1] = data.readByte();
                     element[2] = data.readByte();
                  } else {
                     element[1] = data.readShort();
                     element[2] = data.readShort();
                  }

                  data.readUnsignedByte();
                  int animId = data.readInt();
                  int cacheIndex = DavinciUtilities.loadAnimationIntoCache(
                     animId, hd, this.mParentAnimation.getGfxProfile(), this.mParentAnimation.getCustomScaleValue()
                  );
                  element[4] = (short)cacheIndex;
                  SpriteObject nested = DavinciUtilities.getSpriteObjectFromCache((short)cacheIndex);
                  if (nested != null) {
                     this.mParentAnimation.addAsBundleListener(nested.getBundleDependencies());
                  }
                  break;
               case 10:
                  element = this.loadParticleEmitter(data, byteCoords, hd, count - i);
            }

            element[0] = type;
            this.mRenderable[i] = element;
            this.mColors[i] = color;
            this.mPolygon[i] = polygon;
            if (polygon != null) {
               this.mCollisionPolygon = (byte)i;
            }
         }
      }

      return ok;
   }

   private int[] loadParticleEmitter(IByteArray data, boolean byteCoords, boolean hd, int remaining) throws IOException {
      int animationCount = data.readShort();
      int[] element = new int[animationCount + 3];
      element[2] = animationCount;
      SpriteObject[] sprites = new SpriteObject[animationCount];
      float animationScale = this.mParentAnimation.getCustomScaleValue();

      for (int i = 0; i < animationCount; i++) {
         int animId = data.readInt();
         int cacheIndex = DavinciUtilities.loadAnimationIntoCache(animId, hd, this.mParentAnimation.getGfxProfile(), animationScale);
         element[i + 3] = (short)cacheIndex;
         sprites[i] = DavinciUtilities.getSpriteObjectFromCache(cacheIndex);
      }

      ParticleEmitter emitter;
      if (byteCoords) {
         emitter = new ParticleEmitter(data.readByte(), data.readByte(), data.readUnsignedByte(), data.readUnsignedByte());
      } else {
         emitter = new ParticleEmitter(data.readShort(), data.readShort(), data.readShort(), data.readShort());
      }

      emitter.setAnimations(sprites);
      emitter.setRandomizeAnimationSelection(data.readByte() == 1);
      emitter.setDrawNewestParticleFirst(data.readByte() == 1);
      emitter.setRandomizeParticleSpread(data.readByte() == 1);
      emitter.setDynamicEmitterPosition(data.readByte() == 1);
      emitter.setFixedParticleLife(data.readByte() == 1);
      emitter.setId(data.readShort());
      emitter.setMaxParticleCount(data.readShort());
      emitter.setGravityX(data.readShort());
      emitter.setGravityY(data.readShort());
      emitter.setParticleMinAcceleration(data.readShort());
      emitter.setParticleMaxAcceleration(data.readShort());
      emitter.setParticleMinAngle(data.readShort());
      emitter.setParticleMaxAngle(data.readShort());
      emitter.setParticleSpreadStep(data.readShort());
      emitter.setParticleMinStartVelocity(data.readShort());
      emitter.setParticleMaxStartVelocity(data.readShort());
      emitter.setParticleMinEndVelocity(data.readShort());
      emitter.setParticleMaxEndVelocity(data.readShort());
      emitter.setParticleMinLife(data.readInt());
      emitter.setParticleMaxLife(data.readInt());
      emitter.setDuration(data.readInt());
      emitter.setEmissionInterval(data.readInt());
      if (this.mParticleEmitter == null) {
         this.mParticleEmitter = new ParticleEmitter[remaining];
      }

      for (int slot = 0; slot < this.mParticleEmitter.length; slot++) {
         if (this.mParticleEmitter[slot] == null) {
            this.mParticleEmitter[slot] = emitter;
            element[1] = slot;
            break;
         }
      }

      return element;
   }

   public CollisionBox getCollisionBox(int id) {
      if (this.mCollisionBox == null) {
         return null;
      }

      for (int i = this.mCollisionBox.length - 1; i >= 0; i--) {
         if (this.mCollisionBox[i].getId() == id) {
            return this.mCollisionBox[i];
         }
      }

      return null;
   }

   public CollisionBox[] getCollisionBoxes() {
      return this.mCollisionBox;
   }

   public float[] getCollisionPolygon() {
      return this.mCollisionPolygon == -1 ? null : this.mPolygon[this.mCollisionPolygon];
   }

   public int[] getColors() {
      return this.mColors;
   }

   public ParticleEmitter getParticleEmitter(int id) {
      if (this.mParticleEmitter == null) {
         return null;
      }

      for (int i = this.mParticleEmitter.length - 1; i >= 0; i--) {
         if (this.mParticleEmitter[i].getId() == id) {
            return this.mParticleEmitter[i];
         }
      }

      return null;
   }

   public ParticleEmitter[] getParticleEmitters() {
      return this.mParticleEmitter;
   }

   public float[][] getPolygons() {
      return this.mPolygon;
   }

   public int[][] getRenderables() {
      return this.mRenderable;
   }

   public int getTextures(OGLRegion[] regions) {
      int capacity = regions != null ? regions.length : 0;
      int stored = 0;

      for (int i = 0; i < this.mRenderable.length; i++) {
         if (this.mRenderable[i][0] == 1 && stored < capacity) {
            regions[stored] = DavinciUtilities.getImageFromCache(this.mRenderable[i][3]);
            stored++;
         }
      }

      return stored;
   }

   private int getParticleEmitterCount() {
      int count = 0;
      if (this.mParticleEmitter != null) {
         for (int i = 0; i < this.mParticleEmitter.length; i++) {
            if (this.mParticleEmitter[i] != null) {
               count++;
            }
         }
      }

      return count;
   }

   public boolean hasNestedAnimations() {
      for (int i = 0; i < this.mRenderable.length; i++) {
         if (this.mRenderable[i][0] == 9) {
            return true;
         }
      }

      return false;
   }

   public void doDraw(float x, float y, int flags, int elapsedTime) {
      if (this.mRenderable != null) {
         this.mNestingIndex = 0;
         float pivotX = OGL.getPivotX();
         float pivotY = OGL.getPivotY();
         OGL.setPivot(x, y);
         if (this.mRenderable.length > 0) {
            int px = (int)x;
            int py = (int)y;
            boolean clipped = false;
            int savedX = 0;
            int savedY = 0;
            int savedWidth = 0;
            int savedHeight = 0;

            for (int i = 0; i < this.mRenderable.length; i++) {
               int[] r = this.mRenderable[i];
               switch (r[0]) {
                  case 1:
                     int imageFlags = r[4] & 1;
                     if ((r[4] & 2) != 0) {
                        imageFlags |= 2;
                     }

                     OGL.drawTexture(DavinciUtilities.getImageFromCache(r[3]), r[1], r[2], imageFlags, flags, 20);
                     break;
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 8:
                  default:
                     this.drawPrimitive(r, this.mColors[i], this.mPolygon[i], flags);
                     break;
                  case 7:
                     if (clipped) {
                        OGL.setClip(savedX, savedY, savedWidth, savedHeight);
                     } else {
                        savedX = OGL.getClipX();
                        savedY = OGL.getClipY();
                        savedWidth = OGL.getClipWidth();
                        savedHeight = OGL.getClipHeight();
                     }

                     OGL.clipRect(r[1] + px, r[2] + py, r[3], r[4]);
                     clipped = true;
                     break;
                  case 9:
                     this.drawNested(r, x, y, flags, elapsedTime);
                     break;
                  case 10:
                     ParticleEmitter emitter = this.mParticleEmitter[r[1]];
                     if (emitter.isDynamicEmitterPosition()) {
                        emitter.setElapsedTime(elapsedTime, px, py);
                        emitter.render(0, 0);
                     } else {
                        emitter.setElapsedTime(elapsedTime, 0, 0);
                        emitter.render(px, py);
                     }
               }
            }

            if (clipped) {
               OGL.setClip(savedX, savedY, savedWidth, savedHeight);
            }
         }

         OGL.setPivot(pivotX, pivotY);
      }
   }

   private void drawPrimitive(int[] r, int color, float[] polygon, int flags) {
      boolean custom = this.mParentAnimation.isCustomScale();
      if (custom) {
         float scaleX = OGL.getScaleX();
         float scaleY = OGL.getScaleY();
         OGL.pushParameters();
         float customScale = this.mParentAnimation.getCustomScaleValue();
         OGL.setScale(scaleX / customScale, scaleY / customScale);
      }

      OGL.setColorRGB(color);
      int type = r[0];
      if (type == 4) {
         int x1 = r[1];
         int y1 = r[2];
         int x2 = r[3];
         int y2 = r[4];
         if ((flags & 1) != 0) {
            x1 = -x1;
            x2 = -x2;
         }

         if ((flags & 2) != 0) {
            y1 = -y1;
            y2 = -y2;
         }

         OGL.drawLine(x1, y1, x2, y2);
      } else if (type == 2 || type == 3 || type == 5) {
         int x = r[1];
         int y = r[2];
         int width = r[3];
         int height = r[4];
         if ((flags & 1) != 0) {
            x = -(x + width);
         }

         if ((flags & 2) != 0) {
            y = -(y + height);
         }

         if (type == 2) {
            OGL.fillRect(x, y, width, height);
         } else if (type == 3) {
            OGL.drawRect(x, y, width, height);
         } else {
            OGL.fillEllipse(x, y, width, height);
         }
      } else if (type == 6) {
         OGL.fillPolygon(r[1], polygon, flags);
      } else {
         OGL.fillBezier(r[1], polygon, flags);
      }

      if (custom) {
         OGL.popParameters();
      }
   }

   private void drawNested(int[] r, float x, float y, int flags, int elapsedTime) {
      float scale = this.mParentAnimation.getCustomScaleValue();
      float offsetX = r[1] * scale;
      float offsetY = r[2] * scale;
      if ((flags & 1) != 0) {
         offsetX = -offsetX;
      }

      if ((flags & 2) != 0) {
         offsetY = -offsetY;
      }

      float nestedX = offsetX + x;
      float nestedY = offsetY + y;
      SpriteObject sprite = DavinciUtilities.getSpriteObjectFromCache(r[4]);
      if (sprite != null) {
         boolean custom = this.mParentAnimation.isCustomScale();
         float customScale = custom ? scale : 1.0F;
         if (!WindowManager.isEmpty()) {
            int divisor = !DeviceWrapper.isIPAD() && !DeviceWrapper.isIPHONE4() ? 2 : 4;

            for (int n = 0; n < SCALED_NESTINGS.length; n++) {
               if (this.mParentAnimRid == SCALED_NESTINGS[n]) {
                  CollisionBox[] boxes = sprite.getCollisionBoxes();
                  if (boxes != null) {
                     CollisionBox mine = this.mNestingIndex < this.mCollisionBox.length ? this.mCollisionBox[this.mNestingIndex] : null;
                     if (boxes.length > 0 && mine != null) {
                        int dx = ((int)(boxes[0].getX() * customScale) - mine.getX()) / divisor;
                        int dy = ((int)(boxes[0].getY() * customScale) - mine.getY()) / divisor;
                        if (boxes.length > 1 && boxes[1] != null) {
                           if (DeviceWrapper.isIPAD() || DeviceWrapper.isIPHONE4()) {
                              dx += (int)(boxes[1].getX() / customScale);
                              dy += (int)(boxes[1].getY() / customScale);
                           } else if (this.mParentAnimRid == SCALED_NESTINGS[0] && boxes.length > 2 && boxes[2] != null) {
                              dy += boxes[2].getY();
                           }
                        }

                        nestedX += dx;
                        nestedY += dy;
                     }
                  }

                  this.mNestingIndex = this.mNestingIndex + 1 & 0xFF;
               }
            }
         }

         sprite.setElapsedTime(elapsedTime);
         if (custom) {
            float scaleX = OGL.getScaleX();
            float scaleY = OGL.getScaleY();
            OGL.pushParameters();
            OGL.setScale(scaleX / customScale, scaleY / customScale);
         }

         sprite.draw(nestedX, nestedY, flags, true);
         if (custom) {
            OGL.popParameters();
         }
      }
   }
}
