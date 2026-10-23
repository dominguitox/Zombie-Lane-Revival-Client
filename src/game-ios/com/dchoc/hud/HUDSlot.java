package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.AvatarItem;
import com.dchoc.idead.items.BoosterItem;
import com.dchoc.idead.items.CollectibleItem;
import com.dchoc.idead.items.DecorationItem;
import com.dchoc.idead.items.EdibleItem;
import com.dchoc.idead.items.FarmingItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.idead.player.Inventory;
import com.dchoc.idead.player.InventoryItem;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.idead.tracking.BuyCurrencyEvent;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.iphonewrappers.iWrapper;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Product;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.WindowMarket;
import com.dchoc.windows.WindowNewItem;
import com.dchoc.windows.WindowStuff;

public class HUDSlot extends HUDObject {
   private static final String EMPTY_STRING = "";
   private static final String LETTER_X = "x";
   private static SpriteObject smSlotForItem;
   private static SpriteObject smSimpleSlotForItem;
   private static SpriteObject smSlotForAvatarItem;
   private Product mInAppProduct;
   private BuyCurrencyEvent mInAppEvent;
   private Item mItem;
   private InventoryItem mInventoryItem;
   private int mSoundId;
   private boolean mDisableButton;
   private boolean mDisableDescription;
   private boolean mExternalButton;
   private boolean mUseSecondPrice;
   private HUDImage mItemIcon;
   private HUDImage mAdditionalIcon;
   private HUDImage mSaleIcon;
   private HUDImage mStrikeOutIcon;
   private HUDButton mButton;
   private HUDAutoTextField mTitle;
   private HUDAutoTextField mDescription;
   private HUDAutoTextField mEquippedLabel;
   private HUDAutoTextField mAmountLabel;
   private HUDAutoTextField mUnlockLabel;
   private HUDTime mTime;
   private SlotBackground mBackground;
   private SpriteObject mSlotCollisions;
   private HUDObject[] mObjects;

   public HUDSlot() {
      super((byte)12);
   }

   public HUDSlot(Object object, HUDObject parent, boolean secondPrice) {
      super((byte)12);
      this.init(object, parent, secondPrice, false);
   }

   public void init(Object object, HUDObject parent, boolean secondPrice) {
      this.init(object, parent, secondPrice, false);
   }

   public void init(Object object, HUDObject parent, boolean secondPrice, boolean simple) {
      if (object != null) {
         if (object instanceof Product product) {
            this.mInAppProduct = product;
            boolean cash = WindowMarket.isCashProduct(product.getDchocId());
            String thirdParty = this.mInAppProduct.getThirdPartyId();
            int id = this.mInAppProduct.getDchocId();
            int amount = WindowMarket.getProductAmount(id);
            int price = this.mInAppProduct.getPrice();
            this.mInAppEvent = new BuyCurrencyEvent(thirdParty, cash ? 0 : 1, amount, price);
         } else if (object instanceof Item item) {
            this.mItem = item;
         } else if (object instanceof InventoryItem inventoryItem) {
            this.mInventoryItem = inventoryItem;
         }
      }

      this.setParent(parent);
      this.mUseSecondPrice = secondPrice;
      this.init(simple);
   }

   public void refresh() {
      HUDButton button = this.mButton;
      button.setVisible(this.getButtonVisibility());
      button = this.mButton;
      button.setIcon(this.getIconForButton());
      button = this.mButton;
      button.setText(this.getTextForButton());
      button = this.mButton;
      button.setEnabled(this.getIsButtonEnabled());
      HUDTime time = this.mTime;
      time.setVisible(this.getTimeVisibility());
      CollisionBox box = this.mSlotCollisions.getCollisionBox(15);
      this.applyEquippedText(this.mEquippedLabel, 0);
      this.mEquippedLabel.resizeToFit();
      HUDAutoTextField label = this.mEquippedLabel;
      int width = this.mWidth;
      int labelWidth = label.getWidth();
      int y = box.getY();
      int height = box.getHeight();
      int labelHeight = this.mEquippedLabel.getHeight();
      label.setPosition((width - labelWidth) / 2, y + height - labelHeight * 3 / 2);
      this.applyAmountText(this.mAmountLabel, 2);
      this.mUnlockLabel.setVisible(true);
      if (this.getVisibleForCompleteQuestToUnlock()) {
         this.applyUnlockByMissionText(this.mUnlockLabel, 2);
      } else if (this.getVisibleForLevelUpToUnlock()) {
         this.applyUnlockByLevelText(this.mUnlockLabel, 2);
      } else {
         this.mUnlockLabel.setVisible(false);
      }

      this.mSoundId = this.getSoundId();
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         if (!this.mUnlockLabel.isVisible()) {
            for (int i = 0; i < (this.mObjects == null ? 0 : this.mObjects.length); i++) {
               HUDObject object = this.mObjects[i];
               if (object != null) {
                  object.draw();
               }
            }
         } else {
            OGL.pushParameters();
            OGL.setColorModification(864059520);
            ((SlotBackground)this.mObjects[0]).drawAlpha();

            for (int i = 1; i < (this.mObjects == null ? 0 : this.mObjects.length) - 2; i++) {
               HUDObject object = this.mObjects[i];
               if (object != null) {
                  object.draw();
               }
            }

            OGL.popParameters();
            this.mObjects[(this.mObjects == null ? 0 : this.mObjects.length) - 2].draw();
            this.mObjects[(this.mObjects == null ? 0 : this.mObjects.length) - 1].draw();
         }

         this.drawDebug();
      }
   }

   @Override
   public void callback(int id) {
      if (id == 39000) {
         this.doAction();
         if (this.mSoundId != -1) {
            iWrapper.playSoundFx(this.mSoundId);
         }

         if (this.mParent != null && this.mParent.getType() == 3) {
            HUDObject parent = this.mParent;
            if (parent != null) {
               HUDTouchArea area = ((HUDSetOfSlots)parent).getParentTouchArea();
               if (area != null) {
                  area.refresh();
               }
            }
         }
      }
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.isVisible()) {
         if (this.getTimeVisibility()) {
            HUDTime time = this.mTime;
            time.setTime(this.mInventoryItem.getUseTimeLeft());
         }

         for (int i = 0; i < (this.mObjects == null ? 0 : this.mObjects.length); i++) {
            HUDObject object = this.mObjects[i];
            if (object != null) {
               object.logicUpdate(delta);
            }
         }
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible() && !this.mDisableButton) {
         this.mButton.pointerEvent(event);
      }
   }

   public Item getItem() {
      if (this.mItem != null) {
         return this.mItem;
      } else {
         return this.mInventoryItem != null ? this.mInventoryItem.getItem() : null;
      }
   }

   public int getAmount() {
      if (this.mItem != null) {
         return 1;
      } else {
         return this.mInventoryItem != null ? this.mInventoryItem.getAmount() : 0;
      }
   }

   public boolean isLocked() {
      return this.mUnlockLabel.isVisible();
   }

   public boolean useSecondPrice() {
      return this.mUseSecondPrice;
   }

   public HUDButton getButton() {
      return this.mButton;
   }

   public void disableButton(boolean disable) {
      this.mDisableButton = disable;
   }

   public void disableDescription(boolean disable) {
      this.mDisableDescription = disable;
   }

   public void setExternalButton(boolean external) {
      this.mExternalButton = external;
   }

   public void setRemoveMissionAndLevelLocks() {
      this.mUnlockLabel.setVisible(false);
   }

   public void init(boolean simple) {
      SpriteObject collisions;
      if (this.mItem != null && this.mItem.getType() == 1911) {
         if (smSlotForAvatarItem == null) {
            smSlotForAvatarItem = AnimationsManager.loadShared(74829);
         }

         collisions = smSlotForAvatarItem;
      } else {
         if (smSimpleSlotForItem == null) {
            smSimpleSlotForItem = AnimationsManager.loadShared(80185);
         }

         if (smSlotForItem == null) {
            smSlotForItem = AnimationsManager.loadShared(74743);
         }

         collisions = smSlotForItem;
         if (simple) {
            collisions = smSimpleSlotForItem;
         }
      }

      this.mSlotCollisions = collisions;
      CollisionBox box = collisions.getCollisionBox(0);
      this.mWidth = box.getWidth();
      this.mHeight = box.getHeight();
      this.mBackground = new SlotBackground(this.mSlotCollisions.getCollisionBox(15), this);
      this.mItemIcon = new HUDImage(this.getIconID());
      this.mItemIcon.setAnimationFrame(0);
      if (this.mItem != null && this.mItem.getType() == 1911) {
         int offsetX = 0;
         int offsetY = 0;
         CollisionBox imageBox = this.mItemIcon.getCollisionBox(0);
         box = this.mSlotCollisions.getCollisionBox(12);
         this.mItemIcon.setScaleBox(box);
         if (imageBox != null) {
            float scale = this.mItemIcon.getScaleRatio();
            int x = imageBox.getX();
            int width = imageBox.getWidth();
            int y = imageBox.getY();
            int height = imageBox.getHeight();
            if (scale < 0.0F) {
               scale = 1.0F;
            }

            offsetX = (int)((x + width / 2) * scale);
            offsetY = (int)((y + height / 2) * scale);
         }

         HUDImage icon = this.mItemIcon;
         int x = box.getX();
         int width = box.getWidth();
         int y = box.getY();
         int height = box.getHeight();
         icon.setPosition(x - offsetX + width / 2, y - offsetY + height / 2);
         if (this.mItem.getID() == 20
            || this.mItem.getID() == 29
            || this.mItem.getSubType() == 2119
            || this.mItem.getSubType() == 2117
            || this.mItem.getSubType() == 2118
            || this.mItem.getSubType() == 2124
            || this.mItem.getSubType() == 2125) {
            int gender = ((AvatarItem)this.mItem).getGender();
            if (gender == 43) {
               this.mAdditionalIcon = new HUDImage(74807);
            } else if (gender == 1921) {
               this.mAdditionalIcon = new HUDImage(67205);
            } else if (gender == 1922) {
               this.mAdditionalIcon = new HUDImage(74805);
            }

            if (this.mAdditionalIcon != null) {
               if (offsetX == 0 && offsetY == 0) {
                  imageBox = this.mAdditionalIcon.getCollisionBox(0);
                  x = imageBox.getX();
                  width = imageBox.getWidth();
                  y = imageBox.getY();
                  height = imageBox.getHeight();
                  offsetX = x + width / 2;
                  offsetY = y + height / 2;
               }

               box = this.mSlotCollisions.getCollisionBox(12);
               icon = this.mAdditionalIcon;
               x = box.getX();
               width = box.getWidth();
               y = box.getY();
               height = box.getHeight();
               icon.setPosition(x - offsetX + width / 2, y - offsetY + height / 2);
            }
         }
      } else {
         box = this.mSlotCollisions.getCollisionBox(12);
         HUDImage icon = this.mItemIcon;
         int x = box.getX();
         int width = box.getWidth();
         int y = box.getY();
         int height = box.getHeight();
         icon.setPosition(x + width / 2, y + height / 2);
      }

      boolean newItem = false;
      boolean saleItem = false;
      if (this.mSlotCollisions == smSlotForItem) {
         Item item = this.mItem;
         if (item == null && this.mInventoryItem != null) {
            item = this.mInventoryItem.getItem();
         }

         if (item != null) {
            if (item.getType() == 405) {
               boolean isNew = ((DecorationItem)item).isNewItem();
               boolean isSale = ((DecorationItem)item).isSaleItem();
               if (isNew) {
                  newItem = true;
               } else if (isSale) {
                  saleItem = true;
               }
            } else if (item.getType() == 1583) {
               newItem = ((FarmingItem)item).isNewItem();
            } else if (item.getType() == 101) {
               newItem = ((EdibleItem)item).isNewItem();
            } else if (item.getType() == 1907) {
               newItem = ((WeaponItem)item).isNewItem();
            }

            if (newItem) {
               this.mSaleIcon = new HUDImage(80195);
               this.mSaleIcon.setAnimationFrame(0);
               box = this.mSlotCollisions.getCollisionBox(20);
               HUDImage icon = this.mSaleIcon;
               int x = box.getX();
               int width = box.getWidth();
               int y = box.getY();
               int height = box.getHeight();
               icon.setPosition(x + width / 2, y + height / 2);
            } else if (saleItem) {
               this.mStrikeOutIcon = new HUDImage(80193);
               this.mStrikeOutIcon.setAnimationFrame(0);
               box = this.mSlotCollisions.getCollisionBox(20);
               HUDImage icon = this.mStrikeOutIcon;
               int x = box.getX();
               int width = box.getWidth();
               int y = box.getY();
               int height = box.getHeight();
               icon.setPosition(x + width / 2, y + height / 2);
            }
         }
      }

      if (!this.mDisableButton) {
         this.mButton = new HUDButton(39000, null, 4);
         if (this.mItem != null && this.mItem.getType() == 1911) {
            HUDButton button = this.mButton;
            button.setPosition(this.mSlotCollisions.getCollisionBox(20));
         } else {
            box = this.mSlotCollisions.getCollisionBox(15);
            HUDButton button = this.mButton;
            int width = this.mWidth;
            int buttonWidth = button.getWidth();
            int y = box.getY();
            int height = box.getHeight();
            int buttonHeight = this.mButton.getHeight();
            button.setPosition((width - buttonWidth) / 2, height + y - buttonHeight / 2);
         }

         HUDButton button = this.mButton;
         button.setIcon(this.getIconForButton());
         button = this.mButton;
         button.setText(this.getTextForButton());
         this.mButton.setInheritedClipping(true, true);
         button = this.mButton;
         button.setVisible(this.getButtonVisibility());
         button = this.mButton;
         button.setEnabled(this.getIsButtonEnabled());
      }

      this.mTitle = new HUDAutoTextField(this.mSlotCollisions.getCollisionBox(10));
      this.mTitle.setScaleToFit(true);
      this.mTitle.setCentered(true, simple);
      this.mTitle.setSplitUsingWidth(true);
      this.applyTitleText(this.mTitle, 2);
      if (this.mSlotCollisions != smSimpleSlotForItem && !this.mDisableDescription) {
         this.mDescription = new HUDAutoTextField(this.mSlotCollisions.getCollisionBox(13));
         this.mDescription.setCentered(true, true);
         this.mDescription.setSplitUsingWidth(true);
         this.applyDescriptionText(this.mDescription, 3);
      }

      box = this.mSlotCollisions.getCollisionBox(15);
      this.mEquippedLabel = new HUDAutoTextField(0, 0, 0, 0);
      this.applyEquippedText(this.mEquippedLabel, 0);
      this.mEquippedLabel.resizeToFit();
      HUDAutoTextField label = this.mEquippedLabel;
      int width = this.mWidth;
      int labelWidth = label.getWidth();
      int y = box.getY();
      int height = box.getHeight();
      int labelHeight = this.mEquippedLabel.getHeight();
      label.setPosition((width - labelWidth) / 2, height + y - labelHeight * 3 / 2);
      box = this.mSlotCollisions.getCollisionBox(12);
      this.mAmountLabel = new HUDAutoTextField(0, 0, 0, 0);
      this.applyAmountText(this.mAmountLabel, 2);
      this.mAmountLabel.resizeToFit();
      label = this.mAmountLabel;
      int x = box.getX();
      width = box.getWidth();
      labelWidth = this.mAmountLabel.getWidth();
      y = box.getY();
      height = box.getHeight();
      labelHeight = this.mAmountLabel.getHeight();
      label.setPosition(x + width - labelWidth * 3 / 2, y + height - labelHeight / 2);
      if (this.mItem != null && this.mItem.getType() == 1911) {
         box = this.mSlotCollisions.getCollisionBox(21);
      } else {
         box = this.mSlotCollisions.getCollisionBox(12);
      }

      this.mUnlockLabel = new HUDAutoTextField(box);
      this.mUnlockLabel.setPosition(box);
      this.mUnlockLabel.setCentered(true, true);
      this.mUnlockLabel.setSplitUsingWidth(true);
      this.mUnlockLabel.setVisible(true);
      if (this.getVisibleForCompleteQuestToUnlock()) {
         this.applyUnlockByMissionText(this.mUnlockLabel, 2);
      } else if (this.getVisibleForLevelUpToUnlock()) {
         this.applyUnlockByLevelText(this.mUnlockLabel, 2);
      } else {
         this.mUnlockLabel.setVisible(false);
      }

      ImageFont font = DCiDead.getFont(2);
      box = this.mSlotCollisions.getCollisionBox(15);
      x = box.getX();
      width = box.getWidth();
      y = box.getY();
      height = box.getHeight();
      int fontHeight = font.getHeight();
      this.mTime = new HUDTime(x + width / 2, height + y - fontHeight, 2);
      this.mTime.setCenterType((byte)3);
      HUDTime time = this.mTime;
      time.setVisible(this.getTimeVisibility());
      this.mSoundId = this.getSoundId();
      this.mObjects = new HUDObject[11];
      this.mObjects[0] = this.mBackground;
      this.mObjects[1] = this.mAdditionalIcon;
      this.mObjects[2] = this.mItemIcon;
      if (newItem) {
         this.mObjects[3] = this.mSaleIcon;
      } else if (saleItem) {
         this.mObjects[3] = this.mStrikeOutIcon;
      }

      if (!this.mDisableButton) {
         this.mObjects[4] = this.mButton;
      }

      this.mObjects[5] = this.mTitle;
      if (!this.mDisableDescription) {
         this.mObjects[6] = this.mDescription;
      }

      this.mObjects[7] = this.mEquippedLabel;
      this.mObjects[8] = this.mAmountLabel;
      this.mObjects[9] = this.mTime;
      this.mObjects[10] = this.mUnlockLabel;

      for (int i = 0; i < (this.mObjects == null ? 0 : this.mObjects.length); i++) {
         HUDObject child = this.mObjects[i];
         if (child != null) {
            child.setParent(this);
         }
      }
   }

   public boolean getTimeVisibility() {
      return this.mInventoryItem != null && this.mInventoryItem.isWeapon() && this.mInventoryItem.isActive();
   }

   public int getSoundId() {
      if (!this.mExternalButton) {
         HUDButton button = this.mButton;
         if (button == null || !button.isVisible()) {
            return -1;
         }
      }

      if (this.mItem == null) {
         if (this.mInAppProduct != null) {
            return 33;
         } else {
            return this.mInventoryItem == null ? -1 : 49;
         }
      } else {
         if (this.mItem.getType() == 1911) {
            AvatarDescription avatar = PlayerProfile.getActiveAvatar();
            if (!avatar.isEquipped((AvatarItem)this.mItem)) {
               Inventory inventory = PlayerProfile.getInventory();
               if (inventory.getItemCount(this.mItem) < 1) {
                  this.mItem.getPrice();
                  return 33;
               }
            }
         }

         return 33;
      }
   }

   public boolean getVisibleForLevelUpToUnlock() {
      if (this.mItem == null) {
         return false;
      }

      if (this.mItem.getType() == 1911) {
         Inventory inventory = PlayerProfile.getInventory();
         if (inventory.getItemCount(this.mItem) > 0) {
            return false;
         }
      }

      return this.mItem.isLockedByLevel();
   }

   public boolean getVisibleForCompleteQuestToUnlock() {
      return this.mItem != null && this.mItem.isLockedByMission();
   }

   public boolean getIsButtonEnabled() {
      Item item = this.mItem;
      InventoryItem inventoryItem = this.mInventoryItem;
      if (item == null) {
         if (inventoryItem == null) {
            return true;
         }

         item = inventoryItem.getItem();
      } else if (inventoryItem != null) {
         item = inventoryItem.getItem();
      }

      if (HUD.getState() == 1) {
         if (item.getType() != 101) {
            if (item.getType() != 1911) {
               return true;
            }

            if (!item.isLockedByLevel() && !item.isLockedByMission()) {
               return true;
            }

            Inventory inventory = PlayerProfile.getInventory();
            return inventory.getItemCount(item) != 0;
         }
      } else {
         if (HUD.getState() != 3 && HUD.getState() != 2) {
            return true;
         }

         int type = item.getType();
         if (Integer.compareUnsigned(type, 3270) >= 0 || Integer.compareUnsigned(type, 1907) >= 0) {
            return true;
         }

         if (Integer.compareUnsigned(type, 1583) >= 0) {
            return type != 1583;
         }

         if (type >>> 4 > 68) {
            return type != 1104;
         }

         if (type != 101) {
            return type != 147 && type != 405;
         }
      }

      if (this.mItem != null && item.getCurrency() == 13) {
         int price = item.getPrice();
         int food = PlayerProfile.getFood();
         if (food < price) {
            return false;
         }
      }

      return true;
   }

   public boolean getButtonVisibility() {
      if (this.mItem == null) {
         InventoryItem inventoryItem = this.mInventoryItem;
         if (inventoryItem == null) {
            return true;
         }

         Item item = inventoryItem.getItem();
         int type = item.getType();
         if (Integer.compareUnsigned(type, 1907) < 0) {
            if (type != 47) {
               return true;
            }

            item = this.mInventoryItem.getItem();
            return ((CollectibleItem)item).hasPackagedItems();
         } else if (type == 3270) {
            return !this.mInventoryItem.isActive();
         } else if (type == 1907) {
            WeaponItem weapon = PlayerProfile.getActiveAvatar().getWeapon();
            int id = weapon.getID();
            item = this.mInventoryItem.getItem();
            return id != item.getID();
         } else if (type != 1911) {
            return true;
         } else {
            Item avatarItem = this.mItem;
            AvatarDescription avatar = PlayerProfile.getActiveAvatar();
            if (avatar.isEquipped((AvatarItem)avatarItem)) {
               return false;
            } else {
               return !avatarItem.isLockedByLevel() ? !avatarItem.isLockedByMission() : false;
            }
         }
      } else {
         int type = this.mItem.getType();
         Item item = this.mItem;
         if (type == 1907) {
            if (((WeaponItem)item).getAmmoUsage() == 0) {
               Inventory inventory = PlayerProfile.getInventory();
               if (inventory.getItemCount(item) > 0) {
                  return false;
               }
            }

            if (this.getVisibleForLevelUpToUnlock()) {
               return false;
            }
         } else {
            type = item.getType();
            item = this.mItem;
            if (type == 1911) {
               AvatarDescription avatar = PlayerProfile.getActiveAvatar();
               if (avatar.isEquipped((AvatarItem)item)) {
                  return false;
               }

               if (item.isLockedByLevel() || item.isLockedByMission()) {
                  Inventory inventory = PlayerProfile.getInventory();
                  if (inventory.getItemCount(item) == 0) {
                     return false;
                  }
               }

               return true;
            }

            if (item.getType() != 1583 && this.mItem.getType() != 147 && this.mItem.getType() != 405) {
               if (this.mItem.getType() != 3270) {
                  return true;
               }

               if (this.getVisibleForLevelUpToUnlock()) {
                  return false;
               }
            }
         }

         return !this.getVisibleForCompleteQuestToUnlock();
      }
   }

   public void applyUnlockByMissionText(HUDAutoTextField label, int font) {
      if (label != null) {
         if (this.mItem != null && this.mItem.getID() == 1953) {
            label.setText(2425, font);
         } else {
            label.setText(1639, font);
         }
      }
   }

   public void applyUnlockByLevelText(HUDAutoTextField label, int font) {
      if (this.mItem != null) {
         int level = 0;
         if (this.mItem.getType() == 1907) {
            level = ((WeaponItem)this.mItem).getRequiredLevel();
         } else if (this.mItem.getType() == 1911) {
            level = ((AvatarItem)this.mItem).getRequiredLevel();
         } else if (this.mItem.getType() == 3270) {
            level = ((BoosterItem)this.mItem).getRequiredLevel();
         }

         if (level == 99) {
            label.setText(2064, font);
            return;
         }

         if (level > 0) {
            label.setText(2445, font, Integer.toString(level), -1);
            return;
         }
      }

      label.setText("", font);
   }

   public void applyEquippedText(HUDAutoTextField label, int font) {
      if (label != null) {
         int text = -1;
         if (this.mItem == null) {
            if (this.mInventoryItem != null) {
               Item item = this.mInventoryItem.getItem();
               if (item.getType() == 1907) {
                  WeaponItem weapon = PlayerProfile.getActiveAvatar().getWeapon();
                  if (weapon != null) {
                     int id = weapon.getID();
                     int itemId = item.getID();
                     if (id == itemId) {
                        text = 135;
                     }
                  }
               }
            }
         } else {
            int type = this.mItem.getType();
            Item item = this.mItem;
            if (type == 1911) {
               AvatarDescription avatar = PlayerProfile.getActiveAvatar();
               if (avatar != null && avatar.isEquipped((AvatarItem)item)) {
                  text = 135;
               }
            } else if (item.getType() == 1907) {
               Item weapon = this.mItem;
               Inventory inventory = PlayerProfile.getInventory();
               if (((WeaponItem)weapon).getAmmoUsage() == 0 && inventory.getItemCount(weapon) > 0) {
                  text = 34;
               }
            }
         }

         if (text != -1) {
            label.setText(text, font);
         } else {
            label.setText("", font);
         }
      }
   }

   public void applyAmountText(HUDAutoTextField label, int font) {
      InventoryItem item = this.mInventoryItem;
      if (item != null) {
         if (item.getType() == 1907 && !this.mInventoryItem.isUsingAmmo()) {
            label.setText("", font);
            return;
         }

         if (this.mInventoryItem.getType() != 3270) {
            label.setText("x" + this.mInventoryItem.getAmount(), font);
            return;
         }
      }

      label.setText("", font);
   }

   public int getIconForButton() {
      if (this.mItem == null) {
         return -1;
      }

      if (this.mItem.getType() == 1911) {
         Inventory inventory = PlayerProfile.getInventory();
         if (inventory.getItemCount(this.mItem) > 0) {
            return -1;
         }

         if (this.mItem.getPrice() < 1) {
            return -1;
         }
      }

      return this.getCurrencyResId(this.mItem);
   }

   public int getIconID() {
      if (this.mItem != null) {
         return this.mItem.getIconAnimationID();
      } else if (this.mInAppProduct != null) {
         return this.mInAppProduct.getIconRid();
      } else {
         return this.mInventoryItem != null ? this.mInventoryItem.getItem().getIconAnimationID() : -1;
      }
   }

   public void applyTitleText(HUDAutoTextField label, int font) {
      if (label != null) {
         Item item = this.mItem;
         if (item != null) {
            label.setText(item.getTitle(), font);
         } else {
            Product product = this.mInAppProduct;
            if (product != null) {
               label.setText(product.getDescription(), font);
            } else if (this.mInventoryItem != null) {
               item = this.mInventoryItem.getItem();
               label.setText(item.getTitle(), font);
            } else {
               label.setText("", font);
            }
         }
      }
   }

   public void applyDescriptionText(HUDAutoTextField label, int font) {
      if (label != null) {
         Item item = this.mItem;
         if (item == null) {
            InventoryItem inventoryItem = this.mInventoryItem;
            if (inventoryItem == null) {
               if (this.mInAppProduct == null) {
                  label.setText("", font);
               } else {
                  label.setText(this.mInAppProduct.getTitle(), font);
               }

               return;
            }

            item = inventoryItem.getItem();
         }

         applyItemDescription(item, label, font);
      }
   }

   private static void applyItemDescription(Item item, HUDAutoTextField label, int font) {
      if (item != null) {
         int type = item.getType();
         if (type == 1907) {
            String damage = Integer.toString(((WeaponItem)item).getDamage());
            if (((WeaponItem)item).getAmmoUsage() != 0 && item.getSubType() != 79 && item.getID() != 2061) {
               String ammo = Integer.toString(((WeaponItem)item).getMaxAmmo());
               label.setText(item.getDescription(), font, damage, -1, ammo, -1);
            } else {
               label.setText(item.getDescription(), font, damage, -1);
            }
         } else if (type == 1583) {
            float growing = ((FarmingItem)item).getGrowingTime() / 60000.0F;
            if (growing >= 60.0F) {
               growing /= 60.0F;
               if (growing > 24.0F) {
                  growing /= 24.0F;
               }
            }

            String time = Integer.toString((int)((long)Math.floor(growing)));
            if (item.getSubType() == 1938 && ((FarmingItem)item).getMaxAge() < 0) {
               label.setText(item.getDescription(), font, time, -1);
            } else {
               String food = Integer.toString(item.getRewardFood());
               String coins = Integer.toString(item.getRewardCoins());
               label.setText(item.getDescription(), font, time, -1, food, -1, coins, -1);
            }
         } else {
            label.setText(item.getDescription(), font);
         }
      }
   }

   public String getTextForButton() {
      if (this.mItem == null) {
         if (this.mInAppProduct != null) {
            return this.mInAppProduct.getPriceString();
         }

         if (this.mInventoryItem == null) {
            return "";
         }

         Item item = this.mInventoryItem.getItem();
         int type = item.getType();
         if (type == 47) {
            item = this.mInventoryItem.getItem();
            if (((CollectibleItem)item).hasPackagedItems()) {
               return Toolkit.getText(2464);
            }
         } else if (type == 1907) {
            return Toolkit.getText(134);
         }

         return Toolkit.getText(1675);
      } else {
         int type = this.mItem.getType();
         if (type == 1911) {
            AvatarDescription avatar = PlayerProfile.getActiveAvatar();
            if (avatar.isEquipped((AvatarItem)this.mItem)) {
               return Toolkit.getText(134);
            }

            Inventory inventory = PlayerProfile.getInventory();
            if (inventory.getItemCount(this.mItem) > 0 || this.mItem.getPrice() < 1) {
               return Toolkit.getText(134);
            }
         } else {
            if (type == 1909) {
               return Toolkit.getText(1571);
            }

            if (this.mUseSecondPrice) {
               return Integer.toString(this.mItem.getSecondPrice());
            }
         }

         return Integer.toString(this.mItem.getPrice());
      }
   }

   public void doAction() {
      if (this.mItem == null) {
         if (this.mInAppProduct != null) {
            ((WindowMarket)WindowManager.getWindow((byte)22)).startPurchase(this.mInAppProduct, this.mInAppEvent);
         } else if (this.mInventoryItem != null) {
            int type = this.mInventoryItem.getType();
            if (type == 47) {
               CollectibleItem collectible = (CollectibleItem)this.mInventoryItem.getItem();
               if (collectible.hasPackagedItems()) {
                  WindowManager.closeWindow();
                  Item item = ItemManager.calculateItem(-1, collectible.getPackagedItems());
                  if (item != null) {
                     WindowNewItem window = (WindowNewItem)WindowManager.getWindow((byte)14);
                     PlayerProfile.getInventory().addItem(item, 1);
                     window.setItem(item, -1);
                     WindowManager.openWindow((byte)14);
                  }

                  PlayerProfile.getInventory().removeItem(collectible, 1);
               }
            } else if (type == 147 || type == 405 || type == 1104 || type == 1583) {
               WindowManager.closeWindow();
               IsometricScene scene = GameEngine.getInstance().getScene();
               IsometricTile tile = scene.getTileAtCenterOfScreen();
               Item item = this.mInventoryItem.getItem();
               float x = tile.getX();
               float y = tile.getY();
               IsometricObject object = scene.createObject(item, x, y);
               scene.setSelectedObject(object);
               scene.checkSelectedObjectAtValidPosition();
               HUD.changeState(4);
            } else if (type == 101 || type == 1907 || type == 1911 || type == 3270) {
               PlayerProfile.useItem(this.mInventoryItem, false);
               if (this.mInventoryItem.getType() != 1911 && this.mInventoryItem.getType() != 101) {
                  WindowManager.closeWindow();
               }

               if (this.mInventoryItem.getType() == 101 && this.mInventoryItem.getAmount() == 0) {
                  ((WindowStuff)WindowManager.getWindow((byte)20)).updateInventoryArea();
               }
            }
         }
      } else {
         int type = this.mItem.getType();
         if (type == 101 || type == 3270) {
            InventoryItem bought = PlayerProfile.buyItem(this.mItem, 1, this.mUseSecondPrice);
            if (bought != null) {
               PlayerProfile.useItem(bought, true);
               WindowManager.closeWindow();
            }
         } else if (type == 1907) {
            InventoryItem bought = PlayerProfile.buyItem(this.mItem, 1, this.mUseSecondPrice);
            if (bought != null) {
               PlayerProfile.useItem(bought, true);
               if (!WindowManager.getWindow((byte)22).isOpened()) {
                  WindowManager.closeWindow();
               }
            }
         } else if (type == 1911) {
            int price = this.mItem.getPrice();
            Inventory inventory = PlayerProfile.getInventory();
            int count = inventory.getItemCount(this.mItem.getID());
            if (price < 1) {
               if (count == 0) {
                  PlayerProfile.getInventory().addItem(this.mItem, 1);
               }
            } else if (count < 1) {
               InventoryItem bought = PlayerProfile.buyItem(this.mItem, 1, this.mUseSecondPrice);
               if (bought == null) {
                  return;
               }

               if (this.mItem.getType() != 1911) {
                  WindowManager.closeWindow();
               }

               PlayerProfile.useItem(bought, true);
               return;
            }

            PlayerProfile.useItem(this.mItem, true);
         } else {
            if (type == 147 || type == 405 || type == 1104 || type == 1583) {
               if (!PlayerProfile.canBuy(this.mItem, 1, true, this.mUseSecondPrice)) {
                  return;
               }

               WindowManager.closeWindow();
               IsometricScene scene = GameEngine.getInstance().getScene();
               PlayerCharacter player = scene.getPlayer();
               int layer = this.mItem.getLayer();
               float x = player.getX();
               float y = player.getY();
               IsometricTile tile = scene.getTileAt(x, y);
               int sizeX = this.mItem.getTileSizeX();
               int sizeY = this.mItem.getTileSizeY();
               tile = IsometricUtils.getTileAsSpiralSearch(tile, 2, 25, sizeX, sizeY, true, layer != 1);
               if (tile == null) {
                  tile = scene.getTileAtCenterOfScreen();
               }

               Item item = this.mItem;
               x = tile.getX();
               y = tile.getY();
               IsometricObject object = scene.createObject(item, x, y);
               scene.setSelectedObject(object);
               scene.checkSelectedObjectAtValidPosition();
               scene.centerToObject(object);
               HUD.changeState(5);
               ((WindowMarket)WindowManager.getWindow((byte)22)).savePreviousMode();
            }
         }
      }
   }

   public int getCurrencyResId(Item item) {
      if (item.getType() == 1911) {
         AvatarDescription avatar = PlayerProfile.getActiveAvatar();
         if (avatar.isEquipped((AvatarItem)item)) {
            return -1;
         }
      }

      int currency = this.mUseSecondPrice ? item.getSecondCurrency() : item.getCurrency();
      if (Integer.compareUnsigned(currency - 7, 6) > 0) {
         return -1;
      }

      return switch (currency) {
         case 7 -> 66255;
         default -> -1;
         case 9 -> 66253;
         case 11 -> 66259;
         case 13 -> 66257;
      };
   }
}
