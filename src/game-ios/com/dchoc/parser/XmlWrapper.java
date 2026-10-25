package com.dchoc.parser;
import com.dchoc.toolkit.Sonda;

import java.io.ByteArrayInputStream;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlWrapper {
   public static void parse(byte[] data, IParser parser, IParserHandler handler) {
      if (data != null && data.length != 0) {
         try {
            SAXParserFactory.newInstance().newSAXParser().parse(new InputSource(new ByteArrayInputStream(data)), new XmlWrapper.SaxHandler(parser, handler));
         } catch (Exception var4) {
         }
      }
   }

   private static class SaxHandler extends DefaultHandler {
      private final IParser mParser;
      private final IParserHandler mHandler;

      SaxHandler(IParser parser, IParserHandler handler) {
         this.mParser = parser;
         this.mHandler = handler;
      }

      @Override
      public void characters(char[] chars, int start, int length) {
         this.mHandler.readText(this.mParser, new String(chars, start, length), 0, length);
      }

      @Override
      public void endDocument() {
         this.mHandler.endDocument(this.mParser);
      }

      @Override
      public void endElement(String uri, String localName, String qName) {
         this.mHandler.endElement(this.mParser, qName);
      }

      @Override
      public void startDocument() {
         this.mHandler.startDocument(this.mParser);
      }

      @Override
      public void startElement(String uri, String localName, String qName, Attributes attributes) {
         this.mHandler.startElement(this.mParser, qName);
         int count = attributes.getLength();

         for (int i = 0; i < count; i++) {
            this.mHandler.readAttribute(this.mParser, attributes.getQName(i), attributes.getValue(i), 0, 0);
         }
      }

      @Override
      public void warning(SAXParseException exception) {
         Sonda.probe("XmlWrapper.sax warning", exception.getMessage());
      }
   }
}
