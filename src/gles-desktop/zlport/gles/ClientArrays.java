package zlport.gles;

import java.nio.Buffer;
import java.util.HashSet;
import java.util.Set;

final class ClientArrays {
   static final int GL_VERTEX_ARRAY = 32884;
   static final int GL_COLOR_ARRAY = 32886;
   static final int GL_TEXTURE_COORD_ARRAY = 32888;
   static final int GL_TEXTURE_2D = 3553;
   static final int GL_TEXTURE0 = 33984;
   private final ClientArrays.Array vertex = new ClientArrays.Array("GL_VERTEX_ARRAY");
   private final ClientArrays.Array color = new ClientArrays.Array("GL_COLOR_ARRAY");
   private final ClientArrays.Array texCoord = new ClientArrays.Array("GL_TEXTURE_COORD_ARRAY");
   private int activeTexture = 33984;
   private final Set<Integer> texturedUnits = new HashSet<>();

   private ClientArrays.Array of(int array) {
      return switch (array) {
         case 32884 -> this.vertex;
         default -> null;
         case 32886 -> this.color;
         case 32888 -> this.texCoord;
      };
   }

   void enableClientState(int array, boolean on) {
      ClientArrays.Array a = this.of(array);
      if (a != null) {
         a.enabled = on;
      }
   }

   void enable(int cap, boolean on) {
      if (cap == 3553) {
         if (on) {
            this.texturedUnits.add(this.activeTexture);
         } else {
            this.texturedUnits.remove(this.activeTexture);
         }
      }
   }

   void activeTexture(int unit) {
      this.activeTexture = unit;
   }

   void clientPointer(int array, int size, int type, int stride, Buffer pointer) {
      ClientArrays.Array a = this.of(array);
      a.pointerSet = true;
      a.buffer = pointer;
      a.availableBytes = (long)(pointer.capacity() - pointer.position()) << Buffers.shift(pointer);
      int typeBytes = typeBytes(type);
      a.elementBytes = typeBytes < 0 ? -1 : size * typeBytes;
      a.stride = stride;
   }

   void bufferObjectPointer(int array) {
      ClientArrays.Array a = this.of(array);
      a.pointerSet = true;
      a.buffer = null;
      a.availableBytes = -1L;
   }

   boolean prepareDraw(int first, int count) {
      boolean skipTexCoords = this.texCoord.enabled && !this.texturedUnits.contains(33984);
      if (count <= 0) {
         return skipTexCoords;
      }

      for (ClientArrays.Array a : new ClientArrays.Array[]{this.vertex, this.color, this.texCoord}) {
         if (a.enabled && (a != this.texCoord || !skipTexCoords)) {
            a.check(first, count);
         }
      }

      return skipTexCoords;
   }

   private static int typeBytes(int type) {
      return switch (type) {
         case 5120, 5121 -> 1;
         case 5122 -> 2;
         default -> -1;
         case 5126, 5132 -> 4;
      };
   }

   private static final class Array {
      final String name;
      boolean enabled;
      boolean pointerSet;
      Buffer buffer;
      long availableBytes = -1L;
      int elementBytes;
      int stride;

      Array(String name) {
         this.name = name;
      }

      void check(int first, int count) {
         if (!this.pointerSet) {
            throw new IllegalStateException("glDrawArrays: " + this.name + " habilitado sem ponteiro (o driver leria o endereço 0)");
         }

         if (this.availableBytes >= 0L && this.elementBytes > 0) {
            long step = this.stride == 0 ? this.elementBytes : this.stride;
            long needed = (first + count - 1) * step + this.elementBytes;
            if (needed > this.availableBytes) {
               throw new IllegalStateException(
                  "glDrawArrays: " + this.name + " leria " + needed + " bytes, o buffer tem " + this.availableBytes + " a partir do ponteiro"
               );
            }
         }
      }
   }
}
