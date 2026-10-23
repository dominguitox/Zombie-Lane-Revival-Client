package com.dchoc.toolkit;

import javax.microedition.lcdui.Graphics;

public interface IRenderingPlatform {
   int BOTTOM = 32;
   int FILTER_ANTI_ALIAS_PRIMITIVES = 2;
   int FILTER_BILINEAR = 1;
   int HCENTER = 1;
   int LEFT = 4;
   int NEUTRAL_COLOR = -8355712;
   int NO_SCALING = 1024;
   int RENDER_MODE_ADD = 1;
   int RENDER_MODE_NORMAL = 0;
   int RENDER_MODE_SUB = 2;
   int RIGHT = 8;
   int SCALE_ACCURACY = 10;
   int TOP = 16;
   int TRANS_MIRROR_HORIZONTAL = 1;
   int TRANS_MIRROR_VERTICAL = 2;
   int TRANS_NONE = 0;
   int TRANS_ROTATE_180 = 8;
   int TRANS_ROTATE_270 = 16;
   int TRANS_ROTATE_90 = 4;
   int VCENTER = 2;

   void clipRect(int var1, int var2, int var3, int var4);

   void disableAllEffects();

   void drawImage(DChocImage var1, int var2, int var3, int var4, int var5);

   void drawLine(int var1, int var2, int var3, int var4);

   void drawRect(int var1, int var2, int var3, int var4);

   void fillBezier(int[] var1, int var2, int var3);

   void fillEllipse(int var1, int var2, int var3, int var4);

   void fillPolygon(int[] var1, int var2, int var3);

   void fillRect(int var1, int var2, int var3, int var4);

   void fillTriangle(int var1, int var2, int var3, int var4, int var5, int var6);

   int getBlurHeight();

   int getBlurWidth();

   int getClipHeight();

   int getClipWidth();

   int getClipX();

   int getClipY();

   int getColor();

   int getColorModification();

   int getFilters();

   Graphics getGraphicsContext();

   int getPivotX();

   int getPivotY();

   int getRenderMode();

   int getRotation();

   int getScaleHeight();

   int getScaleWidth();

   boolean isFilterEnabled(int var1);

   boolean isFrameUpToDate(DChocImage var1);

   void makeImageTransparent(DChocImage var1, int var2, int var3);

   void platformBeginFrame(Graphics var1);

   void platformEndFrame();

   void popParameters();

   void pushParameters();

   void setBlur(int var1, int var2);

   void setClip(int var1, int var2, int var3, int var4);

   void setColor(int var1);

   void setColor(int var1, int var2, int var3, int var4);

   void setColorARGB(int var1);

   void setColorKey(int var1);

   void setColorModification(int var1);

   void setColorModification(int var1, int var2, int var3, int var4);

   void setFiltering(int var1, boolean var2);

   void setFilters(int var1);

   void setGraphicsContext(DChocImage var1);

   void setGraphicsContext(Graphics var1);

   void setPivot(int var1, int var2);

   void setRenderMode(int var1);

   void setRotation(int var1);

   void setScale(int var1, int var2);
}
