package com.dchoc.toolkit;

import java.io.EOFException;

public interface IByteArray {
   byte[] getBytes();

   int getSize();

   boolean readBoolean() throws EOFException;

   byte readByte() throws EOFException;

   int readInt() throws EOFException;

   short readShort() throws EOFException;

   String readUTF() throws EOFException;

   int readUnsignedByte() throws EOFException;

   int readUnsignedShort() throws EOFException;

   void skip(int var1);

   void writeBoolean(boolean var1);

   void writeByte(int var1);

   void writeInt(int var1);

   void writeShort(int var1);

   void writeUTF(String var1);
}
