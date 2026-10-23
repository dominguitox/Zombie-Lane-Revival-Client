package android.content.res;

import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import zlport.stubs.NotPorted;

public class Resources {
   public Resources(AssetManager assets, DisplayMetrics metrics, Configuration config) {
   }

   public Drawable getDrawable(int id) throws Resources.NotFoundException {
      return new Resources.InertDrawable();
   }

   private static final class InertDrawable extends Drawable {
      @Override
      public void setAlpha(int alpha) {
      }
   }

   public static class NotFoundException extends RuntimeException {
      public NotFoundException() {
         NotPorted.method("android.content.res.Resources$NotFoundException#<init>()");
      }
   }
}
