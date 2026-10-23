package zlport.android.media;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public final class WavReader {
   private static final int FORMATO_PCM = 1;

   private WavReader() {
   }

   public static WavReader.Pcm read(InputStream in) throws IOException {
      ByteBuffer buf = ByteBuffer.wrap(in.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN);
      if (buf.limit() >= 12 && "RIFF".equals(texto(buf, 0)) && "WAVE".equals(texto(buf, 8))) {
         boolean achouFmt = false;
         int taxa = 0;
         int bits = 0;
         int pos = 12;

         while (pos + 8 <= buf.limit()) {
            String id = texto(buf, pos);
            long tamanho = buf.getInt(pos + 4) & 4294967295L;
            long inicio = pos + 8L;
            if (inicio + tamanho > buf.limit()) {
               throw new IOException("WAV truncado");
            }

            int p = (int)inicio;
            if ("fmt ".equals(id)) {
               if (tamanho < 16L) {
                  throw new IOException("WAV com chunk fmt curto");
               }

               int formato = buf.getShort(p) & '\uffff';
               int canais = buf.getShort(p + 2) & '\uffff';
               taxa = buf.getInt(p + 4);
               bits = buf.getShort(p + 14) & '\uffff';
               if (formato != 1 || canais != 1 || bits != 16 && bits != 8) {
                  String de = formato != 1 ? "formato " + formato + ", " : "";
                  throw new IOException("WAV não suportado: " + de + canais + " canais, " + bits + " bits (só PCM mono de 16 ou 8 bits)");
               }

               achouFmt = true;
            } else if ("data".equals(id)) {
               if (!achouFmt) {
                  throw new IOException("WAV sem chunk fmt antes de data");
               }

               short[] amostras;
               if (bits == 8) {
                  amostras = new short[(int)tamanho];

                  for (int i = 0; i < amostras.length; i++) {
                     amostras[i] = (short)((buf.get(p + i) & 255) - 128 << 8);
                  }
               } else {
                  amostras = new short[(int)(tamanho / 2L)];

                  for (int i = 0; i < amostras.length; i++) {
                     amostras[i] = buf.getShort(p + 2 * i);
                  }
               }

               return new WavReader.Pcm(amostras, taxa);
            }

            pos = (int)(inicio + tamanho + (tamanho & 1L));
         }

         throw new IOException("WAV truncado: sem chunk data");
      } else {
         throw new IOException("não é WAV (falta RIFF/WAVE)");
      }
   }

   private static String texto(ByteBuffer buf, int pos) {
      byte[] bytes = new byte[4];
      buf.get(pos, bytes);
      return new String(bytes, StandardCharsets.US_ASCII);
   }

   public record Pcm(short[] samples, int sampleRate) {
   }
}
