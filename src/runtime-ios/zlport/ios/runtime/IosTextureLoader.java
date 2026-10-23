package zlport.ios.runtime;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.microedition.khronos.opengles.GL11;
import zlport.ios.IosTexture;

public final class IosTextureLoader {
   private static final int GL_TEXTURE_2D = 3553;
   private static final int GL_TEXTURE_MIN_FILTER = 10241;
   private static final int GL_TEXTURE_MAG_FILTER = 10240;
   private static final int GL_TEXTURE_WRAP_S = 10242;
   private static final int GL_TEXTURE_WRAP_T = 10243;
   private static final int GL_LINEAR = 9729;
   private static final int GL_CLAMP_TO_EDGE = 33071;
   private static final int GL_RGB = 6407;
   private static final int GL_RGBA = 6408;
   private static final int GL_UNSIGNED_BYTE = 5121;
   private static final int GL_UNSIGNED_SHORT_5_6_5 = 33635;
   private static final int GL_UNSIGNED_SHORT_4_4_4_4 = 32819;
   private static final int GL_UNSIGNED_SHORT_5_5_5_1 = 32820;

   private IosTextureLoader() {
   }

   public static IosTexture load(GL11 gl, byte[] png, int colorDepth) {
      Objects.requireNonNull(gl);
      BufferedImage image = decode(png);
      int width = image == null ? 0 : image.getWidth();
      int height = image == null ? 0 : image.getHeight();
      ByteBuffer pixels = convert(rgba(image, width, height, colorDepth), colorDepth);
      int[] ids = new int[1];
      gl.glGenTextures(1, ids, 0);
      gl.glBindTexture(3553, ids[0]);
      gl.glTexParameteri(3553, 10241, 9729);
      gl.glTexParameteri(3553, 10240, 9729);
      gl.glTexParameteri(3553, 10242, 33071);
      gl.glTexParameteri(3553, 10243, 33071);
      int format = colorDepth == 3 ? 6407 : 6408;

      int type = switch (colorDepth) {
         case 1 -> '耴';
         case 2 -> '耳';
         case 3 -> '荣';
         default -> 'ᐁ';
      };
      gl.glTexImage2D(3553, 0, format, width, height, 0, format, type, pixels);
      return new IosTexture(ids[0], width, height);
   }

   public static void unload(GL11 gl, int id) {
      gl.glDeleteTextures(1, new int[]{id}, 0);
   }

   private static BufferedImage decode(byte[] png) {
      if (png == null) {
         return null;
      }

      try (MemoryCacheImageInputStream input = new MemoryCacheImageInputStream(new ByteArrayInputStream(png))) {
         Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
         if (!readers.hasNext()) {
            return null;
         }

         ImageReader reader = readers.next();

         try {
            reader.setInput(input, true, true);
            return reader.read(0);
         } finally {
            reader.dispose();
         }
      } catch (IOException exception) {
         return null;
      }
   }

   private static ByteBuffer rgba(BufferedImage image, int width, int height, int colorDepth) {
      int size = Math.multiplyExact(Math.multiplyExact(width, height), 4);
      ByteBuffer pixels = ByteBuffer.allocateDirect(size).order(ByteOrder.LITTLE_ENDIAN);

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            int argb = image.getRGB(x, y);
            int alpha = argb >>> 24;
            pixels.put((byte)premultiply(argb >>> 16 & 0xFF, alpha));
            pixels.put((byte)premultiply(argb >>> 8 & 0xFF, alpha));
            pixels.put((byte)premultiply(argb & 0xFF, alpha));
            pixels.put((byte)(colorDepth == 3 ? 0 : alpha));
         }
      }

      pixels.flip();
      return pixels;
   }

   private static int premultiply(int channel, int alpha) {
      return (channel * alpha + 127) / 255;
   }

   private static ByteBuffer convert(ByteBuffer rgba, int colorDepth) {
      if (colorDepth != 3 && colorDepth != 2 && colorDepth != 1) {
         return rgba;
      }

      ByteBuffer packed = ByteBuffer.allocateDirect(rgba.remaining() / 2).order(ByteOrder.nativeOrder());

      while (rgba.hasRemaining()) {
         int word = rgba.getInt();
         int upper = word >>> 16 & 65535;

         int value = switch (colorDepth) {
            case 1 -> word << 8 & 63488 | word >>> 5 & 1984 | upper >>> 15 | upper >>> 2 & 62;
            case 2 -> word << 8 & 61440 | word >>> 4 & 3840 | upper >>> 12 | upper & 240;
            case 3 -> word >>> 5 & 2016 | word << 8 & 63488 | (word >>> 8 & 65535) >>> 11;
            default -> throw new IllegalStateException("Profundidade sem conversão de 16 bits");
         };
         packed.putShort((short)value);
      }

      packed.flip();
      return packed;
   }
}
