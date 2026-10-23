package com.dchoc.idead.network;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
   private static final char[] HEX_VALUES = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

   public static String create(String str) {
      byte[] digest = digest(str.getBytes(StandardCharsets.US_ASCII));
      char[] hex = new char[digest.length * 2];

      for (int i = 0; i < digest.length; i++) {
         int b = digest[i] & 255;
         hex[i * 2] = HEX_VALUES[b >> 4];
         hex[i * 2 + 1] = HEX_VALUES[b & 15];
      }

      return new String(hex);
   }

   private static byte[] digest(byte[] data) {
      try {
         return MessageDigest.getInstance("MD5").digest(data);
      } catch (NoSuchAlgorithmException e) {
         throw new IllegalStateException("MD5 indisponível", e);
      }
   }
}
