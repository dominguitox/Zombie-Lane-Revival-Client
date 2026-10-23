package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;

public class HUDItemMailbox extends HUDItem {
   protected SlotBackground mSlotBackground;
   protected HUDAutoTextField mTextTitle;
   protected HUDAutoTextField mTextDescription;
   protected HUDButton mButtonAccept;
   protected HUDButton mButtonHire;
   protected HUDButton mButtonKeep;
   protected HUDImage mImageIcon;
   protected HUDImage mImagePortrait;
   protected HUDImage mImageSocial;
   protected SpriteObject mCollisions;
   protected boolean mAccepted;

   public HUDItemMailbox(byte type) {
      this.setType(type);
      this.mCollisions = AnimationsManager.loadShared(74779);
      CollisionBox size = this.mCollisions.getCollisionBox(6);
      this.setSize(size.getWidth(), size.getHeight());
      this.mSlotBackground = new SlotBackground(this.mCollisions.getCollisionBox(0));
      HUDAutoTextField title = new HUDAutoTextField(this.mCollisions.getCollisionBox(4));
      this.mTextTitle = title;
      title.setCentered(false, true);
      HUDAutoTextField description = new HUDAutoTextField(this.mCollisions.getCollisionBox(5));
      this.mTextDescription = description;
      description.setCentered(false, false);
      this.mTextDescription.setSplitUsingWidth(true);
      CollisionBox box = this.mCollisions.getCollisionBox(2);
      this.mImageIcon = new HUDImage(-1, box.getX(), box.getY());
      box = this.mCollisions.getCollisionBox(3);
      this.mImagePortrait = new HUDImage(-1, box.getX(), box.getY());
      box = this.mCollisions.getCollisionBox(10);
      this.mImageSocial = new HUDImage(-1, box.getX(), box.getY());
      box = this.mCollisions.getCollisionBox(1);
      this.mButtonAccept = new HUDButton(0, box.getX(), box.getY(), 66245);
      box = this.mCollisions.getCollisionBox(8);
      HUDButton hire = new HUDButton(1, box.getX(), box.getY(), 66245);
      this.mButtonHire = hire;
      hire.setText(1675);
      box = this.mCollisions.getCollisionBox(9);
      HUDButton keep = new HUDButton(2, box.getX(), box.getY(), 74731);
      this.mButtonKeep = keep;
      keep.setText(1673);
      this.mObjects = new HUDObject[]{
         this.mSlotBackground,
         this.mImagePortrait,
         this.mImageSocial,
         this.mImageIcon,
         this.mTextTitle,
         this.mTextDescription,
         this.mButtonAccept,
         this.mButtonHire,
         this.mButtonKeep
      };
      this.initObjects();
      this.mAccepted = false;
   }

   public void setButtons(boolean accept, boolean hire, boolean keep) {
      this.mButtonAccept.setVisible(accept);
      this.mButtonHire.setVisible(hire);
      this.mButtonKeep.setVisible(keep);
   }

   public void setAccepted(boolean accepted) {
      this.mAccepted = accepted;
   }

   public boolean isAccepted() {
      return this.mAccepted;
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
   }

   public void setValues(String title, int button, boolean special, int social) {
      this.setValues(-1, button, special, social);
      this.mTextTitle.setText(title, DCiDead.getFont(2));
   }

   public void setValues(int title, int button, boolean special, int social) {
      this.mButtonAccept.setText(button);
      this.mTextTitle.setText(title, DCiDead.getFont(2));
      if (special) {
         this.mImagePortrait.setImage(74677);
         this.mImageSocial.setVisible(false);
      } else {
         this.mImagePortrait.setImage(78491);
         this.mImageSocial.setVisible(true);
         switch (social) {
            case 1:
               this.mImageSocial.setImage(74679);
               break;
            case 2:
               this.mImageSocial.setImage(77783);
               break;
            case 3:
               this.mImageSocial.setImage(77777);
               break;
            case 4:
               this.mImageSocial.setImage(80199);
         }
      }
   }
}
