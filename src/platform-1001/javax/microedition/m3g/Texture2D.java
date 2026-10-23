package javax.microedition.m3g;

public class Texture2D extends Transformable {
   public static final int FILTER_BASE_LEVEL = 208;
   public static final int FILTER_LINEAR = 209;
   public static final int FILTER_NEAREST = 210;
   public static final int FUNC_ADD = 224;
   public static final int FUNC_BLEND = 225;
   public static final int FUNC_DECAL = 226;
   public static final int FUNC_MODULATE = 227;
   public static final int FUNC_REPLACE = 228;
   public static final int WRAP_CLAMP = 240;
   public static final int WRAP_REPEAT = 241;

   Texture2D(int var1) {
   }

   public Texture2D(Image2D var1) {
   }

   private static native int create(Image2D var0);

   private native int getImageImpl();

   private native void setImageImpl(Image2D var1);

   public native int getBlendColor();

   public native int getBlending();

   public Image2D getImage() {
      return null;
   }

   public native int getWrappingS();

   public native int getWrappingT();

   public native void setBlendColor(int var1);

   public native void setBlending(int var1);

   public native void setFiltering(int var1, int var2);

   public void setImage(Image2D var1) {
   }

   public native void setWrapping(int var1, int var2);
}
