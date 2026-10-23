package zlport.launcher;

import java.util.ArrayDeque;
import zlport.j2me.Input;
import zlport.j2me.Touch;

final class InputQueue {
   static final InputQueue.Sink GAME = new InputQueue.Sink() {
      @Override
      public void touch(Touch event) {
         Input.touch(event);
      }

      @Override
      public void keyDown(int keyCode) {
         Input.keyDown(keyCode, '\u0000');
      }

      @Override
      public void keyUp(int keyCode) {
         Input.keyUp(keyCode, '\u0000');
      }

      @Override
      public void zoom(double notches, float x, float y) {
         Input.zoom(notches, x, y);
      }

      @Override
      public void hover(float x, float y) {
         Input.hover(x, y);
      }

      @Override
      public void hoverEnd() {
         Input.hoverEnd();
      }
   };
   static final int KEYCODE_BACK = 4;
   private static final Object HOVER_END = new Object();
   private final int logicalWidth;
   private final int logicalHeight;
   private double scaleX = 1.0;
   private double scaleY = 1.0;
   private Presentation.Rect frame;
   private double toLogicalX = 1.0;
   private double toLogicalY = 1.0;
   private final ArrayDeque<Object> pending = new ArrayDeque<>();
   private double cursorX;
   private double cursorY;
   private boolean fingerDown;
   private boolean cancelled;

   InputQueue(int logicalWidth, int logicalHeight, int framebufferWidth, int framebufferHeight, int windowWidth, int windowHeight) {
      this.logicalWidth = logicalWidth;
      this.logicalHeight = logicalHeight;
      this.frame = new Presentation.Rect(0, 0, logicalWidth, logicalHeight);
      this.resize(framebufferWidth, framebufferHeight, windowWidth, windowHeight);
   }

   InputQueue(int framebufferWidth, int framebufferHeight, int windowWidth, int windowHeight) {
      this(framebufferWidth, framebufferHeight, framebufferWidth, framebufferHeight, windowWidth, windowHeight);
   }

   void resize(int framebufferWidth, int framebufferHeight, int windowWidth, int windowHeight) {
      if (framebufferWidth > 0 && framebufferHeight > 0 && windowWidth > 0 && windowHeight > 0) {
         this.scaleX = (double)framebufferWidth / windowWidth;
         this.scaleY = (double)framebufferHeight / windowHeight;
         this.frame = Presentation.fit(this.logicalWidth, this.logicalHeight, framebufferWidth, framebufferHeight);
         this.toLogicalX = (double)this.logicalWidth / this.frame.width();
         this.toLogicalY = (double)this.logicalHeight / this.frame.height();
      }
   }

   void cursor(double x, double y) {
      this.cursorX = x;
      this.cursorY = y;
      if (this.fingerDown && !this.cancelled) {
         Touch move = Touch.one(2, this.fx(), this.fy());
         if (this.pending.peekLast() instanceof Touch last && last.getAction() == 2 && last.getPointerCount() == 1) {
            this.pending.pollLast();
         }

         this.pending.addLast(move);
      } else if (!this.fingerDown) {
         if (this.overFrame()) {
            if (this.pending.peekLast() instanceof InputQueue.Hover) {
               this.pending.pollLast();
            }

            this.pending.addLast(new InputQueue.Hover(this.fx(), this.fy()));
         } else if (this.pending.peekLast() != HOVER_END) {
            this.pending.addLast(HOVER_END);
         }
      }
   }

   void mouseButton(int button, int action) {
      if (button == 0) {
         if (action == 1 && !this.fingerDown) {
            if (!this.overFrame()) {
               return;
            }

            this.fingerDown = true;
            this.cancelled = false;
            this.pending.addLast(HOVER_END);
            this.pending.addLast(Touch.one(0, this.fx(), this.fy()));
         } else if (action == 0 && this.fingerDown) {
            this.fingerDown = false;
            if (this.cancelled) {
               this.cancelled = false;
            } else {
               this.pending.addLast(Touch.one(1, this.fx(), this.fy()));
            }
         }
      }
   }

   void focus(boolean focused) {
      if (!focused) {
         if (this.fingerDown && !this.cancelled) {
            this.cancelled = true;
            this.pending.addLast(Touch.one(3, this.fx(), this.fy()));
         }
      }
   }

   void cursorEnter(boolean entered) {
      if (!entered) {
         this.pending.addLast(HOVER_END);
      }
   }

   void scroll(double dy) {
      if (!this.fingerDown && dy != 0.0 && this.overFrame()) {
         if (this.pending.peekLast() instanceof InputQueue.Zoom last) {
            this.pending.pollLast();
            this.pending.addLast(new InputQueue.Zoom(last.notches() + dy, this.fx(), this.fy()));
         } else {
            this.pending.addLast(new InputQueue.Zoom(dy, this.fx(), this.fy()));
         }
      }
   }

   void key(int glfwKey, int action) {
      if (glfwKey == 256) {
         if (action == 1 || action == 2) {
            this.pending.addLast(new InputQueue.Key(4, true));
         } else if (action == 0) {
            this.pending.addLast(new InputQueue.Key(4, false));
         }
      }
   }

   void deliver(InputQueue.Sink sink) {
      Object e;
      while ((e = this.pending.pollFirst()) != null) {
         if (e instanceof Touch t) {
            sink.touch(t);
         } else if (e instanceof InputQueue.Key k) {
            if (k.down()) {
               sink.keyDown(k.code());
            } else {
               sink.keyUp(k.code());
            }
         } else if (e instanceof InputQueue.Zoom z) {
            sink.zoom(z.notches(), z.x(), z.y());
         } else if (e instanceof InputQueue.Hover h) {
            sink.hover(h.x(), h.y());
         } else if (e == HOVER_END) {
            sink.hoverEnd();
         }
      }
   }

   private double lx() {
      return (this.cursorX * this.scaleX - this.frame.x()) * this.toLogicalX;
   }

   private double ly() {
      return (this.cursorY * this.scaleY - this.frame.y()) * this.toLogicalY;
   }

   private boolean overFrame() {
      double x = this.lx();
      double y = this.ly();
      return x >= 0.0 && x < this.logicalWidth && y >= 0.0 && y < this.logicalHeight;
   }

   private float fx() {
      return clamp((float)this.lx(), 0.0F, this.logicalWidth - 1);
   }

   private float fy() {
      return clamp((float)this.ly(), 0.0F, this.logicalHeight - 1);
   }

   private static float clamp(float v, float min, float max) {
      return Math.max(min, Math.min(max, v));
   }

   private record Hover(float x, float y) {
   }

   private record Key(int code, boolean down) {
   }

   interface Sink {
      void touch(Touch var1);

      void keyDown(int var1);

      void keyUp(int var1);

      void zoom(double var1, float var3, float var4);

      void hover(float var1, float var2);

      void hoverEnd();
   }

   private record Zoom(double notches, float x, float y) {
   }
}
