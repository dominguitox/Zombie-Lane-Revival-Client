package com.dchoc.opengl;

import com.dchoc.toolkit.Toolkit;

public class OGL {
   public static final int BOTTOM = 32;
   public static final int HCENTER = 1;
   public static final int LEFT = 4;
   public static final int MAX_PARAMETERS = 64;
   public static final int NEUTRAL_COLOR = -8355712;
   public static final int RIGHT = 8;
   public static final int SCALE_ACCURACY = 10;
   public static final int TOP = 16;
   public static final int TRANS_MIRROR_HORIZONTAL = 1;
   public static final int TRANS_MIRROR_VERTICAL = 2;
   public static final int TRANS_NONE = 0;
   public static final int TRANS_ROTATE_180 = 8;
   public static final int TRANS_ROTATE_270 = 16;
   public static final int TRANS_ROTATE_90 = 4;
   public static final int VCENTER = 2;
   private static final float INV_255 = 0.003921569F;
   private static final float INV_128 = 0.0078125F;
   private static final int GL_ONE = 1;
   private static final int GL_SRC_ALPHA = 770;
   private static final int GL_ONE_MINUS_SRC_ALPHA = 771;
   private static final int GL_SCISSOR_TEST = 3089;
   private static int smClipHeight;
   private static int smClipWidth;
   private static int smClipX;
   private static int smClipY;
   private static int smColor;
   private static int smColor2;
   private static float[] smCircleVertices;
   private static float[] smColorBuffer;
   private static float[] smGradientBuffer;
   private static OGLParameters[] smParameters;
   private static int smParametersIndex;
   private static float[] smPolygonGradient;
   private static int smPolygonGradientColor;
   private static float smScreenScaleX;
   private static float smScreenScaleY;
   private static float[] smTextureBuffer;
   private static float[] smVertexBuffer;

   public static void init() {
      smParameters = new OGLParameters[64];
      smParametersIndex = 0;
      smParameters[smParametersIndex] = new OGLParameters();
      smVertexBuffer = new float[10];
      smTextureBuffer = new float[10];
      smColorBuffer = new float[4];
      smGradientBuffer = new float[16];
      setClip(0, 0, Toolkit.getScreenWidth(), Toolkit.getScreenHeight());
      smScreenScaleX = 1.0F;
      smScreenScaleY = 1.0F;
      smPolygonGradientColor = -8355712;
      smPolygonGradient = null;
      smCircleVertices = new float[256];

      for (int i = 0; i < smCircleVertices.length; i += 2) {
         double angle = i * 0.02454369260617026;
         smCircleVertices[i] = (float)Math.cos(angle);
         smCircleVertices[i + 1] = (float)Math.sin(angle);
      }
   }

   public static void applyDefaults() {
      OGLHelpers.gl().glDisable(2960);
      OGLHelpers.gl().glDisable(2929);
      OGLHelpers.gl().glDisable(3008);
      OGLHelpers.gl().glDisable(2848);
      OGLHelpers.gl().glDisable(2896);
      OGLHelpers.gl().glDisable(2912);
   }

   public static void setClip(int x, int y, int width, int height) {
      int screenWidth = Toolkit.getScreenWidth();
      int screenHeight = Toolkit.getScreenHeight();
      smClipWidth = width;
      smClipX = x;
      smClipY = y;
      smClipHeight = height;
      OGLHelpers.gl().glScissor(x, screenHeight - (height + y), width, height);
      if (x < 1 && y < 1 && width >= screenWidth && height >= screenHeight) {
         OGLHelpers.gl().glDisable(3089);
      } else {
         OGLHelpers.gl().glEnable(3089);
      }
   }

   public static void clipRect(int x, int y, int width, int height) {
      int left = smClipX;
      int top = smClipY;
      int right = smClipWidth + left;
      if (width + x < right) {
         right = width + x;
      }

      if (left < x) {
         left = x;
      }

      int bottom = smClipHeight + top;
      if (height + y < bottom) {
         bottom = height + y;
      }

      if (top < y) {
         top = y;
      }

      setClip(left, top, right - left, bottom - top);
   }

   public static int getClipHeight() {
      return smClipHeight;
   }

   public static int getClipWidth() {
      return smClipWidth;
   }

   public static int getClipX() {
      return smClipX;
   }

   public static int getClipY() {
      return smClipY;
   }

   private static boolean isVisible(float x, float y, float width, float height) {
      if (width < 0.0F) {
         x += width;
      }

      if (height < 0.0F) {
         y += height;
      }

      return smClipX <= Math.abs(width) + x && x < smClipX + smClipWidth && smClipY <= Math.abs(height) + y && y < smClipY + smClipHeight;
   }

   public static void drawLine(int x1, int y1, int x2, int y2) {
      drawSegment(x1, y1, x2, y2, false, 0.0F);
   }

   public static void drawLineEx(float x1, float y1, float x2, float y2, float width) {
      drawSegment(x1, y1, x2, y2, true, width);
   }

   private static void drawSegment(float x1, float y1, float x2, float y2, boolean setWidth, float lineWidth) {
      OGLParameters parameters = smParameters[smParametersIndex];
      float pivotX = parameters.getPivotX();
      float pivotY = parameters.getPivotY();
      float sx1 = x1 * smScreenScaleX * parameters.getScaleX();
      float sx2 = x2 * smScreenScaleX * parameters.getScaleX();
      float sy1 = y1 * smScreenScaleY * parameters.getScaleY();
      float sy2 = y2 * smScreenScaleY * parameters.getScaleY();
      float angle = parameters.getAngle();
      if (angle == 0.0F) {
         sx1 += pivotX * smScreenScaleX;
         sx2 += pivotX * smScreenScaleX;
         sy1 += pivotY * smScreenScaleY;
         sy2 += pivotY * smScreenScaleY;
      } else {
         OGLHelpers.pushMatrix();
         OGLHelpers.translate(pivotX * smScreenScaleX, pivotY * smScreenScaleY, 0.0F);
         OGLHelpers.rotate(angle, 0.0F, 0.0F, 1.0F);
      }

      smVertexBuffer[0] = sx1;
      smVertexBuffer[1] = sy1;
      smVertexBuffer[2] = sx2;
      smVertexBuffer[3] = sy2;
      OGLHelpers.setVertexBuffer(smVertexBuffer);
      if (setWidth) {
         OGLHelpers.lineWidth(lineWidth);
      }

      setBlending(null, false);
      OGLHelpers.drawLines(0, 2, true);
      restoreModifiedColor();
      if (setWidth) {
         OGLHelpers.lineWidth(1.0F);
      }

      OGLHelpers.clearBuffers();
      if (angle != 0.0F) {
         OGLHelpers.popMatrix();
      }
   }

   public static void drawRect(int x, int y, int width, int height) {
      float[] box = prepareBox(x, y, width, height);
      if (box != null) {
         float left = box[0];
         float top = box[1];
         float right = box[2] + left;
         float bottom = box[3] + top;
         smVertexBuffer[0] = left;
         smVertexBuffer[1] = top;
         smVertexBuffer[2] = right;
         smVertexBuffer[3] = top;
         smVertexBuffer[4] = right;
         smVertexBuffer[5] = bottom;
         smVertexBuffer[6] = left;
         smVertexBuffer[7] = bottom;
         smVertexBuffer[8] = left;
         smVertexBuffer[9] = top;
         OGLHelpers.setVertexBuffer(smVertexBuffer);
         setBlending(null, false);
         OGLHelpers.drawLines(0, 5, true);
         finishBox();
      }
   }

   public static void fillRect(int x, int y, int width, int height) {
      float[] box = prepareBox(x, y, width, height);
      if (box != null) {
         float left = box[0];
         float top = box[1];
         float right = box[2] + left;
         float bottom = box[3] + top;
         smVertexBuffer[0] = left;
         smVertexBuffer[1] = top;
         smVertexBuffer[2] = left;
         smVertexBuffer[3] = bottom;
         smVertexBuffer[4] = right;
         smVertexBuffer[5] = top;
         smVertexBuffer[6] = right;
         smVertexBuffer[7] = bottom;
         OGLHelpers.setVertexBuffer(smVertexBuffer);
         setBlending(null, false);
         OGLHelpers.drawTriangles(0, 4, false, true);
         finishBox();
      }
   }

   private static float[] prepareBox(int x, int y, int width, int height) {
      OGLParameters parameters = smParameters[smParametersIndex];
      float pivotX = parameters.getPivotX();
      float pivotY = parameters.getPivotY();
      float left = x * smScreenScaleX * parameters.getScaleX();
      float boxWidth = width * smScreenScaleX * parameters.getScaleX();
      float top = y * smScreenScaleY * parameters.getScaleY();
      float boxHeight = height * smScreenScaleY * parameters.getScaleY();
      float angle = parameters.getAngle();
      if (angle == 0.0F) {
         left += pivotX * smScreenScaleX;
         top += pivotY * smScreenScaleY;
         if (!isVisible(left, top, boxWidth, boxHeight)) {
            return null;
         }
      } else {
         OGLHelpers.pushMatrix();
         OGLHelpers.translate(pivotX * smScreenScaleX, pivotY * smScreenScaleY, 0.0F);
         OGLHelpers.rotate(angle, 0.0F, 0.0F, 1.0F);
      }

      return new float[]{left, top, boxWidth, boxHeight};
   }

   private static void finishBox() {
      restoreModifiedColor();
      OGLHelpers.clearBuffers();
      if (smParameters[smParametersIndex].getAngle() != 0.0F) {
         OGLHelpers.popMatrix();
      }
   }

   public static void fillRectGradient(int x, int y, int width, int height, boolean vertical) {
      OGLParameters parameters = smParameters[smParametersIndex];
      float pivotX = parameters.getPivotX();
      float pivotY = parameters.getPivotY();
      float left = parameters.getScaleX() * x;
      float boxWidth = parameters.getScaleX() * width;
      float top = y * parameters.getScaleY();
      float boxHeight = height * parameters.getScaleY();
      float angle = parameters.getAngle();
      if (angle == 0.0F) {
         left += pivotX * smScreenScaleX;
         top += pivotY * smScreenScaleY;
         if (!isVisible(left, top, boxWidth, boxHeight)) {
            return;
         }
      } else {
         OGLHelpers.pushMatrix();
         OGLHelpers.translate(pivotX * smScreenScaleX, pivotY * smScreenScaleY, 0.0F);
         OGLHelpers.rotate(angle, 0.0F, 0.0F, 1.0F);
      }

      smVertexBuffer[0] = left;
      smVertexBuffer[1] = top;
      smVertexBuffer[2] = left;
      smVertexBuffer[3] = boxHeight + top;
      smVertexBuffer[4] = left + boxWidth;
      smVertexBuffer[5] = top;
      smVertexBuffer[6] = smVertexBuffer[4];
      smVertexBuffer[7] = smVertexBuffer[3];
      putPremultiplied(smGradientBuffer, 0, smColor);
      putPremultiplied(smGradientBuffer, 4, vertical ? smColor : smColor2);
      putPremultiplied(smGradientBuffer, 8, vertical ? smColor2 : smColor);
      System.arraycopy(smGradientBuffer, vertical ? 8 : 4, smGradientBuffer, 12, 4);
      OGLHelpers.setVertexBuffer(smVertexBuffer);
      OGLHelpers.setGradientBuffer(smGradientBuffer);
      setBlending(null, false);
      OGLHelpers.drawTriangles(0, 4, false, true);
      restoreModifiedColor();
      OGLHelpers.clearBuffers();
      if (angle != 0.0F) {
         OGLHelpers.popMatrix();
      }
   }

   public static void fillEllipse(int x, int y, int width, int height) {
      if (smCircleVertices != null) {
         OGLParameters parameters = smParameters[smParametersIndex];
         float centerX = x + (width >> 1) + parameters.getPivotX();
         float centerY = y + (height >> 1) + parameters.getPivotY();
         OGLHelpers.pushMatrix();
         OGLHelpers.translate(centerX * smScreenScaleX, centerY * smScreenScaleY, 0.0F);
         OGLHelpers.scale(width * 0.5F * parameters.getScaleX() * smScreenScaleX, height * 0.5F * parameters.getScaleY() * smScreenScaleY, 1.0F);
         float angle = parameters.getAngle();
         if (angle != 0.0F) {
            OGLHelpers.rotate(angle, 0.0F, 0.0F, 1.0F);
         }

         OGLHelpers.setVertexBuffer(smCircleVertices);
         setBlending(null, false);
         OGLHelpers.drawTriangles(0, smCircleVertices.length / 2, true, false);
         restoreModifiedColor();
         OGLHelpers.clearBuffers();
         OGLHelpers.popMatrix();
      }
   }

   public static void fillPolygon(int count, float[] points, int flags) {
      fillBezier(count, points, flags);
   }

   public static void fillBezier(int count, float[] points, int flags) {
      if (points != null) {
         if (checkPolygonGradientHack(points)) {
            fillBezierGradient(count, points, smPolygonGradient, flags);
         } else {
            beginPolygon(flags);
            OGLHelpers.setVertexBuffer(points);
            setBlending(null, false);
            OGLHelpers.drawTriangles(0, points.length / 2, false, false);
            endPolygon();
         }
      }
   }

   public static void fillBezierGradient(int count, float[] points, float[] colors, int flags) {
      if (points != null && colors != null) {
         beginPolygon(flags);
         OGLHelpers.setVertexBuffer(points);
         OGLHelpers.setGradientBuffer(colors);
         setBlending(null, true);
         OGLHelpers.drawTriangles(0, points.length / 2, false, false);
         endPolygon();
      }
   }

   private static void beginPolygon(int flags) {
      OGLParameters parameters = smParameters[smParametersIndex];
      float scaleX = parameters.getScaleX();
      float scaleY = parameters.getScaleY();
      if ((flags & 1) != 0) {
         scaleX = -scaleX;
      }

      if ((flags & 2) != 0) {
         scaleY = -scaleY;
      }

      OGLHelpers.pushMatrix();
      OGLHelpers.translate(parameters.getPivotX() * smScreenScaleX, parameters.getPivotY() * smScreenScaleY, 0.0F);
      OGLHelpers.scale(scaleX * smScreenScaleX, scaleY * smScreenScaleY, 1.0F);
      float angle = parameters.getAngle();
      if (angle != 0.0F) {
         OGLHelpers.rotate(angle, 0.0F, 0.0F, 1.0F);
      }
   }

   private static void endPolygon() {
      restoreModifiedColor();
      OGLHelpers.clearBuffers();
      OGLHelpers.popMatrix();
   }

   public static boolean checkPolygonGradientHack(float[] polygon) {
      return polygon != null
         && smPolygonGradientColor != -8355712
         && smColor == smPolygonGradientColor
         && smPolygonGradient != null
         && polygon.length == smPolygonGradient.length / 2;
   }

   public static void setPolygonGradientHack(float[] gradient, int color) {
      smPolygonGradient = gradient;
      smPolygonGradientColor = color;
   }

   public static void resetPolygonGradientHack() {
      smPolygonGradient = null;
      smPolygonGradientColor = -8355712;
   }

   public static void drawTexture(OGLRegion region, float x, float y, int mirror, int anchor) {
      drawTexture(region, x, y, 0, mirror, anchor);
   }

   public static void drawTexture(OGLRegion region, float x, float y, int transform, int mirror, int anchor) {
      if (region != null && region.getTexture() != null) {
         OGLTexture texture = region.getTexture();
         OGLParameters parameters = smParameters[smParametersIndex];
         float pivotX = parameters.getPivotX();
         float pivotY = parameters.getPivotY();
         int clampedX = Math.min(Math.max(region.getRegionX(), 0), texture.getWidth() - 1);
         int clampedY = Math.min(Math.max(region.getRegionY(), 0), texture.getHeight() - 1);
         int clampedWidth = Math.min(Math.max(region.getRegionWidth(), 1), texture.getWidth() - clampedX);
         int clampedHeight = Math.min(Math.max(region.getRegionHeight(), 1), texture.getHeight() - clampedY);
         float regionX = clampedX;
         float regionY = clampedY;
         float regionWidth = clampedWidth;
         float regionHeight = clampedHeight;
         float f8 = regionX + regionWidth;
         float f9 = regionY + regionHeight;
         float scaleX = parameters.getScaleX();
         float scaleY = parameters.getScaleY();
         float f10 = f9;
         float f11 = f8;
         float f12 = regionY;
         float f13 = regionX;
         int i = transform;

         while (i != 0) {
            float f4;
            float f5;
            float f6;
            float f7;
            int i4;
            if ((i & 4) != 0) {
               i4 = i & -5;
               float f14 = f8;
               f5 = regionWidth;
               regionWidth = regionHeight;
               f6 = regionX;
               f7 = f9;
               f9 = f12;
               f12 = regionY;
               f4 = f14;
            } else if ((i & 8) != 0) {
               i4 = i & -9;
               float f15 = regionY;
               f4 = f11;
               f11 = f13;
               f13 = regionX;
               f7 = f10;
               f10 = f15;
               float f16 = regionHeight;
               f6 = f8;
               f5 = f16;
            } else if ((i & 16) != 0) {
               i4 = i & -17;
               float f17 = f12;
               f12 = f9;
               f9 = f10;
               f10 = f17;
               float f18 = regionX;
               f7 = regionY;
               f4 = f13;
               f13 = f8;
               f5 = regionWidth;
               regionWidth = regionHeight;
               f6 = f11;
               f11 = f18;
            } else if ((i & 1) != 0) {
               i4 = i & -2;
               float f19 = f8;
               f5 = regionHeight;
               f6 = f11;
               f11 = regionX;
               f7 = regionY;
               f4 = f19;
               float f20 = f12;
               f12 = f9;
               f9 = f20;
            } else {
               i4 = 0;
               float f21 = f12;
               f12 = regionY;
               f4 = f13;
               f13 = f8;
               f5 = regionHeight;
               f6 = regionX;
               f7 = f9;
               f9 = f10;
               f10 = f21;
            }

            i = i4;
            float f22 = f4;
            regionY = f9;
            f9 = f10;
            f10 = f12;
            f12 = f7;
            regionX = f22;
            float f23 = f5;
            f8 = f11;
            f11 = f13;
            f13 = f6;
            regionHeight = f23;
         }

         if ((mirror & 1) != 0) {
            scaleX = -scaleX;
         }

         if ((mirror & 2) != 0) {
            scaleY = -scaleY;
         }

         float drawX = smScreenScaleX * x * scaleX;
         float drawWidth = scaleX * (regionWidth * smScreenScaleX);
         float drawY = smScreenScaleY * y * scaleY;
         float drawHeight = scaleY * (regionHeight * smScreenScaleY);
         if ((anchor & 1) != 0) {
            drawX -= 0.5F * drawWidth;
         } else if ((anchor & 8) != 0) {
            drawX -= drawWidth;
         }

         if ((anchor & 2) != 0) {
            drawY -= 0.5F * drawHeight;
         } else if ((anchor & 32) != 0) {
            drawY -= drawHeight;
         }

         float angle = parameters.getAngle();
         if (angle == 0.0F) {
            drawX += pivotX * smScreenScaleX;
            drawY += pivotY * smScreenScaleY;
            if (!isVisible(drawX, drawY, drawWidth, drawHeight)) {
               return;
            }
         } else {
            OGLHelpers.pushMatrix();
            OGLHelpers.translate(pivotX * smScreenScaleX, pivotY * smScreenScaleY, 0.0F);
            OGLHelpers.rotate(angle, 0.0F, 0.0F, 1.0F);
         }

         smVertexBuffer[0] = drawX;
         smVertexBuffer[1] = drawY;
         smVertexBuffer[2] = drawX;
         smVertexBuffer[3] = drawY + drawHeight;
         smVertexBuffer[4] = drawX + drawWidth;
         smVertexBuffer[5] = drawY;
         smVertexBuffer[6] = drawX + drawWidth;
         smVertexBuffer[7] = drawY + drawHeight;
         float invWidth = 1.0F / texture.getWidth();
         float invHeight = 1.0F / texture.getHeight();
         smTextureBuffer[0] = f13 * invWidth;
         smTextureBuffer[1] = f12 * invHeight;
         smTextureBuffer[2] = regionX * invWidth;
         smTextureBuffer[3] = f9 * invHeight;
         smTextureBuffer[4] = f11 * invWidth;
         smTextureBuffer[5] = regionY * invHeight;
         smTextureBuffer[6] = f8 * invWidth;
         smTextureBuffer[7] = f10 * invHeight;
         OGLHelpers.setVertexBuffer(smVertexBuffer);
         OGLHelpers.setTextureBuffer(smTextureBuffer);
         setBlending(texture, false);
         OGLHelpers.drawTriangles(0, 4, false, true);
         restoreModifiedColor();
         OGLHelpers.clearBuffers();
         if (angle != 0.0F) {
            OGLHelpers.popMatrix();
         }
      }
   }

   public static void setBlending(OGLTexture texture, boolean forceBlending) {
      OGLHelpers.bindTexture(texture);
      int modification = smParameters[smParametersIndex].getColorModification();
      if (texture == null) {
         if (modification != -8355712) {
            int color = smColor;
            int alpha = clamp((color >>> 24) + (modification >>> 24) - 255);
            int red = clamp((color >> 16 & 0xFF) + (modification >> 16 & 0xFF) - 128);
            int green = clamp((color >> 8 & 0xFF) + (modification >> 8 & 0xFF) - 128);
            int blue = clamp((color & 0xFF) + (modification & 0xFF) - 128);
            if (alpha == 255) {
               OGLHelpers.setBlending(false);
            } else {
               OGLHelpers.setBlending(true);
               OGLHelpers.gl().glBlendFunc(770, 771);
            }

            OGLHelpers.color(red * 0.003921569F, green * 0.003921569F, blue * 0.003921569F, alpha * 0.003921569F);
         } else if ((smColor >>> 24) * 0.003921569F >= 1.0F && !forceBlending) {
            OGLHelpers.setBlending(false);
         } else {
            OGLHelpers.setBlending(true);
            OGLHelpers.gl().glBlendFunc(770, 771);
         }
      } else if (modification == -8355712) {
         if (texture.getColorDepth() == 3) {
            OGLHelpers.setBlending(false);
         } else {
            OGLHelpers.setBlending(true);
            OGLHelpers.gl().glBlendFunc(1, 771);
         }
      } else {
         OGLHelpers.setBlending(true);
         float alpha = (modification >>> 24) * 0.003921569F;
         if ((modification & 16777215) == 8421504) {
            OGLHelpers.setAlphaBlending(alpha);
         } else {
            float red = component(modification >> 16 & 0xFF);
            float green = component(modification >> 8 & 0xFF);
            float blue = component(modification & 0xFF);
            int mode;
            if (red >= 0.0F && green >= 0.0F && blue >= 0.0F) {
               mode = 0;
               smColorBuffer[0] = red;
               smColorBuffer[1] = green;
               smColorBuffer[2] = blue;
            } else if (red <= 0.0F && green <= 0.0F && blue <= 0.0F) {
               mode = 1;
               smColorBuffer[0] = -red;
               smColorBuffer[1] = -green;
               smColorBuffer[2] = -blue;
            } else {
               mode = 2;
               smColorBuffer[0] = red + 0.5F;
               smColorBuffer[1] = green + 0.5F;
               smColorBuffer[2] = blue + 0.5F;
            }

            smColorBuffer[3] = 1.0F;
            OGLHelpers.setColorBuffer(smColorBuffer, mode);
            OGLHelpers.color(1.0F, 1.0F, 1.0F, alpha);
         }
      }
   }

   private static float component(int value) {
      int centered = value - 128;
      return centered == 127 ? 1.0F : centered * 0.0078125F;
   }

   private static int clamp(int value) {
      return value < 0 ? 0 : (value > 255 ? 255 : value);
   }

   private static void restoreModifiedColor() {
      if (smParameters[smParametersIndex].getColorModification() != -8355712) {
         applyPremultiplied(smColor);
      }
   }

   private static void applyPremultiplied(int color) {
      float alpha = (color >>> 24) * 0.003921569F;
      OGLHelpers.color((color >> 16 & 0xFF) * 0.003921569F * alpha, (color >> 8 & 0xFF) * 0.003921569F * alpha, (color & 0xFF) * 0.003921569F * alpha, alpha);
   }

   private static void putPremultiplied(float[] buffer, int offset, int color) {
      float alpha = (color >>> 24) * 0.003921569F;
      buffer[offset] = (color >> 16 & 0xFF) * 0.003921569F * alpha;
      buffer[offset + 1] = (color >> 8 & 0xFF) * 0.003921569F * alpha;
      buffer[offset + 2] = (color & 0xFF) * 0.003921569F * alpha;
      buffer[offset + 3] = alpha;
   }

   public static void setColorARGB(int color) {
      smColor = color;
      applyPremultiplied(color);
   }

   public static void setColor2ARGB(int color) {
      smColor2 = color;
   }

   public static void setColorRGB(int color) {
      smColor = color | 0xFF000000;
      OGLHelpers.color((color >> 16 & 0xFF) * 0.003921569F, (color >> 8 & 0xFF) * 0.003921569F, (color & 0xFF) * 0.003921569F, 1.0F);
   }

   public static OGLParameters getParameters() {
      return smParameters[smParametersIndex];
   }

   public static OGLParameters pushParameters() {
      int next = smParametersIndex + 1;
      if (next >= 64) {
         return null;
      }

      if (smParameters[next] == null) {
         smParameters[next] = new OGLParameters(smParameters[smParametersIndex]);
      } else {
         smParameters[next].set(smParameters[smParametersIndex]);
      }

      smParametersIndex = next;
      return smParameters[next];
   }

   public static void popParameters() {
      if (smParametersIndex > 0) {
         smParametersIndex--;
      }
   }

   public static float getPivotX() {
      return smParameters[smParametersIndex].getPivotX();
   }

   public static float getPivotY() {
      return smParameters[smParametersIndex].getPivotY();
   }

   public static float getScaleX() {
      return smParameters[smParametersIndex].getScaleX();
   }

   public static float getScaleY() {
      return smParameters[smParametersIndex].getScaleY();
   }

   public static float getScreenScaleX() {
      return smScreenScaleX;
   }

   public static float getScreenScaleY() {
      return smScreenScaleY;
   }

   public static void setAlpha(float alpha) {
      smParameters[smParametersIndex].setAlpha(alpha);
   }

   public static void setAngle(float angle) {
      smParameters[smParametersIndex].setAngle(angle);
   }

   public static void setAngleInt(int angle) {
      smParameters[smParametersIndex].setAngleInt(angle);
   }

   public static void setColorModification(int modification) {
      smParameters[smParametersIndex].setColorModification(modification);
   }

   public static void setPivot(float x, float y) {
      smParameters[smParametersIndex].setPivot(x, y);
   }

   public static void setScale(float x, float y) {
      smParameters[smParametersIndex].setScale(x, y);
   }

   public static void setScaleInt(int x, int y) {
      smParameters[smParametersIndex].setScaleInt(x, y);
   }

   public static void setScreenScale(float x, float y) {
      smScreenScaleX = x;
      smScreenScaleY = y;
   }
}
