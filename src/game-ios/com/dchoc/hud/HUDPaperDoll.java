package com.dchoc.hud;

import com.dchoc.idead.GameEngine;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.idead.player.InventoryItem;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.WindowNpcDialog;

public class HUDPaperDoll extends HUDCollection {
   public static String SWITCH_BUTTON_NAME = "Switch_weapon";
   private SpriteObject mLayout;
   private HUDImage mBackground;
   private HUDButton mButtonSwitch;
   private HUDInteger mAmmo;
   private HUDImage mInfiniteAmmo;
   private HUDImage mAmmoIcon;
   private int mAmmoX;
   private int mAmmoY;

   protected HUDPaperDoll() {
      int height = Toolkit.getScreenHeight();
      this.mLayout = AnimationsManager.loadShared(65597);
      this.mBackground = new HUDImage(74765, 0, height);
      this.addObject(this.mBackground);
      CollisionBox box = this.mLayout.getCollisionBox(99);
      int x = box.getX();
      this.mButtonSwitch = new HUDButton(0, x, box.getY() + height, 73949, (byte)21);
      this.addObject(this.mButtonSwitch);
      this.mButtonSwitch.setTrackingEvent(SWITCH_BUTTON_NAME);
      HUDInteger ammo = new HUDInteger(65563, 65559);
      this.mAmmo = ammo;
      ammo.setAnimated(true);
      this.mAmmo.setVisible(false);
      this.mAmmo.setCenterType((byte)0);
      this.addObject(this.mAmmo);
      this.mAmmoIcon = new HUDImage(65599);
      this.addObject(this.mAmmoIcon);
      HUDImage infinite = new HUDImage(75355);
      this.mInfiniteAmmo = infinite;
      infinite.setVisible(false);
      this.addObject(this.mInfiniteAmmo);
      int boxX = this.mLayout.getCollisionBox(99).getX();
      int switchWidth = this.mButtonSwitch.getWidth();
      int iconWidth = this.mAmmoIcon.getWidth();
      this.mAmmoX = boxX + switchWidth / 2 - iconWidth / 2;
      this.mAmmoY = height - 3 - this.mAmmo.getHeight();
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      InventoryItem weapon = PlayerProfile.getCurrentWeapon();
      if (weapon != null) {
         WeaponItem item = (WeaponItem)weapon.getItem();
         if (!weapon.isUsingAmmo() && item.getRange() == 1) {
            this.mAmmo.setVisible(false);
            this.mInfiniteAmmo.setVisible(false);
            this.mAmmoIcon.setVisible(false);
         } else {
            boolean usingAmmo = weapon.isUsingAmmo();
            this.mAmmo.setVisible(usingAmmo);
            this.mInfiniteAmmo.setVisible(!usingAmmo);
            this.mAmmoIcon.setVisible(true);
         }
      }

      if (this.mAmmo.isVisible()) {
         HUDInteger ammo = this.mAmmo;
         ammo.setValue(PlayerProfile.getCurrentAmmo());
      }

      int gender = PlayerProfile.getActiveAvatar().getGender();
      if (gender == 43) {
         this.mButtonSwitch.setVisible(false);
      } else {
         if (Integer.compareUnsigned(gender - 1921, 2) < 0) {
            this.mButtonSwitch.setVisible(true);
         }
      }
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         this.drawPaperDoll();
         if (this.mAmmo.isVisible()) {
            HUDInteger ammo = this.mAmmo;
            int x = this.mAmmoX;
            ammo.setPosition(x - ammo.getWidth(), this.mAmmoY);
         }

         if (this.mAmmoIcon.isVisible()) {
            HUDImage icon = this.mAmmoIcon;
            int x = this.mAmmoX;
            int width = this.mAmmo.getWidth();
            int digit = this.mAmmo.getDigitWidth(7);
            int y = this.mAmmoY;
            icon.setPosition(x - width - digit, y + this.mAmmo.getHeight() / 2);
         }

         if (this.mInfiniteAmmo.isVisible()) {
            HUDImage infinite = this.mInfiniteAmmo;
            int x = this.mAmmoIcon.getFixedX();
            int width = this.mAmmoIcon.getWidth();
            int digit = this.mAmmo.getDigitWidth(7);
            infinite.setPosition(x + width / 2 + digit, this.mAmmoIcon.getFixedY());
         }

         this.mBackground.setVisible(false);
         super.draw();
         this.mBackground.setVisible(true);
      }
   }

   @Override
   public void callback(int id) {
      if (id == 0) {
         PlayerProfile.switchWeapon();
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      super.pointerEvent(event);
      if (!event.isConsumed()) {
         this.pointerEventProfile(event);
      }
   }

   public void pointerEventProfile(TouchEvent event) {
      int height = Toolkit.getScreenHeight();
      AvatarDescription avatar = PlayerProfile.getActiveAvatar();
      CollisionBox box;
      boolean person;
      if (avatar.getGender() == 43) {
         box = this.mLayout.getCollisionBox(77);
         person = false;
      } else {
         box = this.mLayout.getCollisionBox(avatar.getGender() == 1922 ? 88 : 6);
         person = true;
      }

      PaperDoll.setAvatar(avatar);
      int touchX = event.getX();
      int myX = this.getX();
      int boxX = box.getX();
      int touchY = event.getY();
      int myY = this.getY();
      int x = touchX - myX - boxX;
      int y = touchY - height - myY - box.getY();
      if (IsometricUtils.isInsidePolygon(x, y, PaperDoll.getCollisionPolygon(person))) {
         if (event.getType() != 0 && event.getType() != 2) {
            if (event.getType() == 3) {
               IsometricScene scene = GameEngine.getInstance().getScene();
               scene.centerToObject(scene.getPlayer());
               event.setConsumed(true);
               if (WindowManager.isNPCDialogOpen(9)) {
                  ((WindowNpcDialog)WindowManager.getWindow((byte)19)).closeTooltip();
                  WindowManager.closeWindow();
               }
            }
         } else {
            event.setConsumed(true);
         }
      }
   }

   public void drawPaperDoll() {
      int height = Toolkit.getScreenHeight();
      this.mBackground.draw();
      AvatarDescription avatar = PlayerProfile.getActiveAvatar();
      int gender = avatar.getGender();
      CollisionBox box;
      boolean mirrored;
      if (gender == 43) {
         box = this.mLayout.getCollisionBox(77);
         mirrored = false;
      } else if (gender == 1922) {
         box = this.mLayout.getCollisionBox(88);
         mirrored = true;
      } else if (gender == 1921) {
         box = this.mLayout.getCollisionBox(6);
         mirrored = true;
      } else {
         box = null;
         mirrored = true;
      }

      int x = this.getX();
      int boxX = box.getX();
      int y = this.getY();
      PaperDoll.drawPaperdoll(boxX + x, y + height + box.getY(), avatar, mirrored);
   }
}
