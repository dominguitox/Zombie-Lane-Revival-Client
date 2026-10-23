package javax.microedition.sensor;

public class AccelerometerData implements Data {
   ChannelInfo channelInfo;
   private double[] dataSet;
   private int[] dataSetInt;

   public AccelerometerData(ChannelInfo var1) {
      this.channelInfo = var1;
      this.dataSetInt = new int[1];
   }

   @Override
   public ChannelInfo getChannelInfo() {
      return this.channelInfo;
   }

   @Override
   public double[] getDoubleValues() {
      throw new RuntimeException("unsupported method");
   }

   @Override
   public int[] getIntValues() {
      return this.dataSetInt;
   }

   @Override
   public Object[] getObjectValues() {
      throw new RuntimeException("unsupported method");
   }

   @Override
   public long getTimestamp(int var1) {
      throw new RuntimeException("unsupported method");
   }

   @Override
   public float getUncertainty(int var1) {
      throw new RuntimeException("unsupported method");
   }

   @Override
   public boolean isValid(int var1) {
      throw new RuntimeException("unsupported method");
   }

   @Override
   public void setData(double[] var1) {
      this.dataSet = var1;
      this.dataSetInt[0] = (int)var1[0];
   }
}
