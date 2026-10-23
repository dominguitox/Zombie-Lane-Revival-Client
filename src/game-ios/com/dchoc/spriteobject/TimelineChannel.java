package com.dchoc.spriteobject;

import com.dchoc.toolkit.IByteArray;
import java.io.IOException;

public class TimelineChannel {
   private static final int[] INT_ARRAY_SIZE_0 = new int[0];
   private static final int MODE_CONSTANT = 1;
   private static final int MODE_LINEAR = 0;
   private static final int MODE_QUAD_IN = 2;
   private static final int MODE_QUAD_IN_AND_OUT = 4;
   private static final int MODE_QUAD_OUT = 3;
   public static final int TIMELINE_COLOR_MODIFICATION = 0;
   public static final int TIMELINE_POS_ROTATION = 5;
   public static final int TIMELINE_POS_SCALE_HEIGHT = 4;
   public static final int TIMELINE_POS_SCALE_WIDTH = 3;
   public static final int TIMELINE_POS_X = 1;
   public static final int TIMELINE_POS_Y = 2;
   public static final int TIMELINE_TYPE_COUNT = 6;
   private int mDuration;
   private int[] mMode;
   private int[] mTime;
   private int mType;
   private int[] mValue;

   public TimelineChannel(int type, int duration) {
      this.mType = type;
      this.mDuration = duration;
   }

   private int easeInOutQuad(int start, int end, int t, int duration) {
      int twiceT = t * 2;
      if (twiceT < duration) {
         int quotient = twiceT * twiceT / duration;
         quotient = (end - start >> 1) * quotient;
         return quotient / duration + start;
      } else {
         int shifted = twiceT - duration;
         int quotient = shifted * (shifted - duration * 2) / duration;
         quotient = -(end - start >> 1) * (quotient - duration);
         return quotient / duration + start;
      }
   }

   private int easeInQuad(int start, int end, int t, int duration) {
      int quotient = t * t / duration;
      quotient = (end - start) * quotient / duration;
      return quotient + start;
   }

   private int easeOutQuad(int start, int end, int t, int duration) {
      int quotient = t * (start - end) / duration;
      quotient = (t - duration * 2) * quotient / duration;
      return quotient + start;
   }

   private int getIndexInTime(int time) {
      int index = this.mTime.length;

      while (--index >= 0) {
         if (time >= this.mTime[index]) {
            return index;
         }
      }

      return -1;
   }

   private int getNeutralValue() {
      if (this.mType == 0) {
         return -8355712;
      } else {
         return this.mType != 3 && this.mType != 4 ? 0 : 1024;
      }
   }

   public int getEventCount() {
      return this.mTime == null ? 0 : this.mTime.length;
   }

   public int getValue(int time, boolean loop) {
      if (this.mTime != null && this.mTime.length != 0) {
         int index = this.getIndexInTime(time);
         if (index == -1) {
            return -1;
         }

         if (this.mMode[index] == 1) {
            return this.mValue[index];
         }

         int elapsed = time - this.mTime[index];
         int nextIndex = index + 1;
         int span;
         if (nextIndex == this.mTime.length) {
            if (!loop) {
               return this.mValue[index];
            }

            nextIndex = 0;
            span = this.mDuration - this.mTime[index];
         } else {
            span = this.mTime[nextIndex] - this.mTime[index];
         }

         int start = this.mValue[index];
         int end = this.mValue[nextIndex];
         int mode = this.mMode[index];
         if (mode == 0) {
            return this.mType == 0
               ? DavinciUtilities.interpolateColor(start, end, elapsed, span)
               : DavinciUtilities.interpolateValue(start, end, elapsed, span);
         } else if (mode == 2) {
            return this.mType == 0
               ? this.easeInQuad(start & 0xFF, end & 0xFF, elapsed, span)
                  | this.easeInQuad(start >>> 24, end >>> 24, elapsed, span) << 24
                  | this.easeInQuad(start >> 16 & 0xFF, end >> 16 & 0xFF, elapsed, span) << 16
                  | this.easeInQuad(start >> 8 & 0xFF, end >> 8 & 0xFF, elapsed, span) << 8
               : this.easeInQuad(start, end, elapsed, span);
         } else if (mode == 3) {
            return this.mType == 0
               ? this.easeOutQuad(start & 0xFF, end & 0xFF, elapsed, span)
                  | this.easeOutQuad(start >>> 24, end >>> 24, elapsed, span) << 24
                  | this.easeOutQuad(start >> 16 & 0xFF, end >> 16 & 0xFF, elapsed, span) << 16
                  | this.easeOutQuad(start >> 8 & 0xFF, end >> 8 & 0xFF, elapsed, span) << 8
               : this.easeOutQuad(start, end, elapsed, span);
         } else if (mode != 4) {
            return -1;
         } else {
            return this.mType == 0
               ? this.easeInOutQuad(start & 0xFF, end & 0xFF, elapsed, span)
                  | this.easeInOutQuad(start >>> 24, end >>> 24, elapsed, span) << 24
                  | this.easeInOutQuad(start >> 16 & 0xFF, end >> 16 & 0xFF, elapsed, span) << 16
                  | this.easeInOutQuad(start >> 8 & 0xFF, end >> 8 & 0xFF, elapsed, span) << 8
               : this.easeInOutQuad(start, end, elapsed, span);
         }
      } else {
         return this.getNeutralValue();
      }
   }

   public void load(IByteArray data, boolean shortValues) throws IOException {
      int count = data.readUnsignedByte();
      if (count == 0) {
         this.mValue = INT_ARRAY_SIZE_0;
         this.mTime = INT_ARRAY_SIZE_0;
         this.mMode = INT_ARRAY_SIZE_0;
      } else {
         this.mValue = new int[count];
         this.mTime = new int[count];
         this.mMode = new int[count];

         for (int i = 0; i < count; i++) {
            this.mTime[i] = data.readInt();
            this.mMode[i] = data.readUnsignedByte();
            int value = -1;
            if (this.mType == 0 || this.mType == 3 || this.mType == 4) {
               value = data.readInt();
            } else if (this.mType != 1 && this.mType != 2) {
               if (this.mType == 5) {
                  value = data.readInt();
               }
            } else {
               value = shortValues ? data.readByte() : data.readShort();
            }

            this.mValue[i] = value;
         }
      }
   }
}
