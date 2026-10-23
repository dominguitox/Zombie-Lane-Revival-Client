package com.dchoc.opengl;

import com.dchoc.idead.Profiler;
import zlport.ios.Ios;
import zlport.ios.IosTexture;

public class OGLTextureManager {
   public static final int INITIAL_TEXTURE_CAPACITY = 256;

   public static OGLTexture loadTexture(String filename, int colorDepth) {
      Profiler.startTimer(1);
      IosTexture texture = Ios.platform().loadTexture(filename, colorDepth);
      OGLTexture result = new OGLTexture(texture.id(), filename, (short)texture.width(), (short)texture.height(), colorDepth);
      Profiler.stopTimer(1);
      return result;
   }

   public static void unloadTexture(OGLTexture texture) {
      Ios.platform().unloadTexture(texture.getID());
   }
}
