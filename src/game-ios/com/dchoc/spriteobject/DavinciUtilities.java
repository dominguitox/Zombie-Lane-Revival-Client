package com.dchoc.spriteobject;

import com.dchoc.idead.Profiler;
import com.dchoc.idead.tracking.ErrorEvent;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.iphonewrappers.FileWrapper;
import com.dchoc.opengl.OGLRegion;
import com.dchoc.opengl.OGLTexture;
import com.dchoc.opengl.OGLTextureManager;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.IByteArray;
import com.dchoc.toolkit.ResourceIDs;
import com.dchoc.toolkit.Toolkit;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class DavinciUtilities implements ResourceIDs {
   private static final java.lang.System.Logger LOG = System.getLogger("com.dchoc.spriteobject.DavinciUtilities");
   private static final Map<String, Integer> MISSING_RESOURCES = new HashMap<>();
   private static final Map<String, Integer> REPEATED_ERRORS = new HashMap<>();
   private static int smMissingLogged;
   private static int smMissingTotal;
   public static final int ADDITIONAL_TEXTURE_CAPACITY = 32;
   public static final int BILINEAR_ACCURACY = 8;
   public static final int BUNDLE_PACK_BIT = 20;
   public static final String BUNDLE_PREFIX = "bundle_";
   public static final int BUNDLE_STATE_LOADED = 2;
   public static final int BUNDLE_STATE_LOCAL = 1;
   public static final int BUNDLE_STATE_SERVER = 0;
   public static final byte BUNDLE_TYPE_LOCAL = 0;
   public static final byte BUNDLE_TYPE_SERVER = 1;
   public static final boolean ENABLE_CLIP_RECTS = true;
   public static final boolean ENABLE_COLLISION_BOXES = true;
   public static final boolean ENABLE_FILLED_CIRCLES = true;
   public static final boolean ENABLE_FILLED_POLYGONS = true;
   public static final boolean ENABLE_FILLED_RECTS = true;
   public static final boolean ENABLE_LINES = true;
   public static final boolean ENABLE_NESTED_ANIMATIONS = true;
   public static final boolean ENABLE_PARTICLE_EMITTERS = true;
   public static final boolean ENABLE_RECTS = true;
   public static final int INVALID_TEXTURE = -1;
   public static final int NO_SCALING = 1024;
   public static final int PIXELS_24BIT = 254;
   public static final int PIXELS_32BIT = 255;
   public static final int PIXELS_INDEXED = -1;
   public static final int PROFILE_DEFAULT = 0;
   public static final int PROFILE_HD = 2;
   public static final int PROFILE_SD = 1;
   public static final int RENDERABLE_PARAM_TYPE = 0;
   public static final int RENDERABLE_PRIMITIVE_PARAM_COLOR = 1;
   public static final int RENDERABLE_PRIMITIVE_PARAM_HEIGHT = 5;
   public static final int RENDERABLE_PRIMITIVE_PARAM_WIDTH = 4;
   public static final int RENDERABLE_PRIMITIVE_PARAM_X = 2;
   public static final int RENDERABLE_PRIMITIVE_PARAM_Y = 3;
   public static final int RENDERABLE_TYPE_ANIM_PARAM_ID = 4;
   public static final int RENDERABLE_TYPE_ANIM_PARAM_LOOP = 3;
   public static final int RENDERABLE_TYPE_ANIM_PARAM_X = 1;
   public static final int RENDERABLE_TYPE_ANIM_PARAM_Y = 2;
   public static final int RENDERABLE_TYPE_BEZIER = 8;
   public static final int RENDERABLE_TYPE_CLIP_RECT = 7;
   public static final int RENDERABLE_TYPE_CLIP_RECT_HEIGHT = 4;
   public static final int RENDERABLE_TYPE_CLIP_RECT_WIDTH = 3;
   public static final int RENDERABLE_TYPE_CLIP_RECT_X = 1;
   public static final int RENDERABLE_TYPE_CLIP_RECT_Y = 2;
   public static final int RENDERABLE_TYPE_FILLEDCIRCLE = 5;
   public static final int RENDERABLE_TYPE_FILLEDRECT = 2;
   public static final int RENDERABLE_TYPE_IMAGE = 1;
   public static final int RENDERABLE_TYPE_IMAGE_PARAM_ID = 3;
   public static final int RENDERABLE_TYPE_IMAGE_PARAM_TRANSFORMATION = 4;
   public static final int RENDERABLE_TYPE_IMAGE_PARAM_X = 1;
   public static final int RENDERABLE_TYPE_IMAGE_PARAM_Y = 2;
   public static final int RENDERABLE_TYPE_LINE = 4;
   public static final int RENDERABLE_TYPE_LINE_X1 = 2;
   public static final int RENDERABLE_TYPE_LINE_X2 = 4;
   public static final int RENDERABLE_TYPE_LINE_Y1 = 3;
   public static final int RENDERABLE_TYPE_LINE_Y2 = 5;
   public static final int RENDERABLE_TYPE_NESTED_ANIMATION = 9;
   public static final int RENDERABLE_TYPE_PARTICLE_EMITTER = 10;
   public static final int RENDERABLE_TYPE_PARTICLE_EMITTER_PARAM_ANIM_COUNT = 2;
   public static final int RENDERABLE_TYPE_PARTICLE_EMITTER_PARAM_ANIM_DATA = 3;
   public static final int RENDERABLE_TYPE_PARTICLE_EMITTER_PARAM_INDEX = 1;
   public static final int RENDERABLE_TYPE_POLYGON = 6;
   public static final int RENDERABLE_TYPE_RECT = 3;
   public static final int SCALE_ACCURACY = 10;
   public static final String SEPARATOR = "/";
   public static final int TEXTURE_PACK_BIT = 14;
   public static final int TRANSFORMATION_FLIP_HORIZONTAL = 1;
   public static final int TRANSFORMATION_FLIP_VERTICAL = 2;
   public static final int TRANSFORMATION_NONE = 0;
   public static final boolean USE_ANIMATION_CACHING = false;
   public static final boolean USE_IMAGE_CACHING = false;
   public static final boolean USE_MIDP2_TRANSFORMATIONS = true;
   public static final boolean USE_NOKIAUI_TRANSFORMATIONS = false;
   public static final boolean USE_REALTIME_TRANSFORMATIONS = true;
   public static final boolean USE_TEXTURES = true;
   private static final int INITIAL_SIZE = 3400;
   public static int TRACK_PIXELDATA = -1;
   public static int TRACK_IMAGEINDEX = -1;
   public static Vector smImages = new Vector(3400, 1700);
   private static short[] smImageReferences = new short[3400];
   private static int[] smImageIDs = new int[3400];
   private static byte[] smPaletteIndices = new byte[3400];
   private static short[] smImageTransformation = new short[3400];
   private static byte[] smBundleStates;
   private static byte[] smBundleColorDepth;
   private static int[] smBundleVersions;
   private static byte[] smServerBundle;
   private static OGLTexture[] smTextureImage;
   private static short[][] smTextureCoordinate;
   private static int[][] smTextureIndexes;
   private static int smTextureCounter;
   private static Vector smSpriteObjectCache;
   private static int[] smSpriteObjectID;
   private static short[] smSpriteObjectReference;
   private static Object smTempSpriteObject;

   public static void initialize() {
      try {
         textureInit();
      } catch (Exception exception) {
         trackError("initialize", exception, null);
      } finally {
         logMissingResourceSummary();
      }

      ServerAssetLoader.init();
   }

   public static void textureInit() throws IOException {
      DeviceWrapper.checkVGASupport();
      IByteArray data = Toolkit.getResourceByteArray("bundle_0/0");
      int bundleCount = data.readInt() + 1;
      int textureCount = data.readShort();
      smBundleStates = new byte[bundleCount];
      smBundleColorDepth = new byte[bundleCount];
      smTextureImage = new OGLTexture[textureCount + 32];
      smTextureCoordinate = new short[textureCount + 32][];
      smTextureIndexes = new int[bundleCount][];
      smTextureCounter = 0;
      smServerBundle = new byte[bundleCount];
      IByteArray serverList = Toolkit.getResourceByteArray(80409);
      int serverCount = serverList.readInt();
      smBundleVersions = new int[bundleCount];

      for (int i = 0; i < serverCount; i++) {
         int id = serverList.readInt();
         smBundleVersions[id] = serverList.readInt();
         serverList.readInt();
         smServerBundle[id] = 1;
      }

      Vector excluded = new Vector(10);
      IByteArray excludedData = Toolkit.getResourceByteArray(80408);
      if (excludedData != null) {
         try {
            while (true) {
               excluded.addElement(excludedData.readInt());
            }
         } catch (EOFException exception) {
            LOG.log(java.lang.System.Logger.Level.DEBUG, "End of the excluded-resources list");
         }
      }

      IByteArray localList = Toolkit.getResourceByteArray(80407);
      int localCount = localList.readInt();

      for (int i = 0; i < localCount; i++) {
         int bundleId = localList.readInt();
         if (!excluded.contains(bundleId)) {
            try {
               String path = "bundle_" + bundleId + "/";
               path = path + (smServerBundle[bundleId] == 0 ? "0" : getVersionText(smBundleVersions[bundleId]) + "/0");
               IByteArray bundleData = Toolkit.getResourceByteArray(path);
               if (bundleData != null) {
                  if (bundleId == 0) {
                     bundleData.readInt();
                     bundleData.readShort();
                  }

                  int id = readTextureBundleData(bundleData, false);
                  if (id != -1) {
                     smBundleStates[id] = 1;
                  }
               }
            } catch (Exception exception) {
               trackError("textureInit", exception, Integer.toString(i));
            }
         }
      }
   }

   public static int readTextureBundleData(IByteArray data, boolean server) {
      if (data == null) {
         return -1;
      }

      int bundleId = -1;

      try {
         bundleId = data.readInt();
         data.readInt();
         data.readByte();
         if (bundleId >= 0 && smBundleStates != null && bundleId < smBundleStates.length) {
            boolean serverBundle = smServerBundle[bundleId] != 0;
            byte colorDepth = data.readByte();
            int count = data.readShort();
            smTextureIndexes[bundleId] = new int[count];

            for (int i = 0; i < count; i++) {
               ensureTextureCapacity(smTextureCounter);
               smTextureIndexes[bundleId][i] = smTextureCounter;
               readTextureData(data, smTextureCounter);
               smTextureCounter++;
            }

            if (server || serverBundle) {
               for (int i = 1; i <= count; i++) {
                  String path = "bundle_" + bundleId + "/" + getVersionText(smBundleVersions[bundleId]) + "/" + i + ".png";
                  if (!FileWrapper.fileExists(path)) {
                     for (int n = 0; n < count; n++) {
                        smTextureCoordinate[smTextureIndexes[bundleId][n]] = null;
                     }

                     return -1;
                  }
               }
            }

            smBundleColorDepth[bundleId] = colorDepth;
            return bundleId;
         } else {
            throw new Exception("Invalid bundle id");
         }
      } catch (Exception exception) {
         trackError("readTextureBundleData", exception, Integer.toString(bundleId));
         return -1;
      }
   }

   public static void ensureTextureCapacity(int index) {
      if (smTextureImage == null || index >= smTextureImage.length || smTextureCoordinate == null || index >= smTextureCoordinate.length) {
         int capacity = index + 32;
         OGLTexture[] textures = new OGLTexture[capacity];
         short[][] coordinates = new short[capacity][];
         if (smTextureImage != null) {
            System.arraycopy(smTextureImage, 0, textures, 0, smTextureImage.length);
         }

         if (smTextureCoordinate != null) {
            System.arraycopy(smTextureCoordinate, 0, coordinates, 0, smTextureCoordinate.length);
         }

         smTextureImage = textures;
         smTextureCoordinate = coordinates;
      }
   }

   private static void readTextureData(IByteArray data, int index) {
      try {
         int count = data.readShort();
         smTextureCoordinate[index] = new short[count * 4];

         for (int i = 0; i < count; i++) {
            for (int n = 0; n < 4; n++) {
               smTextureCoordinate[index][i * 4 + n] = data.readShort();
            }
         }
      } catch (EOFException exception) {
         trackError("readTextureData", exception, null);
      }
   }

   public static String getVersionText(int version) {
      String text = "" + version;
      String suffix = "_" + Integer.parseInt(text.substring(text.length() - 3));
      text = text.substring(0, text.length() - 3);
      suffix = "_" + Integer.parseInt(text.substring(text.length() - 3)) + suffix;
      text = text.substring(0, text.length() - 3);
      return Integer.parseInt(text) + suffix;
   }

   public static Animation loadAnimation(int id) {
      return loadAnimation(id, false, 0, true);
   }

   public static Animation loadAnimation(int id, boolean immediate, int profile) {
      return loadAnimation(id, false, profile, true);
   }

   public static Animation loadAnimation(int id, boolean immediate, int profile, boolean listener) {
      try {
         return new Animation(id, true, profile, listener);
      } catch (Exception exception) {
         new ErrorEvent().trackDavinciError("DavinciUtilities.loadAnimation", exception.getMessage(), Integer.toString(id));
         return null;
      }
   }

   public static boolean loadImageIntoCache(int id, int palette, int transform, int scale, boolean immediate, int[] result) {
      result[1] = 0;
      int scaling = scale * 256 - 256;

      for (int i = smImages.size() - 1; i >= 0; i--) {
         if (smImageIDs[i] == id && (id == -1 || (smPaletteIndices[i] & 255) == palette && smImageTransformation[i] == scaling)) {
            result[0] = i;
            result[1] = transform;
            return true;
         }
      }

      int index = smImages.size();

      for (int i = index - 1; i >= 0; i--) {
         if (smImageIDs[i] == -1) {
            index = i;
            break;
         }
      }

      if (index == smImages.size()) {
         if (index >= smImageIDs.length) {
            smImageIDs = Arrays.copyOf(smImageIDs, index * 2);
            smPaletteIndices = Arrays.copyOf(smPaletteIndices, index * 2);
            smImageReferences = Arrays.copyOf(smImageReferences, index * 2);
            smImageTransformation = Arrays.copyOf(smImageTransformation, index * 2);
         }

         smImageReferences[index] = 0;
      }

      if (TRACK_PIXELDATA == id) {
         TRACK_IMAGEINDEX = index;
      }

      smImageIDs[index] = id;
      smPaletteIndices[index] = (byte)palette;
      smImageTransformation[index] = (short)scaling;
      if (index == smImages.size()) {
         smImages.addElement(null);
      }

      Profiler.stopTimer(0);
      OGLRegion region = loadTextureImage(id, palette, 0, scale << 10);
      if (region == null) {
         return false;
      }

      smImages.setElementAt(region, index);
      Profiler.startTimer(0);
      result[0] = index;
      result[1] = transform;
      return true;
   }

   private static OGLRegion loadTextureImage(int id, int palette, int transform, int scale) {
      int bundleId = id >> 20;
      int textureNumber = (id & 1048575) >> 14;
      int index = getTextureId(bundleId, textureNumber - 1);
      if (index == -1) {
         return null;
      }

      if (smTextureImage[index] == null) {
         smTextureImage[index] = getImage(bundleId, textureNumber, palette, transform, scale);
         if (smTextureImage[index] == null) {
            return null;
         }
      }

      short[] coordinates = smTextureCoordinate[index];
      if (coordinates == null) {
         return null;
      }

      int offset = (id & 16383) * 4;
      return new OGLRegion(smTextureImage[index], coordinates[offset], coordinates[offset + 1], coordinates[offset + 2], coordinates[offset + 3]);
   }

   private static OGLTexture getImage(int bundleId, int textureNumber, int palette, int transform, int scale) {
      if (palette != 253) {
         return null;
      }

      String path = "bundle_" + bundleId + "/";
      if (smServerBundle[bundleId] != 0) {
         path = path + getVersionText(smBundleVersions[bundleId]) + "/";
      }

      path = path + textureNumber;

      try {
         return OGLTextureManager.loadTexture(path, smBundleColorDepth[bundleId]);
      } catch (Exception exception) {
         trackError("getImage", exception, Integer.toString(bundleId));
         return null;
      }
   }

   public static OGLRegion getImageFromCache(int index) {
      return (OGLRegion)smImages.elementAt(index);
   }

   public static int loadAnimationIntoCache(int id, boolean immediate, int profile, float scale) {
      if (smSpriteObjectID == null) {
         smSpriteObjectID = new int[3400];
         smSpriteObjectReference = new short[3400];
         smSpriteObjectCache = new Vector(3400);
      }

      int[] unlinked = getUnlinkedRID(id, profile, scale);
      int resourceId = unlinked[0];
      float customScale = unlinked[1];
      int size = smSpriteObjectCache.size();
      SpriteObject sprite = null;

      for (int i = 0; i < size; i++) {
         Object entry = smSpriteObjectCache.elementAt(i);
         if (smSpriteObjectID[i] == resourceId && entry != null) {
            if (!(entry instanceof SpriteObject)) {
               smSpriteObjectCache.setElementAt(null, i);
            } else {
               Animation data = ((SpriteObject)entry).getCurrentAnimationData();
               if (data == null) {
                  new ErrorEvent().trackDavinciError("DavinciUtilities.loadAnimationIntoCache", "Missing data", Integer.toString(resourceId));
                  sprite = new SpriteObject();
               } else {
                  Animation copy = data.clone();
                  copy.setCustomScaleValue(customScale);
                  sprite = new SpriteObject(copy);
               }
            }
            break;
         }
      }

      int index = 0;

      while (index < size && smSpriteObjectCache.elementAt(index) != null) {
         index++;
      }

      if (index >= smSpriteObjectID.length) {
         int capacity = Math.min(smSpriteObjectID.length << 1, 32767);
         smSpriteObjectReference = Arrays.copyOf(smSpriteObjectReference, capacity);
         smSpriteObjectID = Arrays.copyOf(smSpriteObjectID, capacity);
      }

      if (index == size) {
         smSpriteObjectCache.addElement(null);
      }

      if (smTempSpriteObject == null) {
         smTempSpriteObject = new Object();
      }

      if (sprite == null) {
         smSpriteObjectCache.setElementAt(smTempSpriteObject, index);
         Animation animation = loadAnimation(resourceId, false, profile, false);
         if (customScale != 1.0F) {
            animation.setCustomScaleValue(customScale);
         }

         sprite = new SpriteObject(animation);
      }

      sprite.setAnimation(0, -2, false);
      smSpriteObjectCache.setElementAt(sprite, index);
      smSpriteObjectID[index] = resourceId;
      return index;
   }

   private static int[] getUnlinkedRID(int id, int profile, float scale) {
      int[] result = new int[]{-1, (int)scale};
      IByteArray data = Toolkit.getResourceByteArray(id);
      if (profile == 1 || !DeviceWrapper.useHDGraphics() && profile != 2) {
         result[1] = 1;
      }

      result[0] = id;

      try {
         if (data.readByte() != 1) {
            return result;
         }

         int count = data.readInt();
         int sdId = -1;
         int hdId = -1;

         for (int i = 0; i < count; i++) {
            int type = data.readInt();
            int alternateId = data.readInt();
            if (type == 2) {
               hdId = alternateId;
            } else if (type == 1) {
               sdId = alternateId;
            }
         }

         DChocByteArray var12;
         if (DeviceWrapper.isVGA()) {
            if (sdId == -1) {
               result[0] = id;
               result[1] = 1;
               return result;
            }

            result[0] = sdId;
            var12 = Toolkit.getResourceByteArray(sdId);
         } else {
            if (profile == 1 || !DeviceWrapper.useHDGraphics() && profile != 2) {
               result[0] = id;
               result[1] = 1;
               return result;
            }

            result[0] = hdId;
            var12 = Toolkit.getResourceByteArray(hdId);
         }

         var12.readByte();
         if (smBundleStates[var12.readInt()] == 0) {
            result[1] = 2;
            result[0] = id;
         }
      } catch (Exception exception) {
         trackError("getUnlinkedRID", exception, Integer.toString(id));
      }

      return result;
   }

   public static SpriteObject getSpriteObjectFromCache(int index) {
      return (SpriteObject)smSpriteObjectCache.elementAt(index);
   }

   public static void modRefCounters(Animation animation, int change) {
      if (animation != null) {
         for (int i = animation.getFrameCount() - 1; i >= 0; i--) {
            if (!animation.isFrameReference(i)) {
               AnimationFrame frame = animation.getFrame(i);
               if (frame != null && frame.getRenderables() != null) {
                  int[][] renderables = frame.getRenderables();

                  for (int n = renderables.length - 1; n >= 0; n--) {
                     int[] element = renderables[n];
                     int type = (short)element[0];
                     if (type == 1) {
                        int index = (short)element[3];
                        smImageReferences[index] = (short)Math.max(0, smImageReferences[index] + change);
                     } else if (type == 9) {
                        animationReferenceChange((short)element[4], change);
                     } else if (type == 10) {
                        for (int p = 0; p < (short)element[2]; p++) {
                           animationReferenceChange((short)element[p + 3], change);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void animationReferenceChange(int index, int change) {
      smSpriteObjectReference[index] = (short)(smSpriteObjectReference[index] + (short)change);
      if (smSpriteObjectReference[index] < 1) {
         smSpriteObjectReference[index] = 0;
         SpriteObject sprite = getSpriteObjectFromCache(index);
         if (sprite != null) {
            sprite.freeResources();
            smSpriteObjectCache.setElementAt(null, index);
         }
      }
   }

   public static void releaseUnreferencedImages() {
      for (int i = smImages.size() - 1; i >= 0; i--) {
         if (smImageReferences[i] == 0 && smImages.elementAt(i) != null) {
            smImages.setElementAt(null, i);
            smImageIDs[i] = -1;
         }
      }

      if (smTextureImage != null) {
         for (int texture = smTextureImage.length - 1; texture >= 0; texture--) {
            if (smTextureImage[texture] != null) {
               boolean referenced = false;

               for (int i = smImages.size() - 1; i >= 0; i--) {
                  int id = smImageIDs[i];
                  if (id >= 0 && getTextureId(id >> 20, ((id & 1048575) >> 14) - 1) == texture && smImageReferences[i] > 0) {
                     referenced = true;
                     break;
                  }
               }

               if (!referenced) {
                  OGLTextureManager.unloadTexture(smTextureImage[texture]);
                  smTextureImage[texture] = null;
               }
            }
         }
      }
   }

   public static int getTextureId(int bundleId, int textureIndex) {
      if (smTextureIndexes != null && bundleId >= 0 && bundleId < smTextureIndexes.length) {
         int[] indexes = smTextureIndexes[bundleId];
         return indexes != null && textureIndex >= 0 && textureIndex < indexes.length ? indexes[textureIndex] : -1;
      } else {
         return -1;
      }
   }

   public static int getBundleState(int bundleId) {
      return smBundleStates[bundleId];
   }

   public static void setBundleState(int bundleId, byte state) {
      smBundleStates[bundleId] = state;
   }

   public static void setServerTextureData(int bundleId, int[] textureIds) {
      if (smBundleStates[bundleId] != 1) {
         smBundleStates[bundleId] = 0;
      }
   }

   public static int interpolateValue(int start, int end, int elapsed, int duration) {
      return elapsed * (end - start) / duration + start;
   }

   public static int interpolateColor(int start, int end, int elapsed, int duration) {
      return interpolateValue(start & 0xFF, end & 0xFF, elapsed, duration)
         | interpolateValue(start >>> 16 & 0xFF, end >>> 16 & 0xFF, elapsed, duration) << 16
         | interpolateValue(start >>> 24, end >>> 24, elapsed, duration) << 24
         | interpolateValue(start & 0xFF00, end & 0xFF00, elapsed, duration) & 0xFF00;
   }

   private static void trackError(String method, Exception exception, String context) {
      if (isMissingResource(exception)) {
         noteMissingResource(method, exception.getMessage());
         return;
      }
      if (exception instanceof EOFException) {
         noteRepeatedError(method, "EOFException");
         return;
      }
      new ErrorEvent().trackDavinciError("DavinciUtilities." + method, exception.getMessage(), context);
      com.dchoc.toolkit.Sonda.fail("DavinciUtilities." + method, exception);
   }

   /** A missing resource is routine in the alpha; it is counted instead of flooding the log. */
   private static boolean isMissingResource(Throwable exception) {
      for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
         String message = cause.getMessage();
         if (message == null) {
            continue;
         }
         if (message.startsWith("Resource not found")
            || message.startsWith("Recurso no encontrado")
            || message.startsWith("Recurso não encontrado")
            || message.startsWith("Falha ao abrir recurso")
            || message.startsWith("No se pudo abrir el recurso")) {
            return true;
         }
      }
      return false;
   }

   private static void noteMissingResource(String method, String message) {
      synchronized (MISSING_RESOURCES) {
         String key = method + " | " + message;
         int count = MISSING_RESOURCES.merge(key, 1, Integer::sum);
         smMissingTotal++;
         if (count == 1 && smMissingLogged < 5) {
            smMissingLogged++;
            LOG.log(java.lang.System.Logger.Level.INFO, "Resource not found in {0}: {1}", method, message);
         }
      }
   }

   private static void noteRepeatedError(String method, String message) {
      synchronized (REPEATED_ERRORS) {
         String key = method + " | " + message;
         int count = REPEATED_ERRORS.merge(key, 1, Integer::sum);
         if (count == 1 || count % 100 == 0) {
            LOG.log(java.lang.System.Logger.Level.WARNING, "{0} repeated {1} times in {2}", message, count, method);
         }
      }
   }

   /** Summarizes the missing resources when loading finishes, without repeating hundreds of lines. */
   public static void logMissingResourceSummary() {
      List<Map.Entry<String, Integer>> entries;
      int total;
      synchronized (MISSING_RESOURCES) {
         if (smMissingTotal == 0) {
            return;
         }
         total = smMissingTotal;
         entries = new ArrayList<>(MISSING_RESOURCES.entrySet());
         MISSING_RESOURCES.clear();
         smMissingTotal = 0;
         smMissingLogged = 0;
      }
      entries.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()));
      LOG.log(java.lang.System.Logger.Level.INFO,
         "Missing resources: {0} attempts across {1} different resources (details in the session report)",
         total, entries.size());
      int shown = Math.min(10, entries.size());
      for (int index = 0; index < shown; index++) {
         Map.Entry<String, Integer> entry = entries.get(index);
         LOG.log(java.lang.System.Logger.Level.INFO, "  {0} x {1}", entry.getValue(), entry.getKey());
      }
      if (entries.size() > shown) {
         LOG.log(java.lang.System.Logger.Level.INFO, "  ... and {0} more resources", entries.size() - shown);
      }
   }
}
