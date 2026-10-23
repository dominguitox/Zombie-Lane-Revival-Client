package com.dchoc.idead.touch;

public class TouchEvent {
   public static final int ID_UNKNOWN = -1;
   public static final int TYPE_DRAGGED = 1;
   public static final int TYPE_GESTURE = 4;
   public static final int TYPE_HOLD = 5;
   public static final int TYPE_NONE = -1;
   public static final int TYPE_PRESSED = 0;
   public static final int TYPE_RELEASED = 2;
   public static final int TYPE_TAPPED = 3;
   private boolean mConsumed;
   private int mDeltaX;
   private int mDeltaY;
   private int mId;
   private int mInitialX;
   private int mInitialY;
   private int mType;
   private int mX;
   private int mY;

   public TouchEvent() {
      this.mX = -1;
      this.mY = -1;
      this.mInitialX = -1;
      this.mInitialY = -1;
      this.mType = -1;
      this.mConsumed = true;
      this.mDeltaX = 0;
      this.mDeltaY = 0;
   }

   public TouchEvent(int x, int y, int initialX, int initialY, int type) {
      this.mX = x;
      this.mY = y;
      this.mInitialX = initialX;
      this.mInitialY = initialY;
      this.mType = type;
      this.mConsumed = false;
      this.mDeltaX = x - initialX;
      this.mDeltaY = y - initialY;
   }

   public void assign(TouchEvent other) {
      this.init(other.mX, other.mY, other.mInitialX, other.mInitialY, other.mType, other.mId, other.mConsumed);
   }

   public void calculateDelta(int x, int y) {
      this.mDeltaX = this.mX - x;
      this.mDeltaY = this.mY - y;
   }

   public int getDeltaX() {
      return this.mDeltaX;
   }

   public int getDeltaY() {
      return this.mDeltaY;
   }

   public int getId() {
      return this.mId;
   }

   public int getInitialX() {
      return this.mInitialX;
   }

   public int getInitialY() {
      return this.mInitialY;
   }

   public int getType() {
      return this.mType;
   }

   public int getX() {
      return this.mX;
   }

   public int getY() {
      return this.mY;
   }

   public void init(int x, int y, int initialX, int initialY, int type, int id, boolean consumed) {
      this.mType = type;
      this.mX = x;
      this.mY = y;
      this.mInitialX = initialX;
      this.mInitialY = initialY;
      this.mConsumed = consumed;
      this.calculateDelta(this.mInitialX, this.mInitialY);
   }

   public boolean isConsumed() {
      return this.mConsumed;
   }

   public void setConsumed(boolean consumed) {
      this.mConsumed = consumed;
   }

   public void setDelta(int deltaX, int deltaY) {
      this.mDeltaX = deltaX;
      this.mDeltaY = deltaY;
   }

   public void setPosition(int x, int y) {
      this.mX = x;
      this.mY = y;
   }

   @Override
   public String toString() {
      String name;
      if (this.mType == 0) {
         name = "Pressed";
      } else if (this.mType == 1) {
         name = "Dragged";
      } else if (this.mType == 2) {
         name = "Released";
      } else if (this.mType == 3) {
         name = "Tapped";
      } else {
         name = "Unknown";
      }

      return "Type: "
         + name
         + " Position: "
         + this.mX
         + " "
         + this.mY
         + " Initial: "
         + this.mInitialX
         + " "
         + this.mInitialY
         + " Delta: "
         + this.mDeltaX
         + " "
         + this.mDeltaY
         + " Id: "
         + this.mId;
   }
}
