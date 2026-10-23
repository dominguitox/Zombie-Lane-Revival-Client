package android.database;

public interface Cursor {
   void close();

   byte[] getBlob(int var1);

   int getCount();

   String getString(int var1);

   boolean moveToNext();
}
