package javax.microedition.io;

import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.sensor.AccelerometerConnection;
import javax.microedition.sensor.AccelerometerInfo;

public class Connector {
   public static Connection open(String var0) throws IOException {
      Object var1;
      if (var0.equals(AccelerometerInfo.ACCELEROMETER_URL)) {
         var1 = new AccelerometerConnection();
         MIDlet.getMIDletInstance().setAccelerometerConnection((AccelerometerConnection)var1);
      } else if (var0.startsWith("http:")) {
         var1 = new HttpConnectionImpl(var0);
      } else {
         var1 = null;
      }

      return (Connection)var1;
   }
}
