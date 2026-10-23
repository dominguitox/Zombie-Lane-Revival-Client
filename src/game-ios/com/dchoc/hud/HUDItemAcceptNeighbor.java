package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;

public class HUDItemAcceptNeighbor extends HUDItemMailbox {
   private NeighborProfile mNeighbor = null;

   public HUDItemAcceptNeighbor() {
      super((byte)19);
   }

   public void setNeighbor(NeighborProfile neighbor) {
      if (neighbor != null) {
         this.mNeighbor = neighbor;
         this.setValues(neighbor.getName(), 1570, neighbor.getType() == 2514, neighbor.getSocialIconType());
         this.setButtons(true, false, false);
         this.mTextDescription.setText(2326, DCiDead.getFont(3));
      }
   }

   public NeighborProfile getNeighbor() {
      return this.mNeighbor;
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      for (int i = (this.mObjects == null ? 0 : this.mObjects.length) - 1; i >= 0; i--) {
         if (this.mObjects[i] != null) {
            this.mObjects[i].pointerEvent(event);
         }
      }
   }

   @Override
   public void callback(int id) {
      if (id == 0) {
         WindowManager.closeWindow();
         PlayerProfile.acceptNeighbor(this.mNeighbor);
         this.setAccepted(true);
         GameEngine.getInstance().travelToNeighbor(this.mNeighbor);
      }
   }
}
