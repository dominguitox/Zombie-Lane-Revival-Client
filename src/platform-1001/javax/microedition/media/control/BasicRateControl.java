package javax.microedition.media.control;

public class BasicRateControl implements RateControl {
   public BasicRateControl() {
      throw new RuntimeException("not implemented");
   }

   @Override
   public int getMaxRate() {
      return 0;
   }

   @Override
   public int getMinRate() {
      return 0;
   }

   @Override
   public int setRate(int var1) {
      return 0;
   }
}
