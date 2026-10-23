package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.GiftItem;
import com.dchoc.idead.neighbors.NeighborGift;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.tracking.ButtonPressedEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.WindowSocial;
import java.util.Vector;

public class HUDFriendsPlace extends HUDCollection {
   public static String HOME_BUTTON = "Return Home";
   public static String LEFT_BUTTON = "Visit Friend Left";
   public static String RIGHT_BUTTON = "Visit Friend Right";
   public static String GIFT_BUTTON = "Visit Friend Gift";
   public static String CANCEL_BUTTON = "Visit Friend Cancel";
   private NeighborProfile mNeighbor;
   private HUDButton mButtonRight;
   private HUDButton mButtonLeft;
   private HUDButton mButtonGift;
   private HUDButtonWithOrangeLabel mButtonHome;
   private HUDButton mButtonCancel;
   private HUDAutoTextField mName;
   private HUDImage mBackground;
   private ButtonPressedEvent[] mTrackingEvents;

   protected HUDFriendsPlace() {
      int width = Toolkit.getScreenWidth();
      int height = Toolkit.getScreenHeight();
      this.mBackground = new HUDImage(74597, width, height);
      this.addObject(this.mBackground);
      CollisionBox box = this.mBackground.getCollisionBox(1);
      this.mButtonRight = new HUDButton(1, box.getX() + width, box.getY() + height, 71303, (byte)-1);
      this.addObject(this.mButtonRight);
      box = this.mBackground.getCollisionBox(0);
      this.mButtonLeft = new HUDButton(0, box.getX() + width, box.getY() + height, 71307, (byte)-1);
      this.addObject(this.mButtonLeft);
      box = this.mBackground.getCollisionBox(2);
      this.mButtonGift = new HUDButton(2, box.getX() + width, box.getY() + height, 67465, (byte)-1);
      this.addObject(this.mButtonGift);
      box = this.mBackground.getCollisionBox(4);
      this.mButtonHome = new HUDButtonWithOrangeLabel(3, box.getX() + width, box.getY() + height, 71025, (byte)-1);
      this.addObject(this.mButtonHome);
      box = this.mBackground.getCollisionBox(5);
      this.mButtonCancel = new HUDButton(4, box.getX() + width, box.getY() + height, 71017, (byte)12);
      this.addObject(this.mButtonCancel);
      box = this.mBackground.getCollisionBox(3);
      int x = box.getX();
      int y = box.getY();
      HUDAutoTextField name = new HUDAutoTextField(width + x, height + y, box.getWidth(), box.getHeight());
      this.mName = name;
      name.setCentered(true, true);
      this.mName.setSplitUsingWidth(true);
      this.mName.setScaleToFit(true);
      this.addObject(this.mName);
      this.mNeighbor = null;
      ButtonPressedEvent[] events = new ButtonPressedEvent[]{
         new ButtonPressedEvent(LEFT_BUTTON),
         new ButtonPressedEvent(RIGHT_BUTTON),
         new ButtonPressedEvent(GIFT_BUTTON),
         new ButtonPressedEvent(HOME_BUTTON),
         new ButtonPressedEvent(CANCEL_BUTTON)
      };
      this.mTrackingEvents = events;
   }

   public void setNeighbor(NeighborProfile neighbor) {
      if (neighbor != null) {
         this.mNeighbor = neighbor;
         HUDAutoTextField name = this.mName;
         name.setText(neighbor.getName(), DCiDead.getFont(0));
         Vector neighbors = ((WindowSocial)WindowManager.getWindow((byte)18)).getNeighbors();
         Vector gifts = neighbor.getGifts();
         boolean canSend = false;
         if (0 < gifts.size()) {
            int i = 0;

            do {
               NeighborGift gift = (NeighborGift)gifts.elementAt(i);
               GiftItem item = gift.getGiftItem();
               if (!item.isLockedByLevel() && !item.isLockedByMission() && !gift.isSent()) {
                  canSend |= !gift.isSending();
               }

               i++;
            } while (i < gifts.size() && i < 4);
         }

         HUDButton giftButton = this.mButtonGift;
         giftButton.setVisible(canSend & neighbor.getType() != 2514);
         HUDButton right = this.mButtonRight;
         right.setEnabled(1 < neighbors.size());
         HUDButton left = this.mButtonLeft;
         left.setEnabled(1 < neighbors.size());
      }
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      int actions = ((WindowSocial)WindowManager.getWindow((byte)18)).getActionsLeft();
      this.mButtonHome.setOrangeLabelVisible(actions == 0);
   }

   @Override
   public void callback(int id) {
      switch (id) {
         case 0:
            this.visitNeighbor(-1);
            break;
         case 1:
            this.visitNeighbor(1);
            break;
         case 2:
            ((WindowSocial)WindowManager.getWindow((byte)18)).setSuppressPlayHaven();
            WindowManager.openWindow((byte)18);
            break;
         case 3:
            GameEngine.getInstance().travelToHome();
            break;
         case 4:
            PlayerCharacter player = IsometricUtils.getScene().getPlayer();
            if (player != null) {
               player.cancelActions(true);
            }
      }

      this.mTrackingEvents[id].trackEvent();
   }

   public void visitNeighbor(int step) {
      GameEngine engine = GameEngine.getInstance();
      NeighborProfile current = engine.getNeighbor();
      Vector neighbors = ((WindowSocial)WindowManager.getWindow((byte)18)).getNeighbors();
      if (current != null && !neighbors.isEmpty()) {
         int index = neighbors.indexOf(current) + step;
         if (index < neighbors.size()) {
            if (index < 0) {
               index = neighbors.size() - 1;
            }
         } else {
            index = 0;
         }

         engine.travelToNeighbor((NeighborProfile)neighbors.elementAt(index));
      }
   }
}
