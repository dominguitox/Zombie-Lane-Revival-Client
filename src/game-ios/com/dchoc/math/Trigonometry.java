package com.dchoc.math;

public class Trigonometry {
   private static final double PI = Math.PI;
   private static final double PIO2 = Math.PI / 2;
   private static final double p0 = 896.7859740366387;
   private static final double p1 = 1780.406316433197;
   private static final double p2 = 1153.029351540485;
   private static final double p3 = 268.42548195503974;
   private static final double p4 = 16.15364129822302;
   private static final double q0 = 896.7859740366387;
   private static final double q1 = 2079.33497444541;
   private static final double q2 = 1666.7838148816338;
   private static final double q3 = 536.2653740312153;
   private static final double q4 = 58.95697050844462;
   private static final double sq2m1 = 0.41421356237309503;
   private static final double sq2p1 = 2.414213562373095;

   public static double atan2(double y, double x) {
      if (y + x == y) {
         return y < 0.0 ? -Math.PI / 2 : Math.PI / 2;
      }

      double ratio = y / x;
      double angle;
      if (ratio <= 0.0) {
         angle = -msatan(-ratio);
      } else {
         angle = msatan(ratio);
      }

      if (x < 0.0) {
         return angle <= 0.0 ? angle + Math.PI : angle - Math.PI;
      } else {
         return angle;
      }
   }

   private static double msatan(double d) {
      if (d < 0.41421356237309503) {
         return rational(d * d) * d;
      } else if (d > 2.414213562373095) {
         double inv = 1.0 / d;
         return (Math.PI / 2) - rational(inv * inv) * inv;
      } else {
         double t = (d - 1.0) / (d + 1.0);
         return (Math.PI / 4) + rational(t * t) * t;
      }
   }

   private static double rational(double d2) {
      double num = (((16.15364129822302 * d2 + 268.42548195503974) * d2 + 1153.029351540485) * d2 + 1780.406316433197) * d2 + 896.7859740366387;
      double den = d2 * ((((d2 + 58.95697050844462) * d2 + 536.2653740312153) * d2 + 1666.7838148816338) * d2 + 2079.33497444541) + 896.7859740366387;
      return num / den;
   }
}
