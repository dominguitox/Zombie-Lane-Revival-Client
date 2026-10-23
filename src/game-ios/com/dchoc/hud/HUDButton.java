package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.events.EventManager;
import com.dchoc.idead.events.TimedEvent;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tracking.ButtonPressedEvent;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.WindowDailyBonus;
import zlport.ios.Ios;

public class HUDButton extends HUDObject {
   public static final int STATE_DEFAULT = 0;
   public static final int STATE_PRESSED = 1;
   public static final int STATE_DISABLED = 2;
   public static final int BUTTON_HUD_SLOT = 39000;
   public static final int[] FONTS_IDS = new int[]{1, 1, 1, 1, 1, 0, 0, 1};
   public static final int[] IMAGES_IDS = new int[]{72415, 72403, 74817, 66245, 66339, 72121, 74731, 79703};
   private int mCurrentStyle = -1;
   private int mForcedPosition = -1;
   private int mState;
   private CollisionBox mCollisions0;
   private CollisionBox mCollisions1;
   private HUDImage mIcon;
   private HUDImage mBackground;
   private HUDAutoTextField mLabel;
   private int mDefaultFontId;
   private ImageFont mCurrentFont;
   private boolean hasInheritedClipingX;
   private boolean hasInheritedClipingY;
   private boolean mClipping;
   private ButtonPressedEvent mTrackinEvent;

   public HUDButton(int id, CollisionBox box, int style) {
      super((byte)0, id);
      int x = 0;
      int y = 0;
      if (box != null) {
         x = box.getX();
         y = box.getY();
      }

      this.init(id, x, y, FONTS_IDS[style], IMAGES_IDS[style], (byte)-1);
   }

   public HUDButton(int id, int x, int y, int image) {
      super((byte)0, id);
      this.init(id, x, y, image != 66245 && image != 66339 && image != 74817 ? 0 : 1, image, (byte)-1);
   }

   public HUDButton(int id, int x, int y, int image, byte tooltip) {
      super((byte)0, id);
      this.init(id, x, y, image != 66245 && image != 66339 && image != 74817 ? 0 : 1, image, tooltip);
   }

   public void changeStyle(int style) {
      if (this.mCurrentStyle != style) {
         int image = IMAGES_IDS[style];
         int font = image != 66245 && image != 66339 && image != 74817 ? 0 : 1;
         this.mCurrentFont = DCiDead.getFont(font);
         this.mLabel.setFont(font);
         this.mBackground = new HUDImage(IMAGES_IDS[style]);
         this.mBackground.setAnimationFrame(0);
         this.mBackground.setParent(this);
         this.mCollisions0 = this.mBackground.getCollisionBox(0);
         this.mCollisions1 = this.mBackground.getCollisionBox(1);
         this.mWidth = this.mBackground.getWidth();
         this.mHeight = this.mBackground.getHeight();
         this.mCurrentStyle = style;
      }
   }

   public String getString() {
      return this.mLabel == null ? null : this.mLabel.getText();
   }

   public void setText(String text) {
      HUDAutoTextField label = this.mLabel;
      if (label != null) {
         ImageFont font = label.getFont();
         label.setText(text, font);
         this.calculatePositions();
         if (this.mId == 56001) {
            this.mLabel.setOffsetY(-3);
            this.mLabel.setOffsetX(7);
         }

         if (this.mId == 56002) {
            this.mLabel.setOffsetY(-3);
            this.mLabel.setOffsetX(7);
         }
      }
   }

   public void setText(int text) {
      HUDAutoTextField label = this.mLabel;
      if (label != null) {
         ImageFont font = label.getFont();
         label.setText(text, font);
         this.calculatePositions();
      }
   }

   public void setClipping(boolean value) {
      this.mClipping = value;
   }

   public void setIcon(int image) {
      this.mIcon.setImage(image);
      this.mIcon.setAnimationFrame(0);
      this.calculatePositions();
   }

   public void removeIcon() {
      this.mIcon = null;
   }

   public void setForcedPosition(int position) {
      this.mForcedPosition = position;
   }

   public void setTargetObject(IsometricObject target) {
      if (this.mTargetObject != target) {
         this.mTargetObject = target;
         if (target != null) {
            this.mPositionType = -1;
            this.updatePositionWithTarget();
         }
      }
   }

   @Override
   public int getWidth() {
      return this.mBackground.getWidth();
   }

   @Override
   public int getHeight() {
      return this.mBackground.getHeight();
   }

   public void setInheritedClipping(boolean x, boolean y) {
      this.hasInheritedClipingX = x;
      this.hasInheritedClipingY = y;
   }

   public void disable() {
      this.setState(2);
   }

   public boolean isDisabled() {
      return this.mState == 2;
   }

   public boolean isPressed() {
      return this.mState == 1;
   }

   public void setEnabled(boolean enabled) {
      if (enabled) {
         if (Integer.compareUnsigned(this.mState, 2) >= 0) {
            this.enable();
         }
      } else if (this.mState != 2) {
         this.disable();
      }
   }

   public void enable() {
      this.setState(0);
   }

   @Override
   public void draw() {
      if (this.mIsVisible) {
         if (this.mClipping) {
            int x = 0;
            int y = 0;
            int width;
            int height;
            if (!this.hasInheritedClipingX && !this.hasInheritedClipingY) {
               width = Toolkit.getScreenWidth();
               height = Toolkit.getScreenHeight();
            } else if (!this.hasInheritedClipingX) {
               y = OGL.getClipY();
               width = Toolkit.getScreenWidth();
               height = OGL.getClipHeight();
            } else if (!this.hasInheritedClipingY) {
               x = OGL.getClipX();
               width = OGL.getClipWidth();
               height = Toolkit.getScreenHeight();
            } else {
               x = OGL.getClipX();
               y = OGL.getClipY();
               width = OGL.getClipWidth();
               height = OGL.getClipHeight();
            }

            OGL.setClip(x, y, width, height);
         }

         int frameCount = this.mBackground.getFrameCount();
         this.mBackground.setAnimationFrame(frameCount > this.mState ? this.mState : 0);
         boolean pushed = this.mState == 2 && this.mBackground.getFrameCount() <= this.mState;
         if (pushed) {
            OGL.pushParameters();
            OGL.setAlpha(0.2F);
         }

         this.mBackground.draw();
         this.mIcon.draw();
         if (this.mLabel != null) {
            this.mLabel.draw();
            this.mLabel.drawDebug(16711935);
         }

         if (pushed) {
            OGL.popParameters();
         }
      }
   }

   @Override
   public boolean isInside(int x, int y) {
      if (Ios.platform().isImpactSupported()) {
         int left = this.mPosX - this.mBackground.getWidth();
         int top = this.mPosY + this.mBackground.getHeight();
         int width = this.mBackground.getWidth();
         int height = this.mBackground.getHeight();
         if (this.mId == 56001 && x >= left && x <= left + width && y >= top && y <= top + height) {
            return false;
         }

         if (this.mId == 56002 && x >= left && x <= left + width && y >= top && y <= top + height) {
            return true;
         }
      }

      return this.mCollisions0 != null
         && x >= this.getX() + this.mCollisions0.getX()
         && x <= this.getX() + this.mCollisions0.getX() + this.mCollisions0.getWidth()
         && y >= this.getY() + this.mCollisions0.getY()
         && y <= this.getY() + this.mCollisions0.getY() + this.mCollisions0.getHeight();
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible() && !event.isConsumed() && this.mState != 2) {
         boolean inside = this.isInside(event.getX(), event.getY());
         boolean initial = this.isInside(event.getInitialX(), event.getInitialY());
         int type = event.getType();
         if (type == 0) {
            if (inside && this.mState == 0) {
               this.setState(1);
               if (this.mId != 39000) {
                  iWrapper.playSoundFx(50);
               }

               event.setConsumed(true);
            }
         } else if (type == 1) {
            if (!initial) {
               return;
            }

            if (this.mState == 1 && !inside) {
               this.setState(0);
            }

            event.setConsumed(true);
         } else if (type != 2 && type != 3) {
            if (type == 5 && inside) {
               this.createTooltip(this.mBackground);
               event.setConsumed(true);
            }
         } else if (inside) {
            if (this.mState != 1) {
               return;
            }

            this.setState(0);
            if (!this.doAction() && this.mParent != null) {
               this.mParent.callback(this.mId);
            }

            if (this.mTrackinEvent != null) {
               this.mTrackinEvent.trackEvent();
            }

            event.setConsumed(true);
         } else if (initial) {
            this.setState(0);
            event.setConsumed(true);
         }
      }
   }

   public int getState() {
      return this.mState;
   }

   public void resetState() {
      this.setState(0);
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.mTargetObject != null && this.mTargetObject.getAnimations().getAnimation() != -1) {
         this.updatePositionWithTarget();
      }

      if (this.mLabel != null) {
         this.mLabel.logicUpdate(delta);
      }
   }

   @Override
   public void callback(int id) {
   }

   public void setTrackingEvent(String event) {
      this.mTrackinEvent = new ButtonPressedEvent(event);
   }

   public void init(int id, int x, int y, int font, int image, byte tooltip) {
      this.mId = id;
      this.mPosX = x;
      this.mPosY = y;
      this.mDefaultFontId = font;
      this.mTooltipType = tooltip;
      this.mCurrentFont = DCiDead.getFont(font);
      this.mBackground = new HUDImage(image);
      this.mBackground.setAnimationFrame(0);
      this.mBackground.setParent(this);
      this.mCollisions0 = this.mBackground.getCollisionBox(0);
      this.mCollisions1 = this.mBackground.getCollisionBox(1);
      this.mWidth = this.mBackground.getWidth();
      this.mHeight = this.mBackground.getHeight();
      this.mIcon = new HUDImage();
      this.mIcon.setParent(this);
      this.mLabel = new HUDAutoTextField(0, 0, 0, 0);
      this.mLabel.setParent(this);
      this.mLabel.setFont(this.mDefaultFontId);
      this.mClipping = false;
      this.setState(0);
      this.calculatePositions();
   }

   public void calculatePositions() {
      if (this.mCollisions0 != null && this.mCollisions1 != null) {
         int width;
         int space;
         if (this.mIcon.getCollisionBox(0) == null) {
            width = this.mIcon.getWidth();
            space = this.mCurrentFont.stringWidth(" ");
         } else {
            width = this.mIcon.getCollisionBox(0).getWidth();
            space = 0;
         }

         if (this.mLabel != null) {
            if (!this.mIcon.isEmpty()) {
               HUDImage icon = this.mIcon;
               int x = this.mCollisions0.getX();
               int y = this.mCollisions0.getY();
               int height = this.mCollisions0.getHeight();
               icon.setPosition(x + width / 2 + space, y + height / 2);
               HUDAutoTextField label = this.mLabel;
               int lx = this.mCollisions1.getX();
               int ly = this.mCollisions1.getY();
               int lw = this.mCollisions1.getWidth();
               int lh = this.mCollisions1.getHeight();
               label.setSizes(lx + width + 2 * space, ly, lw - width - 3 * space, lh);
               this.mLabel.setCentered(false, true);
               this.mLabel.setScaleToFit(true);
               return;
            }

            if (this.mLabel != null) {
               this.mLabel.setSizes(this.mCollisions1);
               this.mLabel.setCentered(true, true);
               this.mLabel.setScaleToFit(true);
               return;
            }
         }

         if (!this.mIcon.isEmpty()) {
            HUDImage icon = this.mIcon;
            int x = this.mCollisions0.getX();
            int w = this.mCollisions0.getWidth();
            int y = this.mCollisions0.getY();
            int h = this.mCollisions0.getHeight();
            icon.setPosition(x + w / 2, y + h / 2);
         }
      }
   }

   public void updatePositionWithTarget() {
      IsometricObject target = this.mTargetObject;
      int type = this.mPositionType;
      int width = this.getWidth();
      int height = this.getHeight();
      int[] pos = IsometricUtils.getPositionTypeXY(target, type, width, height, 0, 0, false, false, this.mForcedPosition);
      if (pos != null) {
         this.mPosX = pos[0];
         this.mPosY = pos[1];
         this.mPositionType = (byte)pos[2];
      }
   }

   public boolean doAction() {
      if (this.mId == 9001) {
         ((WindowDailyBonus)WindowManager.getWindow((byte)4)).giveRewards();
         WindowManager.closeWindow();
         return true;
      }

      if (this.mId != 13000
         && this.mId != 13001
         && this.mId != 14000
         && this.mId != 15000
         && this.mId != 18000
         && this.mId != 20000
         && this.mId != 21000
         && this.mId != 25000
         && this.mId != 34000
         && this.mId != 37000
         && this.mId != 38000
         && this.mId != 40000
         && this.mId != 41000
         && this.mId != 52000
         && this.mId != 54000) {
         return false;
      }

      if (this.mId == 13001) {
         Ios.platform().logNativeMessage("Flurry button pressed");
      }

      WindowManager.closeWindow();
      if (this.mId == 15000) {
         if (PlayerProfile.getLevel() == 10) {
            EventManager.addEvent(new TimedEvent(500, 3882, new int[]{-1}));
         }

         return true;
      } else if (this.mId == 14000) {
         int state = HUD.getState();
         if (state >= 4 && state <= 6) {
            HUD.resetState();
         }

         return false;
      } else {
         return true;
      }
   }

   public void setState(int state) {
      this.mState = state;
      int font;
      if (state != 2 || this.mCurrentFont != DCiDead.getFont(FONTS_IDS[3]) && this.mCurrentFont != DCiDead.getFont(FONTS_IDS[3])) {
         font = this.mDefaultFontId;
      } else {
         font = FONTS_IDS[5];
      }

      this.mCurrentFont = DCiDead.getFont(font);
   }
}
