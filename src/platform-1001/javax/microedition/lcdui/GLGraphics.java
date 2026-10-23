package javax.microedition.lcdui;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;
import javax.microedition.midlet.MIDlet;

public class GLGraphics extends Graphics {
   public static final int RENDER_MODE_QUAD = 0;
   public static final int RENDER_MODE_TEXTURE_EXT = 1;
   public static final int RENDER_MODE_VBO = 2;
   private static GLGraphics instance;
   public static int renderMode = 0;
   private final String STRING_DRAW_TEXTURE_EXT = "GL_OES_draw_texture";
   private final String STRING_VBO_EXTENSION = "GL_ARB_vertex_buffer_object";
   float blue;
   int clipH;
   int clipW = MIDlet.getWidth();
   int clipX;
   int clipY;
   private GL11 gl;
   float green;
   private int mTextureBufferIndex;
   private int mVertexBufferIndex;
   float red;
   private boolean renderModeDecided;

   protected GLGraphics() {
      this.clipH = MIDlet.getHeight();
   }

   private void begin() {
      this.gl.glMatrixMode(5889);
      this.gl.glLoadIdentity();
      this.gl.glOrthof(0.0F, MIDlet.getWidth(), 0.0F, MIDlet.getHeight(), -1.0F, 1.0F);
      this.gl.glMatrixMode(5888);
      this.gl.glLoadIdentity();
      this.gl.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
      this.gl.glTranslatef(0.0F, -MIDlet.getHeight(), 0.0F);
      this.gl.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
      this.gl.glPushMatrix();
   }

   private void createVBOBuffer(GL10 var1) {
      if (this.mVertexBufferIndex == 0 && var1 instanceof GL11) {
         GL11 var3 = (GL11)var1;
         int[] var2 = new int[1];
         var3.glGenBuffers(1, var2, 0);
         this.mVertexBufferIndex = var2[0];
         var3.glGenBuffers(1, var2, 0);
         this.mTextureBufferIndex = var2[0];
      }
   }

   private void end() {
      this.gl.glPopMatrix();
   }

   protected static GLGraphics getInstance() {
      if (instance == null) {
         instance = new GLGraphics();
      }

      return instance;
   }

   private int getNextpowerOf2(int var1) {
      byte var2 = 1;

      while (var2 < var1) {
         var2 <<= 1;
      }

      return var2;
   }

   private void init() {
      this.gl.glViewport(0, 0, MIDlet.getWidth(), MIDlet.getHeight());
      this.gl.glBlendFunc(1, 771);
      this.gl.glEnable(3553);
      this.gl.glEnable(3042);
      this.gl.glEnableClientState(32884);
      this.gl.glEnableClientState(32888);
      this.begin();
   }

   private void setUpGLRenderMode() {
      String var1 = this.gl.glGetString(7939);
      if (var1.indexOf("GL_OES_draw_texture") != -1) {
         renderMode = 1;
      } else if (var1.indexOf("GL_ARB_vertex_buffer_object") != -1) {
         renderMode = 2;
         this.createVBOBuffer(this.gl);
      }
   }

   public void clipRect(int var1, int var2, int var3, int var4) {
      int var5 = this.clipX;
      int var6 = this.clipY;
      if (var1 > this.clipX) {
         var5 = var1;
      }

      if (var2 > this.clipY) {
         var6 = var2;
      }

      if (var1 + var3 < this.clipX + this.clipW) {
         var1 = var1 + var3 - var5;
      } else {
         var1 = this.clipX + this.clipW - var5;
      }

      if (var2 + var4 < this.clipY + this.clipH) {
         var2 = var2 + var4 - var6;
      } else {
         var2 = this.clipY + this.clipH - var6;
      }

      this.setClip(var5, var6, var1, var2);
   }

   public void drawArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      int var16 = var3 / 2;
      int var15 = var4 / 2;
      byte var13 = 1;
      int var12 = var6;
      if (var6 < 0) {
         var13 = -1;
         var12 = -var6;
      }

      float var7 = (float) Math.PI * var5 / 180.0F;
      float[] var18 = new float[var12 << 2];
      float[] var17 = new float[var12 << 3];
      int var14 = 0;
      var6 = 0;

      for (int var19 = 0; var19 <= var12; var19++) {
         float var9 = var1 + var16;
         float var10 = (float)Math.cos(var7) * var3 / 2.0F;
         float var8 = var2 + var15;
         float var11 = var13 * (float)Math.sin(var7) * var4 / 2.0F;
         var18[var14] = var9 + var10;
         var18[++var14] = var8 - var11;
         var14++;
         var17[var6] = this.red;
         var17[++var6] = this.green;
         var17[++var6] = this.blue;
         var17[++var6] = 1.0F;
         var6++;
         var7 += (float) (Math.PI / 180.0);
      }

      this.gl.glDisable(3042);
      this.gl.glDisable(3553);
      this.gl.glDisableClientState(32888);
      this.gl.glEnableClientState(32886);
      this.gl.glVertexPointer(2, 5126, 0, FloatBuffer.wrap(var18));
      this.gl.glColorPointer(4, 5126, 0, FloatBuffer.wrap(var17));
      this.gl.glDrawArrays(3, 0, var14 >> 1);
      this.gl.glDisableClientState(32886);
      this.gl.glEnable(3042);
      this.gl.glEnable(3553);
      this.gl.glEnableClientState(32888);
   }

   public void drawChars(char[] var1, int var2, int var3, int var4, int var5, int var6) {
   }

   public void drawImage(Image var1, int var2, int var3, int var4) {
      var2 = this.getHAdjustForImage(var4, var1.getWidth()) + var2;
      var3 += this.getVAdjustForImage(var4, var1.getHeight());
      float var6 = Math.max(this.clipX, var2);
      float var8 = Math.min(this.clipX + this.clipW, var1.getWidth() + var2);
      float var5 = Math.max(this.clipY, var3);
      float var7 = Math.min(this.clipY + this.clipH, var1.getHeight() + var3);
      if (!(var8 <= var6) && !(var7 <= var5)) {
         switch (renderMode) {
            case 0:
               int var34 = var1.getTexWidth();
               var4 = var1.getTexHeight();
               float var33 = (var6 - var2) / var34;
               float var27 = (var5 - var3) / var4;
               float var31 = (var6 - var2) / var34;
               float var32 = (var7 - var3) / var4;
               float var29 = (var8 - var2) / var34;
               float var28 = (var5 - var3) / var4;
               float var30 = (var8 - var2) / var34;
               float var26 = (var7 - var3) / var4;
               this.gl.glBindTexture(3553, var1.getTexture());
               this.gl.glVertexPointer(2, 5126, 0, FloatBuffer.wrap(new float[]{var6, var5, var6, var7, var8, var5, var8, var7}));
               this.gl.glTexCoordPointer(2, 5126, 0, FloatBuffer.wrap(new float[]{var33, var27, var31, var32, var29, var28, var30, var26}));
               this.gl.glDrawArrays(5, 0, 4);
               break;
            case 1:
               this.gl.glBindTexture(3553, var1.getTexture());
               var7 = MIDlet.getHeight();
               var8 = var1.getHeight();
               ((GL11Ext)this.gl).glDrawTexfOES(var6, var7 - var5 - var8, 0.0F, var1.getWidth(), var1.getHeight());
               break;
            case 2:
               GL11 var18 = this.gl;
               int var17 = var1.getTexWidth();
               var4 = var1.getTexHeight();
               float var15 = (var6 - var2) / var17;
               float var13 = (var5 - var3) / var4;
               float var10 = (var6 - var2) / var17;
               float var12 = (var7 - var3) / var4;
               float var11 = (var8 - var2) / var17;
               float var16 = (var5 - var3) / var4;
               float var14 = (var8 - var2) / var17;
               float var9 = (var7 - var3) / var4;
               if (this.mVertexBufferIndex != 0) {
                  FloatBuffer var19 = FloatBuffer.wrap(new float[]{var6, var5, var6, var7, var8, var5, var8, var7});
                  var18.glBindBuffer(34962, this.mVertexBufferIndex);
                  var18.glBufferData(34962, var19.capacity() * 32, var19, 35044);
                  var18.glVertexPointer(2, 5126, 0, 0);
               }

               if (this.mTextureBufferIndex != 0) {
                  FloatBuffer var35 = FloatBuffer.wrap(new float[]{var15, var13, var10, var12, var11, var16, var14, var9});
                  var18.glBindBuffer(34962, this.mTextureBufferIndex);
                  var18.glBufferData(34962, var35.capacity() * 32, var35, 35044);
                  var18.glBindTexture(3553, var1.getTexture());
                  var18.glTexCoordPointer(2, 5126, 0, 0);
               }

               var18.glDrawArrays(5, 0, 4);
               var18.glBufferData(34962, 0, null, 35044);
               var18.glBindBuffer(34962, 0);
         }
      }
   }

   public void drawLine(int var1, int var2, int var3, int var4) {
      this.gl.glDisableClientState(32888);
      this.gl.glDisable(3553);
      this.gl.glDisable(3042);
      float var6 = var1;
      float var7 = var2;
      float var8 = var3;
      float var5 = var4;
      FloatBuffer var12 = FloatBuffer.allocate(4);
      var12.put(new float[]{var6, var7, var8, var5});
      var7 = this.red;
      float var9 = this.green;
      var6 = this.blue;
      var5 = this.red;
      float var10 = this.green;
      var8 = this.blue;
      FloatBuffer var11 = FloatBuffer.allocate(8);
      var11.put(new float[]{var7, var9, var6, 0.0F, var5, var10, var8, 0.0F});
      this.gl.glEnableClientState(32886);
      this.gl.glVertexPointer(2, 5126, 0, var12);
      this.gl.glColorPointer(4, 5126, 0, var11);
      this.gl.glDrawArrays(1, 0, 2);
      this.gl.glDisableClientState(32886);
      this.gl.glEnableClientState(32888);
      this.gl.glEnable(3553);
      this.gl.glEnable(3042);
   }

   public void drawRGB(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      int[] var26 = new int[1];
      this.gl.glGenTextures(1, var26, 0);
      this.gl.glBindTexture(3553, var26[0]);
      int var22 = this.getNextpowerOf2(var6);
      int var21 = this.getNextpowerOf2(var7);
      int[] var27 = new int[var22 * var21 * 4];

      for (int var28 = 0; var28 < var7; var28++) {
         for (int var30 = 0; var30 < var6; var30++) {
            int var23 = var1[var28 * var6 + var30];
            int var25 = var28 * var22 + var30 << 2;
            int var24 = var25 + 1;
            var27[var25] = var23 >> 16 & 0xFF;
            var25 = var24 + 1;
            var27[var24] = var23 >> 8 & 0xFF;
            var24 = var25 + 1;
            var27[var25] = var23 >> 0 & 0xFF;
            if (var8) {
               var27[var24] = var23 >> 24 & 0xFF;
            } else {
               var27[var24] = 255;
            }
         }
      }

      this.gl.glTexImage2D(3553, 0, 6408, var22, var21, 0, 6408, 5121, IntBuffer.wrap(var27));
      this.gl.glTexParameterx(3553, 10241, 9728);
      var3 = var4 + var6;
      var2 = var5 + var7;
      float var17 = var4;
      float var20 = var5;
      float var11 = var4;
      float var18 = var2;
      float var15 = var3;
      float var16 = var5;
      float var13 = var3;
      float var12 = var2;
      float var9 = 1.0F * var7 / var21;
      float var10 = 1.0F * var6 / var22;
      float var14 = 1.0F * var6 / var22;
      float var19 = 1.0F * var7 / var21;
      this.gl.glVertexPointer(2, 5126, 0, FloatBuffer.wrap(new float[]{var17, var20, var11, var18, var15, var16, var13, var12}));
      this.gl.glTexCoordPointer(2, 5126, 0, FloatBuffer.wrap(new float[]{0.0F, 0.0F, 0.0F, var9, var10, 0.0F, var14, var19}));
      this.gl.glDrawArrays(5, 0, 4);
      this.gl.glDisableClientState(32884);
      this.gl.glDeleteTextures(1, var26, 0);
   }

   public void drawRect(int var1, int var2, int var3, int var4) {
      this.gl.glDisable(3042);
      this.gl.glDisable(3553);
      this.gl.glDisableClientState(32888);
      float var9 = var1;
      float var17 = var2;
      float var8 = var1;
      float var10 = var2 + var4;
      float var12 = var1 + var3;
      float var11 = var2 + var4;
      float var21 = var1 + var3;
      float var19 = var2;
      float var6 = this.red;
      float var13 = this.green;
      float var18 = this.blue;
      float var7 = this.red;
      float var20 = this.green;
      float var5 = this.blue;
      float var14 = this.red;
      float var23 = this.green;
      float var24 = this.blue;
      float var22 = this.red;
      float var16 = this.green;
      float var15 = this.blue;
      this.gl.glEnableClientState(32886);
      this.gl.glVertexPointer(2, 5126, 0, FloatBuffer.wrap(new float[]{var9, var17, var8, var10, var12, var11, var21, var19}));
      this.gl
         .glColorPointer(
            4, 5126, 0, FloatBuffer.wrap(new float[]{var6, var13, var18, 0.0F, var7, var20, var5, 0.0F, var14, var23, var24, 0.0F, var22, var16, var15, 0.0F})
         );
      this.gl.glDrawArrays(2, 0, 4);
      this.gl.glDisableClientState(32886);
      this.gl.glEnable(3042);
      this.gl.glEnable(3553);
      this.gl.glEnableClientState(32888);
   }

   public void drawRegion(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      int var22 = this.getHAdjustForImage(var9, var4);
      var9 = this.getVAdjustForImage(var9, var5);
      float var11 = var5;
      float var21 = var4;
      float var18 = var4;
      float var13 = var5;
      float var12 = (float)var2 / var1.getTexWidth();
      float var16 = (float)var3 / var1.getTexHeight();
      float var17 = (float)var2 / var1.getTexWidth();
      float var14 = (float)(var3 + var5) / var1.getTexHeight();
      float var20 = (float)(var2 + var4) / var1.getTexWidth();
      float var19 = (float)var3 / var1.getTexHeight();
      float var10 = (float)(var2 + var4) / var1.getTexWidth();
      float var15 = (float)(var3 + var5) / var1.getTexHeight();
      this.gl.glPushMatrix();
      this.gl.glTranslatef(var22 + var7, var9 + var8, 0.0F);
      if (var6 != 0) {
         boolean var23;
         if (var6 >= 4) {
            var23 = true;
         } else {
            var23 = false;
         }

         if (var23) {
            this.gl.glTranslatef(var5 * 0.5F, var4 * 0.5F, 0.0F);
         } else {
            this.gl.glTranslatef(var4 * 0.5F, var5 * 0.5F, 0.0F);
         }

         switch (var6) {
            case 1:
               this.gl.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
               this.gl.glScalef(-1.0F, 1.0F, 1.0F);
               break;
            case 2:
               this.gl.glScalef(-1.0F, 1.0F, 1.0F);
               break;
            case 3:
               this.gl.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
               break;
            case 4:
               this.gl.glRotatef(270.0F, 0.0F, 0.0F, 1.0F);
               this.gl.glScalef(-1.0F, 1.0F, 1.0F);
               break;
            case 5:
               this.gl.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
               break;
            case 6:
               this.gl.glRotatef(270.0F, 0.0F, 0.0F, 1.0F);
               break;
            case 7:
               this.gl.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
               this.gl.glScalef(-1.0F, 1.0F, 1.0F);
         }

         this.gl.glTranslatef(-var4 * 0.5F, -var5 * 0.5F, 0.0F);
      }

      this.gl.glBindTexture(3553, var1.getTexture());
      this.gl.glVertexPointer(2, 5126, 0, FloatBuffer.wrap(new float[]{0.0F, 0.0F, 0.0F, var11, var21, 0.0F, var18, var13}));
      this.gl
         .glTexCoordPointer(
            2,
            5126,
            0,
            FloatBuffer.wrap(new float[]{1.0F * var12, 1.0F * var16, 1.0F * var17, 1.0F * var14, 1.0F * var20, 1.0F * var19, 1.0F * var10, 1.0F * var15})
         );
      this.gl.glDrawArrays(5, 0, 4);
      this.gl.glPopMatrix();
   }

   public void drawRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.drawArc(var1 + var3 - var5, var2, var5, var6, 0, 90);
      this.drawArc(var1, var2, var5, var6, 90, 90);
      this.drawArc(var1, var2 + var4 - var6, var5, var6, 180, 90);
      this.drawArc(var1 + var3 - var5, var2 + var4 - var6, var5, var6, 270, 90);
      this.drawLine(var5 / 2 + var1, var2, var1 + var3 - var5 / 2, var2);
      this.drawLine(var1, var6 / 2 + var2, var1, var2 + var4 - var6 / 2);
      this.drawLine(var5 / 2 + var1, var2 + var4, var1 + var3 - var5 / 2, var2 + var4);
      this.drawLine(var1 + var3, var2 + var4 - var6 / 2, var1 + var3, var6 / 2 + var2);
   }

   public void drawString(String var1, int var2, int var3, int var4) {
   }

   public void fillArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      int var13 = var1 + var3 / 2;
      int var14 = var2 + var4 / 2;
      byte var18 = 1;
      var1 = var6;
      if (var6 < 0) {
         var18 = -1;
         var1 = -var6;
      }

      float var7 = (float) Math.PI * var5 / 180.0F;
      var5 = var1 + 1;
      float[] var15 = new float[var5 << 2];
      float[] var16 = new float[var5 << 3];
      var15[0] = var13;
      var15[1] = var14;
      int var12 = 2;
      var16[0] = this.red;
      var16[1] = this.green;
      var16[2] = this.blue;
      var16[3] = 1.0F;
      var6 = 4;

      for (int var20 = 0; var20 <= var1; var20++) {
         float var9 = var13;
         float var8 = (float)Math.cos(var7) * var3 / 2.0F;
         float var10 = var14;
         float var11 = var18 * (float)Math.sin(var7) * var4 / 2.0F;
         var15[var12] = var9 + var8;
         var15[++var12] = var10 - var11;
         var12++;
         var16[var6] = this.red;
         var16[++var6] = this.green;
         var16[++var6] = this.blue;
         var16[++var6] = 1.0F;
         var6++;
         var7 += (float) (Math.PI / 180.0);
      }

      this.gl.glDisable(3042);
      this.gl.glDisable(3553);
      this.gl.glDisableClientState(32888);
      this.gl.glEnableClientState(32886);
      this.gl.glVertexPointer(2, 5126, 0, FloatBuffer.wrap(var15));
      this.gl.glColorPointer(4, 5126, 0, FloatBuffer.wrap(var16));
      this.gl.glDrawArrays(6, 0, var12 >> 1);
      this.gl.glDisableClientState(32886);
      this.gl.glEnable(3042);
      this.gl.glEnable(3553);
      this.gl.glEnableClientState(32888);
   }

   public void fillRect(int var1, int var2, int var3, int var4) {
      this.gl.glDisable(3042);
      this.gl.glDisable(3553);
      this.gl.glDisableClientState(32888);
      float var9 = var1;
      float var24 = var2;
      float var20 = var1;
      float var17 = var2 + var4;
      float var8 = var1 + var3;
      float var12 = var2;
      float var14 = var1 + var3;
      float var15 = var2 + var4;
      float var18 = this.red;
      float var7 = this.green;
      float var22 = this.blue;
      float var19 = this.red;
      float var21 = this.green;
      float var10 = this.blue;
      float var16 = this.red;
      float var13 = this.green;
      float var23 = this.blue;
      float var5 = this.red;
      float var6 = this.green;
      float var11 = this.blue;
      this.gl.glVertexPointer(2, 5126, 0, FloatBuffer.wrap(new float[]{var9, var24, var20, var17, var8, var12, var14, var15}));
      this.gl
         .glColorPointer(
            4, 5126, 0, FloatBuffer.wrap(new float[]{var18, var7, var22, 0.0F, var19, var21, var10, 0.0F, var16, var13, var23, 0.0F, var5, var6, var11, 0.0F})
         );
      this.gl.glEnableClientState(32886);
      this.gl.glDrawArrays(5, 0, 4);
      this.gl.glDisableClientState(32886);
      this.gl.glEnable(3042);
      this.gl.glEnable(3553);
      this.gl.glEnableClientState(32888);
   }

   public void fillRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.fillArc(var1 + var3 - var5, var2, var5, var6, 0, 90);
      this.fillArc(var1, var2, var5, var6, 90, 90);
      this.fillArc(var1, var2 + var4 - var6, var5, var6, 180, 90);
      this.fillArc(var1 + var3 - var5, var2 + var4 - var6, var5, var6, 270, 90);
      this.fillRect(var5 / 2 + var1, var2, var3 - var5, var4);
      this.fillRect(var1, var6 / 2 + var2, var5, var4 - var6);
      this.fillRect(var1 + var3 - var5 / 2, var6 / 2 + var2, var5 / 2, var4 - var6);
   }

   public void fillTriangle(int var1, int var2, int var3, int var4, int var5, int var6) {
      float var18 = var1;
      float var20 = var2;
      float var13 = var3;
      float var19 = var4;
      float var9 = var5;
      float var21 = var6;
      float var15 = this.red;
      float var12 = this.green;
      float var16 = this.blue;
      float var10 = this.red;
      float var11 = this.green;
      float var14 = this.blue;
      float var17 = this.red;
      float var7 = this.green;
      float var8 = this.blue;
      this.gl.glDisable(3042);
      this.gl.glDisable(3553);
      this.gl.glDisableClientState(32888);
      this.gl.glEnableClientState(32886);
      this.gl.glVertexPointer(2, 5126, 0, FloatBuffer.wrap(new float[]{var18, var20, var13, var19, var9, var21}));
      this.gl.glColorPointer(4, 5126, 0, FloatBuffer.wrap(new float[]{var15, var12, var16, 0.0F, var10, var11, var14, 0.0F, var17, var7, var8, 0.0F}));
      this.gl.glDrawArrays(4, 0, 3);
      this.gl.glDisableClientState(32886);
      this.gl.glEnable(3042);
      this.gl.glEnable(3553);
      this.gl.glEnableClientState(32888);
   }

   public int getBlueComponent() {
      return (int)this.blue;
   }

   public int getClipHeight() {
      return this.clipH;
   }

   public int getClipWidth() {
      return this.clipW;
   }

   public int getClipX() {
      return this.clipX;
   }

   public int getClipY() {
      return this.clipY;
   }

   public int getColor() {
      return (int)(this.red * 255.0F) << 16 | (int)(this.green * 255.0F) << 8 | (int)(this.blue * 255.0F) << 0;
   }

   public GL11 getGl() {
      return this.gl;
   }

   public int getGreenComponent() {
      return (int)this.green;
   }

   public int getRedComponent() {
      return (int)this.red;
   }

   public void setClip(int var1, int var2, int var3, int var4) {
      this.clipX = var1;
      this.clipY = var2;
      this.clipW = var3;
      this.clipH = var4;
      this.gl.glScissor(this.clipX, MIDlet.getHeight() - this.clipY - this.clipH, this.clipW, this.clipH);
      this.gl.glEnable(3089);
   }

   public void setColor(int var1) {
      this.setColor(var1 >> 16 & 0xFF, var1 >> 8 & 0xFF, var1 >> 0 & 0xFF);
   }

   public void setColor(int var1, int var2, int var3) {
      this.red = var1 / 255.0F;
      this.green = var2 / 255.0F;
      this.blue = var3 / 255.0F;
   }

   protected void setGl(GL11 var1) {
      this.gl = var1;
      if (!this.renderModeDecided) {
         this.setUpGLRenderMode();
         this.renderModeDecided = true;
      }

      this.init();
   }
}
