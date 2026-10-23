package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.tracking.ButtonPressedEvent;

public class HUDAcceptHelp extends HUDAccept {
   public static String ACCEPT_NAME = "Accept Help";
   public static String CANCEL_NAME = "Cancel Help";

   @Override
   public void init() {
      this.mBackground = new HUDImage(74707, 0, 0);
      this.mTitle = new HUDAutoTextField(this.mBackground.getCollisionBox(2));
      HUDAutoTextField description = new HUDAutoTextField(this.mBackground.getCollisionBox(3));
      this.mDescription = description;
      description.setCentered(true, true);
      this.mDescription.setScaleToFit(true);
      this.mDescription.searchScale(true);
      this.mDescription.setSplitUsingWidth(true);
      HUDAutoTextField text = this.mDescription;
      text.setText(96, DCiDead.getFont(4));
      this.mTrackAccept = new ButtonPressedEvent(ACCEPT_NAME);
      this.mTrackCancel = new ButtonPressedEvent(CANCEL_NAME);
      super.init();
   }

   @Override
   public void callback(int id) {
      if (this.mNeighbor != null) {
         if (id == 1) {
            this.mNeighbor.getProfile().acceptCommands(false);
            this.mTrackCancel.trackEvent();
         } else if (id == 0) {
            this.mNeighbor.getProfile().acceptCommands(true);
            this.mTrackAccept.trackEvent();
         }
      }
   }
}
