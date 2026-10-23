package javax.microedition.sensor;

import java.io.IOException;
import javax.microedition.io.Connection;
import zlport.stubs.NotPorted;

public class AccelerometerConnection implements SensorConnection, Connection {
   public AccelerometerConnection() {
      NotPorted.method("javax.microedition.sensor.AccelerometerConnection#<init>()");
   }

   public void close() throws IOException {
      NotPorted.method("javax.microedition.sensor.AccelerometerConnection#close()");
   }

   public Channel getChannel(ChannelInfo p0) {
      NotPorted.method("javax.microedition.sensor.AccelerometerConnection#getChannel(ChannelInfo)");
      return null;
   }

   public Data[] getData(int p0) throws IOException {
      NotPorted.method("javax.microedition.sensor.AccelerometerConnection#getData(int)");
      return null;
   }

   public Data[] getData(int p0, long p1, boolean p2, boolean p3, boolean p4) throws IOException {
      NotPorted.method("javax.microedition.sensor.AccelerometerConnection#getData(int,long,boolean,boolean,boolean)");
      return null;
   }

   public SensorInfo getSensorInfo() {
      NotPorted.method("javax.microedition.sensor.AccelerometerConnection#getSensorInfo()");
      return null;
   }

   public int getState() {
      NotPorted.method("javax.microedition.sensor.AccelerometerConnection#getState()");
      return 0;
   }

   public void removeDataListener() {
      NotPorted.method("javax.microedition.sensor.AccelerometerConnection#removeDataListener()");
   }

   public void setDataListener(DataListener p0, int p1) {
      NotPorted.method("javax.microedition.sensor.AccelerometerConnection#setDataListener(DataListener,int)");
   }

   public void setDataListener(DataListener p0, int p1, long p2, boolean p3, boolean p4, boolean p5) {
      NotPorted.method("javax.microedition.sensor.AccelerometerConnection#setDataListener(DataListener,int,long,boolean,boolean,boolean)");
   }
}
