package com.dchoc.toolkit;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UTFDataFormatException;

public class DChocByteArray implements IByteArray {
   private boolean readMode;
   private byte[] data;
   private int position;
   private int size;
   private int limit;
   private int markPosition;

   private DChocByteArray() {
   }

   public static DChocByteArray createByteArrayForReading(byte[] data, int offset, int length) {
      DChocByteArray array = new DChocByteArray();
      array.readMode = true;
      array.data = data;
      array.position = offset;
      array.size = length;
      array.limit = length + offset;
      array.markPosition = offset;
      return array;
   }

   public static DChocByteArray createByteArrayForWriting() {
      DChocByteArray array = new DChocByteArray();
      array.readMode = false;
      array.data = new byte[256];
      array.limit = 256;
      return array;
   }

   public static DChocByteArray createByteArrayForWriting(int capacity) {
      DChocByteArray array = new DChocByteArray();
      array.readMode = false;
      array.data = new byte[capacity];
      array.limit = capacity;
      return array;
   }

   public int getSize() {
      return this.size;
   }

   public void mark() {
      this.markPosition = this.position;
   }

   public void reset() {
      this.position = this.markPosition;
   }

   public boolean readBoolean() throws EOFException {
      this.checkRead(1);
      return this.data[this.position++] != 0;
   }

   public byte readByte() throws EOFException {
      this.checkRead(1);
      return this.data[this.position++];
   }

   public int readUnsignedByte() throws EOFException {
      this.checkRead(1);
      return this.data[this.position++] & 0xFF;
   }

   public short readShort() throws EOFException {
      this.checkRead(2);
      int high = this.data[this.position++] & 255;
      int low = this.data[this.position++] & 255;
      return (short)(high << 8 | low);
   }

   public int readUnsignedShort() throws EOFException {
      this.checkRead(2);
      int high = this.data[this.position++] & 255;
      int low = this.data[this.position++] & 255;
      return high << 8 | low;
   }

   public int readInt() throws EOFException {
      this.checkRead(4);
      int b0 = this.data[this.position++] & 255;
      int b1 = this.data[this.position++] & 255;
      int b2 = this.data[this.position++] & 255;
      int b3 = this.data[this.position++] & 255;
      return b0 << 24 | b1 << 16 | b2 << 8 | b3;
   }

   private void checkRead(int count) throws EOFException {
      if (!this.readMode) {
         throw new RuntimeException();
      } else if (this.position >= this.limit + (count - 1) || this.position + count > this.data.length) {
         throw new EOFException();
      }
   }

   public String readUTF() throws EOFException {
      ByteArrayInputStream in = new ByteArrayInputStream(this.data, this.position, this.limit - this.position);

      try {
         String value = new DataInputStream(in).readUTF();
         this.position = this.limit - in.available();
         return value;
      } catch (IOException exception) {
         throw (RuntimeException)sneakyThrow(exception);
      }
   }

   public void skip(int count) {
      if (this.readMode) {
         this.position += count;
      }
   }

   public void writeBoolean(boolean value) {
      if (!this.readMode) {
         this.ensureCapacity(this.position + 1);
         this.data[this.position++] = (byte)(value ? 1 : 0);
         this.size = this.position;
      }
   }

   public void writeByte(int value) {
      if (!this.readMode) {
         this.ensureCapacity(this.position + 1);
         this.data[this.position++] = (byte)value;
         this.size = this.position;
      }
   }

   public void writeShort(int value) {
      if (!this.readMode) {
         this.ensureCapacity(this.position + 2);
         this.data[this.position++] = (byte)(value >>> 8);
         this.data[this.position++] = (byte)value;
         this.size = this.position;
      }
   }

   public void writeInt(int value) {
      if (!this.readMode) {
         this.ensureCapacity(this.position + 4);
         this.data[this.position++] = (byte)(value >>> 24);
         this.data[this.position++] = (byte)(value >>> 16);
         this.data[this.position++] = (byte)(value >>> 8);
         this.data[this.position++] = (byte)value;
         this.size = this.position;
      }
   }

   private void ensureCapacity(int needed) {
      if (this.limit < needed) {
         int capacity = needed <= this.limit * 2 ? this.limit * 2 : needed;
         byte[] grown = new byte[capacity];
         System.arraycopy(this.data, 0, grown, 0, this.limit);
         this.data = grown;
         this.limit = capacity;
      }
   }

   public void writeChars(String value) {
      if (!this.readMode) {
         char[] chars = value.toCharArray();
         int count = 0;

         while (count < chars.length && chars[count] != 0) {
            count++;
         }

         int bytes = 0;

         for (int i = 0; i < count; i++) {
            bytes += chars[i] < 2048 ? (chars[i] > 127 ? 2 : 1) : 3;
         }

         this.ensureCapacity(bytes + this.position + 1);

         for (int i = 0; i < count; i++) {
            int c = chars[i];
            if (c < 128) {
               this.data[this.position] = (byte)(this.data[this.position] + c);
               this.position++;
            } else if (c < 2048) {
               this.data[this.position++] = (byte)(c >> 6 | 192);
               this.data[this.position++] = (byte)(c & 63 | 128);
            } else {
               this.data[this.position++] = (byte)(c >> 12 | 224);
               this.data[this.position++] = (byte)(c >> 6 & 63 | 128);
               this.data[this.position++] = (byte)(c & 63 | 128);
            }
         }

         this.size = this.position;
      }
   }

   public void writeBytes(DChocByteArray other) {
      if (!this.readMode) {
         int count = other.getSize();
         byte[] bytes = other.getBytes();
         this.ensureCapacity(this.position + count + 1);
         System.arraycopy(bytes, 0, this.data, this.position, count);
         this.position += count;
         this.size = this.position;
      }
   }

   public void writeUTF(String value) {
      if (!this.readMode) {
         int length = value.length();
         char[] chars = new char[length];
         value.getChars(0, length, chars, 0);
         int bytes = 0;

         for (int i = 0; i < length; i++) {
            int c = chars[i];
            if (c >= 1 && c <= 127) {
               bytes++;
            } else if (c < 2048) {
               bytes += 2;
            } else {
               bytes += 3;
            }
         }

         if (bytes > 65535) {
            throw (RuntimeException)sneakyThrow(new UTFDataFormatException());
         }

         this.writeByte(bytes >> 8);
         this.writeByte(bytes);

         for (int i = 0; i < length; i++) {
            int c = chars[i];
            if (c >= 1 && c <= 127) {
               this.writeByte(c);
            } else if (c < 2048) {
               this.writeByte(192 | c >> 6 & 31);
               this.writeByte(128 | c & 63);
            } else {
               this.writeByte(224 | c >> 12 & 15);
               this.writeByte(128 | c >> 6 & 63);
               this.writeByte(128 | c & 63);
            }
         }
      }
   }

   public byte[] getBytes() {
      return this.data;
   }

   private static <T extends Throwable> T sneakyThrow(Throwable throwable) throws T {
      throw throwable;
   }
}
