package com.dchoc.hud;

import com.dchoc.idead.tracking.ButtonPressedEvent;

public class HUDAcceptHire extends HUDAccept {
   public static String ACCEPT_NAME = "Accept Hire";
   public static String CANCEL_NAME = "Cancel Hire";

   @Override
   public void init() {
      this.mBackground = new HUDImage(74707, 0, 0);
      this.mTitle = new HUDAutoTextField(this.mBackground.getCollisionBox(7));
      HUDAutoTextField description = new HUDAutoTextField(this.mBackground.getCollisionBox(3));
      this.mDescription = description;
      description.setCentered(true, true);
      this.mDescription.setScaleToFit(true);
      this.mDescription.searchScale(true);
      this.mDescription.setSplitUsingWidth(true);
      this.mTrackAccept = new ButtonPressedEvent(ACCEPT_NAME);
      this.mTrackCancel = new ButtonPressedEvent(CANCEL_NAME);
      super.init();
   }

   public HUDAutoTextField getDescriptionText() {
      return this.mDescription;
   }

   @Override
   public void callback(int id) {
      if (this.mNeighbor != null) {
         if (id == 1) {
            HUDEdit edit = HUD.getInGame().getEdit();
            edit.showAcceptHire(false);
            edit.cancelCurrentState();
            this.mTrackCancel.trackEvent();
         } else if (id == 0) {
            HUD.getInGame().getEdit().callback(4);
            this.mTrackAccept.trackEvent();
         }
      }
   }
}
