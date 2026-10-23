package com.dchoc.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal, dependency-free JSON reader.
 *
 * <p>
 * It is used by the revival to load the shared mission definitions from
 * {@code missions.json}, which are the single source of truth for the mission
 * system. The reader produces plain Java values: {@link LinkedHashMap} for
 * objects, {@link ArrayList} for arrays, {@link String}, {@link Long} for
 * integral numbers, {@link Double} for fractional numbers, {@link Boolean},
 * and null. The static accessors below are convenience conversions that never
 * throw on a missing or mismatched value.
 */
public final class Json {
   private final String source;
   private int index;

   private Json(String source) {
      this.source = source;
   }

   /** Parse a complete JSON document and return the root value. */
   public static Object parse(String source) {
      if (source == null) {
         throw new IllegalArgumentException("JSON text is null");
      }

      Json reader = new Json(source);
      reader.skipWhitespace();
      Object value = reader.readValue();
      reader.skipWhitespace();
      if (reader.index != source.length()) {
         throw new IllegalArgumentException("Unexpected trailing JSON content at offset " + reader.index);
      }

      return value;
   }

   private Object readValue() {
      if (this.index >= this.source.length()) {
         throw new IllegalArgumentException("Unexpected end of JSON input");
      }

      char c = this.source.charAt(this.index);
      switch (c) {
         case '{':
            return this.readObject();
         case '[':
            return this.readArray();
         case '"':
            return this.readString();
         case 't':
            this.expect("true");
            return Boolean.TRUE;
         case 'f':
            this.expect("false");
            return Boolean.FALSE;
         case 'n':
            this.expect("null");
            return null;
         default:
            return this.readNumber();
      }
   }

   private Map<String, Object> readObject() {
      Map<String, Object> object = new LinkedHashMap<>();
      this.index++;
      this.skipWhitespace();
      if (this.peek() == '}') {
         this.index++;
         return object;
      }

      while (true) {
         this.skipWhitespace();
         if (this.peek() != '"') {
            throw new IllegalArgumentException("Expected a JSON object key at offset " + this.index);
         }

         String key = this.readString();
         this.skipWhitespace();
         if (this.peek() != ':') {
            throw new IllegalArgumentException("Expected ':' at offset " + this.index);
         }

         this.index++;
         this.skipWhitespace();
         object.put(key, this.readValue());
         this.skipWhitespace();
         char next = this.peek();
         this.index++;
         if (next == '}') {
            return object;
         }

         if (next != ',') {
            throw new IllegalArgumentException("Expected ',' or '}' at offset " + (this.index - 1));
         }
      }
   }

   private List<Object> readArray() {
      List<Object> array = new ArrayList<>();
      this.index++;
      this.skipWhitespace();
      if (this.peek() == ']') {
         this.index++;
         return array;
      }

      while (true) {
         this.skipWhitespace();
         array.add(this.readValue());
         this.skipWhitespace();
         char next = this.peek();
         this.index++;
         if (next == ']') {
            return array;
         }

         if (next != ',') {
            throw new IllegalArgumentException("Expected ',' or ']' at offset " + (this.index - 1));
         }
      }
   }

   private String readString() {
      this.index++;
      StringBuilder builder = new StringBuilder();

      while (true) {
         if (this.index >= this.source.length()) {
            throw new IllegalArgumentException("Unterminated JSON string");
         }

         char c = this.source.charAt(this.index++);
         if (c == '"') {
            return builder.toString();
         }

         if (c != '\\') {
            builder.append(c);
            continue;
         }

         if (this.index >= this.source.length()) {
            throw new IllegalArgumentException("Unterminated JSON escape");
         }

         char escape = this.source.charAt(this.index++);
         switch (escape) {
            case '"':
               builder.append('"');
               break;
            case '\\':
               builder.append('\\');
               break;
            case '/':
               builder.append('/');
               break;
            case 'b':
               builder.append('\b');
               break;
            case 'f':
               builder.append('\f');
               break;
            case 'n':
               builder.append('\n');
               break;
            case 'r':
               builder.append('\r');
               break;
            case 't':
               builder.append('\t');
               break;
            case 'u':
               if (this.index + 4 > this.source.length()) {
                  throw new IllegalArgumentException("Truncated unicode escape");
               }
               builder.append((char)Integer.parseInt(this.source.substring(this.index, this.index + 4), 16));
               this.index += 4;
               break;
            default:
               throw new IllegalArgumentException("Invalid JSON escape '\\" + escape + "'");
         }
      }
   }

   private Object readNumber() {
      int start = this.index;
      boolean fractional = false;
      while (this.index < this.source.length()) {
         char c = this.source.charAt(this.index);
         if (c == '-' || c == '+' || (c >= '0' && c <= '9')) {
            this.index++;
         } else if (c == '.' || c == 'e' || c == 'E') {
            fractional = true;
            this.index++;
         } else {
            break;
         }
      }

      String token = this.source.substring(start, this.index);
      if (token.isEmpty()) {
         throw new IllegalArgumentException("Expected a JSON value at offset " + start);
      }

      if (fractional) {
         return Double.valueOf(token);
      }

      try {
         return Long.valueOf(token);
      } catch (NumberFormatException exception) {
         return Double.valueOf(token);
      }
   }

   private void expect(String literal) {
      if (!this.source.startsWith(literal, this.index)) {
         throw new IllegalArgumentException("Expected '" + literal + "' at offset " + this.index);
      }

      this.index += literal.length();
   }

   private void skipWhitespace() {
      while (this.index < this.source.length()) {
         char c = this.source.charAt(this.index);
         if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
            this.index++;
         } else {
            return;
         }
      }
   }

   private char peek() {
      if (this.index >= this.source.length()) {
         throw new IllegalArgumentException("Unexpected end of JSON input");
      }

      return this.source.charAt(this.index);
   }

   public static Map<String, Object> asObject(Object value) {
      return value instanceof Map ? (Map<String, Object>)value : null;
   }

   public static List<Object> asArray(Object value) {
      return value instanceof List ? (List<Object>)value : null;
   }

   public static String asString(Object value) {
      if (value == null) {
         return null;
      }

      return value instanceof String ? (String)value : String.valueOf(value);
   }

   public static int asInt(Object value, int fallback) {
      if (value instanceof Number) {
         return ((Number)value).intValue();
      }

      if (value instanceof String) {
         try {
            return (int)Double.parseDouble((String)value);
         } catch (NumberFormatException exception) {
            return fallback;
         }
      }

      return fallback;
   }

   public static long asLong(Object value, long fallback) {
      if (value instanceof Number) {
         return ((Number)value).longValue();
      }

      if (value instanceof String) {
         try {
            return (long)Double.parseDouble((String)value);
         } catch (NumberFormatException exception) {
            return fallback;
         }
      }

      return fallback;
   }

   public static boolean asBoolean(Object value, boolean fallback) {
      if (value instanceof Boolean) {
         return (Boolean)value;
      }

      if (value instanceof String) {
         String text = ((String)value).trim();
         if (text.equalsIgnoreCase("true")) {
            return true;
         }

         if (text.equalsIgnoreCase("false")) {
            return false;
         }
      }

      return fallback;
   }

   public static Object get(Map<String, Object> object, String key) {
      return object == null ? null : object.get(key);
   }

   public static Map<String, Object> object(Map<String, Object> object, String key) {
      return asObject(get(object, key));
   }

   public static List<Object> array(Map<String, Object> object, String key) {
      return asArray(get(object, key));
   }

   public static int intValue(Map<String, Object> object, String key, int fallback) {
      return asInt(get(object, key), fallback);
   }

   public static long longValue(Map<String, Object> object, String key, long fallback) {
      return asLong(get(object, key), fallback);
   }

   public static boolean booleanValue(Map<String, Object> object, String key, boolean fallback) {
      return asBoolean(get(object, key), fallback);
   }

   public static String stringValue(Map<String, Object> object, String key) {
      return asString(get(object, key));
   }

   /** Convert a JSON array of numbers into an int array, or null when empty. */
   public static int[] intArray(Object value) {
      List<Object> list = asArray(value);
      if (list == null || list.isEmpty()) {
         return null;
      }

      int[] result = new int[list.size()];
      for (int i = 0; i < list.size(); i++) {
         result[i] = asInt(list.get(i), 0);
      }

      return result;
   }
}
