package javax.microedition.lcdui;

import javax.microedition.khronos.opengles.GL;

public interface IDCView {
   void createGraphicsContext();

   void destroyGraphicsContext();

   void doDraw();

   void doRequestRender();

   GL getGraphicsContext();

   boolean isGraphicsContextReady();
}
