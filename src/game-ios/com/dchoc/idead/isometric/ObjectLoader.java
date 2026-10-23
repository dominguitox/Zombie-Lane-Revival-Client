package com.dchoc.idead.isometric;

import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.Time;
import com.dchoc.idead.characters.PlayerCharacter;
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
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.objects.ConstructionObject;
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

public class ObjectLoader {
   public static final int VERSION_NUMBER = 103;

   public static int save(IsometricObject object, DChocByteArray data) {
      Item item = object.getItem();
      data.writeShort(item.getType());
      data.writeShort(item.getID());
      int start = data.getSize();
      data.writeShort(object.getType());
      data.writeShort(object.getID());
      IsometricTile tile = object.getTile();
      data.writeByte(tile != null ? tile.getColumn() : 0);
      data.writeByte(tile != null ? tile.getRow() : 0);
      switch (object.getType()) {
         case 0:
            ConstructionObject construction = (ConstructionObject)object;
            data.writeByte(construction.getState());
            data.writeByte(construction.getClicks());
            break;
         case 1:
            data.writeInt(((PlayerCharacter)object).getAvatar().getCharacter());
            break;
         case 2:
            ZombieCharacter zombie = (ZombieCharacter)object;
            data.writeByte(zombie.getHealth());
            data.writeBoolean(zombie.getRewardItem() != null);
            if (zombie.getRewardItem() != null) {
               data.writeShort(zombie.getRewardItem().getID());
            }

            data.writeShort(zombie.getSpawnID());
            break;
         case 3:
            LootObject loot = (LootObject)object;
            data.writeByte(loot.getRealHealth());
            data.writeBoolean(loot.getRewardItem() != null);
            if (loot.getRewardItem() != null) {
               data.writeShort(loot.getRewardItem().getID());
            }

            data.writeShort(loot.getSpawnID());
            break;
         case 4:
            data.writeShort(((DecorationObject)object).getRotation());
            break;
         case 5:
            data.writeByte(((FenceObject)object).getRealHealth());
            break;
         case 6:
            FarmingObject farming = (FarmingObject)object;
            ToolkitHelpers.writeLong(farming.getTimestamp(), data);
            ToolkitHelpers.writeLong(farming.getDestructTimestamp(), data);
            data.writeBoolean(farming.isRevived());
            data.writeBoolean(farming.isHarvested());
            data.writeBoolean(farming.isDestroyed());
            data.writeBoolean(farming.isAttacked());
      }

      return data.getSize() - start;
   }

   public static void load(DChocByteArray data, int size) throws EOFException {
      IsometricScene scene = IsometricUtils.getScene();
      boolean atNeighbor = GameEngineUtils.isAtNeigborsPlace();
      boolean atAdventure = GameEngineUtils.isAtAdventureArea();
      float x = IsometricTile.getTilesSizeHalf();
      float y = IsometricTile.getTilesSizeHalf();
      int itemType = data.readShort();
      int itemId = data.readShort();
      Item item = ItemManager.getItem(itemType, itemId);
      if (item != null && (item.getType() != 50 || !atNeighbor || !((DestructibleItem)item).isHideAtNeighbor())) {
         int type = data.readShort();
         int id = data.readShort();
         int column = data.readByte();
         int row = data.readByte();
         IsometricTile tile = scene.getTile(column, row);
         if (tile != null) {
            x = tile.getX();
            y = tile.getY();
         }

         switch (type) {
            case 0:
               int state = data.readByte();
               int clicks = data.readByte();
               scene.createConstruction((ConstructionItem)item, x, y, state, clicks).setID(id);
               break;
            case 1:
               loadPlayer(scene, data.readInt(), x, y, atNeighbor, atAdventure);
               break;
            case 2:
               ZombieCharacter zombie = scene.createZombie((ZombieItem)item, x, y);
               zombie.setID(id);
               zombie.setHealth(data.readByte());
               zombie.setRewardItem(data.readBoolean() ? ItemManager.getItem(data.readShort()) : null);
               zombie.setSpawnID(data.readShort());
               int spawnId = zombie.getSpawnID();
               if (spawnId == -1) {
                  return;
               }

               SpawningDefinition spawning = SpawningManager.getSpawnID(spawnId);
               if (spawning == null) {
                  return;
               }

               zombie.setGuarding(spawning.isGuarding());
               break;
            case 3:
               LootObject loot = scene.createLoot((DestructibleItem)item, x, y);
               loot.setID(id);
               loot.setRealHealth(data.readByte());
               loot.setRewardItem(data.readBoolean() ? ItemManager.getItem(data.readShort()) : null);
               loot.setSpawnID(data.readShort());
               break;
            case 4:
               DecorationObject decoration = scene.createDecoration((DecorationItem)item, x, y);
               decoration.setID(id);
               decoration.setRotation(data.readShort());
               break;
            case 5:
               FenceObject fence = scene.createFence((FenceItem)item, x, y);
               fence.setID(id);
               fence.setRealHealth(data.readByte());
               break;
            case 6:
               FarmingObject farming = scene.createFarming((FarmingItem)item, x, y);
               farming.setID(id);
               farming.setTimestamp(Fixes.fixTimestamp(ToolkitHelpers.readLong(data)));
               farming.setDestructTimestamp(Fixes.fixTimestamp(ToolkitHelpers.readLong(data)));
               farming.setRevived(data.readBoolean());
               farming.setHarvested(data.readBoolean());
               farming.setDestroyed(data.readBoolean());
               farming.setAttacked(data.readBoolean());
         }
      } else {
         data.skip(size);
      }
   }

   private static void loadPlayer(IsometricScene scene, int character, float x, float y, boolean atNeighbor, boolean atAdventure) {
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
      } else if (!atAdventure) {
         AvatarDescription active = PlayerProfile.getActiveAvatar();
         AvatarDescription avatar = PlayerProfile.getAvatar(character);
         scene.createPlayer(avatar, x, y, avatar == active);
      } else if (scene.getPlayer() == null) {
         scene.createPlayer(PlayerProfile.getActiveAvatar(), x, y, true);
      }
   }

   public static Item loadItem(DChocByteArray data, int size) throws EOFException {
      int type = data.readShort();
      int id = data.readShort();
      Item item = ItemManager.getItem(type, id);
      data.skip(size);
      return item;
   }

   public static void loadUncompressed(DChocByteArray data) throws EOFException {
      IsometricScene scene = IsometricUtils.getScene();
      boolean atNeighbor = GameEngineUtils.isAtNeigborsPlace();
      int id = data.readShort();
      int itemType = data.readShort();
      int itemId = data.readShort();
      int column = data.readByte();
      int row = data.readByte();
      int rotation = data.readByte();
      int minutes = data.readInt();
      int health = data.readByte();
      int mission = data.readShort();
      Item item = ItemManager.getItem(itemType, itemId);
      if (item != null) {
         if ((mission & 65535) == 65535 || MissionManager.getMission(mission).isCompleted()) {
            if (item.getTileSizeY() + row < scene.getRows()) {
               row += item.getTileSizeY();
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
               if (minutes >= 0) {
                  farming.setTimestamp(Time.getTimestamp() - minutes * 60000);
               }
            } else if (itemType == 1104) {
               FenceObject fence = scene.createFence((FenceItem)item, x, y);
               fence.setID(id);
               if (health >= 0) {
                  fence.setHealth(health);
               }
            } else if (itemType == 837) {
               scene.createPlayer(PlayerProfile.getActiveAvatar(), x, y, true);
            } else if (itemType == 147) {
               scene.createConstruction((ConstructionItem)item, x, y, 0, 0).setID(id);
            } else if (itemType == 405) {
               DecorationObject decoration = scene.createDecoration((DecorationItem)item, x, y);
               decoration.setID(id);
               if (rotation >= 0) {
                  decoration.setRotation(rotation);
               }
            } else if (itemType == 50) {
               loadDestructible(scene, (DestructibleItem)item, id, health, x, y, atNeighbor);
            }
         }
      }
   }

   private static void loadDestructible(IsometricScene scene, DestructibleItem item, int id, int health, float x, float y, boolean atNeighbor) {
      if (!atNeighbor || !item.isHideAtNeighbor()) {
         IsometricObject object;
         if (item.isZombie()) {
            ZombieCharacter zombie = scene.createZombie((ZombieItem)item, x, y);
            zombie.setID(id);
            if (health >= 0) {
               zombie.setHealth(health);
            }

            object = zombie;
         } else {
            LootObject loot = scene.createLoot(item, x, y);
            loot.setID(id);
            if (health >= 0) {
               loot.setHealth(health);
            }

            object = loot;
         }

         if (object != null && item.getRewardExtraItems() != null) {
            object.setRewardItem(ItemManager.calculateItem(item.getRewardExtraType(), item.getRewardExtraItems()));
         }
      }
   }

   public static Item loadItemUncompressed(DChocByteArray data) throws EOFException {
      data.skip(2);
      int type = data.readShort();
      int id = data.readShort();
      data.skip(10);
      return ItemManager.getItem(type, id);
   }
}
