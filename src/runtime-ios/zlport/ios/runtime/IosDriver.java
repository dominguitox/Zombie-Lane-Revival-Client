package zlport.ios.runtime;

import android.content.Context;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.lcdui.DCSurfaceView;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.IosRenderer;
import javax.microedition.media.Player;
import javax.microedition.midlet.MIDlet;
import zlport.j2me.GameDriver;
import zlport.j2me.Platform;
import zlport.j2me.Renderer;
import zlport.j2me.Touch;

public final class IosDriver implements GameDriver {
   private volatile IosRenderer renderer;

   public Renderer createRenderer(Context context, Displayable displayable) {
      IosRenderer next = new IosRenderer(displayable);
      this.renderer = next;
      return next;
   }

   public void doDraw() {
      IosRenderer current = this.renderer;
      if (current == null) {
         throw new IllegalStateException("O repaint precisa de uma tela ligada à superfície");
      }

      current.doDraw();
   }

   public void requestRender() {
      Platform.surface().requestRender();
   }

   public GL11 graphicsContext() {
      IosRenderer current = this.renderer;
      return current == null ? null : current.graphicsContext();
   }

   public boolean graphicsContextReady() {
      return this.graphicsContext() != null;
   }

   public int surfaceHeight() {
      IosRenderer current = this.renderer;
      return current == null ? 0 : current.surfaceHeight();
   }

   public boolean touch(DCSurfaceView view, Touch event) {
      IosRenderer current = this.renderer;
      return current != null && current.touch(event);
   }

   public boolean keyDown(DCSurfaceView view, int keyCode, char character) {
      return false;
   }

   public boolean keyUp(DCSurfaceView view, int keyCode, char character) {
      return false;
   }

   public void zoom(double notches, float x, float y) {
      IosWheelZoom.add(notches, x, y);
      Platform.surface().requestRender();
   }

   public void hover(float x, float y) {
      IosHoverCollect.add(x, y);
      Platform.surface().requestRender();
   }

   public void hoverEnd() {
      IosHoverCollect.end();
      Platform.surface().requestRender();
   }

   public void pause(MIDlet midlet) {
   }

   public void destroy() {
   }

   public Player createPlayer(String locator) {
      return (Player)(!locator.isEmpty() && locator.charAt(locator.length() - 1) == '3' ? new IosMusicPlayer(locator) : new IosEffectPlayer(locator));
   }

   public static GL11 currentGl() {
      return IosRenderer.currentGl();
   }
}
