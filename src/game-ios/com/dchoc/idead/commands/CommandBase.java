package com.dchoc.idead.commands;

import com.dchoc.idead.characters.ActionCharacter;

public class CommandBase {
   protected int mType;
   protected long mId;

   protected CommandBase() {
   }

   public CommandBase(int type, long id) {
      this.mType = type;
      this.mId = id;
   }

   public boolean execute(ActionCharacter character) {
      return false;
   }

   public boolean isAccepted() {
      return false;
   }

   public boolean isExecuted() {
      return false;
   }

   public int getType() {
      return this.mType;
   }

   public long getID() {
      return this.mId;
   }
}
