package com.dchoc.parser;

public interface IParserHandler {
   void endDocument(IParser var1);

   void endElement(IParser var1, String var2);

   void readAttribute(IParser var1, String var2, String var3, int var4, int var5);

   void readText(IParser var1, String var2, int var3, int var4);

   void startDocument(IParser var1);

   void startElement(IParser var1, String var2);
}
