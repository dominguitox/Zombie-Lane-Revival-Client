package com.dchoc.toolkit;

public class DChocHTTPConnection implements IHTTPConnection, Runnable {
   private static boolean smCancelCurrentRequest = false;
   private static IHTTPConnection sm_httpConnection;
   private IHTTPResponseListener listener;
   private HTTPRequest request;
   private boolean running;

   public static void cancelCurrentRequest() {
      smCancelCurrentRequest = true;
   }

   public static IHTTPConnection getHTTPConnection() {
      return new DChocHTTPConnection();
   }

   @Override
   public void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 3
      // 002: aconst_null
      // 003: astore 7
      // 005: aconst_null
      // 006: astore 8
      // 008: aconst_null
      // 009: astore 6
      // 00b: getstatic com/dchoc/toolkit/DChocHTTPConnection.smCancelCurrentRequest Z
      // 00e: ifne 27b
      // 011: aload 0
      // 012: getfield com/dchoc/toolkit/DChocHTTPConnection.request Lcom/dchoc/toolkit/HTTPRequest;
      // 015: invokevirtual com/dchoc/toolkit/HTTPRequest.getURL ()Ljava/lang/String;
      // 018: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 01b: checkcast javax/microedition/io/HttpConnection
      // 01e: astore 4
      // 020: aload 4
      // 022: ldc "Accept-Encoding"
      // 024: ldc "identity"
      // 026: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 02b: aload 0
      // 02c: getfield com/dchoc/toolkit/DChocHTTPConnection.request Lcom/dchoc/toolkit/HTTPRequest;
      // 02f: invokevirtual com/dchoc/toolkit/HTTPRequest.getData ()Lcom/dchoc/toolkit/DChocByteArray;
      // 032: ifnull 102
      // 035: aload 4
      // 037: ldc "POST"
      // 039: invokeinterface javax/microedition/io/HttpConnection.setRequestMethod (Ljava/lang/String;)V 2
      // 03e: aload 4
      // 040: ldc "Content-Type"
      // 042: ldc "application/x-www-form-urlencoded"
      // 044: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 049: new java/lang/StringBuilder
      // 04c: astore 5
      // 04e: aload 5
      // 050: invokespecial java/lang/StringBuilder.<init> ()V
      // 053: aload 4
      // 055: ldc "Content-Length"
      // 057: aload 5
      // 059: ldc ""
      // 05b: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 05e: aload 0
      // 05f: getfield com/dchoc/toolkit/DChocHTTPConnection.request Lcom/dchoc/toolkit/HTTPRequest;
      // 062: invokevirtual com/dchoc/toolkit/HTTPRequest.getData ()Lcom/dchoc/toolkit/DChocByteArray;
      // 065: invokevirtual com/dchoc/toolkit/DChocByteArray.getSize ()I
      // 068: invokevirtual java/lang/StringBuilder.append (I)Ljava/lang/StringBuilder;
      // 06b: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // 06e: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 073: aload 0
      // 074: getfield com/dchoc/toolkit/DChocHTTPConnection.request Lcom/dchoc/toolkit/HTTPRequest;
      // 077: invokevirtual com/dchoc/toolkit/HTTPRequest.getHeaders ()Ljava/util/Hashtable;
      // 07a: astore 9
      // 07c: aload 9
      // 07e: invokevirtual java/util/Hashtable.keys ()Ljava/util/Enumeration;
      // 081: astore 5
      // 083: aload 5
      // 085: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 08a: ifeq 10e
      // 08d: aload 5
      // 08f: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 094: checkcast java/lang/String
      // 097: astore 10
      // 099: aload 9
      // 09b: aload 10
      // 09d: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 0a0: checkcast java/lang/String
      // 0a3: astore 11
      // 0a5: aload 11
      // 0a7: ifnull 083
      // 0aa: aload 4
      // 0ac: aload 10
      // 0ae: aload 11
      // 0b0: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 0b5: goto 083
      // 0b8: astore 3
      // 0b9: aconst_null
      // 0ba: astore 3
      // 0bb: bipush -1
      // 0bc: istore 1
      // 0bd: aload 3
      // 0be: astore 7
      // 0c0: aload 6
      // 0c2: astore 3
      // 0c3: aload 4
      // 0c5: astore 5
      // 0c7: aload 7
      // 0c9: astore 4
      // 0cb: aload 4
      // 0cd: ifnull 0d5
      // 0d0: aload 4
      // 0d2: invokevirtual java/io/InputStream.close ()V
      // 0d5: aload 5
      // 0d7: ifnull 0e1
      // 0da: aload 5
      // 0dc: invokeinterface javax/microedition/io/HttpConnection.close ()V 1
      // 0e1: invokestatic java/lang/System.gc ()V
      // 0e4: aload 0
      // 0e5: bipush 0
      // 0e6: putfield com/dchoc/toolkit/DChocHTTPConnection.running Z
      // 0e9: getstatic com/dchoc/toolkit/DChocHTTPConnection.smCancelCurrentRequest Z
      // 0ec: ifne 101
      // 0ef: aload 0
      // 0f0: getfield com/dchoc/toolkit/DChocHTTPConnection.listener Lcom/dchoc/toolkit/IHTTPResponseListener;
      // 0f3: new com/dchoc/toolkit/HTTPResponse
      // 0f6: dup
      // 0f7: iload 1
      // 0f8: aload 3
      // 0f9: invokespecial com/dchoc/toolkit/HTTPResponse.<init> (ILcom/dchoc/toolkit/DChocByteArray;)V
      // 0fc: invokeinterface com/dchoc/toolkit/IHTTPResponseListener.responseReceived (Lcom/dchoc/toolkit/HTTPResponse;)V 2
      // 101: return
      // 102: aload 4
      // 104: ldc "GET"
      // 106: invokeinterface javax/microedition/io/HttpConnection.setRequestMethod (Ljava/lang/String;)V 2
      // 10b: goto 073
      // 10e: aload 0
      // 10f: getfield com/dchoc/toolkit/DChocHTTPConnection.request Lcom/dchoc/toolkit/HTTPRequest;
      // 112: invokevirtual com/dchoc/toolkit/HTTPRequest.getData ()Lcom/dchoc/toolkit/DChocByteArray;
      // 115: ifnull 13a
      // 118: aload 4
      // 11a: invokeinterface javax/microedition/io/HttpConnection.openDataOutputStream ()Ljava/io/DataOutputStream; 1
      // 11f: astore 5
      // 121: aload 5
      // 123: aload 0
      // 124: getfield com/dchoc/toolkit/DChocHTTPConnection.request Lcom/dchoc/toolkit/HTTPRequest;
      // 127: invokevirtual com/dchoc/toolkit/HTTPRequest.getData ()Lcom/dchoc/toolkit/DChocByteArray;
      // 12a: invokevirtual com/dchoc/toolkit/DChocByteArray.getBytes ()[B
      // 12d: invokevirtual java/io/DataOutputStream.write ([B)V
      // 130: aload 5
      // 132: invokevirtual java/io/DataOutputStream.flush ()V
      // 135: aload 5
      // 137: invokevirtual java/io/DataOutputStream.close ()V
      // 13a: getstatic com/dchoc/toolkit/DChocHTTPConnection.smCancelCurrentRequest Z
      // 13d: ifne 150
      // 140: aload 0
      // 141: getfield com/dchoc/toolkit/DChocHTTPConnection.listener Lcom/dchoc/toolkit/IHTTPResponseListener;
      // 144: aload 4
      // 146: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 14b: invokeinterface com/dchoc/toolkit/IHTTPResponseListener.responseCodeReceived (I)V 2
      // 150: aload 4
      // 152: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 157: sipush 200
      // 15a: if_icmpne 247
      // 15d: aload 4
      // 15f: invokeinterface javax/microedition/io/HttpConnection.getLength ()J 1
      // 164: l2i
      // 165: istore 2
      // 166: aload 4
      // 168: invokeinterface javax/microedition/io/HttpConnection.openInputStream ()Ljava/io/InputStream; 1
      // 16d: astore 5
      // 16f: iload 2
      // 170: ifle 1eb
      // 173: aload 7
      // 175: astore 6
      // 177: iload 2
      // 178: newarray 8
      // 17a: astore 3
      // 17b: bipush 0
      // 17c: istore 1
      // 17d: iload 1
      // 17e: iload 2
      // 17f: if_icmpge 1ba
      // 182: aload 7
      // 184: astore 6
      // 186: getstatic com/dchoc/toolkit/DChocHTTPConnection.smCancelCurrentRequest Z
      // 189: ifne 1ba
      // 18c: aload 7
      // 18e: astore 6
      // 190: iload 1
      // 191: aload 5
      // 193: aload 3
      // 194: iload 1
      // 195: iload 2
      // 196: iload 1
      // 197: isub
      // 198: invokevirtual java/io/InputStream.read ([BII)I
      // 19b: iadd
      // 19c: istore 1
      // 19d: aload 7
      // 19f: astore 6
      // 1a1: aload 0
      // 1a2: getfield com/dchoc/toolkit/DChocHTTPConnection.listener Lcom/dchoc/toolkit/IHTTPResponseListener;
      // 1a5: iload 1
      // 1a6: bipush 100
      // 1a8: imul
      // 1a9: iload 2
      // 1aa: idiv
      // 1ab: invokeinterface com/dchoc/toolkit/IHTTPResponseListener.percentageDataReceived (I)V 2
      // 1b0: goto 17d
      // 1b3: astore 3
      // 1b4: aload 5
      // 1b6: astore 3
      // 1b7: goto 0bb
      // 1ba: aload 7
      // 1bc: astore 6
      // 1be: getstatic com/dchoc/toolkit/DChocHTTPConnection.smCancelCurrentRequest Z
      // 1c1: ifne 276
      // 1c4: aload 7
      // 1c6: astore 6
      // 1c8: aload 3
      // 1c9: bipush 0
      // 1ca: iload 2
      // 1cb: invokestatic com/dchoc/toolkit/DChocByteArray.createByteArrayForReading ([BII)Lcom/dchoc/toolkit/DChocByteArray;
      // 1ce: astore 3
      // 1cf: aload 3
      // 1d0: astore 6
      // 1d2: getstatic com/dchoc/toolkit/DChocHTTPConnection.smCancelCurrentRequest Z
      // 1d5: ifne 271
      // 1d8: sipush 200
      // 1db: istore 1
      // 1dc: aload 4
      // 1de: astore 6
      // 1e0: aload 5
      // 1e2: astore 4
      // 1e4: aload 6
      // 1e6: astore 5
      // 1e8: goto 0cb
      // 1eb: aload 7
      // 1ed: astore 6
      // 1ef: invokestatic com/dchoc/toolkit/DChocByteArray.createByteArrayForWriting ()Lcom/dchoc/toolkit/DChocByteArray;
      // 1f2: astore 9
      // 1f4: aload 7
      // 1f6: astore 6
      // 1f8: aload 5
      // 1fa: invokevirtual java/io/InputStream.read ()I
      // 1fd: istore 1
      // 1fe: iload 1
      // 1ff: bipush -1
      // 200: if_icmpeq 224
      // 203: aload 7
      // 205: astore 6
      // 207: getstatic com/dchoc/toolkit/DChocHTTPConnection.smCancelCurrentRequest Z
      // 20a: ifne 224
      // 20d: aload 7
      // 20f: astore 6
      // 211: aload 9
      // 213: iload 1
      // 214: invokevirtual com/dchoc/toolkit/DChocByteArray.writeByte (I)V
      // 217: aload 7
      // 219: astore 6
      // 21b: aload 5
      // 21d: invokevirtual java/io/InputStream.read ()I
      // 220: istore 1
      // 221: goto 1fe
      // 224: aload 7
      // 226: astore 6
      // 228: aload 8
      // 22a: astore 3
      // 22b: getstatic com/dchoc/toolkit/DChocHTTPConnection.smCancelCurrentRequest Z
      // 22e: ifne 1cf
      // 231: aload 7
      // 233: astore 6
      // 235: aload 9
      // 237: invokevirtual com/dchoc/toolkit/DChocByteArray.getBytes ()[B
      // 23a: bipush 0
      // 23b: aload 9
      // 23d: invokevirtual com/dchoc/toolkit/DChocByteArray.getSize ()I
      // 240: invokestatic com/dchoc/toolkit/DChocByteArray.createByteArrayForReading ([BII)Lcom/dchoc/toolkit/DChocByteArray;
      // 243: astore 3
      // 244: goto 1cf
      // 247: aload 4
      // 249: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 24e: istore 1
      // 24f: aload 4
      // 251: astore 5
      // 253: aconst_null
      // 254: astore 4
      // 256: goto 0cb
      // 259: astore 4
      // 25b: goto 0d5
      // 25e: astore 4
      // 260: goto 0e1
      // 263: astore 3
      // 264: aconst_null
      // 265: astore 3
      // 266: aconst_null
      // 267: astore 4
      // 269: goto 0bb
      // 26c: astore 5
      // 26e: goto 13a
      // 271: bipush -1
      // 272: istore 1
      // 273: goto 1dc
      // 276: aconst_null
      // 277: astore 3
      // 278: goto 1cf
      // 27b: bipush -1
      // 27c: istore 1
      // 27d: aconst_null
      // 27e: astore 4
      // 280: aconst_null
      // 281: astore 5
      // 283: goto 0cb
      // try (8 -> 16): 270 java/lang/Exception
      // try (16 -> 47): 73 java/lang/Exception
      // try (47 -> 54): 73 java/lang/Exception
      // try (54 -> 66): 73 java/lang/Exception
      // try (68 -> 72): 73 java/lang/Exception
      // try (88 -> 90): 266 java/lang/Exception
      // try (92 -> 94): 268 java/lang/Exception
      // try (109 -> 112): 73 java/lang/Exception
      // try (113 -> 126): 73 java/lang/Exception
      // try (126 -> 130): 276 java/io/IOException
      // try (126 -> 130): 73 java/lang/Exception
      // try (130 -> 137): 73 java/lang/Exception
      // try (137 -> 148): 73 java/lang/Exception
      // try (152 -> 155): 187 java/lang/Exception
      // try (162 -> 164): 187 java/lang/Exception
      // try (166 -> 176): 187 java/lang/Exception
      // try (178 -> 186): 187 java/lang/Exception
      // try (193 -> 195): 187 java/lang/Exception
      // try (197 -> 202): 187 java/lang/Exception
      // try (204 -> 206): 187 java/lang/Exception
      // try (217 -> 219): 187 java/lang/Exception
      // try (221 -> 224): 187 java/lang/Exception
      // try (229 -> 231): 187 java/lang/Exception
      // try (233 -> 236): 187 java/lang/Exception
      // try (238 -> 241): 187 java/lang/Exception
      // try (246 -> 248): 187 java/lang/Exception
      // try (250 -> 257): 187 java/lang/Exception
      // try (258 -> 261): 73 java/lang/Exception
   }

   @Override
   public void sendRequest(HTTPRequest var1) {
      if (!this.running) {
         smCancelCurrentRequest = false;
         this.request = var1;
         this.running = true;
         new Thread(this).start();
      }
   }

   @Override
   public void setResponseListener(IHTTPResponseListener var1) {
      this.listener = var1;
   }
}
