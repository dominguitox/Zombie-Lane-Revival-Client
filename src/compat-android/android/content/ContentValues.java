package android.content;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.StringJoiner;

public final class ContentValues {
   private final LinkedHashMap<String, Object> mValues = new LinkedHashMap<>();

   public void put(String key, byte[] value) {
      this.mValues.put(key, value);
   }

   public void put(String key, Long value) {
      this.mValues.put(key, value);
   }

   public void put(String key, String value) {
      this.mValues.put(key, value);
   }

   public Object get(String key) {
      return this.mValues.get(key);
   }

   public boolean containsKey(String key) {
      return this.mValues.containsKey(key);
   }

   public Set<String> keySet() {
      return Collections.unmodifiableSet(this.mValues.keySet());
   }

   public int size() {
      return this.mValues.size();
   }

   @Override
   public String toString() {
      StringJoiner s = new StringJoiner(" ");
      this.mValues.forEach((k, v) -> s.add(k + "=" + (v instanceof byte[] b ? "<" + b.length + " bytes>" : v)));
      return s.toString();
   }
}
