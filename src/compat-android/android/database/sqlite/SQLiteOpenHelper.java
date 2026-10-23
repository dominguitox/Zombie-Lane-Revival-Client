package android.database.sqlite;

import android.content.Context;
import android.database.SQLException;
import zlport.stubs.NotPorted;

public abstract class SQLiteOpenHelper {
   private final Context mContext;
   private final String mName;
   private final int mNewVersion;
   private SQLiteDatabase mDatabase;

   public SQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
      if (version < 1) {
         throw new IllegalArgumentException("Version must be >= 1, was " + version);
      }

      if (name == null) {
         NotPorted.method("android.database.sqlite.SQLiteOpenHelper#<init>(Context,String,CursorFactory,int) com banco em memória");
      }

      if (factory != null) {
         NotPorted.method("android.database.sqlite.SQLiteOpenHelper#<init>(Context,String,CursorFactory,int) com CursorFactory");
      }

      this.mContext = context;
      this.mName = name;
      this.mNewVersion = version;
   }

   public synchronized void close() {
      if (this.mDatabase != null && this.mDatabase.isOpen()) {
         this.mDatabase.close();
      }

      this.mDatabase = null;
   }

   public synchronized SQLiteDatabase getWritableDatabase() {
      if (this.mDatabase != null && this.mDatabase.isOpen()) {
         return this.mDatabase;
      }

      SQLiteDatabase db = SQLiteDatabase.openOrCreate(this.mContext.getDatabasePath(this.mName).toPath());
      int version = db.getVersion();
      if (version != this.mNewVersion) {
         if (version > this.mNewVersion) {
            throw new SQLException("Can't downgrade database from version " + version + " to " + this.mNewVersion);
         }

         if (version == 0) {
            this.onCreate(db);
         } else {
            this.onUpgrade(db, version, this.mNewVersion);
         }

         db.setVersion(this.mNewVersion);
      }

      this.mDatabase = db;
      return db;
   }

   public abstract void onCreate(SQLiteDatabase var1);

   public abstract void onUpgrade(SQLiteDatabase var1, int var2, int var3);
}
