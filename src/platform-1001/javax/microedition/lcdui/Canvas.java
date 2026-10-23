package javax.microedition.lcdui;

public abstract class Canvas extends Displayable {
   public static final int DOWN = 20;
   public static final int EVENT_KEY_PRESSED = 0;
   public static final int EVENT_KEY_RELEASED = 1;
   public static final int EVENT_KEY_REPEATED = 2;
   public static final int FIRE = 23;
   public static final int GAME_A = 29;
   public static final int GAME_B = 30;
   public static final int GAME_C = 31;
   public static final int GAME_D = 32;
   public static final int KEY_NUM0 = 7;
   public static final int KEY_NUM1 = 8;
   public static final int KEY_NUM2 = 9;
   public static final int KEY_NUM3 = 10;
   public static final int KEY_NUM4 = 11;
   public static final int KEY_NUM5 = 12;
   public static final int KEY_NUM6 = 13;
   public static final int KEY_NUM7 = 14;
   public static final int KEY_NUM8 = 15;
   public static final int KEY_NUM9 = 16;
   public static final int KEY_POUND = 18;
   public static final int KEY_STAR = 17;
   public static final int LEFT = 21;
   public static final int RIGHT = 22;
   public static final int UP = 19;

   public int getGameAction(int var1) {
      int var2 = var1;
      switch (var1) {
         case 9:
         case 19:
            var2 = 19;
         case 10:
         case 14:
         case 16:
         case 17:
         case 18:
            break;
         case 11:
         case 21:
            var2 = 21;
            break;
         case 12:
         case 23:
            var2 = 23;
            break;
         case 13:
         case 22:
            var2 = 22;
            break;
         case 15:
         case 20:
            var2 = 20;
            break;
         default:
            var2 = var1;
      }

      return var2;
   }

   public final boolean isGLReady() {
      boolean var1;
      if (view != null) {
         var1 = view.isGraphicsContextReady();
      } else {
         var1 = false;
      }

      return var1;
   }

   protected void keyEventWithChars(int var1, int var2, char[] var3) {
   }

   protected void keyPressed(int var1) {
   }

   protected void keyReleased(int var1) {
   }

   protected void keyRepeated(int var1) {
   }

   public final boolean makeGl() {
      boolean var1;
      if (view != null) {
         view.createGraphicsContext();
         var1 = true;
      } else {
         var1 = false;
      }

      return var1;
   }

   protected void pointerDragged(int var1, int var2) {
   }

   protected void pointerPressed(int var1, int var2) {
   }

   protected void pointerReleased(int var1, int var2) {
   }

   public final void releaseGL() {
      if (view != null) {
         view.destroyGraphicsContext();
      }
   }

   public final void repaint() {
      if (view != null) {
         view.doDraw();
      }
   }

   public final void serviceRepaints() {
   }
}
