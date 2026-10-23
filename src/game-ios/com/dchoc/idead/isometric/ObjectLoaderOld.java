package com.dchoc.idead.isometric;

import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.characters.ZombieCharacter;
import com.dchoc.idead.fixes.Fixes;
import com.dchoc.idead.items.ConstructionItem;
import com.dchoc.idead.items.DecorationItem;
import com.dchoc.idead.items.DestructibleItem;
import com.dchoc.idead.items.FarmingItem;
import com.dchoc.idead.items.FenceItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.items.ZombieItem;
import com.dchoc.idead.objects.DecorationObject;
import com.dchoc.idead.objects.FarmingObject;
import com.dchoc.idead.objects.FenceObject;
import com.dchoc.idead.objects.LootObject;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.spawning.SpawningDefinition;
import com.dchoc.idead.spawning.SpawningManager;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ToolkitHelpers;
import java.io.EOFException;

public class ObjectLoaderOld {
   public static final int VERSION_NUMBER = 100;

   public static void writeCrash(DChocByteArray data) {
      data.writeShort(0);
      data.writeShort(-1);
      data.writeShort(-1);
      data.writeByte(0);
      data.writeByte(0);
   }

   public static void readCrash(DChocByteArray data) throws EOFException {
      data.readShort();
      data.readShort();
      data.readShort();
      data.readByte();
      data.readByte();
   }

   public static void load(DChocByteArray data) throws Exception {
      IsometricScene scene = IsometricUtils.getScene();
      boolean atNeighbor = GameEngineUtils.isAtNeigborsPlace();
      int id = data.readShort();
      int itemType = data.readShort();
      int itemId = data.readShort();
      int column = data.readByte();
      int row = data.readByte();
      Item item = ItemManager.getItem(itemType, itemId);
      if (item == null) {
         throw new Exception("Old object loader doesn't support new items");
      }

      IsometricTile tile = scene.getTile(column, row);
      float x = 0.0F;
      float y = 0.0F;
      if (tile != null) {
         x = tile.getX();
         y = tile.getY();
      }

      if (itemType == 1583) {
         FarmingObject farming = scene.createFarming((FarmingItem)item, x, y);
         farming.setID(id);
         farming.setTimestamp(Fixes.fixTimestamp(ToolkitHelpers.readLong(data)));
         farming.setDestructTimestamp(Fixes.fixTimestamp(ToolkitHelpers.readLong(data)));
         farming.setRevived(data.readBoolean());
         farming.setHarvested(data.readBoolean());
         farming.setDestroyed(data.readBoolean());
         farming.setAttacked(data.readBoolean());
      } else if (itemType == 1104) {
         FenceObject fence = scene.createFence((FenceItem)item, x, y);
         fence.setID(id);
         fence.setRealHealth(data.readByte());
      } else if (itemType == 837) {
         loadPlayer(scene, data, item, x, y, atNeighbor);
      } else if (itemType == 50) {
         loadDestructible(scene, data, (DestructibleItem)item, id, x, y, atNeighbor);
      } else if (itemType == 147) {
         int state = data.readByte();
         int clicks = data.readByte();
         scene.createConstruction((ConstructionItem)item, x, y, state, clicks).setID(id);
      } else if (itemType == 405) {
         DecorationObject decoration = scene.createDecoration((DecorationItem)item, x, y);
         decoration.setID(id);
         decoration.setRotation(data.readShort());
      }
   }

   private static void loadPlayer(IsometricScene scene, DChocByteArray data, Item item, float x, float y, boolean atNeighbor) throws EOFException {
      if (item.getID() == 837) {
         scene.createPlayer(PlayerProfile.getActiveAvatar(), x, y, true);
      } else {
         int character = data.readInt();
         if (!data.readBoolean()) {
            if (atNeighbor) {
               scene.createNeighbor(GameEngineUtils.getNeighbor(), character, x, y);
               if (scene.getPlayer() == null) {
                  IsometricTile tile = IsometricUtils.getTileAsSpiralSearch(scene.getTileAt(x, y), 2, 50, 1, 1, true, true);
                  if (tile == null) {
                     tile = scene.getTileAtCenterOfScreen();
                  }

                  AvatarDescription active = PlayerProfile.getActiveAvatar();
                  scene.createPlayer(active, tile.getX(), tile.getY(), true);
               }
            } else {
               AvatarDescription active = PlayerProfile.getActiveAvatar();
               AvatarDescription avatar = PlayerProfile.getAvatar(character);
               scene.createPlayer(avatar, x, y, avatar == active);
            }
         }
      }
   }

   private static void loadDestructible(IsometricScene scene, DChocByteArray data, DestructibleItem item, int id, float x, float y, boolean atNeighbor) throws EOFException {
      if (atNeighbor && item.isHideAtNeighbor()) {
         data.readByte();
         if (data.readBoolean()) {
            data.readShort();
         }

         data.readShort();
      } else if (item.isZombie()) {
         ZombieCharacter zombie = scene.createZombie((ZombieItem)item, x, y);
         zombie.setID(id);
         zombie.setHealth(data.readByte());
         zombie.setRewardItem(data.readBoolean() ? ItemManager.getItem(data.readShort()) : null);
         zombie.setSpawnID(data.readShort());
         int spawnId = zombie.getSpawnID();
         if (spawnId != -1) {
            SpawningDefinition spawning = SpawningManager.getSpawning(spawnId);
            if (spawning != null) {
               zombie.setGuarding(spawning.isGuarding());
            }
         }
      } else {
         LootObject loot = scene.createLoot(item, x, y);
         loot.setID(id);
         loot.setRealHealth(data.readByte());
         loot.setRewardItem(data.readBoolean() ? ItemManager.getItem(data.readShort()) : null);
         loot.setSpawnID(data.readShort());
      }
   }
}
