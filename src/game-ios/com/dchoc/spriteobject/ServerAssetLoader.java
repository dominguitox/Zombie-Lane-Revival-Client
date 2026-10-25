package com.dchoc.spriteobject;

import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.tracking.ErrorEvent;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.iphonewrappers.FileWrapper;
import com.dchoc.toolkit.IByteArray;
import com.dchoc.toolkit.Sonda;
import com.dchoc.toolkit.Toolkit;

public class ServerAssetLoader implements ServerAssetListener {
   private static final String PNG_EXTENSION = ".png";
   private static final String BUNDLE_PREFIX = "0";
   private static boolean smEnabled = true;
   private static ServerAssetLoader smInstance;
   private static int smLoadingIndex;
   private static ServerBundle[] smLoadingQueue;
   private static int smPrioIndex;
   private boolean mSomeFailed;
   private boolean mLoadingStopped;

   public static void init() {
      if (!DeviceWrapper.useHDGraphics()) {
         smEnabled = false;
      } else {
         smInstance = new ServerAssetLoader();
         FileWrapper.removeAllFiles();
         IByteArray data = Toolkit.getResourceByteArray(80409);

         try {
            smLoadingQueue = new ServerBundle[data.readInt()];

            for (int i = 0; i < smLoadingQueue.length; i++) {
               int bundleId = data.readInt();
               int version = data.readInt();
               int[] textureIds = new int[data.readInt()];
               String[] assets = new String[textureIds.length + 1];
               assets[assets.length - 1] = "0";

               for (int n = 0; n < textureIds.length; n++) {
                  textureIds[n] = n + 1;
                  assets[n] = textureIds[n] + ".png";
               }

               smLoadingQueue[i] = new ServerBundle(bundleId, DavinciUtilities.getVersionText(version), assets);
               smLoadingQueue[i].addListener(smInstance);
               DavinciUtilities.setServerTextureData(bundleId, textureIds);
            }
         } catch (Exception exception) {
            new ErrorEvent().trackBundleError("ServerAssetLoader.getUnlinkedRID", exception.getMessage(), null);
            Sonda.probe("ServerAssetLoader.getUnlinkedRID", exception);
         }

         smInstance.loadNextBundle();
      }
   }

   public static boolean requestBundle(int bundleId, ServerAssetListener listener) {
      if (smEnabled && smLoadingQueue != null) {
         for (ServerBundle bundle : smLoadingQueue) {
            if (bundle.getID() == bundleId) {
               if (listener != null) {
                  bundle.addListener(listener);
               }
               break;
            }
         }
      }

      return false;
   }

   public void loadNextBundle() {
      if (smLoadingQueue != null) {
         this.mLoadingStopped = false;

         while (smLoadingIndex < smLoadingQueue.length) {
            ServerBundle bundle = smLoadingQueue[smLoadingIndex];
            boolean loading = bundle != null && bundle.loadBundle();
            smLoadingIndex++;
            if (smPrioIndex < smLoadingIndex) {
               smPrioIndex = smLoadingIndex;
            }

            if (smLoadingIndex == smLoadingQueue.length) {
               if (!this.mSomeFailed) {
                  smLoadingQueue = null;
                  return;
               }

               this.mSomeFailed = false;
               smLoadingIndex = 0;
               if (!GameEngineUtils.isConnected()) {
                  this.mLoadingStopped = true;
                  return;
               }
            } else if (loading) {
               return;
            }
         }
      }
   }

   @Override
   public void assetBundleLoaded(int bundleId, boolean loaded) {
      if (loaded) {
         this.loadNextBundle();
      } else {
         this.mSomeFailed = true;
      }
   }

   public static void resumeLoading() {
      if (smEnabled && smInstance.mLoadingStopped) {
         smInstance.loadNextBundle();
      }
   }
}
