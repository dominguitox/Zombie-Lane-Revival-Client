package zlport.j2me;

import javax.microedition.khronos.opengles.GL10;

public interface Renderer {
   void onSurfaceCreated(GL10 var1);

   void onSurfaceChanged(GL10 var1, int var2, int var3);

   void onDrawFrame(GL10 var1);

   void pumpTextures(GL10 var1);
}
