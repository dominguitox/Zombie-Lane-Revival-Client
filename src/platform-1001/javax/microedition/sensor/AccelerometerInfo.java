package javax.microedition.sensor;

public class AccelerometerInfo implements SensorInfo {
   public static String ACCELEROMETER_URL = "sensor:android_accelerometer";
   private static AccelerometerChannelInfo[] accChannelInfos;

   public AccelerometerInfo() {
      accChannelInfos = new AccelerometerChannelInfo[3];
      accChannelInfos[0] = new AccelerometerChannelInfo("x");
      accChannelInfos[1] = new AccelerometerChannelInfo("y");
      accChannelInfos[2] = new AccelerometerChannelInfo("z");
   }

   @Override
   public ChannelInfo[] getChannelInfos() {
      return accChannelInfos;
   }

   @Override
   public int getConnectionType() {
      return 1;
   }

   @Override
   public String getContextType() {
      return SensorManager.CONTEXT_ACCELERATION;
   }

   @Override
   public String getDescription() {
      return "Description is not available.";
   }

   @Override
   public int getMaxBufferSize() {
      throw new RuntimeException("unsupported method");
   }

   @Override
   public String getModel() {
      return "android_accelerometer";
   }

   @Override
   public Object getProperty(String var1) {
      throw new RuntimeException("unsupported method");
   }

   @Override
   public String[] getPropertyNames() {
      throw new RuntimeException("unsupported method");
   }

   @Override
   public String getQuantity() {
      return SensorManager.CONTEXT_ACCELERATION;
   }

   @Override
   public String getUrl() {
      return ACCELEROMETER_URL;
   }

   @Override
   public boolean isAvailabilityPushSupported() {
      return false;
   }

   @Override
   public boolean isAvailable() {
      return true;
   }

   @Override
   public boolean isConditionPushSupported() {
      return false;
   }
}
