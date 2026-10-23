package com.dchoc.idead.touch;

import java.util.Vector;

public class TouchManager {
   public static final int DEFAULT_POOL_SIZE = 48;
   private int mActiveGestureId;
   private int mActiveTouchId;
   private Vector mEvents = new Vector(48);
   private Vector mEventsPool = new Vector(48);
   private int mHoldTime;
   private boolean mHoldTimerActivated;
   private boolean mIgnoreTouchEvents;
   private TouchEvent mLastGestureEvent;
   private TouchEvent mLastTouchEvent;

   public TouchManager() {
      for (int i = 0; i < 48; i++) {
         this.mEventsPool.addElement(new TouchEvent());
      }

      this.mActiveTouchId = -1;
      this.mLastTouchEvent = new TouchEvent();
      this.mActiveGestureId = -1;
      this.mLastGestureEvent = new TouchEvent();
      this.mHoldTime = 0;
      this.mHoldTimerActivated = true;
      this.mIgnoreTouchEvents = false;
   }

   private TouchEvent createEvent(int x, int y, int initialX, int initialY, int type, int id) {
      if (this.mEventsPool.isEmpty()) {
         return new TouchEvent(x, y, initialX, initialY, type);
      }

      TouchEvent event = (TouchEvent)this.mEventsPool.lastElement();
      event.init(x, y, initialX, initialY, type, id, false);
      this.mEventsPool.removeElementAt(this.mEventsPool.size() - 1);
      return event;
   }

   public boolean hasEvents() {
      return !this.mEvents.isEmpty();
   }

   public int logicUpdate(int delta) {
      if (this.mHoldTimerActivated) {
         this.mHoldTime += delta;
         if (this.mHoldTime >= 500) {
            int x = this.mLastTouchEvent != null ? this.mLastTouchEvent.getInitialX() : 0;
            int y = this.mLastTouchEvent != null ? this.mLastTouchEvent.getInitialY() : 0;
            int id = this.mLastTouchEvent != null ? this.mLastTouchEvent.getId() : -1;
            this.mEvents.addElement(this.createEvent(x, y, x, y, 5, id));
            this.mHoldTimerActivated = false;
         }
      }

      return 0;
   }

   public TouchEvent popEvent() {
      if (!this.hasEvents()) {
         return null;
      }

      TouchEvent event = (TouchEvent)this.mEvents.elementAt(0);
      this.mEvents.removeElementAt(0);
      return event;
   }

   public void pushEvent(TouchEvent event) {
      this.mEventsPool.addElement(event);
   }

   public void setIgnoreTouchEvents(boolean ignore) {
      this.mIgnoreTouchEvents = ignore;
   }

   public void touchEventOccurred(int[][] events, int[] unused) {
      if (!this.mIgnoreTouchEvents) {
         if (events != null && events.length == 1) {
            this.processTouchEvent(events[0], false);
         } else {
            this.processGestureEvent(events);
         }
      }
   }

   private void processTouchEvent(int[] event, boolean fromGesture) {
      this.mActiveGestureId = -1;
      int x = event[0];
      int y = event[1];
      int type = event[2];
      int id = event[3];
      if (this.mActiveTouchId == -1 || this.mActiveTouchId == id) {
         TouchEvent created = null;
         if (type == 2) {
            if (this.mLastTouchEvent.getType() != 1) {
               int dx = x - this.mLastTouchEvent.getInitialX();
               int dy = y - this.mLastTouchEvent.getInitialY();
               if (dx * dx + dy * dy < 6) {
                  this.mLastTouchEvent.setPosition(x, y);
                  return;
               }
            }

            if (this.mLastTouchEvent.getType() != 1 && this.mLastTouchEvent.getType() != 0) {
               TouchEvent pressed = this.createEvent(x, y, x, y, 0, id);
               this.mEvents.addElement(pressed);
               this.mLastTouchEvent.assign(pressed);
            }

            this.mHoldTimerActivated = false;
            created = this.createEvent(x, y, this.mLastTouchEvent.getInitialX(), this.mLastTouchEvent.getInitialY(), 1, id);
            created.calculateDelta(this.mLastTouchEvent.getX(), this.mLastTouchEvent.getY());
         } else if (type == 1) {
            if (this.mLastTouchEvent.getType() != -1) {
               created = this.createEvent(x, y, this.mLastTouchEvent.getInitialX(), this.mLastTouchEvent.getInitialY(), 2, id);
               if (this.mLastTouchEvent.getType() != 1 && this.mHoldTimerActivated && !fromGesture) {
                  this.mEvents.addElement(this.createEvent(x, y, this.mLastTouchEvent.getInitialX(), this.mLastTouchEvent.getInitialY(), 3, id));
               }
            }

            this.mHoldTimerActivated = false;
         } else {
            if (type != 0) {
               return;
            }

            if (this.mLastTouchEvent.getType() == 0 && this.mLastTouchEvent.getX() == x && this.mLastTouchEvent.getY() == y) {
               return;
            }

            created = this.createEvent(x, y, x, y, 0, id);
            this.mHoldTimerActivated = true;
            this.mHoldTime = 0;
         }

         if (created != null) {
            this.mEvents.addElement(created);
            this.mLastTouchEvent.assign(created);
            if (type == 1) {
               this.mActiveTouchId = -1;
            } else {
               this.mActiveTouchId = id;
            }
         }
      }
   }

   private void processGestureEvent(int[][] events) {
      boolean wasGesture = this.mActiveGestureId != -1;
      int touchX = -1;
      int touchY = -1;
      int gestureX = -1;
      int gestureY = -1;
      int count = events == null ? 0 : events.length;

      for (int i = 0; i < count; i++) {
         int[] event = events[i];
         event[0] = Math.abs(event[0]);
         event[1] = Math.abs(event[1]);
         int x = event[0];
         int y = event[1];
         int type = event[2];
         int id = event[3];
         if (type == 2) {
            if (this.mActiveTouchId != -1 && id == this.mActiveTouchId) {
               this.mLastTouchEvent.setPosition(x, y);
            }
         } else if (type == 1) {
            if (this.mActiveTouchId != -1 && id == this.mActiveTouchId) {
               this.processTouchEvent(event, true);
            } else if (this.mActiveGestureId != -1 && id == this.mActiveGestureId) {
               this.mActiveGestureId = -1;
            }
         } else if (type == 0) {
            int gesture = id;
            boolean asTouch = false;
            if (id != this.mActiveGestureId) {
               gesture = this.mActiveGestureId;
               asTouch = this.mActiveTouchId == -1;
            }

            if (asTouch) {
               this.processTouchEvent(event, true);
            } else if (gesture == -1 && id != this.mActiveTouchId) {
               this.mActiveGestureId = id;
            }
         }

         if (id == this.mActiveTouchId) {
            touchX = x;
            touchY = y;
         } else if (id == this.mActiveGestureId) {
            gestureX = x;
            gestureY = y;
         }
      }

      if (gestureX != -1 && gestureY != -1 && touchX != -1 && touchY != -1) {
         TouchEvent gesture = this.createEvent(gestureX, gestureY, touchX, touchY, 4, this.mActiveGestureId);
         if (wasGesture) {
            int dx = Math.abs(gestureX - touchX) - Math.abs(this.mLastGestureEvent.getX() - this.mLastGestureEvent.getInitialX());
            int dy = Math.abs(gestureY - touchY) - Math.abs(this.mLastGestureEvent.getY() - this.mLastGestureEvent.getInitialY());
            gesture.setDelta(dx, dy);
         } else {
            gesture.setDelta(0, 0);
         }

         this.mEvents.addElement(gesture);
         this.mLastGestureEvent.assign(gesture);
      }
   }
}
