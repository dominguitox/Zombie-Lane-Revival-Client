package com.dchoc.idead.items;

import com.dchoc.toolkit.DChocByteArray;
import java.io.EOFException;

public class ZombieItem extends DestructibleItem {
   public ZombieItem(DChocByteArray data) throws EOFException {
      super(data);
   }
}
