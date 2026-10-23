package com.dchoc.idead.missions;

import com.dchoc.idead.events.EventManager;
import com.dchoc.idead.events.TimedEvent;
import com.dchoc.parser.Json;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class ObjectiveEvent {
   private int mDelay;
   private int[] mEventParamters;
   private int mEventType;
   private int mMissionID = -1;
   private int mTarget;
   private int mType;
   private int mValue;

   public ObjectiveEvent(DChocByteArray data) throws EOFException {
      this.mMissionID = data.readShort();
      this.mType = data.readShort();
      this.mValue = data.readShort();
      this.mTarget = data.readInt();
      this.mEventType = data.readShort();
      this.mEventParamters = ToolkitHelpers.readIntArray(data);
      this.mDelay = data.readInt();
   }

   public ObjectiveEvent() {
   }

   /** Build a progress event from one element of the JSON {@code objectiveEvents} array. */
   public static ObjectiveEvent fromJson(java.util.Map<String, Object> data) {
      ObjectiveEvent event = new ObjectiveEvent();
      event.mMissionID = Json.intValue(data, "missionId", -1);
      event.mType = Json.intValue(data, "counterTypeId", -1);
      event.mValue = Json.intValue(data, "valueId", -1);
      event.mTarget = Json.intValue(data, "target", 0);
      event.mEventType = Json.intValue(data, "eventTypeId", -1);
      event.mEventParamters = Json.intArray(Json.get(data, "parameters"));
      event.mDelay = Json.intValue(data, "delayMilliseconds", 0);
      return event;
   }

   public int getMissionID() {
      return this.mMissionID;
   }

   public int getType() {
      return this.mType;
   }

   public int getValue() {
      return this.mValue;
   }

   public int getTarget() {
      return this.mTarget;
   }

   public void execute() {
      EventManager.addEvent(new TimedEvent(this.mDelay, this.mEventType, this.mEventParamters));
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mMissionID = data.readShort();
      this.mType = data.readShort();
      this.mValue = data.readShort();
      this.mTarget = data.readInt();
      this.mEventType = data.readShort();
      this.mEventParamters = ToolkitHelpers.readIntArray(data);
      this.mDelay = data.readInt();
   }
}
