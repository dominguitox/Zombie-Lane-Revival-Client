package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.Toolkit;
import java.util.Vector;

public class HUDAutoTextField extends HUDObject {
   public static final int INITIALI_LINES_CAPACITY = 16;
   public static final float SEMI_TRANSPARENT = 0.2F;
   public static String EMPTY_SPACE_SYMBOL = " ";
   public static String LINE_CHANGE_SYMBOL = "\\n";
   private static Vector smLines = new Vector(16);
   protected String mText;
   protected short mTextId;
   protected byte mLanguageIndex;
   protected short mTextWidth;
   protected short mTextHeight;
   protected ImageFont mFont;
   protected boolean mCenterHorizontally;
   protected boolean mCenterVertically;
   protected boolean mAlignBottom;
   protected boolean mSemiTransparent;
   protected boolean mScaleToFit;
   protected boolean mSearchScale;
   protected int mOffsetX;
   protected int mOffsetY;
   protected String[] mSplittedText;
   private ReplaceParameters mReplaceParameters;
   private boolean mSplitUsingWidth;
   private float mScale;

   public HUDAutoTextField(CollisionBox box) {
      super((byte)5);
      int x = box.getX();
      int y = box.getY();
      int width = box.getWidth();
      int height = box.getHeight();
      this.setPosition(x, y);
      this.setSize(width, height);
      this.mText = null;
      this.mTextId = -1;
      this.mLanguageIndex = (byte)Toolkit.getSelectedLanguageIndex();
      this.mReplaceParameters = null;
      this.mSplitUsingWidth = false;
      this.mScaleToFit = false;
      this.mSearchScale = false;
      this.mCenterHorizontally = false;
      this.mCenterVertically = false;
      this.mAlignBottom = false;
      this.mSemiTransparent = false;
      this.mScale = 1.0F;
   }

   public HUDAutoTextField(CollisionBox box, int text, int font, boolean centerHorizontally, boolean centerVertically, HUDObject parent) {
      super((byte)5);
      int x = box.getX();
      int y = box.getY();
      int width = box.getWidth();
      int height = box.getHeight();
      this.setPosition(x, y);
      this.setSize(width, height);
      this.mText = null;
      this.mTextId = -1;
      this.mLanguageIndex = (byte)Toolkit.getSelectedLanguageIndex();
      this.mReplaceParameters = null;
      this.mSplitUsingWidth = false;
      this.mScaleToFit = false;
      this.mSearchScale = false;
      this.mCenterHorizontally = false;
      this.mCenterVertically = false;
      this.mAlignBottom = false;
      this.mSemiTransparent = false;
      this.mScale = 1.0F;
      this.setText(text, font);
      this.setCentered(centerHorizontally, centerVertically);
      this.setParent(parent);
   }

   public HUDAutoTextField(int x, int y, int width, int height) {
      super((byte)5);
      super.setPosition(x, y);
      this.setSize(width, height);
      this.mText = null;
      this.mTextId = -1;
      this.mLanguageIndex = (byte)Toolkit.getSelectedLanguageIndex();
      this.mReplaceParameters = null;
      this.mSplitUsingWidth = false;
      this.mScaleToFit = false;
      this.mSearchScale = false;
      this.mCenterHorizontally = false;
      this.mCenterVertically = false;
      this.mAlignBottom = false;
      this.mSemiTransparent = false;
      this.mScale = 1.0F;
   }

   public void setText(int text, int font) {
      this.setText(text, DCiDead.getFont(font));
   }

   public void setText(int text, ImageFont font) {
      this.applyText(Toolkit.getText(text), text, font, false);
   }

   public void setText(String text, int font) {
      this.setText(text, DCiDead.getFont(font));
   }

   public void setText(String text, ImageFont font) {
      this.applyText(text, -1, font, false);
   }

   public void setText(int text, int font, String parameter, int parameterId) {
      if (this.mReplaceParameters == null) {
         this.mReplaceParameters = new ReplaceParameters();
      }

      this.mReplaceParameters.init(parameter, parameterId);
      this.applyText(this.mReplaceParameters.constructText(text), text, DCiDead.getFont(font), true);
   }

   public void setText(int text, int font, String parameter1, int parameterId1, String parameter2, int parameterId2) {
      if (this.mReplaceParameters == null) {
         this.mReplaceParameters = new ReplaceParameters();
      }

      this.mReplaceParameters.init(parameter1, parameterId1, parameter2, parameterId2);
      this.applyText(this.mReplaceParameters.constructText(text), text, DCiDead.getFont(font), true);
   }

   public void setText(int text, int font, String parameter1, int parameterId1, String parameter2, int parameterId2, String parameter3, int parameterId3) {
      if (this.mReplaceParameters == null) {
         this.mReplaceParameters = new ReplaceParameters();
      }

      this.mReplaceParameters.init(parameter1, parameterId1, parameter2, parameterId2, parameter3, parameterId3);
      this.applyText(this.mReplaceParameters.constructText(text), text, DCiDead.getFont(font), true);
   }

   public void setFont(int font) {
      if (this.mTextId == -1) {
         this.setText(this.mText, font);
      } else {
         this.setText(this.mTextId, font);
      }
   }

   public void resizeToFit() {
      this.setSize(this.mTextWidth, this.mTextHeight);
   }

   public String getText() {
      return this.mText;
   }

   public void setSemitransparent(boolean semitransparent) {
      this.mSemiTransparent = semitransparent;
   }

   public void setCentered(boolean centerHorizontally, boolean centerVertically) {
      this.mCenterHorizontally = centerHorizontally;
      this.mCenterVertically = centerVertically;
   }

   public void setAlignBottom(boolean alignBottom) {
      this.mAlignBottom = alignBottom;
   }

   public void setSplitUsingWidth(boolean splitUsingWidth) {
      this.mSplitUsingWidth = splitUsingWidth;
   }

   public void setScaleToFit(boolean scaleToFit) {
      this.mScaleToFit = scaleToFit;
   }

   public void searchScale(boolean search) {
      this.mSearchScale = search;
   }

   public int getTextWidth() {
      return this.mTextWidth;
   }

   public int getTextHeight() {
      return this.mTextHeight;
   }

   public String[] getSplittedText() {
      return this.mSplittedText;
   }

   public void setOffsetX(int offset) {
      this.mOffsetX = offset;
   }

   public void setOffsetY(int offset) {
      this.mOffsetY = offset;
   }

   public int getOffsetX() {
      return this.mOffsetX;
   }

   public int getOffsetY() {
      return this.mOffsetY;
   }

   @Override
   public void callback(int id) {
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         if (this.mSemiTransparent) {
            OGL.pushParameters();
            OGL.setAlpha(0.2F);
         }

         int x = this.getX();
         int offsetX = this.mOffsetX;
         int y = this.getY();
         if (this.mText != null && this.mFont != null) {
            int baseX = offsetX + x;
            int baseY = y + this.mOffsetY;
            boolean scaled = false;
            float scale;
            if (!this.mScaleToFit) {
               scale = 1.0F;
            } else {
               if (this.mSearchScale) {
                  scale = this.mScale;
               } else {
                  scale = Math.min((float)this.mWidth / this.mTextWidth, (float)this.mHeight / this.mTextHeight);
               }

               if (scale < 1.0F) {
                  OGL.pushParameters();
                  OGL.setScale(scale, scale);
                  scaled = true;
               }
            }

            for (int i = 0; i < this.mSplittedText.length; i++) {
               int lineY = (int)Math.floor(this.mFont.getHeight() * scale) * i;
               int lineX;
               int anchor;
               if (!this.mCenterHorizontally) {
                  lineX = 0;
                  anchor = 20;
               } else {
                  anchor = 17;
                  lineX = this.mWidth >> 1;
               }

               if (!this.mCenterVertically) {
                  if (!this.mAlignBottom) {
                     if (scaled) {
                        lineY = (int)Math.floor(lineY * scale);
                     }
                  } else if (scaled) {
                     lineY += (int)Math.floor(this.mHeight - scale * this.mTextHeight);
                  } else {
                     lineY = this.mHeight + lineY - this.mTextHeight;
                  }
               } else if (scaled) {
                  lineY += (int)Math.floor((this.mHeight - scale * this.mTextHeight) * 0.5F);
               } else {
                  lineY += this.mHeight - this.mTextHeight >> 1;
               }

               this.mFont.drawString(this.mSplittedText[i], baseX + lineX, baseY + lineY, anchor, scaled);
            }

            if (scaled) {
               OGL.popParameters();
            }
         }

         if (this.mSemiTransparent) {
            OGL.popParameters();
         }
      }
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.mTextId != -1) {
         if (this.mLanguageIndex != Toolkit.getSelectedLanguageIndex()) {
            this.constructText(true);
         }
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
   }

   public ImageFont getFont() {
      return this.mFont;
   }

   protected void init(int x, int y, int width, int height) {
      this.setPosition(x, y);
      this.setSize(width, height);
      this.mText = null;
      this.mTextId = -1;
      this.mLanguageIndex = (byte)Toolkit.getSelectedLanguageIndex();
      this.mReplaceParameters = null;
      this.mSplitUsingWidth = false;
      this.mScaleToFit = false;
      this.mSearchScale = false;
      this.mCenterHorizontally = false;
      this.mCenterVertically = false;
      this.mAlignBottom = false;
      this.mSemiTransparent = false;
      this.mScale = 1.0F;
   }

   protected void applyText(String text, int id, ImageFont font, boolean keepParameters) {
      if (this.mFont != font || this.mText != text) {
         this.mText = text;
         this.mTextId = (short)id;
         this.mFont = font;
         this.mReplaceParameters = keepParameters ? this.mReplaceParameters : null;
         this.constructText(false);
      }
   }

   protected void constructText(boolean reload) {
      this.mLanguageIndex = (byte)Toolkit.getSelectedLanguageIndex();
      if (reload && this.mTextId != -1) {
         if (this.mReplaceParameters == null) {
            this.mText = Toolkit.getText(this.mTextId);
         } else {
            this.mText = this.mReplaceParameters.constructText(this.mTextId);
         }
      }

      this.splitText(this.mText);
   }

   protected void splitText(String text) {
      if (text != null) {
         int length = text.length();
         this.mTextHeight = 0;
         if (text.length() == 0) {
            smLines.removeAllElements();
            smLines.addElement(text);
         } else {
            boolean search = this.mSearchScale;
            this.mScale = 1.0F;

            while (true) {
               smLines.removeAllElements();
               this.mTextWidth = 0;
               int start = 0;
               int end;
               if (0 < length) {
                  do {
                     end = text.indexOf(LINE_CHANGE_SYMBOL, start);
                     if (end == -1) {
                        end = length;
                     }

                     boolean done;
                     do {
                        int limit = this.mSplitUsingWidth ? this.mWidth : Integer.MAX_VALUE;
                        boolean fits = -1 < limit;
                        int lastSpace = -1;
                        int index = start;
                        int width = 0;
                        int widthAtSpace = 0;
                        if (0 <= limit) {
                           while (index < end) {
                              char c = text.charAt(index);
                              width += (int)Math.floor(this.mFont.charWidth(c) * this.mScale);
                              index++;
                              fits = width <= limit;
                              if (c == ' ') {
                                 widthAtSpace = width;
                                 lastSpace = index;
                              }

                              if (!fits) {
                                 break;
                              }
                           }
                        }

                        int lineWidth = width;
                        int cut;
                        if (index == end && fits) {
                           done = true;
                           cut = end;
                        } else if (lastSpace != -1) {
                           cut = lastSpace - 1;
                           done = false;
                           lineWidth = widthAtSpace;
                        } else {
                           cut = index - 1;
                           done = false;
                           boolean found = false;

                           for (int k = cut; start < k; k--) {
                              char c = text.charAt(k);
                              if (c == ' ' || (char)(c - '.') < 2) {
                                 found = true;
                              }
                           }

                           if (found) {
                              int position = cut;
                              int current = width;

                              while (start < position) {
                                 char c = text.charAt(position);
                                 int charWidth = (int)Math.floor(this.mFont.charWidth(c) * this.mScale);
                                 boolean separator = (char)(c - '.') < 2;
                                 if (separator) {
                                    cut = position + 1;
                                    lineWidth = current;
                                 }

                                 if (--position <= start) {
                                    break;
                                 }

                                 current -= charWidth;
                                 if (separator) {
                                    break;
                                 }
                              }
                           } else {
                              int next = index + 1;

                              char c;
                              do {
                                 lineWidth = width;
                                 if (length <= next - 1) {
                                    done = false;
                                    cut = next - 1;
                                    break;
                                 }

                                 done = true;
                                 if (end == next) {
                                    cut = end;
                                    break;
                                 }

                                 width += (int)Math.floor(this.mFont.charWidth(text.charAt(next)) * this.mScale);
                                 lineWidth = width;
                                 cut = next;
                                 c = text.charAt(next);
                                 next++;
                              } while (c != ' ');
                           }
                        }

                        smLines.addElement(text.substring(start, cut));
                        if (done && cut < length) {
                           start = cut + 2;
                        } else {
                           if (lastSpace != -1) {
                              cut++;
                           }

                           start = cut;
                        }

                        this.mTextWidth = (short)Math.max(this.mTextWidth, lineWidth);
                     } while (!done);
                  } while (end < length);
               }

               end = (int)(smLines.size() * (this.mFont.getHeight() * this.mScale));
               this.mTextHeight = (short)end;
               if (!search || end <= this.mHeight) {
                  break;
               }

               float scale = this.mScale + -0.1F;
               if (scale < 0.2F) {
                  break;
               }

               this.mScale = scale;
               search = true;
            }
         }

         this.mSplittedText = new String[smLines.size()];

         for (int i = 0; i < smLines.size(); i++) {
            this.mSplittedText[i] = (String)smLines.elementAt(i);
         }
      }
   }
}
