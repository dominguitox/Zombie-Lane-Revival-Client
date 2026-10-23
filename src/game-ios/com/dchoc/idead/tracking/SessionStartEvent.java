package com.dchoc.idead.tracking;

public class SessionStartEvent extends Event {
   public SessionStartEvent() {
      super(1, 1, 1);
      this.setParameter(19, 0);
      this.setParameter(20, 0);
      this.setParameter(21, 0);
      this.setParameter(22, 0);
   }
}
