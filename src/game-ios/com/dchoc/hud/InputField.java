package com.dchoc.hud;

import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.opengl.OGL;
import com.dchoc.toolkit.Toolkit;

public class InputField {
   public static final String EMPTY_TEXT = "";
   private StringBuffer mText = new StringBuffer();
   private int mMaxCharacterCount = -1;
   private boolean mVisible = true;
   private int mScrollX;
   private int mScrollY;
   private int mWriterX;
   private int mWriterY;
   private ImageFont mFont;
   private int mInputMode;
   private int mKeyCode;
   private int mGameAction;
   private int mKeyRepeatTimer;
   private int mKeyCharIndex;
   private int mCommitTimer;
   private int mCursor;
   private int mBlinkTimer;
   private int mX;
   private int mY;
   private int mWidth;
   private int mHeight;
   private boolean mFocus;
   private boolean mLineWrapped;
   private boolean mMultiline;
   private char[] mKeyChars;

   public InputField(int mode, ImageFont font) {
      this.mInputMode = mode;
      this.setFont(font);
   }

   public void setText(String text) {
      StringBuffer buffer = this.mText;
      buffer.delete(0, buffer.length());
      this.mText.append(text);
      this.mCursor = text.length();
   }

   public String getText() {
      return this.mText.toString();
   }

   public void setFont(ImageFont font) {
      this.mFont = font;
   }

   public void setMaxCharacterCount(int count) {
      this.mMaxCharacterCount = count;
   }

   public void setFocus(boolean focus) {
      this.mFocus = focus;
      if (focus) {
         this.setText("");
      } else {
         this.mKeyRepeatTimer = 0;
         this.mCommitTimer = 0;
      }
   }

   public boolean hasFocus() {
      return this.mFocus;
   }

   public void keyEventOccurred(int key, int action, int type, char[] chars) {
      if (type != 1) {
         if (this.hasFocus()) {
            if (type == 0) {
               if ((this.mKeyRepeatTimer != 0 || this.mCommitTimer >= 1) && chars != null && chars.length != 1 && this.mKeyCode == key) {
                  if (this.mCommitTimer < 1) {
                     this.mKeyChars = chars;
                     this.mKeyCode = key;
                     this.mGameAction = action;
                     return;
                  }

                  this.changeWriterCharacter();
               } else {
                  this.mKeyChars = chars;
                  this.mKeyCode = key;
                  this.mGameAction = action;
                  this.mKeyRepeatTimer = 1;
                  this.mKeyCharIndex = 0;
                  this.processKeyPress();
               }
            } else {
               if (type != 3) {
                  return;
               }

               if (Toolkit.getSoftKeyType(key) != 4) {
                  return;
               }

               this.eraseCharacter();
            }
         }
      } else {
         if ((this.mKeyRepeatTimer > 0 || this.mCommitTimer > 0) && this.mKeyChars != null && this.mKeyChars.length > 1) {
            this.mCommitTimer = 1000;
         } else {
            this.mKeyChars = null;
         }

         this.mKeyRepeatTimer = 0;
      }
   }

   public void setMultiline(boolean multiline) {
      this.mMultiline = multiline;
   }

   public void pointerEventOccurred(int x, int y, int type) {
      if (type != 1) {
         if (x >= this.mX && y >= this.mY && x < this.mWidth + this.mX && y < this.mY + this.mHeight) {
            if (!this.hasFocus()) {
               this.setFocus(true);
            }

            this.calculateScrollPos();
            int lines = this.getCurrentLineCount();
            int lineHeight = this.getLineHeight();
            int line = this.mScrollY / lineHeight + (y - this.mY) / lineHeight;
            if (lines <= line) {
               line = lines - 1;
            }

            int start = this.getLineStartOffset(line);
            this.mCursor = this.getLineCharacterOffset(start, x + this.mScrollX - this.mX);
            this.mBlinkTimer = 0;
         } else {
            this.setFocus(false);
         }
      }
   }

   public int getPreferredHeight(int lines) {
      return lines * this.getLineHeight();
   }

   public void setVisible(boolean visible) {
   }

   public void doDraw() {
      if (this.mVisible) {
         int clipX = OGL.getClipX();
         int clipY = OGL.getClipY();
         int clipWidth = OGL.getClipWidth();
         int clipHeight = OGL.getClipHeight();
         OGL.clipRect(this.mX, this.mY, this.mWidth, this.mHeight);
         int top = OGL.getClipY();
         int height = OGL.getClipHeight();
         this.calculateScrollPos();
         int lineHeight = this.getLineHeight();
         int width = this.getWidth();
         int offsetX = (width - this.getLinePixelWidth(0, this.mText.length())) / 2;
         int fieldHeight = this.getHeight();
         int fontHeight = this.mFont.getHeight();
         int scrollY = this.mScrollY;
         int fieldY = this.mY;
         int length = this.mText.length();
         if (length > 0) {
            int bottom = height + top;
            int y = fieldY + (fieldHeight - fontHeight) / 2 - scrollY;
            if (y < bottom) {
               int i = 0;

               int end;
               do {
                  int count = this.getLineLength(i);
                  end = count + i;
                  if (top < y + lineHeight) {
                     int scrollX = this.mScrollX;
                     int fieldX = this.mX;
                     char c = this.getCharAt(i);
                     int x = offsetX + fieldX - scrollX;
                     int last = i;
                     if (i < end - 1) {
                        last = --count + i;

                        do {
                           i++;
                           this.mFont.drawChar(c, x, y, 20);
                           x += this.mFont.charWidth(c);
                           c = this.getCharAt(i);
                        } while (--count != 0);
                     }

                     if (c != '\n') {
                        this.mFont.drawChar(c, x, y, 20);
                        this.mFont.charWidth(c);
                     }

                     end = last + 1;
                  }

                  if (end != length) {
                     y += lineHeight;
                  }

                  i = end;
               } while (end < length && y < bottom);
            }
         }

         if (this.hasFocus() && (this.mBlinkTimer >> 8 & 1) == 0) {
            this.calculateWriterCoordinate();
            this.mFont
               .drawChar('|', offsetX + this.mX + this.mWriterX - this.mScrollX, this.mWriterY + this.mY + (fieldHeight - fontHeight) / 2 - this.mScrollY, 20);
         }

         OGL.setClip(clipX, clipY, clipWidth, clipHeight);
      }
   }

   public void logicUpdate(int delta) {
      this.mBlinkTimer += delta;
      if (this.mKeyRepeatTimer < 1) {
         if (this.mCommitTimer > 0) {
            this.mCommitTimer -= delta;
            if (this.mCommitTimer > 0) {
               return;
            }

            this.moveToNextOffset();
            this.mCommitTimer = 0;
         }
      } else {
         this.mKeyRepeatTimer += delta;
         if (this.mKeyRepeatTimer - 800 > 49) {
            this.processKeyPress();
            this.mKeyRepeatTimer = 800;
         }
      }
   }

   public void setBounds(int x, int y, int width, int height) {
      this.setX(x);
      this.setY(y);
      this.setWidth(width);
      this.setHeight(height);
   }

   public void setX(int x) {
      this.mX = x;
   }

   public void setY(int y) {
      this.mY = y;
   }

   public void setWidth(int width) {
      this.mWidth = width;
   }

   public void setHeight(int height) {
      this.mHeight = height;
   }

   public ImageFont getFont() {
      return this.mFont;
   }

   public int getX() {
      return this.mX;
   }

   public int getY() {
      return this.mY;
   }

   public int getWidth() {
      return this.mWidth;
   }

   public int getHeight() {
      return this.mHeight;
   }

   public int getTextLength() {
      return this.mText.length();
   }

   public void callback(int id) {
   }

   public void draw() {
   }

   public void pointerEvent(TouchEvent event) {
   }

   public void calculateScrollPos() {
      this.calculateWriterCoordinate();
      this.mScrollX = 0;
      this.mScrollY = 0;
      if (this.isSingleLineField()) {
         int space = this.mWidth - this.mWriterX - this.mFont.charWidth('|');
         this.mScrollX = space > 0 ? 0 : -space;
      } else {
         int lineHeight = this.getLineHeight();
         int writerY = this.mWriterY;
         int height = this.mHeight;
         int scroll = height % lineHeight + (lineHeight - height) + (writerY - writerY % lineHeight);
         if (scroll < 0) {
            scroll = 0;
         }

         this.mScrollY = scroll;
      }
   }

   public void moveToNextOffset() {
      if (this.mCursor < this.mText.length()) {
         this.mCursor++;
         this.mBlinkTimer = 0;
      }
   }

   public void eraseCharacter() {
      if (this.mCursor >= 1) {
         this.mCursor--;
         this.mText.deleteCharAt(this.mCursor);
         this.mBlinkTimer = 0;
      }
   }

   public void processKeyPress() {
      if (this.mInputMode == 0) {
         this.mCommitTimer = 0;
      }

      if (this.mKeyChars != null) {
         this.writeCharacter();
         this.moveToNextOffset();
      } else if (this.mKeyCode == 8) {
         this.eraseCharacter();
      } else {
         int offset;
         switch (this.mGameAction) {
            case 50:
            case 56:
               int lineHeight = this.getLineHeight();
               this.calculateWriterCoordinate();
               int line = (this.mWriterY & 65535) / lineHeight;
               int direction;
               if (this.mGameAction == 50) {
                  direction = line > 0 ? -1 : 0;
               } else {
                  direction = line < this.getCurrentLineCount() - 1 ? 1 : 0;
               }

               if (direction == 0) {
                  return;
               }

               int start = this.getLineStartOffset(direction + line);
               offset = this.getLineCharacterOffset(start, this.mWriterX);
               break;
            case 51:
            case 53:
            case 55:
            default:
               return;
            case 52:
               if (this.mCursor < 1) {
                  return;
               }

               offset = this.mCursor - 1;
               break;
            case 54:
               this.moveToNextOffset();
               return;
         }

         this.mCursor = offset;
         this.mBlinkTimer = 0;
      }
   }

   public void changeWriterCharacter() {
      if (this.mKeyChars != null) {
         this.mKeyCharIndex++;
         int cursor = this.mCursor;
         StringBuffer buffer = this.mText;
         buffer.setCharAt(cursor - 1, this.mKeyChars[this.mKeyCharIndex % (this.mKeyChars == null ? 0 : this.mKeyChars.length)]);
         this.mBlinkTimer = 0;
      }
   }

   public char getCharAt(int index) {
      return this.mInputMode == 0 && (this.mCursor - 1 != index || this.mCommitTimer <= 0) ? '*' : this.mText.charAt(index);
   }

   public void writeCharacter() {
      if (this.mText.length() == this.mMaxCharacterCount) {
         this.mKeyRepeatTimer = 0;
         this.mCommitTimer = 0;
      } else if (this.mKeyChars != null) {
         char c = this.mKeyChars[this.mKeyCharIndex % (this.mKeyChars == null ? 0 : this.mKeyChars.length)];
         if (c == '\n') {
            if (this.isSingleLineField()) {
               this.setFocus(false);
               return;
            }
         } else if (!this.mFont.isCharacterSupported(c)) {
            return;
         }

         if (this.mInputMode == 0) {
            this.mCommitTimer = 1000;
         }

         this.mText.insert(this.mCursor, c);
         this.mBlinkTimer = 0;
         if (c == '\n') {
            if (this.mCursor != this.mText.length() - 1) {
               return;
            }

            this.moveToNextOffset();
         }
      }
   }

   public int getCurrentLineCount() {
      int length = this.mText.length();
      int count = 0;
      if (length > 0) {
         int offset = 0;

         int previous;
         do {
            previous = count;
            offset += this.getLineLength(offset);
            count = previous + 1;
         } while (offset < length);

         if (length > 0 && this.getCharAt(length - 1) == '\n') {
            count = previous + 2;
         }
      }

      return count;
   }

   public int getLineStartOffset(int line) {
      int offset = 0;

      while (line > 0) {
         int count = this.getLineLength(offset);
         line--;
         offset += count;
      }

      return offset;
   }

   public int getLineCharacterOffset(int start, int pixel) {
      int count = this.getLineLength(start);
      if (count > 0) {
         int end = count + start;
         int width = 0;

         int index;
         char c;
         do {
            index = start;
            c = this.getCharAt(index);
            if (c != '\n') {
               width += this.mFont.charWidth(c);
               if (pixel < width) {
                  return index;
               }
            }

            start = index + 1;
         } while (start < end);

         if (c == '\n') {
            return index;
         }
      }

      if (this.mLineWrapped) {
         start--;
      }

      return start;
   }

   public int getLinePixelWidth(int start, int count) {
      int width = 0;
      if (count != 0) {
         char c = this.getCharAt(start);
         width = 0;
         if (start < start + count - 1) {
            int left = count - 1;
            width = 0;

            do {
               start++;
               width += this.mFont.charWidth(c);
               c = this.getCharAt(start);
            } while (--left != 0);
         }

         if (c != '\n') {
            width += this.mFont.charWidth(c);
         }
      }

      return width;
   }

   public boolean isSingleLineField() {
      return !this.mMultiline;
   }

   public int getLineLength(int start) {
      this.mLineWrapped = false;
      boolean single = this.isSingleLineField();
      int length = this.mText.length();
      int count = length;
      if (!single) {
         count = 0;
         if (start < length) {
            boolean inWord = false;
            int width = 0;

            do {
               char c = this.getCharAt(start + count);
               if (c == '\n') {
                  return count + 1;
               }

               int charWidth = this.mFont.charWidth(c);
               if (this.mWidth < charWidth + width) {
                  return count;
               }

               boolean word = false;
               if (c != '\t' && c != ' ') {
                  word = true;
                  if (!inWord) {
                     int packed = this.getWordPixelWidth(start + count);
                     word = true;
                     if (this.mWidth < width + (packed & 65535)) {
                        this.mLineWrapped = true;
                        if (width != 0) {
                           return count;
                        }

                        return packed >>> 16;
                     }
                  }
               }

               inWord = word;
               count++;
               width += charWidth;
            } while (start + count < length);
         }
      }

      return count;
   }

   public int getLineHeight() {
      return this.mFont.getHeight();
   }

   public void calculateWriterCoordinate() {
      if (this.isSingleLineField()) {
         this.mWriterX = this.getLinePixelWidth(0, this.mCursor);
         this.mWriterY = 0;
      } else {
         this.mWriterX = 0;
         this.mWriterY = 0;
         int lineHeight = this.getLineHeight();
         int cursorWidth = this.mFont.charWidth('|');
         int length = this.mText.length();
         if (length > 0) {
            int offset = 0;

            int end;
            do {
               end = this.getLineLength(offset) + offset;
               int cursor = this.mCursor;
               if (cursor <= end) {
                  this.mWriterX = 0;
                  if (offset < end - 1) {
                     while (true) {
                        if (offset == cursor) {
                           return;
                        }

                        char c = this.getCharAt(offset);
                        int width = this.mFont.charWidth(c);
                        offset++;
                        this.mWriterX += width;
                        if (end - 1 <= offset) {
                           break;
                        }

                        cursor = this.mCursor;
                     }
                  }

                  char c = this.getCharAt(offset);
                  if (this.mCursor == offset) {
                     return;
                  }

                  if (c == '\n') {
                     end = offset + 1;
                  } else {
                     int width = this.mFont.charWidth(c);
                     int writerX = this.mWriterX;
                     end = offset + 1;
                     this.mWriterX = width + writerX;
                     if (end == this.mCursor && width + writerX + cursorWidth < this.mWidth) {
                        if (!this.mLineWrapped) {
                           return;
                        }

                        this.mWriterX = 0;
                        this.mWriterY += lineHeight;
                        return;
                     }
                  }
               }

               if (end != length) {
                  this.mWriterY += lineHeight;
               }

               offset = end;
            } while (end < length);

            if (length > 0 && this.mCursor == length && this.getCharAt(length - 1) == '\n') {
               this.mWriterX = 0;
               this.mWriterY += lineHeight;
            }
         }
      }
   }

   public int getWordPixelWidth(int start) {
      int count = this.getWordLength(start);
      int width = 0;
      int fit = 0;
      if (count > 0) {
         int end = count + start;
         width = 0;
         int inside = 0;

         do {
            char c = this.getCharAt(start);
            width += this.mFont.charWidth(c);
            start++;
            if (width <= this.mWidth) {
               inside++;
            }
         } while (start < end);

         fit = inside << 16;
      }

      return fit | width;
   }

   public int getWordLength(int start) {
      int length = this.mText.length();
      int end = length;

      for (int i = start; i < length; i++) {
         char c = this.getCharAt(i);
         if (c == '\t' || c == '\n' || c == ' ') {
            end = i;
            break;
         }
      }

      return end - start;
   }

   public String getLine(int start) {
      int count = this.getLineLength(start);
      return this.mText.toString().substring(start, count + start);
   }
}
