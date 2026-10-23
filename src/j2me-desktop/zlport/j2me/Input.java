package zlport.j2me;

import javax.microedition.lcdui.DCSurfaceView;
import javax.microedition.lcdui.Displayable;

public final class Input {
   private Input() {
   }

   public static boolean touch(Touch e) {
      DCSurfaceView v = view();
      return v != null && Platform.driver().touch(v, e);
   }

   public static boolean keyDown(int keyCode, char ch) {
      DCSurfaceView v = view();
      return v != null && Platform.driver().keyDown(v, keyCode, ch);
   }

   public static boolean keyUp(int keyCode, char ch) {
      DCSurfaceView v = view();
      return v != null && Platform.driver().keyUp(v, keyCode, ch);
   }

   public static void zoom(double notches, float x, float y) {
      Platform.driver().zoom(notches, x, y);
   }

   public static void hover(float x, float y) {
      Platform.driver().hover(x, y);
   }

   public static void hoverEnd() {
      Platform.driver().hoverEnd();
   }

   private static DCSurfaceView view() {
      return Displayable.getView() instanceof DCSurfaceView v ? v : null;
   }
}
