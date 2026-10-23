package javax.microedition.sensor;

public class AccelerometerChannelInfo implements ChannelInfo {
   private MeasurementRange[] mRange;
   private String name;

   public AccelerometerChannelInfo(String var1) {
      this.name = var1;
      this.mRange = new MeasurementRange[1];
      this.mRange[0] = new MeasurementRange();
   }

   @Override
   public float getAccuracy() {
      throw new RuntimeException("unsupported method");
   }

   @Override
   public int getDataType() {
      return 2;
   }

   @Override
   public MeasurementRange[] getMeasurementRanges() {
      return this.mRange;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public int getScale() {
      throw new RuntimeException("unsupported method");
   }

   @Override
   public Unit getUnit() {
      throw new RuntimeException("unsupported method");
   }
}
