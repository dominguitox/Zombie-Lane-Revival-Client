package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tracking.ButtonPressedEvent;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.Toolkit;
import java.util.function.IntConsumer;

/** A 100-step volume slider styled with the game's existing HUD colors. */
public final class HUDVolumeControl extends HUDObject {
   private static final int WIDTH = 230;
   private static final int HEIGHT = 40;
   private static final int TRACK_LEFT = 13;
   private static final int TRACK_WIDTH = 166;
   private static final int VALUE_LEFT = 184;
   private static final int KNOB_WIDTH = 12;
   private static final int KNOB_HEIGHT = 24;
   private int level = 100;
   private boolean changed;
   private final SlotBackground background;
   private ButtonPressedEvent trackingEvent;
   private IntConsumer previewListener;

   public HUDVolumeControl(int id, CollisionBox box) {
      super((byte)0, id);
      this.setSizes(box.getX(), box.getY(), WIDTH, HEIGHT);
      this.background = new SlotBackground(0, 0, WIDTH, HEIGHT);
      this.background.setParent(this);
   }

   public int getLevel() {
      return this.level;
   }

   public void setLevel(int level) {
      this.level = Math.max(0, Math.min(100, level));
   }

   public void setTrackingEvent(String name) {
      this.trackingEvent = new ButtonPressedEvent(name);
   }

   public void setPreviewListener(IntConsumer listener) {
      this.previewListener = listener;
   }

   @Override
   public void draw() {
      if (!this.isVisible()) {
         return;
      }

      this.background.draw();
      int x = this.getX();
      int y = this.getY();
      int trackX = x + TRACK_LEFT;
      int trackY = y + (HEIGHT - 8) / 2;
      int knobCenter = trackX + (TRACK_WIDTH - 1) * this.level / 100;
      int knobX = knobCenter - KNOB_WIDTH / 2;

      // The brown groove, warm fill and cream handle reuse the game's HUD palette.
      OGL.setColorRGB(0x40311a);
      OGL.fillRect(trackX - 2, trackY - 2, TRACK_WIDTH + 4, 12);
      OGL.setColorRGB(0xc7a97c);
      OGL.fillRect(trackX, trackY, TRACK_WIDTH, 8);
      OGL.setColorRGB(0xe39f6a);
      OGL.fillRect(trackX, trackY, Math.max(1, knobCenter - trackX), 8);

      OGL.setColorRGB(0x40311a);
      OGL.fillRect(knobX - 1, y + (HEIGHT - KNOB_HEIGHT) / 2 - 1, KNOB_WIDTH + 2, KNOB_HEIGHT + 2);
      OGL.setColorRGB(0xfff4d5);
      OGL.fillRect(knobX, y + (HEIGHT - KNOB_HEIGHT) / 2, KNOB_WIDTH, KNOB_HEIGHT);

      ImageFont font = DCiDead.getFont(4);
      font.drawString(this.level + "%", x + VALUE_LEFT + 20, y + (HEIGHT - font.getHeight()) / 2, 17);
   }

   @Override
   public void logicUpdate(int delta) {
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (!this.isVisible() || event.isConsumed()) {
         return;
      }

      int type = event.getType();
      boolean startedInside = this.isInside(event.getInitialX(), event.getInitialY());
      boolean pressedInside = type == TouchEvent.TYPE_PRESSED && this.isInside(event.getX(), event.getY());
      if (!pressedInside && !(startedInside && (type == TouchEvent.TYPE_DRAGGED || type == TouchEvent.TYPE_RELEASED || type == TouchEvent.TYPE_TAPPED))) {
         return;
      }

      if (type == TouchEvent.TYPE_PRESSED || type == TouchEvent.TYPE_DRAGGED || type == TouchEvent.TYPE_RELEASED || type == TouchEvent.TYPE_TAPPED) {
         int previous = this.level;
         this.setLevel(this.valueAt(event.getX()));
         if (previous != this.level) {
            if (this.previewListener != null) {
               this.previewListener.accept(this.level);
            } else if (this.mId == 1) {
               Toolkit.setMusicVolume(this.level);
            } else {
               Toolkit.setSoundEffectVolume(this.level);
            }
            this.changed = true;
         }
         if (type == TouchEvent.TYPE_RELEASED || type == TouchEvent.TYPE_TAPPED) {
            if (this.changed) {
               if (this.mParent != null) {
                  this.mParent.callback(this.mId);
               }
               if (this.trackingEvent != null) {
                  this.trackingEvent.setValue(Integer.toString(this.level));
                  this.trackingEvent.trackEvent();
               }
               this.changed = false;
            }
         }
         event.setConsumed(true);
      }
   }

   private int valueAt(int x) {
      int trackX = this.getX() + TRACK_LEFT;
      int relative = Math.max(0, Math.min(TRACK_WIDTH - 1, x - trackX));
      return relative * 100 / (TRACK_WIDTH - 1);
   }

   @Override
   public void callback(int id) {
   }
}
