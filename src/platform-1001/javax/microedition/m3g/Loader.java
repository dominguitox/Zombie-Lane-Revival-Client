package javax.microedition.m3g;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Image;

public class Loader {
   static int BLOCK_SIZE;
   static final int ERROR_ABORT = -2;
   static final int ERROR_DATAPASTEOF = -11;
   static final int ERROR_INVALIDBODY = -8;
   static final int ERROR_INVALIDCHECKSUM = -9;
   static final int ERROR_INVALIDHEADER = -7;
   static final int ERROR_NONE = -1;
   static final int ERROR_NOTFOUND = -3;
   static final int ERROR_READ = -4;
   static final int ERROR_UNABLETOCREATE = -5;
   static final int ERROR_UNABLETOFREE = -6;
   static final int ERROR_UNKNOWN = -10;
   static int m3gIdentifierLength;
   int swerveHandle;

   Loader() {
   }

   Loader(int var1) {
   }

   static Image createImage(String var0) throws IOException {
      return null;
   }

   static Image createImage(byte[] var0, int var1, int var2) {
      return null;
   }

   private static native int createImpl();

   private Object3D[] createRoots() {
      return null;
   }

   static int getImageHeight(Image var0) {
      return -1;
   }

   static void getImageRGB(Image var0, int[] var1, int var2, int var3, int var4, int var5, int var6, int var7) {
   }

   static int getImageWidth(Image var0) {
      return -1;
   }

   private native int getRootImpl(int var1);

   static boolean isOpaque(Image var0) {
      return false;
   }

   private static boolean isRelative(String var0) {
      return false;
   }

   public static Object3D[] load(String var0) throws IOException {
      return null;
   }

   public static Object3D[] load(byte[] var0, int var1) throws IOException {
      return null;
   }

   private static Object3D[] loadHelper(String var0, boolean var1) throws IOException {
      return null;
   }

   private static Object3D[] loadHelper(byte[] var0, int var1, boolean var2) throws IOException {
      return null;
   }

   static InputStream openInputStream(String var0) throws IOException {
      return null;
   }

   private void processXREFs(String var1) throws IOException {
   }

   private static String resolveURI(String var0, String var1) {
      return null;
   }

   native int getRootCount();

   native int getXREFName(byte[] var1);

   native boolean getbContainsExtensions();

   native int onData(byte[] var1, int var2, int var3);

   native int onDataEnd();

   native int onDataStart(boolean var1);

   native void onError(int var1);

   native void resolveXREF(Object3D var1);
}
