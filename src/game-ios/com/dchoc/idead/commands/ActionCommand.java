package com.dchoc.idead.commands;

import com.dchoc.idead.actions.Action;
import com.dchoc.idead.characters.ActionCharacter;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.objects.InteractiveObject;

public class ActionCommand extends CommandBase {
   private int mItemId;
   private int mUpdatedHealth;
   private IsometricObject mTarget;
   private boolean mAccepted;
   private boolean mExecuted;

   public ActionCommand(int itemId, int updatedHealth, long id) {
      super(0, id);
      this.mItemId = itemId;
      this.mUpdatedHealth = updatedHealth;
      this.mAccepted = false;
      this.mExecuted = false;
      this.mTarget = null;
   }

   public IsometricObject updateTarget(ActionCharacter character) {
      if (this.mTarget != null && IsometricUtils.canInteractWith(character, this.mTarget)) {
         return this.mTarget;
      }

      Item item = ItemManager.getItem(this.mItemId);
      IsometricObject target = IsometricUtils.getInteractableObjectByItem(character, item);
      this.mTarget = target;
      return target;
   }

   @Override
   public boolean execute(ActionCharacter character) {
      this.mExecuted = true;
      if (character != null) {
         this.updateTarget(character);
         IsometricObject target = this.mTarget;
         if (target != null) {
            Action action = ((InteractiveObject)target).interactWith(character);
            if (action == null) {
               return false;
            }

            action.setHealth(this.mUpdatedHealth);
            return true;
         }
      }

      return false;
   }

   public void setAccepted(boolean accepted) {
      this.mAccepted = accepted;
   }

   @Override
   public boolean isAccepted() {
      return this.mAccepted;
   }

   @Override
   public boolean isExecuted() {
      return this.mExecuted;
   }

   public int getItemID() {
      return this.mItemId;
   }
}
