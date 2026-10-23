package com.dchoc.idead.events;

import com.dchoc.toolkit.ToolkitHelpers;
import java.util.Vector;

public class EventManager {
   public static final int INITIAL_EVENT_CAPACITY = 12;
   private static Vector smEvents;

   public static void init() {
      smEvents = new Vector(12);
   }

   public static int logicUpdate(int dt) {
      TimedEvent next = null;

      for (int i = 0; i < smEvents.size(); i++) {
         TimedEvent event = (TimedEvent)smEvents.elementAt(i);
         if (event.logicUpdate(dt) == 2 && (next == null || event.getTimeLeft() < next.getTimeLeft())) {
            next = event;
         }
      }

      if (next != null) {
         next.execute();
         ToolkitHelpers.fastRemove(next, smEvents);
      }

      return 0;
   }

   public static void addEvent(TimedEvent event) {
      if (event != null) {
         event.setActive(true);
         if (event.getDelay() != 0) {
            smEvents.addElement(event);
         } else {
            event.execute();
         }
      }
   }
}
