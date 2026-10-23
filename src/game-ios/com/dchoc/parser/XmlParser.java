package com.dchoc.parser;

public class XmlParser implements IParser {
   private byte[] mData;
   private IParserHandler mHandler;

   public XmlParser(byte[] data) {
      this.mData = data;
   }

   @Override
   public void start(IParserHandler handler) {
      this.mHandler = handler;
      XmlWrapper.parse(this.mData, this, handler);
   }

   @Override
   public void stop() {
   }
}
