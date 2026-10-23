package com.dchoc.windows;

import com.dchoc.hud.HUDAutoTextField;
import com.dchoc.hud.HUDAutoTextFieldScale;
import com.dchoc.hud.HUDBubble;
import com.dchoc.hud.HealthBar;
import com.dchoc.idead.GameEngine;
import com.dchoc.idead.animation.AnimationsManager;
import com.dchoc.idead.isometric.Camera;
import com.dchoc.idead.isometric.IsometricObject;
import com.dchoc.idead.isometric.IsometricScene;
import com.dchoc.idead.isometric.IsometricUtils;
import com.dchoc.idead.items.ConstructionItem;
import com.dchoc.idead.items.DestructibleItem;
import com.dchoc.idead.items.FarmingItem;
import com.dchoc.idead.items.Item;
import com.dchoc.idead.items.ItemManager;
import com.dchoc.idead.objects.ConstructionObject;
import com.dchoc.idead.objects.FarmingObject;
import com.dchoc.idead.objects.FenceObject;
import com.dchoc.idead.objects.LootObject;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public class WindowTooltip extends Window {
   public static final String SEPARATOR = "/";
   public static final String SPACE = " ";
   public static final byte TOUCH_TYPE_HOLD = 0;
   public static final byte TOUCH_TYPE_NONE = -1;
   public static final byte TOUCH_TYPE_TAPPED = 1;
   public static final byte TYPE_BOTTOM_HUD_FIRST = 7;
   public static final byte TYPE_BOTTOM_HUD_LAST = 23;
   public static final byte TYPE_DIALOG_CENTER_CAMERA = 24;
   public static final byte TYPE_DIALOG_FIRST = 24;
   public static final byte TYPE_DIALOG_LAST = 24;
   public static final byte TYPE_HUD_ACCEPT = 14;
   public static final byte TYPE_HUD_AMMO = 23;
   public static final byte TYPE_HUD_BRICKS = 1;
   public static final byte TYPE_HUD_CANCEL = 12;
   public static final byte TYPE_HUD_CASH = 6;
   public static final byte TYPE_HUD_COINS = 5;
   public static final byte TYPE_HUD_CUSTOMIZE = 22;
   public static final byte TYPE_HUD_EDIT = 9;
   public static final byte TYPE_HUD_ENERGY = 2;
   public static final byte TYPE_HUD_FOOD = 0;
   public static final byte TYPE_HUD_GOBACK = 13;
   public static final byte TYPE_HUD_GOHOME = 18;
   public static final byte TYPE_HUD_HAPPINESS = 4;
   public static final byte TYPE_HUD_INVENTORY = 8;
   public static final byte TYPE_HUD_MARKET = 7;
   public static final byte TYPE_HUD_ROTATE = 15;
   public static final byte TYPE_HUD_SELL = 16;
   public static final byte TYPE_HUD_SETTINGS = 11;
   public static final byte TYPE_HUD_SOCIAL = 10;
   public static final byte TYPE_HUD_STORE = 17;
   public static final byte TYPE_HUD_SWITCH_WEAPON = 21;
   public static final byte TYPE_HUD_VISIT_NEXT = 19;
   public static final byte TYPE_HUD_VISIT_PREVIOUS = 20;
   public static final byte TYPE_HUD_XP = 3;
   public static final byte TYPE_NONE = -1;
   public static final byte TYPE_SCENE_FIRST = 25;
   public static final byte TYPE_SCENE_LAST = 26;
   public static final byte TYPE_SCENE_OBJECT = 25;
   public static final byte TYPE_SCENE_SPEECH_BUBBLE = 26;
   public static final byte TYPE_TOP_HUD_CENTER_FIRST = 2;
   public static final byte TYPE_TOP_HUD_CENTER_LAST = 4;
   public static final byte TYPE_TOP_HUD_FIRST = 0;
   public static final byte TYPE_TOP_HUD_LAST = 6;
   public static final byte TYPE_TOP_HUD_LEFT_FIRST = 0;
   public static final byte TYPE_TOP_HUD_LEFT_LAST = 1;
   public static final byte TYPE_TOP_HUD_RIGHT_FIRST = 5;
   public static final byte TYPE_TOP_HUD_RIGHT_LAST = 6;
   private static final short[] TEXT = new short[]{
      796, 795, 797, 799, 798, 793, 794, 101, 59, 98, 2238, 2311, 97, 2361, 2316, 2312, 2313, 2314, 103, 2276, 2277, 16, 100, 181, 2444
   };
   private static final int[] TOOLTIP_OPEN_UNTIL_ACTION = new int[0];
   private static int smBottomBorder = -1;
   private static boolean smEnableTooltips;
   private static boolean smLoadText = true;
   private static String[] smParams;
   private static int smTextIndex;
   private static String[] smTexts = new String[5];
   private static int smTopBorder = -1;
   private int mTipType;
   private boolean mCloseAfterOpening;
   private boolean mIsTopLayer;
   private int mCloseTimer;
   private IsometricObject mTargetObject;
   private float mTargetX;
   private float mTargetY;
   private HUDBubble mBubble;
   private HUDAutoTextFieldScale mText;
   private HealthBar mHealthBar;
   private int mLanguageIndex;
   private String mForcedText;
   private int mBubbleX;
   private int mBubbleY;
   private int mTouchType = -1;

   public WindowTooltip() {
      super.setAsTooltipBubble();
      this.mTipType = -1;
      this.mFadeBackground = false;
      HUDBubble bubble = new HUDBubble();
      this.mBubble = bubble;
      bubble.setAsTooltip();
      this.mBubble.setPosition(-1, -1);
      HUDAutoTextFieldScale text = new HUDAutoTextFieldScale();
      this.mText = text;
      text.setCentered(true, false);
      this.mObjects.addObject(this.mBubble);
      this.mObjects.addObject(this.mText);
      if (smLoadText) {
         for (int i = 0; i < (TEXT == null ? 0 : TEXT.length); i++) {
            Toolkit.getText(TEXT[i]);
         }

         Toolkit.getText(774);
         Toolkit.getText(782);
         Toolkit.getText(1645);
         Toolkit.getText(777);
         Toolkit.getText(45);
         Toolkit.getText(353);
         Toolkit.getText(772);
         Toolkit.getText(787);
         Toolkit.getText(20);
         Toolkit.getText(21);
         Toolkit.getText(22);
         Toolkit.getText(23);
         Toolkit.getText(790);
         smParams = new String[1];
         smLoadText = false;
      }

      if (smTopBorder == -1) {
         SpriteObject sprite = AnimationsManager.loadShared(65551);
         CollisionBox box = sprite.getCollisionBox(1);
         int y = box.getY();
         int height = box.getHeight();
         smTopBorder = y + height;
      }

      if (smBottomBorder == -1) {
         SpriteObject sprite = AnimationsManager.loadShared(65597);
         CollisionBox box = sprite.getCollisionBox(99);
         int height = Toolkit.getScreenHeight();
         int y = box.getY();
         smBottomBorder = height + y;
      }
   }

   @Override
   public void logicUpdate(int delta) {
      int state = Byte.toUnsignedInt(this.mState);
      if (state == 2 || state == 3) {
         float scale = this.mTooltipScaleTimer / 250.0F;
         this.mBubble.setScaleFactor(scale);
         this.mText.setScaleFactor(scale);
         state = Byte.toUnsignedInt(this.mState);
      }

      if (state != 1) {
         super.logicUpdate(delta);
         state = Byte.toUnsignedInt(this.mState);
      }

      if (state == 1) {
         this.mBubble.setPosition(-1, -1);
         this.doAfterCloseActions();
      } else if (state == 0) {
         if (this.mCloseAfterOpening) {
            this.mCloseAfterOpening = false;
            this.mCloseTimer = 1000;
         } else if (this.mCloseTimer > 0) {
            this.mCloseTimer -= delta;
            if (this.mCloseTimer <= 0) {
               this.mCloseTimer = 0;
               this.close();
            }
         }
      }

      if (this.mTouchType == 1
         && !this.mCloseAfterOpening
         && this.mCloseTimer == 0
         && this.mHealthBar != null
         && (this.mHealthBar.isCloseAfterOpening() || this.mHealthBar.getCloseTimer() > 0)) {
         this.closeTooltip(false);
      }

      if (this.mState != 1 && Integer.compareUnsigned(this.mTipType - 25, 1) <= 0) {
         this.updatePosition();
      }
   }

   public void updateCounterText(int count, int max) {
      if (this.mTargetObject != null) {
         this.setCounterText(count, max);
         this.setTextField();
      }
   }

   @Override
   public int getTooltipType() {
      return this.mTipType;
   }

   public static boolean isEnableTooltips() {
      return smEnableTooltips;
   }

   public static void enableTooltips(boolean enable) {
      smEnableTooltips = enable;
   }

   public IsometricObject getTargetObject() {
      return this.mTargetObject;
   }

   public static boolean addText(String text) {
      int index = smTextIndex;
      int length = smTexts == null ? 0 : smTexts.length;
      if (index < length) {
         index = smTextIndex;
         smTexts[index] = text;
         smTextIndex = index + 1;
         return true;
      } else {
         return false;
      }
   }

   public void initNewTooltip(IsometricObject target) {
      this.mTargetObject = target;
      this.mTargetX = -1.0F;
      this.mTargetY = -1.0F;
      smTextIndex = 0;

      for (int i = 0; i < (smTexts == null ? 0 : smTexts.length); i++) {
         smTexts[i] = null;
      }
   }

   public void checkCloseAfterAction(IsometricObject target) {
      if (this.mTargetObject == target) {
         if (!IsometricUtils.getPlayer().hasActionWith(this.mTargetObject)) {
            this.closeTooltip(false);
         }
      }
   }

   public void checkCloseAfterRelease() {
      if (this.mTargetObject == null && this.mTouchType != 1) {
         if (this.mTouchType != 0) {
            int i = 0;

            while (i < (TOOLTIP_OPEN_UNTIL_ACTION == null ? 0 : TOOLTIP_OPEN_UNTIL_ACTION.length)) {
               int type = TOOLTIP_OPEN_UNTIL_ACTION[i];
               i++;
               if (type == this.mTipType) {
                  return;
               }
            }
         }

         if ((this.mTipType | 2) != 26) {
            this.closeTooltip(false);
         }
      }
   }

   public void closeTooltip(boolean force) {
      if (force) {
         if (!this.isClosed()) {
            this.close();
         }
      } else {
         if (!this.mCloseAfterOpening && this.mCloseTimer == 0) {
            if (this.mState == 0) {
               this.mCloseTimer = 1000;
            } else if (this.mState == 2) {
               this.mCloseAfterOpening = true;
            }
         }
      }
   }

   public void setTooltip(int type, HealthBar bar, String text) {
      this.setTooltip(type, 0, 0, 0, 0, -1, bar, text);
   }

   public void setTooltip(int type, int x, int y, int width, int height, HealthBar bar) {
      this.setTooltip(type, x, y, width, height, -1, bar, null);
   }

   public void setTooltip(int type, int x, int y, int width, int height, int pipkaType, HealthBar bar, String text) {
      int screenWidth = Toolkit.getScreenWidth();
      this.mTipType = type;
      this.mHealthBar = bar;
      this.mForcedText = text;
      this.mLanguageIndex = Toolkit.getSelectedLanguageIndex();
      this.mBubble.disablePipkaPosition();
      if (this.mTargetObject != null) {
         int count;
         int max;
         if (bar == null) {
            count = -1;
            max = -1;
         } else {
            count = bar.getHealthCount();
            max = bar.getMaxCount();
         }

         this.setCounterText(count, max);
      }

      if (type != -1 && Integer.compareUnsigned(type, 23) > 0) {
         if (Integer.compareUnsigned(type - 25, 1) <= 0) {
            this.setTextField();
            String currentText = this.mText.getText();
            if (currentText != null && !currentText.equals("")) {
               this.setBubbleSize();
               if (this.mBubble.getX() != -1 && this.mBubble.getY() != -1) {
                  this.setState((byte)0);
               } else {
                  this.open();
               }

               this.updatePosition();
            } else {
               this.doAfterCloseActions();
            }
         } else if (type == 24) {
            this.setTextField();
            this.setBubbleSize();
            this.mBubble.setAsTooltip();
            this.mBubble.setPipkaPosType(1);
            int pipkaWidth = this.mBubble.getPipkaWidth();
            this.mBubbleX = pipkaWidth + x;
            this.mBubbleY = y;
            this.mBubble.setPosition(pipkaWidth + x, y);
            this.centerText();
            this.setState((byte)4);
         }
      } else if (this.mTouchType != 1) {
         this.setTextField();
         int bubbleWidth = this.mText.getTextWidth();
         int borderWidth = this.getBorderWidth();
         int bubbleHeight = this.mText.getHeight();
         bubbleWidth += 2 * borderWidth;
         int borderHeight = this.getBorderHeight();
         bubbleHeight += 2 * borderHeight;
         int bubbleX = x;
         if (type != -1) {
            if (Integer.compareUnsigned(type, 7) < 0) {
               int pipkaHeight = this.mBubble.getPipkaHeight();
               y = pipkaHeight + y + height + 5;
            } else {
               int bubbleY = -1;
               if (Integer.compareUnsigned(type - 7, 17) < 0) {
                  int pipkaHeight = this.mBubble.getPipkaHeight();
                  bubbleY = y - 5 - bubbleHeight - pipkaHeight;
               }

               y = bubbleY;
            }

            if (type != 23) {
               bubbleX = x + (width >> 1) - (bubbleWidth >> 1);
            }
         }

         if (pipkaType != -1) {
            this.mBubble.setPipkaPosType(pipkaType);
         } else if (Integer.compareUnsigned(type, 2) < 0) {
            this.mBubble.setPipkaPosType(6);
         } else if (Integer.compareUnsigned(type - 2, 3) < 0) {
            this.mBubble.setPipkaPosType(7);
         } else if (Integer.compareUnsigned(type - 5, 2) < 0) {
            this.mBubble.setPipkaPosType(8);
         } else if (Integer.compareUnsigned(type - 7, 17) < 0) {
            if (type == 12) {
               this.mBubble.setPipkaPosType(11);
               this.mBubble.setPipkaPosition(x + (width >> 1), -1);
            } else {
               HUDBubble bubble = this.mBubble;
               bubble.setPipkaPosType(type == 23 ? 9 : 10);
            }
         }

         if (type != -1) {
            if (bubbleX < 0) {
               bubbleX = 5;
            } else if (screenWidth < bubbleWidth + bubbleX) {
               bubbleX = screenWidth - 5 - bubbleWidth;
            }
         }

         if (this.mBubble.getX() != -1 && this.mBubble.getY() != -1) {
            this.setState((byte)0);
         } else {
            this.open();
         }

         this.setBubbleDimensions(bubbleX, y, bubbleWidth, bubbleHeight);
      }
   }

   public int getTouchType() {
      return this.mTouchType;
   }

   public void setTouchType(int type) {
      this.mTouchType = type;
   }

   public void setTopLayer(boolean top) {
      this.mIsTopLayer = top;
   }

   public boolean isTopLayer() {
      return this.mIsTopLayer;
   }

   @Override
   public void setState(byte state) {
      int unsigned = Byte.toUnsignedInt(state);
      if (unsigned <= 1) {
         this.enableBubbleTextScale(false);
      } else if (unsigned == 2 || unsigned == 3) {
         this.enableBubbleTextScale(true);
      }

      super.setState(state);
   }

   public void doAfterCloseActions() {
      this.mTouchType = -1;
      this.mCloseTimer = 0;
      this.mTargetObject = null;
      this.mPositionType = -1;
   }

   public void enableBubbleTextScale(boolean enable) {
      this.mBubble.enableCenterScale(enable);
      this.mText.enableCenterScale(enable);
      if (!enable) {
         this.mBubble.setScaleFactor(1.0F);
         this.mText.setScaleFactor(1.0F);
      }
   }

   public int getBorderWidth() {
      SpriteObject background = this.mBubble.getBackground();
      CollisionBox box = background.getCollisionBox(2);
      return box.getWidth();
   }

   public int getBorderHeight() {
      SpriteObject background = this.mBubble.getBackground();
      CollisionBox box = background.getCollisionBox(2);
      return box.getHeight();
   }

   public void setCounterText(int count, int max) {
      if (this.mTipType != 26) {
         smTextIndex = 0;

         for (int i = 0; i < (smTexts == null ? 0 : smTexts.length); i++) {
            smTexts[i] = null;
         }

         int type = this.mTargetObject.getType();
         Item item = this.mTargetObject.getItem();
         String title = Toolkit.getText(item.getTitle());
         switch (type) {
            case 0:
               if (!GameEngine.getInstance().isVisitingNeighbor()) {
                  ConstructionObject construction = (ConstructionObject)this.mTargetObject;
                  ConstructionItem constructionItem = (ConstructionItem)item;
                  int[] actions = constructionItem.getActions();
                  int state = construction.getState();
                  if (state == 0) {
                     int current = count;
                     int required = max;
                     if (count == -1 || max == -1) {
                        current = construction.getClicks();
                        required = constructionItem.getRequiredClicks();
                     }

                     String value = title + " " + current + "/" + required;
                     if (smTextIndex < (smTexts == null ? 0 : smTexts.length)) {
                        int index = smTextIndex;
                        smTexts[index] = value;
                        smTextIndex = index + 1;
                     }

                     if (actions != null && actions.length > 0) {
                        value = Toolkit.getText(actions[0]);
                        if (smTextIndex < (smTexts == null ? 0 : smTexts.length)) {
                           int index = smTextIndex;
                           smTexts[index] = value;
                           smTextIndex = index + 1;
                        }
                     }

                     if (constructionItem.getStonePerClick() > 0) {
                        String parameter = Integer.toString(constructionItem.getStonePerClick());
                        smParams[0] = parameter;
                        value = Toolkit.getText(782);
                        value = Toolkit.replaceParameters(value, smParams);
                        if (smTextIndex < (smTexts == null ? 0 : smTexts.length)) {
                           int index = smTextIndex;
                           smTexts[index] = value;
                           smTextIndex = index + 1;
                        }
                     }

                     if (constructionItem.getCoinsPerClick() > 0) {
                        String parameter = Integer.toString(constructionItem.getCoinsPerClick());
                        smParams[0] = parameter;
                        value = Toolkit.getText(783);
                        value = Toolkit.replaceParameters(value, smParams);
                        if (smTextIndex < (smTexts == null ? 0 : smTexts.length)) {
                           int index = smTextIndex;
                           smTexts[index] = value;
                           smTextIndex = index + 1;
                        }
                     }
                  } else {
                     if (smTextIndex < (smTexts == null ? 0 : smTexts.length)) {
                        int index = smTextIndex;
                        smTexts[index] = title;
                        smTextIndex = index + 1;
                     }

                     String value;
                     if (state == 1) {
                        if (constructionItem.getCollectibleItems() == null || actions == null || actions.length < 2) {
                           return;
                        }

                        value = Toolkit.getText(actions[1]);
                     } else {
                        if (actions == null || actions.length < 3) {
                           return;
                        }

                        value = Toolkit.getText(actions[2]);
                     }

                     if (smTextIndex < (smTexts == null ? 0 : smTexts.length)) {
                        int index = smTextIndex;
                        smTexts[index] = value;
                        smTextIndex = index + 1;
                     }
                  }
               }
            case 1:
            case 4:
            default:
               break;
            case 2:
               if (smTextIndex < (smTexts == null ? 0 : smTexts.length)) {
                  int index = smTextIndex;
                  smTexts[index] = title;
                  smTextIndex = index + 1;
               }
               break;
            case 3:
               LootObject loot = (LootObject)this.mTargetObject;
               if (loot.isAlive()) {
                  DestructibleItem destructible = (DestructibleItem)loot.getItem();
                  int remaining = count;
                  if (destructible.isFixable()) {
                     remaining = max - count;
                  }

                  String parameter = Integer.toString(remaining);
                  smParams[0] = parameter;
                  String value = Toolkit.getText(353);
                  value = title + Toolkit.replaceParameters(value, smParams);
                  if (smTextIndex < (smTexts == null ? 0 : smTexts.length)) {
                     int index = smTextIndex;
                     smTexts[index] = value;
                     smTextIndex = index + 1;
                  }
               }
               break;
            case 5:
               FenceObject fence = (FenceObject)this.mTargetObject;
               int health = fence.getHealth();
               int maxHealth = fence.getMaxHealth();
               String parameter = Integer.toString(health);
               smParams[0] = parameter;
               if (smTextIndex < (smTexts == null ? 0 : smTexts.length)) {
                  int index = smTextIndex;
                  smTexts[index] = title;
                  smTextIndex = index + 1;
               }

               String value = Toolkit.getText(45);
               value = Toolkit.replaceParameters(value, smParams) + "/" + maxHealth;
               if (smTextIndex < (smTexts == null ? 0 : smTexts.length)) {
                  int index = smTextIndex;
                  smTexts[index] = value;
                  smTextIndex = index + 1;
               }
               break;
            case 6:
               FarmingObject farming = (FarmingObject)this.mTargetObject;
               FarmingItem farmingItem = (FarmingItem)farming.getItem();
               int[] actions = farmingItem.getActions();
               if (smTextIndex < (smTexts == null ? 0 : smTexts.length)) {
                  int index = smTextIndex;
                  smTexts[index] = title;
                  smTextIndex = index + 1;
               }

               boolean ready = false;
               if (!farming.isGrowing() && !farming.isDestroyed()) {
                  if (!farmingItem.canWither()) {
                     ready = true;
                  } else if (farmingItem.canWither() && !farming.isWithered()) {
                     ready = true;
                  }
               }

               String value;
               if (ready) {
                  value = Toolkit.getText(actions[1]);
               } else if (farming.isWithered()) {
                  value = Toolkit.getText(actions[2]);
               } else {
                  if (!farming.isDestroyed()) {
                     return;
                  }

                  value = Toolkit.getText(actions[3]);
               }

               if (smTextIndex < (smTexts == null ? 0 : smTexts.length)) {
                  int index = smTextIndex;
                  smTexts[index] = value;
                  smTextIndex = index + 1;
               }
         }
      }
   }

   public void setTextField() {
      String text = this.mForcedText;
      if (text == null) {
         if (this.mTipType == -1) {
            return;
         }

         String newline = HUDAutoTextField.LINE_CHANGE_SYMBOL;
         String space = HUDAutoTextField.EMPTY_SPACE_SYMBOL;
         text = "";

         for (int i = 0; i < (smTexts == null ? 0 : smTexts.length); i++) {
            if (smTexts[i] != null) {
               if (i > 0) {
                  text = text + newline;
               }

               text = text + smTexts[i];
            }
         }

         if (Integer.compareUnsigned(this.mTipType - 25, 1) <= 0) {
            if (this.mTargetObject != null) {
               Item item = this.mTargetObject.getItem();
               int type = this.mTargetObject.getType();
               boolean simple = true;
               boolean deadLoot = false;
               if (type == 3 && this.mTargetObject != null) {
                  simple = false;
                  if (!((LootObject)this.mTargetObject).isAlive()) {
                     text = text + Toolkit.getText(item.getTitle());
                     deadLoot = true;
                  }
               }

               if (!deadLoot) {
                  int action = item.getAction();
                  if (simple) {
                     if (action != -1) {
                        text = text + newline + Toolkit.getText(action);
                     }
                  } else {
                     int[][] items = ((DestructibleItem)item).getItemsPerClick();
                     if (items == null) {
                        if (action != -1) {
                           text = text + newline + Toolkit.getText(action);
                        }
                     } else {
                        text = text + newline + newline + Toolkit.getText(4191);

                        for (int i = 0; i < items.length; i++) {
                           Item drop = ItemManager.getItem(items[i][0]);
                           text = text + newline + items[i][1] + space + Toolkit.getText(drop.getTitle());
                        }

                        if (action != -1) {
                           text = text + newline + newline + Toolkit.getText(action);
                        }
                     }
                  }
               }
            }
         } else {
            if (!text.equals("")) {
               text = text + newline;
            }

            if (this.mTipType == 3) {
               String parameter = Integer.toString(PlayerProfile.getXpForLevelUp());
               smParams[0] = parameter;
               String translated = Toolkit.getText(TEXT[this.mTipType]);
               text = text + Toolkit.replaceParameters(translated, smParams);
            } else {
               text = text + Toolkit.getText(TEXT[this.mTipType]);
            }
         }
      }

      this.mText.setText(text, 3);
      int width = this.mText.getTextWidth();
      int height = this.mText.getTextHeight();
      this.mText.setSize(width, height);
      if (this.mLanguageIndex != Toolkit.getSelectedLanguageIndex()) {
         this.updateBubbleSize();
         this.mLanguageIndex = Toolkit.getSelectedLanguageIndex();
      }
   }

   public void updateBubbleSize() {
      this.setBubbleSize();
      this.updatePositionWithTarget();
      this.centerText();
   }

   public void setBubbleSize() {
      int width = this.mText.getTextWidth();
      int borderWidth = this.getBorderWidth();
      int height = this.mText.getHeight();
      int borderHeight = this.getBorderHeight();
      this.mBubble.setWidth(width + 2 * borderWidth);
      this.mBubble.setHeight(height + 2 * borderHeight);
   }

   public void setBubbleDimensions(int x, int y, int width, int height) {
      if (this.mBubble.getX() != -1 && this.mBubble.getY() != -1) {
         this.mBubble.setSize(width, height);
      } else {
         this.mBubble.setSizes(x, y, width, height);
      }

      this.centerText();
   }

   public void centerText() {
      HUDAutoTextFieldScale text = this.mText;
      int x = this.mBubble.getX();
      int width = this.mBubble.getWidth();
      int textWidth = this.mText.getWidth();
      int y = this.mBubble.getY();
      int height = this.mBubble.getHeight();
      int textHeight = this.mText.getHeight();
      text.setPosition(x + (width >> 1) - (textWidth >> 1), y + (height >> 1) - (textHeight >> 1));
   }

   public void updatePosition() {
      if (this.mTargetObject != null) {
         IsometricScene scene = GameEngine.getInstance().getScene();
         Camera camera = scene.getCamera();
         float scale = scene.getScale();
         float screenX = this.mTargetObject.getScreenX();
         float cameraX = camera.getX();
         float screenY = this.mTargetObject.getScreenY();
         float cameraY = camera.getY();
         float x = (screenX - cameraX) * scale;
         float y = (screenY - cameraY) * scale;
         if (x != this.mTargetX || y != this.mTargetY) {
            this.mTargetX = x;
            this.mTargetY = y;
            this.updatePositionWithTarget();
         }
      }
   }

   public void updatePositionWithTarget() {
      IsometricObject target = this.mTargetObject;
      int position = this.mPositionType;
      int[] result;
      if (this.mTipType == 26) {
         int width = this.mBubble.getWidth();
         int height = this.mBubble.getHeight();
         int pipkaWidth = this.mBubble.getPipkaWidth();
         int pipkaHeight = this.mBubble.getPipkaHeight();
         result = IsometricUtils.getPositionTypeXY(target, position, width, height, pipkaWidth, pipkaHeight, false, false, 0);
      } else if (this.mHealthBar == null) {
         int width = this.mBubble.getWidth();
         int height = this.mBubble.getHeight();
         int pipkaWidth = this.mBubble.getPipkaWidth();
         int pipkaHeight = this.mBubble.getPipkaHeight();
         result = IsometricUtils.getPositionTypeXY(target, position, width, height, pipkaWidth, pipkaHeight, false, false, -1);
      } else {
         int attachedPosition = this.mHealthBar.getPositionType();
         int width = this.mBubble.getWidth();
         int height = this.mBubble.getHeight();
         int pipkaWidth = this.mBubble.getPipkaWidth();
         int pipkaHeight = this.mBubble.getPipkaHeight();
         int barWidth = this.mHealthBar.getWidth();
         int barHeight = this.mHealthBar.getHeight();
         result = IsometricUtils.getPositionTypeXY(
            target, position, attachedPosition, width, height, pipkaWidth, pipkaHeight, barWidth, barHeight, false, false, -1
         );
      }

      if (result != null) {
         int x = result[0];
         int width = this.mBubble.getWidth();
         this.mBubbleX = x - width / 2;
         int y = result[1];
         int height = this.mBubble.getHeight();
         this.mBubbleY = y - height / 2;
         this.mBubble.setPosition(this.mBubbleX, this.mBubbleY);
         this.centerText();
         this.mPositionType = (byte)result[2];
         switch (this.mPositionType) {
            case 0:
               this.mBubble.setPipkaPosType(10);
               break;
            case 1:
               this.mBubble.setPipkaPosType(7);
               break;
            case 2:
               this.mBubble.setPipkaPosType(4);
               break;
            case 3:
               this.mBubble.setPipkaPosType(1);
         }
      }
   }
}
