package zlport.j2me;

import android.content.Context;
import java.io.IOException;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.lcdui.DCSurfaceView;
import javax.microedition.lcdui.Displayable;
import javax.microedition.media.Player;
import javax.microedition.midlet.MIDlet;
import zlport.stubs.NotPorted;

public interface GameDriver {
   Renderer createRenderer(Context var1, Displayable var2);

   void doDraw();

   void requestRender();

   GL graphicsContext();

   boolean graphicsContextReady();

   int surfaceHeight();

   boolean touch(DCSurfaceView var1, Touch var2);

   boolean keyDown(DCSurfaceView var1, int var2, char var3);

   boolean keyUp(DCSurfaceView var1, int var2, char var3);

   void zoom(double var1, float var3, float var4);

   void hover(float var1, float var2);

   void hoverEnd();

   void pause(MIDlet var1);

   void destroy();

   default Player createPlayer(String locator) throws IOException {
      NotPorted.method("javax.microedition.media.BasicPlayer#<init>(String)");
      return null;
   }
}
