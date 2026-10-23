package zlport.launcher;

final class Presentation {
   static final int MIN_LOGICAL_WIDTH = 1280;
   static final int MIN_LOGICAL_HEIGHT = 600;

   private Presentation() {
   }

   static Presentation.Size logicalSize(int drawableWidth, int drawableHeight) {
      if (drawableWidth > 0 && drawableHeight > 0) {
         double s = Math.max(1.0, Math.max(1280.0 / drawableWidth, 600.0 / drawableHeight));
         return new Presentation.Size(Math.max(1280, (int)Math.round(drawableWidth * s)), Math.max(600, (int)Math.round(drawableHeight * s)));
      } else {
         return new Presentation.Size(1280, 720);
      }
   }

   static Presentation.Rect fit(int logicalWidth, int logicalHeight, int framebufferWidth, int framebufferHeight) {
      if (framebufferWidth > 0 && framebufferHeight > 0) {
         double s = Math.min((double)framebufferWidth / logicalWidth, (double)framebufferHeight / logicalHeight);
         int w = Math.min(framebufferWidth, Math.max(1, (int)Math.round(logicalWidth * s)));
         int h = Math.min(framebufferHeight, Math.max(1, (int)Math.round(logicalHeight * s)));
         return new Presentation.Rect((framebufferWidth - w) / 2, (framebufferHeight - h) / 2, w, h);
      } else {
         return new Presentation.Rect(0, 0, 0, 0);
      }
   }

   record Rect(int x, int y, int width, int height) {
   }

   record Size(int width, int height) {
   }
}
