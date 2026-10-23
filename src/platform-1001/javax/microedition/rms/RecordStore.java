package javax.microedition.rms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.microedition.midlet.MIDlet;

public class RecordStore {
   private static final String DATA_DIR_NAME = "_data";
   private static final String RECORD_NAME = "1";
   File dataFile;
   String name;

   private RecordStore(String var1, File var2) {
      this.name = var1;
      this.dataFile = var2;
   }

   public static void deleteRecordStore(String var0) throws RecordStoreException {
      File var2 = MIDlet.getMIDletInstance().getFileStreamPath(var0 + "_data");
      File var1 = new File(var2, "1");
      if (!var1.exists()) {
         throw new RecordStoreNotFoundException("");
      }

      if (!var1.delete()) {
         throw new RecordStoreException("unable to delete Resord Store " + var0);
      }

      var2.delete();
   }

   public static String[] listRecordStores() {
      return null;
   }

   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   public static RecordStore openRecordStore(String var0, boolean var1) throws RecordStoreException {
      File var3 = MIDlet.getMIDletInstance().getFileStreamPath(var0 + "_data");
      if (!var3.exists() && !var1) {
         throw new RecordStoreNotFoundException("Record Store " + var0 + " does not exist");
      }

      File var2;
      if (!var3.exists() && var1) {
         label30: {
            try {
               if (var3.mkdirs()) {
                  var2 = new File(var3, "1");
                  var1 = var2.createNewFile();
                  break label30;
               }
            } catch (Exception var5) {
               throw new RecordStoreException("RecordStore " + var0 + " could not be created");
            }

            var2 = null;
            var1 = false;
         }

         if (!var1) {
            try {
               Exception var7 = new Exception();
               throw var7;
            } catch (Exception var4) {
               throw new RecordStoreException("RecordStore " + var0 + " could not be created");
            }
         }
      } else {
         var2 = new File(var3, "1");
      }

      return new RecordStore(var0, var2);
   }

   public int addRecord(byte[] var1, int var2, int var3) throws RecordStoreException {
      try {
         FileOutputStream var4 = new FileOutputStream(this.dataFile);
         var4.write(var1, var2, var3);
         var4.close();
         return 1;
      } catch (IOException var5) {
         throw new RecordStoreException("");
      }
   }

   public void closeRecordStore() {
   }

   public int getNumRecords() {
      return 0;
   }

   public byte[] getRecord(int var1) throws RecordStoreException {
      try {
         FileInputStream var3 = new FileInputStream(this.dataFile);
         byte[] var2 = new byte[var3.available()];
         var3.read(var2);
         var3.close();
         return var2;
      } catch (Exception var4) {
         throw new RecordStoreException("");
      }
   }

   public void setRecord(int var1, byte[] var2, int var3, int var4) {
   }
}
