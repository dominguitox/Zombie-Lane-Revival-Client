package com.dchoc.toolkit;

public class ServerSocketMessage implements IByteArray {
   private static final String CHARSET = "utf-8";
   private static final int CONTINUE_MASK = 128;
   private static byte[] EMPTY_MESSAGE_BYTES = new byte[0];
   private static final int FIRST_BYTE_NEGATIVE = 64;
   private static final int FIRST_BYTE_NEGATIVE_CONTINUE = 192;
   private static final int FIRST_BYTE_POSITIVE_CONTINUE = 128;
   private static final int FIVE_BYTE_NEGATIVE_FILL = Integer.MIN_VALUE;
   private static final int FOUR_BIT_MASK = 15;
   private static final int FOUR_BYTE_NEGATIVE_FILL = -134217728;
   public static final int MAXIMUM_MESSAGE_LENGTH = 300000;
   public static final int MAXIMUM_STRING_LENGTH = 300000;
   private static final int NEEDS_FOUR_BYTES = 134217728;
   private static final int NEEDS_ONE_BYTE = 64;
   private static final int NEEDS_THREE_BYTES = 1048576;
   private static final int NEEDS_TWO_BYTES = 8192;
   private static final int NEGATIVE_MASK = 64;
   private static final int NEGATIVE_NEEDS_FOUR_BYTES = -134217728;
   private static final int NEGATIVE_NEEDS_ONE_BYTE = -64;
   private static final int NEGATIVE_NEEDS_THREE_BYTES = -1048576;
   private static final int NEGATIVE_NEEDS_TWO_BYTES = -8192;
   private static final int ONE_BYTE_NEGATIVE_FILL = -64;
   private static final int SEVEN_BIT_MASK = 127;
   private static final int SIX_BIT_MASK = 63;
   private static final int SUBSEQUENT_BYTE_CONTINUE = 128;
   private static final int THREE_BYTE_NEGATIVE_FILL = -1048576;
   private static final int TWO_BYTE_NEGATIVE_FILL = -8192;
   private int bitOffset;
   private int initialLength;
   private int initialOffset;
   private byte[] messageBytes;
   private int messageType;
   private int offset;
   private int proxySessionId;
   private boolean readMode;

   private ServerSocketMessage() {
   }

   public byte[] getBytes() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.getBytes");
   }

   public int getSize() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.getSize");
   }

   public boolean readBoolean() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.readBoolean");
   }

   public byte readByte() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.readByte");
   }

   public int readInt() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.readInt");
   }

   public short readShort() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.readShort");
   }

   public String readUTF() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.readUTF");
   }

   public int readUnsignedByte() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.readUnsignedByte");
   }

   public int readUnsignedShort() {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.readUnsignedShort");
   }

   public void skip(int i) {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.skip");
   }

   public void writeBoolean(boolean z) {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.writeBoolean");
   }

   public void writeByte(int b) {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.writeByte");
   }

   public void writeInt(int i) {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.writeInt");
   }

   public void writeShort(int i) {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.writeShort");
   }

   public void writeUTF(String str) {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerSocketMessage.writeUTF");
   }
}
