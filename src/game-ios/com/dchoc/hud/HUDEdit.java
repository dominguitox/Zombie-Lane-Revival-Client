package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.Time;
import com.dchoc.idead.actions.Action;
import com.dchoc.idead.areas.AreaRob;
import com.dchoc.idead.characters.NeighborCharacter;
import com.dchoc.idead.characters.PlayerCharacter;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.objects.FarmingObject;
import com.dchoc.idead.objects.InteractiveObject;
import com.dchoc.idead.player.Inventory;
import com.dchoc.idead.player.InventoryItem;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.servlets.HireNeighbor;
import com.dchoc.idead.tracking.HireEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.windows.WindowMarket;
import com.dchoc.windows.WindowOutOfCashCoins;
import com.dchoc.windows.WindowSellItem;
import com.dchoc.windows.WindowSocial;
import java.util.Vector;

public class HUDEdit extends HUDCollection {
   private static String[] smParams;
   private HUDButton mButtonRotate;
   private HUDButton mButtonStore;
   private HUDButton mButtonSell;
   private HUDButton mButtonCancel;
   private HUDButton mButtonAccept;
   private HUDImage mBackground;
   private HUDImage mHireStripe;
   private HUDImage mStripeBackground;
   private HUDAutoTextField mStripeText;
   private int mState;
   private HireNeighbor mHireNeighbor;
   private IsometricTile mSavedTile;
   private int mSavedRotation;
   private HireEvent mHireEvent;
   private IsometricObject mHireTarget;
   private Vector mHireObjects;
   private HUDAcceptHire mAcceptHire;
   private String mHireText;
   private NeighborCharacter mHiredCharacter;
   private IsometricObject mHireObject;

   protected HUDEdit() {
      int width = Toolkit.getScreenWidth();
      int height = Toolkit.getScreenHeight();
      this.mBackground = new HUDImage(74593, width, height);
      this.addObject(this.mBackground);
      this.mHireStripe = new HUDImage(78837, width, height);
      this.addObject(this.mHireStripe);
      CollisionBox box = this.mBackground.getCollisionBox(0);
      this.mButtonRotate = new HUDButton(0, box.getX() + width, box.getY() + height, 71305, (byte)15);
      this.addObject(this.mButtonRotate);
      box = this.mBackground.getCollisionBox(1);
      this.mButtonStore = new HUDButton(1, box.getX() + width, box.getY() + height, 74599, (byte)17);
      this.addObject(this.mButtonStore);
      box = this.mBackground.getCollisionBox(2);
      this.mButtonSell = new HUDButton(2, box.getX() + width, box.getY() + height, 71309, (byte)16);
      this.addObject(this.mButtonSell);
      box = this.mBackground.getCollisionBox(3);
      this.mButtonCancel = new HUDButton(3, box.getX() + width, box.getY() + height, 75025, (byte)13);
      this.addObject(this.mButtonCancel);
      this.mStripeBackground = new HUDImage(74591, width, height);
      this.addObject(this.mStripeBackground);
      box = this.mStripeBackground.getCollisionBox(0);
      int x = box.getX();
      int y = box.getY();
      HUDAutoTextField text = new HUDAutoTextField(width + x, height + y, box.getWidth(), box.getHeight());
      this.mStripeText = text;
      text.setCentered(false, true);
      this.mStripeText.setSplitUsingWidth(true);
      this.addObject(this.mStripeText);
      HUDButton accept = new HUDButton(4, 0, 0, 72523, (byte)14);
      this.mButtonAccept = accept;
      accept.setVisible(false);
      this.mButtonAccept.setForcedPosition(0);
      this.addObject(this.mButtonAccept);
      this.mHireNeighbor = new HireNeighbor();
      this.mHireEvent = new HireEvent();
      this.mHireObjects = new Vector(5);
      this.mHireTarget = null;
      this.mHiredCharacter = null;
      HUDAcceptHire hire = new HUDAcceptHire();
      this.mAcceptHire = hire;
      hire.init();
      this.addObject(this.mAcceptHire);
      smParams = new String[1];
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      if (this.mState != 7) {
         IsometricScene scene = IsometricUtils.getScene();
         HUDButton accept = this.mButtonAccept;
         accept.setEnabled(scene.isSelectedObjectAtValidPosition());
      }
   }

   public void changeState(int state) {
      int width = Toolkit.getScreenWidth();
      int height = Toolkit.getScreenHeight();
      IsometricScene scene = IsometricUtils.getScene();
      IsometricObject target = scene.getSelectedObject();
      if (state == 6) {
         this.mButtonAccept.setVisible(false);
      } else {
         this.mButtonAccept.setVisible(true);
         this.mButtonAccept.setTargetObject(target);
      }

      this.mBackground.setVisible(true);
      this.mHireStripe.setVisible(false);
      CollisionBox box = this.mBackground.getCollisionBox(3);
      HUDButton cancel = this.mButtonCancel;
      int x = box.getX();
      cancel.setPosition(width + x, height + box.getY());
      this.mButtonCancel.setVisible(true);
      this.showButtons(true, true, true);
      this.mAcceptHire.setVisible(false);
      switch (state) {
         case 3:
         case 5: {
            boolean rotateable = false;
            if (this.mState != 7) {
               rotateable = IsometricUtils.isObjectRotateable(target);
            }

            this.enableButtons(rotateable, false, false);
            scene.changeState(2);
            boolean text = IsometricUtils.isObjectRotateable(target);
            HUDAutoTextField stripe = this.mStripeText;
            stripe.setText(text ? 2320 : 2321, DCiDead.getFont(3));
            break;
         }
         case 4: {
            Item item = target.getItem();
            if (item.getType() == 147) {
               this.enableButtons(IsometricUtils.isObjectRotateable(target), false, false);
            } else {
               int type = item.getType();
               boolean rotateable = IsometricUtils.isObjectRotateable(target);
               this.enableButtons(rotateable, type != 1104, item.hasValidPrice());
            }

            scene.changeState(2);
            this.storeObjectPosition(target);
            boolean text = IsometricUtils.isObjectRotateable(target);
            HUDAutoTextField stripe = this.mStripeText;
            stripe.setText(text ? 2318 : 2319, DCiDead.getFont(3));
            break;
         }
         case 6: {
            this.mButtonAccept.setVisible(false);
            this.enableButtons(false, false, false);
            scene.changeState(1);
            HUDAutoTextField stripe = this.mStripeText;
            stripe.setText(2317, DCiDead.getFont(3));
            break;
         }
         case 7:
            this.mButtonAccept.setVisible(false);
            this.showButtons(false, false, false);
            this.enableButtons(false, false, false);
            scene.changeState(1);
            this.mHireObject = null;
            smParams[0] = GameEngineUtils.getNeighbor().getName();
            String hireText = Toolkit.replaceParameters(Toolkit.getText(2757), smParams);
            this.mHireText = hireText;
            this.mStripeText.setText(hireText, 3);
            this.mBackground.setVisible(false);
            this.mHireStripe.setVisible(true);
            CollisionBox stripeBox = this.mHireStripe.getCollisionBox(0);
            HUDButton cancelButton = this.mButtonCancel;
            int stripeX = stripeBox.getX();
            int stripeWidth = stripeBox.getWidth();
            int stripeY = stripeBox.getY();
            int stripeHeight = stripeBox.getHeight();
            cancelButton.setPosition(width + stripeX + (stripeWidth >> 1), stripeY + height + (stripeHeight >> 1));
      }

      this.mState = state;
   }

   public void showAcceptHire(boolean show) {
      HUDAcceptHire hire = this.mAcceptHire;
      if (!show) {
         hire.setNeighbor(null);
         this.mAcceptHire.setVisible(false);
         this.mStripeText.setText(this.mHireText, 3);
         this.mHireStripe.setVisible(true);
      } else {
         hire.setVisible(true);
         NeighborProfile neighbor = GameEngineUtils.getNeighbor();
         HUDAutoTextField stripe = this.mStripeText;
         String name = neighbor.getName();
         stripe.setText(1718, 3, name, -1, Integer.toString(200), -1);
         this.mHireStripe.setVisible(false);
      }

      this.mButtonCancel.setVisible(!show);
   }

   @Override
   public void callback(int id) {
      IsometricScene scene = IsometricUtils.getScene();
      IsometricObject target = scene.getSelectedObject();
      switch (id) {
         case 0:
            if (target != null) {
               IsometricUtils.rotateObject(target);
               scene.checkSelectedObjectAtValidPosition();
            }

            return;
         case 1:
            if (target == null) {
               return;
            }

            scene.removeObject(target, true);
            PlayerProfile.getInventory().addItem(target.getItem(), 1);
            HUD.changeState(6);
            return;
         case 2:
            if (target != null) {
               ((WindowSellItem)WindowManager.getWindow((byte)15)).setObject(target);
               WindowManager.openWindow((byte)15);
            }

            return;
         case 3:
            this.cancelCurrentState();
            switch (this.mState) {
               case 3:
                  WindowManager.openWindow((byte)20);
                  HUD.changeState(1);
                  return;
               case 4:
               case 6:
                  HUD.changeState(1);
                  return;
               case 5:
                  WindowManager.openWindow((byte)22);
                  ((WindowMarket)WindowManager.getWindow((byte)22)).setPreviouMode();
                  HUD.changeState(1);
                  return;
               case 7:
                  WindowManager.openWindow((byte)18);
                  ((WindowSocial)WindowManager.getWindow((byte)18)).showNeighbors(GameEngineUtils.getNeighbor());
                  HUD.changeState(GameEngineUtils.isAtAdventureArea() ? 2 : 1);
                  return;
               default:
                  HUD.changeState(6);
                  return;
            }
         case 4:
            if (this.mState == 7) {
               if (!this.canAcceptHire()) {
                  return;
               }

               if (PlayerProfile.getCoins() < 200) {
                  ((WindowOutOfCashCoins)WindowManager.getWindow((byte)11)).setMode(1);
                  WindowManager.openWindow((byte)11);
                  return;
               }

               NeighborProfile neighbor = GameEngineUtils.getNeighbor();
               if (neighbor.getType() == 2514) {
                  this.hireAccepted();
               } else {
                  this.mHireNeighbor.init(neighbor);
                  this.mHireNeighbor.execute();
               }

               HireEvent event = this.mHireEvent;
               String user = neighbor.getUserID();
               int type = this.mHireTarget.getItem().getType();
               event.trackHire(user, type, this.mHireTarget.getItem().getSubType());
            }

            if (target == null) {
               return;
            } else {
               int state = this.mState;
               if (state == 5) {
                  InventoryItem bought = PlayerProfile.buyItem(target.getItem(), 1, false);
                  if (bought == null) {
                     return;
                  } else {
                     target.setHighlighted(false);
                     if (target.getType() == 6) {
                        Item item = target.getItem();
                        ((FarmingObject)target).resetTimestamp();
                        MissionManager.updateCounter(128, item.getID(), 1);
                        MissionManager.updateCounter(128, item.getSubType(), 1);
                        MissionManager.updateCounter(128, 80, 1);
                     }

                     PlayerProfile.getInventory().removeItem(bought, 1);
                     if (target.getType() == 0) {
                        WindowManager.openWindow((byte)22);
                        ((WindowMarket)WindowManager.getWindow((byte)22)).setPreviouMode();
                        HUD.changeState(1);
                        return;
                     }

                     int layer = target.getItem().getLayer();
                     IsometricTile tile = scene.getTileAt(target.getX(), target.getY());
                     int sizeX = target.getTileSizeX();
                     int sizeY = target.getTileSizeY();
                     tile = IsometricUtils.getTileAsSpiralSearch(tile, 1, 25, sizeX, sizeY, false, layer != 1);
                     if (tile == null) {
                        tile = scene.getTileAtCenterOfScreen();
                     }

                     Item item = target.getItem();
                     IsometricObject copy = scene.createObject(item, tile.getX(), tile.getY());
                     scene.setSelectedObject(copy);
                     scene.checkSelectedObjectAtValidPosition();
                     this.mButtonAccept.setTargetObject(copy);
                     scene.getCamera().flyTo(copy);
                     return;
                  }
               } else if (state == 4) {
                  Item item = target.getItem();
                  MissionManager.updateCounter(2500, item.getID(), 1);
                  MissionManager.updateCounter(2500, item.getType(), 1);
                  MissionManager.updateCounter(2500, item.getSubType(), 1);
                  MissionManager.updateCounter(2500, 80, 1);
                  if (target.getType() == 5) {
                     PlayerCharacter player = scene.getPlayer();
                     Action action = player.getCurrentAction();
                     if (action != null && action.getTarget() == target) {
                        player.cancelActions(true);
                        target.setFixing(false);
                     }
                  }

                  scene.updateAlertedObjects();
                  HUD.changeState(6);
                  return;
               } else {
                  if (state != 3) {
                     return;
                  }

                  Inventory inventory = PlayerProfile.getInventory();
                  inventory.removeItem(target.getItem(), 1);
                  if (target.getType() == 6) {
                     ((FarmingObject)target).resetTimestamp();
                  }

                  HUD.changeState(1);
                  return;
               }
            }
      }
   }

   public void cancelCurrentState() {
      IsometricScene scene = IsometricUtils.getScene();
      PlayerCharacter player = scene.getPlayer();
      if (player != null) {
         player.cancelActions(true);
      }

      IsometricObject target = scene.getSelectedObject();
      boolean restored = false;
      if (target != null) {
         target.setHighlighted(false);
         if (this.mState == 4) {
            if (this.mSavedTile != null) {
               this.restoreObjectPosition(target);
            }

            restored = true;
         }
      }

      if (!restored && (this.mState == 3 || this.mState == 5) && target != null) {
         scene.removeObject(target, true);
      }

      if (this.mHiredCharacter != null) {
         scene.removeObject(this.mHiredCharacter, true);
         this.mHiredCharacter = null;
      }

      if (this.mState == 7) {
         this.showAcceptHire(false);
      }
   }

   public void calculateHireObjects(IsometricObject object) {
      if (object != null && object.getTile() != null && (this.mHireObject == null || !object.equals(this.mHireObject))) {
         this.mHireObject = object;
         IsometricScene scene = IsometricUtils.getScene();
         IsometricTile tile = object.getTile();
         NeighborProfile neighbor = GameEngineUtils.getNeighbor();
         NeighborCharacter character = this.mHiredCharacter;
         if (character == null) {
            if (neighbor.getType() == 2514) {
               character = scene.createNPC(neighbor, 17, 0.0F, 0.0F);
            } else {
               character = scene.createNeighbor(neighbor, neighbor.getActiveCharacter(), 0.0F, 0.0F);
            }

            this.mHiredCharacter = character;
            if (character == null) {
               return;
            }
         }

         character.cancelActions(true);
         this.mHireTarget = null;
         this.mHireObjects.removeAllElements();
         this.calculateHireObjects(tile, this.mHiredCharacter, this.mHireObjects);
         if (!this.mHireObjects.isEmpty()) {
            this.mHireTarget = (IsometricObject)this.mHireObjects.firstElement();
            IsometricTile free = IsometricUtils.getTileAsSpiralSearch(this.mHireTarget.getTile(), 2, 50, 1, 1, true, true);
            if (free == null) {
               free = scene.getTileAtCenterOfScreen();
            }

            NeighborCharacter placed = this.mHiredCharacter;
            placed.setPosition(free.getX(), free.getY());
            NeighborCharacter turned = this.mHiredCharacter;
            turned.turnTo(this.mHireTarget.getX(), this.mHireTarget.getY());
            scene.centerToObjectWithBounds(this.mHireTarget);
            this.mAcceptHire.setNeighbor(this.mHiredCharacter);
            this.showAcceptHire(true);
         }
      }
   }

   public void hireAccepted() {
      this.mHiredCharacter = null;
      PlayerProfile.removeCoins(200);
      NeighborProfile neighbor = GameEngineUtils.getNeighbor();
      neighbor.setHired(true);
      if (neighbor.getType() == 2514) {
         long now = Time.getTimestamp();
         long day = now % 86400000L;
         AreaRob rob = GameEngineUtils.getRob();
         rob.setHireCountdown(now + 86400000L - day);
         rob.saveRobsData();
      } else {
         PlayerProfile.setHiresLeft(PlayerProfile.getHiresLeft() - 1);
      }

      HUD.changeState(GameEngineUtils.isAtAdventureArea() ? 2 : 1);
      MissionManager.updateCounter(118, 80, 1);
   }

   public void storeObjectPosition(IsometricObject object) {
      if (object != null) {
         this.mSavedTile = object.getTile();
         this.mSavedRotation = IsometricUtils.getObjectRotation(object);
      } else {
         this.mSavedTile = null;
         this.mSavedRotation = 1;
      }
   }

   public void restoreObjectPosition(IsometricObject object) {
      if (object != null) {
         if (this.mSavedTile != null) {
            float x = this.mSavedTile.getX();
            object.setPosition(x, this.mSavedTile.getY());
         }

         IsometricUtils.setObjectRotation(object, this.mSavedRotation);
      }
   }

   public void enableButtons(boolean rotate, boolean store, boolean sell) {
      this.mButtonRotate.setEnabled(rotate);
      this.mButtonStore.setEnabled(store);
      this.mButtonSell.setEnabled(sell);
   }

   public void showButtons(boolean rotate, boolean store, boolean sell) {
      this.mButtonRotate.setVisible(rotate);
      this.mButtonStore.setVisible(store);
      this.mButtonSell.setVisible(sell);
   }

   public void calculateHireObjects(IsometricTile tile, NeighborCharacter character, Vector objects) {
      int column = tile.getColumn();
      int row = tile.getRow();
      IsometricObject found = this.interactObjectsAt(column, row, character, objects);
      if (found != null) {
         objects.addElement(found);
         if (objects.size() > 4) {
            return;
         }
      }

      int radius = 1;

      do {
         int side = radius * 2;
         if (0 < side) {
            for (int i = 0; i < side; i++) {
               found = this.interactObjectsAt(column - radius + i, row - radius, character, objects);
               if (found != null) {
                  objects.addElement(found);
                  if (objects.size() > 4) {
                     return;
                  }
               }
            }

            for (int i = 0; i < side; i++) {
               found = this.interactObjectsAt(column - radius + side, row - radius + i, character, objects);
               if (found != null) {
                  objects.addElement(found);
                  if (objects.size() > 4) {
                     return;
                  }
               }
            }

            for (int i = 0; i < side; i++) {
               found = this.interactObjectsAt(column + radius - i, row - radius + side, character, objects);
               if (found != null) {
                  objects.addElement(found);
                  if (objects.size() > 4) {
                     return;
                  }
               }
            }

            for (int i = 0; i < side; i++) {
               found = this.interactObjectsAt(column - radius, row + radius + i, character, objects);
               if (found != null) {
                  objects.addElement(found);
                  if (objects.size() > 4) {
                     return;
                  }
               }
            }
         }
      } while (++radius < 51 || objects.size() >= 5);
   }

   public IsometricObject interactObjectsAt(int column, int row, NeighborCharacter character, Vector objects) {
      IsometricTile tile = IsometricUtils.getScene().getTile(column, row);
      if (tile == null) {
         return null;
      }

      Vector onTile = tile.getObjects();
      if (0 < onTile.size()) {
         int i = 0;

         do {
            InteractiveObject object = (InteractiveObject)onTile.elementAt(i);
            if (object.canInteractWith(character) && !objects.contains(object)) {
               Action action = object.interactWith(character);
               if (action != null) {
                  action.setHired(true);
                  return object;
               }
            }
         } while (++i < onTile.size());
      }

      return null;
   }

   public boolean canAcceptHire() {
      return this.mHireTarget != null && this.mHiredCharacter != null;
   }
}
