package com.dchoc.windows;

import com.dchoc.DCiDead;
import com.dchoc.hud.HUD;
import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDBubble;
import com.dchoc.hud.HUDButton;
import com.dchoc.hud.HUDFoldable;
import com.dchoc.hud.HUDImage;
import com.dchoc.hud.HUDProgressbarEnergy;
import com.dchoc.hud.WindowManager;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.Time;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.events.EventManager;
import com.dchoc.idead.events.TimedEvent;
import com.dchoc.idead.missions.MissionManager;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.idead.social.SocialFacebook;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.iphonewrappers.SettingsWrapper;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.ResourceIDs;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import javax.microedition.io.ConnectionNotFoundException;
import zlport.ios.Ios;

public class WindowNpcDialog extends Window implements ResourceIDs {
   public static final int MODE_BRAINS = 4;
   public static final int MODE_CENTER_CAMERA = 9;
   public static final int MODE_CONNECTIVITY_REMINDER = 7;
   public static final int MODE_CRAFTING_ADVICE = 10;
   public static final int MODE_FREE_HOTDOG = 5;
   public static final int MODE_MORE_BRAINS = 0;
   public static final int MODE_NIEGHBOR_INFO = 11;
   public static final int MODE_NONE = -1;
   public static final int MODE_OFFLINE = 3;
   public static final int MODE_OUT_OF_ENERGY_SHOW_HUD = 8;
   public static final int MODE_PLEASE_RATE_US = 1;
   public static final int MODE_SCROLL_ZOOM = 6;
   public static final int MODE_SHOW_NEIGHBOR_ICON = 13;
   public static final int MODE_WIFI_UNAVAILABLE = 12;
   public static final int MODE_ZOMBIES_AND_FRIENDS = 2;
   public static final int SPEECH_BUBBLE_PORTRAIT_MARGIN = -10;
   private static final String RMS_RATE_US = "RMS_RATE_US";
   private static final boolean[] EXTRA_BUTTON = new boolean[]{false, true, true, false, false, false, false, false, false, false, false, false, false, false};
   private static final byte[] TEXT_ALIGNMENT_HORIZONTAL = new byte[]{2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
   private static final short[] TITLE = new short[]{2296, 4253, 2294, 2293, 2356, 1985, 2353, 2423, 1693, -1, -1, -1, 2749, 2754};
   private static final short[] DESCRIPTION = new short[]{2300, 4254, 2298, 2297, 2357, 1986, 2354, 2424, 32, 2355, 1392, 4096, 2750, 2755};
   private static final short[] BUTTON_TEXT = new short[]{-1, 4255, 2412, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
   private static final int[] BUTTON_TEXT_FONT = new int[]{-1, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
   private static final int[] PORTRAIT = new int[]{66369, 66377, 66457, 66457, 66369, 66377, 66377, 66457, 66377, 66377, 66377, 77039, 77785, 66377};
   private static final int[] ICON = new int[]{66713, 79701, -1, -1, -1, 66307, -1, -1, -1, -1, -1, -1, -1, -1};
   private static final byte[] ICON_ALIGNMENT = new byte[]{2, 1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1};
   private static HUDBubble mBubble;
   private static int mBubbleMargin;
   private static HUDAutoTextField mTitle;
   private static HUDAutoTextField mDescription;
   private static HUDAutoTextField mButtonText;
   private static HUDImage mPortrait;
   private static HUDImage mIcon;
   private static SpriteObject[] mImageIcon;
   private static HUDProgressbarEnergy mEnergyBar;
   private static WindowTooltip mTooltip;
   private static byte mMode;
   private static boolean smMirror;
   private static boolean smEnableSpotlight;
   private static int smPortraitMarginX;
   private static SpriteObject smSpotlightAnimation;
   private static int smSpotlightX;
   private static int smSpotlightY;
   private static boolean smRateUsTriggerActive;
   private static int smRateUsTimeInterval;
   private static long smRateUsTimestamp;
   private static String smVersion;
   private static HUDButton mButtonAppstore;
   private static HUDButton mButtonFacebook;
   private static HUDButton mButtonRateUs;
   private static HUDButton mButtonNotNow;
   private static HUDButton mButtonLater;
   private static HUDButton mButtonGoBack;
   private static HUDButton mButtonRetry;
   private static HUDButton mButtonCancel;
   private static HUDButton mButtonPost;
   private static HUDButton mButtonInventory;
   private static HUDButton mButtonPlay;
   private static HUDButton mButtonOk;
   private static HUDButton mButtonOkLarge;
   private static HUDButton mButtonGifts;

   public WindowNpcDialog() {
      this.mBackground = null;
      this.mCollisions = AnimationsManager.loadShared(74813);
      this.initCollisions();
      mImageIcon = AnimationsManager.load(ICON);
      CollisionBox box = this.getCollisionBox(14);
      mPortrait = new HUDImage(-1);
      HUDImage portrait = mPortrait;
      portrait.setPositionY(box.getY());
      smPortraitMarginX = box.getX();
      mIcon = new HUDImage(-1);
      mButtonAppstore = new HUDButton(32001, this.getCollisionBox(7), 0);
      mButtonFacebook = new HUDButton(32002, this.getCollisionBox(7), 1);
      mButtonRateUs = new HUDButton(32001, this.getCollisionBox(7), 7);
      mButtonNotNow = new HUDButton(32004, this.getCollisionBox(4), 6);
      mButtonLater = new HUDButton(32004, this.getCollisionBox(4), 6);
      mButtonGoBack = new HUDButton(32005, this.getCollisionBox(5), 2);
      mButtonRetry = new HUDButton(32003, this.getCollisionBox(3), 6);
      mButtonCancel = new HUDButton(32006, this.getCollisionBox(3), 6);
      mButtonPost = new HUDButton(32008, this.getCollisionBox(6), 3);
      mButtonInventory = new HUDButton(32009, this.getCollisionBox(4), 2);
      mButtonPlay = new HUDButton(32007, this.getCollisionBox(6), 3);
      mButtonOk = new HUDButton(32010, this.getCollisionBox(4), 3);
      mButtonOkLarge = new HUDButton(32010, this.getCollisionBox(4), 2);
      mButtonGifts = new HUDButton(32011, this.getCollisionBox(6), 3);
      mButtonNotNow.setText(2359);
      mButtonLater.setText(4256);
      mButtonGoBack.setText(2361);
      mButtonRetry.setText(2360);
      mButtonCancel.setText(48);
      mButtonPost.setText(2358);
      mButtonInventory.setText(1987);
      mButtonPlay.setText(1929);
      mButtonOk.setText(54);
      mButtonOkLarge.setText(54);
      HUDButton gifts = mButtonGifts;
      gifts.setText("Open Gifts");

      for (int i = 0; i < (TITLE == null ? 0 : TITLE.length); i++) {
         Toolkit.getText(TITLE[i]);
         Toolkit.getText(DESCRIPTION[i]);
         Toolkit.getText(BUTTON_TEXT[i]);
      }

      mTitle = new HUDAutoTextField(this.getCollisionBox(8));
      mDescription = new HUDAutoTextField(this.getCollisionBox(9));
      mButtonText = new HUDAutoTextField(this.getCollisionBox(9));
      mTitle.setSplitUsingWidth(true);
      mDescription.setSplitUsingWidth(true);
      mButtonText.setSplitUsingWidth(true);
      mButtonText.setCentered(true, false);
      mBubble = new HUDBubble();
      mBubble.disablePipkaPosition();
      int x = this.getCollisionBox(9).getX();
      mBubbleMargin = x - this.getCollisionBox(1).getX();
      this.mObjects.addObject(mBubble);
      this.mObjects.addObject(mTitle);
      this.mObjects.addObject(mDescription);
      this.mObjects.addObject(mButtonText);
      this.mObjects.addObject(mIcon);
      this.mObjects.addObject(mPortrait);
      this.mObjects.addObject(mButtonAppstore);
      this.mObjects.addObject(mButtonFacebook);
      this.mObjects.addObject(mButtonRateUs);
      this.mObjects.addObject(mButtonRetry);
      this.mObjects.addObject(mButtonNotNow);
      this.mObjects.addObject(mButtonLater);
      this.mObjects.addObject(mButtonGoBack);
      this.mObjects.addObject(mButtonCancel);
      this.mObjects.addObject(mButtonPost);
      this.mObjects.addObject(mButtonInventory);
      this.mObjects.addObject(mButtonPlay);
      this.mObjects.addObject(mButtonOk);
      this.mObjects.addObject(mButtonOkLarge);
      this.mObjects.addObject(mButtonGifts);
      smSpotlightAnimation = AnimationsManager.loadShared(67307);
      mTooltip = new WindowTooltip();
      mTooltip.enableFadeBackground(false);
      mTooltip.setAsTooltipBubble();
      mEnergyBar = new HUDProgressbarEnergy(65557, 65569, 71585);
      mEnergyBar.setRefDimensions();
      mEnergyBar.enableForcedPosition(true);
      mMode = -1;
   }

   @Override
   public void logicUpdate(int delta) {
      super.logicUpdate(delta);
      if (mMode == 9) {
         mTooltip.logicUpdate(delta);
         if (this.isOpened() && mTooltip.isClosed()) {
            mTooltip.open();
         }
      } else if (mMode == 8) {
         int[] dimensions = mEnergyBar.getRefDimensions();
         SpriteObject background = mEnergyBar.getBackground();
         int width = dimensions[2];
         int height = dimensions[3];
         int pivotX = background.getPivotX();
         CollisionBox box = this.getCollisionBox(13);
         HUDProgressbarEnergy bar = mEnergyBar;
         bar.setForcedX(pivotX + this.getX() + mBubble.getX() + (mBubble.getWidth() - width) / 2);
         bar = mEnergyBar;
         bar.setForcedY(this.getY() + box.getY() + (box.getHeight() - height) / 2);
      }
   }

   @Override
   public void draw() {
      if (smEnableSpotlight) {
         smSpotlightAnimation.draw(smSpotlightX, smSpotlightY);
      }

      super.draw();
      if (mMode == 8) {
         mEnergyBar.draw();
      } else if (mMode == 9) {
         mTooltip.draw();
      }
   }

   public void closeTooltip() {
      if (mTooltip.isOpened()) {
         mTooltip.close();
      }
   }

   @Override
   public boolean isClosed() {
      if (this.mState == 1) {
         return mMode == 9 ? mTooltip.isClosed() : true;
      } else {
         return false;
      }
   }

   public boolean setMode(byte mode) {
      if (mode != -1 && this.isClosed()) {
         this.mObjects.setAllVisible(false);
         mBubble.setVisible(true);
         mMode = mode;
         smMirror = mode == 9;
         HUDBubble bubble = mBubble;
         bubble.setSizes(this.getCollisionBox(1));
         CollisionBox baseBox = this.getCollisionBox(1);
         CollisionBox bubbleBox = baseBox;
         int unsignedMode = Byte.toUnsignedInt(mode);
         if (unsignedMode < 12 && (1 << unsignedMode & 4065) != 0) {
            bubbleBox = this.getCollisionBox(2);
         }

         CollisionBox textBox;
         if (unsignedMode < 14 && (1 << unsignedMode & 8198) != 0) {
            textBox = this.getCollisionBox(10);
         } else {
            textBox = this.getCollisionBox(9);
         }

         int alignment = TEXT_ALIGNMENT_HORIZONTAL[mMode];
         if (alignment == 1 || alignment == 3) {
            mTitle.setCentered(true, true);
            mDescription.setCentered(true, false);
         } else if (alignment == 2) {
            mTitle.setCentered(false, true);
            mDescription.setCentered(false, false);
         }

         int buttonText = BUTTON_TEXT[mMode];
         int bubbleWidth = bubbleBox.getWidth();
         int baseWidth = this.getCollisionBox(1).getWidth();
         int offsetX = 0;
         int offsetY = 0;
         if (ICON[mMode] == -1) {
            if (mode == 8) {
               offsetY = this.getCollisionBox(13).getHeight();
            }
         } else {
            int iconAlignment = ICON_ALIGNMENT[mMode];
            if (iconAlignment == 0) {
               offsetY = mImageIcon[mMode].getHeight();
            } else if (iconAlignment == 2) {
               offsetX = mImageIcon[mMode].getWidth();
            }
         }

         mDescription.setOffsetX(offsetX);
         mDescription.setOffsetY(offsetY);
         if (buttonText != -1) {
            mButtonText.setOffsetX(offsetX);
         }

         int width = textBox.getWidth() - offsetX + bubbleWidth - baseWidth;
         mTitle.setWidth(width);
         mDescription.setWidth(width);
         mButtonText.setWidth(width);
         ImageFont font = DCiDead.getFont(2);
         int titleId = TITLE[mMode];
         if (titleId == -1) {
            offsetY -= font.getHeight();
         } else {
            mTitle.setText(titleId, font);
         }

         int description;
         if (mMode == 8) {
            description = PlayerProfile.getEnergyChargeTimeMax() == 60000 ? 2743 : DESCRIPTION[mMode];
         } else {
            description = DESCRIPTION[mMode];
         }

         mDescription.setText(description, 4);
         if (buttonText != -1) {
            mButtonText.setText(BUTTON_TEXT[mMode], BUTTON_TEXT_FONT[mMode]);
         }

         int textHeight = mDescription.getTextHeight();
         int delta;
         if (buttonText == -1) {
            delta = textHeight - textBox.getHeight();
         } else {
            int margin = mBubbleMargin;
            int buttonHeight = mButtonText.getTextHeight();
            delta = margin + textHeight + buttonHeight - textBox.getHeight();
         }

         if (mode == 8) {
            delta += offsetY;
         }

         if (ICON[mMode] != -1 && Byte.toUnsignedInt(ICON_ALIGNMENT[mMode]) < 2) {
            delta += mImageIcon[mMode].getHeight();
         }

         bubble = mBubble;
         bubble.setWidth(bubbleBox.getWidth());
         mBubble.setHeight(bubbleBox.getHeight() + delta);
         this.setImages(bubbleBox, delta);
         int portraitWidth = mPortrait.getWidth();
         int translation;
         if (!smMirror) {
            int margin = smPortraitMarginX;
            int pipkaWidth = mBubble.getPipkaWidth();
            translation = portraitWidth + margin + pipkaWidth - 10 - mBubble.getX();
         } else {
            int screenWidth = Toolkit.getScreenWidth();
            int margin = smPortraitMarginX;
            int pipkaWidth = mBubble.getPipkaWidth();
            int bubbleX = mBubble.getX();
            int currentWidth = mBubble.getWidth();
            translation = 10 - portraitWidth + screenWidth - margin - pipkaWidth - bubbleX - currentWidth;
         }

         int bubbleX = mBubble.getX();
         int currentWidth = mBubble.getWidth();
         int screenWidth = Toolkit.getScreenWidth();
         if (screenWidth <= bubbleX + translation + currentWidth) {
            screenWidth = Toolkit.getScreenWidth();
            currentWidth = mBubble.getWidth();
            bubbleX = mBubble.getX();
            mPortrait.setPositionX(5);
            translation = screenWidth - 5 - currentWidth - bubbleX;
         }

         bubble = mBubble;
         bubble.setPositionX(bubble.getX() + translation);
         mBubble.setPipkaPosType(smMirror ? 3 : 0);
         mTitle.setOffsetX(translation);
         HUDAutoTextField text = mDescription;
         text.setOffsetX(text.getOffsetX() + translation);
         if (buttonText != -1) {
            text = mButtonText;
            text.setOffsetX(text.getOffsetX() + translation);
         }

         if (ICON[mMode] != -1) {
            HUDImage icon = mIcon;
            icon.setPositionX(icon.getX() + translation);
         }

         this.setSpotlight();
         this.setText(buttonText != -1);
         this.resetButtonPositions(mode);
         this.setButtons(mBubble, delta);
         if (buttonText != -1) {
            CollisionBox box = this.getCollisionBox(7);
            font = mButtonText.getFont();
            int fontHeight = font.getHeight();
            text = mButtonText;
            int x = textBox.getX();
            int y = box.getY();
            textHeight = mButtonText.getTextHeight();
            text.setPosition(x, delta - (fontHeight >> 1) + y - textHeight);
         }

         if (mode == 9) {
            SpriteObject sprite = AnimationsManager.loadShared(65597);
            CollisionBox box = sprite.getCollisionBox(10);
            int x = box.getX();
            int screenHeight = Toolkit.getScreenHeight();
            int y = box.getY();
            mTooltip.initNewTooltip(null);
            mTooltip.setTooltip(24, x, screenHeight + y, 0, 0, null);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void callback(int id) {
      if (id == 32001) {
         WindowManager.closeWindow();

         try {
            SettingsWrapper.openAppStore();
         } catch (ConnectionNotFoundException e) {
            throw new IllegalStateException(e);
         }
      } else if (id == 32002) {
         WindowSocial social = (WindowSocial)WindowManager.getWindow((byte)18);
         social.getFacebookFriends();
         WindowManager.closeWindow();
      } else if (id == 32004 || id == 32005 || id == 32006 || id == 32007 || id == 32010) {
         WindowManager.closeWindow();
         if (mMode == 3) {
            WindowManager.openWindow((byte)18);
         }

         if (id == 32004 && mMode == 2) {
            MissionManager.updateCounter(2887, 80, 1);
         }

         if (id == 32004 && mMode == 1 && !smRateUsTriggerActive) {
            smRateUsTriggerActive = true;
            smRateUsTimeInterval = 432000;
            smRateUsTimestamp = Time.getTimestamp();
         }
      } else if (id == 32008) {
         WindowManager.closeWindow();
         WindowManager.openWindow((byte)18);
      } else {
         if (id == 32009) {
            WindowManager.closeWindow();
            WindowManager.openWindow((byte)20);
         } else {
            if (id != 32011) {
               return;
            }

            WindowManager.closeWindow();
            WindowManager.openWindow((byte)18);
         }

         HUDFoldable foldable = HUD.getInGame().getFoldable();
         if (foldable.getState() == 0 || foldable.getState() == 3) {
            foldable.changeState(1);
         }
      }
   }

   public boolean checkConnectionDialog() {
      if (MissionManager.getMission(2875).isCompleted()) {
         return false;
      } else {
         WindowSocial social = (WindowSocial)WindowManager.getWindow((byte)18);
         SocialFacebook facebook = GameEngine.getInstance().getFacebook();
         if (!social.isConnectingFacebook() && !facebook.isConnected()) {
            WindowManager.closeWindow();
            MissionManager.updateCounter(2885, 80, 1);
            WindowManager.openNPCDialog((byte)2);
            return true;
         } else {
            return false;
         }
      }
   }

   public int getMode() {
      return mMode;
   }

   public static void updateRateUsTrigger() {
      if (smRateUsTriggerActive) {
         long elapsed = Time.getTimestampDelta(smRateUsTimestamp);
         if (elapsed >= smRateUsTimeInterval * 1000 && PlayerProfile.getEnergy() == 0) {
            smRateUsTriggerActive = false;
            TimedEvent event = new TimedEvent(5000, 2974, null);
            EventManager.addEvent(event);
            DChocByteArray buffer = DChocByteArray.createByteArrayForWriting(1024);
            buffer.writeUTF(smVersion);
            buffer.writeBoolean(smRateUsTriggerActive);
            buffer.writeInt(smRateUsTimeInterval);
            ToolkitHelpers.writeLong(smRateUsTimestamp, buffer);
            Toolkit.writeRecordByteArray("RMS_RATE_US", buffer, 0);
         }
      } else if (MissionManager.getMission(2307).isCompleted()) {
         if (!SettingsWrapper.getVersion().equals(smVersion)) {
            if (!smRateUsTriggerActive) {
               smRateUsTriggerActive = true;
               smRateUsTimeInterval = 86400;
               smRateUsTimestamp = Time.getTimestamp();
            }

            smVersion = SettingsWrapper.getVersion();
            DChocByteArray buffer = DChocByteArray.createByteArrayForWriting(1024);
            buffer.writeUTF(smVersion);
            buffer.writeBoolean(smRateUsTriggerActive);
            buffer.writeInt(smRateUsTimeInterval);
            ToolkitHelpers.writeLong(smRateUsTimestamp, buffer);
            Toolkit.writeRecordByteArray("RMS_RATE_US", buffer, 0);
         }
      }
   }

   public static void load() {
      String record = "RMS_RATE_US";

      try {
         DChocByteArray buffer = Toolkit.readRecordByteArray(record);
         if (buffer != null) {
            smVersion = buffer.readUTF();
            smRateUsTriggerActive = buffer.readBoolean();
            smRateUsTimeInterval = buffer.readInt();
            smRateUsTimestamp = ToolkitHelpers.readLong(buffer);
         } else {
            smVersion = SettingsWrapper.getVersion();
            smRateUsTimestamp = 0L;
            smRateUsTriggerActive = false;
            smRateUsTimeInterval = 0;
            buffer = DChocByteArray.createByteArrayForWriting(1024);
            buffer.writeUTF(smVersion);
            buffer.writeBoolean(smRateUsTriggerActive);
            buffer.writeInt(smRateUsTimeInterval);
            ToolkitHelpers.writeLong(smRateUsTimestamp, buffer);
            Toolkit.writeRecordByteArray(record, buffer, 0);
         }
      } catch (Exception exception) {
         smVersion = SettingsWrapper.getVersion();
         smRateUsTimestamp = 0L;
         smRateUsTriggerActive = false;
         smRateUsTimeInterval = 0;
         DChocByteArray buffer = DChocByteArray.createByteArrayForWriting(1024);
         buffer.writeUTF(smVersion);
         buffer.writeBoolean(smRateUsTriggerActive);
         buffer.writeInt(smRateUsTimeInterval);
         ToolkitHelpers.writeLong(smRateUsTimestamp, buffer);
         Toolkit.writeRecordByteArray(record, buffer, 0);
         Ios.platform().logNativeMessage(exception.toString());
      }
   }

   public void setSpotlight() {
      this.enableFadeBackground(true);
      smEnableSpotlight = false;
      if (mMode == 9) {
         this.enableFadeBackground(false);
         smEnableSpotlight = true;
         CollisionBox box6 = AnimationsManager.loadShared(65597).getCollisionBox(6);
         CollisionBox box7 = smSpotlightAnimation.getCollisionBox(7);
         smSpotlightX = box6.getX() >> 1;
         int height = Toolkit.getScreenHeight();
         int y = box6.getY();
         smSpotlightY = height + y + (box7.getHeight() >> 1);
      }
   }

   public void setImages(CollisionBox bubbleBox, int delta) {
      int mode = mMode;
      if (PORTRAIT[mode] != -1) {
         mPortrait.setImage(PORTRAIT[mode]);
         HUDImage portrait = mPortrait;
         int x;
         if (!smMirror) {
            x = smPortraitMarginX;
         } else {
            int width = Toolkit.getScreenWidth();
            int margin = smPortraitMarginX;
            x = width - margin - mPortrait.getWidth();
         }

         portrait.setPositionX(x);
         mPortrait.setVisible(true);
         mode = mMode;
      }

      if (ICON[mode] != -1) {
         int pivotX = mImageIcon[mode].getPivotX();
         int pivotY = mImageIcon[mMode].getPivotY();
         int alignment = ICON_ALIGNMENT[mMode];
         if (alignment == 2) {
            CollisionBox box = this.getCollisionBox(11);
            int x = box.getX();
            int y = box.getY();
            mIcon.setPosition(pivotX + x, y + pivotY);
         } else if (alignment == 0) {
            CollisionBox box = this.getCollisionBox(12);
            int x = bubbleBox.getX();
            int width = bubbleBox.getWidth();
            int y = box.getY();
            mIcon.setPosition(x + width / 2, y + pivotY);
         } else if (alignment == 1) {
            this.getCollisionBox(12);
            int x = bubbleBox.getX();
            int width = bubbleBox.getWidth();
            int y;
            if (!EXTRA_BUTTON[mMode]) {
               y = this.getCollisionBox(4).getY();
               y -= mImageIcon[mMode].getHeight();
            } else {
               CollisionBox box = this.getCollisionBox(7);
               int textId = BUTTON_TEXT[mMode];
               y = box.getY();
               if (textId != -1) {
                  y -= mButtonText.getTextHeight();
               }

               y -= mImageIcon[mMode].getHeight();
            }

            mIcon.setPosition(x + width / 2, pivotY + y);
         }

         mIcon.setImage(ICON[mMode]);
         mIcon.setVisible(true);
      }
   }

   public void setText(boolean buttonText) {
      if (TITLE[mMode] != -1) {
         mTitle.setVisible(true);
      }

      mDescription.setVisible(true);
      mButtonText.setVisible(buttonText);
   }

   public void setButtons(HUDBubble bubble, int delta) {
      CollisionBox leftBox = this.getCollisionBox(3);
      CollisionBox baseBox = this.getCollisionBox(1);
      int margin = leftBox.getX() - baseBox.getX();
      if (mMode >= 0 && mMode <= 13 && mMode != 9) {
         switch (mMode) {
            case 0: {
               HUDButton button = mButtonCancel;
               button.setPositionX(bubble.getX() + margin);
               button = mButtonPost;
               button.setPositionX(bubble.getX() - margin + bubble.getWidth() - mButtonPost.getWidth());
               mButtonCancel.changePositionY(delta);
               mButtonPost.changePositionY(delta);
               mButtonCancel.setVisible(true);
               mButtonPost.setVisible(true);
               break;
            }
            case 1: {
               HUDButton button = mButtonRateUs;
               button.setPositionX(bubble.getX() + (bubble.getWidth() - mButtonRateUs.getWidth()) / 2);
               button = mButtonLater;
               button.setPositionX(bubble.getX() + (bubble.getWidth() - mButtonLater.getWidth()) / 2);
               mButtonRateUs.changePositionY(delta);
               mButtonLater.changePositionY(delta);
               mButtonRateUs.setVisible(true);
               mButtonLater.setVisible(true);
               break;
            }
            case 2: {
               HUDButton button = mButtonFacebook;
               button.setPositionX(bubble.getX() + (bubble.getWidth() - mButtonFacebook.getWidth()) / 2);
               button = mButtonNotNow;
               button.setPositionX(bubble.getX() + (bubble.getWidth() - mButtonNotNow.getWidth()) / 2);
               mButtonFacebook.changePositionY(delta);
               mButtonNotNow.changePositionY(delta);
               mButtonFacebook.setVisible(true);
               mButtonNotNow.setVisible(true);
               break;
            }
            case 3:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            default: {
               HUDButton button = mButtonOk;
               if (mMode >= 6 && mMode < 12) {
                  button = switch (mMode) {
                     case 6, 8, 10, 11 -> mButtonOkLarge;
                     default -> mButtonOk;
                  };
               }

               button.setPositionX(bubble.getX() + (bubble.getWidth() - button.getWidth()) / 2);
               button.changePositionY(delta);
               button.setVisible(true);
               break;
            }
            case 4: {
               HUDButton button = mButtonGoBack;
               button.setPositionX(bubble.getX() + (bubble.getWidth() - mButtonGoBack.getWidth()) / 2);
               button = mButtonGoBack;
               button.changePositionY(delta);
               mButtonGoBack.setVisible(true);
               break;
            }
            case 5: {
               HUDButton button = mButtonInventory;
               button.setPositionX(bubble.getX() + (bubble.getWidth() - mButtonInventory.getWidth()) / 2);
               mButtonInventory.changePositionY(delta);
               mButtonInventory.setVisible(true);
               break;
            }
            case 12: {
               HUDButton button = mButtonNotNow;
               button.setPositionX(bubble.getX() + margin);
               button = mButtonGifts;
               button.setPositionX(bubble.getX() - margin + bubble.getWidth() - mButtonGifts.getWidth());
               mButtonNotNow.changePositionY(delta);
               mButtonGifts.changePositionY(delta);
               mButtonNotNow.setVisible(true);
               mButtonGifts.setVisible(true);
               break;
            }
            case 13: {
               HUDButton button = mButtonAppstore;
               button.setPositionX(bubble.getX() + (bubble.getWidth() - mButtonAppstore.getWidth()) / 2);
               button = mButtonNotNow;
               button.setPositionX(bubble.getX() + (bubble.getWidth() - mButtonNotNow.getWidth()) / 2);
               mButtonAppstore.changePositionY(delta);
               mButtonNotNow.changePositionY(delta);
               mButtonAppstore.setVisible(true);
               mButtonNotNow.setVisible(true);
            }
         }
      }
   }

   public void resetButtonPositions(int mode) {
      HUDButton button = mButtonAppstore;
      button.setPosition(this.getCollisionBox(7));
      button = mButtonFacebook;
      button.setPosition(this.getCollisionBox(7));
      button = mButtonRateUs;
      button.setPosition(this.getCollisionBox(7));
      button = mButtonNotNow;
      button.setPosition(this.getCollisionBox(4));
      button = mButtonLater;
      button.setPosition(this.getCollisionBox(4));
      button = mButtonGoBack;
      button.setPosition(this.getCollisionBox(4));
      button = mButtonRetry;
      button.setPosition(this.getCollisionBox(3));
      button = mButtonCancel;
      button.setPosition(this.getCollisionBox(3));
      button = mButtonPost;
      button.setPosition(this.getCollisionBox(6));
      button = mButtonInventory;
      button.setPosition(this.getCollisionBox(4));
      button = mButtonPlay;
      button.setPosition(this.getCollisionBox(6));
      button = mButtonGifts;
      button.setPosition(this.getCollisionBox(6));
      int unsignedMode = Byte.toUnsignedInt(mMode);
      if (unsignedMode < 12 && (1 << unsignedMode & 3392) != 0) {
         button = mButtonOkLarge;
         button.setPosition(this.getCollisionBox(5));
      } else if (mMode == 3) {
         button = mButtonOk;
         button.setPosition(this.getCollisionBox(6));
      } else {
         button = mButtonOk;
         button.setPosition(this.getCollisionBox(4));
      }
   }
}
