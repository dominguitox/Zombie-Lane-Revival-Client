package com.dchoc.idead.servlets;

import com.dchoc.parser.IParser;
import com.dchoc.parser.IParserHandler;
import com.dchoc.parser.XmlParser;
import com.dchoc.toolkit.DChocByteArray;

public class ServletRequestWithXml extends ServletRequest implements IParserHandler {
   public static final String EMPTY_VALUE = "";
   private String mValue;

   public ServletRequestWithXml(String serviceName, boolean showOffline) {
      super(serviceName, showOffline, false);
   }

   @Override
   public void processResponse() {
      if (this.mResponse != null && this.mResponse.getResponseCode() == com.dchoc.toolkit.HTTPResponse.RESPONSE_CODE_OK) {
         DChocByteArray data = this.mResponse.getData();
         if (data != null && data.getSize() != 0) {
            new XmlParser(data.getBytes()).start(this);
         }
      }

      super.processResponse();
   }

   @Override
   public void startDocument(IParser parser) {
   }

   @Override
   public void endDocument(IParser parser) {
      this.mValue = null;
   }

   @Override
   public void startElement(IParser parser, String tagName) {
      this.mValue = null;
   }

   @Override
   public void endElement(IParser parser, String tagName) {
      this.endElement(parser, tagName, this.mValue != null ? this.mValue : "");
   }

   public void endElement(IParser parser, String tagName, String text) {
   }

   @Override
   public void readText(IParser parser, String text, int start, int length) {
      if (start < length) {
         text = text.substring(start, length);
      }

      if (this.mValue != null) {
         StringBuffer sb = new StringBuffer(this.mValue.length() + text.length() + 1);
         sb.append(this.mValue);
         sb.append(text);
         text = sb.toString();
      }

      this.mValue = text;
   }

   @Override
   public void readAttribute(IParser parser, String key, String value, int start, int length) {
   }
}
