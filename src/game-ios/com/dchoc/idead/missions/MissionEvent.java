package com.dchoc.idead.missions;

import com.dchoc.idead.events.EventManager;
import com.dchoc.idead.events.TimedEvent;
import com.dchoc.parser.Json;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class MissionEvent {
   private int mDelay;
   private int[] mEventParamters;
   private int mEventType;
   private int mMissionID = -1;
   private int mMissionType = -1;

   public MissionEvent(DChocByteArray data, boolean byType) throws EOFException {
      int selector = data.readShort();
      if (byType) {
         this.mMissionType = selector;
      } else {
         this.mMissionID = selector;
      }

      this.mEventType = data.readShort();
      this.mEventParamters = ToolkitHelpers.readIntArray(data);
      this.mDelay = data.readInt();
   }

   public MissionEvent() {
   }

   /**
    * Build a mission event from one element of the JSON
    * {@code missionEventsById}/{@code missionEventsByType} arrays. The
    * {@code selectedBy} field chooses whether the selector is a mission type or
    * a mission ID.
    */
   public static MissionEvent fromJson(java.util.Map<String, Object> data) {
      MissionEvent event = new MissionEvent();
      int selector = Json.intValue(data, "selectorId", -1);
      if ("missionType".equals(Json.stringValue(data, "selectedBy"))) {
         event.mMissionType = selector;
      } else {
         event.mMissionID = selector;
      }

      event.mEventType = Json.intValue(data, "eventTypeId", -1);
      event.mEventParamters = Json.intArray(Json.get(data, "parameters"));
      event.mDelay = Json.intValue(data, "delayMilliseconds", 0);
      return event;
   }

   public int getMissionID() {
      return this.mMissionID;
   }

   public int getMissionType() {
      return this.mMissionType;
   }

   public void execute(Mission mission) {
      EventManager.addEvent(new TimedEvent(this.mDelay, this.mEventType, this.mEventParamters, mission));
   }

   protected void loadData(DChocByteArray data, boolean byType) throws EOFException {
      int selector = data.readShort();
      if (byType) {
         this.mMissionType = selector;
      } else {
         this.mMissionID = selector;
      }

      this.mEventType = data.readShort();
      this.mEventParamters = ToolkitHelpers.readIntArray(data);
      this.mDelay = data.readInt();
   }
}
