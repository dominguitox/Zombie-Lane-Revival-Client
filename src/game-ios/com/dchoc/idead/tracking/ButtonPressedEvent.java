package com.dchoc.idead.tracking;

public class ButtonPressedEvent extends Event {
   public ButtonPressedEvent(String name) {
      super(0, 20, 18);
      this.setParameter(17, name);
   }

   public void setValue(String value) {
      this.setParameter(18, value);
   }
}
