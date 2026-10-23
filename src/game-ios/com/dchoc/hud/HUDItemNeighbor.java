package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.neighbors.NeighborGift;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.windows.WindowSocial;
import java.util.Vector;

public class HUDItemNeighbor extends HUDItem {
   private NeighborProfile mNeighbor;
   private SlotBackground mSlotBackground;
   private HUDAutoTextField mTextTitle;
   private HUDInteger mIntegerLevel;
   private HUDImage mImageStar;
   private HUDImage mImagePortrait;
   private HUDButton mButtonVisit;
   public HUDButton mButtonHire;
   private HUDTime mTime;
   private SpriteObject mCollisions = AnimationsManager.loadShared(74781);
   private HUDGift[] mGifts;

   public HUDItemNeighbor() {
      CollisionBox size = this.mCollisions.getCollisionBox(12);
      this.setSize(size.getWidth(), size.getHeight());
      this.mSlotBackground = new SlotBackground(this.mCollisions.getCollisionBox(0));
      CollisionBox box = this.mCollisions.getCollisionBox(13);
      this.mImagePortrait = new HUDImage(-1, box.getX(), box.getY());
      HUDAutoTextField title = new HUDAutoTextField(this.mCollisions.getCollisionBox(4));
      this.mTextTitle = title;
      title.setCentered(true, true);
      box = this.mCollisions.getCollisionBox(1);
      HUDButton visit = new HUDButton(0, box.getX(), box.getY(), 66245);
      this.mButtonVisit = visit;
      visit.setText(1570);
      box = this.mCollisions.getCollisionBox(2);
      HUDButton hire = new HUDButton(1, box.getX(), box.getY(), 66245);
      this.mButtonHire = hire;
      hire.setText(509);
      int x = box.getX();
      int width = box.getWidth();
      int y = box.getY();
      int height = box.getHeight();
      HUDTime time = new HUDTime(x + width / 2, y - height / 2, 2);
      this.mTime = time;
      time.setCenterType((byte)1);
      this.mTime.setVisible(false);
      box = this.mCollisions.getCollisionBox(3);
      this.mImageStar = new HUDImage(74863, box.getX(), box.getY());
      box = this.mCollisions.getCollisionBox(3);
      this.mIntegerLevel = new HUDInteger(66959, box.getX(), box.getY());
      this.mGifts = new HUDGift[4];

      for (int i = 0; i < 4; i++) {
         box = this.mCollisions.getCollisionBox(i + 5);
         this.mGifts[i] = new HUDGift(box.getX(), box.getY());
      }

      this.mObjects = new HUDObject[]{
         this.mSlotBackground,
         this.mImagePortrait,
         this.mTextTitle,
         this.mButtonVisit,
         this.mButtonHire,
         this.mImageStar,
         this.mIntegerLevel,
         this.mGifts[0],
         this.mGifts[1],
         this.mGifts[2],
         this.mGifts[3],
         this.mTime
      };
      this.initObjects();
   }

   public void setNeighbor(NeighborProfile neighbor) {
      if (neighbor != null) {
         this.mNeighbor = neighbor;
         this.mTextTitle.setText(neighbor.getName(), DCiDead.getFont(2));
         this.mIntegerLevel.setValue(this.mNeighbor.getLevel());
         Vector gifts = this.mNeighbor.getGifts();

         for (int i = 0; i < gifts.size() && i < 4; i++) {
            Object gift = gifts.elementAt(i);
            this.mGifts[i].setGift((NeighborGift)gift);
         }

         if (this.mNeighbor.getType() == 2514) {
            this.mImagePortrait.setVisible(true);
            this.mImagePortrait.setImage(66373);
         } else {
            this.mImagePortrait.setVisible(false);
         }

         int type = this.mNeighbor.getType();

         for (int i = 0; i < (this.mGifts == null ? 0 : this.mGifts.length); i++) {
            this.mGifts[i].setVisible(type != 2514);
         }
      }
   }

   public NeighborProfile getNeighbor() {
      return this.mNeighbor;
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.isVisible()) {
         super.logicUpdate(delta);
         if (this.mNeighbor.isUpdated()) {
            this.setNeighbor(this.mNeighbor);
            this.mNeighbor.resetUpdated();
         }

         this.mButtonVisit.changeStyle(this.mNeighbor.getActionsLeft() < 1 ? 6 : 3);
         this.updateHire(delta);
      }
   }

   @Override
   public void draw() {
      super.draw();
      if (this.mNeighbor != null && this.mNeighbor.getType() != 2514) {
         int x = this.getX();
         int y = this.getY();
         Vector avatars = this.mNeighbor.getAvatars();
         AvatarDescription first = this.mNeighbor.getAvatar(15);
         AvatarDescription second = this.mNeighbor.getAvatar(41);
         AvatarDescription third = this.mNeighbor.getAvatar(43);
         CollisionBox box9 = this.mCollisions.getCollisionBox(9);
         CollisionBox box10 = this.mCollisions.getCollisionBox(10);
         CollisionBox box11 = this.mCollisions.getCollisionBox(11);
         int count = avatars.size();
         if (count == 1) {
            int boxX = box9.getX();
            int boxY = box9.getY() + y;
            PaperDoll.drawPaperdollByLegs(x + boxX, boxY + box9.getHeight(), first, true);
         } else if (count == 2) {
            int right = box11.getX();
            int left = box9.getX();
            int rightAgain = box11.getX();
            int boxY = box9.getY() + y;
            int drawX = right + x + (left - rightAgain) / 2;
            PaperDoll.drawPaperdollByLegs(drawX, boxY + box9.getHeight(), first, true);
            int base = box9.getX();
            int middle = box10.getX();
            int baseAgain = box9.getX();
            boxY = box10.getY() + y;
            drawX = base + x + (middle - baseAgain) / 2;
            PaperDoll.drawPaperdollByLegs(drawX, boxY + box10.getHeight(), second, true);
         } else if (count == 3) {
            int boxX = box9.getX();
            int boxY = box9.getY() + y;
            PaperDoll.drawPaperdollByLegs(boxX + x, boxY + box9.getHeight(), first, true);
            boxX = box10.getX();
            boxY = box10.getY() + y;
            PaperDoll.drawPaperdollByLegs(boxX + x, boxY + box10.getHeight(), second, true);
            boxX = box11.getX();
            boxY = box11.getY() + y;
            PaperDoll.drawPaperdollByLegs(boxX + x, boxY + box11.getHeight(), third, false);
         }
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible()) {
         for (int i = (this.mObjects == null ? 0 : this.mObjects.length) - 1; i >= 0; i--) {
            if (this.mObjects[i] != null) {
               this.mObjects[i].pointerEvent(event);
            }
         }
      }
   }

   @Override
   public void callback(int id) {
      WindowSocial social = (WindowSocial)WindowManager.getWindow((byte)18);
      if (id == 1) {
         social.setSuppressPlayHaven();
         WindowManager.closeWindow();
         GameEngine.getInstance().setNeighbor(this.mNeighbor);
         HUD.changeState(8);
      } else {
         if (id == 0) {
            social.setSuppressPlayHaven();
            WindowManager.closeWindow();
            Vector items = social.getMailbox().getItems();

            for (int i = 0; i < items.size(); i++) {
               HUDObject item = (HUDObject)items.elementAt(i);
               if (item.getType() == 19) {
                  HUDItemAcceptNeighbor accept = (HUDItemAcceptNeighbor)item;
                  if (accept.getNeighbor().getUserID().equals(this.mNeighbor.getUserID())) {
                     PlayerProfile.acceptNeighbor(this.mNeighbor);
                     accept.setAccepted(true);
                  }
               }
            }

            GameEngine.getInstance().travelToNeighbor(this.mNeighbor);
         }
      }
   }

   @Override
   public int getParameterForSort() {
      int level = 0;
      if (this.mNeighbor != null) {
         level = -this.mNeighbor.getLevel();
      }

      return level;
   }

   public void updateHire(int delta) {
      if (this.mNeighbor.isHired()) {
         long left = PlayerProfile.getHireCountdownLeft();
         if (this.mNeighbor.getType() == 2514) {
            left = GameEngineUtils.getRob().getHireCountdownLeft();
         }

         if (left < 0L) {
            this.mNeighbor.setHired(false);
            this.mTime.setVisible(false);
         } else if (left == 0L) {
            this.mTime.setVisible(false);
         } else {
            this.mTime.setTime(left);
            this.mTime.setVisible(true);
         }
      }

      int state = HUD.getState();
      HUDButton hire = this.mButtonHire;
      if (state == 2) {
         hire.setEnabled(false);
      } else {
         hire.setEnabled(this.canHire());
      }
   }

   public boolean canHire() {
      if (GameEngineUtils.isAtNeigborsPlace()) {
         return false;
      } else if (this.mNeighbor.isHired()) {
         return false;
      } else {
         return PlayerProfile.getHiresLeft() > 0 ? true : this.mNeighbor.getType() == 2514;
      }
   }
}
