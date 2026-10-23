package com.dchoc.idead.missions;

import com.dchoc.parser.Json;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;
import java.util.Vector;

public class MissionObjective extends Counter {
   public static final int ID_INVALID = -1;
   public static final int INITIAL_EVENTS_CAPACITY = 2;
   protected boolean mCompleted;
   protected boolean mConsumed;
   protected int mDescriptionTextID;
   protected int mID;
   protected int mIconAnimationID = -1;
   protected boolean mJustCompleted;
   protected int mMissionID;
   protected Vector mProgressEvents;
   protected boolean mPurchased;
   protected boolean mResetCounter;
   protected int mUnlockCurrency;
   protected int mUnlockPrice;
   private boolean mRelativePrice;
   private int[] mConnectedMissionSetupIDs;

   public MissionObjective(DChocByteArray data) throws EOFException {
      this.loadData(data);
   }

   public MissionObjective() {
      this.mIconAnimationID = -1;
   }

   /**
    * Build an objective from one element of a mission's JSON {@code objectives}
    * array.
    */
   public static MissionObjective fromJson(java.util.Map<String, Object> data) {
      MissionObjective objective = new MissionObjective();
      objective.mID = Json.intValue(data, "id", -1);
      objective.mMissionID = Json.intValue(data, "missionId", -1);
      objective.mDescriptionTextID = MissionJsonText.resolve(Json.object(data, "description"));
      java.util.Map<String, Object> condition = Json.object(data, "condition");
      objective.mType = Json.intValue(condition, "counterTypeId", -1);
      objective.mSubType = Json.intValue(condition, "subtypeId", -1);
      objective.mValue = Json.intValue(condition, "valueId", -1);
      objective.mGoal = Json.intValue(condition, "target", 0);
      objective.mResetCounter = Json.booleanValue(data, "resetCounter", false);
      objective.mConsumed = Json.booleanValue(data, "consumedOnCompletion", false);
      java.util.Map<String, Object> unlock = Json.object(data, "unlock");
      objective.mUnlockPrice = Json.intValue(unlock, "price", -1);
      objective.mUnlockCurrency = Json.intValue(unlock, "currencyId", -1);
      objective.mRelativePrice = Json.booleanValue(unlock, "relativePrice", false);
      objective.mIconAnimationID = Json.intValue(data, "iconAnimationId", -1);
      objective.mConnectedMissionSetupIDs = Json.intArray(Json.get(data, "connectedMissionSetupIds"));
      return objective;
   }

   public void addProgressEvent(ObjectiveEvent event) {
      if (this.mProgressEvents == null) {
         this.mProgressEvents = new Vector(2);
      }

      this.mProgressEvents.addElement(event);
   }

   @Override
   public boolean updateCounter(int type, int subType, int value, int amount) {
      boolean result = super.updateCounter(type, subType, value, amount);
      if (this.isValidUpdate(type, subType, value)) {
         this.updateProgress(amount);
      }

      return result;
   }

   public boolean isCompleted() {
      if (!this.mPurchased && !this.isGoalReached()) {
         this.mCompleted = false;
         return false;
      }

      if (!this.mCompleted) {
         this.mJustCompleted = true;
      }

      this.mCompleted = true;
      return true;
   }

   public int getID() {
      return this.mID;
   }

   public int getMissionID() {
      return this.mMissionID;
   }

   public int getUnlockPrice() {
      return this.mUnlockPrice > 0 && this.mRelativePrice ? Math.max(1, this.mUnlockPrice - this.mCounter * this.mUnlockPrice / this.mGoal) : this.mUnlockPrice;
   }

   public int getUnlockCurrency() {
      return this.mUnlockCurrency;
   }

   public int getDescriptionTextID() {
      return this.mDescriptionTextID;
   }

   public int getIconID() {
      return this.mIconAnimationID;
   }

   public boolean isConsumed() {
      return this.mConsumed;
   }

   public boolean isResetCounter() {
      return this.mResetCounter;
   }

   public void setPurchased(boolean purchased) {
      this.mPurchased = purchased;
      if (purchased) {
         int previous = this.mCounter;
         this.mCounter = this.mGoal;
         this.updateProgress(this.mGoal - previous);
      }
   }

   public void completeObjective() {
      int previous = this.mCounter;
      this.mCounter = this.mGoal;
      this.updateProgress(this.mGoal - previous);
   }

   public boolean isPurchased() {
      return this.mPurchased;
   }

   public boolean isRelativePrice() {
      return this.mRelativePrice;
   }

   public void resetJustComplted() {
      this.mJustCompleted = false;
   }

   public boolean isJustCompleted() {
      return this.mJustCompleted;
   }

   public int[] getConnectedMissionSetupIDs() {
      return this.mConnectedMissionSetupIDs;
   }

   @Override
   public void save(DChocByteArray data) {
      super.save(data);
      if (this.mUnlockPrice > 0) {
         data.writeBoolean(this.mPurchased);
      }
   }

   @Override
   public void load(DChocByteArray data) throws EOFException {
      super.load(data);
      if (this.mUnlockPrice > 0) {
         this.mPurchased = data.readBoolean();
      }
   }

   protected void loadData(DChocByteArray data) throws EOFException {
      this.mID = data.readShort();
      this.mMissionID = data.readShort();
      this.mDescriptionTextID = data.readShort();
      this.mType = data.readShort();
      this.mSubType = data.readShort();
      this.mValue = data.readShort();
      this.mGoal = data.readInt();
      this.mResetCounter = data.readBoolean();
      this.mConsumed = data.readBoolean();
      data.readBoolean();
      this.mUnlockPrice = data.readInt();
      this.mUnlockCurrency = data.readShort();
      this.mRelativePrice = data.readBoolean();
      this.mIconAnimationID = data.readInt();
      this.mConnectedMissionSetupIDs = ToolkitHelpers.readIntArray(data);
   }

   public void updateProgress(int amount) {
      if (this.mProgressEvents != null && amount != 0) {
         for (int i = 0; i < this.mProgressEvents.size(); i++) {
            ObjectiveEvent event = (ObjectiveEvent)this.mProgressEvents.elementAt(i);
            if (event.getTarget() <= this.mCounter && this.mCounter - amount < event.getTarget()) {
               event.execute();
            }
         }
      }
   }
}
