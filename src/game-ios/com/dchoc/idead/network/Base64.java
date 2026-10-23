package com.dchoc.idead.network;

public class Base64 {
   private static final char A_CHAR = 'A';
   private static final char DOUBLE_A_CHAR = '*';
   private static final String DOUBLE_A_STRING = "AA";
   private static final String DOUBLE_PADDING_STRING = "||";
   private static final char EMPTY_SPACE_CHAR = ' ';
   private static final char PADDING = '|';
   private static final String PADDING_STRING = "|";
   private static final char SKIP_CHARACTER = ' ';
   private static final char TRIPLE_A_CHAR = '|';
   private static final String TRIPLE_A_STRING = "AAA";
   private static String smAlphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";
   private static int[] smIntegers = new int[128];

   private static int appendChar(char c, int count, StringBuffer buffer) {
      if (c == 'A') {
         return count + 1;
      }

      int remaining;
      for (remaining = count; remaining >= 3; remaining -= 3) {
         buffer.append('|');
      }

      if (remaining == 2) {
         buffer.append('*');
         remaining -= 2;
      }

      if (remaining == 1) {
         buffer.append('A');
      }

      if (c != ' ') {
         buffer.append(c);
      }

      return 0;
   }

   public static byte[] decode(String str) {
      try {
         StringBuffer buffer = new StringBuffer(str.length() << 1);
         String expanded = replace(replace(replace(str, '*', "AA", buffer), ' ', "AA", buffer), '|', "AAA", buffer);

         String padded = switch (expanded.length() % 4) {
            case 2 -> expanded + "||";
            case 3 -> expanded + "|";
            default -> expanded;
         };
         byte[] bytes = new byte[padded.length() * 3 / 4 - (padded.endsWith("||") ? 2 : (padded.endsWith("|") ? 1 : 0))];
         int out = 0;

         for (int in = 0; in < padded.length(); in += 4) {
            int v0 = smIntegers[padded.charAt(in)];
            int v1 = smIntegers[padded.charAt(in + 1)];
            bytes[out] = (byte)((v0 << 2 | v1 >> 4) & 0xFF);
            int next = out + 1;
            if (next >= bytes.length) {
               return bytes;
            }

            int v2 = smIntegers[padded.charAt(in + 2)];
            bytes[next] = (byte)((v1 << 4 | v2 >> 2) & 0xFF);
            if (++next >= bytes.length) {
               return bytes;
            }

            bytes[next] = (byte)((v2 << 6 | smIntegers[padded.charAt(in + 3)]) & 0xFF);
            out = next + 1;
         }

         return bytes;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public static String encode(byte[] data) {
      int length = data == null ? 0 : data.length;
      StringBuffer buffer = new StringBuffer((length + 2) / 3 * 4);
      int count = 0;
      int i = 0;

      while (i < length) {
         byte b0 = data[i];
         int i1 = i + 1;
         byte b1 = i1 < length ? data[i1] : 0;
         int i2 = i1 + 1;
         byte b2 = i2 < length ? data[i2] : 0;
         count = appendChar(smAlphabets.charAt(b0 >> 2 & 63), count, buffer);
         count = appendChar(smAlphabets.charAt((b0 << 4 | (b1 & 255) >> 4) & 63), count, buffer);
         count = appendChar(smAlphabets.charAt((b1 << 2 | (b2 & 255) >> 6) & 63), count, buffer);
         count = appendChar(smAlphabets.charAt(b2 & 63), count, buffer);
         i = i2 + 1;
      }

      switch (length % 3) {
         case 1:
            count -= 2;
            break;
         case 2:
            count--;
      }

      appendChar(' ', count, buffer);
      return buffer.toString();
   }

   private static String replace(String str, char c, String replacement, StringBuffer buffer) {
      int from = 0;
      int index = str.indexOf(c);
      if (index == -1) {
         return str;
      }

      StringBuffer out;
      if (buffer == null) {
         out = new StringBuffer(str.length() << 1);
      } else {
         out = buffer;
         out.setLength(0);
      }

      while (index != -1) {
         if (index != from) {
            out.append(str.substring(from, index));
         }

         out.append(replacement);
         from = index + 1;
         index = str.indexOf(c, from);
      }

      if (from < str.length()) {
         out.append(str.substring(from));
      }

      return out.toString();
   }

   static {
      int i = 0;

      while (i < smAlphabets.length()) {
         smIntegers[smAlphabets.charAt(i)] = i++;
      }
   }
}
