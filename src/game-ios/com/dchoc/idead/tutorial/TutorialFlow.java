package com.dchoc.idead.tutorial;

import com.dchoc.hud.HUD;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.characters.ZombieCharacter;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricTile;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.missions.MissionObjective;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.tracking.TutorialTrackingEvent;
import com.dchoc.idead.tuner.Tuner;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public class TutorialFlow {
   public static final String[] TUTORIAL_STEPS = new String[]{
      "Talk_to_rob", "Pick_Shovel", "Kill_Zombies", "Get_Collectibles", "Get_Combo", "Get_Weapon", "Give_Weapon", "Tutorial_Completed"
   };
   public static final int[] ARROW_ANIMATIONS_IDS = new int[]{70555, 70559, 70563, 70561};
   private static int smTutorialFlagCenterCamera = 1;
   private static int smState;
   private static IsometricScene smScene;
   private static IsometricObject smSpotlightObject;
   private static Mission smLastMission;
   private static SpriteObject[] smArrowAnimation;
   private static SpriteObject smSpotlightAnimation;
   private static TutorialTrackingEvent smEvent;

   public static void init(IsometricScene scene) {
      int profile = DeviceWrapper.getGfxProfile();
      smState = -1;
      smSpotlightObject = null;
      smScene = scene;
      smLastMission = MissionManager.getMission(1);
      if (smLastMission != null && smLastMission.isCompleted()) {
         PlayerProfile.setTutorialComplete(true);
      }

      smArrowAnimation = AnimationsManager.loadShared(ARROW_ANIMATIONS_IDS, profile);
      smSpotlightAnimation = AnimationsManager.loadShared(67307, profile);
      smEvent = new TutorialTrackingEvent(0);
   }

   public static boolean isCompleted() {
      return smLastMission != null && smLastMission.isCompleted();
   }

   public static int logicUpdate(int delta) {
      return 0;
   }

   public static void tutorialMissionActivated(Mission mission) {
      if (mission.getID() == 2287) {
         changeState(0);
      } else if (mission.getID() == 2289) {
         changeState(2);
      } else if (mission.getID() == 1) {
         changeState(5);
      }
   }

   public static void changeState(int state) {
      String text = null;
      IsometricTile tile = null;
      IsometricObject spotlightCandidate = null;
      switch (state) {
         case 0:
            IsometricObject rob = smScene.findObject(3, 843);
            if (rob != null) {
               ZombieCharacter z840 = (ZombieCharacter)smScene.findObject(2, 840);
               if (z840 != null) {
                  z840.turnTo(rob.getX(), rob.getY());
               }

               ZombieCharacter z841 = (ZombieCharacter)smScene.findObject(2, 841);
               if (z841 != null) {
                  z841.turnTo(rob.getX(), rob.getY());
               }

               ZombieCharacter z842 = (ZombieCharacter)smScene.findObject(2, 842);
               if (z842 != null) {
                  z842.turnTo(rob.getX(), rob.getY());
               }
            }

            tile = smScene.getTile(28, 24);
            HUD.getMissions().setVisible(false);
            WindowManager.openWindow((byte)0);
            break;
         case 1:
            text = Toolkit.getText(1239);
            spotlightCandidate = smScene.findObject(3, 838);
            break;
         case 2:
            text = Toolkit.getText(1240);
            spotlightCandidate = smScene.findObject(3, 843);
            tile = smScene.getTile(29, 24);
            break;
         case 3:
            text = Toolkit.getText(1241);
            break;
         case 4:
            text = Toolkit.getText(10);
            break;
         case 5:
            text = Toolkit.getText(1242);
            break;
         case 6:
            text = Toolkit.getText(1455);
      }

      if (tile != null) {
         smScene.centerToTile(tile, Tuner.getInitialZoom());
      }

      if (spotlightCandidate != null) {
         smSpotlightObject = spotlightCandidate;
      }

      CollisionBox box = smSpotlightAnimation.getCollisionBox(state);
      if (text != null && box != null && smSpotlightObject != null) {
         float screenX = (smSpotlightObject.getScreenX() - smScene.getCamera().getX()) * Tuner.getInitialZoom();
         int floorX = (int)Math.floor(screenX);
         float screenY = (smSpotlightObject.getScreenY() - smScene.getCamera().getY()) * Tuner.getInitialZoom();
         int floorY = (int)Math.floor(screenY);
         int dialogY = state == 2 ? Toolkit.getScreenHeight() : floorY;
         short monologId = (short)(state == 6 ? 2874 : 807);
         WindowManager.openNPCMonolog(text, monologId, null, box.getX() + floorX, box.getY() + dialogY);
      }

      if (smState != -1) {
         smEvent.trackCompleted();
      }

      if (state != -1) {
         smEvent.setStep(state);
         smEvent.trackStarted();
      }

      smState = state;
   }

   public static void tutorialMissionCompleted(Mission mission) {
      if (mission.getID() == 2289) {
         changeState(3);
      } else if (mission.getID() == 1) {
         changeState(6);
         HUD.getMissions().setVisible(true);
      }

      if (smLastMission != null && smLastMission.isCompleted()) {
         PlayerProfile.setTutorialComplete(true);
      }
   }

   public static void tutorialObjectiveCompleted(MissionObjective objective) {
      if (objective.getID() == 2481 && smState != 4) {
         changeState(4);
      }
   }

   public static void drawSpotlight() {
      if (smSpotlightObject != null) {
         float screenX = (smSpotlightObject.getScreenX() - smScene.getCamera().getX()) * Tuner.getInitialZoom();
         int floorX = (int)Math.floor(screenX);
         float screenY = (smSpotlightObject.getScreenY() - smScene.getCamera().getY()) * Tuner.getInitialZoom();
         int floorY = (int)Math.floor(screenY);
         smSpotlightAnimation.draw(floorX, floorY);
      }
   }

   public static void drawArrow(int x, int y, int index) {
      smArrowAnimation[index].setElapsedTime(AnimationsManager.getTimer());
      smArrowAnimation[index].draw(x, y);
   }

   public static boolean isCollectingRewards() {
      return smState >= 2 && smState <= 4;
   }

   public static int getState() {
      return smState;
   }

   public static boolean isCenterCameraActive() {
      if (smLastMission != null && smLastMission.isCompleted()) {
         IsometricScene scene = GameEngine.getInstance().getScene();
         Mission mission = MissionManager.getMission(2426);
         return !mission.isCompleted() && scene.getTouchIdleTimer() > 1999 && scene.getOutsideViewpointTimer() > 4999 && PlayerProfile.getLevel() > 3;
      } else {
         return false;
      }
   }
}
