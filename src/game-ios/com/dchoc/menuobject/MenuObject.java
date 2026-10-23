package com.dchoc.menuobject;

import com.dchoc.imagefont.ImageFont;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.IMenu;
import com.dchoc.toolkit.Toolkit;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class MenuObject implements IMenu {
   public static final int ALIGNMENT_BOTTOM = 8;
   public static final int ALIGNMENT_RIGHT = 4;
   public static final int EXTENDED_PARAMETER_DEFAULT = -1;
   public static final int ITEM_ICON_INDEX_LOCKED = 2;
   public static final int ITEM_ICON_INDEX_NOT_SELECTED = 0;
   public static final int ITEM_ICON_INDEX_SELECTED = 1;
   public static final int ITEM_ICON_INDEX_SETTING_OFF = 3;
   public static final int ITEM_STATE_LOCKED = 1;
   public static final int ITEM_STATE_NORMAL = 0;
   public static final int SCREEN_TYPE_EXAMPLE_HORIZONTAL_ARROWS = 100;
   public static final int SCREEN_TYPE_MENU_COMBINER = 3;
   public static final int STYLE_BLANK = 1;
   public static final int STYLE_BOX = 4;
   public static final int STYLE_BOX_EMBEDDED_TITLE_BAR = 5;
   public static final int STYLE_DEFAULT = 2;
   public static final int STYLE_FULL_SCREEN = 3;
   public static final int STYLE_SUB_MENU = 6;
   public static final int STYLE_SUB_MENU_BORDERS = 7;
   public static final int STYLE_TRANSPARENT = 2;
   public static final int TABLE_COLUMN_WEIGHT_COMPACT = -1;
   private static ImageFont smDefaultSelectionFont;
   private static ImageFont smDefaultTextFont;
   private static ImageFont smDefaultTitleBarFont;
   private static int smScrollArrowDownSpace;
   private static int smScrollArrowUpSpace;
   private static Image smScrollImage;
   private static ImageFont smSplitStringFont;
   private int mBackgroundColor;
   private int mBackgroundGradientColor;
   private int mCurrentPage;
   private boolean mEnableBackground;
   private boolean mEnableBorders;
   private int mEnsureScrollOnce;
   private boolean mHasTitleBar;
   private int mHeight;
   private int mItemAlignment;
   private int mItemAreaHeight;
   private int mItemAreaWidth;
   private int mItemAreaX;
   private int mItemAreaY;
   private int[] mItemBounds;
   private int[] mItemCornerRows;
   private int[] mItemEvents;
   private ImageFont[] mItemFont;
   private int[] mItemImageAreaWidths;
   private String[] mItemSourceTexts;
   private SpriteObject[] mItemSprites;
   private int[] mItemStates;
   private int[] mItemTextAreaWidths;
   private String[][] mItemTexts;
   private int[] mItemTypes;
   private int mLastDrawX;
   private int mLastDrawY;
   private boolean mLooseItemAreaAlignment;
   private int mMaxItemCount;
   private boolean mNewMenuEventAvailable;
   private int mScreenMaxHeight;
   private int mScreenMaxWidth;
   private int mScreenMinX;
   private int mScreenMinY;
   private int mScreenSoftkeyAreaHeight;
   private int mScreenType;
   private int mScreenXMargin;
   private int mScreenYMargin;
   private int mScrollArrowDownY;
   private int mScrollArrowUpY;
   private int mScrollArrowX;
   private int mScrollingAreaHeight;
   private int mScrollingEvent;
   private int mScrollingTimer;
   private int mSelectedIndex;
   private int mSelectedItemBackgroundColor;
   private int mSelectedItemScrollingDirection;
   private int mSelectedItemScrollingOffset;
   private int mSelectedItemUpdateTimer;
   private ImageFont mSelectionFont;
   private int mSelectionFontHeight;
   private int mSelectionItemHeight;
   private int mSelectionItemsPerScreen;
   private boolean mSeparateTitleBarBackground;
   private int[] mSettingItemImageAreaWidths;
   private int[] mSettingItemTextAreaWidths;
   private int[] mSettingItemValueCounts;
   private int[] mSettingItemValues;
   private int mStyle;
   private SpriteObject[] mSwitchItemSprites;
   private String[][] mSwitchItemTexts;
   private int[][] mTableItemColumnAlignments;
   private int[][] mTableItemColumnWeights;
   private int[][] mTableItemColumnWidths;
   private int[][] mTableItemRowHeights;
   private String[][] mTableItemSourceTexts;
   private SpriteObject[][] mTableItemSprites;
   private String[][][] mTableItemTexts;
   private int mTemporaryScrollingDirection;
   private int mTemporaryScrollingOffset;
   private int mTemporaryUpdateTimer;
   private int mTemporaryVisibleLine;
   private ImageFont mTextFont;
   private int mTextFontHeight;
   private int mTitleBarAlignment;
   private int mTitleBarBackgroundColor;
   private int mTitleBarBackgroundGradientColor;
   private ImageFont mTitleBarFont;
   private int mTitleBarFontHeight;
   private int mTitleBarHeight;
   private int mTitleBarImageAreaWidth;
   private int mTitleBarScrollingDirection;
   private int mTitleBarScrollingOffset;
   private String mTitleBarSourceText;
   private SpriteObject mTitleBarSprite;
   private String[] mTitleBarText;
   private int mTitleBarTextAreaWidth;
   private int mTitleBarUpdateTimer;
   private int mTitleBarX;
   private boolean mTitleBarsOnTop;
   private int mVerticalScrollingOffset;
   private int mWidth;
   private int mX;
   private int mY;
   private final int[] mLastMenuEvent = new int[2];
   private final int[] mSoftKeys = new int[2];

   private void calculateItemLayout(boolean preferred) {
      int initialY = this.mHasTitleBar ? this.mTitleBarHeight + (this.mTitleBarsOnTop ? this.mScreenYMargin : 0) : this.mScreenYMargin;
      this.mItemAreaY = initialY;
      int limit = this.mScreenMinY + this.mScreenMaxHeight - this.mScreenSoftkeyAreaHeight;
      int available = Math.min(this.mHeight - initialY - this.mScreenYMargin, limit - initialY);
      int arrowAvailable = Math.min(this.mHeight - this.mScreenYMargin - smScrollArrowDownSpace, limit) - initialY - smScrollArrowUpSpace;
      int imageWidth = 0;
      int imageHeight = 0;
      boolean settings = false;
      if (this.mScreenType == 0) {
         for (int item = 0; item < this.mMaxItemCount; item++) {
            int margin = this.getItemXMargin(item);
            SpriteObject sprite = this.mItemSprites[item];
            if (sprite != null) {
               for (int animation = 0; animation < sprite.getAnimationCount(); animation++) {
                  sprite.setAnimation(animation, -2, true);
                  imageWidth = Math.max(imageWidth, sprite.getWidth() + margin * 2);
                  imageHeight = Math.max(imageHeight, sprite.getHeight() + 6);
               }
            }

            settings = settings || this.mItemTypes[item] == 4 || this.mItemTypes[item] == 5;
            if (this.mItemTypes[item] == 5 && this.mSwitchItemSprites[item] != null) {
               sprite = this.mSwitchItemSprites[item];

               for (int animation = 0; animation < sprite.getAnimationCount(); animation++) {
                  sprite.setAnimation(animation, -2, true);
                  imageHeight = Math.max(imageHeight, sprite.getHeight() + 6);
               }
            }
         }
      }

      this.mItemBounds = new int[this.mMaxItemCount * 4];
      this.mItemImageAreaWidths = new int[this.mMaxItemCount];
      this.mItemTextAreaWidths = new int[this.mMaxItemCount];
      if (this.mScreenType == 1) {
         this.mItemCornerRows = new int[this.mMaxItemCount];
      }

      if (settings) {
         this.mSettingItemImageAreaWidths = new int[this.mMaxItemCount];
         this.mSettingItemTextAreaWidths = new int[this.mMaxItemCount];
      }

      if (this.mScreenType == 0) {
         this.mSelectionItemHeight = Math.max(imageHeight, this.mSelectionFontHeight + 6);
         if (this.mItemFont != null) {
            for (int item = 0; item < this.mMaxItemCount; item++) {
               if (this.mItemFont[item] != null) {
                  this.mSelectionItemHeight = Math.max(this.mSelectionItemHeight, this.mItemFont[item].getHeight() + 6);
               }
            }
         }
      }

      this.calculateItemSizes(imageWidth, imageHeight, preferred);

      for (int item = 0; item < this.mMaxItemCount; item++) {
         int alignment = this.getItemAlignment(item);
         if ((alignment & 1) != 0) {
            this.mItemBounds[item * 4] = this.mItemAreaWidth - this.mItemBounds[item * 4 + 2] >> 1;
         } else if ((alignment & 4) != 0) {
            this.mItemBounds[item * 4] = this.mItemAreaWidth - this.mItemBounds[item * 4 + 2];
         }
      }

      for (int pass = 0; pass < 2; pass++) {
         if (pass == 0) {
            this.mItemAreaHeight = available;
         } else {
            this.mItemAreaY = initialY + smScrollArrowUpSpace;
            this.mItemAreaHeight = arrowAvailable;
            this.mScrollArrowX = this.mWidth >> 1;
            this.mScrollArrowUpY = this.mItemAreaY - smScrollArrowUpSpace + (smScrollArrowUpSpace >> 1);
            this.mScrollArrowDownY = this.mItemAreaY + this.mItemAreaHeight + (smScrollArrowDownSpace >> 1);
         }

         if (this.mScreenType == 1) {
            this.mItemAreaY += 3;
            this.mItemAreaHeight -= 6;
            if (!preferred) {
               boolean hasText = false;

               for (int item = this.mMaxItemCount - 1; item >= 0; item--) {
                  hasText = hasText || this.mItemTexts[item] != null;
               }

               if (hasText) {
                  int lineHeight = this.mTextFontHeight + 1;
                  this.mItemAreaY = this.mItemAreaY + (this.mItemAreaHeight % lineHeight >> 1);
                  this.mItemAreaHeight = this.mItemAreaHeight / lineHeight * lineHeight;
               }
            }
         }

         int y = 0;

         for (int item = 0; item < this.mMaxItemCount; item++) {
            this.mItemBounds[item * 4 + 1] = y;
            y += this.mItemBounds[item * 4 + 3];
            if (this.mScreenType == 1 && y != 0) {
               y = ((y - 1) / (this.mTextFontHeight + 1) + 1) * (this.mTextFontHeight + 1);
            }
         }

         if (this.mMaxItemCount > 0) {
            this.mScrollingAreaHeight = this.mItemBounds[(this.mMaxItemCount - 1) * 4 + 1] + this.mItemBounds[(this.mMaxItemCount - 1) * 4 + 3];
         }

         if (pass == 1) {
            return;
         }

         if (this.mScrollingAreaHeight <= this.mItemAreaHeight) {
            if (!this.mLooseItemAreaAlignment) {
               return;
            }

            if ((this.mScreenType != 0 || arrowAvailable / this.mSelectionItemHeight < 3)
               && (this.mScreenType != 1 || arrowAvailable / (this.mTextFontHeight + 1) < 3)) {
               return;
            }
         }
      }
   }

   private void calculateItemSizes(int baseWidth, int baseHeight, boolean preferred) {
      for (int item = 0; item < this.mMaxItemCount; item++) {
         ImageFont font = this.getMenuItemFont(item);
         int fontHeight = font.getHeight();
         int margin = this.getItemXMargin(item);
         SpriteObject sprite = this.mItemSprites[item];
         int width = this.mScreenType == 0 ? baseWidth : (sprite == null ? 0 : sprite.getWidth() + margin * 2);
         int height = this.mScreenType == 0 ? baseHeight : (sprite == null ? 0 : sprite.getHeight());
         this.mItemImageAreaWidths[item] = width;
         int settingWidth = 0;
         if (this.mItemTypes[item] == 4) {
            settingWidth = this.mSettingItemValueCounts[item] * 7 + margin * 2 - 8;
            this.mSettingItemImageAreaWidths[item] = settingWidth;
            this.mSettingItemTextAreaWidths[item] = 0;
         } else if (this.mItemTypes[item] == 5) {
            int settingImageWidth = 0;
            SpriteObject switchSprite = this.mSwitchItemSprites[item];
            if (switchSprite != null) {
               for (int animation = 0; animation < switchSprite.getAnimationCount(); animation++) {
                  switchSprite.setAnimation(animation, -2, true);
                  settingImageWidth = Math.max(settingImageWidth, switchSprite.getWidth() + margin * 2);
               }
            }

            this.mSettingItemImageAreaWidths[item] = settingImageWidth;
            int textWidth = 0;
            if (this.mSwitchItemTexts[item] != null) {
               for (String text : this.mSwitchItemTexts[item]) {
                  if (text != null) {
                     textWidth = Math.max(textWidth, font.stringWidth(text));
                  }
               }

               textWidth += margin * 2;
            }

            this.mSettingItemTextAreaWidths[item] = textWidth;
            settingWidth = settingImageWidth + textWidth;
         }

         int cornerWidth = 0;
         int cornerRows = 0;
         if (this.mScreenType == 0) {
            settingWidth += width;
         } else if (this.mScreenType == 1) {
            if (sprite != null) {
               cornerWidth = sprite.getWidth() + margin * 2;
               cornerRows = (sprite.getHeight() - 1) / (this.mTextFontHeight + 1) + 1;
            }

            this.mItemCornerRows[item] = cornerRows;
         }

         String[] lines = this.mItemTexts[item];
         if (this.mItemSourceTexts[item] != null) {
            if (this.mScreenType == 0) {
               lines = new String[]{this.mItemSourceTexts[item]};
            } else {
               smSplitStringFont = this.mTextFont;
               lines = splitString(this.mItemSourceTexts[item], this.mItemAreaWidth - settingWidth - margin * 2, cornerWidth, cornerRows);
            }
         }

         if (lines == null) {
            if (sprite == null) {
               height = 0;
            }
         } else {
            ImageFont textFont = this.mScreenType == 0 ? font : this.mTextFont;
            int textWidth = 0;

            for (int line = 0; line < lines.length; line++) {
               int lineWidth = textFont.stringWidth(lines[line]);
               if (line < cornerRows) {
                  lineWidth += cornerWidth;
               }

               textWidth = Math.max(textWidth, lineWidth);
            }

            this.mItemTextAreaWidths[item] = Math.min(textWidth, this.mItemAreaWidth - settingWidth - margin * 2) + margin * 2;
            settingWidth += this.mItemTextAreaWidths[item];
            int textHeight = this.mScreenType == 0 ? fontHeight + 6 : lines.length * (this.mTextFontHeight + 1);
            height = Math.max(height, textHeight);
            if (!preferred) {
               this.mItemSourceTexts[item] = null;
            }

            this.mItemTexts[item] = lines;
         }

         if (this.mScreenType == 1 && lines == null) {
            settingWidth += width;
         }

         if (!preferred && (this.mItemTypes[item] == 4 || this.mItemTypes[item] == 5)) {
            settingWidth = this.mItemAreaWidth;
         }

         if (this.mScreenType == 0 && height > 0) {
            height = this.mSelectionItemHeight;
         }

         this.mItemBounds[item * 4 + 2] = settingWidth;
         this.mItemBounds[item * 4 + 3] = height;
      }
   }

   private void calculateTableItemLayout(int item, boolean preferred) {
      int margin = this.getItemXMargin(item);
      int available = this.mItemAreaWidth - margin * 2;
      int[] weights = this.mTableItemColumnWeights[item];
      int columns = weights == null ? 0 : weights.length;
      int cells = this.mTableItemSourceTexts[item] != null
         ? this.mTableItemSourceTexts[item].length
         : (this.mTableItemSprites[item] != null ? this.mTableItemSprites[item].length : 0);
      int rows = cells / columns;
      int[] widths = new int[columns];
      int[] heights = new int[rows];
      String[][] texts = new String[cells][];
      this.mTableItemColumnWidths[item] = widths;
      this.mTableItemRowHeights[item] = heights;
      this.mTableItemTexts[item] = texts;
      int remaining = available;

      for (int column = 0; column < columns; column++) {
         if (weights[column] == -1) {
            int width = 0;

            for (int cell = column; cell < cells; cell += columns) {
               if (this.mTableItemSourceTexts[item] != null && this.mTableItemSourceTexts[item][cell] != null) {
                  String text = this.mTableItemSourceTexts[item][cell];
                  width = Math.max(width, this.mTextFont.stringWidth(text));
                  texts[cell] = new String[]{text};
               }

               if (this.mTableItemSprites[item] != null && this.mTableItemSprites[item][cell] != null) {
                  width = Math.max(width, this.mTableItemSprites[item][cell].getWidth());
               }
            }

            widths[column] = width + 4;
            remaining -= widths[column];
         }
      }

      for (int column = 0; column < columns; column++) {
         if (weights[column] != -1) {
            widths[column] = weights[column] * remaining / 100;
         }
      }

      int leftover = available;

      for (int width : widths) {
         leftover -= width;
      }

      widths[columns - 1] = widths[columns - 1] + leftover;
      int height = 0;

      for (int row = 0; row < rows; row++) {
         int rowHeight = 0;

         for (int column = 0; column < columns; column++) {
            int cell = row * columns + column;
            if (weights[column] != -1 && this.mTableItemSourceTexts[item] != null && this.mTableItemSourceTexts[item][cell] != null) {
               texts[cell] = splitString(this.mTableItemSourceTexts[item][cell], this.mTextFont, widths[column] - 4);
            }

            if (this.mTableItemSprites[item] != null && this.mTableItemSprites[item][cell] != null) {
               rowHeight = Math.max(rowHeight, this.mTableItemSprites[item][cell].getHeight());
            } else if (this.mTableItemSourceTexts[item] != null && this.mTableItemSourceTexts[item][cell] != null) {
               rowHeight = Math.max(rowHeight, texts[cell] == null ? 0 : texts[cell].length * this.mTextFontHeight);
            }
         }

         heights[row] = (rowHeight / (this.mTextFontHeight + 1) + 1) * (this.mTextFontHeight + 1);
         height += heights[row];
      }

      int width = available;
      if (preferred) {
         int required = 0;
         int firstCell = 0;

         for (int column = 0; column < columns; column++) {
            if (weights[column] != -1) {
               int maximum = 0;

               for (int row = 0; row < rows; row++) {
                  int cell = firstCell + row;
                  if (this.mTableItemSprites[item] != null && this.mTableItemSprites[item][cell] != null) {
                     maximum = Math.max(maximum, this.mTableItemSprites[item][cell].getWidth());
                  } else if (texts[cell] != null) {
                     for (String text : texts[cell]) {
                        maximum = Math.max(maximum, this.mTextFont.stringWidth(text));
                     }
                  }
               }

               firstCell += rows;
               required = Math.max(required, (maximum * 100 + weights[column] + 399) / weights[column]);
            }
         }

         width = 0;
         leftover = required;

         for (int column = 0; column < columns; column++) {
            if (weights[column] != -1) {
               widths[column] = required * weights[column] / 100;
               leftover -= widths[column];
            }

            width += widths[column];
         }

         widths[columns - 1] = widths[columns - 1] + leftover;
         width += leftover;
      } else {
         this.mTableItemSourceTexts[item] = null;
      }

      this.mItemBounds[item * 4 + 2] = width + margin * 2;
      this.mItemBounds[item * 4 + 3] = height;
   }

   private void calculateTitleBarLayout(boolean preferred) {
      if (!this.mHasTitleBar) {
         this.mTitleBarHeight = 0;
      } else {
         int width = this.mTitleBarSprite == null ? 0 : this.mTitleBarSprite.getWidth() + 8;
         int height = this.mTitleBarSprite == null ? 0 : this.mTitleBarSprite.getHeight();
         this.mTitleBarImageAreaWidth = width;
         this.mTitleBarHeight = Math.max(this.mTitleBarFontHeight, height) + 6;
         int available = this.mTitleBarsOnTop ? this.mScreenMaxWidth : this.mWidth;
         if (this.mTitleBarSourceText != null) {
            this.mTitleBarText = new String[]{this.mTitleBarSourceText};
         }

         if (this.mTitleBarText == null) {
            this.mTitleBarTextAreaWidth = 0;
         } else {
            int textWidth = 0;

            for (String text : this.mTitleBarText) {
               textWidth = Math.max(textWidth, this.mTitleBarFont.stringWidth(text));
            }

            if (!preferred) {
               this.mTitleBarSourceText = null;
            }

            this.mTitleBarTextAreaWidth = Math.min(textWidth, available - width - 8) + 8;
            width += this.mTitleBarTextAreaWidth;
         }

         this.mTitleBarX = 0;
         if ((this.mTitleBarAlignment & 1) != 0) {
            this.mTitleBarX = available - width >> 1;
         } else if ((this.mTitleBarAlignment & 4) != 0) {
            this.mTitleBarX = available - width;
         }
      }
   }

   private void drawBackground(Graphics graphics, int i, int i2, int i3, int i4) {
      drawGradientRect(graphics, i, i2, i3, i4, this.mBackgroundColor, this.mBackgroundGradientColor, 1);
   }

   private void drawBorders(Graphics graphics, int i, int i2, int i3, int i4) {
      int clipX = graphics.getClipX();
      int clipY = graphics.getClipY();
      int clipWidth = graphics.getClipWidth();
      int clipHeight = graphics.getClipHeight();
      graphics.setClip(i, i2, i3, i4);
      graphics.setColor(255);
      graphics.drawRect(i, i2, i3 - 1, i4 - 1);
      graphics.drawRect(i + 1, i2 + 1, i3 - 3, i4 - 3);
      graphics.drawRect(i + 3, i2 + 3, i3 - 7, i4 - 7);
      graphics.setColor(this.mBackgroundColor);
      graphics.drawRect(i + 2, i2 + 2, i3 - 5, i4 - 5);
      graphics.setClip(clipX, clipY, clipWidth, clipHeight);
   }

   private void drawExampleHorizontalArrowsScreen(Graphics graphics, int i, int i2) {
   }

   public static void drawGradientRect(Graphics graphics, int i, int i2, int i3, int i4, int i5, int i6, int i7) {
      if (i5 == i6) {
         graphics.setColor(i5);
         graphics.fillRect(i, i2, i3, i4);
      } else {
         int i8 = (0xFF0000 & i5) >> 16;
         int i9 = (0xFF00 & i5) >> 8;
         int i10 = i5 & 0xFF;
         int i11 = ((0xFF0000 & i6) >> 16) - i8;
         int i12 = ((0xFF00 & i6) >> 8) - i9;
         int i13 = (i6 & 0xFF) - i10;
         int i14 = i7;

         for (int i15 = 0; i15 < i4; i15 += i14) {
            if (i15 + i14 > i4) {
               i14 = i4 - i15;
            }

            graphics.setColor(i15 * i11 / (i4 - 1) + i8, i15 * i12 / (i4 - 1) + i9, i15 * i13 / (i4 - 1) + i10);
            graphics.fillRect(i, i2 + i15, i3, i14);
         }
      }
   }

   private void drawInput(Graphics graphics, int i, int i2, int i3, int i4, int i5) {
   }

   private void drawItem(Graphics graphics, int i, int i2, int i3, int i4, int i5, int i6) {
      this.getItemXMargin(i);
      int i9 = this.mItemTextAreaWidths[i];
      int i10 = this.mItemImageAreaWidths[i];
      if (this.mScreenType == 1 && i9 > 0) {
         i10 = 0;
      } else if (this.mScreenType == 0 && i == this.mSelectedIndex) {
         graphics.setColor(this.mSelectedItemBackgroundColor);
         graphics.fillRect(this.mItemAreaX + i6, i3, this.mItemAreaWidth, i5);
      }

      int i11 = i2 + i10;
      int i7;
      int i8;
      if ((this.getItemAlignment(i) & 4) == 0) {
         i7 = i11;
         i8 = i2;
      } else if (this.mScreenType == 0) {
         int i12 = i2 + i4 - i9 - i10;
         i8 = i12 + i9;
         i7 = i12;
      } else {
         i8 = i2 + i4 - this.mItemImageAreaWidths[i];
         i7 = i2;
      }

      this.drawItemIcon(graphics, i, i8, i3, this.mItemImageAreaWidths[i], i5);
      this.drawItemText(graphics, i, i7, i3, i9, i5);
      if (this.mItemTypes[i] == 4) {
         this.drawSlider(graphics, i, i2, i3, i4, i5);
      } else if (this.mItemTypes[i] == 5) {
         this.drawSwitch(graphics, i, i2, i3, i4, i5);
      }
   }

   private void drawItemIcon(Graphics graphics, int item, int x, int y, int width, int height) {
      boolean title = item == -2;
      int clipX = 0;
      int clipY = 0;
      int clipWidth = 0;
      int clipHeight = 0;
      if (title) {
         clipX = graphics.getClipX();
         clipY = graphics.getClipY();
         clipWidth = graphics.getClipWidth();
         clipHeight = graphics.getClipHeight();
         graphics.setClip(0, 0, Toolkit.getScreenWidth(), Toolkit.getScreenHeight());
      }

      int animation = 0;
      if (!title && this.mScreenType != 1) {
         animation = this.mItemStates[item] == 0 ? (this.mSelectedIndex == item ? 1 : 0) : 2;
         if ((this.mItemTypes[item] == 4 || this.mItemTypes[item] == 5) && this.mSettingItemValues[item] == 0) {
            animation = 3;
         }
      }

      SpriteObject sprite = title ? this.mTitleBarSprite : this.mItemSprites[item];
      if (sprite != null) {
         if (animation != sprite.getCurrentAnimationIndex()) {
            sprite.setAnimation(animation, -2, true);
         }

         int drawX = x + (width - sprite.getWidth() >> 1);
         if (title || this.mScreenType == 0) {
            y += height - sprite.getHeight() >> 1;
         } else if (this.mItemTexts[item] != null && this.mItemTexts[item].length > this.mItemCornerRows[item]) {
            int textHeight = this.mItemCornerRows[item] * (this.mTextFontHeight + 1) - 1;
            if (textHeight < sprite.getHeight()) {
               textHeight++;
            }

            y += textHeight - sprite.getHeight() >> 1;
         }

         sprite.draw(drawX + sprite.getPivotX(), y + sprite.getPivotY());
      }

      if (title) {
         graphics.setClip(clipX, clipY, clipWidth, clipHeight);
      }
   }

   private void drawItemText(Graphics graphics, int i, int i2, int i3, int i4, int i5) {
      String[] strArr;
      int i6;
      if (i == -2) {
         strArr = this.mTitleBarText;
         i6 = 4;
      } else {
         int itemXMargin = this.getItemXMargin(i);
         strArr = this.mItemTexts[i];
         i6 = itemXMargin;
      }

      if (strArr != null) {
         int iMin = i2 + i6;
         int i7 = this.mTextFontHeight;
         ImageFont imageFont2 = this.mTextFont;
         ImageFont imageFont;
         int height;
         if (i == -2) {
            int i8 = this.mTitleBarFontHeight;
            imageFont = this.mTitleBarFont;
            height = i8;
         } else if (this.mScreenType != 0) {
            imageFont = imageFont2;
            height = i7;
         } else {
            ImageFont menuItemFont = this.getMenuItemFont(i);
            imageFont = menuItemFont;
            height = menuItemFont.getHeight();
         }

         if (i == -2 || this.mScreenType != 1) {
            int i9 = i3 + (i5 - height >> 1);
            int iStringWidth2 = imageFont.stringWidth(strArr[0]);
            boolean z = iStringWidth2 <= i4 - i6 * 2;
            int clipX = graphics.getClipX();
            int clipY = graphics.getClipY();
            int clipWidth = graphics.getClipWidth();
            int clipHeight = graphics.getClipHeight();
            if (!z) {
               int iMax = Math.max(clipX, iMin);
               int iMax2 = Math.max(clipY, i3);
               graphics.setClip(
                  iMax,
                  iMax2,
                  Math.max(iMax, Math.min(clipX + clipWidth, i4 - i6 * 2 + iMin)) - iMax,
                  Math.max(iMax2, Math.min(clipY + clipHeight, i3 + i5)) - iMax2
               );
               if (i == -2) {
                  if (this.mTitleBarScrollingOffset >= 0) {
                     iMin -= Math.min(iStringWidth2 - (this.mTitleBarTextAreaWidth - (i6 << 1)), this.mTitleBarScrollingOffset);
                  }
               } else if (i == this.mSelectedIndex && this.mSelectedItemScrollingOffset >= 0) {
                  iMin -= Math.min(iStringWidth2 - (this.mItemTextAreaWidths[i] - (i6 << 1)), this.mSelectedItemScrollingOffset);
               }
            }

            imageFont.drawString(strArr[0], iMin, i9, 20);
            graphics.setClip(clipX, clipY, clipWidth, clipHeight);
            return;
         }

         int i10 = height + 1;
         int clipY2 = graphics.getClipY();
         int clipHeight2 = graphics.getClipHeight();
         int i11 = i3 < clipY2 ? (clipY2 - i3) / i10 : 0;
         int i12 = clipY2 + clipHeight2 - (i11 * i10 + i3);
         int iMin2 = Math.min((i12 == 0 ? 0 : (i12 - 1) / i10 + 1) + i11, this.mItemTexts[i].length);
         int i13 = 0;
         int i14 = 0;
         if (this.mScreenType == 1) {
            i13 = this.mItemImageAreaWidths[i];
            i14 = this.mItemCornerRows[i];
         }

         int itemAlignment = this.getItemAlignment(i);

         for (int i15 = i11; i15 < iMin2; i15++) {
            String str = this.mItemTexts[i][i15];
            int i16 = i3 + i10 * i15;
            int iStringWidth;
            if (i15 < i14) {
               iStringWidth = (itemAlignment & 4) != 0 ? i2 + i4 - i13 - imageFont.stringWidth(str) : iMin + i13;
            } else if ((itemAlignment & 1) != 0) {
               iStringWidth = (i4 - imageFont.stringWidth(str) >> 1) - i6 + iMin;
            } else {
               iStringWidth = (itemAlignment & 4) != 0 ? i2 + i4 - imageFont.stringWidth(str) : iMin;
            }

            imageFont.drawString(str, iStringWidth, i16, 20);
         }
      }
   }

   private boolean drawScrollDownArrow() {
      return this.mScrollingAreaHeight > this.mItemAreaHeight && this.mVerticalScrollingOffset + this.mItemAreaHeight < this.mScrollingAreaHeight;
   }

   private boolean drawScrollUpArrow() {
      return this.mScrollingAreaHeight > this.mItemAreaHeight && this.mVerticalScrollingOffset > 0;
   }

   private void drawSlider(Graphics graphics, int i, int i2, int i3, int i4, int i5) {
      int itemXMargin = this.getItemXMargin(i);
      int i6 = i2 + i4 - this.mSettingItemTextAreaWidths[i] - this.mSettingItemImageAreaWidths[i];
      if ((this.getItemAlignment(i) & 4) == 0) {
         i2 = i6;
      }

      int i7 = this.mSettingItemValueCounts[i] - 1;
      int i8 = this.mSettingItemValues[i];
      int height = this.getMenuItemFont(i).getHeight();

      for (int i9 = 1; i9 <= i7; i9++) {
         int i10 = (i9 - 1) * 7 + i2 + itemXMargin;
         int i11 = (i5 + height >> 1) + i3;
         int i12 = height * i9 / i7;
         if (i8 >= i9) {
            graphics.setColor(16711680);
         } else {
            graphics.setColor(0);
         }

         graphics.fillRect(i10, i11 - i12, 6, i12);
      }
   }

   private void drawSubMenus(Graphics graphics, int i, int i2) {
   }

   private void drawSwitch(Graphics graphics, int item, int x, int y, int width, int height) {
      int margin = this.getItemXMargin(item);
      int textWidth = this.mSettingItemTextAreaWidths[item];
      int imageWidth = this.mSettingItemImageAreaWidths[item];
      if ((this.getItemAlignment(item) & 4) == 0) {
         x += width - textWidth - imageWidth;
      }

      int value = this.mSettingItemValues[item];
      SpriteObject sprite = this.mSwitchItemSprites[item];
      if (sprite != null) {
         if (value != sprite.getCurrentAnimationIndex()) {
            sprite.setAnimation(value, -2, true);
         }

         sprite.draw(x + sprite.getPivotX() + (imageWidth - sprite.getWidth() >> 1), y + sprite.getPivotY() + (height - sprite.getHeight() >> 1));
      }

      if (this.mSwitchItemTexts[item] != null) {
         ImageFont font = this.getMenuItemFont(item);
         int fontHeight = font.getHeight();
         String text = this.mSwitchItemTexts[item][value];
         if (text != null) {
            font.drawString(text, x + margin + imageWidth, y + (height - fontHeight >> 1), 20);
         }
      }
   }

   private void drawTable(Graphics graphics, int item, int x, int y, int width, int height) {
      int margin = this.getItemXMargin(item);
      int[] widths = this.mTableItemColumnWidths[item];
      int[] heights = this.mTableItemRowHeights[item];
      int[] alignments = this.mTableItemColumnAlignments[item];
      int columns = widths == null ? 0 : widths.length;
      int cell = 0;
      if (heights != null) {
         for (int rowHeight : heights) {
            int cellX = x + margin;

            for (int column = 0; column < columns; column++) {
               int cellWidth = widths[column] - 4;
               int cellHeight = rowHeight - 1;
               int alignment = alignments[column];
               int drawX = cellX + 2;
               SpriteObject sprite = this.mTableItemSprites[item] == null ? null : this.mTableItemSprites[item][cell];
               if (sprite != null) {
                  if ((alignment & 1) != 0) {
                     drawX += cellWidth - sprite.getWidth() >> 1;
                  } else if ((alignment & 4) != 0) {
                     drawX += cellWidth - sprite.getWidth();
                  }

                  int drawY = y;
                  if ((alignment & 2) != 0) {
                     drawY += cellHeight - sprite.getHeight() >> 1;
                  } else if ((alignment & 8) != 0) {
                     drawY += cellHeight - sprite.getHeight();
                  }

                  sprite.draw(drawX + sprite.getPivotX(), drawY + sprite.getPivotY());
               } else if (this.mTableItemTexts[item] != null && this.mTableItemTexts[item][cell] != null) {
                  String[] lines = this.mTableItemTexts[item][cell];
                  int drawY = y;
                  if ((alignment & 2) != 0) {
                     drawY += cellHeight - this.mTextFontHeight * lines.length >> 1;
                  } else if ((alignment & 8) != 0) {
                     drawY += cellHeight - this.mTextFontHeight * lines.length;
                  }

                  for (String text : lines) {
                     int textX = drawX;
                     if ((alignment & 1) != 0) {
                        textX += cellWidth - this.mTextFont.stringWidth(text) >> 1;
                     } else if ((alignment & 4) != 0) {
                        textX += cellWidth - this.mTextFont.stringWidth(text);
                     }

                     this.mTextFont.drawString(text, textX, drawY, 20);
                     drawY += this.mTextFontHeight;
                  }
               }

               cellX += widths[column];
               cell++;
            }

            y += rowHeight;
         }
      }
   }

   private void drawTitleBar(Graphics graphics, int i, int i2, int i3) {
      int i4 = this.mTitleBarTextAreaWidth;
      int i5 = this.mTitleBarImageAreaWidth;
      this.drawItemIcon(graphics, -2, i, i2, i5, i3);
      this.drawItemText(graphics, -2, i + i5, i2, i4, i3);
   }

   private void drawTitleBarBackground(Graphics graphics, int i, int i2, int i3, int i4) {
      drawGradientRect(graphics, i, i2, i3, i4, this.mTitleBarBackgroundColor, this.mTitleBarBackgroundGradientColor, 2);
   }

   private int getItemAlignment(int i) {
      return this.mItemAlignment;
   }

   private int getItemXMargin(int i) {
      return 4;
   }

   private ImageFont getMenuItemFont(int i) {
      return this.mItemFont != null && this.mItemFont[i] != null ? this.mItemFont[i] : this.mSelectionFont;
   }

   private int getNextSelectableItemIndex(int i) {
      if (this.mMaxItemCount == 0) {
         return -1;
      }

      int i2 = this.mMaxItemCount;

      while (true) {
         i = (i + 1) % this.mMaxItemCount;
         int i3 = i2 - 1;
         if (i2 <= 0) {
            return -1;
         }

         if (this.isItemSelectable(i)) {
            return i;
         }

         i2 = i3;
      }
   }

   private int getPreviousSelectableItemIndex(int i) {
      if (this.mMaxItemCount == 0) {
         return -1;
      }

      if (i == -1) {
         i = 0;
      }

      int i2 = this.mMaxItemCount;

      while (true) {
         i = (this.mMaxItemCount + i - 1) % this.mMaxItemCount;
         int i3 = i2 - 1;
         if (i2 <= 0) {
            return -1;
         }

         if (this.isItemSelectable(i)) {
            return i;
         }

         i2 = i3;
      }
   }

   private boolean isBlinkStateHidden(int i) {
      return false;
   }

   private boolean isItemSelectable(int i) {
      return this.mItemTypes[i] == 0 || this.mItemTypes[i] == 4 || this.mItemTypes[i] == 5;
   }

   private void selectionChanged() {
      Toolkit.removeAllSoftKeys();
      boolean locked = this.mItemStates != null
         && this.mSelectedIndex < this.mItemStates.length
         && this.mSelectedIndex != -1
         && this.mItemStates[this.mSelectedIndex] == 1;
      if (this.mSoftKeys[0] != -1 && !locked) {
         Toolkit.setSoftKey(this.mSoftKeys[0], 0);
      }

      if (this.mSoftKeys[1] != -1) {
         Toolkit.setSoftKey(this.mSoftKeys[1], 0);
      }

      this.mVerticalScrollingOffset = 0;
      this.mCurrentPage = 0;
      this.mSelectedItemUpdateTimer = 0;
   }

   private boolean setClipIfVisible(int i, int i2, int i3, int i4, Graphics graphics) {
      if (i < this.mScreenMinX + this.mScreenMaxWidth
         && i + i3 > this.mScreenMinX
         && i2 < this.mScreenMinY + this.mScreenMaxHeight
         && i2 + i4 > this.mScreenMinY) {
         graphics.setClip(i, i2, i3, i4);
         return true;
      } else {
         return false;
      }
   }

   private void setInputEditingMode(boolean z) {
   }

   private void setSize(int i, int i2, boolean z) {
      if (this.mEnableBorders) {
         i -= 8;
         i2 -= 8;
      }

      this.mWidth = i;
      this.mHeight = i2;
      this.mItemAreaX = this.mScreenXMargin;
      this.mItemAreaWidth = i - (this.mScreenXMargin << 1);
      this.calculateTitleBarLayout(z);
      this.calculateItemLayout(z);
      if (!z) {
         if (this.mScreenType == 0) {
            this.mSelectionItemsPerScreen = this.mItemAreaHeight / this.mSelectionItemHeight;
            if (this.mItemAreaHeight < this.mScrollingAreaHeight) {
               this.mItemAreaY = this.mItemAreaY + (this.mItemAreaHeight - this.mSelectionItemsPerScreen * this.mSelectionItemHeight >> 1);
            }
         }

         if (this.mSelectedIndex == -1) {
            this.mSelectedIndex = 0;
         }

         this.mSelectedIndex = this.getNextSelectableItemIndex(this.mSelectedIndex);
         if (this.mSelectedIndex != -1) {
            this.mSelectedIndex = this.getPreviousSelectableItemIndex(this.mSelectedIndex);
         }

         this.mVerticalScrollingOffset = 0;
         this.mCurrentPage = 0;
         if (this.mSelectedIndex != -1) {
            this.setSelectedItem(this.mSelectedIndex);
         }

         this.mLastDrawX = Integer.MAX_VALUE;
         this.mLastDrawY = Integer.MAX_VALUE;
      }
   }

   private static String[] splitString(String text, int width, int indentWidth, int indentRows) {
      int available = width - smSplitStringFont.getEmptyStringWidth();
      Vector<String> lines = new Vector<>();
      int length = text.length();
      if (length == 0) {
         lines.addElement(text);
      }

      int start = 0;
      int line = 0;
      int separator = 0;

      while (separator < length) {
         separator = text.indexOf("\\n", start);
         if (separator == -1) {
            separator = length;
         }

         boolean complete;
         do {
            int limit = available - (line < indentRows ? indentWidth : 0);
            int measured = 0;
            int space = -1;
            int cursor = start;

            while (measured <= limit && cursor < separator) {
               char character = text.charAt(cursor++);
               measured += smSplitStringFont.charWidth(character);
               if (character == ' ') {
                  space = cursor;
               }
            }

            complete = cursor == separator && measured <= limit;
            int end;
            if (complete) {
               end = separator;
            } else if (space != -1) {
               end = space - 1;
            } else {
               end = cursor - 1;

               for (int scan = end; scan > start; scan--) {
                  char character = text.charAt(scan);
                  if (character == '.' || character == '/') {
                     end = scan + 1;
                     break;
                  }
               }
            }

            lines.addElement(text.substring(start, end));
            line++;
            int skip = complete && end < length ? 2 : (space != -1 ? 1 : 0);
            start = end + skip;
         } while (!complete);
      }

      String[] result = new String[lines.size()];
      lines.copyInto(result);
      smSplitStringFont = null;
      return result;
   }

   public static String[] splitString(String str, ImageFont imageFont, int i) {
      smSplitStringFont = imageFont;
      return splitString(str, i, 0, 0);
   }

   private void updateHorizontalScrolling(int i, int i2, int i3) {
      int i4 = i2 - i3;
      if (i4 <= 0) {
         this.mTemporaryScrollingOffset = 0;
      } else {
         int i5 = -25;
         int i6 = i4 + 25;

         for (this.mTemporaryUpdateTimer -= i; this.mTemporaryUpdateTimer < 0; this.mTemporaryUpdateTimer += 40) {
            if (this.mTemporaryScrollingDirection == 1) {
               this.mTemporaryScrollingOffset++;
            } else {
               this.mTemporaryScrollingOffset--;
            }

            if (this.mTemporaryScrollingOffset > i6) {
               this.mTemporaryScrollingOffset = i6;
               this.mTemporaryScrollingDirection = 0;
            } else if (this.mTemporaryScrollingOffset < i5) {
               this.mTemporaryScrollingOffset = i5;
               this.mTemporaryScrollingDirection = 1;
            }
         }
      }
   }

   private void updateItem(int i, int i2) {
      if (this.mItemSprites[i] != null) {
         this.mItemSprites[i].logicUpdate(i2);
      }

      if (this.mSwitchItemSprites != null && this.mSwitchItemSprites[i] != null) {
         this.mSwitchItemSprites[i].logicUpdate(i2);
      }

      if (i == this.mSelectedIndex && this.mItemTexts[i] != null) {
         this.mTemporaryUpdateTimer = this.mSelectedItemUpdateTimer;
         String[] strArr = this.mItemTexts[i];
         if (strArr != null) {
            this.mTemporaryScrollingOffset = this.mSelectedItemScrollingOffset;
            this.mTemporaryScrollingDirection = this.mSelectedItemScrollingDirection;
            this.updateHorizontalScrolling(i2, this.getMenuItemFont(i).stringWidth(strArr[0]), this.mItemTextAreaWidths[i] - (this.getItemXMargin(i) << 1));
            this.mSelectedItemScrollingOffset = this.mTemporaryScrollingOffset;
            this.mSelectedItemScrollingDirection = this.mTemporaryScrollingDirection;
         }

         this.mSelectedItemUpdateTimer = this.mTemporaryUpdateTimer;
      }
   }

   private void updateTitleBar(int i) {
      if (this.mTitleBarSprite != null) {
         this.mTitleBarSprite.logicUpdate(i);
      }

      String[] strArr = this.mTitleBarText != null ? this.mTitleBarText : null;
      if (strArr != null) {
         this.mTemporaryUpdateTimer = this.mTitleBarUpdateTimer;
         this.mTemporaryScrollingOffset = this.mTitleBarScrollingOffset;
         this.mTemporaryScrollingDirection = this.mTitleBarScrollingDirection;
         this.updateHorizontalScrolling(i, this.mTitleBarFont.stringWidth(strArr[0]), this.mTitleBarTextAreaWidth - 8);
         this.mTitleBarScrollingOffset = this.mTemporaryScrollingOffset;
         this.mTitleBarScrollingDirection = this.mTemporaryScrollingDirection;
         this.mTitleBarUpdateTimer = this.mTemporaryUpdateTimer;
      }
   }

   private void updateVisibleLine(int i, int i2) {
      this.mTemporaryUpdateTimer -= i;
      if (this.mTemporaryUpdateTimer < 0) {
         this.mTemporaryVisibleLine++;
         if (this.mTemporaryVisibleLine >= i2) {
            this.mTemporaryVisibleLine = 0;
         }

         this.mTemporaryUpdateTimer += 2000;
      }
   }

   @Override
   public void destroyItem(int i) {
      if (this.mItemTypes[i] != -1 && this.mSelectedIndex == i) {
         this.mSelectedIndex = this.getNextSelectableItemIndex(i);
         if (this.mSelectedIndex < i) {
            this.mSelectedIndex = this.getPreviousSelectableItemIndex(i);
         }
      }

      if (this.mItemSprites != null && this.mItemSprites[i] != null) {
         this.mItemSprites[i].freeResources();
         this.mItemSprites[i] = null;
      }

      if (this.mSwitchItemSprites != null && this.mSwitchItemSprites[i] != null) {
         this.mSwitchItemSprites[i].freeResources();
         this.mSwitchItemSprites[i] = null;
      }

      this.mItemSourceTexts[i] = null;
      this.mItemTexts[i] = null;
      this.mItemTypes[i] = -1;
      if (this.mSwitchItemTexts != null) {
         this.mSwitchItemTexts[i] = null;
      }
   }

   public void doDraw(Graphics graphics) {
      this.doDraw(graphics, this.mX, this.mY);
   }

   @Override
   public void doDraw(Graphics graphics, int i, int i2) {
      int i3;
      if (this.mEnableBorders) {
         i3 = i + 4;
         i2 += 4;
      } else {
         i3 = i;
      }

      int clipX = graphics.getClipX();
      int clipY = graphics.getClipY();
      int clipWidth = graphics.getClipWidth();
      int clipHeight = graphics.getClipHeight();
      int i12 = 0;
      int i13 = 0;
      int i14 = 0;
      int i15 = 0;
      int i16 = this.mWidth;
      int i17 = this.mHeight;
      if (this.mHasTitleBar) {
         int i9;
         int i10;
         int i11;
         if (this.mTitleBarsOnTop) {
            i11 = this.mScreenMinX;
            i10 = this.mScreenMinY;
            i9 = this.mScreenMaxWidth;
         } else {
            i9 = this.mWidth;
            i10 = i2;
            i11 = i3;
         }

         i15 = this.mTitleBarHeight;
         i14 = i9;
         int i18 = i10;
         i12 = i11;
         i13 = i18;
      }

      int i4;
      int i5;
      if (this.mHasTitleBar && this.mSeparateTitleBarBackground) {
         if (this.setClipIfVisible(i12, i13, i14, i15, graphics)) {
            this.drawTitleBarBackground(graphics, i12, i13, i14, i15);
         }

         i5 = i2 + i15;
         i4 = i17 - i15;
      } else {
         i4 = i17;
         i5 = i2;
      }

      if (this.mEnableBackground && this.setClipIfVisible(i3, i5, i16, i4, graphics)) {
         this.drawBackground(graphics, i3, i5, i16, i4);
      }

      if (this.mEnableBorders) {
         int i7;
         int i8;
         if (!this.mTitleBarsOnTop && this.mSeparateTitleBarBackground) {
            i7 = i4 + i15;
            i8 = i5 - i15;
         } else {
            i7 = i4;
            i8 = i5;
         }

         this.drawBorders(graphics, i3 - 4, i8 - 4, i16 + 8, i7 + 8);
      }

      int i6;
      if (this.mHasTitleBar) {
         int i19 = i12 + this.mTitleBarX;
         if (this.setClipIfVisible(i19, i13, i14, this.mTitleBarHeight, graphics)) {
            this.drawTitleBar(graphics, i19, i13, i15);
         }

         i6 = this.mTitleBarHeight + i2;
      } else {
         i6 = i2;
      }

      graphics.setClip(i3, i6, this.mWidth, this.mHeight - this.mTitleBarHeight);
      if (this.mScrollingAreaHeight < this.mItemAreaHeight) {
         if ((this.mItemAlignment & 2) != 0) {
            i2 += this.mItemAreaHeight - this.mScrollingAreaHeight >> 1;
         } else if ((this.mItemAlignment & 8) != 0) {
            i2 += this.mItemAreaHeight - this.mScrollingAreaHeight;
         }
      }

      if (this.mScreenType == 0) {
         for (int i20 = 0; i20 < this.mMaxItemCount; i20++) {
            int i21 = this.mItemAreaX + i3 + this.mItemBounds[i20 * 4 + 0];
            int i22 = this.mItemBounds[i20 * 4 + 1] - this.mVerticalScrollingOffset;
            int i23 = this.mItemBounds[i20 * 4 + 2];
            int i24 = this.mItemBounds[i20 * 4 + 3];
            if (i22 >= 0 && i22 + i24 <= this.mItemAreaHeight) {
               int i25 = i22 + this.mItemAreaY + i2;
               if (this.setClipIfVisible(i3 + this.mItemAreaX, i2 + this.mItemAreaY, this.mItemAreaWidth, this.mItemAreaHeight, graphics)) {
                  this.drawItem(graphics, i20, i21, i25, i23, i24, i3);
               }
            }
         }
      } else if (this.mScreenType == 1) {
         for (int i26 = 0; i26 < this.mMaxItemCount; i26++) {
            int i27 = this.mItemAreaX + this.mItemBounds[i26 * 4 + 0];
            int i28 = this.mItemAreaY + this.mItemBounds[i26 * 4 + 1] - this.mVerticalScrollingOffset;
            int i29 = this.mItemBounds[i26 * 4 + 2];
            int i30 = this.mItemBounds[i26 * 4 + 3];
            if (i28 + i30 >= this.mItemAreaY
               && i28 < this.mItemAreaY + this.mItemAreaHeight
               && this.setClipIfVisible(i3 + this.mItemAreaX, i2 + this.mItemAreaY, this.mItemAreaWidth, this.mItemAreaHeight, graphics)) {
               this.drawItem(graphics, i26, i3 + i27, i28 + i2, i29, i30, i3);
            }
         }
      }

      if (this.drawScrollUpArrow()) {
         int i31 = this.mScrollArrowX + i3;
         int i32 = this.mScrollArrowUpY + i2;
         int width = i31 - (smScrollImage.getWidth() >> 1);
         int height = i32 - (smScrollImage.getHeight() >> 2);
         graphics.setClip(width, height, smScrollImage.getWidth(), smScrollImage.getHeight() >> 1);
         graphics.drawImage(smScrollImage, width, height, 20);
      }

      if (this.drawScrollDownArrow()) {
         int i33 = this.mScrollArrowX + i3;
         int i34 = this.mScrollArrowDownY + i2;
         int width2 = i33 - (smScrollImage.getWidth() >> 1);
         int height2 = i34 - (smScrollImage.getHeight() >> 2);
         graphics.setClip(width2, height2, smScrollImage.getWidth(), smScrollImage.getHeight() >> 1);
         graphics.drawImage(smScrollImage, width2, height2 - (smScrollImage.getHeight() >> 1), 20);
      }

      graphics.setClip(clipX, clipY, clipWidth, clipHeight);
      this.mLastDrawX = i3;
      this.mLastDrawY = i2;
   }

   @Override
   public void drawLoadingScreen(Graphics graphics, String[] strArr, int i) {
   }

   public int getHeight() {
      return this.mHeight;
   }

   @Override
   public int getItemIntValue(int i) {
      return this.mSettingItemValues[i];
   }

   @Override
   public String getItemStringValue(int i) {
      return null;
   }

   public int getPreferredHeight(int i, int i2) {
      this.setSize(i, i2, true);
      int iMin = Math.min(this.mItemAreaHeight, this.mScrollingAreaHeight);
      int i3;
      if (this.mScreenType == 1) {
         int i4 = this.mMaxItemCount;
         boolean z = false;

         while (true) {
            int i5 = i4 - 1;
            if (i5 < 0) {
               if (z) {
                  i5 = this.mTextFontHeight + 1;
                  i3 = iMin / i5 * i5;
                  if (i3 < iMin) {
                     i3 += i5;
                  }
               } else {
                  i3 = iMin;
               }
               break;
            }

            z = z || this.mItemTexts[i5] != null;
            i4 = i5;
         }
      } else if (this.mScreenType == 0) {
         int i7 = this.mSelectionItemHeight;
         i3 = i7 * (iMin / i7);
      } else {
         i3 = iMin;
      }

      int i8 = this.mHeight - (this.mItemAreaHeight - i3);
      return this.mEnableBorders ? i8 + 8 : i8;
   }

   public int getPreferredWidth(int i) {
      this.setSize(i, Integer.MAX_VALUE, true);
      int iMax = !this.mTitleBarsOnTop ? Math.max(0, this.mTitleBarTextAreaWidth + this.mTitleBarImageAreaWidth) : 0;
      int iMax2 = 0;

      for (int i2 = 0; i2 < this.mMaxItemCount; i2++) {
         iMax2 = Math.max(iMax2, this.mItemBounds[i2 * 4 + 2]);
      }

      int iMax3 = Math.max(iMax, this.mWidth - (this.mItemAreaWidth - iMax2));
      return this.mEnableBorders ? iMax3 + 8 : iMax3;
   }

   @Override
   public int getSelectedItem() {
      return this.mSelectedIndex;
   }

   public int getSelectionItemState(int i) {
      return this.mItemStates[i];
   }

   public int getStyle() {
      return this.mStyle;
   }

   public int getWidth() {
      return this.mWidth;
   }

   public int getX() {
      return this.mLastDrawX;
   }

   public int getY() {
      return this.mLastDrawY;
   }

   @Override
   public void keyEventOccurred(int key, int type) {
      int event = -1;
      if (!this.mNewMenuEventAvailable) {
         switch (type) {
            case 0:
               int action = Toolkit.getToolkitGameAction(key);
               if (this.mScreenType == 0
                  && this.mSelectedIndex != -1
                  && this.mItemStates[this.mSelectedIndex] != 1
                  && (this.mItemTypes[this.mSelectedIndex] == 4 || this.mItemTypes[this.mSelectedIndex] == 5)) {
                  int value = this.mSettingItemValues[this.mSelectedIndex];
                  if (action == 52) {
                     this.mSettingItemValues[this.mSelectedIndex]--;
                  } else if (action == 54) {
                     this.mSettingItemValues[this.mSelectedIndex]++;
                  }

                  int count = this.mSettingItemValueCounts[this.mSelectedIndex];
                  if (this.mItemTypes[this.mSelectedIndex] == 5) {
                     this.mSettingItemValues[this.mSelectedIndex] = (this.mSettingItemValues[this.mSelectedIndex] + count) % count;
                  } else {
                     this.mSettingItemValues[this.mSelectedIndex] = Math.max(Math.min(this.mSettingItemValues[this.mSelectedIndex], count - 1), 0);
                  }

                  if (value != this.mSettingItemValues[this.mSelectedIndex]) {
                     event = 1;
                  }
               }

               if (action != 50 && action != 56) {
                  if (action == 53) {
                     if (this.mScreenType != 0) {
                        if (this.mSoftKeys[0] != -1) {
                           key = this.mSoftKeys[0];
                           event = 0;
                        }
                     } else if (this.mSelectedIndex != -1 && this.mItemStates[this.mSelectedIndex] != 1) {
                        int itemType = this.mItemTypes[this.mSelectedIndex];
                        if (itemType == 0) {
                           event = 1;
                        } else if (itemType == 4 || itemType == 5) {
                           this.mSettingItemValues[this.mSelectedIndex] = (this.mSettingItemValues[this.mSelectedIndex] + 1)
                              % this.mSettingItemValueCounts[this.mSelectedIndex];
                           event = 1;
                        }
                     }
                  }
               } else if (this.mScreenType == 0) {
                  int selected = action == 50 ? this.getPreviousSelectableItemIndex(this.mSelectedIndex) : this.getNextSelectableItemIndex(this.mSelectedIndex);
                  if (selected != -1) {
                     this.setSelectedItem(selected);
                     this.selectionChanged();
                  }
               } else if (this.mScreenType == 1) {
                  this.mScrollingEvent = action == 50 ? 2 : 1;
                  this.mEnsureScrollOnce = 0;
               }
               break;
            case 1:
               if (this.mEnsureScrollOnce != 1) {
                  this.mEnsureScrollOnce = 2;
                  return;
               }

               this.mScrollingEvent = 0;
            case 2:
            default:
               break;
            case 3:
               if (this.mScreenType != 0) {
                  event = 0;
               } else {
                  event = key == this.mSoftKeys[1] ? 0 : -1;
                  if (this.mSelectedIndex != -1 && this.mItemStates[this.mSelectedIndex] != 1 && key == this.mSoftKeys[0]) {
                     int itemType = this.mItemTypes[this.mSelectedIndex];
                     if (itemType == 4 || itemType == 5) {
                        this.mSettingItemValues[this.mSelectedIndex] = (this.mSettingItemValues[this.mSelectedIndex] + 1)
                           % this.mSettingItemValueCounts[this.mSelectedIndex];
                     }

                     event = 1;
                  }
               }
         }
      }

      if (event != -1) {
         this.mNewMenuEventAvailable = true;
         this.mLastMenuEvent[0] = event;
         this.mLastMenuEvent[1] = event == 1 ? this.mItemEvents[this.mSelectedIndex] : key;
      }
   }

   @Override
   public int[] logicUpdate(int i) {
      if (this.mScrollingEvent != 0 && this.mScrollingAreaHeight > this.mItemAreaHeight) {
         this.mScrollingTimer += i;
         int i2 = this.mScrollingTimer * (this.mTextFontHeight + 1) / 300;
         this.mScrollingTimer = this.mScrollingTimer - i2 * 300 / (this.mTextFontHeight + 1);
         if (this.mScrollingEvent == 2) {
            this.mVerticalScrollingOffset -= i2;
         } else if (this.mScrollingEvent == 1) {
            this.mVerticalScrollingOffset += i2;
         }

         if (this.mEnsureScrollOnce == 2) {
            this.mScrollingEvent = 0;
         } else {
            this.mEnsureScrollOnce = 1;
         }

         this.mVerticalScrollingOffset = Math.max(this.mVerticalScrollingOffset, 0);
         this.mVerticalScrollingOffset = Math.min(this.mVerticalScrollingOffset, this.mScrollingAreaHeight - this.mItemAreaHeight);
      }

      if (this.mHasTitleBar) {
         this.updateTitleBar(i);
      }

      for (int i3 = 0; i3 < this.mMaxItemCount; i3++) {
         this.updateItem(i3, i);
      }

      if (!this.mNewMenuEventAvailable) {
         return null;
      }

      this.mNewMenuEventAvailable = false;
      return this.mLastMenuEvent;
   }

   @Override
   public void pointerEventOccurred(int i, int i2, int i3) {
      int i4 = i - this.mLastDrawX;
      int i5 = i2 - this.mLastDrawY;
      if (i3 != 0) {
         if (i3 == 1) {
            this.mScrollingEvent = 0;
         }
      } else {
         if (this.mScrollingAreaHeight > this.mItemAreaHeight) {
            if (i4 >= this.mScrollArrowX - smScrollImage.getWidth()
               && i4 < this.mScrollArrowX + smScrollImage.getWidth()
               && i5 >= this.mScrollArrowUpY - (smScrollImage.getHeight() >> 1)
               && i5 < this.mScrollArrowUpY + (smScrollImage.getHeight() >> 1)) {
               if (this.mScreenType != 0) {
                  this.mScrollingEvent = 2;
                  this.mEnsureScrollOnce = 1;
                  return;
               }

               int previousSelectableItemIndex = this.getPreviousSelectableItemIndex(this.mSelectedIndex);
               if (previousSelectableItemIndex != -1) {
                  this.setSelectedItem(previousSelectableItemIndex);
                  this.selectionChanged();
                  return;
               }

               return;
            }

            if (i4 >= this.mScrollArrowX - smScrollImage.getWidth()
               && i4 < this.mScrollArrowX + smScrollImage.getWidth()
               && i5 >= this.mScrollArrowDownY - (smScrollImage.getHeight() >> 1)
               && i5 < this.mScrollArrowDownY + (smScrollImage.getHeight() >> 1)) {
               if (this.mScreenType != 0) {
                  this.mScrollingEvent = 1;
                  this.mEnsureScrollOnce = 1;
                  return;
               }

               int nextSelectableItemIndex = this.getNextSelectableItemIndex(this.mSelectedIndex);
               if (nextSelectableItemIndex != -1) {
                  this.setSelectedItem(nextSelectableItemIndex);
                  this.selectionChanged();
                  return;
               }

               return;
            }
         }

         if (this.mScreenType == 0) {
            int i6 = this.mSelectionItemsPerScreen * this.mSelectionItemHeight;
            if (i4 < this.mItemAreaX || i4 >= this.mItemAreaX + this.mItemAreaWidth || i5 < this.mItemAreaY || i5 >= this.mItemAreaY + i6) {
               return;
            }

            for (int i7 = 0; i7 < this.mMaxItemCount; i7++) {
               int i8 = this.mItemAreaY + this.mItemBounds[i7 * 4 + 1] - this.mVerticalScrollingOffset;
               int i9 = this.mItemBounds[i7 * 4 + 3];
               if (i5 >= i8 && i5 < i8 + i9) {
                  if (this.mSelectedIndex != i7) {
                     this.setSelectedItem(i7);
                     this.selectionChanged();
                     this.setInputEditingMode(false);
                     return;
                  }

                  if (this.mItemStates[this.mSelectedIndex] == 1) {
                     return;
                  }

                  boolean z;
                  if (this.mItemTypes[i7] == 0) {
                     z = true;
                  } else if (this.mItemTypes[i7] != 4 && this.mItemTypes[i7] != 5) {
                     z = false;
                  } else {
                     this.mSettingItemValues[i7] = (this.mSettingItemValues[i7] + 1) % this.mSettingItemValueCounts[i7];
                     z = true;
                  }

                  if (z) {
                     this.mNewMenuEventAvailable = true;
                     this.mLastMenuEvent[0] = 1;
                     this.mLastMenuEvent[1] = this.mItemEvents[i7];
                     return;
                  }

                  return;
               }
            }
         }
      }
   }

   @Override
   public void releaseScreen() {
      if (this.mItemTypes != null) {
         for (int i = 0; i < this.mMaxItemCount; i++) {
            this.destroyItem(i);
         }
      }

      if (this.mTitleBarSprite != null) {
         this.mTitleBarSprite.freeResources();
      }
   }

   public void setBounds(int i, int i2, int i3, int i4) {
      this.mX = 0;
      this.mY = 0;
      this.mLastDrawX = 0;
      this.mLastDrawY = 0;
      this.setSize(i3, i4);
      this.mX = i;
      this.mY = i2;
      this.mLastDrawX = i;
      this.mLastDrawY = i2;
   }

   public void setExtendedItemParameters(int i, int i2, int i3) {
   }

   public void setImageFonts(ImageFont imageFont, ImageFont imageFont2, ImageFont imageFont3) {
      if (imageFont != null) {
         this.mTitleBarFont = imageFont;
         this.mTitleBarFontHeight = imageFont.getHeight();
      }

      if (imageFont2 != null) {
         this.mTextFont = imageFont2;
         this.mTextFontHeight = imageFont2.getHeight();
      }

      if (imageFont3 != null) {
         this.mSelectionFont = imageFont3;
         this.mSelectionFontHeight = imageFont3.getHeight();
      }
   }

   @Override
   public void setInputItem(int i, int i2, String str, String str2, Image[] imageArr, int i3, int i4) {
   }

   public void setInputItemDvc(int i, int i2, String str, String str2, SpriteObject spriteObject, int i3, int i4) {
   }

   @Override
   public void setItem(int i, int i2, String str, Image[] imageArr, int i3) {
      if (this.mItemTypes[i] != -1) {
         this.destroyItem(i);
      }

      this.mItemTypes[i] = i2;
      this.mItemEvents[i] = i3;
      this.mItemSourceTexts[i] = str;
   }

   public void setItemBlink(int i, int[] iArr) {
   }

   public void setItemDvc(int i, int i2, String str, SpriteObject spriteObject, int i3) {
      this.setItem(i, i2, str, null, i3);
      this.mItemSprites[i] = spriteObject;
   }

   public void setItemIntValue(int i, int i2) {
      this.mSettingItemValues[i] = i2;
   }

   public void setItemSpecificTitleBar(int i, String str) {
   }

   public void setMenuItemFont(int i, ImageFont imageFont) {
      if (this.mItemFont == null) {
         this.mItemFont = new ImageFont[this.mMaxItemCount];
      }

      this.mItemFont[i] = imageFont;
   }

   @Override
   public void setScreen(int i, int i2, int i3) {
      this.mScreenType = i;
      this.mSelectedIndex = -1;
      this.mVerticalScrollingOffset = 0;
      this.mScrollingEvent = 0;
      this.mItemAlignment = i3;
      this.mScrollingTimer = 0;
      this.mTitleBarAlignment = i3;
      this.mItemTypes = new int[i2];

      for (int i4 = 0; i4 < i2; i4++) {
         this.mItemTypes[i4] = -1;
      }

      this.mItemSourceTexts = new String[i2];
      this.mItemTexts = new String[i2][];
      this.mItemEvents = new int[i2];
      this.mItemStates = new int[i2];
      this.mItemSprites = new SpriteObject[i2];
      this.mMaxItemCount = i2;
      this.mTitleBarScrollingDirection = 1;
      this.mHasTitleBar = false;
      this.mSelectedItemScrollingDirection = 1;
      this.mSoftKeys[0] = -1;
      this.mSoftKeys[1] = -1;
      this.setImageFonts(smDefaultTitleBarFont, smDefaultTextFont, smDefaultSelectionFont);
      this.setStyle(2);
      this.mScreenMaxWidth = Toolkit.getScreenWidth();
      this.mScreenMaxHeight = Toolkit.getScreenHeight();
      this.mScreenSoftkeyAreaHeight = Toolkit.getSoftKeyAreaHeight();
   }

   public void setScreenBounds(int i, int i2, int i3, int i4, int i5) {
      this.mScreenMinX = i;
      this.mScreenMinY = i2;
      this.mScreenMaxWidth = i3;
      this.mScreenMaxHeight = i4;
      this.mScreenSoftkeyAreaHeight = i5;
   }

   @Override
   public void setSelectedItem(int i) {
      this.mSelectedIndex = this.getNextSelectableItemIndex(i - 1);
      if (this.mSelectedIndex != -1) {
         this.mSelectedItemUpdateTimer = 0;
         this.mSelectedItemScrollingOffset = 0;
         this.mSelectedItemScrollingDirection = 0;
         if (this.mScrollingAreaHeight > this.mItemAreaHeight) {
            int i2 = this.mVerticalScrollingOffset / this.mSelectionItemHeight;
            int i3 = this.mScrollingAreaHeight / this.mSelectionItemHeight;
            int i4 = this.mItemBounds[i * 4 + 1] / this.mSelectionItemHeight;
            this.mVerticalScrollingOffset = Math.min(
                  Math.max(Math.min(Math.max(i2, i4 - (this.mSelectionItemsPerScreen >> 1)), i4 - (this.mSelectionItemsPerScreen - 1 >> 1)), 0),
                  i3 - this.mSelectionItemsPerScreen
               )
               * this.mSelectionItemHeight;
         } else {
            this.mVerticalScrollingOffset = 0;
         }

         this.mSelectedItemScrollingDirection = 0;
         this.mSelectedItemScrollingOffset = 0;
      }
   }

   public void setSelectionItemState(int i, int i2) {
      this.mItemStates[i] = i2;
   }

   @Override
   public void setSize(int i, int i2) {
      this.setSize(i, i2, false);
   }

   public void setSliderItem(int item, String text, Image[] icons, int value, int maximum, int event) {
      this.setItem(item, 4, text, icons, event);
      if (this.mSettingItemValues == null) {
         this.mSettingItemValues = new int[this.mMaxItemCount];
         this.mSettingItemValueCounts = new int[this.mMaxItemCount];
      }

      this.mSettingItemValues[item] = value;
      this.mSettingItemValueCounts[item] = maximum + 1;
   }

   public void setSliderItemDvc(int item, String text, SpriteObject sprite, int value, int maximum, int event) {
      this.setSliderItem(item, text, null, value, maximum, event);
      this.mItemSprites[item] = sprite;
   }

   @Override
   public void setSoftkey(int i, int i2) {
      this.mSoftKeys[i2] = i;
   }

   public void setStyle(int i) {
      switch (i) {
         case 1:
            this.mEnableBackground = true;
            this.mBackgroundColor = 16777215;
            this.mBackgroundGradientColor = 16777215;
            this.mTitleBarsOnTop = false;
            this.mSeparateTitleBarBackground = false;
            this.mSelectedItemBackgroundColor = 9486550;
            this.mEnableBorders = false;
            this.mLooseItemAreaAlignment = true;
            break;
         case 2:
            this.mEnableBackground = false;
            this.mTitleBarsOnTop = true;
            this.mSelectedItemBackgroundColor = 16776960;
            this.mEnableBorders = false;
            this.mLooseItemAreaAlignment = true;
            break;
         case 3:
            this.mEnableBackground = true;
            this.mBackgroundColor = 13434828;
            this.mBackgroundGradientColor = 10092441;
            this.mTitleBarsOnTop = false;
            this.mSeparateTitleBarBackground = true;
            this.mTitleBarBackgroundColor = 13421823;
            this.mTitleBarBackgroundGradientColor = 10066431;
            this.mSelectedItemBackgroundColor = 16776960;
            this.mEnableBorders = false;
            this.mLooseItemAreaAlignment = true;
            break;
         case 4:
            this.mEnableBackground = true;
            this.mBackgroundColor = 13434828;
            this.mBackgroundGradientColor = 10092441;
            this.mTitleBarsOnTop = true;
            this.mSeparateTitleBarBackground = true;
            this.mTitleBarBackgroundColor = 13421823;
            this.mTitleBarBackgroundGradientColor = 10066431;
            this.mSelectedItemBackgroundColor = 16776960;
            this.mEnableBorders = true;
            this.mLooseItemAreaAlignment = false;
            break;
         case 5:
            this.mEnableBackground = true;
            this.mBackgroundColor = 13434828;
            this.mBackgroundGradientColor = 10092441;
            this.mTitleBarsOnTop = false;
            this.mSeparateTitleBarBackground = true;
            this.mSelectedItemBackgroundColor = 16776960;
            this.mEnableBorders = true;
            this.mLooseItemAreaAlignment = false;
      }

      if (this.mEnableBorders) {
         this.mScreenXMargin = 1;
         this.mScreenYMargin = 1;
      } else {
         this.mScreenXMargin = 4;
         this.mScreenYMargin = 4;
      }

      this.mStyle = i;
   }

   public void setSubMenuItem(int i, MenuObject menuObject) {
   }

   public void setSwitchItem(int item, String text, Image[] icons, String[] values, Image[] valueIcons, int event) {
      this.setItem(item, 5, text, icons, event);
      if (this.mSettingItemValues == null) {
         this.mSettingItemValues = new int[this.mMaxItemCount];
         this.mSettingItemValueCounts = new int[this.mMaxItemCount];
      }

      this.mSettingItemValues[item] = 0;
      this.mSettingItemValueCounts[item] = values == null ? 0 : values.length;
      if (this.mSwitchItemTexts == null) {
         this.mSwitchItemTexts = new String[this.mMaxItemCount][];
      }

      this.mSwitchItemTexts[item] = values;
      if (this.mSwitchItemSprites == null) {
         this.mSwitchItemSprites = new SpriteObject[this.mMaxItemCount];
      }
   }

   public void setSwitchItemDvc(int i, String str, SpriteObject spriteObject, String[] strArr, SpriteObject spriteObject2, int i2) {
      this.setSwitchItem(i, str, null, strArr, null, i2);
      this.mSettingItemValueCounts[i] = Math.max(this.mSettingItemValueCounts[i], spriteObject2 != null ? spriteObject2.getAnimationCount() : 0);
      this.mItemSprites[i] = spriteObject;
      this.mSwitchItemSprites[i] = spriteObject2;
   }

   public void setTableItem(int i, int[] iArr, int[] iArr2, String[] strArr, Image[] imageArr) {
   }

   public void setTableItemDvc(int i, int[] iArr, int[] iArr2, String[] strArr, SpriteObject[] spriteObjectArr) {
   }

   @Override
   public void setTitleBar(String str, Image image, int i) {
      this.mHasTitleBar = str != null || image != null;
      this.mTitleBarSourceText = str;
      this.mTitleBarAlignment = i;
   }

   public void setTitleBarDvc(String str, SpriteObject spriteObject, int i) {
      this.setTitleBar(str, null, i);
      this.mHasTitleBar = this.mHasTitleBar || spriteObject != null;
      this.mTitleBarSprite = spriteObject;
   }

   @Override
   public void setVisible() {
      this.selectionChanged();
      this.mScrollingEvent = 0;
      this.mTitleBarUpdateTimer = 2000;
      this.mTitleBarScrollingOffset = 0;
   }

   public void stopScrolling() {
      this.mScrollingEvent = 0;
   }
}
