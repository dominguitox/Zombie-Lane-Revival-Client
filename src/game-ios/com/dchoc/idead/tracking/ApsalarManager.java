package com.dchoc.idead.tracking;

public class ApsalarManager {
   private static String smGroupValue;
   private static String smTypeValue;
   private static String smLabelValue;
   private static String smProductValue;
   private static String smParam1;
   private static String smParam2;

   public static void trackEvent(TrackingParameter[] parameters) {
      getApsalarHighLevelEvent(parameters, false);
      getApsalarEvent(parameters, false);
   }

   public static String getApsalarHighLevelEvent(TrackingParameter[] parameters, boolean detailed) {
      String[] names = Event.getParameterNames();
      smGroupValue = null;
      smTypeValue = null;
      String result = null;
      int count = parameters == null ? 0 : parameters.length;

      for (int i = 0; i < count; i++) {
         TrackingParameter parameter = parameters[i];
         if (parameter != null && parameter.getDataType() != -1 && parameter.getDataType() == 1) {
            String name = parameter.getName();
            String value = parameter.getStringValue();
            if (detailed && name.equals(names[7])) {
               smGroupValue = value;
            } else if (name.equals(names[8])) {
               smTypeValue = value;
            } else if (name.equals(names[9]) && indexOf(TrackingEvents.HIGH_LEVEL_TYPES_APSALAR, smTypeValue) >= 0) {
               String event = result;
               if (smGroupValue != null && detailed) {
                  event = cat(smGroupValue, ".");
               }

               if (smTypeValue != null) {
                  String type = shortName(TrackingEvents.TYPES, TrackingEvents.TYPES_SHORT, smTypeValue);
                  event = event == null ? cat(type, ".") : cat(event, cat(type, "."));
               }

               String action = shortName(TrackingEvents.ACTIONS, TrackingEvents.ACTIONS_SHORT, value);
               result = event == null ? action : cat(event, action);
            }
         }
      }

      return result;
   }

   public static String getApsalarEvent(TrackingParameter[] parameters, boolean detailed) {
      String[] names = Event.getParameterNames();
      smGroupValue = null;
      smLabelValue = null;
      smProductValue = null;
      smParam1 = null;
      smTypeValue = null;
      smParam2 = null;
      String value = null;
      int count = parameters == null ? 0 : parameters.length;

      for (int i = 0; i < count; i++) {
         TrackingParameter parameter = parameters[i];
         if (parameter != null && parameter.getDataType() != -1) {
            String name = parameter.getName();
            if (parameter.getDataType() == 1) {
               value = parameter.getStringValue();
            } else if (parameter.getDataType() == 0) {
               value = "" + parameter.getIntValue();
            }

            if (name.equals(names[7])) {
               smGroupValue = value;
            } else if (name.equals(names[8])) {
               smTypeValue = value;
            } else if (name.equals(names[9])) {
               smLabelValue = value;
            } else if (name.equals(names[17])) {
               smProductValue = value;
            }
         }
      }

      if (smTypeValue != null && smTypeValue.equals(TrackingEvents.TYPES[0])) {
         ABTestManager.enableEvent();
      }

      String result = null;
      if (smGroupValue != null && detailed) {
         result = cat(smGroupValue, ".");
      }

      if (smTypeValue != null) {
         String type = shortName(TrackingEvents.TYPES, TrackingEvents.TYPES_SHORT, smTypeValue);
         result = result == null ? type : cat(result, type);
      }

      if (smProductValue == null && smLabelValue == null) {
         return result;
      }

      if (smTypeValue == null) {
         return productThenLabel(result);
      }

      result = cat(result, ".");
      boolean productFirst;
      if (smGroupValue != null && smGroupValue.equals(TrackingEvents.GROUPS[2])) {
         productFirst = smTypeValue.equals(TrackingEvents.TYPES[13]) || smTypeValue.equals(TrackingEvents.TYPES[14]);
      } else {
         productFirst = !smTypeValue.equals(TrackingEvents.TYPES[37]);
      }

      productFirst = productFirst && !smTypeValue.equals(TrackingEvents.TYPES[24]);
      if (smTypeValue.equals(TrackingEvents.getEventType(5)) && smProductValue != null && smProductValue.equals(TrackingEvents.getEventType(6))) {
         smProductValue = shortName(TrackingEvents.TYPES, TrackingEvents.TYPES_SHORT, smProductValue);
      }

      return productFirst ? productThenLabel(result) : labelThenProduct(result);
   }

   private static String productThenLabel(String result) {
      if (smProductValue != null) {
         result = cat(result, smProductValue);
      }

      if (smLabelValue == null) {
         return result;
      }

      if (smProductValue != null) {
         result = cat(result, ".");
      }

      return cat(result, shortName(TrackingEvents.ACTIONS, TrackingEvents.ACTIONS_SHORT, smLabelValue));
   }

   private static String labelThenProduct(String result) {
      if (smLabelValue != null) {
         result = cat(result, shortName(TrackingEvents.ACTIONS, TrackingEvents.ACTIONS_SHORT, smLabelValue));
      }

      if (smProductValue == null) {
         return result;
      }

      if (smLabelValue != null) {
         result = cat(result, ".");
      }

      return cat(result, smProductValue);
   }

   private static String shortName(String[] longNames, String[] shortNames, String value) {
      int index = indexOf(longNames, value);
      return index < 0 ? null : shortNames[index];
   }

   private static int indexOf(String[] array, String value) {
      for (int i = 0; i < array.length; i++) {
         if (array[i].equals(value)) {
            return i;
         }
      }

      return -1;
   }

   private static String cat(String a, String b) {
      return (a == null ? "" : a) + (b == null ? "" : b);
   }
}
