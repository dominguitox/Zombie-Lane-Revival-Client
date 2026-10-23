package javax.microedition.khronos.opengles;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface GL10 extends GL {
   void glActiveTexture(int var1);

   void glBindTexture(int var1, int var2);

   void glBlendFunc(int var1, int var2);

   void glClear(int var1);

   void glClearColor(float var1, float var2, float var3, float var4);

   void glColor4f(float var1, float var2, float var3, float var4);

   void glColorPointer(int var1, int var2, int var3, Buffer var4);

   void glDeleteTextures(int var1, int[] var2, int var3);

   void glDeleteTextures(int var1, IntBuffer var2);

   void glDisable(int var1);

   void glDisableClientState(int var1);

   void glDrawArrays(int var1, int var2, int var3);

   void glEnable(int var1);

   void glEnableClientState(int var1);

   void glGenTextures(int var1, int[] var2, int var3);

   void glGenTextures(int var1, IntBuffer var2);

   int glGetError();

   String glGetString(int var1);

   void glHint(int var1, int var2);

   void glLoadIdentity();

   void glLineWidth(float var1);

   void glMatrixMode(int var1);

   void glOrthof(float var1, float var2, float var3, float var4, float var5, float var6);

   void glPopMatrix();

   void glPushMatrix();

   void glRotatef(float var1, float var2, float var3, float var4);

   void glScalef(float var1, float var2, float var3);

   void glScissor(int var1, int var2, int var3, int var4);

   void glTexCoordPointer(int var1, int var2, int var3, Buffer var4);

   void glTexEnvfv(int var1, int var2, FloatBuffer var3);

   void glTexImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, Buffer var9);

   void glTexParameterx(int var1, int var2, int var3);

   void glTranslatef(float var1, float var2, float var3);

   void glVertexPointer(int var1, int var2, int var3, Buffer var4);

   void glViewport(int var1, int var2, int var3, int var4);
}
