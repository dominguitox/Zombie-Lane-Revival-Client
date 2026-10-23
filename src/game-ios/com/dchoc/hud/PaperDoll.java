package com.dchoc.hud;

import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.items.AvatarItem;
import com.dchoc.idead.items.WeaponItem;
import com.dchoc.idead.player.AvatarDescription;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import java.util.Vector;

public class PaperDoll {
   public static final int GENDER_MALE = 0;
   public static final int GENDER_FEMALE = 1;
   public static final int GENDER_DOG = 2;
   private static SpriteObject[] smCollisionPolygon;
   private static SpriteObject[] smCollisionPloygonMirrored;
   private static SpriteObject[] smBody;
   private static SpriteObject[] smSparkle;
   private static SpriteObject[] smHands;
   private static SpriteObject[] smMouth;
   private static SpriteObject[] smEyes;
   private static AvatarDescription smCurrentAvatar;
   private static int smCurrentGender;

   public static void init() {
      smCollisionPolygon = new SpriteObject[]{AnimationsManager.loadShared(67039), AnimationsManager.loadShared(68797), AnimationsManager.loadShared(69793)};
      smCollisionPloygonMirrored = new SpriteObject[]{
         AnimationsManager.loadShared(71299), AnimationsManager.loadShared(71297), AnimationsManager.loadShared(71295)
      };
      smBody = new SpriteObject[]{AnimationsManager.loadShared(66047), AnimationsManager.loadShared(68895), AnimationsManager.loadShared(69827)};
      smSparkle = new SpriteObject[]{AnimationsManager.loadShared(66055), AnimationsManager.loadShared(68781), null};
      smHands = new SpriteObject[]{AnimationsManager.loadShared(66043), AnimationsManager.loadShared(68869), null};
      smMouth = new SpriteObject[]{AnimationsManager.loadShared(66045), AnimationsManager.loadShared(68781), null};
      smEyes = new SpriteObject[]{AnimationsManager.loadShared(66061), AnimationsManager.loadShared(68805), null};

      for (int i = 0; i < smSparkle.length; i++) {
         if (smSparkle[i] != null) {
            smSparkle[i].setAnimation(0, -1, false);
         }
      }

      for (int i = 0; i < smEyes.length; i++) {
         if (smEyes[i] != null) {
            smEyes[i].setAnimation(0, -1, false);
         }
      }
   }

   public static float[] getCollisionPolygon(boolean mirrored) {
      SpriteObject[] polygons = mirrored ? smCollisionPloygonMirrored : smCollisionPolygon;
      return polygons[smCurrentGender].getCollisionPolygon(0);
   }

   public static void setAvatar(AvatarDescription avatar) {
      smCurrentAvatar = avatar;
      int gender = avatar.getGender();
      int current;
      if (gender == 43) {
         current = 2;
      } else if (gender == 1922) {
         current = 1;
      } else {
         if (gender != 1921) {
            return;
         }

         current = 0;
      }

      smCurrentGender = current;
   }

   public static CollisionBox getBodyBox(int gender) {
      return smBody[gender] != null && smBody[gender].getCollisionBox(0) != null ? smBody[gender].getCollisionBox(0) : null;
   }

   public static void drawPaperdollByLegs(int x, int y, AvatarDescription avatar, boolean mirrored) {
      drawPaperdollByLegs(x, y, avatar, mirrored, 1.0F);
   }

   public static void drawPaperdollByLegs(int x, int y, AvatarDescription avatar, boolean mirrored, float scale) {
      if (avatar != null) {
         int gender = avatar.getGender();
         CollisionBox box = null;
         if (gender == 43) {
            box = getBodyBox(2);
         } else if (gender == 1922) {
            box = getBodyBox(1);
         } else if (gender == 1921) {
            box = getBodyBox(0);
         }

         float halfWidth = box.getWidth() * scale * 0.5F;
         int legsX;
         int legsY;
         if (mirrored) {
            int boxX = box.getX();
            int boxHeight = box.getHeight();
            int boxY = box.getY();
            legsX = (int)(x + halfWidth + boxX * scale);
            legsY = (int)(y - boxHeight * scale - boxY * scale);
         } else {
            int boxX = box.getX();
            int boxHeight = box.getHeight();
            int boxY = box.getY();
            legsX = (int)(x - halfWidth - boxX * scale);
            legsY = (int)(y - boxHeight * scale - boxY * scale);
         }

         OGL.pushParameters();
         OGL.setScale(scale, scale);
         drawPaperdoll(legsX, legsY, avatar, mirrored, null);
         OGL.popParameters();
      }
   }

   public static void drawPaperdoll(int x, int y, AvatarDescription avatar, boolean mirrored) {
      drawPaperdoll(x, y, avatar, mirrored, null);
   }

   public static void drawPaperdoll(int x, int y, AvatarDescription avatar, boolean mirrored, SpriteObject weapon) {
      if (avatar != null) {
         int flags = mirrored ? 1 : 0;
         smCurrentAvatar = avatar;
         int gender = avatar.getGender();
         if (gender == 43) {
            smCurrentGender = 2;
         } else if (gender == 1922) {
            smCurrentGender = 1;
         } else if (gender == 1921) {
            smCurrentGender = 0;
         }

         AvatarItem skin = avatar.getItem(2123);
         if (skin != null && skin.getColorModification() != -8355712) {
            OGL.pushParameters();
            OGL.setColorModification(skin.getColorModification());
         }

         if (smBody[smCurrentGender] != null) {
            smBody[smCurrentGender].draw(x, y, flags);
         }

         if (skin != null && skin.getColorModification() != -8355712) {
            OGL.popParameters();
         }

         if (smSparkle[smCurrentGender] != null) {
            smSparkle[smCurrentGender].setElapsedTime(AnimationsManager.getTimer());
            smSparkle[smCurrentGender].draw(x, y, flags);
         }

         if (smMouth[smCurrentGender] != null) {
            smMouth[smCurrentGender].setElapsedTime(AnimationsManager.getTimer());
            smMouth[smCurrentGender].draw(x, y, flags);
         }

         if (smEyes[smCurrentGender] != null) {
            smEyes[smCurrentGender].setElapsedTime(AnimationsManager.getTimer());
            smEyes[smCurrentGender].draw(x, y, flags);
         }

         Vector items = avatar.getItems();
         boolean fullBody = false;
         AvatarItem fullBodyItem = null;
         if (0 < items.size()) {
            int i = 0;

            do {
               AvatarItem item = (AvatarItem)items.elementAt(i);
               if (item.getGender() == avatar.getGender() && item.getSubType() == 2116 && item.getID() != 28 && item.getID() != 37) {
                  SpriteObject sprite = AnimationsManager.loadShared(item.getIconAnimationID());
                  if (sprite != null) {
                     sprite.draw(x, y, flags);
                  }

                  fullBody = true;
                  fullBodyItem = item;
                  break;
               }
            } while (++i < items.size());
         }

         if (!fullBody) {
            for (int order = 0; order < AvatarItem.DRAWING_ORDER.length; order++) {
               if (0 < items.size()) {
                  int i = 0;

                  while (true) {
                     AvatarItem item = (AvatarItem)items.elementAt(i);
                     if (item.getGender() == avatar.getGender() && item.getSubType() == AvatarItem.DRAWING_ORDER[order]) {
                        SpriteObject sprite = AnimationsManager.loadShared(item.getIconAnimationID());
                        if (sprite != null) {
                           sprite.draw(x, y, flags);
                        }
                     }

                     if (++i >= items.size()) {
                        break;
                     }
                  }
               }
            }
         }

         if (skin != null && skin.getColorModification() != -8355712) {
            OGL.pushParameters();
            OGL.setColorModification(skin.getColorModification());
         }

         if (!fullBody
            || fullBodyItem != null
               && (fullBodyItem.getID() == 2210 || fullBodyItem.getID() == 2213 || fullBodyItem.getID() == 3300 || fullBodyItem.getID() == 3301)) {
            drawHands(avatar, x, y, mirrored);
         }

         if (skin != null && skin.getColorModification() != -8355712) {
            OGL.popParameters();
         }

         if (smCurrentGender != 2) {
            if (weapon != null) {
               weapon.draw(x, y, flags);
               return;
            }

            WeaponItem item = avatar.getWeapon();
            if (item != null) {
               SpriteObject[] animations = item.getPaperdollAnimation();
               if (smCurrentGender < animations.length && animations[smCurrentGender] != null) {
                  animations[smCurrentGender].setElapsedTime(AnimationsManager.getTimer());
                  animations[smCurrentGender].draw(x, y, flags);
               }
            }
         }
      }
   }

   private static void drawHands(AvatarDescription avatar, int x, int y, boolean mirrored) {
      if (smCurrentGender != 2) {
         if (smHands[smCurrentGender] != null) {
            smHands[smCurrentGender].setAnimationFrame(1);
         }

         WeaponItem weapon = smCurrentAvatar.getWeapon();
         if (weapon != null
            && (
               weapon.getID() == 49
                  || weapon.getID() == 2070
                  || weapon.getID() == 1962
                  || weapon.getID() == 2114
                  || weapon.getID() == 152
                  || weapon.getID() == 1959
                  || weapon.getID() == 1964
            )
            && smHands[smCurrentGender] != null) {
            smHands[smCurrentGender].setAnimationFrame(0);
         }

         AvatarItem skin = avatar.getItem(2123);
         if (skin != null && skin.getColorModification() != -8355712) {
            OGL.pushParameters();
            OGL.setColorModification(skin.getColorModification());
         }

         if (smHands[smCurrentGender] != null) {
            smHands[smCurrentGender].draw(x, y, mirrored ? 1 : 0);
         }

         if (skin != null && skin.getColorModification() != -8355712) {
            OGL.popParameters();
         }
      }
   }
}
