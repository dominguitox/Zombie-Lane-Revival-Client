package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.spriteobject.SpriteObject;
import com.dchoc.toolkit.Toolkit;

public class HUDDailyRewardPanel extends HUDObject {
   private static final int COLLISION_ID_PANEL = 20;
   private static final int COLLISION_ID_DAY_TEXT = 10;
   private static final int COLLISION_ID_AMOUNT_TEXT = 30;
   private static final int COLLISION_ID_TODAY_TEXT = 40;
   private HUDCollection mCollection;
   private HUDImage mBackgroundPanel;
   private HUDAutoTextField mDayText;
   private HUDAutoTextField mAmountText;
   private HUDAutoTextField mTodayText;

   public HUDDailyRewardPanel(SpriteObject sprite, int day, int progress, int amount) {
      super((byte)-1);
      CollisionBox baseBox = sprite.getCollisionBox(20);
      CollisionBox positionBox = sprite.getCollisionBox(day + 20);
      int x = positionBox.getX();
      int baseX = baseBox.getX();
      int y = positionBox.getY();
      int baseY = baseBox.getY();
      this.setSizes(x - baseX, y - baseY, baseBox.getWidth(), baseBox.getHeight());
      this.mCollection = new HUDCollection();
      this.mCollection.setParent(this);
      int next = progress + 1;
      int resource;
      if (day == 4) {
         resource = day <= progress ? 67325 : 66995;
      } else {
         resource = day <= progress ? 67005 : 67021;
      }

      this.mBackgroundPanel = new HUDImage(resource, baseBox);
      this.mCollection.addObject(this.mBackgroundPanel);
      if (day > progress) {
         this.mDayText = new HUDAutoTextField(sprite.getCollisionBox(10));
         this.mDayText.setCentered(true, true);
         String template = Toolkit.getText(25);
         String[] parameters = new String[]{Integer.toString(day + 1)};
         String text = Toolkit.replaceParameters(template, parameters);
         this.mDayText.setText(text, DCiDead.getFont(2));
         this.mCollection.addObject(this.mDayText);
         if (day != 4) {
            this.mAmountText = new HUDAutoTextField(sprite.getCollisionBox(30));
            this.mAmountText.setCentered(true, true);
            String amountText = "x" + amount;
            this.mAmountText.setText(amountText, DCiDead.getFont(4));
            this.mCollection.addObject(this.mAmountText);
         }
      }

      if (day == progress || day == next) {
         int textId = day == progress ? 40 : 41;
         this.mTodayText = new HUDAutoTextField(sprite.getCollisionBox(40));
         this.mTodayText.setCentered(true, true);
         this.mTodayText.setText(Toolkit.getText(textId), DCiDead.getFont(2));
         this.mCollection.addObject(this.mTodayText);
      }
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         this.mCollection.draw();
      }
   }

   @Override
   public void logicUpdate(int delta) {
      this.mCollection.logicUpdate(delta);
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible()) {
         this.mCollection.pointerEvent(event);
      }
   }

   @Override
   public void callback(int id) {
   }
}
