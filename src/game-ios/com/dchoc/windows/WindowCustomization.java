package com.dchoc.windows;

import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDCounter;
import com.dchoc.hud.HUDImage;
import com.dchoc.hud.HUDMenuPanel;
import com.dchoc.hud.HUDOptionButton;
import com.dchoc.hud.HUDSetOfSlots;
import com.dchoc.hud.HUDTab;
import com.dchoc.hud.HUDTouchArea;
import com.dchoc.hud.PaperDoll;
import com.dchoc.hud.RoundRectangle;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.AvatarItem;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.idead.player.Inventory;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.playhaven.PlayHaven;
import com.dchoc.spriteobject.CollisionBox;
import java.util.Vector;

public class WindowCustomization extends Window {
   public static final int MODE_MAIN = 0;
   public static final int MODE_SPOUSE = 1;
   public static final int MODE_DOG = 2;
   public static final int MODE_MALE_HATS = 0;
   public static final int MODE_MALE_HAIR = 1;
   public static final int MODE_MALE_FACE = 2;
   public static final int MODE_MALE_JACKETS = 3;
   public static final int MODE_MALE_TOPS = 4;
   public static final int MODE_MALE_BOTTOMS = 5;
   public static final int MODE_MALE_SHOES = 6;
   public static final int MODE_MALE_SPECIAL = 7;
   public static final int MODE_FEMALE_HATS = 100;
   public static final int MODE_FEMALE_HAIR = 101;
   public static final int MODE_FEMALE_JACKETS = 102;
   public static final int MODE_FEMALE_TOPS = 103;
   public static final int MODE_FEMALE_BOTTOMS = 104;
   public static final int MODE_FEMALE_SHOES = 105;
   public static final int MODE_FEMALE_SPECIAL = 106;
   public static final int MODE_DOG_COLLAR = 200;
   public static final int MODE_DOG_HEAD = 201;
   private static AvatarItem[] SKINTONES;
   private static HUDTouchArea[][] smAvatarItems;
   private static HUDCounter smCashCounter;
   private static HUDCounter smCoinsCounter;
   private static HUDImage smExtraPiece;
   private static RoundRectangle smLeftPart;
   private static HUDImage smLockedDogImage;
   private static HUDAutoTextField smLockedDogText;
   private static HUDImage smLockedSpouseImage;
   private static HUDAutoTextField smLockedSpouseText;
   private static HUDMenuPanel[] smMenuPanels;
   private static int smMode;
   private static RoundRectangle smRightPart;
   private static HUDOptionButton[] smSexButtons;
   private static HUDOptionButton[] smSkinButtons;
   private static HUDTab[] smTabs;
   private static HUDAutoTextField smTitle;
   private boolean mInitTouchAreas;

   public WindowCustomization() {
      this.mCollisions = AnimationsManager.loadShared(74827);
      this.mBackground = AnimationsManager.loadShared(74619);
      this.initCollisions();
      this.constructCloseButton(38000);
      AvatarItem[] skins = new AvatarItem[]{ItemManager.getAvatar(25), ItemManager.getAvatar(2170), ItemManager.getAvatar(2171)};
      SKINTONES = skins;
      RoundRectangle part = new RoundRectangle(0);
      smLeftPart = part;
      part.setSizes(this.mCollisions.getCollisionBox(6));
      smLeftPart.setRightPart(false);
      smLeftPart.setGradient(false);
      CollisionBox right = this.mCollisions.getCollisionBox(7);
      part = new RoundRectangle(0);
      smRightPart = part;
      part.setSizes(right);
      smRightPart.setLeftPart(false);
      smRightPart.setGradient(false);
      HUDImage image = new HUDImage(74681);
      smExtraPiece = image;
      int x = right.getX();
      int width = smExtraPiece.getWidth();
      int y = right.getY();
      image.setPosition(x - width, y);
      HUDAutoTextField text = new HUDAutoTextField(this.getCollisionBox(30));
      smTitle = text;
      text.setCentered(true, true);
      smTitle.setText(1861, 5);
      smCashCounter = new HUDCounter(1, 66249, this.mCollisions.getCollisionBox(22));
      smCoinsCounter = new HUDCounter(0, 66223, this.mCollisions.getCollisionBox(23));
      HUDTab[] tabs = new HUDTab[]{
         new HUDTab(2000, this.mCollisions.getCollisionBox(12), this.mCollisions.getCollisionBox(8), 74637, false),
         new HUDTab(2001, this.mCollisions.getCollisionBox(13), this.mCollisions.getCollisionBox(9), 74633, false),
         new HUDTab(2002, this.mCollisions.getCollisionBox(14), this.mCollisions.getCollisionBox(10), 74635, false)
      };
      smTabs = tabs;

      for (int i = 0; i < (smTabs == null ? 0 : smTabs.length); i++) {
         smTabs[i].setNeighbors(smTabs);
      }

      smSexButtons = new HUDOptionButton[2];
      CollisionBox box = this.mCollisions.getCollisionBox(80);
      smSexButtons[0] = new HUDOptionButton(74631, box.getX(), box.getY(), 3112, smSexButtons);
      box = this.mCollisions.getCollisionBox(81);
      smSexButtons[1] = new HUDOptionButton(74625, box.getX(), box.getY(), 3113, smSexButtons);
      smSkinButtons = new HUDOptionButton[4];
      box = this.mCollisions.getCollisionBox(70);
      smSkinButtons[0] = new HUDOptionButton(74627, box.getX(), box.getY(), 3114, smSkinButtons);
      box = this.mCollisions.getCollisionBox(71);
      smSkinButtons[1] = new HUDOptionButton(74623, box.getX(), box.getY(), 3115, smSkinButtons);
      box = this.mCollisions.getCollisionBox(72);
      smSkinButtons[2] = new HUDOptionButton(74629, box.getX(), box.getY(), 3116, smSkinButtons);
      box = this.mCollisions.getCollisionBox(73);
      smSkinButtons[3] = new HUDOptionButton(74639, box.getX(), box.getY(), 3117, smSkinButtons);
      HUDMenuPanel[] panels = new HUDMenuPanel[]{new HUDMenuPanel(1), new HUDMenuPanel(2), new HUDMenuPanel(3)};
      smMenuPanels = panels;
      CollisionBox menu = this.mCollisions.getCollisionBox(90);
      CollisionBox left = this.mCollisions.getCollisionBox(6);

      for (int i = 0; i < (smMenuPanels == null ? 0 : smMenuPanels.length); i++) {
         HUDMenuPanel panel = smMenuPanels[i];
         int menuX = menu.getX();
         int panelWidth = smMenuPanels[i].getWidth();
         int leftY = left.getY();
         int leftHeight = left.getHeight();
         int panelHeight = smMenuPanels[i].getHeight();
         panel.setPosition(menuX - panelWidth, leftY + (leftHeight - panelHeight) / 2);
      }

      smLockedSpouseImage = new HUDImage(67093);
      smLockedDogImage = new HUDImage(74769);
      box = this.mCollisions.getCollisionBox(6);
      image = smLockedSpouseImage;
      x = box.getX();
      width = box.getWidth();
      y = box.getY();
      int height = box.getHeight();
      image.setPosition(x + width / 2, y + height / 2);
      image = smLockedDogImage;
      x = box.getX();
      width = box.getWidth();
      y = box.getY();
      height = box.getHeight();
      image.setPosition(x + width / 2, y + height / 2);
      box = this.mCollisions.getCollisionBox(7);
      x = box.getX();
      width = box.getWidth();
      y = box.getY();
      int textWidth = box.getWidth();
      int margin = box.getWidth();
      height = box.getHeight();
      CollisionBox lockedBox = new CollisionBox(width / 10 + x, y, textWidth - margin / 5, height, 0);
      smLockedSpouseText = new HUDAutoTextField(lockedBox);
      smLockedDogText = new HUDAutoTextField(lockedBox);
      smLockedSpouseText.setSplitUsingWidth(true);
      smLockedDogText.setSplitUsingWidth(true);
      smLockedSpouseText.setCentered(true, true);
      smLockedDogText.setCentered(true, true);
      smLockedSpouseText.setText(1638, 4);
      smLockedDogText.setText(1638, 4);
      this.initTouchAreas();
      this.initCollection();
      this.refresh();
   }

   public void initTouchAreas() {
      HUDTouchArea[][] areas = new HUDTouchArea[][]{new HUDTouchArea[8], new HUDTouchArea[7], new HUDTouchArea[2]};
      smAvatarItems = areas;
      Vector[][] sets = new Vector[][]{new Vector[8], new Vector[8], new Vector[2]};

      for (int i = 0; i < sets.length; i++) {
         for (int j = 0; j < (sets[i] == null ? 0 : sets[i].length); j++) {
            sets[i][j] = new Vector(16);
         }
      }

      CollisionBox box = this.mCollisions.getCollisionBox(90);
      Vector items = ItemManager.getItems(1911);
      if (items.size() > 0) {
         int i = 0;

         do {
            AvatarItem item = (AvatarItem)items.elementAt(i);
            Inventory inventory = PlayerProfile.getInventory();
            if (item.isAvailableForPurchase() || inventory.getItemCount(item) > 0) {
               int character = this.getCharacterIndexByGender(item.getGender());
               int index = this.getArrayIndexByItemSubtype(item.getSubType());
               if (character == 1 && index > 1) {
                  index--;
               }

               if (index != -1) {
                  Vector set = sets[character][index];
                  HUDSetOfSlots.addSlotToSet(set, item, box.getWidth(), 2, false);
               }
            }
         } while (++i < items.size());
      }

      for (int i = 0; i < (smAvatarItems == null ? 0 : smAvatarItems.length); i++) {
         for (int j = 0; j < (smAvatarItems[i] == null ? 0 : smAvatarItems[i].length); j++) {
            smAvatarItems[i][j] = new HUDTouchArea(false, true, true, box, sets[i][j], false);
            smAvatarItems[i][j].setFadingBorder(true);
            smAvatarItems[i][j].setParent(this);
            smAvatarItems[i][j].setScrollbar(true);
         }
      }
   }

   @Override
   public void draw() {
      super.draw();
      if (this.mState != 1) {
         CollisionBox box = this.mCollisions.getCollisionBox(7);
         HUDImage piece = smExtraPiece;
         int x = box.getX();
         int width = smExtraPiece.getWidth();
         int y = box.getY();
         piece.setPosition(x - width, y);
         int mode = smMode;
         if (mode != 0) {
            if (mode == 1) {
               if (PlayerProfile.getAvatar(41) == null) {
                  mode = smMode;
                  if (mode != 2) {
                     return;
                  }

                  if (PlayerProfile.getAvatar(43) == null) {
                     return;
                  }
               }
            } else {
               if (mode != 2) {
                  return;
               }

               if (PlayerProfile.getAvatar(43) == null) {
                  return;
               }
            }
         }

         AvatarDescription avatar = PlayerProfile.getActiveAvatar();
         box = this.mCollisions.getCollisionBox(100);
         x = this.getX();
         int boxX = box.getX();
         y = this.getY();
         int boxY = box.getY();
         PaperDoll.drawPaperdollByLegs(boxX + x, boxY + y, avatar, true);
      }
   }

   @Override
   public void open() {
      super.open();
      if (this.mInitTouchAreas) {
         this.mObjects.removeObjects();
         this.initTouchAreas();
         this.initCollection();
         this.refresh();
         this.mInitTouchAreas = false;
      }

      if (PlayerProfile.getActiveAvatar() == PlayerProfile.getAvatar(15)) {
         smTabs[0].press();
         this.callback(2000);
      } else if (PlayerProfile.getActiveAvatar() == PlayerProfile.getAvatar(41)) {
         smTabs[1].press();
         this.callback(2001);
      } else if (PlayerProfile.getActiveAvatar() == PlayerProfile.getAvatar(43)) {
         smTabs[2].press();
         this.callback(2002);
      }
   }

   @Override
   public void callback(int id) {
      if (id < 3112) {
         if (id >= 2000) {
            if (id == 2000) {
               PlayerProfile.setActiveAvatar(15);
               this.setMode(0);
            } else if (id == 2001) {
               if (PlayerProfile.getAvatar(41) != null) {
                  PlayerProfile.setActiveAvatar(41);
               }

               this.setMode(1);
            } else if (id == 2002) {
               if (PlayerProfile.getAvatar(43) != null) {
                  PlayerProfile.setActiveAvatar(43);
               }

               this.setMode(2);
            }
         } else if (id <= 7 || id - 100 >= 0 && id - 100 <= 6 || id - 200 >= 0 && id - 200 <= 1) {
            int gender = PlayerProfile.getActiveAvatar().getGender();
            int character;
            if (gender == 43) {
               character = 2;
            } else if (gender == 1922) {
               character = 1;
            } else {
               character = 0;
            }

            for (int i = 0; i < (smAvatarItems[character] == null ? 0 : smAvatarItems[character].length); i++) {
               smAvatarItems[character][i].setVisible(false);
            }

            int index = id;
            if (index > 199) {
               index -= 200;
            }

            if (index > 99) {
               index -= 100;
            }

            smAvatarItems[character][index].setVisible(true);
            smAvatarItems[character][index].refresh();
         }
      } else {
         switch (id) {
            case 3112:
               PlayerProfile.getActiveAvatar().setGender(1921);
               break;
            case 3113:
               PlayerProfile.getActiveAvatar().setGender(1922);
               break;
            case 3114:
               PlayerProfile.getActiveAvatar().unequipItem(2123);
               return;
            case 3115:
            case 3116:
            case 3117:
               PlayerProfile.getActiveAvatar().equipItem(SKINTONES[id - 3115]);
               return;
            default:
               return;
         }

         this.refresh();
         this.refreshTouchAreas();
      }
   }

   public void setInitTouchAreas() {
      this.mInitTouchAreas = true;
   }

   @Override
   public void setState(byte state) {
      super.setState(state);
      if (state == 1) {
         PlayHaven.triggerCloseCustomization();
      } else if (state == 0) {
         PlayHaven.triggerOpenCustomization();
      }
   }

   public void initCollection() {
      this.mObjects.addObject(smLeftPart);
      this.mObjects.addObject(smRightPart);
      this.mObjects.addObject(smExtraPiece);
      this.mObjects.addObject(smTitle);
      this.mObjects.addObject(smCashCounter);
      this.mObjects.addObject(smCoinsCounter);
      this.mObjects.addObject(this.mButtonClose);
      this.mObjects.addObject(smTabs[0]);
      this.mObjects.addObject(smTabs[1]);
      this.mObjects.addObject(smTabs[2]);
      this.mObjects.addObject(smSexButtons[0]);
      this.mObjects.addObject(smSexButtons[1]);
      this.mObjects.addObject(smSkinButtons[0]);
      this.mObjects.addObject(smSkinButtons[1]);
      this.mObjects.addObject(smSkinButtons[2]);
      this.mObjects.addObject(smSkinButtons[3]);

      for (int i = 0; i < 8; i++) {
         this.mObjects.addObject(smAvatarItems[0][i]);
      }

      for (int i = 0; i < 7; i++) {
         this.mObjects.addObject(smAvatarItems[1][i]);
      }

      this.mObjects.addObject(smAvatarItems[2][0]);
      this.mObjects.addObject(smAvatarItems[2][1]);
      this.mObjects.addObject(smMenuPanels[0]);
      this.mObjects.addObject(smMenuPanels[1]);
      this.mObjects.addObject(smMenuPanels[2]);
      this.mObjects.addObject(smLockedDogImage);
      this.mObjects.addObject(smLockedSpouseImage);
      this.mObjects.addObject(smLockedDogText);
      this.mObjects.addObject(smLockedSpouseText);
   }

   public void refresh() {
      for (int i = 0; i < (smMenuPanels == null ? 0 : smMenuPanels.length); i++) {
         smMenuPanels[i].setVisible(false);
         smMenuPanels[i].select(0);
      }

      int mode = smMode;
      if (mode == 2) {
         if (PlayerProfile.getAvatar(43) != null) {
            smMenuPanels[2].setVisible(true);
         }
      } else if (mode == 1) {
         if (PlayerProfile.getAvatar(41) != null) {
            if (PlayerProfile.getAvatar(41).getGender() == 1921) {
               smMenuPanels[0].setVisible(true);
            } else {
               smMenuPanels[1].setVisible(true);
            }
         }
      } else if (mode == 0) {
         if (PlayerProfile.getAvatar(15).getGender() == 1921) {
            smMenuPanels[0].setVisible(true);
         } else {
            smMenuPanels[1].setVisible(true);
         }
      }

      int gender = PlayerProfile.getActiveAvatar().getGender();
      if (gender == 1922) {
         smSexButtons[1].press();
      } else if (gender == 1921) {
         smSexButtons[0].press();
      }

      AvatarItem[] skins = new AvatarItem[]{ItemManager.getAvatar(25), ItemManager.getAvatar(2170), ItemManager.getAvatar(2171)};
      if (PlayerProfile.getActiveAvatar().isEquipped(skins[0])) {
         smSkinButtons[1].press();
      } else if (PlayerProfile.getActiveAvatar().isEquipped(skins[1])) {
         smSkinButtons[2].press();
      } else if (PlayerProfile.getActiveAvatar().isEquipped(skins[2])) {
         smSkinButtons[3].press();
      } else {
         smSkinButtons[0].press();
      }
   }

   public void refreshTouchAreas() {
      for (int i = 0; i < (smAvatarItems == null ? 0 : smAvatarItems.length); i++) {
         for (int j = 0; j < (smAvatarItems[i] == null ? 0 : smAvatarItems[i].length); j++) {
            smAvatarItems[i][j].setVisible(false);
         }
      }

      int mode = smMode;
      if (mode == 2) {
         if (PlayerProfile.getAvatar(43) != null) {
            smAvatarItems[2][0].setVisible(true);
            smAvatarItems[2][0].refresh();
         }
      } else {
         int character;
         if (mode == 1) {
            if (PlayerProfile.getAvatar(41) == null) {
               return;
            }

            character = 41;
         } else {
            if (mode != 0) {
               return;
            }

            character = 15;
         }

         if (PlayerProfile.getAvatar(character).getGender() == 1921) {
            smAvatarItems[0][0].setVisible(true);
            smAvatarItems[0][0].refresh();
         } else if (PlayerProfile.getAvatar(character).getGender() == 1922) {
            smAvatarItems[1][0].setVisible(true);
            smAvatarItems[1][0].refresh();
         }
      }
   }

   public void setMode(int mode) {
      smMode = mode;
      this.refreshTouchAreas();
      smLockedDogImage.setVisible(false);
      smLockedSpouseImage.setVisible(false);
      smLockedDogText.setVisible(false);
      smLockedSpouseText.setVisible(false);

      for (int i = 0; i < (smSexButtons == null ? 0 : smSexButtons.length); i++) {
         smSexButtons[i].setVisible(false);
      }

      for (int i = 0; i < (smSkinButtons == null ? 0 : smSkinButtons.length); i++) {
         smSkinButtons[i].setVisible(false);
      }

      if (smMode == 2) {
         if (PlayerProfile.getAvatar(43) == null) {
            smLockedDogImage.setVisible(true);
            smLockedDogText.setVisible(true);
         }
      } else if (smMode == 1) {
         if (PlayerProfile.getAvatar(41) != null) {
            for (int i = 0; i < (smSexButtons == null ? 0 : smSexButtons.length); i++) {
               smSexButtons[i].setVisible(true);
            }

            for (int i = 0; i < (smSkinButtons == null ? 0 : smSkinButtons.length); i++) {
               smSkinButtons[i].setVisible(true);
            }
         } else {
            smLockedSpouseImage.setVisible(true);
            smLockedSpouseText.setVisible(true);
         }
      } else if (smMode == 0) {
         for (int i = 0; i < (smSexButtons == null ? 0 : smSexButtons.length); i++) {
            smSexButtons[i].setVisible(true);
         }

         for (int i = 0; i < (smSkinButtons == null ? 0 : smSkinButtons.length); i++) {
            smSkinButtons[i].setVisible(true);
         }
      }

      this.refresh();
   }

   public int getCharacterIndexByGender(int gender) {
      if (gender == 43) {
         return 2;
      } else if (gender == 1921) {
         return 0;
      } else {
         return gender == 1922 ? 1 : -1;
      }
   }

   public int getArrayIndexByItemSubtype(int subType) {
      switch (subType) {
         case 2115:
            return 3;
         case 2116:
            return 7;
         case 2117:
            return 1;
         case 2118:
            return 2;
         case 2119:
            return 0;
         case 2120:
            return 6;
         case 2121:
            return 4;
         case 2122:
            return 5;
         case 2123:
            return -1;
         case 2124:
            return 7;
         case 2125:
            return 0;
         case 2126:
            return -1;
         case 2127:
            return 1;
         case 2128:
            return 1;
         default:
            return -1;
      }
   }
}
