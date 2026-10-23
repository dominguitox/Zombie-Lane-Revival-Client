package com.dchoc.idead.characters;

import com.dchoc.hud.HUD;
import com.dchoc.idead.GameEngineUtils;
import com.dchoc.idead.commands.CommandBase;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.items.PlayerItem;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.neighbors.NeighborProfile;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.toolkit.ToolkitHelpers;
import java.util.Vector;

public class NeighborCharacter extends PlayerCharacter {
   protected NeighborProfile mNeighbor;

   public NeighborCharacter(IsometricScene scene) {
      this(scene, 7);
   }

   protected NeighborCharacter(IsometricScene scene, int type) {
      super(scene, type);
   }

   public void init(float x, float y, PlayerItem item, NeighborProfile profile, int character) {
      this.mNeighbor = profile;
      AvatarDescription avatar = profile.getAvatar(character);
      this.init(x, y, item, avatar);
   }

   @Override
   public WeaponItem getWeapon() {
      return this.mAvatar.getWeapon();
   }

   @Override
   public void remove() {
      super.remove();
      HUD.getInGame().removeAcceptHelp(this.mNeighbor);
      this.mScene.setUpdateNeighbors();
   }

   @Override
   public int logicUpdate(int delta) {
      int result = super.logicUpdate(delta);
      if (result != 0) {
         return result;
      }

      if (!GameEngineUtils.isAtNeigborsPlace()) {
         Vector commands = this.mNeighbor.getCommands();
         if (!this.hasActions() && commands.isEmpty()) {
            return 1;
         }

         if (!GameEngineUtils.isAtNeigborsPlace() && !this.hasActions()) {
            int index = 0;

            while (index < commands.size()) {
               CommandBase command = (CommandBase)commands.elementAt(index);
               if (command.isAccepted() && !command.isExecuted()) {
                  command.execute(this);
                  ToolkitHelpers.fastRemove(index, commands);
               } else {
                  index++;
               }
            }
         }
      }

      return 0;
   }

   public boolean isNPC() {
      return this.mNeighbor.getType() == 2514;
   }

   @Override
   public boolean isPlayer() {
      return false;
   }

   public NeighborProfile getProfile() {
      return this.mNeighbor;
   }

   @Override
   public void addFloater(String text) {
   }
}
