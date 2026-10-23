package com.dchoc.hud;

import com.dchoc.DCiDead;
import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.Toolkit;

public class HUDInputField extends HUDObject {
   private static final String DEFAULT_VALUE = "000";
   private static final char[][] NUMBER_CHARACTERS = new char[][]{{'0'}, {'1'}, {'2'}, {'3'}, {'4'}, {'5'}, {'6'}, {'7'}, {'8'}, {'9'}};
   private HUDCollection mCollection;
   private CollisionBox mBox;
   private SlotBackground mBackground;
   private HUDAutoTextField mTextDefault;
   private InputField mInputField;
   private Rectangle mHighlightFailed;
   private Rectangle mHighlightAdded;

   public HUDInputField(CollisionBox box) {
      super((byte)27);
      this.mBox = box;
      this.mCollection = new HUDCollection();
      this.mCollection.setParent(this);
      this.mBackground = new SlotBackground(box);
      this.mCollection.addObject(this.mBackground);
      this.mInputField = new InputField(2, DCiDead.getFont(4));
      InputField input = this.mInputField;
      input.setBounds(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      this.mInputField.setMaxCharacterCount(3);
      this.mInputField.setMultiline(false);
      this.mTextDefault = new HUDAutoTextField(box);
      this.mTextDefault.setText("000", 4);
      this.mTextDefault.setCentered(true, true);
      this.mTextDefault.setSemitransparent(true);
      this.mCollection.addObject(this.mTextDefault);
      this.mHighlightFailed = new Rectangle(box, this, -2130747296);
      this.mHighlightAdded = new Rectangle(box, this, -2141126816);
      this.setHighlightMode(0);
   }

   public void setHighlightMode(int mode) {
      this.mHighlightFailed.setVisible(mode == 2);
      this.mHighlightAdded.setVisible(mode == 1);
   }

   @Override
   public void callback(int id) {
   }

   @Override
   public void draw() {
      if (this.isVisible()) {
         this.mCollection.draw();
         InputField input = this.mInputField;
         input.setX(this.getX() + this.mBox.getX());
         input = this.mInputField;
         input.setY(this.getY() + this.mBox.getY());
         this.mInputField.doDraw();
         this.mHighlightFailed.draw();
         this.mHighlightAdded.draw();
         this.drawDebug(16711935);
      }
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.isVisible()) {
         this.mCollection.logicUpdate(delta);
         this.mInputField.logicUpdate(delta);
         boolean empty = false;
         if (this.mInputField.getTextLength() == 0) {
            empty = !this.mInputField.hasFocus();
         }

         this.mTextDefault.setVisible(empty);
         this.mInputField.setVisible(!empty);
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.isVisible()) {
         int type = event.getType();
         if (type == 2) {
            this.mInputField.pointerEventOccurred(event.getX(), event.getY(), 1);
         } else if (type == 1) {
            this.mInputField.pointerEventOccurred(event.getX(), event.getY(), 2);
         } else if (type == 0) {
            this.mInputField.pointerEventOccurred(event.getX(), event.getY(), 0);
         }
      }
   }

   public void keyEventOccured(int unused, int key, int type) {
      if (this.isVisible()) {
         InputField input = this.mInputField;
         int action = Toolkit.getToolkitGameAction(key);
         char[] chars = null;
         if (key >= 48 && key <= 57) {
            chars = NUMBER_CHARACTERS[key - 48];
         }

         input.keyEventOccurred(key, action, type, chars);
      }
   }

   public String getText() {
      return this.mInputField.getText();
   }

   public void setText(String text) {
      this.mInputField.setText(text);
   }

   public void setFocus(boolean focus) {
      this.mInputField.setFocus(focus);
      if (focus) {
         this.mInputField.setText("");
      }
   }

   public boolean hasFocus() {
      return this.mInputField.hasFocus();
   }

   public boolean isFull() {
      return this.mInputField.getTextLength() == 3;
   }
}
