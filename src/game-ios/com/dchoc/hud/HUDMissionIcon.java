package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.missions.Mission;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.Animation;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import java.util.Vector;

public class HUDMissionIcon extends HUDObject {
   public static final String SHARP = "#";
   private Mission mMission;
   private SpriteObject mAnimationIntro;
   private HUDImage mNewLabel;
   private HUDImage mMissionIconIntro;
   private HUDImage mMissionIconAccepted;
   private HUDAutoTextField mSurvivalTip;
   private HUDOrangeLabel mLabel;
   private boolean mIsExpandable;
   private int mHighlightTimer;

   public HUDMissionIcon() {
      super((byte)22);
      CollisionBox box = AnimationsManager.loadShared(65643).getCollisionBox(0);
      this.mWidth = box.getWidth();
      this.mHeight = box.getHeight();
      this.mLabel = new HUDOrangeLabel(this, true);
      this.mLabel.setPosition(this.mWidth, (this.mHeight - this.mLabel.getHeight()) / 2);
   }

   public void setExpandable(boolean expandable) {
      this.mIsExpandable = expandable;
      this.mMission = null;
      this.mMissionIconAccepted = new HUDImage(74815);
      this.mMissionIconIntro = new HUDImage(74815);
      this.init();
   }

   public void setOptions(Mission mission) {
      this.mMission = mission;
      this.mMissionIconAccepted = new HUDImage(mission.getIconID());
      if (mission.getType() == 2399) {
         this.mMissionIconIntro = new HUDImage(67033);
         int index = MissionManager.getSurvivalTips().indexOf(mission);
         CollisionBox box = this.mMissionIconIntro.getCollisionBox(0);
         if (box != null) {
            this.mSurvivalTip = new HUDAutoTextField(box);
            this.mSurvivalTip.setText("#" + (index + 1), 6);
            int tipWidth = this.mSurvivalTip.getWidth();
            this.mSurvivalTip.setPosition(this.mPosX + (this.mWidth - tipWidth) / 2, this.mSurvivalTip.getY() + this.mHeight / 2);
            this.mSurvivalTip.setCentered(true, true);
            this.mSurvivalTip.setParent(this);
         }
      } else {
         this.mMissionIconIntro = new HUDImage(mission.getIntroIconID());
      }

      this.init();
   }

   @Override
   public void callback(int id) {
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         if (!this.mAnimationIntro.isFinishedAnimation() && this.mParent != null && this.mParent.getType() == 3) {
            Animation data = this.mAnimationIntro.getCurrentAnimationData();
            int time = this.mAnimationIntro.getElapsedTime();
            int color = data.getTimelineValue(0, time, true);
            int scaleX = data.getTimelineValue(3, time, true);
            int scaleY = data.getTimelineValue(4, time, true);
            OGL.pushParameters();
            OGL.getParameters().setColorModification(color);
            OGL.getParameters().setScaleInt(scaleX, scaleY);
         } else if (this.mIsHighlighted) {
            OGL.pushParameters();
            OGL.setColorModification(-1151311776);
         }

         if (!this.mIsExpandable) {
            HUDImage image = this.mMission.getState() == 2 ? this.mMissionIconAccepted : this.mMissionIconIntro;
            if (image != null) {
               image.draw();
            }

            if (this.mMission.getType() == 2399 && this.mSurvivalTip != null) {
               this.mSurvivalTip.draw();
            }

            if (this.mMission.isNew()) {
               this.mNewLabel.draw();
            }
         } else {
            this.mMissionIconAccepted.draw();
         }

         this.mLabel.draw();
         if (!this.mAnimationIntro.isFinishedAnimation() && this.mParent != null && this.mParent.getType() == 3) {
            OGL.popParameters();
         } else if (this.mIsHighlighted) {
            OGL.popParameters();
         }

         this.drawDebug();
      }
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.isVisible()) {
         boolean expandable = this.mIsExpandable;
         byte parentType = this.mParent.getType();
         if (!expandable) {
            if (parentType != 3) {
               this.setModeForLabel(this.mMission);
            }
         } else if (parentType != 3) {
            Vector icons = ((HUDMissions)this.mParent).getMissions();
            int i = 2;

            while (i < icons.size()) {
               HUDMissionIcon icon = (HUDMissionIcon)icons.elementAt(i);
               boolean set = this.setModeForLabel(icon.getMission());
               i++;
               if (set) {
                  break;
               }
            }
         }

         this.mMissionIconAccepted.logicUpdate(delta);
         this.mMissionIconIntro.logicUpdate(delta);
         this.mAnimationIntro.logicUpdate(delta);
         this.mNewLabel.logicUpdate(delta);
         this.mLabel.logicUpdate(delta);
         if (this.mIsHighlighted) {
            this.mHighlightTimer += delta;
            if (999 < this.mHighlightTimer) {
               this.mIsHighlighted = false;
            }
         }
      }
   }

   @Override
   public boolean isInside(TouchEvent event) {
      return super.isInside(event) ? true : this.mLabel.isInside(event.getX(), event.getY());
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.mIsVisible && !event.isConsumed()) {
         int type = event.getType();
         if (type != 2 && type != 3) {
            if (type == 1) {
               if (this.mIsHighlighted && !this.isInside(event)) {
                  this.mIsHighlighted = false;
               }
            } else if (type == 0 && this.isInside(event)) {
               this.mHighlightTimer = 0;
               this.mIsHighlighted = true;
               event.setConsumed(true);
            }
         } else if (this.mIsHighlighted) {
            this.mIsHighlighted = false;
            if (!this.isInside(event)) {
               return;
            }

            this.doAction();
            event.setConsumed(true);
         }
      }
   }

   public Mission getMission() {
      return this.mMission;
   }

   public void doAction() {
      if (!this.mIsExpandable) {
         if (this.mMission.getType() == 2399) {
            HUD.getMissions().removeMission(this.mMission);
            HUD.openMission(this.mMission);
         }

         if (this.mMission.getState() == 2) {
            HUD.openMission(this.mMission);
         } else {
            this.mMission.executeIntroEvent();
         }

         this.mMission.resetNew();
         this.mMission.resetObjectiveCompleted();
      } else {
         WindowManager.openWindow((byte)26);
      }

      this.mLabel.setVisible(false);
   }

   @Override
   public void freeImages() {
      this.mNewLabel.freeImages();
      if (this.mMissionIconIntro != null) {
         this.mMissionIconIntro.freeImages();
      }

      this.mMissionIconAccepted.freeImages();
      if (this.mSurvivalTip != null) {
         this.mSurvivalTip.freeImages();
      }

      this.mLabel.freeImages();
   }

   public void init() {
      this.mMissionIconAccepted.setPosition(this.mPosX + this.mWidth / 2, this.mPosY + this.mHeight / 2);
      this.mMissionIconAccepted.setParent(this);
      this.mMissionIconIntro.setPosition(this.mPosX + this.mWidth / 2, this.mPosY + this.mHeight / 2);
      this.mMissionIconIntro.setAnimation(0, -1, false);
      this.mMissionIconIntro.setParent(this);
      this.mAnimationIntro = AnimationsManager.load(65635);
      this.mAnimationIntro.setAnimation(0, 1, false);
      this.mNewLabel = new HUDImage(65703);
      this.mNewLabel.setPosition(this.mWidth, 0);
      this.mNewLabel.setAnimation(0, -1, false);
      this.mNewLabel.setParent(this);
   }

   public boolean setModeForLabel(Mission mission) {
      if (mission == null) {
         return false;
      }

      if (mission.getType() == 2399) {
         this.mLabel.setMode(4);
         return true;
      }

      if (mission.isNew()) {
         this.mLabel.setMode(0);
         return true;
      }

      if (!mission.isObjectiveCompleted()) {
         return false;
      }

      this.mLabel.setMode(1);
      return true;
   }
}
