package zlport.j2me;

import android.content.Context;
import android.util.Log;
import com.dchoc.idead.GameEngine;
import com.dchoc.toolkit.Toolkit;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.lcdui.DCRenderer;
import javax.microedition.lcdui.DCSurfaceView;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import zlport.stubs.NotPorted;

public final class Driver1001 implements GameDriver {
   @Override
   public Renderer createRenderer(Context context, Displayable displayable) {
      return new DCRenderer(context, displayable);
   }

   @Override
   public void doDraw() {
      NotPorted.method("javax.microedition.lcdui.DCSurfaceView#doDraw()");
   }

   @Override
   public void requestRender() {
      WheelZoom.apply(System.nanoTime());
      HoverCollect.apply();
      Platform.surface().requestRender();
   }

   @Override
   public GL graphicsContext() {
      return DCRenderer.getGL();
   }

   @Override
   public boolean graphicsContextReady() {
      return DCRenderer.isGLReady();
   }

   @Override
   public int surfaceHeight() {
      return DCRenderer.getSurfaceHeight();
   }

   @Override
   public boolean touch(DCSurfaceView view, Touch e) {
      return view.dispatchTouch(e);
   }

   @Override
   public boolean keyDown(DCSurfaceView view, int keyCode, char ch) {
      return view.onKeyDown(keyCode, ch);
   }

   @Override
   public boolean keyUp(DCSurfaceView view, int keyCode, char ch) {
      return view.onKeyUp(keyCode, ch);
   }

   @Override
   public void zoom(double notches, float x, float y) {
      WheelZoom.add(notches, x, y);
   }

   @Override
   public void hover(float x, float y) {
      HoverCollect.add(x, y);
   }

   @Override
   public void hoverEnd() {
      HoverCollect.end();
   }

   @Override
   public void pause(MIDlet midlet) {
      Toolkit.pauseMidlet(true);
      Log.e("AJ", "MIDLet$onPause GameEngine.loading " + GameEngine.loading + " GameEngine.reloading " + GameEngine.reloading);
      if (GameEngine.loading || GameEngine.reloading) {
         DCRenderer.mGLReady = false;
         DCRenderer.smUnloadTextures.clear();

         try {
            midlet.notifyDestroyed();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public void destroy() {
      DCRenderer.mGLReady = false;
      DCRenderer.smUnloadTextures.clear();
      DCRenderer.smLoadTextures.clear();
      DCRenderer.smActiveTextures.clear();
   }
}
