package javax.microedition.lcdui;

import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

public class Font {
   public static final int FACE_MONOSPACE = 32;
   public static final int FACE_PROPORTIONAL = 64;
   public static final int FACE_SYSTEM = 0;
   public static final int FONT_INPUT_TEXT = 1;
   public static final int FONT_STATIC_TEXT = 0;
   public static final int SIZE_LARGE = 16;
   public static final int SIZE_MEDIUM = 0;
   public static final int SIZE_SMALL = 8;
   public static final int STYLE_BOLD = 1;
   public static final int STYLE_ITALIC = 2;
   public static final int STYLE_PLAIN = 0;
   public static final int STYLE_UNDERLINED = 3;
   private static final FontRenderContext FRC = new FontRenderContext(null, true, true);
   private final java.awt.Font awt;
   private final int face;
   private final int style;
   private final int size;
   private final int height;

   private Font(java.awt.Font awt, int face, int style, int size) {
      this.awt = awt;
      this.face = face;
      this.style = style;
      this.size = size;
      LineMetrics lm = awt.getLineMetrics("", FRC);
      this.height = (int)Math.ceil(lm.getAscent()) + (int)Math.ceil(lm.getDescent()) + Math.round(lm.getLeading());
   }

   public static Font getDefaultFont() {
      return new Font(new java.awt.Font("SansSerif", 0, 12), 0, 0, 12);
   }

   public static Font getFont(int face, int style, int size) {
      String family = switch (face) {
         case 0, 64 -> "SansSerif";
         case 32 -> "Monospaced";
         default -> throw new IllegalArgumentException("Face not supported");
      };

      int awtStyle = switch (style) {
         case 0, 3 -> 0;
         case 1 -> 1;
         case 2 -> 2;
         default -> throw new IllegalArgumentException("Style not supported");
      };

      int px = switch (size) {
         case 0 -> 16;
         case 16 -> 25;
         default -> size;
      };
      return new Font(new java.awt.Font(family, awtStyle, px), face, style, px);
   }

   public int charWidth(char ch) {
      return this.stringWidth(String.valueOf(ch));
   }

   public int getFace() {
      return this.face;
   }

   public int getHeight() {
      return this.height;
   }

   public int getSize() {
      return this.size;
   }

   public int getStyle() {
      return this.style;
   }

   public int stringWidth(String str) {
      return (int)this.awt.getStringBounds(str, FRC).getWidth();
   }
}
