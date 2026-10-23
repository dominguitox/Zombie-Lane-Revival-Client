package com.dchoc.toolkit;

import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.lcdui.GLGraphics;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

public class DChocRenderingPlatform implements IRenderingPlatform {
   private static final int COLOR_MODIFICATION_NONE = -8355712;
   private static final float SCALE_UNIT = 9.765625E-4F;
   private static final float COLOR_UNIT = 0.003921569F;
   private static final float ROTATION_UNIT = 0.005493164F;
   private static final int GL_SCISSOR_TEST = 3089;
   private static final int GL_TEXTURE_2D = 3553;
   private static final int GL_TRIANGLE_STRIP = 5;
   private static final int GL_FLOAT = 5126;
   private static final int GL_VERTEX_ARRAY = 32884;
   private static final int EFFECT_COLOR_MODIFICATION = 0;
   private static final int EFFECT_FILTERS = 1;
   private static final int EFFECT_ROTATION = 2;
   private static final int EFFECT_SCALE_WIDTH = 3;
   private static final int EFFECT_SCALE_HEIGHT = 4;
   private static final int EFFECT_PIVOT_X = 5;
   private static final int EFFECT_PIVOT_Y = 6;
   private static final int EFFECT_RENDER_MODE = 7;
   private static final int EFFECT_BLUR_WIDTH = 8;
   private static final int EFFECT_BLUR_HEIGHT = 9;
   private static final int EFFECT_SIZE = 10;
   static final DChocRenderingPlatform.Surface PC_SURFACE = new DChocRenderingPlatform.Surface() {
      @Override
      public GL11 gl(Graphics graphics) {
         return ((GLGraphics)graphics).getGl();
      }

      @Override
      public int width(Graphics graphics) {
         return MIDlet.getWidth();
      }

      @Override
      public int height(Graphics graphics) {
         return MIDlet.getHeight();
      }
   };
   static DChocRenderingPlatform.Surface smSurface = PC_SURFACE;
   private static int smStackSize;
   private static int smStackCount;
   private static Graphics[] smContextStack;
   private static int[][] smEffectStack;
   private static float smScaleX;
   private static float smScaleY;
   private static float smColorA;
   private static float smColorR;
   private static float smColorG;
   private static float smColorB;
   private static boolean smClipping;
   private static boolean smGlScissorsEnabled;
   private static final float[] smVertices = new float[8];
   private int[] mEffects;
   private Graphics mGraphicsContext;
   private int mColor;
   private int mSavedColor;

   public DChocRenderingPlatform() {
      smStackSize = 8;
      smStackCount = 0;
      smContextStack = new Graphics[smStackSize];
      smEffectStack = new int[smStackSize][];

      for (int i = 0; i < smStackSize; i++) {
         smEffectStack[i] = new int[10];
      }

      this.mEffects = smEffectStack[0];
      this.disableAllEffects();
   }

   public void setGraphicsContext(Graphics graphics) {
      this.mGraphicsContext = graphics;
   }

   public void setGraphicsContext(DChocImage image) {
      if (image.isMutable()) {
         this.setGraphicsContext(image.getGraphics());
      } else {
         this.mGraphicsContext = null;
      }
   }

   public Graphics getGraphicsContext() {
      return this.mGraphicsContext;
   }

   public void platformBeginFrame(Graphics graphics) {
   }

   public void platformEndFrame() {
   }

   public void disableAllEffects() {
      this.setColorModification(-8355712);
      this.setRotation(0);
      this.setScale(1024, 1024);
      this.setRenderMode(0);
      this.setBlur(0, 0);
      this.setFilters(0);
   }

   public void setColorModification(int alpha, int red, int green, int blue) {
      this.setColorModification(alpha << 24 | ((red >> 1) + 128 & 0xFF) << 16 | ((green >> 1) + 128 & 0xFF) << 8 | (blue >>> 1) + 128 & 0xFF);
   }

   public void setColorModification(int modification) {
      this.mEffects[0] = modification;
   }

   public int getColorModification() {
      return this.mEffects[0];
   }

   public void setRotation(int rotation) {
      this.mEffects[2] = rotation;
   }

   public int getRotation() {
      return this.mEffects[2];
   }

   public void setScale(int width, int height) {
      this.mEffects[3] = width;
      this.mEffects[4] = height;
      smScaleY = height * 9.765625E-4F;
      smScaleX = width * 9.765625E-4F;
   }

   public int getScaleWidth() {
      return this.mEffects[3];
   }

   public int getScaleHeight() {
      return this.mEffects[4];
   }

   public void setPivot(int x, int y) {
      this.mEffects[5] = x;
      this.mEffects[6] = y;
   }

   public int getPivotX() {
      return this.mEffects[5];
   }

   public int getPivotY() {
      return this.mEffects[6];
   }

   public void setRenderMode(int mode) {
      this.mEffects[7] = mode;
   }

   public int getRenderMode() {
      return this.mEffects[7];
   }

   public void setFiltering(int filter, boolean enabled) {
      if (enabled) {
         this.mEffects[1] = this.mEffects[1] | filter;
      } else {
         this.mEffects[1] = this.mEffects[1] & ~filter;
      }
   }

   public int getFilters() {
      return this.mEffects[1];
   }

   public void setFilters(int filters) {
      this.mEffects[1] = 0;
      this.setFiltering(filters, true);
   }

   public boolean isFilterEnabled(int filter) {
      return (this.getFilters() & filter) == filter;
   }

   public void setBlur(int width, int height) {
      this.mEffects[8] = width < 0 ? 0 : width;
      this.mEffects[9] = height < 0 ? 0 : height;
   }

   public int getBlurWidth() {
      return this.mEffects[8];
   }

   public int getBlurHeight() {
      return this.mEffects[9];
   }

   public boolean isFrameUpToDate(DChocImage image) {
      return false;
   }

   public void pushParameters() {
      if (smStackCount == smStackSize - 1) {
         int oldSize = smStackSize;
         smStackSize = oldSize << 1;
         Graphics[] contexts = new Graphics[smStackSize];
         System.arraycopy(smContextStack, 0, contexts, 0, oldSize);
         smContextStack = contexts;
         int[][] effects = new int[smStackSize][];
         System.arraycopy(smEffectStack, 0, effects, 0, oldSize);

         for (int i = oldSize; i < smStackSize; i++) {
            effects[i] = new int[10];
         }

         smEffectStack = effects;
      }

      smContextStack[smStackCount] = this.mGraphicsContext;
      int[] next = smEffectStack[smStackCount + 1];
      System.arraycopy(this.mEffects, 0, next, 0, 10);
      smStackCount++;
      this.mEffects = next;
   }

   public void popParameters() {
      smStackCount--;
      this.mEffects = smEffectStack[smStackCount];
      this.mGraphicsContext = smContextStack[smStackCount];
      this.setScale(this.getScaleWidth(), this.getScaleHeight());
   }

   public void setColor(int rgb) {
      this.setColorARGB(rgb | 0xFF000000);
   }

   public void setColor(int alpha, int red, int green, int blue) {
      this.setColorARGB(alpha << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF);
   }

   public int getColor() {
      return this.mColor;
   }

   public void setColorARGB(int argb) {
      this.mColor = argb;
      int alpha = argb >>> 24;
      int red = argb >>> 16 & 0xFF;
      int green = argb >>> 8 & 0xFF;
      int blue = argb & 0xFF;
      setContextColor(this.mGraphicsContext, alpha, red, green, blue);
      smColorA = alpha * 0.003921569F;
      float factor = smColorA * 0.003921569F;
      smColorB = blue * factor;
      smColorG = green * factor;
      smColorR = red * factor;
   }

   public void setColorKey(int color) {
   }

   private void setModifiedColor() {
      int modification = this.getColorModification();
      if (modification != -8355712) {
         int color = this.mColor;
         this.mSavedColor = color;
         int delta = (modification << 1 & 33423360) - 16777216;
         if (delta == 16646144) {
            delta = 16711680;
         }

         int red = clamp((color & 0xFF0000) + delta);
         delta = (modification << 1 & 130560) - 65536;
         if (delta == 65024) {
            delta = 65280;
         }

         int green = clamp((color & 0xFF00) + delta);
         delta = (modification << 1 & 510) - 256;
         if (delta == 254) {
            delta = 255;
         }

         int blue = clamp((color & 0xFF) + delta);
         this.setColorARGB(red | color & 0xFF000000 | green | blue);
      }
   }

   private static int clamp(int value) {
      if (value < 0) {
         value = 0;
      }

      if (value > 16711680) {
         value = 16711680;
      }

      return value;
   }

   public void setClip(int x, int y, int width, int height) {
      setContextClip(this.mGraphicsContext, x, y, width, height);
   }

   public void clipRect(int x, int y, int width, int height) {
      Graphics graphics = this.mGraphicsContext;
      int clipY = graphics.getClipY();
      int clipX = graphics.getClipX();
      int bottom = graphics.getClipHeight() + clipY;
      if (height + y < bottom) {
         bottom = height + y;
      }

      if (clipY < y) {
         clipY = y;
      }

      int right = graphics.getClipWidth() + clipX;
      if (width + x < right) {
         right = width + x;
      }

      if (clipX < x) {
         clipX = x;
      }

      setContextClip(graphics, clipX, clipY, right - clipX, bottom - clipY);
   }

   public int getClipX() {
      return this.mGraphicsContext.getClipX();
   }

   public int getClipY() {
      return this.mGraphicsContext.getClipY();
   }

   public int getClipWidth() {
      return this.mGraphicsContext.getClipWidth();
   }

   public int getClipHeight() {
      return this.mGraphicsContext.getClipHeight();
   }

   public void fillRect(int x, int y, int width, int height) {
      if (smScaleX > 0.0F && smScaleY > 0.0F) {
         Graphics graphics = this.mGraphicsContext;
         int pivotX = this.getPivotX();
         int pivotY = this.getPivotY();
         float scaledY = y * smScaleY;
         float scaledX = x * smScaleX;
         float scaledWidth = width * smScaleX;
         float scaledHeight = height * smScaleY;
         int rotation = this.getRotation();
         GL11 gl = smSurface.gl(graphics);
         float left;
         float top;
         if (rotation == 0) {
            float px = pivotX + scaledX;
            float py = pivotY + scaledY;
            if (!inClipArea(graphics, (int)px, (int)py, (int)scaledWidth, (int)scaledHeight)) {
               return;
            }

            int clipX = this.getClipX();
            int clipY = this.getClipY();
            int clipWidth = this.getClipWidth();
            int clipHeight = this.getClipHeight();
            float clipLeft = clipX;
            if (px < clipLeft) {
               scaledWidth -= clipLeft - px;
               left = clipLeft;
            } else {
               left = px;
            }

            float clipTop = clipY;
            float right = scaledWidth + left;
            if (py < clipTop) {
               scaledHeight -= clipTop - py;
               top = clipTop;
            } else {
               top = py;
            }

            float clipRight = clipWidth + clipX;
            if (right > clipRight) {
               scaledWidth -= right - clipRight;
            }

            float bottom = scaledHeight + top;
            float clipBottom = clipHeight + clipY;
            if (bottom > clipBottom) {
               scaledHeight -= bottom - clipBottom;
            }
         } else {
            enableGlScissors(graphics, gl);
            gl.glPushMatrix();
            gl.glTranslatef(pivotX, pivotY, 0.0F);
            gl.glRotatef(rotation * 0.005493164F, 0.0F, 0.0F, 1.0F);
            left = scaledX;
            top = scaledY;
         }

         float right = scaledWidth + left;
         float bottom = scaledHeight + top;
         smVertices[0] = left;
         smVertices[1] = top;
         smVertices[2] = left;
         smVertices[3] = bottom;
         smVertices[4] = right;
         smVertices[5] = top;
         smVertices[6] = right;
         smVertices[7] = bottom;
         this.setModifiedColor();
         gl.glDisable(3553);
         gl.glEnableClientState(32884);
         gl.glVertexPointer(2, 5126, 0, FloatBuffer.wrap(smVertices));
         gl.glDrawArrays(5, 0, 4);
         gl.glEnable(3553);
         if (this.getColorModification() != -8355712) {
            this.setColorARGB(this.mSavedColor);
         }

         if (rotation != 0) {
            gl.glPopMatrix();
         }
      }
   }

   public void drawRect(int x, int y, int width, int height) {
      this.fillRect(x, y, width, 1);
      this.fillRect(x, height + y, width, 1);
      this.fillRect(x, y + 1, 1, height - 1);
      this.fillRect(width + x, y, 1, height + 1);
   }

   private static void setContextColor(Graphics graphics, int alpha, int red, int green, int blue) {
      graphics.setColor(red, green, blue);
      float alphaFloat = alpha * 0.003921569F;
      float factor = alphaFloat * 0.003921569F;
      smSurface.gl(graphics).glColor4f(red * factor, green * factor, blue * factor, alphaFloat);
   }

   private static void setContextClip(Graphics graphics, int x, int y, int width, int height) {
      int screenWidth = smSurface.width(graphics);
      int clipWidth;
      if (x < screenWidth) {
         int right = width + x;
         if (right > screenWidth) {
            right = screenWidth;
         }

         if (x < 0) {
            x = 0;
         }

         clipWidth = right - x;
         if (clipWidth < 1) {
            height = 0;
            clipWidth = 0;
         }
      } else {
         x = screenWidth - 1;
         clipWidth = 0;
         height = 0;
      }

      int screenHeight = smSurface.height(graphics);
      int clipHeight;
      if (y < screenHeight) {
         int bottom = height + y;
         if (bottom > screenHeight) {
            bottom = screenHeight;
         }

         if (y < 0) {
            y = 0;
         }

         clipHeight = bottom - y;
         if (clipHeight < 1) {
            clipWidth = 0;
            clipHeight = 0;
         }
      } else {
         x = screenHeight - 1;
         clipWidth = 0;
         clipHeight = 0;
      }

      smClipping = true;
      if (x == 0 && y == 0 && clipWidth == screenWidth && graphics.getClipHeight() == clipHeight) {
         smClipping = false;
      }

      graphics.setClip(x, y, clipWidth, clipHeight);
      smSurface.gl(graphics).glDisable(3089);
      smGlScissorsEnabled = false;
   }

   private static boolean inClipArea(Graphics graphics, int x, int y, int width, int height) {
      int clipX = graphics.getClipX();
      int clipY = graphics.getClipY();
      return x < graphics.getClipWidth() + clipX && y < graphics.getClipHeight() + clipY && clipX < width + x && clipY < height + y;
   }

   private static void enableGlScissors(Graphics graphics, GL11 gl) {
      if (smClipping && !smGlScissorsEnabled) {
         smGlScissorsEnabled = true;
         int clipX = graphics.getClipX();
         int clipY = graphics.getClipY();
         int clipHeight = graphics.getClipHeight();
         gl.glEnable(3089);
         gl.glScissor(clipX, smSurface.height(graphics) - clipY - clipHeight, graphics.getClipWidth(), clipHeight);
      }
   }

   public void drawImage(DChocImage image, int x, int y, int anchor, int transform) {
      throw new UnsupportedOperationException("sem uso na 1.4.4: DChocRenderingPlatform.drawImage");
   }

   public void drawLine(int x1, int y1, int x2, int y2) {
      throw new UnsupportedOperationException("sem uso na 1.4.4: DChocRenderingPlatform.drawLine");
   }

   public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
      throw new UnsupportedOperationException("sem uso na 1.4.4: DChocRenderingPlatform.fillTriangle");
   }

   public void fillPolygon(int[] points, int offset, int count) {
      throw new UnsupportedOperationException("sem uso na 1.4.4: DChocRenderingPlatform.fillPolygon");
   }

   public void fillBezier(int[] points, int offset, int count) {
      throw new UnsupportedOperationException("sem uso na 1.4.4: DChocRenderingPlatform.fillBezier");
   }

   public void fillEllipse(int x, int y, int width, int height) {
      throw new UnsupportedOperationException("sem uso na 1.4.4: DChocRenderingPlatform.fillEllipse");
   }

   public void makeImageTransparent(DChocImage image, int width, int height) {
      throw new UnsupportedOperationException("sem uso na 1.4.4: DChocRenderingPlatform.makeImageTransparent");
   }

   interface Surface {
      GL11 gl(Graphics var1);

      int width(Graphics var1);

      int height(Graphics var1);
   }
}
