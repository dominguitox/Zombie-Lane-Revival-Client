package javax.microedition.lcdui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import zlport.stubs.NotPorted;

public class Image {
   private static final int INVALID = -1;
   private final BufferedImage bmp;
   private int texHeight;
   private int texWidth;
   private int texture = -1;

   private Image(BufferedImage bmp) {
      this.bmp = bmp;
   }

   public static Image createImage(int width, int height) {
      NotPorted.method("javax.microedition.lcdui.Image#createImage(int,int)");
      return null;
   }

   public static Image createImage(String name) throws IOException {
      InputStream in = Image.class.getResourceAsStream(name);
      if (in == null) {
         File file = new File(name);
         if (file.exists()) {
            in = new FileInputStream(file);
         }
      }

      if (in == null) {
         throw new IOException();
      }

      try (InputStream stream = in) {
         return new Image(ImageIO.read(stream));
      }
   }

   public Graphics getGraphics() {
      NotPorted.method("javax.microedition.lcdui.Image#getGraphics()");
      return null;
   }

   public int getHeight() {
      return this.bmp.getHeight();
   }

   public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height) {
      if (x < 0) {
         throw new IllegalArgumentException("x must be >= 0");
      }

      if (y < 0) {
         throw new IllegalArgumentException("y must be >= 0");
      }

      if (x + width > this.getWidth()) {
         throw new IllegalArgumentException("x + width must be <= bitmap.width()");
      }

      if (y + height > this.getHeight()) {
         throw new IllegalArgumentException("y + height must be <= bitmap.height()");
      }

      if (Math.abs(scanlength) < width) {
         throw new IllegalArgumentException("abs(stride) must be >= width");
      }

      if (width != 0 && height != 0) {
         this.bmp.getRGB(x, y, width, height, rgbData, offset, scanlength);
      }
   }

   public int getTexHeight() {
      return this.texHeight;
   }

   public int getTexWidth() {
      return this.texWidth;
   }

   public int getTexture() {
      return this.texture;
   }

   public int getWidth() {
      return this.bmp.getWidth();
   }

   public boolean isMutable() {
      return false;
   }

   public void setTexHeight(int height) {
      this.texHeight = height;
   }

   public void setTexWidth(int width) {
      this.texWidth = width;
   }

   public void setTexture(int name) {
      this.texture = name;
   }
}
