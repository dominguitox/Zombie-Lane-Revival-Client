package javax.microedition.khronos.opengles;

import java.nio.Buffer;

public interface GL11 extends GL10 {
   void glBindBuffer(int var1, int var2);

   void glBufferData(int var1, int var2, Buffer var3, int var4);

   void glGenBuffers(int var1, int[] var2, int var3);

   void glTexCoordPointer(int var1, int var2, int var3, int var4);

   void glTexEnvi(int var1, int var2, int var3);

   void glTexParameteri(int var1, int var2, int var3);

   void glVertexPointer(int var1, int var2, int var3, int var4);
}
