package javax.microedition.sensor;

public interface Data {
   ChannelInfo getChannelInfo();

   double[] getDoubleValues();

   int[] getIntValues();

   Object[] getObjectValues();

   long getTimestamp(int var1);

   float getUncertainty(int var1);

   boolean isValid(int var1);

   void setData(double[] var1);
}
