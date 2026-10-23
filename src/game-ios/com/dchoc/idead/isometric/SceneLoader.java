package com.dchoc.idead.isometric;

import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.Profiler;
import com.dchoc.idead.fixes.Fixes;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.tracking.ErrorEvent;
import com.dchoc.iphonewrappers.SettingsWrapper;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;
import java.util.Vector;

public class SceneLoader {
   public static final boolean DEBUG_INDUCE_RANDOM_LOAD_FAILURE = false;
   public static final int LOCATION_HOME = 0;
   public static final int LOCATION_NEIGHBOR = 1;
   public static final String RMS_GAMEFIELD = "RMS_GAMEFIELD";
   public static final String RMS_GAMEFIELD_DBG_BACKUP = "RMS_GAMEFIELD_BACKUP";
   private static Vector smItems;

   public static void init() {
      smItems = new Vector(128);
   }

   public static boolean load(DChocByteArray data, boolean compressed) {
      return load(data, compressed, 3);
   }

   public static boolean load(DChocByteArray data, boolean compressed, int platform) {
      if (data == null) {
         return false;
      }

      Profiler.startTimer(8);
      smItems.removeAllElements();
      Vector items = smItems;
      IsometricScene scene = IsometricUtils.getScene();
      IsometricPool pool = scene.getPool();
      scene.clear(false);
      if (!compressed) {
         data.mark();
         Profiler.startTimer(15);

         try {
            while (true) {
               Item item = ObjectLoader.loadItemUncompressed(data);
               if (item != null && !items.contains(item)) {
                  items.addElement(item);
               }
            }
         } catch (Exception exception) {
            System.err.println(exception);
            pool.releaseAllBut(items);
            Profiler.stopTimer(15);
            data.reset();

            try {
               while (true) {
                  ObjectLoader.loadUncompressed(data);
               }
            } catch (Exception exceptionx) {
               if (!(exceptionx instanceof EOFException)) {
                  new ErrorEvent().trackLoadError("SceneLoader.load", exceptionx.getMessage(), "Uncompressed");
                  Profiler.stopTimer(8);
                  return false;
               }
            }

            System.err.println(exceptionx);
         }
      } else {
         try {
            int version = SettingsWrapper.parseVersion(data.readUTF());
            int count = data.readInt();
            if (count < 0) {
               throw new Exception("Invalid size " + count);
            }

            if (version < (platform == 4 ? 103 : 105)) {
               pool.releaseAll(false);

               for (int i = 0; i < count; i++) {
                  ObjectLoaderOld.load(data);
               }
            } else {
               ObjectLoaderOld.readCrash(data);
               byte[] sizes = new byte[count];

               for (int i = 0; i < sizes.length; i++) {
                  sizes[i] = data.readByte();
               }

               data.mark();
               Profiler.startTimer(15);

               for (int i = 0; i < count; i++) {
                  Item item = ObjectLoader.loadItem(data, sizes[i]);
                  if (item != null && !items.contains(item)) {
                     items.addElement(item);
                  }
               }

               pool.releaseAllBut(items);
               Profiler.stopTimer(15);
               data.reset();

               for (int i = 0; i < count; i++) {
                  ObjectLoader.load(data, sizes[i]);
               }
            }

            long timestamp = Fixes.fixTimestamp(ToolkitHelpers.readLong(data));
            if (!GameEngineUtils.isAtNeigborsPlace()) {
               scene.setUpdateBreakableObjects(timestamp);
            }
         } catch (Exception exception) {
            new ErrorEvent().trackLoadError("SceneLoader.load", exception.getMessage(), "Compressed");
            Profiler.stopTimer(8);
            return false;
         }
      }

      scene.updateWalkableTiles();
      Profiler.stopTimer(8);
      return true;
   }

   public static DChocByteArray saveCurrent() {
      Profiler.startTimer(9);
      IsometricScene scene = IsometricUtils.getScene();

      try {
         DChocByteArray out = DChocByteArray.createByteArrayForWriting(8192);
         DChocByteArray objectData = DChocByteArray.createByteArrayForWriting(1024);
         out.writeUTF(SettingsWrapper.getVersion());
         Vector[] objects = scene.getObjects();
         int count = 0;

         for (int i = 0; i < objects.length; i++) {
            count += objects[i].size();
         }

         out.writeInt(count);
         ObjectLoaderOld.writeCrash(out);

         for (int i = 0; i < objects.length; i++) {
            for (int j = 0; j < objects[i].size(); j++) {
               out.writeByte(ObjectLoader.save((IsometricObject)objects[i].elementAt(j), objectData));
            }
         }

         out.writeBytes(objectData);
         ToolkitHelpers.writeLong(scene.getUpdateBreakableObjects(), out);
         Profiler.stopTimer(9);
         return out;
      } catch (Exception exception) {
         new ErrorEvent().trackError("SceneLoader.saveCurrent", exception.getMessage(), null);
         System.err.println(exception);
         Profiler.stopTimer(9);
         return null;
      }
   }
}
