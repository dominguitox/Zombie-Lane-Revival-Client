package com.dchoc.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL11;

public class OGLHelpers {
   public static final int BLEND_MODE_ADD = 1;
   public static final int BLEND_MODE_ALPHA = 3;
   public static final int BLEND_MODE_DEFAULT = 0;
   public static final int BLEND_MODE_SUB = 2;
   public static final int COLOR_MODE_ADD = 0;
   public static final int COLOR_MODE_ADD_SUB = 2;
   public static final int COLOR_MODE_SUB = 1;
   private static final int GL_LINE_STRIP = 3;
   private static final int GL_TRIANGLES = 4;
   private static final int GL_TRIANGLE_STRIP = 5;
   private static final int GL_TRIANGLE_FAN = 6;
   private static final int GL_ONE = 1;
   private static final int GL_SRC_ALPHA = 770;
   private static final int GL_ONE_MINUS_SRC_ALPHA = 771;
   private static final int GL_BLEND = 3042;
   private static final int GL_TEXTURE_2D = 3553;
   private static final int GL_FLOAT = 5126;
   private static final int GL_VERTEX_ARRAY = 32884;
   private static final int GL_COLOR_ARRAY = 32886;
   private static final int GL_TEXTURE_COORD_ARRAY = 32888;
   private static final int GL_TEXTURE_ENV = 8960;
   private static final int GL_TEXTURE_ENV_MODE = 8704;
   private static final int GL_TEXTURE_ENV_COLOR = 8705;
   private static final int GL_MODULATE = 8448;
   private static final int GL_REPLACE = 7681;
   private static final int GL_COMBINE = 34160;
   private static final int GL_COMBINE_RGB = 34161;
   private static final int GL_SRC1_RGB = 34177;
   private static final int GL_OPERAND1_RGB = 34193;
   private static final int GL_CONSTANT = 34166;
   private static final int GL_SRC_COLOR = 768;
   private static final int GL_ADD = 260;
   private static final int GL_SUBTRACT = 34023;
   private static final int GL_ADD_SIGNED = 34164;
   private static boolean smColorBufferEnabled = false;
   private static boolean smGradientBufferEnabled = false;
   private static boolean smTextureBufferEnabled = false;
   private static boolean smVertexBufferEnabled = false;
   private static FloatBuffer smNativeVertices = createFloatBuffer(512);
   private static FloatBuffer smNativeCoordinates = createFloatBuffer(32);
   private static FloatBuffer smNativeColors = createFloatBuffer(32);
   private static FloatBuffer smNativeGradients = createFloatBuffer(16);
   private static GL11 smGL;

   public static void setGL(GL11 gl) {
      smGL = gl;
   }

   static GL11 gl() {
      GL11 gl = smGL;
      if (gl == null) {
         throw new IllegalStateException("OGLHelpers: nenhum contexto de OpenGL (setGL não foi chamado)");
      } else {
         return gl;
      }
   }

   public static void bindTexture(OGLTexture texture) {
      if (texture != null) {
         gl().glEnable(3553);
         gl().glBindTexture(3553, texture.getID());
      } else {
         gl().glDisable(3553);
      }
   }

   public static void clearBuffers() {
      if (smVertexBufferEnabled) {
         gl().glDisableClientState(32884);
         smVertexBufferEnabled = false;
      }

      if (smTextureBufferEnabled) {
         gl().glDisableClientState(32888);
         smTextureBufferEnabled = false;
      }

      if (smColorBufferEnabled) {
         gl().glTexEnvi(8960, 8704, 7681);
         smColorBufferEnabled = false;
      }

      if (smGradientBufferEnabled) {
         gl().glDisableClientState(32886);
         smGradientBufferEnabled = false;
      }
   }

   public static void color(float red, float green, float blue, float alpha) {
      gl().glColor4f(red, green, blue, alpha);
   }

   public static void drawLines(int first, int count, boolean unused) {
      gl().glDrawArrays(3, first, count);
   }

   public static void drawTriangles(int first, int count, boolean fan, boolean strip) {
      int mode = fan ? 6 : (strip ? 5 : 4);
      gl().glDrawArrays(mode, first, count);
   }

   public static void lineWidth(float width) {
      gl().glLineWidth(width);
   }

   public static void popMatrix() {
      gl().glPopMatrix();
   }

   public static void pushMatrix() {
      gl().glPushMatrix();
   }

   public static void rotate(float angle, float x, float y, float z) {
      gl().glRotatef(angle, x, y, z);
   }

   public static void scale(float x, float y, float z) {
      gl().glScalef(x, y, z);
   }

   public static void setAlphaBlending(float alpha) {
      gl().glBlendFunc(1, 771);
      gl().glTexEnvi(8960, 8704, 8448);
      gl().glColor4f(alpha, alpha, alpha, alpha);
      smColorBufferEnabled = true;
   }

   public static void setBlending(boolean enabled) {
      if (enabled) {
         gl().glEnable(3042);
      } else {
         gl().glDisable(3042);
      }
   }

   public static void setColorBuffer(float[] colors, int mode) {
      smNativeColors = fill(smNativeColors, colors);
      gl().glTexEnvi(8960, 8704, 34160);
      gl().glTexEnvi(8960, 34177, 34166);
      gl().glTexEnvi(8960, 34193, 768);
      gl().glTexEnvfv(8960, 8705, smNativeColors);
      if (mode == 2) {
         gl().glTexEnvi(8960, 34161, 34164);
      } else if (mode == 1) {
         gl().glTexEnvi(8960, 34161, 34023);
      } else if (mode == 0) {
         gl().glTexEnvi(8960, 34161, 260);
      }

      gl().glBlendFunc(770, 771);
      smColorBufferEnabled = true;
   }

   public static void setGradientBuffer(float[] colors) {
      smNativeGradients = fill(smNativeGradients, colors);
      gl().glEnableClientState(32886);
      gl().glColorPointer(4, 5126, 0, smNativeGradients);
      smGradientBufferEnabled = true;
   }

   public static void setTextureBuffer(float[] coordinates) {
      smNativeCoordinates = fill(smNativeCoordinates, coordinates);
      gl().glEnableClientState(32888);
      gl().glTexCoordPointer(2, 5126, 0, smNativeCoordinates);
      smTextureBufferEnabled = true;
   }

   public static void setVertexBuffer(float[] vertices) {
      smNativeVertices = fill(smNativeVertices, vertices);
      gl().glEnableClientState(32884);
      gl().glVertexPointer(2, 5126, 0, smNativeVertices);
      smVertexBufferEnabled = true;
   }

   public static void translate(float x, float y, float z) {
      gl().glTranslatef(x, y, z);
   }

   private static FloatBuffer createFloatBuffer(int capacity) {
      return ByteBuffer.allocateDirect(capacity * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
   }

   private static FloatBuffer fill(FloatBuffer buffer, float[] values) {
      FloatBuffer target = buffer.capacity() >= values.length ? buffer : createFloatBuffer(values.length);
      target.clear();
      target.put(values);
      target.flip();
      return target;
   }
}
