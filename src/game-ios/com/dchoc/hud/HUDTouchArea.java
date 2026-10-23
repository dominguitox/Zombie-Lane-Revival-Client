package com.dchoc.hud;

import com.dchoc.idead.touch.TouchEvent;
import com.dchoc.opengl.OGL;
import com.dchoc.spriteobject.CollisionBox;
import com.dchoc.toolkit.Toolkit;
import java.util.Vector;

public class HUDTouchArea extends HUDObject {
   private boolean mIsClippingEnabled = true;
   private boolean mIsSortingAnimated = false;
   private boolean mIsSortingByIntegers = true;
   private int CLIP_SIZE;
   private int ICON_WIDTH;
   private int ICON_HEIGHT;
   private int ICON_SPACE;
   private int mState;
   private boolean mIsSorted;
   private boolean mIsPageAligned;
   private boolean mHasScrollbar;
   private boolean mHasSwipingBetweenPages;
   private boolean mHasInertia;
   private boolean mHasFadingBorder;
   private int currentPage;
   private int numberOfPages;
   private int iconsPerPage;
   private int pageSize;
   private int containerSize;
   private int containerDelta;
   private int containerTargetDelta;
   private int lastDragDistance;
   private CollisionBox mClipArea;
   private RoundRectangle mBackground;
   private RoundRectangle mOverlay;
   private RoundRectangle mMask;
   private Vector mItems;
   private HUDSpeedController inertiaController;
   private HUDSpeedController autoadjustToBorderController;
   private HUDSpeedController autoadjustToPageController;
   private boolean mCenterItemsVertically;
   private boolean mHideItems;
   private int param;
   private long mScrollbarLastTime;
   private int mScrollbarState = 0;

   public HUDTouchArea(boolean pageAligned, boolean inertia, boolean swiping, CollisionBox clipArea, Vector items, boolean centerItemsVertically) {
      super((byte)2);
      this.mClipArea = clipArea;
      this.mCenterItemsVertically = centerItemsVertically;
      this.mHideItems = false;
      this.mIsSorted = false;
      this.mIsPageAligned = pageAligned;
      this.mHasInertia = inertia;
      this.mHasSwipingBetweenPages = swiping;
      this.inertiaController = new HUDSpeedController(1);
      this.autoadjustToBorderController = new HUDSpeedController(0);
      this.autoadjustToPageController = new HUDSpeedController(0);
      this.mItems = items;
      this.mPosX = clipArea.getX();
      if (clipArea.getY() >= 0) {
         this.mPosY = clipArea.getY();
      } else {
         int screenHeight = Toolkit.getScreenHeight();
         this.mPosY = screenHeight + clipArea.getY();
      }

      this.containerDelta = 0;
      this.containerTargetDelta = 0;
      this.calculateContainer();

      for (int i = 0; i < this.mItems.size(); i++) {
         this.calculateIconPosition(i);
      }

      this.setState(0);
   }

   public void setSortByStrings() {
      this.mIsSortingByIntegers = false;
   }

   public void resetPosition() {
      this.containerDelta = 0;
      this.containerTargetDelta = 0;
   }

   public Vector getItems() {
      return this.mItems;
   }

   public void calculateContainer() {
      if (this.mItems.isEmpty()) {
         this.CLIP_SIZE = this.mClipArea.getHeight();
         this.ICON_WIDTH = this.mClipArea.getWidth();
         this.ICON_HEIGHT = this.mClipArea.getHeight();
         this.ICON_SPACE = 0;
      } else {
         HUDItem first = (HUDItem)this.mItems.firstElement();
         CollisionBox box;
         if (this.mCenterItemsVertically) {
            int width = first.getWidth();
            int height = first.getHeight();
            int x = this.mClipArea.getX();
            int clipWidth = this.mClipArea.getWidth();
            int y = this.mClipArea.getY();
            int clipHeight = this.mClipArea.getHeight();
            box = new CollisionBox((short)(x + (clipWidth - width >> 1)), (short)(y + (clipHeight - height >> 1)), (short)width, (short)height, 0);
            if (box.getY() < 0) {
               int screenHeight = Toolkit.getScreenHeight();
               this.mPosY = box.getY() + screenHeight;
            } else {
               this.mPosY = box.getY();
            }
         } else {
            box = this.mClipArea;
         }

         this.mPosX = this.mClipArea.getX() + (this.mClipArea.getWidth() - first.getWidth()) / 2;
         this.CLIP_SIZE = box.getHeight();
         this.ICON_WIDTH = first.getWidth();
         this.ICON_HEIGHT = first.getHeight();
         this.ICON_SPACE = first.getSpace();
      }

      int count = this.mItems.size();
      this.containerSize = (this.mItems.size() - 1) * this.ICON_SPACE + this.ICON_HEIGHT * count;
      if (this.mIsPageAligned) {
         this.currentPage = 0;
         this.numberOfPages = 0;
         this.iconsPerPage = this.CLIP_SIZE / this.ICON_HEIGHT;

         for (int left = this.mItems.size(); left > 0; this.numberOfPages++) {
            left -= this.iconsPerPage;
         }

         this.pageSize = (this.ICON_SPACE + this.ICON_HEIGHT) * this.iconsPerPage;
         this.containerSize = (this.numberOfPages * this.iconsPerPage - 1) * this.ICON_SPACE + this.numberOfPages * this.iconsPerPage * this.ICON_HEIGHT;
      }
   }

   public void setFadingBorder(boolean fadingBorder) {
      if (this.mClipArea != null) {
         this.mHasFadingBorder = fadingBorder;
         this.mBackground = new RoundRectangle(3);
         this.mOverlay = new RoundRectangle(2);
         this.mMask = new RoundRectangle(4);
      }
   }

   @Override
   public void setParent(HUDObject parent) {
      this.mParent = parent;
   }

   public void setTouchAreaClip() {
      if (this.mParent != null && this.mClipArea != null) {
         int parentX = this.mParent.getX();
         int x = this.mClipArea.getX();
         int parentY = this.mParent.getY();
         int y = this.mClipArea.getY();
         int width = this.mClipArea.getWidth();
         OGL.setClip(x + parentX, y + parentY, width, this.mClipArea.getHeight());
      }
   }

   @Override
   public void pointerEvent(TouchEvent event) {
      if (this.mIsVisible && !this.mHideItems) {
         if (this.isInsideTouchArea(event.getX(), event.getY()) || this.isInsideTouchArea(event.getInitialX(), event.getInitialY())) {
            for (int i = 0; i < this.mItems.size() && !event.isConsumed(); i++) {
               ((HUDItem)this.mItems.elementAt(i)).pointerEvent(event);
            }
         }

         switch (this.mState) {
            case 0:
               if (event.getType() != 0 || !this.isInsideTouchArea(event.getX(), event.getY()) || event.isConsumed()) {
                  return;
               }

               this.setState(1);
               break;
            case 1:
               if (event.getType() == 1) {
                  this.setState(2);
               } else {
                  if (event.getType() != 2) {
                     return;
                  }

                  this.setState(0);
               }
               break;
            case 2:
               int deltaY = event.getDeltaY();
               if (event.getType() == 1) {
                  if (deltaY != 0) {
                     this.lastDragDistance = deltaY;
                  }

                  if (this.containerDelta >= 0 || this.containerDelta + this.containerSize <= this.CLIP_SIZE) {
                     deltaY /= 2;
                  }

                  this.containerDelta += deltaY;
               } else {
                  if (event.getType() != 2) {
                     return;
                  }

                  if (this.mHasInertia && Math.abs(this.lastDragDistance) >= 15) {
                     if (this.containerDelta >= 1 && this.lastDragDistance >= 1) {
                        this.setState(4);
                     } else if (this.containerDelta + this.containerSize < this.CLIP_SIZE && this.lastDragDistance <= -1) {
                        this.setState(5);
                     } else {
                        this.setState(3);
                     }
                  } else if (!this.mIsPageAligned
                     || !this.mHasSwipingBetweenPages
                     || Math.abs(this.lastDragDistance) < 15
                     || (this.currentPage < 1 || this.lastDragDistance <= 0) && (this.currentPage >= this.numberOfPages - 1 || this.lastDragDistance > -1)) {
                     if (this.mIsPageAligned && this.containerDelta <= -1 && this.containerDelta + this.containerSize > this.CLIP_SIZE) {
                        this.setState(6);
                     } else if (this.containerDelta >= 1) {
                        this.setState(4);
                     } else if (this.containerDelta + this.containerSize >= this.CLIP_SIZE) {
                        this.setState(0);
                     } else if (this.containerSize >= this.CLIP_SIZE) {
                        this.setState(5);
                     } else {
                        this.setState(4);
                     }
                  } else {
                     this.setState(9);
                  }
               }
               break;
            case 3:
            case 4:
            case 5:
            case 13:
               if (event.getType() != 0 || !this.isInsideTouchArea(event.getX(), event.getY())) {
                  return;
               }

               this.setState(2);
               break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            default:
               return;
         }

         event.setConsumed(true);
      }
   }

   @Override
   public void logicUpdate(int delta) {
      if (this.mIsVisible && !this.mHideItems) {
         for (int i = 0; i < this.mItems.size(); i++) {
            ((HUDItem)this.mItems.elementAt(i)).logicUpdate(delta);
         }

         if (this.mScrollbarState == 2 && System.currentTimeMillis() - this.mScrollbarLastTime >= 1000L) {
            this.mScrollbarState = 0;
         }

         switch (this.mState) {
            case 3:
               this.inertiaController.logicUpdate(delta);
               int speed = this.inertiaController.getCurrentSpeed();
               this.containerDelta += speed;
               if (speed == 0) {
                  if (this.containerDelta >= 1) {
                     this.setState(4);
                  } else if (this.containerDelta + this.containerSize >= this.CLIP_SIZE) {
                     this.setState(0);
                  } else {
                     this.setState(5);
                  }
               } else {
                  int quarter = this.CLIP_SIZE / 4;
                  if (this.containerDelta > quarter) {
                     this.setState(4);
                  } else if (this.containerDelta + this.containerSize < this.CLIP_SIZE - quarter) {
                     this.setState(5);
                  }
               }
               break;
            case 4:
               this.autoadjustToBorderController.logicUpdate(delta);
               this.containerDelta = this.autoadjustToBorderController.getCurrentSpeed() + this.containerDelta;
               if (this.containerDelta <= -1) {
                  this.containerDelta = 0;
                  this.setState(0);
               }
               break;
            case 5:
               this.autoadjustToBorderController.logicUpdate(delta);
               this.containerDelta = this.containerDelta + this.autoadjustToBorderController.getCurrentSpeed();
               if (this.containerDelta + this.containerSize > this.CLIP_SIZE) {
                  this.containerDelta = this.CLIP_SIZE - this.containerSize;
                  this.setState(0);
               }
               break;
            case 6:
            case 9:
            case 10:
            case 11:
            case 12:
               this.autoadjustToPageController.logicUpdate(delta);
               int speed = this.autoadjustToPageController.getCurrentSpeed();
               if (this.containerDelta < this.containerTargetDelta) {
                  this.containerDelta += speed;
                  if (this.containerDelta >= this.containerTargetDelta) {
                     this.containerDelta = this.containerTargetDelta;
                     this.setState(0);
                  }
               }

               if (this.containerDelta > this.containerTargetDelta) {
                  this.containerDelta -= speed;
                  if (this.containerDelta <= this.containerTargetDelta) {
                     this.containerDelta = this.containerTargetDelta;
                     this.setState(0);
                  }
               }

               if (this.containerDelta == this.containerTargetDelta) {
                  this.setState(0);
               }
               break;
            case 7:
            case 8:
               if (this.mItems.size() >= 1) {
                  boolean stopped = true;

                  for (int i = 0; i < this.mItems.size(); i++) {
                     stopped &= !((HUDItem)this.mItems.elementAt(i)).isMoving();
                  }

                  if (!stopped) {
                     return;
                  }
               }

               if (this.mItems.size() >= 1) {
                  this.setState(0);
               } else {
                  this.setState(-1);
               }
               break;
            case 13:
               this.autoadjustToBorderController.logicUpdate(delta);
               this.containerDelta = this.autoadjustToBorderController.getCurrentSpeed() + this.containerDelta;
               if (this.containerDelta >= 1) {
                  this.containerDelta = 0;
                  this.setState(0);
               }
         }
      }
   }

   @Override
   public void draw() {
      if (this.mIsVisible) {
         if (this.mHasFadingBorder) {
            RoundRectangle background = this.mBackground;
            int parentX = this.mParent.getX();
            int x = this.mClipArea.getX();
            int parentY = this.mParent.getY();
            int y = this.mClipArea.getY();
            int width = this.mClipArea.getWidth();
            background.setSizes(x + parentX, y + parentY, width, this.mClipArea.getHeight());
            this.mBackground.draw();
         }

         if (!this.mHideItems) {
            this.drawScrollbar();
            if (!this.mHideItems) {
               for (int i = 0; i < this.mItems.size(); i++) {
                  HUDItem item = (HUDItem)this.mItems.elementAt(i);
                  if (item.isVisible()) {
                     if (this.mIsClippingEnabled) {
                        this.setTouchAreaClip();
                     }

                     item.draw();
                  }
               }
            }
         }

         if (this.mHasFadingBorder) {
            RoundRectangle overlay = this.mOverlay;
            int parentX = this.mParent.getX();
            int x = this.mClipArea.getX();
            int parentY = this.mParent.getY();
            int y = this.mClipArea.getY();
            int width = this.mClipArea.getWidth();
            overlay.setSizes(x + parentX, y + parentY, width, this.mClipArea.getHeight());
            this.mOverlay.draw();
            if (this.mHasFadingBorder) {
               RoundRectangle mask = this.mMask;
               parentX = this.mParent.getX();
               x = this.mClipArea.getX();
               parentY = this.mParent.getY();
               y = this.mClipArea.getY();
               width = this.mClipArea.getWidth();
               mask.setSizes(x + parentX, y + parentY, width, this.mClipArea.getHeight());
               this.mMask.draw();
            }
         }
      }
   }

   public void setClipping(boolean clipping) {
      this.mIsClippingEnabled = clipping;
   }

   public void setScrollbar(boolean scrollbar) {
      this.mHasScrollbar = scrollbar;
   }

   public void setAnimatedSorting(boolean animated) {
      this.mIsSortingAnimated = animated;
   }

   public void setSorted(boolean sorted) {
      this.mIsSorted = sorted;
   }

   public boolean isSorted() {
      return this.mIsSorted;
   }

   public void setHideItems(boolean hide) {
      this.mHideItems = hide;
   }

   public boolean isHideItems() {
      return this.mHideItems;
   }

   public int getClipAreaX() {
      int parentX = this.mParent.getX();
      return this.mClipArea.getX() + parentX;
   }

   public int getClipAreaY() {
      int parentY = this.mParent.getY();
      return this.mClipArea.getY() + parentY;
   }

   public int getClipAreaWidth() {
      return this.mClipArea.getWidth();
   }

   public int getClipAreaHeight() {
      return this.mClipArea == null ? this.CLIP_SIZE : this.mClipArea.getHeight();
   }

   public int getContainerX() {
      return this.mParent != null ? this.mParent.getX() + this.mPosX : this.mPosX;
   }

   public int getContainerY() {
      return this.mParent != null ? this.mParent.getY() + this.containerDelta + this.mPosY : this.mPosY;
   }

   public int getCurrentPage() {
      if (!this.mIsPageAligned) {
         return -1;
      }

      if (this.mState == 0) {
         this.currentPage = -this.containerDelta / this.pageSize;
      }

      return this.currentPage;
   }

   public int getNumberOfPages() {
      return this.numberOfPages;
   }

   public void setPage(int page) {
      if (page >= 0 && this.getNumberOfPages() > page) {
         this.containerDelta = -page * this.pageSize;
         this.currentPage = page;
      }
   }

   public void gotoPage(int page) {
      if (this.mState == 0 && this.currentPage != page) {
         this.param = page;
         this.setState(12);
      }
   }

   public void gotoNextPage() {
      if (this.mState == 0) {
         if (this.getCurrentPage() < this.getNumberOfPages() - 1) {
            this.setState(10);
         }
      }
   }

   public void gotoPreviousPage() {
      if (this.mState == 0) {
         if (this.getCurrentPage() > 0) {
            this.setState(11);
         }
      }
   }

   public void unpress() {
      this.setState(0);
   }

   @Override
   public void callback(int id) {
   }

   public void sortItems() {
      Vector items = this.mItems;
      int intFirst = 0;
      int intOther = 0;
      String stringFirst = null;
      String stringOther = null;
      int i = 0;

      while (i < items.size() - 1) {
         HUDItem first = (HUDItem)items.elementAt(i);
         if (this.mIsSortingByIntegers) {
            intFirst = first.getParameterForSort();
         } else {
            stringFirst = first.getStringForSort();
         }

         int next = i + 1;

         for (int j = next; j < items.size(); j++) {
            HUDItem other = (HUDItem)items.elementAt(j);
            if (this.mIsSortingByIntegers) {
               intOther = other.getParameterForSort();
            } else {
               stringOther = other.getStringForSort();
            }

            if (this.mIsSortingByIntegers && intOther < intFirst || !this.mIsSortingByIntegers && stringFirst.compareTo(stringOther) > 0) {
               items.setElementAt(other, i);
               items.setElementAt(first, j);
               if (this.mIsSortingAnimated) {
                  this.calculateIconTargetPosition(i);
                  this.calculateIconTargetPosition(j);
               } else {
                  this.calculateIconPosition(i);
                  this.calculateIconPosition(j);
               }

               first = (HUDItem)items.elementAt(i);
               if (this.mIsSortingByIntegers) {
                  intFirst = first.getParameterForSort();
               } else {
                  stringFirst = first.getStringForSort();
               }
            }
         }

         i = next;
      }

      this.calculateContainer();
      this.setState(7);
   }

   public void addItems(Vector items) {
      int i = items.size();

      while (i > 0) {
         this.addItem((HUDItem)items.elementAt(--i));
      }
   }

   public void addItem(HUDItem item) {
      this.containerDelta = 0;
      int i = 0;

      while (i < this.mItems.size()) {
         HUDItem other = (HUDItem)this.mItems.elementAt(i);
         int height = this.ICON_HEIGHT;
         other.setTargetY(this.ICON_SPACE * Math.max(0, ++i) + height * i);
      }

      item.setY(1);
      item.setTouchAreaParent(this);
      this.mItems.insertElementAt(item, 0);
      if (this.mIsSorted) {
         this.sortItems();
      } else {
         this.calculateContainer();

         for (int ix = 0; ix < this.mItems.size(); ix++) {
            this.calculateIconPosition(ix);
         }

         this.setState(7);
      }
   }

   public void removeItems() {
      if (this.mItems != null) {
         if (this.mItems.size() >= 1) {
            this.mItems = new Vector();
            this.calculateContainer();
            this.setState(0);
         }
      }
   }

   public void removeItem(HUDItem item) {
      int index = -1;

      while (this.mItems.size() > ++index) {
         if (this.mItems.elementAt(index) == item) {
            this.mItems.removeElementAt(index);

            while (index < this.mItems.size()) {
               HUDItem other = (HUDItem)this.mItems.elementAt(index);
               int height = this.ICON_HEIGHT * Math.max(0, index);
               other.setTargetY(this.ICON_SPACE * Math.max(0, index) + height);
               index++;
            }

            this.calculateContainer();
            this.setState(8);
            return;
         }
      }
   }

   public void refresh() {
      if (this.mItems != null) {
         for (int i = 0; i < this.mItems.size(); i++) {
            ((HUDItem)this.mItems.elementAt(i)).refresh();
         }
      }
   }

   public boolean isEmpty() {
      return this.mItems.isEmpty();
   }

   public void calculateIconPosition(int index) {
      if (this.mItems.elementAt(index) != null) {
         HUDItem item = (HUDItem)this.mItems.elementAt(index);
         item.setTouchAreaParent(this);
         item.setY(this.ICON_SPACE * Math.max(0, index) + this.ICON_HEIGHT * index);
      }
   }

   public void calculateIconTargetPosition(int index) {
      if (index > -1 && index < this.mItems.size() && this.mItems.elementAt(index) != null) {
         HUDItem item = (HUDItem)this.mItems.elementAt(index);
         item.setTouchAreaParent(this);
         item.setTargetY(this.ICON_SPACE * Math.max(0, index) + this.ICON_HEIGHT * index);
      }
   }

   public boolean isInsideTouchArea(int x, int y) {
      int parentX = 0;
      int parentY = 0;
      if (this.mParent != null) {
         parentX = this.mParent.getX();
         parentY = this.mParent.getY();
      }

      int areaX;
      int areaY;
      int width;
      int height;
      if (this.mClipArea == null) {
         areaX = this.mPosX;
         areaY = this.mPosY;
         height = this.CLIP_SIZE;
         width = this.ICON_WIDTH;
      } else {
         areaX = this.mClipArea.getX();
         areaY = this.mClipArea.getY();
         width = this.mClipArea.getWidth();
         height = this.mClipArea.getHeight();
      }

      return areaX + parentX <= x && x <= areaX + parentX + width && areaY + parentY <= y && y <= height + areaY + parentY;
   }

   public void drawScrollbar() {
      if (this.mHasScrollbar && this.mScrollbarState != 0) {
         OGL.setClip(0, 0, Toolkit.getScreenWidth(), Toolkit.getScreenHeight());
         float clipHeight = this.mClipArea.getHeight();
         int size = this.containerSize;
         int delta = this.containerDelta;
         float bar = clipHeight * (clipHeight / size);
         if (bar > clipHeight) {
            bar = clipHeight;
         }

         bar = Math.max(5.0F, bar);
         float top;
         if (delta < 1) {
            top = -(clipHeight * ((float)delta / size));
         } else {
            bar -= delta;
            top = 0.0F;
         }

         if (size + delta < this.mClipArea.getHeight()) {
            int hidden = this.mClipArea.getHeight() - this.containerDelta - this.containerSize;
            bar = Math.max(5.0F, bar - hidden);
            top = clipHeight - bar;
         }

         OGL.pushParameters();
         float alpha = 0.75F;
         if (this.mScrollbarState == 2) {
            alpha = (float)(System.currentTimeMillis() - this.mScrollbarLastTime) * 0.75F / -1000.0F + 0.75F;
         }

         OGL.setAlpha(alpha);
         OGL.setColorRGB(8421504);
         int parentX = this.mParent.getX();
         int x = this.mClipArea.getX();
         int width = this.mClipArea.getWidth();
         int parentY = this.mParent.getY();
         int y = this.mClipArea.getY() + parentY;
         int height = (int)bar;
         if (bar < 5.0F) {
            height = 5;
         }

         OGL.fillRect(parentX + x + width - 5, (int)(top + y), 4, height);
         OGL.popParameters();
      }
   }

   public void setState(int state) {
      switch (state) {
         case 0:
            this.getCurrentPage();
            this.mScrollbarState = 2;
            this.mScrollbarLastTime = System.currentTimeMillis();
         case 1:
         case 7:
         case 8:
         default:
            break;
         case 2:
            if (this.mState == 5) {
               this.containerDelta = this.CLIP_SIZE - this.containerSize;
            } else if (this.mState == 4) {
               this.containerDelta = 0;
            }

            this.lastDragDistance = 0;
            break;
         case 3:
            this.inertiaController.setParameters(this.lastDragDistance, 100);
            break;
         case 4:
         case 13:
            this.autoadjustToBorderController.setParameters(-this.containerDelta, 200);
            break;
         case 5:
            this.autoadjustToBorderController.setParameters(this.CLIP_SIZE - this.containerDelta - this.containerSize, 200);
            break;
         case 6: {
            int best = 0;
            int bestDelta = Integer.MAX_VALUE;

            for (int page = 0; page < this.numberOfPages; page++) {
               int value = page * this.iconsPerPage * (this.ICON_HEIGHT + this.ICON_SPACE) + this.containerDelta;
               if (Math.abs(value) < Math.abs(bestDelta)) {
                  best = page;
                  bestDelta = value;
               }
            }

            int offset = this.iconsPerPage * best;
            this.containerTargetDelta = -(offset * (this.ICON_HEIGHT + this.ICON_SPACE));
            this.autoadjustToPageController.setParameters(Math.abs(offset * (this.ICON_HEIGHT + this.ICON_SPACE) + this.containerDelta), 250);
            if (this.containerTargetDelta == this.containerDelta) {
               this.setState(0);
            }
            break;
         }
         case 9: {
            int page = this.currentPage + (this.lastDragDistance >> 31 & 2) - 1;
            this.containerTargetDelta = page * -this.pageSize;
            this.autoadjustToPageController.setParameters(Math.abs(this.containerDelta - page * -this.pageSize), 200);
            break;
         }
         case 10:
            this.containerTargetDelta = this.pageSize * ~this.currentPage;
            this.autoadjustToPageController.setParameters(Math.abs(this.containerDelta - this.pageSize * ~this.currentPage), 100);
            break;
         case 11:
            this.containerTargetDelta = (1 - this.currentPage) * this.pageSize;
            this.autoadjustToPageController.setParameters(Math.abs(this.containerDelta - (1 - this.currentPage) * this.pageSize), 100);
            break;
         case 12:
            int pages = Math.abs(this.currentPage - this.param);
            this.containerTargetDelta = this.pageSize * -this.param;
            this.autoadjustToPageController.setParameters(Math.abs(this.containerDelta - this.pageSize * -this.param), pages * 200);
      }

      if (state != 0) {
         this.mScrollbarState = 1;
      }

      this.mState = state;
   }
}
