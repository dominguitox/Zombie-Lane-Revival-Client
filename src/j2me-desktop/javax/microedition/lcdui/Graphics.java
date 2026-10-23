package javax.microedition.lcdui;

import zlport.stubs.NotPorted;

public class Graphics {
   public static final int BASELINE = 64;
   public static final int BOTTOM = 32;
   public static final int DOTTED = 1;
   public static final int HCENTER = 1;
   public static final int LEFT = 4;
   public static final int RIGHT = 8;
   public static final int SOLID = 0;
   public static final int TOP = 16;
   public static final int VCENTER = 2;
   private Font f = Font.getDefaultFont();

   protected Graphics() {
   }

   public Font getFont() {
      return this.f;
   }

   public void setFont(Font font) {
      if (font == null) {
         throw new NullPointerException();
      }

      this.f = font;
   }

   protected int getHAdjustForImage(int anchor, int width) {
      if ((anchor & 4) != 0) {
         return 0;
      } else if ((anchor & 8) != 0) {
         return -width;
      } else if ((anchor & 1) != 0) {
         return -width / 2;
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected int getVAdjustForImage(int anchor, int height) {
      if ((anchor & 16) != 0) {
         return 0;
      } else if ((anchor & 2) != 0) {
         return -height / 2;
      } else if ((anchor & 32) != 0) {
         return -height;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void clipRect(int x, int y, int width, int height) {
      NotPorted.method("javax.microedition.lcdui.Graphics#clipRect(int,int,int,int)");
   }

   public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      NotPorted.method("javax.microedition.lcdui.Graphics#drawArc(int,int,int,int,int,int)");
   }

   public void drawChars(char[] data, int offset, int length, int x, int y, int anchor) {
      NotPorted.method("javax.microedition.lcdui.Graphics#drawChars(char[],int,int,int,int,int)");
   }

   public void drawImage(Image img, int x, int y, int anchor) {
      NotPorted.method("javax.microedition.lcdui.Graphics#drawImage(Image,int,int,int)");
   }

   public void drawLine(int x1, int y1, int x2, int y2) {
      NotPorted.method("javax.microedition.lcdui.Graphics#drawLine(int,int,int,int)");
   }

   public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
      NotPorted.method("javax.microedition.lcdui.Graphics#drawRGB(int[],int,int,int,int,int,int,boolean)");
   }

   public void drawRect(int x, int y, int width, int height) {
      NotPorted.method("javax.microedition.lcdui.Graphics#drawRect(int,int,int,int)");
   }

   public void drawRegion(Image src, int xSrc, int ySrc, int width, int height, int transform, int xDest, int yDest, int anchor) {
      NotPorted.method("javax.microedition.lcdui.Graphics#drawRegion(Image,int,int,int,int,int,int,int,int)");
   }

   public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      NotPorted.method("javax.microedition.lcdui.Graphics#drawRoundRect(int,int,int,int,int,int)");
   }

   public void drawString(String str, int x, int y, int anchor) {
      NotPorted.method("javax.microedition.lcdui.Graphics#drawString(String,int,int,int)");
   }

   public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      NotPorted.method("javax.microedition.lcdui.Graphics#fillArc(int,int,int,int,int,int)");
   }

   public void fillRect(int x, int y, int width, int height) {
      NotPorted.method("javax.microedition.lcdui.Graphics#fillRect(int,int,int,int)");
   }

   public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      NotPorted.method("javax.microedition.lcdui.Graphics#fillRoundRect(int,int,int,int,int,int)");
   }

   public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
      NotPorted.method("javax.microedition.lcdui.Graphics#fillTriangle(int,int,int,int,int,int)");
   }

   public int getBlueComponent() {
      NotPorted.method("javax.microedition.lcdui.Graphics#getBlueComponent()");
      return 0;
   }

   public int getClipHeight() {
      NotPorted.method("javax.microedition.lcdui.Graphics#getClipHeight()");
      return 0;
   }

   public int getClipWidth() {
      NotPorted.method("javax.microedition.lcdui.Graphics#getClipWidth()");
      return 0;
   }

   public int getClipX() {
      NotPorted.method("javax.microedition.lcdui.Graphics#getClipX()");
      return 0;
   }

   public int getClipY() {
      NotPorted.method("javax.microedition.lcdui.Graphics#getClipY()");
      return 0;
   }

   public int getColor() {
      NotPorted.method("javax.microedition.lcdui.Graphics#getColor()");
      return 0;
   }

   public int getGreenComponent() {
      NotPorted.method("javax.microedition.lcdui.Graphics#getGreenComponent()");
      return 0;
   }

   public int getRedComponent() {
      NotPorted.method("javax.microedition.lcdui.Graphics#getRedComponent()");
      return 0;
   }

   public void setClip(int x, int y, int width, int height) {
      NotPorted.method("javax.microedition.lcdui.Graphics#setClip(int,int,int,int)");
   }

   public void setColor(int rgb) {
      NotPorted.method("javax.microedition.lcdui.Graphics#setColor(int)");
   }

   public void setColor(int red, int green, int blue) {
      NotPorted.method("javax.microedition.lcdui.Graphics#setColor(int,int,int)");
   }
}
