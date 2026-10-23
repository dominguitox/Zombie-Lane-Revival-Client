package com.dchoc.imagefont;

import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.opengl.OGL;
import com.dchoc.opengl.OGLRegion;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.IByteArray;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;

public class ImageFont {
   public static final int BORDER_DISABLED = -1;
   public static final String LINE_CHANGE_SYMBOL = "\\n";
   private byte[] mCharacterAscents;
   private byte[] mCharacterHeights;
   private int mCharacterSpacing;
   private byte[] mCharacterWidths;
   private short[] mCharacterXs;
   private short[] mCharacterYs;
   private SpriteObject mFontAnimation;
   private int mFontAscent;
   private int mFontDescent;
   private OGLRegion mFontTexture;
   private int[] mHashTableEntries;

   public ImageFont(int resourceId, IByteArray data) throws IOException {
      this.mFontAnimation = new SpriteObject(resourceId, true, 0);
      OGLRegion[] regions = new OGLRegion[1];
      this.mFontAnimation.getCurrentAnimationData().getFrame(0).getTextures(regions);
      this.mFontTexture = regions[0];
      int count = data.readShort();
      float scale = DeviceWrapper.isVGA() ? 1.5F : 1.0F;
      this.mCharacterSpacing = (int)(data.readByte() * scale);
      this.mFontAscent = (int)(data.readByte() * scale);
      this.mFontDescent = (int)(data.readByte() * scale);
      int capacity = 1;

      while (capacity < count) {
         capacity <<= 1;
      }

      this.hashTableCreate(capacity);
      this.mCharacterXs = new short[count];
      this.mCharacterYs = new short[count];
      this.mCharacterWidths = new byte[count];
      this.mCharacterHeights = new byte[count];
      this.mCharacterAscents = new byte[count];
      int regionX = this.mFontTexture.getRegionX();
      int regionY = this.mFontTexture.getRegionY();

      while (count > 0) {
         int range = data.readUnsignedByte();
         char character = (char)(data.readByte() << 8 | data.readUnsignedByte());

         for (int n = 0; n < range; n++) {
            int index = count - n - 1;
            this.mCharacterXs[index] = (short)(regionX + data.readInt() * scale);
            this.mCharacterYs[index] = (short)(regionY + data.readInt() * scale);
            this.mCharacterWidths[index] = (byte)(data.readByte() * scale);
            this.mCharacterHeights[index] = (byte)(data.readByte() * scale);
            this.mCharacterAscents[index] = (byte)(data.readByte() * scale);
            this.hashTablePut(character++, index);
         }

         count -= range;
      }
   }

   private void hashTableCreate(int capacity) {
      this.mHashTableEntries = new int[capacity];
   }

   private void hashTablePut(char character, int index) {
      if (this.mHashTableEntries != null) {
         int mask = this.mHashTableEntries.length - 1;
         int slot = character;

         for (int remaining = this.mHashTableEntries.length; remaining > 0; remaining--) {
            slot &= mask;
            if (this.mHashTableEntries[slot] == 0) {
               this.mHashTableEntries[slot] = index | character << 16;
               return;
            }

            slot++;
         }
      }
   }

   private int hashTableGet(char character) {
      if (this.mHashTableEntries == null) {
         return -1;
      }

      int mask = this.mHashTableEntries.length - 1;
      int slot = character;

      for (int remaining = this.mHashTableEntries.length; remaining > 0; remaining--) {
         int entry = this.mHashTableEntries[slot & mask];
         if (entry >>> 16 == character) {
            return entry & 65535;
         }

         if (entry == 0) {
            return -1;
         }

         slot++;
      }

      return -1;
   }

   private int getCharIndex(char character) {
      int index = this.hashTableGet(character);
      return index == -1 ? this.hashTableGet((char)32) : index;
   }

   public boolean isCharacterSupported(char character) {
      return this.hashTableGet(character) != -1;
   }

   public int stringWidth(String text) {
      int width = 0;

      for (int i = text.length() - 1; i >= 0; i--) {
         width += this.mCharacterSpacing + this.mCharacterWidths[this.getCharIndex(text.charAt(i))];
      }

      return this.mCharacterSpacing < 0 ? width - this.mCharacterSpacing : width;
   }

   public int charWidth(char character) {
      return this.mCharacterWidths[this.getCharIndex(character)] + this.mCharacterSpacing;
   }

   public int getEmptyStringWidth() {
      return 0;
   }

   public int getHeight() {
      return this.mFontAscent + this.mFontDescent;
   }

   public int getBaselinePosition() {
      return this.mFontAscent;
   }

   public void drawString(Graphics graphics, String text, int x, int y, int anchor) {
      this.drawString(text, x, y, anchor);
   }

   public void drawString(String text, int x, int y, int anchor) {
      this.drawString(text, x, y, anchor, false);
   }

   public void drawString(String text, int x, int y, int anchor, boolean scaleAnchor) {
      float offsetX = 0.0F;
      if ((anchor & 1) != 0) {
         offsetX = -(this.stringWidth(text) >> 1);
      } else if ((anchor & 8) != 0) {
         offsetX = -this.stringWidth(text);
      }

      float offsetY = 0.0F;
      if ((anchor & 16) != 0) {
         offsetY = this.mFontAscent;
      } else if ((anchor & 32) != 0) {
         offsetY = -this.mFontDescent;
      }

      if ((anchor & 2) == 0) {
         if (scaleAnchor) {
            offsetX *= OGL.getScaleX();
            offsetY *= OGL.getScaleY();
         }

         OGL.pushParameters();
         OGL.setPivot(x + offsetX, y + offsetY);
         float drawX = 0.0F;

         for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            int index = this.getCharIndex(character);
            if ((character | 128) != 160) {
               this.drawGlyph(index, drawX, -this.mCharacterAscents[index]);
            }

            drawX += this.mCharacterSpacing + this.mCharacterWidths[index];
         }

         OGL.popParameters();
      }
   }

   public void drawMultiline(String text, int x, int y, int anchor) {
      int start = 0;

      while (start < text.length()) {
         int end = text.indexOf("\\n", start);
         this.drawSubstring(text, start, end == -1 ? text.length() : end, x, y, anchor);
         if (end == -1) {
            return;
         }

         y += this.mFontAscent + this.mFontDescent;
         start = end + "\\n".length();
      }
   }

   public void drawSubstring(String text, int start, int end, int x, int y, int anchor) {
      if ((anchor & 9) != 0) {
         int width = 0;

         for (int i = start; i < end; i++) {
            width += this.mCharacterSpacing + this.mCharacterWidths[this.getCharIndex(text.charAt(i))];
         }

         if (this.mCharacterSpacing < 0) {
            width -= this.mCharacterSpacing;
         }

         x -= (anchor & 1) != 0 ? width >> 1 : width;
      }

      if ((anchor & 16) != 0) {
         y += this.mFontAscent;
      } else if ((anchor & 32) != 0) {
         y -= this.mFontDescent;
      }

      if ((anchor & 2) == 0) {
         for (int i = start; i < end; i++) {
            char character = text.charAt(i);
            int index = this.getCharIndex(character);
            if ((character | 128) != 160) {
               this.drawGlyph(index, x, y - this.mCharacterAscents[index]);
            }

            x += this.mCharacterSpacing + this.mCharacterWidths[index];
         }
      }
   }

   public void drawChar(Graphics graphics, char character, int x, int y, int anchor) {
      this.drawChar(character, x, y, anchor);
   }

   public void drawChar(char character, int x, int y, int anchor) {
      if (character != ' ') {
         if ((anchor & 1) != 0) {
            x -= this.charWidth(character) >> 1;
         } else if ((anchor & 8) != 0) {
            x -= this.charWidth(character);
         }

         if ((anchor & 16) != 0) {
            y += this.mFontAscent;
         } else if ((anchor & 32) != 0) {
            y -= this.mFontDescent;
         }

         if ((anchor & 2) == 0) {
            int index = this.getCharIndex(character);
            this.drawGlyph(index, x, y - this.mCharacterAscents[index]);
         }
      }
   }

   private void drawGlyph(int index, float x, float y) {
      this.mFontTexture.setRegion(this.mCharacterXs[index], this.mCharacterYs[index], this.mCharacterWidths[index], this.mCharacterHeights[index], false);
      OGL.drawTexture(this.mFontTexture, x, y, 0, 20);
   }

   public void freeResources() {
      if (this.mFontAnimation != null) {
         this.mFontAnimation.freeResources();
      }
   }
}
