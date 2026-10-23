package zlport.launcher;

import java.util.List;

final class WindowGeometry {
   private WindowGeometry() {
   }

   static WindowGeometry.Point position(
      Integer x, Integer y, int width, int height, WindowGeometry.Frame frame, List<WindowGeometry.Area> workAreas, WindowGeometry.Area primary
   ) {
      if (x != null && y != null) {
         int px = x + Math.min(width / 2, 100);
         int py = y - Math.max(1, frame.top() / 2);

         for (WindowGeometry.Area a : workAreas) {
            if (a.contains(px, py)) {
               return new WindowGeometry.Point(x, y);
            }
         }
      }

      int outerWidth = width + frame.left() + frame.right();
      int outerHeight = height + frame.top() + frame.bottom();
      return new WindowGeometry.Point(
         primary.x() + Math.max(0, (primary.width() - outerWidth) / 2) + frame.left(),
         primary.y() + Math.max(0, (primary.height() - outerHeight) / 2) + frame.top()
      );
   }

   static int monitorOf(WindowGeometry.Area window, List<WindowGeometry.Area> monitors) {
      int best = 0;
      long bestOverlap = 0L;

      for (int i = 0; i < monitors.size(); i++) {
         long o = window.overlap(monitors.get(i));
         if (o > bestOverlap) {
            best = i;
            bestOverlap = o;
         }
      }

      return best;
   }

   record Area(int x, int y, int width, int height) {
      boolean contains(int px, int py) {
         return px >= this.x && px < this.x + this.width && py >= this.y && py < this.y + this.height;
      }

      long overlap(WindowGeometry.Area o) {
         long w = (long)Math.min(this.x + this.width, o.x + o.width) - Math.max(this.x, o.x);
         long h = (long)Math.min(this.y + this.height, o.y + o.height) - Math.max(this.y, o.y);
         return w > 0L && h > 0L ? w * h : 0L;
      }
   }

   record Frame(int left, int top, int right, int bottom) {
   }

   record Point(int x, int y) {
   }
}
