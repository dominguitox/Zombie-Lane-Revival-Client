package com.dchoc.spriteobject;

import com.dchoc.iphonewrappers.FileWrapper;
import com.dchoc.iphonewrappers.SettingsWrapper;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.DChocHTTPConnection;
import com.dchoc.toolkit.HTTPRequest;
import com.dchoc.toolkit.HTTPResponse;
import com.dchoc.toolkit.IHTTPResponseListener;
import java.util.Enumeration;
import java.util.Vector;
import zlport.ios.Ios;

public class ServerBundle implements IHTTPResponseListener {
   public static final boolean USE_BUNDLE_VERSION = true;
   public static String IMAGE_BASE_URL = "server_assets/";
   private final String[] mBundleAssets;
   private final int mBundleID;
   private DChocHTTPConnection mConnection;
   private boolean mFailed;
   private final String mFileBundlePath;
   private boolean mInLoading;
   private boolean mInformedListeners;
   private Vector mListeners;
   private boolean mLoadedAssets;
   private int mLoadingIndex;
   public String mURL = SettingsWrapper.getAssetServerURL();

   public ServerBundle(int bundleId, String version, String[] assets) {
      this.mBundleID = bundleId;
      this.mBundleAssets = assets;
      this.mFileBundlePath = "bundle_" + bundleId + "/" + version + "/";
      String bundlePath = "bundle_" + bundleId;
      if (!Ios.platform().cacheFileExists(bundlePath)) {
         Ios.platform().createCacheDirectory(bundlePath, false);
      }

      String versionPath = bundlePath + "/" + version;
      if (!Ios.platform().cacheFileExists(versionPath)) {
         Ios.platform().createCacheDirectory(versionPath, false);
      }
   }

   public void addListener(ServerAssetListener listener) {
      if (this.mListeners == null) {
         this.mListeners = new Vector(20);
      }

      this.mListeners.addElement(listener);
      if (this.mInformedListeners) {
         listener.assetBundleLoaded(this.mBundleID, true);
      }
   }

   public int getID() {
      return this.mBundleID;
   }

   public boolean loadBundle() {
      return this.loadNext();
   }

   public boolean loaded() {
      return this.mLoadingIndex == (this.mBundleAssets == null ? 0 : this.mBundleAssets.length);
   }

   private boolean loadNext() {
      if (!this.loaded()) {
         return this.loadFile(this.mLoadingIndex) ? true : this.loadNext();
      }

      if (this.mListeners != null && this.mLoadedAssets) {
         if (!this.mFailed) {
            DavinciUtilities.setBundleState(this.mBundleID, (byte)2);
         }

         Enumeration listeners = this.mListeners.elements();

         while (listeners.hasMoreElements()) {
            ((ServerAssetListener)listeners.nextElement()).assetBundleLoaded(this.mBundleID, !this.mFailed);
         }

         if (!this.mFailed) {
            this.mInformedListeners = true;
            this.mListeners.removeAllElements();
            this.mListeners = null;
         } else {
            this.mFailed = false;
            this.mLoadingIndex = 0;
         }
      }

      return this.mLoadedAssets;
   }

   private boolean loadFile(int index) {
      while (FileWrapper.fileExists(this.mFileBundlePath + this.mBundleAssets[this.mLoadingIndex])) {
         this.mInLoading = false;
         index = ++this.mLoadingIndex;
         if (index == this.mBundleAssets.length) {
            return false;
         }
      }

      this.mLoadedAssets = true;
      String url = this.mURL + this.mFileBundlePath + this.mBundleAssets[index];
      this.mConnection = new DChocHTTPConnection();
      this.mConnection.setResponseListener(this);
      this.mConnection.sendRequest(new HTTPRequest(url));
      this.mInLoading = true;
      return true;
   }

   public void responseReceived(HTTPResponse response) {
      if (response != null && response.getResponseCode() == 200) {
         DChocByteArray received = response.getData();
         byte[] data = received == null ? null : received.getBytes();
         if (data != null) {
            String path = this.mFileBundlePath + this.mBundleAssets[this.mLoadingIndex];
            if (!FileWrapper.fileExists(path)) {
               Ios.platform().writeCacheFileAtomically(path, data);
            }

            if (this.mLoadingIndex == this.mBundleAssets.length - 1) {
               DavinciUtilities.readTextureBundleData(response.getData(), true);
            }
         }
      } else {
         this.mFailed = true;
      }

      this.mLoadingIndex++;
      this.mInLoading = false;
      this.loadNext();
   }

   public void percentageDataReceived(int percentage) {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerBundle.percentageDataReceived");
   }

   public void responseCodeReceived(int code) {
      throw new UnsupportedOperationException("sem código na 1.4.4: ServerBundle.responseCodeReceived");
   }
}
