package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public abstract class GameCanvas extends Canvas {
   protected GameCanvas(boolean var1) {
   }

   public void flushGraphics() {
      throw new RuntimeException("Method not supported");
   }

   public void flushGraphics(int var1, int var2, int var3, int var4) {
      throw new RuntimeException("Method not supported");
   }

   protected Graphics getGraphics() {
      throw new RuntimeException("Method not supported");
   }

   public void setFullScreenMode(boolean var1) {
   }
}
