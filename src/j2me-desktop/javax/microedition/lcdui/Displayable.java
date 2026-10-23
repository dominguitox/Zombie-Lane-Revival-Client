package javax.microedition.lcdui;

import javax.microedition.midlet.MIDlet;

public abstract class Displayable {
   protected static DCSurfaceView view;

   public static void destroyView() {
      view = null;
   }

   public static IDCView getView() {
      return view;
   }

   public int getHeight() {
      return MIDlet.getHeight();
   }

   public int getWidth() {
      return MIDlet.getWidth();
   }

   protected abstract void keyEventWithChars(int var1, int var2, char[] var3);

   protected abstract void keyPressed(int var1);

   protected abstract void keyReleased(int var1);

   protected abstract void keyRepeated(int var1);

   protected abstract void paint(GLGraphics var1);

   protected abstract void pointerDragged(int var1, int var2);

   protected abstract void pointerPressed(int var1, int var2);

   protected abstract void pointerReleased(int var1, int var2);

   protected void setView(MIDlet midlet) {
      view = new DCSurfaceView(midlet, this);
   }
}
