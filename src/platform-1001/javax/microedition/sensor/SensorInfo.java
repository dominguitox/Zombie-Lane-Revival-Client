package javax.microedition.sensor;

public interface SensorInfo {
   int CONN_EMBEDDED = 1;
   int CONN_REMOTE = 2;
   int CONN_SHORT_RANGE_WIRELESS = 4;
   int CONN_WIRED = 8;

   ChannelInfo[] getChannelInfos();

   int getConnectionType();

   String getContextType();

   String getDescription();

   int getMaxBufferSize();

   String getModel();

   Object getProperty(String var1);

   String[] getPropertyNames();

   String getQuantity();

   String getUrl();

   boolean isAvailabilityPushSupported();

   boolean isAvailable();

   boolean isConditionPushSupported();
}
