package javax.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnectionImpl implements HttpConnection {
   private HttpURLConnection connection;
   protected boolean isConnected = false;
   private URL url;

   public HttpConnectionImpl(String var1) throws IOException {
      this.url = new URL(var1);
      this.connection = (HttpURLConnection)this.url.openConnection();
   }

   private int getImplIndex(int var1) {
      int var2 = var1;
      if (this.connection.getHeaderFieldKey(0) == null) {
         var2 = var1;
         if (this.connection.getHeaderField(0) != null) {
            var2 = var1 + 1;
         }
      }

      return var2;
   }

   @Override
   public void close() throws IOException {
      if (this.connection != null) {
         this.connection.disconnect();
         this.connection = null;
      }
   }

   @Override
   public long getDate() throws IOException {
      if (this.connection == null) {
         throw new IOException();
      }

      if (!this.isConnected) {
         this.connection.connect();
         this.isConnected = true;
      }

      return this.connection.getDate();
   }

   @Override
   public String getEncoding() {
      String var1;
      try {
         var1 = this.getHeaderField("content-encoding");
      } catch (IOException var2) {
         var1 = null;
      }

      return var1;
   }

   @Override
   public long getExpiration() throws IOException {
      if (this.connection == null) {
         throw new IOException();
      }

      if (!this.isConnected) {
         this.connection.connect();
         this.isConnected = true;
      }

      return this.connection.getExpiration();
   }

   @Override
   public String getFile() {
      String var1;
      if (this.connection == null) {
         var1 = null;
      } else {
         var1 = this.connection.getURL().getFile();
      }

      return var1;
   }

   @Override
   public String getHeaderField(int var1) throws IOException {
      if (this.connection == null) {
         throw new IOException();
      }

      if (!this.isConnected) {
         this.connection.connect();
         this.isConnected = true;
      }

      return this.connection.getHeaderField(this.getImplIndex(var1));
   }

   @Override
   public String getHeaderField(String var1) throws IOException {
      if (this.connection == null) {
         throw new IOException();
      }

      if (!this.isConnected) {
         this.connection.connect();
         this.isConnected = true;
      }

      return this.connection.getHeaderField(var1);
   }

   @Override
   public long getHeaderFieldDate(String var1, long var2) throws IOException {
      if (this.connection == null) {
         throw new IOException();
      }

      if (!this.isConnected) {
         this.connection.connect();
         this.isConnected = true;
      }

      return this.connection.getHeaderFieldDate(var1, var2);
   }

   @Override
   public int getHeaderFieldInt(String var1, int var2) throws IOException {
      if (this.connection == null) {
         throw new IOException();
      }

      if (!this.isConnected) {
         this.connection.connect();
         this.isConnected = true;
      }

      return this.connection.getHeaderFieldInt(var1, var2);
   }

   @Override
   public String getHeaderFieldKey(int var1) throws IOException {
      if (this.connection == null) {
         throw new IOException();
      }

      if (!this.isConnected) {
         this.connection.connect();
         this.isConnected = true;
      }

      return this.connection.getHeaderFieldKey(this.getImplIndex(var1));
   }

   @Override
   public String getHost() {
      String var1;
      if (this.connection == null) {
         var1 = null;
      } else {
         var1 = this.connection.getURL().getHost();
      }

      return var1;
   }

   @Override
   public long getLastModified() throws IOException {
      if (this.connection == null) {
         throw new IOException();
      }

      if (!this.isConnected) {
         this.connection.connect();
         this.isConnected = true;
      }

      return this.connection.getLastModified();
   }

   @Override
   public long getLength() {
      int var1;
      try {
         var1 = this.getHeaderFieldInt("content-length", -1);
      } catch (IOException var5) {
         return -1L;
      }

      return var1;
   }

   @Override
   public int getPort() {
      int var1 = -1;
      if (this.connection != null) {
         var1 = this.connection.getURL().getPort();
         if (var1 == -1) {
            var1 = 80;
         }
      }

      return var1;
   }

   @Override
   public String getProtocol() {
      return "http";
   }

   @Override
   public String getQuery() {
      return null;
   }

   @Override
   public String getRef() {
      String var1;
      if (this.connection == null) {
         var1 = null;
      } else {
         var1 = this.connection.getURL().getRef();
      }

      return var1;
   }

   @Override
   public String getRequestMethod() {
      String var1;
      if (this.connection == null) {
         var1 = null;
      } else {
         var1 = this.connection.getRequestMethod();
      }

      return var1;
   }

   @Override
   public String getRequestProperty(String var1) {
      if (this.connection == null) {
         var1 = null;
      } else {
         var1 = this.connection.getRequestProperty(var1);
      }

      return var1;
   }

   @Override
   public int getResponseCode() throws IOException {
      if (this.connection == null) {
         throw new IOException();
      }

      if (!this.isConnected) {
         this.connection.connect();
         this.isConnected = true;
      }

      return this.connection.getResponseCode();
   }

   @Override
   public String getResponseMessage() throws IOException {
      if (this.connection == null) {
         throw new IOException();
      }

      if (!this.isConnected) {
         this.connection.connect();
         this.isConnected = true;
      }

      return this.connection.getResponseMessage();
   }

   @Override
   public String getType() {
      String var1;
      try {
         var1 = this.getHeaderField("content-type");
      } catch (IOException var2) {
         var1 = null;
      }

      return var1;
   }

   @Override
   public String getURL() {
      String var1;
      if (this.connection == null) {
         var1 = null;
      } else {
         var1 = this.connection.getURL().toString();
      }

      return var1;
   }

   @Override
   public DataInputStream openDataInputStream() throws IOException {
      return new DataInputStream(this.openInputStream());
   }

   @Override
   public DataOutputStream openDataOutputStream() throws IOException {
      return new DataOutputStream(this.openOutputStream());
   }

   @Override
   public InputStream openInputStream() throws IOException {
      if (this.connection == null) {
         throw new IOException();
      }

      this.isConnected = true;
      return this.connection.getInputStream();
   }

   @Override
   public OutputStream openOutputStream() throws IOException {
      if (this.connection == null) {
         throw new IOException();
      }

      this.isConnected = true;
      return this.connection.getOutputStream();
   }

   @Override
   public void setRequestMethod(String var1) throws IOException {
      if (this.connection == null) {
         throw new IOException();
      }

      if (var1.equals("POST")) {
         this.connection.setDoOutput(true);
      }

      this.connection.setRequestMethod(var1);
   }

   @Override
   public void setRequestProperty(String var1, String var2) throws IOException {
      if (this.connection != null && !this.isConnected) {
         this.connection.setRequestProperty(var1, var2);
      } else {
         throw new IOException();
      }
   }
}
