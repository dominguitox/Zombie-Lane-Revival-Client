package javax.microedition.sensor;

public interface Channel {
   void addCondition(ConditionListener var1, Condition var2);

   ChannelInfo getChannelInfo();

   String getChannelUrl();

   Condition[] getConditions(ConditionListener var1);

   void removeAllConditions();

   void removeCondition(ConditionListener var1, Condition var2);

   void removeConditionListener(ConditionListener var1);
}
