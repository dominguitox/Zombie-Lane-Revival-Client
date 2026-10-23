package javax.microedition.sensor;

import java.io.IOException;

public interface SensorConnection {
   int STATE_CLOSED = 4;
   int STATE_LISTENING = 2;
   int STATE_OPENED = 1;

   Channel getChannel(ChannelInfo var1);

   Data[] getData(int var1) throws IOException;

   Data[] getData(int var1, long var2, boolean var4, boolean var5, boolean var6) throws IOException;

   SensorInfo getSensorInfo();

   int getState();

   void removeDataListener();

   void setDataListener(DataListener var1, int var2);

   void setDataListener(DataListener var1, int var2, long var3, boolean var5, boolean var6, boolean var7);
}
