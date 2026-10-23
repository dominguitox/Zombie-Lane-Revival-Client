package zlport.gles;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.system.MemoryUtil;

final class Buffers {
   private Buffers() {
   }

   static int shift(Buffer buffer) {
      return switch (buffer) {
         case ByteBuffer b -> 0;
         case ShortBuffer b -> 1;
         case CharBuffer b -> 1;
         case IntBuffer b -> 2;
         case FloatBuffer b -> 2;
         case LongBuffer b -> 3;
         case DoubleBuffer b -> 3;
         default -> throw new IllegalArgumentException("Tipo de buffer não suportado: " + buffer.getClass());
      };
   }

   static long remainingBytes(Buffer buffer) {
      return (long)buffer.remaining() << shift(buffer);
   }

   static long address(Buffer direct) {
      return MemoryUtil.memAddress0(direct) + ((long)direct.position() << shift(direct));
   }

   static ByteBuffer directCopy(Buffer buffer) {
      ByteBuffer out = ByteBuffer.allocateDirect(Math.toIntExact(remainingBytes(buffer))).order(ByteOrder.nativeOrder());
      switch (buffer) {
         case ByteBuffer b:
            out.put(b.duplicate());
            break;
         case ShortBuffer b:
            out.asShortBuffer().put(b.duplicate());
            break;
         case CharBuffer b:
            out.asCharBuffer().put(b.duplicate());
            break;
         case IntBuffer b:
            out.asIntBuffer().put(b.duplicate());
            break;
         case FloatBuffer b:
            out.asFloatBuffer().put(b.duplicate());
            break;
         case LongBuffer b:
            out.asLongBuffer().put(b.duplicate());
            break;
         case DoubleBuffer b:
            out.asDoubleBuffer().put(b.duplicate());
            break;
         default:
            throw new IllegalArgumentException("Tipo de buffer não suportado: " + buffer.getClass());
      }

      return out.clear();
   }
}
