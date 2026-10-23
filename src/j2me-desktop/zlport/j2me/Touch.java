package zlport.j2me;

public final class Touch {
   public static final int ACTION_DOWN = 0;
   public static final int ACTION_UP = 1;
   public static final int ACTION_MOVE = 2;
   public static final int ACTION_CANCEL = 3;
   public static final int ACTION_POINTER_DOWN = 5;
   public static final int ACTION_POINTER_UP = 6;
   public static final int ACTION_POINTER_INDEX_SHIFT = 8;
   private final int action;
   private final float[] x;
   private final float[] y;

   private Touch(int action, float[] x, float[] y) {
      this.action = action;
      this.x = x;
      this.y = y;
   }

   public static Touch one(int action, float x, float y) {
      return new Touch(action, new float[]{x}, new float[]{y});
   }

   public static Touch two(int action, float x0, float y0, float x1, float y1) {
      return new Touch(action, new float[]{x0, x1}, new float[]{y0, y1});
   }

   public int getAction() {
      return this.action;
   }

   public int getPointerCount() {
      return this.x.length;
   }

   public float getX() {
      return this.x[0];
   }

   public float getY() {
      return this.y[0];
   }

   public float getX(int pointer) {
      return this.x[pointer];
   }

   public float getY(int pointer) {
      return this.y[pointer];
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder("Touch[").append(this.action);

      for (int i = 0; i < this.x.length; i++) {
         s.append(' ').append(this.x[i]).append(',').append(this.y[i]);
      }

      return s.append(']').toString();
   }
}
